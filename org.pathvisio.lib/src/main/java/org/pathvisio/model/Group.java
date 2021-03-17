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

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * This class stores all information relevant to a Group pathway element.
 * 
 * @author finterly
 */
public class Group extends ShapedElement{

	private String textLabel; // optional
	private GroupType type = GroupType.GROUP; // optional	
	private Xref xref; // optional


	// Add Constructors



	
	/**
	 * Gets the Group Xref.
	 * 
	 * @return xref the group xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of Group Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}

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
		if (this.type != type) {
			this.type = type;
			// fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this,
			// StaticProperty.GROUPTYPE));
		}
	}


}
