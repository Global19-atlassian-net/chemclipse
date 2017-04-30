/*******************************************************************************
 * Copyright (c) 2012, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.runnables;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support.Identifier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.IVendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.VendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author Dr. Philip Wenig
 */
public class MassSpectrumIdentifierGUIRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(MassSpectrumIdentifierGUIRunnable.class);
	private static final String DESCRIPTION = "NIST GUI Mass Spectrum Identifier";
	private IChromatogramSelectionMSD chromatogramSelection;

	public MassSpectrumIdentifierGUIRunnable(IChromatogramSelectionMSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			/*
			 * Identify Peaks in actual chromatogram selection.
			 */
			IVendorMassSpectrumIdentifierSettings identifierSettings = new VendorMassSpectrumIdentifierSettings();
			identifierSettings.setNistApplication(PreferenceSupplier.getNistApplication());
			identifierSettings.setNumberOfTargets(PreferenceSupplier.getNumberOfTargets());
			identifierSettings.setStoreTargets(PreferenceSupplier.getStoreTargets());
			identifierSettings.setTimeoutInMinutes(PreferenceSupplier.getTimeoutInMinutes());
			IScanMSD massSpectrum = chromatogramSelection.getSelectedScan();
			IMassSpectra massSpectra = new MassSpectra();
			massSpectra.addMassSpectrum(massSpectrum);
			/*
			 * Open the GUI
			 */
			try {
				Identifier identifier = new Identifier();
				identifier.openNistForMassSpectrumIdentification(massSpectra, identifierSettings, monitor);
			} catch(FileNotFoundException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}
}
