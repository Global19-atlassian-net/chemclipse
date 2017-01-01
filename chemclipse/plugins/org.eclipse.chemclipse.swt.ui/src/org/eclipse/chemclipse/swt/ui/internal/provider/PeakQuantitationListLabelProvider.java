/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.support.PeakQuantitation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class PeakQuantitationListLabelProvider extends AbstractChemClipseLabelProvider {

	public PeakQuantitationListLabelProvider() {
		super("0.0##");
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else {
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof PeakQuantitation) {
			PeakQuantitation peakQuantitationEntry = (PeakQuantitation)element;
			switch(columnIndex) {
				case 0:
					text = decimalFormat.format(peakQuantitationEntry.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 1:
					text = decimalFormat.format(peakQuantitationEntry.getIntegratedArea());
					break;
				default:
					int index = columnIndex - 2;
					if(index < peakQuantitationEntry.getConcentrations().size()) {
						text = decimalFormat.format(peakQuantitationEntry.getConcentrations().get(index));
					} else {
						text = "";
					}
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImage.SIZE_16x16);
	}
}
