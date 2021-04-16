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

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.type.StateType;

/**
 * This class stores all information relevant to a State pathway element.
 * 
 * @author finterly
 */
public class State extends ElementInfo {

	private DataNode dataNode; // parent dataNode (NB: elementRef was formerly elementId of parent data node)
	private String textLabel;
	private StateType type;
	private double relX;
	private double relY;
	private double width;
	private double height;
	private FontProperty fontProperty;
	private ShapeStyleProperty shapeStyleProperty;
	private Xref xref; // optional

	/**
	 * Instantiates a State pathway element given all possible parameters.
	 * 
	 * @param elementId          the unique pathway element identifier.
	 * @param pathwayModel       the parent pathway model.
	 * @param dataNode           the parent data node (NB: elementRef was formerly
	 *                           elementId of parent data node).
	 * @param textLabel          the text label of the state.
	 * @param type               the type of the state, e.g. protein modification.
	 * @param relX               the relative x coordinates on the parent object,
	 *                           where 0,0 is at the center of the object and 1,1 at
	 *                           the bottom-right corner of the object.
	 * @param relY               the relative y coordinates on the parent object,
	 *                           where 0,0 is at the center of the object and 1,1 at
	 *                           the bottom-right corner of the object.
	 * @param centerXY           TODO
	 * @param width              the pixel value for the x dimensional length.
	 * @param height             the pixel value for the y dimensional length.
	 * @param fontProperty       the font properties, e.g. textColor, fontName...
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor...
	 * @param xref               the state xref.
	 */
	public State(String elementId, PathwayModel pathwayModel, DataNode dataNode, String textLabel, StateType type,
			double relX, double relY, double width, double height, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Xref xref) {
		super(elementId, pathwayModel);
		this.dataNode = dataNode;
		this.textLabel = textLabel;
		this.type = type;
		this.relX = relX;
		this.relY = relY;
		this.width = width;
		this.height = height;
		this.fontProperty = fontProperty;
		this.shapeStyleProperty = shapeStyleProperty;
		this.xref = xref;
	}

	/**
	 * Instantiates a State pathway element given all possible parameters except
	 * xref.
	 */
	public State(String elementId, PathwayModel pathwayModel, DataNode dataNode, String textLabel, StateType type,
			double relX, double relY, double width, double height, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty) {
		this(elementId, pathwayModel, dataNode, textLabel, type, relX, relY, width, height, fontProperty,
				shapeStyleProperty, null);
	}

	/**
	 * Returns the parent data node to which the state belongs.
	 * 
	 * NB: prior to GPML2021, elementRef was used to refer to the elementId of
	 * parent data node.
	 * 
	 * @return dataNode the parent data node of the state.
	 */
	public DataNode getDataNode() {
		return dataNode;
	}

	/**
	 * Sets the parent data node to which the state belongs.
	 * 
	 * NB: prior to GPML2021, elementRef was used to refer to the elementId of
	 * parent data node.
	 * 
	 * @param dataNode the parent data node of the state.
	 */
	public void setDataNode(DataNode dataNode) {
		this.dataNode = dataNode;
	}

	/**
	 * Returns the text of of the state.
	 * 
	 * @return textLabel the text of of the state.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the state.
	 * 
	 * @param textLabel the text of of the state.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Returns the type of the state.
	 * 
	 * @return type the type of state, e.g. complex.
	 */
	public StateType getType() {
		return type;
	}

	/**
	 * Sets the type of the state.
	 * 
	 * @param type the type of state, e.g. complex.
	 */
	public void setType(StateType type) {
		this.type = type;
	}

	/**
	 * Returns the relative x coordinate. When the given point is linked to a
	 * pathway element, relX and relY are the relative coordinates on the element,
	 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
	 * of the object.
	 * 
	 * @param relX the relative x coordinate.
	 */
	public double getRelX() {
		return relX;
	}

	/**
	 * Sets the relative x coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relX the relative x coordinate.
	 * @throws IllegalArgumentException if relX is not between -1.0 and 1.0. t
	 */
	public void setRelX(double relX) {
		if (Math.abs(relX) <= 1.0) {
			this.relX = relX;
		} else {
			throw new IllegalArgumentException("relX " + relX + " should be between -1.0 and 1.0");
		}
	}

	/**
	 * Returns the relative y coordinate. When the given point is linked to a
	 * pathway element, relX and relY are the relative coordinates on the element,
	 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
	 * of the object.
	 * 
	 * @param relY the relative y coordinate.
	 */
	public double getRelY() {
		return relY;
	}

	/**
	 * Sets the relative y coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relY the relative y coordinate.
	 */
	public void setRelY(double relY) {
		if (Math.abs(relY) <= 1.0) {
			this.relY = relY;
		} else {
			throw new IllegalArgumentException("relY " + relY + " should be between -1.0 and 1.0");
		}
	}

	/**
	 * Returns the width of the state.
	 * 
	 * @return width the width of the state.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the width of the state.
	 * 
	 * @param width the width of the state.
	 * @throws IllegalArgumentException if width is a negative value.
	 */
	public void setWidth(double width) {
		if (width < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + width);
		} else {
			this.width = width;
		}
	}

	/**
	 * Returns the height of the state.
	 * 
	 * @return height the height of the state.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the height of the state.
	 * 
	 * @param height the height of the state.
	 * @throws IllegalArgumentException if height is a negative value.
	 */
	public void setHeight(double height) {
		if (height < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + height);
		} else {
			this.height = height;
		}
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
	 * Returns the Xref for the state.
	 * 
	 * @return xref the xref of the state.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the state.
	 * 
	 * @param xref the xref of the state.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

	/**
	 * Instantiates state Xref given identifier and dataSource. Checks whether
	 * dataSource string is fullName, systemCode, or invalid.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 * @throws IllegalArgumentException is given dataSource does not exist.
	 */
	public void createXref(String identifier, String dataSource) {
		if (DataSource.fullNameExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		} else if (DataSource.systemCodeExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getByAlias(dataSource));
		} else {
			DataSource.register(dataSource, dataSource);
			System.out.println("DataSource: " + dataSource + " is registered."); // TODO warning
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource)); // TODO fullname/code both ok
		}
	}

}
