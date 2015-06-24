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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public interface IChromatogramIntegrator {

	/**
	 * Integrates the entire chromatogram selection.
	 * 
	 * @param chromatogram
	 * @return double
	 */
	double integrate(IChromatogramSelection chromatogramSelection);
}
