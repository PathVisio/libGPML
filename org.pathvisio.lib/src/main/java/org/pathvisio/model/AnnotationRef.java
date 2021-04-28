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
 * This class stores information for an AnnotationRef.
 * 
 * @author finterly
 */
public class AnnotationRef {

	private Annotation annotation; // elementRef in GPML is this.annotation.getElementId()
	private PathwayElement pathwayElement;
	private List<Citation> citationRefs; // 0 to unbounded
	private List<Evidence> evidenceRefs; // 0 to unbounded

	/**
	 * Instantiates an AnnotationRef given annotation and parent pathway element,
	 * and initializes citation and evidence lists.
	 * 
	 * @param annotation     the Annotation this AnnotationRef refers to.
	 * @param pathwayElement the pathway element to which the annotationRef belongs.
	 */
	public AnnotationRef(Annotation annotation, PathwayElement pathwayElement) {
		this.annotation = annotation;
		this.setPathwayElement(pathwayElement);
		this.citationRefs = new ArrayList<Citation>();
		this.evidenceRefs = new ArrayList<Evidence>();
	}

	/**
	 * Instantiates an AnnotationRef given annotation and initializes citation and
	 * evidence lists. No pathway element is given as this annotationRef belongs to
	 * the {@link Pathway}.
	 * 
	 * @param annotation the Annotation this AnnotationRef refers to.
	 */
	public AnnotationRef(Annotation annotation) {
		this(annotation, null);
	}

	/**
	 * Returns the parent pathway element to which the annotationRef belongs.
	 * 
	 * @return pathwayElement the parent pathway element the annotationRef.
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
			annotation.removePathwayElement(pathwayElement);
		}
		annotation.addPathwayElement(pathwayElement);
		this.pathwayElement = pathwayElement;
	}

	/**
	 * Returns the annotation referenced.
	 * 
	 * @return annotation the annotation referenced.
	 */
	public Annotation getAnnotation() {
		return annotation;
	}

	/**
	 * Sets the annotation to be referenced.
	 * 
	 * @param annotation the annotation referenced.
	 */
	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	/**
	 * Returns the list of citation references.
	 * 
	 * @return citationRefs the list of citations referenced, an empty list if no
	 *         properties are defined.
	 */
	public List<Citation> getCitationRefs() {
		return citationRefs;
	}

	/**
	 * Adds given citationRef to citationRefs list.
	 * 
	 * @param citationRef the citationRef to be added.
	 */
	public void addCitationRef(Citation citationRef) {
		citationRefs.add(citationRef);
	}

	/**
	 * Removes given citationRef from citationRefs list.
	 * 
	 * @param citationRef the citationRef to be removed.
	 */
	public void removeCitationRef(Citation citationRef) {
		citationRefs.remove(citationRef);
	}

	/**
	 * Returns the list of evidence references.
	 * 
	 * @return evidenceRefs the list of evidences referenced, an empty list if no
	 *         properties are defined.
	 */
	public List<Evidence> getEvidenceRefs() {
		return evidenceRefs;
	}

	/**
	 * Adds given evidenceRef to evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be added.
	 */
	public void addEvidenceRef(Evidence evidenceRef) {
		evidenceRefs.add(evidenceRef);
	}

	/**
	 * Removes given evidenceRef from evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be removed.
	 */
	public void removeEvidenceRef(Evidence evidenceRef) {
		evidenceRefs.remove(evidenceRef);
	}

}
