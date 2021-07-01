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
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayModel;

/**
 * This class stores information for an Evidence.
 * 
 * @author finterly
 */
public class Evidence extends PathwayElement {

	private String value; // optional
	private Xref xref;
	private UrlRef url; // optional
	/** evidenceRefs with this evidence as source */
	private List<EvidenceRef> evidenceRefs;

	/**
	 * Instantiates an Evidence pathway element given all possible parameters:
	 * elementId, parent pathway model, value, xref, and url.
	 * 
	 * NB: Manipulated the order of variables to overload constructor. This is not
	 * best practice, however variable inheritance complicates use of a builder.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param value        the name, term, or text of the evidence.
	 * @param xref         the evidence xref.
	 * @param url          the url of the evidence.
	 */
	public Evidence(PathwayModel pathwayModel, String elementId, String value, Xref xref, UrlRef url) {
		super(pathwayModel, elementId);
		this.value = value;
		this.xref = xref;
		this.url = url;
		this.evidenceRefs = new ArrayList<EvidenceRef>();

	}

	/**
	 * Instantiates an Evidence given all possible parameters except value.
	 */
	public Evidence(PathwayModel pathwayModel, String elementId, Xref xref, UrlRef url) {
		this(pathwayModel, elementId, null, xref, url);
	}

	/**
	 * Instantiates an Evidence given all possible parameters except url.
	 */
	public Evidence(PathwayModel pathwayModel, String elementId, String value, Xref xref) {
		this(pathwayModel, elementId, value, xref, null);
	}

	/**
	 * Instantiates an Evidence given all possible parameters except value and url.
	 */
	public Evidence(PathwayModel pathwayModel, String elementId, Xref xref) {
		this(pathwayModel, elementId, null, xref, null);
	}

	/**
	 * Returns the Evidence Xref.
	 * 
	 * @return xref the evidence xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the evidence.
	 * 
	 * @param xref the xref of the evidence.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

	/**
	 * Returns the name, term, or text of the evidence.
	 * 
	 * @return value the name, term, or text of the evidence.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the name, term, or text of the evidence.
	 * 
	 * @param value the name, term, or text of the evidence.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the url of the evidence.
	 * 
	 * @return url the url of the evidence.
	 */
	public UrlRef getUrl() {
		return url;
	}

	/**
	 * Sets the url of the evidence.
	 * 
	 * @param url the url of the evidence.
	 */
	public void setUrl(UrlRef url) {
		this.url = url;
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
	 * @param evidenceRef the evidenceRef to look for.
	 * @return true if has evidenceRef, false otherwise.
	 */
	public boolean hasEvidenceRef(EvidenceRef evidenceRef) {
		return evidenceRefs.contains(evidenceRef);
	}

	/**
	 * Adds the given evidenceRef to evidenceRefs list of the evidence.
	 * 
	 * @param evidenceRef the given evidenceRef to add.
	 */
	public void addEvidenceRef(EvidenceRef evidenceRef) {
		assert (evidenceRef != null) && (evidenceRef.getEvidence() == this);
		assert !hasEvidenceRef(evidenceRef);
		evidenceRefs.add(evidenceRef);
	}

	/**
	 * Removes the given evidenceRef from evidenceRefs list of the evidence. If
	 * evidenceRefs becomes empty, this evidence is removed from the pathway model
	 * because it is no longer referenced/used.
	 * 
	 * @param evidenceRef the given evidenceRef to remove.
	 */
	public void removeEvidenceRef(EvidenceRef evidenceRef) {
		evidenceRef.terminate();
		// remove this evidence from pathway model if empty TODO
		if (evidenceRefs.isEmpty()) {
			terminate();
		}
	}

	/**
	 * Removes all evidenceRefs from evidenceRefs list of the evidence.
	 */
	public void removeEvidenceRefs() {
		for (EvidenceRef evidenceRef : evidenceRefs) {
			removeEvidenceRef(evidenceRef);
		}
	}

	/**
	 * Terminates this evidence. The pathway model, if any, is unset from this
	 * evidence. Links to all evidenceRefs are removed from this evidence.
	 */
	@Override
	public void terminate() {
		unsetPathwayModel();
		removeEvidenceRefs();
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
		// checks if url link and description are equivalent
		if (url != null && evidence.getUrl() == null)
			return false;
		if (url == null && evidence.getUrl() != null)
			return false;
		if (url != null && evidence.getUrl() != null) {
			if (!Objects.equals(url.getLink(), evidence.getUrl().getLink()))
				return false;
			if (!Objects.equals(url.getDescription(), evidence.getUrl().getDescription()))
				return false;
		}
		// checks if value is equivalent
		if (!Objects.equals(value, evidence.getValue()))
			return false;
		// checks if evidence has the same evidenceRefs
		if (!Objects.equals(evidenceRefs, evidence.getEvidenceRefs()))
			return false;
		return true;
	}

}
