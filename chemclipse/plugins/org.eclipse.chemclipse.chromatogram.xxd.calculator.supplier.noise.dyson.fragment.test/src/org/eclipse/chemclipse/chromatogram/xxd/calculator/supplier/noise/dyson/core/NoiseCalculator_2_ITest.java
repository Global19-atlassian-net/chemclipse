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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.dyson.core;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.dyson.TestPathHelper;

public class NoiseCalculator_2_ITest extends ChromatogramReaderTestCase {

	private NoiseCalculator noiseCalculator;

	@Override
	protected void setUp() throws Exception {

		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_2);
		super.setUp();
		noiseCalculator = new NoiseCalculator();
		noiseCalculator.setChromatogram(chromatogram, 13);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testReader_1() {

		/*
		 * The loading time of the chromatogram takes a while.
		 * That's why several tests are made here.
		 */
		assertEquals(0.0f, noiseCalculator.getSignalToNoiseRatio(0));
		assertEquals(0.3802281369f, noiseCalculator.getSignalToNoiseRatio(50));
		assertEquals(1.0f, noiseCalculator.getSignalToNoiseRatio(131.5f));
		assertEquals(608.3650190114f, noiseCalculator.getSignalToNoiseRatio(80000));
	}
}
