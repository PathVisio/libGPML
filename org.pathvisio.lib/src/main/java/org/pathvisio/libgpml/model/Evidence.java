/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.libgpml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bridgedb.Xref;
import org.pathvisio.libgpml.model.PathwayElement.EvidenceRef;
import org.pathvisio.libgpml.model.type.ObjectType;
import org.pathvisio.libgpml.prop.StaticProperty;
import org.pathvisio.libgpml.util.Utils;
import org.pathvisio.libgpml.util.XrefUtils;

/**
 * This class stores information for an Evidence.
 *
 * @author finterly
 */
public class Evidence extends PathwayObject {

	private String value; // optional
	private Xref xref;
	private String urlLink; // optional
	/** evidenceRefs with this evidence as source */
	private List<EvidenceRef> evidenceRefs;

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates an Evidence pathway element given all possible parameters:
	 * elementId, parent pathway model, value, xref, and url.
	 *
	 * NB: Manipulated the order of variables to overload constructor.
	 *
	 * @param value   the name, term, or text of this evidence.
	 * @param xref    this evidence xref.
	 * @param urlLink the url of this evidence.
	 */
	protected Evidence(String value, Xref xref, String urlLink) {
		super();
		this.value = value;
		setXref(xref); // must be valid
		this.urlLink = urlLink;
		this.evidenceRefs = new ArrayList<EvidenceRef>();
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the object type of this pathway element.
	 *
	 * @return the object type.
	 */
	@Override
	public ObjectType getObjectType() {
		return ObjectType.EVIDENCE;
	}

	/**
	 * Returns the name, term, or text of this evidence.
	 *
	 * @return value the name, term, or text of this evidence.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the name, term, or text of this evidence.
	 *
	 * @param v the name, term, or text of this evidence.
	 */
	protected void setValue(String v) {
		if (v != null && Utils.stringEquals(value, v)) {
			value = v;
		}
	}

	/**
	 * Returns this evidence Xref.
	 *
	 * @return xref this evidence xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this evidence.
	 *
	 * @param v the xref of this evidence.
	 */
	protected void setXref(Xref v) {
		if (v == null) {
			throw new IllegalArgumentException("Evidence must have valid xref.");
		}
		if (v != null || xref != v) {
			xref = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
		}
	}

	/**
	 * Returns the url link for a web address.
	 *
	 * @return urlLink the url link.
	 */
	public String getUrlLink() {
		return urlLink;
	}

	/**
	 * Sets the url link for a web address.
	 *
	 * @param v the url link.
	 */
	protected void setUrlLink(String v) {
		if (v != null && !Utils.stringEquals(urlLink, v)) {
			urlLink = v;
		}
	}

	/**
	 * Returns the list of evidenceRefs which reference this evidence.
	 *
	 * @return evidenceRefs the list of evidenceRefs which reference this evidence.
	 */
	public List<EvidenceRef> getEvidenceRefs() {
		return evidenceRefs;
	}

	/**
	 * Check whether evidenceRefs has the given evidenceRef.
	 *
	 * @param evidenceRef this evidenceRef to look for.
	 * @return true if has evidenceRef, false otherwise.
	 */
	public boolean hasEvidenceRef(EvidenceRef evidenceRef) {
		return evidenceRefs.contains(evidenceRef);
	}

	/**
	 * Adds the given evidenceRef to evidenceRefs list of this evidence. NB: This
	 * method is not used directly.
	 *
	 * @param evidenceRef the given evidenceRef to add.
	 */
	protected void addEvidenceRef(EvidenceRef evidenceRef) {
		if (evidenceRef == null) {
			throw new IllegalArgumentException("Cannot add invalid evidenceRef to evidence.");
		}
		// add evidenceRef to evidenceRefs
		if (evidenceRef.getEvidence() == this && !hasEvidenceRef(evidenceRef))
			evidenceRefs.add(evidenceRef);
	}

	/**
	 * Removes the given evidenceRef from evidenceRefs list of this evidence. If
	 * evidenceRefs becomes empty, this evidence is removed from the pathway model
	 * because it is no longer referenced/used. NB: This method is not used
	 * directly.
	 *
	 * @param evidenceRef the given evidenceRef to remove.
	 */
	protected void removeEvidenceRef(EvidenceRef evidenceRef) {
		if (evidenceRef != null) {
			evidenceRefs.remove(evidenceRef);
			evidenceRef.terminate();
			// if citationResf empty, remove this evidence from pathway model
			if (evidenceRefs.isEmpty()) {
				pathwayModel.removeEvidence(this);
			}
		}
	}

	/**
	 * Removes all evidenceRefs from evidenceRefs list.
	 */
	private void removeEvidenceRefs() {
		for (int i = evidenceRefs.size() - 1; i >= 0; i--) {
			EvidenceRef ref = evidenceRefs.get(i);
			evidenceRefs.remove(ref);
			ref.terminate();
		}
	}

	/**
	 * Terminates this evidence. The pathway model, if any, is unset from this
	 * evidence. Links to all evidenceRefs are removed from this evidence.
	 */
	@Override
	protected void terminate() {
		removeEvidenceRefs();
		super.terminate();
	}

	// ================================================================================
	// Equals Methods
	// ================================================================================
	/**
	 * Compares this evidence to the given evidence. Checks all properties except
	 * evidenceRefs list to determine whether they are equal.
	 *
	 * @param evidence the evidence to compare to.
	 * @return true if evidences have equal properties, false otherwise.
	 */
	public boolean equalsEvidence(Evidence evidence) {
		// checks if value is equivalent
		if (!Objects.equals(value, evidence.getValue()))
			return false;
		// checks if xref is equivalent
		if (!XrefUtils.equivalentXrefs(xref, evidence.getXref())) {
			return false;
		}
		// checks if url link property equivalent
		if (!Utils.stringNullEqualsEmpty(urlLink, evidence.getUrlLink()))
			return false;
		return true;
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Copies values from the given source pathway element.
	 *
	 * <p>
	 * NB:
	 * <ol>
	 * <li>evidenceRefs list is not copied, evidenceRefs are created and added when
	 * an evidence is added to a pathway element.
	 * </ol>
	 *
	 * @param src the source pathway element.
	 */
	public void copyValuesFrom(Evidence src) {
		value = src.value;
		xref = src.xref;
		urlLink = src.urlLink;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copies this evidence.
	 *
	 * @return the new evidence copied from this evidence.
	 */
	public Evidence copyRef() {
		Evidence result = new Evidence(value, xref, urlLink);
		result.copyValuesFrom(this);
		return result;
	}

}
