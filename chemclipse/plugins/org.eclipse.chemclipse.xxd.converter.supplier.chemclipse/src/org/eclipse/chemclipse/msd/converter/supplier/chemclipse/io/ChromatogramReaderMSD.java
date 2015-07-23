/*******************************************************************************
 * Copyright (c) 2013, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.io.ChromatogramReaderFID;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_0701;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_0801;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_0802;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_0803;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_0901;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_0902;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_0903;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1001;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1002;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1003;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1004;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.ReaderHelper;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class ChromatogramReaderMSD extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

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
		IChromatogramMSDReader chromatogramReader = null;
		if(version.equals(IFormat.VERSION_0701)) {
			chromatogramReader = new ChromatogramReader_0701();
		} else if(version.equals(IFormat.VERSION_0801)) {
			chromatogramReader = new ChromatogramReader_0801();
		} else if(version.equals(IFormat.VERSION_0802)) {
			chromatogramReader = new ChromatogramReader_0802();
		} else if(version.equals(IFormat.VERSION_0803)) {
			chromatogramReader = new ChromatogramReader_0803();
		} else if(version.equals(IFormat.VERSION_0901)) {
			chromatogramReader = new ChromatogramReader_0901();
		} else if(version.equals(IFormat.VERSION_0902)) {
			chromatogramReader = new ChromatogramReader_0902();
		} else if(version.equals(IFormat.VERSION_0903)) {
			chromatogramReader = new ChromatogramReader_0903();
		} else if(version.equals(IFormat.VERSION_1001)) {
			chromatogramReader = new ChromatogramReader_1001();
		} else if(version.equals(IFormat.VERSION_1002)) {
			chromatogramReader = new ChromatogramReader_1002();
		} else if(version.equals(IFormat.VERSION_1003)) {
			chromatogramReader = new ChromatogramReader_1003();
		} else if(version.equals(IFormat.VERSION_1004)) {
			chromatogramReader = new ChromatogramReader_1004();
		}
		//
		if(chromatogramReader != null) {
			try {
				chromatogramOverview = chromatogramReader.readOverview(file, monitor);
			} catch(Exception e) {
				chromatogramOverview = createChromatogramMSDFromFID(AbstractIon.TIC_ION, file, monitor);
			}
		} else {
			chromatogramOverview = createChromatogramMSDFromFID(AbstractIon.TIC_ION, file, monitor);
		}
		return chromatogramOverview;
	}

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSD chromatogramMSD;
		ReaderHelper readerHelper = new ReaderHelper();
		String version = readerHelper.getVersion(file);
		/*
		 * It's used to support older versions of
		 * the *.ocb format.
		 */
		IChromatogramMSDReader chromatogramReader = null;
		if(version.equals(IFormat.VERSION_0701)) {
			chromatogramReader = new ChromatogramReader_0701();
		} else if(version.equals(IFormat.VERSION_0801)) {
			chromatogramReader = new ChromatogramReader_0801();
		} else if(version.equals(IFormat.VERSION_0802)) {
			chromatogramReader = new ChromatogramReader_0802();
		} else if(version.equals(IFormat.VERSION_0803)) {
			chromatogramReader = new ChromatogramReader_0803();
		} else if(version.equals(IFormat.VERSION_0901)) {
			chromatogramReader = new ChromatogramReader_0901();
		} else if(version.equals(IFormat.VERSION_0902)) {
			chromatogramReader = new ChromatogramReader_0902();
		} else if(version.equals(IFormat.VERSION_0903)) {
			chromatogramReader = new ChromatogramReader_0903();
		} else if(version.equals(IFormat.VERSION_1001)) {
			chromatogramReader = new ChromatogramReader_1001();
		} else if(version.equals(IFormat.VERSION_1002)) {
			chromatogramReader = new ChromatogramReader_1002();
		} else if(version.equals(IFormat.VERSION_1003)) {
			chromatogramReader = new ChromatogramReader_1003();
		} else if(version.equals(IFormat.VERSION_1004)) {
			chromatogramReader = new ChromatogramReader_1004();
		}
		//
		if(chromatogramReader != null) {
			try {
				chromatogramMSD = chromatogramReader.read(file, monitor);
			} catch(Exception e) {
				chromatogramMSD = createChromatogramMSDFromFID(18.0d, file, monitor);
			}
		} else {
			chromatogramMSD = createChromatogramMSDFromFID(18.0d, file, monitor);
		}
		/*
		 * Load scan proxies in the background on demand.
		 * Only load the proxies if the file size is bigger than a
		 * minimum value.
		 */
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		boolean useScanProxies = preferences.getBoolean(PreferenceSupplier.P_USE_SCAN_PROXIES, PreferenceSupplier.DEF_USE_SCAN_PROXIES);
		boolean loadScanProxiesInBackground = preferences.getBoolean(PreferenceSupplier.P_LOAD_SCAN_PROXIES_IN_BACKGROUND, PreferenceSupplier.DEF_LOAD_SCAN_PROXIES_IN_BACKGROUND);
		int minBytesToLoadInBackground = preferences.getInt(PreferenceSupplier.P_MIN_BYTES_TO_LOAD_IN_BACKGROUND, PreferenceSupplier.DEF_MIN_BYTES_TO_LOAD_IN_BACKGROUND);
		//
		if(useScanProxies) {
			if(loadScanProxiesInBackground && file.length() > minBytesToLoadInBackground) {
				/*
				 * Using the thread could lead to a
				 * java.util.ConcurrentModificationException
				 * if scans are deleted before they are loaded.
				 * We should find a way to handle this.
				 */
				final IChromatogramMSD chromatogram = chromatogramMSD;
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {

						chromatogram.enforceLoadScanProxies(new NullProgressMonitor());
					}
				});
				t.start();
			}
		}
		//
		return chromatogramMSD;
	}

	private IChromatogramMSD createChromatogramMSDFromFID(double mz, File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSD chromatogramMSD = null;
		//
		ChromatogramReaderFID chromatogramReaderFID = new ChromatogramReaderFID();
		IChromatogramCSD chromatogramFID = chromatogramReaderFID.read(file, monitor);
		if(chromatogramFID != null) {
			chromatogramMSD = new VendorChromatogram();
			for(IScan scan : chromatogramFID.getScans()) {
				IVendorScan massSpectrum = new VendorScan();
				massSpectrum.setRetentionTime(scan.getRetentionTime());
				massSpectrum.setRetentionIndex(scan.getRetentionIndex());
				try {
					IVendorIon ion = new VendorIon(mz, scan.getTotalSignal());
					massSpectrum.addIon(ion);
				} catch(Exception e1) {
					//
				}
				chromatogramMSD.addScan(massSpectrum);
			}
			//
			chromatogramMSD.setConverterId(IFormat.CONVERTER_ID);
			File fileConverted = new File(file.getAbsolutePath().replace(".ocb", "-fromFID.ocb"));
			chromatogramMSD.setFile(fileConverted);
			// Delay
			int startRetentionTime = chromatogramMSD.getStartRetentionTime();
			int scanDelay = startRetentionTime;
			chromatogramMSD.setScanDelay(scanDelay);
			// Interval
			int endRetentionTime = chromatogramMSD.getStopRetentionTime();
			int scanInterval = endRetentionTime / chromatogramMSD.getNumberOfScans();
			chromatogramMSD.setScanInterval(scanInterval);
		}
		//
		return chromatogramMSD;
	}
}
