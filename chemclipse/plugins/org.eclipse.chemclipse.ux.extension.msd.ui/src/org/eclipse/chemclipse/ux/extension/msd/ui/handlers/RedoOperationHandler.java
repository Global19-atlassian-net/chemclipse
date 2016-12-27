/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.handlers;

import javax.inject.Named;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class RedoOperationHandler implements EventHandler {

	private static IChromatogramSelectionMSD chromatogramSelection;

	@CanExecute
	boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		if(part != null) {
			if(chromatogramSelection != null && part.getElementId().equals(ChromatogramEditorMSD.ID)) {
				return chromatogramSelection.getChromatogramMSD().canRedo();
			}
		}
		return false;
	}

	@Execute
	public void execute(UISynchronize uiSynchronize, final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		if(chromatogramSelection != null) {
			StatusLineLogger.setInfo(InfoType.MESSAGE, "Start Redo Operation");
			uiSynchronize.syncExec(new Runnable() {

				@Override
				public void run() {

					Cursor cursor = shell.getCursor();
					try {
						Cursor cursorNew = Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT);
						shell.setCursor(cursorNew);
						/*
						 * Redo the operation.
						 */
						IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
						if(chromatogram != null) {
							chromatogram.redoOperation(chromatogramSelection);
						}
					} finally {
						shell.setCursor(cursor);
					}
				}
			});
			StatusLineLogger.setInfo(InfoType.MESSAGE, "Redo Operation finished");
		}
	}

	@Override
	public void handleEvent(Event event) {

		if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionMSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		}
	}
}
