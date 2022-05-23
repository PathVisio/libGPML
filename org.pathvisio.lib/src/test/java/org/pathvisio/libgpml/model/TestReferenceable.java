package org.pathvisio.libgpml.model;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.model.PathwayElement.AnnotationRef;
import org.pathvisio.libgpml.model.PathwayElement.CitationRef;
import org.pathvisio.libgpml.model.PathwayElement.EvidenceRef;
import org.pathvisio.libgpml.model.Referenceable.Annotatable;
import org.pathvisio.libgpml.model.Referenceable.Citable;
import org.pathvisio.libgpml.model.Referenceable.Evidenceable;
import org.pathvisio.libgpml.model.type.AnnotationType;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.util.XrefUtils;

import junit.framework.TestCase;

/**
 * Tests for {@link Referenceable}:
 * 
 * <p>
 * Tests:
 * <ol>
 * <li>{@link Annotatable}, {@link Citable}, {@link Evidenceable}
 * <li>{@link Annotation} and {@link AnnotationRef}
 * <li>{@link Citation} and {@link CitationRef}
 * <li>{@link Evidence} and {@link EvidenceRef}.
 * </ol>
 * 
 * @author finterly
 */
public class TestReferenceable extends TestCase {

	private PathwayModel p;
	private DataNode d1;

	private Annotation a1;
	private Citation c1;
	private Evidence e1;

	private AnnotationRef ar1;
	private CitationRef cr1;
	private EvidenceRef er1;

	private AnnotationRef ar2;
	private CitationRef cr2;
	private EvidenceRef er2;

	@Before
	public void setUp() throws Exception {
		p = new PathwayModel();
		d1 = new DataNode("d1", DataNodeType.UNDEFINED); // instantiate dataNode
		p.addDataNode(d1); // add datanode to pathway model

		// add annotation, citation, evidence to data node
		ar1 = d1.addAnnotation("value", AnnotationType.ONTOLOGY, null, null);
		cr1 = d1.addCitation(null, "urlLink");
		er1 = d1.addEvidence("value", XrefUtils.createXref("123", "doid"), "urlLink");
		a1 = ar1.getAnnotation();
		e1 = er1.getEvidence();
		c1 = cr1.getCitation();

		// add citation and evidence to annotationRef
		cr2 = ar1.addCitation(c1);
		er2 = ar1.addEvidence(e1);
		// add annotation to citationRef
		ar2 = cr1.addAnnotation(a1);

	}

	@Test
	public void testCheckSetUp() {
		// pathway model has objects
		assertTrue(p.hasPathwayObject(d1));
		assertTrue(p.hasPathwayObject(a1));
		assertTrue(p.hasPathwayObject(c1));
		assertTrue(p.hasPathwayObject(e1));

		// datanode
		assertTrue(d1.hasAnnotationRef(ar1));
		assertTrue(d1.hasCitationRef(cr1));
		assertTrue(d1.hasEvidenceRef(er1));

		// annotation and annotationRefs
		assertTrue(a1.hasAnnotationRef(ar1));
		assertTrue(a1.hasAnnotationRef(ar2));
		assertTrue(ar1.hasCitationRef(cr2));
		assertTrue(ar1.hasEvidenceRef(er2));
		assertEquals(ar1.getAnnotatable(), d1);
		assertEquals(ar1.getAnnotation(), a1);
		assertEquals(ar2.getAnnotatable(), cr1);
		assertEquals(ar2.getAnnotation(), a1);
		assertEquals(ar2.getTopPathwayElement(), d1);
		assertEquals(ar1.getTopPathwayElement(), ar1.getAnnotatable());

		// citation and citationRefs
		assertTrue(c1.hasCitationRef(cr1));
		assertTrue(c1.hasCitationRef(cr2));
		assertEquals(cr1.getCitable(), d1);
		assertEquals(cr1.getCitation(), c1);
		assertEquals(cr2.getCitable(), ar1);
		assertEquals(cr2.getCitation(), c1);
		assertEquals(cr2.getTopPathwayElement(), d1);

		// evidence and evidenceRefs
		assertTrue(e1.hasEvidenceRef(er1));
		assertTrue(e1.hasEvidenceRef(er2));
		assertEquals(er1.getEvidenceable(), d1);
		assertEquals(er1.getEvidence(), e1);
		assertEquals(er2.getEvidenceable(), ar1);
		assertEquals(er2.getEvidence(), e1);
		assertEquals(er2.getTopPathwayElement(), d1);
	}

	@Test
	public void testRemoveAnnotation() {
		p.removeAnnotation(a1);

		// pathway model has objects
		assertTrue(p.hasPathwayObject(d1));
		assertFalse(p.hasPathwayObject(a1));
		assertTrue(p.hasPathwayObject(c1));
		assertTrue(p.hasPathwayObject(e1));

		// datanode
		assertFalse(d1.hasAnnotationRef(ar1));
		assertTrue(d1.hasCitationRef(cr1));
		assertTrue(d1.hasEvidenceRef(er1));

		// annotation and annotationRefs
		assertFalse(a1.hasAnnotationRef(ar1));
		assertFalse(a1.hasAnnotationRef(ar2));
		assertFalse(ar1.hasCitationRef(cr2));
		assertFalse(ar1.hasEvidenceRef(er2));
		assertNull(ar1.getAnnotatable());
		assertNull(ar1.getAnnotation());
		assertNull(ar2.getAnnotatable());
		assertNull(ar2.getAnnotation());

		// citation and citationRefs
		assertTrue(c1.hasCitationRef(cr1));
		assertFalse(c1.hasCitationRef(cr2));
		assertEquals(cr1.getCitable(), d1);
		assertEquals(cr1.getCitation(), c1);
		assertNull(cr2.getCitable());
		assertNull(cr2.getCitation());

		// evidence and evidenceRefs
		assertTrue(e1.hasEvidenceRef(er1));
		assertFalse(e1.hasEvidenceRef(er2));
		assertEquals(er1.getEvidenceable(), d1);
		assertEquals(er1.getEvidence(), e1);
		assertNull(er2.getEvidenceable());
		assertNull(er2.getEvidence());
	}

	@Test
	public void testRemoveAnnotationRef() {
		d1.removeAnnotationRef(ar1);

		// pathway model has objects
		assertTrue(p.hasPathwayObject(d1));
		assertTrue(p.hasPathwayObject(a1));
		assertTrue(p.hasPathwayObject(c1));
		assertTrue(p.hasPathwayObject(e1));

		// datanode
		assertFalse(d1.hasAnnotationRef(ar1));
		assertTrue(d1.hasCitationRef(cr1));
		assertTrue(d1.hasEvidenceRef(er1));

		// annotation and annotationRefs
		assertFalse(a1.hasAnnotationRef(ar1));
		assertTrue(a1.hasAnnotationRef(ar2));
		assertFalse(ar1.hasCitationRef(cr2));
		assertFalse(ar1.hasEvidenceRef(er2));
		assertNull(ar1.getAnnotatable());
		assertNull(ar1.getAnnotation());
		assertEquals(ar2.getAnnotatable(), cr1);
		assertEquals(ar2.getAnnotation(), a1);
		assertEquals(ar2.getTopPathwayElement(), d1);

		// citation and citationRefs
		assertTrue(c1.hasCitationRef(cr1));
		assertFalse(c1.hasCitationRef(cr2));
		assertEquals(cr1.getCitable(), d1);
		assertEquals(cr1.getCitation(), c1);
		assertNull(cr2.getCitable());
		assertNull(cr2.getCitation());

		// evidence and evidenceRefs
		assertTrue(e1.hasEvidenceRef(er1));
		assertFalse(e1.hasEvidenceRef(er2));
		assertEquals(er1.getEvidenceable(), d1);
		assertEquals(er1.getEvidence(), e1);
		assertNull(er2.getEvidenceable());
		assertNull(er2.getEvidence());
	}

	@Test
	public void testRemoveDataNode() {
		p.removeDataNode(d1);

		// pathway model has objects
		assertFalse(p.hasPathwayObject(d1));
		assertFalse(p.hasPathwayObject(a1));
		assertFalse(p.hasPathwayObject(c1));
		assertFalse(p.hasPathwayObject(e1));

		// datanode
		assertFalse(d1.hasAnnotationRef(ar1));
		assertFalse(d1.hasCitationRef(cr1));
		assertFalse(d1.hasEvidenceRef(er1));

		// annotation and annotationRefs
		assertFalse(a1.hasAnnotationRef(ar1));
		assertFalse(a1.hasAnnotationRef(ar2));
		assertFalse(ar1.hasCitationRef(cr2));
		assertFalse(ar1.hasEvidenceRef(er2));
		assertNull(ar1.getAnnotatable());
		assertNull(ar1.getAnnotation());
		assertNull(ar2.getAnnotatable());
		assertNull(ar2.getAnnotation());
		assertEquals(ar2.getTopPathwayElement(), d1);

		// citation and citationRefs
		assertFalse(c1.hasCitationRef(cr1));
		assertFalse(c1.hasCitationRef(cr2));
		assertNull(cr1.getCitable());
		assertNull(cr1.getCitation());
		assertNull(cr2.getCitable());
		assertNull(cr2.getCitation());
		assertEquals(cr2.getTopPathwayElement(), d1);

		// evidence and evidenceRefs
		assertFalse(e1.hasEvidenceRef(er1));
		assertFalse(e1.hasEvidenceRef(er2));
		assertNull(er1.getEvidenceable());
		assertNull(er1.getEvidence());
		assertNull(er2.getEvidenceable());
		assertNull(er2.getEvidence());
		assertEquals(er2.getTopPathwayElement(), d1);
	}
}
