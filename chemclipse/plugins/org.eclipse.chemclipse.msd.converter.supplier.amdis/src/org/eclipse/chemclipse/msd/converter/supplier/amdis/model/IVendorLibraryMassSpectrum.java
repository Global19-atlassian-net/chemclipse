/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.model;

import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;

public interface IVendorLibraryMassSpectrum extends IRegularLibraryMassSpectrum {

	/**
	 * Returns the source.
	 * 
	 * @return String
	 */
	String getSource();

	/**
	 * Sets the source.
	 * 
	 * @param source
	 */
	void setSource(String source);
}
