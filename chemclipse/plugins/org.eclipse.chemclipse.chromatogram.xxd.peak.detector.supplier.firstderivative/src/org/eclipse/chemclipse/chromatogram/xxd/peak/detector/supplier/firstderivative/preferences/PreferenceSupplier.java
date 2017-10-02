/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.FirstDerivativePeakDetectorCSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.FirstDerivativePeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.IFirstDerivativePeakDetectorCSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.IFirstDerivativePeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.Threshold;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_THRESHOLD = "threshold";
	public static final String DEF_THRESHOLD = Threshold.MEDIUM.toString();
	public static final String P_INCLUDE_BACKGROUND = "includeBackground";
	public static final boolean DEF_INCLUDE_BACKGROUND = false; // false will use BV oder VB, if true VV will be used.
	public static final String P_MIN_SN_RATIO = "minSNRatio";
	public static final float DEF_MIN_SN_RATIO = 0.0f; // 0 = all peaks will be added
	public static final String P_MOVING_AVERAGE_WINDOW_SIZE = "movingAverageWindowSize";
	public static final String DEF_MOVING_AVERAGE_WINDOW_SIZE = WindowSize.SCANS_3.toString();
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
		defaultValues.put(P_INCLUDE_BACKGROUND, Boolean.toString(DEF_INCLUDE_BACKGROUND));
		defaultValues.put(P_MIN_SN_RATIO, Float.toString(DEF_MIN_SN_RATIO));
		defaultValues.put(P_MOVING_AVERAGE_WINDOW_SIZE, DEF_MOVING_AVERAGE_WINDOW_SIZE);
		defaultValues.put(P_THRESHOLD, DEF_THRESHOLD);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static IFirstDerivativePeakDetectorMSDSettings getPeakDetectorMSDSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		IFirstDerivativePeakDetectorMSDSettings peakDetectorSettings = new FirstDerivativePeakDetectorMSDSettings();
		peakDetectorSettings.setThreshold(preferences.get(P_THRESHOLD, DEF_THRESHOLD));
		peakDetectorSettings.setIncludeBackground(preferences.getBoolean(P_INCLUDE_BACKGROUND, DEF_INCLUDE_BACKGROUND));
		peakDetectorSettings.setMinimumSignalToNoiseRatio(preferences.getFloat(P_MIN_SN_RATIO, DEF_MIN_SN_RATIO));
		peakDetectorSettings.setWindowSize(preferences.get(P_MOVING_AVERAGE_WINDOW_SIZE, DEF_MOVING_AVERAGE_WINDOW_SIZE));
		return peakDetectorSettings;
	}

	public static IFirstDerivativePeakDetectorCSDSettings getPeakDetectorCSDSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		IFirstDerivativePeakDetectorCSDSettings peakDetectorSettings = new FirstDerivativePeakDetectorCSDSettings();
		peakDetectorSettings.setThreshold(Threshold.valueOf(preferences.get(P_THRESHOLD, DEF_THRESHOLD)));
		peakDetectorSettings.setIncludeBackground(preferences.getBoolean(P_INCLUDE_BACKGROUND, DEF_INCLUDE_BACKGROUND));
		peakDetectorSettings.setMinimumSignalToNoiseRatio(preferences.getFloat(P_MIN_SN_RATIO, DEF_MIN_SN_RATIO));
		peakDetectorSettings.setMovingAverageWindowSize(getMovingAverageWindowSize());
		return peakDetectorSettings;
	}

	public static WindowSize getMovingAverageWindowSize() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String size = preferences.get(P_MOVING_AVERAGE_WINDOW_SIZE, DEF_MOVING_AVERAGE_WINDOW_SIZE);
		return WindowSize.valueOf(size);
	}

	/**
	 * Returns the threshold stored in the settings.
	 * 
	 * @return {@link Threshold}
	 */
	public static Threshold getThreshold() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String threshold = preferences.get(P_THRESHOLD, DEF_THRESHOLD);
		if(threshold.equals("")) {
			/*
			 * TODO initialize preferenceStore at startup There is something
			 * wrong when the preferenceStore isn't not actually
			 * initialized.<br/> threshold =
			 * preferenceStore.getDefaultString(PreferenceConstants
			 * .P_THRESHOLD);
			 */
			threshold = Threshold.OFF.toString();
		}
		return Threshold.valueOf(threshold);
	}

	public static boolean isIncludeBackground() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_INCLUDE_BACKGROUND, DEF_INCLUDE_BACKGROUND);
	}

	public static float getMinimumSignalToNoiseRatio() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getFloat(P_MIN_SN_RATIO, DEF_MIN_SN_RATIO);
	}
}
