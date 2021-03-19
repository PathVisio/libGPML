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
 * This class represents a generic point in an coordinates.length dimensional
 * space. The point is automatically a {@link ElementLink.ElementIdContainer} and
 * therefore lines can link to the point.
 * 
 * @see Point
 * @see Anchor
 * @author thomas, finterly
 *
 */
abstract class GenericPoint implements Cloneable, IElementIdContainer {
	
	private String elementId;
	private Coordinate coordinates;
	private Pathway parent;
	

	GenericPoint(Coordinate coordinates) {
		this.coordinates = coordinates;
	}


	/**
	 * Copy Constructor???
	 * 
	 * @param p
	 */
	GenericPoint(GenericPoint p) {
		coordinates = p.coordinates;
		if (p.elementId != null)
			elementId = p.elementId;
	}
	
	public Object clone() throws CloneNotSupportedException {
		GenericPoint p = (GenericPoint) super.clone();
		if (elementId != null)
			p.elementId = elementId;
		return p;
	}
	
//	/**
//	 * Copy Constructor 
//	 * 
//	 * @param p
//	 */
//	GenericPoint(GenericPoint p) {
//		coordinates = new double[p.coordinates.length];
//		System.arraycopy(p.coordinates, 0, coordinates, 0, coordinates.length);
//		if (p.elementId != null)
//			elementId = p.elementId;
//	}

	protected void moveBy(Coordinate delta) {
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] += delta[i];
		}
	}

	protected void setCoordinates(Coordinate coordinates) {
		this.coordinates = coordinates;
	}

	protected void moveTo(GenericPoint p) {
		coordinates = p.coordinates;
	}

//	protected double getCoordinate(int i) {
//		return coordinates[i];
//	}

	public String getElementId() {
		return elementId;
	}

	public String setGeneratedElementId() {
		setElementId(parent.getUniqueGraphId());
		return elementId;
	}

	public void setElementId(String v) {
		ElementLink.setElementId(v, this, PathwayElement.this.parent);
		elementId = v;
	}


	public Set<ElementRefContainer> getReferences() {
		return ElementLink.getReferences(this, parent);
	}

	public Pathway getPathway() {
		return parent;
	}

	public PathwayElement getParent() {
		return PathwayElement.this;
	}

}
