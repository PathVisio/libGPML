package org.pathvisio.model;

import org.pathvisio.model.PathwayElement.AnnotationRef;
import org.pathvisio.model.PathwayElement.CitationRef;
import org.pathvisio.model.type.AnnotationType;

import junit.framework.TestCase;

/**
 * Tests for PathwayElement class: Comments, References
 * 
 * 
 * @author finterly
 */
public class TestPathwayElement extends TestCase {

	/**
	 * Tests the creation, addition, and removal of annotation/annotationRef,
	 * citation/citationRef, and evidence/evidenceRef to pathway elements.
	 */
	public static void testReferences() {

		PathwayModel p = new PathwayModel(); // instantiate pathway model
		DataNode d1 = new DataNode("d1", null); // instantiate dataNode
		p.addDataNode(d1); // add datanode to pathway model

		// create refs and add
		AnnotationRef ar1 = d1.addAnnotation("value", AnnotationType.ONTOLOGY, null, null);
		CitationRef cr1 = ar1.addCitation(null, "urlLink");
		Annotation a1 = ar1.getAnnotation();
		Citation c1 = cr1.getCitation();

		assertTrue(a1.getAnnotationRefs().contains(ar1));
		assertTrue(c1.getCitationRefs().contains(cr1));
		assertTrue(ar1.getCitationRefs().contains(cr1));
		assertTrue(d1.getAnnotationRefs().contains(ar1));
		assertFalse(d1.getCitationRefs().contains(cr1));
		assertEquals(ar1.getAnnotatable(), d1);
		assertEquals(ar1.getAnnotation(), a1);
		assertEquals(cr1.getTopPathwayElement(), d1);
		assertEquals(ar1.getTopPathwayElement(), d1);

		System.out.println("PathwayModel contains PathwayElements " + p.getPathwayObjects());
		System.out.println("DataNode has AnnotationRef " + d1.getAnnotationRefs());
		System.out.println("Annotation has AnnotationRef " + a1.getAnnotationRefs());
		System.out.println("PathwayModel contains PathwayElements " + p.getPathwayObjects());
//		 a1.removeAnnotationRef(ar1);
		ar1.unsetAnnotation();
//		 d1.removeAnnotationRef(ar1);

		assertNull(ar1.getAnnotatable());
		assertNull(ar1.getAnnotation());
		System.out.println("DataNode has AnnotationRef " + d1.getAnnotationRefs());
		System.out.println("Annotation has AnnotationRef " + a1.getAnnotationRefs());
		System.out.println("PathwayModel contains PathwayElements " + p.getPathwayObjects());
	}

}
