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
 * This class stores all information relevant to an Anchor pathway element.
 * Anchor element is a connection point on a graphical line or an interaction.
 * 
 * @author finterly
 */
public class Anchor {

	private String elementId;
	private double position;
	private Coordinate coordinate;
//	private double x;
//	private double y;
	private String shapeType;
	private PathwayElement parent;

	/**
	 * Instantiates an Anchor pathway element.
	 * 
	 * @param elementId the unique id of the anchor.
	 * @param position  the proportional distance of an anchor along the line it
	 *                  belongs to.
	 * @param x         the x coordinate position of the anchor.
	 * @param y         the y coordinate position of the anchor.
	 * @param shapeType the visual representation of an anchor.
	 */
	public Anchor(String elementId, double position, double x, double y, String shapeType) {
		this.elementId = elementId;
		this.position = position;
		this.x = x;
		this.y = y;
		if (shapeType != null)
			this.shapeType = shapeType;
	}

	/**
	 * Gets the elementId of the anchor.
	 * 
	 * @return elementId the unique id of the anchor.
	 * 
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the anchor.
	 * 
	 * @param elementId the unique id of the anchor.
	 * 
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the proportional distance of an anchor along the line it belongs to,
	 * between 0 and 1.
	 * 
	 * @return position the position of the anchor.
	 */
	public double getPosition() {
		return position;
	}

	/**
	 * Sets the proportional distance of an anchor along the line it belongs to,
	 * between 0 and 1.
	 * 
	 * @param position the position of the anchor.
	 */
	public void setPosition(double position) {
		this.position = position;
	}

	/**
	 * Gets the x and y coordinate position of the anchor.
	 * 
	 * @return coordinate the coordinate position of the anchor.
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}

	/**
	 * Gets the x and y coordinate position of the anchor.
	 * 
	 * @param coordinate the coordinate position of the anchor.
	 */
	public void setX(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
//	/**
//	 * Gets the x coordinate position of the anchor.
//	 * 
//	 * @return x the x coordinate position of the anchor.
//	 */
//	public double getX() {
//		return x;
//	}
//
//	/**
//	 * Sets the x coordinate position of the anchor.
//	 * 
//	 * @param x the x coordinate position of the anchor.
//	 */
//	public void setX(double x) {
//		this.x = x;
//	}
//
//	/**
//	 * Gets the y coordinate position of the anchor.
//	 * 
//	 * @return y the y coordinate position of the anchor.
//	 */
//	public double getY() {
//		return y;
//	}
//
//	/**
//	 * Sets the y coordinate position of the anchor.
//	 * 
//	 * @param y the y coordinate position of the anchor.
//	 */
//	public void setY(double y) {
//		this.y = y;
//	}

	/**
	 * Gets the visual representation of an anchor, e.g., none, square.
	 * 
	 * @return shapeType the shape type of the anchor.
	 */
	public String getShapeType() {
		if (shapeType == null) {
			return "None";
		} else {
			return shapeType;
		}
	}

	/**
	 * Sets the visual representation of an anchor, e.g., none, square.
	 * 
	 * @param shapeType the shape type of the anchor.
	 */
	public void setShapeType(String shapeType) {
		this.shapeType = shapeType;
	}

}
