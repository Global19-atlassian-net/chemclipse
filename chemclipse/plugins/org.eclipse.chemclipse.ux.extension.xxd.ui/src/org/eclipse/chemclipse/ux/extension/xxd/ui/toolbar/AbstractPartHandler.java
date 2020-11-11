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
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * fragment.e4xmi
 * 
 * @Evaluate is used by the "Imperative Expression"
 * @Execute is used by "Direct Menu Item"
 * 
 */
public abstract class AbstractPartHandler implements IPartHandler {

	@Inject
	private EPartService partService;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication application;
	@Inject
	private IEventBroker eventBroker;

	@Override
	public String getPartId() {

		return "";
	}

	@Override
	public String getStackPositionKey() {

		return "";
	}

	@Evaluate
	public boolean isVisible(IEclipseContext context) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String partStackId = preferenceStore.getString(getStackPositionKey());
		return !PartSupport.PARTSTACK_NONE.equals(partStackId);
	}

	@Execute
	public void execute() {

		toggleVisibility();
	}

	protected void toggleVisibility() {

		boolean show = !isPartVisible();
		action(show);
	}

	protected boolean isPartVisible() {

		String partId = getPartId();
		//
		boolean isVisible = PartSupport.isPartVisible(partId, partService, modelService, application);
		if(isVisible) {
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			String stackPositionKey = getStackPositionKey();
			String partStackId = preferenceStore.getString(stackPositionKey);
			isVisible = PartSupport.partStackContainsPart(partId, partStackId, modelService, application);
		}
		return isVisible;
	}

	protected void action(boolean show) {

		String partId = getPartId();
		String stackPositionKey = getStackPositionKey();
		action(partId, stackPositionKey, show);
	}

	protected void action(String partId, String stackPositionKey, boolean show) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String partStackId = preferenceStore.getString(stackPositionKey);
		//
		PartSupport.setPartVisibility(partId, partStackId, false, partService, modelService, application, eventBroker);
		if(show) {
			PartSupport.togglePartVisibility(partId, partStackId, partService, modelService, application, eventBroker);
		}
	}
}
