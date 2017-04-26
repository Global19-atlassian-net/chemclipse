/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.toolcontrols;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ActivePerspective {

	@Inject
	private Composite parent;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private EventHandler eventHandler;

	@PostConstruct
	private void createControl() {

		parent.setLayout(new GridLayout(1, true));
		//
		Label label = new Label(parent, SWT.NONE);
		setPerspectiveLabel(label, ModelSupportAddon.getActivePerspective());
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.minimumWidth = 300;
		label.setLayoutData(gridData);
		//
		if(eventBroker != null) {
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					String perspectiveName = (String)event.getProperty(IChemClipseEvents.PROPERTY_PERSPECTIVE_NAME);
					setPerspectiveLabel(label, perspectiveName);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, eventHandler);
		}
	}

	private void setPerspectiveLabel(Label label, String perspectiveName) {

		perspectiveName = perspectiveName.replaceAll("<", "");
		perspectiveName = perspectiveName.replaceAll(">", "");
		label.setText("Perspective: " + perspectiveName);
	}

	@PreDestroy
	private void preDestroy() {

		if(eventBroker != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}
}
