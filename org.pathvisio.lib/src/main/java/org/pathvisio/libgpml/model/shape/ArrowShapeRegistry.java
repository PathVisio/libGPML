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
package org.pathvisio.libgpml.model.shape;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * Defines and registers all arrowhead shapes.
 *
 * Shapes are defined and registered in the static section of this class.
 * 
 * @author unknown
 */
class ArrowShapeRegistry {

	// Register ArrowHead shapes
	static void registerShapes() {

		// Interaction panel 
		// NB: "Undirected" (no arrow head)
		ShapeRegistry.registerArrow("Directed", getArrowHead(), ArrowShape.FillType.CLOSED);
		ShapeRegistry.registerArrow("Conversion", getArrowHead(), ArrowShape.FillType.CLOSED);
		ShapeRegistry.registerArrow("Inhibition", getTBar(), ArrowShape.FillType.OPEN, TBARWIDTH + TBAR_GAP);
		ShapeRegistry.registerArrow("Catalysis", getCatalysis(), ArrowShape.FillType.OPEN,
				CATALYSIS_DIAM + CATALYSIS_GAP);
		ShapeRegistry.registerArrow("Stimulation", getArrowHead(), ArrowShape.FillType.OPEN);
		ShapeRegistry.registerArrow("Binding", getBinding(), ArrowShape.FillType.CLOSED);
		ShapeRegistry.registerArrow("Translocation", getArrowHead(), ArrowShape.FillType.CLOSED);
		ShapeRegistry.registerArrow("TranscriptionTranslation", getTranscriptionTranslation(), ArrowShape.FillType.WIRE,
				ARROWWIDTH + ARROWHEIGHT);

		// Other
		ShapeRegistry.registerArrow("LigandRound", getLRound(), ArrowShape.FillType.CLOSED);
		ShapeRegistry.registerArrow("ReceptorRound", getRRound(), ArrowShape.FillType.WIRE);
		ShapeRegistry.registerArrow("Receptor", getReceptor(), ArrowShape.FillType.WIRE);
		ShapeRegistry.registerArrow("ReceptorSquare", getReceptorSquare(), ArrowShape.FillType.WIRE);
		ShapeRegistry.registerArrow("LigandSquare", getLigand(), ArrowShape.FillType.CLOSED);
	}

	/**
	 * These are all model coordinates:
	 */
	private static final int ARROWHEIGHT = 4;
	private static final int ARROWWIDTH = 9;
	private static final int TBARHEIGHT = 15;
	private static final int TBARWIDTH = 1;
	private static final int TBAR_GAP = 6;
	private static final int LRDIAM = 11;
	private static final int RRDIAM = LRDIAM + 3;
	private static final int LIGANDWIDTH = 8;
	private static final int LIGANDHEIGHT = 11;
	private static final int RECEPWIDTH = LIGANDWIDTH + 2;
	private static final int RECEPHEIGHT = LIGANDHEIGHT + 2;
	// catalysis
	static final int CATALYSIS_DIAM = 8;
	static final int CATALYSIS_GAP = CATALYSIS_DIAM / 4;
	static final int CATALYSIS_GAP_HEIGHT = 6;
	// transcription translation
	private static final int TAIL = ARROWWIDTH / 2;

	/**
	 * Returns standard arrowhead shape.
	 * 
	 * @return the arrowhead shape.
	 */
	private static Shape getArrowHead() {
		int[] xpoints = new int[] { 0, -ARROWWIDTH, -ARROWWIDTH };
		int[] ypoints = new int[] { 0, -ARROWHEIGHT, ARROWHEIGHT };
		return new Polygon(xpoints, ypoints, 3);
	}

	/**
	 * Returns catalysis arrowhead shape.
	 * 
	 * @return the catalysis arrowhead shape.
	 */
	static private java.awt.Shape getCatalysis() {
		return new Ellipse2D.Double(0, -CATALYSIS_DIAM / 2, CATALYSIS_DIAM, CATALYSIS_DIAM);
	}

	/**
	 * Returns binding arrowhead shape.
	 * 
	 * @return the binding arrowhead shape.
	 */
	static private java.awt.Shape getBinding() {
		GeneralPath path = new GeneralPath();
		path.moveTo(0, 0);
		path.lineTo(-ARROWWIDTH, -ARROWHEIGHT);
		path.lineTo(-ARROWWIDTH / 2, 0);
		path.lineTo(-ARROWWIDTH, ARROWHEIGHT);
		path.closePath();
		return path;
	}

	/**
	 * Returns transcription-translation arrowhead shape.
	 * 
	 * @return the transcription-translation arrowhead shape.
	 */
	static private java.awt.Shape getTranscriptionTranslation() {
		GeneralPath path = new GeneralPath();
		path.moveTo(-TAIL, 0);
		path.lineTo(-TAIL, ARROWHEIGHT * 2);
		path.lineTo(TAIL, ARROWHEIGHT * 2);
		path.lineTo(TAIL, ARROWHEIGHT * 3);
		path.lineTo(TAIL + ARROWWIDTH, ARROWHEIGHT * 2);
		path.lineTo(TAIL, ARROWHEIGHT);
		path.lineTo(TAIL, ARROWHEIGHT * 2);
		return path;
	}

	/**
	 * Returns inhibition (Tbar) arrowhead shape.
	 * 
	 * @return the inhibition (Tbar) arrowhead shape.
	 */
	private static Shape getTBar() {
		return new Rectangle2D.Double(0, -TBARHEIGHT / 2, TBARWIDTH, TBARHEIGHT);
	}

	/**
	 * @return
	 */
	private static Shape getLRound() {
		return new Ellipse2D.Double(-LRDIAM / 2, -LRDIAM / 2, LRDIAM, LRDIAM);
	}

	/**
	 * @return
	 */
	private static Shape getRRound() {
		return new Arc2D.Double(0, -RRDIAM / 2, RRDIAM, RRDIAM, 90, 180, Arc2D.OPEN);
	}

	/**
	 * @return
	 */
	private static Shape getReceptorSquare() {
		GeneralPath rec = new GeneralPath();
		rec.moveTo(RECEPWIDTH, RECEPHEIGHT / 2);
		rec.lineTo(0, RECEPHEIGHT / 2);
		rec.lineTo(0, -RECEPHEIGHT / 2);
		rec.lineTo(RECEPWIDTH, -RECEPHEIGHT / 2);
		return rec;
	}

	/**
	 * @return
	 */
	private static Shape getReceptor() {
		GeneralPath rec = new GeneralPath();
		rec.moveTo(RECEPWIDTH, RECEPHEIGHT / 2);
		rec.lineTo(0, 0);
		rec.lineTo(RECEPWIDTH, -RECEPHEIGHT / 2);
		return rec;
	}

	/**
	 * @return
	 */
	private static Shape getLigand() {
		return new Rectangle2D.Double(-LIGANDWIDTH, -LIGANDHEIGHT / 2, LIGANDWIDTH, LIGANDHEIGHT);
	}

}
