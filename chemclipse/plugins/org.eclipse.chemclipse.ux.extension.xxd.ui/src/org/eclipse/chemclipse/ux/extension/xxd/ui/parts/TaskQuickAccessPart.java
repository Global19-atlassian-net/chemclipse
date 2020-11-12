/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - change to injection instead of static access
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskCombined;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskComparison;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskESTD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskHeatmaps;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskISTD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskLists;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskOverlay;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskOverview;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskPCR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskPeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskQuantitation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskResults;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTaskSubtract;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTasks;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class TaskQuickAccessPart {

	private final IPreferenceStore preferenceStore;
	private final EPartService partService;
	private final EModelService modelService;
	private final MApplication application;

	@Inject
	public TaskQuickAccessPart(Composite parent, MPart part, EPartService partService, EModelService modelService, MApplication application) {

		this.partService = partService;
		this.modelService = modelService;
		this.application = application;
		preferenceStore = Activator.getDefault().getPreferenceStore();
		initialize(parent);
	}

	private void initialize(Composite parent) {

		/*
		 * Add buttons here to focus specialized views.
		 */
		parent.setLayout(new RowLayout());
		//
		createOverviewTask(parent);
		createOverlayTask(parent);
		createScansTask(parent);
		createPeaksTask(parent);
		createListTask(parent);
		createQuantitationTask(parent);
		createInternalStandardsTask(parent);
		createExternalStandardsTask(parent);
		createSubtractScanTask(parent);
		createCombinedScanTask(parent);
		createComparisonScanTask(parent);
		createMeasurementResultTask(parent);
		createHeatmapTask(parent);
		createPcrTask(parent);
		createSettingsTask(parent);
		//
		showInitialViews();
	}

	private void showInitialViews() {

		// PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_TARGETS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_TARGETS), partService, modelService, application);
		// PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SCAN_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_CHART), partService, modelService, application);
		// PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SCAN_TABLE, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_TABLE), partService, modelService, application);
		PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SCAN_BROWSE, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_BROWSE), partService, modelService, application);
	}

	private void createOverviewTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERVIEW_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERVIEW_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the overview modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_HEADER_DATA, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_HEADER_DATA), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_OVERVIEW), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_MISCELLANEOUS_INFO, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_MISCELLANEOUS_INFO), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_SCAN_INFO, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_SCAN_INFO), partService, modelService, application);
			}
		});
		//
	}

	private void createOverlayTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERLAY_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERLAY_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the overlay modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_OVERLAY_CHROMATOGRAM, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_OVERLAY_CHROMATOGRAM_DEFAULT), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_OVERLAY_NMR, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_OVERLAY_NMR), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_OVERLAY_XIR, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_OVERLAY_XIR), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_BASELINE, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_BASELINE), partService, modelService, application);
			}
		});
		//
	}

	private void createScansTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_SCANS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_SCANS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the scan(s) modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_TARGETS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_TARGETS), partService, modelService, application);
				// PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SCAN_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_CHART), partService, modelService, application);
				// PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SCAN_TABLE, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_TABLE), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SCAN_BROWSE, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_BROWSE), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SYNONYMS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SYNONYMS), partService, modelService, application);
				// PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_MOLECULE, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_MOLECULE_STRUCTURE), partService, modelService, application);
			}
		});
		//
	}

	private void createPeaksTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_PEAKS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_PEAKS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the peak(s) modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_CHART), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_DETAILS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_DETECTOR, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_DETECTOR), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_TRACES, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_TRACES), partService, modelService, application);
			}
		});
	}

	private void createListTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_PEAK_LIST_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_PEAK_LIST_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the scan/peak list modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_SCAN_LIST, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_SCAN_LIST), partService, modelService, application);
			}
		});
	}

	private void createQuantitationTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_QUANTITATION_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_QUANTITATION_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the quantitation modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_QUANTITATION_LIST, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_QUANTITATION_LIST), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_QUANTITATION, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_QUANTITATION), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_INTEGRATION_AREA, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_INTEGRATION_AREA), partService, modelService, application);
			}
		});
	}

	private void createInternalStandardsTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INTERNAL_STANDARDS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INTERNAL_STANDARDS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the internal standards modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_INTERNAL_STANDARDS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_INTERNAL_STANDARDS), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_QUANTITATION_REFERENCES, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_QUANTITATION_REFERENCES), partService, modelService, application);
			}
		});
	}

	private void createExternalStandardsTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXTERNAL_STANDARDS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXTERNAL_STANDARDS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the external standards modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_QUANT_PEAKS_LIST, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_QUANT_PEAKS_LIST), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_QUANT_PEAKS_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_QUANT_PEAKS_CHART), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_QUANT_SIGNALS_LIST, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_QUANT_SIGNALS_LIST), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_QUANT_RESPONSE_LIST, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_QUANT_RESPONSE_LIST), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_QUANT_RESPONSE_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_QUANT_RESPONSE_CHART), partService, modelService, application);
			}
		});
	}

	private void createSubtractScanTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_SCAN_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the subtract scan modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SUBTRACT_SCAN, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SUBTRACT_SCAN_PART), partService, modelService, application);
			}
		});
		//
	}

	private void createCombinedScanTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMBINED_SCAN_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMBINED_SCAN_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the combined scan modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_COMBINED_SCAN, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_COMBINED_SCAN_PART), partService, modelService, application);
			}
		});
		//
	}

	private void createComparisonScanTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMPARISON_SCAN_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMPARISON_SCAN_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the comparison scan modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_COMPARISON_SCAN, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_COMPARISON_SCAN_CHART), partService, modelService, application);
			}
		});
	}

	private void createMeasurementResultTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the measurement result modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_MEASUREMENT_RESULTS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_MEASUREMENT_RESULTS), partService, modelService, application);
			}
		});
	}

	private void createHeatmapTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEATMAP_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEATMAP_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the heatmap modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_HEATMAP, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_HEATMAP), partService, modelService, application);
			}
		});
	}

	private void createPcrTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PCR_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PCR_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the PCR modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PlATE_CHARTS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PLATE_CHARTS), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_WELL_DATA, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_WELL_DATA), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_WELL_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_WELL_CHART), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_WELL_CHANNELS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_WELL_CHANNELS), partService, modelService, application);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PlATE_DATA, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PLATE_DATA), partService, modelService, application);
			}
		});
	}

	private void createSettingsTask(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<IPreferencePage> preferencePages = new ArrayList<>();
				preferencePages.add(new PreferencePageTasks());
				preferencePages.add(new PreferencePageTaskOverview());
				preferencePages.add(new PreferencePageTaskOverlay());
				preferencePages.add(new PreferencePageTaskScans());
				preferencePages.add(new PreferencePageTaskPeaks());
				preferencePages.add(new PreferencePageTaskLists());
				preferencePages.add(new PreferencePageTaskQuantitation());
				preferencePages.add(new PreferencePageTaskISTD());
				preferencePages.add(new PreferencePageTaskESTD());
				preferencePages.add(new PreferencePageTaskSubtract());
				preferencePages.add(new PreferencePageTaskCombined());
				preferencePages.add(new PreferencePageTaskComparison());
				preferencePages.add(new PreferencePageTaskResults());
				preferencePages.add(new PreferencePageTaskHeatmaps());
				preferencePages.add(new PreferencePageTaskPCR());
				//
				int i = 1;
				PreferenceManager preferenceManager = new PreferenceManager();
				for(IPreferencePage preferencePage : preferencePages) {
					preferenceManager.addToRoot(new PreferenceNode(Integer.toString(i++), preferencePage));
				}
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				preferenceDialog.open();
			}
		});
	}
}
