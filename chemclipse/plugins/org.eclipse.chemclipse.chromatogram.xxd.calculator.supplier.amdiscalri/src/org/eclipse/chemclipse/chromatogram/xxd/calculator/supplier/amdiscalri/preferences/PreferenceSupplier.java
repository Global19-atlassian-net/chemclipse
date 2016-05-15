/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.IRetentionIndexFilterSettingsPeak;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.ISupplierCalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.RetentionIndexFilterSettingsPeak;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.SupplierCalculatorSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_PATH_RI_FILE = "pathCalibrationFile";
	public static final String DEF_PATH_RI_FILE = "";
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_PATH_RI_FILE, DEF_PATH_RI_FILE);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ISupplierCalculatorSettings getChromatogramCalculatorSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String pathCalibrationFile = preferences.get(P_PATH_RI_FILE, DEF_PATH_RI_FILE);
		ISupplierCalculatorSettings chromatogramCalculatorSettings = new SupplierCalculatorSettings();
		chromatogramCalculatorSettings.setPathRetentionIndexFile(pathCalibrationFile);
		return chromatogramCalculatorSettings;
	}

	public static IRetentionIndexFilterSettingsPeak getPeakFilterSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String pathCalibrationFile = preferences.get(P_PATH_RI_FILE, DEF_PATH_RI_FILE);
		IRetentionIndexFilterSettingsPeak peakFilterSettings = new RetentionIndexFilterSettingsPeak();
		peakFilterSettings.setPathRetentionIndexFile(pathCalibrationFile);
		return peakFilterSettings;
	}
}
