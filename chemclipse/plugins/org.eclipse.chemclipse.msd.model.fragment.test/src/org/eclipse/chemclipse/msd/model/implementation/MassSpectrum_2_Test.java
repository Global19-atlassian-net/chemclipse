/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
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

/**
 * Equals, hashCode
 * 
 * @author eselmeister
 */
public class MassSpectrum_2_Test extends TestCase {

	private ScanMSD massSpectrum1;
	private ScanMSD massSpectrum2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum1 = new ScanMSD();
		massSpectrum1.addIon(new Ion(45.4f, 7830.4f));
		massSpectrum2 = new ScanMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum1 = null;
		massSpectrum2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertEquals("equals", false, massSpectrum1.equals(massSpectrum2));
	}

	public void testEquals_2() {

		assertEquals("equals", false, massSpectrum2.equals(massSpectrum1));
	}

	public void testHashCode_1() {

		assertTrue("hashCode", massSpectrum1.hashCode() != massSpectrum2.hashCode());
	}

	public void testHashCode_2() {

		assertTrue("hashCode", massSpectrum2.hashCode() != massSpectrum1.hashCode());
	}
}
