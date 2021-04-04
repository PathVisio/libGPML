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
package temp;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum class contains possible values for the cellular component property.
 * These values describe the biological meaning of an element. Values do not
 * always correlate with a particular appearance.
 * 
 * @author unknown, finterly
 */
public enum CellularComponentType {
	/** */
	NONE("None"), 
	/** */
	CELL("Cell"),
	/** */
	NUCLEUS("Nucleus"),
	/** */
	MITOCHONDRIA("Mitochondria"), 	
	/** */
	GOLGIAPPARATUS("Golgi Apparatus"),
	/** */
	ENDOPLASMICRETICULUM("Endoplasmic Reticulum"), 
	/** */
	SARCOPLASMICRETICULUM("Sarcoplasmic Reticulum"), 
	/** */
	VESICLE("Vesicle"),
	/** */
	ORGANELLE("Organelle"), 
	/** */
	NUCLEOLUS("Nucleolus"), 
	/** */
	VACUOLE("Vacuole"), 
	/** */
	LYSOSOME("Lysosome"), 
	/** */
	CYTOSOL("Cytosol region"),
	/** */
	EXTRACELLULAR("Extracellular region"), 
	/** */
	MEMBRANE("Membrane region");

	private final String name;
	private static Map<String, CellularComponentType> nameToCellularComponentType = new HashMap<String, CellularComponentType>();

	/**
	 * Temporary Dynamic Property for cellular component TODO: refactor as Static
	 * Property with next GPML update
	 */
	public final static String CELL_COMPONENT_KEY = "org.pathvisio.CellularComponentProperty";

	/**
	 * 
	 */
	public static final PropertyType CELL_COMPONENT_TYPE = new PropertyType() {
		public String getId() {
			return "core.CellularComponentType";
		}
	};

	/**
	 * 
	 */
	public static final Property CELL_COMPONENT_PROPERTY = new Property() {
		public String getId() {
			return CELL_COMPONENT_KEY;
		}

		public String getDescription() {
			return "This property associates Shapes with cellular component terms";
		}

		public String getName() {
			return "Cellular Component";
		}

		public PropertyType getType() {
			return CELL_COMPONENT_TYPE;
		}

		public boolean isCollection() {
			return false;
		}
	};

	/**
	 * Inserts mappings into map by associating specified values with specified
	 * keys.
	 */
	static {
		for (CellularComponentType cellularComponentType : values())
			nameToCellularComponentType.put(cellularComponentType.name, cellularComponentType);
	}

	/**
	 * Constructor to initialize the state of enum types.
	 * 
	 * @param name the string identifier of this DataNodeType.
	 */
	private CellularComponentType(String name) {
		this.name = name;
	}

	/**
	 * Returns the CellularComponentType from given string value.
	 * 
	 * @param value the string value.
	 * @return the CellularComponentType with given string value.
	 */
	public static CellularComponentType fromName(String value) {
		return nameToCellularComponentType.get(value);
	}

	/**
	 * Returns the stable identifier for this CellularComponentType.
	 * 
	 * @return name the stable identifier for this CellularComponentType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the names of all registered CellularComponentTypes as an array.
	 * 
	 * @return result the names of all registered CellularComponentTypes.
	 */
	public static String[] getNames() {
		String[] result = new String[values().length];
		for (int i = 0; i < values().length; ++i)
			result[i] = values()[i].name;
		return result;
	}

	/**
	 * Returns a string representation of this CellularComponentType.
	 * 
	 * @return name the identifier of this CellularComponentType.
	 */
	public String toString() {
		return name;
	}

}
