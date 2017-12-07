/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;

public class ScanSupport {

	private static DecimalFormat DECIMAL_FORMAT = ValueFormat.getDecimalFormatEnglish("0.0##");
	//
	public static final String[] DATA_TYPES_DEFAULT = new String[]{DataType.AUTO_DETECT.toString()};
	public static final String[] DATA_TYPES_MSD = new String[]{DataType.AUTO_DETECT.toString(), DataType.MSD_NOMINAL.toString(), DataType.MSD_TANDEM.toString(), DataType.MSD_HIGHRES.toString()};
	public static final String[] DATA_TYPES_CSD = new String[]{DataType.AUTO_DETECT.toString(), DataType.CSD.toString()};
	public static final String[] DATA_TYPES_WSD = new String[]{DataType.AUTO_DETECT.toString(), DataType.WSD.toString()};
	//
	public static final String[] SIGNAL_TYPES_DEFAULT = new String[]{SignalType.AUTO_DETECT.toString()};
	public static final String[] SIGNAL_TYPES_MSD = new String[]{SignalType.AUTO_DETECT.toString(), SignalType.CENTROID.toString(), SignalType.PROFILE.toString()};
	public static final String[] SIGNAL_TYPES_CSD = new String[]{SignalType.AUTO_DETECT.toString(), SignalType.CENTROID.toString()};
	public static final String[] SIGNAL_TYPES_WSD = new String[]{SignalType.AUTO_DETECT.toString(), SignalType.CENTROID.toString(), SignalType.PROFILE.toString()};

	private ScanSupport() {
	}

	public static String getScanLabel(IScan scan) {

		StringBuilder builder = new StringBuilder();
		if(scan != null) {
			if(scan instanceof IPeakMassSpectrum) {
				builder.append("Peak Scan");
				builder.append(" | ");
			} else {
				builder.append("Scan: ");
				builder.append(scan.getScanNumber());
				builder.append(" | ");
			}
			//
			builder.append("RT: ");
			builder.append(DECIMAL_FORMAT.format(scan.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
			builder.append(" | ");
			builder.append("RI: ");
			if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
				builder.append(Integer.toString((int)scan.getRetentionIndex()));
			} else {
				builder.append(DECIMAL_FORMAT.format(scan.getRetentionIndex()));
			}
			//
			if(scan instanceof IRegularMassSpectrum) {
				IRegularMassSpectrum massSpectrum = (IRegularMassSpectrum)scan;
				builder.append(" | ");
				builder.append("Detector: MS");
				builder.append(massSpectrum.getMassSpectrometer());
				builder.append(" | ");
				builder.append("Type: ");
				builder.append(massSpectrum.getMassSpectrumTypeDescription());
			}
			//
			builder.append(" | ");
			builder.append("Signal: ");
			builder.append((int)scan.getTotalSignal());
			//
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
				if(optimizedMassSpectrum != null) {
					builder.append(" | ");
					builder.append("optimized");
				}
			}
		} else {
			builder.append("No scan has been selected yet.");
		}
		return builder.toString();
	}

	public static boolean containsOptimizedScan(IScan scan) {

		boolean containsOptimizedScan = false;
		//
		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
			if(optimizedMassSpectrum != null) {
				containsOptimizedScan = true;
			}
		}
		//
		return containsOptimizedScan;
	}
}
