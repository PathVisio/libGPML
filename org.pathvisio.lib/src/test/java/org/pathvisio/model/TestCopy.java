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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.model.DataNode.State;
import org.pathvisio.model.LineElement.LinePoint;
import org.pathvisio.model.type.DataNodeType;
import org.pathvisio.model.type.StateType;

import junit.framework.TestCase;

/**
 * Test for Clone methods.
 * 
 * @author finterly
 */
public class TestCopy extends TestCase {

	private PathwayModel p;

	@Before
	public void setUp() throws Exception {
		p = new PathwayModel();
	}

	/**
	 * 
	 */
	@Test
	public void testCopyDataNode() {
		DataNode o1 = new DataNode("o1", DataNodeType.UNDEFINED);
		p.addDataNode(o1);
		State s1 = o1.addState("st1", StateType.UNDEFINED, 0, 0);

		DataNode o2 = o1.copy();
		State s2 = o2.getStates().get(0);	

		assertEquals(o1, o1);
		assertFalse(o1 == o2);
		assertEquals(s1.getDataNode(), o1);
		assertEquals(s2.getDataNode(), o2);
		assertFalse(s1 == s2);

		assertEquals(o1.getPathwayModel(), p);
		assertEquals(s1.getPathwayModel(), p);
		assertNull(o2.getPathwayModel());
		assertNull(s2.getPathwayModel());

		PathwayModel p2 = new PathwayModel();
		p2.addDataNode(o2);
		assertEquals(o2.getPathwayModel(), p2);
		for (State i : o2.getStates()) {
			assertEquals(i.getPathwayModel(), p2);
		}
	}

	/**
	 * 
	 */
	@Test
	public void testCopyInteraction() {
		Interaction o1 = new Interaction();
		p.addInteraction(o1);

		List<LinePoint> points = new ArrayList<LinePoint>();
		points.add(o1.new LinePoint(null, 9, 18));
		points.add(o1.new LinePoint(null, 9, 18));
		o1.setLinePoints(points);
		o1.addAnchor(0, null);

		Interaction o2 = o1.copy();
		assertEquals(o1, o1);
		assertFalse(o1 == o2);
		assertEquals(o1.getPathwayModel(), p);
		assertNull(o2.getPathwayModel());

		PathwayModel p2 = new PathwayModel();
		p2.addInteraction(o2);
		assertEquals(o2.getPathwayModel(), p2);
		for (LinePoint i : o2.getLinePoints()) {
			assertEquals(i.getPathwayModel(), p2);
		}
		assertEquals(o2.getAnchors().get(0).getPathwayModel(), p2);
	}

	/**
	 * 
	 */
	@Test
	public void testCopyGraphicalLine() {
		GraphicalLine o1 = new GraphicalLine();
		p.addGraphicalLine(o1);

		List<LinePoint> points = new ArrayList<LinePoint>();
		points.add(o1.new LinePoint(null, 9, 18));
		points.add(o1.new LinePoint(null, 9, 18));
		o1.setLinePoints(points);
		o1.addAnchor(0, null);

		GraphicalLine o2 = o1.copy();
		assertEquals(o1, o1);
		assertFalse(o1 == o2);
		assertEquals(o1.getPathwayModel(), p);
		assertNull(o2.getPathwayModel());

		PathwayModel p2 = new PathwayModel();
		p2.addGraphicalLine(o2);
		assertEquals(o2.getPathwayModel(), p2);
		for (LinePoint i : o2.getLinePoints()) {
			assertEquals(i.getPathwayModel(), p2);
		}
		assertEquals(o2.getAnchors().get(0).getPathwayModel(), p2);
	}
	
	/**
	 * 
	 */
	@Test
	public void testCopyLabel() {
		Label o1 = new Label("o1");
		p.addLabel(o1);

		Label o2 = o1.copy();

		assertEquals(o1, o1);
		assertFalse(o1 == o2);
		assertEquals(o1.getPathwayModel(), p);
		assertNull(o2.getPathwayModel());
	}

	/**
	 * 
	 */
	@Test
	public void testCopyShape() {
		Shape o1 = new Shape();
		p.addShape(o1);

		Shape o2 = o1.copy();

		assertEquals(o1, o1);
		assertFalse(o1 == o2);
		assertEquals(o1.getPathwayModel(), p);
		assertNull(o2.getPathwayModel());
	}

}