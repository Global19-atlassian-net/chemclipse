/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public abstract class AbstractSamplesVisualization<V extends IVariableVisualization, S extends ISampleVisualization<? extends ISampleData>> implements ISamplesVisualization<V, S>, ISamples<V, S> {

	public <VM extends IVariable, SM extends ISample<? extends ISampleData>> AbstractSamplesVisualization(ISamples<VM, SM> samplesModel) {
		super();
	}
}
