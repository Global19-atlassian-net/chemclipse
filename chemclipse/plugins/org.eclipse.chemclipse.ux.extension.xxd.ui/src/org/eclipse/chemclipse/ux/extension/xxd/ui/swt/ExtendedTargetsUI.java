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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.core.identifier.chromatogram.IChromatogramTargetCSD;
import org.eclipse.chemclipse.csd.model.core.identifier.scan.IScanTargetCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramTargetCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanTargetCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.ITarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.chromatogram.IChromatogramTargetMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IScanTargetMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.MassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramTargetMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.IColumnMoveListener;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.util.TargetListUtil;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ListSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageLists;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTargets;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.identifier.chromatogram.IChromatogramTargetWSD;
import org.eclipse.chemclipse.wsd.model.core.identifier.scan.IScanTargetWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ChromatogramTargetWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanTargetWSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ExtendedTargetsUI {

	private static final String IDENTIFIER_MANUAL = "Manual";
	private static final String MENU_CATEGORY_TARGETS = "Targets";
	//
	private Label labelTargetOption;
	private Label labelInfo;
	private Composite toolbarInfo;
	private Composite toolbarSearch;
	private Composite toolbarModify;
	private Combo comboTargetName;
	private Button buttonAddTarget;
	private Button buttonDeleteTarget;
	private TargetsListUI targetListUI;
	private TargetListUtil targetListUtil;
	/*
	 * IScan,
	 * IPeak,
	 * IChromatogram
	 */
	private Object object;
	private Map<String, Object> map;
	//
	private boolean showChromatogramTargets = false;
	//
	private PeakDataSupport peakDataSupport = new PeakDataSupport();
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	private ScanDataSupport scanDataSupport = new ScanDataSupport();
	private ListSupport listSupport = new ListSupport();
	private Display display = Display.getDefault();
	private Shell shell = display.getActiveShell();

	@Inject
	public ExtendedTargetsUI(Composite parent) {
		targetListUtil = new TargetListUtil();
		map = new HashMap<String, Object>();
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateTargets();
	}

	public void updateChromatogram(Object object) {

		if(showChromatogramTargets) {
			this.object = object;
			updateTargets();
		}
	}

	public void updateScanOrPeak(Object object) {

		if(!showChromatogramTargets) {
			this.object = object;
			updateTargets();
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarSearch = createToolbarSearch(parent);
		toolbarModify = createToolbarModify(parent);
		targetListUI = createTargetTable(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarSearch, false);
		PartSupport.setCompositeVisibility(toolbarModify, false);
		//
		targetListUI.setEditEnabled(false);
		applySettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createTargetOptionLabel(composite);
		createButtonToggleToolbarInfo(composite);
		createButtonToggleOptionTargets(composite);
		createButtonToggleToolbarSearch(composite);
		createButtonToggleToolbarModify(composite);
		createButtonToggleEditModus(composite);
		createSettingsButton(composite);
		//
		updateTargetOptionLabel();
	}

	private void createTargetOptionLabel(Composite parent) {

		labelTargetOption = new Label(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		labelTargetOption.setLayoutData(gridData);
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleOptionTargets(Composite parent) {

		Image imageChromatogram = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16);
		Image imageScan = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MASS_SPECTRUM, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle whether to display chromatogram or scan/peak targets.");
		button.setText("");
		button.setImage(showChromatogramTargets ? imageChromatogram : imageScan);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				showChromatogramTargets = !showChromatogramTargets;
				button.setImage(showChromatogramTargets ? imageChromatogram : imageScan);
				updateTargetOptionLabel();
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarSearch(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle search toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarSearch);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarModify(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle modify toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarModify);
				if(visible) {
					setComboTargetNameItems();
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleEditModus(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Enable/disable to edit the table.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean editEnabled = !targetListUI.isEditEnabled();
				targetListUI.setEditEnabled(editEnabled);
				updateLabel();
			}
		});
		//
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

				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				IPreferencePage preferencePageTargets = new PreferencePageTargets();
				preferencePageTargets.setTitle("Target Settings");
				IPreferencePage preferencePageLists = new PreferencePageLists();
				preferencePageLists.setTitle("Lists");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageTargets));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageSWT));
				preferenceManager.addToRoot(new PreferenceNode("3", preferencePageLists));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfo = new Label(composite, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				targetListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private Composite createToolbarModify(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		comboTargetName = createComboTarget(composite);
		buttonAddTarget = createButtonAdd(composite);
		buttonDeleteTarget = createButtonDelete(composite);
		//
		return composite;
	}

	private Combo createComboTarget(Composite parent) {

		Combo combo = new Combo(parent, SWT.NONE);
		combo.setText("");
		combo.setToolTipText("Select a target or type in a new substance name.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					addTarget();
				}
			}
		});
		return combo;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add the target.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addTarget();
			}
		});
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected target(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteTargets();
			}
		});
		return button;
	}

	private TargetsListUI createTargetTable(Composite parent) {

		TargetsListUI listUI = new TargetsListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = listUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		listUI.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				propagateTarget();
			}
		});
		/*
		 * Set/Save the column order.
		 */
		String preferenceName = PreferenceConstants.P_COLUMN_ORDER_TARGET_LIST;
		listSupport.setColumnOrder(table, preferenceName);
		listUI.addColumnMoveListener(new IColumnMoveListener() {

			@Override
			public void handle() {

				listSupport.saveColumnOrder(table, preferenceName);
			}
		});
		/*
		 * Add the delete targets support.
		 */
		ITableSettings tableSettings = listUI.getTableSettings();
		addDeleteMenuEntry(tableSettings);
		addVerifyTargetsMenuEntry(tableSettings);
		addUnverifyTargetsMenuEntry(tableSettings);
		addKeyEventProcessors(tableSettings);
		listUI.applySettings(tableSettings);
		//
		return listUI;
	}

	private void addDeleteMenuEntry(ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Target(s)";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_TARGETS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteTargets();
			}
		});
	}

	private void addVerifyTargetsMenuEntry(ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Verify Target(s) Check";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_TARGETS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				verifyTargets(true);
			}
		});
	}

	private void addUnverifyTargetsMenuEntry(ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Verify Target(s) Uncheck";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_TARGETS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				verifyTargets(false);
			}
		});
	}

	private void addKeyEventProcessors(ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					/*
					 * DEL
					 */
					deleteTargets();
				} else if(e.keyCode == BaseChart.KEY_CODE_i && (e.stateMask & SWT.CTRL) == SWT.CTRL) {
					if((e.stateMask & SWT.ALT) == SWT.ALT) {
						/*
						 * CTRL + ALT + I
						 */
						verifyTargets(false);
					} else {
						/*
						 * CTRL + I
						 */
						verifyTargets(true);
					}
				} else {
					propagateTarget();
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void verifyTargets(boolean verified) {

		Iterator iterator = targetListUI.getStructuredSelection().iterator();
		while(iterator.hasNext()) {
			Object object = iterator.next();
			if(object instanceof IIdentificationTarget) {
				IIdentificationTarget identificationTarget = (IIdentificationTarget)object;
				identificationTarget.setManuallyVerified(verified);
			}
		}
		updateTargets();
	}

	private void propagateTarget() {

		Table table = targetListUI.getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IIdentificationTarget) {
				/*
				 * Send the mass spectrum if available.
				 */
				IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
				IScanMSD massSpectrum = getMassSpectrum();
				/*
				 * Send the identification target update to let e.g. the molecule renderer react on an update.
				 */
				IIdentificationTarget target = (IIdentificationTarget)object;
				eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, target);
				/*
				 * Send the mass spectrum update, e.g. used by the comparison part.
				 */
				if(massSpectrum != null) {
					map.clear();
					IIdentificationTarget identificationTarget = (IIdentificationTarget)object;
					map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN, massSpectrum);
					map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY, identificationTarget);
					eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, map);
				}
			}
		}
	}

	private void applySettings() {

		setComboTargetNameItems();
	}

	private void setComboTargetNameItems() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean useTargetList = preferenceStore.getBoolean(PreferenceConstants.P_USE_TARGET_LIST);
		//
		String[] items;
		if(useTargetList) {
			items = targetListUtil.parseString(preferenceStore.getString(PreferenceConstants.P_TARGET_LIST));
			Arrays.sort(items);
		} else {
			items = new String[]{};
		}
		//
		comboTargetName.setItems(items);
	}

	private void updateTargets() {

		updateLabel();
		updateWidgets();
		//
		targetListUI.sortTable();
		Table table = targetListUI.getTable();
		if(table.getItemCount() > 0) {
			table.setSelection(0);
			//
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			boolean propagateTargetOnUpdate = preferenceStore.getBoolean(PreferenceConstants.P_PROPAGATE_TARGET_ON_UPDATE);
			if(propagateTargetOnUpdate) {
				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						propagateTarget();
					}
				});
			}
		}
	}

	private void updateLabel() {

		if(object instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)object;
			labelInfo.setText(scanDataSupport.getScanLabel(scanMSD));
			targetListUI.setInput(scanMSD.getTargets());
		} else if(object instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)object;
			labelInfo.setText(scanDataSupport.getScanLabel(scanCSD));
			targetListUI.setInput(scanCSD.getTargets());
		} else if(object instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)object;
			labelInfo.setText(scanDataSupport.getScanLabel(scanWSD));
			targetListUI.setInput(scanWSD.getTargets());
		} else if(object instanceof IPeak) {
			IPeak peak = (IPeak)object;
			labelInfo.setText(peakDataSupport.getPeakLabel(peak));
			targetListUI.setInput(peak.getTargets());
		} else if(object instanceof IChromatogramMSD) {
			IChromatogramMSD chromatogramMSD = (IChromatogramMSD)object;
			labelInfo.setText(chromatogramDataSupport.getChromatogramLabel(chromatogramMSD));
			targetListUI.setInput(chromatogramMSD.getTargets());
		} else if(object instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)object;
			labelInfo.setText(chromatogramDataSupport.getChromatogramLabel(chromatogramCSD));
			targetListUI.setInput(chromatogramCSD.getTargets());
		} else if(object instanceof IChromatogramWSD) {
			IChromatogramWSD chromatogramWSD = (IChromatogramWSD)object;
			labelInfo.setText(chromatogramDataSupport.getChromatogramLabel(chromatogramWSD));
			targetListUI.setInput(chromatogramWSD.getTargets());
		} else {
			labelInfo.setText("No target data has been selected yet.");
			targetListUI.clear();
		}
		//
		String editInformation = targetListUI.isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
		labelInfo.setText(labelInfo.getText() + " - " + editInformation);
	}

	private void updateWidgets() {

		boolean enabled = (object == null) ? false : true;
		comboTargetName.setEnabled(enabled);
		buttonAddTarget.setEnabled(enabled);
		buttonDeleteTarget.setEnabled(enabled);
	}

	@SuppressWarnings("rawtypes")
	private void deleteTargets() {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Target(s)");
		messageBox.setMessage("Would you like to delete the selected target(s)?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Delete Target
			 */
			Iterator iterator = targetListUI.getStructuredSelection().iterator();
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof ITarget) {
					deleteTarget((ITarget)object);
				}
			}
			updateTargets();
		}
	}

	private void deleteTarget(ITarget target) {

		if(object instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)object;
			scanMSD.removeTarget((IScanTargetMSD)target);
		} else if(object instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)object;
			scanCSD.removeTarget((IScanTargetCSD)target);
		} else if(object instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)object;
			scanWSD.removeTarget((IScanTargetWSD)target);
		} else if(object instanceof IPeak) {
			IPeak peak = (IPeak)object;
			peak.removeTarget((IPeakTarget)target);
		} else if(object instanceof IChromatogramMSD) {
			IChromatogramMSD chromatogramMSD = (IChromatogramMSD)object;
			chromatogramMSD.removeTarget((IChromatogramTargetMSD)target);
		} else if(object instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)object;
			chromatogramCSD.removeTarget((IChromatogramTargetCSD)target);
		} else if(object instanceof IChromatogramWSD) {
			IChromatogramWSD chromatogramWSD = (IChromatogramWSD)object;
			chromatogramWSD.removeTarget((IChromatogramTargetWSD)target);
		}
		/*
		 * Don't do an table update here, cause this method could be called several times in a loop.
		 */
	}

	private void addTarget() {

		String substanceName = comboTargetName.getText().trim();
		//
		if("".equals(substanceName)) {
			MessageDialog.openError(shell, "Add Target", "The substance name must be not empty.");
		} else {
			/*
			 * Add a new entry.
			 */
			ILibraryInformation libraryInformation = new LibraryInformation();
			libraryInformation.setName(substanceName);
			IComparisonResult comparisonResult = ComparisonResult.createBestMatchComparisonResult();
			//
			if(object instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)object;
				MassSpectrumTarget identificationTarget = new MassSpectrumTarget(libraryInformation, comparisonResult);
				setIdentifier(identificationTarget);
				scanMSD.addTarget(identificationTarget);
			} else if(object instanceof IScanCSD) {
				IScanCSD scanCSD = (IScanCSD)object;
				ScanTargetCSD identificationTarget = new ScanTargetCSD(libraryInformation, comparisonResult);
				setIdentifier(identificationTarget);
				scanCSD.addTarget(identificationTarget);
			} else if(object instanceof IScanWSD) {
				IScanWSD scanWSD = (IScanWSD)object;
				IScanTargetWSD identificationTarget = new ScanTargetWSD(libraryInformation, comparisonResult);
				setIdentifier(identificationTarget);
				scanWSD.addTarget(identificationTarget);
			} else if(object instanceof IPeak) {
				IPeak peak = (IPeak)object;
				IPeakTarget identificationTarget = new PeakTarget(libraryInformation, comparisonResult);
				setIdentifier(identificationTarget);
				peak.addTarget(identificationTarget);
			} else if(object instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)object;
				IChromatogramTargetMSD identificationTarget = new ChromatogramTargetMSD(libraryInformation, comparisonResult);
				setIdentifier(identificationTarget);
				chromatogramMSD.addTarget(identificationTarget);
			} else if(object instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)object;
				IChromatogramTargetCSD identificationTarget = new ChromatogramTargetCSD(libraryInformation, comparisonResult);
				setIdentifier(identificationTarget);
				chromatogramCSD.addTarget(identificationTarget);
			} else if(object instanceof IChromatogramWSD) {
				IChromatogramWSD chromatogramWSD = (IChromatogramWSD)object;
				IChromatogramTargetWSD identificationTarget = new ChromatogramTargetWSD(libraryInformation, comparisonResult);
				setIdentifier(identificationTarget);
				chromatogramWSD.addTarget(identificationTarget);
			}
			//
			comboTargetName.setText("");
			updateTargets();
		}
	}

	/**
	 * May return null.
	 * 
	 * @return IScanMSD
	 */
	private IScanMSD getMassSpectrum() {

		if(object instanceof IScanMSD) {
			return (IScanMSD)object;
		} else if(object instanceof IPeakMSD) {
			IPeakMSD peakMSD = (IPeakMSD)object;
			return peakMSD.getExtractedMassSpectrum();
		} else {
			return null;
		}
	}

	private void setIdentifier(IIdentificationTarget identificationTarget) {

		identificationTarget.setIdentifier(IDENTIFIER_MANUAL);
	}

	private void updateTargetOptionLabel() {

		String text = showChromatogramTargets ? "Chromatogram Targets Active" : "Scan/Peak Targets Active";
		labelTargetOption.setText(text);
	}
}
