/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.AbstractPeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.processing.IPeakDetectorCSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.processing.PeakDetectorCSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IDetectorSlope;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.RawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.IFirstDerivativePeakDetectorCSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakModelCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.support.PeakBuilderCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.miscellaneous.Evaluation;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetectorCSD extends AbstractPeakDetectorCSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorCSD.class);
	private static float NORMALIZATION_BASE = 100000.0f;
	private static int CONSECUTIVE_SCAN_STEPS = 3;
	private static final String DETECTOR_DESCRIPTION = "Peak Detector First Derivative";
	//
	private double threshold = 0.005d;
	private boolean includeBackground = false;
	private float minimumSignalToNoiseRatio = 0.0f;
	private WindowSize movingAverageWindow = WindowSize.SCANS_3;

	@Override
	public IPeakDetectorCSDProcessingInfo detect(IChromatogramSelectionCSD chromatogramSelection, IPeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		/*
		 * Check whether the chromatogram selection is null or not.
		 */
		IPeakDetectorCSDProcessingInfo processingInfo = new PeakDetectorCSDProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, peakDetectorSettings, monitor));
		//
		if(!processingInfo.hasErrorMessages()) {
			setThresholdValue(peakDetectorSettings);
			setIncludeBackground(peakDetectorSettings);
			setMinimumSignalToNoiseRation(peakDetectorSettings);
			setMovingAverageWindowSize(peakDetectorSettings);
			//
			detectPeaks(chromatogramSelection, monitor);
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Peak Detector First Derivative", "Peaks have been detected successfully."));
		}
		return processingInfo;
	}

	@Override
	public IPeakDetectorCSDProcessingInfo detect(IChromatogramSelectionCSD chromatogramSelection, IProgressMonitor monitor) {

		IFirstDerivativePeakDetectorCSDSettings peakDetectorSettings = PreferenceSupplier.getPeakDetectorCSDSettings();
		return detect(chromatogramSelection, peakDetectorSettings, monitor);
	}

	/**
	 * Sets the appropriate threshold value for this extension point.
	 */
	private void setThresholdValue(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IFirstDerivativePeakDetectorCSDSettings) {
			IFirstDerivativePeakDetectorCSDSettings firstDerivativePeakDetectorSettings = (IFirstDerivativePeakDetectorCSDSettings)peakDetectorSettings;
			/*
			 * The threshold value depends on the actual calculation.<br/> The
			 * threshold defines the slope sensitivity.
			 */
			switch(firstDerivativePeakDetectorSettings.getThreshold()) {
				case OFF:
					this.threshold = 0.0005d;
					break;
				case LOW:
					this.threshold = 0.005d;
					break;
				case MEDIUM:
					this.threshold = 0.05d;
					break;
				case HIGH:
					this.threshold = 0.5d;
					break;
				default:
					this.threshold = 0.005d;
					break;
			}
		}
	}

	private void setIncludeBackground(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IFirstDerivativePeakDetectorCSDSettings) {
			IFirstDerivativePeakDetectorCSDSettings firstDerivativePeakDetectorSettings = (IFirstDerivativePeakDetectorCSDSettings)peakDetectorSettings;
			this.includeBackground = firstDerivativePeakDetectorSettings.isIncludeBackground();
		}
	}

	private void setMinimumSignalToNoiseRation(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IFirstDerivativePeakDetectorCSDSettings) {
			IFirstDerivativePeakDetectorCSDSettings firstDerivativePeakDetectorSettings = (IFirstDerivativePeakDetectorCSDSettings)peakDetectorSettings;
			this.minimumSignalToNoiseRatio = firstDerivativePeakDetectorSettings.getMinimumSignalToNoiseRatio();
		}
	}

	private void setMovingAverageWindowSize(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IFirstDerivativePeakDetectorCSDSettings) {
			IFirstDerivativePeakDetectorCSDSettings firstDerivativePeakDetectorSettings = (IFirstDerivativePeakDetectorCSDSettings)peakDetectorSettings;
			this.movingAverageWindow = firstDerivativePeakDetectorSettings.getMovingAverageWindowSize();
		}
	}

	/**
	 * Detect the peaks in the selection and add them to the chromatogram.
	 * 
	 * @param chromatogramSelection
	 * @throws ValueMustNotBeNullException
	 */
	private void detectPeaks(IChromatogramSelectionCSD chromatogramSelection, IProgressMonitor monitor) {

		IFirstDerivativeDetectorSlopes slopes = getFirstDerivativeSlopes(chromatogramSelection);
		List<IRawPeak> rawPeaks = getRawPeaks(slopes, monitor);
		buildAndStorePeaks(rawPeaks, chromatogramSelection.getChromatogramCSD());
	}

	/**
	 * Builds from each raw peak a valid {@link IChromatogramPeakMSD} and adds it to the
	 * chromatogram.
	 * 
	 * @param rawPeaks
	 * @param chromatogram
	 */
	private void buildAndStorePeaks(List<IRawPeak> rawPeaks, IChromatogramCSD chromatogram) {

		IChromatogramPeakCSD peak = null;
		IScanRange scanRange = null;
		for(IRawPeak rawPeak : rawPeaks) {
			/*
			 * Build the peak and add it.
			 */
			try {
				scanRange = new ScanRange(rawPeak.getStartScan(), rawPeak.getStopScan());
				/*
				 * includeBackground
				 * false: BV or VB
				 * true: VV
				 */
				peak = PeakBuilderCSD.createPeak(chromatogram, scanRange, includeBackground);
				if(isValidPeak(peak)) {
					/*
					 * Add the detector description. Add the peak to the
					 * chromatogram.
					 */
					peak.setDetectorDescription(DETECTOR_DESCRIPTION);
					chromatogram.addPeak(peak);
				}
			} catch(IllegalArgumentException e) {
				logger.warn(e);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * Initializes the slope values.
	 * 
	 * @param chromatogramSelection
	 * @return {@link IFirstDerivativeDetectorSlopes}
	 */
	private IFirstDerivativeDetectorSlopes getFirstDerivativeSlopes(IChromatogramSelectionCSD chromatogramSelection) {

		ITotalScanSignals signals = new TotalScanSignals(chromatogramSelection);
		TotalScanSignalsModifier.normalize(signals, NORMALIZATION_BASE);
		/*
		 * Get the start and stop scan of the chromatogram selection.
		 */
		IFirstDerivativeDetectorSlopes slopes = new FirstDerivativeDetectorSlopes(signals);
		/*
		 * Fill the slope list.
		 */
		int startScan = signals.getStartScan();
		int stopScan = signals.getStopScan();
		//
		for(int scan = startScan; scan < stopScan; scan++) {
			ITotalScanSignal s1 = signals.getTotalScanSignal(scan);
			ITotalScanSignal s2 = signals.getNextTotalScanSignal(scan);
			if(s1 != null && s2 != null) {
				IPoint p1 = new Point(s1.getRetentionTime(), s1.getTotalSignal());
				IPoint p2 = new Point(s2.getRetentionTime(), s2.getTotalSignal());
				IFirstDerivativeDetectorSlope slope = new FirstDerivativeDetectorSlope(p1, p2, s1.getRetentionTime());
				slopes.add(slope);
			}
		}
		slopes.calculateMovingAverage(movingAverageWindow);
		return slopes;
	}

	/**
	 * Marks the peaks with start, stop and max.
	 * 
	 * @param slopeList
	 */
	private List<IRawPeak> getRawPeaks(IFirstDerivativeDetectorSlopes slopes, IProgressMonitor monitor) {

		/*
		 * It should be also possible to detect peaks in a selected retention
		 * time area of the chromatogram.<br/> The value for scan in the for
		 * loop is by default 1 (detector array), but the slopes are storing
		 * start and end point of selection (scans).<br/> E.g. the selection is
		 * from scan 850 to scan 1000, then the loop starts at >
		 * slopes.getDetectorSlope(1 + 849);
		 */
		int size = slopes.size();
		int scanOffset = slopes.getStartScan() - 1;
		int peaks = 1;
		IRawPeak rawPeak;
		List<IRawPeak> rawPeaks = new ArrayList<IRawPeak>();
		for(int i = 1; i <= size - CONSECUTIVE_SCAN_STEPS; i++) {
			/*
			 * Get the scan numbers without offset.<br/> Why? To not get out of
			 * borders of the slopes list.
			 */
			int peakStart = detectPeakStart(slopes, i, scanOffset);
			int peakMaximum = detectPeakMaximum(slopes, peakStart, scanOffset);
			int peakStop = detectPeakStop(slopes, peakMaximum, scanOffset);
			/*
			 * Begin the detection of the next peak at the end of the actual
			 * peak.
			 */
			i = peakStop;
			/*
			 * Adjust the peak to their real positions (scan numbers) in the
			 * chromatogram.<br/> Keep in mind, the slopes list starts at
			 * position and not at the position of the scan.
			 */
			peakStart += scanOffset;
			peakMaximum += scanOffset;
			peakStop += scanOffset;
			//
			rawPeak = new RawPeak(peakStart, peakMaximum, peakStop);
			if(isValidRawPeak(rawPeak)) {
				monitor.subTask("Add peak " + peaks++);
				rawPeaks.add(rawPeak);
			}
		}
		return rawPeaks;
	}

	/**
	 * Checks if the peak is a valid raw peak.<br/>
	 * For example if it contains not less than the needed amount of scans.
	 * 
	 * @param rawPeak
	 * @return boolean
	 */
	private boolean isValidRawPeak(IRawPeak rawPeak) {

		boolean isValid = false;
		int width = rawPeak.getStopScan() - rawPeak.getStartScan() + 1;
		if(width >= IPeakModelCSD.MINIMUM_SCANS) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * Checks that the peak is not null and that it matches
	 * the min S/N requirements.
	 * 
	 * @param peak
	 * @return boolean
	 */
	private boolean isValidPeak(IChromatogramPeakCSD peak) {

		if(peak != null && peak.getSignalToNoiseRatio() >= minimumSignalToNoiseRatio) {
			return true;
		}
		return false;
	}

	/**
	 * Detects the peak start.
	 * 
	 * @param slope
	 * @param startScan
	 * @param scanOffset
	 * @return int
	 */
	private int detectPeakStart(IFirstDerivativeDetectorSlopes slopes, int startScan, int scanOffset) {

		int size = slopes.size();
		int peakStart = size - 1;
		IDetectorSlope slope;
		double[] values = new double[CONSECUTIVE_SCAN_STEPS];
		exitloop:
		for(int scan = startScan; scan <= size - CONSECUTIVE_SCAN_STEPS; scan++) {
			slope = slopes.getDetectorSlope(scan + scanOffset);
			if(slope.getSlope() > threshold) {
				/*
				 * Get the actual and the next slope values.
				 */
				for(int j = 0; j < CONSECUTIVE_SCAN_STEPS; j++) {
					values[j] = slopes.getDetectorSlope(scan + j + scanOffset).getSlope();
				}
				if(Evaluation.valuesAreGreaterThanThreshold(values, threshold) && Evaluation.valuesAreIncreasing(values)) {
					peakStart = scan;
					break exitloop;
				}
			}
		}
		return peakStart;
	}

	/**
	 * Detects the peak maxima.<br/>
	 * The peak start and stops needs to be detected previously.
	 * 
	 * @param slope
	 * @param startScan
	 * @param scanOffset
	 * @return int
	 */
	private int detectPeakMaximum(IFirstDerivativeDetectorSlopes slopes, int startScan, int scanOffset) {

		int size = slopes.size();
		IDetectorSlope slope;
		int peakMaximum = startScan;
		exitloop:
		for(int scan = startScan; scan <= size - CONSECUTIVE_SCAN_STEPS; scan++) {
			slope = slopes.getDetectorSlope(scan + scanOffset);
			if(slope.getSlope() < 0.0d) {
				peakMaximum = scan;
				break exitloop;
			}
		}
		return peakMaximum;
	}

	/**
	 * Detects the peak stops.
	 * 
	 * @param slope
	 * @param startScan
	 * @param scanOffset
	 * @return int
	 */
	private int detectPeakStop(IFirstDerivativeDetectorSlopes slopes, int startScan, int scanOffset) {

		int size = slopes.size();
		int peakStop = size - CONSECUTIVE_SCAN_STEPS;
		IDetectorSlope slope;
		exitloop:
		for(int scan = startScan; scan <= size - CONSECUTIVE_SCAN_STEPS; scan++) {
			slope = slopes.getDetectorSlope(scan + scanOffset);
			if(slope.getSlope() > 0.0d) {
				peakStop = scan;
				break exitloop;
			}
		}
		return peakStop;
	}
}
