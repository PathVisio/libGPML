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

import org.pathvisio.libgpml.model.DataNode.State;
import org.pathvisio.libgpml.model.type.ObjectType;

/**
 * This class stores information for GraphicalLine pathway element.
 *
 * @author finterly
 */
public class GraphicalLine extends LineElement {

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates a GraphicalLine pathway element given all possible parameters.
	 */
	public GraphicalLine() {
		super();
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the object type of this pathway element.
	 *
	 * @return the object type.
	 */
	@Override
	public ObjectType getObjectType() {
		return ObjectType.GRAPHLINE;
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Copies values from the given source pathway element.
	 *
	 * @param src the source pathway element.
	 */
	public void copyValuesFrom(State src) {
		super.copyValuesFrom(src);
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copies this pathway element.
	 *
	 * @return the copyElement for the new pathway element and this source pathway
	 *         element.
	 */
	@Override
	public CopyElement copy() {
		GraphicalLine result = new GraphicalLine();
		result.copyValuesFrom(this);
		return new CopyElement(result, this);
	}

}
