/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IDataExtraction;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class MainPropertiesScansInputWizardPage extends WizardPage {

	private DataBindingContext dbc = new DataBindingContext();
	private int extractionType;
	private IObservableValue<Integer> maximalNumberScans = new WritableValue<>();
	private IObservableValue<Integer> numerOfComponents = new WritableValue<>();
	private IObservableValue<Double> retentionTimeWindow = new WritableValue<>();
	private boolean useDefoultProperties;

	protected MainPropertiesScansInputWizardPage(String pageName) {
		super(pageName);
		setTitle("Set Main Parameters");
		numerOfComponents.setValue(3);
		retentionTimeWindow.setValue(1.0);
		maximalNumberScans.setValue(5000);
		extractionType = IDataExtraction.CLOSEST_SCAN;
		useDefoultProperties = true;
	}

	@Override
	public void createControl(Composite parent) {

		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, true));
		Button button = new Button(composite, SWT.CHECK);
		button.setText("Use default properties if it is possible (chromatogram have same start retention time and same scan interval)");
		button.setSelection(true);
		button.addListener(SWT.Selection, e -> useDefoultProperties = ((Button)e.widget).getSelection());
		Label label = new Label(composite, SWT.None);
		label.setText("Retention Time Windows will be set according to scan interval");
		button = new Button(composite, SWT.RADIO);
		button.setText("Select the closest scan");
		button.setSelection(true);
		button.addListener(SWT.Selection, e -> extractionType = IDataExtraction.CLOSEST_SCAN);
		button = new Button(composite, SWT.RADIO);
		button.setText("Interpolate scan");
		button.addListener(SWT.Selection, e -> extractionType = IDataExtraction.LINEAR_INTERPOLATION_SCAN);
		label = new Label(composite, SWT.None);
		label.setText("Retention Time Windows (s)");
		Text text = new Text(composite, SWT.BORDER);
		UpdateValueStrategy targetToModel = UpdateValueStrategy.create(IConverter.create(String.class, Double.class, o1 -> {
			try {
				return Double.parseDouble((String)o1);
			} catch(NumberFormatException e) {
			}
			return null;
		}));
		targetToModel.setBeforeSetValidator(o1 -> {
			if(o1 instanceof Double) {
				Double i = (Double)o1;
				if(i > 0) {
					return ValidationStatus.ok();
				}
			}
			return ValidationStatus.error("Warning The value must be positive value");
		});
		UpdateValueStrategy modelToTarget = UpdateValueStrategy.create(IConverter.create(Double.class, String.class, o1 -> Double.toString(((Double)o1))));
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), retentionTimeWindow, targetToModel, modelToTarget);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(composite, SWT.None);
		label.setText("Number of principal components");
		Spinner spinner = new Spinner(composite, SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spinner.setMinimum(3);
		dbc.bindValue(WidgetProperties.selection().observe(spinner), numerOfComponents);
		label = new Label(composite, SWT.None);
		label.setText("Maximal number of scans");
		spinner = new Spinner(composite, SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spinner.setMinimum(1);
		spinner.setIncrement(1000);
		spinner.setMaximum(Integer.MAX_VALUE);
		dbc.bindValue(WidgetProperties.selection().observe(spinner), maximalNumberScans);
		setControl(composite);
	}

	public int getExtractionType() {

		return extractionType;
	}

	public int getMaximalNumberScans() {

		return maximalNumberScans.getValue();
	}

	public int getNumerOfComponents() {

		return numerOfComponents.getValue();
	}

	public int getRetentionTimeWindow() {

		return (int)Math.round(retentionTimeWindow.getValue() * 1000);
	}

	public boolean isUseDefoultProperties() {

		return useDefoultProperties;
	}
}
