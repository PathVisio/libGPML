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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.pathvisio.libgpml.util.LinAlg;
import org.pathvisio.libgpml.util.LinAlg.Point;

/**
 * Implements a straight connector Shape, i.e. a Connector with only 90-degree
 * angles.
 * 
 * @author unknown
 */
public class StraightConnectorShape extends AbstractConnector {

	/**
	 * Forces the connector to redraw it's path. The cache for segments, waypoints
	 * and shape.
	 * 
	 * @param restrictions the ConnectorRestrictions that provides the start, end
	 *                     and preferred waypoints
	 */
	@Override
	public void recalculateShape(ConnectorRestrictions restrictions) {
		setSegments(new Segment[] { new Segment(restrictions.getStartPoint2D(), restrictions.getEndPoint2D()) });
		setShape(new Line2D.Double(restrictions.getStartPoint2D(), restrictions.getEndPoint2D()));
		setWayPoints(new WayPoint[0]);
	}

	/**
	 * Calculates shape from the width of the line endings.
	 * 
	 * @param segments the array of segments.
	 * @return the calculated shape.
	 */
	@Override
	protected java.awt.Shape calculateShape(Segment[] segments) {
		Point2D start = segments[0].getMStart();
		Point2D end = segments[segments.length - 1].getMEnd();
		return (new Line2D.Double(start, end));
	}

	@Override
	public boolean hasValidWaypoints(ConnectorRestrictions restrictions) {
		return false;
	}

	/**
	 * Translates a 1-dimensional line coordinate to a 2-dimensional view
	 * coordinate. The 1-dimensional line coordinate is position objects that are
	 * attached to the line.
	 * 
	 * @param l the double.
	 */
	@Override
	public Point2D fromLineCoordinate(double l) {
		Segment[] segments = getSegments();
		Point2D start = segments[0].getMStart();
		Point2D end = segments[segments.length - 1].getMEnd();

		double vsx = start.getX();
		double vsy = start.getY();
		double vex = end.getX();
		double vey = end.getY();

		int dirx = vsx > vex ? -1 : 1;
		int diry = vsy > vey ? -1 : 1;

		return new Point2D.Double(vsx + dirx * Math.abs(vsx - vex) * l, vsy + diry * Math.abs(vsy - vey) * l);
	}

	/**
	 * Translates a 2-dimensional view coordinate to a 1-dimensional line
	 * coordinate.
	 * 
	 * @param v the Point2D
	 */
	@Override
	public double toLineCoordinate(Point2D v) {
		Segment[] segments = getSegments();
		return LinAlg.toLineCoordinates(new Point(segments[0].getMStart()),
				new Point(segments[segments.length - 1].getMEnd()), new Point(v));
	}
}
