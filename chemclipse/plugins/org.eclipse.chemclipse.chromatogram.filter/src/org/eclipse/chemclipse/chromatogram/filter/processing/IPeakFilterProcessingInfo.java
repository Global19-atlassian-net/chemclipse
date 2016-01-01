/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.processing;

import org.eclipse.chemclipse.chromatogram.filter.result.IPeakFilterResult;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IPeakFilterProcessingInfo extends IProcessingInfo {

	IPeakFilterResult getPeakFilterResult() throws TypeCastException;

	void setPeakFilterResult(IPeakFilterResult peakFilterResult);
}
