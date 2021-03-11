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
 * This class stores all information relevant to a Annotation pathway element.
 * 
 * @author finterly
 */
public class Annotation extends PathwayElement{

	private String elementId;
	private String name; //optional
	private AnnotationType type; //optional
	private String url; //optional
	private Xref xref; //optional

	
	
	// Add Constructors

	
	
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
	 * Gets the Annotation Xref.
	 * 
	 * @return xref the annotation xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of Annotation Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}

	/**
	 * Gets the text of of the annotation.
	 * 
	 * @return textLabel the text of of the annotation.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the annotation.
	 * 
	 * @param textLabel the text of of the annotation.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Gets the url of the annotation.
	 * 
	 * @return url the url of the annotation.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url of the annotation.
	 * 
	 * @param url the url of the annotation.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the type of the annotation.
	 * 
	 * @return type the type of annotation, e.g. ontology term.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the annotation.
	 * 
	 * @param type the type of annotation, e.g. ontology term.
	 */
	public void setType(String type) {
		this.type = type;
	}

}
