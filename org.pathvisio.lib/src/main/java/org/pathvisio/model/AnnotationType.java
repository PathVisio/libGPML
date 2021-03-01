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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains extensible enum for annotation types.
 * 
 * @author unknown, finterly
 */
public class AnnotationType {
	private static Map<String, AnnotationType> nameToAnnotationType = new HashMap<String, AnnotationType>();
	private static List<AnnotationType> annotationTypes = new ArrayList<AnnotationType>();

	/**
	 * Add more....
	 */
	public static final AnnotationType SUBCELLULAR_LOCATION = new AnnotationType("Subcellular location");

	private String name;

	/**
	 * The constructor is private. AnnotationType cannot be directly instantiated. Use
	 * create() method to instantiate AnnotationType.
	 *  
	 * @param name the string identifier of this AnnotationType.
	 * @throws NullPointerException if name is null.
	 */
	private AnnotationType(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToAnnotationType.put(name, this); // adds this name and AnnotationType to map.
		annotationTypes.add(this); // adds this AnnotationType to list.
	}

	/**
	 * Returns a AnnotationType from a given string identifier name. If the
	 * AnnotationType doesn't exist yet, it is created to extend the enum. The create
	 * method makes sure that the same object is not added twice.
	 * 
	 * @param name the string identifier.
	 * @return the AnnotationType for given name. If name does not exist, creates and
	 *         returns a new AnnotationType.
	 */
	public static AnnotationType create(String name) {
		if (nameToAnnotationType.containsKey(name)) {
			return nameToAnnotationType.get(name);
		} else {
			return new AnnotationType(name);
		}
	}

	/**
	 * Returns the AnnotationType from given string value.
	 * 
	 * @param value the string.
	 * @return the AnnotationType with given string value.
	 */
	public static AnnotationType fromName(String value) {
		return nameToAnnotationType.get(value);
	}

	/**
	 * Returns the stable identifier for this AnnotationType.
	 * 
	 * @return name the stable identifier for this AnnotationType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the names of all registered AnnotationTypes as a String array.
	 * 
	 * @return result the names of all registered AnnotationTypes, in such a way that
	 *         the index is equal to it's ordinal value. i.e.
	 *         AnnotationType.fromName(AnnotationType.getNames[n]).getOrdinal() == n
	 */
	static public String[] getNames() {
		String[] result = new String[annotationTypes.size()];

		for (int i = 0; i < annotationTypes.size(); ++i) {
			result[i] = annotationTypes.get(i).getName();
		}
		return result;
	}

	/**
	 * Returns the annotation type values of all AnnotationTypes as an array.
	 * 
	 * @return the array of AnnotationTypes.
	 */
	static public AnnotationType[] getValues() {
		return annotationTypes.toArray(new AnnotationType[0]);
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
