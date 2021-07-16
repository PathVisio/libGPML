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

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.Xref;
import org.pathvisio.model.graphics.FontProperty;
import org.pathvisio.model.graphics.RectProperty;
import org.pathvisio.model.graphics.ShapeStyleProperty;
import org.pathvisio.model.type.DataNodeType;

/**
 * This class stores information for a DataNode pathway element.
 * 
 * @author finterly
 */
public class DataNode extends ShapedElement {

	private String textLabel;
	private DataNodeType type = DataNodeType.UNDEFINED;
	private List<State> states;
	private Xref xref; // optional
	private PathwayElement elementRef; // optional, the pathway element to which the data node refers to as an alias.

	/**
	 * 
	 * Instantiates a Data Node pathway element given all possible parameters. The
	 * data node is an alias and refers to another pathway element. In GPML, the
	 * datanode has elementRef which refers to the elementId of a pathway element
	 * (normally gpml:Group).
	 * 
	 * @param rectProperty       the centering (position) and dimension properties.
	 * @param fontProperty       the font properties, e.g. textColor, fontName...
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor.
	 * @param textLabel          the text of the datanode.
	 * @param type               the type of datanode, e.g. complex.
	 * @param xref               the data node Xref.
	 * @param elementRef         the pathway element the data node refers to.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			String textLabel, DataNodeType type, Xref xref, PathwayElement elementRef) {
		super(rectProperty, fontProperty, shapeStyleProperty);
		this.textLabel = textLabel;
		this.type = type;
		this.states = new ArrayList<State>();
		this.xref = xref;
		this.elementRef = elementRef;
	}

	/**
	 * Instantiates a DataNode given all possible parameters except xref.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			String textLabel, DataNodeType type, PathwayElement elementRef) {
		this(rectProperty, fontProperty, shapeStyleProperty, textLabel, type, null, elementRef);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except elementRef,
	 * because the data node does not refer to another pathway element.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			String textLabel, DataNodeType type, Xref xref) {
		this(rectProperty, fontProperty, shapeStyleProperty, textLabel, type, xref, null);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except xref and
	 * elementRef.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			String textLabel, DataNodeType type) {
		this(rectProperty, fontProperty, shapeStyleProperty, textLabel, type, null, null);
	}

	/**
	 * Returns the text of of the datanode.
	 * 
	 * @return textLabel the text of of the datanode.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the datanode.
	 * 
	 * @param textLabel the text of of the datanode.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Returns the type of the datanode.
	 * 
	 * @return type the type of datanode, e.g. complex.
	 */
	public DataNodeType getType() {
		return type;
	}

	/**
	 * Sets the type of the datanode.
	 * 
	 * @param type the type of datanode, e.g. complex.
	 */
	public void setType(DataNodeType type) {
		this.type = type;
	}

	/**
	 * Returns the Xref for the data node.
	 * 
	 * @return xref the xref of the data node.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the data node.
	 * 
	 * @param xref the xref of the data node.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

	/*
	 * Returns the list of states of the data node.
	 * 
	 * @return states the list of states.
	 */
	public List<State> getStates() {
		return states;
	}

	/**
	 * Checks whether states has the given state.
	 * 
	 * @param state the state to look for.
	 * @return true if has state, false otherwise.
	 */
	public boolean hasState(State state) {
		return states.contains(state);
	}

	/**
	 * Adds given state to states list.
	 * 
	 * @param state the state to be added.
	 */
	public void addState(State state) {
		assert (state != null);
		state.setDataNodeTo(this); // TODO
		assert (state.getDataNode() == this);
		assert !hasState(state);
		// add state to same pathway model as data node if applicable
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayElement(state);
		states.add(state);
	}

	/**
	 * Removes given state from states list.
	 * 
	 * @param state the state to be removed.
	 */
	public void removeState(State state) {
		assert (state != null && hasState(state));
		if (getPathwayModel() != null)
			getPathwayModel().removePathwayElement(state);
		states.remove(state);
		state.terminate();
	}

	/**
	 * Removes all states from states list.
	 */
	public void removeStates() {
		for (int i = 0; i < states.size(); i++) {
			removeState(states.get(i));
		}
	}

	/**
	 * Returns the pathway element to which the data node refers to as an alias. In
	 * GPML, this is elementRef which refers to the elementId of a pathway element
	 * (normally gpml:Group). TODO
	 * 
	 * @return elementRef the pathway element to which the data node refers.
	 */
	public PathwayElement getElementRef() {
		return elementRef;
	}

	/**
	 * Sets the pathway element to which the data node refers to as an alias. In
	 * GPML, this is elementRef which refers to the elementId of a pathway element
	 * (normally gpml:Group). TODO
	 * 
	 * @param elementRef the pathway element to which the data node refers.
	 */
	public void setElementRef(PathwayElement elementRef) {
		this.elementRef = elementRef;
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
		// if data node has states, also add states to pathway model TODO
		for (State state : states)
			pathwayModel.addPathwayElement(state);
	}

	/**
	 * Unsets the pathway model, if any, from this pathway element. The pathway
	 * element no longer belongs to a pathway model. NB: This method is not used
	 * directly.
	 */
	protected void unsetPathwayModel() {
		if (hasPathwayModel()) {
			setPathwayModel(null);
			for (State state : states) // TODO
				state.setPathwayModel(null);
		}
	}

	/**
	 * Terminates this data node. The pathway model, if any, is unset from this data
	 * node. Links to all states, annotationRefs, citationRefs, and evidenceRefs are
	 * removed from this data node.
	 */
	@Override
	public void terminate() {
		removeStates();
		unsetPathwayModel();
		unsetGroupRef();
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();// TODO
	}

}
