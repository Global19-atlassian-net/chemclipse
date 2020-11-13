/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import java.util.List;

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.GroupHandlerOverlay;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.IPartHandler;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageTaskOverlay extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageTaskOverlay() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Overlay");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_OVERLAY_CHROMATOGRAM_EXTRA, "Overlay Chromatogram Extra:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		//
		List<IPartHandler> partHandlers = new GroupHandlerOverlay().getPartHandler();
		for(IPartHandler partHandler : partHandlers) {
			addField(new ComboFieldEditor(partHandler.getStackPositionKey(), partHandler.getName() + ":", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		}
	}

	public void init(IWorkbench workbench) {

	}
}
