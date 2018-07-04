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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ChartXIR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.chemclipse.xir.model.core.IScanXIR;
import org.eclipse.chemclipse.xir.model.core.ISignalXIR;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ExtendedXIROverlayUI {

	private ChartXIR chartXIR;
	//
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	private Display display = Display.getDefault();
	private Shell shell = display.getActiveShell();
	//
	private List<IScanXIR> scanSelections = new ArrayList<>();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IColorScheme colorSchemeNormal = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_NORMAL_OVERLAY));

	@Inject
	public ExtendedXIROverlayUI(Composite parent) {
		initialize(parent);
	}

	public void update() {

		scanSelections = editorUpdateSupport.getScanSelectionsXIR();
		refreshUpdateOverlayChart();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		createOverlayChart(parent);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chartXIR.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				applyOverlaySettings();
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

				IPreferencePage preferencePageOverlay = new PreferencePageOverlay();
				preferencePageOverlay.setTitle("Overlay Settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageOverlay));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applyOverlaySettings();
					} catch(Exception e1) {
						System.out.println(e1);
						MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void createOverlayChart(Composite parent) {

		chartXIR = new ChartXIR(parent, SWT.BORDER);
		chartXIR.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void applyOverlaySettings() {

	}

	private void refreshUpdateOverlayChart() {

		chartXIR.deleteSeries();
		if(scanSelections.size() > 0) {
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			int i = 1;
			Color color = colorSchemeNormal.getColor();
			//
			for(IScanXIR scanXIR : scanSelections) {
				/*
				 * Get the data.
				 */
				ILineSeriesData lineSeriesData = getLineSeriesData(scanXIR, "XIR_" + i++);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
				lineSeriesSettings.setLineColor(color);
				lineSeriesSettings.setEnableArea(false);
				//
				lineSeriesDataList.add(lineSeriesData);
				color = colorSchemeNormal.getNextColor();
			}
			//
			chartXIR.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
		}
	}

	private ILineSeriesData getLineSeriesData(IScanXIR scanXIR, String id) {

		ILineSeriesData lineSeriesData = new LineSeriesData(getSeriesDataProcessed(scanXIR, id));
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(Colors.RED);
		lineSeriesSettings.setEnableArea(true);
		//
		return lineSeriesData;
	}

	private ISeriesData getSeriesDataProcessed(IScanXIR scanXIR, String id) {

		double[] xSeries;
		double[] ySeries;
		//
		if(scanXIR != null) {
			int size = scanXIR.getProcessedSignals().size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(ISignalXIR scanSignal : scanXIR.getProcessedSignals()) {
				xSeries[index] = scanSignal.getWavelength();
				ySeries[index] = scanSignal.getIntensity();
				index++;
			}
		} else {
			xSeries = new double[0];
			ySeries = new double[0];
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}
}
