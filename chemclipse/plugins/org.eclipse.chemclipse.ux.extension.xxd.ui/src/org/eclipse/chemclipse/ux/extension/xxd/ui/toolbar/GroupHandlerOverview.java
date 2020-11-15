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

public class GroupHandlerOverview extends AbstractGroupHandler {

	//
	private static final String NAME = "Overview";
	private static final String SETTINGS_CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.SettingsHandlerOverview";
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_CHROMATOGRAM_OVERVIEW_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_CHROMATOGRAM_OVERVIEW_DEFAULT;
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
		partHandler.add(new PartHandler("Header", PartSupport.PARTDESCRIPTOR_HEADER_DATA, PreferenceConstants.P_STACK_POSITION_HEADER_DATA));
		partHandler.add(new PartHandler("Overview (Chromatogram)", PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW, PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_OVERVIEW));
		partHandler.add(new PartHandler("Header (Miscellaneous)", PartSupport.PARTDESCRIPTOR_MISCELLANEOUS_INFO, PreferenceConstants.P_STACK_POSITION_MISCELLANEOUS_INFO));
		partHandler.add(new PartHandler("Scan Info (Chromatogram)", PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_SCAN_INFO, PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_SCAN_INFO));
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
