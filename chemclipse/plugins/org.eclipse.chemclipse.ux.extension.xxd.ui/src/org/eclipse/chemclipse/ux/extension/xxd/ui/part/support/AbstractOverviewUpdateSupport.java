/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.nmr.converter.core.ScanConverterNMR;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.xir.converter.core.ScanConverterXIR;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public abstract class AbstractOverviewUpdateSupport extends AbstractDataUpdateSupport implements IOverviewUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractOverviewUpdateSupport.class);
	private String filePath = "";

	public AbstractOverviewUpdateSupport(MPart part) {
		super(part);
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_RAWFILE);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_RAWFILE);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_RAWFILE);
		//
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_OVERVIEW);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_OVERVIEW);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_OVERVIEW);
		//
		registerEvent(IChemClipseEvents.TOPIC_SCAN_NMR_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_SCAN_NMR_RAWFILE);
		// registerEvent(IChemClipseEvents.TOPIC_SCAN_NMR_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_RAWFILE);
		//
		registerEvent(IChemClipseEvents.TOPIC_SCAN_XIR_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_SCAN_XIR_RAWFILE);
		// registerEvent(IChemClipseEvents.TOPIC_SCAN_XIR_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_RAWFILE);
		//
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_XXD_RAWFILE);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		Object object = objects.get(0);
		//
		if(object instanceof IChromatogramOverview) {
			IChromatogramOverview chromatogramOverview = (IChromatogramOverview)object;
			if(chromatogramOverview != null) {
				update(chromatogramOverview);
			}
		} else if(object instanceof File) {
			File file = (File)object;
			if(!file.getAbsolutePath().equals(filePath)) {
				/*
				 * Only load the overview if it is a new file.
				 */
				filePath = file.getAbsolutePath();
				//
				if(topic.equals(IChemClipseEvents.TOPIC_SCAN_NMR_UPDATE_RAWFILE)) {
					/*
					 * NMR
					 */
					IProcessingInfo processingInfo = ScanConverterNMR.convert(file, new NullProgressMonitor());
					Object data = processingInfo.getProcessingResult();
					if(data instanceof IMeasurementInfo) {
						update((IMeasurementInfo)data);
					}
				} else if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XIR_UPDATE_RAWFILE)) {
					/*
					 * XIR
					 */
					IProcessingInfo processingInfo = ScanConverterXIR.convert(file, new NullProgressMonitor());
					Object data = processingInfo.getProcessingResult();
					if(data instanceof IMeasurementInfo) {
						update((IMeasurementInfo)data);
					}
				} else {
					/*
					 * MSD, CSD, WSD
					 */
					IChromatogramOverview chromatogramOverview = getChromatogramOverview(file, topic);
					if(chromatogramOverview != null) {
						update(chromatogramOverview);
					}
				}
			}
		} else {
			if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE)) {
				update(null);
			}
		}
	}

	private IChromatogramOverview getChromatogramOverview(File file, String topic) {

		IChromatogramOverview chromatogramOverview = null;
		IProcessingInfo processingInfo = null;
		switch(topic) {
			case IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE:
				processingInfo = ChromatogramConverterMSD.convertOverview(file, new NullProgressMonitor());
				break;
			case IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE:
				processingInfo = ChromatogramConverterCSD.convertOverview(file, new NullProgressMonitor());
				break;
			case IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE:
				processingInfo = ChromatogramConverterWSD.convertOverview(file, new NullProgressMonitor());
				break;
		}
		//
		try {
			if(processingInfo != null) {
				chromatogramOverview = processingInfo.getProcessingResult(IChromatogramOverview.class);
			}
		} catch(TypeCastException e) {
			logger.warn(e);
		}
		//
		return chromatogramOverview;
	}
}
