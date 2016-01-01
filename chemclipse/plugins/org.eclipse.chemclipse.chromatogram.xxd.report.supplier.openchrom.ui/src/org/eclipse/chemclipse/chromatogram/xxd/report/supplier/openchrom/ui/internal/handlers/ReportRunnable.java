/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.ui.internal.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.preferences.PreferenceSupplier;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ReportRunnable implements IRunnableWithProgress {

	private File file;
	private IChromatogram chromatogram;
	private static final String REPORT_SUPPLIER_ID = "org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.chromatogramReportASCII";

	public ReportRunnable(File file, IChromatogram chromatogram) {
		this.file = file;
		this.chromatogram = chromatogram;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("ChemClipse Chromatogram Report", IProgressMonitor.UNKNOWN);
			boolean appendFiles = PreferenceSupplier.isAppendFiles();
			ChromatogramReports.generate(file, appendFiles, chromatogram, REPORT_SUPPLIER_ID, monitor);
		} finally {
			monitor.done();
		}
	}
}
