/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2021 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.model.LineElement.Anchor;
import org.pathvisio.model.LineElement.LinePoint;
import org.pathvisio.model.type.AnchorShapeType;

import junit.framework.TestCase;

/**
 * Test for {@link Anchor}.
 * 
 * @author finterly
 */
public class TestAnchor extends TestCase {

	private PathwayModel p;
	private Interaction i1;
	private Anchor a1;

	/**
	 * Creates and adds anchor to line, line to pathwayModel.
	 */
	@Before
	public void setUp() {
		p = new PathwayModel();
		i1 = new Interaction(null);
		p.addInteraction(i1);

		List<LinePoint> points = new ArrayList<LinePoint>();
		points.add(i1.new LinePoint(null, 9, 18));
		points.add(i1.new LinePoint(null, 9, 18));
		i1.setLinePoints(points);

		a1 = i1.addAnchor(0.5, AnchorShapeType.SQUARE);

		assertTrue(i1.hasAnchor(a1));
		assertTrue(p.hasPathwayObject(a1));
		assertTrue(p.hasPathwayObject(i1));
		assertEquals(a1.getLineElement(), i1);
		assertEquals(a1.getPathwayModel(), p);
	}

	/**
	 * Tests removing anchor.
	 */
	@Test
	public void testRemoveAnchor() {
		// remove anchor
		i1.removeAnchor(a1);
		assertFalse(i1.hasAnchor(a1));
		assertFalse(p.hasPathwayObject(a1));
	}

	/**
	 * Tests removing line and thus anchor.
	 */
	@Test
	public void testRemoveLine() {
		p.removeInteraction(i1);
		assertFalse(i1.hasAnchor(a1));
		assertFalse(p.hasPathwayObject(a1));
		assertFalse(p.hasPathwayObject(i1));
	}

}