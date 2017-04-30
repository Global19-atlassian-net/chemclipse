/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter;

public class PdfTableCell {

	private String text;
	private float width;
	private boolean printLeftLine;

	public PdfTableCell(String text, float width) {
		this(text, width, true);
	}

	public PdfTableCell(String text, float width, boolean printLeftLine) {
		this.text = text;
		this.width = width;
		this.printLeftLine = printLeftLine;
	}

	public String getText() {

		return text;
	}

	public float getWidth() {

		return width;
	}

	public boolean isPrintLeftLine() {

		return printLeftLine;
	}
}
