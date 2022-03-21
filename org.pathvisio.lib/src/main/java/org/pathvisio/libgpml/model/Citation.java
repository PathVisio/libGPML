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
import org.pathvisio.libgpml.model.PathwayElement.CitationRef;
import org.pathvisio.libgpml.model.type.ObjectType;
import org.pathvisio.libgpml.prop.StaticProperty;
import org.pathvisio.libgpml.util.Utils;
import org.pathvisio.libgpml.util.XrefUtils;

/**
 * This class stores information for a Citation.
 * 
 * @author finterly
 */
public class Citation extends PathwayObject {

	/** One or both xref and/or Url link is required */
	private Xref xref;
	private String urlLink;
	/** Optional attributes for GPML2013a Biopax */
	private String title;
	private String source;
	private String year;
	private List<String> authors;
	/** citationRefs with this citation as source */
	private List<CitationRef> citationRefs;

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates a Citation pathway element given all possible parameters. NB: A
	 * citation much have either xref or url, or both.
	 * 
	 * @param xref    the citation xref.
	 * @param urlLink the url link and description (optional) for a web address.
	 */
	protected Citation(Xref xref, String urlLink) {
		super();
		if (xref == null && urlLink == null) {
			throw new IllegalArgumentException("Citation must have valid xref or url, or both.");
		}
		this.xref = xref;
		this.urlLink = urlLink;
		this.citationRefs = new ArrayList<CitationRef>();
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
		return ObjectType.CITATION;
	}
	
	/**
	 * Returns this Citation Xref.
	 * 
	 * @return xref this citation xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this citation.
	 * 
	 * @param v the xref of this citation.
	 */
	public void setXref(Xref v) {
		if (v != null) {
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
	public void setUrlLink(String v) {
		if (v != null && !Utils.stringEquals(urlLink, v)) {
			urlLink = v;
		}
	}

	/**
	 * Returns source for this citation (for GPML2013a Biopax).
	 * 
	 * @return title the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets source for this citation (for GPML2013a Biopax).
	 * 
	 * @param v the title.
	 */
	public void setTitle(String v) {
		if (v != null && !Utils.stringEquals(title, v)) {
			title = v;
		}
	}

	/**
	 * Returns source for this citation (for GPML2013a Biopax).
	 * 
	 * @return source the source.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets source for this citation (for GPML2013a Biopax).
	 * 
	 * @param v the source.
	 */
	public void setSource(String v) {
		if (v != null && !Utils.stringEquals(source, v)) {
			source = v;
		}
	}

	/**
	 * Returns year for this citation (for GPML2013a Biopax).
	 * 
	 * @return year the year.
	 */
	public String getYear() {
		return year;
	}

	/**
	 * Sets year for this citation (for GPML2013a Biopax).
	 * 
	 * @param v the year.
	 */
	public void setYear(String v) {
		if (v != null && !Utils.stringEquals(year, v)) {
			year = v;
		}
	}

	/**
	 * Returns list of authors for this citation (for GPML2013a Biopax).
	 * 
	 * @return authors the list of authors.
	 */
	public List<String> getAuthors() {
		return authors;
	}

	/**
	 * Sets list of authors for this citation (for GPML2013a Biopax).
	 * 
	 * @param authors the list of authors.
	 */
	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	/**
	 * Returns the list of citationRefs which reference this citation.
	 * 
	 * @return citationRefs the list of citationRefs which reference this citation.
	 */
	public List<CitationRef> getCitationRefs() {
		return citationRefs;
	}

	/**
	 * Check whether citationRefs has the given citationRef.
	 * 
	 * @param citationRef the citationRef to look for.
	 * @return true if has citationRef, false otherwise.
	 */
	public boolean hasCitationRef(CitationRef citationRef) {
		return citationRefs.contains(citationRef);
	}

	/**
	 * Adds the given citationRef to citationRefs list of this citation. NB: This
	 * method is not used directly.
	 * 
	 * @param citationRef the given citationRef to add.
	 */
	protected void addCitationRef(CitationRef citationRef) {
		if (citationRef == null) {
			throw new IllegalArgumentException("Cannot add invalid citationRef to citation.");
		}
		// add citationRef to citationRefs
		if (citationRef.getCitation() == this && !hasCitationRef(citationRef))
			citationRefs.add(citationRef);
	}

	/**
	 * Removes the given citationRef from citationRefs list of the citation. If
	 * citationRefs becomes empty, this citation is removed from the pathway model
	 * because it is no longer referenced/used. NB: This method is not used
	 * directly.
	 * 
	 * @param citationRef the given citationRef to remove.
	 */
	protected void removeCitationRef(CitationRef citationRef) {
		if (citationRef != null) {
			citationRefs.remove(citationRef);
			citationRef.terminate();
			// if citationResf empty, remove this citation from pathway model
			if (citationRefs.isEmpty()) {
				pathwayModel.removeCitation(this);
			}
		}
	}

	/**
	 * Removes all citationRefs from citationRefs list.
	 */
	private void removeCitationRefs() {
		for (int i = citationRefs.size() - 1; i >= 0; i--) {
			CitationRef ref = citationRefs.get(i);
			citationRefs.remove(ref);
			ref.terminate();
		}
	}

	/**
	 * Terminates this citation. The pathway model, if any, are unset from this
	 * citationRef. Links to all citationRefs are removed from this citationRef.
	 */
	@Override
	protected void terminate() {
		removeCitationRefs();
		super.terminate();
	}

	// ================================================================================
	// Equals Methods
	// ================================================================================
	/**
	 * Compares this citation to the given citation. Checks xref and url to
	 * determine whether they are equal.
	 * 
	 * NB: Optional properties (title, source, year, authors) are assumed to be the
	 * same if xref and url are equal.
	 * 
	 * @param citation the citation to compare to.
	 * @return true if citations have equal properties, false otherwise.
	 */
	public boolean equalsCitation(Citation citation) {
		// checks if xref is equivalent
		if (!XrefUtils.equivalentXrefs(xref, citation.getXref())) {
			return false;
		}
		// checks if url link property equivalent
		if (!Objects.equals(urlLink, citation.getUrlLink()))
			return false;
		return true;
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Note: doesn't change parent, only fields
	 *
	 * Used by UndoAction.
	 *
	 * NB: citationRefs list is not copied, citationRefs are created and added when
	 * a citation is added to a pathway element.
	 * 
	 * @param src
	 */
	public void copyValuesFrom(Citation src) { // TODO
		xref = src.xref;
		urlLink = src.urlLink;
		title = src.title;
		source = src.source;
		year = src.year;
		authors = src.authors;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public Citation copyRef() {
		Citation result = new Citation(xref, urlLink);
		result.copyValuesFrom(this);
		return result;
	}

}
