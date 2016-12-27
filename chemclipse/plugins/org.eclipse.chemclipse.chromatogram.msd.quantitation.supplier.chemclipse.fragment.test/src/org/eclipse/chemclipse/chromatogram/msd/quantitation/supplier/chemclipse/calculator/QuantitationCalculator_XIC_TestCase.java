/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.calculator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;

public class QuantitationCalculator_XIC_TestCase extends ReferencePeakMSDTestCase {

	private IQuantitationCompoundMSD quantitationCompound;
	private List<IQuantitationPeakMSD> quantitationPeaks;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationCompound = new QuantitationCompoundMSD("Styrene", "mg/ml", 5500);
		quantitationCompound.setChemicalClass("Styrene-Butadiene");
		//
		quantitationPeaks = new ArrayList<IQuantitationPeakMSD>();
		IQuantitationPeakMSD quantitationPeak1 = new QuantitationPeakMSD(getReferencePeakMSD_XIC_1(), 0.08d, "mg/ml");
		quantitationPeaks.add(quantitationPeak1);
		IQuantitationPeakMSD quantitationPeak2 = new QuantitationPeakMSD(getReferencePeakMSD_XIC_2(), 0.09d, "mg/ml");
		quantitationPeaks.add(quantitationPeak2);
		IQuantitationPeakMSD quantitationPeak3 = new QuantitationPeakMSD(getReferencePeakMSD_XIC_3(), 0.06d, "mg/ml");
		quantitationPeaks.add(quantitationPeak3);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		quantitationCompound = null;
		quantitationPeaks = null;
	}

	public IQuantitationCompoundMSD getQuantitationCompound() {

		return quantitationCompound;
	}

	public List<IQuantitationPeakMSD> getQuantitationPeaks() {

		return quantitationPeaks;
	}
}
