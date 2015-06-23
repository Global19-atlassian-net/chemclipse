/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io.chromatogram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.implementation.Peaks;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl.MSLPeakExportConverter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;

public class ChromatogramWriterMSL extends AbstractChromatogramMSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		if(chromatogram == null || file == null) {
			throw new IOException("The chromatogram and the file must be not null.");
		}
		/*
		 * Extract the peaks
		 */
		IPeaks peaks = new Peaks();
		List<IChromatogramPeakMSD> chromatogramPeaks = chromatogram.getPeaks();
		for(IChromatogramPeakMSD chromatogramPeak : chromatogramPeaks) {
			peaks.addPeak(chromatogramPeak);
		}
		/*
		 * Export the peaks
		 */
		MSLPeakExportConverter peakExportConverter = new MSLPeakExportConverter();
		peakExportConverter.convert(file, peaks, false, monitor);
	}
}
