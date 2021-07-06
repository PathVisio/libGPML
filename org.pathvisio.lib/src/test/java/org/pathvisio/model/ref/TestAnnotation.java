package org.pathvisio.model.ref;


import org.pathvisio.model.PathwayModel;
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
	public static void testAnnotation() {

		PathwayModel pathwayModel = new PathwayModel();

		assert(pathwayModel.getAnnotations().isEmpty());
		
		Annotation a1 = new Annotation("value", AnnotationType.ONTOLOGY);
		System.out.println(a1);
		pathwayModel.addAnnotation(a1);
		System.out.println(pathwayModel.getPathwayElement(a1.getElementId()));
		System.out.println(pathwayModel.getAnnotations());

	}
	
	
	
	
}
