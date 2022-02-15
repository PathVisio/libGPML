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
 * Possible values for PathwayElement.getObjectType(), such as "DataNode" or
 * "Shape"
 */
public enum ObjectType {

	/** The pathway description, one per pathway. In GPML this is the root tag. */
	PATHWAY("Pathway"),

	/**
	 * A data node pathway element denotes a biological entity that forms a node in
	 * a pathway.
	 */
	DATANODE("DataNode"),

	/**
	 * A state pathway element represents a specific state of the biological entity.
	 * A state is linked to a data node.
	 */
	STATE("State"),

	/**
	 * A connector pathway element which represents biological relation between
	 * entities.
	 */
	INTERACTION("Interaction"),

	/** A connector pathway element without semantic meaning. */
	GRAPHLINE("GraphicalLine"),
	
	/** A point on a line pathway element. TODO */ 
	LINEPOINT("Point"),
	
	/** An anchor point on a line pathway element. TODO */
	ANCHOR("Anchor"),

	/** A pathway element to attach simple labels to the pathway. */
	LABEL("Label"),

	/** A graphical pathway element with or without a text label. */
	SHAPE("Shape"),

	/** A pathway element grouping of other pathway elements. */
	GROUP("Group"),

	/** A reference with additional information, e.g. some Ontology. */
	ANNOTATION("Annotation"),

	/** A reference to a source of information. */
	CITATION("Citation"),

	/** A evidence provides information on type of scientific evidence. */
	EVIDENCE("Evidence");

	private String tag;

	static private final Map<String, ObjectType> TAG_MAP = new HashMap<String, ObjectType>();

	static {
		for (ObjectType o : ObjectType.values()) {
			TAG_MAP.put(o.tag, o);
		}
	}

	/**
	 * Private constructor for object type.
	 * 
	 * @param aTag tag used in Gpml for this object type.
	 */
	private ObjectType(String aTag) {
		tag = aTag;
	}

	/**
	 * Return the ObjectType that corresponds to a certain tag. Returns null if no
	 * such ObjectType exists.
	 * 
	 * @param value the string value.
	 * @return the object type for given string.
	 */
	public static ObjectType getTagMapping(String value) {
		if (TAG_MAP.containsKey(value)) {
			return TAG_MAP.get(value);
		} else {
			return null;
		}
	}

	/**
	 * Returns the GPML tag corresponding to this object type, can also function as
	 * a human-readable description.
	 * 
	 * @return the string value for object type.
	 */
	public String getTag() {
		return tag;
	}
}
