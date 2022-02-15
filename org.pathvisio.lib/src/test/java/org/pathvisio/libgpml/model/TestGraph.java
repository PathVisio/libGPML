/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.pathvisio.libgpml.model;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.model.DataNode;
import org.pathvisio.libgpml.model.Interaction;
import org.pathvisio.libgpml.model.PathwayModel;
import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.model.type.DataNodeType;

import junit.framework.TestCase;

/**
 * Test graph properties of a pathway model.
 *
 * @author unknown
 */
public class TestGraph extends TestCase {

	PathwayModel p;
	Interaction l;
	DataNode n1, n2;
	LinePoint start, end;

	@Before
	public void setUp() {
		p = new PathwayModel();

		l = new Interaction();

		l.setStartLinePointX(11.0);
		l.setStartLinePointY(9.0);
		l.setEndLinePointX(51.0);
		l.setEndLinePointY(49.0);
		p.add(l);

		start = l.getStartLinePoint();
		end = l.getEndLinePoint();

		n1 = new DataNode("", DataNodeType.UNDEFINED);
		n1.setCenterX(10.0);
		n1.setCenterY(10.0);
		n1.setWidth(5.0);
		n1.setHeight(5.0);
		p.add(n1);

		n2 = new DataNode("", DataNodeType.UNDEFINED);
		n2.setCenterX(50.0);
		n2.setCenterY(50.0);
		n2.setWidth(5.0);
		n2.setHeight(5.0);
		p.add(n2);
	}

	/**
	 * test that the isRelative() method on mPoint properly reflects the fact that
	 * that the mPoint is linked to an object or not.
	 */
	@Test
	public void testRelative() {
		assertFalse(start.isRelative());
		assertFalse(end.isRelative());

		l.setStartElementRef(n1);

		assertTrue(start.isRelative());
		assertFalse(end.isRelative());

		l.setStartElementRef(null);
		l.setEndElementRef(n2);

		assertFalse(start.isRelative());
		assertTrue(end.isRelative());

		assertTrue(p.hasPathwayObject(n2));

		// re-generate id
		n2.setGeneratedElementId();

		assertFalse(p.getElementIds().contains(n2.getElementId()));

		assertFalse(start.isRelative());
		// assertFalse(end.isRelative()); // TODO should not be used this way
	}

	/**
	 * test that, if a line points to a node, and the node is removed, the line is
	 * properly unlinked.
	 */
	@Test
	public void testRemove() {
		assertFalse(start.isRelative());
		assertEquals(11.0, start.getX(), 0.01);

		// link start to n1
		start.linkTo(n1, -1.0, -1.0);

		assertTrue(start.isRelative());
		assertEquals(7.5, start.getX(), 0.01);
		assertEquals(-1.0, start.getRelX(), 0.01);

		// remove n1
		p.remove(n1);

		assertFalse(start.isRelative());
		assertEquals(7.5, start.getX(), 0.01);
	}

//	/**
//	 * The file under test has some points specified in absolute coordinates, and
//	 * some specified in relative coordinates.
//	 *
//	 * There is a datanode, initialized before the lines, and a shape, initialized
//	 * after the lines.
//	 *
//	 * Test that the coordinates are properly calculated.
//	 */
//	public void testAbsRelMPoint() throws ConverterException {
//		
//		String inputFile = "mpoint-test.gpml";
//		URL url = Thread.currentThread().getContextClassLoader().getResource(inputFile);
//		File fTest = new File(url.getPath());
//		assertTrue(fTest.exists());
//		System.out.println("OK");
//		
//		PathwayModel q = new PathwayModel();
//		q.readFromXml(fTest, true);
//
//		PathwayElement base1 = (PathwayElement) q.getPathwayObject("bad0f");
//		PathwayElement base2 = (PathwayElement) q.getPathwayObject("da8cb");
//
//		:Oe[] l = new PathwayElement[4];
//		for (int i = 0; i < 4; ++i) {
//			l[i] = (PathwayElement) q.getPathwayObject("l" + (i + 1));
//			assertEquals(1750.0 / 15, ((LineElement) l[i]).getEndLinePointX(), 0.01);
//			assertEquals(2000.0 / 15, ((LineElement) l[i]).getEndLinePointY(), 0.01);
//			assertTrue(((LineElement) l[i]).getEndLinePoint().isRelative());
//			assertFalse(((LineElement) l[i]).getStartLinePoint().isRelative());
//		}
//		assertEquals(3000.0 / 15, l[0].getStartLinePointX());
//		assertEquals(800.0 / 15, l[0].getStartLinePointY());
//
//		base1.setCenterX(1850.0 / 15);
//		base2.setCenterX(1850.0 / 15);
//
//		for (int i = 0; i < 4; ++i) {
//			assertEquals(1850.0 / 15, l[i].getEndLinePointX(), 0.01);
//			assertEquals(2000.0 / 15, l[i].getEndLinePointY(), 0.01);
//		}
//		assertEquals(3000.0 / 15, l[0].getMStartX());
//		assertEquals(800.0 / 15, l[0].getMStartY());
//	}
}
