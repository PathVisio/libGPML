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
	 * Unsets the parent group, if any, from this pathway element.
	 */
	public void unsetGroupRef();

	/**
	 * Returns the rectangular bounds of the object after rotation is applied.
	 *
	 * @return rectangle2d the rectangular bounds.
	 */
	public Rectangle2D getRotatedBounds();

	/**
	 * Get the rectangular bounds of the object without rotation taken into account.
	 *
	 * @return rectangle2d the rectangular bounds.
	 */
	public Rectangle2D getBounds();

	/**
	 * Returns the center x coordinate of the bounding box around (start, end) this
	 * pathway element.
	 *
	 * @return the center x coordinate.
	 */
	public double getCenterX();

	/**
	 * Sets the x position of the center of the pathway element.
	 *
	 * @param v the center x coordinate to set.
	 */
	public void setCenterX(double v);

	/**
	 * Returns the center y coordinate of the bounding box around (start, end) this
	 * pathway element.
	 *
	 * @return the center y coordinate
	 */
	public double getCenterY();

	/**
	 * Sets the y position of the center of the pathway element.
	 *
	 * @param v the center y coordinate to set.
	 */
	public void setCenterY(double v);

	/**
	 * Calculates and returns the width of the bounding box around (start, end) this
	 * pathway element.
	 *
	 * @return width the width.
	 */
	public double getWidth();

	/**
	 * Calculates and returns the height of the bounding box around (start, end)
	 * this pathway element.
	 *
	 * @return height the height.
	 */
	public double getHeight();

	/**
	 * Returns the left x coordinate of the bounding box around (start, end) this
	 * pathway element.
	 *
	 * @return left the left x coordinate.
	 */
	public double getLeft();

	/**
	 * Sets the position of the left side of the rectangular bounds of the pathway
	 * element.
	 *
	 * @param v the left x coordinate to set.
	 */
	public void setLeft(double v);

	/**
	 * Returns the top y coordinate of the bounding box around (start, end) this
	 * pathway element.
	 *
	 * @return the top y coordinate.
	 */
	public double getTop();

	/**
	 * Sets the position of the top side of the rectangular bounds of the pathway
	 * element.
	 *
	 * @param v the top y coordinate to set.
	 */
	public void setTop(double v);

	/**
	 * Converts a point to pathway coordinates (relative to the pathway).
	 *
	 * @param p the point2d.
	 * @return the absolute point2d coordinate.
	 */
	@Override
	public Point2D toAbsoluteCoordinate(Point2D p);

	/**
	 * Converts a point to shape coordinates (relative to the bounds of the object)
	 *
	 * @param p a point in absolute model coordinates
	 * @return the same point relative to the bounding box of this pathway element:
	 *         -1,-1 meaning the top-left corner, 1,1 meaning the bottom right
	 *         corner, and 0,0 meaning the center.
	 */
	@Override
	public Point2D toRelativeCoordinate(Point2D p);

}