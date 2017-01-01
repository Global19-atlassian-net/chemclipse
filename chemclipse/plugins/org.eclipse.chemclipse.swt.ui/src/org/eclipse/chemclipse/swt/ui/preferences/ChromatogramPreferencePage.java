/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.preferences;

import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.Activator;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ChromatogramPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ChromatogramPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStoreChromatogram());
		setDescription("Specific chromatogram settings.");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {

		addField(new StringFieldEditor(PreferenceSupplier.P_MISC_SEPARATOR, "Separater miscellaneous.", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceSupplier.P_MISC_SEPARATED_DELIMITER, "Delimiter for separated values.", getFieldEditorParent()));
	}
}
