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
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.comparator.TimeRangeComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRangeSupport;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;

public class TimeRangesEditor extends Composite {

	private static final String DESCRIPTION = "Time Ranges";
	private static final String FILTER_EXTENSION = "*.txt";
	private static final String FILTER_NAME = "Time Ranges (*.txt)";
	private static final String FILE_NAME = "TimeRanges.txt";
	//
	private TimeRangesUI timeRangesUI;
	private TimeRangesListUI timeRangesListUI;
	//
	private String clipStartIdentifier = "";
	//
	private TimeRanges timeRanges;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public TimeRangesEditor(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setClipStartIdentifier(String clipStartIdentifier) {

		this.clipStartIdentifier = clipStartIdentifier;
	}

	public void setInput(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		updateTimeRanges();
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(6, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		createTimeRangesSection(this);
		timeRangesListUI = createTimeRangesListUI(this);
	}

	private void createTimeRangesSection(Composite parent) {

		timeRangesUI = createTimeRangesUI(parent);
		createButtonImport(parent);
		createButtonImportChromatogram(parent);
		createButtonExport(parent);
		createButtonReset(parent);
		createButtonDelete(parent);
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Import time range(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(timeRanges != null) {
					FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
					fileDialog.setText(DESCRIPTION);
					fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
					fileDialog.setFilterNames(new String[]{FILTER_NAME});
					fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER));
					String pathname = fileDialog.open();
					if(pathname != null) {
						File file = new File(pathname);
						String path = file.getParentFile().getAbsolutePath();
						preferenceStore.putValue(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER, path);
						timeRanges.importItems(file);
						updateTimeRanges();
					}
				}
			}
		});
		return button;
	}

	private Button createButtonImportChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Import time range(s) from a *.ocb file.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(timeRanges != null) {
					FileDialog fileDialog = new FileDialog(e.display.getActiveShell(), SWT.READ_ONLY);
					fileDialog.setText("Chromatogram (*.ocb)");
					fileDialog.setFilterExtensions(new String[]{"*.ocb"});
					fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER));
					//
					String pathname = fileDialog.open();
					if(pathname != null) {
						preferenceStore.setValue(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER, fileDialog.getFilterPath());
						File file = new File(pathname);
						TimeRanges timeRangesImport = extractTimeRangesFromChromatogram(file, new NullProgressMonitor());
						if(timeRangesImport != null) {
							TimeRangeSupport.transferTimeRanges(timeRangesImport, timeRanges);
							updateTimeRanges();
						}
					}
				}
			}
		});
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Export time range(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(timeRanges != null) {
					FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
					fileDialog.setOverwrite(true);
					fileDialog.setText(DESCRIPTION);
					fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
					fileDialog.setFilterNames(new String[]{FILTER_NAME});
					fileDialog.setFileName(FILE_NAME);
					fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER));
					String pathname = fileDialog.open();
					if(pathname != null) {
						File file = new File(pathname);
						String path = file.getParentFile().getAbsolutePath();
						preferenceStore.putValue(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER, path);
						if(timeRanges.exportItems(file)) {
							MessageDialog.openInformation(button.getShell(), DESCRIPTION, "The time ranges have been exported successfully.");
						} else {
							MessageDialog.openWarning(button.getShell(), DESCRIPTION, "Something went wrong to export the time ranges.");
						}
					}
				}
			}
		});
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete all time range(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(timeRanges != null) {
					if(MessageDialog.openQuestion(e.display.getActiveShell(), "Delete Time Range(s)", "Would you like to delete all time range(s)?")) {
						timeRanges.clear();
						updateTimeRanges();
					}
				}
			}
		});
		return button;
	}

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Reset the time ranges to the default values.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(timeRanges != null) {
					if(MessageDialog.openQuestion(e.display.getActiveShell(), "Reset Time Range(s)", "Would you like to reset the time range(s)?")) {
						for(TimeRange timeRange : timeRanges.values()) {
							timeRange.update(0, 0, 0);
						}
						updateTimeRanges();
					}
				}
			}
		});
		return button;
	}

	private TimeRangesUI createTimeRangesUI(Composite parent) {

		TimeRangesUI timeRangesUI = new TimeRangesUI(parent, SWT.NONE);
		timeRangesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		timeRangesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateTimeRangesTable();
			}
		});
		return timeRangesUI;
	}

	private TimeRangesListUI createTimeRangesListUI(Composite parent) {

		TimeRangesListUI timeRangesListUI = new TimeRangesListUI(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 6;
		timeRangesListUI.getTable().setLayoutData(gridData);
		timeRangesListUI.setEditEnabled(false);
		timeRangesListUI.setSortEnabled(false);
		Table table = timeRangesListUI.getTable();
		/*
		 * Selection
		 */
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = timeRangesListUI.getStructuredSelection().getFirstElement();
				if(object instanceof TimeRange) {
					TimeRange timeRange = (TimeRange)object;
					String[] items = timeRangesUI.getItems();
					exitloop:
					for(int i = 0; i < items.length; i++) {
						if(items[i].equals(timeRange.getIdentifier())) {
							timeRangesUI.select(i);
							break exitloop;
						}
					}
				}
			}
		});
		/*
		 * Delete item(s)
		 */
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(timeRanges != null) {
					if(e.keyCode == SWT.DEL) {
						MessageBox messageBox = new MessageBox(e.display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						messageBox.setText("Delete time range(s)");
						messageBox.setMessage("Would you like to delete the selected time range(s)?");
						if(messageBox.open() == SWT.YES) {
							/*
							 * Collect
							 */
							List<TimeRange> deleteItems = new ArrayList<>();
							for(Object object : timeRangesListUI.getStructuredSelection().toList()) {
								if(object instanceof TimeRange) {
									TimeRange timeRange = (TimeRange)object;
									deleteItems.add(timeRange);
								}
							}
							/*
							 * Delete/Update
							 */
							delete(deleteItems);
							updateTimeRanges();
						}
					}
				}
			}
		});
		//
		return timeRangesListUI;
	}

	private void delete(List<TimeRange> deleteItems) {

		if(timeRanges != null) {
			for(TimeRange deleteItem : deleteItems) {
				timeRanges.remove(deleteItem.getIdentifier());
			}
		}
	}

	private void updateTimeRanges() {

		updateTimeRangesUI();
		updateTimeRangesTable();
	}

	private void updateTimeRangesUI() {

		if(timeRanges != null) {
			timeRangesUI.setInput(timeRanges);
		} else {
			timeRangesUI.setInput(null);
		}
	}

	private void updateTimeRangesTable() {

		if(timeRanges != null) {
			List<TimeRange> list = new ArrayList<>(timeRanges.values());
			Collections.sort(list, new TimeRangeComparator());
			timeRangesListUI.setInput(list);
		} else {
			timeRangesListUI.setInput(null);
		}
	}

	private TimeRanges extractTimeRangesFromChromatogram(File file, IProgressMonitor monitor) {

		TimeRanges timeRanges = new TimeRanges();
		IChromatogram<? extends IPeak> chromatogram = loadChromatogram(file, monitor);
		if(chromatogram != null) {
			/*
			 * Extract the time ranges from the chromatogram.
			 */
			timeRanges.addAll(extractTimeRanges(chromatogram).values());
			/*
			 * Adjust or set the clip start identifier.
			 */
			if(clipStartIdentifier != null && !clipStartIdentifier.isEmpty()) {
				TimeRange timeRange = timeRanges.get(clipStartIdentifier);
				if(timeRange != null) {
					if(timeRange.getStop() == 0) {
						timeRange.updateStop(chromatogram.getStartRetentionTime());
						timeRange.updateCenter();
					}
				} else {
					timeRanges.add(new TimeRange(clipStartIdentifier, 0, chromatogram.getStartRetentionTime()));
				}
			}
		}
		return timeRanges;
	}

	private IChromatogram<? extends IPeak> loadChromatogram(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramMSD> processingInfoMSD = ChromatogramConverterMSD.getInstance().convert(file, monitor);
		IChromatogram<? extends IPeak> chromatogram = processingInfoMSD.getProcessingResult();
		if(chromatogram == null) {
			IProcessingInfo<IChromatogramCSD> processingInfoCSD = ChromatogramConverterCSD.getInstance().convert(file, monitor);
			chromatogram = processingInfoCSD.getProcessingResult();
		}
		//
		return chromatogram;
	}

	private TimeRanges extractTimeRanges(IChromatogram<? extends IPeak> chromatogram) {

		TimeRanges timeRanges = new TimeRanges();
		//
		if(chromatogram != null && timeRanges != null) {
			for(IPeak peak : chromatogram.getPeaks()) {
				IPeakModel peakModel = peak.getPeakModel();
				int start = peakModel.getStartRetentionTime();
				int stop = peakModel.getStopRetentionTime();
				IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets());
				if(identificationTarget != null) {
					ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
					String identifier = libraryInformation.getName();
					timeRanges.add(new TimeRange(identifier, start, stop));
				}
			}
		}
		//
		return timeRanges;
	}
}
