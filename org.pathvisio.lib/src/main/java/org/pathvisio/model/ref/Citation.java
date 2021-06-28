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
import org.pathvisio.model.element.*;

/**
 * This class stores information for a Citation.
 * 
 * @author finterly
 */
public class Citation extends PathwayElement {

	/** citationRefs with this citation as source */
	private List<CitationRef> citationRefs;

	/** One or both xref and/or Url link is required */
	private Xref xref;
	private UrlRef url;

	/** Optional attributes for GPML2013a Biopax */
	private String title;
	private String source;
	private String year;
	private List<String> authors;

	/**
	 * Instantiates a Citation pathway element given all possible parameters.
	 * 
	 * @param pathwayModel the parent pathway model.
	 * @param elementId    the unique pathway element identifier.
	 * @param xref         the citation xref.
	 * @param url          the url link and description (optional) for a web
	 *                     address.
	 */
	public Citation(PathwayModel pathwayModel, String elementId, Xref xref, UrlRef url) {
		super(pathwayModel, elementId);
		this.citationRefs = new ArrayList<CitationRef>();
		this.xref = xref;
		this.url = url;
	}

	/**
	 * Instantiates a Citation pathway element given all possible parameters except
	 * xref. A citation much have either xref or url, or both.
	 */
	public Citation(PathwayModel pathwayModel, String elementId, UrlRef url) {
		this(pathwayModel, elementId, null, url);
	}

	/**
	 * Instantiates a Citation pathway element given all possible parameters except
	 * url. A citation much have either xref or url, or both.
	 */
	public Citation(PathwayModel pathwayModel, String elementId, Xref xref) {
		this(pathwayModel, elementId, xref, null);
	}

	/**
	 * Returns the list of citationRefs which reference the citation.
	 * 
	 * @return citationRefs the list of citationRefs which reference the citation.
	 */
	public List<CitationRef> getCitationRefs() {
		return citationRefs;
	}

	/**
	 * Adds the given citationRef to citationRefs list of the citation.
	 * 
	 * @param citationRef the given citationRef to add.
	 */
	public void addCitationRef(CitationRef citationRef) {
		citationRefs.add(citationRef);
	}

	/**
	 * Removes the given citationRef to citationRefs list of the citation.
	 * 
	 * @param citationRef the given citationRef to remove.
	 */
	public void removeCitationRef(CitationRef citationRef) {
		citationRefs.remove(citationRef);
	}

	/**
	 * Returns the Citation Xref.
	 * 
	 * @return xref the citation xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the citation.
	 * 
	 * @param xref the xref of the citation.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

	/**
	 * Returns the url of the citation.
	 * 
	 * @return url the url of the citation.
	 */
	public UrlRef getUrlRef() {
		return url;
	}

	/**
	 * Sets the url of the citation.
	 * 
	 * @param url the url of the citation.
	 */
	public void setUrlRef(UrlRef url) {
		this.url = url;
	}

	/**
	 * Returns source for the citation (for GPML2013a Biopax).
	 * 
	 * @return title the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets source for the citation (for GPML2013a Biopax).
	 * 
	 * @param title the title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns source for the citation (for GPML2013a Biopax).
	 * 
	 * @return source the source.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets source for the citation (for GPML2013a Biopax).
	 * 
	 * @param source the source.
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Returns year for the citation (for GPML2013a Biopax).
	 * 
	 * @return year the year.
	 */
	public String getYear() {
		return year;
	}

	/**
	 * Sets year for the citation (for GPML2013a Biopax).
	 * 
	 * @param year the year.
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * Returns list of authors for the citation (for GPML2013a Biopax).
	 * 
	 * @return authors the list of authors.
	 */
	public List<String> getAuthors() {
		return authors;
	}

	/**
	 * Sets list of authors for the citation (for GPML2013a Biopax).
	 * 
	 * @param authors the list of authors.
	 */
	public void setAuthors(List<String> authors) {
		this.authors = authors;
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
		if (xref != null && citation.getXref() == null)
			return false;
		if (xref == null && citation.getXref() != null)
			return false;
		if (xref != null && citation.getXref() != null) {
			if (!xref.getId().equals(citation.getXref().getId()))
				return false;
			if (!xref.getDataSource().equals(citation.getXref().getDataSource()))
				return false;
		}
		if (!Objects.equals(url.getLink(), citation.getUrlRef().getLink()))
			return false;
		if (!Objects.equals(url.getDescription(), citation.getUrlRef().getDescription()))
			return false;
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
