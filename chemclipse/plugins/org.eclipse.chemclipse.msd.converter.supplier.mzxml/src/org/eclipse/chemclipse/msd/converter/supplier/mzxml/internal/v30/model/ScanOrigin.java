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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v30.model;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class ScanOrigin implements Serializable {

	private final static long serialVersionUID = 300L;
	@XmlAttribute(name = "parentFileID", required = true)
	protected String parentFileID;
	@XmlAttribute(name = "num", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	protected BigInteger num;

	public String getParentFileID() {

		return parentFileID;
	}

	public void setParentFileID(String value) {

		this.parentFileID = value;
	}

	public BigInteger getNum() {

		return num;
	}

	public void setNum(BigInteger value) {

		this.num = value;
	}
}
