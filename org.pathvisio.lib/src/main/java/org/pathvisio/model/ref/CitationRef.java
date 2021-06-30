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
 * This class stores information for a CitationRef with source {@link Citation},
 * target {@link Citable}, and a list of {@link AnnotationRef}. The Citable
 * target can be a {@link Pathway}, pathway element {@link ElementInfo}, or
 * annotationRef {@link AnnotationRef}. In gpml:CitationRef, the attribute
 * elementRef refers to the elementId of the source gpml:Citation.
 * 
 * @author finterly
 */
public class CitationRef implements Annotatable {

	private Citation citation; // source citation, elementRef in GPML
	private Citable citable; // target pathway, pathway element, or annotationRef
	private List<AnnotationRef> annotationRefs; // 0 to unbounded

	/**
	 * Instantiates an CitationRef given source {@link Citation} and target
	 * {@link Citable}, and initializes annotationRefs lists.
	 * 
	 * @param citation the source citation this CitationRef refers to.
	 * @param citable  the target pathway, pathway element, or annotationRef to
	 *                 which the CitationRef belongs.
	 */
	public CitationRef(Citation citation, Citable citable) {
		this.setCitation(citation);
		this.setCitable(citable);
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
	 * Returns the citation referenced.
	 * 
	 * @return citation the citation referenced.
	 */
	public Citation getCitation() {
		return citation;
	}

	/**
	 * Checks whether this citationRef has a source citation.
	 *
	 * @return true if and only if the citation of this citationRef is effective.
	 */
	public boolean hasCitation() {
		return getCitation() != null;
	}

	/**
	 * Sets the source citation for this citationRef.
	 * 
	 * @param citation the given source citation to set.
	 */
	public void setCitationTo(Citation citation) {
		if (citation == null)
			throw new IllegalArgumentException("Invalid citation.");
		if (hasCitation())
			throw new IllegalStateException("CitationRef already has a source citation.");
		setCitation(citation);
		citation.addCitationRef(this);
	}

	/**
	 * Sets the source citation for this citationRef.
	 * 
	 * @param citation the given source citation to set.
	 */
	private void setCitation(Citation citation) {
		assert (citation != null);
		this.citation = citation;
	}

	/**
	 * Unsets the citation, if any, from this citationRef.
	 */
	public void unsetCitation() {
		if (hasCitation()) {
			Citation formerCitation = this.getCitation();
			setCitable(null);
			formerCitation.removeCitationRef(this);
		}
	}

	/**
	 * Returns the target pathway, pathway element, or annotationRef {@link Citable}
	 * for this citationRef.
	 * 
	 * @return citable the target of the citationRef.
	 */
	public Citable getCitable() {
		return citable;
	}

	/**
	 * Checks whether this citationRef has a target citable.
	 *
	 * @return true if and only if the citable of this citationRef is effective.
	 */
	public boolean hasCitable() {
		return getCitable() != null;
	}

	/**
	 * Sets the target pathway, pathway element, or annotationRef {@link Citable}
	 * for this annotationRef.
	 * 
	 * @param citable the given target citable to set.
	 */
	public void setCitableTo(Citable citable) {
		if (citable == null)
			throw new IllegalArgumentException("Invalid citable.");
		if (hasCitable())
			throw new IllegalStateException("CitationRef already has a target citable.");
		setCitable(citable);
		citable.addCitationRef(this);
	}

	/**
	 * Sets the target pathway, pathway element, or annotationRef {@link Citable} to
	 * which the annotationRef belongs.
	 * 
	 * @param citable the given target citable to set.
	 */
	private void setCitable(Citable citable) {
		assert (citable != null);
		this.citable = citable;
	}

	/**
	 * Unsets the citable, if any, from this citationRef.
	 */
	public void unsetCitable() {
		if (hasCitable()) {
			Citable formerCitable = this.getCitable();
			setCitable(null);
			formerCitable.removeCitationRef(this);
		}
	}

	/**
	 * Returns the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotations referenced, an empty list if
	 *         no properties are defined.
	 */
	@Override
	public List<AnnotationRef> getAnnotationRefs() {
		return annotationRefs;
	}

	/**
	 * Checks whether annotationRefs has the given annotationRef. 
	 * 
	 * @param annotationRef the annotationRef to look for.
	 * @return true if has annotationRef, false otherwise.
	 */
	@Override
	public boolean hasAnnotationRef(AnnotationRef annotationRef) {
		return annotationRefs.contains(annotationRef);
	}

	/**
	 * Adds given annotationRef to annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be added.
	 */
	@Override
	public void addAnnotationRef(AnnotationRef annotationRef) {
		assert (annotationRef != null) && (annotationRef.getAnnotatable() == this);
		assert !hasAnnotationRef(annotationRef);
		annotationRefs.add(annotationRef);
	}

	/**
	 * Removes given annotationRef from annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be removed.
	 */
	@Override
	public void removeAnnotationRef(AnnotationRef annotationRef) {
		annotationRef.terminate();
	}

	/**
	 * Removes all annotationRefs from annotationRefs list.
	 */
	@Override
	public void removeAnnotationRefs() {
		for (AnnotationRef annotationRef : annotationRefs) {
			removeAnnotationRef(annotationRef);
		}
	}

	/**
	 * Terminates this citationRef. The citation and citable, if any, are unset from
	 * this citationRef. Links to all annotationRefs are removed from this
	 * citationRef.
	 */
	public void terminate() {
		unsetCitation();
		unsetCitable();
		removeAnnotationRefs();
	}

}
