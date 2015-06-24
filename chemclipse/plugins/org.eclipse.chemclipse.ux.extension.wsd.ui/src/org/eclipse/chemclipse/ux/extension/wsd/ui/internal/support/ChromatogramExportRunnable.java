/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.internal.support;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramExportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ChromatogramExportRunnable.class);
	private File data;
	private File file;
	private IChromatogramWSD chromatogram;
	private ISupplier supplier;

	public ChromatogramExportRunnable(File file, IChromatogramWSD chromatogram, ISupplier supplier) {

		this.file = file;
		this.chromatogram = chromatogram;
		this.supplier = supplier;
	}

	/**
	 * Returns the written chromatogram file or null.
	 */
	public File getData() {

		return data;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Export Chromatogram", IProgressMonitor.UNKNOWN);
			IChromatogramExportConverterProcessingInfo processingInfo = ChromatogramConverterWSD.convert(file, chromatogram, supplier.getId(), new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			try {
				data = processingInfo.getFile();
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}
}
