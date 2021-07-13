package org.pathvisio.model.ref;


import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.element.DataNode;
import org.pathvisio.model.ref.Annotation;
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

		PathwayModel pathwayModel = new PathwayModel();

		assert(pathwayModel.getAnnotations().isEmpty());
		
		Annotation a1 = new Annotation("value", AnnotationType.ONTOLOGY);
		assertNull(a1.getElementId());
		pathwayModel.addAnnotation(a1);
		assertNotNull(a1.getElementId());
		assertTrue(pathwayModel.getPathwayElements().contains(a1));
		assertTrue(pathwayModel.getAnnotations().contains(a1));
		
		DataNode d1 = new DataNode(null, null, null, "d1", null);
		AnnotationRef ar1 = new AnnotationRef(a1, d1);
		d1.addAnnotationRef(ar1); //TODO 
		a1.addAnnotationRef(ar1); //TODO 
		System.out.println(d1.getAnnotationRefs());
		System.out.println(a1.getAnnotationRefs());
		
		

	}
	
	
	
	
}
