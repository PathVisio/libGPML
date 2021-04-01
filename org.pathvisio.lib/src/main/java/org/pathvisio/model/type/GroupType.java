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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * This class contains extensible enum for Group type property. Groups can have
 * different biological meanings (e.g. protein Complex), and can be rendered in
 * different ways based on that.
 *
 * NB: group type previously named group style.
 * 
 * @author unknown, finterly
 */
public class GroupType implements Comparable<GroupType> {
	
	private static Map<String, GroupType> nameToGroupType = new LinkedHashMap<String, GroupType>();

	public static final GroupType NONE = new GroupType("None");
	public static final GroupType GROUP = new GroupType("Group");
	public static final GroupType COMPLEX = new GroupType("Complex"); //disallowLink = false, allow alias? 
	public static final GroupType PATHWAY = new GroupType("Pathway");

	private String name;

	/**
	 * The constructor is private. GroupType cannot be directly instantiated. Use
	 * create() method to instantiate GroupType.
	 * 
	 * @param name the string key.
	 */
	private GroupType(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToGroupType.put(name, this); // adds this name and GroupType to map.
	}


	/**
	 * Creates a GroupType from a given string identifier name. New GroupType
	 * extends the enum.
	 * 
	 * @param name the string identifier.
	 * @return the new GroupType for given name.
	 */
	public static GroupType register(String name) {
		if (nameToGroupType.containsKey(name)) {
			return nameToGroupType.get(name);
		} else {
			return new GroupType(name);
		}
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


	/**
	 * Returns the names of all registered GroupTypes as a list.
	 * 
	 * @return names the names of all registered GroupTypes in order of insertion.
	 */
	static public List<String> getNames() {
		List<String> names = new ArrayList<>(nameToGroupType.keySet());
		return names; 
	}

	/**
	 * Returns the group type values of all GroupTypes as a list.
	 * 
	 * @return groupTypes the list of all registered GroupTypes.
	 */
	static public List<GroupType> getValues() {
		List<GroupType> groupTypes = new ArrayList<>(nameToGroupType.values());
		return groupTypes; 
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

}
