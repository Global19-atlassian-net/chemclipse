/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.dialogs.DialogSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.runnables.MassSpectrumIdentifierGUIRunnable;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.runnables.MassSpectrumIdentifierRunnable;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;

public class IdentifySelectedMassSpectrumHandler implements EventHandler {

	private static final Logger logger = Logger.getLogger(IdentifySelectedPeakHandler.class);
	private static IChromatogramSelectionMSD chromatogramSelection;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		/*
		 * Try to select and show the perspective and view.
		 */
		List<String> viewIds = new ArrayList<String>();
		viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRA_IDENTIFIED_LIST);
		viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM);
		viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM_TARGETS);
		PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_MSD, viewIds);
		//
		if(chromatogramSelection != null) {
			final Display display = Display.getCurrent();
			StatusLineLogger.setInfo(InfoType.MESSAGE, "Start the mass spectrum identification.");
			/*
			 * Do the operation.<br/> Open a progress monitor dialog.
			 */
			IRunnableWithProgress runnable;
			if(PreferenceSupplier.isUseGUIDirect()) {
				/*
				 * Opens the GUI version
				 */
				DialogSupport.showGUIDialog();
				runnable = new MassSpectrumIdentifierGUIRunnable(chromatogramSelection);
			} else {
				runnable = new MassSpectrumIdentifierRunnable(chromatogramSelection);
			}
			runOperation(display, runnable);
			StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: Mass spectrum identified");
		}
	}

	private void runOperation(Display display, IRunnableWithProgress runnable) {

		ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
		try {
			/*
			 * Use true, true ... instead of false, true ... if the progress bar
			 * should be shown in action.
			 */
			monitor.run(true, true, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
	}

	@Override
	public void handleEvent(Event event) {

		if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionMSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		} else {
			chromatogramSelection = null;
		}
	}
}
