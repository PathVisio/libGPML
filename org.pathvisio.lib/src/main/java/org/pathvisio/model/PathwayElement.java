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
 * Abstract class of pathway elements which are part of a pathway and have an
 * elementId.
 * 
 * Children: DataNode, State, Interaction, GraphicalLine, Label, Shape, Group,
 * Anchor, Point, Annotation, Citation, and Evidence.
 * 
 * @author finterly
 */
public abstract class PathwayElement {

	private String elementId;
	/* parent pathway model: may be null (e.g. when object is in clipboard) */
	private PathwayModel pathwayModel = null;

	/**
	 * Instantiates this pathway element with generated elementId and given parent
	 * pathway model.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 */
	public PathwayElement(String elementId, PathwayModel pathwayModel) {
		this.elementId = elementId; 
		pathwayModel.addElementId(elementId, this); // TODO Setter
		this.pathwayModel = pathwayModel;
	}

	/**
	 * Returns the elementId of the pathway element.
	 * 
	 * @return elementId the unique pathway element identifier.
	 */
	public String getElementId() {
		return elementId;
	}

	// TODO CLEAN UP METHOD

	/**
	 * Sets the elementId of the pathway element. ElementId must be a unique within
	 * the Pathway model. {@link PathwayModel#getUniqueId}
	 * {@link PathwayModel#addElementId} {@link PathwayModel#removeElementId}
	 * 
	 * @param newElementId the given elementId to set
	 */
	public void setElementId(String newElementId) {
		if (elementId == null || !elementId.equals(newElementId)) {
			if (pathwayModel != null) {
				if (elementId != null) {
					pathwayModel.removeElementId(elementId);
				}
				if (newElementId != null) {
					pathwayModel.addElementId(newElementId, this);
				}
				this.elementId = newElementId;
			}
		}
	}

	public String setGeneratedElementId() {
		setElementId(pathwayModel.getUniqueElementId());
		return elementId;
	}

	/**
	 * Returns the parent pathway model.
	 * 
	 * @return pathwayModel the parent pathway model.
	 */
	public PathwayModel getPathwayModel() {
		return pathwayModel;
	}

	/**
	 * Sets the parent pathway model.
	 * 
	 * @param pathwayModel the parent pathway model.
	 */
	public void setPathwayModel(PathwayModel pathwayModel) {
		this.pathwayModel = pathwayModel;
	}


}
