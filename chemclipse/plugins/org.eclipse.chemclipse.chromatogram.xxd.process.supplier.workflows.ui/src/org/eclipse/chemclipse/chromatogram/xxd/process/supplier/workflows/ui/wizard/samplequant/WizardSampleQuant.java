/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.samplequant;

import java.io.File;
import java.util.Date;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.core.SampleQuantProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.ui.wizards.AbstractFileWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizardPage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings.DataType;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;

public class WizardSampleQuant extends AbstractFileWizard {

	public static final int PREFERRED_WIDTH = 350;
	public static final int PREFERRED_HEIGHT = 500;
	//
	private ISampleQuantWizardElements wizardElements = new SampleQuantWizardElements();
	//
	private InputEntriesWizardPage pageInputEntries;
	private PageReportDataSelection pageReportDataSelection;
	private PageDataVerification pageDataVerification;

	public WizardSampleQuant() {
		super("SampleQuantReport_" + new Date().getTime(), SampleQuantProcessor.REPORT_FILE_EXTENSION);
	}

	@Override
	public void addPages() {

		super.addPages();
		/*
		 * Pages must implement IExtendedWizardPage / extend AbstractExtendedWizardPage
		 */
		InputWizardSettings inputWizardSettings = new InputWizardSettings(DataType.MSD_CHROMATOGRAM);
		inputWizardSettings.setTitle("Open Chromatogram (MSD) File(s)");
		inputWizardSettings.setDescription("Select a chromatogram/chromatograms file to open.");
		inputWizardSettings.setPathPreferences(PreferenceSupplier.INSTANCE().getPreferences(), PreferenceSupplier.P_SAMPLEQUANT_FILTER_PATH_CHROMATOGRAM);
		//
		pageInputEntries = new InputEntriesWizardPage(inputWizardSettings);
		pageReportDataSelection = new PageReportDataSelection(wizardElements);
		pageDataVerification = new PageDataVerification(wizardElements);
		//
		addPage(pageInputEntries);
		addPage(pageReportDataSelection);
		addPage(pageDataVerification);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {

		IWizardPage nextPage = super.getNextPage(page);
		//
		if(page == pageInputEntries) {
			wizardElements.clearSelectedChromatograms();
			wizardElements.addElements(pageInputEntries.getChromatogramWizardElements());
			pageInputEntries.saveSelectedPath();
		}
		//
		return nextPage;
	}

	@Override
	public boolean canFinish() {

		boolean canFinish = (wizardElements.getSelectedChromatograms().size() > 0) ? true : false;
		if(canFinish) {
			canFinish = wizardElements.isDataVerified();
		}
		return canFinish;
	}

	@Override
	public void doFinish(IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Create Sample Quantitation", IProgressMonitor.UNKNOWN);
		final IFile file = super.prepareProject(monitor);
		//
		ISampleQuantReport sampleQuantReport = wizardElements.getSampleQuantReport();
		String chromatogram = wizardElements.getSelectedChromatograms().get(0);
		File sampleQuantReportFile = file.getLocation().toFile();
		SampleQuantProcessor sampleQuantProcessor = new SampleQuantProcessor();
		sampleQuantProcessor.createSampleQuantReport(sampleQuantReportFile, sampleQuantReport, chromatogram, monitor);
		/*
		 * Refresh
		 */
		super.refreshWorkspace(monitor);
		super.runOpenEditor(file, monitor);
	}
}
