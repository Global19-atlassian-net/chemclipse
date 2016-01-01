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
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetectorSupplier;

import junit.framework.TestCase;

public class BaselineDetectorSupplier_1_Test extends TestCase {

	private BaselineDetectorSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier = new BaselineDetectorSupplier();
	}

	@Override
	protected void tearDown() throws Exception {

		supplier = null;
		super.tearDown();
	}

	public void testGetId_1() {

		assertEquals("id", "", supplier.getId());
	}

	public void testGetDescription_1() {

		assertEquals("description", "", supplier.getDescription());
	}

	public void testGetDetectorName_1() {

		assertEquals("detectorName", "", supplier.getDetectorName());
	}
}
