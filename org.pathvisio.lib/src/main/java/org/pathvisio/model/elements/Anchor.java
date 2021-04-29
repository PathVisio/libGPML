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
package org.pathvisio.model.elements;

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.type.AnchorType;

/**
 * This class stores information for an Anchor pathway element. Anchor element
 * is a connection point on a graphical line or an interaction.
 * 
 * @author finterly
 */
public class Anchor extends PathwayElement {

	private LineElement lineElement; // parent line element
	private double position;
	private AnchorType shapeType = AnchorType.NONE;

	/**
	 * Instantiates an Anchor pathway element.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param lineElement  the parent line pathway element.
	 * @param position     the proportional distance of an anchor along the line it
	 *                     belongs to.
	 * @param shapeType    the visual representation of an anchor.
	 */
	public Anchor(String elementId, PathwayModel pathwayModel, LineElement lineElement, double position,
			AnchorType shapeType) {
		super(elementId, pathwayModel);
		this.lineElement = lineElement;
		if (position < 0 || position > 1) {
			throw new IllegalArgumentException("Invalid position value '" + position + "' must be between 0 and 1");
		}
		this.position = position; // must be valid
		if (shapeType != null)
			this.shapeType = shapeType;
	}

	/**
	 * Returns the parent interaction or graphicalLine to which the anchor belongs.
	 * 
	 * @return lineElement the parent line element of the anchor.
	 */
	public LineElement getLineElement() {
		return lineElement;
	}

	/**
	 * Sets the parent interaction or graphicalLine to which the anchor belongs.
	 * 
	 * @param lineElement the parent line element of the anchor.
	 */
	public void setLineElement(LineElement lineElement) {
		this.lineElement = lineElement;
	}

	/**
	 * Gets the proportional distance of an anchor along the line it belongs to,
	 * between 0 and 1.
	 * 
	 * @return position the position of the anchor.
	 */
	public double getPosition() {
		return position;
	}

	/**
	 * Sets the proportional distance of an anchor along the line it belongs to,
	 * between 0 and 1.
	 * 
	 * @param position the position of the anchor.
	 */
	public void setPosition(double position) {
		if (position < 0 || position > 1) {
			throw new IllegalArgumentException("Invalid position value '" + position + "' must be between 0 and 1");
		}
		this.position = position;
	}

	/**
	 * Gets the visual representation of an anchor, e.g., none, square.
	 * 
	 * @return shapeType the shape type of the anchor. Return default square
	 *         shapeType if null.
	 */
	public AnchorType getShapeType() {
		if (shapeType == null) {
			return AnchorType.SQUARE;
		} else {
			return shapeType;
		}
	}

	/**
	 * Sets the shapeType for given anchor pathway element.
	 * 
	 * @param shapeType the shape type of the anchor.
	 * @throws IllegalArgumentException if shapeType null.
	 */
	public void setShape(AnchorType shapeType) {
		if (shapeType == null) {
			throw new IllegalArgumentException();
		} else {
			this.shapeType = shapeType;
		}
	}

}
