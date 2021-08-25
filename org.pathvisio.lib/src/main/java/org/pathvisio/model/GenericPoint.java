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

import java.util.Set;

/**
 * Abstract class of generic point, extended by {@link LinePoint} and
 * {@link Anchor}.
 * 
 * @author unknown, finterly
 */
public abstract class GenericPoint extends PathwayElement {

	private LineElement lineElement; // is set when line adds point

	/**
	 * Constructor for a generic point.
	 */
	public GenericPoint() {
		super();
	}

	/**
	 * Returns the parent interaction or graphicalLine for this point.
	 * 
	 * @return lineElement the parent line element of this point.
	 */
	public LineElement getLineElement() {
		return lineElement;
	}

	/**
	 * Checks whether this point has a parent line element.
	 *
	 * @return true if and only if the line element of this point is effective.
	 */
	public boolean hasLineElement() {
		return getLineElement() != null;
	}

	/**
	 * Sets the parent interaction or graphicalLine for this point. NB: Only set
	 * when a line adds this point. This method is not used directly.
	 * 
	 * @param lineElement the line element to set.
	 */
	protected void setLineElementTo(LineElement lineElement) {
		if (lineElement == null)
			throw new IllegalArgumentException("Invalid line pathway element.");
		if (hasLineElement())
			throw new IllegalStateException("Point already belongs to a line element.");
		setLineElement(lineElement);
	}

	/**
	 * Sets the parent interaction or graphicalLine for this point. NB: This method
	 * is not used directly.
	 * 
	 * @param lineElement the line element to set.
	 */
	private void setLineElement(LineElement lineElement) {
		this.lineElement = lineElement;
	}

	/**
	 * Unsets the line element, if any, from this point. NB: This method is not used
	 * directly.
	 */
	protected void unsetLineElement() {
		if (hasLineElement())
			setLineElement(null);
	}
	


}
