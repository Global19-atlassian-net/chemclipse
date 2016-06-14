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
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;

public interface IReportConverterSupport {

	void add(final IReportSupplier supplier);

	String[] getFilterExtensions() throws NoConverterAvailableException;

	String[] getFilterNames() throws NoConverterAvailableException;

	String getConverterId(int index) throws NoConverterAvailableException;

	String getConverterId(String name) throws NoConverterAvailableException;

	List<String> getAvailableConverterIds(File file) throws NoConverterAvailableException;

	List<IReportSupplier> getSupplier();

	IReportSupplier getSupplier(String id) throws NoConverterAvailableException;
}
