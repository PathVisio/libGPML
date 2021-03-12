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

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * This class stores all information relevant to a Group pathway element.
 * 
 * @author finterly
 */
public class Group extends PathwayElement{

	protected String elementId;
	protected String textLabel; // optional
	protected GroupType type = GroupType.GROUP; // optional
	protected String groupRef; // if part of group
	protected Xref xref; // optional
	protected List<Comment> comments; // optional
	protected List<DynamicProperty> dynamicProperties; // optional
	protected List<AnnotationRef> annotationRefs; // optional
	protected List<CitationRef> citationRefs; // optional
	protected List<EvidenceRef> evidenceRefs; // optional

	// Add Constructors

	/**
	 * Gets the elementId of the group.
	 * 
	 * @return elementId the unique id of the group.
	 * 
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the group.
	 * 
	 * @param elementId the unique id of the group.
	 * 
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the groupRef of the group. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the group.
	 * 
	 */
	public Object getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the group. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the group.
	 * 
	 */
	public void setGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

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

	/**
	 * Gets the list of comments.
	 * 
	 * @return comments the list of comments.
	 */
	public List<Comment> getCommentList() {
		return comments;
	}

	/**
	 * Gets the list of key value pair information properties.
	 * 
	 * @return properties the list of properties.
	 */
	public List<Property> getPropertyList() {
		return properties;
	}

	/**
	 * Gets the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotation references.
	 */
	public List<AnnotationRef> getAnnotationRefList() {
		return annotationRefs;
	}

	/**
	 * Gets the list of citation references.
	 * 
	 * @return citationRefs the list of citation references.
	 */
	public List<CitationRef> getCitationRefList() {
		return citationRefs;
	}

//	public List<AnnotationRef> getAnnotationRefList() {
//		if (annotationRefs == null) {
//			annotationRefs = new ArrayList<AnnotationRef>();
//		}
//		return this.annotationRefs;
//	}

}
