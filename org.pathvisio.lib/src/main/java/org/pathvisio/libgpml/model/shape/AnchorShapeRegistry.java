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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.pathvisio.libgpml.model.type.AnchorShapeType;

/**
 * Defines and registers all Anchor shapes. 
 *
 * Shapes are defined and registered in the static section of this class.
 * 
 * @author unknown
 */
class AnchorShapeRegistry {

	// Register Anchor shapes
	static void registerShapes() {
		ShapeRegistry.registerAnchor(AnchorShapeType.SQUARE.getName(), getAnchorSquare());
		ShapeRegistry.registerAnchor(AnchorShapeType.NONE.getName(), getAnchorNone());
		ShapeRegistry.registerAnchor(AnchorShapeType.CIRCLE.getName(), getAnchorCircle());
	}

	/**
	 * These are all model coordinates:
	 */
	private static final int ANCHOR_SQUARE_SIZE = 6; // TODO
	private static final int ANCHOR_NONE_SIZE = 3;
	private static final int ANCHOR_CIRCLE_SIZE = 8;

	/**
	 * Returns default square anchor shape. 
	 * 
	 * @return the default square anchor shape. 
	 */
	private static Shape getAnchorSquare() {
		return new Rectangle2D.Double(-ANCHOR_SQUARE_SIZE / 2, -ANCHOR_SQUARE_SIZE / 2, ANCHOR_SQUARE_SIZE,
				ANCHOR_SQUARE_SIZE);
	}

	/**
	 * Returns anchor shape none, which appears invisible.  
	 * 
	 * @return the anchor shape for none. 
	 */
	private static Shape getAnchorNone() {
		return new Rectangle2D.Double(-ANCHOR_NONE_SIZE / 2, -ANCHOR_NONE_SIZE / 2, ANCHOR_NONE_SIZE, ANCHOR_NONE_SIZE);
	}

	/**
	 * Returns circle anchor shape. 
	 * 
	 * @return the circle anchor shape. 
	 */
	private static Shape getAnchorCircle() {
		return new Ellipse2D.Double(-ANCHOR_CIRCLE_SIZE / 2, -ANCHOR_CIRCLE_SIZE / 2, ANCHOR_CIRCLE_SIZE,
				ANCHOR_CIRCLE_SIZE);
	}

}
