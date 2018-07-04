/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.chemclipse.support.settings.IonsSelectionSettingProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class SupplierFilterSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterSettings {

	@JsonProperty(value = "Ions To Remove", defaultValue = "18;28;84;207")
	@JsonPropertyDescription(value = "List the ions to remove, separated by a white space.")
	@IonsSelectionSettingProperty
	private String ionsToRemove = "18;28;84;207";
	@JsonProperty(value = "Ions To Preserve", defaultValue = "103;104")
	@JsonPropertyDescription(value = "List the ions to preserve, separated by a white space.")
	@IonsSelectionSettingProperty
	private String ionsToPreserve = "103;104";
	@JsonProperty(value = "Adjust Threshold Transitions", defaultValue = "true")
	@JsonPropertyDescription(value = "Adjust zero threshold transitions.")
	private boolean adjustThresholdTransitions = true;
	@JsonProperty(value = "Number Used Ions For Coefficient", defaultValue = "1")
	@JsonPropertyDescription(value = "The number of used ions for coefficient calculation.")
	@IntSettingsProperty(minValue = PreferenceSupplier.NUMBER_OF_USE_IONS_FOR_COEFFICIENT_MIN, maxValue = PreferenceSupplier.NUMBER_OF_USE_IONS_FOR_COEFFICIENT_MAX)
	private int numberOfUsedIonsForCoefficient = 1;
	@JsonProperty(value = "Segment Width", defaultValue = "13")
	@IntSettingsProperty(minValue = PreferenceSupplier.SEGMENT_WIDTH_MIN, maxValue = PreferenceSupplier.SEGMENT_WIDTH_MAX, validation = Validation.ODD_NUMBER)
	private int segmentWidth = 13;

	@Override
	public String getIonsToRemove() {

		return ionsToRemove;
	}

	@Override
	public void setIonsToRemove(String ionsToRemove) {

		this.ionsToRemove = ionsToRemove;
	}

	@Override
	public String getIonsToPreserve() {

		return ionsToPreserve;
	}

	@Override
	public void setIonsToPreserve(String ionsToPreserve) {

		this.ionsToPreserve = ionsToPreserve;
	}

	@Override
	public boolean isAdjustThresholdTransitions() {

		return adjustThresholdTransitions;
	}

	@Override
	public void setAdjustThresholdTransitions(boolean adjustThresholdTransitions) {

		this.adjustThresholdTransitions = adjustThresholdTransitions;
	}

	@Override
	public int getNumberOfUsedIonsForCoefficient() {

		return numberOfUsedIonsForCoefficient;
	}

	@Override
	public void setNumberOfUsedIonsForCoefficient(int numberOfUsedIonsForCoefficient) {

		if(numberOfUsedIonsForCoefficient <= 0) {
			this.numberOfUsedIonsForCoefficient = 1;
		} else {
			this.numberOfUsedIonsForCoefficient = numberOfUsedIonsForCoefficient;
		}
	}

	@Override
	public int getSegmentWidth() {

		return segmentWidth;
	}

	@Override
	public void setSegmentWidth(int segmentWidth) {

		this.segmentWidth = segmentWidth;
	}
}
