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
package org.pathvisio.model.elements;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.graphics.LineStyleProperty;

/**
 * This class stores information for an Interaction pathway element.
 * 
 * @author finterly
 */
public class Interaction extends LineElement {

	private Xref xref; // optional

	/**
	 * Instantiates an Interaction pathway element given all possible parameters.
	 * 
	 * @param elementId         the unique pathway element identifier.
	 * @param pathwayModel      the parent pathway model.
	 * @param points            the list of points.
	 * @param anchors           the list of anchors.
	 * @param lineStyleProperty the line style properties, e.g. lineColor.
	 * @param groupRef          the parent group in which the pathway element
	 *                          belongs.
	 * @param xref              the interaction Xref.
	 */
	public Interaction(String elementId, PathwayModel pathwayModel, LineStyleProperty lineStyleProperty, Group groupRef,
			Xref xref) {
		super(elementId, pathwayModel, lineStyleProperty, groupRef);
		this.xref = xref;
	}

	/**
	 * Instantiates an Interaction pathway element given all possible parameters
	 * except groupRef, because the interaction does not belong in a group.
	 */
	public Interaction(String elementId, PathwayModel pathwayModel, LineStyleProperty lineStyleProperty, Xref xref) {
		this(elementId, pathwayModel, lineStyleProperty, null, xref);
	}
	
	/**
	 * Instantiates an Interaction pathway element given all possible parameters
	 * except xref.
	 */
	public Interaction(String elementId, PathwayModel pathwayModel, LineStyleProperty lineStyleProperty, Group groupRef) {
		this(elementId, pathwayModel, lineStyleProperty, groupRef, null);
	}
	
	/**
	 * Instantiates an Interaction pathway element given all possible parameters
	 * except groupRef and xref.
	 */
	public Interaction(String elementId, PathwayModel pathwayModel, LineStyleProperty lineStyleProperty) {
		this(elementId, pathwayModel, lineStyleProperty, null, null);
	}


	/**
	 * Returns the Xref for the interaction.
	 * 
	 * @return xref the xref of interaction.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the interaction.
	 * 
	 * @param xref the xref of the interaction.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

	/**
	 * Instantiates data node Xref given identifier and dataSource. Checks whether
	 * dataSource string is fullName, systemCode, or invalid.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 * @throws IllegalArgumentException is given dataSource does not exist.
	 */
	public void createXref(String identifier, String dataSource) {
		if (DataSource.fullNameExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		} else if (DataSource.systemCodeExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getByAlias(dataSource));
		} else {
			DataSource.register(dataSource, dataSource);
			System.out.println("DataSource: " + dataSource + " is registered."); // TODO warning
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource)); // TODO fullname/code both ok
		}
	}

}
