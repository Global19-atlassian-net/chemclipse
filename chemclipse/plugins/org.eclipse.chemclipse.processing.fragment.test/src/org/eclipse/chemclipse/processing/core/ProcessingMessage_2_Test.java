/*******************************************************************************
 * Copyright (c) 2012, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import junit.framework.TestCase;

public class ProcessingMessage_2_Test extends TestCase {

	private IProcessingMessage processingMessage;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		processingMessage = new ProcessingMessage(null, null, null);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetValues_1() {

		assertEquals(MessageType.ERROR, processingMessage.getMessageType());
	}

	public void testGetValues_2() {

		assertEquals("Description was null.", processingMessage.getDescription());
	}

	public void testGetValues_3() {

		assertEquals("Message was null.", processingMessage.getMessage());
	}
}
