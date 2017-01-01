/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.statistics.model;

public interface IUnivariateStatistics extends IStatistics {

	int getSampleSize();

	double getMean();

	double getMedian();

	double getVariance();

	double getRelativeStandardDeviation();

	double getStandardDeviation();

	double[] getValues();
}