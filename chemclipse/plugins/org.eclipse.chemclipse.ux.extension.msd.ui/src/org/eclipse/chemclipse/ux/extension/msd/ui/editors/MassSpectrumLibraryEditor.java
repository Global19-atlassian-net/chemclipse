/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.msd.converter.exceptions.NoMassSpectrumConverterAvailableException;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumLibraryUI;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectraFileSupport;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.support.MassSpectrumImportRunnable;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class MassSpectrumLibraryEditor implements IChemClipseEditor {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massSpectrumLibraryEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.msd.ui/org.eclipse.chemclipse.ux.extension.msd.ui.editors.MassSpectrumLibraryEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/massSpectrum.gif";
	public static final String TOOLTIP = "Mass Spectrum Library - Detector Type: MSD";
	//
	private static final Logger logger = Logger.getLogger(MassSpectrumLibraryEditor.class);
	/*
	 * Injected member in constructor
	 */
	@Inject
	private MPart part;
	@Inject
	private MDirtyable dirtyable;
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private IEventBroker eventBroker;
	/*
	 * Mass spectrum selection and the GUI element.
	 */
	private MassSpectrumLibraryUI massSpectrumLibraryUI;
	private File massSpectrumFile;
	private IMassSpectra massSpectra;
	/*
	 * Showing additional info in tabs.
	 */
	private TabFolder tabFolder;

	@PostConstruct
	private void createControl(Composite parent) {

		loadMassSpectra();
		createPages(parent);
	}

	@Focus
	public void setFocus() {

		eventBroker.post(IChemClipseEvents.TOPIC_LIBRARY_MSD_UPDATE_SELECTION, massSpectra);
	}

	@PreDestroy
	private void preDestroy() {

		/*
		 * Remove the editor from the listed parts.
		 */
		eventBroker.post(IChemClipseEvents.TOPIC_LIBRARY_MSD_UNLOAD_SELECTION, null);
		//
		if(modelService != null) {
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			partStack.getChildren().remove(part);
		}
		/*
		 * Run the garbage collector.
		 */
		System.gc();
	}

	@Persist
	public void save() {

		Shell shell = Display.getDefault().getActiveShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask("Save Mass Spectra", IProgressMonitor.UNKNOWN);
					try {
						saveMassSpectra(monitor, shell);
					} catch(NoMassSpectrumConverterAvailableException e) {
						throw new InvocationTargetException(e);
					}
				} finally {
					monitor.done();
				}
			}
		};
		/*
		 * Run the export
		 */
		try {
			/*
			 * True to show the moving progress bar. False, a chromatogram
			 * should be imported as a whole.
			 */
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			saveAs();
		} catch(InterruptedException e) {
			logger.warn(e);
		}
	}

	private void saveMassSpectra(IProgressMonitor monitor, Shell shell) throws NoMassSpectrumConverterAvailableException {

		/*
		 * Try to save the chromatogram automatically if it is an *.chrom
		 * type.<br/> If not, show the file save dialog.
		 */
		if(massSpectrumFile != null && massSpectra != null && shell != null) {
			/*
			 * Convert the mass spectra.
			 */
			String converterId = massSpectra.getConverterId();
			if(converterId != null && !converterId.equals("")) {
				/*
				 * Try to save the chromatogram.
				 */
				monitor.subTask("Save Mass Spectra");
				IMassSpectrumExportConverterProcessingInfo processingInfo = MassSpectrumConverter.convert(massSpectrumFile, massSpectra, false, converterId, monitor);
				try {
					/*
					 * If no failures have occurred, set the dirty status to
					 * false.
					 */
					processingInfo.getFile();
					dirtyable.setDirty(false);
				} catch(TypeCastException e) {
					logger.warn(e);
				}
			} else {
				throw new NoMassSpectrumConverterAvailableException();
			}
		}
	}

	@Override
	public boolean saveAs() {

		boolean saveSuccessful = false;
		if(massSpectra != null) {
			try {
				saveSuccessful = MassSpectraFileSupport.saveMassSpectra(massSpectra);
				dirtyable.setDirty(!saveSuccessful);
			} catch(NoConverterAvailableException e) {
				logger.warn(e);
			}
		}
		return saveSuccessful;
	}

	private void loadMassSpectra() {

		try {
			/*
			 * Import the chromatogram without showing it on the gui. The GUI
			 * will take care itself of this action.
			 */
			Object object = part.getObject();
			if(object instanceof String) {
				File file = new File((String)object);
				importMassSpectra(file);
			}
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	private void importMassSpectra(File file) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException, ChromatogramIsNullException {

		/*
		 * Import the chromatogram here, but do not set to the chromatogram ui,
		 * as it must be initialized first.
		 */
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		MassSpectrumImportRunnable runnable = new MassSpectrumImportRunnable(file, massSpectra);
		try {
			/*
			 * True to show the moving progress bar. False, a chromatogram
			 * should be imported as a whole.
			 */
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		/*
		 * Add the mass spectra handling.
		 */
		dirtyable.setDirty(true);
		massSpectra = runnable.getMassSpectra();
		massSpectrumFile = file;
		massSpectra.addUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateMassSpectrumListUI();
			}
		});
	}

	private void createPages(Composite parent) {

		if(massSpectra != null && massSpectra.getMassSpectrum(1) != null) {
			part.setLabel(massSpectrumFile.getName());
			tabFolder = new TabFolder(parent, SWT.BOTTOM);
			createMassSpectrumPage();
		} else {
			createErrorMessagePage(parent);
		}
	}

	private void createMassSpectrumPage() {

		/*
		 * Create the mass spectrum UI.
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Mass Spectra");
		massSpectrumLibraryUI = new MassSpectrumLibraryUI(tabFolder, SWT.NONE);
		massSpectrumLibraryUI.update(massSpectra, true);
		tabItem.setControl(massSpectrumLibraryUI);
	}

	private void createErrorMessagePage(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		Label label = new Label(composite, SWT.NONE);
		label.setText("The mass spectrum couldn't be loaded.");
	}

	private void updateMassSpectrumListUI() {

		massSpectrumLibraryUI.update(massSpectra, true);
	}
}