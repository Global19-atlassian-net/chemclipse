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

import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractSettingsHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		PreferenceManager preferenceManager = new PreferenceManager();
		List<IPreferencePage> preferencePages = getPreferencePages();
		//
		int i = 1;
		for(IPreferencePage preferencePage : preferencePages) {
			preferenceManager.addToRoot(new PreferenceNode(Integer.toString(i++), preferencePage));
		}
		//
		PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
		preferenceDialog.create();
		preferenceDialog.setMessage("Settings");
		preferenceDialog.open();
	}

	abstract List<IPreferencePage> getPreferencePages();
}
