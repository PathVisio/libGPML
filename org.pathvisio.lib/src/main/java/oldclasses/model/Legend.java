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
package oldclasses.model;

/**
 * This class stores all information relevant to a Legend pathway element.
 * 
 * @author finterly
 */
public class Legend {

	private double centerX;
	private double centerY;

	/**
	 * Instantiates a Legend pathway element.
	 * 
	 * @param centerX the middle of the legend in the x direction.
	 * @param centerY the middle of the legend in the y direction.
	 */
	public Legend(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
	}

	/**
	 * Gets the center x coordinate of the legend.
	 * 
	 * @return centerX the middle of the legend in the x direction.
	 */
	public double getCenterX() {
		return centerX;
	}

	/**
	 * Sets the center x coordinate of the legend.
	 * 
	 * @param centerX the middle of the legend in the x direction.
	 */
	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	/**
	 * Gets the center y coordinate of the legend.
	 * 
	 * @return centerY the middle of the legend in the y direction.
	 */
	public double getCenterY() {
		return centerY;
	}

	/**
	 * Sets the center y coordinate of the legend.
	 * 
	 * @param centerY the middle of the legend in the y direction.
	 * 
	 */
	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

}
