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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.model.reports.ISequence;
import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.MethodSupportUI;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.EditorSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSequences;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.SequenceListUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class ExtendedSequenceListUI {

	private static final Logger logger = Logger.getLogger(ExtendedSequenceListUI.class);
	//
	private Text dataPath;
	private Composite toolbarSearch;
	private Composite toolbarDataPath;
	private Composite toolbarMethod;
	private SearchSupportUI searchSupportUI;
	private MethodSupportUI methodSupportUI;
	private SequenceListUI sequenceListUI;
	//
	private ISequence<? extends ISequenceRecord> sequence;
	//
	private List<ISupplierEditorSupport> supplierEditorSupportList;

	public ExtendedSequenceListUI(Composite parent) {
		supplierEditorSupportList = new ArrayList<>();
		supplierEditorSupportList.add(new EditorSupportFactory(DataType.MSD).getInstanceEditorSupport());
		supplierEditorSupportList.add(new EditorSupportFactory(DataType.CSD).getInstanceEditorSupport());
		supplierEditorSupportList.add(new EditorSupportFactory(DataType.WSD).getInstanceEditorSupport());
		initialize(parent);
	}

	public void update(ISequence<? extends ISequenceRecord> sequence) {

		this.sequence = sequence;
		updateDataSequenceData();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarSearch = createToolbarSearch(parent);
		toolbarDataPath = createToolbarDataPath(parent);
		toolbarMethod = createToolbarMethod(parent);
		createSequenceList(parent);
		//
		PartSupport.setCompositeVisibility(toolbarSearch, false);
		PartSupport.setCompositeVisibility(toolbarDataPath, false);
		PartSupport.setCompositeVisibility(toolbarMethod, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		createButtonToggleToolbarSearch(composite);
		createButtonToggleToolbarDataPath(composite);
		createButtonToggleToolbarMethod(composite);
		createBatchOpenButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarSearch(Composite parent) {

		searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				sequenceListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private Composite createToolbarDataPath(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(4, false));
		//
		createDataPathLabel(composite);
		createDataPathText(composite);
		createSetDataPathButton(composite);
		createSelectDataPathButton(composite);
		//
		return composite;
	}

	private Composite createToolbarMethod(Composite parent) {

		methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener(new IMethodListener() {

			@Override
			public void execute() {

			}
		});
		//
		return methodSupportUI;
	}

	private void createDataPathLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Data Path:");
	}

	private void createDataPathText(Composite parent) {

		dataPath = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		dataPath.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		dataPath.setLayoutData(gridData);
	}

	private Button createSetDataPathButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Set the data path folder.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(sequence != null) {
					File file = new File(sequence.getCanonicalPath());
					if(file.exists()) {
						sequence.setDataPath(file.getParent());
						updateDataSequenceData();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createSelectDataPathButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select the data path folder.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(sequence != null) {
					DirectoryDialog directoryDialog = new DirectoryDialog(DisplayUtils.getShell(button));
					directoryDialog.setText("Sequence Folder");
					directoryDialog.setMessage("Select the sequence root folder.");
					// directoryDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER));
					String directory = directoryDialog.open();
					if(directory != null) {
						// preferenceStore.setValue(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER, directory);
						sequence.setDataPath(directory);
						updateDataSequenceData();
					}
				}
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

	private Button createButtonToggleToolbarDataPath(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the data path toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarDataPath);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the method toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarMethod);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createBatchOpenButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the selected chromatograms");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = sequenceListUI.getTable();
				int[] indices = table.getSelectionIndices();
				List<File> files = new ArrayList<>();
				//
				for(int index : indices) {
					Object object = table.getItem(index).getData();
					if(object instanceof ISequenceRecord) {
						ISequenceRecord sequenceRecord = (ISequenceRecord)object;
						files.add(new File(sequence.getDataPath() + File.separator + sequenceRecord.getDataFile()));
					}
				}
				//
				try {
					openFiles(files);
				} catch(Exception e1) {
					showDataPathWarningMessage();
					logger.warn(e1);
				}
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the scan");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageSequences = new PreferencePageSequences();
				preferencePageSequences.setTitle("Sequences Settings ");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageSequences));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(DisplayUtils.getShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

	}

	private void reset() {

		searchSupportUI.reset();
		sequenceListUI.setInput(sequence);
	}

	private void createSequenceList(Composite parent) {

		sequenceListUI = new SequenceListUI(parent, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = sequenceListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				Object object = sequenceListUI.getStructuredSelection().getFirstElement();
				if(object instanceof ISequenceRecord) {
					ISequenceRecord sequenceRecord = (ISequenceRecord)object;
					List<File> files = new ArrayList<>();
					files.add(new File(sequence.getDataPath() + File.separator + sequenceRecord.getDataFile()));
					try {
						openFiles(files);
					} catch(Exception e1) {
						showDataPathWarningMessage();
						logger.warn(e1);
					}
				}
			}
		});
	}

	private boolean isSupplierFile(ISupplierEditorSupport supplierEditorSupport, File file) {

		if(file.isDirectory()) {
			if(supplierEditorSupport.isSupplierFileDirectory(file)) {
				return true;
			}
		} else {
			if(supplierEditorSupport.isSupplierFile(file)) {
				return true;
			}
		}
		return false;
	}

	private void updateDataSequenceData() {

		if(sequence != null) {
			dataPath.setText(sequence.getDataPath());
			sequenceListUI.setInput(sequence);
		} else {
			dataPath.setText("");
			sequenceListUI.setInput(null);
		}
	}

	private void openFiles(List<File> files) throws Exception {

		Display display = DisplayUtils.getDisplay();
		//
		if(display != null) {
			for(File file : files) {
				if(file.exists()) {
					exitloop:
					for(ISupplierEditorSupport supplierEditorSupport : supplierEditorSupportList) {
						if(isSupplierFile(supplierEditorSupport, file)) {
							display.asyncExec(new Runnable() {

								@Override
								public void run() {

									supplierEditorSupport.openEditor(file);
								}
							});
							break exitloop;
						}
					}
				} else {
					throw new Exception();
				}
			}
		}
	}

	private void showDataPathWarningMessage() {

		MessageDialog.openWarning(DisplayUtils.getShell(), "Open Chromatogram", "The file doesn't exist. Please check that the data path is set correctly.");
	}
}
