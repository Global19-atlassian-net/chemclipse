/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.supplier;

import java.util.function.Consumer;

public interface ProcessSupplierContext {

	/**
	 * Gets the {@link IProcessSupplier} for the given id from this context or <code>null</code> if no supplier exits for the id
	 * 
	 * @param id
	 * @return
	 */
	<T> IProcessSupplier<T> getSupplier(String id);

	/**
	 * 
	 * iterates all available {@link IProcessSupplier}
	 */
	void visitSupplier(Consumer<? super IProcessSupplier<?>> consumer);
}
