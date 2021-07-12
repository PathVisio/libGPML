package org.pathvisio.model;

import org.pathvisio.model.element.DataNode;
import org.pathvisio.model.element.Group;

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
		Group g1 = new Group(null, null, null, null);
		assertNull(g1.getElementId());
		assertNull(g1.getPathwayModel());
		// add to pathway model
		p1.addGroup(g1);
		System.out.println("Group elementId is " + g1.getElementId());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());
		System.out.println("PathwayModel contains Groups " + p1.getGroups());
		assertTrue(p1.getPathwayElements().contains(g1));
		assertTrue(p1.getGroups().contains(g1));
		assertEquals(g1.getPathwayModel(), p1);

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
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());
	}

	/**
	 * Create a {@link Group} and add to pathway model. Create {@link DataNode} and
	 * add to pathway model. Add DataNode to Group using
	 * {@link Groupable#setGroupRefTo()} and {@link Groupable#unsetGroupRef()}.
	 */
	public static void testGroupableSetUnset() {
		System.out.println("TEST 1");

		// create a pathway model
		PathwayModel p1 = new PathwayModel();

		// create group
		Group g1 = new Group(null, null, null, null);
		assertNull(g1.getElementId());
		assertNull(g1.getPathwayModel());
		// add to pathway model
		p1.addGroup(g1);
		System.out.println("Group elementId is " + g1.getElementId());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());
		System.out.println("PathwayModel contains Groups " + p1.getGroups());
		assertTrue(p1.getPathwayElements().contains(g1));
		assertTrue(p1.getGroups().contains(g1));
		assertEquals(g1.getPathwayModel(), p1);

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
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());
	}

}
