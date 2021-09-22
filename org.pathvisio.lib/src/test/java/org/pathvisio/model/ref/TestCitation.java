package org.pathvisio.model.ref;

import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.type.AnnotationType;

import junit.framework.TestCase;

/**
 * For testing Citation methods
 * 
 * @author finterly
 */
public class TestCitation extends TestCase {

	/**
	 * Tests for when citation with duplicate information is added to pathway
	 * model.
	 */
	public static void testDuplicateCitation() {
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
