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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.nebula.widgets.nattable.sort.ISortModel;
import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;

public class SortModel implements ISortModel {

	private boolean isSorted;
	private SortDirectionEnum sortDirection;
	private int sortedColumn;
	private List<Integer> sortedRow;
	private TableProvider tableProvider;

	public SortModel(TableProvider tableProvider) {
		this.sortedRow = new ArrayList<>();
		sortDirection = SortDirectionEnum.NONE;
		this.tableProvider = tableProvider;
	}

	@Override
	public void clear() {

		for(int i = 0; i < sortedRow.size(); i++) {
			sortedRow.set(i, i);
		}
		sortDirection = SortDirectionEnum.NONE;
		isSorted = false;
	}

	@Override
	public Comparator<?> getColumnComparator(int columnIndex) {

		return null;
	}

	@Override
	public List<Comparator> getComparatorsForColumnIndex(int columnIndex) {

		return null;
	}

	public List<Integer> getOrderRow() {

		return sortedRow;
	}

	@Override
	public SortDirectionEnum getSortDirection(int columnIndex) {

		if(isSorted && columnIndex == sortedColumn) {
			return sortDirection;
		}
		return SortDirectionEnum.NONE;
	}

	@Override
	public List<Integer> getSortedColumnIndexes() {

		List<Integer> sortedColumnIndexes = new ArrayList<>();
		if(isSorted) {
			sortedColumnIndexes.add(sortedColumn);
		}
		return sortedColumnIndexes;
	}

	@Override
	public int getSortOrder(int columnIndex) {

		return 0;
	}

	@Override
	public boolean isColumnIndexSorted(int columnIndex) {

		if(isSorted && columnIndex == sortedColumn) {
			return true;
		}
		return false;
	}

	@Override
	public void sort(int columnIndex, SortDirectionEnum sortDirection, boolean accumulate) {

		clear();
		if(!sortDirection.equals(SortDirectionEnum.NONE)) {
			/*
			 * Set direction of sorting
			 */
			int direction = 1;
			switch(sortDirection) {
				case ASC:
					direction = 1;
					break;
				case DESC:
					direction = -1;
			}
			final int setDirection = direction;
			if(columnIndex == TableProvider.COLUMN_INDEX_SELECTED) {
				List<? extends IVariable> variables = tableProvider.getDataTable().getVariables();
				sortedRow.sort((i, j) -> setDirection * Boolean.compare(variables.get(i).isSelected(), variables.get(j).isSelected()));
			} else if(columnIndex == TableProvider.COLUMN_INDEX_RETENTION_TIMES) {
				/*
				 * sort by retention time
				 */
				for(int i = 0; i < sortedRow.size(); i++) {
					sortedRow.set(i, i);
				}
				// reverse sorting
				if(direction == -1) {
					Collections.reverse(sortedRow);
				}
			} else if(columnIndex == TableProvider.COLUMN_INDEX_PEAK_NAMES) {
				List<String> peaksNames = tableProvider.getDataTable().getVariables().stream().map(r -> r.getDescription()).collect(Collectors.toList());
				Comparator<String> nullSafeStringComparator = Comparator.nullsFirst(String::compareTo);
				sortedRow.sort((i, j) -> {
					return setDirection * nullSafeStringComparator.compare(peaksNames.get(i), peaksNames.get(j));
				});
			} else {
				/*
				 * sort by abundance
				 */
				ISample sample = tableProvider.getDataTable().getSamples().get(columnIndex - TableProvider.NUMER_OF_DESCRIPTION_COLUMN);
				List<ISampleData> sampleData = sample.getSampleData();
				sortedRow.sort((i, j) -> {
					ISampleData sampleDataA = sampleData.get(i);
					ISampleData sampleDataB = sampleData.get(j);
					if(!sampleDataA.isEmpty() && !sampleDataB.isEmpty()) {
						return setDirection * Double.compare(sampleDataA.getModifiedData(), sampleDataB.getModifiedData());
					} else if(!sampleDataA.isEmpty()) {
						return setDirection;
					} else if(!sampleDataB.isEmpty()) {
						return -setDirection;
					} else {
						return 0;
					}
				});
			}
			this.isSorted = true;
		}
		this.sortDirection = sortDirection;
		this.sortedColumn = columnIndex;
	}

	/**
	 * call this method after data in object tableData has been changed
	 */
	public void update() {

		int numberOfRow = tableProvider.getDataTable().getVariables().size();
		sortedRow.clear();
		for(int i = 0; i < numberOfRow; i++) {
			sortedRow.add(i);
		}
		sortDirection = SortDirectionEnum.NONE;
		isSorted = false;
	}
}
