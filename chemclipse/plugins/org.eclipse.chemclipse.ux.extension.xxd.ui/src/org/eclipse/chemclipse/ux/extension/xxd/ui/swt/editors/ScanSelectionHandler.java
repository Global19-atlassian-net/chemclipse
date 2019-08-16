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

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.events.AbstractHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;

public class ScanSelectionHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

	private ExtendedChromatogramUI extendedChromatogramUI;

	public ScanSelectionHandler(ExtendedChromatogramUI extendedChromatogramUI) {
		this.extendedChromatogramUI = extendedChromatogramUI;
	}

	@Override
	public int getEvent() {

		return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
	}

	@Override
	public int getButton() {

		return BaseChart.BUTTON_LEFT;
	}

	@Override
	public int getStateMask() {

		return SWT.NONE;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handleEvent(BaseChart baseChart, Event event) {

		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			int retentionTime = (int)baseChart.getSelectedPrimaryAxisValue(event.x, IExtendedChart.X_AXIS);
			int scanNumber = chromatogram.getScanNumber(retentionTime);
			IScan scan = chromatogram.getScan(scanNumber);
			if(scan != null) {
				chromatogramSelection.setSelectedScan(scan);
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
						eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, scan);
					}
				});
			}
		}
	}
}
