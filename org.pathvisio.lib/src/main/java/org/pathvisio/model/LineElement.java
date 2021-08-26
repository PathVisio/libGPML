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
import java.util.ArrayList;
import java.util.List;

import org.pathvisio.io.listener.PathwayElementEvent;
import org.pathvisio.model.ref.ElementInfo;
import org.pathvisio.model.type.ConnectorType;
import org.pathvisio.model.type.LineStyleType;

/**
 * This abstract class stores information for a Line pathway element, e.g.
 * {@link GraphicalLine} or {@link Interaction}.
 * 
 * @author finterly
 */
public abstract class LineElement extends ElementInfo implements Groupable {

	private List<LinePoint> points; // minimum 2
	private List<Anchor> anchors;
	private Group groupRef; // optional, the parent group to which a pathway element belongs.
	
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
	 * another group.
	 * 
	 */
	public LineElement() {
		super();
		this.points = new ArrayList<LinePoint>(); // should have at least two points
		this.anchors = new ArrayList<Anchor>();
	}

	/**
	 * Get the points for this line.
	 * 
	 * @return points the list of points, an empty list if no anchors are defined.
	 */
	public List<LinePoint> getLinePoints() {
		return points;
	}

	/**
	 * Checks whether points has the given point.
	 * 
	 * @param point the point to look for.
	 * @return true if has point, false otherwise.
	 */
	public boolean hasLinePoint(LinePoint point) {
		return points.contains(point);
	}

	/**
	 * Adds given point to points list. Sets lineElement for the given point.
	 * 
	 * @param point the point to be added.
	 */
	public void addLinePoint(LinePoint point) {
		assert (point != null);
		point.setLineElementTo(this); // TODO
		assert (point.getLineElement() == this);
		assert !hasLinePoint(point);
		// add point to same pathway model as line if applicable
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayElement(point);
		points.add(point);
		// TODO
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
	}

	/**
	 * Removes given point from the points list. Point ceases to exist and is
	 * terminated.
	 * 
	 * @param point the point to be removed.
	 */
	public void removeLinePoint(LinePoint point) {
		assert (point != null && hasLinePoint(point));
		if (getPathwayModel() != null)
			getPathwayModel().removePathwayElement(point);
		points.remove(point);
		point.terminate();
	}

	/**
	 * Removes all points from the points list.
	 */
	public void removeLinePoints() {
		for (int i = 0; i < points.size(); i++) {
			removeLinePoint(points.get(i));
		}
	}

	/**
	 * Returns the start (first) point of points list. TODO necessary method?
	 * 
	 * @return the first point of points list.
	 */
	public LinePoint getStartLinePoint() {
		return points.get(0);
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
		return points.get(points.size() - 1);
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
	 * Adds given anchor to anchors list. Sets lineElement for the given anchor.
	 * 
	 * @param anchor the anchor to be added.
	 */
	public void addAnchor(Anchor anchor) {
		assert (anchor != null);
		anchor.setLineElementTo(this); // TODO
		assert (anchor.getLineElement() == this);
		assert !hasAnchor(anchor);
		// add anchor to same pathway model as line if applicable
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayElement(anchor);
		anchors.add(anchor);
		// TODO
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, anchor));

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
			getPathwayModel().removePathwayElement(anchor);
		anchors.remove(anchor);
		anchor.terminate();
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
	 * Returns the parent group of the pathway element. In GPML, groupRef refers to
	 * the elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of the pathway element.
	 */
	public Group getGroupRef() {
		return groupRef;
	}

	/**
	 * Checks whether this pathway element belongs to a group.
	 *
	 * @return true if and only if the group of this pathway element is effective.
	 */
	public boolean hasGroupRef() {
		return getGroupRef() != null;
	}

	/**
	 * Verifies if given parent group is new and valid. Sets the parent group of the
	 * pathway element. Adds this pathway element to the the pathwayElements list of
	 * the new parent group. If there is an old parent group, this pathway element
	 * is removed from its pathwayElements list.
	 * 
	 * @param groupRef the new parent group to set.
	 */
	public void setGroupRefTo(Group groupRef) {
		if (groupRef == null)
			throw new IllegalArgumentException("Invalid group.");
		unsetGroupRef(); // first unsets if necessary
		setGroupRef(groupRef);
		if (!groupRef.hasPathwayElement(this))
			groupRef.addPathwayElement(this);
	}

	/**
	 * Sets the parent group for this pathway element.
	 * 
	 * @param groupRef the given group to set.
	 */
	private void setGroupRef(Group groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	public void unsetGroupRef() {
		if (hasGroupRef()) {
			Group groupRef = getGroupRef();
			setGroupRef(null);
			if (groupRef.hasPathwayElement(this))
				groupRef.removePathwayElement(this);
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
	 * @param lineColor the color of a line.
	 * @throws IllegalArgumentException if color null.
	 */
	public void setLineColor(Color lineColor) {
		if (lineColor == null) {
			throw new IllegalArgumentException();
		} else {
			this.lineColor = lineColor;
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
	 * @param lineStyle the style of a line.
	 * @throws IllegalArgumentException if lineStyle null.
	 */
	public void setLineStyle(LineStyleType lineStyle) {
		if (lineStyle == null) {
			throw new IllegalArgumentException();
		}
		if (this.lineStyle != lineStyle) {
			this.lineStyle = lineStyle;
//			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
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
	 * @param lineWidth the width of a line.
	 * @throws IllegalArgumentException if lineWidth is a negative value.
	 */
	public void setLineWidth(double lineWidth) {
		if (lineWidth < 0) {
			throw new IllegalArgumentException();
		} else {
			this.lineWidth = lineWidth;
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
	public void setConnectorType(ConnectorType connectorType) {
		if (connectorType == null) {
			throw new IllegalArgumentException();
		}
		if (!this.connectorType.equals(connectorType)) {
			this.connectorType = connectorType;
			// TODO
//			propChangeSupport.firePropertyChange("connectorType", this.connectorType, connectorType);
		}
	}

	/**
	 * Returns the order of a line.
	 * 
	 * @return zOrder the order of a line.
	 */
	public int getZOrder() {
		return zOrder;
	}

	/**
	 * Sets the order of a line.
	 * 
	 * @param zOrder the order of a line.
	 */
	public void setZOrder(int zOrder) {
		this.zOrder = zOrder;
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
		for (LinePoint point : points) // TODO
			pathwayModel.addPathwayElement(point);
		for (Anchor anchor : anchors) // TODO
			pathwayModel.addPathwayElement(anchor);
	}

	/**
	 * Unsets the pathway model, if any, from this pathway element. The pathway
	 * element no longer belongs to a pathway model. NB: This method is not used
	 * directly.
	 */
	protected void unsetPathwayModel() {
		if (hasPathwayModel()) {
			setPathwayModel(null);
			for (LinePoint point : points) // TODO
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
}
