/*******************************************************************************
 * Copyright (c) 2010, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ChromatogramOutputEntry implements IChromatogramOutputEntry {

	private String outputFolder = "";
	private String converterId = "";

	/**
	 * Set the output file path and the converter id.
	 * 
	 * @param outputFile
	 * @param converterId
	 */
	public ChromatogramOutputEntry(String outputFolder, String converterId) {
		if(outputFolder != null && converterId != null) {
			this.outputFolder = outputFolder;
			this.converterId = converterId;
		}
	}

	@Override
	public String getOutputFolder() {

		return outputFolder;
	}

	@Override
	public String getConverterId() {

		return converterId;
	}
}
