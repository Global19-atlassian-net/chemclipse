/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.activator.ContextAddon;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Display;

public class GroupHandlerScans extends AbstractGroupHandler {

	private static final String TOOLBAR_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.dataanalysis";
	private static final String TOOL_ITEM_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.directtoolitem.scans";
	private static final String MENU_ITEM_SETTINGS_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.directmenuitem.settings.scans";
	private static final String COMMAND_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.command.partHandler";
	//
	private static final String IMAGE_HIDE = IApplicationImage.IMAGE_SELECTED_SCANS_ACTIVE;
	private static final String IMAGE_SHOW = IApplicationImage.IMAGE_SELECTED_SCANS_DEFAULT;
	//
	private static boolean partsAreActivated = false;

	public static List<IPartHandler> getHandler() {

		List<IPartHandler> partHandler = new ArrayList<>();
		//
		partHandler.add(new PartHandler("Targets", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.targetsPartDescriptor", PreferenceConstants.P_STACK_POSITION_TARGETS));
		partHandler.add(new PartHandler("Scan Chart", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanChartPartDescriptor", PreferenceConstants.P_STACK_POSITION_SCAN_CHART));
		partHandler.add(new PartHandler("Scan Table", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanTablePartDescriptor", PreferenceConstants.P_STACK_POSITION_SCAN_TABLE));
		partHandler.add(new PartHandler("Molecule", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.moleculePartDescriptor", PreferenceConstants.P_STACK_POSITION_MOLECULE));
		partHandler.add(new PartHandler("Scan Browse", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanBrowsePartDescriptor", PreferenceConstants.P_STACK_POSITION_SCAN_BROWSE));
		partHandler.add(new PartHandler("Synonyms", "org.eclipse.chemclipse.ux.extension.xxd.ui.part.synonymsPartDescriptor", PreferenceConstants.P_STACK_POSITION_SYNONYMS));
		//
		return partHandler;
	}

	public static void enableToolBar(boolean show) {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		if(modelService != null && application != null) {
			Display display = Display.getDefault();
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					MToolBar toolBar = PartSupport.getToolBar(TOOLBAR_ID, modelService, application);
					toolBar.setToBeRendered(show);
					toolBar.setVisible(show);
				}
			});
		}
	}

	public static void updateMenu() {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		if(modelService != null && application != null) {
			MDirectToolItem directToolItem = PartSupport.getDirectToolItem(TOOL_ITEM_ID, modelService, application);
			MCommand command = getCommand(modelService, application);
			MMenu menu = directToolItem.getMenu();
			/*
			 * Parts
			 */
			List<IPartHandler> partHandlers = getHandler();
			int size = partHandlers.size();
			for(int i = 0; i < size; i++) {
				IPartHandler partHandler = partHandlers.get(i);
				MHandledMenuItem menuItem = getHandledItem(menu, partHandler.getId());
				if(menuItem == null) {
					/*
					 * Create a new menu item.
					 * Workaround!!!
					 * The element id is used to store the partId and stackPositionId, have a look at partHandler.getId()
					 * All approaches to create a specific parameterized command failed.
					 * Frankly, I don't know how to do this without getting tons of errors.
					 */
					menuItem = modelService.createModelElement(MHandledMenuItem.class);
					menuItem.setElementId(partHandler.getId());
					menuItem.setLabel(partHandler.getName());
					menuItem.setTooltip("");
					menuItem.setIconURI(partHandler.getIconURI());
					menuItem.setCommand(command);
					/*
					 * Place the items in the correct order.
					 */
					menu.getChildren().add(i, menuItem);
				}
				/*
				 * Adjust the label.
				 */
				String prefix = partHandler.isPartVisible() ? "Hide " : "Show ";
				String label = prefix + partHandler.getName();
				menuItem.setLabel(label);
				/*
				 * If the user has defined to use the part, show it.
				 */
				menuItem.setVisible(partHandler.isPartStackAssigned());
			}
			/*
			 * Settings
			 */
			MDirectMenuItem settingsMenuItem = getDirectItem(menu, MENU_ITEM_SETTINGS_ID);
			if(settingsMenuItem == null) {
				/*
				 * Create a new settings item.
				 */
				MDirectMenuItem menuItem = modelService.createModelElement(MDirectMenuItem.class);
				menuItem.setElementId(MENU_ITEM_SETTINGS_ID);
				menuItem.setLabel("Settings");
				menuItem.setTooltip("Settings to show/hide parts.");
				menuItem.setIconURI("platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/preferences.gif");
				menuItem.setContributionURI("bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.SettingsHandlerScans");
				//
				menu.getChildren().add(menuItem);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static MCommand getCommand(EModelService modelService, MApplication application) {

		Object object = modelService.findElements(application, COMMAND_ID, MCommand.class, Collections.EMPTY_LIST).get(0);
		if(object instanceof MCommand) {
			return (MCommand)object;
		}
		//
		return null;
	}

	private static MHandledMenuItem getHandledItem(MMenu menu, String id) {

		MMenuElement menuElement = get(menu, id);
		if(menuElement instanceof MHandledMenuItem) {
			return (MHandledMenuItem)menuElement;
		}
		//
		return null;
	}

	private static MDirectMenuItem getDirectItem(MMenu menu, String id) {

		MMenuElement menuElement = get(menu, id);
		if(menuElement instanceof MDirectMenuItem) {
			return (MDirectMenuItem)menuElement;
		}
		//
		return null;
	}

	private static MMenuElement get(MMenu menu, String id) {

		for(MMenuElement menuElement : menu.getChildren()) {
			if(menuElement.getElementId().equals(id)) {
				return menuElement;
			}
		}
		//
		return null;
	}

	/**
	 * This static method activates the referenced parts.
	 */
	public static void activateParts() {

		EPartService partService = ContextAddon.getPartService();
		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		if(partService != null && modelService != null && application != null) {
			/*
			 * Try to get tool item to modify the tooltip and image.
			 */
			MDirectToolItem directToolItem = PartSupport.getDirectToolItem(TOOL_ITEM_ID, modelService, application);
			//
			Display display = Display.getDefault();
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					GroupHandlerScans groupHandler = new GroupHandlerScans();
					groupHandler.activateParts(directToolItem, partService, modelService, application, groupHandler.toggleShow());
					updateMenu();
				}
			});
		}
	}

	@Override
	public List<IPartHandler> getPartHandler() {

		return getHandler();
	}

	@Override
	public String getImageHide() {

		return IMAGE_HIDE;
	}

	@Override
	public String getImageShow() {

		return IMAGE_SHOW;
	}

	@Override
	public boolean toggleShow() {

		partsAreActivated = !partsAreActivated;
		return partsAreActivated;
	}
}
