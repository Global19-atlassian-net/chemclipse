/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.jcampdx.converter;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.massspectrum.AbstractMassSpectrumExportConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.MassSpectrumExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumExportConverter extends AbstractMassSpectrumExportConverter {

	@Override
	public IMassSpectrumExportConverterProcessingInfo convert(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) {

		return getProcessingInfo();
	}

	@Override
	public IMassSpectrumExportConverterProcessingInfo convert(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) {

		return getProcessingInfo();
	}

	private IMassSpectrumExportConverterProcessingInfo getProcessingInfo() {

		IMassSpectrumExportConverterProcessingInfo processingInfo = new MassSpectrumExportConverterProcessingInfo();
		processingInfo.addErrorMessage("JCAMP-DX Library", "The JCAMP-DX converter has no capabilities to export a library.");
		return processingInfo;
	}
}
