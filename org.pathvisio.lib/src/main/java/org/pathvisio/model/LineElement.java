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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.pathvisio.debug.Logger;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.GraphLink.LinkableFrom;
import org.pathvisio.model.GraphLink.LinkableTo;
import org.pathvisio.model.connector.ConnectorRestrictions;
import org.pathvisio.model.connector.ConnectorShape;
import org.pathvisio.model.connector.ConnectorShape.WayPoint;
import org.pathvisio.model.connector.ConnectorShapeFactory;
import org.pathvisio.model.connector.ElbowConnectorShape;
import org.pathvisio.model.type.AnchorShapeType;
import org.pathvisio.model.type.ArrowHeadType;
import org.pathvisio.model.type.ConnectorType;
import org.pathvisio.model.type.LineStyleType;
import org.pathvisio.props.StaticProperty;
import org.pathvisio.util.Utils;

/**
 * This abstract class stores information for a Line pathway element, e.g.
 * {@link GraphicalLine} or {@link Interaction}.
 * 
 * @author finterly
 */
public abstract class LineElement extends PathwayElement implements Groupable, ConnectorRestrictions {

	private Group groupRef; // optional, the parent group to which a pathway element belongs.
	private List<LinePoint> linePoints; // minimum 2
	private List<Anchor> anchors;

	// line style properties
	private Color lineColor = Color.decode("#000000"); // black
	private LineStyleType lineStyle = LineStyleType.SOLID; // solid, dashed, or double
	private double lineWidth = 1.0; // 1.0
	private ConnectorType connectorType = ConnectorType.STRAIGHT; // straight, elbow, curved...
	private int zOrder; // optional

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates a line pathway element.
	 * 
	 * NB: Property groupRef is to be set by {@link #setGroupRefTo(Group)}. In GPML,
	 * groupRef refers to the elementId (formerly groupId) of the parent gpml:Group.
	 * Note, a group can also belong in another group. Graphics properties have
	 * default values and can be set after a line pathway element is already
	 * instantiated.
	 */
	public LineElement() {
		super();
		// instantiated points list and set
		linePoints = new ArrayList<LinePoint>();
		setLinePoints(new ArrayList<LinePoint>(Arrays.asList(new LinePoint(ArrowHeadType.UNDIRECTED, 0, 0),
				new LinePoint(ArrowHeadType.UNDIRECTED, 0, 0))));
		this.anchors = new ArrayList<Anchor>();
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the parent group of this pathway element. In GPML, groupRef refers to
	 * the elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of this pathway element.
	 */
	@Override
	public Group getGroupRef() {
		return groupRef;
	}

	/**
	 * Checks whether this pathway element belongs to a group.
	 *
	 * @return true if and only if the group of this pathway element is effective.
	 */
	@Override
	public boolean hasGroupRef() {
		return getGroupRef() != null;
	}

	/**
	 * Verifies if given parent group is new and valid. Sets the parent group of
	 * this pathway element. Adds this pathway element to the the pathwayElements
	 * list of the new parent group. If there is an old parent group, this pathway
	 * element is removed from its pathwayElements list.
	 * 
	 * @param v the new parent group to set.
	 */
	@Override
	public void setGroupRefTo(Group v) {
		if (v == null)
			throw new IllegalArgumentException("Invalid group.");
		if (v.getPathwayModel() != getPathwayModel()) {
			throw new IllegalArgumentException(
					getClass().getSimpleName() + " cannot be added to a group of a different pathway model.");
		}
		if (groupRef != v) {
			unsetGroupRef(); // first unsets if necessary
			setGroupRef(v);
			if (!v.hasPathwayElement(this))
				v.addPathwayElement(this);
		}
	}

	/**
	 * Sets the parent group for this pathway element.
	 * 
	 * @param v the given group to set.
	 */
	private void setGroupRef(Group v) {
		// TODO
		groupRef = v;
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
	}

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	@Override
	public void unsetGroupRef() {
		if (hasGroupRef()) {
			Group groupRef = getGroupRef();
			setGroupRef(null);
			if (groupRef.hasPathwayElement(this))
				groupRef.removePathwayElement(this);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
		}
	}

	// ================================================================================
	// LinePoint Methods
	// ================================================================================
	/**
	 * Get the points for this line.
	 * 
	 * @return points the list of points, an empty list if no anchors are defined.
	 */
	public List<LinePoint> getLinePoints() {
		return linePoints;
	}

	/**
	 * Sets linePoints to the given list of LinePoints. Removes old points
	 * {@link removeLinePoints}, if any, then adds new points {@link addLinePoints}.
	 * 
	 * @param points the list of points to set.
	 */
	public void setLinePoints(List<LinePoint> points) {
		if (points != null) {
			if (points.size() < 2) {
				throw new IllegalArgumentException("Points array should at least have two elements for "
						+ getClass().getSimpleName() + " " + getElementId());
			}
			removeLinePoints(); // remove points before setting new points
			addLinePoints(points);
		}
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
	}

	/**
	 * Adds all given points to the linePoints list. Adds each point to the pathway
	 * model {@link PathwayModel#addPathwayObject} if applicable.
	 * 
	 * @param points the points.
	 */
	private void addLinePoints(List<LinePoint> points) {
		for (LinePoint point : points) {
			if (point == null) {
				throw new IllegalArgumentException("Cannot add invalid point to line " + getElementId());
			}
			if (point.getLineElement() != this) {
				throw new IllegalArgumentException("Cannot add point to line other than its parent line");
			}
			if (pathwayModel != null) {
				pathwayModel.addPathwayObject(point);
			}
			linePoints.add(point);
		}

	}

	/**
	 * Removes all points from the linePoints list.
	 */
	private void removeLinePoints() {
		for (int i = linePoints.size() - 1; i >= 0; i--) {
			LinePoint point = linePoints.get(i);
			if (point.pathwayModel != null)
				pathwayModel.removePathwayObject(point);
		}
		linePoints.clear();
	}

	// ================================================================================
	// Anchor Methods
	// ================================================================================
	/**
	 * Get the anchors for this line.
	 * 
	 * @return anchors the list of anchors, an empty list if no anchors are defined.
	 */
	public List<Anchor> getAnchors() {
		return anchors;
	}

	/**
	 * Checks whether anchors has the given anchor.
	 * 
	 * @param anchor the anchor to look for.
	 * @return true if has anchor, false otherwise.
	 */
	public boolean hasAnchor(Anchor anchor) {
		return anchors.contains(anchor);
	}

	/**
	 * Adds given anchor to anchors list. Sets lineElement for the given anchor.
	 * 
	 * @param anchor the anchor to be added.
	 */
	public void addAnchor(Anchor anchor) {
		if (anchor != null && !hasAnchor(anchor)) {
			assert (anchor.getLineElement() == this);
			// add anchor to same pathway model as line if applicable TODO
			if (getPathwayModel() != null)
				getPathwayModel().addPathwayObject(anchor);
			anchors.add(anchor);
			// No anchor property, use LINESTYLE as dummy property to force redraw on line
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));

		}
	}

	/**
	 * Adds a new anchor to this line at the given position with anchorShapeType
	 * property. Calls {@link #addAnchor(Anchor anchor)}.
	 * 
	 * @param position        the relative position on the line, between 0 (start)
	 *                        to 1 (end).
	 * @param anchorShapeType the shape type of the anchor.
	 */
	public Anchor addAnchor(double position, AnchorShapeType anchorShapeType) {
		Anchor anchor = new Anchor(position, anchorShapeType);
		addAnchor(anchor);
		return anchor;
	}

	/**
	 * Creates and adds a new anchor to this line at the given position with
	 * anchorShapeType property. Anchor elementId is set immediately after creation.
	 * This method is used when reading gpml.Calls
	 * {@link #addAnchor(Anchor anchor)}.
	 * 
	 * @param elementId       the elementId to set for created anchor.
	 * @param position        the relative position on the line, between 0 (start)
	 *                        to 1 (end).
	 * @param anchorShapeType the shape type of the anchor.
	 */
	public Anchor addAnchor(String elementId, double position, AnchorShapeType anchorShapeType) {
		Anchor anchor = new Anchor(position, anchorShapeType);
		anchor.setElementId(elementId);
		addAnchor(anchor);
		return anchor;
	}

	/**
	 * Removes given anchor from the anchors list. Anchor ceases to exist and is
	 * terminated.
	 * 
	 * @param anchor the anchor to be removed.
	 */
	public void removeAnchor(Anchor anchor) {
		assert (anchor != null && hasAnchor(anchor));
		if (getPathwayModel() != null)
			getPathwayModel().removePathwayObject(anchor);
		anchors.remove(anchor);
		// No anchor property, use LINESTYLE as dummy property to force redraw on line
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
	}

	/**
	 * Removes all anchors from the anchors list.
	 */
	private void removeAnchors() {
		for (int i = anchors.size() - 1; i >= 0; i--) {
			if (getPathwayModel() != null)
				getPathwayModel().removePathwayObject(anchors.get(i));
		}
		anchors.clear();
	}

	// ================================================================================
	// Line Style Graphics Properties
	// ================================================================================
	/**
	 * Returns the color of a line.
	 * 
	 * @return lineColor the color of a line.
	 */
	public Color getLineColor() {
		if (lineColor == null) {
			return Color.decode("#000000"); // black
		} else {
			return lineColor;
		}
	}

	/**
	 * Sets the color of a line.
	 * 
	 * @param v the color of a line.
	 * @throws IllegalArgumentException if color null.
	 */
	public void setLineColor(Color v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (lineColor != v) {
			lineColor = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINECOLOR));
		}
	}

	/**
	 * Returns the visual appearance of a line, e.g. Solid or Broken.
	 * 
	 * @return lineStyle the style of a line.
	 */
	public LineStyleType getLineStyle() {
		if (lineStyle == null) {
			return LineStyleType.SOLID;
		} else {
			return lineStyle;
		}
	}

	/**
	 * Sets the visual appearance of a line, e.g. Solid or Broken.
	 * 
	 * @param v the line style to set.
	 * @throws IllegalArgumentException if lineStyle null.
	 */
	public void setLineStyle(LineStyleType v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (lineStyle != v) {
			lineStyle = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
		}
	}

	/**
	 * Returns the pixel value for the width of a line.
	 * 
	 * @return lineWidth the width of a line.
	 */
	public double getLineWidth() {
		if (lineWidth < 0) {
			return 1.0;
		} else {
			return lineWidth;
		}
	}

	/**
	 * Sets the pixel value for the width of a line.
	 * 
	 * @param v the width of a line.
	 * @throws IllegalArgumentException if lineWidth is a negative value.
	 */
	public void setLineWidth(double v) {
		if (v < 0) {
			throw new IllegalArgumentException();
		}
		if (lineWidth != v) {
			lineWidth = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINEWIDTH));
		}
	}

	/**
	 * Returns the value of the connectorType property. Specifies a set of rules to
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
	 * @param v the layout of a line.
	 * @throws IllegalArgumentException if ConnectorType null.
	 */
	public void setConnectorType(ConnectorType v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (connectorType != v) {
			connectorType = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.CONNECTORTYPE));
		}
	}

	/**
	 * Returns the z-order of this pathway element.
	 * 
	 * @return zOrder the order of this pathway element.
	 */
	public int getZOrder() {
		return zOrder;
	}

	/**
	 * Sets the z-order of this pathway element.
	 * 
	 * @param v the order of this pathway element.
	 */
	public void setZOrder(int v) {
		if (zOrder != v) {
			zOrder = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ZORDER));
		}
	}

	// ================================================================================
	// Start and End LinePoint Methods
	// ================================================================================
	/**
	 * Returns the start (first) point of points list. TODO necessary method?
	 * 
	 * @return the first point of points list.
	 */
	public LinePoint getStartLinePoint() {
		return linePoints.get(0);
	}

	/**
	 * Sets start linePoint coordinates to the coordinates of the given linePoint.
	 * 
	 * @param linePoint
	 */
	public void setStartLinePoint(LinePoint linePoint) {
		getStartLinePoint().moveTo(linePoint);
	}

	/**
	 * Returns the end (last) point of points list. TODO necessary method?
	 * 
	 * @return the last point of points list.
	 */
	public LinePoint getEndLinePoint() {
		return linePoints.get(linePoints.size() - 1);
	}

	// TODO weird
	public void setEndLinePoint(LinePoint linePoint) {
		getEndLinePoint().moveTo(linePoint);
	}

	public double getStartLinePointX() {
		return getStartLinePoint().getX();
	}

	public void setStartLinePointX(double v) {
		getStartLinePoint().setX(v);
	}

	public double getStartLinePointY() {
		return getStartLinePoint().getY();
	}

	public void setStartLinePointY(double v) {
		getStartLinePoint().setY(v);
	}

	public double getEndLinePointX() {
		return getEndLinePoint().getX();
	}

	public void setEndLinePointX(double v) {
		getEndLinePoint().setX(v);
	}

	public double getEndLinePointY() {
		return getEndLinePoint().getY();
	}

	public void setEndLinePointY(double v) {
		getEndLinePoint().setY(v);
	}

	// TODO are these methods necessary?
	public ArrowHeadType getStartLineType() {
		ArrowHeadType startLineType = getStartLinePoint().getArrowHead();
		return startLineType == null ? ArrowHeadType.UNDIRECTED : startLineType;
	}

	public ArrowHeadType getEndLineType() {
		ArrowHeadType endLineType = getEndLinePoint().getArrowHead();
		return endLineType == null ? ArrowHeadType.UNDIRECTED : endLineType;
	}

	public void setStartLineType(ArrowHeadType value) {
		getStartLinePoint().setArrowHead(value);
	}

	public void setEndLineType(ArrowHeadType value) {
		getEndLinePoint().setArrowHead(value);
	}

	public LinkableTo getStartElementRef() {
		return getStartLinePoint().getElementRef();
	}

	public void setStartElementRef(LinkableTo elementRef) {
		getStartLinePoint().setElementRef(elementRef);
	}

	public LinkableTo getEndElementRef() {
		return getEndLinePoint().getElementRef();
	}

	public void setEndElementRef(LinkableTo elementRef) {
		getEndLinePoint().setElementRef(elementRef);

	}

	// ================================================================================
	// Point2D Methods
	// ================================================================================
	/** converts start point from MPoint to Point2D */
	public Point2D getStartPoint2D() {
		return getStartLinePoint().toPoint2D();
	}

	/** converts end point from MPoint to Point2D */
	public Point2D getEndPoint2D() {
		return getEndLinePoint().toPoint2D();
	}

	/** converts all points from MPoint to Point2D */
	public List<Point2D> getPoint2Ds() {
		List<Point2D> pts = new ArrayList<Point2D>();
		for (LinePoint p : linePoints) {
			pts.add(p.toPoint2D());
		}
		return pts;
	}

	// ================================================================================
	// Bounds Methods
	// ================================================================================
	/**
	 * Returns the rectangular bounds of this line pathway elements. This method
	 * simply calls {@link #getBounds()} because lines do not have property
	 * rotation.
	 * 
	 * @return the rectangular bounds for this line pathway element.
	 */
	@Override
	public Rectangle2D getRotatedBounds() {
		return getBounds();
	}

	/**
	 * Returns the rectangular bounds for this line pathway element. The bounds for
	 * a line is calculated from its ends points (first and last).
	 * 
	 * @return the rectangular bounds for this line pathway element.
	 */
	@Override
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(getLeft(), getTop(), getWidth(), getHeight());
	}

	/**
	 * Returns the center x coordinate of the bounding box around (start, end) this
	 * line pathway element.
	 */
	public double getCenterX() {
		double start = getStartLinePointX();
		double end = getEndLinePointX();
		return start + (end - start) / 2;
	}

	/**
	 * Returns the center y coordinate of the bounding box around (start, end) this
	 * line pathway element.
	 */
	public double getCenterY() {
		double start = getStartLinePointY();
		double end = getEndLinePointY();
		return start + (end - start) / 2;
	}

	/**
	 * Returns the width of the bounding box around (start, end) this line pathway
	 * element.
	 */
	public double getWidth() {
		double start = getStartLinePointX();
		double end = getEndLinePointX();
		return Math.abs(start - end);
	}

	/**
	 * Returns the height of the bounding box around (start, end) this line pathway
	 * element.
	 */
	public double getHeight() {
		double start = getStartLinePointY();
		double end = getEndLinePointY();
		return Math.abs(start - end);
	}

	/**
	 * Returns the left x coordinate of the bounding box around (start, end) this
	 * line pathway element.
	 */
	public double getLeft() {
		double start = getStartLinePointX();
		double end = getEndLinePointX();
		return Math.min(start, end);
	}

	/**
	 * Returns the top y coordinate of the bounding box around (start, end) this
	 * line pathway element.
	 */
	public double getTop() {
		double start = getStartLinePointY();
		double end = getEndLinePointY();
		return Math.min(start, end);
	}

	/**
	 * Sets the x position of the center of the line. This makes the line move as a
	 * whole
	 */
	public void setCenterX(double v) {
		double dx = v - getCenterX();
		setStartLinePointX(getStartLinePointX() + dx);
		setEndLinePointX(getEndLinePointX() + dx);
	}

	/**
	 * Sets the y position of the center of the line. This makes the line move as a
	 * whole.
	 */
	public void setCenterY(double v) {
		double dy = v - getCenterY();
		setStartLinePointY(getStartLinePointY() + dy);
		setEndLinePointY(getEndLinePointY() + dy);
	}

	/**
	 * Sets the position of the top side of the rectangular bounds of the line
	 */
	public void setTop(double v) {
		if (getDirectionY() > 0) {
			setStartLinePointY(v);
		} else {
			setEndLinePointY(v);
		}
	}

	/**
	 * Sets the position of the left side of the rectangular bounds of the line
	 */
	public void setLeft(double v) {
		if (getDirectionX() > 0) {
			setStartLinePointX(v);
		} else {
			setEndLinePointX(v);
		}
	}

	/** returns the sign of end.x - start.x */
	private int getDirectionX() {
		return (int) Math.signum(getEndLinePointX() - getStartLinePointX());
	}

	/** returns the sign of end.y - start.y */
	private int getDirectionY() {
		return (int) Math.signum(getEndLinePointY() - getStartLinePointY());
	}

	// ================================================================================
	// Connector Methods
	// ================================================================================
	ConnectorShape shape;

	/**
	 * the Connector Shape for this line - the connector shape can calculate a Shape
	 * based on the connector type (straight, elbow or curved) and possibly way
	 * points
	 */
	public ConnectorShape getConnectorShape() {
		String type = getConnectorType().getName();

		// Recreate the ConnectorShape when it's null or when the type
		// doesn't match the implementing class
		if (shape == null || !shape.getClass().equals(ConnectorShapeFactory.getImplementingClass(type))) {
			shape = ConnectorShapeFactory.createConnectorShape(getConnectorType().getName());
			shape.recalculateShape(this);
		}
		return shape;
	}

	/**
	 * Calculate on which side of a PathwayElement (SIDE_NORTH, SIDE_EAST,
	 * SIDE_SOUTH or SIDE_WEST) the start of this line is connected to.
	 *
	 * If the start is not connected to anything, returns SIDE_WEST
	 */
	public int getStartSide() {
		int side = SIDE_WEST;

		LinkableTo e = getStartLinePoint().getElementRef();
		if (e != null) {
			if (e instanceof PathwayElement) {
				side = getSide(getStartLinePoint().getRelX(), getStartLinePoint().getRelY());
			} else if (e instanceof Anchor) {
				side = getAttachedLineDirection((Anchor) e);
			}
		}
		return side;
	}

	/**
	 * Calculate on which side of a PathwayElement (SIDE_NORTH, SIDE_EAST,
	 * SIDE_SOUTH or SIDE_WEST) the end of this line is connected to.
	 *
	 * If the end is not connected to anything, returns SIDE_EAST
	 */
	public int getEndSide() {
		int side = SIDE_EAST;

		LinkableTo e = getEndLinePoint().getElementRef();
		if (e != null) {
			if (e instanceof PathwayElement) {
				side = getSide(getEndLinePoint().getRelX(), getEndLinePoint().getRelY());
			} else if (e instanceof Anchor) {
				side = getAttachedLineDirection((Anchor) e);
			}
		}
		return side;
	}

	private int getAttachedLineDirection(Anchor anchor) {
		int side;
		double pos = anchor.getPosition();
		LineElement attLine = anchor.getLineElement();
		if (attLine.getConnectorShape() instanceof ElbowConnectorShape) {
			ConnectorShape.Segment attSeg = findAnchorSegment(attLine, pos);
			int orientationX = Utils.getDirectionX(attSeg.getMStart(), attSeg.getMEnd());
			int orientationY = Utils.getDirectionY(attSeg.getMStart(), attSeg.getMEnd());
			side = getSide(orientationY, orientationX);
		} else {
			side = getOppositeSide(
					getSide(getEndLinePointX(), getEndLinePointY(), getStartLinePointX(), getStartLinePointY()));
			if (attLine.almostPerfectAlignment(side)) {
				side = getClockwisePerpendicularSide(side);
			}
		}
		return side;
	}

	private ConnectorShape.Segment findAnchorSegment(LineElement attLine, double pos) {
		ConnectorShape.Segment[] segments = attLine.getConnectorShape().getSegments();
		Double totLength = 0.0;
		ConnectorShape.Segment attSeg = null;
		for (ConnectorShape.Segment segment : segments) {
			totLength = totLength + segment.getMLength();
		}
		Double currPos;
		Double segSum = 0.0;
		for (ConnectorShape.Segment segment : segments) {
			segSum = segSum + segment.getMLength();
			currPos = segSum / totLength;
			attSeg = segment;
			if (currPos > pos) {
				break;
			}
		}
		return attSeg;
	}

	/**
	 * Check if either the line segment has less than or equal to 10 degree
	 * alignment with the side passed
	 * 
	 * @param side
	 * @return true if less or equal to 10 degree alignment else false
	 */
	private boolean almostPerfectAlignment(int side) {
		int MAXOFFSET = 30; /*
							 * cutoff point where we see a shallow angle still as either horizontal or
							 * vertical
							 */
		// X axis
		if ((side == SIDE_EAST) || (side == SIDE_WEST)) {
			double angleDegree = (180 / Math.PI)
					* Math.atan2(Math.abs(getStartPoint2D().getY() - getEndPoint2D().getY()),
							Math.abs(getStartPoint2D().getX() - getEndPoint2D().getX()));
			if (angleDegree <= MAXOFFSET)
				return true;
		} else {// north south or Y axis
			double angleDegree = (180 / Math.PI)
					* Math.atan2(Math.abs(getStartPoint2D().getX() - getEndPoint2D().getX()),
							Math.abs(getStartPoint2D().getY() - getEndPoint2D().getY()));
			if (angleDegree <= MAXOFFSET)
				return true;
		}
		return false;
	}

	/**
	 * Returns the Perpendicular for a SIDE_* constant (e.g. SIDE_EAST, SIDE_WEST)
	 */
	private int getClockwisePerpendicularSide(int side) {
		switch (side) {
		case SIDE_EAST:
			return SIDE_SOUTH;
		case SIDE_WEST:
			return SIDE_NORTH;
		case SIDE_NORTH:
			return SIDE_EAST;
		case SIDE_SOUTH:
			return SIDE_WEST;
		}
		return -1;
	}

	public void adjustWayPointPreferences(WayPoint[] waypoints) {
		List<LinePoint> mpoints = linePoints;
		for (int i = 0; i < waypoints.length; i++) {
			WayPoint wp = waypoints[i];
			LinePoint mp = mpoints.get(i + 1);
			if (mp.getX() != wp.getX() || mp.getY() != wp.getY()) {
				dontFireEvents(1);
				mp.moveTo(wp.getX(), wp.getY());
			}
		}
	}

	public void resetWayPointPreferences() {
		List<LinePoint> mps = linePoints;
		while (mps.size() > 2) {
			mps.remove(mps.size() - 2);
		}
	}

	/**
	 * Get the preferred waypoints, to which the connector must draw it's path. The
	 * waypoints returned by this method are preferences and the connector shape may
	 * decide not to use them if they are invalid.
	 */
	public WayPoint[] getWayPointPreferences() {
		List<LinePoint> pts = linePoints;
		WayPoint[] wps = new WayPoint[pts.size() - 2];
		for (int i = 0; i < wps.length; i++) {
			wps[i] = new WayPoint(pts.get(i + 1).toPoint2D());
		}
		return wps;
	}

	/**
	 * Get the side of the given pathway element to which the x and y coordinates
	 * connect
	 * 
	 * @param x  the x coordinate
	 * @param y  the y coordinate
	 * @param cx
	 * @param cy // e The element to find the side of
	 * @return One of the SIDE_* constants
	 */
	private static int getSide(double x, double y, double cx, double cy) {
		return getSide(x - cx, y - cy);
	}

	private static int getSide(double relX, double relY) {
		int direction = 0;
		if (Math.abs(relX) > Math.abs(relY)) {
			if (relX > 0) {
				direction = SIDE_EAST;
			} else {
				direction = SIDE_WEST;
			}
		} else {
			if (relY > 0) {
				direction = SIDE_SOUTH;
			} else {
				direction = SIDE_NORTH;
			}
		}
		return direction;
	}

	/**
	 * Returns the opposite for a SIDE_* constant (e.g. SIDE_EAST, SIDE_WEST)
	 */
	private int getOppositeSide(int side) {
		switch (side) {
		case SIDE_EAST:
			return SIDE_WEST;
		case SIDE_WEST:
			return SIDE_EAST;
		case SIDE_NORTH:
			return SIDE_SOUTH;
		case SIDE_SOUTH:
			return SIDE_NORTH;
		}
		return -1;
	}

	/**
	 * Check if the connector may cross this point Optionally, returns a shape that
	 * defines the boundaries of the area around this point that the connector may
	 * not cross. This method can be used for advanced connectors that route along
	 * other objects on the drawing
	 * 
	 * @return A shape that defines the boundaries of the area around this point
	 *         that the connector may not cross. Returning null is allowed for
	 *         implementing classes.
	 */
	public Rectangle2D mayCross(Point2D point) { // TODO was Shape before...
		PathwayModel pathwayModel = getPathwayModel();
		Rectangle2D rect = null;
		if (pathwayModel != null) {
			for (PathwayObject e : pathwayModel.getPathwayObjects()) { // TODO Object or Elements?
				if (e.getClass() == Shape.class || e.getClass() == DataNode.class || e.getClass() == Label.class) {
					Rectangle2D b = ((ShapedElement) e).getBounds(); // TODO okay???
					if (b.contains(point)) {
						if (rect == null)
							rect = b;
						else
							rect.add(b);
					}
				}
			}
		}
		return rect;
	}

	// ================================================================================
	// Inherited Methods
	// ================================================================================
	/**
	 * Sets the pathway model for this pathway element. NB: Only set when a pathway
	 * model adds this pathway element.
	 * 
	 * NB: This method is not used directly. It is called by
	 * {@link PathwayModel#addPathwayObject}.
	 * 
	 * @param pathwayModel the parent pathway model.
	 */
	@Override
	protected void setPathwayModelTo(PathwayModel pathwayModel) throws IllegalArgumentException, IllegalStateException {
		super.setPathwayModelTo(pathwayModel);
		// if line element has points and anchors, also add them to pathway model TODO
		for (LinePoint point : linePoints) {
			pathwayModel.addPathwayObject(point);
		}
		for (Anchor anchor : anchors) {
			pathwayModel.addPathwayObject(anchor);
		}
	}

	/**
	 * Terminates this LineElement. The pathway model, if any, is unset from this
	 * anchor.Links to all annotationRefs, citationRefs, and evidenceRefs are
	 * removed from this data node.
	 */
	@Override
	protected void terminate() {
		removeLinePoints();
		removeAnchors();
		super.terminate();
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Note: doesn't change parent, only fields
	 *
	 * Used by UndoAction.
	 *
	 * @param src
	 */
	public void copyValuesFrom(LineElement src) { // TODO
		super.copyValuesFrom(src);
		groupRef = src.groupRef;
		List<LinePoint> points = new ArrayList<LinePoint>();
		for (LinePoint pt : src.linePoints) {
			LinePoint result = new LinePoint(null, 0, 0); // TODO
			result.copyValuesFrom(pt);
			points.add(result);
		}
		setLinePoints(points);
//		for (LinePoint p : src.linePoints) {
//			linePoints.add(p);
//		}
		anchors = new ArrayList<Anchor>();
		for (Anchor a : src.anchors) {
			Anchor result = new Anchor(0, null); // TODO
			result.copyValuesFrom(a);
			addAnchor(result);
		}
//		for (Anchor a : src.anchors) {
//		anchors.add(a);
//	}
		lineColor = src.lineColor;
		lineWidth = src.lineWidth;
		connectorType = src.connectorType;
		zOrder = src.zOrder;
		fireObjectModifiedEvent(PathwayElementEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public abstract LineElement copy();

// ================================================================================
// GenericPoint Class
// ================================================================================
	/**
	 * Abstract class of generic point, extended by {@link LinePoint} and
	 * {@link Anchor}.
	 * 
	 * @author unknown, finterly
	 */
	public abstract class GenericPoint extends PathwayObject implements Cloneable { // TODO

		// ================================================================================
		// Constructors
		// ================================================================================
		/**
		 * Constructor for a generic point.
		 */
		public GenericPoint() {
			super();
		}

		// ================================================================================
		// Clone Methods
		// ================================================================================
		public Object clone() throws CloneNotSupportedException {
			GenericPoint p = (GenericPoint) super.clone(); // TODO
			if (getElementId() != null)
				p.setElementId(getElementId()); // TODO????
			return p;
		}

		// ================================================================================
		// Accessors
		// ================================================================================
		/**
		 * Returns the parent interaction or graphicalLine for this point.
		 * 
		 * @return lineElement the parent line element of this point.
		 */
		public LineElement getLineElement() {
			return LineElement.this;
		}

		// ================================================================================
		// Inherited Methods
		// ================================================================================

		/**
		 * Sets the pathway model for this line point or anchor. The generic point must
		 * be added to the same pathway model as its parent line.
		 * 
		 * NB: This method is not used directly. It is called by
		 * {@link PathwayModel#addPathwayObject}.
		 * 
		 * @param pathwayModel the parent pathway model.
		 */
		@Override
		protected void setPathwayModelTo(PathwayModel pathwayModel)
				throws IllegalArgumentException, IllegalStateException {
			if (pathwayModel == null) {
				throw new IllegalArgumentException("Invalid pathway model.");
			}
			if (pathwayModel == getLineElement().getPathwayModel()) {
				setPathwayModel(pathwayModel);
			} else {
				throw new IllegalArgumentException(this.getClass().getSimpleName()
						+ " must be added to the same pathway model as its parent line.");
			}
		}

	}

// ================================================================================
// LinePoint Class
// ================================================================================
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

		// ================================================================================
		// Constructors
		// ================================================================================

		/**
		 * Instantiates a Point pathway element, with no reference to another pathway
		 * element. Method {@link #linkTo} to set elementRef, relX, relY.
		 * 
		 * @param arrowHead the arrowhead property of the point (line by default).
		 * @param x         the x coordinate position of the point.
		 * @param y         the y coordinate position of the point.
		 */
		public LinePoint(ArrowHeadType arrowHead, double x, double y) {
			super();
			this.arrowHead = arrowHead;
			setX(x);
			setY(y);
		}

		// ================================================================================
		// Accessors
		// ================================================================================
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
				fireObjectModifiedEvent(
						PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ARROWHEADTYPE));
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
			if (v < 0) {
				Logger.log.trace("Warning: negative x coordinate " + String.valueOf(v));
			}
			if (x != v) {
				x = v;
				fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(LineElement.this));
			}
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
			if (v < 0) {
				Logger.log.trace("Warning: negative y coordinate " + String.valueOf(v));
			}
			if (y != v) {
				y = v;
				fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(LineElement.this));
			}
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
		private void setElementRef(LinkableTo v) {
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
		private void setRelX(double v) {
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
		private void setRelY(double v) {
			if (Math.abs(v) > 1.0) {
				Logger.log.trace("Warning: relY absolute value of " + String.valueOf(v) + " greater than 1");
			}
			if (relY != v) {
				relY = v;
				fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
			}
		}

		// ================================================================================
		// Point Link Methods
		// ================================================================================

		private boolean relativeSet; // TODO

//		/**
//		 * Helper method for converting older GPML files without relative coordinates.
//		 * 
//		 * @return true if {@link #setRelativePosition(double, double)} was called to
//		 *         set the relative coordinates, false if not.
//		 */
//		protected boolean relativeSet() {
//			return relativeSet;
//		}

		private Point2D getAbsolute() {
			return elementRef.toAbsoluteCoordinate(new Point2D.Double(getRelX(), getRelY()));
		}

		// TODO
		public Point2D toPoint2D() {
			return new Point2D.Double(getX(), getY());
		}

		// TODO
		public Point2D toAbsoluteCoordinate(Point2D p) {
			return new Point2D.Double(p.getX() + getX(), p.getY() + getY());
		}

		// TODO
		public Point2D toRelativeCoordinate(Point2D p) {
			return new Point2D.Double(p.getX() - getX(), p.getY() - getY());
		}

		/**
		 * Link to an object. Current absolute coordinates will be converted to relative
		 * coordinates based on the object to link to. TODO
		 * 
		 * @param pathwayElement the linkableTo pathway element to link to.
		 */
		public void linkTo(LinkableTo pathwayElement) {
			Point2D rel = pathwayElement.toRelativeCoordinate(toPoint2D());
			linkTo(pathwayElement, rel.getX(), rel.getY());
		}

		/**
		 * Link to an object using the given relative coordinates TODO
		 */
		public void linkTo(LinkableTo pathwayElement, double relX, double relY) {
			setElementRef(pathwayElement);
			setRelXY(relX, relY);
		}

		/**
		 * note that this may be called any number of times when this point is already
		 * unlinked
		 */
		public void unlink() {
			if (elementRef != null) {
//				if (getPathwayModel() != null) { TODO 
//					Point2D abs = getAbsolute();
//					moveTo(abs.getX(), abs.getY());
//				}
				relativeSet = false;
				setElementRef(null);
				fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
			}
		}

		// ================================================================================
		// Point Move Methods
		// ================================================================================

		// TODO
		/**
		 * Sets relX and relY for this point. When the given point is linked to a
		 * pathway element, relX and relY are the relative coordinates on the element,
		 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
		 * of the object. TODO was named set RelativePosition
		 * 
		 * @param relX the relative x coordinate.
		 * @param relY the relative y coordinate.
		 */
		public void setRelXY(double relX, double relY) {
			setRelX(relX);
			setRelY(relY);
			relativeSet = true;
		}

		// TODO
		public void moveBy(double deltaX, double deltaY) {
			setX(x + deltaX);
			setY(y + deltaY);
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
			setX(linePoint.getX());
			setY(linePoint.getY());
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}

		public void refeeChanged() {
			// called whenever the object being referred to has changed.
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}

		// /**
		// * Helper method for converting older GPML files without relative coordinates.
		// *
		// * @return true if {@link #setRelativePosition(double, double)} was called to
		// * set the relative coordinates, false if not.
		// */
		// protected boolean relativeSet() {
		// return relativeSet;
		// }

		// ================================================================================
		// Inherited Methods
		// ================================================================================

		/**
		 * Terminates this line point element. Removes any link to an elementRef. The
		 * pathway model, if any, is unset from this pathway element.
		 */
		@Override
		protected void terminate() {
			unlink(); // TODO unset as a LinkableTo
			super.terminate();
		}

		// ================================================================================
		// Clone Methods
		// ================================================================================
		public Object clone() throws CloneNotSupportedException {
			LinePoint p = (LinePoint) super.clone();
			if (elementRef != null)
				p.elementRef = elementRef;
			return p;
		}

		/**
		 * Note: doesn't change parent, only fields
		 *
		 * Used by UndoAction.
		 *
		 * @param src
		 */
		public void copyValuesFrom(LinePoint src) { // TODO
			arrowHead = src.arrowHead;
			x = src.x;
			y = src.y;
			elementRef = src.elementRef;
			relX = src.relX;
			relY = src.relY;
			fireObjectModifiedEvent(PathwayElementEvent.createAllPropertiesEvent(this));
		}

	}

// ================================================================================
// Anchor Class
// ================================================================================
	/**
	 * This class stores information for an Anchor pathway element. Anchor element
	 * is a connection point on a graphical line or an interaction.
	 * 
	 * @author finterly
	 */
	public class Anchor extends GenericPoint implements LinkableTo {

		private double position;
		private AnchorShapeType shapeType = AnchorShapeType.NONE;

		// ================================================================================
		// Constructors
		// ================================================================================
		/**
		 * Instantiates an Anchor pathway element.
		 * 
		 * @param position  the proportional distance of an anchor along the line it
		 *                  belongs to.
		 * @param shapeType the visual representation of an anchor.
		 */
		private Anchor(double position, AnchorShapeType shapeType) {
			super();
			setPosition(position); // must be valid
			setShapeType(shapeType);
		}

		// ================================================================================
		// Accessors
		// ================================================================================
		/**
		 * Returns the proportional distance of an anchor along the line it belongs to,
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
		 * @param v the position of the anchor to set.
		 */
		public void setPosition(double v) {
			if (v < 0 || v > 1) {
				throw new IllegalArgumentException("Invalid position value '" + v + "' must be between 0 and 1");
			}
			if (position != v) {
				position = v;
				fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(LineElement.this));
			}
		}

		/**
		 * Returns the visual representation of an anchor, e.g., none, square.
		 * 
		 * @return shapeType the shape type of the anchor. Return default square
		 *         shapeType if null.
		 */
		public AnchorShapeType getShapeType() {
			if (shapeType == null) {
				return AnchorShapeType.SQUARE;
			} else {
				return shapeType;
			}
		}

		/**
		 * Sets the shapeType for given anchor pathway element.
		 * 
		 * @param v the shape type of the anchor to set.
		 * @throws IllegalArgumentException if shapeType null.
		 */
		public void setShapeType(AnchorShapeType v) {
			if (v == null) {
				shapeType = AnchorShapeType.SQUARE;
			} // TODO
			if (shapeType != v) {
				shapeType = v;
			}
			fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ANCHORSHAPETYPE));
		}

		// ================================================================================
		// Inherited Methods
		// ================================================================================
		/**
		 * Returns {@link LinkableFrom} pathway elements, at this time that only goes
		 * for {@link LinePoint}, for this {@link LinkableTo} pathway element.
		 */
		@Override
		public Set<LinkableFrom> getLinkableFroms() {
			return GraphLink.getReferences(this, getPathwayModel());
		}

		// TODO
		public void unsetAllLinkableFroms() {
			for (LinkableFrom linePoint : getLinkableFroms()) {
				pathwayModel.removeElementRef(this, linePoint);
			}
		}

		@Override
		public Point2D toAbsoluteCoordinate(Point2D p) {
			Point2D l = getLineElement().getConnectorShape().fromLineCoordinate(getPosition());
			return new Point2D.Double(p.getX() + l.getX(), p.getY() + l.getY());
		}

		@Override
		public Point2D toRelativeCoordinate(Point2D p) {
			Point2D l = getLineElement().getConnectorShape().fromLineCoordinate(getPosition());
			return new Point2D.Double(p.getX() - l.getX(), p.getY() - l.getY());
		}

		/**
		 * Terminates this anchor element. Removes any link by line point. The pathway
		 * model, if any, is unset from this pathway element.
		 */
		@Override
		protected void terminate() {
			unsetAllLinkableFroms(); // TODO unset as a LinkableTo
			super.terminate();
		}

		// ================================================================================
		// Clone Methods
		// ================================================================================
		/**
		 * Note: doesn't change parent, only fields
		 *
		 * Used by UndoAction.
		 *
		 * @param src
		 */
		public void copyValuesFrom(Anchor src) { // TODO
			position = src.position;
			shapeType = src.shapeType;
			fireObjectModifiedEvent(PathwayElementEvent.createAllPropertiesEvent(this));
		}

	}

}
