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
 * This class stores information for the visual appearance of a two dimensional
 * (shape) object, e.g. Pathway elements: DataNode, State, Shape, Label.
 * 
 * @author finterly
 */
public class ShapeStyleProperty implements Graphics {

	protected Color borderColor; // TODO: Set color by type?
	protected String borderStyle; // enum?
	protected double borderWidth = 1.0; // double?
	protected Color fillColor = null; // TODO: fix transparency implementation
	protected String shapeType; // enum?
	/**
	 * Z-order of this object. Z-order is an ordering of overlapping
	 * two-dimensional objects.
	 */
	protected int zOrder;
	protected PathwayElement parent; // TODO: Getter/Setter

	/**
	 * Set shapeType to NONE if objectType is Label. Otherwise set to RECTANGLE.
	 */
	if(objectType==ObjectType.LABEL)
	{
		this.shapeType = ShapeType.NONE;
	}else
	{
		this.shapeType = ShapeType.RECTANGLE;
	}

	/**
	 * Default IShape shapeType is Rectangle.
	 */
	protected IShape shapeType = ShapeType.RECTANGLE;

	/**
	 * Gets shapeType the IShape.
	 * 
	 * @return shapeType the IShape.
	 */
	public IShape getShapeType() {
		return shapeType;
	}

	/**
	 * Sets shape type to given IShape.
	 * 
	 * @param IShape the shape type.
	 */
	public void setShapeType(IShape shapeType) {
		if (this.shapeType != shapeType) {
			this.shapeType = shapeType;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.SHAPETYPE));
		}
	}
	/**
	 * LINE AND GRAPHLINE DO NOT HAVE FILLCOLOR? Set fillColor to WHITE if
	 * objectType is Line, Label, DataNode, State, or GraphLine. Otherwise set to
	 * null.
	 */
	if(objectType==ObjectType.LINE||objectType==ObjectType.LABEL||objectType==ObjectType.DATANODE||objectType==ObjectType.STATE||objectType==ObjectType.GRAPHLINE)
	{
		this.fillColor = Color.WHITE;
	}else
	{
		this.fillColor = null;
	}

	/**
	 * Gets the border color of an object.
	 * 
	 * @return borderColor the border color of an object.
	 */
	public Color getBorderColor() {
		if (borderColor == null) {
			return new Color(0, 0, 0); // black
		} else {
			return borderColor;
		}
	}

	/**
	 * Sets the border color of an object.
	 * 
	 * @param borderColor the border color of an object.
	 * @throws IllegalArgumentException if color null.
	 */
	public void setBorderColor(Color borderColor) {
		if (borderColor == null)
			throw new IllegalArgumentException();
		if (this.borderColor != borderColor) {
			this.borderColor = borderColor;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.BORDERCOLOR));
		}
	}

	/**
	 * Gets the visual appearance of a border, e.g. Solid or Broken.
	 * 
	 * @return borderStyle the style of a border.
	 */
	public String getBorderStyle() {
		if (borderStyle == null) {
			return "solid"; // enum?
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
			return 0; // TODO: Can borderWidth be zero?
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
		if (this.borderWidth != borderWidth) {
			this.borderWidth = borderWidth;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.LINETHICKNESS));
		}
	}

	/**
	 * Gets the color used to paint the area of an object, not including its border.
	 * 
	 * @return fillColor the fill color of an object.
	 */
	public Color getFillColor() {
		if (fillColor == null) {
			return new Color(255, 255, 255); // white
		} else {
			return fillColor;
		}
	}

	/**
	 * Sets the color used to paint the area of an object, not including its border.
	 * 
	 * @param fillColor the fill color of an object.
	 */
	public void setFillColor(Color fillColor) {
		if (this.fillColor != fillColor) {
			this.fillColor = fillColor;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.FILLCOLOR));
		}
	}

	/**
	 * Checks if fill color is equal to null or the alpha value is equal to 0.
	 * 
	 * @return true if fill color equal to null or alpha value equal to 0, false
	 *         otherwise.
	 */
	public boolean isTransparent() {
		return fillColor == null || fillColor.getAlpha() == 0;
	}

	/**
	 * TODO: Logic seems weird...
	 * 
	 * Sets the alpha component of fillColor to 0 if true, sets the alpha component
	 * of fillColor to 255 if false.
	 * 
	 * @param value the boolean value.
	 */
	public void setTransparent(boolean value) {
		if (isTransparent() != value) {
			if (fillColor == null) {
				fillColor = Color.WHITE;
			}
			int alpha = value ? 0 : 255;
			fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), alpha);
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.TRANSPARENT));
		}
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
	 * Gets the z-order of an object. 
	 * 
	 * @param zOrder the order of an object.
	 */
	public int getZOrder() {
		return zOrder;
	}

	/**
	 * Sets the z-order of an object.
	 * 
	 * @param zOrder the order of an object.
	 */
	public void setZOrder(int zOrder) {
		if (this.zOrder != zOrder) {
			this.zOrder = zOrder;
			parent.fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.ZORDER));

		}
	}

	/**
	 * Gets the parent PathwayElement to which the shape graphic property belongs.
	 * 
	 * @return parent the parent pathway element.
	 */
	public PathwayElement getParent() {
		return parent;
	}
	
	/**
	 * Sets the parent PathwayElement to which the shape graphic property belongs.
	 * 
	 * @return parent the parent pathway element.
	 */
	public void setParent(PathwayElement parent) {
		this.parent = parent;
	}

}
