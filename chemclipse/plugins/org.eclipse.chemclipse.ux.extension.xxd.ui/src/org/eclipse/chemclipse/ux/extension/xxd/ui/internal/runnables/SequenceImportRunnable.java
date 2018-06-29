/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.converter.model.reports.ISequence;
import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.chemclipse.converter.sequence.SequenceConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class SequenceImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(SequenceImportRunnable.class);
	//
	private File file;
	private ISequence<? extends ISequenceRecord> sequence;

	public SequenceImportRunnable(File file) {
		this.file = file;
	}

	public ISequence<? extends ISequenceRecord> getSequence() {

		return sequence;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Import Chromatogram", IProgressMonitor.UNKNOWN);
			/*
			 * Don't fire an update.
			 */
			try {
				IProcessingInfo processingInfo = SequenceConverter.convert(file, monitor);
				sequence = processingInfo.getProcessingResult(ISequence.class);
			} catch(TypeCastException e) {
				// No action - can't parse the chromatogram.
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			monitor.done();
		}
	}
}
