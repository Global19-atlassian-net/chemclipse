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

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public interface IPartHandler {

	boolean isPartStackAssigned();

	boolean isPartVisible();

	void action(boolean show, EPartService partService, EModelService modelService, MApplication application);

	default String getId() {

		return "";
	}

	default String getName() {

		return "";
	}

	default String getIconURI() {

		return "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/tag.gif";
	}

	String getPartId();

	String getStackPositionKey();
}
