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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileReader;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.ISupplierCalculatorSettings;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class RetentionIndexCalculator {

	public IProcessingInfo apply(IChromatogramSelection chromatogramSelection, ISupplierCalculatorSettings supplierCalculatorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		List<String> retentionIndexFiles = supplierCalculatorSettings.getRetentionIndexFiles();
		/*
		 * Create a calibration map for different column polarities.
		 */
		CalibrationFileReader calibrationFileReader = new CalibrationFileReader();
		Map<String, ISeparationColumnIndices> calibrationMap = new HashMap<String, ISeparationColumnIndices>();
		for(String retentionIndexFile : retentionIndexFiles) {
			File file = new File(retentionIndexFile);
			ISeparationColumnIndices separationColumnIndices = calibrationFileReader.parse(file);
			ISeparationColumn separationColumn = separationColumnIndices.getSeparationColumn();
			calibrationMap.put(separationColumn.getName(), separationColumnIndices);
		}
		/*
		 * Run the calculation.
		 * Use the separation column info to get the correct RI file.
		 */
		//
		ISeparationColumn separationColumn = chromatogramSelection.getChromatogram().getSeparationColumn();
		ISeparationColumnIndices separationColumnIndices = calibrationMap.get(separationColumn.getName());
		if(separationColumnIndices != null) {
			calculateIndex(chromatogramSelection, separationColumnIndices);
		}
		//
		return processingInfo;
	}

	/**
	 * Calculate the value if both entries exists.
	 * See AMDIS manual:
	 * RIcomp = RIlo + ( (RIhi - RIlo) * (RTact - RTlo) / (RThi - RTlo) )
	 */
	public float calculateRetentionIndex(int retentionTime, ISeparationColumnIndices separationColumnIndices) {

		float retentionIndex = 0;
		Map.Entry<Integer, IRetentionIndexEntry> floorEntry = separationColumnIndices.floorEntry(retentionTime);
		Map.Entry<Integer, IRetentionIndexEntry> ceilingEntry = separationColumnIndices.ceilingEntry(retentionTime);
		//
		if(floorEntry != null && ceilingEntry != null) {
			/*
			 * Get the values.
			 */
			IRetentionIndexEntry floorIndex = floorEntry.getValue();
			IRetentionIndexEntry ceilingIndex = ceilingEntry.getValue();
			float retentionIndexLow = floorIndex.getRetentionIndex();
			int retentionTimeLow = floorIndex.getRetentionTime();
			float retentionIndexHigh = ceilingIndex.getRetentionIndex();
			int retentionTimeHigh = ceilingIndex.getRetentionTime();
			if(retentionTimeLow == retentionTimeHigh) {
				/*
				 * We are at an exact value, return simply the RI, otherwise it will fail at if(denominatorRT != 0)
				 */
				return retentionIndexLow;
			}
			/*
			 * Execute the calculation.
			 */
			float factorRetentionIndex = retentionIndexHigh - retentionIndexLow;
			float nominatorRT = retentionTime - retentionTimeLow;
			float denominatorRT = retentionTimeHigh - retentionTimeLow;
			if(denominatorRT != 0) {
				/*
				 * Calculate the retention index.
				 */
				retentionIndex = retentionIndexLow + (factorRetentionIndex * nominatorRT / denominatorRT);
			}
		}
		//
		return retentionIndex;
	}

	@SuppressWarnings("unchecked")
	private void calculateIndex(IChromatogramSelection chromatogramSelection, ISeparationColumnIndices separationColumnIndices) {

		if(separationColumnIndices != null) {
			IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			int startScan = chromatogram.getScanNumber(startRetentionTime);
			int stopScan = chromatogram.getScanNumber(stopRetentionTime);
			/*
			 * Scans
			 */
			for(int scan = startScan; scan <= stopScan; scan++) {
				IScan supplierScan = chromatogram.getScan(scan);
				int retentionTime = supplierScan.getRetentionTime();
				float retentionIndex = calculateRetentionIndex(retentionTime, separationColumnIndices);
				supplierScan.setRetentionIndex(retentionIndex);
				/*
				 * Calculate RI also for the optimized MS.
				 */
				if(supplierScan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)supplierScan;
					IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
					if(optimizedMassSpectrum != null) {
						optimizedMassSpectrum.setRetentionIndex(retentionIndex);
					}
				}
			}
			/*
			 * Peaks
			 */
			List<? extends IPeak> peaks = chromatogram.getPeaks();
			for(IPeak peak : peaks) {
				IScan scan = peak.getPeakModel().getPeakMaximum();
				int retentionTime = scan.getRetentionTime();
				if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
					float retentionIndex = calculateRetentionIndex(retentionTime, separationColumnIndices);
					scan.setRetentionIndex(retentionIndex);
				}
			}
		}
	}
}
