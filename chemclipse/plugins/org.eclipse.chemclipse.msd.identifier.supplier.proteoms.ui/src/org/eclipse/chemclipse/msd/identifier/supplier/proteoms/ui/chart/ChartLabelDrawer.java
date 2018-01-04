/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.chart;

import java.util.Random;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IAxisSet;
import org.swtchart.ISeries;

public class ChartLabelDrawer implements PaintListener, DisposeListener {

	private Chart chart;
	private IAxis xAxis;
	private IAxis yAxis;
	private int firstIntesitiyPeak = 30;

	public ChartLabelDrawer(Chart chart) {
		this.chart = chart;
		IAxisSet axisSet = chart.getAxisSet();
		xAxis = axisSet.getXAxis(0);
		yAxis = axisSet.getYAxis(0);
		chart.getPlotArea().addPaintListener(this);
	}

	/**
	 * Set
	 * 
	 * @param firstIntesitiyPeak
	 */
	public void setFirstIntesitiyPeak(int firstIntesitiyPeak) {

		this.firstIntesitiyPeak = firstIntesitiyPeak;
		double maxIntensity = findMostIntensityPeaks();
		ISeries series = chart.getSeriesSet().getSeries()[0];
	}

	public int getFirstIntesitiyPeak() {

		return firstIntesitiyPeak;
	}

	private double findMostIntensityPeaks() {

		ISeries ser = chart.getSeriesSet().getSeries()[0];
		double[] ySeries = ser.getYSeries();
		return ChartUtil.getMaxValue(ySeries);
	}

	@Override
	public void paintControl(PaintEvent e) {

		ISeries ser = chart.getSeriesSet().getSeries()[0];
		Random r = new Random();
		for(double mz : ser.getXSeries()) {
			if(r.nextFloat() < 0.96F) {
				continue;
			}
			int xPos = xAxis.getPixelCoordinate(mz);
			int yPos = yAxis.getPixelCoordinate(mz);
			// log.debug("mz= " + mz + " x= {} y {}", xPos, yPos);
			String mzString = mz + "";
			Point textExtent = e.gc.textExtent(mzString);
			Rectangle clientArea = chart.getPlotArea().getClientArea();
			// System.out.println(textExtent);
			int xPosText = xPos - textExtent.x / 2;
			int yPosText = yPos - textExtent.y;
			if(xPosText < 0) { // most left peak
				xPosText = 0;
			}
			if(yPosText < 0) {
				yPosText = textExtent.y;
			}
			if(yPosText == clientArea.width) {
				yPosText -= textExtent.y;
			}
			// if(xPosText == clientArea.width) {
			// xPosText = xPos - textExtent.x;
			// }
			e.gc.drawString(mz + "", xPosText, yPosText);
			// log.debug("x=" + xPosText + " y=" + yPosText);
			// System.out.println();
			// System.out.println(clientArea);
		}
	}

	@Override
	protected void finalize() throws Throwable {

		clear();
		super.finalize();
	}

	protected void clear() {

		if(chart != null) {
			chart.getPlotArea().removePaintListener(this);
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {

		clear();
	}
}
