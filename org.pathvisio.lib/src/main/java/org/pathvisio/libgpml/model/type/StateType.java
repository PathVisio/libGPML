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
 * This class contains extensible enum for state types.
 * 
 * NB: State type is in the GPML2013a schema, but was never implemented/written
 * for GPML2013a files.
 * 
 * @author finterly
 */
public class StateType {
	private static Map<String, StateType> nameToStateType = new TreeMap<String, StateType>(
			String.CASE_INSENSITIVE_ORDER);

	// TODO Add more and changes
	public static final StateType UNDEFINED = new StateType("Undefined");
	public static final StateType PROTEIN_MODIFICATION = new StateType("ProteinModification");
	public static final StateType GENETIC_VARIANT = new StateType("GeneticVariant");
	public static final StateType EPIGENETIC_MODIFICATION = new StateType("EpigeneticModification");

	// (something for metabolites?)
	// gene modification?

	private String name;

	/**
	 * The constructor is private. StateType cannot be directly instantiated. Use
	 * create() method to instantiate StateType.
	 * 
	 * @param name the key of this StateType.
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
	 * doesn't exist yet, it is created to extend the enum. The method makes sure
	 * that the same object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the StateType for given name. If name does not exist, creates and
	 *         returns a new StateType.
	 */
	public static StateType register(String name) {
		if (nameToStateType.containsKey(name)) {
			return nameToStateType.get(name);
		} else {
			Logger.log.trace("Registered state type " + name);
			return new StateType(name);
		}
	}

	/**
	 * Returns the name key for this StateType.
	 * 
	 * @return name the key for this StateType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the StateType from given string name.
	 * 
	 * @param name the string key.
	 * @return the StateType with given string name.
	 */
	public static StateType fromName(String name) {
		return nameToStateType.get(name);
	}

	/**
	 * Returns the names of all registered StateTypes as an array.
	 * 
	 * @return names the names of all registered StateTypes in order of insertion.
	 */
	static public String[] getNames() {
		return nameToStateType.keySet().toArray(new String[nameToStateType.size()]);
	}

	/**
	 * Returns the state type values of all StateTypes as an array.
	 * 
	 * @return stateTypes the list of all registered StateTypes.
	 */
	static public StateType[] getValues() {
		return nameToStateType.values().toArray(new StateType[0]);
	}

	/**
	 * Returns a string representation of this StateType. Adds space between lower
	 * and upper case letters to make more human readable.
	 * 
	 * @return name the identifier of this StateType.
	 */
	public String toString() {
//		return name.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2"); //TODO 
		return name;
	}
}
