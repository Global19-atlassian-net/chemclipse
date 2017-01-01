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
package org.eclipse.chemclipse.msd.model.core.quantitation;

public class ConcentrationResponseEntryMSD extends AbstractConcentrationResponseEntry implements IConcentrationResponseEntryMSD {

	/**
	 * 
	 */
	private static final long serialVersionUID = 602913566986867408L;

	public ConcentrationResponseEntryMSD(double ion, double concentration, double response) {
		super(ion, concentration, response);
	}
}
