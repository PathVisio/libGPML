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
 * Shape, Label.
 * 
 * @author finterly
 */
public class RectGraphics implements Graphics {

	private double centerX;
	private double centerY;
	private double width;
	private double height;

	/**
	 * Gets the x coordinate of the middle of an object.
	 * 
	 * @return centerX the middle of an object in the x direction.
	 */
	public double getCenterX() {
		return centerX;
	}

	/**
	 * Sets the x coordinate of the middle of an object.
	 * 
	 * @param centerX the middle of an object in the x direction.
	 */
	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	/**
	 * Gets the y coordinate of the middle of an object.
	 * 
	 * @return centerY the middle of an object in the y direction.
	 */
	public double getCenterY() {
		return centerY;
	}

	/**
	 * Sets the y coordinate of the middle of an object.
	 * 
	 * @param centerY the middle of an object in the y direction.
	 */
	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	/**
	 * Gets the pixel value for the x dimensional length of an object.
	 * 
	 * @return width the width of an object.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the pixel value for the x dimensional length of an object.
	 * 
	 * @param width the width of an object.
	 */
	public void setWidth(double value) {
		this.width = value;
	}

	/**
	 * Gets the pixel value for the y dimensional length of an object.
	 * 
	 * @return height the height of an object.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the pixel value for the y dimensional length of an object.
	 * 
	 * @param height the height of an object.
	 */
	public void setHeight(double value) {
		this.height = value;
	}
}
