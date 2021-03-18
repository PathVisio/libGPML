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
 * This class stores information for an Annotation.
 * 
 * @author finterly
 */
public class Annotation {

	private String elementId;
	private String name;
	private AnnotationType type;
	private String url; // optional
	private Xref xref; // optional

	//TODO Fix constructors....
	public Annotation(String elementId, String name, AnnotationType type) {
		this.elementId = elementId;
		this.name = name;
		this.type = type;
	}
	
	public Annotation(String elementId, String name, AnnotationType type, String url) {
		this(elementId, name, type);
		this.url = url;
	}

	public Annotation(String elementId, String name, AnnotationType type, Xref xref) {
		this(elementId, name, type);
		this.xref = xref;
	}
	
	public Annotation(String elementId, String name, AnnotationType type, String url, Xref xref) {
		this(elementId, name, type, url);
		this.xref = xref;
	}
	

	

	
	// Add Constructors

	/**
	 * Gets the name, term, or text of the annotation.
	 * 
	 * @return name the name, term, or text of the annotation.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name, term, or text of the annotation.
	 * 
	 * @param name the name, term, or text of the annotation.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type of the annotation.
	 * 
	 * @return type the type of annotation, e.g. ontology term.
	 */
	public AnnotationType getType() {
		return type;
	}

	/**
	 * Sets the type of the annotation.
	 * 
	 * @param type the type of annotation, e.g. ontology term.
	 */
	public void setType(AnnotationType type) {
		this.type = type;
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

}
