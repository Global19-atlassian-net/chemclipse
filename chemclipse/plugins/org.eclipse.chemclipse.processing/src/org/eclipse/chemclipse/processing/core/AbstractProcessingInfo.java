/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public abstract class AbstractProcessingInfo implements IProcessingInfo {

	private List<IProcessingMessage> processingMessages;
	private Object processingResult;

	public AbstractProcessingInfo() {
		processingMessages = new ArrayList<IProcessingMessage>();
	}

	public AbstractProcessingInfo(IProcessingInfo processingInfo) {
		this();
		addMessages(processingInfo);
	}

	@Override
	public void addMessages(IProcessingInfo processingInfo) {

		if(processingInfo != null && processingInfo != this) {
			/*
			 * Add each message to this message queue.
			 */
			for(IProcessingMessage message : processingInfo.getMessages()) {
				addMessage(message);
			}
		}
	}

	@Override
	public void addMessage(IProcessingMessage processingMessage) {

		processingMessages.add(processingMessage);
	}

	@Override
	public void addInfoMessage(String description, String message) {

		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.INFO, description, message);
		addMessage(processingMessage);
	}

	@Override
	public void addWarnMessage(String description, String message) {

		addWarnMessage(description, message, "");
	}

	@Override
	public void addWarnMessage(String description, String message, String proposedSolution) {

		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.WARN, description, message, proposedSolution);
		addMessage(processingMessage);
	}

	@Override
	public void addErrorMessage(String description, String message) {

		addErrorMessage(description, message, "");
	}

	@Override
	public void addErrorMessage(String description, String message, String proposedSolution) {

		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, description, message, proposedSolution);
		addMessage(processingMessage);
	}

	@Override
	public List<IProcessingMessage> getMessages() {

		return processingMessages;
	}

	@Override
	public void setProcessingResult(Object processingResult) {

		this.processingResult = processingResult;
	}

	@Override
	public Object getProcessingResult() {

		return processingResult;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProcessingResult(Class<T> type) throws TypeCastException {

		if(type.isInstance(processingResult)) {
			return (T)processingResult;
		} else {
			Class<?> actualClass = (processingResult == null) ? new Exception("NULL").getClass() : processingResult.getClass();
			throw createTypeCastException("Processing Info", actualClass, type);
		}
	}

	@Override
	public boolean hasErrorMessages() {

		for(IProcessingMessage processingMessage : processingMessages) {
			if(processingMessage.getMessageType().equals(MessageType.ERROR)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasWarnMessages() {

		for(IProcessingMessage processingMessage : processingMessages) {
			if(processingMessage.getMessageType().equals(MessageType.WARN)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public TypeCastException createTypeCastException(String description, Class<?> actual, Class<?> expected) {

		String message = "Failed to cast from " + actual + " to " + expected;
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, description, message);
		addMessage(processingMessage);
		return new TypeCastException(message);
	}
}
