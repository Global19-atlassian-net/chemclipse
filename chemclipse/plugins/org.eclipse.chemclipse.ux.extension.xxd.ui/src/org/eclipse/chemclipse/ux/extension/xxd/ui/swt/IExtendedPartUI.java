/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public interface IExtendedPartUI {

	String PREFIX_SHOW = "Show";
	String PREFIX_HIDE = "Hide";
	//
	String PREFIX_ENABLE = "Enable";
	String PREFIX_DISABLE = "Disable";
	String TOOLTIP_TABLE = "the table content edit modus.";

	default Button createButton(Composite parent, String text, String tooltip, String image) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected header entrie(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
		//
		return button;
	}

	default Button createButtonToggleToolbar(Composite parent, AtomicReference<? extends Composite> toolbar, String image, String tooltip) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Composite composite = toolbar.get();
				if(composite != null) {
					boolean active = PartSupport.toggleCompositeVisibility(composite);
					setButtonImage(button, image, PREFIX_SHOW, PREFIX_HIDE, tooltip, active);
				}
			}
		});
		//
		return button;
	}

	default Button createButtonToggleEditTable(Composite parent, AtomicReference<? extends ExtendedTableViewer> viewer, String image) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ExtendedTableViewer tableViewer = viewer.get();
				if(tableViewer != null) {
					boolean edit = !tableViewer.isEditEnabled();
					tableViewer.setEditEnabled(edit);
					setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_TABLE, edit);
				}
			}
		});
		//
		return button;
	}

	default void enableToolbar(AtomicReference<? extends Composite> toolbar, Button button, String image, String tooltip, boolean active) {

		Composite composite = toolbar.get();
		if(composite != null) {
			PartSupport.setCompositeVisibility(composite, active);
			setButtonImage(button, image, PREFIX_SHOW, PREFIX_HIDE, tooltip, active);
		}
	}

	default void enableEdit(AtomicReference<? extends ExtendedTableViewer> viewer, Button button, String image, boolean edit) {

		ExtendedTableViewer tableViewer = viewer.get();
		if(tableViewer != null) {
			tableViewer.setEditEnabled(edit);
			setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_TABLE, edit);
		}
	}

	default void setButtonImage(Button button, String image, String prefixActivate, String prefixDeactivate, String tooltip, boolean enabled) {

		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16, enabled));
		StringBuilder builder = new StringBuilder();
		builder.append(enabled ? prefixDeactivate : prefixActivate);
		builder.append(" ");
		builder.append(tooltip);
		button.setToolTipText(builder.toString());
	}
}
