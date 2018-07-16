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

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageTaskLists extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageTaskLists() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Peaks");
	}

	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_PEAK_LIST, "Peak List:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_SCAN_LIST, "Scan List:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_STACK_POSITION_PEAK_QUANTITATION_LIST, "Peak Quantitation List:", PreferenceConstants.PART_STACKS, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
