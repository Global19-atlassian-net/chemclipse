/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.tic.ui.internal.modifier;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.processing.IBaselineDetectorProcessingInfo;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class BaselineDetectorRunnable extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final String DESCRIPTION = "Threshold (TIC) Baseline";
	private static final String BASELINE_DETECTOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.tic";

	public BaselineDetectorRunnable(IChromatogramSelection chromatogramSelection) {
		super(chromatogramSelection);
	}

	@Override
	public void execute(IProgressMonitor monitor) {

		if(getChromatogramSelection() != null) {
			IChromatogramSelection chromatogramSelection = getChromatogramSelection();
			IBaselineDetectorProcessingInfo processingInfo = BaselineDetector.setBaseline(chromatogramSelection, BASELINE_DETECTOR_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					chromatogramSelection.reset(true);
				}
			});
		}
	}

	@Override
	public String getDescription() {

		return DESCRIPTION;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			getChromatogramSelection().getChromatogram().doOperation(this, monitor);
		} finally {
			monitor.done();
		}
	}
}
