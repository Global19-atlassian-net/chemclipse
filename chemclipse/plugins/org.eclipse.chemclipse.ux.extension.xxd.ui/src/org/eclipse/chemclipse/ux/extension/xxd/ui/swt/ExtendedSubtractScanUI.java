/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.support.FilterSupport;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class ExtendedSubtractScanUI {

	private static final Logger logger = Logger.getLogger(ExtendedScanChartUI.class);
	//
	private TabFolder tabFolder;
	private static final int INDEX_CHART = 0;
	private static final int INDEX_TABLE = 1;
	//
	private ScanChartUI scanChartUI;
	private ExtendedScanTableUI extendedScanTableUI;
	//
	private IScanMSD scanMSD;
	private IChromatogramSelectionMSD chromatogramSelectionMSD;
	private Shell shell = Display.getDefault().getActiveShell();

	@Inject
	public ExtendedSubtractScanUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateScan();
	}

	public void update(Object object) {

		if(object instanceof IChromatogramSelectionMSD) {
			chromatogramSelectionMSD = (IChromatogramSelectionMSD)object;
		} else if(object instanceof IScanMSD) {
			scanMSD = (IScanMSD)object;
		}
		updateScan();
	}

	private void updateScan() {

		updateScanData();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		createScanTabFolderSection(parent);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createAddSelectedScanButton(composite);
		createAddCombinedScanButton(composite);
		createClearSessionButton(composite);
		createLoadSessionButton(composite);
		createStoreSessionButton(composite);
		createSaveButton(composite);
		createSettingsButton(composite);
	}

	private void createScanTabFolderSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));
		//
		tabFolder = new TabFolder(composite, SWT.BOTTOM);
		tabFolder.setBackground(Colors.WHITE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateScanData();
			}
		});
		//
		createScanChart(tabFolder);
		createScanTable(tabFolder);
	}

	private void createScanChart(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Chart");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);
		//
		scanChartUI = new ScanChartUI(composite, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createScanTable(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Table");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);
		//
		extendedScanTableUI = new ExtendedScanTableUI(composite);
		extendedScanTableUI.enableEditModus(true);
		extendedScanTableUI.setFireUpdate(false);
	}

	private void createAddSelectedScanButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add selected scan to subtract spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_SELECTED_SCAN, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogramSelectionMSD != null && chromatogramSelectionMSD.getSelectedScan() != null) {
					/*
					 * Add the selected scan to the session MS.
					 */
					IScanMSD massSpectrum1 = PreferenceSupplier.getSessionSubtractMassSpectrum();
					IVendorMassSpectrum massSpectrum2 = chromatogramSelectionMSD.getSelectedScan();
					boolean useNormalize = org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan();
					IScanMSD normalizedMassSpectrum = FilterSupport.getCombinedMassSpectrum(massSpectrum1, massSpectrum2, null, useNormalize);
					PreferenceSupplier.setSessionSubtractMassSpectrum(normalizedMassSpectrum);
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
				}
			}
		});
	}

	private void createAddCombinedScanButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add combined scan to subtract spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_COMBINED_SCAN, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogramSelectionMSD != null) {
					boolean useNormalize = org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan();
					IScanMSD massSpectrum1 = PreferenceSupplier.getSessionSubtractMassSpectrum();
					IScanMSD massSpectrum2 = FilterSupport.getCombinedMassSpectrum(chromatogramSelectionMSD, null, useNormalize);
					IScanMSD normalizedMassSpectrum = FilterSupport.getCombinedMassSpectrum(massSpectrum1, massSpectrum2, null, useNormalize);
					PreferenceSupplier.setSessionSubtractMassSpectrum(normalizedMassSpectrum);
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
				}
			}
		});
	}

	private void createClearSessionButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Clear the session spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_CLEAR_SESSION_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText("Clear Session");
				messageBox.setMessage("Would you like to clear the session subtract scan?");
				if(messageBox.open() == SWT.YES) {
					PreferenceSupplier.setSessionSubtractMassSpectrum(null);
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
				}
			}
		});
	}

	private void createLoadSessionButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Load the session spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_LOAD_SESSION_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText("Load Session");
				messageBox.setMessage("Would you like to load the session subtract scan?");
				if(messageBox.open() == SWT.YES) {
					PreferenceSupplier.loadSessionSubtractMassSpectrum();
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
				}
			}
		});
	}

	private void createStoreSessionButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Store the session spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_STORE_SESSION_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.storeSessionSubtractMassSpectrum();
				MessageDialog.openInformation(shell, "Session", "The session subtract scan has been stored successfully.");
			}
		});
	}

	private Button createSaveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save the subtract scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(scanMSD != null) {
						DatabaseFileSupport.saveMassSpectrum(shell, scanMSD, "SubtractMS");
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		return button;
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageScans = new PreferencePageScans();
				preferencePageScans.setTitle("Scan Settings");
				IPreferencePage preferencePageSubtract = new PreferencePageSubtract();
				preferencePageSubtract.setTitle("Subtract Settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageScans));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageSubtract));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		updateScan();
	}

	private void updateScanData() {

		switch(tabFolder.getSelectionIndex()) {
			case INDEX_CHART:
				scanChartUI.setInput(scanMSD);
				break;
			case INDEX_TABLE:
				extendedScanTableUI.update(scanMSD);
				break;
		}
	}
}
