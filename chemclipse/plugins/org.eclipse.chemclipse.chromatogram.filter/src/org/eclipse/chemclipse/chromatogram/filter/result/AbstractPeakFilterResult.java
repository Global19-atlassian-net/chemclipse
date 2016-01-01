/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.result;

public abstract class AbstractPeakFilterResult implements IPeakFilterResult {

	private ResultStatus resultStatus = ResultStatus.UNDEFINED;
	private String description = "";

	/**
	 * Creates a new FilterResult instance.<br/>
	 * If a value of the parameters is null, the default value will be chosen.<br/>
	 * resultStatus (default) == ResultStatus.UNDEFINED<br/>
	 * description (default) == ""
	 * 
	 * @param resultStatus
	 * @param description
	 */
	public AbstractPeakFilterResult(ResultStatus resultStatus, String description) {
		if(resultStatus != null) {
			this.resultStatus = resultStatus;
		}
		if(description != null) {
			this.description = description;
		}
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public ResultStatus getResultStatus() {

		return resultStatus;
	}
}
