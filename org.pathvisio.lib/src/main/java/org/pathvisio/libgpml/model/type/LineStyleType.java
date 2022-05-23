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

import java.util.Map;
import java.util.TreeMap;

import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.model.LineElement;
import org.pathvisio.libgpml.model.ShapedElement;

/**
 * This class contains extensible enum for lineStyle or borderStyle property.
 * Line style can be either solid, dashed or double. Used for lineStyle of
 * {@link LineElement} or borderStyle of {@link ShapedElement}.
 * 
 * @author unknown, finterly
 */
public class LineStyleType {

	private static Map<String, LineStyleType> nameToLineStyleType = new TreeMap<String, LineStyleType>(
			String.CASE_INSENSITIVE_ORDER);

	// TODO Add dotted?
	public static final LineStyleType SOLID = new LineStyleType("Solid"); // DEFAULT
	public static final LineStyleType DASHED = new LineStyleType("Dashed"); // NB: renamed from "Broken"
	public static final LineStyleType DOUBLE = new LineStyleType("Double");

	private String name;

	/**
	 * The constructor is private. LineStyleType cannot be directly instantiated.
	 * Use create() method to instantiate LineStyleType.
	 * 
	 * @param name the string key of this LineStyleType.
	 * @throws NullPointerException if string name is null.
	 */
	private LineStyleType(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToLineStyleType.put(name, this); // adds this name and LineStyleType to map.
	}

	/**
	 * Returns a LineStyleType from a given string identifier name. If the
	 * LineStyleType doesn't exist yet, it is created to extend the enum. The method
	 * makes sure that the same object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the LineStyleType for given name. If name does not exist, registers
	 *         and returns a new LineStyleType.
	 */
	public static LineStyleType register(String name) {
		if (nameToLineStyleType.containsKey(name)) {
			return nameToLineStyleType.get(name);
		} else {
			Logger.log.trace("Registered linestyle type " + name);
			return new LineStyleType(name);
		}
	}

	/**
	 * Returns the name key for this LineStyleType.
	 * 
	 * @return name the key for this LineStyleType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the LineStyleType from given string name.
	 * 
	 * @param name the string.
	 * @return the LineStyleType with given string name.
	 */
	public static LineStyleType fromName(String name) {
		return nameToLineStyleType.get(name);
	}

	/**
	 * Returns the names of all registered LineStyleTypes as a String array.
	 * 
	 * @return names the names of all registered LineStyleTypes in order of
	 *         insertion.
	 */
	static public String[] getNames() {
		return nameToLineStyleType.keySet().toArray(new String[nameToLineStyleType.size()]);
	}

	/**
	 * Returns the line style type values of all LineStyleType as an array.
	 * 
	 * @return lineStyleTypes the list of all registered LineStyleType.
	 */
	static public LineStyleType[] getValues() {		
		return nameToLineStyleType.values().toArray(new LineStyleType[0]);
	}

	/**
	 * Returns a string representation of this LineStyleType.
	 * 
	 * @return name the identifier of this LineStyleType.
	 */
	public String toString() {
		return name;
	}
}