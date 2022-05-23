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
import org.pathvisio.libgpml.model.LineElement;

/**
 * This class contains extensible enum pattern for different arrow head types. A
 * Line in PathVisio has two endings {@link LineElement.LinePoint} that each can have a
 * different arrow head.
 * 
 * NB: previously named LineType.
 * 
 * @author unknown, finterly
 */
public class ArrowHeadType {

	private static Map<String, ArrowHeadType> nameToArrowHeadType = new TreeMap<String, ArrowHeadType>(String.CASE_INSENSITIVE_ORDER);
	private static List<ArrowHeadType> values = new ArrayList<ArrowHeadType>();
	private static List<ArrowHeadType> visible = new ArrayList<ArrowHeadType>();
	
	public static final ArrowHeadType UNDIRECTED = new ArrowHeadType("Undirected"); //previous "Line" 
	public static final ArrowHeadType DIRECTED = new ArrowHeadType("Directed");
	public static final ArrowHeadType CONVERSION = new ArrowHeadType("Conversion");
	public static final ArrowHeadType INHIBITION = new ArrowHeadType("Inhibition");
	public static final ArrowHeadType CATALYSIS = new ArrowHeadType("Catalysis");
	public static final ArrowHeadType STIMULATION = new ArrowHeadType("Stimulation");
	public static final ArrowHeadType BINDING = new ArrowHeadType("Binding");
	public static final ArrowHeadType TRANSLOCATION = new ArrowHeadType("Translocation");
	public static final ArrowHeadType TRANSCRIPTION_TRANSLATION = new ArrowHeadType("TranscriptionTranslation");
	
	private String name;

	/**
	 * The constructor is private. ArrowHeadType cannot be directly instantiated.
	 * Use create() method to instantiate ArrowHeadType.
	 * 
	 * @param name the string key of this ArrowHeadType.
	 * @param hidden the boolean
	 * @throws NullPointerException if name is null.
	 */
	private ArrowHeadType(String name, boolean hidden) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToArrowHeadType.put(name, this); // adds this name and ArrowHeadType to map.
		values.add(this);
		if (!hidden)
			visible.add(this);
	}
	
	/**
	 * Private constructor. 
	 * 
	 * @param name the string key of this ArrowHeadType.
	 */
	private ArrowHeadType(String name) {
		this(name, false);
	}

	/**
	 * Returns a ArrowHeadType from a given string identifier name. If the
	 * ArrowHeadType doesn't exist yet, it is created to extend the enum. The 
	 * method makes sure that the same object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the ArrowHeadType for given name. If name does not exist, creates and
	 *         returns a new ArrowHeadType.
	 */
	public static ArrowHeadType register(String name) {
		if (nameToArrowHeadType.containsKey(name)) {
			return nameToArrowHeadType.get(name);
		} else {
			Logger.log.trace("Registered arrowhead type " + name); 
			return new ArrowHeadType(name);
		}
	}

	/**
	 * Returns the ArrowHeadType from given string name.
	 * 
	 * @param name the string.
	 * @return the ArrowHeadType with given string name.
	 */
	public static ArrowHeadType fromName(String name) {
		return nameToArrowHeadType.get(name);
	}

	/**
	 * Returns the name key for this ArrowHeadType.
	 * 
	 * @return name the key for this ArrowHeadType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the names of all registered ArrowHeadTypes as a list.
	 * 
	 * @return names the names of all registered ArrowHeadTypes in order of
	 *         insertion.
	 */
	static public String[] getNames() {
		return nameToArrowHeadType.keySet().toArray(new String[nameToArrowHeadType.size()]);
	}

	/**
	 * Returns the arrow head type values of all ArrowHeadTypes as a list.
	 * 
	 * @return arrowHead the list of all registered ArrowHeadTypes.
	 */
	static public ArrowHeadType[] getValues() {
		return nameToArrowHeadType.values().toArray(new ArrowHeadType[0]);
	}
	
	/**
	 * Returns an array of visible arrowheads name.  
	 * 
	 * @return the array of visible arrowhead names. 
	 */
	static public String[] getVisibleNames() {
		String[] result = new String[visible.size()];
		for (int i = 0; i < visible.size(); ++i) {
			result[i] = visible.get(i).getName();
		}
		return result;
	}

	/**
	 * Returns an array of visible arrowheads values.  
	 * 
	 * @return the array of visible arrowhead values. 
	 */
	static public ArrowHeadType[] getVisibleValues() {
		return visible.toArray(new ArrowHeadType[0]);
	}
	
	/**
	 * Returns a string representation of this ArrowHeadType. Adds space between
	 * lower and upper case letters to make more human readable.
	 * 
	 * @return name the identifier of this ArrowHeadType.
	 */
	public String toString() {
		return name.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
	}
}
