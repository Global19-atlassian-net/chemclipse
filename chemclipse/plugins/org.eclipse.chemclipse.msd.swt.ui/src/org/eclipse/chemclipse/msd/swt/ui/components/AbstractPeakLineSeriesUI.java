/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Range;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.notifier.IPeakMSDSelectionUpdateNotifier;
import org.eclipse.chemclipse.swt.ui.components.AbstractLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.series.ISeriesSetter;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.chemclipse.swt.ui.support.IAxisTitles;

/**
 * This class offers a solution to draw chromatographic data like chromatograms,
 * baselines and peaks.<br/>
 * It has four axes: milliseconds and minutes on x, abundance and relative
 * abundance on y.<br/>
 * Childs must only override the method given by {@link ISeriesSetter}.
 * 
 * @author eselmeister
 */
public abstract class AbstractPeakLineSeriesUI extends AbstractLineSeriesUI implements ISeriesSetter, KeyListener, MouseListener, IPeakMSDSelectionUpdateNotifier {

	protected IPeakMSD peak;

	public AbstractPeakLineSeriesUI(Composite parent, int style, IAxisTitles axisTitles) {

		super(parent, style, axisTitles);
	}

	@Override
	public void update(IPeakMSD peak, boolean forceReload) {

		this.peak = peak;
		if(!isMaster() || (isMaster() && forceReload)) {
			IPeakModelMSD peakModel = this.peak.getPeakModel();
			double maxSignal = peakModel.getPeakAbundance() + peakModel.getBackgroundAbundance();
			setMaxSignal(maxSignal);
			setSeries(forceReload);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {

	}

	@Override
	public void mouseDown(MouseEvent e) {

	}

	@Override
	public void mouseUp(MouseEvent e) {

	}

	private void setSeries(boolean forceReload) {

		/*
		 * Delete the current and set the new series.
		 */
		deleteAllCurrentSeries();
		setViewSeries();
		getAxisSet().adjustRange();
		setSecondaryRanges();
		redraw();
	}

	/**
	 * Sets the secondary ranges.
	 */
	private void setSecondaryRanges() {

		assert (getXAxisBottom() != null) : "The minutes instance must be not null.";
		assert (getYAxisRight() != null) : "The relativeAbundance instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Minutes
		 */
		min = peak.getPeakModel().getStartRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		max = peak.getPeakModel().getStopRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		range = new Range(min, max);
		getXAxisBottom().setRange(range);
		/*
		 * Relative Abundance Range
		 */
		IPeakModelMSD peakModel = peak.getPeakModel();
		min = ChartUtil.getRelativeAbundance(getMaxSignal(), 0);
		max = ChartUtil.getRelativeAbundance(getMaxSignal(), peakModel.getPeakAbundance() + peakModel.getBackgroundAbundance());
		range = new Range(min, max);
		getYAxisRight().setRange(range);
	}

	@Override
	public void redrawXAxisBottomScale() {

		assert (getXAxisTop() != null) : "The xAxisTop instance must be not null.";
		assert (getXAxisBottom() != null) : "The xAxisBottom instance must be not null.";
		double min, max;
		Range range;
		/*
		 * Set xAxisBottom scale.
		 */
		range = getXAxisTop().getRange();
		min = range.lower / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		max = range.upper / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		ChartUtil.setRange(getXAxisBottom(), min, max);
	}
}
