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

import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public abstract class AbstractGroupHandler implements IGroupHandler {

	@Execute
	public void execute(MDirectToolItem directToolItem, EPartService partService, EModelService modelService, MApplication application) {

		activateParts(directToolItem, partService, modelService, application, toggleShow());
	}

	protected void adjustToolTip(MDirectToolItem directToolItem, boolean show) {

		if(directToolItem != null) {
			directToolItem.setTooltip(show ? "Deactivate all referenced parts." : "Activate all referenced parts.");
		}
	}

	protected void adjustIcon(MDirectToolItem directToolItem, boolean show) {

		if(directToolItem != null) {
			String iconHide = IApplicationImage.getLocation(getImageHide(), IApplicationImage.SIZE_16x16);
			String iconShow = IApplicationImage.getLocation(getImageShow(), IApplicationImage.SIZE_16x16);
			directToolItem.setIconURI(show ? iconHide : iconShow);
		}
	}

	protected void action(IPartHandler partHandler, boolean show, EPartService partService, EModelService modelService, MApplication application) {

		partHandler.action(show, partService, modelService, application);
	}

	protected void activateParts(MDirectToolItem directToolItem, EPartService partService, EModelService modelService, MApplication application, boolean show) {

		adjustToolTip(directToolItem, show);
		adjustIcon(directToolItem, show);
		//
		List<IPartHandler> partHandlers = getPartHandler();
		for(IPartHandler partHandler : partHandlers) {
			action(partHandler, show, partService, modelService, application);
		}
	}
}
