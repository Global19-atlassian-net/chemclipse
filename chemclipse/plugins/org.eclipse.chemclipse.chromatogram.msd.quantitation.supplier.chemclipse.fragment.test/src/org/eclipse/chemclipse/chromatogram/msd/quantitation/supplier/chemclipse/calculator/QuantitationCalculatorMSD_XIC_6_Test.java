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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.calculator;

import java.util.List;

import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.msd.model.core.quantitation.IConcentrationResponseEntriesMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationPeakMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationSignalsMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

public class QuantitationCalculatorMSD_XIC_6_Test extends QuantitationCalculator_XIC_TestCase {

	/*
	 * UseTIC: false (-> m/z 180) -> does not exist
	 * CalibrationMethod: LINEAR
	 * isZeroCrossing: false
	 */
	private IQuantitationCompoundMSD quantitationCompound;
	private IQuantitationSignalsMSD quantitationSignals;
	private IConcentrationResponseEntriesMSD concentrationResponseEntries;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
		quantitationCompound = getQuantitationCompound();
		List<IQuantitationPeakMSD> quantitationPeaks = getQuantitationPeaks();
		for(IQuantitationPeakMSD peak : quantitationPeaks) {
			peak.getReferencePeakMSD().getPeakModel().getPeakMassSpectrum().addIon(new Ion(108.0, 54903.5f));
		}
		//
		quantitationCompound.setUseTIC(false);
		quantitationCompound.setCalibrationMethod(CalibrationMethod.LINEAR);
		quantitationCompound.calculateQuantitationSignalsAndConcentrationResponseEntries(quantitationPeaks);
		//
		quantitationSignals = quantitationCompound.getQuantitationSignals();
		concentrationResponseEntries = quantitationCompound.getConcentrationResponseEntries();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		quantitationCompound = null;
		quantitationSignals = null;
		concentrationResponseEntries = null;
	}

	public void testSize_1() {

		assertEquals(0, quantitationSignals.size());
	}

	public void testSize_2() {

		assertEquals(0, concentrationResponseEntries.size());
	}
}
