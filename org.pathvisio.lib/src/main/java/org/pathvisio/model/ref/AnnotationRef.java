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

import org.pathvisio.model.Pathway;
import org.pathvisio.model.element.ElementInfo;

/**
 * This class stores information for an AnnotationRef with source
 * {@link Annotation}, target {@link Annotatable}, and a list of
 * {@link CitationRef} and/or {@link EvidencRef}. The Annotatable target can be
 * a {@link Pathway}, pathway element {@link ElementInfo}, or annotationRef
 * {@link CitationRef}. In gpml:AnnotationRef, the attribute elementRef refers
 * to the elementId of the source gpml:Annotation.
 * 
 * @author finterly
 */
public class AnnotationRef implements Citable {

	private Annotation annotation; // source annotation, elementRef in GPML
	private Annotatable annotatable; // target pathway, pathway element, or citationRef
	private List<CitationRef> citationRefs; // 0 to unbounded
	private List<Evidence> evidenceRefs; // 0 to unbounded

	/**
	 * Instantiates an AnnotationRef given source {@link Annotation} and target
	 * {@link Annotatable}, and initializes citationRefs and evidenceRefs lists.
	 * 
	 * @param annotation  the source annotation this AnnotationRef refers to.
	 * @param annotatable the target pathway, pathway element, or citationRef to
	 *                    which the AnnotationRef belongs.
	 */
	public AnnotationRef(Annotation annotation, Annotatable annotatable) {
		this.annotation = annotation;
		this.setAnnotatable(annotatable);
		this.citationRefs = new ArrayList<CitationRef>();
		this.evidenceRefs = new ArrayList<Evidence>();
	}

	/**
	 * Instantiates an AnnotationRef given annotation and initializes citation and
	 * evidence lists. No pathway element is given as this AnnotationRef belongs to
	 * the {@link Pathway}.
	 * 
	 * @param annotation the Annotation this AnnotationRef refers to.
	 */
	public AnnotationRef(Annotation annotation) {
		this(annotation, null);
	}

	/**
	 * Returns the target pathway, pathway element, or citationRef
	 * {@link Annotatable} to which the AnnotationRef belongs.
	 * 
	 * @return pathwayElement the parent pathway element the AnnotationRef.
	 */
	public Annotatable getAnnotatable() {
		return annotatable;
	}

	/**
	 * Sets the target pathway, pathway element, or citationRef {@link Annotatable}
	 * to which the AnnotationRef belongs.
	 * 
	 * @param pathwayElement the parent pathway element the annotationRef.
	 */
	public void setAnnotatable(Annotatable annotatable) {
		if (annotatable != null) {
			annotation.removePathwayElement(annotatable);
		}
		annotation.addPathwayElement(annotatable);
		this.annotatable = annotatable;
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
	public List<CitationRef> getCitationRefs() {
		return citationRefs;
	}

	/**
	 * Adds given citationRef to citationRefs list.
	 * 
	 * @param citationRef the citationRef to be added.
	 */
	public void addCitationRef(CitationRef citationRef) {
		citationRefs.add(citationRef);
	}

	/**
	 * Removes given citationRef from citationRefs list.
	 * 
	 * @param citationRef the citationRef to be removed.
	 */
	public void removeCitationRef(CitationRef citationRef) {
		citationRefs.remove(citationRef);
	}

	/**
	 * Removes all citationRef from citationRefs list.
	 */
	public void removeCitationRefs() {
		for (CitationRef citationRef : citationRefs) {
			this.removeCitationRef(citationRef);
		}
	}

	/*
	 * -----------------------------------------------------------------------------
	 */

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
