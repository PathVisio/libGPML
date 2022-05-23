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
package org.pathvisio.libgpml.prop;

/**
 * This interface defines a typed property.
 *
 * @author Mark Woon
 */
public interface Property {

	/**
	 * Returns the Id for this property. Ids must be unique.
	 * 
	 * @return the id for this property.
	 */
	String getId();

	/**
	 * Returns the name of property, used e.g. as row header in the properties
	 * table.
	 * 
	 * @return the name.
	 */
	String getName();

	/**
	 * Description of property, used e.g. as tooltip text when mousing over the
	 * properties table. Descriptions are optional.
	 * 
	 * @return description. May return null.
	 */
	String getDescription();

	/**
	 * Returns the data type for this property.
	 * 
	 * @return the data property type.
	 */
	PropertyType getType();

	/**
	 * Gets whether this property has accepts values.
	 * 
	 * @return true if this property accepts values.
	 */
	boolean isCollection();
}
