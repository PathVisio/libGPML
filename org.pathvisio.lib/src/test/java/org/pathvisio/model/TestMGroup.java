/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2019 BiGCaT Bioinformatics
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

import org.pathvisio.model.DataNode;
import org.pathvisio.model.Group;
import org.pathvisio.model.Interaction;
import org.pathvisio.model.PathwayModel;

import junit.framework.TestCase;

/**
 * Test for Group class. 
 * 
 * @author unknown, finterly
 */
public class TestMGroup extends TestCase
{
	/**
	 * Check that when a line points to the group, it stays at the same position when the group disappears.
	 * Test for regression of bug #1058
	 */
	public void testUngroup()
	{
		PathwayModel pwy = new PathwayModel();
		Interaction line = new Interaction(null);
		
		Group group = new Group(null, null, null, null);
		DataNode node = new DataNode(null, null, null, "d1", null );
		pwy.addInteraction(line);
		pwy.addDataNode(node);
		pwy.addGroup(group);
		
		assertNotNull(group.getElementId());
		
		node.getRectProperty().getCenterXY().setX(120);
		node.getRectProperty().getCenterXY().setY(20);
		node.getRectProperty().setWidth(20);
		node.getRectProperty().setHeight(20);
		
		assertEquals (0, group.getPathwayElements().size());
		
		// add node to group
		node.setGroupRefTo(group);
		
		// check that now it's really part of group
		assertEquals (1, group.getPathwayElements().size());
		assertTrue (group.getPathwayElements().contains(node));
	
//		line.getPoints().get(line.getPoints().size()-1).getXY().setX(group.getMCenterX());
//		line..getPoints().get(line.getPoints().size()-1).getXY().setY(group.getMTop());
//		line.setEndGraphRef(group.getElementId());
//		assertEquals (line.getEndGraphRef(), group.getElementId());
//		
//		assertEquals (8.0, group.getGroupType().getMMargin());
//		
//		assertEquals (120.0, line.getMEndX(), 0.01);
//		assertEquals (2.0, line.getMEndY(), 0.01);
//		
//		// ungroup
//		pwy.remove(group);
//		
//		// check that line points at same position
//		assertEquals (120.0, line.getMEndX());
//		assertEquals (2.0, line.getMEndY());
//		assertNull (line.getEndGraphRef());
//		assertNull (node.getGroupRef());
	}
	
}
