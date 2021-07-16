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

import org.pathvisio.model.Interaction;
import org.pathvisio.model.LinePoint;
import org.pathvisio.model.PathwayModel;

import junit.framework.TestCase;

/**
 * Test for {@link LinePoint}.
 * 
 * @author finterly
 */
public class TestLinePoint extends TestCase {

	/**
	 * Create a {@link LineElement}, add Line to pathway model. Then create and add
	 * {@link LinePoint} to LineElement. TODO Line must have Points?
	 */
	public static void testLineThenPoint() {
		System.out.println("TEST 1");

		// create a pathway model
		PathwayModel p1 = new PathwayModel();
		assertTrue(p1.getInteractions().isEmpty());

		// create a line element, an interaction, for this test 
		Interaction i1 = new Interaction(null);
		assertNull(i1.getElementId());
		assertNull(i1.getPathwayModel());
		// add to pathway model
		p1.addInteraction(i1);
		System.out.println("Interaction elementId is " + i1.getElementId());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());
		System.out.println("PathwayModel contains DataNodes " + p1.getInteractions());
		assertTrue(p1.getPathwayElements().contains(i1));
		assertTrue(p1.getInteractions().contains(i1));
		assertEquals(i1.getPathwayModel(), p1);

		// create a point
		LinePoint pt1 = new LinePoint(null, null);
		assertNull(pt1.getElementId());
		assertNull(pt1.getLineElement());
		assertNull(pt1.getPathwayModel());
		assertTrue(i1.getPoints().isEmpty());
		// add state to data node (which also adds to pathway model)
		i1.addPoint(pt1);
		assertEquals(pt1.getLineElement(), i1);
		assertEquals(pt1.getPathwayModel(), p1);
		System.out.println("Point elementId is " + pt1.getElementId());
		System.out.println("Interaction contains Points " + i1.getPoints());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());

		// remove point
		i1.removePoint(pt1);
		System.out.println("Interaction contains Points " + i1.getPoints());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());

		// remove line element
		p1.removeInteraction(i1);
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());
		System.out.println("PathwayModel contains Interactions " + p1.getInteractions());
	}

	
	/**
	 * Create a {@link LineElement}. Create and add {@link LinePoint} to
	 * LineElement. Add both to Pathway model.
	 */
	public static void testDataNodeState() {
		System.out.println("TEST 2");

		// create a pathway model
		PathwayModel p2 = new PathwayModel();
		assertTrue(p2.getInteractions().isEmpty());
		// create an interaction
		Interaction i2 = new Interaction(null);
		assertNull(i2.getElementId());
		assertNull(i2.getPathwayModel());

		// create a point
		LinePoint pt2 = new LinePoint(null, null);
		assertNull(pt2.getElementId());
		assertNull(pt2.getLineElement());
		assertNull(pt2.getPathwayModel());
		assertTrue(i2.getPoints().isEmpty());
		// add point to interaction
		i2.addPoint(pt2);
		assertNull(pt2.getElementId());
		assertNull(pt2.getPathwayModel());
		assertEquals(pt2.getLineElement(), i2);
		assertNull(i2.getPathwayModel());
		System.out.println("Interaction contains Points " + i2.getPoints());

		// add interaction to pathway model
		p2.addInteraction(i2);
		System.out.println("Interaction elementId is " + i2.getElementId());
		System.out.println("Point elementId is " + pt2.getElementId());
		System.out.println("PathwayModel contains PathwayElements " + p2.getPathwayElements());
		System.out.println("PathwayModel contains Interactions " + p2.getDataNodes());

		// remove point
		i2.removePoint(pt2);
		System.out.println("DataNode contains Points " + i2.getPoints());
		System.out.println("PathwayModel contains PathwayElements " + p2.getPathwayElements());

		// remove interaction
		p2.removeInteraction(i2);
		System.out.println("PathwayModel contains PathwayElements " + p2.getPathwayElements());
		System.out.println("PathwayModel contains Interactions " + p2.getDataNodes());
	}
}