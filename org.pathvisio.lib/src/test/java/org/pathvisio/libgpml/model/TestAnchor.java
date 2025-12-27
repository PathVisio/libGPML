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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pathvisio.libgpml.model.LineElement.Anchor;
import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.model.type.AnchorShapeType;
import org.pathvisio.libgpml.model.type.ArrowHeadType;

/**
 * Test for {@link Anchor}.
 * 
 * @author finterly
 */
class TestAnchor {

	private PathwayModel p;
	private Interaction i1;
	private Anchor a1;

	/**
	 * Creates and adds anchor to line, line to pathwayModel.
	 */
	@BeforeEach
	void setUp() {
		p = new PathwayModel();
		i1 = new Interaction();
		LinePoint pt1 = i1.getLinePoints().get(0);
		LinePoint pt2 = i1.getLinePoints().get(1);

		// checks
		assertNull(i1.getPathwayModel());
		assertNull(pt1.getPathwayModel());
		assertNull(pt2.getPathwayModel());
		p.addInteraction(i1);

		// checks
		assertTrue(p.hasPathwayObject(i1));
		assertTrue(p.getInteractions().contains(i1));
		assertEquals(i1.getPathwayModel(), p);
		assertTrue(p.hasPathwayObject(pt1));
		assertTrue(p.hasPathwayObject(pt2));
		assertEquals(pt1.getPathwayModel(), p);
		assertEquals(pt2.getPathwayModel(), p);

		// set new points
		List<LinePoint> points = new ArrayList<LinePoint>();
		LinePoint pt3 = i1.new LinePoint(10, 18);
		LinePoint pt4 = i1.new LinePoint(20, 18);
		points.add(pt3);
		points.add(pt4);
		i1.setLinePoints(points);
		i1.setStartArrowHeadType(ArrowHeadType.BINDING);
		i1.setEndArrowHeadType(ArrowHeadType.BINDING);

		// checks
		assertTrue(p.hasPathwayObject(i1));
		assertTrue(p.getInteractions().contains(i1));
		assertEquals(i1.getPathwayModel(), p);
		assertFalse(p.hasPathwayObject(pt1));
		assertFalse(p.hasPathwayObject(pt2));
		assertNull(pt1.getPathwayModel());
		assertNull(pt2.getPathwayModel());
		assertTrue(p.hasPathwayObject(pt3));
		assertTrue(p.hasPathwayObject(pt4));
		assertEquals(pt3.getPathwayModel(), p);
		assertEquals(pt4.getPathwayModel(), p);

		// add anchor
		a1 = i1.addAnchor(0.5, AnchorShapeType.NONE);

		// checks
		assertTrue(i1.hasAnchor(a1));
		assertTrue(p.hasPathwayObject(a1));
		assertEquals(a1.getLineElement(), i1);
		assertEquals(a1.getPathwayModel(), p);
		assertEquals(a1.getPathwayModel(), p);
	}

	/**
	 * Tests removing anchor.
	 */
	@Test
	void removeAnchor() {
		// remove anchor
		i1.removeAnchor(a1);
		assertFalse(i1.hasAnchor(a1));
		assertFalse(p.hasPathwayObject(a1));
	}

	/**
	 * Tests removing line and thus anchor.
	 */
	@Test
	void removeLine() {
		p.removeInteraction(i1);
		assertFalse(i1.hasAnchor(a1));
		assertFalse(p.hasPathwayObject(a1));
		assertFalse(p.hasPathwayObject(i1));
	}

}