/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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

	public PeakMSD(IPeakModelMSD peakModel, String modelDescription) throws IllegalArgumentException {
		super(peakModel, modelDescription);
	}

	public PeakMSD(IPeakModelMSD peakModel) throws IllegalArgumentException {
		super(peakModel);
	}
}
