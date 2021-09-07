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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bridgedb.Xref;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.PathwayObject;
import org.pathvisio.props.StaticProperty;
import org.pathvisio.util.Utils;

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
	 * @param value the name, term, or text of this evidence.
	 * @param xref  this evidence xref.
	 * @param url   the url of this evidence.
	 */
	public Evidence(String value, Xref xref, String url) {
		super();
		this.value = value;
		this.xref = xref;
		this.urlLink = url;
		this.evidenceRefs = new ArrayList<EvidenceRef>();

	}

	/**
	 * Instantiates an Evidence given all possible parameters except value.
	 */
	public Evidence(Xref xref, String url) {
		this(null, xref, url);
	}

	/**
	 * Instantiates an Evidence given all possible parameters except url.
	 */
	public Evidence(String value, Xref xref) {
		this(value, xref, null);
	}

	/**
	 * Instantiates an Evidence given all possible parameters except value and url.
	 */
	public Evidence(Xref xref) {
		this(null, xref, null);
	}

	// ================================================================================
	// Accessors
	// ================================================================================
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
	public void setValue(String v) {
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
	public void setXref(Xref v) {
		if (v != null) {
			xref = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
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
	public void setUrlLink(String v) {
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
		assert (evidenceRef != null);
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
		assert (evidenceRef != null);
		Evidence evidence = evidenceRef.getEvidence();
		// remove citationRef from this citation
		if (evidence == this || evidence == null && hasEvidenceRef(evidenceRef)) {
			evidenceRefs.remove(evidenceRef);
			evidenceRef.terminate();
		}
		// remove this evidence from pathway model if empty!
		if (evidenceRefs.isEmpty())
			getPathwayModel().removeEvidence(this);
	}

	/**
	 * Removes all evidenceRefs from evidenceRefs list of this evidence.
	 */
	public void removeEvidenceRefs() {
		for (int i = 0; i < evidenceRefs.size(); i++) {
			removeEvidenceRef(evidenceRefs.get(i));
		}
	}

	/**
	 * Terminates this evidence. The pathway model, if any, is unset from this
	 * evidence. Links to all evidenceRefs are removed from this evidence.
	 */
	@Override
	public void terminate() {
		removeEvidenceRefs();
		unsetPathwayModel();
	}

	/**
	 * Checks all properties of given evidences to determine whether they are equal.
	 * 
	 * TODO
	 * 
	 * @param evidence the evidence to compare to.
	 * @return true if evidences have equal properties, false otherwise.
	 */
	public boolean equalsEvidence(Evidence evidence) {
		// checks if value is equivalent
		if (!Objects.equals(value, evidence.getValue()))
			return false;
		// checks if xref is equivalent
		if (xref != null && evidence.getXref() == null)
			return false;
		if (xref == null && evidence.getXref() != null)
			return false;
		if (xref != null && evidence.getXref() != null) {
			if (!Objects.equals(xref.getId(), evidence.getXref().getId()))
				return false;
			if (!Objects.equals(xref.getDataSource(), evidence.getXref().getDataSource()))
				return false;
		}
		// checks if url link property equivalent
		if (!Objects.equals(urlLink, evidence.getUrlLink()))
			return false;
		// checks if evidence has the same evidenceRefs
		if (!Objects.equals(evidenceRefs, evidence.getEvidenceRefs()))
			return false;
		return true;
	}

}
