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

import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * This class stores information for a DataNode pathway element.
 * 
 * @author finterly
 */
public class DataNode extends ShapedElement {

	private String textLabel;
	private DataNodeType type = DataNodeType.UNKNOWN; // TODO: Getter/Setter weird
	private Xref xref;
	private List<State> states;
	/*
	 * The pathway element to which the data node refers to as an alias. In GPML,
	 * this is elementRef which refers to the elementId of a pathway element
	 * (normally gpml:Group).
	 */
	private PathwayElement elementRef; // optional

	
//	/*
//	 * The group to which the shaped element belongs. In GPML, this is groupRef
//	 * which refers to elementId (formerly groupId) of a gpml:Group.
//	 */
//	private Group groupRef; //optional
//
//	
//	 * @param groupRef           the group to which the shaped element belongs. In
//	 *                           GPML, this is groupRef which refers to elementId
//	 *                           (formerly groupId) of a gpml:Group.
	 
//	/**
//	 * Returns the group to which the pathway element belongs. A groupRef indicates an object is
//	 * part of a gpml:Group with a elementId.
//	 * 
//	 * @return groupRef the groupRef of the pathway element.
//	 */
//	public Group getGroupRef() {
//		return groupRef;
//	}
//
//	/**
//	 * Sets the group to which the pathway element belongs. A groupRef indicates an object is
//	 * part of a gpml:Group with a elementId.
//	 * 
//	 * @param groupRef the groupRef of the pathway element.
//	 */
//	public void setGroupRef(Group groupRef) {
//		this.groupRef = groupRef;
//	}
//	 
	
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
		this.type = type; // TODO: default type
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
	 * Instantiates data node Xref given identifier and dataSource. Checks whether
	 * dataSource string is fullName, systemCode, or invalid.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 * @throws IllegalArgumentException is given dataSource does not exist.
	 */
	public void setXref(String identifier, String dataSource) {
		if (DataSource.fullNameExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		} else if (DataSource.systemCodeExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getByAlias(dataSource));
		} else {
			throw new IllegalArgumentException("Invalid xref dataSource: " + dataSource);
		}
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
	 * Adds given state to states list.
	 * 
	 * @param state the state to be added.
	 */
	public void addState(State state) {
		states.add(state);
	}
	
	/**
	 * Removes given state to states list.
	 * 
	 * @param state the state to be removed.
	 */
	public void removeState(State state) {
		states.remove(state);
	}

	/**
	 * Returns the pathway element to which the data node refers to as an alias. In
	 * GPML, this is elementRef which refers to the elementId of a pathway element
	 * (normally gpml:Group).
	 * 
	 * @return elementRef the pathway element to which the data node refers.
	 */
	public PathwayElement getElementRef() {
		return elementRef;
	}

	/**
	 * Sets the pathway element to which the data node refers to as an alias. In
	 * GPML, this is elementRef which refers to the elementId of a pathway element
	 * (normally gpml:Group).
	 * 
	 * @param elementRef the pathway element to which the data node refers.
	 */
	public void setElementRef(PathwayElement elementRef) {
		this.elementRef = elementRef;
	}

}
