/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedMoleculeUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MoleculePart {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;
	//
	private ExtendedMoleculeUI control;
	private boolean initialUpdate = true;
	//
	private DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
	private IDataUpdateListener updateListener = new IDataUpdateListener() {

		@Override
		public void update(String topic, List<Object> objects) {

			updateSelection(objects, topic);
		}
	};

	@Inject
	public MoleculePart(Composite parent, MPart part) {

		control = new ExtendedMoleculeUI(parent, SWT.NONE);
		dataUpdateSupport.add(updateListener);
	}

	@Focus
	public void setFocus() {

		if(initialUpdate) {
			updateSelection(dataUpdateSupport.getUpdates(TOPIC), TOPIC);
		}
	}

	@PreDestroy
	protected void preDestroy() {

		dataUpdateSupport.remove(updateListener);
		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			eventBroker.send(IChemClipseEvents.TOPIC_PART_CLOSED, getClass().getSimpleName());
		}
	}

	private void updateSelection(List<Object> objects, String topic) {

		if(DataUpdateSupport.isVisible(control)) {
			if(objects.size() == 1) {
				if(isScanOrPeakTopic(topic)) {
					initialUpdate = false;
					Object object = objects.get(0);
					ILibraryInformation libraryInformation = null;
					if(object instanceof ILibraryMassSpectrum) {
						ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)object;
						libraryInformation = libraryMassSpectrum.getLibraryInformation();
					}
					control.setInput(libraryInformation);
				} else if(isIdentificationTargetTopic(topic)) {
					initialUpdate = false;
					Object object = objects.get(0);
					ILibraryInformation libraryInformation = null;
					if(object instanceof IIdentificationTarget) {
						IIdentificationTarget identificationTarget = (IIdentificationTarget)object;
						libraryInformation = identificationTarget.getLibraryInformation();
					}
					control.setInput(libraryInformation);
				}
			}
		}
	}

	private boolean isScanOrPeakTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION)) {
			return true;
		}
		return false;
	}

	private boolean isIdentificationTargetTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE)) {
			return true;
		}
		return false;
	}
}
