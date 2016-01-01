/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

/**
 * More informations about the class structure of mass spectra are stored in {@link IScanMSD}.
 * 
 * @author eselmeister
 */
public interface ICombinedMassSpectrum extends IScanMSD {

	// ---------------Retention Time
	/**
	 * Returns the start retention time in milliseconds.
	 * 
	 * @return int
	 */
	int getStartRetentionTime();

	/**
	 * Sets the start retention time in milliseconds.<br/>
	 * Only values >= 0 are allowed.
	 * 
	 * @param startRetentionTime
	 */
	void setStartRetentionTime(int startRetentionTime);

	/**
	 * Returns the stop retention time in milliseconds.
	 * 
	 * @return int
	 */
	int getStopRetentionTime();

	/**
	 * Sets the stop retention time in milliseconds.<br/>
	 * Only values >= 0 are allowed.
	 * 
	 * @param stopRetentionTime
	 */
	void setStopRetentionTime(int stopRetentionTime);

	// ---------------Retention Index
	/**
	 * Returns the start retention index.
	 * 
	 * @return float
	 */
	float getStartRetentionIndex();

	/**
	 * Sets the start retention index.<br/>
	 * Only values >= 0 are allowed.
	 * 
	 * @param startRetentionIndex
	 */
	void setStartRetentionIndex(float startRetentionIndex);

	/**
	 * Returns the stop retention index.
	 * 
	 * @return float
	 */
	float getStopRetentionIndex();

	/**
	 * Sets the stop retention index.<br/>
	 * Only values >= 0 are allowed.
	 * 
	 * @param stopRetentionIndex
	 */
	void setStopRetentionIndex(float stopRetentionIndex);

	// ---------------Scans
	/**
	 * Returns the start scan.
	 * 
	 * @return int
	 */
	int getStartScan();

	/**
	 * Sets the start scan.<br/>
	 * Only values >= 0 are allowed.
	 * 
	 * @param startScan
	 */
	void setStartScan(int startScan);

	/**
	 * Returns the stop scan.
	 * 
	 * @return int
	 */
	int getStopScan();

	/**
	 * Sets the stop scan.<br/>
	 * Only values >= 0 are allowed.
	 * 
	 * @param stopScan
	 */
	void setStopScan(int stopScan);
}
