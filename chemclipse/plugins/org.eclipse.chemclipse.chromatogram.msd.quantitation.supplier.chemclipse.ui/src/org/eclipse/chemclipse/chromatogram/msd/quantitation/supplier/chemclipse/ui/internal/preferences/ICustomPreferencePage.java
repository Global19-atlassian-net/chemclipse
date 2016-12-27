/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.ui.IWorkbenchPreferencePage;

public interface ICustomPreferencePage extends IWorkbenchPreferencePage {

	/**
	 * The addField is protected only.
	 * 
	 * @param editor
	 */
	void addFieldEditor(FieldEditor editor);

	/**
	 * Add additional pages and field editors.
	 * 
	 */
	void createSettingPages();
}
