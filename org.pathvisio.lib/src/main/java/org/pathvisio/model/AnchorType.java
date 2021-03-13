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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class contains extensible enum for Anchor types.
 * 
 * @author unknown, finterly
 */
public class AnchorType implements Comparable<AnchorType> {

	// Initialize map
	private static Map<String, AnchorType> nameToAnchorType = new HashMap<String, AnchorType>();
	// Initialize set
	private static Set<AnchorType> anchorTypes = new TreeSet<AnchorType>();

	/**
	 * Anchor type can be either None (hidden) or Circle.
	 */
	public static final AnchorType NONE = new AnchorType("None");
	public static final AnchorType SQUARE = new AnchorType("Square");

	private String name;
	private boolean disallowLinks;

	/**
	 * Constructor to initialize the state of enum types.
	 * 
	 * @param name the string.
	 */
	private AnchorType(String name) {
		this(name, false);
	}

	/**
	 * Constructor to initialize the state of enum types.
	 * 
	 * @param name          the string.
	 * @param disallowLinks the boolean, if set to true nothing will be able to
	 *                      attach to this anchor.
	 */
	private AnchorType(String name, final boolean disallowLinks) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.disallowLinks = disallowLinks;
		this.name = name;
		anchorTypes.add(this);
		nameToAnchorType.put(name, this);
	}

	/**
	 * Creates a AnchorType given string identifier name. New AnchorType extends the
	 * enum.
	 * 
	 * @param name the identifier for anchor type.
	 * @return a new AnchorType object.
	 */
	public static AnchorType create(String name) {
		return new AnchorType(name, false);
	}

	/**
	 * Creates a AnchorType given string identifier name and given disallowLinks.
	 * New AnchorType extends the enum.
	 * 
	 * @param name          the identifier for anchor type.
	 * @param disallowLinks the boolean, if set to true nothing will be able to
	 *                      attach to this anchor.
	 * @return a new AnchorType object.
	 */
	public static AnchorType create(String name, final boolean disallowLinks) {
		return new AnchorType(name, disallowLinks);
	}

	/**
	 * Returns the AnchorType from given string value.
	 * 
	 * @param value the string.
	 * @return the AnchorType with given string value.
	 */
	public static AnchorType fromName(String value) {
		return nameToAnchorType.get(value);
	}

	/**
	 * Stable identifier for this AnchorType.
	 * 
	 * @return name the string.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the anchor type values of all AnchorType as an array.
	 * 
	 * @return anchortype array of values.
	 */
	static public AnchorType[] getValues() {
		return anchorTypes.toArray(new AnchorType[0]);
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
	 * Returns disallowLink boolean of this AnchorType.
	 * 
	 * @return disallowLink the boolean, if set to true nothing will be able to
	 *         attach to this anchor.
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
	public int compareTo(AnchorType o) {
		return toString().compareTo(o.toString());
	}
}
