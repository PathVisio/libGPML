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
package org.pathvisio.model.element;

import org.pathvisio.model.graphics.LineStyleProperty;

/**
 * This class stores information for GraphicalLine pathway element.
 * 
 * @author finterly
 */
public class GraphicalLine extends LineElement {

	/**
	 * Instantiates a GraphicalLine pathway element given all possible parameters.
	 * 
	 * @param pathwayModel      the parent pathway model.
	 * @param elementId         the unique pathway element identifier.
	 * @param lineStyleProperty the line style properties, e.g. lineColor.
	 */
	public GraphicalLine(LineStyleProperty lineStyleProperty) {
		super(lineStyleProperty);
	}

}
