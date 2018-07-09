/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.workbench;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

public class DisplayUtils {

	private static final Logger logger = Logger.getLogger(DisplayUtils.class);

	public static Display getDisplay() {

		Display display = null;
		//
		display = Display.getDefault();
		if(display == null) {
			logger.info("Default Display is null.");
			display = Display.getCurrent();
			if(display == null) {
				logger.info("Current Display is null.");
				try {
					display = PlatformUI.getWorkbench().getDisplay();
					if(display == null) {
						logger.info("PlatformUI Display is null.");
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
		//
		if(display == null) {
			logger.warn("Display is null!");
		}
		//
		return display;
	}

	public static Shell getShell(Widget widget) {

		Shell shell = null;
		if(widget instanceof Control) {
			shell = ((Control)widget).getShell();
		} else {
			shell = getShell();
		}
		return shell;
	}

	public static Shell getShell() {

		Shell shell = null;
		//
		Display display = getDisplay();
		if(display != null) {
			shell = display.getActiveShell();
		}
		//
		if(shell == null) {
			logger.warn("Shell is null!");
		}
		//
		return shell;
	}
}
