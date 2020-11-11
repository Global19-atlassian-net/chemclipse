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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;

public class GroupHandlerPeaks extends AbstractPartHandler {

	private boolean active = false;
	// TODO - Initially activate parts
	// TODO - change button icon and tooltip, dependent on active

	@Execute
	public void execute(MDirectToolItem directToolItem) {

		System.out.println("DTI: " + directToolItem);
		boolean show = !active;
		//
		// TODO
		action(TargetsPartHandler.PART_ID, TargetsPartHandler.STACK_POSITION_KEY, show);
		action(ScanChartPartHandler.PART_ID, ScanChartPartHandler.STACK_POSITION_KEY, show);
		action(ScanTablePartHandler.PART_ID, ScanTablePartHandler.STACK_POSITION_KEY, show);
		action(MoleculePartHandler.PART_ID, MoleculePartHandler.STACK_POSITION_KEY, show);
		//
		active = show;
	}
}
