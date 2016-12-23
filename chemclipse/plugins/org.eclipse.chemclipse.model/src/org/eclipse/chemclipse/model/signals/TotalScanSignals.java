/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.signals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.ChromatogramSelection;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.numeric.statistics.Calculations;

public class TotalScanSignals implements ITotalScanSignals {

	private List<ITotalScanSignal> signals;
	private int startScan;
	private int stopScan;
	private IChromatogram chromatogram = null;

	/**
	 * Creates a TotalIonSignals instance with the given scan length.
	 * 
	 * @param numberOfScans
	 */
	public TotalScanSignals(int numberOfScans) {
		if(numberOfScans <= 0) {
			numberOfScans = 0;
			startScan = 0;
			stopScan = 0;
		} else {
			startScan = 1;
			stopScan = numberOfScans;
		}
		signals = new ArrayList<ITotalScanSignal>(numberOfScans);
	}

	/**
	 * Sets additionally the parent chromatogram to the signals instance.
	 */
	public TotalScanSignals(int numberOfScans, IChromatogram chromatogram) {
		this(numberOfScans);
		this.chromatogram = chromatogram;
	}

	/**
	 * Creates a {@link TotalScanSignals} instance.<br/>
	 * The start and stop scan needs also to be specified, if e.g. only a
	 * selection from scan 40 - 60 is chosen.
	 * 
	 * @param startScan
	 * @param stopScan
	 */
	public TotalScanSignals(int startScan, int stopScan) {
		startScan = (startScan <= 0) ? 0 : startScan;
		stopScan = (stopScan <= 0) ? 0 : stopScan;
		int start = Math.min(startScan, stopScan);
		int stop = Math.max(startScan, stopScan);
		int numberOfScans;
		if(start == 0 || stop == 0) {
			numberOfScans = 0;
			start = 0;
			stop = 0;
		} else {
			numberOfScans = stop - start + 1;
		}
		signals = new ArrayList<ITotalScanSignal>(numberOfScans);
		this.startScan = start;
		this.stopScan = stop;
	}

	/**
	 * Sets additionally the parent chromatogram to the signals instance.
	 * 
	 * @param startScan
	 * @param stopScan
	 * @param chromatogram
	 */
	public TotalScanSignals(int startScan, int stopScan, IChromatogram chromatogram) {
		this(startScan, stopScan);
		this.chromatogram = chromatogram;
	}

	// TODO JUnit
	public TotalScanSignals(IChromatogram chromatogram) throws ChromatogramIsNullException {
		this(new ChromatogramSelection(chromatogram));
	}

	// TODO JUnit
	public TotalScanSignals(IChromatogramSelection chromatogramSelection) {
		chromatogram = chromatogramSelection.getChromatogram();
		startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		signals = new ArrayList<ITotalScanSignal>();
		//
		for(int scan = startScan; scan <= stopScan; scan++) {
			/*
			 * Extract the signals.
			 */
			IScan supplierScan = chromatogram.getScan(scan);
			int retentionTime = supplierScan.getRetentionTime();
			float retentionIndex = supplierScan.getRetentionIndex();
			float totalSignal = supplierScan.getTotalSignal();
			signals.add(new TotalScanSignal(retentionTime, retentionIndex, totalSignal));
		}
	}

	// ---------------------------------------------ITotalIonSignals
	@Override
	public IChromatogram getChromatogram() {

		return chromatogram;
	}

	@Override
	public void add(ITotalScanSignal totalScanSignal) {

		signals.add(totalScanSignal);
	}

	@Override
	public ITotalScanSignal getTotalScanSignal(int scan) {

		if(scan <= 0) {
			return null;
		}
		if(scan < startScan || scan > stopScan) {
			return null;
		}
		int correction = startScan - 1;
		scan -= correction;
		return signals.get(--scan);
	}

	@Override
	public ITotalScanSignal getNextTotalScanSignal(int scan) {

		ITotalScanSignal signal = getTotalScanSignal(++scan);
		return signal;
	}

	@Override
	public ITotalScanSignal getPreviousTotalScanSignal(int scan) {

		ITotalScanSignal signal = getTotalScanSignal(--scan);
		return signal;
	}

	@Override
	public int size() {

		return signals.size();
	}

	@Override
	public int getStartScan() {

		return startScan;
	}

	@Override
	public int getStopScan() {

		return stopScan;
	}

	@Override
	public float getMaxSignal() {

		if(size() == 0) {
			return 0.0f;
		}
		/*
		 * Get the highest value.
		 */
		float[] values = getValues();
		float max = Calculations.getMax(values);
		return max;
	}

	@Override
	public float getMinSignal() {

		if(size() == 0) {
			return 0.0f;
		}
		/*
		 * Get the lowest value.
		 */
		float[] values = getValues();
		float min = Calculations.getMin(values);
		return min;
	}

	@Override
	public ITotalScanSignals makeDeepCopy() {

		ITotalScanSignals totalIonSignals = new TotalScanSignals(getStartScan(), getStopScan());
		ITotalScanSignal totalIonSignal;
		for(ITotalScanSignal signal : signals) {
			totalIonSignal = signal.makeDeepCopy();
			totalIonSignals.add(totalIonSignal);
		}
		return totalIonSignals;
	}

	@Override
	public List<ITotalScanSignal> getTotalScanSignals() {

		return new ArrayList<ITotalScanSignal>(signals);
	}

	@Override
	public ITotalScanSignal getMaxTotalScanSignal() {

		return Collections.max(signals, new TotalScanSignalComparator());
	}

	@Override
	public ITotalScanSignal getMinTotalScanSignal() {

		return Collections.min(signals, new TotalScanSignalComparator());
	}

	@Override
	public void setNegativeTotalSignalsToZero() {

		for(ITotalScanSignal signal : signals) {
			if(signal.getTotalSignal() < 0) {
				signal.setTotalSignal(0.0f);
			}
		}
	}

	@Override
	public void setPositiveTotalSignalsToZero() {

		for(ITotalScanSignal signal : signals) {
			if(signal.getTotalSignal() > 0) {
				signal.setTotalSignal(0.0f);
			}
		}
	}

	@Override
	public void setTotalSignalsAsAbsoluteValues() {

		float abundance = 0.0f;
		for(ITotalScanSignal signal : signals) {
			abundance = Math.abs(signal.getTotalSignal());
			signal.setTotalSignal(abundance);
		}
	}

	// ---------------------------------------------ITotalIonSignals
	/**
	 * Returns all total ion signals as an float array.
	 * 
	 * @return float[]
	 */
	private float[] getValues() {

		float[] values = new float[size()];
		int i = 0;
		for(ITotalScanSignal signal : signals) {
			values[i++] = signal.getTotalSignal();
		}
		return values;
	}
}
