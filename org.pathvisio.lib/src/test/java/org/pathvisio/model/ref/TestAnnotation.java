package org.pathvisio.model.ref;


import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.element.DataNode;
import org.pathvisio.model.type.AnnotationType;

import junit.framework.TestCase;

/**
 * Tests for Annotation class.
 * 
 * @author p70073399
 */
public class TestAnnotation extends TestCase {

	/**
	 */
	public static void testAnnotationWithAnnotationRef() {

		PathwayModel p1 = new PathwayModel();

		assert(p1.getAnnotations().isEmpty());
		
		Annotation a1 = new Annotation("value", AnnotationType.ONTOLOGY);
		assertNull(a1.getElementId());
		p1.addAnnotation(a1);
		assertNotNull(a1.getElementId());
		assertTrue(p1.getPathwayElements().contains(a1));
		assertTrue(p1.getAnnotations().contains(a1));
		
		DataNode d1 = new DataNode(null, null, null, "d1", null);
		AnnotationRef ar1 = new AnnotationRef();
		d1.addAnnotationRef(ar1); //TODO 
		ar1.setAnnotationTo(a1);
		
		System.out.println("DataNode has AnnotationRef " + d1.getAnnotationRefs());
		System.out.println("Annotation has AnnotationRef " + a1.getAnnotationRefs());
		assertEquals(ar1.getAnnotatable(), d1);
		assertEquals(ar1.getAnnotation(), a1);
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());
		d1.removeAnnotationRef(ar1);
		
		System.out.println(ar1.getAnnotatable());
		System.out.println(ar1.getAnnotation());
		System.out.println("DataNode has AnnotationRef " + d1.getAnnotationRefs());
		System.out.println("Annotation has AnnotationRef " + a1.getAnnotationRefs());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayElements());

		

	}
	
	
	
	
}
