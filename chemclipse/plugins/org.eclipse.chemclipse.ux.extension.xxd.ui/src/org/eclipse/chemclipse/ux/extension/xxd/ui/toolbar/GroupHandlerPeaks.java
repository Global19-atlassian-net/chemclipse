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

public class GroupHandlerPeaks extends AbstractGroupHandler {

	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_SELECTED_PEAKS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_SELECTED_PEAKS_DEFAULT;
	//
	private static boolean partsAreActivated = false;

	@Override
	public List<IPartHandler> getPartHandler() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PeakChartPartHandler());
		partHandler.add(new PeakDetailsPartHandler());
		partHandler.add(new PeakDetectorPartHandler());
		partHandler.add(new PeakTracesPartHandler());
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
