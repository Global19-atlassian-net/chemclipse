/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public abstract class AbstractComparisonResult implements IComparisonResult {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 7832661625546592609L;
	//
	private boolean isMatch = true;
	private float matchFactor;
	private float matchFactorDirect;
	private float reverseMatchFactor;
	private float reverseMatchFactorDirect;
	private float probability;
	private float penalty;
	private String advise = "";

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect) {
		this.matchFactor = matchFactor;
		this.reverseMatchFactor = reverseMatchFactor;
		this.matchFactorDirect = matchFactorDirect;
		this.reverseMatchFactorDirect = reverseMatchFactorDirect;
		determineAdvise();
	}

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect, float probability) {
		this(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect);
		if(probability >= MIN_ALLOWED_PROBABILITY && probability <= MAX_ALLOWED_PROBABILITY) {
			this.probability = probability;
		}
	}

	public float getPenalty() {

		return penalty;
	}

	@Override
	public void clearPenalty() {

		this.penalty = 0;
	}

	@Override
	public void setPenalty(float penalty) {

		this.penalty += penalty;
	}

	@Override
	public boolean isMatch() {

		return isMatch;
	}

	@Override
	public void setMatch(boolean match) {

		this.isMatch = match;
	}

	@Override
	public float getMatchFactor() {

		return getAdjustedValue(matchFactor, penalty);
	}

	@Override
	public float getMatchFactorDirect() {

		return matchFactorDirect;
	}

	public float getMatchFactorNotAdjusted() {

		return matchFactor;
	}

	public float getMatchFactorDirectNotAdjusted() {

		return matchFactorDirect;
	}

	private static float getAdjustedValue(float value, float penalty) {

		float result = value - penalty;
		if(result < 0) {
			return 0;
		}
		return result;
	}

	@Override
	public void adjustMatchFactor(float penalty) {

		if(penalty >= MIN_ALLOWED_PENALTY && penalty <= MAX_ALLOWED_PENALTY) {
			this.penalty = penalty;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public float getReverseMatchFactor() {

		return getAdjustedValue(reverseMatchFactor, penalty);
	}

	@Override
	public float getReverseMatchFactorDirect() {

		return reverseMatchFactorDirect;
	}

	@Override
	public void adjustReverseMatchFactor(float penalty) {

		if(penalty >= MIN_ALLOWED_PENALTY && penalty <= MAX_ALLOWED_PENALTY) {
			this.penalty = penalty;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public float getProbability() {

		return probability;
	}

	@Override
	public String getAdvise() {

		return advise;
	}

	@Override
	public float getRating() {

		float rating = (matchFactor + reverseMatchFactor) / 2.0f;
		/*
		 * Shall the probability be used too?
		 */
		if(matchFactorDirect > 0.0f) {
			rating = (rating + matchFactorDirect) / 2.0f;
		}
		//
		if(reverseMatchFactorDirect > 0.0f) {
			rating = (rating + reverseMatchFactorDirect) / 2.0f;
		}
		//
		return rating;
	}

	// ----------------------------------------private methods
	/**
	 * Determines the advise.
	 */
	private void determineAdvise() {

		if(getMatchFactor() >= MAX_LIMIT_MATCH_FACTOR && getReverseMatchFactor() <= MIN_LIMIT_REVERSE_MATCH_FACTOR) {
			advise = ADVISE_INCOMPLETE;
		} else if(getMatchFactor() <= MIN_LIMIT_MATCH_FACTOR && getReverseMatchFactor() >= MAX_LIMIT_REVERSE_MATCH_FACTOR) {
			advise = ADVISE_IMPURITIES;
		}
	}

	// ----------------------------hashCode, equals, toString
	@Override
	public boolean equals(Object other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(this.getClass() != other.getClass()) {
			return false;
		}
		IComparisonResult otherResult = (IComparisonResult)other;
		return getMatchFactor() == otherResult.getMatchFactor() && getReverseMatchFactor() == otherResult.getReverseMatchFactor() && getProbability() == otherResult.getProbability();
	}

	@Override
	public int hashCode() {

		return 7 * Float.valueOf(getMatchFactor()).hashCode() + 11 * Float.valueOf(getReverseMatchFactor()).hashCode() + 13 * Float.valueOf(probability).hashCode();
	}

	@Override
	public String toString() {

		return "AbstractComparisonResult [isMatch=" + isMatch + ", matchFactor=" + matchFactor + ", matchFactorDirect=" + matchFactorDirect + ", reverseMatchFactor=" + reverseMatchFactor + ", reverseMatchFactorDirect=" + reverseMatchFactorDirect + ", probability=" + probability + ", penalty=" + penalty + ", advise=" + advise + "]";
	}
}
