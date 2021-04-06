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
	private List<Citation> citationRefs; // 0 to unbounded
	private List<Evidence> evidenceRefs; // 0 to unbounded

	//TODO Annotation reference tricky...
	
	/**
	 * Instantiates an AnnotationRef given annotation and initializes citation and
	 * evidence lists.
	 * 
	 * @param annotation the Annotation this AnnotationRef refers to.
	 */
	public AnnotationRef(Annotation annotation) {
		this.annotation = annotation;
		this.citationRefs = new ArrayList<Citation>();
		this.evidenceRefs = new ArrayList<Evidence>();
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public List<Citation> getCitationRefs() {
		return citationRefs;
	}

	public void addCitationRef(Citation citationRef) {
		citationRefs.add(citationRef);
	}
	
	public void removeCitationRef(Citation citationRef) {
		citationRefs.remove(citationRef);
	}

	public List<Evidence> getEvidenceRefs() {
		return evidenceRefs;
	}

	public void addEvidenceRef(Evidence evidenceRef) {
		evidenceRefs.add(evidenceRef);
	}

	public void removeEvidenceRef(Evidence evidenceRef) {
		evidenceRefs.remove(evidenceRef);
	}
}
