/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.report;

import java.io.File;

import org.eclipse.chemclipse.converter.core.IImportConverter;
import org.eclipse.chemclipse.converter.processing.report.IReportImportConverterProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IReportImportConverter extends IImportConverter {

	IReportImportConverterProcessingInfo convert(File file, IProgressMonitor monitor);
}
