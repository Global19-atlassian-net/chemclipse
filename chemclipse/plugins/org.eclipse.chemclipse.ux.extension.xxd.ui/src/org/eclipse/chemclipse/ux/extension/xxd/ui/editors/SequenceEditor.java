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
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.converter.model.reports.ISequence;
import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.SequenceImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedSequenceTableUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class SequenceEditor extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private static final Logger logger = Logger.getLogger(SequenceEditor.class);
	//
	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.sequenceEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.SequenceEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/sequenceListDefault.gif";
	public static final String TOOLTIP = "Sequence Editor";
	//
	private MPart part;
	private MDirtyable dirtyable;
	//
	private File sequenceFile;
	private ExtendedSequenceTableUI extendedSequenceTableUI;

	@Inject
	public SequenceEditor(Composite parent, MPart part, MDirtyable dirtyable, Shell shell) {
		super(part);
		//
		this.part = part;
		this.dirtyable = dirtyable;
		//
		initialize(parent);
	}

	@Override
	public void registerEvents() {

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

		EModelService modelService = ModelSupportAddon.getModelService();
		if(modelService != null) {
			MApplication application = ModelSupportAddon.getApplication();
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			DisplayUtils.getDisplay().asyncExec(new Runnable() {

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

		System.out.println(sequenceFile);
		dirtyable.setDirty(false);
	}

	private void initialize(Composite parent) {

		createEditorPages(parent);
		extendedSequenceTableUI.update(loadSequence());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private synchronized ISequence<? extends ISequenceRecord> loadSequence() {

		ISequence<? extends ISequenceRecord> sequence = null;
		try {
			Object object = part.getObject();
			if(object instanceof Map) {
				/*
				 * Map
				 */
				Map<String, Object> map = (Map<String, Object>)object;
				File file = new File((String)map.get(ISupplierFileEditorSupport.MAP_FILE));
				boolean batch = (boolean)map.get(ISupplierFileEditorSupport.MAP_BATCH);
				sequence = loadSequence(file, batch);
			} else {
				/*
				 * Already available.
				 */
				if(object instanceof ISequence) {
					sequence = (ISequence)object;
				}
				sequenceFile = null;
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		//
		return sequence;
	}

	private synchronized ISequence<? extends ISequenceRecord> loadSequence(File file, boolean batch) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException, ChromatogramIsNullException {

		ISequence<? extends ISequenceRecord> sequence = null;
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
		SequenceImportRunnable runnable = new SequenceImportRunnable(file);
		try {
			/*
			 * No fork, otherwise it might crash when loading a chromatogram takes too long.
			 */
			boolean fork = (batch) ? false : true;
			dialog.run(fork, false, runnable);
			sequence = runnable.getSequence();
			sequenceFile = file;
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		//
		return sequence;
	}

	private void createEditorPages(Composite parent) {

		createScanPage(parent);
	}

	private void createScanPage(Composite parent) {

		extendedSequenceTableUI = new ExtendedSequenceTableUI(parent);
	}
}
