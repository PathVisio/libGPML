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

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.type.AnchorShapeType;

/**
 * This class stores information for an Anchor pathway element. Anchor element
 * is a connection point on a graphical line or an interaction.
 * 
 * @author finterly
 */
public class Anchor extends PathwayElement {

	private LineElement lineElement; // parent line element
	private double position;
	private AnchorShapeType shapeType = AnchorShapeType.NONE;

	/**
	 * Instantiates an Anchor pathway element.
	 * 
	 * @param pathwayModel the parent pathway model.
	 * @param elementId    the unique pathway element identifier.
	 * @param lineElement  the parent line pathway element.
	 * @param position     the proportional distance of an anchor along the line it
	 *                     belongs to.
	 * @param shapeType    the visual representation of an anchor.
	 */
	public Anchor(PathwayModel pathwayModel, String elementId, LineElement lineElement, double position,
			AnchorShapeType shapeType) {
		super(pathwayModel, elementId);
		this.lineElement = lineElement;
		if (position < 0 || position > 1) {
			throw new IllegalArgumentException("Invalid position value '" + position + "' must be between 0 and 1");
		}
		this.position = position; // must be valid
		if (shapeType != null)
			this.shapeType = shapeType;
	}

	/**
	 * Returns the parent interaction or graphicalLine for this anchor.
	 * 
	 * @return lineElement the parent line element of this anchor.
	 */
	public LineElement getLineElement() {
		return lineElement;
	}

	/**
	 * Checks whether this anchor has a parent line element.
	 *
	 * @return true if and only if the line element of this anchor is effective.
	 */
	public boolean hasLineElement() {
		return getLineElement() != null;
	}

	/**
	 * Sets the parent interaction or graphicalLine for this anchor.
	 * 
	 * @param lineElement the line element to set.
	 */
	public void setLineElementTo(LineElement lineElement) {
		if (lineElement == null)
			throw new IllegalArgumentException("Invalid line pathway element.");
		if (hasLineElement())
			throw new IllegalStateException("Anchor already belongs to a line element.");
		setLineElement(lineElement);
		lineElement.addAnchor(this);
	}

	/**
	 * Sets the parent interaction or graphicalLine for this anchor.
	 * 
	 * @param lineElement the line element to set.
	 */
	private void setLineElement(LineElement lineElement) {
		assert (lineElement != null);
		this.lineElement = lineElement;
	}

	/**
	 * Unsets the line element, if any, from this anchor.
	 */
	public void unsetLineElement() {
		if (hasLineElement()) {
			LineElement formerLineElement = this.getLineElement();
			setLineElement(null);
			formerLineElement.removeAnchor(this);
		}
	}

	/**
	 * Returns the proportional distance of an anchor along the line it belongs to,
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
	 * Returns the visual representation of an anchor, e.g., none, square.
	 * 
	 * @return shapeType the shape type of the anchor. Return default square
	 *         shapeType if null.
	 */
	public AnchorShapeType getShapeType() {
		if (shapeType == null) {
			return AnchorShapeType.SQUARE;
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
	public void setShape(AnchorShapeType shapeType) {
		if (shapeType == null) {
			throw new IllegalArgumentException();
		} else {
			this.shapeType = shapeType;
		}
	}
	
	/**
	 * Terminates this anchor. The pathway model and line element, if any, are unset
	 * from this anchor.
	 */
	@Override
	public void terminate() {
		unsetPathwayModel();
		unsetLineElement();
	}

}
