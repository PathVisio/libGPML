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

import java.util.HashMap;
import java.util.Map;

/**
 * This enum class contains possible values for horizontal text alignment
 * property.
 * 
 * @author unknown, finterly
 */
public enum HAlignType {

	LEFT("Left"), CENTER("Center"), RIGHT("Right");

	private final String name;
	// Initialize map
	private static Map<String, HAlignType> nameToHAlignType = new HashMap<String, HAlignType>();

	/**
	 * Inserts mappings into map by associating specified values with specified
	 * keys.
	 */
	static {
		for (HAlignType t : values())
			nameToHAlignType.put(t.name, t);
	}

	/**
	 * Constructor to initialize the state of enum types.
	 * 
	 * @param name the string.
	 */
	private HAlignType(String name) {
		this.name = name;
	}

	/**
	 * Returns String.
	 * 
	 * @return name the string value.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns HAlignType enum constant from given string name.
	 * 
	 * @param value the string value.
	 * @return HAlignType enum constant.
	 */
	public static HAlignType fromName(String value) {
		return nameToHAlignType.get(value);
	}

	/**
	 * Returns String values as an array.
	 * 
	 * @return result the string array.
	 */
	public static String[] getNames() {
		String[] result = new String[values().length];
		for (int i = 0; i < values().length; ++i)
			result[i] = values()[i].name;
		return result;
	}

	/**
	 * Returns a string representation of this HAlignType.
	 * 
	 * @return name the identifier of this HAlignType.
	 */
	public String toString() {
		return name;
	}

}
