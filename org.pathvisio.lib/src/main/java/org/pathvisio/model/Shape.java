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

import org.bridgedb.Xref;

/**
 * This class stores all information relevant to a Shape pathway element.
 * 
 * @author finterly
 */
public class Shape extends PathwayElement {

//	protected String elementId;
	protected String textLabel;
	protected ShapeType type; // TODO: Getter/Setter weird
	protected String groupRef; // if part of group
	protected RectProperty rectProperty;
	protected FontProperty fontProperty;
	protected ShapeStyleProperty shapeStyleProperty;
	protected String rotation; 
	protected List<Comment> comments; // optional
	protected List<DynamicProperty> dynamicProperties; // optional
	protected List<AnnotationRef> annotationRefs; // optional
	protected List<CitationRef> citationRefs; // optional
	protected List<EvidenceRef> evidenceRefs; // optional


	// Add Constructors

	
	




	/**
	 * Gets the orientation for shapes.
	 * 
	 * @return the orientation for
	 */
	public int getOrientation() {
		double r = rotation / Math.PI;
		if (r < 1.0 / 4 || r >= 7.0 / 4)
			return OrientationType.TOP;
		if (r > 5.0 / 4 && r <= 7.0 / 4)
			return OrientationType.LEFT;
		if (r > 3.0 / 4 && r <= 5.0 / 4)
			return OrientationType.BOTTOM;
		if (r > 1.0 / 4 && r <= 3.0 / 4)
			return OrientationType.RIGHT;
		return 0;
	}

	/**
	 * Sets the orientation for shapes.
	 * 
	 * @param orientation the orientation integer.
	 */
	public void setOrientation(int orientation) {
		switch (orientation) {
		case OrientationType.TOP:
			setRotation(0);
			break;
		case OrientationType.LEFT:
			setRotation(Math.PI * (3.0 / 2));
			break;
		case OrientationType.BOTTOM:
			setRotation(Math.PI);
			break;
		case OrientationType.RIGHT:
			setRotation(Math.PI / 2);
			break;
		}
	}

	/**
	 * Rotation of this shape object.
	 */
	protected double rotation = 0; // in radians

	/**
	 * Gets the rotation of this shape.
	 * 
	 * @return rotation the rotation of this shape.
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Sets the rotation of this shape.
	 * 
	 * @return rotation the rotation of this shape.
	 */
	public void setRotation(double rotation) {
		if (this.rotation != rotation) {
			this.rotation = rotation;

			// Rotation is not stored for State, so we use a dynamic property.
			// TODO: remove after next GPML update.
			if (objectType == ObjectType.STATE && rotation != 0) {
				setDynamicProperty(State.ROTATION_KEY, "" + rotation);
			}
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}

	}
	
	public List<Graphics> getGraphics() {
		return graphics;
	}
	
	// removing graphic from list of graphics
	public void add(Graphics graphic) {
		graphics.add(graphic);
	}

	// removing graphic from list of graphics
	public void remove(Graphics graphic) {
		graphics.remove(graphic);
	}


	/**
	 * Gets the groupRef of the shape. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the shape.
	 * 
	 */
	public Object getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the shape. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the shape.
	 * 
	 */
	public void getGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * Gets the text of of the shape.
	 * 
	 * @return textLabel the text of of the shape.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the shape.
	 * 
	 * @param textLabel the text of of the shape.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Gets the type of the shape.
	 * 
	 * @return type the type of shape, e.g. complex.
	 */
	public ShapeType getType() {
		return type;
	}

	/**
	 * Sets the type of the shape.
	 * 
	 * @param type the type of shape, e.g. complex.
	 */
	public void setType(ShapeType type) {
		this.type = type;
	}

	/**
	 * Gets the value of the rotation property.
	 * 
	 * @return rotation the rotation of the shape.
	 */
	public String getRotation() {
		if (rotation == null) {
			return "Top";
		} else {
			return rotation;
		}
	}

	/**
	 * Sets the value of the rotation property.
	 * 
	 * @param rotation the rotation of the shape.
	 */
	public void setRotation(String value) {
		this.rotation = value;
	}

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
}
