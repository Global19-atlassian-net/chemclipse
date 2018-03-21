/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.model.core.identifier.chromatogram.IChromatogramTargetCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public abstract class AbstractChromatogramCSD extends AbstractChromatogram<IChromatogramPeakCSD> implements IChromatogramCSD {

	private static final long serialVersionUID = -1514838958855146167L;
	//
	private Set<IChromatogramTargetCSD> targets;
	private INoiseCalculator noiseCalculator;

	public AbstractChromatogramCSD() {
		targets = new HashSet<IChromatogramTargetCSD>();
		String noiseCalculatorId = PreferenceSupplier.getSelectedNoiseCalculatorId();
		noiseCalculator = NoiseCalculator.getNoiseCalculator(noiseCalculatorId);
		if(noiseCalculator != null) {
			int segmentWidth = PreferenceSupplier.getSelectedSegmentWidth();
			noiseCalculator.setChromatogram(this, segmentWidth);
		}
	}

	@Override
	public void recalculateTheNoiseFactor() {

		if(noiseCalculator != null) {
			noiseCalculator.recalculate();
		}
	}

	@Override
	public float getSignalToNoiseRatio(float abundance) {

		if(noiseCalculator != null) {
			return noiseCalculator.getSignalToNoiseRatio(abundance);
		}
		return 0;
	}

	// -----------------------------------------------------
	@Override
	public IScanCSD getSupplierScan(int scan) {

		int position = scan;
		if(position > 0 && position <= getScans().size()) {
			IScan storedScan = getScans().get(--position);
			if(storedScan instanceof IScanCSD) {
				return (IScanCSD)storedScan;
			}
		}
		return null;
	}

	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

		/*
		 * Fire an update to inform all listeners.
		 */
		if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
			((ChromatogramSelectionCSD)chromatogramSelection).update(true);
		}
	}

	// -----------------------------------------------------------do, undo, redo
	@Override
	public double getPeakIntegratedArea() {

		double integratedArea = 0.0d;
		for(IChromatogramPeakCSD peak : getPeaks()) {
			integratedArea += peak.getIntegratedArea();
		}
		return integratedArea;
	}

	@Override
	public void addTarget(IChromatogramTargetCSD chromatogramTarget) {

		if(chromatogramTarget != null) {
			targets.add(chromatogramTarget);
		}
	}

	@Override
	public void removeTarget(IChromatogramTargetCSD chromatogramTarget) {

		targets.remove(chromatogramTarget);
	}

	@Override
	public void removeTargets(List<IChromatogramTargetCSD> targetsToRemove) {

		targets.removeAll(targetsToRemove);
	}

	@Override
	public void removeAllTargets() {

		targets.clear();
	}

	@Override
	public List<IChromatogramTargetCSD> getTargets() {

		List<IChromatogramTargetCSD> targetList = new ArrayList<IChromatogramTargetCSD>(targets);
		return targetList;
	}
}
