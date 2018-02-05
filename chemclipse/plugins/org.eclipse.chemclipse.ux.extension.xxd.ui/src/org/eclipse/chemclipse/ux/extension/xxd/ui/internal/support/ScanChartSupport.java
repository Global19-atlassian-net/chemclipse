/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonValueComparator;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.wsd.model.comparator.WavelengthValueComparator;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.eavp.service.swtchart.barcharts.BarSeriesData;
import org.eclipse.eavp.service.swtchart.barcharts.IBarSeriesData;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;

public class ScanChartSupport {

	private IonValueComparator ionValueComparator = new IonValueComparator(SortOrder.ASC);
	private WavelengthValueComparator wavelengthValueComparator = new WavelengthValueComparator(SortOrder.ASC);

	public IBarSeriesData getBarSeriesData(IScan scan, String postfix, boolean mirrored) {

		ISeriesData seriesData = getSeriesData(scan, postfix, mirrored);
		IBarSeriesData barSeriesData = new BarSeriesData(seriesData);
		return barSeriesData;
	}

	public ILineSeriesData getLineSeriesData(IScan scan, String postfix, boolean mirrored) {

		ISeriesData seriesData = getSeriesData(scan, postfix, mirrored);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		return lineSeriesData;
	}

	public ILineSeriesData getLineSeriesDataPoint(IScan scan, boolean mirrored, String seriesId) {

		double[] xSeries = new double[1];
		double[] ySeries = new double[1];
		//
		if(scan != null) {
			xSeries[0] = scan.getRetentionTime();
			ySeries[0] = (mirrored) ? scan.getTotalSignal() * -1 : scan.getTotalSignal();
		}
		//
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		return lineSeriesData;
	}

	private ISeriesData getSeriesData(IScan scan, String postfix, boolean mirrored) {

		double[] xSeries;
		double[] ySeries;
		String scanNumber = (scan.getScanNumber() > 0 ? Integer.toString(scan.getScanNumber()) : "--");
		String id = "Scan " + scanNumber;
		if(!"".equals(postfix)) {
			id += " " + postfix;
		}
		/*
		 * Sort the scan data, otherwise the line chart could be odd.
		 */
		if(scan instanceof IScanMSD) {
			/*
			 * MSD
			 */
			IScanMSD scanMSD = (IScanMSD)scan;
			List<IIon> ions = new ArrayList<IIon>(scanMSD.getIons());
			Collections.sort(ions, ionValueComparator);
			int size = ions.size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IIon ion : ions) {
				xSeries[index] = ion.getIon();
				ySeries[index] = (mirrored) ? ion.getAbundance() * -1 : ion.getAbundance();
				index++;
			}
		} else if(scan instanceof IScanCSD) {
			/*
			 * CSD
			 */
			IScanCSD scanCSD = (IScanCSD)scan;
			xSeries = new double[]{scanCSD.getRetentionTime()};
			ySeries = new double[]{(mirrored) ? scanCSD.getTotalSignal() * -1 : scanCSD.getTotalSignal()};
		} else if(scan instanceof IScanWSD) {
			/*
			 * WSD
			 */
			IScanWSD scanWSD = (IScanWSD)scan;
			List<IScanSignalWSD> scanSignalsWSD = new ArrayList<IScanSignalWSD>(scanWSD.getScanSignals());
			Collections.sort(scanSignalsWSD, wavelengthValueComparator);
			int size = scanSignalsWSD.size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IScanSignalWSD scanSignalWSD : scanSignalsWSD) {
				xSeries[index] = scanSignalWSD.getWavelength();
				ySeries[index] = (mirrored) ? scanSignalWSD.getAbundance() * -1 : scanSignalWSD.getAbundance();
				index++;
			}
		} else {
			xSeries = new double[0];
			ySeries = new double[0];
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}
}
