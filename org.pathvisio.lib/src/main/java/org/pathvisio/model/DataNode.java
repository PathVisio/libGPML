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
import org.pathvisio.util.Utils;

/**
 * This class stores all information relevant to a DataNode pathway element.
 * 
 * @author finterly
 */
public class DataNode extends PathwayElement {

	protected String elementId;
	protected String elementRef; // optional
	protected String textLabel;
	protected DataNodeType type = DataNodeType.UNKNOWN; // TODO: Getter/Setter weird
	protected String groupRef; // optional
	protected RectProperty rectProperty;
	protected FontProperty fontProperty;
	protected ShapeStyleProperty shapeStyleProperty;
	protected Xref xref;
	protected List<Comment> comments = new ArrayList<Comment>(); // length 0 to unbounded
	protected List<DynamicProperty> dynamicProperties = new ArrayList<DynamicProperty>(); // length 0 to unbounded
	protected List<AnnotationRef> annotationRefs = new ArrayList<AnnotationRef>(); // length 0 to unbounded
	protected List<CitationRef> citationRefs = new ArrayList<CitationRef>(); // length 0 to unbounded
	protected List<EvidenceRef> evidenceRefs = new ArrayList<EvidenceRef>(); // length 0 to unbounded
	
	
	
	protected String identifier = "";

	protected DataSource dataSource = null;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException();
		} else {
			identifier = identifier.trim();
			this.identifier = identifier;
		}
	}


	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Xref getXref() {
		// TODO: Store Xref by default, derive setGeneID and dataSource from it.
		return new Xref(identifier, dataSource);
	}

//	/** TODO
//	 * Sets data node type to given DataNodeType.
//	 * 
//	 * @param dataNodeType the data node type.
//	 */
//	public void setDataNodeType(DataNodeType type) {
//		setDataNodeType(type.getName());
//	}

//	/** TODO
//	 * Sets data node type to given String.
//	 * 
//	 * @return dataNodeType the data node type.
//	 */
//	public void setDataNodeType(String value) {
//		if (value == null) {
//			throw new IllegalArgumentException();
//		}
//		if (!Utils.stringEquals(dataNodeType, value)) {
//			dataNodeType = value;
//			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TYPE));
//		}
//	}

	// Add Constructors

	/**
	 * Returns the elementId of the datanode.
	 * 
	 * @return elementId the unique id of the datanode.
	 * 
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the datanode.
	 * 
	 * @param elementId the unique id of the datanode.
	 * 
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Returns the groupRef of the datanode. A groupRef indicates an object is part of
	 * a gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the datanode.
	 * 
	 */
	public Object getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the datanode. A groupRef indicates an object is part of
	 * a gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the datanode.
	 * 
	 */
	public void getGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * Returns the DataNode Xref.
	 * 
	 * @return xref the datanode xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of DataNode Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}

	/**
	 * Returns the source of data, e.g. the full name, code name or abbreviation of the
	 * database...
	 */

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
	 * Returns the list of comments.
	 * 
	 * @return comments the list of comments.
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/**
	 * Sets comments to given list of Comment.
	 * 
	 * @param comments the list of comments.
	 */
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	/**
	 * Adds given comment to comments list.
	 * 
	 * @param comment the comment to be added.
	 */
	public void addComment(Comment comment) {
		comments.add(comment);
	}

	/**
	 * Removes given comment from comments list.
	 * 
	 * @param comment the comment to be removed.
	 */
	public void removeComment(Comment comment) {
		comments.remove(comment);
	}

	/**
	 * TODO: Move this
	 * 
	 * Adds String comment and String source...
	 * 
	 * @param comment the comment.
	 * @param source  the source.
	 */
	public void addComment(String comment, String source) {
		addComment(new Comment(comment, source));
	}

	/**
	 * TODO: Need to be moved or something...
	 * 
	 * Finds the first comment with a specific source.
	 * 
	 * @returns the comment with a given source.
	 */
	public String findComment(String source) {
		for (Comment c : comments) {
			if (source.equals(c.source)) {
				return c.comment;
			}
		}
		return null;
	}

	/**
	 * Returns the list of key value pair information properties.
	 * 
	 * @return properties the list of properties.
	 */
	public List<DynamicProperty> getProperties() {
		return dynamicProperties;
	}

	/**
	 * Returns the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotation references.
	 */
	public List<AnnotationRef> getAnnotationRefs() {
		return annotationRefs;
	}

	/**
	 * Returns the list of citation references.
	 * 
	 * @return citationRefs the list of citation references.
	 */
	public List<CitationRef> getCitationRefs() {
		return citationRefs;
	}

//	public List<AnnotationRef> getAnnotationRefList() {
//		if (annotationRefs == null) {
//			annotationRefs = new ArrayList<AnnotationRef>();
//		}
//		return this.annotationRefs;
//	}

}
