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
import java.util.Set;

import org.bridgedb.Xref;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.GraphLink.LinkableFrom;
import org.pathvisio.model.GraphLink.LinkableTo;
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
	// Constructors
	// ================================================================================
	/**
	 * 
	 * Instantiates a Data Node pathway element given all possible parameters. The
	 * data node is an alias and refers to another pathway element. In GPML, the
	 * datanode has aliasRef which refers to the elementId of a pathway element
	 * (normally gpml:Group).
	 * 
	 * @param textLabel the text of this datanode.
	 * @param type      the type of datanode, e.g. complex.
	 * @param xref      the data node Xref.
	 * @param aliasRef  the group this data node alias refers to.
	 */
	public DataNode(String textLabel, DataNodeType type, Xref xref, Group aliasRef) {
		this.textLabel = textLabel;
		this.type = type;
		this.states = new ArrayList<State>();
		this.xref = xref;
		setAliasRefTo(aliasRef);
		System.out.println("Constructor1");
	}

	/**
	 * Instantiates a DataNode given all possible parameters except xref.
	 */
	public DataNode(String textLabel, DataNodeType type, Group aliasRef) {
		this(textLabel, type, null, aliasRef);
		System.out.println("Constructor2");

	}

	/**
	 * Instantiates a DataNode given all possible parameters except aliasRef.
	 */
	public DataNode(String textLabel, DataNodeType type, Xref xref) {
		this(textLabel, type, xref, null);
		System.out.println("Constructor3");
	}

	/**
	 * Instantiates a DataNode given all possible parameters except xref and
	 * aliasRef.
	 */
	public DataNode(String textLabel, DataNodeType type) {
		this(textLabel, type, null, null);
		System.out.println("Constructor4");

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
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TEXTLABEL));
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
	 * @param v the type to set for this datanode.
	 */
	public void setType(DataNodeType v) {
		if (type != v && v != null) {
			type = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.DATANODETYPE));
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
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
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
		assert (state != null);
		assert (state.getDataNode() == this);
		assert !hasState(state);
		// add state to same pathway model as data node if applicable
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayObject(state);
		states.add(state);
		// No state property, use BORDERSTYLE as dummy property to force redraw TODO
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BORDERSTYLE));
	}

	/**
	 * Instantiates a state with the given properties. Adds new state to states list
	 * and pathway model.
	 * 
	 * @param elementId
	 * @param textLabel
	 * @param stateType
	 * @param relX
	 * @param relY
	 * @return
	 */
	public State addState(String elementId, String textLabel, StateType stateType, double relX, double relY) {
		State state = new State(textLabel, stateType, relX, relY);
		state.setElementId(elementId);
		// add state to same pathway model as data node if applicable
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayObject(state);
		states.add(state);
		// No state property, use BORDERSTYLE as dummy property to force redraw TODO
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BORDERSTYLE));
		return state;
	}

	/**
	 * Instantiates a state with the given properties. Adds new state to states list
	 * and pathway model.
	 * 
	 * @param textLabel
	 * @param stateType
	 * @param relX
	 * @param relY
	 * @return
	 */
	public State addState(String textLabel, StateType stateType, double relX, double relY) {
		State state = new State(textLabel, stateType, relX, relY);
		// add state to same pathway model as data node if applicable
		if (getPathwayModel() != null)
			getPathwayModel().addPathwayObject(state);
		states.add(state);
		// No state property, use BORDERSTYLE as dummy property to force redraw TODO
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BORDERSTYLE));
		return state;
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
			getPathwayModel().removePathwayObject(state);
		states.remove(state);
		state.terminate();
		// No state property, use BORDERSTYLE as dummy property to force redraw TODO
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BORDERSTYLE));
	}

	/**
	 * Removes all states from states list.
	 */
	public void removeStates() {
		for (int i = 0; i < states.size(); i++) {
			removeState(states.get(i));
		}
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
	 * Checks whether this data node has an aliasRef.
	 *
	 * @return true if and only if the aliasRef of this data node is effective.
	 */
	public boolean hasAliasRef() {
		return getAliasRef() != null;
	}

	/**
	 * Sets the group aliasRef to which this data node refers to as an alias. In
	 * GPML, this is aliasRef which refers to the elementId of gpml:Group.
	 * 
	 * @param aliasRef the group to which this data node refers.
	 */
	public void setAliasRefTo(Group v) {
		// TODO
		if (aliasRef != v && v != null) {
			unsetAliasRef(); // first unsets if necessary
			setAliasRef(v);
			getPathwayModel().addAlias(v, this);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ALIASREF));
		}
	}

	/**
	 * Sets the aliasRef for this data node.
	 * 
	 * @param v the given group to set.
	 */
	private void setAliasRef(Group v) {
		aliasRef = v;
	}

	/**
	 * Unsets the aliasRef, if any, from this data node. Also removes references in
	 * pathway model.
	 */
	public void unsetAliasRef() {
		if (hasAliasRef()) {
			getPathwayModel().removeAlias(getAliasRef(), this);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ALIASREF));
		}
	}

	// ================================================================================
	// Inherited Methods
	// ================================================================================
	/**
	 * Sets the pathway model for this pathway element. NB: Only set when a pathway
	 * model adds this pathway element. This method is not used directly.
	 * 
	 * @param pathwayModel the parent pathway model.
	 */
	@Override
	protected void setPathwayModelTo(PathwayModel pathwayModel) throws IllegalArgumentException, IllegalStateException {
		if (pathwayModel == null)
			throw new IllegalArgumentException("Invalid pathway model.");
		if (hasPathwayModel())
			throw new IllegalStateException("Pathway element already belongs to a pathway model.");
		setPathwayModel(pathwayModel);
		// if data node has states, also add states to pathway model TODO
		for (State state : states)
			pathwayModel.addPathwayObject(state);
	}

	/**
	 * Unsets the pathway model, if any, from this pathway element. The pathway
	 * element no longer belongs to a pathway model. NB: This method is not used
	 * directly.
	 */
	@Override 
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
		 * Returns the parent data node to which the state belongs.
		 * 
		 * NB: prior to GPML2021, elementRef was used to refer to the elementId of
		 * parent data node.
		 * 
		 * @return dataNode the parent data node of the state.
		 */
		public DataNode getDataNode() {
			return DataNode.this;
		}

		/**
		 * Returns the pathway model for this pathway element.
		 * 
		 * @return pathwayModel the parent pathway model.
		 */
		@Override
		public PathwayModel getPathwayModel() {
			return DataNode.this.getPathwayModel();
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
				fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TEXTLABEL));
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
				fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.STATETYPE));
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
				fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
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
				fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
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
				fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
			}
		}

		/**
		 * Returns {@link LinkableFrom} pathway elements, at this time that only goes
		 * for {@link LineElement.LinePoint}, for this {@link LinkableTo} pathway
		 * element.
		 */
		@Override
		public Set<LinkableFrom> getLinkableFroms() {
			return GraphLink.getReferences(this, getPathwayModel());
		}

	}

}
