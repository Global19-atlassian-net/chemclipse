/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.marker;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class GCPoint implements IGCPoint {

	private int y;
	private int x;

	public GCPoint(int x, int y) {
		setX(x);
		setY(y);
	}

	@Override
	public int getY() {

		return y;
	}

	@Override
	public void setY(int y) {

		this.y = y;
	}

	@Override
	public int getX() {

		return x;
	}

	@Override
	public void setX(int x) {

		this.x = x;
	}
}
