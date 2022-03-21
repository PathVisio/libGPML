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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * Class for shapes.
 * 
 * @author unknown
 */
public class AbstractShape implements IShape {

	private String name;
	private boolean isResizeable;
	private boolean isRotatable;
	private Shape sh;

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates.
	 * 
	 * @param sh           the Shape.
	 * @param name         the String name.
	 * @param isResizeable if true shape is resizeable.
	 * @param isRotatable  if true shape is rotatable.
	 */
	public AbstractShape(Shape sh, String name, boolean isResizeable, boolean isRotatable) {
		this.name = name;
		this.sh = sh;
		this.isRotatable = isRotatable;
		this.isResizeable = isResizeable;
		ShapeRegistry.registerShape(this);
	}

	/**
	 * @param sh
	 * @param name
	 */
	public AbstractShape(Shape sh, String name) {
		this(sh, name, true, true);
	}

	// ================================================================================
	// Accessors
	// ================================================================================

	/**
	 * Returns String name of this shape.
	 * 
	 * @return name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 *
	 */
	@Override
	public Shape getShape(double mw, double mh) {
		// now scale the path so it has proper w and h.
		Rectangle r = sh.getBounds();
		AffineTransform at = new AffineTransform();
		at.translate(-r.x, -r.y);
		at.scale(mw / r.width, mh / r.height);
		return at.createTransformedShape(sh);
	}

	/**
	 * Returns boolean for whether shape is resizeable.
	 * 
	 * @return isResizeable if true shape is resizeable.
	 */
	@Override
	public boolean isResizeable() {
		return isResizeable;
	}

	/**
	 * Returns boolean for whether shape is rotatable.
	 * 
	 * @return isRotatable if true shape is rotatable.
	 */
	@Override
	public boolean isRotatable() {
		return isRotatable;
	}

	/**
	 * Returns String name of this shape.
	 * 
	 * @return name
	 */
	@Override
	public String toString() {
		return name;
	}

}
