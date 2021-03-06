/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsGapFiller;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanWSD;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterGapFiller extends AbstractChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsGapFiller) {
				FilterSettingsGapFiller filterSettings = (FilterSettingsGapFiller)chromatogramFilterSettings;
				autofillScans(chromatogramSelection, filterSettings);
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram has been autofilled successfully."));
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsGapFiller filterSettings = PreferenceSupplier.getFilterSettingsFillGaps();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	@SuppressWarnings("unchecked")
	private void autofillScans(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsGapFiller filterSettings) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		//
		int scanInterval = chromatogram.getScanInterval();
		if(scanInterval > 0) {
			/*
			 * Settings
			 */
			List<IScan> gapFillerScans = new ArrayList<>();
			/*
			 * At least 3 * scan interval, because two scans are added in case of a gap, each with a delta of scan interval.
			 */
			int limitFactor = filterSettings.getLimitFactor() >= 3 ? filterSettings.getLimitFactor() : 3;
			int limit = scanInterval * limitFactor;
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime()) + 1;
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			//
			for(int i = startScan; i <= stopScan; i++) {
				/*
				 * Try to detect the gap.
				 */
				IScan scanGapStart = chromatogram.getScan(i - 1);
				IScan scanGapEnd = chromatogram.getScan(i);
				//
				int retentionTimeGapStart = scanGapStart.getRetentionTime();
				int retentionTimeGapEnd = scanGapEnd.getRetentionTime();
				int intervalGap = retentionTimeGapEnd - retentionTimeGapStart;
				//
				if(intervalGap > limit) {
					/*
					 * Start/Stop of the gap.
					 */
					retentionTimeGapStart += scanInterval;
					retentionTimeGapEnd -= scanInterval;
					/*
					 * First
					 */
					addScan(scanGapStart, gapFillerScans, retentionTimeGapStart);
					/*
					 * Middle
					 */
					int deltaStep = (retentionTimeGapEnd - retentionTimeGapStart) / scanInterval;
					if(deltaStep > 0) {
						int retentionTime = retentionTimeGapStart;
						int next = retentionTimeGapEnd - deltaStep;
						while(retentionTime < next) {
							retentionTime += deltaStep;
							addScan(scanGapStart, gapFillerScans, retentionTime);
						}
					}
					/*
					 * Last
					 */
					addScan(scanGapStart, gapFillerScans, retentionTimeGapEnd);
				}
			}
			/*
			 * Insert new scans and sort them by retention time.
			 */
			if(gapFillerScans.size() > 0) {
				List<IScan> scans = new ArrayList<>(chromatogram.getScans());
				scans.addAll(gapFillerScans);
				Collections.sort(scans, (s1, s2) -> Integer.compare(s1.getRetentionTime(), s2.getRetentionTime()));
				chromatogram.replaceAllScans(scans);
			}
		}
	}

	/**
	 * Create an empty scan and add it to the list.
	 * 
	 * @param scanReference
	 * @param gapFillerScans
	 * @param retentionTime
	 */
	private void addScan(IScan scanReference, List<IScan> gapFillerScans, int retentionTime) {

		IScan scan = createScan(scanReference, retentionTime);
		if(scan != null) {
			gapFillerScans.add(scan);
		}
	}

	/**
	 * Create an empty scan.
	 * 
	 * @param scanReference
	 * @param retentionTime
	 * @return IScan
	 */
	private IScan createScan(IScan scanReference, int retentionTime) {

		IScan scan = null;
		//
		if(scanReference instanceof IScanCSD) {
			scan = new ScanCSD(retentionTime, 0);
		} else if(scanReference instanceof IScanMSD) {
			scan = new ScanMSD();
			scan.setRetentionTime(retentionTime);
		} else if(scanReference instanceof IScanWSD) {
			scan = new ScanWSD();
			scan.setRetentionTime(retentionTime);
		}
		//
		return scan;
	}
}
