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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PcaResultVisualization implements IPcaResultVisualization {

	private IntegerProperty color;
	private IPcaResult delegator;

	public PcaResultVisualization(IPcaResult dataModel) {
		super();
		this.color = new SimpleIntegerProperty();
		this.delegator = dataModel;
	}

	@Override
	public IntegerProperty colorProperty() {

		return this.color;
	}

	@Override
	public int getColor() {

		return this.colorProperty().get();
	}

	@Override
	public double[] getScoreVector() {

		return delegator.getScoreVector();
	}

	@Override
	public double getErrorMemberShip() {

		return delegator.getErrorMemberShip();
	}

	@Override
	public String getGroupName() {

		return delegator.getGroupName();
	}

	@Override
	public String getName() {

		return delegator.getName();
	}

	@Override
	public ISample<? extends ISampleData> getSample() {

		return delegator.getSample();
	}

	@Override
	public double[] getSampleData() {

		return delegator.getSampleData();
	}

	@Override
	public boolean isDisplayed() {

		return delegator.isDisplayed();
	}

	@Override
	public void setColor(final int color) {

		this.colorProperty().set(color);
	}

	@Override
	public void setDisplayed(boolean displayed) {

		delegator.setDisplayed(displayed);
	}

	@Override
	public void setScoreVector(double[] scoreVector) {

		delegator.setScoreVector(scoreVector);
	}

	@Override
	public void setErrorMemberShip(double errorMemberShip) {

		delegator.setErrorMemberShip(errorMemberShip);
	}

	@Override
	public void setGroupName(String groupName) {

		delegator.setGroupName(groupName);
	}

	@Override
	public void setName(String name) {

		delegator.setName(name);
	}

	@Override
	public void setSampleData(double[] sampleData) {

		delegator.setSampleData(sampleData);
	}
}
