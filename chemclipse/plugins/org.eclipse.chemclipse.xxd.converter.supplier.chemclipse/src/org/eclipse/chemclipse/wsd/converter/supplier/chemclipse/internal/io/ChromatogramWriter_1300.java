/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.internal.io;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.io.ChromatogramWriterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMethod;
import org.eclipse.chemclipse.model.core.RetentionIndexType;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io.ChromatogramWriterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.io.ChromatogramWriterWSD;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.io.IChromatogramWSDZipWriter;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class ChromatogramWriter_1300 extends AbstractChromatogramWriter implements IChromatogramWSDZipWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		// monitor.subTask(IConstants.EXPORT_CHROMATOGRAM);
		/*
		 * ZIP
		 */
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
		zipOutputStream.setLevel(PreferenceSupplier.getCompressionLevel());
		zipOutputStream.setMethod(IFormat.METHOD);
		/*
		 * Write the data
		 */
		writeChromatogram(zipOutputStream, "", chromatogram, monitor);
		/*
		 * Flush and close the output stream.
		 */
		zipOutputStream.flush();
		zipOutputStream.close();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void writeChromatogram(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		writeVersion(zipOutputStream, directoryPrefix, monitor);
		writeOverviewFolder(zipOutputStream, directoryPrefix, chromatogram, monitor);
		writeChromatogramFolder(zipOutputStream, directoryPrefix, chromatogram, monitor);
		/*
		 * Referenced Chromatograms
		 */
		List<IChromatogram> referencedChromatograms = chromatogram.getReferencedChromatograms();
		writeChromatogramReferenceInfo(zipOutputStream, directoryPrefix, referencedChromatograms, monitor);
		writeReferencedChromatograms(zipOutputStream, directoryPrefix, referencedChromatograms, monitor);
	}

	private void writeVersion(ZipOutputStream zipOutputStream, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Version
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_VERSION);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		String version = IFormat.VERSION_1300;
		dataOutputStream.writeInt(version.length()); // Length Version
		dataOutputStream.writeChars(version); // Version
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeOverviewFolder(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Create the overview folder
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.DIR_OVERVIEW_WSD);
		zipOutputStream.putNextEntry(zipEntry);
		zipOutputStream.closeEntry();
		/*
		 * TIC
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_TIC_WSD);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		int scans = chromatogram.getNumberOfScans();
		dataOutputStream.writeInt(scans); // Number of Scans
		// Retention Times - Total Signals
		for(int scan = 1; scan <= scans; scan++) {
			IScanWSD scanWSD = chromatogram.getSupplierScan(scan);
			dataOutputStream.writeFloat(scanWSD.getTotalSignal());
			dataOutputStream.writeInt(scanWSD.getRetentionTime());
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramFolder(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		SubMonitor subMonitor = SubMonitor.convert(monitor, "Write Chromatogram", 100);
		try {
			/*
			 * Create the chromatogram folder
			 */
			ZipEntry zipEntry = new ZipEntry(directoryPrefix + IFormat.DIR_CHROMATOGRAM_WSD);
			zipOutputStream.putNextEntry(zipEntry);
			zipOutputStream.closeEntry();
			/*
			 * WRITE THE FILES
			 */
			writeChromatogramMethod(zipOutputStream, directoryPrefix, chromatogram, monitor);
			subMonitor.worked(20);
			writeChromatogramScans(zipOutputStream, directoryPrefix, chromatogram, monitor);
			subMonitor.worked(20);
			writeChromatogramBaseline(zipOutputStream, directoryPrefix, chromatogram, monitor);
			subMonitor.worked(20);
			writeChromatogramHistory(zipOutputStream, directoryPrefix, chromatogram, monitor);
			subMonitor.worked(20);
			writeChromatogramMiscellaneous(zipOutputStream, directoryPrefix, chromatogram, monitor);
			writeSeparationColumn(zipOutputStream, directoryPrefix, chromatogram, monitor);
			subMonitor.worked(20);
		} finally {
			SubMonitor.done(monitor);
		}
	}

	private void writeChromatogramMethod(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Edit-History
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_SYSTEM_SETTINGS_WSD);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		IMethod method = chromatogram.getMethod();
		//
		writeString(dataOutputStream, method.getInstrumentName());
		writeString(dataOutputStream, method.getIonSource());
		dataOutputStream.writeDouble(method.getSamplingRate());
		dataOutputStream.writeInt(method.getSolventDelay());
		dataOutputStream.writeDouble(method.getSourceHeater());
		writeString(dataOutputStream, method.getStopMode());
		dataOutputStream.writeInt(method.getStopTime());
		dataOutputStream.writeInt(method.getTimeFilterPeakWidth());
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramScans(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_SCANS_WSD);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		int scans = chromatogram.getNumberOfScans();
		dataOutputStream.writeInt(scans);
		for(int scan = 1; scan <= scans; scan++) {
			// monitor.subTask(IConstants.EXPORT_SCANS + scan);
			IScanWSD scanWSD = chromatogram.getSupplierScan(scan);
			int scanSignalTotal = scanWSD.getScanSignals().size();
			dataOutputStream.writeInt(scanSignalTotal);
			for(int signal = 0; signal < scanSignalTotal; signal++) {
				IScanSignalWSD scanSignal = scanWSD.getScanSignal(signal);
				double wavelength = scanSignal.getWavelength();
				float abundance = scanSignal.getAbundance();
				dataOutputStream.writeDouble(wavelength);
				dataOutputStream.writeFloat(abundance);
			}
			dataOutputStream.writeInt(scanWSD.getRetentionTime()); // Retention Time
			dataOutputStream.writeInt(scanWSD.getRelativeRetentionTime());
			dataOutputStream.writeInt(scanWSD.getRetentionTimeColumn1());
			dataOutputStream.writeInt(scanWSD.getRetentionTimeColumn2());
			dataOutputStream.writeFloat(scanWSD.getRetentionIndex()); // Retention Index
			dataOutputStream.writeBoolean(scanWSD.hasAdditionalRetentionIndices());
			if(scanWSD.hasAdditionalRetentionIndices()) {
				Map<RetentionIndexType, Float> retentionIndicesTyped = scanWSD.getRetentionIndicesTyped();
				dataOutputStream.writeInt(retentionIndicesTyped.size());
				for(Map.Entry<RetentionIndexType, Float> retentionIndexTyped : retentionIndicesTyped.entrySet()) {
					writeString(dataOutputStream, retentionIndexTyped.getKey().toString());
					dataOutputStream.writeFloat(retentionIndexTyped.getValue());
				}
			}
			dataOutputStream.writeFloat(scanWSD.getTotalSignal()); // Total Signal
			dataOutputStream.writeInt(scanWSD.getTimeSegmentId()); // Time Segment Id
			dataOutputStream.writeInt(scanWSD.getCycleNumber()); // Cycle Number
		}
		// clean up flush the stream and close zip-entry 1
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramBaseline(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Baseline
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_BASELINE_WSD);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		int scans = chromatogram.getNumberOfScans();
		dataOutputStream.writeInt(scans); // Number of Scans
		//
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		// Scans
		for(int scan = 1; scan <= scans; scan++) {
			// monitor.subTask(IConstants.EXPORT_BASELINE + scan);
			int retentionTime = chromatogram.getSupplierScan(scan).getRetentionTime();
			float backgroundAbundance = baselineModel.getBackgroundAbundance(retentionTime);
			dataOutputStream.writeInt(retentionTime); // Retention Time
			dataOutputStream.writeFloat(backgroundAbundance); // Background Abundance
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramHistory(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Edit-History
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_HISTORY_WSD);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		IEditHistory editHistory = chromatogram.getEditHistory();
		dataOutputStream.writeInt(editHistory.getHistoryList().size()); // Number of entries
		// Date, Description
		for(IEditInformation editInformation : editHistory.getHistoryList()) {
			dataOutputStream.writeLong(editInformation.getDate().getTime()); // Date
			writeString(dataOutputStream, editInformation.getDescription()); // Description
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramMiscellaneous(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Miscellaneous
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_MISC_WSD);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		//
		Map<String, String> headerData = chromatogram.getHeaderDataMap();
		dataOutputStream.writeInt(headerData.size());
		for(Map.Entry<String, String> data : headerData.entrySet()) {
			writeString(dataOutputStream, data.getKey());
			writeString(dataOutputStream, data.getValue());
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeSeparationColumn(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		//
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_SEPARATION_COLUMN_WSD);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		//
		ISeparationColumnIndices separationColumnIndices = chromatogram.getSeparationColumnIndices();
		dataOutputStream.writeInt(separationColumnIndices.size());
		for(Map.Entry<Integer, IRetentionIndexEntry> entry : separationColumnIndices.entrySet()) {
			IRetentionIndexEntry retentionIndexEntry = (IRetentionIndexEntry)entry;
			writeString(dataOutputStream, retentionIndexEntry.getName());
			dataOutputStream.writeInt(retentionIndexEntry.getRetentionTime());
			dataOutputStream.writeFloat(retentionIndexEntry.getRetentionIndex());
		}
		//
		ISeparationColumn separationColumn = separationColumnIndices.getSeparationColumn();
		writeString(dataOutputStream, separationColumn.getName());
		writeString(dataOutputStream, separationColumn.getLength());
		writeString(dataOutputStream, separationColumn.getDiameter());
		writeString(dataOutputStream, separationColumn.getPhase());
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	@SuppressWarnings("rawtypes")
	private void writeChromatogramReferenceInfo(ZipOutputStream zipOutputStream, String directoryPrefix, List<IChromatogram> referencedChromatograms, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntryType = new ZipEntry(directoryPrefix + IFormat.FILE_REFERENCE_INFO);
		zipOutputStream.putNextEntry(zipEntryType);
		DataOutputStream dataOutputStream = new DataOutputStream(zipOutputStream);
		dataOutputStream.writeInt(referencedChromatograms.size());
		zipOutputStream.closeEntry();
	}

	@SuppressWarnings("rawtypes")
	private void writeReferencedChromatograms(ZipOutputStream zipOutputStream, String directoryPrefix, List<IChromatogram> referencedChromatograms, IProgressMonitor monitor) throws IOException {

		ChromatogramWriterMSD chromatogramWriterMSD = new ChromatogramWriterMSD();
		ChromatogramWriterCSD chromatogramWriterCSD = new ChromatogramWriterCSD();
		ChromatogramWriterWSD chromatogramWriterWSD = new ChromatogramWriterWSD();
		//
		int i = 0;
		for(IChromatogram referencedChromatogram : referencedChromatograms) {
			/*
			 * Create the measurement folder.
			 */
			directoryPrefix = directoryPrefix + IFormat.DIR_CHROMATOGRAM_REFERENCE + IFormat.CHROMATOGRAM_REFERENCE_SEPARATOR + i++ + IFormat.DIR_SEPARATOR;
			ZipEntry zipEntryType = new ZipEntry(directoryPrefix + IFormat.FILE_CHROMATOGRAM_TYPE);
			zipOutputStream.putNextEntry(zipEntryType);
			DataOutputStream dataOutputStream = new DataOutputStream(zipOutputStream);
			//
			if(referencedChromatogram instanceof IChromatogramMSD) {
				/*
				 * MSD
				 */
				writeString(dataOutputStream, IFormat.DATA_TYPE_MSD);
				dataOutputStream.flush();
				//
				directoryPrefix = directoryPrefix + IFormat.DIR_CHROMATOGRAM_REFERENCE + IFormat.DIR_SEPARATOR;
				ZipEntry zipEntryChromtogram = new ZipEntry(directoryPrefix);
				zipOutputStream.putNextEntry(zipEntryChromtogram);
				chromatogramWriterMSD.writeChromatogram(zipOutputStream, directoryPrefix, (IChromatogramMSD)referencedChromatogram, monitor);
			} else if(referencedChromatogram instanceof IChromatogramCSD) {
				/*
				 * CSD
				 */
				writeString(dataOutputStream, IFormat.DATA_TYPE_CSD);
				dataOutputStream.flush();
				//
				directoryPrefix = directoryPrefix + IFormat.DIR_CHROMATOGRAM_REFERENCE + IFormat.DIR_SEPARATOR;
				ZipEntry zipEntryChromtogram = new ZipEntry(directoryPrefix);
				zipOutputStream.putNextEntry(zipEntryChromtogram);
				chromatogramWriterCSD.writeChromatogram(zipOutputStream, directoryPrefix, (IChromatogramCSD)referencedChromatogram, monitor);
			} else if(referencedChromatogram instanceof IChromatogramWSD) {
				/*
				 * WSD
				 */
				writeString(dataOutputStream, IFormat.DATA_TYPE_WSD);
				dataOutputStream.flush();
				//
				directoryPrefix = directoryPrefix + IFormat.DIR_CHROMATOGRAM_REFERENCE + IFormat.DIR_SEPARATOR;
				ZipEntry zipEntryChromtogram = new ZipEntry(directoryPrefix);
				zipOutputStream.putNextEntry(zipEntryChromtogram);
				chromatogramWriterWSD.writeChromatogram(zipOutputStream, directoryPrefix, (IChromatogramWSD)referencedChromatogram, monitor);
			}
			//
			zipOutputStream.closeEntry();
		}
	}

	private void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		dataOutputStream.writeInt(value.length()); // Value Length
		dataOutputStream.writeChars(value); // Value
	}
}
