/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.libgpml.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.bridgedb.Xref;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.ObjectType;
import org.pathvisio.libgpml.model.type.StateType;
import org.pathvisio.libgpml.prop.StaticProperty;
import org.pathvisio.libgpml.util.Utils;

/**
 * This class stores information for a DataNode pathway element.
 *
 * @author finterly
 */
public class DataNode extends ShapedElement implements Xrefable {

	private String textLabel;
	private DataNodeType type = DataNodeType.UNDEFINED;
	private List<State> states;
	private Xref xref; // optional
	private Group aliasRef; // optional, the pathway element to which this data node refers to as an alias.

	// ================================================================================
	// Constructors for DataNode or "Alias" DataNode
	// ================================================================================

	/**
	 * Instantiates a DataNode given all possible parameters. A DataNode of type
	 * Alias can have an aliasRef which refers to a {@link Group} and/or have a
	 * textLabel which points to a group somewhere.
	 *
	 * @param textLabel the text or link of this datanode.
	 * @param type      the type of datanode, e.g. complex.
	 * @param xref      the data node Xref.
	 * @param aliasRef  the group this data node alias refers to.
	 */
	public DataNode(String textLabel, DataNodeType type, Xref xref, Group aliasRef) {
		super();
		this.textLabel = textLabel;
		setType(type);
		this.states = new ArrayList<State>();
		this.xref = xref;
		setAliasRef(aliasRef);
	}

	/**
	 * Instantiates a DataNode given all required parameters.
	 * 
	 * @param textLabel the datanode text label.
	 * @param type      the datanode type.
	 */
	public DataNode(String textLabel, DataNodeType type) {
		this(textLabel, type, null, null);
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the object type of this pathway element.
	 *
	 * @return the object type.
	 */
	@Override
	public ObjectType getObjectType() {
		return ObjectType.DATANODE;
	}

	/**
	 * Returns the text of this datanode.
	 *
	 * @return textLabel the text of this datanode.
	 *
	 */
	@Override
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of this shaped pathway element.
	 *
	 * @param v the text to set.
	 */
	@Override
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
		if (type != v && v != null) {
			if (type == DataNodeType.ALIAS && aliasRef != null) {
				unsetAliasRef();
				int n = JOptionPane.showConfirmDialog(null, "Warning: aliasRef connection will be lost", "Warning",
						JOptionPane.OK_CANCEL_OPTION);
				if (n == JOptionPane.CANCEL_OPTION) {
					return; // do not set new data node type
				}
			}
			// set new data node type
			type = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.DATANODETYPE));
		}
	}

	/**
	 * Returns the Xref for this datanode.
	 *
	 * @return xref the xref of this datanode.
	 */
	@Override
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this datanode.
	 *
	 * @param v the xref to set for this datanode.
	 */
	@Override
	public void setXref(Xref v) {
		xref = v;
		fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
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
			if (pathwayModel != null) {
				pathwayModel.addPathwayObject(state);
			}
			states.add(state);
			// No state property, use BORDERSTYLE as dummy property to force redraw
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
	 * state to states list and pathway model.
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
		if (pathwayModel != null)
			pathwayModel.removePathwayObject(state);
		states.remove(state);
		// No state property, use BORDERSTYLE as dummy property to force redraw
		fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.BORDERSTYLE));
	}

	/**
	 * Removes all states from states list.
	 */
	private void removeStates() {
		for (int i = states.size() - 1; i >= 0; i--) {
			if (pathwayModel != null)
				pathwayModel.removePathwayObject(states.get(i));
		}
		states.clear();
	}

	// ================================================================================
	// AliasRef Methods
	// ================================================================================
	/**
	 * Returns the pathway element to which the data node refers to as an alias. In
	 * GPML, this is aliasRef which refers to the elementId of a pathway element
	 * (normally gpml:Group).
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
	 * <li>This method calls {@link #unsetAliasRef} to remove any existing links.
	 * <li>This method calls {@link PathwayModel#linkAlias} to add information to
	 * pathway model.
	 * <li>DataNode type must be "Alias".
	 * </ol>
	 *
	 * @param v the group to which this data node refers.
	 */
	public void setAliasRef(Group v) {
		if (v != null) {
			if (type != DataNodeType.ALIAS) {
				throw new IllegalArgumentException("DataNode type must be Alias before setting aliasRef");
			}
			unsetAliasRef();
			v.getPathwayModel().linkAlias(v, this);
			aliasRef = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ALIASREF));
		}
	}

	/**
	 * Unsets the aliasRef, if any, from this data node. Also removes references in
	 * pathway model.
	 * <p>
	 * <ol>
	 * <li>This method calls {@link #unsetAliasRef} to remove any existing links.
	 * <li>This method calls {@link PathwayModel#unlinkAlias} to remove information
	 * in the pathway model.
	 * <li>This method is also called when this data node alias is
	 * {@link #terminate}.
	 * </ol>
	 */
	public void unsetAliasRef() {
		if (getAliasRef() != null) {
			pathwayModel.unlinkAlias(aliasRef, this);
			aliasRef = null;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ALIASREF));
		}
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Copies values from the given source pathway element.
	 *
	 * <p>
	 * NB:
	 * <ol>
	 * <li>AliasRef value is not copied. References to other PathwayObjects are
	 * stored in {@link CopyElement} by {@link #copy}.
	 * </ol>
	 *
	 * @param src the source pathway element.
	 */
	public void copyValuesFrom(DataNode src) {
		super.copyValuesFrom(src);
		textLabel = src.textLabel;
		type = src.type;
		states = new ArrayList<State>();
		for (State s : src.states) {
			State result = addState(null, null, 0, 0);
			result.copyValuesFrom(s);
		}
		xref = src.xref;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copies this pathway element.
	 *
	 * @return the copyElement for the new pathway element and this source pathway
	 *         element.
	 */
	@Override
	public CopyElement copy() {
		DataNode result = new DataNode(textLabel, type);
		result.copyValuesFrom(this);
		return new CopyElement(result, this);
	}

	/**
	 * Copies references from the given source data node, including state
	 * references.
	 * <p>
	 * NB:
	 * <ol>
	 * <li>For each state, copies references from the corresponding source state.
	 * <li>To be called after new data node is added to a pathway model.
	 * <li>The source data node may be the immediate copy element source of the new
	 * data node, or an older source data node.
	 * </ol>
	 *
	 * @param srcDataNode the source element to copy references from.
	 */
	@Override
	public void copyReferencesFrom(PathwayElement srcDataNode) {
		super.copyReferencesFrom(srcDataNode);
		List<State> srcStates = ((DataNode) srcDataNode).getStates();
		for (int i = 0; i < getStates().size(); i++) {
			states.get(i).copyReferencesFrom(srcStates.get(i));
		}
	}

	// ================================================================================
	// Property Methods
	// ================================================================================
	/**
	 * Returns all static properties for this pathway object.
	 *
	 * @return result the set of static property for this pathway object.
	 */
	@Override
	public Set<StaticProperty> getStaticPropertyKeys() {
		Set<StaticProperty> result = super.getStaticPropertyKeys();
		Set<StaticProperty> propsDataNode = EnumSet.of(StaticProperty.TEXTLABEL, StaticProperty.DATANODETYPE,
				StaticProperty.XREF, StaticProperty.ALIASREF);
		result.addAll(propsDataNode);
		return result;
	}

	/**
	 * Returns static property value for given key.
	 *
	 * @param key the key.
	 * @return the static property value.
	 */
	@Override
	public Object getStaticProperty(StaticProperty key) {
		Object result = super.getStaticProperty(key);
		if (result == null) {
			switch (key) {
			case TEXTLABEL:
				result = getTextLabel();
				break;
			case DATANODETYPE:
				result = getType().getName();
				break;
			case XREF:
				result = getXref();
				break;
			case ALIASREF:
				result = getAliasRef();
				break;
			default:
				// do nothing
			}
		}
		return result;
	}

	/**
	 * This works so that o.setNotes(x) is the equivalent of o.setProperty("Notes",
	 * x);
	 *
	 * Value may be null in some cases, e.g. graphRef
	 *
	 * @param key   the key.
	 * @param value the static property value.
	 */
	@Override
	public void setStaticProperty(StaticProperty key, Object value) {
		super.setStaticProperty(key, value);
		switch (key) {
		case TEXTLABEL:
			setTextLabel((String) value);
			break;
		case DATANODETYPE:
			if (value instanceof DataNodeType) {
				setType((DataNodeType) value);
			} else {
				setType(DataNodeType.fromName((String) value));
			}
			break;
		case XREF:
			setXref((Xref) value);
			break;
		case ALIASREF:
			setAliasRef((Group) value);
			break;
		default:
			// do nothing
		}
	}

	// ================================================================================
	// Inherited Methods
	// ================================================================================

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
		// if data node has states, also add states to pathway model
		for (State state : states) {
			pathwayModel.addPathwayObject(state);
		}
	}

	/**
	 * Terminates this data node and removes all links and references.
	 */
	@Override
	protected void terminate() {
		unsetAliasRef(); // unsets aliasRef
		removeStates();
		unsetAllLinkableFroms();
		unsetGroupRef();
		super.terminate();
	}

	// ================================================================================
	// State Class
	// ================================================================================
	/**
	 * This class stores all information relevant to a State pathway element.
	 *
	 * @author finterly
	 */
	public class State extends ShapedElement implements Xrefable {

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
			setRelX(relX);
			setRelY(relY);
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
		 * Returns the object type of this pathway element.
		 *
		 * @return the object type.
		 */
		@Override
		public ObjectType getObjectType() {
			return ObjectType.STATE;
		}

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
		@Override
		public String getTextLabel() {
			return textLabel;
		}

		/**
		 * Sets the text of of this shaped pathway element.
		 *
		 * @param v the text to set for this shaped pathway element.
		 */
		@Override
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
				if (relX != v) {
					relX = v;
					updateCoordinates();
					fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
				}
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
				if (relY != v) {
					relY = v;
					updateCoordinates();
					fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
				}
			} else {
				throw new IllegalArgumentException("relY " + v + " should be between -1.0 and 1.0");
			}
		}

		/**
		 * Returns the Xref for this state.
		 *
		 * @return xref the xref of this state.
		 */
		@Override
		public Xref getXref() {
			return xref;
		}

		/**
		 * Sets the Xref for this state.
		 *
		 * @param v the xref of this state.
		 */
		@Override
		public void setXref(Xref v) {
			if (v != null) {
				xref = v;
				fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
			}
		}

		/**
		 * Updates coordinates.
		 */
		private void updateCoordinates() {
			DataNode dn = getDataNode();
			if (dn != null && pathwayModel != null) {
				double centerx = dn.getCenterX() + (getRelX() * dn.getWidth() / 2);
				double centery = dn.getCenterY() + (getRelY() * dn.getHeight() / 2);
				setCenterY(centery);
				setCenterX(centerx);
			}
		}

		/**
		 * Updates coordinates, called by {@link PathwayModel#childModified}
		 */
		public void coordinatesChanged() {
			updateCoordinates();
		}

		// ================================================================================
		// Inherited Methods
		// ================================================================================
		/**
		 * Returns the z-order of this pathway element.
		 *
		 * NB: State z-order is always z-order of parent data node +1. This is because
		 * z-order is not written out to the gpml file.
		 *
		 * @return zOrder the order of this pathway element.
		 */
		@Override
		public int getZOrder() {
			return getDataNode().getZOrder() + 1;
		}

		/**
		 * Do nothing. State z-order is always z-order of parent data node +1. This is
		 * because z-order is not written out to the gpml file.
		 *
		 * @param v the input
		 */
		@Override
		public void setZOrder(int v) {
			// do nothing
		}

		/**
		 * Returns the parent group of the dataNode of this state.
		 *
		 * NB: A state should always belong to the same group as its parent data node.
		 *
		 * @return the parent group of this state and its parent dataNode.
		 */
		@Override
		public Group getGroupRef() {
			return DataNode.this.getGroupRef();
		}

		/**
		 * Do not allow groupRef to be set for this state. A state will always belong to
		 * the same group as its parent data node.
		 *
		 * @param v the group.
		 */
		@Override
		public void setGroupRefTo(Group v) {
			// do nothing
		}

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

		/**
		 * Terminates this state and removes all links and references.
		 */
		@Override
		protected void terminate() {
			unsetAllLinkableFroms();
			unsetGroupRef();
			super.terminate();
		}

		// ================================================================================
		// Copy Methods
		// ================================================================================
		/**
		 * Copies values from the given source pathway element.
		 *
		 * @param src the source pathway element.
		 */
		public void copyValuesFrom(State src) {
			super.copyValuesFrom(src);
			textLabel = src.textLabel;
			type = src.type;
			relX = src.relX;
			relY = src.relY;
			xref = src.xref;
			fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
		}

		/**
		 * Copies this pathway element.
		 * 
		 * NB: this method is never actually used.
		 *
		 * @return the copyElement for the new pathway element and this source pathway
		 *         element.
		 */
		@Override
		public CopyElement copy() {
			State result = new State(textLabel, type, relX, relX);
			result.copyValuesFrom(this);
			return new CopyElement(result, this);
		}

		// ================================================================================
		// Property Methods
		// ================================================================================
		/**
		 * Returns all static properties for this pathway object.
		 *
		 * @return result the set of static property for this pathway object.
		 */
		@Override
		public Set<StaticProperty> getStaticPropertyKeys() {
			Set<StaticProperty> result = super.getStaticPropertyKeys();
			Set<StaticProperty> propsState = EnumSet.of(StaticProperty.TEXTLABEL, StaticProperty.STATETYPE,
					StaticProperty.RELX, StaticProperty.RELY, StaticProperty.XREF);
			result.addAll(propsState);
			return result;
		}

		/**
		 * Returns static property value for given key.
		 *
		 * @param key the key.
		 * @return the static property value.
		 */
		@Override
		public Object getStaticProperty(StaticProperty key) {
			Object result = super.getStaticProperty(key);
			if (result == null) {
				switch (key) {
				case TEXTLABEL:
					result = getTextLabel();
					break;
				case STATETYPE:
					result = getType().getName();
					break;
				case RELX:
					result = getRelX();
					break;
				case RELY:
					result = getRelY();
					break;
				case XREF:
					result = getXref();
					break;
				default:
					// do nothing
				}
			}
			return result;
		}

		/**
		 * This works so that o.setNotes(x) is the equivalent of o.setProperty("Notes",
		 * x);
		 *
		 * Value may be null in some cases, e.g. graphRef
		 *
		 * @param key   the key.
		 * @param value the static property value.
		 */
		@Override
		public void setStaticProperty(StaticProperty key, Object value) {
			super.setStaticProperty(key, value);
			switch (key) {
			case TEXTLABEL:
				setTextLabel((String) value);
				break;
			case STATETYPE:
				if (value instanceof StateType) {
					setType((StateType) value);
				} else {
					setType(StateType.fromName((String) value));
				}
				break;
			case RELX:
				setRelX((Double) value);
				break;
			case RELY:
				setRelY((Double) value);
				break;
			case XREF:
				setXref((Xref) value);
				break;
			default:
				// do nothing
			}
		}

	}

}
