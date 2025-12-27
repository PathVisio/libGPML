package org.pathvisio.libgpml.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.GroupType;

/**
 * Tests for aliasRef of {@link DataNode}.
 * 
 * @author finterly
 *
 */
class TestAlias {

	private PathwayModel p;
	private Group g;
	private DataNode d;
	private DataNode alias;

	@BeforeEach
	void setUp() throws Exception {
		p = new PathwayModel();
		g = new Group(GroupType.GROUP);
		d = new DataNode("textLabel", DataNodeType.UNDEFINED);
		p.addGroup(g);
		p.addDataNode(d);
		g.addPathwayElement(d);
		alias = g.addAlias("textLabel");

		assertEquals(d.getGroupRef(), g);
		assertTrue(g.hasPathwayElement(d));
		assertFalse(g.hasPathwayElement(alias));
		assertTrue(p.hasLinkedAlias(g, alias));
		assertTrue(p.hasAliasRef(g));
		
		System.out.println(p.getPathwayObjects());
	}

	/**
	 * 
	 */
	@Test
	void removeAlias() {
		p.removeDataNode(alias);
		assertNull(alias.getAliasRef());
		assertNull(p.getLinkedAliases(g));
		assertFalse(p.hasAliasRef(g));
	}

	/**
	 * 
	 */
	@Test
	void removeGroup() {
		p.removeGroup(g);
		assertNull(alias.getAliasRef());
		assertNull(p.getLinkedAliases(g));
		assertFalse(p.hasAliasRef(g));
	}

	/**
	 * 
	 */
	@Test
	void writeGPML2021() throws Exception {
		File tmp = File.createTempFile("alias_testwriteGPML2021_", ".gpml");
		GPML2021Writer.GPML2021WRITER.writeToXml(p, tmp, false);
		System.out.println(tmp);
	}

}
