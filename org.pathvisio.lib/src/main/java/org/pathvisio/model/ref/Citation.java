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

/**
 * This class stores information for a Citation.
 * 
 * @author finterly
 */
public class Citation extends PathwayElement {

	/** One or both xref and/or Url link is required */
	private Xref xref;
	private UrlRef url;
	/** Optional attributes for GPML2013a Biopax */
	private String title;
	private String source;
	private String year;
	private List<String> authors;
	/** citationRefs with this citation as source */
	private List<CitationRef> citationRefs;

	/**
	 * Instantiates a Citation pathway element given all possible parameters.
	 * 
	 * @param xref the citation xref.
	 * @param url  the url link and description (optional) for a web address.
	 */
	public Citation(Xref xref, UrlRef url) {
		super();
		this.xref = xref;
		this.url = url;
		this.citationRefs = new ArrayList<CitationRef>();
	}

	/**
	 * Instantiates a Citation pathway element given all possible parameters except
	 * xref. A citation much have either xref or url, or both.
	 */
	public Citation(UrlRef url) {
		this(null, url);
	}

	/**
	 * Instantiates a Citation pathway element given all possible parameters except
	 * url. A citation much have either xref or url, or both.
	 */
	public Citation(Xref xref) {
		this(xref, null);
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
		xref = v;
	}

	/**
	 * Returns the url of this citation.
	 * 
	 * @return url the url of this citation.
	 */
	public UrlRef getUrl() {
		return url;
	}

	/**
	 * Sets the url of this citation.
	 * 
	 * @param v the url to set for this citation.
	 */
	public void setUrl(UrlRef v) {
		url = v;
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
		title = v;
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
		source = v;
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
		year = v;
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
	 * Adds the given citationRef to citationRefs list of this citation. NB: This method
	 * is not used directly.
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
	 * because it is no longer referenced/used. NB: This method
	 * is not used directly.
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
		// checks if url link and description are equivalent
		if (url != null && citation.getUrl() == null)
			return false;
		if (url == null && citation.getUrl() != null)
			return false;
		if (url != null && citation.getUrl() != null) {
			if (!Objects.equals(url.getLink(), citation.getUrl().getLink()))
				return false;
			if (!Objects.equals(url.getDescription(), citation.getUrl().getDescription()))
				return false;
		}
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

}
