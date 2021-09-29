package org.pathvisio.model;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.model.type.DataNodeType;

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
	public void testElementId() {
		DataNode d = new DataNode("textLabel", DataNodeType.UNDEFINED);
		assertNull(d.getElementId());
		p.addDataNode(d);
		assertTrue(p.getDataNodes().contains(d));
		assertTrue(p.getPathwayObjects().contains(d));
		
		
	}

}
