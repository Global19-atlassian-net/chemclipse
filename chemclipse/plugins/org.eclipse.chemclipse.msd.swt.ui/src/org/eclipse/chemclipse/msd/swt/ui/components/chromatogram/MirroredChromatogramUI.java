/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.chromatogram;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;

import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

/**
 * Shows the chromatogram selection by the ions without the excluded
 * ones stored in the chromatogram selection instance.<br/>
 * 
 * @author eselmeister
 */
public class MirroredChromatogramUI extends AbstractViewMSDChromatogramUI {

	public MirroredChromatogramUI(Composite parent, int style) {
		super(parent, style);
	}

	// ---------------------------------------------------------------ISeriesSetter
	@Override
	public void setViewSeries() {

		// TODO nur ein Test, anpassen!
		ISeries series;
		ILineSeries lineSeries;
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		/*
		 * Converts the positive chromatogram.
		 */
		series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.POSITIVE, true);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId() + "+");
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.RED);
		/*
		 * Converts the negative chromatogram.
		 */
		series = SeriesConverter.convertChromatogram(chromatogramSelection, Sign.NEGATIVE, true);
		addSeries(series);
		lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId() + "-");
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(Colors.GREEN);
	}
	// ---------------------------------------------------------------ISeriesSetter
}
