/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVaribleExtracted;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.SeriesConverter;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.ICustomSelectionHandler;
import org.eclipse.eavp.service.swtchart.events.AbstractHandledEventProcessor;
import org.eclipse.eavp.service.swtchart.events.IHandledEventProcessor;
import org.eclipse.eavp.service.swtchart.events.MouseDownEvent;
import org.eclipse.eavp.service.swtchart.events.MouseMoveCursorEvent;
import org.eclipse.eavp.service.swtchart.events.MouseMoveSelectionEvent;
import org.eclipse.eavp.service.swtchart.events.MouseMoveShiftEvent;
import org.eclipse.eavp.service.swtchart.events.MouseUpEvent;
import org.eclipse.eavp.service.swtchart.events.ResetSeriesEvent;
import org.eclipse.eavp.service.swtchart.events.SelectDataPointEvent;
import org.eclipse.eavp.service.swtchart.events.UndoRedoEvent;
import org.eclipse.eavp.service.swtchart.events.ZoomEvent;
import org.eclipse.eavp.service.swtchart.scattercharts.IScatterSeriesData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.swtchart.ISeries;

public class LoadingPlot extends PCA2DPlot {

	private class SelectSeriesEvent extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getButton() {

			return BaseChart.BUTTON_LEFT;
		}

		@Override
		public int getEvent() {

			return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getStateMask() {

			return SWT.CTRL;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			String selectedSeriesId = baseChart.getSelectedseriesId(event);
			if(!selectedSeriesId.equals("")) {
				IVaribleExtracted variable = extractedValues.get(selectedSeriesId);
				variable.getVariableOrigin().setSelected(!variable.getVariableOrigin().isSelected());
			}
		}
	}

	final public static int LABELS_DESCRIPTION = 2;
	final public static int LABELS_RETENTION_TIME_MINUTES = 1;
	final private Set<String> actualSelection = new HashSet<>();
	final private Map<String, IVaribleExtracted> extractedValues = new HashMap<>();
	private int labelsType = LABELS_RETENTION_TIME_MINUTES;

	public LoadingPlot(Composite parent) {
		super(parent, "Loading Plot");
		IChartSettings chartSettings = getChartSettings();
		chartSettings.clearHandledEventProcessors();
		chartSettings.addHandledEventProcessor(new SelectSeriesEvent());
		chartSettings.addHandledEventProcessor(new ResetSeriesEvent());
		chartSettings.addHandledEventProcessor(new SelectDataPointEvent());
		chartSettings.addHandledEventProcessor(new ZoomEvent());
		chartSettings.addHandledEventProcessor(new MouseDownEvent());
		chartSettings.addHandledEventProcessor(new MouseMoveSelectionEvent());
		chartSettings.addHandledEventProcessor(new MouseMoveShiftEvent());
		chartSettings.addHandledEventProcessor(new MouseMoveCursorEvent());
		chartSettings.addHandledEventProcessor(new MouseUpEvent());
		chartSettings.addHandledEventProcessor(new UndoRedoEvent());
		applySettings(chartSettings);
		getBaseChart().addCustomRangeSelectionHandler(new ICustomSelectionHandler() {

			@Override
			public void handleUserSelection(Event event) {

				updateSelection();
			}
		});
	}

	public Set<String> getActualSelection() {

		return actualSelection;
	}

	public Map<String, IVaribleExtracted> getExtractedValues() {

		return extractedValues;
	}

	public int getLabelsType() {

		return labelsType;
	}

	public void setLabelsType(int labelsType) {

		if(labelsType == LABELS_DESCRIPTION || labelsType == LABELS_RETENTION_TIME_MINUTES) {
			this.labelsType = labelsType;
		}
	}

	public void update(IPcaResultsVisualization pcaResults) {

		int pcX = pcaResults.getPcaSettingsVisualization().getPcX();
		int pcY = pcaResults.getPcaSettingsVisualization().getPcY();
		List<IScatterSeriesData> series;
		if(labelsType == LABELS_RETENTION_TIME_MINUTES) {
			series = SeriesConverter.basisVectorsToSeries(pcaResults, pcX, pcY, extractedValues);
		} else {
			series = SeriesConverter.basisVectorsToSeriesDescription(pcaResults, pcX, pcY, extractedValues);
		}
		deleteSeries();
		addSeriesData(series);
		update(pcX, pcY);
		updateSelection();
	}

	private void updateSelection() {

		BaseChart baseChart = getBaseChart();
		Rectangle plotAreaBounds = baseChart.getPlotArea().getBounds();
		ISeries[] series = baseChart.getSeriesSet().getSeries();
		//
		actualSelection.clear();
		for(ISeries scatterSeries : series) {
			if(scatterSeries != null) {
				int size = scatterSeries.getXSeries().length;
				String id = scatterSeries.getId();
				for(int i = 0; i < size; i++) {
					Point point = scatterSeries.getPixelCoordinates(i);
					if(isPointVisible(point, plotAreaBounds)) {
						actualSelection.add(id);
					}
				}
			}
		}
	}
}
