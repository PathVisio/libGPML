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

import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.type.LineStyleType;
import org.pathvisio.model.type.ShapeType;

/**
 * This class stores information for the visual appearance of a two dimensional
 * (shape) object, e.g. Pathway elements: DataNode, State, Shape, Label.
 * 
 * @author finterly
 */
public class ShapeStyleProperty {

	private Color borderColor = Color.decode("#000000"); // black
	private LineStyleType borderStyle = LineStyleType.SOLID; // solid TODO: Fix
	private double borderWidth = 1.0; // TODO: type?
	private Color fillColor = Color.decode("#ffffff"); // white TODO: Transparent if Label
	private ShapeType shapeType = ShapeType.RECTANGLE; // rectangle TODO: NONE if Label.
	private int zOrder; // optional

	/**
	 * Constructor for all shape style properties. Default values in ( ).
	 * 
	 * @param borderColor the color of a border, (Black).
	 * @param borderStyle the style of a border, (Solid).
	 * @param borderWidth the thickness of a border, (1.0).
	 * @param fillColor   the fill color of an object, (White).
	 * @param shapeType   the shape type of an object, (Rectangle).
	 * @param zOrder      the z order, an ordering of overlapping two-dimensional
	 *                    objects.
	 * @param parent      the parent pathway element of these properties.
	 */
	public ShapeStyleProperty(Color borderColor, LineStyleType borderStyle, double borderWidth, Color fillColor,
			ShapeType shapeType, int zOrder, PathwayElement parent) {
		super();
		this.borderColor = borderColor;
		this.borderStyle = borderStyle;
		this.borderWidth = borderWidth;
		this.fillColor = fillColor;
		this.shapeType = shapeType;
		this.zOrder = zOrder;
	}

	/**
	 * Constructor for all shape style properties except zOrder, an optional
	 * attribute.
	 */
	public ShapeStyleProperty(Color borderColor, LineStyleType borderStyle, double borderWidth, Color fillColor,
			ShapeType shapeType, PathwayElement parent) {
		super();
		this.borderColor = borderColor;
		this.borderStyle = borderStyle;
		this.borderWidth = borderWidth;
		this.fillColor = fillColor;
		this.shapeType = shapeType;
	}

	/**
	 * Returns the border color of an object.
	 * 
	 * @return borderColor the border color of an object.
	 */
	public Color getBorderColor() {
		if (borderColor == null) {
			return Color.decode("#000000"); // black
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
		if (borderColor == null) {
			throw new IllegalArgumentException();
		} else {
			this.borderColor = borderColor;
		}
	}

	/**
	 * Returns the visual appearance of a border, e.g. Solid or Broken.
	 * 
	 * @return borderStyle the style of a border.
	 */
	public LineStyleType getBorderStyle() {
		if (borderStyle == null) {
			return LineStyleType.SOLID;
		} else {
			return borderStyle;
		}
	}

	/**
	 * Sets the visual appearance of a border, e.g. Solid or Broken.
	 * 
	 * @param borderStyle the style of a border.
	 */
	public void setBorderStyle(LineStyleType borderStyle) {
		this.borderStyle = borderStyle;
	}

	/**
	 * Returns the pixel value for the border of an object.
	 * 
	 * @return borderWidth the width of a border.
	 */
	public double getBorderWidth() {
		if (borderWidth < 0) {
			return 1.0; // TODO: Can borderWidth be zero?
		} else {
			return borderWidth;
		}
	}

	/**
	 * Sets the pixel value for the border of an object.
	 * 
	 * @param borderWidth the width of a border.
	 * @throws IllegalArgumentException if borderWidth is a negative value.
	 */
	public void setBorderWidth(double borderWidth) {
		if (borderWidth < 0) {
			throw new IllegalArgumentException();
		} else {
			this.borderWidth = borderWidth;
		}
	}

	/**
	 * Returns the color used to paint the area of an object, not including its border.
	 * 
	 * @return fillColor the fill color of an object.
	 */
	public Color getFillColor() {
		if (fillColor == null) {
			return Color.decode("#ffffff"); // white
		} else {
			return fillColor;
		}
	}

	/**
	 * Sets the color used to paint the area of an object, not including its border.
	 * 
	 * @param fillColor the fill color of an object.
	 * @throws IllegalArgumentException if fillColor null.
	 */
	public void setFillColor(Color fillColor) {
		if (fillColor == null) {
			throw new IllegalArgumentException();
		} else {
			this.fillColor = fillColor;
		}
	}

	/**
	 * Returns the visual appearance of a two dimensional object, e.g. Rectangle, Arc,
	 * Mitochondria, Oval.
	 * 
	 * NB: Shape.type is for object type while shapeType is the visual appearance.
	 * For example, an object may have Shape.type "Nucleus" and shapeType "Oval".
	 * 
	 * @return shapeType the visual appearance of an object.
	 */
	public ShapeType getShapeType() {
		if (shapeType == null) {
			return ShapeType.RECTANGLE;
		} else {
			return shapeType;
		}
	}

	/**
	 * Returns the visual appearance of a two dimensional object, e.g. Rectangle, Arc,
	 * Mitochondria, Oval.
	 * 
	 * NB: Shape.type is for object type while shapeType is the visual appearance.
	 * For example, an object may have Shape.type "Nucleus" and shapeType "Oval".
	 * 
	 * @param shapeType the visual appearance of an object.
	 * @throws IllegalArgumentException if shapeType null.
	 */
	public void setShapeType(ShapeType shapeType) {
		if (shapeType == null) {
			throw new IllegalArgumentException();
		} else {
			this.shapeType = shapeType;
		}
	}

	/**
	 * Returns the z-order of an object.
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
		this.zOrder = zOrder;
	}

//	/**
//	 * Returns the parent PathwayElement to which the shape graphic property belongs.
//	 * 
//	 * @return parent the parent pathway element.
//	 */
//	public PathwayElement getParent() {
//		return parent;
//	}
//
//	/**
//	 * Sets the parent PathwayElement to which the shape graphic property belongs.
//	 * 
//	 * @return parent the parent pathway element.
//	 */
//	public void setParent(PathwayElement parent) {
//		this.parent = parent;
//	}

}
