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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.nmr.model.core.IScanNMR;
import org.eclipse.chemclipse.nmr.model.core.ISignalNMR;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ChartNMR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
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
import org.eclipse.swt.widgets.Shell;

public class ExtendedNMRScanUI {

	private ChartNMR chartNMR;
	private IScanNMR scanNMR;
	//
	private Display display = Display.getDefault();
	private Shell shell = display.getActiveShell();
	//
	private boolean rawData = false;

	public ExtendedNMRScanUI(Composite parent) {
		initialize(parent);
	}

	public void update(IScanNMR scanNMR) {

		chartNMR.modifyChart(rawData);
		this.scanNMR = scanNMR;
		updateScan();
	}

	private void updateScan() {

		chartNMR.deleteSeries();
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		ILineSeriesData lineSeriesData = getLineSeriesData(scanNMR, "NMR", rawData);
		//
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(Colors.RED);
		if(rawData) {
			lineSeriesSettings.setEnableArea(false);
		} else {
			lineSeriesSettings.setEnableArea(true);
		}
		//
		lineSeriesDataList.add(lineSeriesData);
		chartNMR.addSeriesData(lineSeriesDataList);
	}

	private ILineSeriesData getLineSeriesData(IScanNMR scanNMR, String id, boolean raw) {

		ISeriesData seriesData;
		if(raw) {
			seriesData = getSeriesDataRaw(scanNMR, id);
		} else {
			seriesData = getSeriesDataProcessed(scanNMR, id);
		}
		//
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		return lineSeriesData;
	}

	private ISeriesData getSeriesDataProcessed(IScanNMR scanNMR, String id) {

		double[] xSeries;
		double[] ySeries;
		//
		if(scanNMR != null) {
			int size = scanNMR.size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(ISignalNMR scanSignal : scanNMR) {
				xSeries[index] = scanSignal.getChemicalShift();
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

	private ISeriesData getSeriesDataRaw(IScanNMR scanNMR, String id) {

		double[] ySeries;
		//
		if(scanNMR != null) {
			ySeries = scanNMR.getRawSignals().clone();
		} else {
			ySeries = new double[0];
		}
		//
		return new SeriesData(ySeries, id);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		createScanChart(parent);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createRawProcessedButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createRawProcessedButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the raw/processed modus");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_NMR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				rawData = !rawData;
				chartNMR.modifyChart(rawData);
				updateScan();
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

				IPreferencePage preferencePageChromatogram = new PreferencePageChromatogram();
				preferencePageChromatogram.setTitle("Scan Settings ");
				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageChromatogram));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageSWT));
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

	private void applySettings() {

		updateScan();
	}

	private void reset() {

		updateScan();
	}

	private void createScanChart(Composite parent) {

		chartNMR = new ChartNMR(parent, SWT.BORDER);
		chartNMR.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chartNMR.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		//
		chartNMR.applySettings(chartSettings);
	}
}
