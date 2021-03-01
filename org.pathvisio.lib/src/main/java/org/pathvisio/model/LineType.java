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
 * This class contains extensible enum pattern for different arrow head types. A
 * Line in PathVisio has two endings that each can have a different arrow head
 * or "LineType".
 * 
 * NB: the name LineType is slightly misleading, as it refers strictly to
 * arrowheads and other kinds of line endings.
 * 
 * @author unknown, finterly
 */
public class LineType {
	private static Map<String, LineType> nameToLineType = new HashMap<String, LineType>();
	private static List<LineType> lineTypes = new ArrayList<LineType>();
	private static List<LineType> visibleLineTypes = new ArrayList<LineType>();

	/** LineType LINE means the absence of an arrowhead */
	public static final LineType LINE = new LineType("Line", "Line");
	public static final LineType ARROW = new LineType("Arrow", "Arrow");
	public static final LineType TBAR = new LineType("TBar", "TBar");

	@Deprecated
	public static final LineType RECEPTOR = new LineType("Receptor", "Receptor", true);
	@Deprecated
	public static final LineType LIGAND_SQUARE = new LineType("LigandSquare", "LigandSq", true);
	@Deprecated
	public static final LineType RECEPTOR_SQUARE = new LineType("ReceptorSquare", "ReceptorSq", true);
	@Deprecated
	public static final LineType LIGAND_ROUND = new LineType("LigandRound", "LigandRd", true);
	@Deprecated
	public static final LineType RECEPTOR_ROUND = new LineType("ReceptorRound", "ReceptorRd", true);

	private String mappName;
	private String name;

	/**
	 * The constructor is private. LineType cannot be directly instantiated. Use
	 * create() method to instantiate LineType.
	 * 
	 * @param name     the string identifier.
	 * @param mappName the string identifier. mappName may be null for new lines
	 *                 that don't have a .mapp equivalent.
	 */
	private LineType(String name, String mappName) {
		this(name, mappName, false);
	}

	/**
	 * The constructor is private. LineType cannot be directly instantiated. Use
	 * create() method to instantiate LineType.
	 * 
	 * @param name     the string identifier.
	 * @param mappName the string identifier. mappName may be null for new lines
	 *                 that don't have a .mapp equivalent.
	 * @param hidden   if false, ?????? is visible.
	 */
	private LineType(String name, String mappName, boolean hidden) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.mappName = mappName;
		this.name = name;
		nameToLineType.put(name, this);
		lineTypes.add(this);
		if (!hidden)
			visibleLineTypes.add(this);
	}

	/**
	 * Returns a LineType from a given name and given mappName. If the LineType
	 * doesn't exist yet, it is created to extend the enum. The create method makes
	 * sure that the same object is not added twice.
	 * 
	 * @param name     the string identifier.
	 * @param mappName the string identifier.
	 * @return a LineType object.
	 */
	public static LineType create(String name, String mappName) {
		if (nameToLineType.containsKey(name)) {
			return nameToLineType.get(name);
		} else {
			return new LineType(name, mappName);
		}
	}

	/**
	 * Gets mappname of LineType.
	 * 
	 * @return mappName the mappName of LineType.
	 */
	public String getMappName() {
		return mappName;
	}

	/**
	 * Returns the LineType from given string value.
	 * 
	 * @param value the string.
	 * @return the LineType with given string value.
	 */
	public static LineType fromName(String value) {
		return nameToLineType.get(value);
	}

	/**
	 * Returns the stable identifier for this LineType.
	 * 
	 * @return name the stable identifier for this LineType.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the names of all registered LineTypes as a String array.
	 * 
	 * @return the names of all registered LineTypes.
	 */
	static public String[] getNames() {
		return nameToLineType.keySet().toArray(new String[nameToLineType.size()]);
	}

	/**
	 * Returns the names of all visible???? registered LineTypes as a String array.
	 * 
	 * @return result the names of all registered visible LineTypes, in such a way
	 *         that the index is equal to it's ordinal value. i.e.
	 *         visibleLineTypes.fromName(visibleLineTypes.getNames[n]).getOrdinal()
	 *         == n
	 */
	static public String[] getVisibleNames() {
		String[] result = new String[visibleLineTypes.size()];
		for (int i = 0; i < visibleLineTypes.size(); ++i) {
			result[i] = visibleLineTypes.get(i).getName();
		}
		return result;
	}

	/**
	 * Returns the line type values of all LineTypes as an array.
	 * 
	 * @return the array of LineTypes.
	 */
	static public LineType[] getValues() {
		return nameToLineType.values().toArray(new LineType[nameToLineType.size()]);
	}

	/**
	 * Returns the line type values of all visible LineTypes as an array.
	 * 
	 * @return the array of visible LineTypes.
	 */
	static public LineType[] getVisibleValues() {
		return visibleLineTypes.toArray(new LineType[0]);
	}

	/**
	 * Returns a string representation of this LineType.
	 * 
	 * @return name the identifier of this LineType.
	 */
	public String toString() {
		return name;
	}
}
