/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

public class PartHandler extends AbstractPartHandler {

	private static final String DELIMITER = "_";
	//
	private String name = "";
	private String partId = "";
	private String stackPositionKey = "";

	public PartHandler(String id) {

		String[] values = id.split(DELIMITER);
		this.partId = values[0];
		this.stackPositionKey = values[1];
	}

	public PartHandler(String name, String partId, String stackPositionKey) {

		this.name = name;
		this.partId = partId;
		this.stackPositionKey = stackPositionKey;
	}

	public String getId() {

		return getPartId() + DELIMITER + getStackPositionKey();
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String getPartId() {

		return partId;
	}

	@Override
	public String getStackPositionKey() {

		return stackPositionKey;
	}
}
