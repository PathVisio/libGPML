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
package org.pathvisio.model.elements;

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

	private List<Point> points;
	private List<Anchor> anchors;
	private LineStyleProperty lineStyleProperty;

	/*
	 * The parent group to which the line belongs. In GPML, groupRef refers to the
	 * elementId (formerly groupId) of the parent gpml:Group.
	 */
	private Group groupRef; // optional

	/**
	 * Instantiates a line pathway element which is also a member of a group pathway
	 * element and thus has groupRef. In GPML, groupRef refers to the elementId
	 * (formerly groupId) of the parent gpml:Group. Note, a group can also belong in
	 * another group.
	 * 
	 * @param elementId         the unique pathway element identifier.
	 * @param pathwayModel      the parent pathway model.
	 * @param points            the list of points.
	 * @param anchors           the list of anchors.
	 * @param lineStyleProperty the line style properties, e.g. lineColor.
	 * @param groupRef          the parent group in which the pathway element
	 *                          belongs.
	 */
	public LineElement(String elementId, PathwayModel pathwayModel, List<Point> points, List<Anchor> anchors,
			LineStyleProperty lineStyleProperty, Group groupRef) {
		super(elementId, pathwayModel);
		this.points = new ArrayList<Point>();
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
	public LineElement(String elementId, PathwayModel pathwayModel, List<Point> points, List<Anchor> anchors,
			LineStyleProperty lineStyleProperty) {
		this(elementId, pathwayModel, new ArrayList<Point>(), new ArrayList<Anchor>(), lineStyleProperty, null);
	}

	/**
	 * Get the points for this line.
	 * 
	 * @return points the list of points, an empty list if no anchors are defined.
	 */
	public List<Point> getPoints() {
		return points;
	}

	// TODO needed?
	public void setPoints(List<Point> points) {
		if (points != null) {
			if (points.size() < 2) {
				throw new IllegalArgumentException("Points array should at least have two elements");
			}
			this.points = points;
		}
	}

	/**
	 * Adds given point to points list.
	 * 
	 * @param point the point to be added.
	 */
	public void addPoint(Point point) {
		points.add(point);
	}

	/**
	 * Removes given point from the points list.
	 * 
	 * @param point the point to be removed.
	 */
	public void removePoint(Point point) {
		points.remove(point);
	}

	// TODO necessary method?
	public Point getStartPoint() {
		return points.get(0);
	}

	// TODO necessary method?
	public Point getEndPoint() {
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
		anchors.add(anchor);
	}

	/**
	 * Removes given anchor from the anchors list.
	 * 
	 * @param anchor the anchor to be removed.
	 */
	public void removeAnchor(Anchor anchor) {
		anchors.remove(anchor);
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
	 * @return lineStyleProperty the line style properties.
	 */
	public void setLineStyleProperty(LineStyleProperty lineStyleProperty) {
		this.lineStyleProperty = lineStyleProperty;
	}

	/**
	 * Returns the parent group of the line. In GPML, groupRef refers to the
	 * elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of the line.
	 */
	public Group getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the parent group of the line. The group is added to the pathwayElements
	 * list of the parent group.
	 * 
	 * @param groupRef the parent group of the line.
	 */
	public void setGroupRef(Group groupRef) {
		groupRef.addPathwayElement(this);
		this.groupRef = groupRef;
	}
}
