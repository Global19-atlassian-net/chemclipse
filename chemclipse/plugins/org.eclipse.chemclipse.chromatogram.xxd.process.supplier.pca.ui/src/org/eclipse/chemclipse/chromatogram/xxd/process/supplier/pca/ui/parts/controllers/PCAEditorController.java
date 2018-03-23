/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.controllers;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IDataExtraction;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.PcaInputRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.IPcaInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaChromatogramsMSDInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaPeaksInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.Coloring;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.SamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

public class PCAEditorController {

	private ListChangeListener<ISample<? extends ISampleData>> actualSelectionChangeListener;
	@FXML // fx:id="cColor"
	private TableColumn<ISampleVisualization<? extends ISampleData>, Integer> cColor; // Value injected by FXMLLoader
	@FXML // fx:id="cGroupNames"
	private TableColumn<ISampleVisualization<? extends ISampleData>, String> cGroupNames; // Value injected by FXMLLoader
	@FXML
	private Label cLabelNumblerSelectedSamples;
	@FXML // fx:id="cSelections"
	private TableColumn<ISampleVisualization<? extends ISampleData>, Boolean> cSelections; // Value injected by FXMLLoader
	@FXML // fx:id="cTableSamples"
	private TableView<ISampleVisualization<? extends ISampleData>> cTableSamples; // Value injected by FXMLLoader
	private Consumer<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>>> inputSamples;
	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;
	private ListChangeListener<ISample<? extends ISampleData>> sampleChangeSelectionListener;
	private Optional<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>>> samples;

	public PCAEditorController() {
		samples = Optional.empty();
		sampleChangeSelectionListener = new ListChangeListener<ISample<? extends ISampleData>>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ISample<? extends ISampleData>> c) {

				updateNumerSeletedSamples();
			}
		};
		actualSelectionChangeListener = new ListChangeListener<ISample<? extends ISampleData>>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ISample<? extends ISampleData>> c) {

				if(samples.isPresent() && SelectionManagerSamples.getInstance().getSelection().contains(samples.get())) {
					ObservableList<ISample<? extends ISampleData>> selection = SelectionManagerSample.getInstance().getSelection();
					if(!selection.isEmpty()) {
						ISample<? extends ISampleData> s = selection.get(0);
						seletedSample((ISampleVisualization<? extends ISampleData>)s);
					} else {
						removeSelectedSample();
					}
				}
			}
		};
	}

	public Optional<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>>> getSamples() {

		return samples;
	}

	public ISampleVisualization<? extends ISampleData> getSelectedSamples() {

		return cTableSamples.getSelectionModel().getSelectedItem();
	}

	@FXML
	void handlerChangeGroupName(TableColumn.CellEditEvent<ISampleVisualization<? extends ISampleData>, String> event) {

		String newGroupName = event.getNewValue();
		String oldGroupName = event.getOldValue();
		ISampleVisualization<? extends ISampleData> sample = event.getRowValue();
		if(newGroupName != null) {
			newGroupName = newGroupName.trim();
			if(!newGroupName.equals(oldGroupName)) {
				if(!newGroupName.isEmpty()) {
					sample.setGroupName(newGroupName);
				} else {
					sample.setGroupName(null);
				}
			}
		} else {
			if(newGroupName != oldGroupName) {
				sample.setGroupName(newGroupName);
			}
		}
		if(samples.isPresent()) {
			Set<String> groupNames = samples.get().getSampleList().stream().map(s -> s.getGroupName()).collect(Collectors.toSet());
			Map<String, Integer> colors = Coloring.getColorsForGroup(groupNames);
			samples.get().getSampleList().forEach(s -> s.setColor(colors.get(s.getGroupName())));
		}
	}

	@FXML
	void handlerDeselectAll(ActionEvent event) {

		if(samples.isPresent()) {
			samples.get().getSampleList().forEach(s -> s.setSelected(false));
		}
		// cTableSamples.refresh();
	}

	@FXML
	void handlerKeyPress(KeyEvent event) {

		if(event.getCode() == KeyCode.ESCAPE) {
			cTableSamples.getSelectionModel().clearSelection();
			SelectionManagerSample.getInstance().getSelection().clear();
		}
	}

	@FXML
	void handlerLoadPeaks(ActionEvent event) {

		try {
			openWizardPcaInput(new PcaPeaksInputWizard());
		} catch(InvocationTargetException | InterruptedException e) {
		}
	}

	@FXML
	void handlerLoadScans(ActionEvent event) {

		try {
			openWizardPcaInput(new PcaChromatogramsMSDInputWizard());
		} catch(InvocationTargetException | InterruptedException e) {
		}
	}

	@FXML
	void handlerSelectAll(ActionEvent event) {

		if(samples.isPresent()) {
			samples.get().getSampleList().forEach(s -> s.setSelected(true));
		}
		// cTableSamples.refresh();
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {

		assert cTableSamples != null : "fx:id=\"cTableSamples\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert cSelections != null : "fx:id=\"cSelections\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert cColor != null : "fx:id=\"cColor\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		cSelections.setCellFactory(new Callback<TableColumn<ISampleVisualization<? extends ISampleData>, Boolean>, //
				TableCell<ISampleVisualization<? extends ISampleData>, Boolean>>() {

			@Override
			public TableCell<ISampleVisualization<? extends ISampleData>, Boolean> call(TableColumn<ISampleVisualization<? extends ISampleData>, Boolean> p) {

				CheckBoxTableCell<ISampleVisualization<? extends ISampleData>, Boolean> cell = new CheckBoxTableCell<ISampleVisualization<? extends ISampleData>, Boolean>();
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});
		cColor.setCellFactory(param -> {
			final TableCell<ISampleVisualization<? extends ISampleData>, Integer> cell = new TableCell<ISampleVisualization<? extends ISampleData>, Integer>() {

				final Rectangle r = new Rectangle(20, 20);

				@Override
				public void updateItem(Integer item, boolean empty) {

					super.updateItem(item, empty);
					if(empty) {
						setGraphic(null);
						setText(null);
					} else {
						Color c = PcaColorGroup.getSampleColorFX(item);
						if(c != null) {
							r.setFill(c);
							setGraphic(r);
							setText(null);
						}
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		cTableSamples.setRowFactory(new Callback<TableView<ISampleVisualization<? extends ISampleData>>, TableRow<ISampleVisualization<? extends ISampleData>>>() {

			@Override
			public TableRow<ISampleVisualization<? extends ISampleData>> call(TableView<ISampleVisualization<? extends ISampleData>> tableView2) {

				final TableRow<ISampleVisualization<? extends ISampleData>> row = new TableRow<>();
				row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						event.getTarget();
						if(event.getClickCount() == 1) {
							final int index = row.getIndex();
							if(index < 0 || index >= cTableSamples.getItems().size()) {
								cTableSamples.getSelectionModel().clearSelection();
								SelectionManagerSample.getInstance().getSelection().clear();
								event.consume();
							}
						}
					}
				});
				return row;
			}
		});
		cTableSamples.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<ISampleVisualization<? extends ISampleData>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISampleVisualization<? extends ISampleData>> c) {

				List<? extends ISampleVisualization<? extends ISampleData>> samples = c.getList();
				if(!samples.isEmpty()) {
					ISampleVisualization<? extends ISampleData> s = samples.get(0);
					if(!SelectionManagerSample.getInstance().getSelection().contains(s)) {
						SelectionManagerSample.getInstance().getSelection().setAll(s);
					}
				}
			}
		});
		SelectionManagerSample.getInstance().getSelection().addListener(actualSelectionChangeListener);
		updateNumerSeletedSamples();
	}

	private int openWizardPcaInput(IPcaInputWizard wizard) throws InvocationTargetException, InterruptedException {

		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getDefault().getActiveShell(), wizard);
		int status = wizardDialog.open();
		if(status == Window.OK) {
			PcaFiltrationData pcaFiltrationData = wizard.getPcaFiltrationData();
			PcaPreprocessingData pcaPreprocessingData = wizard.getPcaPreprocessingData();
			IDataExtraction pcaExtractionData = wizard.getPcaExtractionData();
			/*
			 * Run the process.
			 */
			PcaInputRunnable runnable = new PcaInputRunnable(pcaExtractionData, pcaFiltrationData, pcaPreprocessingData);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			/*
			 * Calculate the results and show the score plot page.
			 */
			monitor.run(true, true, runnable);
			setSamples(new SamplesVisualization(runnable.getSamples()));
		}
		return status;
	}

	public void preDestroy() {

		setSamples(null);
		SelectionManagerSample.getInstance().getSelection().remove(actualSelectionChangeListener);
	}

	public void removeSelectedSample() {

		cTableSamples.getSelectionModel().clearSelection();
	}

	public void seletedSample(ISampleVisualization<? extends ISampleData> sample) {

		if(samples.isPresent()) {
			Optional<? extends ISampleVisualization<? extends ISampleData>> sampleVisalization = samples.get().getSampleList().stream().filter(s -> s == sample).findAny();
			if(sampleVisalization.isPresent()) {
				if(!cTableSamples.getSelectionModel().getSelectedItems().contains(sampleVisalization.get())) {
					cTableSamples.getSelectionModel().select(sampleVisalization.get());
					cTableSamples.scrollTo(sampleVisalization.get());
				}
			}
		}
	}

	private void setSamples(ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>> newSamples) {

		/*
		 * Set samples
		 */
		if(samples.isPresent()) {
			SelectionManagerSamples.getInstance().getElements().remove(samples.get());
			SelectionManagerSamples.getInstance().getSelection().clear();
			samples.get().getSampleList().removeListener(sampleChangeSelectionListener);
		}
		if(newSamples != null) {
			this.samples = Optional.of(newSamples);
			SelectionManagerSamples.getInstance().getElements().add(samples.get());
			SelectionManagerSamples.getInstance().getSelection().add(samples.get());
			samples.get().getSampleList().addListener(sampleChangeSelectionListener);
			updateNumerSeletedSamples();
			inputSamples.accept(samples.get());
			cTableSamples.setItems(FXCollections.observableArrayList(samples.get().getSampleList()));
			Set<String> groupNames = samples.get().getSampleList().stream().map(s -> s.getGroupName()).collect(Collectors.toSet());
			Map<String, Integer> colors = Coloring.getColorsForGroup(groupNames);
			samples.get().getSampleList().forEach(s -> s.setColor(colors.get(s.getGroupName())));
		}
	}

	public void setSamplesConsumer(Consumer<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>>> consumer) {

		this.inputSamples = consumer;
	}

	private void updateNumerSeletedSamples() {

		long selected = 0;
		int totalSamples = 0;
		if(samples.isPresent()) {
			selected = samples.get().getSampleList().stream().filter(s -> s.isSelected()).count();
			totalSamples = samples.get().getSampleList().size();
		}
		cLabelNumblerSelectedSamples.setText("Selected: " + selected + " from " + totalSamples + " samples");
	}
}
