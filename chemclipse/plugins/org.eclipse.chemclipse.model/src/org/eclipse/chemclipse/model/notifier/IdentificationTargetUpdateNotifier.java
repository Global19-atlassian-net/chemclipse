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
package org.eclipse.chemclipse.model.notifier;

import org.eclipse.chemclipse.model.Activator;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;

public class IdentificationTargetUpdateNotifier {

	/**
	 * Sends an update event. The identification target selection must be not null.
	 * 
	 * @param identificationTarget
	 */
	public static void update(IIdentificationTarget identificationTarget) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(identificationTarget != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, identificationTarget);
			}
		}
	}
}
