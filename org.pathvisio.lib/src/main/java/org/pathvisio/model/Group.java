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
 * This class stores all information relevant to a Group pathway element.
 * 
 * @author finterly
 */
public class Group extends ShapedElement {

	private GroupType type = GroupType.GROUP;
	private String textLabel; // optional
	/**
	 * The list of pathway elements which belong to the group.
	 */
	private List<PathwayElement> pathwayElements; // 0 to unbounded
	/*
	 * The parent group to which the group belongs. In other words, a group can
	 * belong in another group. In GPML, groupRef refers to the elementId (formerly
	 * groupId) of the parent gpml:Group.
	 */
	private Group groupRef; // optional
	private Xref xref; // optional

	/**
	 * Gets the text of of the group.
	 * 
	 * @return textLabel the text of of the group.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the group.
	 * 
	 * @param textLabel the text of of the group.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Gets GroupType. GroupType is GROUP by default.
	 * 
	 * TODO: Default GroupType used to be NONE. NONE needs to be changed to GROUP.
	 * 
	 * @return type the type of group, e.g. complex.
	 */
	public GroupType getType() {
		if (type == null) {
			type = GroupType.GROUP;
		}
		return type;
	}

	/**
	 * Sets GroupType to the given groupType.
	 * 
	 * @param type the type of group, e.g. complex.
	 */
	public void setType(GroupType type) {
		this.type = type;
	}

	public List<PathwayElement> getPathwayElements() {
		return pathwayElements;
	}

	public void addPathwayElement(PathwayElement pathwayElement) {
		pathwayElements.add(pathwayElement);
	}

	public void removePathwayElement(PathwayElement pathwayElement) {
		pathwayElements.remove(pathwayElement);
	}

	/**
	 * Returns the parent group of the group. In GPML, groupRef refers to the
	 * elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of the group.
	 */
	public Group getGroup() {
		return groupRef;
	}

	/**
	 * Sets the parent group of the group. The group is added to the pathwayElements
	 * list of the parent group.
	 * 
	 * @param groupRef the parent group of the group.
	 */
	public void setGroup(Group groupRef) {
		if (groupRef.getPathwayElements() != null && groupRef != null) {
			groupRef.addPathwayElement(this);
			this.groupRef = groupRef;
		}

		//TODO how to handle groupRef properly...
		
//			if (groupRef == null || !groupRef.equals(id)) {
//				if (pathwayModel != null) {
//					if (groupRef != null) {
//						pathwayModel.removeGroupRef(groupRef, this);
//					}
//					// Check: move add before remove??
//					if (id != null) {
//						pathwayModel.addGroupRef(id, this);
//					}
//				}
//				groupRef = id;
//			}
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

}
