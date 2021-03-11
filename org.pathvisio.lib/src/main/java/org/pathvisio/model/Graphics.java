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

import java.awt.Color;

/**
 * This interface stores all information relevant to Graphics.
 * 
 * @author finterly
 */
public interface Graphics {


	public boolean isValidColor();
    
	
	/**
	 * Checks if fill color is equal to null or the alpha value is equal to 0.
	 * 
	 * @return true if fill color equal to null or alpha value equal to 0, false
	 *         otherwise.
	 */
	public boolean isTransparent() {
		return fillColor == null || fillColor.getAlpha() == 0;
	}
	/**
	 * TODO: Logic seems weird...
	 * 
	 * Sets the alpha component of fillColor to 0 if true, sets the alpha component
	 * of fillColor to 255 if false.
	 * 
	 * @param value the boolean value.
	 */
	public void setTransparent(boolean value) {
		if (isTransparent() != value) {
			if (fillColor == null) {
				fillColor = Color.WHITE;
			}
			int alpha = value ? 0 : 255;
			fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), alpha);
		}
	}
}
