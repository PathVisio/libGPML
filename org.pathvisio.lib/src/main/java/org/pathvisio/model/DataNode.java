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
import org.pathvisio.events.PathwayObjectEvent;
import org.pathvisio.model.type.DataNodeType;
import org.pathvisio.model.type.StateType;
import org.pathvisio.props.StaticProperty;
import org.pathvisio.util.Utils;

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
	private Group aliasRef; // optional, the pathway element to which this data node refers to as an alias.

	// ================================================================================
	// Constructors for DataNode or "Alias" DataNode
	// ================================================================================

	/**
	 * Instantiates a DataNode (type != "Alias) given all possible parameters.
	 * 
	 * @param textLabel the text of this datanode.
	 * @param type      the type of datanode, e.g. complex.
	 * @param xref      the data node Xref.
	 */
	public DataNode(String textLabel, DataNodeType type, Xref xref) {
		super();
		this.textLabel = textLabel;
		setType(type);
		this.states = new ArrayList<State>();
		this.xref = xref;
	}

	/**
	 * Instantiates a DataNode (type != "Alias) given all required parameters.
	 */
	public DataNode(String textLabel, DataNodeType type) {
		this(textLabel, type, null);
	}

	/**
	 * Instantiates an Alias Data Node pathway element given all possible
	 * parameters. An alias data node acts as an alias for a {@link Group}. In GPML,
	 * the datanode has aliasRef which refers to the elementId of the group
	 * aliasRef.
	 * 
	 * NB: This method is not used directly. Aliases are created by a group
	 * {@link Group#createAlias(String)}.
	 * 
	 * @param textLabel the text of this datanode.
	 * @param xref      the data node Xref.
	 * @param aliasRef  the group this data node alias refers to.
	 */
	protected DataNode(String textLabel, Xref xref, Group aliasRef) {
		super();
		this.textLabel = textLabel;
		this.type = DataNodeType.ALIAS;
		this.states = new ArrayList<State>(); // TODO
		this.xref = xref; // TODO
		setAliasRef(aliasRef);
	}

	/**
	 * Instantiates an Alias Data Node pathway element given all possible parameters
	 * except xref.
	 * 
	 * NB: This method is not used directly. Aliases are created by a group
	 * {@link Group#createAlias(String)}.
	 */
	protected DataNode(String textLabel, Group aliasRef) {
		this(textLabel, null, aliasRef);
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the text of this datanode.
	 * 
	 * @return textLabel the text of this datanode.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of this shaped pathway element.
	 * 
	 * @param v the text to set.
	 */
	public void setTextLabel(String v) {
		String value = (v == null) ? "" : v;
		if (!Utils.stringEquals(textLabel, value)) {
			textLabel = value;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.TEXTLABEL));
		}
	}

	/**
	 * Returns the type of this datanode.
	 * 
	 * @return type the type of this datanode, e.g. complex.
	 */
	public DataNodeType getType() {
		return type;
	}

	/**
	 * Sets the type of this datanode, e.g. complex.
	 * 
	 * NB: Cannot change type if this is an alias data node.
	 * 
	 * @param v the type to set for this datanode.
	 */
	public void setType(DataNodeType v) {
		if (type == DataNodeType.ALIAS) {
			throw new IllegalArgumentException("DataNodeType cannot be changed for an alias data node");
		}
		if (v == DataNodeType.ALIAS) {
			throw new IllegalArgumentException("DataNodeType Alias is reserved for alias data nodes.");
		}
		if (type != v && v != null) {
			type = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.DATANODETYPE));
		}
	}

	/**
	 * Returns the Xref for this datanode.
	 * 
	 * @return xref the xref of this datanode.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this datanode.
	 * 
	 * @param v the xref to set for this datanode.
	 */
	public void setXref(Xref v) {
		if (v != null) {
			xref = v;
			// TODO
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
		}
	}

	// ================================================================================
	// State Methods
	// ================================================================================
	/*
	 * Returns the list of states of this data node.
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
	 * Adds given state to states list. Sets datanode for the given state.
	 * 
	 * @param state the state to be added.
	 */
	public void addState(State state) {
		if (state == null) {
			throw new IllegalArgumentException("Cannot add invalid state to data node " + getElementId());
		}
		if (state.getDataNode() != this) {
			throw new IllegalArgumentException("Cannot add state to data node other than its parent data node");
		}
		if (!hasState(state)) {
			// add state to same pathway model as data node if applicable
			if (getPathwayModel() != null) {
				getPathwayModel().addPathwayObject(state);
			}
			if (getGroupRef() != null) {
				getGroupRef().addPathwayElement(state); // TODO add state to PathwayElements list...?
			}
			states.add(state);
			// No state property, use BORDERSTYLE as dummy property to force redraw TODO
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.BORDERSTYLE));
		} else {
			System.out.println("State " + state.getElementId() + " already belongs to data node " + getElementId());
		}
	}

	/**
	 * Instantiates a state with the given properties. Adds new state to states list
	 * and pathway model.
	 * 
	 * @param textLabel the text label of the state.
	 * @param type      the type of the state, e.g. protein modification.
	 * @param relX      the relative x coordinates.
	 * @param relY      the relative y coordinates.
	 * @return state the instantiated state.
	 */
	public State addState(String textLabel, StateType type, double relX, double relY) {
		State state = new State(textLabel, type, relX, relY);
		addState(state);
		return state;
	}

	/**
	 * Instantiates a state with the given properties including elementId. Adds new
	 * state to states list and pathway model. //TODO
	 * 
	 * @param elementId the elementId to set for the instantiated state.
	 * @param textLabel the text label of the state.
	 * @param type      the type of the state, e.g. protein modification.
	 * @param relX      the relative x coordinates.
	 * @param relY      the relative y coordinates.
	 * @return state the instantiated state.
	 */
	public State addState(String elementId, String textLabel, StateType type, double relX, double relY) {
		State state = new State(textLabel, type, relX, relY);
		state.setElementId(elementId);
		addState(state);
		return state;
	}

	/**
	 * Removes given state from states list. State ceases to exist and is
	 * terminated.
	 * 
	 * @param state the state to be removed.
	 */
	public void removeState(State state) {
		if (getPathwayModel() != null)
			getPathwayModel().removePathwayObject(state);
		states.remove(state);
		// No state property, use BORDERSTYLE as dummy property to force redraw
		fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.BORDERSTYLE));
	}

	/**
	 * Removes all states from states list.
	 */
	private void removeStates() {
		for (int i = states.size() - 1; i >= 0; i--) {
			if (getPathwayModel() != null)
				getPathwayModel().removePathwayObject(states.get(i));
		}
		states.clear();
	}

	// ================================================================================
	// AliasRef Methods
	// ================================================================================
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
	 * Sets the group aliasRef to which this data node refers to as an alias. In
	 * GPML, this is aliasRef which refers to the elementId of gpml:Group.
	 * 
	 * <p>
	 * NB:
	 * <ol>
	 * <li>This method calls {@link PathwayModel#addAlias}.
	 * <li>DataNode type must be "Alias".
	 * </ol>
	 * 
	 * @param v the group to which this data node refers.
	 */
	private void setAliasRef(Group v) {
		if (v != null) {
			if (type != DataNodeType.ALIAS) {
				throw new IllegalArgumentException("DataNode type must be Alias before setting aliasRef");
			}
			aliasRef = v;
			v.getPathwayModel().addAlias(v, this);
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ALIASREF));
		}
	}

	/**
	 * Unsets the aliasRef, if any, from this data node. Also removes references in
	 * pathway model.
	 * <p>
	 * NB:
	 * <ol>
	 * <li>This method does not call {@link PathwayModel#removeAlias}.
	 * <li>This method is not used directly. It is called by when the data node
	 * alias is deleted by {@link PathwayModel#removePathwayObject} which in turn calls
	 * {@link #terminate}.
	 * </ol>
	 */
	private void unsetAliasRef() {
		if (getAliasRef() != null) {
			aliasRef = null;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ALIASREF));
		}
	}

	// ================================================================================
	// Inherited Methods
	// ================================================================================

	/**
	 * Verifies if given parent group is new and valid. Sets the parent group of
	 * this pathway element. Adds this pathway element to the the pathwayElements
	 * list of the new parent group. If there is an old parent group, this pathway
	 * element is removed from its pathwayElements list.
	 * 
	 * @param v the new parent group to set.
	 */
	@Override
	public void setGroupRefTo(Group v) {
		if (v == null)
			throw new IllegalArgumentException("Invalid group.");
		if (getGroupRef() != v) {
			unsetGroupRef(); // first unsets if necessary
			setGroupRef(v);
			if (!v.hasPathwayElement(this)) {
				v.addPathwayElement(this);
			}
			// if data node has states, set groupRef for states and add to group
			v.addPathwayElements(states);
		}
	}

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	@Override
	public void unsetGroupRef() {
		super.unsetGroupRef();
		for (State state : states) {
			state.unsetGroupRef();
		}
	}

	/**
	 * Sets the pathway model for this pathway element. NB: Only set when a pathway
	 * model adds this pathway element. This method is not used directly.
	 * 
	 * NB: This method is not used directly. It is called by
	 * {@link PathwayModel#addPathwayObject}.
	 * 
	 * @param pathwayModel the parent pathway model.
	 */
	@Override
	protected void setPathwayModelTo(PathwayModel pathwayModel) throws IllegalArgumentException, IllegalStateException {
		super.setPathwayModelTo(pathwayModel);
		// if data node has states, also add states to pathway model TODO
		for (State state : states) {
			pathwayModel.addPathwayObject(state);
		}
	}

	/**
	 * Terminates this data node. The pathway model, if any, is unset from this data
	 * node. Links to all states, annotationRefs, citationRefs, and evidenceRefs are
	 * removed from this data node.
	 */
	@Override
	protected void terminate() {
		unsetAliasRef();
		removeStates();
		super.terminate();
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Note: doesn't change parent, only fields
	 *
	 * Used by UndoAction.
	 *
	 * @param src
	 */
	public void copyValuesFrom(DataNode src) {
		super.copyValuesFrom(src);
		textLabel = src.textLabel;
		type = src.type;
		states = new ArrayList<State>();
		for (State s : src.states) {
			State result = new State(null, null, 0, 0); // TODO
			result.copyValuesFrom(s);
			addState(result);
		}
		xref = src.xref;
		aliasRef = src.aliasRef;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public DataNode copy() {
		DataNode result = new DataNode(textLabel, type);
		result.copyValuesFrom(this);
		return result;
	}

	// ================================================================================
	// State Class
	// ================================================================================
	/**
	 * This class stores all information relevant to a State pathway element.
	 * 
	 * @author finterly
	 */
	public class State extends ShapedElement {

		private String textLabel;
		private StateType type;
		private double relX;
		private double relY;
		private Xref xref; // optional

		// ================================================================================
		// Constructors
		// ================================================================================
		/**
		 * Instantiates a State pathway element given all possible parameters.
		 * 
		 * @param textLabel the text label of the state.
		 * @param type      the type of the state, e.g. protein modification.
		 * @param relX      the relative x coordinates on the parent object, where 0,0
		 *                  is at the center of the object and 1,1 at the bottom-right
		 *                  corner of the object.
		 * @param relY      the relative y coordinates on the parent object, where 0,0
		 *                  is at the center of the object and 1,1 at the bottom-right
		 *                  corner of the object.
		 * @param xref      the state xref.
		 */
		private State(String textLabel, StateType type, double relX, double relY, Xref xref) {
			super();
			this.textLabel = textLabel;
			this.type = type;
			this.relX = relX;
			this.relY = relY;
			this.xref = xref;
		}

		/**
		 * Instantiates a State pathway element given all possible parameters except
		 * xref.
		 */
		private State(String textLabel, StateType type, double relX, double relY) {
			this(textLabel, type, relX, relY, null);
		}

		// ================================================================================
		// Accessors
		// ================================================================================
		/**
		 * Returns the parent data node, outer class, to which the state belongs.
		 * <p>
		 * NB:
		 * <ol>
		 * <li>Returns the parent data node even if this state has been removed from the
		 * data node states list.
		 * <li>In GPML2013a, elementRef was used to refer to the elementId of parent
		 * data node, thus linking state to parent data node.
		 * </ol>
		 * 
		 * @return dataNode the parent data node of the state.
		 */
		public DataNode getDataNode() {
			return DataNode.this;
		}

		/**
		 * Returns the text of of this state.
		 * 
		 * @return textLabel the text of of this state.
		 * 
		 */
		public String getTextLabel() {
			return textLabel;
		}

		/**
		 * Sets the text of of this shaped pathway element.
		 * 
		 * @param v the text to set for this shaped pathway element.
		 */
		public void setTextLabel(String v) {
			String value = (v == null) ? "" : v;
			if (!Utils.stringEquals(textLabel, value)) {
				textLabel = value;
				fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.TEXTLABEL));
			}
		}

		/**
		 * Returns the type of this state.
		 * 
		 * @return type the type of this state, e.g. complex.
		 */
		public StateType getType() {
			return type;
		}

		/**
		 * Sets the type of this state.
		 * 
		 * @param v the type of this state, e.g. complex.
		 */
		public void setType(StateType v) {
			if (type != v && v != null) {
				type = v;
				fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.STATETYPE));
			}
		}

		/**
		 * Returns the relative x coordinate. When the given state is linked to a data
		 * node, relX and relY are the relative coordinates on the data node, where 0,0
		 * is at the center of the data node and 1,1 at the bottom right corner of the
		 * data node.
		 * 
		 * @return relX the relative x coordinate.
		 */
		public double getRelX() {
			return relX;
		}

		/**
		 * Sets the relative x coordinate. When the given state is linked to a data
		 * node, relX and relY are the relative coordinates on the data node, where 0,0
		 * is at the center of the data node and 1,1 at the bottom right corner of the
		 * data node.
		 * 
		 * @param v the relative x coordinate.
		 * @throws IllegalArgumentException if relX is not between -1.0 and 1.0. t
		 */
		public void setRelX(double v) {
			if (Math.abs(v) <= 1.0) {
				relX = v;
				fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
			} else {
				throw new IllegalArgumentException("relX " + v + " should be between -1.0 and 1.0");
			}
		}

		/**
		 * Returns the relative y coordinate. When the given state is linked to a data
		 * node, relX and relY are the relative coordinates on the data node, where 0,0
		 * is at the center of the data node and 1,1 at the bottom right corner of the
		 * data node.
		 * 
		 * @return relY the relative y coordinate.
		 */
		public double getRelY() {
			return relY;
		}

		/**
		 * Sets the relative y coordinate. When the given state is linked to a data
		 * node, relX and relY are the relative coordinates on the data node, where 0,0
		 * is at the center of the data node and 1,1 at the bottom right corner of the
		 * data node.
		 * 
		 * @param v the relative y coordinate.
		 */
		public void setRelY(double v) {
			if (Math.abs(v) <= 1.0) {
				relY = v;
				fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
			} else {
				throw new IllegalArgumentException("relY " + v + " should be between -1.0 and 1.0");
			}
		}

		/**
		 * Returns the Xref for this state.
		 * 
		 * @return xref the xref of this state.
		 */
		public Xref getXref() {
			return xref;
		}

		/**
		 * Sets the Xref for this state.
		 * 
		 * @param v the xref of this state.
		 */
		public void setXref(Xref v) {
			if (v != null) {
				xref = v;
				// TODO
				fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
			}
		}

		// ================================================================================
		// Inherited Methods
		// ================================================================================

//		/**
//		 * Returns the parent group of the dataNode of this state.
//		 * 
//		 * NB: Although state groupRef is updated, a state should always belong to the
//		 * same group as its parent data node.
//		 * 
//		 * @return the parent group of this state and its parent dataNode.
//		 */
//		@Override
//		public Group getGroupRef() {
//			return DataNode.this.getGroupRef();
//		}

		/**
		 * Sets the pathway model for this pathway element.
		 * 
		 * NB: This method is not used directly. It is called by
		 * {@link PathwayModel#addPathwayObject}.
		 * 
		 * @param pathwayModel the parent pathway model.
		 */
		@Override
		protected void setPathwayModelTo(PathwayModel pathwayModel)
				throws IllegalArgumentException, IllegalStateException {
			if (pathwayModel == null)
				throw new IllegalArgumentException("Invalid pathway model.");
			if (pathwayModel == getDataNode().getPathwayModel()) {
				setPathwayModel(pathwayModel);
			} else {
				throw new IllegalArgumentException(this.getClass().getSimpleName()
						+ " must be added to the same pathway model as its parent data node.");
			}
		}

		// FROM MState
//		@Override
//		public void setParent(Pathway v) {
//			if (parent != v) {
//				super.setParent(v);
//				if (parent != null && graphRef != null) {
//					updateCoordinates();
//				}
//			}
//		}

		// TODO
		private void updateCoordinates() {
			if (getPathwayModel() != null) {
				DataNode dn = getDataNode();
				double centerx = dn.getCenterX() + (getRelX() * dn.getWidth() / 2);
				double centery = dn.getCenterY() + (getRelY() * dn.getHeight() / 2);
				setCenterY(centery);
				setCenterX(centerx);
			}
		}

		// ================================================================================
		// Copy Methods
		// ================================================================================
		/**
		 * Note: doesn't change parent, only fields
		 *
		 * Used by UndoAction.
		 *
		 * @param src
		 */
		public void copyValuesFrom(State src) { // TODO
			super.copyValuesFrom(src);
			textLabel = src.textLabel;
			type = src.type;
			relX = src.relX;
			relY = src.relY;
			xref = src.xref;
			fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
		}

		/**
		 * Copy Object. The object will not be part of the same Pathway object, it's
		 * parent will be set to null.
		 *
		 * No events will be sent to the parent of the original.
		 */
		public State copy() {
			// make empty
			State result = new State(textLabel, type, relX, relX); // TODO NEVER USED
			result.copyValuesFrom(this);
			return result;
		}

	}

}
