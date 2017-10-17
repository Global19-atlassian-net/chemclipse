/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MArea;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class PartSupport {

	public static final String PERSPECTIVE_DATA_ANALYSIS = "org.eclipse.chemclipse.ux.extension.xxd.ui.perspective.main";
	//
	public static final String AREA = "org.eclipse.chemclipse.rcp.app.ui.editor";
	//
	public static final String PARTDESCRIPTOR_CHROMATOGRAM_OVERLAY = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.chromatogramOverlayPartDescriptor";
	public static final String PARTDESCRIPTOR_CHROMATOGRAM_HEADER = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.chromatogramHeaderPartDescriptor";
	public static final String PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.chromatogramOverviewPartDescriptor";
	//
	public static final String PARTSTACK_QUICKACCESS = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.quickaccess";
	public static final String PARTSTACK_FILES = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.files";
	public static final String PARTSTACK_OVERVIEW = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.overview";
	public static final String PARTSTACK_BOTTOM_LEFT = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.bottom.left";
	public static final String PARTSTACK_BOTTOM_CENTER = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.bottom.center";
	public static final String PARTSTACK_BOTTOM_RIGHT = "org.eclipse.chemclipse.ux.extension.xxd.ui.partstack.bottom.right";
	//
	private static MApplication application = ModelSupportAddon.getApplication();
	private static EModelService modelService = ModelSupportAddon.getModelService();
	private static EPartService partService = ModelSupportAddon.getPartService();

	public static MPart getPart(String partId, String partStackId) {

		MPart part = null;
		MUIElement element = modelService.find(partId, application);
		if(element instanceof MPart) {
			/*
			 * Get the part or create it.
			 */
			part = (MPart)element;
			if(!partService.getParts().contains(part)) {
				partService.createPart(part.getElementId());
			}
		} else {
			part = partService.createPart(partId);
			MPartStack partStack = (MPartStack)modelService.find(partStackId, application);
			partStack.getChildren().add(part);
		}
		//
		return part;
	}

	public static void togglePartVisibility(MPart part, String partStackId) {

		if(part != null) {
			if(part.isVisible()) {
				part.setVisible(false);
				partService.hidePart(part);
			} else {
				part.setVisible(true);
				partService.showPart(part, PartState.ACTIVATE);
			}
		}
	}

	public static boolean isChildrenOfPartStack(MPartStack partStack, String elementId) {

		if(partStack != null) {
			for(MStackElement stackElement : partStack.getChildren()) {
				if(stackElement.getElementId().equals(elementId)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isPartVisible(MPart part) {

		if(part != null) {
			if(partService.isPartVisible(part)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static void showPart(MPart part) {

		if(part != null) {
			partService.showPart(part, PartState.ACTIVATE);
		}
	}

	public static void hidePart(MPart part) {

		if(part != null) {
			partService.hidePart(part);
		}
	}

	public static void setPartVisibility(String partId, String partStackId, boolean visible) {

		MPart part = getPart(partId, partStackId);
		if(part != null) {
			part.setVisible(visible);
		}
	}

	public static void setPartStackVisibility(String partStackId, boolean visible) {

		MPartStack partStack = getPartStack(partStackId);
		if(partStack != null) {
			partStack.setVisible(visible);
		}
	}

	public static MPartStack getPartStack(String partStackId) {

		return (MPartStack)modelService.find(partStackId, application);
	}

	public static void setAreaVisibility(String areaId, boolean visible) {

		MArea area = (MArea)modelService.find(areaId, application);
		if(area != null) {
			area.setVisible(visible);
		}
	}
}
