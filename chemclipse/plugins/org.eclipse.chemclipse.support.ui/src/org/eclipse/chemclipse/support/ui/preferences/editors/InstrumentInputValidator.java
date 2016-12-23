/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.editors;

import org.eclipse.chemclipse.support.l10n.Messages;
import org.eclipse.chemclipse.support.messages.ISupportMessages;
import org.eclipse.chemclipse.support.messages.SupportMessages;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.List;

public class InstrumentInputValidator implements IInputValidator {

	private String[] items;

	public InstrumentInputValidator() {
		items = new String[]{};
	}

	public InstrumentInputValidator(List list) {
		if(list != null) {
			items = list.getItems();
		} else {
			items = new String[]{};
		}
	}

	@Override
	public String isValid(String newInstrument) {

		Messages messages = SupportMessages.INSTANCE();
		if(newInstrument.equals("")) {
			return messages.getMessage(ISupportMessages.LABEL_TYPE_IN_INSTRUMENT);
		} else {
			for(String item : items) {
				if(item.equals(newInstrument)) {
					return messages.getMessage(ISupportMessages.LABEL_INSTRUMENT_EXISTS);
				}
			}
		}
		return null;
	}
}
