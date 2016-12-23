/*******************************************************************************
 * Copyright (c) 2011, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.core.BackgroundIntegrator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.core.ISumareaIntegrator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.core.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings.ISumareaIntegrationSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.implementation.IntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ChromatogramIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.logging.core.Logger;

public class SumareaChromatogramIntegratorSupport implements ISumareaChromatogramIntegratorSupport {

	private static final Logger logger = Logger.getLogger(SumareaChromatogramIntegratorSupport.class);
	public static String INTEGRATOR_DESCRIPTION = "SumArea Integrator";

	@Override
	public IChromatogramIntegrationResults calculateChromatogramIntegrationResults(IChromatogramSelectionMSD chromatogramSelection, IChromatogramIntegrationSettings chromatogramIntegrationSettings, IProgressMonitor monitor) {

		boolean integrateAll = false;
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		IExtractedIonSignalExtractor extractedIonSignalExtractor;
		try {
			extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
			int startIon = extractedIonSignals.getStartIon();
			int stopIon = extractedIonSignals.getStopIon();
			/*
			 * Each ion result will be added to the results instance.
			 */
			IChromatogramIntegrationResults chromatogramIntegrationResults = new ChromatogramIntegrationResults();
			/*
			 * Selected Ions (size == 0 : integrate all)
			 */
			IMarkedIons selectedIons = getSelectedIons(chromatogramIntegrationSettings);
			Set<Integer> selectedIonsNominal = selectedIons.getIonsNominal();
			if(selectedIonsNominal.size() == 0 || selectedIonsNominal.contains(0)) {
				integrateAll = true;
			}
			/*
			 * Calculate area.
			 */
			ISumareaIntegrator chromatogramIntegrator = new ChromatogramIntegrator();
			ISumareaIntegrator backgroundIntegrator = new BackgroundIntegrator();
			/*
			 * Chromatogram Integration Entries
			 */
			List<IIntegrationEntry> chromatogramIntegrationEntries = new ArrayList<IIntegrationEntry>();
			List<IIntegrationEntry> backgroundIntegrationEntries = new ArrayList<IIntegrationEntry>();
			/*
			 * TODO Optimize
			 */
			for(int ion = startIon; ion <= stopIon; ion++) {
				monitor.subTask("Integrate the chromatogram area of ion: " + ion);
				if(integrateAll) {
					calculateIntegrationResults(ion, chromatogramIntegrator, backgroundIntegrator, chromatogramSelection, chromatogramIntegrationEntries, backgroundIntegrationEntries, chromatogramIntegrationResults);
				} else if(selectedIonsNominal.contains(ion)) {
					calculateIntegrationResults(ion, chromatogramIntegrator, backgroundIntegrator, chromatogramSelection, chromatogramIntegrationEntries, backgroundIntegrationEntries, chromatogramIntegrationResults);
				}
			}
			/*
			 * Set the integration entries.
			 */
			chromatogram.setChromatogramIntegratedArea(chromatogramIntegrationEntries, INTEGRATOR_DESCRIPTION);
			chromatogram.setBackgroundIntegratedArea(backgroundIntegrationEntries, INTEGRATOR_DESCRIPTION);
			//
			return chromatogramIntegrationResults;
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
			return null;
		}
	}

	/*
	 * TODO Optimize
	 */
	private void calculateIntegrationResults(int ion, ISumareaIntegrator chromatogramIntegrator, ISumareaIntegrator backgroundIntegrator, IChromatogramSelectionMSD chromatogramSelection, List<IIntegrationEntry> chromatogramIntegrationEntries, List<IIntegrationEntry> backgroundIntegrationEntries, IChromatogramIntegrationResults chromatogramIntegrationResults) {

		IIntegrationEntryMSD chromatogramIntegrationEntry = calculateChromatogramIonArea(ion, chromatogramIntegrator, chromatogramSelection);
		IIntegrationEntryMSD backgroundIntegrationEntry = calculateBackgroundIonArea(ion, backgroundIntegrator, chromatogramSelection);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		backgroundIntegrationEntries.add(backgroundIntegrationEntry);
		setIntegrationResult(ion, chromatogramIntegrationResults, chromatogramIntegrationEntry.getIntegratedArea(), backgroundIntegrationEntry.getIntegratedArea());
	}

	private void setIntegrationResult(int ion, IChromatogramIntegrationResults chromatogramIntegrationResults, double chromatogramArea, double backgroundArea) {

		/*
		 * Result
		 */
		IChromatogramIntegrationResult chromatogramIntegrationResult = new ChromatogramIntegrationResult(ion, chromatogramArea, backgroundArea);
		chromatogramIntegrationResults.add(chromatogramIntegrationResult);
	}

	private IIntegrationEntryMSD calculateChromatogramIonArea(int ion, ISumareaIntegrator chromatogramIntegrator, IChromatogramSelectionMSD chromatogramSelection) {

		double chromatogramArea = chromatogramIntegrator.integrate(chromatogramSelection, ion);
		IIntegrationEntryMSD chromatogramIntegrationEntry = new IntegrationEntryMSD(ion, chromatogramArea);
		return chromatogramIntegrationEntry;
	}

	private IIntegrationEntryMSD calculateBackgroundIonArea(int ion, ISumareaIntegrator backgroundIntegrator, IChromatogramSelectionMSD chromatogramSelection) {

		double backgroundArea = backgroundIntegrator.integrate(chromatogramSelection, ion);
		IIntegrationEntryMSD backgroundIntegrationEntry = new IntegrationEntryMSD(ion, backgroundArea);
		return backgroundIntegrationEntry;
	}

	private IMarkedIons getSelectedIons(IChromatogramIntegrationSettings chromatogramIntegrationSettings) {

		IMarkedIons selectedIons;
		if(chromatogramIntegrationSettings instanceof ISumareaIntegrationSettings) {
			selectedIons = ((ISumareaIntegrationSettings)chromatogramIntegrationSettings).getSelectedIons();
		} else {
			selectedIons = new MarkedIons();
		}
		return selectedIons;
	}
}
