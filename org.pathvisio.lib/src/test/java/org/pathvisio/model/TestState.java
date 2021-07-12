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

import org.pathvisio.model.element.DataNode;
import org.pathvisio.model.element.State;
import org.pathvisio.model.ref.Annotation;
import org.pathvisio.model.type.AnnotationType;

import junit.framework.TestCase;

/**
 * Test for {@link State}.
 * 
 * @author finterly
 */
public class TestState extends TestCase {

	/**
	 * Create a DataNode, add DataNode to pathway model. Then create and add State
	 * to DataNode.
	 */
	public static void testDataNodeThenState() {
		// create a pathway model
		PathwayModel p1 = new PathwayModel();
		assertTrue(p1.getDataNodes().isEmpty());

		// create a data node
		DataNode d1 = new DataNode(null, null, null, "d1", null);
		assertNull(d1.getElementId());
		assertNull(d1.getPathwayModel());
		// add to pathway model
		p1.addDataNode(d1);
		System.out.println("DataNode elementId is " + d1.getElementId());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());
		System.out.println("PathwayModel contains DataNodes " + p1.getDataNodes());
		assertTrue(p1.getPathwayElements().contains(d1));
		assertTrue(p1.getDataNodes().contains(d1));
		assertEquals(d1.getPathwayModel(), p1);

		// create a state
		State st1 = new State("st1", null, 0, 0, 0, 0, null, null);
		assertNull(st1.getElementId());
		assertNull(st1.getDataNode());
		assertNull(st1.getPathwayModel());
		assertTrue(d1.getStates().isEmpty());
		// add state to data node (which also adds to pathway model)
		d1.addState(st1);
		assertEquals(st1.getDataNode(), d1);
		assertEquals(st1.getPathwayModel(), p1);
		System.out.println("State elementId is " + st1.getElementId());
		System.out.println("DataNode contains States " + d1.getStates());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());

//		// remove state
//		d1.removeState(st1);
//		System.out.println("DataNode contains States " + d1.getStates());
//		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());

		//remove data node
		p1.removeDataNode(d1);
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());
		System.out.println("PathwayModel contains DataNodes " + p1.getDataNodes());


	}
}