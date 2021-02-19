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
 * This class stores information for the visual appearance of a two dimensional
 * (shape) object, e.g. Pathway elements: DataNode, State, Shape, Label.
 * 
 * @author finterly
 */
public class ShapeGraphics implements Graphics {

	private String borderColor; //enum?
	private String borderStyle; //enum?
	private double borderWidth; //double? 
	private String fillColor;  
	private String shapeType; //enum?
	private int zOrder;

	/**
	 * Gets the border color of an object.
	 * 
	 * @return borderColor the border color of an object.
	 */
	public String getBorderColor() {
		if (borderColor == null) {
			return "Black";
		} else {
			return borderColor;
		}
	}

	/**
	 * Sets the border color of an object.
	 * 
	 * @param borderColor the border color of an object.
	 */
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * Gets the visual appearance of a border, e.g. Solid or Broken.
	 * 
	 * @return borderStyle the style of a border.
	 */
	public String getBorderStyle() {
		if (borderStyle == null) {
			return "solid"; //enum?
		} else {
			return borderStyle;
		}
	}

	/**
	 * Sets the visual appearance of a border, e.g. Solid or Broken.
	 * 
	 * @param borderStyle the style of a border.
	 */
	public void setBorderStyle(String borderStyle) {
		this.borderStyle = borderStyle;
	}

	/**
	 * Gets the pixel value for the border of an object.
	 * 
	 * @return borderWidth the width of a border.
	 */
	public double getBorderWidth() {
		if (borderWidth == 0) {
			return 0;
		} else {
			return borderWidth;
		}
	}

	/**
	 * Sets the pixel value for the border of an object.
	 * 
	 * @param borderWidth the width of a border.
	 */
	public void setBorderWidth(double borderWidth) {
		this.borderWidth = borderWidth;
	}

	/**
	 * Gets the color used to paint the area of an object, not including its border.
	 * 
	 * @return fillColor the fill color of an object. 
	 */
	public String getFillColor() {
		if (fillColor == null) {
			return "White";
		} else {
			return fillColor;
		}
	}

	/**
	 * Sets the color used to paint the area of an object, not including its border.
	 * 
	 * @param fillColor the fill color of an object.
	 */
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	/**
	 * Gets the visual appearance of a two dimensional object, e.g. Rectangle, Arc,
	 * Nucleus. Not to be confused with Shape.type, the categories of shapeType,
	 * e.g. Basic, CellularComponent, Virus...
	 * 
	 * @return shapeType the visual appearance of an object.
	 * 
	 */
	public String getShapeType() {
		if (shapeType == null) {
			return "Rectangle";
		} else {
			return shapeType;
		}
	}

	/**
	 * Sets the visual appearance of a two dimensional object, e.g. Rectangle, Arc,
	 * Nucleus. Not to be confused with Shape.type, the categories of shapeType,
	 * e.g. Basic, CellularComponent, Virus...
	 * 
	 * @param shapeType the visual appearance of an object.
	 * 
	 */
	public void setShapeType(String shapeType) {
		this.shapeType = shapeType;
	}

	/**
	 * Gets the order of an object.
	 * 
	 * @param zOrder the order of an object.
	 */
	public int getZOrder() {
		return zOrder;
	}

	/**
	 * Sets the order of an object.
	 * 
	 * @param zOrder the order of an object.
	 */
	public void setZOrder(int zOrder) {
		this.zOrder = zOrder;
	}

}
