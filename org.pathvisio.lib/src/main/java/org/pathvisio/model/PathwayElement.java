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
		this.elementId = setGeneratedElementId(); // TODO Setter
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

//	/* ------------------------------- GROUPREF ------------------------------- */
//	public boolean isValidElementId(String elementId) {
//		if (elementId == null && isUniqueElementId(elementId) == true) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//	protected String groupRef;
//
//	/**
//	 * 
//	 * @return
//	 */
//	public String getGroupRef() {
//		return groupRef;
//	}
//
//	/**
//	 * 
//	 * @param s
//	 */
//	public void setGroupRef(String id) {
//		if (groupRef == null || !groupRef.equals(id)) {
//			if (pathwayModel != null) {
//				if (groupRef != null) {
//					pathwayModel.removeGroupRef(groupRef, this);
//				}
//				// Check: move add before remove??
//				if (id != null) {
//					pathwayModel.addGroupRef(id, this);
//				}
//			}
//			groupRef = id;
//		}
//	}
//
//	/* ------------------------------- GROUPID ------------------------------- */
//
//	protected String groupId;
//
//	public String getGroupId() {
//		return groupId;
//	}
//
//	public String createGroupId() {
//		if (groupId == null) {
//			setGroupId(pathwayModel.getUniqueGroupId());
//		}
//		return groupId;
//	}
//
//	/**
//	 * Set groupId. This id must be any string unique within the Pathway object
//	 *
//	 * @see Pathway#getUniqueId(java.util.Set)
//	 */
//	public void setGroupId(String id) {
//		if (groupId == null || !groupId.equals(id)) {
//			if (pathwayModel != null) {
//				if (groupId != null) {
//					pathwayModel.removeGroupId(groupId);
//				}
//				// Check: move add before remove??
//				if (id != null) {
//					pathwayModel.addGroupId(id, this);
//				}
//			}
//			groupId = id;
//		}
//	}

	/* ------------------------------- ELEMENTID ------------------------------- */

	/* ------------------------------- ELEMENTREF ------------------------------- */
//
//
//	/**
//	 * Return a list of ElementRefContainers (i.e. points) referring to this pathway
//	 * element.
//	 */
//	public Set<ElementRefContainer> getReferences() {
//		return ElementLink.getReferences(this, pathwayModel);
//	}
//
//	/** elementRef property, used by Modification */
//	public String getElementRef() {
//		return elementRef;
//	}
//
//	/**
//	 * Set graphRef property, used by State The new graphRef should exist and point
//	 * to an existing DataNode
//	 */
//	public void setElementRef(String value) {
//		// TODO: check that new elementRef exists and that it points to a DataNode
//		if (!(elementRef == null ? value == null : elementRef.equals(value))) {
//			elementRef = value;
//		}
//	}

}
