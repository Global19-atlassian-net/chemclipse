/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig, Matthias Mailänder.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - refined the wavelength selection
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.selection;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.AbstractChromatogramSelection;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.notifier.ChromatogramSelectionWSDUpdateNotifier;

public class ChromatogramSelectionWSD extends AbstractChromatogramSelection implements IChromatogramSelectionWSD {

	private IScanWSD selectedScan;
	private IPeak selectedPeak;
	private IMarkedWavelengths selectedWavelengths;

	public ChromatogramSelectionWSD(IChromatogram chromatogram) throws ChromatogramIsNullException {
		this(chromatogram, true);
	}

	public ChromatogramSelectionWSD(IChromatogram chromatogram, boolean fireUpdate) throws ChromatogramIsNullException {
		/*
		 * Set all members to default values.<br/> This includes also to set a
		 * valid scan and if exists a valid peak.
		 */
		super(chromatogram, fireUpdate);
		/*
		 * Populate the list with wavelengths from the first scan of the currently loaded chromatogram.
		 */
		IChromatogramWSD wsdChromatogram = (IChromatogramWSD)chromatogram;
		IScanWSD scan = (IScanWSD)wsdChromatogram.getScans().stream().findFirst().get();
		selectedWavelengths = new MarkedWavelengths();
		for(IScanSignalWSD signal : scan.getScanSignals()) {
			selectedWavelengths.add(new MarkedWavelength(signal.getWavelength()));
		}
		//
		reset(fireUpdate);
	}

	public void dispose() {

		selectedScan = null;
		selectedPeak = null;
		super.dispose();
	}

	@Override
	public IChromatogramWSD getChromatogramWSD() {

		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramWSD) {
			return (IChromatogramWSD)chromatogram;
		}
		return null;
	}

	@Override
	public IScanWSD getSelectedScan() {

		return selectedScan;
	}

	@Override
	public IPeak getSelectedPeak() {

		return selectedPeak;
	}

	@Override
	public void reset() {

		reset(true);
	}

	@Override
	public void reset(boolean fireUpdate) {

		super.reset(fireUpdate);
		IChromatogram chromatogram = getChromatogram();
		/*
		 * Scan
		 */
		if(chromatogram.getNumberOfScans() >= 1) {
			/*
			 * Chromatogram WSD
			 */
			if(chromatogram instanceof IChromatogramWSD) {
				selectedScan = ((IChromatogramWSD)chromatogram).getSupplierScan(1);
			}
		} else {
			selectedScan = null;
		}
		/*
		 * Fire an update.
		 */
		if(fireUpdate) {
			ChromatogramSelectionWSDUpdateNotifier.fireUpdateChange(this, false);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan) {

		if(selectedScan instanceof IScanWSD) {
			setSelectedScan((IScanWSD)selectedScan);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan, boolean update) {

		if(selectedScan instanceof IScanWSD) {
			setSelectedScan((IScanWSD)selectedScan, update);
		}
	}

	@Override
	public void setSelectedScan(IScanWSD selectedScan) {

		/*
		 * FireUpdateChange will be called in the validate method.
		 */
		setSelectedScan(selectedScan, true);
	}

	@Override
	public void setSelectedScan(IScanWSD selectedScan, boolean update) {

		if(selectedScan != null) {
			this.selectedScan = selectedScan;
			/*
			 * Fire update change if necessary.
			 */
			if(update) {
				ChromatogramSelectionWSDUpdateNotifier.fireUpdateChange(this, false);
			}
		}
	}

	@Override
	public void fireUpdateChange(boolean forceReload) {

		ChromatogramSelectionWSDUpdateNotifier.fireUpdateChange(this, forceReload);
	}

	@Override
	public void update(boolean forceReload) {

		super.update(forceReload);
		setSelectedScan(selectedScan, false);
		fireUpdateChange(forceReload);
	}

	@Override
	public IMarkedWavelengths getSelectedWavelengths() {

		return selectedWavelengths;
	}
}
