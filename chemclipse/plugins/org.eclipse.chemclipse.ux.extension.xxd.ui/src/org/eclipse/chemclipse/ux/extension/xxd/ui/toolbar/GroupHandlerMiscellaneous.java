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

public class GroupHandlerMiscellaneous extends AbstractGroupHandler {

	private static final String NAME = "Miscellaneous";
	private static final String SETTINGS_CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.SettingsHandlerMiscellaneous";
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_MEASUREMENT_RESULTS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT;
	//
	private static boolean partsAreActivated = false;

	public static void activateReferencedParts() {

		IGroupHandler groupHandler = new GroupHandlerScans();
		groupHandler.activateParts();
	}

	@Override
	public List<IPartHandler> getPartHandler() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PartHandler("Scan Subtract", PartSupport.PARTDESCRIPTOR_SUBTRACT_SCAN, PreferenceConstants.P_STACK_POSITION_SUBTRACT_SCAN_PART));
		partHandler.add(new PartHandler("Scan Combined", PartSupport.PARTDESCRIPTOR_COMBINED_SCAN, PreferenceConstants.P_STACK_POSITION_COMBINED_SCAN_PART));
		partHandler.add(new PartHandler("Scan Comparison", PartSupport.PARTDESCRIPTOR_COMPARISON_SCAN, PreferenceConstants.P_STACK_POSITION_COMPARISON_SCAN_CHART));
		partHandler.add(new PartHandler("Measurement Results", PartSupport.PARTDESCRIPTOR_MEASUREMENT_RESULTS, PreferenceConstants.P_STACK_POSITION_MEASUREMENT_RESULTS));
		//
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
