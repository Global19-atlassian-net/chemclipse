/*******************************************************************************
 * Copyright (c) 2013, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_0701;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_0801;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_0802;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_0803;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_0901;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_0902;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_0903;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_1001;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_1002;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_1003;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_1004;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriterMSD extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		String versionSave = PreferenceSupplier.getVersionSave();
		IChromatogramMSDWriter chromatogramWriter;
		/*
		 * Check the requested version of the file to be exported.
		 * TODO Optimize
		 */
		if(versionSave.equals(IFormat.VERSION_0701)) {
			chromatogramWriter = new ChromatogramWriter_0701();
		} else if(versionSave.equals(IFormat.VERSION_0801)) {
			chromatogramWriter = new ChromatogramWriter_0801();
		} else if(versionSave.equals(IFormat.VERSION_0802)) {
			chromatogramWriter = new ChromatogramWriter_0802();
		} else if(versionSave.equals(IFormat.VERSION_0803)) {
			chromatogramWriter = new ChromatogramWriter_0803();
		} else if(versionSave.equals(IFormat.VERSION_0901)) {
			chromatogramWriter = new ChromatogramWriter_0901();
		} else if(versionSave.equals(IFormat.VERSION_0902)) {
			chromatogramWriter = new ChromatogramWriter_0902();
		} else if(versionSave.equals(IFormat.VERSION_0903)) {
			chromatogramWriter = new ChromatogramWriter_0903();
		} else if(versionSave.equals(IFormat.VERSION_1001)) {
			chromatogramWriter = new ChromatogramWriter_1001();
		} else if(versionSave.equals(IFormat.VERSION_1002)) {
			chromatogramWriter = new ChromatogramWriter_1002();
		} else if(versionSave.equals(IFormat.VERSION_1003)) {
			chromatogramWriter = new ChromatogramWriter_1003();
		} else {
			chromatogramWriter = new ChromatogramWriter_1004();
		}
		/*
		 * Load all scan proxies before exporting the file.
		 */
		chromatogram.enforceLoadScanProxies(monitor);
		chromatogramWriter.writeChromatogram(file, chromatogram, monitor);
	}
}
