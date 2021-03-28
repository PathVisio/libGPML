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
 * This class stores information for shaped pathway element, e.g. DataNode,
 * TODO: STATE?, Label, Shape, and Group.
 * 
 * @author finterly
 */
public abstract class ShapedElement extends ElementInfo {

	private RectProperty rectProperty;
	private FontProperty fontProperty;
	private ShapeStyleProperty shapeStyleProperty;
	private Group groupRef; // optional, the parent group to which a pathway element belongs.

	/**
	 * Instantiates a Shaped pathway element which is also a member of a group
	 * pathway element and thus has groupRef. In GPML, groupRef refers to the
	 * elementId (formerly groupId) of the parent gpml:Group. Note, a group can also
	 * belong in another group.
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
	 * @param group              the parent group in which the pathway element
	 *                           belongs.
	 */
	public ShapedElement(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Group groupRef) {
		super(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs);
		this.rectProperty = rectProperty;
		this.fontProperty = fontProperty;
		this.shapeStyleProperty = shapeStyleProperty;
		if (groupRef != null) {
			setGroupRef(groupRef); // set group TODO
		}
	}

	/**
	 * Instantiates a Shaped pathway element given all possible parameters except
	 * groupRef, because the pathway element is not a member of a group. 
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
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor,
	 */
	public ShapedElement(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty) {
		this(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, null);

	}

	/**
	 * Returns the centering and dimension properties of the pathway element.
	 * 
	 * @return rectProperty the centering and dimension properties.
	 */
	public RectProperty getRectProperty() {
		return rectProperty;
	}

	/**
	 * Sets the centering and dimension properties of the pathway element.
	 * 
	 * @param rectProperty the centering and dimension properties.
	 */
	public void setRectProperty(RectProperty rectProperty) {
		this.rectProperty = rectProperty;
	}

	/**
	 * Returns the font properties of the pathway element, e.g. textColor,
	 * fontName...
	 * 
	 * @return fontProperty the font properties.
	 */
	public FontProperty getFontProperty() {
		return fontProperty;
	}

	/**
	 * Sets the font properties of the pathway element, e.g. textColor, fontName...
	 * 
	 * @param fontProperty the font properties.
	 */
	public void setFontProperty(FontProperty fontProperty) {
		this.fontProperty = fontProperty;
	}

	/**
	 * Returns the shape style properties of the pathway element, e.g.
	 * borderColor...
	 * 
	 * @return shapeStyleProperty the shape style properties.
	 */
	public ShapeStyleProperty getShapeStyleProperty() {
		return shapeStyleProperty;
	}

	/**
	 * Sets the shape style properties of the pathway element, e.g. borderColor...
	 * 
	 * @param shapeStyleProperty the shape style properties.
	 */
	public void setShapeStyleProperty(ShapeStyleProperty shapeStyleProperty) {
		this.shapeStyleProperty = shapeStyleProperty;
	}

	/**
	 * Returns the parent group of the group. In GPML, groupRef refers to the
	 * elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of the group.
	 */
	public Group getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the parent group of the group. The group is added to the pathwayElements
	 * list of the parent group.
	 * 
	 * @param groupRef the parent group of the group.
	 */
	public void setGroupRef(Group groupRef) {
		if (groupRef.getPathwayElements() != null && groupRef != null) {
			groupRef.addPathwayElement(this);
			this.groupRef = groupRef;
		}

		// TODO how to handle groupRef properly...

//			if (groupRef == null || !groupRef.equals(id)) {
//				if (pathwayModel != null) {
//					if (groupRef != null) {
//						pathwayModel.removeGroupRef(groupRef, this);
//					}
//					// Check: move add before remove??
//					if (id != null) {
//						pathwayModel.addGroupRef(id, this);
//					}
//				}
//				groupRef = id;
//			}
	}
}
