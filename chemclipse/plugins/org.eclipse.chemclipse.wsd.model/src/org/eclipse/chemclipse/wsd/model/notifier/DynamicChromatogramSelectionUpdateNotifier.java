/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.notifier;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

public class DynamicChromatogramSelectionUpdateNotifier implements IChromatogramSelectionWSDUpdateNotifier {

	@Inject
	private IEventBroker eventBroker;
	private Map<String, Object> map;

	public DynamicChromatogramSelectionUpdateNotifier() {
		map = new HashMap<String, Object>();
	}

	@Override
	public void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

		/*
		 * Don't use a new map each time, to prevent unnecessary object creation.
		 */
		map.clear();
		map.put(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION, chromatogramSelection);
		map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, forceReload);
		eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, map);
	}
}
