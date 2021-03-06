/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;

import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences.DialogBehavior;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class SettingsPreferencesPage<T> extends WizardPage {

	private boolean isDontAskAgain;
	private boolean isUseSystemDefaults;
	//
	private Button buttonDefault;
	private Button buttonUser;
	private SettingsUI<?> settingsUI;
	//
	private String jsonSettings;
	private final ProcessorPreferences<T> preferences;

	public SettingsPreferencesPage(ProcessorPreferences<T> preferences) {

		super(SettingsPreferencesPage.class.getName());
		this.preferences = preferences;
	}

	@Override
	public void createControl(Composite parent) {

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setLayout(new GridLayout(1, true));
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		Composite control = createOptionSection(scrolledComposite);
		//
		scrolledComposite.setMinSize(control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setContent(control);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setAlwaysShowScrollBars(false);
		//
		setControl(scrolledComposite);
	}

	public boolean getIsDontAskAgainEdited() {

		return isDontAskAgain;
	}

	public String getSettingsEdited() throws IOException {

		return jsonSettings;
	}

	public boolean getIsUseSystemDefaultsEdited() {

		return isUseSystemDefaults;
	}

	private Composite createOptionSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createSystemOptions(composite);
		createUserOptions(composite);
		//
		Listener validationListener = createValidationListener();
		SelectionListener selectionListener = createSelectionListener(validationListener);
		addButtonSettings(composite, validationListener, selectionListener);
		//
		return composite;
	}

	private void createSystemOptions(Composite parent) {

		buttonDefault = createButtonDefault(parent);
		createLabelSeparator(parent);
	}

	private Button createButtonDefault(Composite parent) {

		Button button = new Button(parent, SWT.RADIO);
		button.setText("Use System Options");
		if(preferences.requiresUserSettings()) {
			button.setEnabled(false);
			button.setToolTipText("This processor does not offer system options or they are not applicable at the moment.");
		}
		//
		return button;
	}

	private void createLabelSeparator(Composite parent) {

		Label label = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createUserOptions(Composite parent) {

		buttonUser = createButtonUser(parent);
		settingsUI = createSettingsUI(parent);
	}

	private Button createButtonUser(Composite parent) {

		Button button = new Button(parent, SWT.RADIO);
		button.setText("Use Specific Options");
		//
		return button;
	}

	private SettingsUI<?> createSettingsUI(Composite parent) {

		SettingsUI<?> settingsUI = null;
		//
		try {
			settingsUI = new SettingsUI<>(parent, preferences);
			settingsUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		} catch(IOException e1) {
			throw new RuntimeException("Reading the settings failed.", e1);
		}
		//
		return settingsUI;
	}

	private Listener createValidationListener() {

		Listener validationListener = new Listener() {

			@Override
			public void handleEvent(Event event) {

				jsonSettings = null;
				if(buttonUser.getSelection()) {
					IStatus validate = settingsUI.getControl().validate();
					if(validate.isOK()) {
						setErrorMessage(null);
						setPageComplete(true);
					} else {
						setErrorMessage(validate.getMessage());
						setPageComplete(false);
					}
				} else {
					setErrorMessage(null);
					setPageComplete(true);
				}
				try {
					jsonSettings = settingsUI.getControl().getSettings();
				} catch(Exception e) {
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, getClass().getName(), "Error while fetching the settings.", e));
					setErrorMessage(e.toString());
					setPageComplete(false);
				}
			}
		};
		//
		return validationListener;
	}

	private SelectionListener createSelectionListener(Listener validationListener) {

		SelectionListener selectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				settingsUI.setEnabled(buttonUser.getSelection());
				validationListener.handleEvent(null);
				isUseSystemDefaults = buttonDefault.getSelection();
			}
		};
		//
		return selectionListener;
	}

	private void addButtonSettings(Composite parent, Listener validationListener, SelectionListener selectionListener) {

		buttonDefault.addSelectionListener(selectionListener);
		buttonUser.addSelectionListener(selectionListener);
		//
		if(preferences.getDialogBehaviour() == DialogBehavior.NONE) {
			isDontAskAgain = false;
		} else {
			Button buttonDontAskAgain = new Button(parent, SWT.CHECK);
			buttonDontAskAgain.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false));
			buttonDontAskAgain.setText("Remember my decision and don't ask again.");
			buttonDontAskAgain.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					isDontAskAgain = buttonDontAskAgain.getSelection();
				}
			});
			buttonDontAskAgain.setSelection(isDontAskAgain = !(preferences.getDialogBehaviour() == DialogBehavior.SHOW));
		}
		//
		if(preferences.isUseSystemDefaults() && !preferences.requiresUserSettings()) {
			buttonDefault.setSelection(true);
		} else {
			buttonUser.setSelection(true);
		}
		//
		selectionListener.widgetSelected(null);
		settingsUI.getControl().addChangeListener(validationListener);
	}
}
