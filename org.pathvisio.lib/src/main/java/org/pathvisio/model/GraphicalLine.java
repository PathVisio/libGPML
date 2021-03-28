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
 * This class stores information for GraphicalLine pathway element.
 * 
 * @author finterly
 */
public class GraphicalLine extends LineElement {

	public GraphicalLine(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, List<Point> points, List<Anchor> anchors, LineStyleProperty lineStyleProperty,
			Group groupRef) {
		super(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs, points, anchors,
				lineStyleProperty, groupRef);
		// TODO Auto-generated constructor stub
	}

	public GraphicalLine(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, List<Point> points, List<Anchor> anchors,
			LineStyleProperty lineStyleProperty) {
		super(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs, points, anchors,
				lineStyleProperty);
	}

}
