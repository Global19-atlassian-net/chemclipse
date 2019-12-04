/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - refactoring for new method API, optimize E4 access
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.support.ui.workbench.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.ProcessMethodNotifications;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ProcessMethodEditor implements IModificationHandler {

	public static final String SNIPPET_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.processMethodEditor";
	//
	@Inject
	private MPart part;
	//
	private File processMethodFile;
	private ExtendedMethodUI extendedMethodUI;
	@Inject
	private ProcessMethodNotifications notifications;
	@Inject
	private PartSupport partsupport;
	// @Inject
	// @Optional
	private IProcessMethod currentProcessMethod;
	@Inject
	private ProcessSupplierContext processSupplierContext;

	@Focus
	public void setFocus() {

		extendedMethodUI.setFocus();
	}

	@PreDestroy
	private void preDestroy() {

		partsupport.closePart(part);
	}

	@Persist
	public void save(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		File saveFile = getSaveFile(shell);
		if(saveFile != null) {
			IProcessMethod oldMethod = currentProcessMethod;
			IProcessMethod editedMethod = extendedMethodUI.getProcessMethod();
			ProcessMethod newMethod = new ProcessMethod(editedMethod);
			newMethod.setSourceFile(saveFile);
			// copy the UUID from the old method to keep the file consistent
			newMethod.setUUID(oldMethod.getUUID());
			// copy the readOnlyFlag
			if(editedMethod.isFinal()) {
				newMethod.setReadOnly(editedMethod.isFinal());
			}
			IProcessingInfo<?> info = MethodConverter.convert(saveFile, newMethod, MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
			if(info.hasErrorMessages()) {
				ProcessingInfoViewSupport.updateProcessingInfo(info);
			} else {
				part.setDirty(false);
				currentProcessMethod = newMethod;
				notifications.updated(newMethod, oldMethod);
				processMethodFile = saveFile;
				part.setLabel(processMethodFile.getName() + " " + currentProcessMethod.getDataCategories());
			}
		}
	}

	private File getSaveFile(Shell shell) {

		if(processMethodFile != null && processMethodFile.exists()) {
			return processMethodFile;
		}
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setOverwrite(true);
		fileDialog.setText("Save Process Method as");
		String methodName = extendedMethodUI.getMethodName();
		if(methodName.isEmpty()) {
			fileDialog.setFileName(MethodConverter.DEFAULT_METHOD_FILE_NAME);
		} else {
			fileDialog.setFileName(methodName + "." + MethodConverter.DEFAULT_METHOD_FILE_NAME_EXTENSION);
		}
		fileDialog.setFilterExtensions(MethodConverter.DEFAULT_METHOD_FILE_EXTENSIONS);
		fileDialog.setFilterNames(MethodConverter.DEFAULT_METHOD_FILE_NAMES);
		File userDirectory = MethodConverter.getUserMethodDirectory();
		if(userDirectory.exists()) {
			fileDialog.setFilterPath(MethodConverter.getUserMethodDirectory().getAbsolutePath());
		}
		String selectedFile = fileDialog.open();
		if(selectedFile != null) {
			File file = new File(selectedFile);
			if(!userDirectory.exists()) {
				MethodConverter.setUserMethodDirectory(file.getParentFile());
			}
			return file;
		}
		return null;
	}

	@PostConstruct
	public void initialize(Composite parent, @Optional IProcessMethod processMethod, @Optional File editorInput) {

		if(processMethod == null && editorInput == null) {
			throw new IllegalStateException("no method file and no processmethod specified for part");
		}
		if(editorInput != null) {
			currentProcessMethod = Adapters.adapt(editorInput, IProcessMethod.class);
			processMethodFile = editorInput;
			if(currentProcessMethod == null) {
				throw new RuntimeException("can't read file " + processMethodFile);
			}
		} else {
			currentProcessMethod = processMethod;
		}
		DataCategory[] categories = currentProcessMethod.getDataCategories().toArray(new DataCategory[]{});
		if(categories == null || categories.length == 0) {
			// for backward compatibility
			categories = new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD};
		}
		part.setLabel(part.getLabel() + " " + Arrays.asList(categories));
		extendedMethodUI = new ExtendedMethodUI(parent, SWT.NONE, processSupplierContext, categories);
		extendedMethodUI.setModificationHandler(this);
		extendedMethodUI.setProcessMethod(currentProcessMethod);
		extendedMethodUI.setHeaderToolbarVisible(processMethodFile == null);
	}

	@Override
	public void setDirty(boolean dirty) {

		part.setDirty(processMethodFile == null || !extendedMethodUI.getProcessMethod().contentEquals(currentProcessMethod, true));
	}
}
