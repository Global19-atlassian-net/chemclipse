/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.baseline.detector.supplier.tic.core;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.baseline.detector.supplier.tic.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.processing.IBaselineDetectorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.tic.settings.ITicBaselineDetectorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.tic.settings.TicBaselineDetectorSettings;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class TICBaselineDetector_1_ITest extends TestCase {

	private final static String CHROMATOGRAM_CONVERTER_ID = "net.openchrom.msd.converter.supplier.agilent";
	private final static String DETECTOR_ID = "org.eclipse.chemclipse.chromatogram.msd.baseline.detector.supplier.tic";
	private IChromatogramSelectionMSD chromatogramSelection;
	private ITicBaselineDetectorSettings settings;
	private IChromatogramMSD chromatogram;
	private String pathImport;
	private File fileImport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1);
		fileImport = new File(this.pathImport);
		IChromatogramMSDImportConverterProcessingInfo processingInfo = ChromatogramConverterMSD.convert(fileImport, CHROMATOGRAM_CONVERTER_ID, new NullProgressMonitor());
		chromatogram = processingInfo.getChromatogram();
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
		settings = new TicBaselineDetectorSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		pathImport = null;
		fileImport = null;
		chromatogram = null;
		chromatogramSelection = null;
		settings = null;
		//
		System.gc();
		//
		super.tearDown();
	}

	public void testBaseline_1() {

		assertEquals("numberOfScans", 5726, chromatogram.getNumberOfScans());
		IBaselineDetectorProcessingInfo processingInfo = BaselineDetector.setBaseline(chromatogramSelection, settings, DETECTOR_ID, new NullProgressMonitor());
		assertFalse(processingInfo.hasErrorMessages());
	}
}
