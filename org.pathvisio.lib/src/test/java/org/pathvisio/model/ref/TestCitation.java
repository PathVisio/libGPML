package org.pathvisio.model.ref;


import org.pathvisio.model.DataNode;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.ref.PathwayElement.CitationRef;

import junit.framework.TestCase;

/**
 * For testing Citation methods 
 * 
 * @author finterly
 */
public class TestCitation extends TestCase {

	/**
	 * Test for adding Citation and CitationRef to pathway model
	 */
	public static void testCitation() {

		PathwayModel p1 = new PathwayModel();

		assert (p1.getCitations().isEmpty());

		Citation c1 = new Citation("urlLink");
		Annotation a1 = new Annotation("a1", null);
		p1.addCitation(c1);
		p1.addAnnotation(a1);
		System.out.println("Citation has CitationRefs " + c1.getCitationRefs());
		System.out.println("Citation has CitationRefs " + c1.getCitationRefs());
		
		// data node has annotationRef which has a citationRef
		DataNode d1 = new DataNode("d1", null);
		p1.addDataNode(d1);
		CitationRef cr1 = d1.addCitationRef(c1); 
		cr1.addAnnotationRef(a1);
		System.out.println("DataNode has CitationRefs " + d1.getCitationRefs());
		System.out.println("CitationRef has AnnotationRefs " + cr1.getAnnotationRefs());

		
		assertEquals(cr1.getCitable(), d1);
		assertEquals(cr1.getCitation(), c1);
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());

//		 c1.removeCitationRef(cr1);
		 cr1.unsetCitation();
//		 d1.removeCitationRef(cr1);
		
		assertNull(cr1.getCitable());
		assertNull(cr1.getCitation());
		System.out.println("DataNode has CitationRef " + d1.getCitationRefs());
		System.out.println("Citation has CitationRef " + c1.getCitationRefs());
		System.out.println("PathwayModel contains PathwayElements " + p1.getPathwayObjects());

	}
	
	
}
