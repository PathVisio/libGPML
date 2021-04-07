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
 * This class contains extensible enum for DataNode type property.
 * 
 * @author unknown, finterly
 */
public class DataNodeType {

	private static final Map<String, DataNodeType> nameToDataNodeType = new LinkedHashMap<String, DataNodeType>();

	public static final DataNodeType UNKNOWN = new DataNodeType("Unknown");
	public static final DataNodeType RNA = new DataNodeType("Rna");
	public static final DataNodeType PROTEIN = new DataNodeType("Protein");
	public static final DataNodeType COMPLEX = new DataNodeType("Complex");
	public static final DataNodeType GENEPRODUCT = new DataNodeType("GeneProduct");
	public static final DataNodeType METABOLITE = new DataNodeType("Metabolite");
	public static final DataNodeType PATHWAY = new DataNodeType("Pathway");
	
	// TODO Add?
	public static final DataNodeType DISEASE = new DataNodeType("Disease");
	public static final DataNodeType PHENOTYPE = new DataNodeType("Phenotype");
	public static final DataNodeType ALIAS = new DataNodeType("Alias");
	public static final DataNodeType DNA = new DataNodeType("DNA");
	public static final DataNodeType EVENT = new DataNodeType("Event");

	private String name;

	/**
	 * The constructor is private. DataNodeType cannot be directly instantiated. Use
	 * create() method to instantiate DataNodeType.
	 * 
	 * @param name the string identifier of this DataNodeType.
	 * @throws NullPointerException if name is null.
	 */
	private DataNodeType(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		nameToDataNodeType.put(name, this); // adds this name and DataNodeType to map.
	}

	/**
	 * Returns a DataNodeType from a given string identifier name. If the
	 * DataNodeType doesn't exist yet, it is created to extend the enum. The method
	 * makes sure that the same object is not added twice.
	 * 
	 * @param name the string key.
	 * @return the DataNodeType for given name. If name does not exist, creates and
	 *         returns a new DataNodeType.
	 */
	public static DataNodeType register(String name) {
		if (nameToDataNodeType.containsKey(name)) {
			return nameToDataNodeType.get(name);
		} else {
			return new DataNodeType(name);
		}
	}

	/**
	 * Returns the name key for this DataNodeType.
	 * 
	 * @return name the key for this DataNodeType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the DataNodeType from given string name.
	 * 
	 * @param name the string.
	 * @return the DataNodeType with given string name.
	 */
	public static DataNodeType fromName(String name) {
		return nameToDataNodeType.get(name);
	}

	/**
	 * Returns the names of all registered DataNodeTypes as a String array.
	 * 
	 * @return names the names of all registered DataNodeTypes in order of
	 *         insertion.
	 */
	static public List<String> getNames() {
		List<String> names = new ArrayList<>(nameToDataNodeType.keySet());
		return names;
	}

	/**
	 * Returns the data node type values of all DataNodeTypes as a list.
	 * 
	 * @return dataNodeTypes the list of all registered DataNodeTypes.
	 */
	static public List<DataNodeType> getValues() {
		List<DataNodeType> dataNodeTypes = new ArrayList<>(nameToDataNodeType.values());
		return dataNodeTypes;
	}

	/**
	 * Returns a string representation of this DataNodeType.
	 * 
	 * @return name the identifier of this DataNodeType.
	 */
	public String toString() {
		return name;
	}
}