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
package org.eclipse.chemclipse.msd.swt.ui.converter;

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;

/**
 * Tests SeriesConverter.convertPeak(peak, includeBackground, sign)
 * 
 * @author eselmeister
 */
public class SeriesConverter_24_Test extends PeakTestCase {

	private ISeries series;
	private IChromatogramPeakMSD peak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		this.peak = getPeak();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		peak = null;
		series = null;
	}

	public void testPeak_1() {

		assertNotNull(peak);
	}

	public void testConvertPeak_1() {

		series = SeriesConverter.convertPeak(peak, true, Sign.POSITIVE);
		assertEquals("XMin", 1500.0d, series.getXMin());
	}

	public void testConvertPeak_2() {

		series = SeriesConverter.convertPeak(peak, true, Sign.POSITIVE);
		assertEquals("XMax", 15500.0d, series.getXMax());
	}

	public void testConvertPeak_3() {

		series = SeriesConverter.convertPeak(peak, true, Sign.POSITIVE);
		assertEquals("YMin", 1760.0d, series.getYMin());
	}

	public void testConvertPeak_4() {

		/*
		 * 5231.0d +1760 background
		 */
		series = SeriesConverter.convertPeak(peak, true, Sign.POSITIVE);
		assertEquals("YMax", 6991.0d, series.getYMax());
	}
}
