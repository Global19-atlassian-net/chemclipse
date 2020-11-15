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
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.preference.IPreferencePage;

public class GroupHandler {

	private static List<IGroupHandler> groupHandlers = new ArrayList<>();
	static {
		/*
		 * Group Handlers
		 */
		groupHandlers.add(new GroupHandlerOverview());
		groupHandlers.add(new GroupHandlerOverlay());
		groupHandlers.add(new GroupHandlerScans());
		groupHandlers.add(new GroupHandlerPeaks());
		groupHandlers.add(new GroupHandlerQuantitation());
		groupHandlers.add(new GroupHandlerISTD());
		groupHandlers.add(new GroupHandlerESTD());
		groupHandlers.add(new GroupHandlerPCR());
		groupHandlers.add(new GroupHandlerMiscellaneous());
	}

	/*
	 * Only use static methods.
	 */
	private GroupHandler() {

	}

	public static void activateReferencedParts() {

		IGroupHandler groupHandler = new GroupHandlerScans();
		groupHandler.activateParts();
	}

	public static void updateGroupHandlerMenu() {

		for(IGroupHandler groupHandler : groupHandlers) {
			groupHandler.updateMenu();
		}
	}

	public static List<IPreferencePage> getPreferencePages(String elementId) {

		for(IGroupHandler groupHandler : groupHandlers) {
			if(groupHandler.getSettingsMenuId().equals(elementId)) {
				return groupHandler.getPreferencePages();
			}
		}
		//
		return Collections.emptyList();
	}
}
