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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifier;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.eavp.service.swtchart.core.ScrollableChart;
import org.eclipse.eavp.service.swtchart.menu.AbstractChartMenuEntry;
import org.eclipse.eavp.service.swtchart.menu.IChartMenuEntry;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

public class PeakIdentifierMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	private ExtendedChromatogramUI extendedChromatogramUI;
	private String name;
	private String peakIdentifierId;
	private String type;
	private IChromatogramSelection chromatogramSelection;

	public PeakIdentifierMenuEntry(ExtendedChromatogramUI extendedChromatogramUI, String name, String peakIdentifierId, String type, IChromatogramSelection chromatogramSelection) {
		this.extendedChromatogramUI = extendedChromatogramUI;
		this.name = name;
		this.peakIdentifierId = peakIdentifierId;
		this.type = type;
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public String getCategory() {

		return "Peak Identifier";
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		if(chromatogramSelection != null) {
			/*
			 * Create the runnable.
			 */
			IRunnableWithProgress runnable = new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					switch(type) {
						case ExtendedChromatogramUI.TYPE_MSD:
							if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
								IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
								PeakIdentifier.identify(chromatogramSelectionMSD, peakIdentifierId, monitor);
							}
							break;
					}
				}
			};
			/*
			 * Execute
			 */
			extendedChromatogramUI.processChromatogram(runnable);
		}
	}
}
