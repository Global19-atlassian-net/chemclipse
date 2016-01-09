/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import java.util.List;

import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IProcessingInfo {

	/**
	 * Adds all message from {@link IProcessingInfo} to this processing info
	 * instance.
	 * 
	 * @param processingInfo
	 */
	void addMessages(IProcessingInfo processingInfo);

	/**
	 * Adds a message to the processing info.
	 * 
	 * @param processingMessage
	 */
	void addMessage(IProcessingMessage processingMessage);

	/**
	 * Adds an info message to the processing info.
	 * 
	 * @param description
	 * @param message
	 */
	void addInfoMessage(String description, String message);

	/**
	 * Adds a warn message to the processing info.
	 * 
	 * @param description
	 * @param message
	 */
	void addWarnMessage(String description, String message);

	/**
	 * Adds an error message to the processing info.
	 * 
	 * @param description
	 * @param message
	 */
	void addErrorMessage(String description, String message);

	/**
	 * Returns the list of messages.
	 * 
	 * @return the list of messages
	 */
	List<IProcessingMessage> getMessages();

	/**
	 * Sets the processing result. Each plug-in knows which instance it expects
	 * as the return.
	 * 
	 * @param processingResult
	 */
	void setProcessingResult(Object processingResult);

	/**
	 * Returns the processing result.
	 * May return null.
	 * 
	 * @return Object
	 */
	Object getProcessingResult();

	/**
	 * Returns whether the process info stores error message or not.
	 * 
	 * @return boolean
	 */
	boolean hasErrorMessages();

	/**
	 * Returns whether the process info stores warn message or not.
	 * 
	 * @return boolean
	 */
	boolean hasWarnMessages();

	/**
	 * Creates a new TypeCastException and adds a IProcessingMessage.
	 * 
	 * @param clazz
	 * @param String
	 * @return {@link TypeCastException}
	 */
	TypeCastException createTypeCastException(String description, Class<?> clazz);
}
