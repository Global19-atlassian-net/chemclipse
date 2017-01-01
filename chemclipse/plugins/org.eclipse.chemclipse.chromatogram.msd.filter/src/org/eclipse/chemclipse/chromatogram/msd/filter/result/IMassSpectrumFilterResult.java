/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.result;

import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;

/**
 * @author eselmeister
 */
public interface IMassSpectrumFilterResult {

	/**
	 * Returns the result status of the applied filter.
	 * 
	 * @return {@link ResultStatus}
	 */
	ResultStatus getResultStatus();

	/**
	 * Returns a description of the applied filter or the failure that has been
	 * occurred.
	 * 
	 * @return String
	 */
	String getDescription();
}
