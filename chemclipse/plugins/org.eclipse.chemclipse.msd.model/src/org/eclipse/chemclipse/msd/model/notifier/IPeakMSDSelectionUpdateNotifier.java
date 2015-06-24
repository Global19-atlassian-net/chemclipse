/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.notifier;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

/**
 * The interfaces declares a method to update listeners and inform about changes
 * in the peak selection.<br/>
 * See the method update(...). The parameter forceReload is used to inform about
 * substantial changes.
 * 
 * @author eselmeister
 */
public interface IPeakMSDSelectionUpdateNotifier {

	/**
	 * If some changes have been occurred, this method will inform about the
	 * changed peak selection instance.<br/>
	 * Each implementing class has to decide, which information of the selection
	 * should be used (draw the chromatogram?, draw the scan? ...).<br/>
	 * Use forceUpdate to inform the listeners that substantial information has
	 * been changed.<br/>
	 * Why and when use it? Think of GUI elements which store representations of
	 * the chromatogram model.<br/>
	 * In a normal retention time selection change, the model do not need to be
	 * reloaded.<br/>
	 * But if the chromatogram itself has been modified (background remove), the
	 * GUI model needs to be reloaded to show correct values.
	 * 
	 * @param IPeakMSD
	 * @param forceReload
	 */
	void update(IPeakMSD peakMSD, boolean forceReload);
}
