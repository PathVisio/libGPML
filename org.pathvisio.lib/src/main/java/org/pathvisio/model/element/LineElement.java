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
 * {@link GraphicalLine} or {@link Interaction}.
 * 
 * @author finterly
 */
public abstract class LineElement extends ElementInfo implements Groupable {

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
	 * @param lineStyleProperty the line style properties, e.g. lineColor.
	 * @param groupRef          the parent group in which the pathway element
	 *                          belongs.
	 */
	public LineElement(LineStyleProperty lineStyleProperty, Group groupRef) {
		super();
		this.points = new ArrayList<LinePoint>(); // should have at least two points
		this.anchors = new ArrayList<Anchor>();
		this.lineStyleProperty = lineStyleProperty;
		if (groupRef != null) {
			setGroupRefTo(groupRef); // set group TODO
		}
	}

	/**
	 * Instantiates a line pathway element given all possible parameters except
	 * groupRef, because the pathway element is not a member of a group.
	 */
	public LineElement(LineStyleProperty lineStyleProperty) {
		this(lineStyleProperty, null);
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
	 * Checks whether points has the given point.
	 * 
	 * @param point the point to look for.
	 * @return true if has point, false otherwise.
	 */
	public boolean hasPoint(LinePoint point) {
		return points.contains(point);
	}

	/**
	 * Adds given point to points list.
	 * 
	 * @param point the point to be added.
	 */
	public void addPoint(LinePoint point) {
		assert (point != null) && (point.getLineElement() == this);
		assert !hasPoint(point);
		points.add(point);
	}

	/**
	 * Removes given point from the points list.
	 * 
	 * @param point the point to be removed.
	 */
	public void removePoint(LinePoint point) {
		point.terminate();
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
	 * Checks whether anchors has the given anchor.
	 * 
	 * @param anchor the anchor to look for.
	 * @return true if has anchor, false otherwise.
	 */
	public boolean hasAnchor(Anchor anchor) {
		return anchors.contains(anchor);
	}

	/**
	 * Adds given anchor to anchors list.
	 * 
	 * @param anchor the anchor to be added.
	 */
	public void addAnchor(Anchor anchor) {
		assert (anchor != null) && (anchor.getLineElement() == this);
		assert !hasAnchor(anchor);
		anchors.add(anchor);
	}

	/**
	 * Removes given anchor from the anchors list.
	 * 
	 * @param anchor the anchor to be removed.
	 */
	public void removeAnchor(Anchor anchor) {
		anchor.terminate();
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
	 * @param groupRefNew the new parent group to set.
	 */
	public void setGroupRefTo(Group groupRef) {
		if (groupRef == null)
			throw new IllegalArgumentException("Invalid datanode.");
		if (hasGroupRef())
			throw new IllegalStateException("Line element already belongs to a group.");
		setGroupRef(groupRef);
		groupRef.addPathwayElement(this); // TODO
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
			Group formerGroupRef = this.getGroupRef();
			setGroupRef(null);
			formerGroupRef.removePathwayElement(this);
		}
	}

	/**
	 * Terminates this LineElement. The pathway model, if any, is unset from this
	 * anchor.Links to all annotationRefs, citationRefs, and evidenceRefs are
	 * removed from this data node.
	 */
	@Override
	public void terminate() {
		unsetPathwayModel();
		unsetGroupRef();
		removePoints();
		removeAnchors();
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();
	}
}
