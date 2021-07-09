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
package org.pathvisio.model.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.pathvisio.debug.Logger;

/**
 * This enum class contains extensible enum for Shape type property.
 * 
 * @author unknown, finterly
 */
public class ShapeType {

	private static final Map<String, ShapeType> nameToShapeType = new TreeMap<String, ShapeType>(String.CASE_INSENSITIVE_ORDER);

	// TODO unify case 
	public static final ShapeType NONE = new ShapeType("None");
	public static final ShapeType RECTANGLE = new ShapeType("Rectangle"); // TODO: DEFAULT?
	public static final ShapeType ROUNDED_RECTANGLE = new ShapeType("RoundedRectangle");
	public static final ShapeType OVAL = new ShapeType("Oval");
	public static final ShapeType TRIANGLE = new ShapeType("Triangle");
	public static final ShapeType PENTAGON = new ShapeType("Pentagon");
	public static final ShapeType HEXAGON = new ShapeType("Hexagon");
	public static final ShapeType OCTAGON = new ShapeType("Octagon");

	public static final ShapeType EDGE = new ShapeType("Line");
	public static final ShapeType ARC = new ShapeType("Arc");
	public static final ShapeType BRACE = new ShapeType("Brace");

	public static final ShapeType MITOCHONDRIA = new ShapeType("Mitochondria");
	public static final ShapeType SARCOPLASMIC_RETICULUM = new ShapeType("SarcoplasmicReticulum");
	public static final ShapeType ENDOPLASMIC_RETICULUM = new ShapeType("EndoplasmicReticulum");
	public static final ShapeType GOLGI_APPARATUS = new ShapeType("GolgiApparatus");

	// From CellularComponent.java
	public static final ShapeType NUCLEOLUS = new ShapeType("Nucleolus");
	public static final ShapeType VACUOLE = new ShapeType("Vacuole");
	public static final ShapeType LYSOSOME = new ShapeType("Lysosome");
	public static final ShapeType CYTOSOL = new ShapeType("CytosolRegion");
	public static final ShapeType EXTRACELLULAR = new ShapeType("ExtracellularRegion");

	// Deprecated
	public static final ShapeType CELL = new ShapeType("Cell"); // Rounded rectangle
	public static final ShapeType NUCLEUS = new ShapeType("Nucleus"); // Oval
	public static final ShapeType ORGANELLE = new ShapeType("Organelle"); // Rounded rectangle
	public static final ShapeType VESICLE = new ShapeType("Vesicle"); // Oval
	public static final ShapeType MEMBRANE = new ShapeType("Membrane"); // Rounded rectangle
	public static final ShapeType CELLA = new ShapeType("CellA"); // Oval
	public static final ShapeType RIBOSOME = new ShapeType("Ribosome"); // Hexagon
	public static final ShapeType ORGANA = new ShapeType("OrganA"); // Oval
	public static final ShapeType ORGANB = new ShapeType("OrganB"); // Oval
	public static final ShapeType ORGANC = new ShapeType("OrganC"); // Oval
	public static final ShapeType PROTEINB = new ShapeType("ProteinB"); // Hexagon

	
	private String name;

	/**
	 * The constructor is private. ShapeType cannot be directly instantiated. Use
	 * create() method to instantiate ShapeType.
	 * 
	 * @param name the string key of this ShapeType.
	 * @throws NullPointerException if name is null.
	 */
	private ShapeType(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToShapeType.put(name, this); // adds this name and ShapeType to map.
	}

	/**
	 * Returns a ShapeType from a given string identifier name. If the ShapeType
	 * doesn't exist yet, it is created to extend the enum. The method makes sure
	 * that the same object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the ShapeType for given name. If name does not exist, creates and
	 *         returns a new ShapeType.
	 */
	public static ShapeType register(String name) {
		if (nameToShapeType.containsKey(name)) {
			return nameToShapeType.get(name);
		} else {
			Logger.log.trace("Registered shape type " + name); 
			return new ShapeType(name);
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
