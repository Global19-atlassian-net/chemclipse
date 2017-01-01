/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.AbstractPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;

public class PeakMSD extends AbstractPeakMSD implements IPeakMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 8841007450906820774L;

	public PeakMSD(IPeakModelMSD peakModel, String modelDescription) throws IllegalArgumentException {
		super(peakModel, modelDescription);
	}

	public PeakMSD(IPeakModelMSD peakModel) throws IllegalArgumentException {
		super(peakModel);
	}
}
