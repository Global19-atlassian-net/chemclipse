/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters;

import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public interface IFilter extends IPreprocessing {

	static String getErrorMessage(String messagge) {

		return "Error: " + messagge;
	}

	static String getNumberSelectedRow(Collection<Boolean> selection) {

		long countSelectedData = selection.stream().filter(b -> b).count();
		return Long.toString(countSelectedData);
	}

	static <V extends IVariable> boolean isRetentionTimes(List<V> variables) {

		return variables.stream().allMatch(v -> (v instanceof IRetentionTime));
	}

	<V extends IVariable, S extends ISample<? extends ISampleData>> List<Boolean> filter(ISamples<V, S> samples);

	String getSelectionResult();

	@Override
	boolean isOnlySelected();

	@Override
	default <V extends IVariable, S extends ISample<? extends ISampleData>> void process(ISamples<V, S> samples) {

		List<Boolean> result = filter(samples);
		List<V> variables = samples.getVariables();
		for(int j = 0; j < result.size(); j++) {
			variables.get(j).setSelected(variables.get(j).isSelected() && result.get(j));
		}
	}

	@Override
	void setOnlySelected(boolean onlySelected);
}
