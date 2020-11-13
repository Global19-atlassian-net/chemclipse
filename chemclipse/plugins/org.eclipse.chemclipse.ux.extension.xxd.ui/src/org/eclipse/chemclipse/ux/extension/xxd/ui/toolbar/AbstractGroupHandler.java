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

import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.activator.ContextAddon;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractGroupHandler implements IGroupHandler {

	private static final String COMMAND_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.command.partHandler";

	@Execute
	public void execute(MDirectToolItem directToolItem) {

		activateParts(directToolItem, toggleShow());
	}

	@Override
	public void activateParts() {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		//
		if(modelService != null && application != null) {
			/*
			 * Try to get tool item to modify the tooltip and image.
			 */
			MDirectToolItem directToolItem = getDirectToolItem();
			//
			Display display = Display.getDefault();
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					activateParts(directToolItem, toggleShow());
					updateMenu();
				}
			});
		}
	}

	@Override
	public void updateMenu() {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		if(modelService != null && application != null) {
			/*
			 * Adjust the menu.
			 */
			MDirectToolItem directToolItem = getDirectToolItem();
			MCommand command = getCommand();
			MMenu menu = directToolItem.getMenu();
			/*
			 * Parts
			 */
			List<IPartHandler> partHandlers = getPartHandler();
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
			String menuSettingsId = getMenuSettingsId();
			MDirectMenuItem settingsMenuItem = getDirectItem(menu, menuSettingsId);
			if(settingsMenuItem == null) {
				/*
				 * Create a new settings item.
				 */
				MDirectMenuItem menuItem = modelService.createModelElement(MDirectMenuItem.class);
				menuItem.setElementId(menuSettingsId);
				menuItem.setLabel("Settings");
				menuItem.setTooltip("Settings to show/hide parts.");
				menuItem.setIconURI("platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/preferences.gif");
				menuItem.setContributionURI(getSettingsContributionURI());
				//
				menu.getChildren().add(menuItem);
			}
		}
	}

	private void adjustToolTip(MDirectToolItem directToolItem, boolean show) {

		if(directToolItem != null) {
			directToolItem.setTooltip(show ? "Deactivate all referenced parts." : "Activate all referenced parts.");
		}
	}

	private void adjustIcon(MDirectToolItem directToolItem, boolean show) {

		if(directToolItem != null) {
			String iconHide = IApplicationImage.getLocation(getImageHide(), IApplicationImage.SIZE_16x16);
			String iconShow = IApplicationImage.getLocation(getImageShow(), IApplicationImage.SIZE_16x16);
			directToolItem.setIconURI(show ? iconHide : iconShow);
		}
	}

	private void activateParts(MDirectToolItem directToolItem, boolean show) {

		adjustToolTip(directToolItem, show);
		adjustIcon(directToolItem, show);
		//
		List<IPartHandler> partHandlers = getPartHandler();
		for(IPartHandler partHandler : partHandlers) {
			partHandler.action(show);
		}
	}

	private MDirectToolItem getDirectToolItem() {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		if(modelService != null && application != null) {
			String toolItemId = getToolItemId();
			return PartSupport.getDirectToolItem(toolItemId, modelService, application);
		}
		//
		return null;
	}

	@SuppressWarnings("unchecked")
	private MCommand getCommand() {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		if(modelService != null && application != null) {
			Object object = modelService.findElements(application, COMMAND_ID, MCommand.class, Collections.EMPTY_LIST).get(0);
			if(object instanceof MCommand) {
				return (MCommand)object;
			}
		}
		//
		return null;
	}

	private MHandledMenuItem getHandledItem(MMenu menu, String id) {

		MMenuElement menuElement = get(menu, id);
		if(menuElement instanceof MHandledMenuItem) {
			return (MHandledMenuItem)menuElement;
		}
		//
		return null;
	}

	private MDirectMenuItem getDirectItem(MMenu menu, String id) {

		MMenuElement menuElement = get(menu, id);
		if(menuElement instanceof MDirectMenuItem) {
			return (MDirectMenuItem)menuElement;
		}
		//
		return null;
	}

	private MMenuElement get(MMenu menu, String id) {

		for(MMenuElement menuElement : menu.getChildren()) {
			if(menuElement.getElementId().equals(id)) {
				return menuElement;
			}
		}
		//
		return null;
	}
}
