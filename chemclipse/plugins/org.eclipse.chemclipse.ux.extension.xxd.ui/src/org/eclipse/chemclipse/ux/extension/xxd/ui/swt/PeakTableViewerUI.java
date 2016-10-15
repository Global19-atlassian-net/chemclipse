/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakLabelProvider;
import org.eclipse.swt.widgets.Composite;

public class PeakTableViewerUI extends ExtendedTableViewer {

	private String[] titles = {"Retention Time (Minutes)", "Retention Index", "S/N", "Peak Area"};
	private int[] bounds = {150, 150, 150, 150};

	public PeakTableViewerUI(Composite parent) {
		super(parent);
		initialize();
	}

	public PeakTableViewerUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {

		createColumns(titles, bounds);
		setLabelProvider(new PeakLabelProvider());
		setContentProvider(new ListContentProvider());
	}
}
