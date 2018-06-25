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
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.internal.support.SpecificationValidator;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.io.ReportWriter2;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.IReportSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class Report2 extends AbstractReport {

	private static final Logger logger = Logger.getLogger(Report2.class);

	@Override
	public IProcessingInfo report(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IReportSettings chromatogramReportSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		/*
		 * Validate the file.
		 */
		file = SpecificationValidator.validateSpecification(file);
		IProcessingInfo processingInfoValidate = super.validate(file);
		/*
		 * Don't process if errors have occurred.
		 */
		if(processingInfoValidate.hasErrorMessages()) {
			processingInfo.addMessages(processingInfoValidate);
		} else {
			ReportWriter2 chromatogramReport = new ReportWriter2();
			try {
				chromatogramReport.generate(file, append, chromatograms, chromatogramReportSettings, monitor);
				processingInfo.setProcessingResult(file);
			} catch(IOException e) {
				logger.warn(e);
				processingInfo.addErrorMessage("ChemClipse Chromatogram Report", "The report couldn't be created. An error occured.");
			}
		}
		return processingInfo;
	}
}
