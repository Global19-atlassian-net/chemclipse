/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate GmbH.
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScanType", propOrder = {"scanWindowList"})
public class ScanType extends ParamGroupType {

	private ScanWindowListType scanWindowList;
	@XmlAttribute(name = "spectrumRef")
	private String spectrumRef;
	@XmlAttribute(name = "sourceFileRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object sourceFileRef;
	@XmlAttribute(name = "externalSpectrumID")
	private String externalSpectrumID;
	@XmlAttribute(name = "instrumentConfigurationRef")
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Object instrumentConfigurationRef;

	public ScanWindowListType getScanWindowList() {

		return scanWindowList;
	}

	public void setScanWindowList(ScanWindowListType value) {

		this.scanWindowList = value;
	}

	public String getSpectrumRef() {

		return spectrumRef;
	}

	public void setSpectrumRef(String value) {

		this.spectrumRef = value;
	}

	public Object getSourceFileRef() {

		return sourceFileRef;
	}

	public void setSourceFileRef(Object value) {

		this.sourceFileRef = value;
	}

	public String getExternalSpectrumID() {

		return externalSpectrumID;
	}

	public void setExternalSpectrumID(String value) {

		this.externalSpectrumID = value;
	}

	public Object getInstrumentConfigurationRef() {

		return instrumentConfigurationRef;
	}

	public void setInstrumentConfigurationRef(Object value) {

		this.instrumentConfigurationRef = value;
	}
}
