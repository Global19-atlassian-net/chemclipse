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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support.Identifier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.IVendorPeakIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.VendorPeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author Dr. Philip Wenig
 */
public class PeakListIdentifierGUIRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(PeakListIdentifierGUIRunnable.class);
	private static final String DESCRIPTION = "NIST GUI Peak List Identifier";
	private IChromatogramSelectionMSD chromatogramSelection;

	public PeakListIdentifierGUIRunnable(IChromatogramSelectionMSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			/*
			 * Identify Peaks in actual chromatogram selection.
			 */
			IVendorPeakIdentifierSettings identifierSettings = new VendorPeakIdentifierSettings();
			identifierSettings.setNistApplication(PreferenceSupplier.getNistApplication());
			identifierSettings.setNumberOfTargets(PreferenceSupplier.getNumberOfTargets());
			identifierSettings.setStoreTargets(PreferenceSupplier.getStoreTargets());
			identifierSettings.setTimeoutInMinutes(PreferenceSupplier.getTimeoutInMinutes());
			List<IChromatogramPeakMSD> peaks = chromatogramSelection.getChromatogramMSD().getPeaks();
			/*
			 * TODO make generic
			 * May use a better generic supertype, e.g <? extends IPeak>???
			 */
			List<IPeakMSD> peakList = new ArrayList<IPeakMSD>();
			for(IChromatogramPeakMSD chromatogramPeak : peaks) {
				peakList.add(chromatogramPeak);
			}
			/*
			 * Open the GUI
			 */
			try {
				Identifier identifier = new Identifier();
				identifier.openNistForPeakIdentification(peakList, identifierSettings, monitor);
			} catch(FileNotFoundException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}
}
