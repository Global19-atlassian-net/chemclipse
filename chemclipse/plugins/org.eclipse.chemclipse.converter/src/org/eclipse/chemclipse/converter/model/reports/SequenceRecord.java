/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model.reports;

public class SequenceRecord implements ISequenceRecord {

	private String substance = "";
	private String method = "";
	private int vial = 0;
	private String sampleName = ""; // Sample Name/Keyword Text
	private double multiplier = 1;
	private String dataFile = "";
	private String description = "";

	@Override
	public String getSubstance() {

		return substance;
	}

	@Override
	public void setSubstance(String substance) {

		this.substance = substance;
	}

	@Override
	public String getMethod() {

		return method;
	}

	@Override
	public void setMethod(String method) {

		this.method = method;
	}

	@Override
	public int getVial() {

		return vial;
	}

	@Override
	public void setVial(int vial) {

		this.vial = vial;
	}

	@Override
	public String getSampleName() {

		return sampleName;
	}

	@Override
	public void setSampleName(String sampleName) {

		this.sampleName = sampleName;
	}

	@Override
	public double getMultiplier() {

		return multiplier;
	}

	@Override
	public void setMultiplier(double multiplier) {

		this.multiplier = multiplier;
	}

	@Override
	public String getDataFile() {

		return dataFile;
	}

	@Override
	public void setDataFile(String dataFile) {

		this.dataFile = dataFile;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataFile == null) ? 0 : dataFile.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		long temp;
		temp = Double.doubleToLongBits(multiplier);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		result = prime * result + ((sampleName == null) ? 0 : sampleName.hashCode());
		result = prime * result + ((substance == null) ? 0 : substance.hashCode());
		result = prime * result + vial;
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		SequenceRecord other = (SequenceRecord)obj;
		if(dataFile == null) {
			if(other.dataFile != null)
				return false;
		} else if(!dataFile.equals(other.dataFile))
			return false;
		if(method == null) {
			if(other.method != null)
				return false;
		} else if(!method.equals(other.method))
			return false;
		if(Double.doubleToLongBits(multiplier) != Double.doubleToLongBits(other.multiplier))
			return false;
		if(sampleName == null) {
			if(other.sampleName != null)
				return false;
		} else if(!sampleName.equals(other.sampleName))
			return false;
		if(substance == null) {
			if(other.substance != null)
				return false;
		} else if(!substance.equals(other.substance))
			return false;
		if(vial != other.vial)
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "SequenceRecord [substance=" + substance + ", method=" + method + ", vial=" + vial + ", sampleName=" + sampleName + ", multiplier=" + multiplier + ", dataFile=" + dataFile + "]";
	}
}
