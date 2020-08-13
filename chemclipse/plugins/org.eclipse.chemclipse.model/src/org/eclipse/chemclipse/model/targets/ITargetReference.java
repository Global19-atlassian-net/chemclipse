/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring target label support
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public interface ITargetReference extends ITargetSupplier {

	ISignal getSignal();

	/**
	 * Display Name
	 * 
	 * @return {String}
	 */
	String getName();

	/**
	 * Unique ID to persist the data.
	 * 
	 * @return {String}
	 */
	String getID();

	/**
	 * Type: Peak/Scan
	 * 
	 * @return {String}
	 */
	TargetReferenceType getType();

	/**
	 * Returns the best target or null if no target is available.
	 * 
	 * @return {IIdentificationTarget}
	 */
	default IIdentificationTarget getBestIdentificationTarget() {

		return IIdentificationTarget.getBestIdentificationTarget(getTargets());
	}

	default String getTargetLabel(LibraryField libraryField) {

		IIdentificationTarget identificationTarget = getBestIdentificationTarget();
		if(identificationTarget != null) {
			return libraryField.stringTransformer().apply(identificationTarget);
		}
		//
		return null;
	}
}
