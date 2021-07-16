/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2021 BiGCaT Bioinformatics, WikiPathways
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.pathvisio.model.ref;

/**
 * This class stores information for a EvidenceRef which references an
 * {@link Evidence}.
 * 
 * @author finterly
 */
public class EvidenceRef {

	private Evidence evidence; // source evidence, elementRef in GPML
	private Evidenceable evidenceable; // target pathway, pathway element, or evidenceRef

	/**
	 * Instantiates an EvidenceRef given source {@link Evidence} and initializes
	 * evidenceRefs lists.
	 * 
	 * @param evidence the source evidence this EvidenceRef refers to.
	 */
	public EvidenceRef(Evidence evidence) {
		this.setEvidenceTo(evidence);
	}

	/**
	 * Returns the evidence referenced.
	 * 
	 * @return evidence the evidence referenced.
	 */
	public Evidence getEvidence() {
		return evidence;
	}

	/**
	 * Checks whether this evidenceRef has a source evidence.
	 *
	 * @return true if and only if the evidence of this evidenceRef is effective.
	 */
	public boolean hasEvidence() {
		return getEvidence() != null;
	}

	/**
	 * Sets the source evidence for this evidenceRef. Adds this evidencRef to the
	 * source evidence.
	 * 
	 * @param evidence the given source evidence to set.
	 */
	public void setEvidenceTo(Evidence evidence) {
		if (evidence == null)
			throw new IllegalArgumentException("Invalid evidence.");
		if (hasEvidence())
			throw new IllegalStateException("EvidenceRef already has a source citation.");
		setEvidence(evidence);
		if (!evidence.hasEvidenceRef(this))
			evidence.addEvidenceRef(this);
	}

	/**
	 * Sets the source evidence for this evidenceRef.
	 * 
	 * @param evidence the given source evidence to set.
	 */
	private void setEvidence(Evidence evidence) {
		this.evidence = evidence;
	}

	/**
	 * Unsets the evidence, if any, from this evidenceRef. Removes this evidenceRef
	 * from the source evidence.
	 */
	public void unsetEvidence() {
		if (hasEvidence()) {
			Evidence evidence = getEvidence();
			setEvidence(null);
			if (evidence.hasEvidenceRef(this))
				evidence.removeEvidenceRef(this);
		}
	}

	/**
	 * Returns the target pathway, pathway element, or evidenceRef
	 * {@link Evidenceable} for this evidenceRef.
	 * 
	 * @return evidenceable the target of the evidenceRef.
	 */
	public Evidenceable getEvidenceable() {
		return evidenceable;
	}

	/**
	 * Checks whether this evidenceRef has a target evidenceable.
	 *
	 * @return true if and only if the evidenceable of this evidenceRef is
	 *         effective.
	 */
	public boolean hasEvidenceable() {
		return getEvidenceable() != null;
	}

	/**
	 * Sets the target pathway, pathway element, or evidenceRef {@link Evidenceable}
	 * for this evidenceRef.
	 * 
	 * @param evidenceable the given target evidenceable to set.
	 */
	public void setEvidenceableTo(Evidenceable evidenceable) {
		if (evidenceable == null)
			throw new IllegalArgumentException("Invalid evidenceable.");
		if (hasEvidenceable())
			throw new IllegalStateException("EvidenceRef already has a target evidenceable.");
		setEvidenceable(evidenceable);
	}

	/**
	 * Sets the target pathway, pathway element, or evidenceRef {@link Evidenceable}
	 * to which the evidenceRef belongs.
	 * 
	 * @param evidenceable the given target evidenceable to set.
	 */
	private void setEvidenceable(Evidenceable evidenceable) {
		this.evidenceable = evidenceable;
	}

	/**
	 * Unsets the evidenceable, if any, from this evidenceRef.
	 */
	public void unsetEvidenceable() {
		if (hasEvidenceable()) {
			Evidenceable evidenceable = getEvidenceable();
			setEvidenceable(null);
			if (evidenceable.hasEvidenceRef(this))
				evidenceable.removeEvidenceRef(this);
		}
	}

	/**
	 * Terminates this evidenceRef. The evidence and evidenceable, if any, are unset
	 * from this evidenceRef. Links to all evidenceRefs are removed from this
	 * evidenceRef.
	 */
	public void terminate() {
		unsetEvidence();
		unsetEvidenceable();
	}
}
