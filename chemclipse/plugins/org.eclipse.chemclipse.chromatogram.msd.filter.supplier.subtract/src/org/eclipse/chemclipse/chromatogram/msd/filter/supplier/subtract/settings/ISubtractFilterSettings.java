/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public interface ISubtractFilterSettings {

	/**
	 * Returns the mass spectrum that shall be subtracted.
	 * May return null.
	 * 
	 * @return {@link IScanMSD}
	 */
	IScanMSD getSubtractMassSpectrum();

	/**
	 * Sets the mass spectrum that shall be subtracted.
	 * 
	 * @param massSpectrum
	 */
	void setSubtractMassSpectrum(IScanMSD massSpectrum);

	boolean isUseNominalMasses();

	void setUseNominalMasses(boolean useNominalMasses);

	boolean isNormalize();

	void setUseNormalize(boolean useNormalize);
}
