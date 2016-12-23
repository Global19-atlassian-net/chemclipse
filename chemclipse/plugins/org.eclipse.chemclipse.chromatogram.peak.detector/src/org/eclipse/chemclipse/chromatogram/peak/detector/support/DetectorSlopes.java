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
package org.eclipse.chemclipse.chromatogram.peak.detector.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

/**
 * @author eselmeister
 */
public class DetectorSlopes implements IDetectorSlopes {

	private List<IDetectorSlope> slopes;
	private int startScan;
	private int stopScan;

	public DetectorSlopes(ITotalScanSignals totalIonSignals) {
		startScan = totalIonSignals.getStartScan();
		stopScan = totalIonSignals.getStopScan() - 1; // Why -1? Because the
														// slope will be
														// calculated between
														// the actual and the
														// next scan. So the
														// last scan will be
														// dropped.
		int amount = stopScan - startScan + 1;
		slopes = new ArrayList<IDetectorSlope>(amount);
	}

	// ----------------------------------------IFirstDerivativeSlopes
	// TODO JUnit
	@Override
	public int getStartScan() {

		return startScan;
	}

	// TODO JUnit
	@Override
	public int getStopScan() {

		return stopScan;
	}

	@Override
	public void add(IDetectorSlope detectorSlope) {

		slopes.add(detectorSlope);
	}

	@Override
	public void calculateMovingAverage(WindowSize windowSize) {

		/*
		 * Return if the windowSize is null.
		 */
		if(windowSize == null) {
			return;
		}
		/*
		 * Return if the available number of slopes are lower than the window
		 * size.
		 */
		if(slopes.size() < windowSize.getSize()) {
			return;
		}
		int diff = windowSize.getSize() / 2;
		int windowStop = windowSize.getSize() - diff;
		/*
		 * Moving average calculation.
		 */
		int size = slopes.size() - diff;
		double[] values = new double[windowSize.getSize()];
		for(int i = diff; i < size; i++) {
			for(int j = -diff, k = 0; j < windowStop; j++, k++) {
				values[k] = slopes.get(i + j).getSlope();
			}
			/*
			 * Set the new slope value.
			 */
			slopes.get(i).setSlope(Calculations.getMean(values));
		}
	}

	@Override
	public IDetectorSlope getDetectorSlope(int scan) {

		if(scan >= startScan && scan <= stopScan) {
			scan -= startScan;
			return slopes.get(scan);
		} else {
			return null;
		}
	}

	@Override
	public int size() {

		return slopes.size();
	}
	// ----------------------------------------IFirstDerivativeSlopes
}
