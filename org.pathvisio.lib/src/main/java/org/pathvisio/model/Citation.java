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

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * This class stores all information relevant to a Citation pathway element.
 * 
 * @author saurabh, finterly
 */
public class Citation extends PathwayElement{
	
	private String elementId;
	private String url; //optional
	private Xref xref; //optional

	/**
	 * Instantiates a Citation pathway element given elementId, given xref, and
	 * given url.
	 * 
	 * @param elementId the unique id of the citation element.
	 * @param xref      the citation xref.
	 * @param url       the url of the citation.
	 */
	public Citation(String elementId, Xref xref, String url) {
		this.elementId = elementId;
		if (xref != null)
			this.xref = xref;
		if (url != null)
			this.url = url;
	}

	/**
	 * Instantiates a Citation pathway element given elementId, given xref, and no
	 * url.
	 * 
	 * @param elementId the unique id of the citation element.
	 * @param xref      the citation xref.
	 */
	public Citation(String elementId, Xref xref) {
		this.elementId = elementId;
		if (xref != null)
			this.xref = xref;
	}

	/**
	 * Instantiates a Citation pathway element given elementId, given url, and no
	 * xref.
	 * 
	 * @param elementId the unique id of the citation element.
	 * @param url       the url of the citation.
	 */
	public Citation(String elementId, String url) {
		this.elementId = elementId;
		if (url != null)
			this.url = url;
	}

	/**
	 * Gets the Citation Xref.
	 * 
	 * @return xref the citation xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of Citation Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}

	/**
	 * Gets the elementId of the citation.
	 * 
	 * @return elementId the unique id of the citation.
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets ID of the citations
	 * 
	 * @param elementId the unique id of the citation.
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the url of the citation.
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

}
