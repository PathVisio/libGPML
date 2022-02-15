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
 * This class contains extensible enum for different connectorType property.
 *
 * @author unknown, finterly
 */
public class ConnectorType {

	private static Map<String, ConnectorType> nameToConnectorType = new TreeMap<String, ConnectorType>(
			String.CASE_INSENSITIVE_ORDER);

	public static final ConnectorType STRAIGHT = new ConnectorType("Straight"); // DEFAULT
	public static final ConnectorType ELBOW = new ConnectorType("Elbow");
	public static final ConnectorType CURVED = new ConnectorType("Curved");
	public static final ConnectorType SEGMENTED = new ConnectorType("Segmented"); // has waypoints

	private String name;

	/**
	 * The constructor is private. ConnectorType cannot be directly instantiated.
	 * Use create() method to instantiate ConnectorType.
	 * 
	 * @param name the string key.
	 * @throws NullPointerException if name is null.
	 */
	private ConnectorType(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToConnectorType.put(name, this);
	}

	/**
	 * Returns a ConnectorType from a given name. If the ConnectorType doesn't exist
	 * yet, it is created to extend the enum. The method makes sure that the same
	 * object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the ConnectorType for given name or new ConnectorType if name does
	 *         not exist.
	 */
	public static ConnectorType register(String name) {
		if (nameToConnectorType.containsKey(name)) {
			return nameToConnectorType.get(name);
		} else
			Logger.log.trace("Registered connector type " + name);
		return new ConnectorType(name);
	}

	/**
	 * Returns the name key for this ConnectorType.
	 * 
	 * @return name the key for this ConnectorType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the ConnectorType from given string name.
	 * 
	 * @param name the string.
	 * @return the ConnectorType with given string name.
	 */
	public static ConnectorType fromName(String name) {
		return nameToConnectorType.get(name);
	}

	/**
	 * Returns the names of all registered ConnectorTypes as a String array.
	 * 
	 * @return names the names of all registered ConnectorTypes in order of
	 *         insertion.
	 */
	static public List<String> getNames() {
		List<String> names = new ArrayList<>(nameToConnectorType.keySet());
		return names;
	}

	/**
	 * Returns the connector type values of all ConnectorTypes as a list.
	 * 
	 * @return connectorTypes the list of all registered ConnectorTypes.
	 */
	static public List<ConnectorType> getValues() {
		List<ConnectorType> connectorTypes = new ArrayList<>(nameToConnectorType.values());
		return connectorTypes;
	}

	/**
	 * Returns a string representation of this ConnectorType.
	 * 
	 * @return name the identifier of this ConnectorType.
	 */
	public String toString() {
		return name;
	}

	/**
	 * Compares string representations of given ConnectorType lexicographically.
	 * 
	 * @param connectorType
	 * @return positive number (integer difference of character value) if first
	 *         string is lexicographically greater than second string, negative
	 *         number if first string is less than second string lexicographically,
	 *         and 0 if first string is lexicographically equal to second string.
	 */
	public int compareTo(ConnectorType connectorType) {
		return toString().compareTo(connectorType.toString());
	}
}
