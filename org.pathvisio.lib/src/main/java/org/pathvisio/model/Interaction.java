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

import org.bridgedb.Xref;

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
	 * @param xref the interaction Xref.
	 */
	public Interaction(Xref xref) {
		super();
		this.xref = xref;
	}

	/**
	 * Instantiates an Interaction pathway element given all possible parameters
	 * except xref.
	 */
	public Interaction() {
		this(null);
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

}
