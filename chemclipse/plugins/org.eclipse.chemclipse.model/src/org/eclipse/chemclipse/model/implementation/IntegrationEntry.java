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
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractIntegrationEntry;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;

public class IntegrationEntry extends AbstractIntegrationEntry implements IIntegrationEntry {

	private static final long serialVersionUID = -7897277307510543981L;

	public IntegrationEntry(double integratedArea) {
		super(integratedArea);
	}
}
