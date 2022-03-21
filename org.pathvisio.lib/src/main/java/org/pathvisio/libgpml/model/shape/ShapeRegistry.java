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

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.Map;

/**
 * The Shape registry stores all arrow heads and shapes
 * 
 * at this moment the shape registry initializes itself, by calling
 * registerShape() on BasicShapes, GenMAPPShapes and MIMShapes.
 */

public class ShapeRegistry {

	private static Shape defaultShape = null;
	public static final IShape DEFAULT_SHAPE;
	private static ArrowShape defaultArrow = null;
	private static AnchorShape defaultAnchor = null;

	private static Map<String, IShape> shapeMap = new HashMap<String, IShape>();
	private static Map<String, ArrowShape> arrowMap = new HashMap<String, ArrowShape>();
	private static Map<String, AnchorShape> anchorMap = new HashMap<String, AnchorShape>();

	static {
		GeneralPath temp = new GeneralPath();
		temp.moveTo(-10, -10);
		temp.lineTo(10, -10);
		temp.lineTo(10, 10);
		temp.lineTo(-10, 10);
		temp.closePath();
		temp.moveTo(-6, -6);
		temp.lineTo(6, 6);
		temp.moveTo(-6, 6);
		temp.lineTo(6, -6);
		defaultArrow = new ArrowShape(temp, ArrowShape.FillType.OPEN);

		temp = new GeneralPath();
		temp.moveTo(0, 0);
		temp.lineTo(10, 0);
		temp.lineTo(10, 10);
		temp.lineTo(0, 10);
		temp.closePath();
		temp.moveTo(2, 2);
		temp.lineTo(8, 8);
		temp.moveTo(2, 8);
		temp.lineTo(8, 2);
		defaultShape = temp;
		DEFAULT_SHAPE = new AbstractShape(defaultShape, "default");

		AnchorShapeRegistry.registerShapes();
		ArrowShapeRegistry.registerShapes();
		ShapeCatalog.registerShapes();
//		MIMShapes.registerShapes(); TODO plugin probably
	}

	// ================================================================================
	// Shape Methods
	// ================================================================================

	/**
	 * Registers the given IShape.
	 * 
	 * @param iShape the IShape.
	 */
	public static void registerShape(IShape iShape) {
		shapeMap.put(iShape.getName(), iShape);
	}

	/**
	 * Returns ShapeType corresponding to given name.
	 * 
	 * @param name the string key.
	 * @return the IShape.
	 */
	public static IShape fromName(String name) {
		return shapeMap.get(name);
	}

	// ================================================================================
	// Arrow Methods
	// ================================================================================
	/**
	 * Registers an arrow shape.
	 * 
	 * @param key              the key used to identify the arrow shape.
	 * @param sh               the shape used to draw the stroke.
	 * @param fillType         the fill type, see {@link ArrowShape}.
	 * @param lineEndingLength the line ending width.
	 */
	static public void registerArrow(String key, Shape sh, ArrowShape.FillType fillType, int lineEndingLength) {
		// pass in zero as the gap between line line ending and anchor
		arrowMap.put(key, new ArrowShape(sh, fillType, lineEndingLength));
	}

	/**
	 * Registers an arrow shape (without lineEndingLength parameter).
	 * 
	 * @param key      the key used to identify the arrow shape.
	 * @param sh       the shape used to draw the stroke and fill (in case fillType
	 *                 is open or closed).
	 * @param fillType The fill type, see {@link ArrowShape}
	 */
	static public void registerArrow(String key, Shape sh, ArrowShape.FillType fillType) {
		arrowMap.put(key, new ArrowShape(sh, fillType));
	}

	/**
	 * Returns a named arrow head. The shape is normalized so that it fits with a
	 * line that goes along the positive x-axis. The tip of the arrow head is in
	 * 0,0.
	 * 
	 * @param name the string name.
	 * @return the arrow shape.
	 */
	public static ArrowShape getArrow(String name) {
		ArrowShape sh = arrowMap.get(name);
		if (sh == null) {
			sh = defaultArrow;
		}
		return sh;
		// TODO: here we return a reference to the object on the
		// registry itself we should really return a clone, although
		// in practice this is not a problem since we do a affine
		// transform immediately after.
	}

	// ================================================================================
	// Anchor Methods
	// ================================================================================

	/**
	 * Registers an anchor shape.
	 * 
	 * @param key the key used to identify the anchor shape.
	 * @param sh  the anchor shape.
	 */
	static public void registerAnchor(String key, Shape sh) {
		anchorMap.put(key, new AnchorShape(sh));
	}

	/**
	 * Returns an anchor shape
	 * 
	 * @param name the string name.
	 * @return the anchor shape.
	 */
	public static AnchorShape getAnchor(String name) {
		AnchorShape sh = anchorMap.get(name);
		if (sh == null) {
			sh = defaultAnchor;
		}
		return sh;
	}

}
