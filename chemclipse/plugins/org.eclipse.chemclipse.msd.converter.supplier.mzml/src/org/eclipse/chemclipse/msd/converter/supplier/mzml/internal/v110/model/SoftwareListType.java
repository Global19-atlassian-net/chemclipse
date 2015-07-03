/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoftwareListType", propOrder = {"software"})
public class SoftwareListType {

	@XmlElement(required = true)
	private List<SoftwareType> software;
	@XmlAttribute(name = "count", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	private BigInteger count;

	public List<SoftwareType> getSoftware() {

		if(software == null) {
			software = new ArrayList<SoftwareType>();
		}
		return this.software;
	}

	public BigInteger getCount() {

		return count;
	}

	public void setCount(BigInteger value) {

		this.count = value;
	}
}
