/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ScanXIRImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedXIRScanUI;
import org.eclipse.chemclipse.xir.model.core.IScanXIR;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ScanEditorXIR extends AbstractDataUpdateSupport implements IChemClipseEditor, IDataUpdateSupport {

	private static final Logger logger = Logger.getLogger(ScanEditorXIR.class);
	//
	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanEditorXIR";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ScanEditorXIR";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/scan-xir.gif";
	public static final String TOOLTIP = "FTIR/NIR/MIR Editor";
	//
	private MPart part;
	private MDirtyable dirtyable;
	//
	private IEventBroker eventBroker;
	//
	private File scanFile;
	private ExtendedXIRScanUI extendedScanXIREditorUI;
	//
	private Shell shell;

	@Inject
	public ScanEditorXIR(Composite parent, MPart part, MDirtyable dirtyable, Shell shell) {
		super(part);
		//
		this.part = part;
		this.dirtyable = dirtyable;
		this.eventBroker = ModelSupportAddon.getEventBroker();
		this.shell = shell;
		//
		initialize(parent);
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_SCAN_XIR_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SCAN_SELECTION);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			//
		}
	}

	@Focus
	public void setFocus() {

		//
	}

	@PreDestroy
	private void preDestroy() {

		eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XIR_UNLOAD_SELECTION, null);
		//
		EModelService modelService = ModelSupportAddon.getModelService();
		if(modelService != null) {
			MApplication application = ModelSupportAddon.getApplication();
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					partStack.getChildren().remove(part);
				}
			});
		}
		/*
		 * Run the garbage collector.
		 */
		System.gc();
	}

	@Persist
	public void save() {

		System.out.println(scanFile);
		dirtyable.setDirty(false);
	}

	@Override
	public boolean saveAs() {

		return true;
	}

	private void initialize(Composite parent) {

		IScanXIR scanXIR = loadScan();
		createEditorPages(parent);
		extendedScanXIREditorUI.update(scanXIR);
	}

	private synchronized IScanXIR loadScan() {

		IScanXIR scanXIR = null;
		try {
			Object object = part.getObject();
			if(object instanceof String) {
				/*
				 * String
				 */
				File file = new File((String)object);
				scanXIR = loadScan(file);
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		//
		return scanXIR;
	}

	private IScanXIR loadScan(File file) {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		ScanXIRImportRunnable runnable = new ScanXIRImportRunnable(file);
		try {
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		//
		scanFile = file;
		return runnable.getScanXIR();
	}

	private void createEditorPages(Composite parent) {

		createScanPage(parent);
	}

	private void createScanPage(Composite parent) {

		extendedScanXIREditorUI = new ExtendedXIRScanUI(parent);
	}
}
