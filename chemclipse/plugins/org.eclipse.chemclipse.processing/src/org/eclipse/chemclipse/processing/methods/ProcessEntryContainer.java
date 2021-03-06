/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

import java.util.Iterator;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.processing.methods.SubProcessExecutionConsumer.SubProcess;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

/**
 * A {@link ProcessEntryContainer} holds some {@link IProcessEntry}s
 *
 */
public interface ProcessEntryContainer extends Iterable<IProcessEntry> {

	/**
	 * 
	 * @return an informative name describing the container
	 */
	String getName();

	/**
	 * return an informative description of this container
	 */
	default String getDescription() {

		return "";
	}

	int getNumberOfEntries();

	/**
	 * Compares that this all contained {@link IProcessEntry}s are equal to the other one given, the default implementation works as follows:
	 * <ol>
	 * <li>if other is null, and this container does not contains any entry, <code>true</code> is returned</li>
	 * <li>if any entry is not contentEquals to the other one <code>false</code> is returned</li>
	 * <li>if any of the iterator return more elements than the other <code>false</code> is returned
	 * </ol>
	 * 
	 * this method is different to {@link #equals(Object)} that it does compares for user visible properties to be equal in contrast to objects identity and it allows to compare different instance type, this also means that it is not required that
	 * Object1.contentEquals(Object2} == Object2.contentEquals(Object1}
	 * 
	 * @param other
	 * @return
	 */
	default boolean entriesEquals(ProcessEntryContainer other) {

		Iterator<IProcessEntry> thisEntries = iterator();
		if(other == null) {
			return !thisEntries.hasNext();
		}
		Iterator<IProcessEntry> otherEntries = other.iterator();
		while(thisEntries.hasNext() && otherEntries.hasNext()) {
			IProcessEntry thisEntry = thisEntries.next();
			IProcessEntry otherEntry = otherEntries.next();
			if(!thisEntry.contentEquals(otherEntry)) {
				return false;
			}
		}
		if(otherEntries.hasNext() || thisEntries.hasNext()) {
			// not all where consumed
			return false;
		}
		return true;
	}

	static <X, T> T applyProcessEntries(ProcessEntryContainer container, ProcessExecutionContext context, ProcessExecutionConsumer<T> consumer) {

		return applyProcessEntries(container, context, new BiFunction<IProcessEntry, IProcessSupplier<X>, ProcessorPreferences<X>>() {

			@Override
			public ProcessorPreferences<X> apply(IProcessEntry processEntry, IProcessSupplier<X> processSupplier) {

				return processEntry.getPreferences(processSupplier);
			}
		}, consumer);
	}

	static <X, T> T applyProcessEntries(ProcessEntryContainer container, ProcessExecutionContext context, BiFunction<IProcessEntry, IProcessSupplier<X>, ProcessorPreferences<X>> preferenceSupplier, ProcessExecutionConsumer<T> consumer) {

		context.setWorkRemaining(container.getNumberOfEntries());
		for(IProcessEntry processEntry : container) {
			IProcessSupplier<X> processor = context.getSupplier(processEntry.getProcessorId());
			if(processor == null) {
				context.addWarnMessage(processEntry.getName(), "processor not found, will be skipped");
				continue;
			}
			try {
				ProcessorPreferences<X> processorPreferences = preferenceSupplier.apply(processEntry, processor);
				context.setContextObject(IProcessEntry.class, processEntry);
				context.setContextObject(IProcessSupplier.class, processor);
				context.setContextObject(ProcessExecutionConsumer.class, consumer);
				context.setContextObject(ProcessorPreferences.class, processorPreferences);
				ProcessExecutionContext entryContext = context.split(processor.getContext());
				try {
					if(processEntry.getNumberOfEntries() > 0) {
						IProcessSupplier.applyProcessor(processorPreferences, new SubProcessExecutionConsumer<T>(consumer, new SubProcess<T>() {

							@Override
							public <SubX> void execute(ProcessorPreferences<SubX> preferences, ProcessExecutionConsumer<T> parent, ProcessExecutionContext subcontext) {

								applyProcessEntries(processEntry, subcontext, preferenceSupplier, parent);
							}
						}), entryContext);
					} else {
						IProcessSupplier.applyProcessor(processorPreferences, consumer, entryContext);
					}
				} finally {
					context.setContextObject(IProcessSupplier.class, null);
					context.setContextObject(IProcessEntry.class, null);
					context.setContextObject(ProcessExecutionConsumer.class, null);
					context.setContextObject(ProcessorPreferences.class, null);
				}
			} catch(RuntimeException e) {
				context.addErrorMessage(processEntry.getName(), "internal error", e);
			}
		}
		// return the final result
		return consumer.getResult();
	}
}
