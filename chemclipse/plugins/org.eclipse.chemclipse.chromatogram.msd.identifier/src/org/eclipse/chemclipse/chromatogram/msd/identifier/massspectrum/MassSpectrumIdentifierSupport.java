/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 *
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum;

import org.eclipse.chemclipse.chromatogram.msd.identifier.core.AbstractSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.exceptions.NoIdentifierAvailableException;

public class MassSpectrumIdentifierSupport extends AbstractSupport<IMassSpectrumIdentifierSupplier> implements IMassSpectrumIdentifierSupport {

	@Override
	public IMassSpectrumIdentifierSupplier getIdentifierSupplier(String identifierId) throws NoIdentifierAvailableException {

		return getSpecificIdentifierSupplier(identifierId);
	}
}
