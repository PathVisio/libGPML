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
 * The properties in {@link StaticProperty} define properties of different
 * types, all the possible types are defined here.
 * 
 * @author unknown, finterly
 */
public enum StaticPropertyType implements PropertyType {
	// basic types
	BOOLEAN, DOUBLE, INTEGER, STRING, COLOR,

	// Pathway
	ORGANISM, DESCRIPTION, AUTHOR, 

	// Pathway Element
	COMMENT, ANNOTATIONREF, CITATIONREF, EVIDENCEREF, ANNOTATIONTYPE,

	// Xrefable
	XREF, DATASOURCE, //TODO

	// ShapedElement
	DATANODETYPE, STATETYPE, GROUPTYPE,
	
	// DataNode
	ALIASREF,

	// LineElement
	ARROWHEADTYPE, LINKABLETO, ANCHORSHAPETYPE, CONNECTORTYPE,

	// Style properties
	FONTNAME, HALIGNTYPE, VALIGNTYPE, SHAPETYPE, LINESTYLETYPE, // for both lines and shape borders 
	ROTATION; // for state and group

	private String id;

	private StaticPropertyType() {
		id = "core." + name();
		PropertyManager.registerPropertyType(this);
	}

	public String getId() {
		return id;
	}
}
