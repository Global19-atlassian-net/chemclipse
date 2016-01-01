/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import junit.framework.TestCase;

import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;

public class RegularMassSpectrum_3_Test extends TestCase {

	private IRegularMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new RegularMassSpectrum();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		super.tearDown();
	}

	public void test_1() {

		massSpectrum.setMassSpectrometer((short)2);
		assertEquals(2, massSpectrum.getMassSpectrometer());
	}

	public void test_2() {

		massSpectrum.setMassSpectrumType((short)2);
		assertEquals(2, massSpectrum.getMassSpectrumType());
	}

	public void test_3() {

		massSpectrum.setMassSpectrumType((short)2);
		assertEquals("Unknown", massSpectrum.getMassSpectrumTypeDescription());
	}

	public void test_4() {

		massSpectrum.setTimeSegmentId(3);
		assertEquals(3, massSpectrum.getTimeSegmentId());
	}

	public void test_5() {

		massSpectrum.setCycleNumber(3);
		assertEquals(3, massSpectrum.getCycleNumber());
	}
}
