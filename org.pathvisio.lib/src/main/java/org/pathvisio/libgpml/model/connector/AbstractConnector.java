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

/**
 * Abstract connector shape implementation that deals with cached shapes,
 * segments and waypoints. ConnectorShapes may implement this class and use the
 * setShape, setSegments and setWayPoints to refresh the cached shape.
 * 
 * @author thomas
 */
public abstract class AbstractConnector implements ConnectorShape {
	private Shape shape;
	private Segment[] segments;
	private WayPoint[] waypoints;

	/**
	 * Returns the connector shape.
	 * 
	 * @return shape the shape.
	 */
	@Override
	public Shape getShape() {
		return shape;
	}

	/**
	 * Calculates shape from the width of the line endings.
	 * 
	 * @param segments the array of segments.
	 * @return the calculated shape.
	 */
	abstract protected Shape calculateShape(Segment[] segments);

	/**
	 * Calculates shape from the width of the line endings.
	 *
	 * @param startGap the double.
	 * @param endGap   the double.
	 */
	@Override
	public Shape calculateAdjustedShape(double startGap, double endGap) {
		// gets the segments to local array
		Segment[] segments = getSegments();
		int numSegments = segments.length;
		Segment[] localsegments = new Segment[numSegments];

		for (int i = 0; i < segments.length; i++)
			localsegments[i] = new Segment(segments[i].getMStart(), segments[i].getMEnd());

		// co-ordinate calculations on start and end segments
		// make changes on the segment array downloaded
		Point2D adjustedLineEnd = segments[segments.length - 1].calculateNewEndPoint(endGap);
		localsegments[numSegments - 1] = new Segment(segments[numSegments - 1].getMStart(), adjustedLineEnd);

		// now for the first segment in the connector shape
		Point2D adjustedLineStart = segments[0].calculateNewStartPoint(startGap);
		localsegments[0] = new Segment(adjustedLineStart, localsegments[0].getMEnd());

		Shape adjustedShape = calculateShape(localsegments);

		return adjustedShape;
	}

	/**
	 * Sets the shape cache that will be returned by {@link #getShape()}.
	 * 
	 * @param shape the shape.
	 */
	protected void setShape(Shape shape) {
		this.shape = shape;
	}

	/**
	 * Returns the segments of the connector shape as an array.
	 * 
	 * @return segments the segment array.
	 */
	@Override
	public Segment[] getSegments() {
		return segments;
	}

	/**
	 * Sets the segment cache that will be returned by {@link #getSegments()}.
	 * 
	 * @param segments the segment array.
	 */
	protected void setSegments(Segment[] segments) {
		this.segments = segments;
	}

	/**
	 * Returns the waypoints of the connector shape as an array.
	 * 
	 * @return waypoints the waypoint array.
	 */
	@Override
	public WayPoint[] getWayPoints() {
		return waypoints;
	}

	/**
	 * Sets the waypoints cache that will be returned by {@link #getWayPoints()}.
	 * 
	 * @param waypoints the waypoint array.
	 */
	public void setWayPoints(WayPoint[] waypoints) {
		this.waypoints = waypoints;
	}
}
