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
import java.util.Arrays;

import org.pathvisio.libgpml.model.Interaction;
import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.model.connector.ConnectorShape.Segment;

import junit.framework.TestCase;

/**
 * Test for segmented connector. 
 * 
 * @author unknown
 *
 */
public class TestSegmentedConnector extends TestCase {
	
	/** test conversion to / from line coordinates */
	public void testLineCoordinates() {
		Interaction line = new Interaction();
		LinePoint[] points = new LinePoint[] { line.new LinePoint(75.0, 125.0),
				line.new LinePoint(75.0, 50.0),
				line.new LinePoint(50.0, 50.0),
				line.new LinePoint(25.0, 25.0) };
		line.setLinePoints(Arrays.asList(points));

		assertEquals(75.0, line.getStartLinePointX());
		assertEquals(125.0, line.getStartLinePointY());
		assertEquals(25.0, line.getEndLinePointX());
		assertEquals(25.0, line.getEndLinePointY());
		assertEquals(4, line.getLinePoints().size());

		FreeConnectorShape con = new FreeConnectorShape();
		con.recalculateShape(line);
		Segment[] segments = con.getSegments();

		assertEquals(3, segments.length);
		assertEquals(135.36, con.getTotalLength(segments), 0.01);

		Point2D v = con.fromLineCoordinate(0.5);
		Point2D w = con.fromLineCoordinate(0.628);

		assertEquals(75.0, v.getX(), 0.01);
		assertEquals(57.32, v.getY(), 0.01);
		assertEquals(65.0, w.getX(), 0.01);
		assertEquals(50.0, w.getY(), 0.01);

		assertEquals(0.5, con.toLineCoordinate(v), 0.01);
		assertEquals(0.628, con.toLineCoordinate(w), 0.01);
	}

}
