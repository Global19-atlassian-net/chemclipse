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
package org.eclipse.chemclipse.pcr.model.core;

public class ChannelSpecification extends AbstractDataModel implements IChannelSpecification {

	public ChannelSpecification() {
		addProtectedKey(NAME);
	}

	@Override
	public String getName() {

		return getData(NAME, "");
	}
}
