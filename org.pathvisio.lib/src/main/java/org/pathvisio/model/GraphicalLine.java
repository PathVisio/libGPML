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

import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.DataNode.State;

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
	// Copy Methods
	// ================================================================================
	/**
	 * Note: doesn't change parent, only fields
	 *
	 * Used by UndoAction.
	 *
	 * @param src
	 */
	public void copyValuesFrom(State src) { //TODO
		super.copyValuesFrom(src);
		fireObjectModifiedEvent(PathwayElementEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public GraphicalLine copy() {
		GraphicalLine result = new GraphicalLine(); //TODO 
		result.copyValuesFrom(this);
		return result;
	}

}
