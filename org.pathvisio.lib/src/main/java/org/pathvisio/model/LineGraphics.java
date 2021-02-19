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
 * This class stores information for the visual appearance of a line object,
 * e.g. Pathway elements: Interaction or GraphicalLine.
 * 
 * @author finterly
 */
public class LineGraphics implements Graphics {

	private String lineColor;
	private String lineStyle;
	private double lineWidth; // double or integer?
	private String connectorType; // enum?
	private int zOrder;

	/**
	 * Gets the color of a line.
	 * 
	 * @return lineColor the color of a line.
	 */
	public String getLineColor() {
		if (lineColor == null) {
			return "Black";
		} else {
			return lineColor;
		}
	}

	/**
	 * Sets the color of a line.
	 * 
	 * @param lineColor the color of a line.
	 */
	public void setLineColor(String value) {
		this.lineColor = value;
	}

	/**
	 * Gets the visual appearance of a line, e.g. Solid or Broken.
	 * 
	 * @return lineStyle the style of a line.
	 */
	public String getLineStyle() {
		if (lineStyle == null) {
			return "Solid"; // enum
		} else {
			return lineStyle;
		}
	}

	/**
	 * Sets the visual appearance of a line, e.g. Solid or Broken.
	 * 
	 * @param lineStyle the style of a line.
	 */
	public void setLineStyle(String value) {
		this.lineStyle = value;
	}

	/**
	 * Gets the pixel value for the width of a line.
	 * 
	 * @return lineWidth the width of a line.
	 */
	public double getLineWidth() {
		if (lineWidth == 0) {
			return 1.0F;
		} else {
			return lineWidth;
		}
	}

	/**
	 * Sets the pixel value for the width of a line.
	 * 
	 * @param lineWidth the width of a line.
	 */
	public void setLineWidth(double value) {
		this.lineWidth = value;
	}

	/**
	 * Gets the value of the connectorType property. Specifies a set of rules to
	 * govern layout of Graphical Lines and Interactions. PathVisio (Java): Line
	 * Type and GPML: ConnectorType e.g. Curved, Elbow, Straight
	 * 
	 * @return connectorType the layout of a line.
	 */
	public String getConnectorType() {
		if (connectorType == null) {
			return "Straight";
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
	public void setConnectorType(String value) {
		this.connectorType = value;
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
	public void setZOrder(int value) {
		this.zOrder = value;
	}

}
