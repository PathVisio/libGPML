package org.pathvisio.libgpml.model;

import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.model.Citation;
import org.pathvisio.libgpml.model.DataNode;
import org.pathvisio.libgpml.model.PathwayModel;
import org.pathvisio.libgpml.model.PathwayElement.CitationRef;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.util.XrefUtils;

import junit.framework.TestCase;

/**
 * For testing Citation methods
 * 
 * @author finterly
 */
public class TestCitation extends TestCase {

	private PathwayModel p;
	private DataNode d1;
	private CitationRef ar1;
	private CitationRef ar2;
	private Citation a;
	private Citation a2;

	/**
	 * Two annotationRefs (same annotation) added to a data node. 
	 */
	@Before
	public void setUp() {
		DataSourceTxt.init();
		p = new PathwayModel();
		d1 = new DataNode("d1", DataNodeType.UNDEFINED); 
		p.addDataNode(d1); 
		Xref xref = XrefUtils.createXref("11", "ensembl");
		ar1 = d1.addCitation(xref, "urlLink");
		ar2 = d1.addCitation(null, "urlLink2");
		a = ar1.getCitation();
		a2 = ar2.getCitation();
		
		assertTrue(p.hasPathwayObject(a));
		assertTrue(p.hasPathwayObject(a2));

		assertTrue(d1.hasCitationRef(ar1));
		assertTrue(d1.hasCitationRef(ar2));
		assertEquals(ar1.getCitable(), d1);
		assertEquals(ar2.getCitable(), d1);
		assertTrue(a.hasCitationRef(ar1));
		assertTrue(a2.hasCitationRef(ar2));
	}

	/**
	 * Tests for removing annotation.
	 */
	@Test
	public void testRemoveCitation() {
		System.out.println(d1.getCitationRefs());
		System.out.println(a.getCitationRefs());
		p.removeCitation(a);		
		assertTrue(a.getCitationRefs().isEmpty());
		assertFalse(p.hasPathwayObject(a));
		assertFalse(d1.hasCitationRef(ar1));
		assertTrue(d1.hasCitationRef(ar2));
	}

	/**
	 * Tests for when annotation with duplicate information is added to pathway
	 * model.
	 */
	@Test
	public void testDuplicateCitation() {
		Xref xref = XrefUtils.createXref("11", "ensembl");
		CitationRef ar3 = d1.addCitation(xref, "urlLink");
		assertEquals(ar1.getCitation(), ar3.getCitation());
	}
}
