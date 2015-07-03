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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v31.model;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"robotManufacturer", "robotModel"})
public class Robot implements Serializable {

	private final static long serialVersionUID = 310L;
	@XmlElement(required = true)
	protected OntologyEntry robotManufacturer;
	@XmlElement(required = true)
	protected OntologyEntry robotModel;
	@XmlAttribute(name = "timePerSpot", required = true)
	protected Duration timePerSpot;
	@XmlAttribute(name = "deadVolume")
	@XmlSchemaType(name = "nonNegativeInteger")
	protected BigInteger deadVolume;

	public OntologyEntry getRobotManufacturer() {

		return robotManufacturer;
	}

	public void setRobotManufacturer(OntologyEntry value) {

		this.robotManufacturer = value;
	}

	public OntologyEntry getRobotModel() {

		return robotModel;
	}

	public void setRobotModel(OntologyEntry value) {

		this.robotModel = value;
	}

	public Duration getTimePerSpot() {

		return timePerSpot;
	}

	public void setTimePerSpot(Duration value) {

		this.timePerSpot = value;
	}

	public BigInteger getDeadVolume() {

		return deadVolume;
	}

	public void setDeadVolume(BigInteger value) {

		this.deadVolume = value;
	}
}
