/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.connector.supplier.microsoft.office.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import org.eclipse.jface.viewers.ISelection;

public class WordFileDocWizard extends OfficeFileWizard implements INewWizard {

	private WordFileDocWizardPage page;
	private ISelection selection;

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {

		page = new WordFileDocWizardPage(selection);
		addPage(page);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		this.selection = selection;
	}
}