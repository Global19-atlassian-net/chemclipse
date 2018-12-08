/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.selection;

import org.eclipse.chemclipse.nmr.model.core.IMeasurementNMR;
import org.eclipse.chemclipse.nmr.model.core.MeasurementNMR;

public class DataNMRSelection implements IDataNMRSelection {

	private IMeasurementNMR measurementNMR;

	public DataNMRSelection(IMeasurementNMR measurementNMR) {

		this.measurementNMR = measurementNMR;
	}

	public DataNMRSelection() {

		this.measurementNMR = new MeasurementNMR();
	}

	@Override
	public IMeasurementNMR getMeasurmentNMR() {

		return measurementNMR;
	}
}
