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
package org.pathvisio.model.element;

import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.graphics.FontProperty;
import org.pathvisio.model.graphics.RectProperty;
import org.pathvisio.model.graphics.ShapeStyleProperty;

/**
 * This class stores all information relevant to a Shape pathway element.
 * 
 * @author finterly
 */
public class Shape extends ShapedElement {

	private double rotation = 0; // in radians? TODO just read/write
	private String textLabel; // optional

	/**
	 * Instantiates a Label pathway element given all possible parameters.
	 * 
	 * @param rectProperty       the centering (position) and dimension properties.
	 * @param fontProperty       the font properties, e.g. textColor, fontName...
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor.
	 * @param groupRef           the parent group in which the pathway element
	 *                           belongs.
	 * @param rotation           the rotation in radians? TODO
	 * @param textLabel          the text of the shape.
	 */
	public Shape(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			Group groupRef, double rotation, String textLabel) {
		super(rectProperty, fontProperty, shapeStyleProperty, groupRef);
		this.rotation = rotation;
		this.textLabel = textLabel;
	}

	/**
	 * Instantiates a Shape given all possible parameters except textLabel.
	 */
	public Shape(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			Group groupRef, double rotation) {
		this(rectProperty, fontProperty, shapeStyleProperty, groupRef, rotation, null);
	}

	/**
	 * Instantiates a Shape given all possible parameters except groupRef, because
	 * the pathway element does not belong in a group.
	 */
	public Shape(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			double rotation, String textLabel) {
		this(rectProperty, fontProperty, shapeStyleProperty, null, rotation, textLabel);
	}

	/**
	 * Instantiates a Shape given all possible parameters except groupRef and
	 * textLbel, because the shape neither belongs in a group nor has a textLabel .
	 */
	public Shape(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			double rotation) {
		this(rectProperty, fontProperty, shapeStyleProperty, null, rotation, null);
	}

	/**
	 * Returns the text of of the shape.
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
	 * Returns the rotation of this shape.
	 * 
	 * @return rotation the rotation of the shape.
	 */
	public double getRotation() {

		return rotation;

	}

	/**
	 * Sets the rotation of this shape.
	 * 
	 * @param rotation the rotation of the shape.
	 */
	public void setRotation(Double rotation) {
		this.rotation = rotation;
	}

}
