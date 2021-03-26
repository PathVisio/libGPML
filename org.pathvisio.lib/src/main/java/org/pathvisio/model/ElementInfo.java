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

/**
 * Abstract class of pathway elements which are part of a pathway, have an
 * elementId, have Comment,
 * 
 * Children: DataNode, State, Interaction, GraphicalLine, Label, Shape, Group.
 * 
 * @author unknown, AP20070508, finterly
 */
public abstract class ElementInfo extends PathwayElement {

	private List<Comment> comments;
	private List<DynamicProperty> dynamicProperties;
	private List<AnnotationRef> annotationRefs;
	private List<Citation> citationRefs;
	private List<Evidence> evidenceRefs;

	/**
	 * Instantiates a pathway element with meta data information.
	 * 
	 * @param elementId         the unique pathway element identifier.
	 * @param pathwayModel      the parent pathway model.
	 * @param comments          the list of comments.
	 * @param dynamicProperties the list of dynamic properties, key value pairs.
	 * @param annotationRefs    the list of annotations referenced.
	 * @param citationRefs      the list of citations referenced.
	 * @param evidenceRefs      the list of evidences referenced.
	 */
	public ElementInfo(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs) {
		super(elementId, pathwayModel);
		this.comments = new ArrayList<Comment>(); // 0 to unbounded
		this.dynamicProperties = new ArrayList<DynamicProperty>(); // 0 to unbounded
		this.annotationRefs = new ArrayList<AnnotationRef>(); // 0 to unbounded
		this.citationRefs = new ArrayList<Citation>(); // 0 to unbounded
		this.evidenceRefs = new ArrayList<Evidence>(); // 0 to unbounded
	}

	/*
	 * Returns the list of comments.
	 * 
	 * @return comments the list of comments.
	 */
	public List<Comment> getComments() {
		return comments;
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
	 * TODO Finds the first comment with a specific source.
	 * 
	 * @returns the comment content with a given source.
	 */
	public String findComment(String source) {
		for (Comment comment : comments) {
			if (source.equals(comment.getSource())) {
				return comment.getContent();
			}
		}
		return null;
	}

	/**
	 * Returns the list of key value pair information properties.
	 * 
	 * @return properties the list of properties, an empty list if no properties are
	 *         defined.
	 */
	public List<DynamicProperty> getDynamicProperties() {
		return dynamicProperties;
	}

	/**
	 * Adds given dynamic property to dynamicProperties list.
	 * 
	 * @param dynamicProperty the dynamic property to be added.
	 */
	public void addDynamicProperty(DynamicProperty dynamicProperty) {
		dynamicProperties.add(dynamicProperty);
	}

	/**
	 * Removes given dynamic property from the dynamicProperties list.
	 * 
	 * @param dynamicProperty the dynamic property to be removed.
	 */
	public void removeDynamicProperty(DynamicProperty dynamicProperty) {
		dynamicProperties.remove(dynamicProperty);
	}

	/**
	 * Returns the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotation references, an empty list if no
	 *         properties are defined.
	 */
	public List<AnnotationRef> getAnnotationRefs() {
		return annotationRefs;
	}

	/**
	 * Adds given annotationRef to annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be added.
	 */
	public void addAnnotationRef(AnnotationRef annotationRef) {
		annotationRefs.add(annotationRef);
	}

	/**
	 * Removes given annotationRef from annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be removed.
	 */
	public void removeAnnotationRef(AnnotationRef annotationRef) {
		annotationRefs.remove(annotationRef);
	}

	/**
	 * Returns the list of citation references.
	 * 
	 * @return citationRefs the list of citations referenced, an empty list if no
	 *         properties are defined.
	 */
	public List<Citation> getCitationRefs() {
		return citationRefs;
	}

	/**
	 * Adds given citationRef to citationRefs list.
	 * 
	 * @param citationRef the citationRef to be added.
	 */
	public void addCitationRef(Citation citationRef) {
		citationRefs.add(citationRef);
	}

	/**
	 * Removes given citationRef from citationRefs list.
	 * 
	 * @param citationRef the citationRef to be removed.
	 */
	public void removeCitationRef(Citation citationRef) {
		citationRefs.remove(citationRef);
	}

	/**
	 * Returns the list of evidence references.
	 * 
	 * @return evidenceRefs the list of evidences referenced, an empty list if no
	 *         properties are defined.
	 */
	public List<Evidence> getEvidenceRef() {
		return evidenceRefs;
	}

	/**
	 * Adds given evidenceRef to evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be added.
	 */
	public void addEvidenceRef(Evidence evidenceRef) {
		evidenceRefs.add(evidenceRef);
	}

	/**
	 * Removes given evidenceRef from evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be removed.
	 */
	public void removeEvidenceRef(Evidence evidenceRef) {
		evidenceRefs.remove(evidenceRef);
	}
}
