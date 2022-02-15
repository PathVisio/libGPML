package org.pathvisio.libgpml.model;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.model.DataNode;
import org.pathvisio.libgpml.model.Group;
import org.pathvisio.libgpml.model.Interaction;
import org.pathvisio.libgpml.model.PathwayModel;
import org.pathvisio.libgpml.model.DataNode.State;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.GroupType;

import junit.framework.TestCase;

/**
 * Test for {@link Group}.
 * 
 * @author finterly
 */
public class TestGroup extends TestCase {

	PathwayModel p;
	Group g1;
	Group g2;
	DataNode d1;
	DataNode d2;
	DataNode d3;

	/**
	 * Creates and adds anchor to line, line to pathwayModel.
	 */
	@Before
	public void setUp() {

		p = new PathwayModel();
		g1 = new Group(GroupType.GROUP);
		d1 = new DataNode("d1", DataNodeType.UNDEFINED);
		p.addGroup(g1);
		p.addDataNode(d1);
		g1.addPathwayElement(d1);
		
		State st = d1.addState("string", "string", null, 1, 1);
		assertFalse(g1.hasPathwayElement(st));
		assertEquals(st.getGroupRef(), g1);
		assertTrue(d1.hasState(st));
		assertTrue(p.hasPathwayObject(st));

		d1.removeState(st);
		assertFalse(g1.hasPathwayElement(st));
		assertEquals(st.getGroupRef(), g1);
		assertFalse(d1.hasState(st));
		assertFalse(p.hasPathwayObject(st));

		assertTrue(p.hasPathwayObject(g1));
		assertTrue(p.hasPathwayObject(d1));
		assertTrue(p.getGroups().contains(g1));
		assertTrue(p.getDataNodes().contains(d1));
		assertTrue(g1.hasPathwayElement(d1));
		assertEquals(g1.getPathwayModel(), p);
		assertEquals(d1.getPathwayModel(), p);
		assertEquals(d1.getGroupRef(), g1);

		g2 = new Group(GroupType.GROUP);
		d2 = new DataNode("d2", DataNodeType.UNDEFINED);
		d3 = new DataNode("d3", DataNodeType.UNDEFINED);
		p.addGroup(g2);
		p.addDataNode(d2);
		p.addDataNode(d3);
		g2.addPathwayElement(d2);
		g2.addPathwayElement(d3);

		assertTrue(p.hasPathwayObject(g2));
		assertTrue(p.hasPathwayObject(d2));
		assertTrue(p.hasPathwayObject(d3));
		assertTrue(g2.hasPathwayElement(d2));
		assertTrue(g2.hasPathwayElement(d3));
		assertEquals(g2.getPathwayModel(), p);
		assertEquals(d2.getPathwayModel(), p);
		assertEquals(d3.getPathwayModel(), p);
		assertEquals(d2.getGroupRef(), g2);
		assertEquals(d3.getGroupRef(), g2);

	}

	/**
	 * 
	 */
	@Test
	public void testRemoveDataNode() {
		// terminates data node and empty group
		p.removeDataNode(d1);
		assertFalse(p.hasPathwayObject(g1));
		assertFalse(p.hasPathwayObject(d1));
		assertFalse(p.getGroups().contains(g1));
		assertFalse(p.getDataNodes().contains(d1));
		assertFalse(g1.hasPathwayElement(d1));
		assertNull(g1.getPathwayModel());
		assertNull(d1.getPathwayModel());
		assertNull(d1.getGroupRef());

		// terminates data node, group remains
		p.removeDataNode(d2);
		assertTrue(p.hasPathwayObject(g2));
		assertFalse(p.hasPathwayObject(d2));
		assertTrue(p.hasPathwayObject(d3));
		assertFalse(g2.hasPathwayElement(d2));
		assertTrue(g2.hasPathwayElement(d3));
		assertEquals(g2.getPathwayModel(), p);
		assertNull(d2.getPathwayModel());
		assertEquals(d3.getPathwayModel(), p);
		assertNull(d2.getGroupRef());
		assertEquals(d3.getGroupRef(), g2);
	}

	/**
	 * 
	 */
	@Test
	public void testRemoveDataNodeFromGroup() {

		// removes data node from group, terminates group, data node remains
		g1.removePathwayElement(d1);
		assertFalse(p.hasPathwayObject(g1));
		assertTrue(p.hasPathwayObject(d1));
		assertFalse(p.getGroups().contains(g1));
		assertTrue(p.getDataNodes().contains(d1));
		assertFalse(g1.hasPathwayElement(d1));
		assertNull(g1.getPathwayModel());
		assertEquals(d1.getPathwayModel(), p);
		assertNull(d1.getGroupRef());

		// removes data node from group, group and data node remains
		g2.removePathwayElement(d2);
		assertTrue(p.hasPathwayObject(g2));
		assertTrue(p.hasPathwayObject(d2));
		assertTrue(p.hasPathwayObject(d3));
		assertFalse(g2.hasPathwayElement(d2));
		assertTrue(g2.hasPathwayElement(d3));
		assertEquals(g2.getPathwayModel(), p);
		assertEquals(d2.getPathwayModel(), p);
		assertEquals(d3.getPathwayModel(), p);
		assertNull(d2.getGroupRef());
		assertEquals(d3.getGroupRef(), g2);

		// removes data node from group, group terminates, and data nodes remain
		g2.removePathwayElement(d3);
		assertFalse(p.hasPathwayObject(g2));
		assertTrue(p.hasPathwayObject(d2));
		assertTrue(p.hasPathwayObject(d3));
		assertFalse(g2.hasPathwayElement(d2));
		assertFalse(g2.hasPathwayElement(d3));
		assertNull(g2.getPathwayModel());
		assertEquals(d2.getPathwayModel(), p);
		assertEquals(d3.getPathwayModel(), p);
		assertNull(d2.getGroupRef());
		assertNull(d3.getGroupRef());
	}

	/**
	 * 
	 */
	@Test
	public void testRemoveGroup() {
		// terminates group, data nodes remain
		p.removeGroup(g1);
		assertFalse(p.hasPathwayObject(g1));
		assertTrue(p.hasPathwayObject(d1));
		assertFalse(p.getGroups().contains(g1));
		assertTrue(p.getDataNodes().contains(d1));
		assertFalse(g1.hasPathwayElement(d1));
		assertNull(g1.getPathwayModel());
		assertEquals(d1.getPathwayModel(), p);
		assertNull(d1.getGroupRef());

		// terminates group, data nodes remain
		p.removeGroup(g2);
		assertFalse(p.hasPathwayObject(g2));
		assertTrue(p.hasPathwayObject(d2));
		assertTrue(p.hasPathwayObject(d3));
		assertFalse(g2.hasPathwayElement(d2));
		assertFalse(g2.hasPathwayElement(d3));
		assertNull(g2.getPathwayModel());
		assertEquals(d2.getPathwayModel(), p);
		assertEquals(d3.getPathwayModel(), p);
		assertNull(d2.getGroupRef());
		assertNull(d3.getGroupRef());
	}

	/**
	 * 
	 */
	@Test
	public void testSwitchGroup() {
		d1.setGroupRefTo(g2);
		assertFalse(p.hasPathwayObject(g1));
		assertTrue(p.hasPathwayObject(g2));
		assertTrue(p.hasPathwayObject(d1));
		assertTrue(p.hasPathwayObject(d2));
		assertTrue(p.hasPathwayObject(d3));
		assertTrue(g2.hasPathwayElement(d1));
		assertTrue(g2.hasPathwayElement(d2));
		assertTrue(g2.hasPathwayElement(d3));
		assertNull(g1.getPathwayModel());
		assertEquals(g2.getPathwayModel(), p);
		assertEquals(d1.getPathwayModel(), p);
		assertEquals(d2.getPathwayModel(), p);
		assertEquals(d3.getPathwayModel(), p);
		assertEquals(d1.getGroupRef(), g2);
		assertEquals(d2.getGroupRef(), g2);
		assertEquals(d3.getGroupRef(), g2);
	}

	/**
	 * Check that when a line points to the group, it stays at the same position
	 * when the group disappears. Test for regression of bug #1058
	 * 
	 * @author unknown, finterly
	 */
	@Test
	public void testUngroup() {
		PathwayModel pwy = new PathwayModel();
		Interaction line = new Interaction(null);
		Group group = new Group(null);
		DataNode node = new DataNode("d1", DataNodeType.UNDEFINED);
		DataNode node2 = new DataNode("d2", DataNodeType.UNDEFINED);
		pwy.addInteraction(line);
		pwy.addDataNode(node);
		pwy.addDataNode(node2);
		pwy.addGroup(group);

		assertNotNull(group.getElementId());
		assertEquals(0, group.getPathwayElements().size());

		// add nodes to group
		node.setGroupRefTo(group);
		node2.setGroupRefTo(group);

		// check that now it's really part of group
		assertEquals(2, group.getPathwayElements().size());
		assertTrue(group.getPathwayElements().contains(node));

		// manipulate data node size
		node.setCenterX(120);
		node.setCenterY(20);
		node.setWidth(20);
		node.setHeight(20);
		node2.setCenterX(120);
		node2.setCenterY(20);
		node2.setWidth(10);
		node2.setHeight(10);

		// move point to "top-center" of group.
		line.setEndLinePointX(group.getCenterX());
		line.setEndLinePointY(group.getTop());
		// link point and group. relX and relY should be 0.0 and -1.0.
		line.getEndLinePoint().linkTo(group);
		assertEquals(line.getEndLinePoint().getElementRef(), group);

		assertEquals(8.0, group.getMargin());
	
		assertEquals(120.0, line.getEndLinePointX(), 0.01);
		assertEquals(2.0, line.getEndLinePointY(), 0.01);

		// remove group completely
		pwy.remove(group);
		
		System.out.println(line.getEndLinePointX());
		// check that line points at same position
		assertEquals(120.0, line.getEndLinePointX());
		assertEquals(2.0, line.getEndLinePointY());

		assertNull(line.getEndLinePoint().getElementRef());
		assertNull(node.getGroupRef());
	}

}
