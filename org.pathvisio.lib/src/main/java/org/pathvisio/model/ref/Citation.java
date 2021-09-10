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
import org.pathvisio.model.ref.PathwayElement.CitationRef;
import org.pathvisio.props.StaticProperty;
import org.pathvisio.util.Utils;


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
	 * Instantiates a Citation pathway element given all possible parameters.
	 * 
	 * @param xref    the citation xref.
	 * @param urlLink the url link and description (optional) for a web address.
	 */
	public Citation(Xref xref, String urlLink) {
		super();
		this.xref = xref;
		this.urlLink = urlLink;
		this.citationRefs = new ArrayList<CitationRef>();
	}

	/**
	 * Instantiates a Citation pathway element given all possible parameters except
	 * xref. A citation much have either xref or url, or both.
	 */
	public Citation(String urlLink) {
		this(null, urlLink);
	}

	/**
	 * Instantiates a Citation pathway element given all possible parameters except
	 * url. A citation much have either xref or url, or both.
	 */
	public Citation(Xref xref) {
		this(xref, null);
	}

	// ================================================================================
	// Accessors
	// ================================================================================
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
		assert (citationRef != null);
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
		assert (citationRef != null);
		Citation citation = citationRef.getCitation();
		// remove citationRef from this citation
		if (citation == this || citation == null && hasCitationRef(citationRef)) {
			citationRefs.remove(citationRef);
			citationRef.terminate();
		}
		// remove this citation from pathway model if empty!
		if (citationRefs.isEmpty())
			getPathwayModel().removeCitation(this);
	}

	/**
	 * Removes all citationRefs from citationRefs list of the citation.
	 */
	public void removeCitationRefs() {
		for (int i = 0; i < citationRefs.size(); i++) {
			removeCitationRef(citationRefs.get(i));
		}
	}

	/**
	 * Terminates this citation. The pathway model, if any, are unset from this
	 * citationRef. Links to all citationRefs are removed from this citationRef.
	 */
	@Override
	public void terminate() {
		removeCitationRefs();
		unsetPathwayModel();
	}

	// ================================================================================
	// Equals Methods
	// ================================================================================
	/**
	 * Checks all properties of given citations to determine whether they are equal.
	 * 
	 * TODO
	 * 
	 * @param citation the citation to compare to.
	 * @return true if citations have equal properties, false otherwise.
	 */
	public boolean equalsCitation(Citation citation) {
		// checks if xref is equivalent
		if (xref != null && citation.getXref() == null)
			return false;
		if (xref == null && citation.getXref() != null)
			return false;
		if (xref != null && citation.getXref() != null) {
			if (!Objects.equals(xref.getId(), citation.getXref().getId()))
				return false;
			if (!Objects.equals(xref.getDataSource(), citation.getXref().getDataSource()))
				return false;
		}
		// checks if url link property equivalent
		if (!Objects.equals(urlLink, citation.getUrlLink()))
			return false;
		// checks if citation has the same citationRefs
		if (!Objects.equals(citationRefs, citation.getCitationRefs()))
			return false;
		// checks if optional GPML2013a properties are equivalent
		if (!Objects.equals(title, citation.getTitle()))
			return false;
		if (!Objects.equals(source, citation.getSource()))
			return false;
		if (!Objects.equals(year, citation.getYear()))
			return false;
		if (!Objects.equals(authors, citation.getAuthors()))
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
	 * @param src
	 */
	public void copyValuesFrom(Citation src) { // TODO
//		super.copyValuesFrom(src);
		xref = src.xref;
		urlLink = src.urlLink;
		title = src.title;
		source = src.source;
		year = src.year;
		authors = new ArrayList<String>();
		for (String a : src.authors) {
			authors.add(a);
		} //TODO 
		citationRefs = new ArrayList<CitationRef>();
		for (CitationRef c : src.citationRefs) { // TODO????
			addCitationRef(c);
		}
		fireObjectModifiedEvent(PathwayElementEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public Citation copy() {
		Citation result = new Citation(xref); // TODO
		result.copyValuesFrom(this);
		return result;
	}

}
