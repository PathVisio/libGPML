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
public class Shape extends ShapedElement {

	private ShapeType type; // TODO: Getter/Setter weird
	private double rotation = 0; // in radians TODO 
	private String textLabel; // optional

	/**
	 * Instantiates a Label pathway element given all possible parameters.
	 * 
	 * @param elementId          the unique pathway element identifier.
	 * @param pathwayModel       the parent pathway model.
	 * @param comments           the list of comments.
	 * @param dynamicProperties  the list of dynamic properties, key value pairs.
	 * @param annotationRefs     the list of annotations referenced.
	 * @param citationRefs       the list of citations referenced.
	 * @param evidenceRefs       the list of evidences referenced.
	 * @param rectProperty       the centering (position) and dimension properties.
	 * @param fontProperty       the font properties, e.g. textColor, fontName...
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor.
	 * @param groupRef           the parent group in which the pathway element
	 *                           belongs.
	 * @param textLabel          the text of the label.
	 * @param href               the hyperlink of the label.
	 * @param type               the type of the shape, e.g. rectangle, nucleus.
	 * @param rotation           the rotation in radians. TODO
	 * @param textLabel          the text of the shape.
	 */
	public Shape(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Group groupRef, ShapeType type, double rotation, String textLabel) {
		super(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, groupRef);
		this.type = type;
		this.rotation = rotation;
		this.textLabel = textLabel;
	}

	/**
	 * Instantiates a Shape given all possible parameters except textLabel.
	 */
	public Shape(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Group groupRef, ShapeType type, double rotation) {
		this(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, groupRef, type, rotation, null);
	}

	/**
	 * Instantiates a Shape given all possible parameters except groupRef, because
	 * the pathway element does not belong in a group.
	 */
	public Shape(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, ShapeType type, double rotation, String textLabel) {
		this(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, null, type, rotation, textLabel);
	}

	/**
	 * Instantiates a Shape given all possible parameters except groupRef and textLbel, because
	 * the shape neither belongs in a group nor has a textLabel .
	 */
	public Shape(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, ShapeType type, double rotation) {
		this(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, null, type, rotation, null);
	}

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
	 * Gets the rotation of this shape.
	 * 
	 * @return rotation the rotation of the shape.
	 */
	public double getRotation() {

		return rotation;

	}

	/**
	 * Sets the rotation of this shape. TODO 
	 * 
	 * @param rotation the rotation of the shape.
	 */
	public void setRotation(Double rotation) {
		this.rotation = rotation;

//		// Rotation is not stored for State, so we use a dynamic property.
//		// TODO: remove after next GPML update.
//		if (objectType == ObjectType.STATE && rotation != 0) {
//			setDynamicProperty(State.ROTATION_KEY, "" + rotation);
	}

}
