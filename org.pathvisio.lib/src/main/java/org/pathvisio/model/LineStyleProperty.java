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

import java.awt.Color;

/**
 * This class stores information for the visual appearance of a line object,
 * e.g. Pathway elements: Interaction or GraphicalLine.
 * 
 * @author finterly
 */
public class LineStyleProperty implements Graphics {

	protected Color lineColor = Color.BLACK;
	/**
	 * The visual appearance of a line, e.g. Solid or Broken.
	 */
	protected int lineStyle = LineStyleType.SOLID; // TODO: int? enum?
	protected double lineWidth = 1.0; // double
	private ConnectorType connectorType = ConnectorType.STRAIGHT; // enum?
	protected int zOrder;
	protected PathwayElement parent; // TODO: Getter/Setter

	/**
	 * Gets the color of a line.
	 * 
	 * @return lineColor the color of a line.
	 */
	public Color getLineColor() {
		if (lineColor == null) {
			return new Color(0, 0, 0);
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
		if (lineColor == null)
			throw new IllegalArgumentException();
		if (this.lineColor != lineColor) {
			this.lineColor = lineColor;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.LINECOLOR));
		}
	}

	/**
	 * Gets the visual appearance of a line, e.g. Solid or Broken.
	 * 
	 * @return lineStyle the style of a line.
	 */
	public int getLineStyle() {
//		if (lineStyle == null) {
//			return "Solid"; // TODO:enum
//		} else {
		return lineStyle;
//		}
	}

	/**
	 * Sets the visual appearance of a line, e.g. Solid or Broken.
	 * 
	 * @param lineStyle the style of a line.
	 */
	public void setLineStyle(int lineStyle) {
		if (this.lineStyle != lineStyle) {
			this.lineStyle = lineStyle;
			// handle LineStyle.DOUBLE until GPML is updated
			// TODO: remove after next GPML update
//			if (this.lineStyle == LineStyleType.DOUBLE)
//				setDynamicProperty(LineStyleType.DOUBLE_LINE_KEY, "Double");
//			else
//				setDynamicProperty(LineStyleType.DOUBLE_LINE_KEY, null);
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.LINESTYLE));
		}
	}

	/**
	 * Gets the pixel value for the width of a line.
	 * 
	 * @return lineWidth the width of a line.
	 */
	public double getLineWidth() {
		if (lineWidth == 0) {
			return 1.0;
		} else {
			return lineWidth;
		}
	}

	/**
	 * Sets the pixel value for the width of a line.
	 * 
	 * @param lineWidth the width of a line.
	 */
	public void setLineWidth(double lineWidth) {
		if (this.lineWidth != lineWidth) {
			this.lineWidth = lineWidth;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.LINETHICKNESS));
		}
	}

	/**
	 * Gets the value of the connectorType property. Specifies a set of rules to
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
	 */
	public void setConnectorType(ConnectorType connectorType) {
		if (connectorType == null) {
			throw new IllegalArgumentException();
		}
		if (!this.connectorType.equals(connectorType)) {
			this.connectorType = connectorType;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.CONNECTORTYPE));
		}
	}

	/**
	 * Gets the order of a line.
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
		if (this.zOrder != zOrder) {
			this.zOrder = zOrder;
			parent.fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.ZORDER));

		}
	}

}
