package org.pathvisio.libgpml.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pathvisio.libgpml.model.PathwayElement.EvidenceRef;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.util.XrefUtils;

/**
 * For testing Evidence methods
 * 
 * @author finterly
 */
class TestEvidence {

	private PathwayModel p;
	private DataNode d1;
	private EvidenceRef ar1;
	private EvidenceRef ar2;
	private Evidence a;
	private Evidence a2;

	/**
	 * Two annotationRefs (same annotation) added to a data node.
	 */
	@BeforeEach
	void setUp() {
		p = new PathwayModel();
		d1 = new DataNode("d1", DataNodeType.UNDEFINED);
		p.addDataNode(d1);

		ar1 = d1.addEvidence("value", XrefUtils.createXref("123", "doid"), "urlLink");
		ar2 = d1.addEvidence("value2", XrefUtils.createXref("123", "doid"), "urlLink");
		a = ar1.getEvidence();
		a2 = ar2.getEvidence();

		assertTrue(p.hasPathwayObject(a));
		assertTrue(p.hasPathwayObject(a2));

		assertTrue(d1.hasEvidenceRef(ar1));
		assertTrue(d1.hasEvidenceRef(ar2));
		assertEquals(ar1.getEvidenceable(), d1);
		assertEquals(ar2.getEvidenceable(), d1);
		assertTrue(a.hasEvidenceRef(ar1));
		assertTrue(a2.hasEvidenceRef(ar2));
	}

	/**
	 * Tests for removing annotation.
	 */
	@Test
	void removeEvidence() {
		System.out.println(d1.getEvidenceRefs());
		System.out.println(a.getEvidenceRefs());
		p.removeEvidence(a);
		assertTrue(a.getEvidenceRefs().isEmpty());
		assertFalse(p.hasPathwayObject(a));
		assertFalse(d1.hasEvidenceRef(ar1));
		assertTrue(d1.hasEvidenceRef(ar2));
	}

	/**
	 * Tests for when annotation with duplicate information is added to pathway
	 * model.
	 */
	@Test
	void duplicateEvidence() {
		EvidenceRef ar3 = d1.addEvidence("value", XrefUtils.createXref("123", "doid"), "urlLink");
		assertEquals(ar1.getEvidence(), ar3.getEvidence());
	}

}
