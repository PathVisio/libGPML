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

import org.pathvisio.model.LineElement.LinePoint;

import junit.framework.TestCase;

/**
 * Test for {@link LinePoint}.
 * 
 * @author finterly
 */
public class TestLinePoint extends TestCase {

//	/**
//	 * Create a {@link LineElement}, add Line to pathway model. Then create and add
//	 * {@link LinePoint} to LineElement. TODO Line must have Points?
//	 */
//	public static void testLineThenPoint() {
//		System.out.println("TEST 1");
//
//		// create a pathway model
//		PathwayModel p = new PathwayModel();
//		Interaction i1 = new Interaction(null);
//		p.addInteraction(i1);
//
//		System.out.println(i1.getLinePoints().size());
//		System.out.println(i1.getLinePoints());
//
//		LinePoint pt1 = i1.new LinePoint(ArrowHeadType.CATALYSIS, 2, 2);
//		i1.setEndLinePoint(pt1);
//		System.out.println(pt1.getPathwayModel());
//		System.out.println(pt1.getLineElement());
//
//		LinePoint home = i1.getEndLinePoint();
//		System.out.println("Contains " + p.getPathwayObject(pt1.getElementId()));
//		System.out.println("Contains " + p.getPathwayObject(home.getElementId()));
//
//		System.out.println(i1.getLinePoints().size());
//		System.out.println(i1.getLinePoints());
//
////		// create a point
////		LinePoint pt1 = i1.addLinePoint(null, 0, 0);
////		assertNull(pt1.getElementId());
////		assertNull(pt1.getLineElement());
////		assertNull(pt1.getPathwayModel());
////		assertTrue(i1.getLinePoints().isEmpty());
////		// add state to data node (which also adds to pathway model)
////		i1.addLinePoint(pt1);
////		assertEquals(pt1.getLineElement(), i1);
////		assertEquals(pt1.getPathwayModel(), p1);
////		System.out.println("Point elementId is " + pt1.getElementId());
////		System.out.println("Interaction contains Points " + i1.getLinePoints());
////		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
////
////		// remove point
////		i1.removeLinePoint(pt1);
////		System.out.println("Interaction contains Points " + i1.getLinePoints());
////		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
////
////		// remove line element
////		p1.removeInteraction(i1);
////		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
////		System.out.println("PathwayModel contains Interactions " + p1.getInteractions());
//	}

	// FROM GROUP 
	public static void testLinkTo() {

		PathwayModel pwy = new PathwayModel();
		Interaction line = new Interaction(null);
		DataNode node = new DataNode("d1", null);
		pwy.addInteraction(line);
		pwy.addDataNode(node);

		List<LinePoint> points = new ArrayList<LinePoint>();
		points.add(line.new LinePoint(null, 9, 18));
		points.add(line.new LinePoint(null, 9, 18));
		line.setLinePoints(points);

		// manipulate data node size
		node.setCenterX(120);
		node.setCenterY(20);
		node.setWidth(20);
		node.setHeight(20);

		// move point to "top-center" of group.
		line.setEndLinePointX(node.getCenterX());
		line.setEndLinePointY(node.getTop());
		// link point and group. relX and relY should be 0.0 and -1.0.
		line.getEndLinePoint().linkTo(node);
		assertEquals(line.getEndLinePoint().getElementRef(), node);


		assertEquals(120.0, line.getEndLinePointX(), 0.01);
		assertEquals(10.0, line.getEndLinePointY(), 0.01);
		System.out.println("RelXY:  " + line.getEndLinePoint().getRelX() + "    " + line.getEndLinePoint().getRelY());
		System.out.println("XY:  " + line.getEndLinePoint().getX() + "    " + line.getEndLinePoint().getY());

		// ungroup
		pwy.remove(node);
		System.out.println("RelXY:  " + line.getEndLinePoint().getRelX() + "    " + line.getEndLinePoint().getRelY());
		System.out.println("XY:  " + line.getEndLinePoint().getX() + "    " + line.getEndLinePoint().getY());
		// check that line points at same position
		assertEquals(120.0, line.getEndLinePointX());
		assertEquals(10.0, line.getEndLinePointY());

		assertNull(line.getEndLinePoint().getElementRef());
		assertNull(node.getGroupRef());
	}
//	/**
//	 * Create a {@link LineElement}. Create and add {@link LinePoint} to
//	 * LineElement. Add both to Pathway model.
//	 */
//	public static void testDataNodeState() {
//		System.out.println("TEST 2");
//
//		// create a pathway model
//		PathwayModel p2 = new PathwayModel();
//		assertTrue(p2.getInteractions().isEmpty());
//		// create an interaction
//		Interaction i2 = new Interaction(null);
//		assertNull(i2.getElementId());
//		assertNull(i2.getPathwayModel());
//
//		// create a point
//		LinePoint pt2 = i2.addLinePoint(null, 0, 0);
//		assertNull(pt2.getElementId());
//		assertNull(pt2.getLineElement());
//		assertNull(pt2.getPathwayModel());
//		assertTrue(i2.getLinePoints().isEmpty());
//		// add point to interaction
//		i2.addLinePoint(pt2);
//		assertNull(pt2.getElementId());
//		assertNull(pt2.getPathwayModel());
//		assertEquals(pt2.getLineElement(), i2);
//		assertNull(i2.getPathwayModel());
//		System.out.println("Interaction contains Points " + i2.getLinePoints());
//
//		// add interaction to pathway model
//		p2.addInteraction(i2);
//		System.out.println("Interaction elementId is " + i2.getElementId());
//		System.out.println("Point elementId is " + pt2.getElementId());
//		System.out.println("PathwayModel contains PathwayElements " + p2.getPathwayObjects());
//		System.out.println("PathwayModel contains Interactions " + p2.getDataNodes());
//
//		// remove point
//		i2.removeLinePoint(pt2);
//		System.out.println("DataNode contains Points " + i2.getLinePoints());
//		System.out.println("PathwayModel contains PathwayElements " + p2.getPathwayObjects());
//
//		// remove interaction
//		p2.removeInteraction(i2);
//		System.out.println("PathwayModel contains PathwayElements " + p2.getPathwayObjects());
//		System.out.println("PathwayModel contains Interactions " + p2.getDataNodes());
//	}
}