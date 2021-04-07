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
 * This class contains extensible enum for Annotation type property.
 * 
 * @author finterly
 */
public class AnnotationType {
	private static Map<String, AnnotationType> nameToAnnotationType = new LinkedHashMap<String, AnnotationType>();

	// TODO Add more
	public static final AnnotationType UNDEFINED = new AnnotationType("Undefined");
	public static final AnnotationType ONTOLOGY = new AnnotationType("Ontology");
	public static final AnnotationType TAXONOMY = new AnnotationType("Taxonomy");
//	public static final AnnotationType PATHWAY = new AnnotationType("Pathway");
//	public static final AnnotationType DISEASE = new AnnotationType("Disease");
//	public static final AnnotationType CELL_TYPE = new AnnotationType("Cell type");

	private String name;

	/**
	 * The constructor is private. AnnotationType cannot be directly instantiated.
	 * Use create() method to instantiate AnnotationType.
	 * 
	 * @param name the string key of this AnnotationType.
	 * @throws NullPointerException if name is null.
	 */
	private AnnotationType(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToAnnotationType.put(name, this); // adds this name and AnnotationType to map.
	}

	/**
	 * Returns an AnnotationType from a given string identifier name. If the
	 * AnnotationType doesn't exist yet, it is created to extend the enum. The
	 * method makes sure that the same object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the AnnotationType for given name. If name does not exist, creates
	 *         and returns a new AnnotationType.
	 */
	public static AnnotationType register(String name) {
		if (nameToAnnotationType.containsKey(name)) {
			return nameToAnnotationType.get(name);
		} else {
			return new AnnotationType(name);
		}
	}

	/**
	 * Returns the name key for this AnnotationType.
	 * 
	 * @return name the key for this AnnotationType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the AnnotationType from given string name.
	 * 
	 * @param name the string.
	 * @return the AnnotationType with given string name.
	 */
	public static AnnotationType fromName(String name) {
		return nameToAnnotationType.get(name);
	}

	/**
	 * Returns the names of all registered AnnotationTypes as a String array.
	 * 
	 * @return names the names of all registered AnnotationTypes in order of
	 *         insertion.
	 */
	static public List<String> getNames() {
		List<String> names = new ArrayList<>(nameToAnnotationType.keySet());
		return names;
	}

	/**
	 * Returns the annotation type values of all AnnotationTypes as a list.
	 * 
	 * @return annotationTypes the list of all registered AnnotationTypes.
	 */
	static public List<AnnotationType> getValues() {
		List<AnnotationType> annotationTypes = new ArrayList<>(nameToAnnotationType.values());
		return annotationTypes;
	}

	/**
	 * Returns a string representation of this AnnotationType.
	 * 
	 * @return name the identifier of this AnnotationType.
	 */
	public String toString() {
		return name;
	}
}