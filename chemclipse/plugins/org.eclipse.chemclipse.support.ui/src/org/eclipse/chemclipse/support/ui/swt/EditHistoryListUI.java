/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import org.eclipse.chemclipse.support.ui.internal.provider.EditHistoryContentProvider;
import org.eclipse.chemclipse.support.ui.internal.provider.EditHistoryLabelProvider;
import org.eclipse.chemclipse.support.ui.internal.provider.EditHistoryComparator;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;

public class EditHistoryListUI extends ExtendedTableViewer {

	private LabelProvider labelProvider = new EditHistoryLabelProvider();
	private IContentProvider contentProvider = new EditHistoryContentProvider();
	private ViewerComparator comparator = new EditHistoryComparator();

	public EditHistoryListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		createColumns(EditHistoryLabelProvider.TITLES, EditHistoryLabelProvider.BOUNDS);
		//
		setLabelProvider(labelProvider);
		setContentProvider(contentProvider);
		setComparator(comparator);
	}
}
