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
 * This class contains extensible enum for different connector types.
 *
 * @author unknown, finterly
 */
public class ConnectorType implements Comparable<ConnectorType> {
	private static Map<String, ConnectorType> nameToConnectorType = new HashMap<String, ConnectorType>();
	private static Set<ConnectorType> connectorTypes = new TreeSet<ConnectorType>();

	/**
	 * Straight connector type is the shortest path between two points.
	 */
	public static final ConnectorType STRAIGHT = new ConnectorType("Straight");
	/**
	 * Elbow connector type connects with horizontal or vertical segments and 90
	 * degree angles.
	 */
	public static final ConnectorType ELBOW = new ConnectorType("Elbow");
	/**
	 * Curved connector type uses splines to generate a smooth curve while keeping
	 * the end points perpendicular to the connecting element.
	 */
	public static final ConnectorType CURVED = new ConnectorType("Curved");
	/**
	 * Segmented connector type consists of segments connected at waypoints.
	 */
	public static final ConnectorType SEGMENTED = new ConnectorType("Segmented");

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
		connectorTypes.add(this);
	}

	/**
	 * Returns a ConnectorType from a given name. If the ConnectorType doesn't exist
	 * yet, it is created to extend the enum.The create method makes sure that the
	 * same object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the ConnectorType for given name or new ConnectorType if name does
	 *         not exist.
	 */
	public static ConnectorType create(String name) {
		if (nameToConnectorType.containsKey(name)) {
			return nameToConnectorType.get(name);
		} else
			return new ConnectorType(name);
	}

	/**
	 * Returns the stable identifier for this ConnectorType.
	 * 
	 * @return name the stable identifier for this ConnectorType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the names of all registered ConnectorTypes as an array.
	 * 
	 * @return result the names of all registered ConnectorTypes.
	 */
	static public ConnectorType[] getValues() {
		return connectorTypes.toArray(new ConnectorType[0]);
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
	 * @return positive number (integer difference of character value) if first
	 *         string is lexicographically greater than second string, negative
	 *         number if first string is less than second string lexicographically,
	 *         and 0 if first string is lexicographically equal to second string.
	 */
	public int compareTo(ConnectorType connectorType) {
		return toString().compareTo(connectorType.toString());
	}
}
