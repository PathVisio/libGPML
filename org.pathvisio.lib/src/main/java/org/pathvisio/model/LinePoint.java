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

import org.pathvisio.debug.Logger;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.GraphLink.LinkableFrom;
import org.pathvisio.model.GraphLink.LinkableTo;
import org.pathvisio.model.type.ArrowHeadType;
import org.pathvisio.props.StaticProperty;

/**
 * This class stores information for a Point pathway element. This class is
 * named LinePoint to avoid name conflict with awt.Point in downstream
 * applications.
 * 
 * @author finterly
 */
public class LinePoint extends GenericPoint implements LinkableFrom {

	private ArrowHeadType arrowHead;
	private double x;
	private double y;
	private LinkableTo elementRef; // optional, the pathway element to which the point refers.
	private double relX; // optional
	private double relY; // optional

	/**
	 * Instantiates a Point pathway element, with reference to another pathway
	 * element.
	 * 
	 * @param arrowHead  the glyph at the ends of lines, intermediate points have
	 *                   arrowhead type "line" by default.
	 * @param xy         the xy coordinate position of the point.
	 * @param elementRef the pathway element to which the point refers.
	 * @param relX       the relative x coordinate.
	 * @param relY       the relative x coordinate.
	 */
	public LinePoint(ArrowHeadType arrowHead, double x, double y, LinkableTo elementRef, double relX, double relY) {
		super();
		this.arrowHead = arrowHead;
		this.x = x;
		this.y = y;
		linkTo(elementRef, relX, relY);
	}

	/**
	 * Instantiates a Point pathway element, with no reference to another pathway
	 * element.
	 * 
	 * @param arrowHead the arrowhead property of the point (line by default).
	 * @param xy        the xy coordinate position of the point.
	 */
	public LinePoint(ArrowHeadType arrowHead, double x, double y) {
		super();
		this.arrowHead = arrowHead;
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the arrowHead property of the point. Arrowhead specifies the glyph at
	 * the ends of graphical lines and interactions. Intermediate points have
	 * arrowhead type LINE (the absence of an arrowhead).
	 * 
	 * @return arrowhead the arrowhead property of the point.
	 */
	public ArrowHeadType getArrowHead() {
		if (arrowHead == null) {
			return ArrowHeadType.UNDIRECTED;
		} else {
			return arrowHead;
		}
	}

	/**
	 * Sets the arrowHead property of the point. Arrowhead specifies the glyph at
	 * the ends of graphical lines and interactions. Intermediate points have
	 * arrowhead type LINE (the absence of an arrowhead).
	 * 
	 * @param arrowHead the arrowhead property of the point.
	 */
	public void setArrowHead(ArrowHeadType arrowHead) {
		if (this.arrowHead != arrowHead && arrowHead != null) {
			this.arrowHead = arrowHead;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ARROWHEADTYPE));
		}
	}

	/**
	 * Returns x coordinate value.
	 * 
	 * @return x the coordinate value for x.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets x coordinate to given value.
	 * 
	 * @param v the coordinate value to set for x.
	 */
	public void setX(double v) {
		if (v < 0)
			Logger.log.trace("Warning: negative x coordinate " + String.valueOf(v));
		x = v;
	}

	/**
	 * Returns y coordinate value.
	 * 
	 * @return y the coordinate value for y.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets y coordinate to given value.
	 * 
	 * @param v the coordinate value to set for y.
	 */
	public void setY(double v) {
		if (v < 0)
			Logger.log.trace("Warning: negative y coordinate " + String.valueOf(v));
		y = v;
	}

	// TODO
	public Point2D toPoint2D() {
		return new Point2D.Double(getX(), getY());
	}

	/**
	 * Returns the pathway element to which this point refers to. In GPML, this is
	 * elementRef which refers to the elementId of a pathway element.
	 * 
	 * @return elementRef the pathway element to which this point refers.
	 */
	public LinkableTo getElementRef() {
		return elementRef;
	}

	/**
	 * Sets the pathway element to which the point refers to. In GPML, this is
	 * elementRef which refers to the elementId of a pathway element. If a
	 * pathwayModel is set, this will automatically deregister the previously held
	 * elementRef and register the new elementRef as necessary
	 *
	 * @param v reference to set.
	 */
	public void setElementRef(LinkableTo v) {
		if (elementRef != v) {
			if (getPathwayModel() != null) {
				if (elementRef != null) {
					getPathwayModel().removeElementRef(elementRef, this);
				}
				if (v != null) {
					getPathwayModel().addElementRef(v, this);
				}
			}
			elementRef = v;
			// TODO????
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ELEMENTREF));
		}
	}

	/**
	 * Returns the relative x coordinate. When the given point is linked to a
	 * pathway element, relX and relY are the relative coordinates on the element,
	 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
	 * of the object.
	 * 
	 * @return relX the relative x coordinate.
	 */
	public double getRelX() {
		return relX;
	}

	/**
	 * Sets the relative x coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param v the relative x coordinate.
	 * @throws IllegalArgumentException if relX is not between -1.0 and 1.0. t
	 */
	public void setRelX(double v) {
		if (Math.abs(v) > 1.0) {
			Logger.log.trace("Warning: relX absolute value of " + String.valueOf(v) + " greater than 1");
		}
		if (relX != v) {
			relX = v;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * Returns the relative y coordinate. When the given point is linked to a
	 * pathway element, relX and relY are the relative coordinates on the element,
	 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
	 * of the object.
	 * 
	 * @return relY the relative y coordinate.
	 */
	public double getRelY() {
		return relY;
	}

	/**
	 * Sets the relative y coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param v the relative y coordinate.
	 */
	public void setRelY(double v) {
		if (Math.abs(v) > 1.0) {
			Logger.log.trace("Warning: relY absolute value of " + String.valueOf(v) + " greater than 1");
		}
		if (relY != v) {
			relY = v;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * Link to an object. Current absolute coordinates will be converted to relative
	 * coordinates based on the object to link to. TODO
	 * 
	 * @param pathwayElement the linkableTo pathway element to link to.
	 */
	public void linkTo(LinkableTo pathwayElement) {
//		Point2D rel = pathwayElement.toRelativeCoordinate(toPoint2D());
		linkTo(pathwayElement, relX, relY);
	}

	/**
	 * Link to an object using the given relative coordinates TODO
	 */
	public void linkTo(LinkableTo pathwayElement, double relX, double relY) {
		setElementRef(pathwayElement);
		setRelativePosition(relX, relY);
	}

	/**
	 * note that this may be called any number of times when this point is already
	 * unlinked
	 */
	public void unlink() {
		if (elementRef != null) {
			if (getPathwayModel() != null) {
//				Point2D abs = getAbsolute();
//				moveTo(abs.getX(), abs.getY());
			}
			// relativeSet = false;
			setElementRef(null);
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this)); // TODO this or
																								// lineElement????
		}
	}

	// TODO
	public void setRelativePosition(double rx, double ry) {
		moveTo(rx, ry);
//		relativeSet = true; TODO 
	}

//	private boolean relativeSet; TODO 

//	/**
//	 * Helper method for converting older GPML files without relative coordinates. TODO
//	 * 
//	 * @return true if {@link #setRelativePosition(double, double)} was called to
//	 *         set the relative coordinates, false if not.
//	 */
//	protected boolean relativeSet() {
//		return relativeSet;
//	}

	// TODO
	public void moveBy(double deltaX, double deltaY) {
		double x = getX() + deltaX;
		double y = getY() + deltaY;
		setX(x);
		setY(y);
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
	}

	// TODO
	public void moveTo(double x, double y) {
		setX(x);
		setY(y);
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
	}

	// TODO weird
	public void moveTo(LinePoint linePoint) {
//		xy = linePoint.getXY(); TODO 
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
	}

	public void refeeChanged() {
		// called whenever the object being referred to has changed.
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this)); // TODO this or lineElement?
	}

	/**
	 * Terminates this point. The pathway model and line element, if any, are unset
	 * from this point.
	 */
	@Override
	public void terminate() {
		unsetLineElement();
		unsetPathwayModel();
	}

}
