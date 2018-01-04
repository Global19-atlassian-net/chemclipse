/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.comparator;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.AbstractMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.math.GeometricDistanceCalculator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.math.IMatchCalculator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.MassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.results.MassSpectrumComparisonResult;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

/**
 * This comparator evaluate two mass spectra by the geometric distance
 * algorithm.<br/>
 * See article:<br/>
 * <br/>
 * Alfassi, Z. B., Vector analysis of multi-measurements identification
 * 
 * @author eselmeister
 */
public class MassSpectrumComparator extends AbstractMassSpectrumComparator implements IMassSpectrumComparator {

	public static final String COMPARATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.alfassi.geometric";
	private static final int NORMALIZATION_FACTOR = 100;
	private static final Logger logger = Logger.getLogger(MassSpectrumComparator.class);

	@Override
	public IMassSpectrumComparatorProcessingInfo compare(IScanMSD unknown, IScanMSD reference) {

		IMassSpectrumComparatorProcessingInfo processingInfo = new MassSpectrumComparatorProcessingInfo();
		IProcessingInfo processingInfoValidate = super.validate(unknown, reference);
		if(processingInfoValidate.hasErrorMessages()) {
			processingInfo.addMessages(processingInfoValidate);
		} else {
			try {
				IMatchCalculator geometricDistanceCalculator = new GeometricDistanceCalculator();
				IScanMSD unknownAdjusted = adjustMassSpectrum(unknown);
				IScanMSD referenceAdjusted = adjustMassSpectrum(reference);
				/*
				 * Match Factor, Reverse Match Factor
				 * Internally the match is normalized to 1, but a percentage value is used normally.
				 */
				IExtractedIonSignal signalU = unknownAdjusted.getExtractedIonSignal();
				IExtractedIonSignal signalR = referenceAdjusted.getExtractedIonSignal();
				//
				float matchFactor = geometricDistanceCalculator.calculate(unknownAdjusted, referenceAdjusted, signalU.getIonRange()) * 100;
				float reverseMatchFactor = geometricDistanceCalculator.calculate(referenceAdjusted, unknownAdjusted, signalR.getIonRange()) * 100;
				float matchFactorDirect = geometricDistanceCalculator.calculate(unknownAdjusted, referenceAdjusted) * 100;
				float reverseMatchFactorDirect = geometricDistanceCalculator.calculate(referenceAdjusted, unknownAdjusted) * 100;
				/*
				 * Result
				 */
				IMassSpectrumComparisonResult massSpectrumComparisonResult = new MassSpectrumComparisonResult(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect);
				processingInfo.setMassSpectrumComparisonResult(massSpectrumComparisonResult);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
				processingInfo.addErrorMessage("Alfassi MassSpectrum Comparator", "The mass spectrum couldn't be cloned.");
			}
		}
		return processingInfo;
	}

	private IScanMSD adjustMassSpectrum(IScanMSD massSpectrum) throws CloneNotSupportedException {

		/*
		 * Make a deep copy to not destroy the original mass spectrum.
		 */
		IScanMSD massSpectrumNormalized;
		massSpectrumNormalized = massSpectrum.makeDeepCopy();
		massSpectrumNormalized.normalize(NORMALIZATION_FACTOR);
		return massSpectrumNormalized;
	}
}
