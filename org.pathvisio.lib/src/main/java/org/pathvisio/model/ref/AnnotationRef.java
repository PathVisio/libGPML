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
public class AnnotationRef implements Citable, Evidenceable {

	private Annotation annotation; // source annotation, elementRef in GPML
	private Annotatable annotatable; // target pathway, pathway element, or citationRef
	private List<CitationRef> citationRefs; // 0 to unbounded
	private List<EvidenceRef> evidenceRefs; // 0 to unbounded

	/**
	 * Instantiates an AnnotationRef given source {@link Annotation} and target
	 * {@link Annotatable}, and initializes citationRefs and evidenceRefs lists.
	 * 
	 * @param annotation  the source annotation this AnnotationRef refers to.
	 * @param annotatable the target pathway, pathway element, or citationRef to
	 *                    which the AnnotationRef belongs.
	 */
	public AnnotationRef(Annotation annotation, Annotatable annotatable) {
		this.setAnnotationTo(annotation);
		this.setAnnotatableTo(annotatable);
		this.citationRefs = new ArrayList<CitationRef>();
		this.evidenceRefs = new ArrayList<EvidenceRef>();
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
	 * Checks whether this annotationRef has a source annotation.
	 *
	 * @return true if and only if the annotation of this annotationRef is
	 *         effective.
	 */
	public boolean hasAnnotation() {
		return getAnnotation() != null;
	}

	/**
	 * Sets the source annotation for this annotationRef.
	 * 
	 * @param annotation the given source annotation to set.
	 */
	public void setAnnotationTo(Annotation annotation) {
		if (annotation == null)
			throw new IllegalArgumentException("Invalid annotation.");
		if (hasAnnotation())
			throw new IllegalStateException("AnnotationRef already has a source annotation.");
		setAnnotation(annotation);
	}

	/**
	 * Sets the source annotation for this annotationRef.
	 * 
	 * @param annotation the given source annotation to set.
	 */
	private void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	/**
	 * Unsets the annotation, if any, from this annotationRef.
	 */
	public void unsetAnnotation() {
		if (hasAnnotation()) {
			Annotation formerAnnotation = this.getAnnotation();
			setAnnotation(null);
			formerAnnotation.removeAnnotationRef(this);
		}
	}

	/**
	 * Returns the target pathway, pathway element, or citationRef
	 * {@link Annotatable} for this annotationRef.
	 * 
	 * @return annotatable the target of the annotationRef.
	 */
	public Annotatable getAnnotatable() {
		return annotatable;
	}

	/**
	 * Checks whether this annotationRef has a target annotatable.
	 *
	 * @return true if and only if the annotatable of this annotationRef is
	 *         effective.
	 */
	public boolean hasAnnotatable() {
		return getAnnotatable() != null;
	}

	/**
	 * Sets the target pathway, pathway element, or citationRef {@link Annotatable}
	 * for this annotationRef.
	 * 
	 * @param citable the given target citable to set.
	 */
	public void setAnnotatableTo(Annotatable annotatable) {
		if (annotatable == null)
			throw new IllegalArgumentException("Invalid annotatable.");
		if (hasAnnotatable())
			throw new IllegalStateException("AnnotationRef already has a target annotatable.");
		setAnnotatable(annotatable);
	}

	/**
	 * Sets the target pathway, pathway element, or citationRef {@link Annotatable}
	 * for this annotationRef.
	 * 
	 * @param annotatable the given target annotatable to set.
	 */
	private void setAnnotatable(Annotatable annotatable) {
		this.annotatable = annotatable;
	}

	/**
	 * Unsets the annotatable, if any, from this annotationRef.
	 */
	public void unsetAnnotatable() {
		if (hasAnnotatable()) {
			Annotatable formerAnnotatable = this.getAnnotatable();
			setAnnotatable(null);
			formerAnnotatable.removeAnnotationRef(this);
		}
	}

	/**
	 * Returns the list of citation references.
	 * 
	 * @return citationRefs the list of citations referenced, an empty list if no
	 *         properties are defined.
	 */
	@Override
	public List<CitationRef> getCitationRefs() {
		return citationRefs;
	}

	/**
	 * Checks whether citationRefs has the given citationRef.
	 * 
	 * @param citationRef the citationRef to look for.
	 * @return true if has citationRef, false otherwise.
	 */
	@Override
	public boolean hasCitationRef(CitationRef citationRef) {
		return citationRefs.contains(citationRef);
	}

	/**
	 * Adds given citationRef to citationRefs list.
	 * 
	 * @param citationRef the citationRef to be added.
	 */
	@Override
	public void addCitationRef(CitationRef citationRef) {
		assert (citationRef != null) && (citationRef.getCitable() == this);
		assert !hasCitationRef(citationRef);
		citationRefs.add(citationRef);
	}

	/**
	 * Removes given citationRef from citationRefs list.
	 * 
	 * @param citationRef the citationRef to be removed.
	 */
	@Override
	public void removeCitationRef(CitationRef citationRef) {
		citationRef.terminate();
	}

	/**
	 * Removes all citationRef from citationRefs list.
	 */
	@Override
	public void removeCitationRefs() {
		for (CitationRef citationRef : citationRefs) {
			removeCitationRef(citationRef);
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
	@Override
	public List<EvidenceRef> getEvidenceRefs() {
		return evidenceRefs;
	}

	/**
	 * Checks whether citationRefs has the given citationRef.
	 * 
	 * @param evidenceRef the evidenceRef to look for.
	 * @return true if has evidenceRef, false otherwise.
	 */
	@Override
	public boolean hasEvidenceRef(EvidenceRef evidenceRef) {
		return evidenceRefs.contains(evidenceRef);
	}

	/**
	 * Adds given evidenceRef to evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be added.
	 */
	@Override
	public void addEvidenceRef(EvidenceRef evidenceRef) {
		assert (evidenceRef != null) && (evidenceRef.getEvidenceable() == this);
		assert !hasEvidenceRef(evidenceRef);
		evidenceRefs.add(evidenceRef);
	}

	/**
	 * Removes given evidenceRef from evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be removed.
	 */
	@Override
	public void removeEvidenceRef(EvidenceRef evidenceRef) {
		evidenceRef.terminate();
	}

	/**
	 * Removes all evidenceRef from citationRefs list.
	 */
	@Override
	public void removeEvidenceRefs() {
		for (EvidenceRef evidenceRef : evidenceRefs) {
			removeEvidenceRef(evidenceRef);
		}
	}

	/**
	 * Terminates this annotationRef. The annotation and annotatable, if any, are
	 * unset from this annotationRef.
	 */
	public void terminate() {
		unsetAnnotation();
		unsetAnnotatable();
		removeCitationRefs();
		removeEvidenceRefs();
	}

}
