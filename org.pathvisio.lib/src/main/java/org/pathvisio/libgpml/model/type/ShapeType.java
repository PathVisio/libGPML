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
	private static Shape roundedRectangle = new RoundRectangle2D.Double(0, 0, 10, 10, 20, 20);
	private static Shape ellipse = new Ellipse2D.Double(0, 0, 10, 10);

	// ================================================================================
	// Instantiates and registers shapes
	// ================================================================================
	private static final Map<String, ShapeType> nameToShapeType = new TreeMap<String, ShapeType>(
			String.CASE_INSENSITIVE_ORDER);

	// Basic shapes
	public static final ShapeType NONE = new ShapeType("None", null);
	public static final ShapeType RECTANGLE = new ShapeType("Rectangle", rectangle);
	public static final ShapeType ROUNDED_RECTANGLE = new ShapeType("RoundedRectangle", roundedRectangle);
	public static final ShapeType OVAL = new ShapeType("Oval", ellipse);
	public static final ShapeType TRIANGLE = new ShapeType("Triangle", ShapeCatalog.getRegularPolygon(3, 10, 10));
	public static final ShapeType PENTAGON = new ShapeType("Pentagon", ShapeCatalog.getRegularPolygon(5, 10, 10));
	public static final ShapeType HEXAGON = new ShapeType("Hexagon", ShapeCatalog.getRegularPolygon(6, 10, 10));
	public static final ShapeType OCTAGON = new ShapeType("Octagon", ShapeCatalog.getRegularPolygon(8, 10, 10));

	// Basic line shapes
	public static final ShapeType EDGE = new ShapeType("Line", new Line2D.Double(0, 0, 10, 10));
	public static final ShapeType ARC = new ShapeType("Arc", new Arc2D.Double(0, 0, 10, 10, 0, -180, Arc2D.OPEN));
	public static final ShapeType BRACE = new ShapeType("Brace", ShapeCatalog.getPluggableShape(Internal.BRACE));

	// Cellular components with special shape
	public static final ShapeType MITOCHONDRIA = new ShapeType("Mitochondria",
			ShapeCatalog.getPluggableShape(Internal.MITOCHONDRIA));
	public static final ShapeType SARCOPLASMIC_RETICULUM = new ShapeType("SarcoplasmicReticulum",
			ShapeCatalog.getPluggableShape(Internal.SARCOPLASMIC_RETICULUM));
	public static final ShapeType ENDOPLASMIC_RETICULUM = new ShapeType("EndoplasmicReticulum",
			ShapeCatalog.getPluggableShape(Internal.ENDOPLASMIC_RETICULUM));
	public static final ShapeType GOLGI_APPARATUS = new ShapeType("GolgiApparatus",
			ShapeCatalog.getPluggableShape(Internal.ENDOPLASMIC_RETICULUM));

	// Cellular components (rarely used)
	public static final ShapeType NUCLEOLUS = new ShapeType("Nucleolus", null);
	public static final ShapeType VACUOLE = new ShapeType("Vacuole", null);
	public static final ShapeType LYSOSOME = new ShapeType("Lysosome", null);
	public static final ShapeType CYTOSOL = new ShapeType("CytosolRegion", null);

	// Cellular components with basic shape
	public static final ShapeType EXTRACELLULAR = new ShapeType("ExtracellularRegion", roundedRectangle); // roundRect
	public static final ShapeType CELL = new ShapeType("Cell", roundedRectangle); // roundRect
	public static final ShapeType NUCLEUS = new ShapeType("Nucleus", ellipse); // oval
	public static final ShapeType ORGANELLE = new ShapeType("Organelle", roundedRectangle); // roundRect
	public static final ShapeType VESICLE = new ShapeType("Vesicle", ellipse); // oval

	// Deprecated since GPML2013a? TODO
	public static final ShapeType MEMBRANE = new ShapeType("Membrane", null); // roundRect
	public static final ShapeType CELLA = new ShapeType("CellA", null); // oval
	public static final ShapeType RIBOSOME = new ShapeType("Ribosome", null); // Hexagon
	public static final ShapeType ORGANA = new ShapeType("OrganA", null); // oval
	public static final ShapeType ORGANB = new ShapeType("OrganB", null); // oval
	public static final ShapeType ORGANC = new ShapeType("OrganC", null); // oval
	public static final ShapeType PROTEINB = new ShapeType("ProteinB", null); // hexagon

	// Special shapes
	public static final ShapeType CORONAVIRUS = new ShapeType("Coronavirus",
			ShapeCatalog.getPluggableShape(Internal.CORONAVIRUS));
	public static final ShapeType DNA = new ShapeType("DNA", ShapeCatalog.getPluggableShape(Internal.DNA)); //
	public static final ShapeType CELL_ICON = new ShapeType("CellIcon",
			ShapeCatalog.getPluggableShape(Internal.CELL_ICON)); //

	private final String name;
	private final Shape shape;
	private final boolean isResizeable;
	private final boolean isRotatable;

	/**
	 * The constructor is private. ShapeType cannot be directly instantiated. Use
	 * create() method to instantiate ShapeType.
	 * 
	 * @param name the string key of this ShapeType. // isResizeable if true object
	 *             is resizeable.
	 * @throws NullPointerException if name is null.
	 */
	private ShapeType(String name, Shape shape) {
		this(name, shape, true, true);
	}

	// TODO documentation
	private ShapeType(String name, Shape shape, boolean isResizeable, boolean isRotatable) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToShapeType.put(name, this); // adds this name and ShapeType to map.
		this.shape = shape;
		this.isResizeable = isResizeable;
		this.isRotatable = isRotatable;
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
		if (nameToShapeType.containsKey(name)) {
			return nameToShapeType.get(name);
		} else {
			Logger.log.trace("Registered shape type " + name);
			return new ShapeType(name, shape);
		}
	}

	/**
	 * Returns the name key for this ShapeType.
	 * 
	 * @return name the key for this ShapeType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param mw
	 * @param mh
	 * @return the shape resized.
	 */
	public Shape getShape(double mw, double mh) {
		// now scale the path so it has proper w and h.
		Rectangle r = shape.getBounds();
		AffineTransform at = new AffineTransform();
		at.translate(-r.x, -r.y);
		at.scale(mw / r.width, mh / r.height);
		return at.createTransformedShape(shape);
	}

	public boolean isResizeable() {
		return isResizeable;
	}

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
		return nameToShapeType.get(name);
	}

	/**
	 * Returns the names of all registered ShapeTypes as a list.
	 * 
	 * @return names the names of all registered ShapeTypes in the order of
	 *         insertion.
	 */
	static public List<String> getNames() {
		List<String> names = new ArrayList<>(nameToShapeType.keySet());
		return names;
	}

	/**
	 * Returns the data node type values of all ShapeTypes as a list.
	 * 
	 * @return shapeTypes the list of all registered ShapeTypes.
	 */
	static public List<ShapeType> getValues() {
		List<ShapeType> shapeTypes = new ArrayList<>(nameToShapeType.values());
		return shapeTypes;
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
