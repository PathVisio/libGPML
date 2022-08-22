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
package org.pathvisio.libgpml.model.type;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.model.shape.IShape;
import org.pathvisio.libgpml.model.shape.ShapeCatalog;
import org.pathvisio.libgpml.model.shape.ShapeCatalog.Internal;
import org.pathvisio.libgpml.model.shape.ShapeRegistry;

/**
 * This enum class contains extensible enum for Shape type property.
 * 
 * @author unknown, finterly
 */
public class ShapeType implements IShape {

	// ================================================================================
	// Static variables
	// ================================================================================
	private static Shape rectangle = new Rectangle(0, 0, 10, 10);
	private static Shape ellipse = new Ellipse2D.Double(0, 0, 10, 10);

	// ================================================================================
	// Instantiates and registers shapes
	// ================================================================================
	private static final Map<String, ShapeType> SHAPETYPE_MAP = new TreeMap<String, ShapeType>(
			String.CASE_INSENSITIVE_ORDER);
	private static final List<ShapeType> VISIBLE_VALUES = new ArrayList<ShapeType>();

	// ========================================
	// Basic shapes
	// ========================================
	public static final ShapeType NONE = new ShapeType("None", null);
	public static final ShapeType RECTANGLE = new ShapeType("Rectangle", rectangle);
	public static final ShapeType ROUNDED_RECTANGLE = new ShapeType("RoundedRectangle", null) {
		public Shape getShape(double mw, double mh) {
			return new RoundRectangle2D.Double(0, 0, mw, mh, 20, 20);
		}
	};
	public static final ShapeType OVAL = new ShapeType("Oval", ellipse);
	public static final ShapeType TRIANGLE = new ShapeType("Triangle", ShapeCatalog.getRegularPolygon(3, 10, 10));
	public static final ShapeType PENTAGON = new ShapeType("Pentagon", ShapeCatalog.getRegularPolygon(5, 10, 10));
	public static final ShapeType HEXAGON = new ShapeType("Hexagon", ShapeCatalog.getRegularPolygon(6, 10, 10));
	public static final ShapeType OCTAGON = new ShapeType("Octagon", ShapeCatalog.getPluggableShape(Internal.OCTAGON));

	// ========================================
	// Basic line shapes
	// ========================================
	public static final ShapeType EDGE = new ShapeType("Undirected", new Line2D.Double(0, 0, 10, 10));
	public static final ShapeType ARC = new ShapeType("Arc", new Arc2D.Double(0, 0, 10, 10, 0, -180, Arc2D.OPEN));
	public static final ShapeType BRACE = new ShapeType("Brace", ShapeCatalog.getPluggableShape(Internal.BRACE));

	// ========================================
	// Cellular components (irregular shape)
	// ========================================
	public static final ShapeType MITOCHONDRIA = new ShapeType("Mitochondria",
			ShapeCatalog.getPluggableShape(Internal.MITOCHONDRIA));
	public static final ShapeType SARCOPLASMIC_RETICULUM = new ShapeType("SarcoplasmicReticulum",
			ShapeCatalog.getPluggableShape(Internal.SARCOPLASMIC_RETICULUM));
	public static final ShapeType ENDOPLASMIC_RETICULUM = new ShapeType("EndoplasmicReticulum",
			ShapeCatalog.getPluggableShape(Internal.ENDOPLASMIC_RETICULUM));
	public static final ShapeType GOLGI_APPARATUS = new ShapeType("GolgiApparatus",
			ShapeCatalog.getPluggableShape(Internal.GOLGI_APPARATUS));

	// ========================================
	// Cellular components (basic shape)
	// ========================================
	public static final ShapeType EXTRACELLULAR_REGION = new ShapeType("ExtracellularRegion", null) {
		public Shape getShape(double mw, double mh) { // rounded rectangle
			return new RoundRectangle2D.Double(0, 0, mw, mh, 20, 20);
		}
	};
	public static final ShapeType CELL = new ShapeType("Cell", null) {
		public Shape getShape(double mw, double mh) { // rounded rectangle
			return new RoundRectangle2D.Double(0, 0, mw, mh, 20, 20);
		}
	};
	public static final ShapeType NUCLEUS = new ShapeType("Nucleus", ellipse); // oval
	public static final ShapeType ORGANELLE = new ShapeType("Organelle", null) {
		public Shape getShape(double mw, double mh) { // rounded rectangle
			return new RoundRectangle2D.Double(0, 0, mw, mh, 20, 20);
		}
	};
	public static final ShapeType VESICLE = new ShapeType("Vesicle", ellipse); // oval

	// ========================================
	// Miscellaneous shapes
	// ========================================
	public static final ShapeType CORONAVIRUS_ICON = new ShapeType("CoronavirusIcon",
			ShapeCatalog.getPluggableShape(Internal.CORONAVIRUS_ICON));
	public static final ShapeType DNA_ICON = new ShapeType("DNAIcon",
			ShapeCatalog.getPluggableShape(Internal.DNA_ICON)); 
	public static final ShapeType RNA_ICON = new ShapeType("RNAIcon",
			ShapeCatalog.getPluggableShape(Internal.RNA_ICON)); 
	public static final ShapeType CELL_ICON = new ShapeType("CellIcon",
			ShapeCatalog.getPluggableShape(Internal.CELL_ICON));
	public static final ShapeType MEMBRANE_ICON = new ShapeType("MembraneIcon",
			ShapeCatalog.getPluggableShape(Internal.MEMBRANE_ICON));
	public static final ShapeType DEGRADATION = new ShapeType("Degradation",
			ShapeCatalog.getPluggableShape(Internal.DEGRADATION));

	// ================================================================================
	// Properties
	// ================================================================================
	private final String name;
	private final Shape shape;
	private final boolean isResizeable;
	private final boolean isRotatable;

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * The constructor is private. ShapeType cannot be directly instantiated. Use
	 * create() method to instantiate ShapeType.
	 * 
	 * @param name         the string key of this ShapeType.
	 * @param shape        the shape.
	 * @param isResizeable if true object is resizeable.
	 * @param isRotatable  if true object is rotatable.
	 * 
	 * @throws NullPointerException if name is null.
	 */
	private ShapeType(String name, Shape shape, boolean isResizeable, boolean isRotatable) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.shape = shape;
		this.isResizeable = isResizeable;
		this.isRotatable = isRotatable;
		SHAPETYPE_MAP.put(name, this); // adds this name and ShapeType to map.
		VISIBLE_VALUES.add(this);
	}

	/**
	 * The constructor is private. ShapeType is resizeable and rotatable by default.
	 * 
	 * @param name  the string key of this ShapeType.
	 * @param shape the shape.
	 */
	private ShapeType(String name, Shape shape) {
		this(name, shape, true, true);
	}

	/**
	 * Returns a ShapeType from a given string identifier name. If the ShapeType
	 * doesn't exist yet, it is created to extend the enum. The method makes sure
	 * that the same object is not added twice.
	 * 
	 * @param name  the string key.
	 * @param shape the shape.
	 * @return the ShapeType for given name. If name does not exist, creates and
	 *         returns a new ShapeType.
	 */
	public static ShapeType register(String name, Shape shape) {
		if (SHAPETYPE_MAP.containsKey(name)) {
			return SHAPETYPE_MAP.get(name);
		} else {
			Logger.log.trace("Registered shape type " + name);
			return new ShapeType(name, shape);
		}
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the name key for this ShapeType.
	 * 
	 * @return name the key for this ShapeType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the shape resized given width and height.
	 * 
	 * @param w the width.
	 * @param h the height.
	 * @return the shape resized.
	 */
	public Shape getShape(double w, double h) {
		if (shape == null) {
			return ShapeRegistry.DEFAULT_SHAPE.getShape(w, h);
		}
		// now scale the path so it has proper w and h.
		Rectangle r = shape.getBounds();
		AffineTransform at = new AffineTransform();
		at.translate(-r.x, -r.y);
		at.scale(w / r.width, h / r.height);
		return at.createTransformedShape(shape);
	}

	/**
	 * Returns boolean for whether shape is resizeable.
	 * 
	 * @return isResizeable if true shape is resizeable.
	 */
	public boolean isResizeable() {
		return isResizeable;
	}

	/**
	 * Returns boolean for whether shape is rotatable.
	 * 
	 * @return isRotatable if true shape is rotatable.
	 */
	public boolean isRotatable() {
		return isRotatable;
	}

	/**
	 * Returns the ShapeType from given string name.
	 * 
	 * @param name the string.
	 * @return the ShapeType with given string name.
	 */
	public static ShapeType fromName(String name) {
		return SHAPETYPE_MAP.get(name);
	}

	/**
	 * Returns the names of all registered ShapeTypes as a list.
	 * 
	 * @return names the names of all registered ShapeTypes in the order of
	 *         insertion.
	 */
	static public String[] getNames() {
		return SHAPETYPE_MAP.keySet().toArray(new String[SHAPETYPE_MAP.size()]);
	}

	/**
	 * Returns the data node type values of all ShapeTypes as a list.
	 * 
	 * @return shapeTypes the list of all registered ShapeTypes.
	 */
	static public ShapeType[] getValues() {
		return SHAPETYPE_MAP.values().toArray(new ShapeType[0]);
	}

	/**
	 * Returns the names of visible registered ShapeTypes as a list.
	 * 
	 * @return result the names of registered ShapeTypes.
	 */
	static public String[] getVisibleNames() {
		String[] result = new String[VISIBLE_VALUES.size()];

		for (int i = 0; i < VISIBLE_VALUES.size(); ++i) {
			result[i] = VISIBLE_VALUES.get(i).getName();
		}
		return result;
	}

	/**
	 * Returns the data node type values of visible ShapeTypes as a list.
	 * 
	 * @return shapeTypes the list of registered ShapeTypes.
	 */
	static public ShapeType[] getVisibleValues() {
		return VISIBLE_VALUES.toArray(new ShapeType[0]);
	}

	/**
	 * Returns a string representation of this ShapeType.
	 * 
	 * @return name the identifier of this ShapeType.
	 */
	public String toString() {
		return name;
	}

}
