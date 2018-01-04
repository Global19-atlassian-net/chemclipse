/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.core;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.AbstractBaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.processing.IBaselineDetectorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.calculator.SnipCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.settings.ISnipBaselineDetectorSettings;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.core.runtime.IProgressMonitor;

public class BaselineDetector extends AbstractBaselineDetector {

	@Override
	public IBaselineDetectorProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor) {

		IBaselineDetectorProcessingInfo processingInfo = super.validate(chromatogramSelection, baselineDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			calculateBaseline(chromatogramSelection, baselineDetectorSettings, monitor);
		}
		return processingInfo;
	}

	@Override
	public IBaselineDetectorProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IBaselineDetectorSettings baselineDetectorSettings = PreferenceSupplier.getBaselineDetectorSettings();
		return setBaseline(chromatogramSelection, baselineDetectorSettings, monitor);
	}

	/**
	 * Calculates the baseline.
	 */
	private void calculateBaseline(IChromatogramSelection chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		IScanRange scanRange = new ScanRange(startScan, stopScan);
		/*
		 * Iterations
		 */
		int iterations = 0;
		int windowSize = 0;
		if(baselineDetectorSettings instanceof ISnipBaselineDetectorSettings) {
			ISnipBaselineDetectorSettings snipSettings = (ISnipBaselineDetectorSettings)baselineDetectorSettings;
			iterations = snipSettings.getIterations();
			windowSize = snipSettings.getWindowSize().getSize();
		} else {
			iterations = PreferenceSupplier.getIterations();
			windowSize = PreferenceSupplier.getWindowSize().getSize();
		}
		/*
		 * If the scan range is lower than the given window size, do nothing.
		 */
		if(scanRange.getWidth() <= windowSize) {
			return;
		}
		/*
		 * Calculate the Baseline
		 */
		try {
			ITotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals totalIonSignals = totalScanSignalExtractor.getTotalScanSignals(startScan, stopScan);
			calculateSNIPBaseline(totalIonSignals, iterations, monitor);
			IBaselineModel baselineModel = chromatogram.getBaselineModel();
			applyBaseline(baselineModel, totalIonSignals, startScan, stopScan, monitor);
		} catch(ChromatogramIsNullException e) {
			return;
		}
	}

	private void calculateSNIPBaseline(ITotalScanSignals totalIonSignals, int iterations, IProgressMonitor monitor) {

		/*
		 * Calculates the SNIP baseline.
		 */
		int size = totalIonSignals.size();
		float[] intensityValues = new float[size];
		int counter = 0;
		for(ITotalScanSignal signal : totalIonSignals.getTotalScanSignals()) {
			intensityValues[counter++] = signal.getTotalSignal();
		}
		SnipCalculator snipCalculator = new SnipCalculator();
		snipCalculator.calculateBaselineIntensityValues(intensityValues, iterations, monitor);
		/*
		 * Set the calculated values.
		 */
		counter = 0;
		for(ITotalScanSignal signal : totalIonSignals.getTotalScanSignals()) {
			signal.setTotalSignal(intensityValues[counter++]);
		}
	}

	/**
	 * Sets the baseline.
	 * 
	 * @param baselineModel
	 */
	private void applyBaseline(IBaselineModel baselineModel, ITotalScanSignals totalIonSignals, int startScan, int stopScan, IProgressMonitor monitor) {

		ITotalScanSignal actualTotalIonSignal;
		ITotalScanSignal nextTotalIonSignal;
		/*
		 * Why scan < numberOfScans instead of scan <= numberOfScans? Because of
		 * .getNextTotalIonSignal();
		 */
		for(int scan = startScan; scan < stopScan; scan++) {
			//
			monitor.subTask("Apply baseline to scan: " + scan);
			actualTotalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			nextTotalIonSignal = totalIonSignals.getNextTotalScanSignal(scan);
			/*
			 * Retention times and background abundances.
			 */
			int startRetentionTime = actualTotalIonSignal.getRetentionTime();
			float startBackgroundAbundance = actualTotalIonSignal.getTotalSignal();
			int stopRetentionTime = nextTotalIonSignal.getRetentionTime();
			float stopBackgroundAbundance = nextTotalIonSignal.getTotalSignal();
			/*
			 * Set the baseline.
			 * It is validate == false, cause we know that the segments are calculated without overlap.
			 */
			baselineModel.addBaseline(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance, false);
		}
	}
}
