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
package org.eclipse.chemclipse.ux.extension.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

public class CreateSnapshotHandler {

	private Clipboard clipboard;

	public CreateSnapshotHandler() {
		clipboard = new Clipboard(Display.getDefault());
	}

	@Execute
	public void execute(Composite composite) {

		copyCompositeToClipboard(composite);
	}

	private void copyCompositeToClipboard(Composite composite) {

		Image image = getImage(composite);
		if(image == null) {
			openMessageBox("The focus of the selected view/editor couldn't be retrieved.");
		} else {
			/*
			 * There is a bug on Linux x86 and x86_64.
			 */
			OperatingSystemUtils operatingSystemUtils = new OperatingSystemUtils();
			if(operatingSystemUtils.isLinux()) {
				/*
				 * Save the image to a file.
				 */
				Shell shell = Display.getCurrent().getActiveShell();
				FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
				fileDialog.setText("Save Clipboard To File");
				fileDialog.setFileName("Clipboard.png");
				fileDialog.setFilterExtensions(new String[]{"*.png"});
				fileDialog.setOverwrite(true);
				fileDialog.setFilterNames(new String[]{" PNG (*.png)"});
				String file = fileDialog.open();
				if(file != null && !file.equals("")) {
					ImageLoader imageLoader = new ImageLoader();
					imageLoader.data = new ImageData[]{image.getImageData()};
					imageLoader.save(file, SWT.IMAGE_PNG);
				}
			} else {
				/*
				 * Copy the image to the clipboard
				 */
				ImageTransfer imageTransfer = ImageTransfer.getInstance();
				Object[] data = new Object[]{image.getImageData()};
				Transfer[] dataTypes = new Transfer[]{imageTransfer};
				clipboard.setContents(data, dataTypes);
				openMessageBox("The selected view/editor has been copied to clipboard.");
			}
		}
	}

	private Image getImage(Composite composite) {

		GC gc = null;
		Image image = null;
		Display display = Display.getCurrent();
		try {
			gc = new GC(composite);
			image = new Image(display, composite.getBounds());
			gc.copyArea(image, 0, 0);
		} finally {
			if(gc != null) {
				gc.dispose();
			}
		}
		return image;
	}

	private void openMessageBox(String message) {

		Display display = Display.getCurrent();
		String text = "Copy Selection To Clipboard";
		Shell shell = display.getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.NONE);
		messageBox.setText(text);
		messageBox.setMessage(message);
		messageBox.open();
	}
}
