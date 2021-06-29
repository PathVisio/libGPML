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
	 * Sets the citation source to be referenced.
	 * 
	 * @param newCitation the given citation source to set.
	 */
	public void setCitation(Citation newCitation) {
		if (citation == null || !citation.equals(newCitation)) {
			// if necessary, removes link to existing citation source
			if (citation != null) {
				this.citation.removeCitationRef(this);
			}
			if (newCitation != null) {
				newCitation.addCitationRef(this);
			}
			citation = newCitation;
		}
	}

	/**
	 * Returns the target pathway, pathway element, or annotationRef {@link Citable}
	 * to which the citationRef belongs.
	 * 
	 * @return citable the target of the citationRef.
	 */
	public Citable getCitable() {
		return citable;
	}

	/**
	 * Sets the target pathway, pathway element, or annotationRef {@link Citable} to
	 * which the annotationRef belongs.
	 * 
	 * @param newCitable the given target citable to set.
	 */
	public void setCitable(Citable newCitable) {
		if (citable == null || !citable.equals(newCitable)) {
			// if necessary, removes link to existing target citable
			if (citable != null) {
				this.citable.removeCitationRef(this);
			}
			if (newCitable != null) {
				newCitable.addCitationRef(this);
			}
			citable = newCitable;
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
	 * Adds given annotationRef to annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be added.
	 */
	@Override
	public void addAnnotationRef(AnnotationRef annotationRef) {
		if (annotationRef.getAnnotatable() != this) 
			annotationRef.setAnnotatable(this);
		assert (annotationRef.getAnnotatable() == this); // TODO
		// add to annotationRefs if not already added
		if (!annotationRefs.contains(annotationRef))
			annotationRefs.add(annotationRef);
	}

	/**
	 * Removes given annotationRef from annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be removed.
	 */
	@Override
	public void removeAnnotationRef(AnnotationRef annotationRef) {
		// remove all citationRefs of annotationRef
		if (!annotationRef.getCitationRefs().isEmpty())
			annotationRef.removeCitationRefs();
		// annotationRef.removeAllEvidenceRefs(); //TODO

		// remove links between annotationRef and its annotation
		if (annotationRef.getAnnotation() != null)
			annotationRef.getAnnotation().removeAnnotationRef(annotationRef); // TODO citationRef.setCitation(null);

		// remove annotationRef from this annotatable
		assert (annotationRef.getAnnotatable() == this);
		annotationRef.setAnnotatable(null);
		annotationRefs.remove(annotationRef);
	}

	/**
	 * Removes all annotationRefs from annotationRefs list.
	 */
	@Override
	public void removeAnnotationRefs() {
		for (AnnotationRef annotationRef : annotationRefs) {
			this.removeAnnotationRef(annotationRef);
		}
	}

}
