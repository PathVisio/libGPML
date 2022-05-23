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

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.model.DataNode.State;
import org.pathvisio.libgpml.model.type.DataNodeType;

import junit.framework.TestCase;

/**
 * Test for {@link State}.
 * <p>
 * NB:
 * <ol>
 * <li>For a state, getPathwayModel() is only null when outer class data node
 * terminated
 * <li>For a state, getDataNode() will always return outer class data node.
 * </ol>
 * 
 * @author finterly
 */
public class TestState extends TestCase {

	private PathwayModel p;
	private DataNode d1;
	private State st1;
	private State st2;
	private State st3;

	@Before
	public void setUp() throws Exception {
		p = new PathwayModel();
		d1 = new DataNode("d1", DataNodeType.UNDEFINED);
		p.addDataNode(d1);
		assertTrue(p.hasPathwayObject(d1));
		assertTrue(p.getDataNodes().contains(d1));
		assertEquals(d1.getPathwayModel(), p);

		// add states
		st1 = d1.addState("st1", null, 0, 0);
		st2 = d1.addState("st2", null, 0, 0);
		st3 = d1.addState("st2", null, 0, 0);
		assertTrue(d1.hasState(st1));
		assertTrue(p.hasPathwayObject(st1));
		assertEquals(st1.getDataNode(), d1);
		assertEquals(st1.getPathwayModel(), p);
	}

	/**
	 * Tests removing a state.
	 * 
	 */
	@Test
	public void testRemoveState() {
		d1.removeState(st3);
		assertFalse(d1.hasState(st3));
		assertFalse(p.hasPathwayObject(st3));
		assertNull(st3.getPathwayModel());
	}

	/**
	 * Tests removing a data node and its effect on states.
	 */
	@Test
	public void testRemoveDataNode() {
		p.removeDataNode(d1);
		assertTrue(d1.getStates().isEmpty());
		assertTrue(p.getDataNodes().isEmpty());
		assertTrue(p.getPathwayObjects().isEmpty());
		assertNull(d1.getPathwayModel());
		assertNull(st1.getPathwayModel());
		assertNull(st2.getPathwayModel());
	}

}