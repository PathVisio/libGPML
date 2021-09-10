package org.pathvisio.model.ref;

import org.pathvisio.model.DataNode;
import org.pathvisio.model.Interaction;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.ref.PathwayElement.AnnotationRef;
import org.pathvisio.model.ref.PathwayElement.CitationRef;
import org.pathvisio.model.type.AnnotationType;

import junit.framework.TestCase;

/**
 * Tests for Annotation class.
 * 
 * @author p70073399
 */
public class TestAnnotationRef extends TestCase {

	/**
	 * Test for annotationRef
	 */
	public static void testAnnotationRef() {

		// instantiate pathway
		PathwayModel p1 = new PathwayModel();

		// instantiate dataNode
		DataNode d1 = new DataNode("d1", null);

		// instantiate annotation
		Annotation a1 = new Annotation("value", AnnotationType.ONTOLOGY);

		// instantiate citation
		Citation c1 = new Citation("urlLink");
		Citation c2 = new Citation("urlLink2");

		// we add dataNode to pathwayModel, citation to dataNode, annotation to
		// citation, and citation to annotation...
		p1.addDataNode(d1);
		CitationRef cr1 = d1.addCitationRef(c1);
		AnnotationRef ar1 = cr1.addAnnotationRef(a1);
		CitationRef cr2 = ar1.addCitationRef(c2);

		System.out.println(cr2.getCitable());
		System.out.println(cr2.getCitation());
		System.out.println(cr2.getTop());
		System.out.println(cr1.getTop());
		System.out.println(ar1.getAnnotatable());

		// instantiate dataNode
		Interaction i1 = new Interaction();
		
		//do the same for interaction 
		p1.addInteraction(i1);
		CitationRef cr3 = i1.addCitationRef(c1);
		AnnotationRef ar3 = cr3.addAnnotationRef(a1);
		CitationRef cr4 = ar3.addCitationRef(c2);
		System.out.println(cr3.getCitable());
		System.out.println(cr3.getCitation());
		System.out.println(cr3.getTop());
		System.out.println(cr4.getTop());
		System.out.println(ar3.getAnnotatable());
//	
//		DataNode d1 = new DataNode("d1", null);
//
//		Annotation a1 = new Annotation("value", AnnotationType.ONTOLOGY);
//		Citation c1 = new Citation("urlLink");
//		p1.addAnnotation(a1);
//		p1.addCitation(c1);
//		AnnotationRef ar1 = d1.addAnnotationRef(a1);
//		ar1.addCitationRef(c1);
//		System.out.println("Annotation has AnnotationRefs " + a1.getAnnotationRefs());
//		System.out.println("Citation has CitationRefs " + c1.getCitationRefs());
//		System.out.println("AnnotationRef has CitationRefs " + ar1.getCitationRefs());
//
//		// data node has annotationRef which has a citationRef
//		System.out.println("DataNode has AnnotationRefs " + d1.getAnnotationRefs());
//
//		assertEquals(ar1.getAnnotatable(), d1);
//		assertEquals(ar1.getAnnotation(), a1);
//		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
//
////		 a1.removeAnnotationRef(ar1);
//		ar1.unsetAnnotation();
////		 d1.removeAnnotationRef(ar1);
//
//		assertNull(ar1.getAnnotatable());
//		assertNull(ar1.getAnnotation());
//		System.out.println("DataNode has AnnotationRef " + d1.getAnnotationRefs());
//		System.out.println("Annotation has AnnotationRef " + a1.getAnnotationRefs());
//		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());
	}

	/**
	 * Tests for case when annotation with duplicate information is added to pathway
	 * model
	 */
	public static void testDuplicateAnnotation() {
		PathwayModel p2 = new PathwayModel();
		assert (p2.getAnnotations().isEmpty());
		Annotation a1 = new Annotation("value", AnnotationType.ONTOLOGY);
		Annotation a2 = new Annotation("value", AnnotationType.ONTOLOGY);
		Annotation annotationExisting = p2.addAnnotation(a1);
		assertEquals(annotationExisting, a1);
		// annotation a2 contains duplicate information and is not added
		Annotation annotationExisting2 = p2.addAnnotation(a2);
		assertEquals(annotationExisting2, a1);
		assertTrue(p2.getPathwayObjects().contains(a1));
		assertFalse(p2.getPathwayObjects().contains(a2));
		assertTrue(p2.getAnnotations().contains(a1));
		assertFalse(p2.getAnnotations().contains(a2));
	}

}
