/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges;

public class PeakDeconv implements IPeakDeconv {

	private int peak;

	public PeakDeconv(int value) {
		peak = value;
	}

	public int getPeak() {

		return peak;
	}

	public void setPeak(int value) {

		this.peak = value;
	}
}
