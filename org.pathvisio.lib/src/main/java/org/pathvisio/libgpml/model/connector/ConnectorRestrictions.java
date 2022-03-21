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
package org.pathvisio.libgpml.model.connector;

import java.awt.Shape;
import java.awt.geom.Point2D;

import org.pathvisio.libgpml.model.connector.ConnectorShape.WayPoint;

/**
 * Methods to provide restrictions for the connector path
 * 
 * @author unknown
 */
public interface ConnectorRestrictions {

	/** line is connected to a PathwayElement on it's NORTH side. */
	public final static int SIDE_NORTH = 0;
	/** line is connected to a PathwayElement on it's EAST side. */
	public final static int SIDE_EAST = 1;
	/** line is connected to a PathwayElement on it's SOUTH side. */
	public final static int SIDE_SOUTH = 2;
	/** line is connected to a PathwayElement on it's WEST side. */
	public final static int SIDE_WEST = 3;

	/**
	 * Checks if the connector may cross this point Optionally, returns a shape that
	 * defines the boundaries of the area around this point that the connector may
	 * not cross. This method can be used for advanced connectors that route along
	 * other objects on the drawing
	 * 
	 * @param point the point to check.
	 * @return A shape that defines the boundaries of the area around this point
	 *         that the connector may not cross. Returning null is allowed for
	 *         implementing classes.
	 */
	Shape mayCross(Point2D point);

	/**
	 * Returns the side of the object to which the start of the connector connects
	 * 
	 * @return The side, one of the SIDE_* constants
	 */
	int getStartSide();

	/**
	 * Returns the side of the object to which the end of the connector connects
	 * 
	 * @return The side, one of the SIDE_* constants
	 */
	int getEndSide();

	/**
	 * Returns the start point to which the connector must connect
	 * 
	 * @return the start point.
	 */
	Point2D getStartPoint2D();

	/**
	 * Returns the end point to which the connector must connect
	 * 
	 * @return the end point.
	 */
	Point2D getEndPoint2D();

	/**
	 * Returns the preferred waypoints, to which the connector must draw it's path.
	 * The waypoints returned by this method are preferences and the connector shape
	 * may decide not to use them if they are invalid.
	 */
	WayPoint[] getWayPointPreferences();
}