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

public class GroupHandlerESTD extends AbstractGroupHandler {

	private static final String NAME = "ESTD";
	private static final String SETTINGS_CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.SettingsHandlerESTD";
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_EXTERNAL_STANDARDS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_EXTERNAL_STANDARDS_DEFAULT;
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
		partHandler.add(new PartHandler("Quant Peak List", PartSupport.PARTDESCRIPTOR_QUANT_PEAKS_LIST, PreferenceConstants.P_STACK_POSITION_QUANT_PEAKS_LIST));
		partHandler.add(new PartHandler("Quant Peak Chart", PartSupport.PARTDESCRIPTOR_QUANT_PEAKS_CHART, PreferenceConstants.P_STACK_POSITION_QUANT_PEAKS_CHART));
		partHandler.add(new PartHandler("Quant Signals List", PartSupport.PARTDESCRIPTOR_QUANT_SIGNALS_LIST, PreferenceConstants.P_STACK_POSITION_QUANT_SIGNALS_LIST));
		partHandler.add(new PartHandler("Quant Response List", PartSupport.PARTDESCRIPTOR_QUANT_RESPONSE_LIST, PreferenceConstants.P_STACK_POSITION_QUANT_RESPONSE_LIST));
		partHandler.add(new PartHandler("Quant Response Chart", PartSupport.PARTDESCRIPTOR_QUANT_RESPONSE_CHART, PreferenceConstants.P_STACK_POSITION_QUANT_RESPONSE_CHART));
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
