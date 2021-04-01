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
 * This class contains extensible enum for state types.
 * 
 * @author finterly
 */
public class StateType {
	private static Map<String, StateType> nameToStateType = new LinkedHashMap<String, StateType>();

	/**
	 * Add more....
	 */
	public static final StateType PHOSPHORYLATED = new StateType("phosphorylated");

	private String name;

	/**
	 * The constructor is private. StateType cannot be directly instantiated. Use
	 * create() method to instantiate StateType.
	 * 
	 * @param name the string identifier of this StateType.
	 * @throws NullPointerException if name is null.
	 */
	private StateType(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToStateType.put(name, this); // adds this name and StateType to map.
	}

	/**
	 * Returns a StateType from a given string identifier name. If the StateType
	 * doesn't exist yet, it is created to extend the enum. The create method makes
	 * sure that the same object is not added twice.
	 * 
	 * @param name the string identifier.
	 * @return the StateType for given name. If name does not exist, creates and
	 *         returns a new StateType.
	 */
	public static StateType create(String name) {
		if (nameToStateType.containsKey(name)) {
			return nameToStateType.get(name);
		} else {
			return new StateType(name);
		}
	}

	/**
	 * Returns the StateType from given string value.
	 * 
	 * @param value the string.
	 * @return the StateType with given string value.
	 */
	public static StateType fromName(String value) {
		return nameToStateType.get(value);
	}

	/**
	 * Returns the stable identifier for this StateType.
	 * 
	 * @return name the stable identifier for this StateType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the names of all registered StateTypes as a list.
	 * 
	 * @return names the names of all registered StateTypes in order of insertion.
	 */
	static public List<String> getNames() {
		List<String> names = new ArrayList<>(nameToStateType.keySet());
		return names;
	}

	/**
	 * Returns the state type values of all StateTypes as a list.
	 * 
	 * @return stateTypes the list of akk registered StateTypes.
	 */
	static public List<StateType> getValues() {
		List<StateType> stateTypes = new ArrayList<>(nameToStateType.values());
		return stateTypes;
	}

	/**
	 * Returns a string representation of this StateType.
	 * 
	 * @return name the identifier of this StateType.
	 */
	public String toString() {
		return name;
	}
}
