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
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.identifier.chromatogram.IChromatogramTargetMSD;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.MassSpectrometer;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.MassSpectrumType;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class ChromatogramWriter_0701 extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		monitor.subTask(IConstants.EXPORT_CHROMATOGRAM);
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
		writeVersion(zipOutputStream, monitor);
		writeOverviewFolder(zipOutputStream, chromatogram, monitor);
		writeChromatogramFolder(zipOutputStream, chromatogram, monitor);
		/*
		 * Flush and close the output stream.
		 */
		zipOutputStream.flush();
		zipOutputStream.close();
	}

	private void writeVersion(ZipOutputStream zipOutputStream, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Version
		 */
		zipEntry = new ZipEntry(IFormat.FILE_VERSION);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		String version = IFormat.VERSION_0701;
		dataOutputStream.writeInt(version.length()); // Length Version
		dataOutputStream.writeChars(version); // Version
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeOverviewFolder(ZipOutputStream zipOutputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Create the overview folder
		 */
		zipEntry = new ZipEntry(IFormat.DIR_OVERVIEW);
		zipOutputStream.putNextEntry(zipEntry);
		zipOutputStream.closeEntry();
		/*
		 * TIC
		 */
		zipEntry = new ZipEntry(IFormat.FILE_TIC);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		int scans = chromatogram.getNumberOfScans();
		dataOutputStream.writeInt(scans); // Number of Scans
		// Retention Times - Total Signals
		for(int scan = 1; scan <= scans; scan++) {
			monitor.subTask(IConstants.EXPORT_SCAN + scan);
			dataOutputStream.writeInt(chromatogram.getScan(scan).getRetentionTime()); // Retention Time
			dataOutputStream.writeFloat(chromatogram.getScan(scan).getTotalSignal()); // Total Signal
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramFolder(ZipOutputStream zipOutputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		/*
		 * Create the chromatogram folder
		 */
		zipEntry = new ZipEntry(IFormat.DIR_CHROMATOGRAM);
		zipOutputStream.putNextEntry(zipEntry);
		zipOutputStream.closeEntry();
		/*
		 * WRITE THE FILES
		 */
		writeChromatogramScans(zipOutputStream, chromatogram, monitor);
		writeChromatogramPeaks(zipOutputStream, chromatogram, monitor);
		writeChromatogramArea(zipOutputStream, chromatogram, monitor);
		writeChromatogramIdentification(zipOutputStream, chromatogram, monitor);
		writeChromatogramHistory(zipOutputStream, chromatogram, monitor);
		writeChromatogramMiscellaneous(zipOutputStream, chromatogram, monitor);
	}

	private void writeChromatogramScans(ZipOutputStream zipOutputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Scans
		 */
		zipEntry = new ZipEntry(IFormat.FILE_SCANS);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		int scans = chromatogram.getNumberOfScans();
		dataOutputStream.writeInt(scans); // Number of Scans
		// Scans
		for(int scan = 1; scan <= scans; scan++) {
			monitor.subTask(IConstants.EXPORT_SCAN + scan);
			IVendorMassSpectrum massSpectrum = chromatogram.getSupplierScan(scan);
			writeMassSpectrum(dataOutputStream, massSpectrum);
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramPeaks(ZipOutputStream zipOutputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Peaks
		 */
		zipEntry = new ZipEntry(IFormat.FILE_PEAKS);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		List<IChromatogramPeakMSD> peaks = chromatogram.getPeaks();
		dataOutputStream.writeInt(peaks.size()); // Number of Peaks
		// Peaks
		int counter = 1;
		for(IChromatogramPeakMSD peak : peaks) {
			monitor.subTask(IConstants.EXPORT_PEAK + counter++);
			writePeak(dataOutputStream, peak);
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramArea(ZipOutputStream zipOutputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Area
		 */
		zipEntry = new ZipEntry(IFormat.FILE_AREA);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		//
		List<IIntegrationEntry> chromatogramIntegrationEntries = chromatogram.getChromatogramIntegrationEntries();
		writeString(dataOutputStream, chromatogram.getChromatogramIntegratorDescription()); // Chromatogram Integrator Description
		writeIntegrationEntries(dataOutputStream, chromatogramIntegrationEntries);
		//
		List<IIntegrationEntry> backgroundIntegrationEntries = chromatogram.getBackgroundIntegrationEntries();
		writeString(dataOutputStream, chromatogram.getBackgroundIntegratorDescription()); // Background Integrator Description
		writeIntegrationEntries(dataOutputStream, backgroundIntegrationEntries);
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramIdentification(ZipOutputStream zipOutputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Identification
		 */
		zipEntry = new ZipEntry(IFormat.FILE_IDENTIFICATION);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		//
		List<IChromatogramTargetMSD> chromatogramTargets = chromatogram.getTargets();
		dataOutputStream.writeInt(chromatogramTargets.size()); // Number of Targets
		for(IChromatogramTargetMSD chromatogramTarget : chromatogramTargets) {
			if(chromatogramTarget instanceof IIdentificationTarget) {
				IIdentificationTarget identificationEntry = (IIdentificationTarget)chromatogramTarget;
				writeIdentificationEntry(dataOutputStream, identificationEntry);
			}
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramHistory(ZipOutputStream zipOutputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Edit-History
		 */
		zipEntry = new ZipEntry(IFormat.FILE_HISTORY);
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

	private void writeChromatogramMiscellaneous(ZipOutputStream zipOutputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Miscellaneous
		 */
		zipEntry = new ZipEntry(IFormat.FILE_MISC);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		//
		dataOutputStream.writeLong(chromatogram.getDate().getTime()); // Date
		writeString(dataOutputStream, chromatogram.getMiscInfo()); // Miscellaneous Info
		writeString(dataOutputStream, chromatogram.getOperator()); // Operator
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeMassSpectrum(DataOutputStream dataOutputStream, IRegularMassSpectrum massSpectrum) throws IOException {

		writeString(dataOutputStream, getMassSpectrometer(massSpectrum.getMassSpectrometer()).toString()); // Mass Spectrometer
		writeString(dataOutputStream, getMassSpectrumType(massSpectrum.getMassSpectrumType()).toString()); // Mass Spectrum Type
		dataOutputStream.writeInt(massSpectrum.getRetentionTime()); // Retention Time
		dataOutputStream.writeFloat(massSpectrum.getRetentionIndex()); // Retention Index
		List<IIon> ions = massSpectrum.getIons();
		writeMassSpectrumIons(dataOutputStream, ions); // Ions
	}

	private void writeMassSpectrumIons(DataOutputStream dataOutputStream, List<IIon> ions) throws IOException {

		dataOutputStream.writeInt(ions.size()); // Number of ions
		for(IIon ion : ions) {
			dataOutputStream.writeDouble(ion.getIon()); // m/z
			dataOutputStream.writeFloat(ion.getAbundance()); // Abundance
		}
	}

	private void writePeak(DataOutputStream dataOutputStream, IPeakMSD peak) throws IOException {

		IPeakModelMSD peakModel = peak.getPeakModel();
		//
		writeString(dataOutputStream, peak.getDetectorDescription()); // Detector Description
		writeString(dataOutputStream, peak.getIntegratorDescription()); // Integrator Description
		writeString(dataOutputStream, peak.getModelDescription()); // Model Description
		writeString(dataOutputStream, peak.getPeakType().toString()); // Peak Type
		//
		dataOutputStream.writeFloat(peakModel.getBackgroundAbundance(peakModel.getStartRetentionTime())); // Start Background Abundance
		dataOutputStream.writeFloat(peakModel.getBackgroundAbundance(peakModel.getStopRetentionTime())); // Stop Background Abundance
		//
		IPeakMassSpectrum massSpectrum = peakModel.getPeakMassSpectrum();
		writeMassSpectrum(dataOutputStream, massSpectrum); // Mass Spectrum
		//
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		dataOutputStream.writeInt(retentionTimes.size()); // Number Retention Times
		for(int retentionTime : retentionTimes) {
			dataOutputStream.writeInt(retentionTime); // Retention Time
			dataOutputStream.writeFloat(peakModel.getPeakAbundance(retentionTime)); // Intensity
		}
		//
		List<? extends IIntegrationEntry> integrationEntries = peak.getIntegrationEntries();
		writeIntegrationEntries(dataOutputStream, integrationEntries);
		//
		List<IPeakTarget> peakTargets = peak.getTargets();
		dataOutputStream.writeInt(peakTargets.size()); // Number Peak Targets
		for(IPeakTarget peakTarget : peakTargets) {
			if(peakTarget instanceof IIdentificationTarget) {
				IIdentificationTarget identificationEntry = (IIdentificationTarget)peakTarget;
				writeIdentificationEntry(dataOutputStream, identificationEntry);
			}
		}
	}

	private void writeIntegrationEntries(DataOutputStream dataOutputStream, List<? extends IIntegrationEntry> integrationEntries) throws IOException {

		dataOutputStream.writeInt(integrationEntries.size()); // Number Integration Entries
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			if(integrationEntry instanceof IIntegrationEntryMSD) {
				/*
				 * It must be a MSD integration entry.
				 */
				IIntegrationEntryMSD integrationEntryMSD = (IIntegrationEntryMSD)integrationEntry;
				dataOutputStream.writeDouble(integrationEntryMSD.getIon()); // m/z
				dataOutputStream.writeDouble(integrationEntryMSD.getIntegratedArea()); // Integrated Area
			}
		}
	}

	private void writeIdentificationEntry(DataOutputStream dataOutputStream, IIdentificationTarget identificationEntry) throws IOException {

		ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
		IComparisonResult comparisonResult = identificationEntry.getComparisonResult();
		//
		writeString(dataOutputStream, identificationEntry.getIdentifier()); // Identifier
		//
		writeString(dataOutputStream, libraryInformation.getCasNumber()); // CAS-Number
		writeString(dataOutputStream, libraryInformation.getComments()); // Comments
		writeString(dataOutputStream, libraryInformation.getMiscellaneous()); // Miscellaneous
		writeString(dataOutputStream, libraryInformation.getName()); // Name
		//
		dataOutputStream.writeFloat(comparisonResult.getMatchFactor()); // Match Factor
		dataOutputStream.writeFloat(comparisonResult.getReverseMatchFactor()); // Reverse Match Factor
		dataOutputStream.writeFloat(comparisonResult.getProbability()); // Probability
	}

	private void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		dataOutputStream.writeInt(value.length()); // Value Length
		dataOutputStream.writeChars(value); // Value
	}

	private MassSpectrometer getMassSpectrometer(short massSpectrometer) {

		MassSpectrometer detector;
		switch(massSpectrometer) {
			case 1:
				detector = MassSpectrometer.MS1;
				break;
			case 2:
				detector = MassSpectrometer.MS2;
				break;
			case 3:
				detector = MassSpectrometer.MS3;
				break;
			default:
				detector = MassSpectrometer.MS1;
				break;
		}
		return detector;
	}

	private MassSpectrumType getMassSpectrumType(short massSpectrumType) {

		MassSpectrumType type = MassSpectrumType.CENTROID;
		if(massSpectrumType == 1) {
			type = MassSpectrumType.PROFILE;
		}
		return type;
	}
}
