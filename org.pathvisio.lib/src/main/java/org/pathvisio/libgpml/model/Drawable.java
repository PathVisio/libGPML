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

import org.pathvisio.libgpml.model.LineElement.Anchor;
import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.model.type.ObjectType;

/**
 * Interface for classes which can be drawn a pathway model canvas. This class
 * is extended by {@link Groupable} and is implemented by {@link LinePoint} and
 * {@link Anchor}.
 *
 * NB: Groupable is implemented by ShapedElement and LineElement.
 *
 * @author finterly
 */
public interface Drawable {

	/**
	 * Returns the pathway model of this pathway element.
	 *
	 * @return the pathway model of this pathway element.
	 */
	public PathwayModel getPathwayModel();

	/**
	 * Returns the object type of this pathway element.
	 *
	 * @return objectType the object type.
	 */
	public ObjectType getObjectType();

	/**
	 * Returns the z-order of this pathway element.
	 *
	 * @return zOrder the order of this pathway element.
	 */
	public int getZOrder();

	/**
	 * Sets the z-order of this pathway element.
	 *
	 * @param v the order of this pathway element.
	 */
	public void setZOrder(int v);

	/**
	 * Converts a point to pathway coordinates (relative to the pathway).
	 *
	 * @param p the point2d.
	 * @return the absolute point2d coordinate.
	 */
	public Point2D toAbsoluteCoordinate(Point2D p);

	/**
	 * Converts a point to shape coordinates (relative to the bounds of the
	 * LinkableTo)
	 *
	 * @param p a point in absolute model coordinates
	 * @return the same point relative to the bounding box of this pathway element:
	 *         -1,-1 meaning the top-left corner, 1,1 meaning the bottom right
	 *         corner, and 0,0 meaning the center.
	 */
	public Point2D toRelativeCoordinate(Point2D p);

}