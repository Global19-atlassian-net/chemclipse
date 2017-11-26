/*******************************************************************************
 * Copyright (c) 2017 jan.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public class CenteringMedian extends AbstractCentering {

	@Override
	public String getDescription() {

		return "";
	}

	@Override
	public String getName() {

		return "Median Centering";
	}

	@Override
	public <V extends IVariable, S extends ISample<? extends ISampleData>> void process(ISamples<V, S> samples) {

		List<V> variables = samples.getVariables();
		for(int i = 0; i < variables.size(); i++) {
			final double value = getCenteringValue(samples.getSampleList(), i, 2);
			final int j = i;
			samples.getSampleList().stream().forEach(s -> {
				ISampleData data = s.getSampleData().get(j);
				if(!data.isEmpty()) {
					data.setModifiedData(data.getModifiedData() - value);
				}
			});
		}
	}
}
