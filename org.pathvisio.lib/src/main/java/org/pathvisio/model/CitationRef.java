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

/**
 * This class stores information for a CitationRef which references a
 * {@link Citation} and can contain annotationRefs {@link AnnotationRef}.
 * 
 * @author finterly
 */
public class CitationRef {

	private Citation citation; // elementRef in GPML is this.citation.getElementId()
	private PathwayElement pathwayElement;
	private List<AnnotationRef> annotationRefs; // 0 to unbounded

	/**
	 * Instantiates an CitationRef given a citation and parent pathway element, and
	 * initializes annotationRefs list.
	 * 
	 * @param citation       the Citation this CitationRef refers to.
	 * @param pathwayElement the pathway element to which the CitationRef belongs.
	 */
	public CitationRef(Citation citation, PathwayElement pathwayElement) {
		this.citation = citation;
		this.setPathwayElement(pathwayElement);
		this.annotationRefs = new ArrayList<AnnotationRef>();
	}

	/**
	 * Instantiates an CitationRef given citation and initializes annotationRefs
	 * list. No pathway element is given as this CitationRef belongs to the
	 * {@link Pathway}.
	 * 
	 * @param citation the Citation this CitationRef refers to.
	 */
	public CitationRef(Citation citation) {
		this(citation, null);
	}

	/**
	 * Returns the parent pathway element to which the CitationRef belongs.
	 * 
	 * @return pathwayElement the parent pathway element of the CitationRef.
	 */
	public PathwayElement getPathwayElement() {
		return pathwayElement;
	}

	/**
	 * Sets the parent pathway element to which the annotationRef belongs.
	 * 
	 * @param pathwayElement the parent pathway element the annotationRef.
	 */
	public void setPathwayElement(PathwayElement pathwayElement) {
		if (pathwayElement != null) {
			citation.removePathwayElement(pathwayElement);
		}
		citation.addPathwayElement(pathwayElement);
		this.pathwayElement = pathwayElement;
	}

	/**
	 * Returns the citation referenced.
	 * 
	 * @return citation the citation referenced.
	 */
	public Citation getCitation() {
		return citation;
	}

	/**
	 * Sets the citation to be referenced.
	 * 
	 * @param citation the citation referenced.
	 */
	public void setCitation(Citation citation) {
		this.citation = citation;
	}

	/**
	 * Returns the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotations referenced, an empty list if
	 *         no properties are defined.
	 */
	public List<AnnotationRef> getAnnotationRefs() {
		return annotationRefs;
	}

	/**
	 * Adds given annotationRef to annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be added.
	 */
	public void addAnnotationRef(AnnotationRef annotationRef) {
		annotationRefs.add(annotationRef);
	}

	/**
	 * Removes given annotationRef from annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be removed.
	 */
	public void removeAnnotationRef(AnnotationRef annotationRef) {
		annotationRefs.remove(annotationRef);
	}

}
