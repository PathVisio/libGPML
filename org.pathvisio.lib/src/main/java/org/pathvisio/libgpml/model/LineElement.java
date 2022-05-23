/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.libgpml.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.model.GraphLink.LinkableFrom;
import org.pathvisio.libgpml.model.GraphLink.LinkableTo;
import org.pathvisio.libgpml.model.connector.ConnectorRestrictions;
import org.pathvisio.libgpml.model.connector.ConnectorShape;
import org.pathvisio.libgpml.model.connector.ConnectorShape.WayPoint;
import org.pathvisio.libgpml.model.connector.ConnectorShapeFactory;
import org.pathvisio.libgpml.model.connector.ElbowConnectorShape;
import org.pathvisio.libgpml.model.type.AnchorShapeType;
import org.pathvisio.libgpml.model.type.ArrowHeadType;
import org.pathvisio.libgpml.model.type.ConnectorType;
import org.pathvisio.libgpml.model.type.LineStyleType;
import org.pathvisio.libgpml.model.type.ObjectType;
import org.pathvisio.libgpml.prop.StaticProperty;
import org.pathvisio.libgpml.util.Utils;

/**
 * This abstract class stores information for a Line pathway element, e.g.
 * {@link GraphicalLine} or {@link Interaction}.
 *
 * @author finterly
 */
public abstract class LineElement extends PathwayElement implements Groupable, ConnectorRestrictions {

	private ArrowHeadType startArrowHeadType = ArrowHeadType.UNDIRECTED;
	private ArrowHeadType endArrowHeadType = ArrowHeadType.UNDIRECTED;
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
		setLinePoints(new ArrayList<LinePoint>(Arrays.asList(new LinePoint(0, 0), new LinePoint(0, 0))));
		this.anchors = new ArrayList<Anchor>();
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the arrowHead property of the start point. Arrowhead specifies the
	 * glyph at the ends of graphical lines and interactions. Intermediate points
	 * have arrowhead type UNDIRECTED (the absence of an arrowhead).
	 *
	 * @return startArrowHeadType the arrow head type.
	 */
	public ArrowHeadType getStartArrowHeadType() {
		return startArrowHeadType == null ? ArrowHeadType.UNDIRECTED : startArrowHeadType;
	}

	/**
	 * Sets the arrow head type of the start point.
	 *
	 * @param value the arrow head type to set.
	 */
	public void setStartArrowHeadType(ArrowHeadType value) {
		if (startArrowHeadType != value && value != null) {
			startArrowHeadType = value;
			fireObjectModifiedEvent(
					PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.STARTARROWHEADTYPE));
		}
	}

	/**
	 * Returns the arrowHead property of the end point. Arrowhead specifies the
	 * glyph at the ends of graphical lines and interactions. Intermediate points
	 * have arrowhead type UNDIRECTED (the absence of an arrowhead).
	 *
	 * @return endArrowHeadType the arrow head type.
	 */
	public ArrowHeadType getEndArrowHeadType() {
		return endArrowHeadType == null ? ArrowHeadType.UNDIRECTED : endArrowHeadType;
	}

	/**
	 * Sets the arrow head type of the end point.
	 *
	 * @param value the arrow head type to set.
	 */
	public void setEndArrowHeadType(ArrowHeadType value) {
		if (endArrowHeadType != value && value != null) {
			endArrowHeadType = value;
			fireObjectModifiedEvent(
					PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ENDARROWHEADTYPE));
		}
	}

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
		if (v.getPathwayModel() != pathwayModel) {
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
		groupRef = v;
		fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
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
	 * In the case of updating waypoints, start and end line points are preserved
	 * and should not be removed.
	 *
	 * @param points the list of points to set.
	 */
	public void setLinePoints(List<LinePoint> points) {
		if (points != null) {
			if (points.size() < 2) {
				throw new IllegalArgumentException("Points array should at least have two elements for "
						+ getClass().getSimpleName() + " " + getElementId());
			}
			List<LinePoint> toRemove = new ArrayList<LinePoint>();
			if (linePoints != null) {
				for (LinePoint linePoint : linePoints) {
					// in some cases, start and end points are preserved and should not be removed
					if (!points.contains(linePoint)
							&& (linePoint == getStartLinePoint() || linePoint == getEndLinePoint())) {
						toRemove.add(linePoint);
					}
				}
				removeLinePoints(toRemove); // remove points before setting new points
			}
			addLinePoints(points); // adds points to pathway model and to this line
			fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * Adds all given points to the linePoints list. Adds each point to the pathway
	 * model {@link PathwayModel#addPathwayObject} if applicable. This method is
	 * called only by {@link #setLinePoints}.
	 *
	 * @param points the points to add to pathway model..
	 */
	private void addLinePoints(List<LinePoint> points) {
		for (LinePoint point : points) {
			if (point == null) {
				throw new IllegalArgumentException("Cannot add invalid point to line " + getElementId());
			}
			if (point.getLineElement() != this) {
				throw new IllegalArgumentException("Cannot add point to line other than its parent line");
			}
			// if start and end points already belong to the pathway model, do not add again
			if (pathwayModel != null && !pathwayModel.hasPathwayObject(point)) {
				pathwayModel.addPathwayObject(point);
			}
			linePoints.add(point);
		}
	}

	/**
	 * Removes all points from the given line points list.
	 * 
	 * <p>
	 * NB:
	 * <ol>
	 * <li>Called from {@link setLinePoints}. For which in some cases, start and end
	 * line points are preserved and should not be removed.
	 * <li>When this line element is terminated {@link terminate}, all points of
	 * linePoints list are removed.
	 * </ol>
	 * 
	 * @param toRemove the list of points to remove.
	 */
	private void removeLinePoints(List<LinePoint> toRemove) {
		for (int i = toRemove.size() - 1; i >= 0; i--) {
			LinePoint point = toRemove.get(i);
			if (point.pathwayModel != null) {
				pathwayModel.removePathwayObject(point);
			}
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
			// add anchor to same pathway model as line if applicable
			if (pathwayModel != null)
				pathwayModel.addPathwayObject(anchor);
			anchors.add(anchor);
			// No anchor property, use LINESTYLE as dummy property to force redraw on line
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
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
		if (pathwayModel != null)
			pathwayModel.removePathwayObject(anchor);
		anchors.remove(anchor);
		// No anchor property, use LINESTYLE as dummy property to force redraw on line
		fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
	}

	/**
	 * Removes all anchors from the anchors list.
	 */
	private void removeAnchors() {
		for (int i = anchors.size() - 1; i >= 0; i--) {
			if (pathwayModel != null)
				pathwayModel.removePathwayObject(anchors.get(i));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.LINECOLOR));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
		}
	}

	/**
	 * Returns the pixel value for the width of a line.
	 *
	 * @return lineWidth the width of a line.
	 */
	public double getLineWidth() {
		return lineWidth;
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.LINEWIDTH));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.CONNECTORTYPE));
		}
	}

	/**
	 * Returns the z-order of this pathway element.
	 *
	 * @return zOrder the order of this pathway element.
	 */
	@Override
	public int getZOrder() {
		return zOrder;
	}

	/**
	 * Sets the z-order of this pathway element.
	 *
	 * @param v the order of this pathway element.
	 */
	@Override
	public void setZOrder(int v) {
		if (zOrder != v) {
			zOrder = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ZORDER));
		}
	}

	// ================================================================================
	// Start and End LinePoint Methods
	// ================================================================================
	/**
	 * Returns the start (first) point of points list.
	 *
	 * @return the first point of points list.
	 */
	public LinePoint getStartLinePoint() {
		return linePoints.get(0);
	}

	/**
	 * Sets start linePoint coordinates to the coordinates of the given linePoint.
	 *
	 * @param linePoint the given line point.
	 */
	public void setStartLinePoint(LinePoint linePoint) {
		getStartLinePoint().moveTo(linePoint);
	}

	/**
	 * Returns the end (last) point of points list.
	 *
	 * @return the last point of points list.
	 */
	public LinePoint getEndLinePoint() {
		return linePoints.get(linePoints.size() - 1);
	}

	/**
	 * Sets end linePoint coordinates to the coordinates of the given linePoint.
	 *
	 * @param linePoint the given line point.
	 */
	public void setEndLinePoint(LinePoint linePoint) {
		getEndLinePoint().moveTo(linePoint);
	}

	/**
	 * Returns the x coordinate of the start point.
	 *
	 * @return the x coordinate of the start point.
	 */
	public double getStartLinePointX() {
		return getStartLinePoint().getX();
	}

	/**
	 * Sets the x coordinate of the start point.
	 *
	 * @param v the value to set.
	 */
	public void setStartLinePointX(double v) {
		getStartLinePoint().setX(v);
	}

	/**
	 * Returns the y coordinate of the start point.
	 *
	 * @return the y coordinate of the start point.
	 */
	public double getStartLinePointY() {
		return getStartLinePoint().getY();
	}

	/**
	 * Sets the y coordinate of the start point.
	 *
	 * @param v the value to set.
	 */
	public void setStartLinePointY(double v) {
		getStartLinePoint().setY(v);
	}

	/**
	 * Returns the x coordinate of the end point.
	 *
	 * @return the x coordinate of the end point.
	 */
	public double getEndLinePointX() {
		return getEndLinePoint().getX();
	}

	/**
	 * Sets the x coordinate of the end point.
	 *
	 * @param v the value to set.
	 */
	public void setEndLinePointX(double v) {
		getEndLinePoint().setX(v);
	}

	/**
	 * Returns the y coordinate of the end point.
	 *
	 * @return the y coordinate of the end point.
	 */
	public double getEndLinePointY() {
		return getEndLinePoint().getY();
	}

	/**
	 * Sets the y coordinate of the end point.
	 *
	 * @param v the value to set.
	 */
	public void setEndLinePointY(double v) {
		getEndLinePoint().setY(v);
	}

	/**
	 * Returns the elementRef of the end point.
	 *
	 * @return the elementRef linkableTo pathway element.
	 */
	public LinkableTo getStartElementRef() {
		return getStartLinePoint().getElementRef();
	}

	/**
	 * Sets the elementRef for the start point.
	 *
	 * @param elementRef to link to.
	 */
	public void setStartElementRef(LinkableTo elementRef) {
		getStartLinePoint().linkTo(elementRef);
	}

	/**
	 * Returns the elementRef of the end point.
	 *
	 * @return the elementRef linkableTo pathway element.
	 */
	public LinkableTo getEndElementRef() {
		return getEndLinePoint().getElementRef();
	}

	/**
	 * Sets the elementRef for the end point.
	 *
	 * @param elementRef to link to.
	 */
	public void setEndElementRef(LinkableTo elementRef) {
		getEndLinePoint().linkTo(elementRef);

	}

	// ================================================================================
	// Point2D Methods
	// ================================================================================
	/** converts start point from MPoint to Point2D */
	@Override
	public Point2D getStartPoint2D() {
		return getStartLinePoint().toPoint2D();
	}

	/** converts end point from MPoint to Point2D */
	@Override
	public Point2D getEndPoint2D() {
		return getEndLinePoint().toPoint2D();
	}

	/** converts all points from MPoint to Point2D */
	public List<Point2D> getPoints2D() {
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
	@Override
	public double getCenterX() {
		double start = getStartLinePointX();
		double end = getEndLinePointX();
		return start + (end - start) / 2;
	}

	/**
	 * Sets the x position of the center of the line. This makes the line move as a
	 * whole
	 */
	@Override
	public void setCenterX(double v) {
		double dx = v - getCenterX();
		setStartLinePointX(getStartLinePointX() + dx);
		setEndLinePointX(getEndLinePointX() + dx);
	}

	/**
	 * Returns the center y coordinate of the bounding box around (start, end) this
	 * line pathway element.
	 */
	@Override
	public double getCenterY() {
		double start = getStartLinePointY();
		double end = getEndLinePointY();
		return start + (end - start) / 2;
	}

	/**
	 * Sets the y position of the center of the line. This makes the line move as a
	 * whole.
	 */
	@Override
	public void setCenterY(double v) {
		double dy = v - getCenterY();
		setStartLinePointY(getStartLinePointY() + dy);
		setEndLinePointY(getEndLinePointY() + dy);
	}

	/**
	 * Calculates and returns the width of the bounding box around (start, end) this
	 * line pathway element.
	 */
	@Override
	public double getWidth() {
		double start = getStartLinePointX();
		double end = getEndLinePointX();
		return Math.abs(start - end);
	}

	/**
	 * Calculates and returns the height of the bounding box around (start, end)
	 * this line pathway element.
	 */
	@Override
	public double getHeight() {
		double start = getStartLinePointY();
		double end = getEndLinePointY();
		return Math.abs(start - end);
	}

	/**
	 * Returns the left x coordinate of the bounding box around (start, end) this
	 * line pathway element.
	 */
	@Override
	public double getLeft() {
		double start = getStartLinePointX();
		double end = getEndLinePointX();
		return Math.min(start, end);
	}

	/**
	 * Sets the position of the left side of the rectangular bounds of the line
	 */
	@Override
	public void setLeft(double v) {
		if (getDirectionX() > 0) {
			setStartLinePointX(v);
		} else {
			setEndLinePointX(v);
		}
	}

	/**
	 * Returns the top y coordinate of the bounding box around (start, end) this
	 * line pathway element.
	 */
	@Override
	public double getTop() {
		double start = getStartLinePointY();
		double end = getEndLinePointY();
		return Math.min(start, end);
	}

	/**
	 * Sets the position of the top side of the rectangular bounds of the line
	 */
	@Override
	public void setTop(double v) {
		if (getDirectionY() > 0) {
			setStartLinePointY(v);
		} else {
			setEndLinePointY(v);
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

	/**
	 * @param p
	 * @return
	 */
	@Override
	public Point2D toAbsoluteCoordinate(Point2D p) {
		double x = p.getX();
		double y = p.getY();
		Rectangle2D bounds = getRotatedBounds();
		// Scale
		if (bounds.getWidth() != 0)
			x *= bounds.getWidth() / 2;
		if (bounds.getHeight() != 0)
			y *= bounds.getHeight() / 2;
		// Translate
		x += bounds.getCenterX();
		y += bounds.getCenterY();
		return new Point2D.Double(x, y);
	}

	/**
	 * @param mp a point in absolute model coordinates
	 * @return the same point relative to the bounding box of this pathway element:
	 *         -1,-1 meaning the top-left corner, 1,1 meaning the bottom right
	 *         corner, and 0,0 meaning the center.
	 */
	@Override
	public Point2D toRelativeCoordinate(Point2D mp) {
		double relX = mp.getX();
		double relY = mp.getY();
		Rectangle2D bounds = getRotatedBounds();
		// Translate
		relX -= bounds.getCenterX();
		relY -= bounds.getCenterY();
		// Scalebounds.getCenterX();
		if (relX != 0 && bounds.getWidth() != 0)
			relX /= bounds.getWidth() / 2;
		if (relY != 0 && bounds.getHeight() != 0)
			relY /= bounds.getHeight() / 2;
		return new Point2D.Double(relX, relY);
	}

	// ================================================================================
	// Connector Methods
	// ================================================================================
	ConnectorShape shape;

	/**
	 * The Connector Shape for this line - the connector shape can calculate a Shape
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
	@Override
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
	@Override
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
	@Override
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
	@Override
	public Rectangle2D mayCross(Point2D point) {
		Rectangle2D rect = null;
		if (pathwayModel != null) {
			for (PathwayObject e : pathwayModel.getPathwayObjects()) {
				ObjectType ot = e.getObjectType();
				if (ot == ObjectType.SHAPE || ot == ObjectType.DATANODE || ot == ObjectType.LABEL) {
					Rectangle2D b = ((ShapedElement) e).getBounds();
					if (b.contains(point)) {
						if (rect == null) {
							rect = b;
						} else {
							rect.add(b);
						}
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
		// if line element has points and anchors, also add them to pathway model
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
		removeLinePoints(linePoints);
		removeAnchors();
		super.terminate();
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Copies values from the given source pathway element.
	 *
	 * <p>
	 * NB:
	 * <ol>
	 * <li>GroupRef is not copied, but can be set later if the parent group and all
	 * other pathway element members are copied.
	 * </ol>
	 *
	 * @param src the source pathway element.
	 */
	public void copyValuesFrom(LineElement src) {
		super.copyValuesFrom(src);
		groupRef = src.groupRef;
		// copy line points
		List<LinePoint> points = new ArrayList<LinePoint>();
		for (LinePoint pt : src.getLinePoints()) {
			points.add(new LinePoint(pt.getX(), pt.getY()));
		}
		setLinePoints(points);
		// copy anchors
		for (Anchor a : src.anchors) {
			addAnchor(new Anchor(a.getPosition(), a.getShapeType()));
		}
		lineColor = src.lineColor;
		lineStyle = src.lineStyle;
		lineWidth = src.lineWidth;
		connectorType = src.connectorType;
		zOrder = src.zOrder;
		startArrowHeadType = src.startArrowHeadType;
		endArrowHeadType = src.endArrowHeadType;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	// ================================================================================
	// Property Methods
	// ================================================================================
	/**
	 * Returns all static properties for this pathway object.
	 *
	 * @return result the set of static property for this pathway object.
	 */
	@Override
	public Set<StaticProperty> getStaticPropertyKeys() {
		Set<StaticProperty> result = super.getStaticPropertyKeys();
		Set<StaticProperty> propsLineElement = EnumSet.of(StaticProperty.GROUPREF, StaticProperty.LINECOLOR,
				StaticProperty.LINESTYLE, StaticProperty.LINEWIDTH, StaticProperty.CONNECTORTYPE, StaticProperty.STARTX,
				StaticProperty.STARTY, StaticProperty.ENDX, StaticProperty.ENDY, StaticProperty.STARTARROWHEADTYPE,
				StaticProperty.ENDARROWHEADTYPE, StaticProperty.STARTELEMENTREF, StaticProperty.ENDELEMENTREF,
				StaticProperty.ZORDER);
		result.addAll(propsLineElement);
		return result;
	}

	/**
	 * Returns static property value for given key.
	 *
	 * @param key the key.
	 * @return the static property value.
	 */
	@Override
	public Object getStaticProperty(StaticProperty key) {
		Object result = super.getStaticProperty(key);
		if (result == null) {
			switch (key) {
			case GROUPREF:
				result = getGroupRef();
				break;
			case LINECOLOR:
				result = getLineColor();
				break;
			case LINESTYLE:
				result = getLineStyle().getName();
				break;
			case LINEWIDTH:
				result = getLineWidth();
				break;
			case CONNECTORTYPE:
				result = getConnectorType().getName();
				break;
			case STARTX:
				result = getStartLinePointX();
				break;
			case STARTY:
				result = getStartLinePointY();
				break;
			case ENDX:
				result = getEndLinePointX();
				break;
			case ENDY:
				result = getEndLinePointY();
				break;
			case STARTARROWHEADTYPE:
				result = getStartArrowHeadType().getName();
				break;
			case ENDARROWHEADTYPE:
				result = getEndArrowHeadType().getName();
				break;
			case STARTELEMENTREF:
				result = getStartElementRef();
				break;
			case ENDELEMENTREF:
				result = getEndElementRef();
				break;
			case ZORDER:
				result = getZOrder();
				break;
			default:
				// do nothing
			}
		}
		return result;
	}

	/**
	 * This works so that o.setNotes(x) is the equivalent of o.setProperty("Notes",
	 * x);
	 *
	 * Value may be null in some cases, e.g. graphRef
	 *
	 * @param key   the key.
	 * @param value the property value.
	 */
	@Override
	public void setStaticProperty(StaticProperty key, Object value) {
		super.setStaticProperty(key, value);
		switch (key) {
		case GROUPREF:
			setGroupRef((Group) value);
			break;
		case LINECOLOR:
			setLineColor((Color) value);
			break;
		case LINESTYLE:
			if (value instanceof LineStyleType) {
				setLineStyle((LineStyleType) value);
			} else {
				setLineStyle(LineStyleType.fromName((String) value));
			}
			break;
		case LINEWIDTH:
			setLineWidth((Double) value);
			break;
		case CONNECTORTYPE:
			if (value instanceof ConnectorType) {
				setConnectorType((ConnectorType) value);
			} else {
				setConnectorType(ConnectorType.fromName((String) value));
			}
			break;
		case STARTX:
			setStartLinePointX((Double) value);
			break;
		case STARTY:
			setStartLinePointY((Double) value);
			break;
		case ENDX:
			setEndLinePointX((Double) value);
			break;
		case ENDY:
			setEndLinePointY((Double) value);
			break;
		case STARTARROWHEADTYPE:
			if (value instanceof ArrowHeadType) {
				setStartArrowHeadType((ArrowHeadType) value);
			} else {
				setStartArrowHeadType(ArrowHeadType.fromName((String) value));
			}
			break;
		case ENDARROWHEADTYPE:
			if (value instanceof ArrowHeadType) {
				setEndArrowHeadType((ArrowHeadType) value);
			} else {
				setEndArrowHeadType(ArrowHeadType.fromName((String) value));
			}
			break;
		case STARTELEMENTREF:
			setStartElementRef((LinkableTo) value);
			break;
		case ENDELEMENTREF:
			setEndElementRef((LinkableTo) value);
			break;
		case ZORDER:
			setZOrder((Integer) value);
			break;
		default:
			// do nothing
		}
	}

// ================================================================================
// GenericPoint Class
// ================================================================================
	/**
	 * Abstract class of generic point, extended by {@link LinePoint} and
	 * {@link Anchor}.
	 *
	 * @author unknown, finterly
	 */
	public abstract class GenericPoint extends PathwayObject implements Drawable {

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
		 * @param x the x coordinate position of the point.
		 * @param y the y coordinate position of the point.
		 */
		public LinePoint(double x, double y) {
			super();
			setX(x);
			setY(y);
		}

		// ================================================================================
		// Accessors
		// ================================================================================
		/**
		 * Returns the object type of this pathway element.
		 *
		 * @return the object type.
		 */
		@Override
		public ObjectType getObjectType() {
			return ObjectType.LINEPOINT;
		}

		/**
		 * Returns x coordinate value.
		 *
		 * @return x the coordinate value for x.
		 */
		public double getX() {
			if (isRelative()) {
				return getAbsolute().getX();
			} else {
				return x;
			}
		}

		/**
		 * Sets x coordinate to given value.
		 *
		 * @param v the coordinate value to set for x.
		 */
		public void setX(double v) {
			if (v != getX()) {
				moveBy(v - getX(), 0);
			}
			if (x < 0) {
				Logger.log.trace("Warning: negative x coordinate " + String.valueOf(v));
			}

		}

		/**
		 * Returns y coordinate value.
		 *
		 * @return y the coordinate value for y.
		 */
		public double getY() {
			if (isRelative()) {
				return getAbsolute().getY();
			} else {
				return y;
			}
		}

		/**
		 * Sets y coordinate to given value.
		 *
		 * @param v the coordinate value to set for y.
		 */
		public void setY(double v) {
			if (v != getY()) {
				moveBy(0, v - getY());
			}
			if (y < 0) {
				Logger.log.trace("Warning: negative y coordinate " + String.valueOf(v));
			}
		}

		/**
		 * Returns the pathway element to which this point refers to. In GPML, this is
		 * elementRef which refers to the elementId of a pathway element.
		 *
		 * @return elementRef the pathway element to which this point refers.
		 */
		@Override
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
				if (pathwayModel != null) {
					if (elementRef != null) {
						pathwayModel.removeElementRef(elementRef, this);
					}
					if (v != null) {
						pathwayModel.addElementRef(v, this);
					}
				}
				elementRef = v;
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
		@Override
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
				LineElement.this
						.fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(LineElement.this));
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
		@Override
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
				LineElement.this
						.fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(LineElement.this));
			}
		}

		// ================================================================================
		// Point Link Methods
		// ================================================================================

		/**
		 * Checks if the position of this point should be stored as relative or absolute
		 * coordinates.
		 *
		 * @return true if the coordinates are relative, false otherwise.
		 */
		public boolean isRelative() {
			if (pathwayModel != null && elementRef != null) {
				return pathwayModel.hasPathwayObject((PathwayObject) elementRef);
			} else {
				return false;
			}
		}

		/**
		 * Returns absolute coordinates based on elementRef.
		 *
		 * @return
		 */
		private Point2D getAbsolute() {
			return elementRef.toAbsoluteCoordinate(new Point2D.Double(getRelX(), getRelY()));
		}

		/**
		 * Returns this line point as a {@link Point2D}.
		 *
		 * @return this point as point2d.
		 */
		public Point2D toPoint2D() {
			return new Point2D.Double(getX(), getY());
		}

		/**
		 * Returns the absolute coordinate as a {@link Point2D}.
		 *
		 * @return the absolute coordinate as point2d.
		 */
		@Override
		public Point2D toAbsoluteCoordinate(Point2D p) {
			return new Point2D.Double(p.getX() + getX(), p.getY() + getY());
		}

		/**
		 * Returns the relative coordinate as a {@link Point2D}.
		 *
		 * @return the relative coordinate as point2d.
		 */
		@Override
		public Point2D toRelativeCoordinate(Point2D p) {
			return new Point2D.Double(p.getX() - getX(), p.getY() - getY());
		}

		/**
		 * Links this line point to the given object. Updates xy and relXY coordinates.
		 *
		 * @param elementRef the linkableTo pathway element or anchor to link to.
		 */
		public void linkTo(LinkableTo elementRef) {
			if (elementRef != null) {
				Point2D rel = elementRef.toRelativeCoordinate(toPoint2D());
				linkTo(elementRef, rel.getX(), rel.getY());
			} else {
				unlink();
			}
		}

		/**
		 * Links this line point to the given object. Updates xy and relXY coordinates.
		 *
		 * @param elementRef the linkableTo pathway element or anchor to link to.
		 * @param relX       the relative x coordinate to set.
		 * @param relY       the relative y coordinate to set.
		 */
		@Override
		public void linkTo(LinkableTo elementRef, double relX, double relY) {
			setElementRef(elementRef);
			setRelativePosition(relX, relY);
		}

		/**
		 * Unlinks this LinePoint from its LinkableTo elementRef.
		 * 
		 * NB: This may be called any number of times when this point is already
		 * unlinked
		 */
		@Override
		public void unlink() {
			if (elementRef != null) {
				if (pathwayModel != null) {
					Point2D abs = getAbsolute();
					moveTo(abs.getX(), abs.getY());
				}
				setElementRef(null);
				LineElement.this
						.fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(LineElement.this));
			}
		}

		/**
		 * Sets X, Y, relX and relY for this point. Updates x and y absolute coordinates
		 * for this point which is now relative. When the given point is linked to a
		 * pathway element, relX and relY are the relative coordinates on the element,
		 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
		 * of the object.
		 *
		 * @param relX the relative x coordinate.
		 * @param relY the relative y coordinate.
		 */
		public void setRelativePosition(double relX, double relY) {
			moveTo(getX(), getY());
			setRelX(relX);
			setRelY(relY);
		}

		// ================================================================================
		// Point Move Methods
		// ================================================================================

		/**
		 * Moves x and y coordinates for this line point by the given values.
		 *
		 * @param deltaX the value to move x coordinate by.
		 * @param deltaY the value to move y coordinate by.
		 */
		public void moveBy(double deltaX, double deltaY) {
			x = getX() + deltaX;
			y = getY() + deltaY;
			LineElement.this
					.fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(LineElement.this));
		}

		/**
		 * Moves x and y coordinates for this line point to given values.
		 *
		 * @param vx the value to move x coordinate to.
		 * @param vy the value to move y coordinate to.
		 */
		public void moveTo(double vx, double vy) {
			x = vx;
			y = vy;
			LineElement.this
					.fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(LineElement.this));
		}

		/**
		 * Moves the xy and relXY coordinates for this line point to the coordinate
		 * values of the given line point.
		 *
		 * @param linePoint the linePoint to move to.
		 */
		public void moveTo(LinePoint linePoint) {
			setX(linePoint.getX());
			setY(linePoint.getY());
			setRelX(linePoint.getRelX());
			setRelY(linePoint.getRelY());
			LineElement.this
					.fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(LineElement.this));
		}

		/**
		 * Called whenever the object being referred to has changed.
		 */
		@Override
		public void refeeChanged() {
			LineElement.this
					.fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(LineElement.this));
		}

		// ================================================================================
		// Inherited Methods
		// ================================================================================

		/**
		 * Returns the z-order of this pathway element.
		 *
		 * NB: LinePoint z-order is always z-order of parent line +1. This is because
		 * z-order is not written out to the gpml file.
		 *
		 * @return zOrder the order of this pathway element.
		 */
		@Override
		public int getZOrder() {
			return getLineElement().getZOrder() + 1;
		}

		/**
		 * Do nothing. LinePoint z-order is always z-order of parent line +1. This is
		 * because z-order is not written out to the gpml file.
		 *
		 * @param v the input
		 */
		@Override
		public void setZOrder(int v) {
			// do nothing
		}

		/**
		 * Terminates this line point and removes all links and references.
		 */
		@Override
		protected void terminate() {
			unlink();
			super.terminate();
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
		 * Returns the object type of this pathway element.
		 *
		 * @return the object type.
		 */
		@Override
		public ObjectType getObjectType() {
			return ObjectType.ANCHOR;
		}

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
				LineElement.this
						.fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(LineElement.this));
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
				return AnchorShapeType.NONE;
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
				shapeType = AnchorShapeType.NONE;
			}
			if (shapeType != v) {
				shapeType = v;
			}
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ANCHORSHAPETYPE));
		}

		// ================================================================================
		// Inherited Methods
		// ================================================================================

		/**
		 * Returns the z-order of this pathway element.
		 *
		 * NB: Anchor z-order is always z-order of parent line +1. This is because
		 * z-order is not written out to the gpml file.
		 *
		 * @return zOrder the order of this pathway element.
		 */
		@Override
		public int getZOrder() {
			return getLineElement().getZOrder() + 1;
		}

		/**
		 * Do nothing. Anchor z-order is always z-order of parent line +1. This is
		 * because z-order is not written out to the gpml file.
		 *
		 * @param v the input
		 */
		@Override
		public void setZOrder(int v) {
			// do nothing
		}

		/**
		 * Returns {@link LinkableFrom} pathway elements, at this time that only goes
		 * for {@link LinePoint}, for this {@link LinkableTo} pathway element.
		 */
		@Override
		public Set<LinkableFrom> getLinkableFroms() {
			return GraphLink.getReferences(this, pathwayModel);
		}

		/**
		 * Removes links from all {@link LinkableFrom} line points to this
		 * {@link LinkableTo} pathway element.
		 */
		public void unsetAllLinkableFroms() {
			for (LinkableFrom linePoint : getLinkableFroms()) {
				((LinePoint) linePoint).unlink();
			}
		}

		/**
		 * Returns the absolute coordinate as a {@link Point2D}.
		 *
		 * @return the absolute coordinate as point2d.
		 */
		@Override
		public Point2D toAbsoluteCoordinate(Point2D p) {
			Point2D l = getLineElement().getConnectorShape().fromLineCoordinate(getPosition());
			return new Point2D.Double(p.getX() + l.getX(), p.getY() + l.getY());
		}

		/**
		 * Returns the relative coordinate as a {@link Point2D}.
		 *
		 * @return the relative coordinate as point2d.
		 */
		@Override
		public Point2D toRelativeCoordinate(Point2D p) {
			Point2D l = getLineElement().getConnectorShape().fromLineCoordinate(getPosition());
			return new Point2D.Double(p.getX() - l.getX(), p.getY() - l.getY());
		}

		/**
		 * Terminates this anchor and removes all links and references.
		 */
		@Override
		protected void terminate() {
			unsetAllLinkableFroms();
			super.terminate();
		}

	}

}
