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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SequenceListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SequenceListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SequenceListTableComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class SequenceListUI extends ExtendedTableViewer {

	private SequenceListTableComparator sequenceListTableComparator;
	private SequenceListFilter sequenceListFilter;

	public SequenceListUI(Composite parent, int style) {
		super(parent, style);
		sequenceListTableComparator = new SequenceListTableComparator();
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		sequenceListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(SequenceListLabelProvider.TITLES, SequenceListLabelProvider.BOUNDS);
		setLabelProvider(new SequenceListLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(sequenceListTableComparator);
		sequenceListFilter = new SequenceListFilter();
		setFilters(new ViewerFilter[]{sequenceListFilter});
	}
}
