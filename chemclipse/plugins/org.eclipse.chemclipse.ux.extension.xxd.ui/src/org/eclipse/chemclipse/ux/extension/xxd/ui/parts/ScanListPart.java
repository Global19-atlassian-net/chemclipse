/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanListUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class ScanListPart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private ExtendedScanListUI extendedScanListUI;

	@Inject
	public ScanListPart(Composite parent, MPart part) {
		super(part);
		extendedScanListUI = new ExtendedScanListUI(parent);
	}

	@Focus
	public void setFocus() {

		updateObjects(getObjects(), getTopic());
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_LOAD_CHROMATOGRAM_SELECTION, IEventBroker.DATA);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION_XXD);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			if(isChromatogramUnloadEvent(topic)) {
				extendedScanListUI.updateChromatogramSelection(null);
			} else {
				if(isChromatogramTopic(topic)) {
					Object object = objects.get(0);
					if(object instanceof IChromatogramSelection) {
						IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
						extendedScanListUI.updateChromatogramSelection(chromatogramSelection);
					}
				}
			}
		}
	}

	private boolean isChromatogramUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_CHROMATOGRAM_SELECTION)) {
			return true;
		}
		return false;
	}

	private boolean isChromatogramTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_LOAD_CHROMATOGRAM_SELECTION)) {
			return true;
		}
		return false;
	}
}
