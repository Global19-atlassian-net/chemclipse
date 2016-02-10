/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.ui.preferences;

import org.eclipse.chemclipse.msd.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.converter.ui.Activator;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ConverterPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ConverterPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("MSD Converter");
	}

	@Override
	protected void createFieldEditors() {

		addField(new StringFieldEditor(PreferenceSupplier.P_REFERENCE_IDENTIFIER_MARKER, "Reference ID Marker", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceSupplier.P_REFERENCE_IDENTIFIER_PREFIX, "Reference ID Prefix", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
