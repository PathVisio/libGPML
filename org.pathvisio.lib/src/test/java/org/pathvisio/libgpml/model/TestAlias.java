package org.pathvisio.libgpml.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.io.ConverterException;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.GroupType;

/**
 * Tests for aliasRef of {@link DataNode}.
 * 
 * @author finterly
 *
 */
public class TestAlias {

	private PathwayModel p;
	private Group g;
	private DataNode d;
	private DataNode alias;

	@Before
	public void setUp() throws Exception {
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
	public void testRemoveAlias() {
		p.removeDataNode(alias);
		assertNull(alias.getAliasRef());
		assertNull(p.getLinkedAliases(g));
		assertFalse(p.hasAliasRef(g));
	}

	/**
	 * 
	 */
	@Test
	public void testRemoveGroup() {
		p.removeGroup(g);
		assertNull(alias.getAliasRef());
		assertNull(p.getLinkedAliases(g));
		assertFalse(p.hasAliasRef(g));
	}

	/**
	 * 
	 */
	@Test
	public void testWriteGPML2021() throws IOException, ConverterException {
		File tmp = File.createTempFile("alias_testwriteGPML2021_", ".gpml");
		Gpml2021Writer.GPML2021WRITER.writeToXml(p, tmp, false);
		System.out.println(tmp);
	}

}
