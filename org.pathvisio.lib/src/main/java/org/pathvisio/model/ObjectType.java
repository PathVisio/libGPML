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

import java.util.HashMap;
import java.util.Map;

/**
 * This enum class contains possible values for PathwayElement.getObjectType(),
 * e.g. "DataNode", "State", "Shape".
 * 
 * @author unknown, finterly
 */
public enum ObjectType {
	/** Any shape with width and height */
	SHAPE("Shape"),

	/** A connector. Can be straight, or can consist of multiple line segments */
	GRAPHLINE("GraphicalLine"),

	/** A rectangle that contains a link to an online biological database */
	DATANODE("DataNode"),

	/** A piece of text */
	LABEL("Label"),

	/**
	 * A connector. Can be straight, or can consist of multiple line segments and
	 * can contain a that contains a link to an online biological database
	 */
	LINE("Line"),

	/**
	 * Zero or one per pathway. Placeholder object to let visualization plugins draw
	 * a legend
	 */
	LEGEND("Legend"),

	/** One per pathway. TODO: unused. */
	INFOBOX("InfoBox"),

	/** The pathway description, one per pathway. In GPML this is the root tag */
	MAPPINFO("Pathway"),

	/** A grouping of pathway elements */
	GROUP("Group"),

	/** A pool of BioPAX definitions */
	BIOPAX("Biopax"),

	/**
	 * Similar to DataNode, but State is always attached to - and specified relative
	 * to - another DataNode
	 */
	STATE("State");
	
	/** 
	 * New Object Types:
	 * Anchor?
	 * Point?
	 * ...
	 */

	private String tag;
	static private final Map<String, ObjectType> tagToObjectType = new HashMap<String, ObjectType>();

	/**
	 * Inserts mappings into map by associating specified values with specified
	 * keys.
	 */
	static {
		for (ObjectType object : ObjectType.values()) {
			tagToObjectType.put(object.tag, object);
		}
	}

	/**
	 * Constructor to initialize the state of enum types.
	 * 
	 * @param tag the tag used in gpml for this object type.
	 */
	private ObjectType(String tag) {
		this.tag = tag;
	}

	/**
	 * Returns the ObjectType that corresponds to a certain tag. Returns null if no
	 * such ObjectType exists.
	 * 
	 * @param value the String value.
	 * @return the ObjectType that corresponds to given tag value. If no
	 *         such ObjectType exists, returns null. 
	 */
	public static ObjectType getTagMapping(String value) {
		if (tagToObjectType.containsKey(value)) {
			return tagToObjectType.get(value);
		} else {
			return null;
		}
	}

	/**
	 * Returns the GPML tag corresponding to this object type, can also function as
	 * a human-readable description.
	 * 
	 * @return tag the gpml tag corresponding to this object type.
	 */
	public String getTag() {
		return tag;
	}
}
