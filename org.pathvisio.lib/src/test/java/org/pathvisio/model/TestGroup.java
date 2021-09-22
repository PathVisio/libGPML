package org.pathvisio.model;

import junit.framework.TestCase;

/**
 * Test for {@link Group}.
 * 
 * @author finterly
 */
public class TestGroup extends TestCase {

	/**
	 * Create a {@link Group} and add to pathway model. Create {@link DataNode} and
	 * add to pathway model. Add DataNode to Group using
	 * {@link Group#addPathwayElement} and {@link Group#removePathwayElement}.
	 */
	public static void testGroupAddRemove() {
		System.out.println("TEST 1");

		// create a pathway model
		PathwayModel p1 = new PathwayModel();

		// create group
		Group g1 = new Group(null);
		assertNull(g1.getElementId());
		assertNull(g1.getPathwayModel());
		// add to pathway model
		p1.addGroup(g1);
		System.out.println("Group elementId is " + g1.getElementId());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
		System.out.println("PathwayModel contains Groups " + p1.getGroups());
		assertTrue(p1.getPathwayObjects().contains(g1));
		assertTrue(p1.getGroups().contains(g1));
		assertEquals(g1.getPathwayModel(), p1);

		// create a data node
		DataNode d1 = new DataNode("d1", null);
		assertNull(d1.getElementId());
		assertNull(d1.getPathwayModel());
		// add to pathway model
		p1.addDataNode(d1);
		System.out.println("DataNode elementId is " + d1.getElementId());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
		System.out.println("PathwayModel contains DataNodes " + p1.getDataNodes());
		assertTrue(p1.getPathwayObjects().contains(d1));
		assertTrue(p1.getDataNodes().contains(d1));
		assertEquals(d1.getPathwayModel(), p1);
		assertNull(d1.getGroupRef());

		// add data node to group
		assertTrue(g1.getPathwayElements().isEmpty());
		g1.addPathwayElement(d1);
		assertEquals(d1.getGroupRef(), g1);
		System.out.println("Group contains PathwayElements " + g1.getPathwayElements());
		assertEquals(d1.getGroupRef(), g1);

		// terminate data node
		// p1.removeDataNode(d1);

		// remove data node from group
		g1.removePathwayElement(d1);
		System.out.println("Group contains PathwayElements " + g1.getPathwayElements());
		assertNull(d1.getGroupRef());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
	}

	/**
	 * Create a {@link Group} and add to pathway model. Create {@link DataNode} and
	 * add to pathway model. Add DataNode to Group using
	 * {@link Groupable#setGroupRefTo} and {@link Groupable#unsetGroupRef}.
	 */
	public static void testGroupableSetUnset() {
		System.out.println("TEST 2");

		// create a pathway model
		PathwayModel p1 = new PathwayModel();

		// create group
		Group g1 = new Group(null);
		assertNull(g1.getElementId());
		assertNull(g1.getPathwayModel());
		// add to pathway model
		p1.addGroup(g1);
		System.out.println("Group elementId is " + g1.getElementId());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
		System.out.println("PathwayModel contains Groups " + p1.getGroups());
		assertTrue(p1.getPathwayObjects().contains(g1));
		assertTrue(p1.getGroups().contains(g1));
		assertEquals(g1.getPathwayModel(), p1);

		// create a data node
		DataNode d1 = new DataNode("d1", null);
		assertNull(d1.getElementId());
		assertNull(d1.getPathwayModel());
		// add to pathway model
		p1.addDataNode(d1);
		System.out.println("DataNode elementId is " + d1.getElementId());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
		System.out.println("PathwayModel contains DataNodes " + p1.getDataNodes());
		assertTrue(p1.getPathwayObjects().contains(d1));
		assertTrue(p1.getDataNodes().contains(d1));
		assertEquals(d1.getPathwayModel(), p1);
		assertNull(d1.getGroupRef());

		// add data node to group
		assertTrue(g1.getPathwayElements().isEmpty());
		d1.setGroupRefTo(g1);
		assertEquals(d1.getGroupRef(), g1);
		System.out.println("Group contains PathwayElements " + g1.getPathwayElements());
		assertEquals(d1.getGroupRef(), g1);

		// terminate data node
		// p1.removeDataNode(d1);

		// remove data node from group
		d1.unsetGroupRef();
		System.out.println("Group contains PathwayElements " + g1.getPathwayElements());
		assertNull(d1.getGroupRef());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
	}

	/**
	 * Create a {@link Group} and add to pathway model. Create {@link DataNode} and
	 * add to pathway model. Add DataNode to Group. Create another Group and add
	 * DataNode to new Group.
	 */
	public static void testAlreadyInGroup() {
		System.out.println("TEST 3");

		// create a pathway model
		PathwayModel p1 = new PathwayModel();

		// create groups
		Group g1 = new Group(null);
		Group g2 = new Group(null);

		// add to pathway model
		p1.addGroup(g1);
		p1.addGroup(g2);
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
		System.out.println("PathwayModel contains Groups " + p1.getGroups());

		// create a data node
		DataNode d1 = new DataNode("d1", null);
		p1.addDataNode(d1);

		// add data node to group 1
//		d1.setGroupRefTo(g1);
		g1.addPathwayElement(d1);
		assertEquals(d1.getGroupRef(), g1);

		// add data node to group 2
//		d1.setGroupRefTo(g2);
		g2.addPathwayElement(d1);
		assertEquals(d1.getGroupRef(), g2);

		// remove data node from group
//		d1.unsetGroupRef();
		System.out.println("Group 1 contains PathwayElements " + g1.getPathwayElements());
		System.out.println("Group 2 contains PathwayElements " + g2.getPathwayElements());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
	}

	/**
	 * Check that when a line points to the group, it stays at the same position
	 * when the group disappears. Test for regression of bug #1058
	 * 
	 * @author unknown, finterly
	 */
	public void testUngroup() {
		PathwayModel pwy = new PathwayModel();
		Interaction line = new Interaction(null);

		Group group = new Group(null);
		DataNode node = new DataNode("d1", null);
		pwy.addInteraction(line);
		pwy.addDataNode(node);
		pwy.addGroup(group);

		assertNotNull(group.getElementId());

		node.setCenterX(120);
		node.setCenterY(20);
		node.setWidth(20);
		node.setHeight(20);

		assertEquals(0, group.getPathwayElements().size());

		// add node to group
		node.setGroupRefTo(group);

		// check that now it's really part of group
		assertEquals(1, group.getPathwayElements().size());
		assertTrue(group.getPathwayElements().contains(node));

		line.setEndLinePointX(group.getCenterX());
		line.setEndLinePointY(group.getTop());

		line.getEndLinePoint().setElementRef(group);
		assertEquals(line.getEndLinePoint().getElementRef(), group);

		assertEquals(8.0, group.getMargin());

		assertEquals(120.0, line.getEndLinePointX(), 0.01);
		assertEquals(2.0, line.getEndLinePointY(), 0.01);

		// ungroup
		pwy.remove(group);

		// check that line points at same position
		assertEquals(120.0, line.getEndLinePointX());
		assertEquals(2.0, line.getEndLinePointY());

		assertNull(line.getEndLinePoint().getElementRef());
		assertNull(node.getGroupRef());

	}

}
