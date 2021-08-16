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
import org.pathvisio.model.GraphLink.LinkableTo;
import org.pathvisio.model.graphics.FontProperty;
import org.pathvisio.model.graphics.RectProperty;
import org.pathvisio.model.graphics.ShapeStyleProperty;
import org.pathvisio.model.type.DataNodeType;

/**
 * This class stores information for a DataNode pathway element.
 * 
 * @author finterly
 */
public class DataNode extends ShapedElement implements Rotatable {

	private double rotation; // optional, in radians
	private String textLabel;
	private DataNodeType type = DataNodeType.UNDEFINED;
	private List<State> states;
	private Xref xref; // optional
	private Group aliasRef; // optional, the pathway element to which the data node refers to as an alias.

	/**
	 * 
	 * Instantiates a Data Node pathway element given all possible parameters. The
	 * data node is an alias and refers to another pathway element. In GPML, the
	 * datanode has aliasRef which refers to the elementId of a pathway element
	 * (normally gpml:Group).
	 * 
	 * @param rectProperty       the centering (position) and dimension properties.
	 * @param fontProperty       the font properties, e.g. textColor, fontName...
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor.
	 * @param rotation           the rotation of shape in radians.
	 * @param textLabel          the text of the datanode.
	 * @param type               the type of datanode, e.g. complex.
	 * @param xref               the data node Xref.
	 * @param aliasRef           the group the data node alias refers to.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			double rotation, String textLabel, DataNodeType type, Xref xref, Group aliasRef) {
		super(rectProperty, fontProperty, shapeStyleProperty);
		this.rotation = rotation;
		this.textLabel = textLabel;
		this.type = type;
		this.states = new ArrayList<State>();
		this.xref = xref;
		setAliasRefTo(aliasRef);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except rotation.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			String textLabel, DataNodeType type, Xref xref, Group aliasRef) {
		this(rectProperty, fontProperty, shapeStyleProperty, 0, textLabel, type, xref, aliasRef);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except xref.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			double rotation, String textLabel, DataNodeType type, Group aliasRef) {
		this(rectProperty, fontProperty, shapeStyleProperty, rotation, textLabel, type, null, aliasRef);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except rotation and
	 * xref.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			String textLabel, DataNodeType type, Group aliasRef) {
		this(rectProperty, fontProperty, shapeStyleProperty, 0, textLabel, type, null, aliasRef);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except aliasRef.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			double rotation, String textLabel, DataNodeType type, Xref xref) {
		this(rectProperty, fontProperty, shapeStyleProperty, rotation, textLabel, type, xref, null);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except rotation and
	 * aliasRef.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			String textLabel, DataNodeType type, Xref xref) {
		this(rectProperty, fontProperty, shapeStyleProperty, 0, textLabel, type, xref, null);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except xref and
	 * aliasRef.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			double rotation, String textLabel, DataNodeType type) {
		this(rectProperty, fontProperty, shapeStyleProperty, rotation, textLabel, type, null, null);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except rotation, xref,
	 * and aliasRef.
	 */
	public DataNode(RectProperty rectProperty, FontProperty fontProperty, ShapeStyleProperty shapeStyleProperty,
			String textLabel, DataNodeType type) {
		this(rectProperty, fontProperty, shapeStyleProperty, 0, textLabel, type, null, null);
	}

	/**
	 * Returns the rotation of this data node.
	 * 
	 * @return rotation the rotation of the data node.
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Sets the rotation of this data node.
	 * 
	 * @param rotation the rotation of the data node.
	 */
	public void setRotation(Double rotation) {
		this.rotation = rotation;
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
	 * Adds given state to states list. Sets dataNode for the given state.
	 * 
	 * @param state the state to be added.
	 */
	public void addState(State state) {
		assert (state != null);
		state.setDataNodeTo(this);
		assert (state.getDataNode() == this);
		assert !hasState(state);
		// add state to same pathway model as data node if applicable
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayElement(state);
		states.add(state);
	}

	/**
	 * Removes given state from states list. State ceases to exist and is
	 * terminated.
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
	 * GPML, this is aliasRef which refers to the elementId of a pathway element
	 * (normally gpml:Group). TODO
	 * 
	 * @return aliasRef the pathway element to which the data node refers.
	 */
	public Group getAliasRef() {
		return aliasRef;
	}

	/**
	 * Checks whether this data node has an aliasRef.
	 *
	 * @return true if and only if the aliasRef of this data node is effective.
	 */
	public boolean hasAliasRef() {
		return getAliasRef() != null;
	}

	/**
	 * Sets the group aliasRef to which the data node refers to as an alias. In
	 * GPML, this is aliasRef which refers to the elementId of gpml:Group.
	 * 
	 * @param aliasRef the group to which the data node refers.
	 */
	public void setAliasRefTo(Group aliasRef) {
		if (aliasRef == null)
			throw new IllegalArgumentException("Invalid aliasRef.");
		unsetAliasRef(); // first unsets if necessary
		setAliasRef(aliasRef);
		getPathwayModel().addAlias(aliasRef, this);
	}

	/**
	 * Sets the aliasRef for this data node.
	 * 
	 * @param aliasRef the given group to set.
	 */
	private void setAliasRef(Group aliasRef) {
		this.aliasRef = aliasRef;
	}

	/**
	 * Unsets the aliasRef, if any, from this data node. Also removes references in
	 * pathway model.
	 */
	public void unsetAliasRef() {
		if (hasAliasRef())
			getPathwayModel().removeAlias(getAliasRef(), this);
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
			for (State state : states)
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
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();
		unsetGroupRef();
		unsetPathwayModel();
	}

}
