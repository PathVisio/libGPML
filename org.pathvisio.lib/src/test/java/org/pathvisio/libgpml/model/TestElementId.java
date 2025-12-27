package org.pathvisio.libgpml.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.GroupType;

/**
 * Tests for elementId. 
 * 
 * @author finterly
 */
class TestElementId {

	private PathwayModel p;

	@BeforeEach
	void setUp() throws Exception {
		p = new PathwayModel();
	}

	/**
	 * 
	 */
	@Test
	void dataNode() {
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
	void interaction() {
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
	void graphicalLine() {
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
	void label() {
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
	void shape() {
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
	void group() {
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
