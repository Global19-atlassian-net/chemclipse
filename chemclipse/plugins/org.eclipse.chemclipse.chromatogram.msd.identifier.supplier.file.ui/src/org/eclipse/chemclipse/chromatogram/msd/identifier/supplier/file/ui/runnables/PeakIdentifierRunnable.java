/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.runnables;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class PeakIdentifierRunnable implements IRunnableWithProgress {

	private static final String description = "File Peak Identifier";
	private static final String IDENTIFIER_ID = "org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.peak";
	private IChromatogramSelectionMSD chromatogramSelection;
	private boolean identifySelectedPeak;

	public PeakIdentifierRunnable(IChromatogramSelectionMSD chromatogramSelection, boolean identifySelectedPeak) {
		this.chromatogramSelection = chromatogramSelection;
		this.identifySelectedPeak = identifySelectedPeak;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(description, IProgressMonitor.UNKNOWN);
			//
			IPeakIdentifierProcessingInfo processingInfo;
			if(identifySelectedPeak) {
				IChromatogramPeakMSD peak = chromatogramSelection.getSelectedPeak();
				processingInfo = PeakIdentifier.identify(peak, IDENTIFIER_ID, monitor);
			} else {
				List<IChromatogramPeakMSD> peaks = chromatogramSelection.getChromatogramMSD().getPeaks(chromatogramSelection);
				List<IPeakMSD> peakList = new ArrayList<IPeakMSD>();
				for(IChromatogramPeakMSD chromatogramPeak : peaks) {
					peakList.add(chromatogramPeak);
				}
				processingInfo = PeakIdentifier.identify(peakList, IDENTIFIER_ID, monitor);
			}
			/*
			 * Update the chromatogram selection.
			 */
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, true);
			if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
				((ChromatogramSelectionMSD)chromatogramSelection).update(false);
			}
		} finally {
			monitor.done();
		}
	}
}
