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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * 
 */
public class GraphicalLine extends PathwayElement {

//	protected String elementId;
	protected String groupRef; //optional
	protected LineStyleProperty lineStyleProperty;
	protected List<Point> points;
	protected List<Anchor> anchors = new ArrayList<Anchor>(); 
	protected List<Comment> comments; //optional
	protected List<DynamicProperty> dynamicProperties; // optional
	protected List<AnnotationRef> annotationRefs; //optional
	protected List<CitationRef> citationRefs; //optional
	protected List<EvidenceRef> evidenceRefs; // optional

//	protected String type; //debate
	
	
	

	/**
	 * List of Points.
	 */
	private List<MPoint> mPoints = Arrays.asList(new MPoint(0, 0), new MPoint(0, 0));

	public void setMPoints(List<MPoint> points) {
		if (points != null) {
			if (points.size() < 2) {
				throw new IllegalArgumentException("Points array should at least have two elements");
			}
			mPoints = points;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	public List<MPoint> getMPoints() {
		return mPoints;
	}
	

	/**
	 * Get the anchors for this line.
	 * 
	 * @return A list with the anchors, or an empty list, if no anchors are defined
	 */
	public List<MAnchor> getMAnchors() {
		return anchors;
	}

	
	
	// Add Constructors


	/**
	 * Gets the groupRef of the graphical line. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the graphical line.
	 * 
	 */
	public String getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the graphical line. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the graphical line.
	 * 
	 */
	public void getGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}


//	/**
//	 * Gets the type of the graphical line.
//	 * 
//	 * @return type the type of graphical line, e.g. complex.
//	 */
//	public String getType() {
//		return type;
//	}
//
//	/**
//	 * Sets the type of the graphical line.
//	 * 
//	 * @param type the type of graphical line, e.g. complex.
//	 */
//	public void setType(String type) {
//		this.type = type;
//	}

	/**
	 * Gets the list of comments.
	 * 
	 * @return comments the list of comments.
	 */
	public List<Comment> getCommentList() {
		return comments;
	}

	/**
	 * Gets the list of key value pair information properties.
	 * 
	 * @return properties the list of properties.
	 */
	public List<Property> getPropertyList() {
		return properties;
	}

	/**
	 * Gets the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotation references.
	 */
	public List<AnnotationRef> getAnnotationRefList() {
		return annotationRefs;
	}

	/**
	 * Gets the list of citation references.
	 * 
	 * @return citationRefs the list of citation references.
	 */
	public List<CitationRef> getCitationRefList() {
		return citationRefs;
	}

//	public List<AnnotationRef> getAnnotationRefList() {
//		if (annotationRefs == null) {
//			annotationRefs = new ArrayList<AnnotationRef>();
//		}
//		return this.annotationRefs;
//	}

}
