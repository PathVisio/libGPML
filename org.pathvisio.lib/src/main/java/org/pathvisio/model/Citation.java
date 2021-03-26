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
 * This class stores information for a Citation.
 * 
 * @author saurabh, finterly
 */
public class Citation extends PathwayElement {

	private Xref xref;
	private String url; // optional

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

	private boolean isValidUrl(String url2) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isValidXref(Xref xref2) {
		// TODO Auto-generated method stub
		return false;
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
