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
 * This class contains extensible enum for group types. Groups can have
 * different biological meanings (e.g. protein Complex), and can be rendered in
 * different ways based on that.
 *
 * NB: group type previously named group style.
 * 
 * @author unknown, finterly
 */
public class GroupType implements Comparable<GroupType> {
	private static Map<String, GroupType> nameToGroupType = new HashMap<String, GroupType>();
	private static Set<GroupType> groupTypes = new TreeSet<GroupType>();

	/**
	 * Group bounds are slightly larger than the summed bounds of the contained
	 * pathway elements.
	 */
	public static final double DEFAULT_M_MARGIN = 8;
	public static final double COMPLEX_M_MARGIN = 12;

	/**
	 * Type for pathway elements that do no belong to a group. TODO: remove?
	 */
	public static final GroupType NONE = new GroupType("None");

	/**
	 * Type for pathway elements that belong to a group.
	 */
	public static final GroupType GROUP = new GroupType("Group");

	/**
	 * Type for pathway elements that belong to a complex.
	 */
	public static final GroupType COMPLEX = new GroupType("Complex", false, COMPLEX_M_MARGIN);

	/**
	 * Type for pathway elements that belong to a pathway.
	 */
	public static final GroupType PATHWAY = new GroupType("Pathway");

	private String name;
	private boolean disallowLinks;
	private double mMargin;

	/**
	 * The constructor is private. GroupType cannot be directly instantiated. Use
	 * create() method to instantiate GroupType.
	 * 
	 * @param name the string key.
	 */
	private GroupType(String name) {
		this(name, false, DEFAULT_M_MARGIN);
	}

	/**
	 * The constructor is private. GroupType cannot be directly instantiated. Use
	 * create() method to instantiate GroupType.
	 * 
	 * @param name            the string key.
	 * @param disallowedLinks the boolean, if set to true nothing will be able to
	 *                        attach to this group.
	 */
	private GroupType(String name, boolean disallowLinks) {
		this(name, disallowLinks, DEFAULT_M_MARGIN);
	}

	/**
	 * The constructor is private. GroupType cannot be directly instantiated. Use
	 * create() method to instantiate GroupType.
	 *  
	 * @param name            the string key.
	 * @param disallowedLinks the boolean, if set to true nothing will be able to
	 *                        attach to this group.
	 * @param mMargin         the margin of group bounds.
	 * @throws NullPointerException if name is null.
	 */
	private GroupType(String name, boolean disallowLinks, double mMargin) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.disallowLinks = disallowLinks;
		this.mMargin = mMargin;
		groupTypes.add(this);
		nameToGroupType.put(name, this);
	}

	/**
	 * Creates a GroupType from a given string identifier name. New GroupType
	 * extends the enum.
	 * 
	 * @param name the string identifier.
	 * @return the new GroupType for given name.
	 */
	public static GroupType create(String name) {
		return new GroupType(name);
	}

	/**
	 * Creates a GroupType from a given name and given disallowLinks. New GroupType
	 * extends the enum.
	 * 
	 * @param name            the identifier for group type.
	 * @param disallowedLinks the boolean, if set to true nothing will be able to
	 *                        attach to this group.
	 * @return the new GroupType for given name.
	 */
	public static GroupType create(String name, boolean disallowLinks) {
		return new GroupType(name, disallowLinks);
	}

	/**
	 * Looks up the ConnectorType corresponding to that name.
	 */
	public static GroupType fromName(String value) {
		return nameToGroupType.get(value);
	}

	/**
	 * Returns the stable identifier for this GroupType.
	 * 
	 * @return name the stable identifier for this GroupType.
	 */
	public String getName() {
		return name;
	}

	public boolean isDisallowLinks() {
		return disallowLinks;
	}

	/**
	 * Returns the names of all registered GroupTypes as an array.
	 * 
	 * @return result the names of all registered GroupTypes.
	 */
	public static String[] getNames() {
		return nameToGroupType.keySet().toArray(new String[nameToGroupType.size()]);
	}

	/**
	 * Returns the group type values of all GroupTypes as an array.
	 * 
	 * @return the array of DataNodeTypes.
	 */
	static public GroupType[] getValues() {
		return groupTypes.toArray(new GroupType[0]);
	}

	/**
	 * Returns a string representation of this GroupType.
	 * 
	 * @return name the identifier of this GroupType.
	 */
	public String toString() {
		return name;
	}

	/**
	 * Compares string representations of given GroupType lexicographically.
	 * 
	 * @return positive number (integer difference of character value) if first
	 *         string is lexicographically greater than second string, negative
	 *         number if first string is less than second string lexicographically,
	 *         and 0 if first string is lexicographically equal to second string.
	 */
	public int compareTo(GroupType groupType) {
		return toString().compareTo(groupType.toString());
	}

	/**
	 * Gets the margin of group bounding-box around contained elements.
	 * 
	 * @returns the margin of group bounding-box.
	 */
	public double getMMargin() {
		return mMargin;
	}
}
