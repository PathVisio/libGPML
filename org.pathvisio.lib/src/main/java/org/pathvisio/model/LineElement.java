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
public abstract class LineElement extends PathwayElement {

	private Group parentGroup; // optional instead of groupRef
	private LineStyleProperty lineStyleProperty;
	private List<Point> points;
	private List<Anchor> anchors = new ArrayList<Anchor>();
	// TODO: No Type?

	/**
	 * Returns the groupRef of the pathway element. A groupRef indicates an object
	 * is part of a gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the datanode.
	 * 
	 */
	public String getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the pathway element. A groupRef indicates an object is
	 * part of a gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the datanode.
	 * 
	 */
	public void setGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

//	private List<MPoint> mPoints = Arrays.asList(new Point(), new Point());

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

	public void addPoint(Point point) {
		points.add(point);
	}

	public void removePoint(Point point) {
		points.remove(point);
	}

	public LineStyleProperty getLineStyleProperty() {
		return lineStyleProperty;
	}

	public void setLineStyleProperty(LineStyleProperty lineStyleProperty) {
		this.lineStyleProperty = lineStyleProperty;
	}

	/**
	 * Get the anchors for this line.
	 * 
	 * @return A list with the anchors, or an empty list, if no anchors are defined
	 */
	public List<Anchor> getAnchors() {
		return anchors;
	}

	public void addAnchors(Anchor anchor) {
		anchors.add(anchor);
	}

	public void removeAnchors(Anchor anchor) {
		anchors.remove(anchor);
	}

	public Group getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(Group parentGroup) {
		this.parentGroup = parentGroup;
	}
}
