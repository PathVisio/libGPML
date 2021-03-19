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
 * This class stores information for the InfoBox. InfoBox holds the xy
 * coordinates for where information, e.g. name and organism, are displayed in
 * the pathway. Is Pathway.InfoBox or gpml:InfoBox in GPML.
 * 
 * @author finterly
 */
public class InfoBox {

	private Coordinate centerXY;

	/**
	 * Instantiates a InfoBox pathway element.
	 * 
	 * @param centerXY the middle of the info box in the x and y direction.
	 */
	public InfoBox(Coordinate centerXY) {
		this.centerXY = centerXY;
	}

	/**
	 * Instantiates a InfoBox pathway element with default coordinates x = 0, y = 0.
	 */
	public InfoBox() {
		this.centerXY = new Coordinate(0, 0);
	}

	/**
	 * Gets the center x and y coordinate of the info box.
	 * 
	 * @return centerXY the middle of the info box.
	 */
	public Coordinate getCenterXY() {
		return centerXY;
	}

	/**
	 * Sets the center x and y coordinate of the info box.
	 * 
	 * @param centerXY the middle of the info box.
	 */
	public void setCenterXY(Coordinate centerXY) {
		this.centerXY = centerXY;
	}

}
