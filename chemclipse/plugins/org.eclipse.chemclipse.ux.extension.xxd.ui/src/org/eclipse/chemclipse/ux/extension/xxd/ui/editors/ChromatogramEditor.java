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
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataType;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ChromatogramEditor extends AbstractChromatogramEditor {

	private Composite parent;

	public ChromatogramEditor(DataType dataType, Composite parent, MPart part, MDirtyable dirtyable, Shell shell) {
		super(dataType, parent, part, dirtyable, shell);
		this.parent = parent;
	}

	public void setFocus() {

		parent.setFocus();
	}
}
