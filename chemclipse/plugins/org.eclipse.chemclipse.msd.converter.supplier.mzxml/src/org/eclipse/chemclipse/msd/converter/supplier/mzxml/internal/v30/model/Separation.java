/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"separationTechnique"})
public class Separation implements Serializable {

	private final static long serialVersionUID = 300L;
	@XmlElement(required = true)
	private List<SeparationTechnique> separationTechnique;

	public List<SeparationTechnique> getSeparationTechnique() {

		if(separationTechnique == null) {
			separationTechnique = new ArrayList<SeparationTechnique>();
		}
		return this.separationTechnique;
	}
}
