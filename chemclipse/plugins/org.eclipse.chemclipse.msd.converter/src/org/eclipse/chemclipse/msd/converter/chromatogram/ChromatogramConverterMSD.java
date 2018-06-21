/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.chromatogram;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.chromatogram.ChromatogramSupplier;
import org.eclipse.chemclipse.converter.core.Converter;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.processing.chromatogram.ChromatogramOverviewImportConverterProcessingInfo;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramOverviewImportConverterProcessingInfo;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

/**
 * This class offers only static methods for file and chromatogram conversion.<br/>
 * A specific conversion converter can be used to get an IChromatogram or an
 * File object.<br/>
 * You can also use conversion converter where the type of chromatogram supplier
 * is not known. The method will test all possible converters. <br/>
 * <br/>
 * The goal is to implement as many chromatogram converters as possible.<br/>
 * Be aware that some extensions are directories and some are files. The
 * following example gives some impressions:<br/>
 * Agilent ChemStation *.D<br/>
 * Agilent MS Engine *.MS<br/>
 * Bruker *.MSF<br/>
 * Finnigan GCQ *.MS<br/>
 * Finnigan INCOS *.MI<br/>
 * Finnigan ITDS *.DAT<br/>
 * INFICON GCMS *.acq<br/>
 * JEOL/Schrader *.lrp<br/>
 * Kratos Mach 3 *.run<br/>
 * MassLynx NT<br/>
 * NetCDF *.CDF<br/>
 * Perkin Elmer TurboMass *.raw<br/>
 * Shimadzu *.R##<br/>
 * Schrader/GCMate *.lrp<br/>
 * Varian *.MS<br/>
 * Varian SMS *.sms<br/>
 * Varian XMS *.xms<br/>
 * XCalibur Raw *.raw <br/>
 * <br/>
 * There are many more supplier. Here a list, just to give you an impression.<br/>
 * Anelva AGS-7000 Anelva DOS Balzers QuadStar 420 Balzers QuadStar 421 EPA
 * Extrel Merlin Finnigan Incos Finnigan ITS80 Finnigan ITS40 Finnigan GCQ<br/>
 * Finnigan Magnum Finnigan MassLab Finnigan SSX Fisons/VG 11-250 Fisons/VG
 * JCAMP Fisons/VG Lab Base/Trio Fisons/VG MassLab Fisons/VG Thermolab Hitachi
 * NEC9801 Hitachi 3DQ<br/>
 * HP FirstDerivative (Agilent) HP RTE JEOL Automass JEOL Complement JEOL DA5000
 * JEOL DA6000 JEOL GCMate JEOL JCAMP JEOL K9 JEOL MARIO<br/>
 * Kratos DS90 Kratos MACH3 Mass Evolution MassLib JCAMP MSS Nermag Automass
 * Nermag SIDAR netCDF Netzsch Palisade<br/>
 * PerkinElmer Clarus PerkinElmer Qmass 910 PerkinElmer TurboMass Shimadzu
 * PAC200 Shimadzu QP5000 Shimadzu GC Solutions Shrader for Windows Teknivent
 * Vector/1 Teknivent Vector/2 Text<br/>
 * Varian Saturn Varian Saturn 5 Varian Saturn Extended
 * 
 * @author eselmeister
 */
public final class ChromatogramConverterMSD {

	private static final Logger logger = Logger.getLogger(ChromatogramConverterMSD.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.msd.converter.chromatogramSupplier";

	/**
	 * This class has only static methods.
	 */
	private ChromatogramConverterMSD() {
	}

	/**
	 * Returns an IChromatogram instance.<br/>
	 * The raw files will be converted depending on the selected converter id.<br/>
	 * If a failure in the file could be detected an exception will be thrown.<br/>
	 * For example that the file is not readable or the file is not existent.<br/>
	 * If no converter was able to read the file, a
	 * NoChromatogramConverterAvailableException will be thrown.
	 * 
	 * @param chromatogram
	 * @param converterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo convert(final File file, final String converterId, final IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		/*
		 * Do not use a safe runnable here, because a IChromatogram object must
		 * be returned or null.
		 */
		IChromatogramMSDImportConverter importConverter = getChromatogramImportConverter(converterId);
		if(importConverter != null) {
			processingInfo = importConverter.convert(file, monitor);
		} else {
			processingInfo = getNoImportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	/**
	 * Returns an IChromatogram instance. Use this method if you do not know
	 * which chromatogram converter to use.<br/>
	 * This method will test all suitable converters.<br/>
	 * If no converter was able to read the file, a
	 * NoChromatogramConverterAvailableException will be thrown.
	 * 
	 * @param chromatogram
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo convert(final File file, final IProgressMonitor monitor) {

		IProcessingInfo processingInfo = getChromatogram(file, false, monitor);
		processingInfo.addMessages(processingInfo);
		processingInfo.setProcessingResult(processingInfo.getProcessingResult());
		return processingInfo;
	}

	/**
	 * If no suitable parser was found, null will be returned.
	 * 
	 * @param file
	 * @param overview
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	private static IProcessingInfo getChromatogram(final File file, boolean overview, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		List<IProcessingMessage> processingMessagesError = new ArrayList<IProcessingMessage>();
		ChromatogramConverterSupport converterSupport = getChromatogramConverterSupport();
		//
		try {
			List<String> availableConverterIds = converterSupport.getAvailableConverterIds(file);
			exitloop:
			for(String converterId : availableConverterIds) {
				/*
				 * Do not use a safe runnable here, because a IChromatogram
				 * object must be returned or null.
				 */
				IChromatogramMSDImportConverter importConverter = getChromatogramImportConverter(converterId);
				if(importConverter != null) {
					/*
					 * Why should the method not declare the exceptions that
					 * could be thrown? Here is the explanation.<br/><br/> Think
					 * about an extension which implements an chromatogram
					 * import filter for the file extension ".ms" and the
					 * supplier XY.<br/> If the extension has implemented the
					 * import filter in a wrong way, an IOException could be
					 * thrown for example.<br/> Now, if you want to convert a
					 * chromatogram from the supplier XZ with the file extension
					 * ".ms" but you do not know the correct extension id, you
					 * would call this method.<br/> The first import filter
					 * extension ".ms from XY" would try to convert the file. It
					 * would test if the file format is valid ... and then an
					 * IOExceptions happens ... ufff.<br/> The exception handler
					 * would check if this class can handle the exception. IF
					 * NOT the method would return with an IOException
					 * immediately.<br/> There is no chance to reach the correct
					 * converter. That is really not what we want.<br/> If all
					 * approaches have failed null will be returned.<br/><br/> I
					 * hope it's a little bit more clear now.<br/> eselmeister
					 */
					if(overview) {
						/*
						 * OVERVIEW
						 */
						IProcessingInfo processingInfox = importConverter.convertOverview(file, monitor);
						if(!processingInfox.hasErrorMessages()) {
							/*
							 * File OK, but converter failed if
							 * object is not type of IChromatogramOverview.
							 */
							Object object = processingInfox.getProcessingResult();
							if(object instanceof IChromatogramOverview) {
								processingInfo.addMessages(processingInfox);
								processingInfo.setProcessingResult(object);
								break exitloop;
							}
						} else {
							/*
							 * Something wrong with the file.
							 * Cache the messages until all converters have been tested.
							 */
							processingMessagesError.addAll(processingInfox.getMessages());
						}
					} else {
						/*
						 * NORMAL
						 */
						IProcessingInfo processingInfox = importConverter.convert(file, monitor);
						if(!processingInfox.hasErrorMessages()) {
							/*
							 * File OK, but converter failed if
							 * object is not type of IChromatogramMSD.
							 */
							Object object = processingInfox.getProcessingResult();
							if(object instanceof IChromatogramMSD) {
								processingInfo.addMessages(processingInfox);
								processingInfo.setProcessingResult(object);
								break exitloop;
							}
						} else {
							/*
							 * Something wrong with the file.
							 * Cache the messages until all converters have been tested.
							 */
							processingMessagesError.addAll(processingInfox.getMessages());
						}
					}
				}
			}
		} catch(NoConverterAvailableException e) {
			processingInfo.addErrorMessage("Chromatogram Export Converter", "There is no suitable converter available to save the chromatogram to the file: " + file.getAbsolutePath());
		}
		/*
		 * Test if at least one converter was successful.
		 * If not, add the error messages.
		 */
		if(processingInfo.getProcessingResult() == null) {
			for(IProcessingMessage processingMessage : processingMessagesError) {
				processingInfo.addMessage(processingMessage);
			}
		}
		//
		return processingInfo;
	}

	// ------------------------------------convenience-import
	/**
	 * Returns an IChromatogramOverview instance if the given converter is
	 * available.<br/>
	 * If no converter was available, a
	 * NoChromatogramConverterAvailableException will be thrown.
	 * 
	 * @param chromatogram
	 * @param converterId
	 * @param monitor
	 * @return {@link IChromatogramOverviewImportConverterProcessingInfo}
	 */
	public static IChromatogramOverviewImportConverterProcessingInfo convertOverview(final File file, final String converterId, IProgressMonitor monitor) {

		IChromatogramOverviewImportConverterProcessingInfo processingInfo;
		/*
		 * Do not use a safe runnable here, because a IChromatogram object must
		 * be returned or null.
		 */
		IChromatogramMSDImportConverter importConverter = getChromatogramImportConverter(converterId);
		if(importConverter != null) {
			processingInfo = importConverter.convertOverview(file, monitor);
		} else {
			processingInfo = getNoOverviewImportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	/**
	 * Returns an IChromatogram instance. Use this method if you do not know
	 * which chromatogram converter to use.<br/>
	 * This method will test all suitable converters.<br/>
	 * If no converter was able to read the file, a
	 * NoChromatogramConverterAvailableException will be thrown.
	 * 
	 * @param chromatogram
	 * @param monitor
	 * @return {@link IChromatogramOverviewImportConverterProcessingInfo}
	 */
	public static IChromatogramOverviewImportConverterProcessingInfo convertOverview(final File file, final IProgressMonitor monitor) {

		IProcessingInfo processingInfo = getChromatogram(file, true, monitor);
		IChromatogramOverviewImportConverterProcessingInfo processingInfoImport = new ChromatogramOverviewImportConverterProcessingInfo();
		processingInfoImport.addMessages(processingInfo);
		processingInfoImport.setProcessingResult(processingInfo.getProcessingResult());
		return processingInfoImport;
	}

	/**
	 * Returns the written file or a NoChromatogramConverterAvailableException
	 * if something has gone wrong.
	 * 
	 * @param file
	 * @param chromatogram
	 * @param converterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo convert(final File file, final IChromatogramMSD chromatogram, final String converterId, final IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		/*
		 * Do not use a safe runnable here, because a IChromatogram object must
		 * be returned or null.
		 */
		IChromatogramMSDExportConverter exportConverter = getChromatogramExportConverter(converterId);
		if(exportConverter != null) {
			processingInfo = exportConverter.convert(file, chromatogram, monitor);
		} else {
			processingInfo = getNoExportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	// ---------------------------------------------ConverterMethods
	/**
	 * Returns an IChromatogramImportConverter instance or null if none is
	 * available.
	 * 
	 * @param converterId
	 * @return IChromatogramImportConverter
	 */
	private static IChromatogramMSDImportConverter getChromatogramImportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IChromatogramMSDImportConverter instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramMSDImportConverter)element.createExecutableExtension(Converter.IMPORT_CONVERTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IChromatogramExportConverter instance or null if none is
	 * available.
	 * 
	 * @param converterId
	 * @return IChromatogramExportConverter
	 */
	private static IChromatogramMSDExportConverter getChromatogramExportConverter(final String converterId) {

		IConfigurationElement element;
		element = getConfigurationElement(converterId);
		IChromatogramMSDExportConverter instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramMSDExportConverter)element.createExecutableExtension(Converter.EXPORT_CONVERTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IChromatogramExportConverter instance or null if none is
	 * available.
	 * 
	 * @param converterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String converterId) {

		if("".equals(converterId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(Converter.ID).equals(converterId)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * This methods returns an {@link ChromatogramConverterSupport} instance.<br/>
	 * The {@link ChromatogramConverterSupport} instance stores descriptions
	 * about all valid and registered chromatogram converters.<br/>
	 * It can be used to get more information about the registered converters
	 * such like filter names, file extensions.
	 * 
	 * @return ChromatogramConverterSupport
	 */
	public static ChromatogramConverterSupport getChromatogramConverterSupport() {

		ChromatogramSupplier supplier;
		ChromatogramConverterSupport chromatogramConverterSupport = new ChromatogramConverterSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			/*
			 * Set the values to the ChromatogramSupplier instance first before
			 * validating them.<br/> Why? Because the return value of
			 * element.getAttribute(...) could be null. If the element is null
			 * and stored in a ChromatogramSupplier instance it will be at least
			 * "".
			 */
			supplier = new ChromatogramSupplier();
			supplier.setFileExtension(element.getAttribute(Converter.FILE_EXTENSION));
			supplier.setFileName(element.getAttribute(Converter.FILE_NAME));
			supplier.setDirectoryExtension(element.getAttribute(Converter.DIRECTORY_EXTENSION));
			/*
			 * Check if these values contain not allowed characters. If yes than
			 * do not add the supplier to the supported converter list.
			 */
			if(Converter.isValid(supplier.getFileExtension()) && Converter.isValid(supplier.getFileName()) && Converter.isValid(supplier.getDirectoryExtension())) {
				supplier.setId(element.getAttribute(Converter.ID));
				supplier.setDescription(element.getAttribute(Converter.DESCRIPTION));
				supplier.setFilterName(element.getAttribute(Converter.FILTER_NAME));
				supplier.setExportable(Boolean.valueOf(element.getAttribute(Converter.IS_EXPORTABLE)));
				supplier.setImportable(Boolean.valueOf(element.getAttribute(Converter.IS_IMPORTABLE)));
				supplier.setMagicNumberMatcher(getMagicNumberMatcher(element));
				chromatogramConverterSupport.add(supplier);
			}
		}
		return chromatogramConverterSupport;
	}

	/*
	 * This method may return null.
	 */
	private static IMagicNumberMatcher getMagicNumberMatcher(IConfigurationElement element) {

		IMagicNumberMatcher magicNumberMatcher;
		try {
			magicNumberMatcher = (IMagicNumberMatcher)element.createExecutableExtension(Converter.IMPORT_MAGIC_NUMBER_MATCHER);
		} catch(Exception e) {
			magicNumberMatcher = null;
		}
		return magicNumberMatcher;
	}

	// ---------------------------------------------ConverterMethods
	private static IProcessingInfo getNoExportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addErrorMessage("Chromatogram Export Converter", "There is no suitable converter available to save the chromatogram to the file: " + file.getAbsolutePath());
		return processingInfo;
	}

	private static IProcessingInfo getNoImportConverterAvailableProcessingInfo(File file) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addErrorMessage("Chromatogram Import Converter", "There is no suitable converter available to load the chromatogram from the file: " + file.getAbsolutePath());
		return processingInfo;
	}

	private static IChromatogramOverviewImportConverterProcessingInfo getNoOverviewImportConverterAvailableProcessingInfo(File file) {

		IChromatogramOverviewImportConverterProcessingInfo processingInfo = new ChromatogramOverviewImportConverterProcessingInfo();
		processingInfo.addErrorMessage("ChromatogramOverview Import Converter", "There is no suitable converter available to load the chromatogram overview from the file: " + file.getAbsolutePath());
		return processingInfo;
	}
}
