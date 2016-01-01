/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.converter.processing.chromatogram.IChromatogramCSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class ChromatogramImportWizard extends Wizard implements IImportWizard {

	private static final Logger logger = Logger.getLogger(ChromatogramImportWizard.class);
	private static final String DESCRIPTION = "Chromatogram CSD Import";
	private RawFileSelectionWizardPage rawFileSelectionWizardPage;
	private ImportDirectoryWizardPage importDirectoryWizardPage;

	public ChromatogramImportWizard() {
		setNeedsProgressMonitor(true);
		setWindowTitle("Chromatogram Import Wizard");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	@Override
	public void addPages() {

		rawFileSelectionWizardPage = new RawFileSelectionWizardPage(DESCRIPTION, "Select the chromatograms to import.", null);
		addPage(rawFileSelectionWizardPage);
		importDirectoryWizardPage = new ImportDirectoryWizardPage(DESCRIPTION, "Select the import folder", null);
		addPage(importDirectoryWizardPage);
	}

	@Override
	public boolean performFinish() {

		/*
		 * Chromatograms.
		 */
		final List<File> inputFiles = getInputFiles();
		if(inputFiles.size() == 0) {
			MessageDialog.openError(getShell(), "Error", "Please select at least one chromatogram.");
			return false;
		}
		/*
		 * Directory
		 */
		final String importDirectory = importDirectoryWizardPage.getSelectedDirectory();
		if(importDirectory == null || importDirectory.equals("")) {
			MessageDialog.openError(getShell(), "Error", "Please select an import directory.");
			return false;
		}
		/*
		 * Converter Id
		 */
		final String converterId = importDirectoryWizardPage.getSelectedConverterId();
		if(converterId == null || converterId.equals("")) {
			MessageDialog.openError(getShell(), "Error", "Please select a valid converter.");
			return false;
		}
		/*
		 * Import the chromatograms.
		 */
		IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				/*
				 * Process the chromatograms
				 */
				for(File inputFile : inputFiles) {
					/*
					 * Convert each chromatogram
					 */
					try {
						/*
						 * Import
						 */
						IChromatogramCSDImportConverterProcessingInfo processingInfo = ChromatogramConverterCSD.convert(inputFile, monitor);
						IChromatogramCSD chromatogram = processingInfo.getChromatogram();
						//
						String directory = importDirectory;
						if(!importDirectory.endsWith(File.separator)) {
							directory += File.separator;
						}
						/*
						 * Export
						 */
						File outputFile = new File(directory + chromatogram.getName());
						ChromatogramConverterCSD.convert(outputFile, chromatogram, converterId, monitor);
					} catch(TypeCastException e) {
						logger.warn(e);
					}
				}
			}
		};
		try {
			getContainer().run(true, false, runnableWithProgress);
		} catch(InvocationTargetException e) {
			MessageDialog.openError(getShell(), "Error", "Something has gone wrong with the chromatogram import.");
			return false;
		} catch(InterruptedException e) {
			MessageDialog.openError(getShell(), "Error", "Something has gone wrong with the chromatogram import.");
			return false;
		}
		MessageDialog.openInformation(getShell(), DESCRIPTION, "All chromatograms have been imported successfully.");
		return true;
	}

	private List<File> getInputFiles() {

		List<File> inputFiles = new ArrayList<File>();
		ISelection selection = rawFileSelectionWizardPage.getSelection();
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			for(Object object : structuredSelection.toArray()) {
				if(object instanceof File) {
					inputFiles.add((File)object);
				}
			}
		}
		return inputFiles;
	}
}
