/*******************************************************************************
 * Copyright (c) 2017 Jan Holy
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;

public class RetentionTime extends AbstractVariable implements IRetentionTime {

	public static List<RetentionTime> create(List<Integer> retentionTimes) {

		List<RetentionTime> retentionTimesList = new ArrayList<>();
		for(int i = 0; i < retentionTimes.size(); i++) {
			retentionTimesList.add(new RetentionTime(retentionTimes.get(i)));
		}
		return retentionTimesList;
	}

	private NumberFormat nf = NumberFormat.getInstance(Locale.US);
	private int retentionTime;

	public RetentionTime(int retentioTime) {
		super();
		this.retentionTime = retentioTime;
		setValue(convertValue());
		setType(IRetentionTime.TYPE);
	}

	public RetentionTime(int retentionTime, String description) {
		this(retentionTime);
		setDescription(description);
	}

	@Override
	public int compareTo(IVariable o) {

		if(o instanceof IRetentionTime) {
			IRetentionTime retentionTime = (IRetentionTime)o;
			return Integer.compare(getRetentionTime(), retentionTime.getRetentionTime());
		}
		return 0;
	}

	private String convertValue() {

		return nf.format(getRetentionTimeMinutes());
	}

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public double getRetentionTimeMinutes() {

		return retentionTime / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
	}

	@Override
	public void setRetentioTime(int retentionTime) {

		this.retentionTime = retentionTime;
		setValue(convertValue());
	}
}
