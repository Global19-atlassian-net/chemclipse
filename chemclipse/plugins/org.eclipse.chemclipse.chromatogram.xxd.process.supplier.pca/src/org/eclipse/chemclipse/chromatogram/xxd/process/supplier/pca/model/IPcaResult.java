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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

public interface IPcaResult {

	double[] getEigenSpace();

	double getErrorMemberShip();

	String getGroupName();

	String getName();

	ISample<?> getSample();

	double[] getSampleData();

	boolean isDisplayed();

	void setDisplayed(boolean displayed);

	void setEigenSpace(double[] eigenSpace);

	void setErrorMemberShip(double errorMemberShip);

	void setGroupName(String groupName);

	void setName(String name);

	void setSampleData(double[] sampleData);
}