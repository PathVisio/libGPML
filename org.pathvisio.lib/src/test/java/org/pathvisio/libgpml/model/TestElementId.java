package org.pathvisio.libgpml.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.GroupType;

/**
 * @author p70073399
 *
 */
public class TestElementId {

	private PathwayModel p;

	@Before
	public void setUp() throws Exception {
		p = new PathwayModel();
	}

	/**
	 * 
	 */
	@Test
	public void testDataNode() {
		DataNode o = new DataNode("textLabel", DataNodeType.UNDEFINED);
		assertNull(o.getElementId());
		p.add(o);
		assertNotNull(o.getElementId());
		assertTrue(p.getDataNodes().contains(o));
		assertTrue(p.hasPathwayObject(o));
		assertEquals(o.getPathwayModel(), p);
		p.remove(o);
		assertNull(o.getElementId());
	}

	@Test
	public void testInteraction() {
		Interaction o = new Interaction();
		assertNull(o.getElementId());
		p.add(o);
		assertNotNull(o.getElementId());
		assertTrue(p.getInteractions().contains(o));
		assertTrue(p.hasPathwayObject(o));
		assertEquals(o.getPathwayModel(), p);
		p.remove(o);
		assertNull(o.getElementId());
	}

	@Test
	public void testGraphicalLine() {
		GraphicalLine o = new GraphicalLine();
		assertNull(o.getElementId());
		p.add(o);
		assertNotNull(o.getElementId());
		assertTrue(p.getGraphicalLines().contains(o));
		assertTrue(p.hasPathwayObject(o));
		assertEquals(o.getPathwayModel(), p);
		p.remove(o);
		assertNull(o.getElementId());
	}

	@Test
	public void testLabel() {
		Label o = new Label("textLabel");
		assertNull(o.getElementId());
		p.add(o);
		assertNotNull(o.getElementId());
		assertTrue(p.getLabels().contains(o));
		assertTrue(p.hasPathwayObject(o));
		assertEquals(o.getPathwayModel(), p);
		p.remove(o);
		assertNull(o.getElementId());
	}

	@Test
	public void testShape() {
		Shape o = new Shape();
		assertNull(o.getElementId());
		p.add(o);
		assertNotNull(o.getElementId());
		assertTrue(p.getShapes().contains(o));
		assertTrue(p.hasPathwayObject(o));
		assertEquals(o.getPathwayModel(), p);
		p.remove(o);
		assertNull(o.getElementId());
	}

	@Test
	public void testGroup() {
		Group o = new Group(GroupType.GROUP);
		assertNull(o.getElementId());
		p.add(o);
		assertNotNull(o.getElementId());
		assertTrue(p.getGroups().contains(o));
		assertTrue(p.hasPathwayObject(o));
		assertEquals(o.getPathwayModel(), p);
		p.remove(o);
		assertNull(o.getElementId());
	}

}
