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

public class GroupHandlerScans extends AbstractGroupHandler {

	private static final String NAME = "Scans";
	private static final String SETTINGS_CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.SettingsHandlerScans";
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_SELECTED_SCANS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_SELECTED_SCANS_DEFAULT;
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
		partHandler.add(new PartHandler("Targets", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.targetsPartDescriptor", PreferenceConstants.P_STACK_POSITION_TARGETS));
		partHandler.add(new PartHandler("Scan Chart", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanChartPartDescriptor", PreferenceConstants.P_STACK_POSITION_SCAN_CHART));
		partHandler.add(new PartHandler("Scan Table", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanTablePartDescriptor", PreferenceConstants.P_STACK_POSITION_SCAN_TABLE));
		partHandler.add(new PartHandler("Molecule", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.moleculePartDescriptor", PreferenceConstants.P_STACK_POSITION_MOLECULE));
		partHandler.add(new PartHandler("Scan Browse", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanBrowsePartDescriptor", PreferenceConstants.P_STACK_POSITION_SCAN_BROWSE));
		partHandler.add(new PartHandler("Synonyms", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.synonymsPartDescriptor", PreferenceConstants.P_STACK_POSITION_SYNONYMS));
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
