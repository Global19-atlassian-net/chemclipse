/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.GroupHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.GroupHandlerScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.IPartHandler;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageTaskScans extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageTaskScans() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Scans");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new LabelFieldEditor("Mandatory", getFieldEditorParent()));
		List<IPartHandler> partHandlersMandatory = new GroupHandlerScans().getPartHandlerMandatory();
		for(IPartHandler partHandler : partHandlersMandatory) {
			addField(new ComboFieldEditor(partHandler.getStackPositionKey(), partHandler.getName() + ":", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		}
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Additional", getFieldEditorParent()));
		List<IPartHandler> partHandlersAdditional = new GroupHandlerScans().getPartHandlerAdditional();
		for(IPartHandler partHandler : partHandlersAdditional) {
			addField(new ComboFieldEditor(partHandler.getStackPositionKey(), partHandler.getName() + ":", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		}
	}

	public void init(IWorkbench workbench) {

	}

	@Override
	public boolean performOk() {

		boolean ok = super.performOk();
		GroupHandler.updateGroupHandlerMenu();
		return ok;
	}
}
