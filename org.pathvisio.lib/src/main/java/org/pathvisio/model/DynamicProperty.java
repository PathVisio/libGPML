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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class stores all information relevant to a dynamic property, or
 * CommentGroup.Property in GPML. Dynamic property is used to store arbitrary
 * data.
 * 
 * @author unknown, finterly
 */
public class DynamicProperty {

	/* -------------------------------- ADDED ---------------------------------- */

	// TreeMap has better performance than HashMap
	// in the (common) case where no attributes are present
	// This map should never contain non-null values, if a value
	// is set to null the key should be removed.

	/**
	 * Map for storing dynamic properties. Dynamic properties can have any String as
	 * key and value of type String. Dynamic properties represent
	 * CommentGroup.Property in gpml.
	 */
	private Map<String, String> dynamicProperties = new TreeMap<String, String>();

	/**
	 * Gets a set of all dynamic property keys.
	 * 
	 * @return a set of all dynamic property keys.
	 */
	public Set<String> getDynamicPropertyKeys() {
		return dynamicProperties.keySet();
	}

	/**
	 * Sets a dynamic property. Setting to null means removing this dynamic property
	 * altogether.
	 * 
	 * @param key   the key of a key value pair.
	 * @param value the value of a key value pair.
	 */
	public void setDynamicProperty(String key, String value) {
		if (value == null)
			dynamicProperties.remove(key);
		else
			dynamicProperties.put(key, value);
	}

	/**
	 * Gets a dynamic property string value.
	 * 
	 * @param key the key of a key value pair.
	 * @return the value or dynamic property.
	 */
	public String getDynamicProperty(String key) {
		return dynamicProperties.get(key);
	}

	/* ------------------------------- END ADDED ------------------------------- */

	private String key;
	private String value;

	/**
	 * Instantiates a Property, key value pair information.
	 * 
	 * @param key   the key of a key value pair.
	 * @param value the value of a key value pair.
	 */
	public DynamicProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Gets the key of a key value pair.
	 * 
	 * @return key the key of a key value pair.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of a key value pair.
	 * 
	 * @param key the key of a key value pair.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the value of a key value pair.
	 * 
	 * @return value the value of a key value pair.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of a key value pair.
	 * 
	 * @param value the value of a key value pair.
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
