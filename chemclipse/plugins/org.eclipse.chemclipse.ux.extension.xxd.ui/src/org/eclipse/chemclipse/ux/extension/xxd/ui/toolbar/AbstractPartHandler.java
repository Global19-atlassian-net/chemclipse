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
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
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
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	@Evaluate
	public boolean isVisible(IEclipseContext context) {

		return isPartStackAssigned();
	}

	@Execute
	public void execute() {

		toggleVisibility();
	}

	@Override
	public void action(boolean show, EPartService partService, EModelService modelService, MApplication application) {

		String partId = getPartId();
		//
		if(isPartStackAssigned()) {
			/*
			 * Part Stack
			 */
			String stackPositionKey = getStackPositionKey();
			String partStackId = preferenceStore.getString(stackPositionKey);
			/*
			 * Show/Hide the part.
			 */
			PartSupport.setPartVisibility(partId, partStackId, false, partService, modelService, application);
			if(show) {
				PartSupport.togglePartVisibility(partId, partStackId, partService, modelService, application);
			}
		} else {
			/*
			 * If the part stack has been set to "--", hide it.
			 */
			MPart part = PartSupport.getPart(partId, modelService, application);
			if(part != null && part.isVisible()) {
				part.setVisible(false);
			}
		}
	}

	protected void toggleVisibility() {

		boolean show = !isPartVisible();
		action(show);
	}

	protected boolean isPartVisible() {

		String partId = getPartId();
		return PartSupport.isPartVisible(partId, modelService, application);
	}

	protected void action(boolean show) {

		action(show, partService, modelService, application);
	}

	private boolean isPartStackAssigned() {

		String partStackId = preferenceStore.getString(getStackPositionKey());
		if(partStackId.isEmpty() || PartSupport.PARTSTACK_NONE.equals(partStackId)) {
			return false;
		} else {
			return true;
		}
	}
}
