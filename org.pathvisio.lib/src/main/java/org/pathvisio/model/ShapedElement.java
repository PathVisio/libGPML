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

/**
 * This class stores information for shaped pathway element, e.g. DataNode,
 * TODO: STATE?, Label, Shape, and Group.
 * 
 * @author finterly
 */
public class ShapedElement extends CommentGroupElement {
	
	private String groupRef = null; // optional
	private RectProperty rectProperty;
	private FontProperty fontProperty;
	private ShapeStyleProperty shapeStyleProperty;

	/**
	 * Returns the groupRef of the pathway element. A groupRef indicates an object
	 * is part of a gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the datanode.
	 * 
	 */
	public String getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the pathway element. A groupRef indicates an object is
	 * part of a gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the datanode.
	 * 
	 */
	public void setGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}
	
	
	/**
	 * @return
	 */
	public RectProperty getRectProperty() {
		return rectProperty;
	}

	/**
	 * @param rectProperty
	 */
	public void setRectProperty(RectProperty rectProperty) {
		this.rectProperty = rectProperty;
	}

	/**
	 * @return
	 */
	public FontProperty getFontProperty() {
		return fontProperty;
	}

	/**
	 * @param fontProperty
	 */
	public void setFontProperty(FontProperty fontProperty) {
		this.fontProperty = fontProperty;
	}

	/**
	 * @return
	 */
	public ShapeStyleProperty getShapeStyleProperty() {
		return shapeStyleProperty;
	}

	/**
	 * @param shapeStyleProperty
	 */
	public void setShapeStyleProperty(ShapeStyleProperty shapeStyleProperty) {
		this.shapeStyleProperty = shapeStyleProperty;
	}

}
