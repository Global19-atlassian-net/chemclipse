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
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;

public class GroupHandlerPeaks extends AbstractGroupHandler {

	//
	private static final String TOOL_ITEM_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.directtoolitem.peaks";
	private static final String MENU_SETTINGS_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.directmenuitem.settings.peaks";
	private static final String SETTINGS_CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.SettingsHandlerPeaks";
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_SELECTED_PEAKS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_SELECTED_PEAKS_DEFAULT;
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
		partHandler.add(new PartHandler("Peak Chart", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.peakChartPartDescriptor", PreferenceConstants.P_STACK_POSITION_PEAK_CHART));
		partHandler.add(new PartHandler("Peak Details", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.peakDetailsPartDescriptor", PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS));
		partHandler.add(new PartHandler("Peak Detector", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.peakDetectorPartDescriptor", PreferenceConstants.P_STACK_POSITION_PEAK_DETECTOR));
		partHandler.add(new PartHandler("Peak List", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.peakScanListPartDescriptor", PreferenceConstants.P_STACK_POSITION_PEAK_SCAN_LIST));
		partHandler.add(new PartHandler("Peak Traces", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.peakTracesPartDescriptor", PreferenceConstants.P_STACK_POSITION_PEAK_TRACES));
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
	public String getToolItemId() {

		return TOOL_ITEM_ID;
	}

	@Override
	public String getMenuSettingsId() {

		return MENU_SETTINGS_ID;
	}

	@Override
	public String getSettingsContributionURI() {

		return SETTINGS_CONTRIBUTION_URI;
	}

	@Override
	public boolean toggleShow() {

		partsAreActivated = !partsAreActivated;
		return partsAreActivated;
	}
}
