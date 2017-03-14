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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakTableTargetComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private TargetExtendedComparator targetExtendedComparator;

	public PeakTableTargetComparator() {
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IPeak && e2 instanceof IPeak) {
			/*
			 * Peak 1
			 */
			IPeak peak1 = (IPeak)e1;
			IPeakModel peakModel1 = peak1.getPeakModel();
			IScan peakMaximum1 = peakModel1.getPeakMaximum();
			//
			List<IPeakTarget> peakTargets1 = null;
			if(peak1 instanceof IPeakMSD) {
				IPeakMSD peakMSD = (IPeakMSD)peak1;
				peakTargets1 = new ArrayList<>(peakMSD.getTargets());
			} else if(peak1 instanceof IPeakCSD) {
				IPeakCSD peakCSD = (IPeakCSD)peak1;
				peakTargets1 = new ArrayList<>(peakCSD.getTargets());
			}
			//
			String peakTarget1 = "";
			if(peakTargets1 != null && peakTargets1.size() > 0) {
				Collections.sort(peakTargets1, targetExtendedComparator);
				peakTarget1 = peakTargets1.get(0).getLibraryInformation().getName();
			}
			/*
			 * Peak 2
			 */
			IPeak peak2 = (IPeak)e2;
			IPeakModel peakModel2 = peak2.getPeakModel();
			IScan peakMaximum2 = peakModel2.getPeakMaximum();
			//
			List<IPeakTarget> peakTargets2 = null;
			if(peak1 instanceof IPeakMSD) {
				IPeakMSD peakMSD = (IPeakMSD)peak2;
				peakTargets2 = new ArrayList<>(peakMSD.getTargets());
			} else if(peak2 instanceof IPeakCSD) {
				IPeakCSD peakCSD = (IPeakCSD)peak2;
				peakTargets2 = new ArrayList<>(peakCSD.getTargets());
			}
			//
			String peakTarget2 = "";
			if(peakTargets2 != null && peakTargets2.size() > 0) {
				Collections.sort(peakTargets2, targetExtendedComparator);
				peakTarget2 = peakTargets2.get(0).getLibraryInformation().getName();
			}
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Integer.compare(peakMaximum2.getRetentionTime(), peakMaximum1.getRetentionTime());
					break;
				case 1:
					sortOrder = peakTarget2.compareTo(peakTarget1);
					break;
				case 2:
					if(peak1 instanceof IChromatogramPeakMSD && peak2 instanceof IChromatogramPeakMSD) {
						sortOrder = Float.compare(((IChromatogramPeakMSD)peak2).getSignalToNoiseRatio(), ((IChromatogramPeakMSD)peak1).getSignalToNoiseRatio());
					} else if(peak1 instanceof IChromatogramPeakCSD && peak2 instanceof IChromatogramPeakCSD) {
						sortOrder = Float.compare(((IChromatogramPeakCSD)peak2).getSignalToNoiseRatio(), ((IChromatogramPeakCSD)peak1).getSignalToNoiseRatio());
					}
					break;
				case 3:
					sortOrder = Double.compare(peak2.getIntegratedArea(), peak1.getIntegratedArea());
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
