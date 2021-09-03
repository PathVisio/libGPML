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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.pathvisio.debug.Logger;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.GraphLink.LinkableFrom;
import org.pathvisio.model.GraphLink.LinkableTo;
import org.pathvisio.model.ref.PathwayElement;
import org.pathvisio.model.type.AnchorShapeType;
import org.pathvisio.model.type.ArrowHeadType;
import org.pathvisio.model.type.ConnectorType;
import org.pathvisio.model.type.LineStyleType;
import org.pathvisio.props.StaticProperty;

/**
 * This abstract class stores information for a Line pathway element, e.g.
 * {@link GraphicalLine} or {@link Interaction}.
 * 
 * @author finterly
 */
public abstract class LineElement extends PathwayElement implements Groupable {

	private Group groupRef; // optional, the parent group to which a pathway element belongs.
	private List<LinePoint> linePoints; // minimum 2
	private List<Anchor> anchors;

	// line style properties
	private Color lineColor = Color.decode("#000000"); // black
	private LineStyleType lineStyle = LineStyleType.SOLID; // solid, dashed, or double
	private double lineWidth = 1.0; // 1.0
	private ConnectorType connectorType = ConnectorType.STRAIGHT; // straight, elbow, curved...
	private int zOrder; // optional

	/**
	 * Instantiates a line pathway element. Property groupRef is to be set by
	 * {@link #setGroupRefTo(Group)}. In GPML, groupRef refers to the elementId
	 * (formerly groupId) of the parent gpml:Group. Note, a group can also belong in
	 * another group. Graphics properties have default values and can be set after a
	 * line pathway element is already instantiated.
	 * 
	 */
	public LineElement() {
		super();
		this.linePoints = new ArrayList<LinePoint>(); // should have at least two points
		this.anchors = new ArrayList<Anchor>();
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

	/**
	 * Get the points for this line.
	 * 
	 * @return points the list of points, an empty list if no anchors are defined.
	 */
	public List<LinePoint> getLinePoints() {
		return linePoints;
	}

	/**
	 * Sets line points, only if linePoints list is empty.
	 * 
	 * @param points the list of points to set.
	 */
	public void setLinePoints(List<LinePoint> points) {
		// TODO only allow setLinePoints if empty???
		if (linePoints.isEmpty() && points != null) {
			if (points.size() < 2) {
				throw new IllegalArgumentException("Points array should at least have two elements");
			}
			this.linePoints = points;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * Checks whether linePoints has the given point.
	 * 
	 * @param point the point to look for.
	 * @return true if has point, false otherwise.
	 */
	public boolean hasLinePoint(LinePoint point) {
		return linePoints.contains(point);
	}

	/**
	 * Adds given point to linePoints list. Sets lineElement for the given point.
	 * 
	 * @param point the linePoint to be added.
	 */
	public void addLinePoint(LinePoint point) {
		if (point != null && !hasLinePoint(point)) {
			assert (point.getLineElement() == this);
			// add point to same pathway model as line if applicable TODO
			if (getPathwayModel() != null)
				getPathwayModel().addPathwayObject(point);
			linePoints.add(point);
			// TODO
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * Adds given point to linePoints list. Sets lineElement for the given point.
	 * 
	 * @param elementId the pathway object id.
	 * @param arrowHead the type of arrowhead.
	 * @param x         the x coordinate of point.
	 * @param y         the y coordinate of the point.
	 * @return point the line point instantiated and added to this line.
	 */
	public LinePoint addLinePoint(String elementId, ArrowHeadType arrowHead, double x, double y) {
		LinePoint point = new LinePoint(arrowHead, x, y);
		point.setElementId(elementId);
		// add point to same pathway model as line if applicable TODO
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayObject(point);
		linePoints.add(point);
		// TODO
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		return point;
	}

	/**
	 * Adds given point to linePoints list. Sets lineElement for the given point.
	 * 
	 * @param arrowHead the type of arrowhead.
	 * @param x         the x coordinate of point.
	 * @param y         the y coordinate of the point.
	 */
	public LinePoint addLinePoint(ArrowHeadType arrowHead, double x, double y) {
		LinePoint point = new LinePoint(arrowHead, x, y);
		// add point to same pathway model as line if applicable TODO
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayObject(point);
		linePoints.add(point);
		// TODO
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		return point;
	}

	/**
	 * Adds all given points to the end of linePoints list.
	 * 
	 * @param points the list of points to add.
	 */
	public void addLinePoints(List<LinePoint> points) {
		for (int i = 0; i < points.size(); i++) {
			addLinePoint(points.get(i));
		}
	}

	/**
	 * Removes given point from the linePoints list. Point ceases to exist and is
	 * terminated.
	 * 
	 * @param point the linePoint to be removed.
	 */
	public void removeLinePoint(LinePoint point) {
		assert (point != null && hasLinePoint(point));
		if (getPathwayModel() != null)
			getPathwayModel().removePathwayObject(point);
		linePoints.remove(point);
		point.terminate();
	}

	/**
	 * Removes all linePoints from the linePoints list.
	 */
	public void removeLinePoints() {
		for (int i = 0; i < linePoints.size(); i++) {
			removeLinePoint(linePoints.get(i));
		}
	}

	/**
	 * Returns the start (first) point of points list. TODO necessary method?
	 * 
	 * @return the first point of points list.
	 */
	public LinePoint getStartLinePoint() {
		return linePoints.get(0);
	}

	// TODO weird
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
	 * Add a new anchor to this line at the given position with anchorShapeType
	 * property. TODO for read/write
	 * 
	 * @param elementId
	 * @param position        the relative position on the line, between 0 (start)
	 *                        to 1 (end).
	 * @param anchorShapeType the shape type of the anchor.
	 * @return
	 */
	public Anchor addAnchor(String elementId, double position, AnchorShapeType anchorShapeType) {
		Anchor anchor = new Anchor(position, anchorShapeType);
		anchor.setElementId(elementId);
		// add anchor to same pathway model as line if applicable TODO
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayObject(anchor);
		anchors.add(anchor);
		// No anchor property, use LINESTYLE as dummy property to force redraw on line
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
		return anchor;
	}

	/**
	 * Add a new anchor to this line at the given position with anchorShapeType
	 * property.
	 * 
	 * @param position        the relative position on the line, between 0 (start)
	 *                        to 1 (end).
	 * @param anchorShapeType the shape type of the anchor.
	 */
	public Anchor addAnchor(double position, AnchorShapeType anchorShapeType) {
		Anchor anchor = new Anchor(position, anchorShapeType);
		// add anchor to same pathway model as line if applicable TODO
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayObject(anchor);
		anchors.add(anchor);
		// No anchor property, use LINESTYLE as dummy property to force redraw on line
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
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
		anchor.terminate();
		// No anchor property, use LINESTYLE as dummy property to force redraw on line
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
	}

	/**
	 * Removes all anchors from the anchors list.
	 */
	public void removeAnchors() {
		for (int i = 0; i < anchors.size(); i++) {
			removeAnchor(anchors.get(i));
		}
	}

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
	 * @param connectorType the layout of a line.
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

	/**
	 * Sets the pathway model for this pathway element. NB: Only set when a pathway
	 * model adds this pathway element. This method is not used directly.
	 * 
	 * @param pathwayModel the parent pathway model.
	 */
	protected void setPathwayModelTo(PathwayModel pathwayModel) throws IllegalArgumentException, IllegalStateException {
		if (pathwayModel == null)
			throw new IllegalArgumentException("Invalid pathway model.");
		if (hasPathwayModel())
			throw new IllegalStateException("Pathway element already belongs to a pathway model.");
		setPathwayModel(pathwayModel);
		// if line element has points and anchors, also add them to pathway model TODO
		for (LinePoint point : linePoints) // TODO
			pathwayModel.addPathwayObject(point);
		for (Anchor anchor : anchors) // TODO
			pathwayModel.addPathwayObject(anchor);
	}

	/**
	 * Unsets the pathway model, if any, from this pathway element. The pathway
	 * element no longer belongs to a pathway model. NB: This method is not used
	 * directly.
	 */
	protected void unsetPathwayModel() {
		if (hasPathwayModel()) {
			setPathwayModel(null);
			for (LinePoint point : linePoints) // TODO
				point.setPathwayModel(null);
			for (Anchor anchor : anchors) // TODO
				anchor.setPathwayModel(null);
		}
	}

	/**
	 * Terminates this LineElement. The pathway model, if any, is unset from this
	 * anchor.Links to all annotationRefs, citationRefs, and evidenceRefs are
	 * removed from this data node.
	 */
	@Override
	public void terminate() {
		removeLinePoints();
		removeAnchors();
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();
		unsetGroupRef();
		unsetPathwayModel();
	}

	/**
	 * Abstract class of generic point, extended by {@link LinePoint} and
	 * {@link Anchor}.
	 * 
	 * @author unknown, finterly
	 */
	public abstract class GenericPoint extends PathwayObject {

		/**
		 * Constructor for a generic point.
		 */
		public GenericPoint() {
			super();
		}

		/**
		 * Returns the parent interaction or graphicalLine for this point.
		 * 
		 * @return lineElement the parent line element of this point.
		 */
		public LineElement getLineElement() {
			return LineElement.this;
		}

		/**
		 * Returns the pathway model for this pathway element.
		 * 
		 * @return pathwayModel the parent pathway model.
		 */
		@Override
		public PathwayModel getPathwayModel() {
			return LineElement.this.getPathwayModel();
		}

	}

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
		 * @param x          the x coordinate position of the point.
		 * @param y          the y coordinate position of the point.
		 * @param elementRef the pathway element to which the point refers.
		 * @param relX       the relative x coordinate.
		 * @param relY       the relative x coordinate.
		 */
		private LinePoint(ArrowHeadType arrowHead, double x, double y, LinkableTo elementRef, double relX,
				double relY) {
			super();
			this.arrowHead = arrowHead;
			setX(x);
			setY(y);
			linkTo(elementRef, relX, relY);
		}

		/**
		 * Instantiates a Point pathway element, with no reference to another pathway
		 * element.
		 * 
		 * @param arrowHead the arrowhead property of the point (line by default).
		 * @param x         the x coordinate position of the point.
		 * @param y         the y coordinate position of the point.
		 */
		private LinePoint(ArrowHeadType arrowHead, double x, double y) {
			this(arrowHead, x, y, null, 0, 0);
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
//			Point2D rel = pathwayElement.toRelativeCoordinate(toPoint2D());
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
//					Point2D abs = getAbsolute();
//					moveTo(abs.getX(), abs.getY());
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
//			relativeSet = true; TODO 
		}

//		private boolean relativeSet; TODO 

//		/**
//		 * Helper method for converting older GPML files without relative coordinates. TODO
//		 * 
//		 * @return true if {@link #setRelativePosition(double, double)} was called to
//		 *         set the relative coordinates, false if not.
//		 */
//		protected boolean relativeSet() {
//			return relativeSet;
//		}

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
//			xy = linePoint.getXY(); TODO 
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}

		public void refeeChanged() {
			// called whenever the object being referred to has changed.
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this)); // TODO this or
																								// lineElement?
		}

	}

	/**
	 * This class stores information for an Anchor pathway element. Anchor element
	 * is a connection point on a graphical line or an interaction.
	 * 
	 * @author finterly
	 */
	public class Anchor extends GenericPoint implements LinkableTo {

		private double position;
		private AnchorShapeType shapeType = AnchorShapeType.NONE;

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

		/**
		 * Returns {@link LinkableFrom} pathway elements, at this time that only goes
		 * for {@link LinePoint}, for this {@link LinkableTo} pathway element.
		 */
		@Override
		public Set<LinkableFrom> getLinkableFroms() {
			return GraphLink.getReferences(this, getPathwayModel());
		}

	}

}
