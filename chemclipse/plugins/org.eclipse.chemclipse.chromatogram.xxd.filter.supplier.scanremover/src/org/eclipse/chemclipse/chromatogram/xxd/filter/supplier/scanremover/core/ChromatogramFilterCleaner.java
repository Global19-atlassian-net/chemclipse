/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This filter removes empty scans.
 *
 */
public class ChromatogramFilterCleaner extends AbstractChromatogramFilter {

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		/*
		 * Validate the settings.
		 */
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Try to remove the given scans.
		 */
		IChromatogramFilterResult chromatogramFilterResult;
		try {
			applyChromatogramCleanerFilter(chromatogramSelection, monitor);
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Chromatogram Cleaner", "Empty scans have been removed successfully."));
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "Empty scans have been removed successfully.");
		} catch(FilterException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		}
		processingInfo.setProcessingResult(chromatogramFilterResult);
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getChromatogramFilterSettings();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}

	private void applyChromatogramCleanerFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) throws FilterException {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		List<Integer> scansToRemove = new ArrayList<Integer>();
		/*
		 * Iterate through all selected scans and mark those to be removed.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScan chromatogramScan = chromatogram.getScan(scan);
			if(chromatogramScan instanceof IScanMSD) {
				/*
				 * MSD
				 */
				if(((IScanMSD)chromatogramScan).getNumberOfIons() == 0) {
					monitor.subTask("Remove scan from chromatogram: " + scan);
					scansToRemove.add(scan);
				}
			} else if(chromatogramScan instanceof IScanCSD) {
				/*
				 * CSD
				 */
				if(((IScanCSD)chromatogramScan).getTotalSignal() == 0) {
					monitor.subTask("Remove scan from chromatogram: " + scan);
					scansToRemove.add(scan);
				}
			} else if(chromatogramScan instanceof IScanWSD) {
				/*
				 * WSD
				 */
				if(((IScanWSD)chromatogramScan).getScanSignals().size() == 0) {
					monitor.subTask("Remove scan from chromatogram: " + scan);
					scansToRemove.add(scan);
				}
			}
		}
		/*
		 * Use a remove counter, because each time a scan will be removed, the chromatogram contains one scan less.
		 */
		int removeCounter = 0;
		for(Integer scan : scansToRemove) {
			scan -= removeCounter;
			chromatogram.removeScan(scan);
			removeCounter++;
		}
		//
		chromatogram.recalculateScanNumbers();
		chromatogramSelection.reset();
	}
}
