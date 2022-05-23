package org.pathvisio.libgpml.model;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.model.PathwayElement.AnnotationRef;
import org.pathvisio.libgpml.model.type.AnnotationType;
import org.pathvisio.libgpml.model.type.DataNodeType;

import junit.framework.TestCase;

/**
 * Tests for Annotation class.
 * 
 * @author p70073399
 */
public class TestAnnotation extends TestCase {

	private PathwayModel p;
	private DataNode d1;
	private AnnotationRef ar1;
	private AnnotationRef ar2;
	private Annotation a;
	private Annotation a2;

	/**
	 * Two annotationRefs (same annotation) added to a data node. 
	 */
	@Before
	public void setUp() {
		p = new PathwayModel();
		d1 = new DataNode("d1", DataNodeType.UNDEFINED); 
		p.addDataNode(d1); 
		ar1 = d1.addAnnotation("value", AnnotationType.ONTOLOGY, null, null);
		ar2 = d1.addAnnotation("value2", AnnotationType.ONTOLOGY, null, null);
		a = ar1.getAnnotation();
		a2 = ar2.getAnnotation();
		
		assertTrue(p.hasPathwayObject(a));
		assertTrue(p.hasPathwayObject(a2));

		assertTrue(d1.hasAnnotationRef(ar1));
		assertTrue(d1.hasAnnotationRef(ar2));
		assertEquals(ar1.getAnnotatable(), d1);
		assertEquals(ar2.getAnnotatable(), d1);
		assertTrue(a.hasAnnotationRef(ar1));
		assertTrue(a2.hasAnnotationRef(ar2));
	}

	/**
	 * Tests for removing annotation.
	 */
	@Test
	public void testRemoveAnnotation() {
		System.out.println(d1.getAnnotationRefs());
		System.out.println(a.getAnnotationRefs());
		p.removeAnnotation(a);		
		assertTrue(a.getAnnotationRefs().isEmpty());
		assertFalse(p.hasPathwayObject(a));
		assertFalse(d1.hasAnnotationRef(ar1));
		assertTrue(d1.hasAnnotationRef(ar2));
	}

	/**
	 * Tests for when annotation with duplicate information is added to pathway
	 * model.
	 */
	@Test
	public void testDuplicateAnnotation() {
		AnnotationRef ar3 = d1.addAnnotation("value", AnnotationType.ONTOLOGY, null, null);
		assertEquals(ar1.getAnnotation(), ar3.getAnnotation());
	}

}
