/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core.MassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core.PeakIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFileIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IVendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IVendorPeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.VendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.VendorPeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.TargetBuilder;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.PathResolver;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.IBasePeakSettings;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.MassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class BasePeakIdentifier {

	private static final Logger logger = Logger.getLogger(BasePeakIdentifier.class);
	//
	private static final String IDENTIFIER = "BASEPEAK-IDENT";
	private static final String MASS_SPECTRUM_COMPARATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.incos";
	//
	private static final String SYRINGYL = "Syringyl";
	private static final String GUAIACYL = "Guaiacyl";
	private static final String PHYDROXYPHENYL = "p-Hydroxyphenyl";
	private static final String CARBOHYDRATE = "Carbohydrate";
	// private static final String ESSTOGEERATIO = "S/G Ratio";
	private static final String NOTFOUND = "Not Found (BasePeak Identifier)";
	private static final ArrayList<Integer> syringylBaseMZs = new ArrayList<Integer>();
	private static final ArrayList<Integer> guaiacylBaseMZs = new ArrayList<Integer>();
	private static final ArrayList<Integer> pHydroxyPhenylBaseMZs = new ArrayList<Integer>();
	private static final ArrayList<Integer> carbohydrateBaseMZs = new ArrayList<Integer>();
	//
	private TargetBuilder targetBuilder;
	// These one's are run when initializing the class
	private static final IMassSpectra references = getStandardsMassSpectra();
	private static final IScanMSD syringyl = getSyringyl();
	//
	private List<String> massSpectraFiles;
	static {
		Integer[] syringyl = {149, 154, 167, 181, 182, 192, 194, 208, 210};
		Integer[] guaiacyl = {109, 123, 136, 137, 138, 140, 150, 151, 152, 162, 164, 168, 178,};
		Integer[] pHydroxyPhenyl = {94, 107, 108, 120, 121, 124, 134};
		Integer[] carbohydrate = {29, 31, 39, 41, 42, 43, 44, 45, 46, 55, 56, 57, 58, 59, 60, 68, 69, 73, 81, 82, 84, 85, 87, 95, 96, 98, 114, 126, 142,};
		for(Integer currentMZ : syringyl) {
			syringylBaseMZs.add(currentMZ);
		}
		for(Integer currentMZ : guaiacyl) {
			guaiacylBaseMZs.add(currentMZ);
		}
		for(Integer currentMZ : pHydroxyPhenyl) {
			pHydroxyPhenylBaseMZs.add(currentMZ);
		}
		for(Integer currentMZ : carbohydrate) {
			carbohydrateBaseMZs.add(currentMZ);
		}
	}

	public BasePeakIdentifier() {
		targetBuilder = new TargetBuilder();
		massSpectraFiles = new ArrayList<String>();
		massSpectraFiles.add(PathResolver.getAbsolutePath(PathResolver.GERBER_ET_AL_2012));
	}

	public void identifyPeaks(List<IPeakMSD> peaks, IBasePeakSettings settings, IProgressMonitor monitor) {

		List<IPeakMSD> peaksNotFound = new ArrayList<IPeakMSD>();
		for(int i = 0; i < peaks.size(); i++) {
			// current peak to work with
			IPeakMSD peak = peaks.get(i);
			// get MassSpectrum data from peak object
			IScanMSD massSpectrum = peak.getExtractedMassSpectrum();
			// identify the massSpectrum
			String name = getIdentification(massSpectrum, settings, i);
			IMassSpectrumComparisonResult comparisonResult = getComparisonResult();
			// construct a new target object using targetBuilder
			IPeakTarget peakTarget = targetBuilder.getPeakTarget(massSpectrum, comparisonResult, IDENTIFIER);
			setLibraryInformationFields(peakTarget.getLibraryInformation(), name);
			peak.addTarget(peakTarget);
			/*
			 * Grep all not identified peaks.
			 */
			if(peakTarget.getLibraryInformation().getName().equals(NOTFOUND)) {
				peaksNotFound.add(peak);
			}
		}
		/*
		 * Post identify NOTFOUND peaks.
		 */
		IVendorPeakIdentifierSettings peakIdentifierSettings = new VendorPeakIdentifierSettings();
		setIdentifierSettings(peakIdentifierSettings);
		setFileIdentifierSettings(peakIdentifierSettings);
		PeakIdentifier peakIdentifier = new PeakIdentifier();
		peakIdentifier.identify(peaksNotFound, peakIdentifierSettings, monitor);
		//
		for(IPeakMSD peak : peaksNotFound) {
			List<IPeakTarget> peakTargets = peak.getTargets();
			if(containsMoreThanOneBasePeakIdentification(peakTargets)) {
				IPeakTarget peakTargetToRemove = null;
				exitloop:
				for(IPeakTarget peakTarget : peakTargets) {
					ILibraryInformation libraryInformation = peakTarget.getLibraryInformation();
					if(libraryInformation.getName().equals(NOTFOUND)) {
						peakTargetToRemove = peakTarget;
						break exitloop;
					}
				}
				//
				if(peakTargetToRemove != null) {
					peak.removeTarget(peakTargetToRemove);
				}
			}
		}
	}

	public void identifyMassSpectra(List<IScanMSD> massSpectrumList, IBasePeakSettings settings, IProgressMonitor monitor) {

		List<IScanMSD> scansNotFound = new ArrayList<IScanMSD>();
		for(int i = 0; i < massSpectrumList.size(); i++) {
			// current scan to work with
			IScanMSD massSpectrum = massSpectrumList.get(i);
			// identify the scan
			String name = getIdentification(massSpectrum, settings, i);
			IMassSpectrumComparisonResult comparisonResult = getComparisonResult();
			// construct a new target object using targetBuilder and assign the data to it
			IMassSpectrumTarget massSpectrumTarget = targetBuilder.getMassSpectrumTarget(massSpectrum, comparisonResult, IDENTIFIER);
			setLibraryInformationFields(massSpectrumTarget.getLibraryInformation(), name);
			massSpectrum.addTarget(massSpectrumTarget);
			/*
			 * Grep all not identified scans.
			 */
			if(massSpectrumTarget.getLibraryInformation().getName().equals(NOTFOUND)) {
				scansNotFound.add(massSpectrum);
			}
		}
		/*
		 * Post identify NOTFOUND peaks.
		 */
		IVendorMassSpectrumIdentifierSettings massSpectrumIdentifierSettings = new VendorMassSpectrumIdentifierSettings();
		setIdentifierSettings(massSpectrumIdentifierSettings);
		setFileIdentifierSettings(massSpectrumIdentifierSettings);
		MassSpectrumIdentifier massSpectrumIdentifier = new MassSpectrumIdentifier();
		massSpectrumIdentifier.identify(scansNotFound, massSpectrumIdentifierSettings, monitor);
		//
		for(IScanMSD scan : scansNotFound) {
			List<IMassSpectrumTarget> scanTargets = scan.getTargets();
			if(containsMoreThanOneBasePeakIdentificationMassSpectrum(scanTargets)) {
				IMassSpectrumTarget peakTargetToRemove = null;
				exitloop:
				for(IMassSpectrumTarget scanTarget : scanTargets) {
					ILibraryInformation libraryInformation = scanTarget.getLibraryInformation();
					if(libraryInformation.getName().equals(NOTFOUND)) {
						peakTargetToRemove = scanTarget;
						break exitloop;
					}
				}
				//
				if(peakTargetToRemove != null) {
					scan.removeTarget(peakTargetToRemove);
				}
			}
		}
	}

	/**
	 * Returns identified mass spectra from the database.
	 * 
	 * @param identificationTarget
	 * @param monitor
	 * @return {@link IMassSpectra}
	 */
	public IMassSpectra getMassSpectra(IIdentificationTarget identificationTarget, IProgressMonitor monitor) {

		IMassSpectra massSpectra = new MassSpectra();
		if(identificationTarget != null) {
			String identifier = identificationTarget.getIdentifier();
			if(identifier.equals(IDENTIFIER)) {
				String nameTarget = identificationTarget.getLibraryInformation().getName();
				if(nameTarget.equals(SYRINGYL)) {
					/*
					 * Single ion
					 */
					if(syringyl != null) {
						massSpectra.addMassSpectrum(syringyl);
					}
				} else if(nameTarget.equals(GUAIACYL) || nameTarget.equals(PHYDROXYPHENYL)) {
					/*
					 * List mass spectra
					 */
					List<IScanMSD> identifiedMassSpectra = new ArrayList<IScanMSD>();
					if(references != null) {
						for(IScanMSD reference : references.getList()) {
							/*
							 * Search for Guaiacyl and Lorenzyl
							 */
							if(reference instanceof ILibraryMassSpectrum) {
								ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)reference;
								String nameReference = libraryMassSpectrum.getLibraryInformation().getName();
								if(nameReference.equals(nameTarget)) {
									identifiedMassSpectra.add(reference);
								}
							}
						}
					}
					/*
					 * Only add the list if targets have been found.
					 */
					if(identifiedMassSpectra.size() > 0) {
						massSpectra.addMassSpectra(identifiedMassSpectra);
					}
				}
			}
		}
		//
		return massSpectra;
	}

	private static IScanMSD getSyringyl() {

		IScanMSD massSpectrum = null;
		try {
			massSpectrum = new ScanMSD();
			massSpectrum.addIon(new Ion(156.0d, 1000.0f));
		} catch(AbundanceLimitExceededException e) {
			logger.warn(e);
		} catch(IonLimitExceededException e) {
			logger.warn(e);
		}
		return massSpectrum;
	}

	private static IMassSpectra getStandardsMassSpectra() {

		File file = new File(PathResolver.getAbsolutePath(PathResolver.REFERENCES));
		IMassSpectrumImportConverterProcessingInfo processingInfo = MassSpectrumConverter.convert(file, new NullProgressMonitor());
		IMassSpectra massSpectra = processingInfo.getMassSpectra();
		return massSpectra;
	}

	private void setLibraryInformationFields(ILibraryInformation libraryInformation, String name) {

		// TODO implement library info
		libraryInformation.setName(name);
		libraryInformation.setMiscellaneous("");
		libraryInformation.setComments("");
		libraryInformation.setCasNumber("");
	}

	private String getIdentification(IScanMSD massSpectrum, IBasePeakSettings settings, int index) {

		int basePeak = (int)massSpectrum.getBasePeak();
		if(syringylBaseMZs.contains(basePeak)) {
			return SYRINGYL;
		} else if(guaiacylBaseMZs.contains(basePeak)) {
			return GUAIACYL;
		} else if(pHydroxyPhenylBaseMZs.contains(basePeak)) {
			return PHYDROXYPHENYL;
		} else if(carbohydrateBaseMZs.contains(basePeak)) {
			return CARBOHYDRATE;
		} else
			return NOTFOUND;
	}

	private IMassSpectrumComparisonResult getComparisonResult() {

		return new MassSpectrumComparisonResult(100.0f, 100.0f, 100.0f, 100.0f);
	}

	private void setIdentifierSettings(IIdentifierSettings identifierSettings) {

		identifierSettings.setMassSpectrumComparatorId(MASS_SPECTRUM_COMPARATOR_ID);
		identifierSettings.setPenaltyCalculation(IIdentifierSettings.PENALTY_CALCULATION_NONE);
		identifierSettings.setPenaltyCalculationLevelFactor(0.0f);
		identifierSettings.setMaxPenalty(0.0f);
		identifierSettings.setRetentionTimeWindow(0);
		identifierSettings.setRetentionIndexWindow(0.0f);
	}

	private void setFileIdentifierSettings(IFileIdentifierSettings fileIdentifierSettings) {

		fileIdentifierSettings.setMassSpectraFiles(massSpectraFiles);
		fileIdentifierSettings.setUsePreOptimization(false);
		fileIdentifierSettings.setThresholdPreOptimization(0.1d);
		fileIdentifierSettings.setNumberOfTargets(10);
		fileIdentifierSettings.setMinMatchFactor(70.0f);
		fileIdentifierSettings.setMinReverseMatchFactor(70.0f);
		fileIdentifierSettings.setAddUnknownMzListTarget(false);
		fileIdentifierSettings.setAlternateIdentifierId(IDENTIFIER);
	}

	private boolean containsMoreThanOneBasePeakIdentification(List<IPeakTarget> peakTargets) {

		int counter = 0;
		for(IPeakTarget peakTarget : peakTargets) {
			if(peakTarget.getIdentifier().equals(IDENTIFIER)) {
				counter++;
				if(counter > 1) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean containsMoreThanOneBasePeakIdentificationMassSpectrum(List<IMassSpectrumTarget> massSpectrumTargets) {

		int counter = 0;
		for(IMassSpectrumTarget peakTarget : massSpectrumTargets) {
			if(peakTarget.getIdentifier().equals(IDENTIFIER)) {
				counter++;
				if(counter > 1) {
					return true;
				}
			}
		}
		return false;
	}
}
