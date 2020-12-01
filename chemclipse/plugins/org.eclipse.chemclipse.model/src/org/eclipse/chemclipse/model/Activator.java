/*******************************************************************************
 * Copyright (c) 2010, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.support.settings.serialization.JSONSerialization;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final Logger logger = Logger.getLogger(Activator.class);
	//
	private static Activator activator;
	private IEclipseContext eclipseContext = null;
	private BundleContext bundleContext;

	public static Activator getDefault() {

		return activator;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {

		JSONSerialization.addMapping(IProcessMethod.class, ProcessMethod.class);
		JSONSerialization.addMapping(IProcessEntry.class, ProcessEntry.class);
		this.bundleContext = bundleContext;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {

		JSONSerialization.removeMapping(IProcessMethod.class, ProcessMethod.class);
		JSONSerialization.removeMapping(IProcessEntry.class, ProcessEntry.class);
		this.bundleContext = null;
	}

	public BundleContext getBundleContext() {

		return bundleContext;
	}

	public IEventBroker getEventBroker() {

		IEclipseContext eclipseContext = getEclipseContext();
		return eclipseContext.get(IEventBroker.class);
	}

	public IEclipseContext getEclipseContext() {

		if(eclipseContext == null) {
			/*
			 * Create and initialize the context.
			 */
			eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
			eclipseContext.set(Logger.class, logger);
		}
		//
		return eclipseContext;
	}
}
