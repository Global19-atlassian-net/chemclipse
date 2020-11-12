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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.activator.ContextAddon;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Display;

public class GroupHandlerScans extends AbstractGroupHandler {

	private static final String TOOL_ITEM_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.directtoolitem.scans";
	//
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_SELECTED_SCANS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_SELECTED_SCANS_DEFAULT;
	//
	private static boolean partsAreActivated = false;

	/**
	 * This static method activates the referenced parts.
	 */
	public static void activateParts() {

		EPartService partService = ContextAddon.getPartService();
		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		if(partService != null && modelService != null && application != null) {
			/*
			 * Try to get tool item to modify the tooltip and image.
			 */
			MDirectToolItem directToolItem = PartSupport.getDirectToolItem(TOOL_ITEM_ID, modelService, application);
			Display display = Display.getDefault();
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					GroupHandlerScans groupHandler = new GroupHandlerScans();
					groupHandler.activateParts(directToolItem, partService, modelService, application, groupHandler.toggleShow());
				}
			});
		}
	}

	@Override
	public List<IPartHandler> getPartHandler() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new TargetsPartHandler());
		partHandler.add(new ScanChartPartHandler());
		partHandler.add(new ScanTablePartHandler());
		partHandler.add(new MoleculePartHandler());
		partHandler.add(new ScanBrowsePartHandler());
		partHandler.add(new SynonymsPartHandler());
		//
		return partHandler;
	}

	@Override
	public String getImageHide() {

		return IMAGE_HIDE;
	}

	@Override
	public String getImageShow() {

		return IMAGE_SHOW;
	}

	@Override
	public boolean toggleShow() {

		partsAreActivated = !partsAreActivated;
		return partsAreActivated;
	}
}
