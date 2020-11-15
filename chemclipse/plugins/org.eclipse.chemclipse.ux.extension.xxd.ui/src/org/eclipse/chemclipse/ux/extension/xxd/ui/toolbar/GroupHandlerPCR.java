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
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;

public class GroupHandlerPCR extends AbstractGroupHandler {

	private static final String NAME = "PCR";
	private static final String SETTINGS_CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.SettingsHandlerPCR";
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_PCR_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_PCR_DEFAULT;
	//
	private static boolean partsAreActivated = false;

	public static void activateReferencedParts() {

		IGroupHandler groupHandler = new GroupHandlerScans();
		groupHandler.activateParts();
	}

	@Override
	public List<IPartHandler> getPartHandlerMandatory() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PartHandler("Plate Charts", PartSupport.PARTDESCRIPTOR_PlATE_CHARTS, PreferenceConstants.P_STACK_POSITION_PLATE_CHARTS));
		partHandler.add(new PartHandler("Well Data", PartSupport.PARTDESCRIPTOR_WELL_DATA, PreferenceConstants.P_STACK_POSITION_WELL_DATA));
		partHandler.add(new PartHandler("Well Chart", PartSupport.PARTDESCRIPTOR_WELL_CHART, PreferenceConstants.P_STACK_POSITION_WELL_CHART));
		partHandler.add(new PartHandler("Well Channels", PartSupport.PARTDESCRIPTOR_WELL_CHANNELS, PreferenceConstants.P_STACK_POSITION_WELL_CHANNELS));
		partHandler.add(new PartHandler("Plate Data", PartSupport.PARTDESCRIPTOR_PlATE_DATA, PreferenceConstants.P_STACK_POSITION_PLATE_DATA));
		//
		return partHandler;
	}

	@Override
	public List<IPartHandler> getPartHandlerAdditional() {

		List<IPartHandler> partHandler = new ArrayList<>();
		return partHandler;
	}

	@Override
	public String getName() {

		return NAME;
	}

	@Override
	public String getSettingsContributionURI() {

		return SETTINGS_CONTRIBUTION_URI;
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
