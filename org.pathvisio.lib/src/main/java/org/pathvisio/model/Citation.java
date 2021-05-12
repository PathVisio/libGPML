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
package org.pathvisio.model;

import java.util.ArrayList;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * This class stores information for a Citation.
 * 
 * @author finterly
 */
public class Citation extends PathwayElement {

	/* list of parent pathway elements with citationRef for this citation. */
	private List<PathwayElement> pathwayElements;
	private Xref xref;
	private String url; // optional
	/* for GPML2013a Biopax */
	private String title;
	private String source;
	private String year;
	private List<String> authors;

	/**
	 * Instantiates a Citation pathway element given all possible parameters:
	 * elementId, parent pathway model, xref, and url.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param xref         the citation xref.
	 * @param url          the url of the citation.
	 */
	public Citation(String elementId, PathwayModel pathwayModel, Xref xref, String url) {
		super(elementId, pathwayModel);
		this.pathwayElements = new ArrayList<PathwayElement>();
		this.xref = xref;
		this.url = url;
	}

	/**
	 * Instantiates a Citation pathway element given all possible parameters except
	 * xref.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param xref         the citation xref.
	 */
	public Citation(String elementId, PathwayModel pathwayModel, Xref xref) {
		this(elementId, pathwayModel, xref, null);
	}

	/**
	 * Returns the list of pathway elements with citationRef for the citation.
	 * 
	 * @return pathwayElements the list of pathway elements which reference the
	 *         citation.
	 */
	public List<PathwayElement> getPathwayElements() {
		return pathwayElements;
	}

	/**
	 * Adds the given pathway element to pathwayElements list of the citation.
	 * 
	 * @param pathwayElement the given pathwayElement to add.
	 */
	public void addPathwayElement(PathwayElement pathwayElement) {
		pathwayElements.add(pathwayElement);
	}

	/**
	 * Removes the given pathway element from pathwayElements list of the citation.
	 * 
	 * @param pathwayElement the given pathwayElement to remove.
	 */
	public void removePathwayElement(PathwayElement pathwayElement) {
		pathwayElements.remove(pathwayElement);
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
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url of the citation.
	 * 
	 * @param url the url of the citation.
	 */
	public void setUrl(String url) {
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
}
