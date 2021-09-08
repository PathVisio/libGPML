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

import java.util.HashSet;
import java.util.Set;

import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.events.PathwayElementListener;
import org.pathvisio.props.StaticProperty;

/**
 * Abstract class of pathway elements which are part of a pathway and have an
 * elementId.
 * 
 * Children: DataNode, State, Interaction, GraphicalLine, Label, Shape, Group,
 * Anchor, Point, Annotation, Citation, and Evidence.
 * 
 * @author finterly
 */
public abstract class PathwayObject {

	/* parent pathway model: may be null (e.g. when object is in clipboard) */
	private PathwayModel pathwayModel;
	private String elementId;

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates a pathway element. Parent pathway model and elementId are set
	 * through {@link PathwayModel} add pathway element methods. elementId.
	 */
	public PathwayObject() {
	}

	// ================================================================================
	// Accessors
	// ================================================================================
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
	 * Sets the pathway model for this pathway element. NB: Only set when a pathway
	 * model adds this pathway element. This method is not used directly.
	 * 
	 * @param pathwayModel the parent pathway model.
	 */
	protected void setPathwayModelTo(PathwayModel pathwayModel) throws IllegalArgumentException, IllegalStateException {
		if (pathwayModel == null)
			throw new IllegalArgumentException("Invalid pathway model.");
		if (hasPathwayModel())
			throw new IllegalStateException("Pathway element already belongs to a pathway model.");
		setPathwayModel(pathwayModel);
	}

	/**
	 * Sets the pathway model of this pathway element to the given pathway model.
	 * NB: This method is not used directly.
	 * 
	 * @param pathwayModel the new pathway model for this pathway element.
	 */
	protected void setPathwayModel(PathwayModel pathwayModel) {
		this.pathwayModel = pathwayModel;
	}

	/**
	 * Unsets the pathway model, if any, from this pathway element. The pathway
	 * element no longer belongs to a pathway model. NB: This method is not used
	 * directly.
	 */
	protected void unsetPathwayModel() {
		if (hasPathwayModel())
			setPathwayModel(null);
	}

	/**
	 * Returns the elementId of the pathway element.
	 * 
	 * @return elementId the unique pathway element identifier.
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the pathway element.
	 * 
	 * @param v the unique pathway element identifier.
	 */
	public void setElementId(String v) {
		elementId = v;
		fireObjectModifiedEvent(
				PathwayElementEvent.createSinglePropertyEvent(PathwayObject.this, StaticProperty.ELEMENTID));
	}

	/**
	 * Sets the elementId to generated elementId from pathwayModel.
	 */
	public void setGeneratedElementId() {
		setElementId(pathwayModel.getUniqueElementId());
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

	// ================================================================================
	// FireEvent and Listener Methods
	// ================================================================================
	int noFire = 0;

	/**
	 * @param times
	 */
	public void dontFireEvents(int times) {
		noFire = times;
	}

	private Set<PathwayElementListener> listeners = new HashSet<PathwayElementListener>();

	/**
	 * @param v
	 */
	public void addListener(PathwayElementListener v) {
		if (!listeners.contains(v))
			listeners.add(v);
	}

	/**
	 * @param v
	 */
	public void removeListener(PathwayElementListener v) {
		listeners.remove(v);
	}

	/**
	 * @param e
	 */
	public void fireObjectModifiedEvent(PathwayElementEvent e) {
		if (noFire > 0) {
			noFire -= 1;
			return;
		}
		if (pathwayModel != null)
			pathwayModel.childModified(e);
		for (PathwayElementListener g : listeners) {
			g.gmmlObjectModified(e);
		}
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
//	/**
//	 * Note: doesn't change parent, only fields TODO 
//	 *
//	 * Used by UndoAction.
//	 *
//	 * @param src the source pathway object 
//	 */
//	public abstract void copyValuesFrom(PathwayObject src);

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public abstract PathwayObject copy();

}
