/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.ui.internal.handler;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.CombinedIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.CombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.ICombinedIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ICombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.ITrapezoidPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.notifier.IntegrationResultUpdateNotifier;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class CombinedIntegratorRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(CombinedIntegratorRunnable.class);
	private static final String COMBINED_INTEGRATOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.combinedIntegrator";
	private IChromatogramSelection chromatogramSelection;

	public CombinedIntegratorRunnable(IChromatogramSelection chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Integrator Trapezoid", IProgressMonitor.UNKNOWN);
			/*
			 * Detect Peaks in actual chromatogram selection.
			 */
			IChromatogramIntegrationSettings chromatogramIntegrationSettings = new ChromatogramIntegrationSettings();
			/*
			 * Peak Settings
			 */
			ITrapezoidPeakIntegrationSettings peakIntegrationSettings = PreferenceSupplier.getPeakIntegrationSettings();
			ICombinedIntegrationSettings combinedIntegrationSettings = new CombinedIntegrationSettings(chromatogramIntegrationSettings, peakIntegrationSettings);
			/*
			 * Show the processing view if error messages occurred.
			 */
			ICombinedIntegratorProcessingInfo processingInfo = CombinedIntegrator.integrate(chromatogramSelection, combinedIntegrationSettings, COMBINED_INTEGRATOR_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			/*
			 * Try to set the results.
			 */
			try {
				ICombinedIntegrationResult combinedIntegrationResult = processingInfo.getCombinedIntegrationResult();
				IntegrationResultUpdateNotifier.fireUpdateChange(combinedIntegrationResult);
				updateSelection();
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}

	// ---------------------------------------------------------private methods
	/*
	 * Updates the selection using the GUI thread.
	 */
	private void updateSelection() {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
					((ChromatogramSelectionMSD)chromatogramSelection).update(false);
				} else if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
					((ChromatogramSelectionCSD)chromatogramSelection).update(false);
				}
			}
		});
	}
	// ---------------------------------------------------------private methods
}
