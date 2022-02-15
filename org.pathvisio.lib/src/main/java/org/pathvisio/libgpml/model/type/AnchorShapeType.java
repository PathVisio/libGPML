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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.pathvisio.libgpml.debug.Logger;

/**
 * This class contains extensible enum for Anchor type property.
 * 
 * @author unknown, finterly
 */
public class AnchorShapeType {

	private static Map<String, AnchorShapeType> nameToAnchorShapeType = new TreeMap<String, AnchorShapeType>(
			String.CASE_INSENSITIVE_ORDER);

	public static final AnchorShapeType SQUARE = new AnchorShapeType("Square"); // default
	public static final AnchorShapeType CIRCLE = new AnchorShapeType("Circle");
	public static final AnchorShapeType NONE = new AnchorShapeType("None");

	private String name;
	private boolean disallowLinks;

	/**
	 * Constructor to initialize the state of enum types.
	 * 
	 * @param name          the string key.
	 * @param disallowLinks the boolean if set to true nothing will be able to
	 *                      attach to this anchor
	 */
	private AnchorShapeType(String name, final boolean disallowLinks) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.disallowLinks = disallowLinks;
		nameToAnchorShapeType.put(name, this); // adds this name and ShapeType to map.
	}

	/**
	 * Constructor to initialize the state of enum types without disallowLinks.
	 * 
	 * @param name the string key.
	 */
	private AnchorShapeType(String name) {
		this(name, false);
	}

	/**
	 * Returns an AnchorType from a given string identifier name. If the AnchorType
	 * doesn't exist yet, it is created to extend the enum. The method makes sure
	 * that the same object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the AnchorType for given name. If name does not exist, creates and
	 *         returns a new AnchorType.
	 */
	public static AnchorShapeType register(String name) {
		if (nameToAnchorShapeType.containsKey(name)) {
			return nameToAnchorShapeType.get(name);
		} else {
			Logger.log.trace("Registered anchor type " + name);
			return new AnchorShapeType(name);
		}
	}

	/**
	 * Returns the name key for this AnchorType.
	 * 
	 * @return name the string key.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the AnchorType from given string name.
	 * 
	 * @param name the string key.
	 * @return the AnchorType with given string name.
	 */
	public static AnchorShapeType fromName(String name) {
		return nameToAnchorShapeType.get(name);
	}

	/**
	 * Returns the names of all registered AnchorTypes as a String array.
	 * 
	 * @return names the names of all registered AnchorTypes in order of insertion.
	 */
	static public List<String> getNames() {
		List<String> names = new ArrayList<>(nameToAnchorShapeType.keySet());
		return names;
	}

	/**
	 * Returns the anchor type values of all AnchorTypes as a list.
	 * 
	 * @return anchorTypes the list of all registered AnchorType.
	 */
	static public List<AnchorShapeType> getValues() {
		List<AnchorShapeType> anchorTypes = new ArrayList<>(nameToAnchorShapeType.values());
		return anchorTypes;
	}

	/**
	 * Returns a string representation of this AnchorType.
	 * 
	 * @return name the identifier of this AnchorType.
	 */
	public String toString() {
		return name;
	}

	/**
	 * Returns the booleans disallowLinks.
	 * 
	 * @return disallowLinks the boolean if set to true nothing will be able to
	 *         attach to this anchor
	 */
	public boolean isDisallowLinks() {
		return disallowLinks;
	}

	/**
	 * Compares string representations of given AnchorType lexicographically.
	 * 
	 * @return positive number (integer difference of character value) if first
	 *         string is lexicographically greater than second string, negative
	 *         number if first string is less than second string lexicographically,
	 *         and 0 if first string is lexicographically equal to second string.
	 */
	public int compareTo(AnchorShapeType o) {
		return toString().compareTo(o.toString());
	}
}
