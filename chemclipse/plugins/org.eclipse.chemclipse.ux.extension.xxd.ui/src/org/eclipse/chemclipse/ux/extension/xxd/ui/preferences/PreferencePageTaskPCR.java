/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.GroupHandlerPCR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.IPartHandler;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageTaskPCR extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageTaskPCR() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("PCR");
		setDescription("");
	}

	public void createFieldEditors() {

		List<IPartHandler> partHandlers = new GroupHandlerPCR().getPartHandler();
		for(IPartHandler partHandler : partHandlers) {
			addField(new ComboFieldEditor(partHandler.getStackPositionKey(), partHandler.getName() + ":", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		}
	}

	public void init(IWorkbench workbench) {

	}
}
