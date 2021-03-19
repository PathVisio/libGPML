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
 * This class stores information for centering (position) and dimension
 * properties of a two dimensional object, e.g. Pathway elements: DataNode,
 * State, Label, Shape, and Group. Is gpml:RectAttributes in GPML.
 * 
 * @author finterly
 */
public class RectProperty {

	private Coordinate centerXY;
	private double width;
	private double height;

	/**
	 * Constructor for centering and dimension properties.
	 * 
	 * @param centerX the x coordinate of the middle of an object.
	 * @param centerY the y coordinate of the middle of an object.
	 * @param width   the pixel value for the x dimensional length of an object.
	 * @param height  the pixel value for the y dimensional length of an object.
	 */
	public RectProperty(Coordinate centerXY, double width, double height) {
		this.centerXY = centerXY;
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the center x and y coordinate of an object.
	 * 
	 * @return centerXY the middle of an object in the x and y direction.
	 */
	public Coordinate getCenterXY() {
		return centerXY;
	}

	/**
	 * Sets the center x and y coordinate of an object.
	 * 
	 * @param centerXY the middle of an object in the x and y direction.
	 */
	public void setCenterXY(Coordinate centerXY) {
		this.centerXY = centerXY;
	}

	/**
	 * Returns the width of an object.
	 * 
	 * @return width the width of an object.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the width of an object.
	 * 
	 * @param width the width of an object.
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
	 * Returns the height of an object.
	 * 
	 * @return height the height of an object.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the height of an object.
	 * 
	 * @param height the height of an object.
	 * @throws IllegalArgumentException if height is a negative value.
	 */
	public void setHeight(double height) {
		if (height < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + height);
		} else {
			this.height = height;
		}
	}
}
