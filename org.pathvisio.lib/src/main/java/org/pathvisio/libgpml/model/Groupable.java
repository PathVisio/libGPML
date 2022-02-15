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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Interface for classes which can belong in a {@link Group}. This class is
 * implemented by {@link ShapedElement} and {@link LineElement}.
 * 
 * @author finterly
 */
public interface Groupable extends Drawable {

	/**
	 * Returns the parent group of this pathway element. In GPML, groupRef refers to
	 * the elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of this pathway element.
	 */
	public Group getGroupRef();

	/**
	 * Checks whether this pathway element belongs to a group.
	 *
	 * @return true if and only if the group of this pathway element is effective.
	 */
	public boolean hasGroupRef();

	/**
	 * Verifies if given parent group is new and valid. Sets the parent group of the
	 * pathway element. Adds this pathway element to the the pathwayElements list of
	 * the new parent group. If there is an old parent group, this pathway element
	 * is removed from its pathwayElements list.
	 * 
	 * @param groupRef the new parent group to set.
	 */
	public void setGroupRefTo(Group groupRef);

	/**
	 * Sets the parent group for this pathway element.
	 * 
	 * @param groupRef the given group to set.
	 */
//	protected void setGroupRef(Group groupRef);

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	public void unsetGroupRef();

	/**
	 * Returns the rectangular bounds of the object after rotation is applied.
	 */
	public Rectangle2D getRotatedBounds();

	/**
	 * Get the rectangular bounds of the object without rotation taken into account.
	 */
	public Rectangle2D getBounds();

	/**
	 * Returns the center x coordinate of the bounding box around (start, end) this
	 * pathway element.
	 */
	public double getCenterX();

	/**
	 * Sets the x position of the center of the pathway element.
	 */
	public void setCenterX(double v);

	/**
	 * Returns the center y coordinate of the bounding box around (start, end) this
	 * pathway element.
	 */
	public double getCenterY();

	/**
	 * Sets the y position of the center of the pathway element.
	 */
	public void setCenterY(double v);

	/**
	 * Calculates and returns the width of the bounding box around (start, end) this
	 * pathway element.
	 */
	public double getWidth();

	/**
	 * Calculates and returns the height of the bounding box around (start, end)
	 * this pathway element.
	 */
	public double getHeight();

	/**
	 * Returns the left x coordinate of the bounding box around (start, end) this
	 * pathway element.
	 */
	public double getLeft();

	/**
	 * Sets the position of the left side of the rectangular bounds of the pathway
	 * element.
	 */
	public void setLeft(double v);

	/**
	 * Returns the top y coordinate of the bounding box around (start, end) this
	 * pathway element.
	 */
	public double getTop();

	/**
	 * Sets the position of the top side of the rectangular bounds of the pathway
	 * element.
	 */
	public void setTop(double v);

	/**
	 * @param p
	 * @return
	 */
	public Point2D toAbsoluteCoordinate(Point2D p);

	/**
	 * @param mp a point in absolute model coordinates
	 * @return the same point relative to the bounding box of this pathway element:
	 *         -1,-1 meaning the top-left corner, 1,1 meaning the bottom right
	 *         corner, and 0,0 meaning the center.
	 */
	public Point2D toRelativeCoordinate(Point2D mp);

}