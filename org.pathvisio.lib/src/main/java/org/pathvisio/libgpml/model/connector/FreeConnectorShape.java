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

import java.awt.geom.Point2D;

/**
 * Implements a connector that draws straight lines between multiple waypoints.
 * In contrast to the automatic connectors (Elbow and Curved), the waypoints can
 * be added/removed freely by the user.
 * 
 * @author leon
 */
public class FreeConnectorShape extends SegmentedConnector {

	/**
	 * Constructor for free connector shape.
	 */
	public FreeConnectorShape() {
		super();
	}

	/**
	 * Forces the connector to redraw it's path. The cache for segments, waypoints
	 * and shape.
	 * 
	 * @param restrictions the ConnectorRestrictions that provides the start, end
	 *                     and preferred waypoints
	 */
	@Override 
	public void recalculateShape(ConnectorRestrictions restrictions) {
		WayPoint[] wps = restrictions.getWayPointPreferences();
		setSegments(calculateSegments(restrictions, wps));
		setWayPoints(wps);
		setShape(calculateShape(getSegments()));
	}

	/**
	 * Checks if number of waypoints matches number of segments.
	 * 
	 * @param restrictions the connector restrictions.
	 * @return true if number of waypoints matches number of segments, false
	 *         otherwise.
	 */
	public boolean hasValidWaypoints(ConnectorRestrictions restrictions) {
		// Only check if number of waypoints matches number of segments
		return getSegments() != null && getNrSegments(restrictions) == getSegments().length;
	}

	/**
	 * Returns the number of segments given connector restrictions.
	 * 
	 * @param restrictions the connector restrictions.
	 * @return the number of segments.
	 */
	private int getNrSegments(ConnectorRestrictions restrictions) {
		return restrictions.getWayPointPreferences().length + 1;
	}

	/**
	 * Calculates segments given restrictions and waypoints.
	 * 
	 * @param restrictions the connector restrictions.
	 * @param waypoints    the waypoint array
	 * @return the array of segments.
	 */
	protected Segment[] calculateSegments(ConnectorRestrictions restrictions, WayPoint[] waypoints) {
		Segment[] segments = new Segment[waypoints.length + 1];
		Point2D start = restrictions.getStartPoint2D();
		Point2D end = restrictions.getEndPoint2D();

		if (segments.length == 1) {
			// no waypoints, behave like StraightConnectorShape
			segments[0] = new Segment(start, end);
		} else {
			segments[0] = new Segment(start, waypoints[0]);
			for (int i = 1; i < segments.length - 1; i++) {
				segments[i] = new Segment(segments[i - 1].getMEnd(), waypoints[i]);
			}
			segments[segments.length - 1] = new Segment(waypoints[waypoints.length - 1], end);
		}
		return segments;
	}
}