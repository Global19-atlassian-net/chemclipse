/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class FloatFieldEditor extends StringFieldEditor {

	private float minValue = -Float.MAX_VALUE;
	private float maxValue = Float.MAX_VALUE;

	public FloatFieldEditor(String name, String labelText, Composite parent) {
		this(name, labelText, -Float.MAX_VALUE, Float.MAX_VALUE, parent);
	}

	public FloatFieldEditor(String name, String labelText, float minValue, float maxValue, Composite parent) {
		super(name, labelText, parent);
		if(minValue >= maxValue) {
			throw new IllegalArgumentException("Invalid min/max values: " + minValue + ", " + maxValue);
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
		getTextControl().addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {

				getPage().setMessage(null, IMessageProvider.INFORMATION);
			}

			@Override
			public void focusGained(FocusEvent e) {

				getPage().setMessage("blablablabla blablablabla blablablabla blablablabla blablablabla blablablabla blablablabla", IMessageProvider.INFORMATION);
			}
		});
	}

	@Override
	protected boolean checkState() {

		Text textControl = getTextControl();
		if(textControl == null) {
			clearErrorMessage();
			return true;
		}
		if(!getTextControl().isEnabled()) {
			clearErrorMessage();
			return true;
		}
		String stringValue = textControl.getText();
		Float value;
		try {
			value = Float.valueOf(stringValue);
			if(value >= minValue && value <= maxValue) {
				clearErrorMessage();
				return true;
			} else {
				setAndShowErrorMessage();
			}
		} catch(NumberFormatException e) {
			setAndShowErrorMessage();
		}
		return false;
	}

	@Override
	protected void doLoad() {

		Text textControl = getTextControl();
		if(textControl != null) {
			Float value = getPreferenceStore().getFloat(getPreferenceName());
			textControl.setText(value.toString());
		}
		super.doLoad();
	}

	@Override
	public boolean isValid() {

		// stupid workaround, since 'refreshValidState()' is only package
		// private
		refreshValidState();
		return !getTextControl().isEnabled() || super.isValid();
	}

	@Override
	protected void refreshValidState() {

		// TODO Auto-generated method stub
		super.refreshValidState();
	}

	@Override
	protected boolean doCheckState() {

		// TODO Auto-generated method stub
		return super.doCheckState();
	}

	@Override
	protected void doLoadDefault() {

		Text textControl = getTextControl();
		if(textControl != null) {
			Float value = getPreferenceStore().getDefaultFloat(getPreferenceName());
			textControl.setText(value.toString());
		}
		valueChanged();
	}

	@Override
	public void setEnabled(boolean enabled, Composite parent) {

		super.setEnabled(enabled, parent);
	}

	@Override
	protected void doStore() {

		Text textControl = getTextControl();
		if(textControl != null && textControl.isEnabled()) {
			float value = Float.valueOf(textControl.getText());
			getPreferenceStore().setValue(getPreferenceName(), value);
		}
	}

	private void setAndShowErrorMessage() {

		showErrorMessage("Allowed range (MIN = " + minValue + ")(MAX = " + maxValue + ")");
	}
}
