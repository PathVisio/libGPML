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
 * This class contains extensible enum for Group type property. Groups can have
 * different biological meanings (e.g. protein Complex), and can be rendered in
 * different ways based on that.
 *
 * TODO: Add group type="Transparent" to GPML Schema  
 * 
 * NB: group type previously named group style.
 * 
 * @author unknown, finterly
 */
public class GroupType {

	private static Map<String, GroupType> nameToGroupType = new TreeMap<String, GroupType>(
			String.CASE_INSENSITIVE_ORDER);

	public static final GroupType GROUP = new GroupType("Group"); // default: replaces "NONE" of 2013a
	public static final GroupType TRANSPARENT = new GroupType("Transparent"); // replaces "GROUP" of 2013a
	public static final GroupType COMPLEX = new GroupType("Complex");
	public static final GroupType PATHWAY = new GroupType("Pathway");
	public static final GroupType ANALOG = new GroupType("Analog");
	public static final GroupType PARALOG = new GroupType("Paralog");

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
	 * Returns a GroupType from a given string identifier name. If the GroupType
	 * doesn't exist yet, it is created to extend the enum. The method makes sure
	 * that the same object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the GroupType for given name. If name does not exist, creates and
	 *         returns a new GroupType.
	 */
	public static GroupType register(String name) {
		if (nameToGroupType.containsKey(name)) {
			return nameToGroupType.get(name);
		} else {
			Logger.log.trace("Registered group type " + name);
			return new GroupType(name);
		}
	}

	/**
	 * Returns the name key for this GroupType.
	 * 
	 * @return name the key for this GroupType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Looks up the ConnectorType corresponding to that name.
	 */
	public static GroupType fromName(String name) {
		return nameToGroupType.get(name);
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
