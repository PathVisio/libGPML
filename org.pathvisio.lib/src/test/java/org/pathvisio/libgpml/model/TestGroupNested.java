package org.pathvisio.libgpml.model;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.io.ConverterException;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.GroupType;

import junit.framework.TestCase;

/**
 * Tests for nested {@link Group}(s).
 * 
 * @author finterly
 */
public class TestGroupNested extends TestCase {

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

		g2 = new Group(GroupType.GROUP);
		d2 = new DataNode("d2", DataNodeType.UNDEFINED);
		d3 = new DataNode("d3", DataNodeType.UNDEFINED);
		p.addGroup(g2);
		p.addDataNode(d2);
		p.addDataNode(d3);
		g2.addPathwayElement(d2);
		g2.addPathwayElement(d3);
		g2.addPathwayElement(g1);

		assertTrue(p.hasPathwayObject(g1));
		assertTrue(p.hasPathwayObject(d1));
		assertTrue(p.getGroups().contains(g1));
		assertTrue(p.getDataNodes().contains(d1));
		assertTrue(g1.hasPathwayElement(d1));
		assertEquals(g1.getPathwayModel(), p);
		assertEquals(d1.getPathwayModel(), p);
		assertEquals(d1.getGroupRef(), g1);

		assertTrue(p.hasPathwayObject(g2));
		assertTrue(p.hasPathwayObject(d2));
		assertTrue(p.hasPathwayObject(d3));
		assertTrue(g2.hasPathwayElement(d2));
		assertTrue(g2.hasPathwayElement(d3));
		assertTrue(g2.hasPathwayElement(g1)); // g2 has g1
		assertEquals(g2.getPathwayModel(), p);
		assertEquals(d2.getPathwayModel(), p);
		assertEquals(d3.getPathwayModel(), p);
		assertEquals(d2.getGroupRef(), g2);
		assertEquals(d3.getGroupRef(), g2);
		assertEquals(g1.getGroupRef(), g2); // g1 references g2

	}

	/**
	 * 
	 */
	@Test
	public void testRemoveDataNode() {
		// terminates data node and g1
		p.removeDataNode(d1);
		assertFalse(p.hasPathwayObject(g1));
		assertFalse(p.hasPathwayObject(d1));
		assertFalse(p.getGroups().contains(g1));
		assertFalse(p.getDataNodes().contains(d1));
		assertFalse(g1.hasPathwayElement(d1));
		assertNull(g1.getPathwayModel());
		assertNull(d1.getPathwayModel());
		assertNull(d1.getGroupRef());
		assertFalse(g2.hasPathwayElement(g1)); // g2 no longer has g1
		assertNull(g1.getGroupRef()); // g1 no longer references g2

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
	public void testRemoveDataNode2() {
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
		assertTrue(g2.hasPathwayElement(g1)); // g2 has g1
		assertEquals(g1.getGroupRef(), g2); // g1 references g2

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
		assertFalse(g2.hasPathwayElement(g1)); // g2 no longer has g1
		assertNull(g1.getGroupRef()); // g1 no longer references g2

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
		assertFalse(g2.hasPathwayElement(g1)); // g2 no longer has g1
		assertNull(g1.getGroupRef()); // g1 no longer references g2

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
		assertFalse(g2.hasPathwayElement(g1)); // g2 no longer has g1
		assertNull(g1.getGroupRef()); // g1 no longer references g2
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
	 * 
	 */
	@Test
	public void testWriteGPML2021() throws IOException, ConverterException {
		File tmp = File.createTempFile("group_nested_testwriteGPML2021_", ".gpml");
		GPML2021Writer.GPML2021WRITER.writeToXml(p, tmp, false);
		System.out.println(tmp);
	}
}
