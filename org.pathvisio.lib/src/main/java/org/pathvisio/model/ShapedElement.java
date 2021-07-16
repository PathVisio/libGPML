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

import org.pathvisio.model.graphics.FontProperty;
import org.pathvisio.model.graphics.RectProperty;
import org.pathvisio.model.graphics.ShapeStyleProperty;
import org.pathvisio.model.ref.ElementInfo;

/**
 * This class stores information for shaped pathway element {@link DataNode},
 * {@link Label}, {@link Shape}, and {@link Group}.
 * 
 * @author finterly
 */
public abstract class ShapedElement extends ElementInfo implements Groupable {

	private RectProperty rectProperty;
	private FontProperty fontProperty;
	private ShapeStyleProperty shapeStyleProperty;
	private Group groupRef; // optional, the parent group to which a pathway element belongs.

	/**
	 * Instantiates a shaped pathway element. Property groupRef is to be set by
	 * {@link #setGroupRefTo(Group)}. In GPML, groupRef refers to the elementId
	 * (formerly groupId) of the parent gpml:Group. Note, a group can also belong in
	 * another group.
	 * 
	 * @param rectProperty       the centering (position) and dimension properties.
	 * @param fontProperty       the font properties, e.g. textColor, fontName...
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor.
	 */
	public ShapedElement(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty) {
		super();
		this.rectProperty = rectProperty;
		this.fontProperty = fontProperty;
		this.shapeStyleProperty = shapeStyleProperty;
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
	 * Returns the parent group of the pathway element. In GPML, groupRef refers to
	 * the elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of the pathway element.
	 */
	public Group getGroupRef() {
		return groupRef;
	}

	/**
	 * Checks whether this pathway element belongs to a group.
	 *
	 * @return true if and only if the group of this pathway element is effective.
	 */
	public boolean hasGroupRef() {
		return getGroupRef() != null;
	}

	/**
	 * Verifies if given parent group is new and valid. Sets the parent group of the
	 * pathway element. Adds this pathway element to the the pathwayElements list of
	 * the new parent group. If there is an old parent group, this pathway element
	 * is removed from its pathwayElements list.
	 * 
	 * @param groupRef the new parent group to set.
	 */
	public void setGroupRefTo(Group groupRef) {
		if (groupRef == null)
			throw new IllegalArgumentException("Invalid group.");
		if (hasGroupRef()) {
			Group formerGroup = getGroupRef();
			formerGroup.removePathwayElement(this);
		}
		setGroupRef(groupRef);
		if (!groupRef.hasPathwayElement(this))
			groupRef.addPathwayElement(this);
	}

	/**
	 * Sets the parent group for this pathway element.
	 * 
	 * @param groupRef the given group to set.
	 */
	private void setGroupRef(Group groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	public void unsetGroupRef() {
		if (hasGroupRef()) {
			Group groupRef = getGroupRef();
			setGroupRef(null);
			if (groupRef.hasPathwayElement(this))
				groupRef.removePathwayElement(this);
		}
	}

	/**
	 * Terminates this pathway element. The pathway model, if any, is unset from
	 * this pathway element. Links to all annotationRefs, citationRefs, and
	 * evidenceRefs are removed from this data node.
	 */
	@Override
	public void terminate() {
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();// TODO
		unsetGroupRef();
		unsetPathwayModel();
	}

}
