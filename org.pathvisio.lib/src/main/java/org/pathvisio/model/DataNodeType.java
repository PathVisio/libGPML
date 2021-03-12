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
 * This class contains extensible enum for data node types.
 * 
 * @author unknown, finterly
 */
public class DataNodeType {
	private static final Map<String, DataNodeType> nameToDataNodeType = new HashMap<String, DataNodeType>();
	private static List<DataNodeType> dataNodeTypes = new ArrayList<DataNodeType>();

	public static final DataNodeType UNKNOWN = new DataNodeType("Unknown");
	public static final DataNodeType RNA = new DataNodeType("Rna");
	public static final DataNodeType PROTEIN = new DataNodeType("Protein");
	public static final DataNodeType COMPLEX = new DataNodeType("Complex");
	public static final DataNodeType GENEPRODUCT = new DataNodeType("GeneProduct");
	public static final DataNodeType METABOLITE = new DataNodeType("Metabolite");
	public static final DataNodeType PATHWAY = new DataNodeType("Pathway");

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
		dataNodeTypes.add(this); // adds this DataNodeType to list.
	}

	/**
	 * Returns a DataNodeType from a given string identifier name. If the
	 * DataNodeType doesn't exist yet, it is created to extend the enum. The create
	 * method makes sure that the same object is not added twice.
	 * 
	 * @param name the string identifier.
	 * @return the DataNodeType for given name. If name does not exist, creates and
	 *         returns a new DataNodeType.
	 */
	public static DataNodeType create(String name) {
		if (nameToDataNodeType.containsKey(name)) {
			return nameToDataNodeType.get(name);
		} else {
			return new DataNodeType(name);
		}
	}

	/**
	 * Returns the DataNodeType from given string value.
	 * 
	 * @param value the string.
	 * @return the DataNodeType with given string value.
	 */
	public static DataNodeType fromName(String value) {
		return nameToDataNodeType.get(value);
	}

	/**
	 * Returns the stable identifier for this DataNodeType.
	 * 
	 * @return name the stable identifier for this DataNodeType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the names of all registered DataNodeTypes as a String array.
	 * 
	 * @return result the names of all registered DataNodeTypes, in such a way that
	 *         the index is equal to it's ordinal value. i.e.
	 *         DataNodeType.fromName(DataNodeType.getNames[n]).getOrdinal() == n
	 */
	static public String[] getNames() {
		String[] result = new String[dataNodeTypes.size()];

		for (int i = 0; i < dataNodeTypes.size(); ++i) {
			result[i] = dataNodeTypes.get(i).getName();
		}
		return result;
	}

	/**
	 * Returns the data node type values of all DataNodeTypes as an array.
	 * 
	 * @return the array of DataNodeTypes.
	 */
	static public DataNodeType[] getValues() {
		return dataNodeTypes.toArray(new DataNodeType[0]);
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
