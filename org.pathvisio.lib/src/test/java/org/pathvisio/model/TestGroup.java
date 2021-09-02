package org.pathvisio.model;

import org.pathvisio.model.DataNode;
import org.pathvisio.model.Group;
import org.pathvisio.model.PathwayModel;

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
	 * {@link Group#addPathwayElement()} and {@link Group#removePathwayElement()}.
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
	 * {@link Groupable#setGroupRefTo()} and {@link Groupable#unsetGroupRef()}.
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

}
