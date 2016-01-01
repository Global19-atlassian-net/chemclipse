/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.result;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;

public interface IDenoisingFilterResult extends IChromatogramFilterResult {

	/**
	 * Returns the calculated combined noise mass spectra.
	 * 
	 * @return
	 */
	List<ICombinedMassSpectrum> getNoiseMassSpectra();
}
