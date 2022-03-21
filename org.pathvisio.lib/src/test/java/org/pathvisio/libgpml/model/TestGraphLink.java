package org.pathvisio.libgpml.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.model.DataNode;
import org.pathvisio.libgpml.model.Interaction;
import org.pathvisio.libgpml.model.PathwayModel;
import org.pathvisio.libgpml.model.LineElement.Anchor;
import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.model.type.ArrowHeadType;
import org.pathvisio.libgpml.model.type.DataNodeType;

import junit.framework.TestCase;

/**
 * For testing methods for elementRef:
 * <p>
 * Tests:
 * <ol>
 * <li>linking point to shaped pathway element.
 * <li>linking point to anchor or line pathway element.
 * </ol>
 * 
 * @author finterly
 */
public class TestGraphLink extends TestCase {

	private PathwayModel p;
	private Interaction i;
	private Interaction i2;

	@Before
	public void setUp() {
		p = new PathwayModel();
		i = new Interaction();
		i2 = new Interaction();
		p.addInteraction(i);
		p.addInteraction(i2);

		List<LinePoint> points = new ArrayList<LinePoint>();
		points.add(i.new LinePoint(4, 2));
		points.add(i.new LinePoint(8, 2));
		i.setLinePoints(points);

		List<LinePoint> points2 = new ArrayList<LinePoint>();
		points2.add(i2.new LinePoint(4, 0));
		points2.add(i2.new LinePoint(4, 4));
		i2.setLinePoints(points2);
	}

	/**
	 * Tests linking of interaction i to datanodes d and d2.
	 * <p>
	 * To look something like this:
	 * 
	 * <pre>
	      d1       d2            
	     ____      ____
	    |    |____|    |   
	    |____| i  |____|
	 * </pre>
	 */
	@Test
	public void testLinkToShapedElement() {

		DataNode d1 = new DataNode("d", DataNodeType.METABOLITE);
		p.addDataNode(d1);
		d1.setCenterX(2);
		d1.setCenterY(2);
		d1.setWidth(4);
		d1.setHeight(4);
		DataNode d2 = new DataNode("d2", DataNodeType.METABOLITE);
		p.addDataNode(d2);
		d2.setCenterX(10);
		d2.setCenterY(2);
		d2.setWidth(4);
		d2.setHeight(4);

		// links start point of interaction i to data node d.
		LinePoint start = i.getStartLinePoint();
		start.linkTo(d1);
		// links end point of interaction i to data node d2.
		LinePoint end = i.getEndLinePoint();
		end.linkTo(d2);

		assertTrue(d1.getLinkableFroms().contains(start));
		assertTrue(d2.getLinkableFroms().contains(end));

	}

	/**
	 * Test linking of start of interaction i to anchor of interaction i2.
	 * <p>
	 * To look something like this:
	 * 
	 * <pre>
	    |____  
	    |
	 * </pre>
	 */
	@Test
	public void testLinkToAnchor() {
		// adds anchor to interaction i2
		Anchor a = i2.addAnchor(0.5, null);

		// links start point of interaction i to anchor of interaction i2
		LinePoint start = i.getStartLinePoint();
		start.linkTo(a);

		assertTrue(a.getLinkableFroms().contains(start));
		assertEquals(start.getElementRef(), a);
	}

}
