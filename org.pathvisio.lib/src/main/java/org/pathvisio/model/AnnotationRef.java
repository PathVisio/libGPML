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
	private List<Citation> citations; // 0 to unbounded
	private List<Evidence> evidences; // 0 to unbounded

	//TODO Annotation reference tricky...
	
	/**
	 * Instantiates an AnnotationRef given annotation and initializes citation and
	 * evidence lists.
	 * 
	 * @param annotation the Annotation this AnnotationRef refers to.
	 */
	public AnnotationRef(Annotation annotation) {
		this.annotation = annotation;
		this.citations = new ArrayList<Citation>();
		this.evidences = new ArrayList<Evidence>();
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public List<Citation> getCitations() {
		return citations;
	}

	public void addCitation(Citation citation) {
		citations.add(citation);
	}
	
	public void removeCitation(Citation citation) {
		citations.remove(citation);
	}

	public List<Evidence> getEvidences() {
		return evidences;
	}

	public void addEvidence(Evidence evidence) {
		evidences.add(evidence);
	}

	public void removeEvidence(Evidence evidence) {
		evidences.remove(evidence);
	}
}
