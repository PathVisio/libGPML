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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains possible values for lineStyle or borderStyle property. Line style can
 * be either solid, dashed or double. Used for lineStyle {@link LineStyleProperty} 
 * and borderStyle {@link ShapeStyleProperty}. 
 * 
 * @author unknown, finterly
 */
/**
 * This class contains extensible enum for Anchor types.
 * 
 * @author unknown, finterly
 */
public class LineStyleType {

	private static Map<String, LineStyleType> nameToLineStyleType = new HashMap<String, LineStyleType>();
	private static List<LineStyleType> lineStyleTypes = new ArrayList<LineStyleType>();

	/**
	 * Add more....
	 */
	public static final LineStyleType SOLID = new LineStyleType("Solid");
	public static final LineStyleType DASHED = new LineStyleType("Dashed");
	public static final LineStyleType DOUBLE = new LineStyleType("Double");

	private String name;

	/**
	 * The constructor is private. LineStyleType cannot be directly instantiated.
	 * Use create() method to instantiate LineStyleType.
	 * 
	 * @param name the string identifier of this LineStyleType.
	 * @throws NullPointerException if name is null.
	 */
	private LineStyleType(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToLineStyleType.put(name, this); // adds this name and LineStyleType to map.
		lineStyleTypes.add(this); // adds this LineStyleType to list.
	}

	/**
	 * Returns a LineStyleType from a given string identifier name. If the
	 * LineStyleType doesn't exist yet, it is created to extend the enum. The create
	 * method makes sure that the same object is not added twice.
	 * 
	 * @param name the string identifier.
	 * @return the LineStyleType for given name. If name does not exist, creates and
	 *         returns a new LineStyleType.
	 */
	public static LineStyleType create(String name) {
		if (nameToLineStyleType.containsKey(name)) {
			return nameToLineStyleType.get(name);
		} else {
			return new LineStyleType(name);
		}
	}

	/**
	 * Returns the LineStyleType from given string value.
	 * 
	 * @param value the string.
	 * @return the LineStyleType with given string value.
	 */
	public static LineStyleType fromName(String value) {
		return nameToLineStyleType.get(value);
	}

	/**
	 * Returns the stable identifier for this LineStyleType.
	 * 
	 * @return name the stable identifier for this LineStyleType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the names of all registered LineStyleTypes as a String array.
	 * 
	 * @return result the names of all registered LineStyleTypes, in such a way that
	 *         the index is equal to it's ordinal value. i.e.
	 *         LineStyleType.fromName(LineStyleType.getNames[n]).getOrdinal() == n
	 */
	static public String[] getNames() {
		String[] result = new String[lineStyleTypes.size()];

		for (int i = 0; i < lineStyleTypes.size(); ++i) {
			result[i] = lineStyleTypes.get(i).getName();
		}
		return result;
	}

	/**
	 * Returns the annotation type values of all LineStyleTypes as an array.
	 * 
	 * @return the array of LineStyleTypes.
	 */
	static public LineStyleType[] getValues() {
		return lineStyleTypes.toArray(new LineStyleType[0]);
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