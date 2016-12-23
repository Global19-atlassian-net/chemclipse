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
package org.eclipse.chemclipse.model.baseline;

public interface IBaselineModel {

	/*
	 * Min and max retention times.
	 */
	int MIN_RETENTION_TIME = 1;
	int MAX_RETENTION_TIME = Integer.MAX_VALUE;

	/**
	 * Adds a baseline to the corresponding chromatogram.<br/>
	 * Set the start and end retention time and respectively the start and
	 * background abundance.<br/>
	 * The method returns immediately if the start retention time is >= the stop
	 * retention time.
	 * If validate is yes, further checks and constraints are performed. If it's no,
	 * the caller must be sure that the baseline segment is in no conflict with other
	 * baseline segments.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 * @param startBackgroundAbundance
	 * @param stopBackgroundAbundance
	 * @param validate
	 */
	void addBaseline(int startRetentionTime, int stopRetentionTime, float startBackgroundAbundance, float stopBackgroundAbundance, boolean validate);

	/**
	 * Remove the baseline between the given retention times.<br/>
	 * * The method returns immediately if the start retention time is >= the
	 * stop retention time.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 */
	void removeBaseline(int startRetentionTime, int stopRetentionTime);

	/**
	 * Remove the baseline totally.
	 */
	void removeBaseline();

	/**
	 * Get the background abundance at a given retention time.<br/>
	 * The retention time is given in milliseconds.<br/>
	 * If the given retention time is out of chromatogram borders, 0 will be
	 * returned.
	 * 
	 * @param retentionTime
	 * @return float
	 */
	float getBackgroundAbundance(int retentionTime);

	/**
	 * Returns a deep copy of the actual baseline model.
	 * 
	 * @return {@link IBaselineModel}
	 */
	IBaselineModel makeDeepCopy();
}
