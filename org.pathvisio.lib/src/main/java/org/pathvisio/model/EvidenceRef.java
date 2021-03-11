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

import java.util.List;

/**
 * This class stores all information relevant to a CitationRef. CitationRef
 * refers to the unique elementId of an Citation pathway element. In the case
 * that meta annotations are used to annotate citations, CitationRef may contain
 * a list of annotationRefs (meta annotations).
 * 
 * @author finterly
 */
public class EvidenceRef {

	private String elementRef;
	private List<AnnotationRef> annotationRefs;

	/**
	 * Instantiates a CitationRef given elementRef and given a list of
	 * annotationRefs, meta annotations used to annotate the referenced Citation.
	 * 
	 * @param elementRef     the id of the Citation element to which the CitationRef
	 *                       refers.
	 * @param annotationRefs the list of meta annotation references.
	 */
	public CitationRef(String elementRef, List<AnnotationRef> annotationRefs) {
		super();
		this.elementRef = elementRef;
		this.annotationRefs = annotationRefs;
	}

	/**
	 * Instantiates a CitationRef given elementRef and no meta annotationRefs.
	 * 
	 * @param elementRef the id of the Citation element to which the CitationRef
	 *                   refers.
	 */
	public CitationRef(String elementRef) {
		super();
		this.elementRef = elementRef;
	}

	/**
	 * Gets elementRef of the CitationRef, refers to the elementId of a Citation.
	 * 
	 * @return elementRef the id of the Citation element to which the CitationRef
	 *         refers.
	 */
	public String getElementRef() {
		return elementRef;
	}

	/**
	 * Sets elementRef of the CitationRef, refers to the elementId of a Citation.
	 * 
	 * @param elementRef the id of the Citation element to which the CitationRef
	 *                   refers.
	 */
	public void setElementRef(String elementRef) {
		this.elementRef = elementRef;
	}

	/**
	 * Gets the list of meta annotationRefs.
	 * 
	 * @return annotationRefs the list of meta annotation references.
	 */
	public List<AnnotationRef> getAnnotationRefList() {
		return annotationRefs;
	}

	/**
	 * Sets the list of meta annotationRefs.
	 * 
	 * @param annotationRefs the list of meta annotation references.
	 */
	public void setAnnotationRefList(List<AnnotationRef> annotationRefs) {
		this.annotationRefs = annotationRefs;
	}

}
