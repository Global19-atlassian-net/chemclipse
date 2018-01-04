/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider;

import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class QuantitationCompoundTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IQuantitationCompoundMSD && e2 instanceof IQuantitationCompoundMSD) {
			IQuantitationCompoundMSD compound1 = (IQuantitationCompoundMSD)e1;
			IRetentionTimeWindow retentionTimeWindow1 = compound1.getRetentionTimeWindow();
			IRetentionIndexWindow retentionIndexWindow1 = compound1.getRetentionIndexWindow();
			//
			IQuantitationCompoundMSD compound2 = (IQuantitationCompoundMSD)e2;
			IRetentionTimeWindow retentionTimeWindow2 = compound2.getRetentionTimeWindow();
			IRetentionIndexWindow retentionIndexWindow2 = compound2.getRetentionIndexWindow();
			//
			switch(getPropertyIndex()) {
				case 0: // Name
					sortOrder = compound2.getName().compareTo(compound1.getName());
					break;
				case 1: // Chemical Class
					sortOrder = compound2.getChemicalClass().compareTo(compound1.getChemicalClass());
					break;
				case 2: // Concentration Unit
					sortOrder = compound2.getConcentrationUnit().compareTo(compound1.getConcentrationUnit());
					break;
				case 3: // Calibration Method
					sortOrder = compound2.getCalibrationMethod().toString().compareTo(compound1.getCalibrationMethod().toString());
					break;
				case 4: // Cross Zero
					sortOrder = Boolean.valueOf(compound2.isCrossZero()).compareTo(compound1.isCrossZero());
					break;
				case 5: // TIC / XIC
					sortOrder = Boolean.valueOf(compound2.isUseTIC()).compareTo(compound1.isUseTIC());
					break;
				case 6: // Retention Time
					sortOrder = retentionTimeWindow2.getRetentionTime() - retentionTimeWindow1.getRetentionTime();
					break;
				case 7: // Allowed Negative Deviation
					sortOrder = Float.compare(retentionTimeWindow2.getAllowedNegativeDeviation(), retentionTimeWindow1.getAllowedNegativeDeviation());
					break;
				case 8: // Allowed Positive Deviation
					sortOrder = Float.compare(retentionTimeWindow2.getAllowedPositiveDeviation(), retentionTimeWindow1.getAllowedPositiveDeviation());
					break;
				case 9: // Retention Index
					sortOrder = Float.compare(retentionIndexWindow2.getRetentionIndex(), retentionIndexWindow1.getRetentionIndex());
					break;
				case 10: // Allowed Negative Deviation
					sortOrder = Float.compare(retentionIndexWindow2.getAllowedNegativeDeviation(), retentionIndexWindow1.getAllowedNegativeDeviation());
					break;
				case 11: // Allowed Positive Deviation
					sortOrder = Float.compare(retentionIndexWindow2.getAllowedPositiveDeviation(), retentionIndexWindow1.getAllowedPositiveDeviation());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
