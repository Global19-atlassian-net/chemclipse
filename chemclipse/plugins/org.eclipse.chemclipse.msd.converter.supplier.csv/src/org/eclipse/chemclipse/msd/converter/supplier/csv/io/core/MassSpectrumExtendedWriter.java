/*******************************************************************************
 * Copyright (c) 2016, 2017 Matthias Mailänder.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.io.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.converter.supplier.csv.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IScanTargetMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;

/**
 * Writes a simple peaklist into a text file.
 */
public class MassSpectrumExtendedWriter implements IMassSpectraWriter {

	private static final Logger logger = Logger.getLogger(MassSpectrumExtendedWriter.class);
	/*
	 * Don't modify this instance.
	 */
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
	private TargetExtendedComparator targetExtendedComparator;

	public MassSpectrumExtendedWriter() {
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

	@Override
	public void writeMassSpectrum(FileWriter fileWriter, IScanMSD massSpectrum, IProgressMonitor monitor) throws IOException {

		MassSpectra massSpectra = new MassSpectra();
		massSpectra.addMassSpectrum(massSpectrum);
		writeMassSpectrumToCsv(massSpectra, fileWriter);
	}

	@Override
	public void write(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		MassSpectra massSpectra = new MassSpectra();
		massSpectra.addMassSpectrum(massSpectrum);
		writeMassSpectrumToCsv(massSpectra, fileWriter);
	}

	@Override
	public void write(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		writeMassSpectrumToCsv(massSpectra, fileWriter);
	}

	private void writeMassSpectrumToCsv(IMassSpectra massSpectra, FileWriter fileWriter) throws IOException {

		if(massSpectra != null) {
			ICsvListWriter csvListWriter = null;
			try {
				csvListWriter = new CsvListWriter(fileWriter, PreferenceSupplier.getCsvPreference());
				csvListWriter.writeHeader("Retention Time", "Retention Index", "Base Peak", "Base Peak Abundance", "Number of Ions", "Name", "CAS", "MW", "Formula", "Reference Identifier");
				//
				for(IScanMSD massSpectrum : massSpectra.getList()) {
					/*
					 * Library Information
					 */
					ILibraryInformation libraryInformation = null;
					if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
						/*
						 * Library Entry
						 */
						IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
						libraryInformation = libraryMassSpectrum.getLibraryInformation();
					} else {
						libraryInformation = getLibraryInformation(massSpectrum.getTargets());
					}
					/*
					 * Try to make a deep copy and normalize.
					 */
					IScanMSD massSpectrumExport;
					try {
						massSpectrumExport = massSpectrum.makeDeepCopy();
						massSpectrumExport.normalize(1000.0f);
					} catch(CloneNotSupportedException e) {
						logger.warn(e);
						massSpectrumExport = massSpectrum;
					}
					//
					int retentionIndexNoPrecision = (int)massSpectrum.getRetentionIndex();
					int basePeakNoPrecision = (int)massSpectrum.getBasePeak();
					int basePeakAbundanceNoPrecision = (int)massSpectrum.getBasePeakAbundance();
					//
					String retentionTime = (massSpectrum.getRetentionTime() == 0) ? "0" : decimalFormat.format(massSpectrum.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					String retentionIndex = (retentionIndexNoPrecision == massSpectrum.getRetentionIndex()) ? Integer.toString(retentionIndexNoPrecision) : decimalFormat.format(massSpectrum.getRetentionIndex());
					String basePeak = (basePeakNoPrecision == massSpectrum.getBasePeak()) ? Integer.toString(basePeakNoPrecision) : decimalFormat.format(massSpectrum.getBasePeak());
					String basePeakAbundance = (basePeakAbundanceNoPrecision == massSpectrum.getBasePeakAbundance()) ? Integer.toString(basePeakAbundanceNoPrecision) : decimalFormat.format(massSpectrum.getBasePeakAbundance());
					String numberOfIons = Integer.toString(massSpectrum.getNumberOfIons());
					String name = (libraryInformation != null) ? libraryInformation.getName() : "";
					String cas = (libraryInformation != null) ? libraryInformation.getCasNumber() : "";
					String mw = getMolWeight(libraryInformation);
					String formula = (libraryInformation != null) ? libraryInformation.getFormula() : "";
					String referenceIdentifier = (libraryInformation != null) ? libraryInformation.getReferenceIdentifier() : "";
					//
					csvListWriter.write(retentionTime, retentionIndex, retentionIndex, basePeak, basePeakAbundance, numberOfIons, name, cas, mw, formula, referenceIdentifier);
				}
			} finally {
				if(csvListWriter != null) {
					csvListWriter.close();
				}
			}
		}
	}

	private String getMolWeight(ILibraryInformation libraryInformation) {

		if(libraryInformation != null) {
			int molWeightNoPrecision = (int)libraryInformation.getMolWeight();
			if(molWeightNoPrecision == libraryInformation.getMolWeight()) {
				return Integer.toString(molWeightNoPrecision);
			} else {
				return decimalFormat.format(libraryInformation.getMolWeight());
			}
		}
		return "";
	}

	private ILibraryInformation getLibraryInformation(List<IScanTargetMSD> targets) {

		ILibraryInformation libraryInformation = null;
		targets = new ArrayList<>(targets);
		Collections.sort(targets, targetExtendedComparator);
		if(targets.size() >= 1) {
			libraryInformation = targets.get(0).getLibraryInformation();
		}
		return libraryInformation;
	}
}
