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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * This abstract class stores information for a Line pathway element, e.g.
 * GraphicalLine or Interaction.
 * 
 * @author finterly
 */
public abstract class LineElement extends ElementInfo {

	private List<Point> points;
	private List<Anchor> anchors = new ArrayList<Anchor>();
	private LineStyleProperty lineStyleProperty;

	/*
	 * The parent group to which the line belongs. In GPML, groupRef refers to the
	 * elementId (formerly groupId) of the parent gpml:Group.
	 */
	private Group groupRef; // optional

//	private List<MPoint> mPoints = Arrays.asList(new Point(), new Point());

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
