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


/**
 * This class stores information for a EvidenceRef which references a
 * {@link Evidence}.
 * 
 * @author finterly
 */
public class EvidenceRef {

	private Evidence evidence; // elementRef in GPML is this.citation.getElementId()

//	private Evidenceable evidenceable; 
	
	
	/**
	 * Instantiates an EvidenceRef given an evidence and...
	 * 
	 * @param evidence       the Evidence this EvidenceRef refers to.
	 */
	public EvidenceRef(Evidence evidence) {
		this.evidence = evidence;
	}


	/**
	 * Returns the evidence referenced.
	 * 
	 * @return evidence the evidence referenced.
	 */
	public Evidence getEvidence() {
		return evidence;
	}

	/**
	 * Sets the evidence to be referenced.
	 * 
	 * @param evidence the evidence referenced.
	 */
	public void setEvidence(Evidence evidence) {
		this.evidence = evidence;
	}



}
