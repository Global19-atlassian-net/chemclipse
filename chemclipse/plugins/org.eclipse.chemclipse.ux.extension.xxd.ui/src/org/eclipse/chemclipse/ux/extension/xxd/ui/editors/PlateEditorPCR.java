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
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.pcr.converter.core.PlateConverterPCR;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedPCRPlateUI;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class PlateEditorPCR extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.plateEditorPCR";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.PlateEditorPCR";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/plate-pcr.gif";
	public static final String TOOLTIP = "PCR Editor";
	//
	private MPart part;
	private MDirtyable dirtyable;
	//
	private File scanFile;
	private IPlate plate = null;
	private ExtendedPCRPlateUI extendedPCRPlateUI;
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	@Inject
	public PlateEditorPCR(Composite parent, MPart part, MDirtyable dirtyable, Shell shell) {
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

		showPart(PartSupport.PARTDESCRIPTOR_PlATE_CHARTS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PLATE_CHARTS));
		showPart(PartSupport.PARTDESCRIPTOR_WELL_DATA, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_WELL_DATA));
		showPart(PartSupport.PARTDESCRIPTOR_WELL_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_WELL_CHART));
		updatePlate();
	}

	@PreDestroy
	private void preDestroy() {

		hidePart(PartSupport.PARTDESCRIPTOR_PlATE_CHARTS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PLATE_CHARTS));
		hidePart(PartSupport.PARTDESCRIPTOR_WELL_DATA, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_WELL_DATA));
		hidePart(PartSupport.PARTDESCRIPTOR_WELL_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_WELL_CHART));
		unloadPlate();
		//
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

		System.out.println(scanFile);
		dirtyable.setDirty(false);
	}

	private void initialize(Composite parent) {

		createEditorPages(parent);
		plate = loadPlate();
		extendedPCRPlateUI.update(plate);
	}

	private synchronized IPlate loadPlate() {

		IPlate plate = null;
		Object object = part.getObject();
		if(object instanceof Map) {
			/*
			 * Map
			 */
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)object;
			File file = new File((String)map.get(EditorSupport.MAP_FILE));
			boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
			//
			IProcessingInfo processingInfo = PlateConverterPCR.convert(file, new NullProgressMonitor());
			try {
				plate = (IPlate)processingInfo.getProcessingResult();
			} catch(TypeCastException e) {
				plate = null;
			}
		}
		//
		return plate;
	}

	private void createEditorPages(Composite parent) {

		createScanPage(parent);
	}

	private void createScanPage(Composite parent) {

		extendedPCRPlateUI = new ExtendedPCRPlateUI(parent);
	}

	private void updatePlate() {

		IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
		DisplayUtils.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				eventBroker.send(IChemClipseEvents.TOPIC_PLATE_PCR_UPDATE_SELECTION, plate);
			}
		});
	}

	private void showPart(String partId, String partStackId) {

		PartSupport.setPartVisibility(partId, partStackId, true);
		PartSupport.showPart(partId, partStackId);
	}

	private void hidePart(String partId, String partStackId) {

		PartSupport.setPartVisibility(partId, partStackId, false);
	}

	private void unloadPlate() {

		IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
		DisplayUtils.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				eventBroker.send(IChemClipseEvents.TOPIC_PLATE_PCR_UNLOAD_SELECTION, null);
			}
		});
	}
}
