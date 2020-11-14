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

public class GroupHandlerPeaks extends AbstractGroupHandler {

	//
	private static final String NAME = "Peaks";
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
		partHandler.add(new PartHandler("Peak Chart", PartSupport.PARTDESCRIPTOR_PEAK_CHART, PreferenceConstants.P_STACK_POSITION_PEAK_CHART));
		partHandler.add(new PartHandler("Peak Details", PartSupport.PARTDESCRIPTOR_PEAK_DETAILS, PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS));
		partHandler.add(new PartHandler("Peak Detector", PartSupport.PARTDESCRIPTOR_PEAK_DETECTOR, PreferenceConstants.P_STACK_POSITION_PEAK_DETECTOR));
		partHandler.add(new PartHandler("Peak List", PartSupport.PARTDESCRIPTOR_PEAK_SCAN_LIST, PreferenceConstants.P_STACK_POSITION_PEAK_SCAN_LIST));
		partHandler.add(new PartHandler("Peak Traces", PartSupport.PARTDESCRIPTOR_PEAK_TRACES, PreferenceConstants.P_STACK_POSITION_PEAK_TRACES));
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
