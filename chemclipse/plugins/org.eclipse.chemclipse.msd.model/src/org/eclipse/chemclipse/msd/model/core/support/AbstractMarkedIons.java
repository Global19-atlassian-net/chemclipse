/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;

public class AbstractMarkedIons implements IMarkedIons {

	private Set<IMarkedIon> markedIons;

	public AbstractMarkedIons() {
		markedIons = new HashSet<IMarkedIon>();
	}

	@Override
	public boolean add(IMarkedIon e) {

		return markedIons.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends IMarkedIon> c) {

		return markedIons.addAll(c);
	}

	@Override
	public void clear() {

		markedIons.clear();
	}

	@Override
	public boolean contains(Object o) {

		return markedIons.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {

		return markedIons.containsAll(c);
	}

	@Override
	public boolean isEmpty() {

		return markedIons.isEmpty();
	}

	@Override
	public Iterator<IMarkedIon> iterator() {

		return markedIons.iterator();
	}

	@Override
	public boolean remove(Object o) {

		return markedIons.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {

		return markedIons.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {

		return markedIons.retainAll(c);
	}

	@Override
	public int size() {

		return markedIons.size();
	}

	@Override
	public Object[] toArray() {

		return markedIons.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {

		return markedIons.toArray(a);
	}

	@Override
	public Set<Integer> getIonsNominal() {

		Set<Integer> nominalIons = new HashSet<Integer>();
		for(IMarkedIon markedIon : markedIons) {
			nominalIons.add(AbstractIon.getIon(markedIon.getIon()));
		}
		return nominalIons;
	}

	@Override
	public void add(int ionStart, int ionStop) {

		if(ionStart > ionStop) {
			int tmp = ionStart;
			ionStart = ionStop;
			ionStop = tmp;
		}
		for(int i = ionStart; i <= ionStop; i++) {
			markedIons.add(new MarkedIon(i));
		}
	}
}
