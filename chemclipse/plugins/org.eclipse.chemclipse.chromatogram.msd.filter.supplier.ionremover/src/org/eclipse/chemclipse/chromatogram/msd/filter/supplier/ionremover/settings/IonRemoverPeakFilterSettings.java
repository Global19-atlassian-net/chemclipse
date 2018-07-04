/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractPeakFilterSettings;
import org.eclipse.chemclipse.support.settings.IonsSelectionSettingProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class IonRemoverPeakFilterSettings extends AbstractPeakFilterSettings implements IIonRemoverPeakFilterSettings {

	@JsonProperty(value = "Ions To Remove", defaultValue = "18;28;84;207")
	@JsonPropertyDescription(value = "List the ions to remove, separated by a semicolon.")
	@IonsSelectionSettingProperty
	private String ionsToRemove = "18;28;84;207";

	@Override
	public String getIonsToRemove() {

		return ionsToRemove;
	}

	@Override
	public void setIonsToRemove(String ionsToRemove) {

		this.ionsToRemove = ionsToRemove;
	}
}
