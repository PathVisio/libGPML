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
 * This class stores information for an AnnotationRef.
 * 
 * @author finterly
 */
public class AnnotationRef implement IElementRefContainer {

	private Annotation annotation;
	private List<Citation> citations;
	private List<Evidence> evidences;

	/**
	 * Instantiates a AnnotationRef given elementRef and given a list of
	 * annotationRefs, meta annotations used to annotate the referenced Annotation.
	 * 
	 * @param elementRef     the id of the Annotation element to which the
	 *                       AnnotationRef refers.
	 * @param annotationRefs the list of meta annotation references.
	 */
	public AnnotationRef(String elementRef, List<AnnotationRef> annotationRefs) {
		super();
		this.elementRef = elementRef;
		this.annotationRefs = annotationRefs;
	}

	/**
	 * Instantiates a AnnotationRef given elementRef and no meta annotationRefs.
	 * 
	 * @param elementRef the id of the Annotation element to which the AnnotationRef
	 *                   refers.
	 */
	public AnnotationRef(String elementRef) {
		super();
		this.elementRef = elementRef;
	}

	/**
	 * Gets elementRef of the AnnotationRef, refers to the elementId of an
	 * Annotation.
	 * 
	 * @return elementRef the id of the Annotation element to which the
	 *         AnnotationRef refers.
	 */
	public String getElementRef() {
		return elementRef;
	}

	/**
	 * Sets elementRef of the AnnotationRef, refers to the elementId of an
	 * Annotation.
	 * 
	 * @param elementRef the id of the Annotation element to which the AnnotationRef
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
