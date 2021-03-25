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
 * This class stores information for an Evidence.
 * 
 * @author finterly
 */
public class Evidence extends PathwayElement {

	private String value; // optional
	private Xref xref;
	private String url; // optional

	/*
	 * NB: Manipulated the order of variables to overload constructor. Cannot
	 * have two constructors with the same signature. This is not best practice,
	 * however variable inheritance complicates use of a builder. F
	 */

	/**
	 * Instantiates an Evidence pathway element given elementId, given parent
	 * pathway model, given xref, no value, and no url.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param xref         the evidence xref.
	 */
	public Evidence(String elementId, PathwayModel pathwayModel, Xref xref) {
		this(elementId, pathwayModel, null, xref, null);
	}

	/**
	 * Instantiates an Evidence pathway element given elementId, given parent
	 * pathway model, given xref, given url, and no value. 
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param value        the name, term, or text of the evidence.
	 * @param xref         the evidence xref.
	 * @param url          the url of the evidence.
	 */
	public Evidence(String elementId, PathwayModel pathwayModel, Xref xref, String url) {
		this(elementId, pathwayModel, null, xref, url);
	}

	/**
	 * Instantiates an Evidence pathway element given elementId, given parent
	 * pathway model, given value, given xref, and no url.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param value        the name, term, or text of the evidence.
	 * @param xref         the evidence xref.
	 */
	public Evidence(String elementId, PathwayModel pathwayModel, String value, Xref xref) {
		this(elementId, pathwayModel, value, xref, null);
	}

	/**
	 * Instantiates an Evidence pathway element given elementId, given parent
	 * pathway model, given value, given xref, and given url.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param value        the name, term, or text of the evidence.
	 * @param xref         the evidence xref.
	 * @param url          the url of the evidence.
	 */
	public Evidence(String elementId, PathwayModel pathwayModel, String value, Xref xref, String url) {
		super(elementId, pathwayModel);
		this.value = value;
		this.xref = xref;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
