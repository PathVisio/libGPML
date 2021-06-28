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

import org.bridgedb.Xref;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayModel;

/**
 * This class stores information for an Evidence.
 * 
 * @author finterly
 */
public class Evidence extends PathwayElement {

	/* list of parent pathway elements which have evidenceRef for this evidence. */
	private List<PathwayElement> pathwayElements;
	private String value; // optional
	private Xref xref;
	private String url; // optional

	/*
	 * NB: Manipulated the order of variables to overload constructor. This is not
	 * best practice, however variable inheritance complicates use of a builder.
	 */

	/**
	 * Instantiates an Evidence pathway element given all possible parameters:
	 * elementId, parent pathway model, value, xref, and url.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param value        the name, term, or text of the evidence.
	 * @param xref         the evidence xref.
	 * @param url          the url of the evidence.
	 */
	public Evidence(PathwayModel pathwayModel, String elementId, String value, Xref xref, String url) {
		super(pathwayModel, elementId);
		this.pathwayElements = new ArrayList<PathwayElement>();
		this.value = value;
		this.xref = xref;
		this.url = url;
	}

	/**
	 * Instantiates an Evidence given all possible parameters except value.
	 */
	public Evidence(PathwayModel pathwayModel, String elementId, Xref xref, String url) {
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
	 * Returns the list of pathway elements with evidenceRef for the evidence.
	 * 
	 * @return pathwayElements the list of pathway elements which reference the
	 *         evidence.
	 */
	public List<PathwayElement> getPathwayElements() {
		return pathwayElements;
	}

	/**
	 * Adds the given pathway element to pathwayElements list of the evidence.
	 * 
	 * @param pathwayElement the given pathwayElement to add.
	 */
	public void addPathwayElement(PathwayElement pathwayElement) {
		pathwayElements.add(pathwayElement);
	}

	/**
	 * Removes the given pathway element from pathwayElements list of the evidence.
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
	 * Sets the Xref for the evidence.
	 * 
	 * @param xref the xref of the evidence.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

}
