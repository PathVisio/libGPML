package org.pathvisio.model;

import org.pathvisio.model.DataNode;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.PathwayElement.AnnotationRef;
import org.pathvisio.model.type.AnnotationType;

import junit.framework.TestCase;

/**
 * Tests for Annotation class.
 * 
 * @author p70073399
 */
public class TestAnnotation extends TestCase {

	/**
	 * Tests for when annotation with duplicate information is added to pathway
	 * model.
	 */
	public static void testDuplicateAnnotation() {
		PathwayModel p = new PathwayModel();
		DataNode d1 = new DataNode("d1", null); // instantiate dataNode
		p.addDataNode(d1); // add datanode to pathway model
		AnnotationRef ar1 = d1.addAnnotation("value", AnnotationType.ONTOLOGY, null, null);
		AnnotationRef ar2 = d1.addAnnotation("value", AnnotationType.ONTOLOGY, null, null);
		System.out.println(ar1.getAnnotation());
		System.out.println(ar2.getAnnotation());
		System.out.println(p.getAnnotations());

//		Annotation a1 = new Annotation("value", AnnotationType.ONTOLOGY);
//		Annotation a2 = new Annotation("value", AnnotationType.ONTOLOGY);
//		Annotation annotationExisting = p.addAnnotation(a1);
//		assertEquals(annotationExisting, a1);
//		// annotation a2 contains duplicate information and is not added
//		Annotation annotationExisting2 = p.addAnnotation(a2);
//		assertEquals(annotationExisting2, a1);
//		assertTrue(p.getPathwayObjects().contains(a1));
//		assertFalse(p.getPathwayObjects().contains(a2));
//		assertTrue(p.getAnnotations().contains(a1));
//		assertFalse(p.getAnnotations().contains(a2));
	}

}
