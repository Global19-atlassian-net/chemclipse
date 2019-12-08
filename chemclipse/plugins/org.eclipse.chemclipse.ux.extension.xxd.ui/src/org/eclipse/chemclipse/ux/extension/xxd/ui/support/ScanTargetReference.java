/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public class ScanTargetReference implements TargetReference {

	private static final NumberFormat FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
	private final IScan scan;
	private final String name;
	private final String id;

	public ScanTargetReference(IScan scan) {
		this.scan = scan;
		name = FORMAT.format(TimeUnit.MILLISECONDS.toMinutes(scan.getRetentionTime()));
		id = String.valueOf(scan.getRetentionTime());
	}

	@Override
	public Set<IIdentificationTarget> getTargets() {

		return scan.getTargets();
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String getID() {

		return id;
	}

	public static <T> List<ScanTargetReference> getReferences(List<T> items, Function<T, IScan> conversionFunction) {

		List<ScanTargetReference> list = new ArrayList<>();
		for(T item : items) {
			IScan scan = conversionFunction.apply(item);
			if(scan != null) {
				list.add(new ScanTargetReference(scan));
			}
		}
		return list;
	}

	public IScan getScan() {

		return scan;
	}
}
