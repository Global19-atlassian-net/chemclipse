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

import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;

public class ScanBrowsePartHandler extends AbstractPartHandler {

	public static final String PART_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanBrowsePartDescriptor";
	public static final String STACK_POSITION_KEY = PreferenceConstants.P_STACK_POSITION_SCAN_BROWSE;

	@Override
	public String getPartId() {

		return PART_ID;
	}

	@Override
	public String getStackPositionKey() {

		return STACK_POSITION_KEY;
	}
}
