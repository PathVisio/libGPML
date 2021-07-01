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

	/* parent pathway model: may be null (e.g. when object is in clipboard) */
	private PathwayModel pathwayModel = null;
	private String elementId;

	/**
	 * Instantiates this pathway element given a parent pathway model and generates
	 * an elementId.
	 * 
	 * @param pathwayModel the parent pathway model.
	 * @param elementId    the unique pathway element identifier.
	 */
	public PathwayElement(PathwayModel pathwayModel, String elementId) {
		setPathwayModelTo(pathwayModel);
		this.elementId = elementId;
	}

	/**
	 * Returns the pathway model for this pathway element.
	 * 
	 * @return pathwayModel the parent pathway model.
	 */
	public PathwayModel getPathwayModel() {
		return pathwayModel;
	}

	/**
	 * Checks whether this pathway element belongs to a pathway model.
	 *
	 * @return true if and only if the pathwayNodel of this pathway element is
	 *         effective.
	 */
	public boolean hasPathwayModel() {
		return getPathwayModel() != null;
	}

	/**
	 * Sets the pathway model for this pathway element.
	 * 
	 * @param pathwayModel the parent pathway model.
	 */
	public void setPathwayModelTo(PathwayModel pathwayModel) throws IllegalArgumentException, IllegalStateException {
		if (pathwayModel == null)
			throw new IllegalArgumentException("Invalid pathway model.");
		if (hasPathwayModel())
			throw new IllegalStateException("Pathway element already belongs to a pathway model.");
		setPathwayModel(pathwayModel);
	}

	/**
	 * Sets the pathway model of this pathway element to the given pathway model.
	 * 
	 * @param pathwayModel the new pathway model for this pathway element.
	 */
	protected void setPathwayModel(PathwayModel pathwayModel) {
		assert (pathwayModel != null);
		this.pathwayModel = pathwayModel;
		pathwayModel.addElementId(elementId, this); // TODO 
	}

	/**
	 * Unsets the pathway model, if any, from this pathway element. The pathway
	 * element no longer belongs to a pathway model.
	 */
	public void unsetPathwayModel() {
		if (hasPathwayModel()) {
			PathwayModel formerPathwayModel = this.getPathwayModel();
			setPathwayModel(null);
			formerPathwayModel.removeElementId(elementId); //TODO
		}
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
	 * Terminates this pathway element. The pathway model, if any, is unset from
	 * this pathway element. The elementId of this pathway element is changed.
	 */
	public void terminate() {
		unsetPathwayModel();
		// At this point we cannot use the method setElementId,
		// because it does not accept null as a legal value.
		this.elementId = null;
	}

}
