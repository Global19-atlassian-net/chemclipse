/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.incos.comparator;

import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;

public class INCOSMassSpectrumComparator_2_Test extends MassSpectrumSetTestCase {

	private INCOSMassSpectrumComparator comparator;
	private IMassSpectrumComparatorProcessingInfo processingInfo;
	private IMassSpectrumComparisonResult result;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
		IScanMSD unknown = sinapylAclohol.getMassSpectrum();
		IScanMSD reference = benzenepropanoicAcid.getMassSpectrum();
		//
		comparator = new INCOSMassSpectrumComparator();
		processingInfo = comparator.compare(unknown, reference);
		result = processingInfo.getMassSpectrumComparisonResult();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertFalse(processingInfo.hasErrorMessages());
	}

	public void test2() {

		assertEquals(69.00421f, result.getMatchFactor());
	}

	public void test3() {

		assertEquals(68.81291f, result.getReverseMatchFactor());
	}
}
