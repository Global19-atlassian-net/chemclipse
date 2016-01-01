/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.exceptions.CodaCalculatorException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

import junit.framework.TestCase;

public class MassChromatographicQualityResult_1_ITest extends TestCase {

	private IMassChromatographicQualityResult result;
	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD chromatogramSelection;
	private File importFile;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1));
		IChromatogramMSDImportConverterProcessingInfo processingInfo = ChromatogramConverterMSD.convert(importFile, new NullProgressMonitor());
		chromatogram = processingInfo.getChromatogram();
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		importFile = null;
		chromatogram = null;
		chromatogramSelection = null;
		//
		System.gc();
		//
		super.tearDown();
	}

	public void testConstructor_1() {

		try {
			result = new MassChromatographicQualityResult(chromatogramSelection, 0.7f, WindowSize.SCANS_3);
			float drv = result.getDataReductionValue();
			assertEquals("Data reduction value", 0.8737201f, drv);
			IMarkedIons exludedIons = result.getExcludedIons();
			assertNotNull(exludedIons);
		} catch(CodaCalculatorException e) {
			assertTrue("CodaCalculatorException", false);
		}
	}

	public void testConstructor_2() {

		try {
			result = new MassChromatographicQualityResult(chromatogramSelection, 0.7f, WindowSize.SCANS_5);
			float drv = result.getDataReductionValue();
			assertEquals("Data reduction value", 0.78327644f, drv);
			IMarkedIons exludedIons = result.getExcludedIons();
			assertNotNull(exludedIons);
		} catch(CodaCalculatorException e) {
			assertTrue("CodaCalculatorException", false);
		}
	}

	public void testConstructor_3() {

		try {
			result = new MassChromatographicQualityResult(chromatogramSelection, 0.7f, WindowSize.SCANS_7);
			float drv = result.getDataReductionValue();
			assertEquals("Data reduction value", 0.721843f, drv);
			IMarkedIons exludedIons = result.getExcludedIons();
			assertNotNull(exludedIons);
		} catch(CodaCalculatorException e) {
			assertTrue("CodaCalculatorException", false);
		}
	}
}
