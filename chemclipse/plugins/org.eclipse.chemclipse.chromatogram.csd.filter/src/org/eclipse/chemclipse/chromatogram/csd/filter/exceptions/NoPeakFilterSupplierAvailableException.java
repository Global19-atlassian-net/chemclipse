/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.filter.exceptions;

public class NoPeakFilterSupplierAvailableException extends Exception {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -1389214655540242218L;

	public NoPeakFilterSupplierAvailableException() {
		super();
	}

	public NoPeakFilterSupplierAvailableException(String message) {
		super(message);
	}
}
