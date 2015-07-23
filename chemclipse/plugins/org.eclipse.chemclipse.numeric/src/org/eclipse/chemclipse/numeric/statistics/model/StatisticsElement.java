/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.statistics.model;

import java.util.List;

public class StatisticsElement<T> implements IStatisticsElement<T> {

	private Object identifier;
	private List<IStatisticsSourceObject<T>> sourceElements;
	private Object content; // this is either an IStatistics object or a List<StatisticsElement>

	public StatisticsElement(Object identifier, List<IStatisticsSourceObject<T>> sourceElements) {

		this.identifier = identifier;
		this.sourceElements = sourceElements;
	}

	public StatisticsElement(Object identifier, List<IStatisticsSourceObject<T>> sourceElements, IStatistics statistics) {

		this(identifier, sourceElements);
		this.content = statistics;
	}

	public StatisticsElement(Object identifier, List<IStatisticsSourceObject<T>> sourceElements, List<StatisticsElement<T>> elements) {

		this(identifier, sourceElements);
		this.content = elements;
	}

	@Override
	public Object getIdentifier() {

		return identifier;
	}

	@Override
	public void setIdentifier(Object identifier) {

		this.identifier = identifier;
	}

	@Override
	public List<IStatisticsSourceObject<T>> getSourceElements() {

		return sourceElements;
	}

	@Override
	public void setSourceElements(List<IStatisticsSourceObject<T>> sourceElements) {

		this.sourceElements = sourceElements;
	}

	@Override
	public boolean isContentStatistics() {

		if(content instanceof IStatistics) {
			return true;
		}
		return false;
	}

	@Override
	public IStatistics getStatisticsContent() {

		if(isContentStatistics()) {
			return (IStatistics)content;
		}
		/*
		 * Should we throw instead an exception?
		 */
		return null;
	}

	@Override
	public void setStatisticsContent(IStatistics content) {

		this.content = content;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<StatisticsElement<T>> getStatisticsElements() {

		if(content instanceof List) {
			return (List<StatisticsElement<T>>)content;
		}
		/*
		 * Should we throw instead an exception?
		 */
		return null;
	}

	@Override
	public void setStatisticsElements(List<StatisticsElement<T>> content) {

		this.content = content;
	}
}
