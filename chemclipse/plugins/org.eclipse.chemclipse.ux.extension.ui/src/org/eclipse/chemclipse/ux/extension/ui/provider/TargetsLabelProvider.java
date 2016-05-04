/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TargetsLabelProvider extends AbstractChemClipseLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			/*
			 * CheckBox
			 */
			if(element instanceof IIdentificationTarget) {
				IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
				if(identificationTarget.isManuallyVerified()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16);
				}
			}
		} else if(columnIndex == 1) {
			/*
			 * Rating
			 */
			if(element instanceof IIdentificationTarget) {
				IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
				float rating = identificationTarget.getComparisonResult().getRating();
				if(rating >= IComparisonResult.RATING_LIMIT_UP) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP, IApplicationImage.SIZE_16x16);
				} else if(rating >= IComparisonResult.RATING_LIMIT_EQUAL) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_EQUAL, IApplicationImage.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN, IApplicationImage.SIZE_16x16);
				}
			}
		} else if(columnIndex == 2) {
			/*
			 * Entry
			 */
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		/*
		 * SYNCHRONIZE: PeakListLabelProvider PeakListLabelComparator PeakListView
		 */
		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IIdentificationTarget) {
			IIdentificationTarget identificationEntry = (IIdentificationTarget)element;
			ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
			IComparisonResult comparisonResult = identificationEntry.getComparisonResult();
			switch(columnIndex) {
				case 0:
					text = "";
					break;
				case 1: // Rating
					text = "";
					break;
				case 2: // Name
					text = libraryInformation.getName();
					break;
				case 3: // CAS
					text = libraryInformation.getCasNumber();
					break;
				case 4: // MQ
					text = decimalFormat.format(comparisonResult.getMatchFactor());
					break;
				case 5: // RMQ
					text = decimalFormat.format(comparisonResult.getReverseMatchFactor());
					break;
				case 6: // MQD
					text = decimalFormat.format(comparisonResult.getMatchFactorDirect());
					break;
				case 7: // RMQD
					text = decimalFormat.format(comparisonResult.getReverseMatchFactorDirect());
					break;
				case 8: // Probability
					text = decimalFormat.format(comparisonResult.getProbability());
					break;
				case 9: // Formula
					text = libraryInformation.getFormula();
					break;
				case 10: // SMILES
					text = libraryInformation.getSmiles();
					break;
				case 11: // InChI
					text = libraryInformation.getInChI();
					break;
				case 12: // Mol Weight
					text = decimalFormat.format(libraryInformation.getMolWeight());
					break;
				case 13: // Advise
					text = comparisonResult.getAdvise();
					break;
				case 14: // Identifier
					text = identificationEntry.getIdentifier();
					break;
				case 15: // Miscellaneous
					text = libraryInformation.getMiscellaneous();
					break;
				case 16: // Comments
					text = libraryInformation.getComments();
					break;
				case 17:
					text = libraryInformation.getDatabase();
					break;
				case 18:
					text = libraryInformation.getContributor();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		Image image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TARGETS, IApplicationImage.SIZE_16x16);
		return image;
	}
}
