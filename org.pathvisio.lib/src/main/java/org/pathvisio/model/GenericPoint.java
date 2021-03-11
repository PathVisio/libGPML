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
abstract class GenericPoint implements Cloneable, ElementLink.ElementIdContainer {
	private double[] coordinates;

	private String graphId;

	GenericPoint(double[] coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * Copy Constructor 
	 * 
	 * @param p
	 */
	GenericPoint(GenericPoint p) {
		coordinates = new double[p.coordinates.length];
		System.arraycopy(p.coordinates, 0, coordinates, 0, coordinates.length);
		if (p.graphId != null)
			graphId = p.graphId;
	}

	protected void moveBy(double[] delta) {
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] += delta[i];
		}
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(PathwayElement.this));
	}

	protected void moveTo(double[] coordinates) {
		this.coordinates = coordinates;
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(PathwayElement.this));
	}

	protected void moveTo(GenericPoint p) {
		coordinates = p.coordinates;
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(PathwayElement.this));
		;
	}

	protected double getCoordinate(int i) {
		return coordinates[i];
	}

	public String getElementId() {
		return graphId;
	}

	public String setGeneratedElementId() {
		setElementId(parent.getUniqueGraphId());
		return graphId;
	}

	public void setElementId(String v) {
		ElementLink.setElementId(v, this, PathwayElement.this.parent);
		graphId = v;
		fireObjectModifiedEvent(
				PathwayElementEvent.createSinglePropertyEvent(PathwayElement.this, StaticProperty.GRAPHID));
	}

	public Object clone() throws CloneNotSupportedException {
		GenericPoint p = (GenericPoint) super.clone();
		if (graphId != null)
			p.graphId = graphId;
		return p;
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
