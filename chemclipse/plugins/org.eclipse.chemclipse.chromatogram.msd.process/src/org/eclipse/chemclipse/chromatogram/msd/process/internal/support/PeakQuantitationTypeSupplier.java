/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.internal.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.msd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.PeakQuantifier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class PeakQuantitationTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Peak Quantifier";
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		IPeakQuantifierSupplier quantifierSupplier = PeakQuantifier.getPeakQuantifierSupport().getPeakQuantifierSupplier(processorId);
		return quantifierSupplier.getPeakQuantifierName();
	}

	@Override
	public List<String> getPluginIds() throws Exception {

		return PeakQuantifier.getPeakQuantifierSupport().getAvailablePeakQuantifierIds();
	}

	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelectionMSD chromatogramSelection, String processorId, IProgressMonitor monitor) {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		for(IPeakMSD peak : chromatogramSelection.getChromatogramMSD().getPeaks()) {
			peaks.add(peak);
		}
		return PeakQuantifier.quantify(peaks, processorId, monitor);
	}
}
