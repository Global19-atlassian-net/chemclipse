/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramOverviewImportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractChromatogramUpdateSupport extends AbstractUpdateSupport implements IChromatogramUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractChromatogramUpdateSupport.class);
	//
	private static IChromatogramOverview chromatogramOverview;
	//
	private IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
	private List<EventHandler> registeredEventHandler;

	public AbstractChromatogramUpdateSupport(MPart part) {
		super(part);
		registeredEventHandler = new ArrayList<EventHandler>();
		registerEventBroker(eventBroker);
	}

	@Override
	public IChromatogramOverview getChromatogramOverview() {

		return chromatogramOverview;
	}

	@PreDestroy
	private void preDestroy() {

		if(eventBroker != null) {
			for(EventHandler eventHandler : registeredEventHandler) {
				eventBroker.unsubscribe(eventHandler);
			}
		}
	}

	private void registerEventBroker(IEventBroker eventBroker) {

		if(eventBroker != null) {
			registeredEventHandler.add(registerEventHandlerFile(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_RAWFILE, IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE));
			registeredEventHandler.add(registerEventHandlerFile(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_RAWFILE, IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE));
			registeredEventHandler.add(registerEventHandlerFile(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_RAWFILE, IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE));
			registeredEventHandler.add(registerEventHandlerFile(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_XXD_RAWFILE, IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE));
			registeredEventHandler.add(registerEventHandlerInstance(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_OVERVIEW, IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW));
			registeredEventHandler.add(registerEventHandlerInstance(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_OVERVIEW, IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW));
			registeredEventHandler.add(registerEventHandlerInstance(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_OVERVIEW, IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW));
		}
	}

	private EventHandler registerEventHandlerFile(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			public void handleEvent(Event event) {

				try {
					if(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE.equals(topic)) {
						setChromatogramOverview(null);
					} else {
						Object object = event.getProperty(property);
						if(object instanceof File) {
							setChromatogram((File)object, topic);
						} else {
							setChromatogramOverview(null);
						}
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private EventHandler registerEventHandlerInstance(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			public void handleEvent(Event event) {

				try {
					IChromatogramOverview chromatogramOverview = null;
					Object object = event.getProperty(property);
					if(object instanceof IChromatogramOverview) {
						chromatogramOverview = (IChromatogramOverview)object;
					}
					setChromatogramOverview(chromatogramOverview);
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private void setChromatogram(File file, String topic) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException {

		if(doUpdate()) {
			IChromatogramOverviewImportConverterProcessingInfo processingInfo = null;
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
				IChromatogramOverview chromatogramOverview = null;
				if(processingInfo != null) {
					chromatogramOverview = processingInfo.getChromatogramOverview();
				}
				setChromatogramOverview(chromatogramOverview);
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		}
	}

	public void setChromatogramOverview(IChromatogramOverview overview) {

		chromatogramOverview = overview;
		updateChromatogram(chromatogramOverview);
	}
}
