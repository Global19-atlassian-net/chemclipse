/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.jcampdx.model;

import org.eclipse.chemclipse.csd.model.core.AbstractChromatogramCSD;

public class VendorChromatogram extends AbstractChromatogramCSD implements IVendorChromatogram {

	public VendorChromatogram() {
		super();
	}

	@Override
	public String getName() {

		return extractNameFromFile("Chromatogram");
	}
}
