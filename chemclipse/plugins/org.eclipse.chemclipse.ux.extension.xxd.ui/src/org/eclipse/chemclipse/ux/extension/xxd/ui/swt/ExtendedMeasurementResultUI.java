/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - improve data handling
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createColumn;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createTreeTable;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinition;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeColumn;

public class ExtendedMeasurementResultUI extends Composite {

	private Label labelChromatogramInfo;
	private Label labelMeasurementResultInfo;
	private Composite toolbarChromatogramInfo;
	private Composite toolbarMeasurementResultInfo;
	private ComboViewer comboMeasurementResults;
	private TreeViewer resultTable;
	private ProxySelectionChangedListener selectionChangedListener;
	private ProxyTableLabelProvider labelProvider;
	private ProxyStructuredContentProvider contentProvider;
	private IMeasurementResult<?> lastResult;

	public ExtendedMeasurementResultUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	/**
	 * Update the UI with the given data
	 * 
	 * @param results
	 *            the results to show, must not be <code>null</code>
	 * @param infoLabel
	 *            the infolabel to show must not be <code>null</code>
	 */
	public void update(Collection<IMeasurementResult<?>> results, String infoLabel) {

		if(!labelChromatogramInfo.isDisposed()) {
			labelChromatogramInfo.setText(infoLabel);
		}
		IStructuredSelection selection = comboMeasurementResults.getStructuredSelection();
		comboMeasurementResults.setInput(results);
		comboMeasurementResults.refresh();
		IMeasurementResult<?> selectedElement = (IMeasurementResult<?>)selection.getFirstElement();
		if(!results.contains(selectedElement)) {
			selectedElement = null;
		}
		if(selectedElement == null && results.size() == 1) {
			selectedElement = results.iterator().next();
		}
		if(selectedElement == null) {
			comboMeasurementResults.setSelection(StructuredSelection.EMPTY);
		} else {
			comboMeasurementResults.setSelection(new StructuredSelection(selectedElement));
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		toolbarChromatogramInfo = createToolbarChromatogramInfo(this);
		toolbarMeasurementResultInfo = createToolbarMeasurementResultInfo(this);
		resultTable = createResultSection(this);
		//
		PartSupport.setCompositeVisibility(toolbarChromatogramInfo, true);
		PartSupport.setCompositeVisibility(toolbarMeasurementResultInfo, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleChromatogramToolbarInfo(composite);
		comboMeasurementResults = createResultCombo(composite);
		createButtonToggleMeasurementResultToolbarInfo(composite);
	}

	public ComboViewer getComboMeasurementResults() {

		return comboMeasurementResults;
	}

	private Composite createToolbarChromatogramInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelChromatogramInfo = new Label(composite, SWT.NONE);
		labelChromatogramInfo.setText("");
		labelChromatogramInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarMeasurementResultInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelMeasurementResultInfo = new Label(composite, SWT.NONE);
		labelMeasurementResultInfo.setText("");
		labelMeasurementResultInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private TreeViewer createResultSection(Composite parent) {

		TreeViewer treeTable = createTreeTable(parent, true);
		treeTable.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		contentProvider = new ProxyStructuredContentProvider();
		treeTable.setContentProvider(contentProvider);
		labelProvider = new ProxyTableLabelProvider();
		treeTable.setLabelProvider(labelProvider);
		selectionChangedListener = new ProxySelectionChangedListener();
		treeTable.addSelectionChangedListener(selectionChangedListener);
		return treeTable;
	}

	private Button createButtonToggleChromatogramToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarChromatogramInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleMeasurementResultToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle measurement results description toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarMeasurementResultInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private ComboViewer createResultCombo(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.PUSH);
		comboViewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {

				if(element instanceof IMeasurementResult<?>) {
					return ((IMeasurementResult<?>)element).isVisible();
				}
				return true;
			}
		});
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMeasurementResult<?>) {
					IMeasurementResult<?> measurementResult = (IMeasurementResult<?>)element;
					return measurementResult.getName();
				}
				return super.getText(element);
			}
		});
		combo.setToolTipText("Show the available measurement results.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		combo.setLayoutData(gridData);
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IMeasurementResult<?>) {
					updateMeasurementResult((IMeasurementResult<?>)object);
				} else {
					updateMeasurementResult(null);
				}
			}
		});
		return comboViewer;
	}

	private void updateMeasurementResult(IMeasurementResult<?> measurementResult) {

		updateLabel(measurementResult);
		if(lastResult != measurementResult) {
			contentProvider.setProxy(adaptTo(measurementResult, IStructuredContentProvider.class));
			selectionChangedListener.setProxy(adaptTo(measurementResult, ISelectionChangedListener.class));
			TreeColumn[] columns = resultTable.getTree().getColumns();
			for(TreeColumn column : columns) {
				column.dispose();
			}
			ColumnDefinitionProvider columnDefinitionProvider = adaptTo(measurementResult, ColumnDefinitionProvider.class);
			if(columnDefinitionProvider != null) {
				for(ColumnDefinition<?, ?> definition : columnDefinitionProvider.getColumnDefinitions()) {
					createColumn(resultTable, definition);
				}
			}
			ITableLabelProvider tableLabelProvider = adaptTo(measurementResult, ITableLabelProvider.class);
			labelProvider.setProxy(tableLabelProvider);
			if(tableLabelProvider != null) {
				resultTable.setLabelProvider(tableLabelProvider);
			}
			resultTable.setInput(measurementResult);
		}
		this.lastResult = measurementResult;
		resultTable.refresh();
	}

	private static <T> T adaptTo(IMeasurementResult<?> measurementResult, Class<T> desiredType) {

		if(measurementResult == null) {
			return null;
		}
		T resultAdapted = Adapters.adapt(measurementResult, desiredType);
		if(resultAdapted != null) {
			return resultAdapted;
		} else {
			return Adapters.adapt(measurementResult.getResult(), desiredType);
		}
	}

	public void updateLabel(IMeasurementResult<?> measurementResult) {

		if(!labelMeasurementResultInfo.isDisposed()) {
			if(measurementResult != null) {
				labelMeasurementResultInfo.setText(measurementResult.getDescription());
			} else {
				labelMeasurementResultInfo.setText("");
			}
		}
	}
}
