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
package org.pathvisio.libgpml.model;

import org.jdom2.Namespace;

/**
 * Read / write GPML files. Base implementation for different GpmlFormat
 * versions. Code that is shared between multiple versions is located here.
 */
public abstract class Gpml2021FormatAbstract extends GpmlFormatAbstract {

	/**
	 * Constructor for GPML2021Format Abstract.
	 *
	 * @param xsdFile the schema file.
	 * @param nsGPML  the GPML namespace.
	 */
	protected Gpml2021FormatAbstract(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

	/**
	 * Default values necessary for reading (when validation off)
	 */
	public final static String BACKGROUNDCOLOR_DEFAULT = "ffffff";
	public final static String GROUPTYPE_DEFAULT = "Group";
	public final static String DATANODETYPE_DEFAULT = "Undefined";
	public final static String STATETYPE_DEFAULT = "Undefined";
	public final static String ARROWHEAD_DEFAULT = "Undirected";
	public final static String ANCHORSHAPETYPE_DEFAULT = "Square";
	public final static String ANNOTATIONTYPE_DEFAULT = "Undefined";
	// font properties
	public final static String TEXTCOLOR_DEFAULT = "000000";
	public final static String FONTNAME_DEFAULT = "Arial";
	public final static String FONTWEIGHT_DEFAULT = "Normal";
	public final static String FONTSTYLE_DEFAULT = "Normal";
	public final static String FONTDECORATION_DEFAULT = "Normal";
	public final static String FONTSTRIKETHRU_DEFAULT = "Normal";
	public final static String FONTSIZE_DEFAULT = "12";
	public final static String HALIGN_DEFAULT = "Center";
	public final static String VALIGN_DEFAULT = "Middle";
	// shape style properties
	public final static String BORDERCOLOR_DEFAULT = "000000";
	public final static String BORDERSTYLE_DEFAULT = "Solid";
	public final static String BORDERWIDTH_DEFAULT = "1.0";
	public final static String FILLCOLOR_DEFAULT = "ffffff";
	public final static String SHAPETYPE_DEFAULT = "Rectangle";
	// line style properties
	public final static String LINECOLOR_DEFAULT = "000000";
	public final static String LINESTYLE_DEFAULT = "Solid";
	public final static String LINEWIDTH_DEFAULT = "1.0";
	public final static String CONNECTORTYPE_DEFAULT = "Straight";

}
