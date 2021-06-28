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
import org.pathvisio.model.type.AnnotationType;

/**
 * This class stores information for an Annotation.
 * 
 * @author finterly
 */
public class Annotation extends PathwayElement {

	/* list of parent pathway elements with annotationRef for this annotation. */
	private List<PathwayElement> pathwayElements;
	private String value;
	private AnnotationType type;
	private Xref xref; // optional
	private UrlRef url; // optional

	/**
	 * Instantiates an Annotation pathway element given all possible parameters:
	 * elementId, parent pathway model, value, type, url, and xref.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param value        the name, term, or text of the annotation.
	 * @param type         the type of the annotation, e.g. ontology.
	 * @param xref         the annotation xref.
	 * @param url          the url of the annotation.
	 */
	public Annotation(String elementId, PathwayModel pathwayModel, String value, AnnotationType type, Xref xref,
			UrlRef url) {
		super(pathwayModel, elementId);
		this.pathwayElements = new ArrayList<PathwayElement>();
		this.value = value;
		this.type = type;
		this.xref = xref;
		this.url = url;
	}

	/**
	 * Instantiates an Annotation given all possible parameters except xref.
	 */
	public Annotation(String elementId, PathwayModel pathwayModel, String value, AnnotationType type, UrlRef url) {
		this(elementId, pathwayModel, value, type, null, url);

	}

	/**
	 * Instantiates an Annotation given all possible parameters except url.
	 */
	public Annotation(String elementId, PathwayModel pathwayModel, String value, AnnotationType type, Xref xref) {
		this(elementId, pathwayModel, value, type, xref, null);
	}

	/**
	 * Instantiates an Annotation given all possible parameters except url and xref.
	 */
	public Annotation(String elementId, PathwayModel pathwayModel, String value, AnnotationType type) {
		this(elementId, pathwayModel, value, type, null, null);
	}

	/**
	 * Returns the list of pathway elements with annotationRef for the annotation.
	 * 
	 * @return pathwayElements the list of pathway elements which reference the
	 *         annotation.
	 */
	public List<PathwayElement> getPathwayElements() {
		return pathwayElements;
	}

	/**
	 * Adds the given pathway element to pathwayElements list of the annotation.
	 * 
	 * @param pathwayElement the given pathwayElement to add.
	 */
	public void addPathwayElement(PathwayElement pathwayElement) {
		pathwayElements.add(pathwayElement);
	}

	/**
	 * Removes the given pathway element from pathwayElements list of the
	 * annotation.
	 * 
	 * @param pathwayElement the given pathwayElement to remove.
	 */
	public void removePathwayElement(PathwayElement pathwayElement) {
		pathwayElements.remove(pathwayElement);
	}

	/**
	 * Returns the name, term, or text of the annotation.
	 * 
	 * @return value the name, term, or text of the annotation.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the name, term, or text of the annotation.
	 * 
	 * @param value the name, term, or text of the annotation.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the type of the annotation.
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
	 * Returns the url of the annotation.
	 * 
	 * @return url the url of the annotation.
	 */
	public UrlRef getUrlRef() {
		return url;
	}

	/**
	 * Sets the url of the annotation.
	 * 
	 * @param url the url of the annotation.
	 */
	public void setUrlRef(UrlRef url) {
		this.url = url;
	}

	/**
	 * Returns the Annotation Xref.
	 * 
	 * @return xref the annotation xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the annotation.
	 * 
	 * @param xref the xref of the annotation.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

}
