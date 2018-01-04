/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.chromatogram;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.core.IImportConverter;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramOverviewImportConverterProcessingInfo;

public interface IChromatogramImportConverter extends IImportConverter {

	/**
	 * All implementing classes must return an IChromatogramOverview instance.<br/>
	 * If no suitable converter is available, null will be returned.<br/>
	 * <br/>
	 * AbstractChromatogramImportConverter implements
	 * IChromatogramImportConverter. When extending from
	 * AbstractChromatogramImportConverter => super.validate(chromatogram) can
	 * be used.
	 * 
	 * @param chromatogram
	 * @param monitor
	 * @return {@link IChromatogramOverviewImportConverterProcessingInfo}
	 */
	IChromatogramOverviewImportConverterProcessingInfo convertOverview(File file, IProgressMonitor monitor);
}
