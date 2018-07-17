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

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.eavp.service.swtchart.preferences.PreferenceSupport;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageOverlay extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageOverlay() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Overlay");
	}

	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE, "Compression Type:", PreferenceConstants.COMPRESSION_TYPES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		//
		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_NORMAL_OVERLAY, "Display Color Scheme Normal", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS, "Show Referenced Chromatograms", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SIC_OVERLAY, "Display Color Scheme SIC", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SWC_OVERLAY, "Display Color Scheme SWC", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_TIC_OVERLAY, "Line Style TIC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_BPC_OVERLAY, "Line Style BPC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_XIC_OVERLAY, "Line Style XIC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_SIC_OVERLAY, "Line Style SIC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_TSC_OVERLAY, "Line Style TSC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_SWC_OVERLAY, "Line Style SWC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_DEFAULT_OVERLAY, "Line Style Default:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		//
		addField(new DoubleFieldEditor(PreferenceConstants.P_MINUTES_SHIFT_X, "Shift X (Minutes):", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceConstants.P_ABSOLUTE_SHIFT_Y, "Shift Y (Absolute):", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		//
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, "Selected Overlay:", OverlayChartSupport.SELECTED_IONS_CHOICES, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE, "Overlay Ions User Choice:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_HYDROCARBONS, "Overlay Ions Hydrocarbons:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FATTY_ACIDS, "Overlay Ions Fatty Acids:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FAME, "Overlay Ions FAME:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SOLVENT_TAILING, "Overlay Ions Solvent Tailing:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_COLUMN_BLEED, "Overlay Ions Column Bleed:", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
