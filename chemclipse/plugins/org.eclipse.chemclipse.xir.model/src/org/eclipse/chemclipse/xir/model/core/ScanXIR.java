/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.core;

import java.util.TreeSet;

public class ScanXIR extends TreeSet<ISignalXIR> implements IScanXIR {

	private static final long serialVersionUID = 3955396880394067950L;
	//
	private double rotationAngle = 0.0d;
	private double[] rawSignals = new double[0];
	private double[] backgroundSignals = new double[0];

	@Override
	public double getRotationAngle() {

		return rotationAngle;
	}

	@Override
	public void setRotationAngle(double rotationAngle) {

		this.rotationAngle = rotationAngle;
	}

	@Override
	public double[] getRawSignals() {

		return rawSignals;
	}

	@Override
	public void setRawSignals(double[] rawSignals) {

		this.rawSignals = rawSignals;
	}

	@Override
	public double[] getBackgroundSignals() {

		return backgroundSignals;
	}

	@Override
	public void setBackgroundSignals(double[] backgroundSignals) {

		this.backgroundSignals = backgroundSignals;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(rotationAngle);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		ScanXIR other = (ScanXIR)obj;
		if(Double.doubleToLongBits(rotationAngle) != Double.doubleToLongBits(other.rotationAngle))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "ScanXIR [rotationAngle=" + rotationAngle + "]";
	}
}
