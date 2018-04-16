/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

import java.util.List;

import javafx.collections.ObservableList;

public interface IPcaResults<R extends IPcaResult, V extends IVaribleExtracted> {

	List<double[]> getLoadingVectors();

	double[] getExplainedVariances();

	ObservableList<V> getExtractedVariables();

	ObservableList<R> getPcaResultList();

	IPcaSettings getPcaSettings();

	IPcaModelResult getPcaModelResult();

	void setLoadingVectors(List<double[]> loadingVectors);

	void setExplainedVariances(double[] explainedVariances);

	double[] getCumulativeExplainedVariances();

	void setCumulativeExplainedVariances(double[] cumulativeExplainedVariances);
}