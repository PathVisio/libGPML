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

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.ElementLink.ElementRefContainer;

/**
 * This class stores all information relevant to a State pathway element.
 * 
 * @author finterly
 */
public class State extends ElementInfo {

	private DataNode dataNode; // parent dataNode, previously elementRef
	private String textLabel;
	private StateType type = StateType.PHOSPHORYLATED; // TODO: Getter/Setter weird
	private double relX;
	private double relY;
	private double width;
	private double height;
	private FontProperty fontProperty;
	private ShapeStyleProperty shapeStyleProperty;
	private Xref xref; // optional

	
	/**
	 * 
	 * @param elementId
	 * @param pathwayModel
	 * @param dataNode
	 * @param textLabel
	 * @param type
	 * @param relX
	 * @param relY
	 * @param width
	 * @param height
	 * @param fontProperty
	 * @param shapeStyleProperty
	 */
	public State(String elementId, PathwayModel pathwayModel, DataNode dataNode, String textLabel, StateType type,
			double relX, double relY, double width, double height, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty) {
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
	}

	/**
	 * @param elementId
	 * @param pathwayModel
	 * @param dataNode
	 * @param textLabel
	 * @param type
	 * @param relX
	 * @param relY
	 * @param width
	 * @param height
	 * @param fontProperty
	 * @param shapeStyleProperty
	 * @param xref
	 */
	public State(String elementId, PathwayModel pathwayModel, DataNode dataNode, String textLabel, StateType type,
			double relX, double relY, double width, double height, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Xref xref) {
		this(textLabel, pathwayModel, dataNode, textLabel, type, height, height, height, height, fontProperty,
				shapeStyleProperty);
		this.xref = xref;
	}



	public DataNode getDataNode() {
		return dataNode;
	}

	public void setDataNode(DataNode dataNode) {
		this.dataNode = dataNode;
	}

	/**
	 * Gets the text of of the state.
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
	 * Gets the type of the state.
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
	 * relX property, used by State. Should normally be between -1.0 and 1.0, where
	 * 1.0 corresponds to the edge of the parent object
	 */
	public double getRelX() {
		return relX;
	}

	/**
	 * See getRelX
	 */
	public void setRelX(double relX) {
		this.relX = relX;
	}

	/**
	 * relX property, used by State. Should normally be between -1.0 and 1.0, where
	 * 1.0 corresponds to the edge of the parent object
	 */
	public double getRelY() {
		return relY;
	}

	/**
	 * See getRelX
	 */
	public void setRelY(double relY) {
		this.relY = relY;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public FontProperty getFontProperty() {
		return fontProperty;
	}

	public void setFontProperty(FontProperty fontProperty) {
		this.fontProperty = fontProperty;
	}

	public ShapeStyleProperty getShapeStyleProperty() {
		return shapeStyleProperty;
	}

	public void setShapeStyleProperty(ShapeStyleProperty shapeStyleProperty) {
		this.shapeStyleProperty = shapeStyleProperty;
	}

	/**
	 * Gets the state Xref.
	 * 
	 * @return xref the state xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of state Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}

}
