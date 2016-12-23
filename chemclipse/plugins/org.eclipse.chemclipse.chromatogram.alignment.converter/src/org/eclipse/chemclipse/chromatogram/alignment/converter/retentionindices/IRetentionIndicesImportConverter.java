/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.alignment.converter.retentionindices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.alignment.model.core.IRetentionIndices;
import org.eclipse.chemclipse.chromatogram.alignment.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.chromatogram.alignment.converter.exceptions.FileIsNotReadableException;

public interface IRetentionIndicesImportConverter {

	/**
	 * All implementing classes must return an IRetentionIndices instance.<br/>
	 * If no suitable converter is available, null will be returned.<br/>
	 * <br/>
	 * AbstractRetentionIndexImportConverter implements
	 * IRetentionIndexImportConverter. When extending from
	 * AbstractRetentionIndexImportConverter => super.validate(retentionIndices)
	 * can be used.
	 * 
	 * @param retentionIndices
	 * @return IRetentionIndices
	 * @throws FileNotFoundException
	 * @throws FileIsNotReadableException
	 * @throws FileIsEmptyException
	 * @throws IOException
	 */
	IRetentionIndices convert(File retentionIndices) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException;

	/**
	 * This method validates the file which contains the retention indices to be
	 * imported.
	 * 
	 * @param retention
	 *            indices
	 * @throws FileNotFoundException
	 * @throws FileIsNotReadableException
	 * @throws FileIsEmptyException
	 * @throws IOException
	 */
	void validate(File retentionIndices) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException;
}
