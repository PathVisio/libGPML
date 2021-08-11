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
package org.pathvisio.model.graphics;

import java.awt.Color;

import org.pathvisio.io.listener.PathwayElementEvent;
import org.pathvisio.model.type.ConnectorType;
import org.pathvisio.model.type.LineStyleType;

/**
 * This class stores information for the visual appearance of a line object,
 * e.g. Pathway elements: Interaction or GraphicalLine.
 * 
 * @author finterly
 */
public class LineStyleProperty extends Graphics{

	private Color lineColor = Color.decode("#000000"); // black
	private LineStyleType lineStyle = LineStyleType.SOLID; // solid, dashed, or double
	private double lineWidth = 1.0; // 1.0
	private ConnectorType connectorType = ConnectorType.STRAIGHT; // straight, elbow, curved...
	private int zOrder; // optional

	/**
	 * Constructor for all line style properties. Default values in ( ).
	 * 
	 * @param lineColor     the color of a line, (#000000 black).
	 * @param lineStyle     the style of a line, (Solid).
	 * @param lineWidth     the pixel value for the width of a line, (1.0).
	 * @param connectorType the connector type of a line, (Straight}.
	 * @param zOrder        the z order, an ordering of overlapping two-dimensional
	 *                      objects.
	 */
	public LineStyleProperty(Color lineColor, LineStyleType lineStyle, double lineWidth, ConnectorType connectorType,
			int zOrder) {
		this.lineColor = lineColor;
		this.lineStyle = lineStyle;
		this.lineWidth = lineWidth;
		this.connectorType = connectorType;
		this.zOrder = zOrder;
	}

	/**
	 * Constructor for all line style properties except zOrder, an optional
	 * attribute.
	 */
	public LineStyleProperty(Color lineColor, LineStyleType lineStyle, double lineWidth, ConnectorType connectorType) {
		this.lineColor = lineColor;
		this.lineStyle = lineStyle;
		this.lineWidth = lineWidth;
		this.connectorType = connectorType;
	}

	/**
	 * Returns the color of a line.
	 * 
	 * @return lineColor the color of a line.
	 */
	public Color getLineColor() {
		if (lineColor == null) {
			return Color.decode("#000000"); // black
		} else {
			return lineColor;
		}
	}

	/**
	 * Sets the color of a line.
	 * 
	 * @param lineColor the color of a line.
	 * @throws IllegalArgumentException if color null.
	 */
	public void setLineColor(Color lineColor) {
		if (lineColor == null) {
			throw new IllegalArgumentException();
		} else {
			this.lineColor = lineColor;
		}
	}

	/**
	 * Returns the visual appearance of a line, e.g. Solid or Broken.
	 * 
	 * @return lineStyle the style of a line.
	 */
	public LineStyleType getLineStyle() {
		if (lineStyle == null) {
			return LineStyleType.SOLID;
		} else {
			return lineStyle;
		}
	}

	/**
	 * Sets the visual appearance of a line, e.g. Solid or Broken.
	 * 
	 * @param lineStyle the style of a line.
	 * @throws IllegalArgumentException if lineStyle null.
	 */
	public void setLineStyle(LineStyleType lineStyle) {
		if (lineStyle == null) {
			throw new IllegalArgumentException();
		}
		if (this.lineStyle != lineStyle) {
			this.lineStyle = lineStyle;
//			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
		}
	}


	/**
	 * Returns the pixel value for the width of a line.
	 * 
	 * @return lineWidth the width of a line.
	 */
	public double getLineWidth() {
		if (lineWidth < 0) {
			return 1.0;
		} else {
			return lineWidth;
		}
	}

	/**
	 * Sets the pixel value for the width of a line.
	 * 
	 * @param lineWidth the width of a line.
	 * @throws IllegalArgumentException if lineWidth is a negative value.
	 */
	public void setLineWidth(double lineWidth) {
		if (lineWidth < 0) {
			throw new IllegalArgumentException();
		} else {
			this.lineWidth = lineWidth;
		}
	}

	/**
	 * Returns the value of the connectorType property. Specifies a set of rules to
	 * govern layout of Graphical Lines and Interactions. PathVisio (Java): Line
	 * Type and GPML: ConnectorType e.g. Curved, Elbow, Straight
	 * 
	 * @return connectorType the layout of a line.
	 */
	public ConnectorType getConnectorType() {
		if (connectorType == null) {
			return ConnectorType.STRAIGHT;
		} else {
			return connectorType;
		}
	}

	/**
	 * Sets the value of the connectorType property. Specifies a set of rules to
	 * govern layout of Graphical Lines and Interactions. PathVisio (Java): Line
	 * Type and GPML: ConnectorType e.g. Curved, Elbow, Straight
	 * 
	 * @param connectorType the layout of a line.
	 * @throws IllegalArgumentException if ConnectorType null.
	 */
	public void setConnectorType(ConnectorType connectorType) {
		if (connectorType == null) {
			throw new IllegalArgumentException();
		} 
		if (!this.connectorType.equals(connectorType)) {
			this.connectorType = connectorType;
			//TODO 
		    propChangeSupport.firePropertyChange("connectorType", this.connectorType, connectorType);
		}
	}
	
	
	/**
	 * Returns the order of a line.
	 * 
	 * @return zOrder the order of a line.
	 */
	public int getZOrder() {
		return zOrder;
	}

	/**
	 * Sets the order of a line.
	 * 
	 * @param zOrder the order of a line.
	 */
	public void setZOrder(int zOrder) {
		this.zOrder = zOrder;
	}

}
