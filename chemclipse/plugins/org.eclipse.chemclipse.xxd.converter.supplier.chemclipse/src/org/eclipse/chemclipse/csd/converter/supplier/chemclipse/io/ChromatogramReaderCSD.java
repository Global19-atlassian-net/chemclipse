/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.chemclipse.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipInputStream;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1001;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1002;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1003;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1004;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1005;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1006;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1007;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1100;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io.ChromatogramReaderMSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.ReaderHelper;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReaderCSD extends AbstractChromatogramCSDReader implements IChromatogramCSDZipReader {

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramOverview chromatogramOverview = null;
		ReaderHelper readerHelper = new ReaderHelper();
		String version = readerHelper.getVersion(file);
		/*
		 * It's used to support older versions of
		 * the *.ocb format.
		 * TODO Optimize
		 */
		IChromatogramCSDReader chromatogramReader = getChromatogramReader(version);
		if(chromatogramReader != null) {
			try {
				chromatogramOverview = chromatogramReader.readOverview(file, monitor);
			} catch(Exception e) {
				chromatogramOverview = createChromatogramFIDFromMSD(file, monitor);
			}
		} else {
			chromatogramOverview = createChromatogramFIDFromMSD(file, monitor);
		}
		return chromatogramOverview;
	}

	@Override
	public IChromatogramCSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramCSD chromatogramCSD = null;
		ReaderHelper readerHelper = new ReaderHelper();
		String version = readerHelper.getVersion(file);
		/*
		 * It's used to support older versions of
		 * the *.ocb format.
		 */
		IChromatogramCSDReader chromatogramReader = getChromatogramReader(version);
		if(chromatogramReader != null) {
			try {
				chromatogramCSD = chromatogramReader.read(file, monitor);
			} catch(Exception e) {
				chromatogramCSD = createChromatogramFIDFromMSD(file, monitor);
			}
		} else {
			chromatogramCSD = createChromatogramFIDFromMSD(file, monitor);
		}
		return chromatogramCSD;
	}

	@Override
	public IChromatogramCSD read(ZipInputStream zipInputStream, IProgressMonitor monitor) throws IOException {

		IChromatogramCSDZipReader chromatogramReader = null;
		IChromatogramCSD chromatogramCSD = null;
		ReaderHelper readerHelper = new ReaderHelper();
		//
		String version = readerHelper.getVersion(zipInputStream);
		chromatogramReader = getChromatogramReader(version);
		//
		if(chromatogramReader != null) {
			chromatogramCSD = chromatogramReader.read(zipInputStream, monitor);
		}
		//
		return chromatogramCSD;
	}

	private IChromatogramCSDZipReader getChromatogramReader(String version) {

		IChromatogramCSDZipReader chromatogramReader = null;
		//
		if(version.equals(IFormat.VERSION_1001)) {
			chromatogramReader = new ChromatogramReader_1001();
		} else if(version.equals(IFormat.VERSION_1002)) {
			chromatogramReader = new ChromatogramReader_1002();
		} else if(version.equals(IFormat.VERSION_1003)) {
			chromatogramReader = new ChromatogramReader_1003();
		} else if(version.equals(IFormat.VERSION_1004)) {
			chromatogramReader = new ChromatogramReader_1004();
		} else if(version.equals(IFormat.VERSION_1005)) {
			chromatogramReader = new ChromatogramReader_1005();
		} else if(version.equals(IFormat.VERSION_1006)) {
			chromatogramReader = new ChromatogramReader_1006();
		} else if(version.equals(IFormat.VERSION_1007)) {
			chromatogramReader = new ChromatogramReader_1007();
		} else if(version.equals(IFormat.VERSION_1100)) {
			chromatogramReader = new ChromatogramReader_1100();
		}
		//
		return chromatogramReader;
	}

	@SuppressWarnings("unchecked")
	private IChromatogramCSD createChromatogramFIDFromMSD(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramCSD chromatogramFID = null;
		/*
		 * Is the force modus used?
		 */
		if(PreferenceSupplier.isForceLoadAlternateDetector()) {
			ChromatogramReaderMSD chromatogramReaderMSD = new ChromatogramReaderMSD();
			IChromatogramOverview chromatogramOverview = chromatogramReaderMSD.readOverview(file, monitor);
			if(chromatogramOverview instanceof IChromatogram) {
				IChromatogram<? extends IPeak> chromatogram = (IChromatogram<? extends IPeak>)chromatogramOverview;
				chromatogramFID = new VendorChromatogram();
				for(IScan scan : chromatogram.getScans()) {
					IVendorScan scanFID = new VendorScan(scan.getRetentionTime(), scan.getTotalSignal());
					scanFID.setRetentionIndex(scan.getRetentionIndex());
					scanFID.setTimeSegmentId(scan.getTimeSegmentId());
					scanFID.setCycleNumber(scan.getCycleNumber());
					chromatogramFID.addScan(scanFID);
				}
				//
				chromatogramFID.setConverterId(IFormat.CONVERTER_ID);
				File fileConverted = new File(file.getAbsolutePath().replace(".ocb", "-fromMSD.ocb"));
				chromatogramFID.setFile(fileConverted);
				// Delay
				int startRetentionTime = chromatogramFID.getStartRetentionTime();
				int scanDelay = startRetentionTime;
				chromatogramFID.setScanDelay(scanDelay);
				// Interval
				int endRetentionTime = chromatogramFID.getStopRetentionTime();
				int scanInterval = endRetentionTime / chromatogramFID.getNumberOfScans();
				chromatogramFID.setScanInterval(scanInterval);
			}
		}
		//
		return chromatogramFID;
	}
}
