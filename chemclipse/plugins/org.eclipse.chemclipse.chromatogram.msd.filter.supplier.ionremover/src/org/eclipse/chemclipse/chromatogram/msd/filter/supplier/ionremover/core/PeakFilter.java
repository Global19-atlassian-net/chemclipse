/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.filter.processing.IPeakFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.processing.PeakFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.result.IPeakFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.PeakFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.AbstractPeakFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.IIonRemoverPeakFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public class PeakFilter extends AbstractPeakFilter {

	private static final String DESCRIPTION = "Ion Remover Peak Filter";

	@Override
	public IPeakFilterProcessingInfo applyFilter(List<IPeakMSD> peaks, IPeakFilterSettings peakFilterSettings, IProgressMonitor monitor) {

		IPeakFilterProcessingInfo processingInfo = new PeakFilterProcessingInfo();
		processingInfo.addMessages(validate(peaks, peakFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Apply the ion remover filter to the mass spectrum.
		 */
		if(peakFilterSettings instanceof IIonRemoverPeakFilterSettings) {
			//
			IIonRemoverPeakFilterSettings ionRemoverPeakFilterSettings = (IIonRemoverPeakFilterSettings)peakFilterSettings;
			IMarkedIons ionsToRemove = ionRemoverPeakFilterSettings.getIonsToRemove();
			for(IPeakMSD peak : peaks) {
				peak.getTargets().clear();
				IPeakMassSpectrum peakMassSpectrum = peak.getPeakModel().getPeakMassSpectrum();
				peakMassSpectrum.removeIons(ionsToRemove);
			}
			//
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The mass spectrum has been optimized successfully."));
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The filter settings instance is not a type of: " + IIonRemoverPeakFilterSettings.class);
		}
		//
		IPeakFilterResult peakFilterResult = new PeakFilterResult(ResultStatus.OK, "The ion remover filter has been applied successfully.");
		processingInfo.setPeakFilterResult(peakFilterResult);
		return processingInfo;
	}

	// ----------------------------------------------------CONVENIENT METHODS
	@Override
	public IPeakFilterProcessingInfo applyFilter(IPeakMSD peak, IPeakFilterSettings peakFilterSettings, IProgressMonitor monitor) {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		peaks.add(peak);
		return applyFilter(peaks, peakFilterSettings, monitor);
	}

	@Override
	public IPeakFilterProcessingInfo applyFilter(IPeakMSD peak, IProgressMonitor monitor) {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		peaks.add(peak);
		IPeakFilterSettings peakFilterSettings = PreferenceSupplier.getPeakFilterSettings();
		return applyFilter(peaks, peakFilterSettings, monitor);
	}

	@Override
	public IPeakFilterProcessingInfo applyFilter(List<IPeakMSD> peaks, IProgressMonitor monitor) {

		IPeakFilterSettings peakFilterSettings = PreferenceSupplier.getPeakFilterSettings();
		return applyFilter(peaks, peakFilterSettings, monitor);
	}

	@Override
	public IPeakFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IPeakFilterSettings peakFilterSettings, IProgressMonitor monitor) {

		IPeakFilterProcessingInfo processingInfo = new PeakFilterProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, peakFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Get the peaks of the selection.
		 */
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		List<IChromatogramPeakMSD> peakList = chromatogram.getPeaks(chromatogramSelection);
		/*
		 * Create a list. This could be implemented in a better way.
		 */
		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		for(IChromatogramPeakMSD peak : peakList) {
			peaks.add(peak);
		}
		/*
		 * Apply the filter.
		 */
		return applyFilter(peaks, peakFilterSettings, monitor);
	}

	@Override
	public IPeakFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IPeakFilterSettings peakFilterSettings = PreferenceSupplier.getPeakFilterSettings();
		return applyFilter(chromatogramSelection, peakFilterSettings, monitor);
	}
}
