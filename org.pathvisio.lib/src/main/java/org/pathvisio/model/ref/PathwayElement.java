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
package org.pathvisio.model.ref;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.PathwayObject;
import org.pathvisio.props.StaticProperty;

/**
 * Abstract class of pathway elements which are part of a pathway, have an
 * elementId, have Comment,
 * 
 * Children: DataNode, State, Interaction, GraphicalLine, Label, Shape, Group.
 * 
 * @author unknown, AP20070508, finterly
 */
public abstract class PathwayElement extends PathwayObject implements Annotatable, Citable, Evidenceable {

	private List<Comment> comments;
	/**
	 * Map for storing dynamic properties. Dynamic properties can have any String as
	 * key and value of type String. If a value is set to null the key should be
	 * removed.
	 */
	private Map<String, String> dynamicProperties;
	private List<AnnotationRef> annotationRefs;
	private List<CitationRef> citationRefs;
	private List<EvidenceRef> evidenceRefs;

	/**
	 * Instantiates a pathway element with meta data information.
	 */
	public PathwayElement() {
		super();
		this.comments = new ArrayList<Comment>(); // 0 to unbounded
		this.dynamicProperties = new TreeMap<String, String>(); // 0 to unbounded
		this.annotationRefs = new ArrayList<AnnotationRef>(); // 0 to unbounded
		this.citationRefs = new ArrayList<CitationRef>(); // 0 to unbounded
		this.evidenceRefs = new ArrayList<EvidenceRef>(); // 0 to unbounded
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
		// TODO Change source/comment text?
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.COMMENT));
	}

	public void addComment(String commentText, String source) {
		addComment(new Comment(commentText, source));
	}

	/**
	 * Removes given comment from comments list.
	 * 
	 * @param comment the comment to be removed.
	 */
	public void removeComment(Comment comment) {
		comments.remove(comment);
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.COMMENT));
	}

	/**
	 * TODO Finds the first comment with a specific source.
	 * 
	 * @param source the source of the comment to be found.
	 * @return the comment content with a given source.
	 */
	public String findComment(String source) {
		for (Comment comment : comments) {
			if (source.equals(comment.getSource())) {
				return comment.getCommentText();
			}
		}
		return null;
	}

	/**
	 * Returns the map of dynamic properties.
	 * 
	 * @return dynamicProperties the map of dynamic properties.
	 */
	public Map<String, String> getDynamicProperties() {
		return dynamicProperties;
	}

	/**
	 * Returns a set of all dynamic property keys.
	 * 
	 * @return a set of all dynamic property keys.
	 */
	public Set<String> getDynamicPropertyKeys() {
		return dynamicProperties.keySet();
	}

	/**
	 * Returns a dynamic property string value.
	 * 
	 * @param key the key of a key value pair.
	 * @return the value or dynamic property.
	 */
	public String getDynamicProperty(String key) {
		return dynamicProperties.get(key);
	}

	/**
	 * Sets a dynamic property. Setting to null means removing this dynamic property
	 * altogether.
	 * 
	 * @param key   the key of a key value pair.
	 * @param value the value of a key value pair.
	 */
	public void setDynamicProperty(String key, String value) {
		if (value == null)
			dynamicProperties.remove(key);
		else
			dynamicProperties.put(key, value);
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, key));

	}

	/**
	 * Returns the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotation references, an empty list if no
	 *         properties are defined.
	 */
	@Override
	public List<AnnotationRef> getAnnotationRefs() {
		return annotationRefs;
	}

	/**
	 * Checks whether annotationRefs has the given annotationRef. *
	 * 
	 * @param annotationRef the annotationRef to look for.
	 * @return true if has annotationRef, false otherwise.
	 */
	@Override
	public boolean hasAnnotationRef(AnnotationRef annotationRef) {
		return annotationRefs.contains(annotationRef);
	}

	/**
	 * Adds given annotationRef to annotationRefs list. Sets annotable for the given
	 * annotationRef.
	 * 
	 * @param annotationRef the annotationRef to be added.
	 */
	@Override
	public void addAnnotationRef(AnnotationRef annotationRef) {
		if (annotationRef != null && !hasAnnotationRef(annotationRef)) {
			annotationRef.setAnnotatableTo(this);
			assert (annotationRef.getAnnotatable() == this);
			annotationRefs.add(annotationRef);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATIONREF));
		}
	}

	/**
	 * Removes given annotationRef from annotationRefs list. The annotationRef
	 * ceases to exist and is terminated.
	 * 
	 * @param annotationRef the annotationRef to be removed.
	 */
	@Override
	public void removeAnnotationRef(AnnotationRef annotationRef) {
		if (annotationRef != null && hasAnnotationRef(annotationRef)) {
			annotationRefs.remove(annotationRef);
			annotationRef.terminate();
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATIONREF));
		}
	}

	/**
	 * Removes all annotationRefs from annotationRefs list.
	 */
	@Override
	public void removeAnnotationRefs() {
		for (int i = 0; i < annotationRefs.size(); i++) {
			removeAnnotationRef(annotationRefs.get(i));
		}
	}

	/**
	 * Returns the list of citation references.
	 * 
	 * @return citationRefs the list of citations referenced, an empty list if no
	 *         properties are defined.
	 */
	@Override
	public List<CitationRef> getCitationRefs() {
		return citationRefs;
	}

	/**
	 * Checks whether citationRefs has the given citationRef.
	 * 
	 * @param citationRef the citationRef to look for.
	 * @return true if has citationRef, false otherwise.
	 */
	@Override
	public boolean hasCitationRef(CitationRef citationRef) {
		return citationRefs.contains(citationRef);
	}

	/**
	 * Adds given citationRef to citationRefs list. Sets citable for the given
	 * citationRef.
	 * 
	 * @param citationRef the citationRef to be added.
	 */
	@Override
	public void addCitationRef(CitationRef citationRef) {
		if (citationRef != null && !hasCitationRef(citationRef)) {
			citationRef.setCitableTo(this);
			assert (citationRef.getCitable() == this);
			citationRefs.add(citationRef);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.CITATIONREF));

		}
	}

	/**
	 * Removes given citationRef from citationRefs list. The citationRef ceases to
	 * exist and is terminated.
	 * 
	 * @param citationRef the citationRef to be removed.
	 */
	@Override
	public void removeCitationRef(CitationRef citationRef) {
		if (citationRef != null && hasCitationRef(citationRef)) {
			citationRefs.remove(citationRef);
			citationRef.terminate();
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.CITATIONREF));
		}
	}

	/**
	 * Removes all citationRef from citationRefs list.
	 */
	@Override
	public void removeCitationRefs() {
		for (int i = 0; i < citationRefs.size(); i++) {
			removeCitationRef(citationRefs.get(i));
		}
	}

	/**
	 * Returns the list of evidence references.
	 * 
	 * @return evidenceRefs the list of evidences referenced, an empty list if no
	 *         properties are defined.
	 */
	@Override
	public List<EvidenceRef> getEvidenceRefs() {
		return evidenceRefs;
	}

	/**
	 * Checks whether evidenceRefs has the given evidenceRef.
	 * 
	 * @param evidenceRef the evidenceRef to look for.
	 * @return true if has evidenceRef, false otherwise.
	 */
	@Override
	public boolean hasEvidenceRef(EvidenceRef evidenceRef) {
		return evidenceRefs.contains(evidenceRef);
	}

	/**
	 * Adds given evidenceRef to evidenceRefs list. Sets evidenceable for the given
	 * evidenceRef.
	 * 
	 * @param evidenceRef the evidenceRef to be added.
	 */
	@Override
	public void addEvidenceRef(EvidenceRef evidenceRef) {
		if (evidenceRef != null && !hasEvidenceRef(evidenceRef)) {
			evidenceRef.setEvidenceableTo(this);
			assert (evidenceRef.getEvidenceable() == this);
			evidenceRefs.add(evidenceRef);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.EVIDENCEREF));
		}
	}

	/**
	 * Removes given evidenceRef from evidenceRefs list. The evidenceRef ceases to
	 * exist and is terminated.
	 * 
	 * @param evidenceRef the evidenceRef to be removed.
	 */
	@Override
	public void removeEvidenceRef(EvidenceRef evidenceRef) {
		if (evidenceRef != null && hasEvidenceRef(evidenceRef)) {
			evidenceRefs.remove(evidenceRef);
			evidenceRef.terminate();
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.EVIDENCEREF));
		}
	}

	/**
	 * Removes all evidenceRefs from evidenceRefs list.
	 */
	@Override
	public void removeEvidenceRefs() {
		for (EvidenceRef evidenceRef : evidenceRefs) {
			removeEvidenceRef(evidenceRef);
		}
	}

	/**
	 * Terminates this pathway element. The pathway model, if any, is unset from
	 * this pathway element. Links to all annotationRefs, citationRefs, and
	 * evidenceRefs are removed from this data node.
	 */
	@Override
	public void terminate() {
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();
		unsetPathwayModel();
	}

	/**
	 * This class stores all information relevant to a Comment. Comments can be
	 * descriptions or arbitrary notes. Each comment has a source and a text.
	 * Pathway elements (e.g. DataNode, State, Interaction, GraphicalLine, Label,
	 * Shape, Group) can have zero or more comments with it.
	 * 
	 * @author unknown, finterly
	 */
	public class Comment {

		private String source; // optional
		private String commentText; // required

		/**
		 * Instantiates a Comment with source and commentText.
		 * 
		 * @param source      the source of this comment.
		 * @param commentText the text of the comment, between Comment tags in GPML.
		 */
		public Comment(String source, String commentText) {
			this.source = source;
			this.commentText = commentText;
		}

		/**
		 * Instantiates a Comment with just commentText.
		 * 
		 * @param commentText the text of this comment, between Comment tags in GPML.
		 */
		public Comment(String commentText) {
			this.commentText = commentText;
		}

		/**
		 * Returns the source of this Comment.
		 * 
		 * @return source the source of this comment.
		 */
		public String getSource() {
			return source;
		}

		/**
		 * Sets the source of this Comment.
		 * 
		 * @param v the source of this comment.
		 */
		public void setSource(String v) {
			if (v != null) {
				source = v;
				// changed();
			}
		}

		/**
		 * Returns the text of this Comment.
		 * 
		 * @return commentText the text of this comment.
		 */
		public String getCommentText() {
			return commentText;
		}

		/**
		 * Sets the text of this Comment.
		 * 
		 * @param v the text of this comment.
		 */
		public void setCommentText(String v) {
			if (v != null) {
				commentText = v;
			}
		}

	}
}