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

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.ImageDescriptor;

public class GroupHandlerPeaks {

	private static boolean active = false;

	public static void activateParts() {

		System.out.println("TODO - Initially activate parts");
	}

	@Execute
	public void execute(MDirectToolItem directToolItem, EPartService partService, EModelService modelService, MApplication application) {

		boolean show = !active;
		adjustToolTip(directToolItem, show);
		adjustIcon(directToolItem, show);
		//
		action(new TargetsPartHandler(), show, partService, modelService, application);
		action(new ScanChartPartHandler(), show, partService, modelService, application);
		action(new ScanTablePartHandler(), show, partService, modelService, application);
		action(new MoleculePartHandler(), show, partService, modelService, application);
		//
		active = show;
	}

	private void adjustToolTip(MDirectToolItem directToolItem, boolean show) {

		directToolItem.setTooltip(show ? "Deactivate all parts" : "Activate all parts");
	}

	private void adjustIcon(MDirectToolItem directToolItem, boolean show) {

		ImageDescriptor imageDescriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_OVERLAY_ACTIVE, IApplicationImage.SIZE_16x16);
		/*
		 * platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/comparisonScanDefault.gif
		 */
		// TODO - IApplicationImage: move path prefix "org.eclipse.chemclipse.rcp.ui.icons/" so that shortcuts can be used.
		String location = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/";
		directToolItem.setIconURI(show ? location + "selectedScansActive.gif" : location + "selectedScansDefault.gif");
	}

	private void action(IPartHandler partHandler, boolean show, EPartService partService, EModelService modelService, MApplication application) {

		partHandler.action(show, partService, modelService, application);
	}
}
