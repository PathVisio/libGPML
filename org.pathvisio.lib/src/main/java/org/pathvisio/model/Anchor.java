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

import java.awt.geom.Point2D;

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
	private AnchorType shapeType = AnchorType.NONE;
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

	
	/**
	 * Instantiates an Anchor pathway element.
	 * 
	 * @param position the proportional distance of an anchor along the line it
	 *                 belongs to.
	 * @param parent   the parent pathway element the anchor belongs to.
	 */
	public MAnchor(double position, PathwayElement parent) {
		super(new double[] { position }, parent);
	}

	/**
	 * Copy constructor for Anchor pathway element which adds shapeType attribute.
	 * 
	 * @param a the Anchor pathway element.
	 */
	public MAnchor(MAnchor a) {
		super(a);
		shapeType = a.shapeType;
	}

	/**
	 * Sets the shapeType for given anchor pathway element. 
	 * 
	 * @param type the anchor type enum value.
	 * 
	 */
	public void setShape(AnchorType type) {
		if (!this.shapeType.equals(type) && type != null) {
			this.shapeType = type;
			getParent().fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(getParent(), StaticProperty.LINESTYLE));
		}
	}

	/**
	 * Gets the shapeType of given anchor pathway element. 
	 * 
	 * @return shapeType the shape of given anchor pathway element. 
	 */
	public AnchorType getShape() {
		return shapeType;
	}

	/**
	 * Gets the proportional distance of an anchor along the line it belongs to,
	 * between 0 and 1.
	 * 
	 * @return position the position of the anchor.
	 */
	public double getPosition() {
		return getCoordinate(0);
	}

	/**
	 * Sets the proportional distance of an anchor along the line it belongs to,
	 * between 0 and 1.
	 * 
	 * @param position the position of the anchor.
	 */
	public void setPosition(double position) {
		if (position != getPosition()) {
			moveBy(position - getPosition());
		}
	}

	/**
	 * Moves position of anchor by given value along the line it belongs to. 
	 * 
	 * @param delta the value to move position of anchor. 
	 */
	public void moveBy(double delta) {
		super.moveBy(new double[] { delta });
	}
	
	/**
	 * Returns position of anchor as a Point2d absolute coordinate. 
	 * 
	 * @param p the Point2d.  
	 */
	public Point2D toAbsoluteCoordinate(Point2D p) {
		Point2D l = ((MLine) getParent()).getConnectorShape().fromLineCoordinate(getPosition());
		return new Point2D.Double(p.getX() + l.getX(), p.getY() + l.getY());
	}
	
	/**
	 * Returns position of anchor as a Point2d relative coordinate. 
	 * 
	 * @param p the Point2d.  
	 */
	public Point2D toRelativeCoordinate(Point2D p) {
		Point2D l = ((MLine) getParent()).getConnectorShape().fromLineCoordinate(getPosition());
		return new Point2D.Double(p.getX() - l.getX(), p.getY() - l.getY());
	}
}
