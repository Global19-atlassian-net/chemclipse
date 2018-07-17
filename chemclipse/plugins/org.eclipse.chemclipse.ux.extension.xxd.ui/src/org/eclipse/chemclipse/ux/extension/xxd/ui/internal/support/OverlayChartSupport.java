/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class OverlayChartSupport {

	public static final String OVERLAY_TYPE_CONCATENATOR = "+";
	public static final String ESCAPE_CONCATENATOR = "\\";
	public static final String SELECTED_IONS_CONCATENATOR = " ";
	public static final String OVERLAY_START_MARKER = "_(";
	public static final String OVERLAY_STOP_MARKER = ")";
	public static final String DELIMITER_SIGNAL_DERIVATIVE = ",";
	//
	public static final String SELECTED_IONS_USERS_CHOICE = "Users Choice";
	public static final String SELECTED_IONS_HYDROCARBONS = "Hydrocarbons";
	public static final String SELECTED_IONS_FATTY_ACIDS = "Fatty Acids";
	public static final String SELECTED_IONS_FAME = "FAME";
	public static final String SELECTED_IONS_SOLVENT_TAILING = "Solvent Tailing";
	public static final String SELECTED_IONS_COLUMN_BLEED = "Column Bleed";
	//
	public static String[][] SELECTED_IONS_CHOICES = new String[][]{//
			{"Users Choice", SELECTED_IONS_USERS_CHOICE}, //
			{"Hydrocarbons", SELECTED_IONS_HYDROCARBONS}, //
			{"Fatty Acids", SELECTED_IONS_FATTY_ACIDS}, //
			{"FAME", SELECTED_IONS_FAME}, //
			{"Solvent Tailing", SELECTED_IONS_SOLVENT_TAILING}, //
			{"Column Bleed", SELECTED_IONS_COLUMN_BLEED}//
	};
	//
	public static final String DISPLAY_MODUS_NORMAL = "Normal";
	public static final String DISPLAY_MODUS_MIRRORED = "Mirrored";
	//
	private String[] overlayTypes;
	private String[] derivativeTypes;
	private String[] selectedIons;
	private String[] displayModi;
	//

	public OverlayChartSupport() {
		initialize();
	}

	public String[] getOverlayTypes() {

		return overlayTypes;
	}

	public String[] getDerivativeTypes() {

		return derivativeTypes;
	}

	public String[] getSelectedIons() {

		return selectedIons;
	}

	public String[] getDisplayModi() {

		return displayModi;
	}

	public double getSettingsMinutesShiftX() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		return preferenceStore.getDouble(PreferenceConstants.P_MINUTES_SHIFT_X);
	}

	public void setSettingsMinutesShiftX(double value) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setValue(PreferenceConstants.P_MINUTES_SHIFT_X, value);
	}

	public double getSettingsAbsoluteShiftY() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		return preferenceStore.getDouble(PreferenceConstants.P_ABSOLUTE_SHIFT_Y);
	}

	public void setSettingsAbsoluteShiftY(double value) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setValue(PreferenceConstants.P_ABSOLUTE_SHIFT_Y, value);
	}

	private void initialize() {

		overlayTypes = new String[]{//
				ChromatogramChartSupport.DISPLAY_TYPE_TIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_BPC, //
				ChromatogramChartSupport.DISPLAY_TYPE_XIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_SIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TSC, //
				ChromatogramChartSupport.DISPLAY_TYPE_SWC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + ChromatogramChartSupport.DISPLAY_TYPE_BPC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + ChromatogramChartSupport.DISPLAY_TYPE_XIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + ChromatogramChartSupport.DISPLAY_TYPE_SIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + ChromatogramChartSupport.DISPLAY_TYPE_TSC};
		//
		derivativeTypes = new String[]{//
				ChromatogramChartSupport.DERIVATIVE_NONE, //
				ChromatogramChartSupport.DERIVATIVE_FIRST, //
				ChromatogramChartSupport.DERIVATIVE_SECOND, //
				ChromatogramChartSupport.DERIVATIVE_THIRD};
		//
		selectedIons = new String[]{//
				SELECTED_IONS_USERS_CHOICE, //
				SELECTED_IONS_HYDROCARBONS, //
				SELECTED_IONS_FATTY_ACIDS, //
				SELECTED_IONS_FAME, //
				SELECTED_IONS_SOLVENT_TAILING, //
				SELECTED_IONS_COLUMN_BLEED};
		//
		displayModi = new String[]{//
				DISPLAY_MODUS_NORMAL, //
				DISPLAY_MODUS_MIRRORED //
		};
	}
}
