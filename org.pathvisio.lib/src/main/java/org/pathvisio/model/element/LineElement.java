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
package org.pathvisio.model.element;

import java.util.ArrayList;
import java.util.List;

import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.graphics.LineStyleProperty;

/**
 * This abstract class stores information for a Line pathway element, e.g.
 * GraphicalLine or Interaction.
 * 
 * @author finterly
 */
public abstract class LineElement extends ElementInfo {

	private List<LinePoint> points; // minimum 2
	private List<Anchor> anchors;
	private LineStyleProperty lineStyleProperty;
	private Group groupRef; // optional, the parent group to which a pathway element belongs.

	/**
	 * Instantiates a line pathway element which is also a member of a group pathway
	 * element and thus has groupRef. In GPML, groupRef refers to the elementId
	 * (formerly groupId) of the parent gpml:Group. Note, a group can also belong in
	 * another group.
	 * 
	 * @param pathwayModel      the parent pathway model.
	 * @param elementId         the unique pathway element identifier.
	 * @param lineStyleProperty the line style properties, e.g. lineColor.
	 * @param groupRef          the parent group in which the pathway element
	 *                          belongs.
	 */
	public LineElement(PathwayModel pathwayModel, String elementId, LineStyleProperty lineStyleProperty,
			Group groupRef) {
		super(pathwayModel, elementId);
		this.points = new ArrayList<LinePoint>(); // should have at least two points
		this.anchors = new ArrayList<Anchor>();
		this.lineStyleProperty = lineStyleProperty;
		if (groupRef != null) {
			setGroupRef(groupRef); // set group TODO
		}
	}

	/**
	 * Instantiates a line pathway element given all possible parameters except
	 * groupRef, because the pathway element is not a member of a group.
	 */
	public LineElement(PathwayModel pathwayModel, String elementId, LineStyleProperty lineStyleProperty) {
		this(pathwayModel, elementId, lineStyleProperty, null);
	}

	/**
	 * Get the points for this line.
	 * 
	 * @return points the list of points, an empty list if no anchors are defined.
	 */
	public List<LinePoint> getPoints() {
		return points;
	}

	/**
	 * Adds given point to points list.
	 * 
	 * @param point the point to be added.
	 */
	public void addPoint(LinePoint point) {
		if (point.getLineElement() != this)
			point.setLineElement(this); // TODO
		if (point.getLineElement() == this && !points.contains(point))
			points.add(point); // TODO
	}

	/**
	 * Removes given point from the points list.
	 * 
	 * @param point the point to be removed.
	 */
	public void removePoint(LinePoint point) {
		if (point.getLineElement() == this && points.contains(point)) {
			point.setLineElement(null);
			points.remove(point);
		}
	}

	/**
	 * Removes all points from the points list.
	 */
	public void removePoints() {
		for (LinePoint point : points) {
			this.removePoint(point);
		}
	}

	/**
	 * Returns the start (first) point of points list. TODO necessary method?
	 * 
	 * @return the first point of points list.
	 */
	public LinePoint getStartPoint() {
		return points.get(0);
	}

	/**
	 * Returns the end (last) point of points list. TODO necessary method?
	 * 
	 * @return the last point of points list.
	 */
	public LinePoint getEndPoint() {
		return points.get(points.size() - 1);
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
	 * Adds given anchor to anchors list.
	 * 
	 * @param anchor the anchor to be added.
	 */
	public void addAnchor(Anchor anchor) {
		if (anchor.getLineElement() != this)
			anchor.setLineElement(this); // TODO
		if (anchor.getLineElement() == this && !anchors.contains(anchor))
			anchors.add(anchor); // TODO
	}

	/**
	 * Removes given anchor from the anchors list.
	 * 
	 * @param anchor the anchor to be removed.
	 */
	public void removeAnchor(Anchor anchor) {
		if (anchor.getLineElement() == this && anchors.contains(anchor)) {
			anchor.setLineElement(null);
			anchors.remove(anchor);
		}
	}

	/**
	 * Removes all anchors from the anchors list.
	 */
	public void removeAnchors() {
		for (Anchor anchor : anchors) {
			this.removeAnchor(anchor);
		}
	}

	/**
	 * Returns the line style properties of the pathway element, e.g. lineColor...
	 * 
	 * @return lineStyleProperty the line style properties.
	 */
	public LineStyleProperty getLineStyleProperty() {
		return lineStyleProperty;
	}

	/**
	 * Sets the line style properties of the pathway element, e.g. lineColor...
	 * 
	 * @param lineStyleProperty the line style properties.
	 */
	public void setLineStyleProperty(LineStyleProperty lineStyleProperty) {
		this.lineStyleProperty = lineStyleProperty;
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
	 * Verifies if given parent group is new and valid. Sets the parent group of the
	 * pathway element. Adds this pathway element to the the pathwayElements list of
	 * the new parent group. If there is an old parent group, this pathway element
	 * is removed from its pathwayElements list.
	 * 
	 * @param groupRefNew the new parent group to set.
	 */
	public void setGroupRef(Group newGroupRef) {
		if (groupRef == null || !groupRef.equals(newGroupRef)) {
			if (this.getPathwayModel() != null) { // TODO?
				// if necessary, removes link to existing parent group
				if (groupRef != null)
					this.groupRef.removePathwayElement(this);
				if (newGroupRef != null)
					newGroupRef.addPathwayElement(this);
			}
			groupRef = newGroupRef; // TODO
		}
	}
}
