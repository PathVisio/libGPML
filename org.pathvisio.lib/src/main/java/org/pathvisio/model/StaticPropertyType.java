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

/**
 * This enum class defines properties of different types. All the possible types
 * are defined here.
 * 
 * @author unknown, finterly
 */
public enum StaticPropertyType implements PropertyType {
	/** */
	BOOLEAN,
	/** */
	DOUBLE,
	/** */
	INTEGER,
	/** */
	DATASOURCE,
	/** */
	LINESTYLE,
	/** */
	COLOR,
	/** */
	STRING,
	/** */
	ORIENTATION,
	/** */
	SHAPETYPE,
	/** */
	LINETYPE,
	/** */
	OUTLINETYPE,
	/** */
	GENETYPE,
	/** */
	FONT,
	/** */
	ANGLE,
	/** */
	ORGANISM,
	/** */
	DB_ID,
	/** */
	DB_SYMBOL,
	/** */
	BIOPAXREF,
	/** */
	COMMENTS,
	/** TODO: remove */
	GROUPSTYLETYPE,
	/** */
	GROUPTYPE,
	/** TODO: remove */
	ALIGNTYPE,
	/** */
	HALIGNTYPE,
	/** */
	VALIGNTYPE;

	private String id;

	/**
	 * Constructor to initialize the state of enum types.
	 */
	private StaticPropertyType() {
		id = "core." + name();
		PropertyManager.registerPropertyType(this);
	}

	/**
	 * Returns the id of this StaticPropertyType.
	 * 
	 * @return id the identifier.
	 */
	public String getId() {
		return id;
	}
}
