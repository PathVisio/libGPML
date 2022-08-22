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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bridgedb.Xref;
import org.pathvisio.libgpml.model.Referenceable.Annotatable;
import org.pathvisio.libgpml.model.Referenceable.Citable;
import org.pathvisio.libgpml.model.Referenceable.Evidenceable;
import org.pathvisio.libgpml.model.type.AnnotationType;
import org.pathvisio.libgpml.prop.StaticProperty;
import org.pathvisio.libgpml.util.Utils;
import org.pathvisio.libgpml.util.XrefUtils;

/**
 * Abstract class of pathway elements which are part of a pathway, have an
 * elementId, have Comment,
 *
 * Children: DataNode, State, Interaction, GraphicalLine, Label, Shape, Group.
 *
 * @author unknown, AP20070508, finterly
 */
public abstract class PathwayElement extends PathwayObject implements Cloneable, Annotatable, Citable, Evidenceable {

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

	// ================================================================================
	// Constructors
	// ================================================================================
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

	// ================================================================================
	// Comment and DynamicProperty Methods
	// ================================================================================

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
		fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.COMMENT));
	}

	/**
	 * Creates a comment with given properties and adds to comments list. Calls
	 * {@link #addComment(Comment comment)}.
	 *
	 * @param commentText the text of the comment, between Comment tags in GPML.
	 * @param source      the source of this comment.
	 * @return the created comment.
	 */
	public Comment addComment(String commentText, String source) {
		Comment comment = new Comment(commentText, source);
		addComment(comment);
		return comment;
	}

	/**
	 * Removes given comment from comments list.
	 *
	 * @param comment the comment to be removed.
	 */
	public void removeComment(Comment comment) {
		comments.remove(comment);
		fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.COMMENT));
	}

	/**
	 * Sets comments to the given comments list.
	 *
	 * @param value the given comment list.
	 */
	public void setComments(List<Comment> value) {
		if (comments != value) {
			comments = value;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.COMMENT));
		}
	}

	/**
	 * Finds the first comment with a specific source.
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
		fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, key));
	}

	// ================================================================================
	// AnnotationRef, CitationRef and EvidenceRef Methods
	// ================================================================================
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
	 * Checks whether annotationRefs has the given annotationRef.
	 *
	 * @param annotationRef the annotationRef to look for.
	 * @return true if has annotationRef, false otherwise.
	 */
	@Override
	public boolean hasAnnotationRef(AnnotationRef annotationRef) {
		return annotationRefs.contains(annotationRef);
	}

	/**
	 * Creates and adds an annotationRef to annotationRefs list. Sets annotable for
	 * the given annotationRef.
	 *
	 * @param annotation the annotation for annotationRef.
	 * @return the annotationRef of added annotation.
	 */
	@Override
	public AnnotationRef addAnnotation(Annotation annotation) {
		// add annotation to pathway model if applicable
		if (pathwayModel != null && !pathwayModel.getAnnotations().contains(annotation)) {
			annotation = pathwayModel.addAnnotation(annotation);
		}
		AnnotationRef annotationRef = new AnnotationRef(annotation);
		// adds annotationRef
		if (annotationRef != null && !hasAnnotationRef(annotationRef)) {
			annotationRef.setAnnotatableTo(this);
			assert (annotationRef.getAnnotatable() == this);
			annotationRefs.add(annotationRef);
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATIONREF));
		}
		return annotationRef;
	}

	/**
	 * Creates a annotation with given properties, and adds annotation to pathway
	 * model. Creates a annotationRef for annotation, and adds to annotationRefs
	 * list for this annotatable. Calls
	 * {@link #addAnnotation(Annotation annotation)}.
	 *
	 * @param value   the name, term, or text of the annotation.
	 * @param type    the type of the annotation, e.g. ontology.
	 * @param xref    the annotation xref.
	 * @param urlLink the url link of the annotation.
	 * @return the annotationRef of added annotation.
	 */
	@Override
	public AnnotationRef addAnnotation(String value, AnnotationType type, Xref xref, String urlLink) {
		Annotation annotation = new Annotation(value, type, xref, urlLink);
		// adds annotation to pathway model, creates and adds annotationRef
		return addAnnotation(annotation);
	}

	/**
	 * Creates a annotation with given properties, and adds annotation to pathway
	 * model. Creates a annotationRef for annotation, and adds to annotationRefs
	 * list for this annotatable. Sets elementId for annotation. This method is used
	 * when reading gpml. Calls {@link #addAnnotation(Annotation annotation)}.
	 *
	 * @param elementId the elementId to set.
	 * @param value     the name, term, or text of the annotation.
	 * @param type      the type of the annotation, e.g. ontology.
	 * @param xref      the annotation xref.
	 * @param urlLink   the url link of the annotation.
	 * @return the annotationRef of added annotation.
	 */
	@Override
	public AnnotationRef addAnnotation(String elementId, String value, AnnotationType type, Xref xref, String urlLink) {
		Annotation annotation = new Annotation(value, type, xref, urlLink);
		annotation.setElementId(elementId);
		// adds annotation to pathway model, creates and adds annotationRef
		return addAnnotation(annotation);
	}

	/**
	 * Removes given annotationRef from annotationRefs list. The annotationRef
	 * ceases to exist and is terminated.
	 *
	 * @param annotationRef the annotationRef to be removed.
	 */
	@Override
	public void removeAnnotationRef(AnnotationRef annotationRef) {
		if (annotationRef != null) {
			annotationRefs.remove(annotationRef);
			annotationRef.terminate();
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATIONREF));
		}
	}

	/**
	 * Removes all annotationRefs from annotationRefs list.
	 */
	@Override
	public void removeAnnotationRefs() {
		for (int i = annotationRefs.size() - 1; i >= 0; i--) {
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
	 * Creates and adds an citationRef to citationRefs list. Sets citable for the
	 * given citationRef.
	 *
	 * @param citation the citation for citationRef.
	 * @return the citationRef of added citation.
	 */
	@Override
	public CitationRef addCitation(Citation citation) {
		// add citation to pathway model if applicable
		if (pathwayModel != null && !pathwayModel.getCitations().contains(citation)) {
			citation = pathwayModel.addCitation(citation);
		}
		CitationRef citationRef = new CitationRef(citation);
		// adds citationRef
		if (citationRef != null && !hasCitationRef(citationRef)) {
			citationRef.setCitableTo(this);
			assert (citationRef.getCitable() == this);
			citationRefs.add(citationRef);
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.CITATIONREF));
		}
		return citationRef;
	}

	/**
	 * Creates a citation with given xref and urlLink, and adds citation to pathway
	 * model. Creates a citationRef for citation, and adds to citationRefs list for
	 * this citable.Calls {@link #addCitation(Citation citation)}.
	 *
	 * @param xref    the citation xref.
	 * @param urlLink the url link and description (optional) for a web address.
	 * @return the citationRef of added citation.
	 */
	@Override
	public CitationRef addCitation(Xref xref, String urlLink) {
		Citation citation = new Citation(xref, urlLink);
		// adds citation to pathway model, creates and adds citationRef
		return addCitation(citation);
	}

	/**
	 * Creates a citation with given xref and urlLink, and adds citation to pathway
	 * model. Creates a citationRef for citation, and adds to citationRefs list for
	 * this citable. Sets elementId for citation. This method is used when reading
	 * gpml. Calls {@link #addCitation(Citation citation)}.
	 *
	 * @param elementId the elementId to set.
	 * @param xref      the citation xref.
	 * @param urlLink   the url link and description (optional) for a web address.
	 * @return the citationRef of added citation.
	 */
	@Override
	public CitationRef addCitation(String elementId, Xref xref, String urlLink) {
		Citation citation = new Citation(xref, urlLink);
		// set elementId
		citation.setElementId(elementId);
		// adds citation to pathway model, creates and adds citationRef
		return addCitation(citation);
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.CITATIONREF));
		}
	}

	/**
	 * Removes all citationRef from citationRefs list.
	 */
	@Override
	public void removeCitationRefs() {
		for (int i = citationRefs.size() - 1; i >= 0; i--) {
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
	 * Creates and adds an evidenceRef to evidenceRefs list. Sets evidenceable for
	 * the given evidenceRef.
	 *
	 * @param evidence the evidenceRef for evidenceRef.
	 * @return the evidenceRef of added evidence.
	 */
	@Override
	public EvidenceRef addEvidence(Evidence evidence) {
		// add evidence to pathway model if applicable
		if (pathwayModel != null && !pathwayModel.getEvidences().contains(evidence)) {
			evidence = pathwayModel.addEvidence(evidence);
		}
		EvidenceRef evidenceRef = new EvidenceRef(evidence);
		// adds evidenceRef
		if (evidenceRef != null && !hasEvidenceRef(evidenceRef)) {
			evidenceRef.setEvidenceableTo(this);
			assert (evidenceRef.getEvidenceable() == this);
			evidenceRefs.add(evidenceRef);
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.EVIDENCEREF));
		}
		return evidenceRef;
	}

	/**
	 * Creates an evidence with given properties, and adds evidence to pathway
	 * model. Creates a evidenceRef for evidence, and adds to evidenceRefs list for
	 * this evidenceable. Calls {@link #addEvidence(Evidence evidence)}.
	 *
	 * @param value   the name, term, or text of the evidence.
	 * @param xref    the evidence xref.
	 * @param urlLink the url link and description (optional) for a web address.
	 * @return the evidenceRef of added evidence.
	 */
	@Override
	public EvidenceRef addEvidence(String value, Xref xref, String urlLink) {
		Evidence evidence = new Evidence(value, xref, urlLink);
		// adds evidence to pathway model, creates and adds evidenceRef
		return addEvidence(evidence);
	}

	/**
	 * Creates an evidence with given properties, and adds evidence to pathway
	 * model. Creates a evidenceRef for evidence, and adds to evidenceRefs list for
	 * this evidenceable. Sets elementId for evidence. This method is used when
	 * reading gpml. Calls {@link #addEvidence(Evidence evidence)}.
	 *
	 * @param elementId the elementId to set.
	 * @param value     the name, term, or text of the evidence.
	 * @param xref      the evidence xref.
	 * @param urlLink   the url link and description (optional) for a web address.
	 * @return the evidenceRef of added evidence.
	 */
	@Override
	public EvidenceRef addEvidence(String elementId, String value, Xref xref, String urlLink) {
		Evidence evidence = new Evidence(value, xref, urlLink);
		// set elementId
		evidence.setElementId(elementId);
		// adds evidence to pathway model, creates and adds evidenceRef
		return addEvidence(evidence);
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.EVIDENCEREF));
		}
	}

	/**
	 * Removes all evidenceRefs from evidenceRefs list.
	 */
	@Override
	public void removeEvidenceRefs() {
		for (int i = evidenceRefs.size() - 1; i >= 0; i--) {
			removeEvidenceRef(evidenceRefs.get(i));
		}
	}

	/**
	 * Terminates this pathway element. The pathway model, if any, is unset from
	 * this pathway element. Links to all annotationRefs, citationRefs, and
	 * evidenceRefs are removed from this data node.
	 */
	@Override
	protected void terminate() {
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();
		super.terminate();
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
	 * <li>Doesn't change parent, only fields
	 * <li>Used by UndoAction.
	 * <li>AnnotationRefs, citationRefs, and evidenceRefs are copied later using
	 * {@link copyReferencesFrom}.
	 * </ol>
	 *
	 * @param src the source pathway element.
	 */
	public void copyValuesFrom(PathwayElement src) {
		dynamicProperties = new TreeMap<String, String>(src.dynamicProperties); // create copy
		comments = new ArrayList<Comment>();
		for (Comment c : src.comments) {
			try {
				comments.add((Comment) c.clone());
			} catch (CloneNotSupportedException e) {
				assert (false);
				/* not going to happen */
			}
		}
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copies this pathway element.
	 *
	 * @return the copyElement for the new pathway element and this source pathway
	 *         element.
	 */
	public abstract CopyElement copy();

	/**
	 * Copies references from the given source pathway element.
	 * <p>
	 * NB:
	 * <ol>
	 * <li>To be called after new pathway element is added to a pathway model.
	 * <li>The srcElement may be the immediate copy element source of the new
	 * pathway element, or an older source.
	 * </ol>
	 *
	 * @param srcElement the source element to copy references from.
	 */
	public void copyReferencesFrom(PathwayElement srcElement) {
		if (srcElement != null && this.getObjectType() == srcElement.getObjectType()) {
			copyAnnotationRefs(srcElement.getAnnotationRefs());
			copyCitationRefs(srcElement.getCitationRefs());
			copyEvidenceRefs(srcElement.getEvidenceRefs());
		}
	}

	/**
	 * Copies citationsRefs and nested annotationRefs if applicable.
	 *
	 * @param citationRefs the citationsRefs list.
	 */
	private void copyCitationRefs(List<CitationRef> citationRefs) {
		for (CitationRef citationRef : citationRefs) {
			this.addCitation(citationRef.getCitation().copyRef());
			copyAnnotationRefs(citationRef.getAnnotationRefs());
		}
	}

	/**
	 * Copies annotationRefs and nested citationRefs and evidenceRefs if applicable.
	 *
	 * @param annotationRefs the annotationRefs list.
	 */
	private void copyAnnotationRefs(List<AnnotationRef> annotationRefs) {
		for (AnnotationRef annotationRef : annotationRefs) {
			this.addAnnotation(annotationRef.getAnnotation().copyRef());
			copyCitationRefs(annotationRef.getCitationRefs());
			copyEvidenceRefs(annotationRef.getEvidenceRefs());

		}
	}

	/**
	 * Copies evidenceRefs.
	 *
	 * @param evidenceRefs the evidenceRefs list.
	 */
	private void copyEvidenceRefs(List<EvidenceRef> evidenceRefs) {
		for (EvidenceRef evidenceRef : evidenceRefs) {
			this.addEvidence(evidenceRef.getEvidence().copyRef());
		}
	}

	// ================================================================================
	// Property Methods
	// ================================================================================
	/**
	 * Returns keys of available static properties and dynamic properties as an
	 * object list
	 */
	@Override
	public Set<Object> getPropertyKeys() {
		Set<Object> keys = new HashSet<Object>();
		keys.addAll(getStaticPropertyKeys());
		keys.addAll(getDynamicPropertyKeys());
		return keys;
	}

	/**
	 * Returns all static properties for this pathway object.
	 *
	 * @return result the set of static property for this pathway object.
	 */
	@Override
	public Set<StaticProperty> getStaticPropertyKeys() {
		Set<StaticProperty> result = super.getStaticPropertyKeys();
		Set<StaticProperty> propsPathwayElement = EnumSet.of(StaticProperty.COMMENT, StaticProperty.ANNOTATIONREF,
				StaticProperty.CITATIONREF, StaticProperty.EVIDENCEREF);
		result.addAll(propsPathwayElement);
		return result;
	}

	/**
	 * Returns property of given key.
	 *
	 * @param key the key.
	 * @return
	 */
	@Override
	public Object getPropertyEx(Object key) {
		if (key instanceof StaticProperty) {
			return getStaticProperty((StaticProperty) key);
		} else if (key instanceof String) {
			return getDynamicProperty((String) key);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Sets dynamic or static properties at the same time.
	 *
	 * @param key   the key for the property to set.
	 * @param value the value for the property to set.
	 */
	@Override
	public void setPropertyEx(Object key, Object value) {
		if (key instanceof StaticProperty) {
			setStaticProperty((StaticProperty) key, value);
		} else if (key instanceof String) {
			setDynamicProperty((String) key, value.toString());
		} else {
			throw new IllegalArgumentException();
		}
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
			case COMMENT:
				result = getComments();
				break;
			case ANNOTATIONREF:
				// advanced TODO
				break;
			case CITATIONREF:
				// advanced TODO
				break;
			case EVIDENCEREF:
				// advanced TODO
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
	 * @param value the property value.
	 */
	@Override
	public void setStaticProperty(StaticProperty key, Object value) {
		super.setStaticProperty(key, value);
		switch (key) {
		case COMMENT:
			setComments((List<Comment>) value);
			break;
		case ANNOTATIONREF:
			// advanced TODO
			break;
		case CITATIONREF:
			// advanced TODO
			break;
		case EVIDENCEREF:
			// advanced TODO
			break;
		default:
			// do nothing
		}
	}

	// ================================================================================
	// Comment Class
	// ================================================================================
	/**
	 * This class stores all information relevant to a Comment. Comments can be
	 * descriptions or arbitrary notes. Each comment has a source and a text.
	 * Pathway elements (e.g. DataNode, State, Interaction, GraphicalLine, Label,
	 * Shape, Group) can have zero or more comments with it.
	 *
	 * @author unknown, finterly
	 */
	public class Comment implements Cloneable {

		private String commentText; // required
		private String source; // optional

		// ================================================================================
		// Constructors
		// ================================================================================
		/**
		 * Instantiates a Comment with commentText and source. This private constructor
		 * is called by {@link #addComment(String commentText, String source)}.
		 *
		 * @param commentText the text of the comment, between Comment tags in GPML.
		 * @param source      the source of this comment.
		 *
		 */
		public Comment(String commentText, String source) {
			setCommentText(commentText);
			setSource(source);
		}

		// ================================================================================
		// Clone Methods
		// ================================================================================

		/**
		 * Clones this comment.
		 * 
		 * @return the cloned comment.
		 */
		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		// ================================================================================
		// Accessors
		// ================================================================================

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
			if (v != null && !Utils.stringEquals(commentText, v)) {
				commentText = v;
				fireObjectModifiedEvent(
						PathwayObjectEvent.createSinglePropertyEvent(PathwayElement.this, StaticProperty.COMMENT));
			}
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
			if (v != null && !Utils.stringEquals(source, v)) {
				source = v;
				fireObjectModifiedEvent(
						PathwayObjectEvent.createSinglePropertyEvent(PathwayElement.this, StaticProperty.COMMENT));
			}
		}

		/**
		 * Writes comment out as a string.
		 */
		@Override
		public String toString() {
			String src = "";
			if (source != null && !"".equals(source)) {
				src = " (" + source + ")";
			}
			return commentText + src;
		}
	}

	/**
	 * Abstract class of {@link AnnotationRef}, {@link CitationRef}, and
	 * {@link EvidenceRef}.
	 *
	 * @author finterly
	 *
	 */
	public abstract class InfoRef {

		/**
		 * Returns the top target pathway element {@link PathwayElement} for a
		 * {@link AnnotationRef}, {@link CitationRef}, or {@link EvidenceRef}.
		 *
		 * NB: Returns the top pathway element, outer class, even if this ref has been
		 * removed.
		 *
		 * @return the top target pathway element of the infoRef.
		 */
		public PathwayElement getTopPathwayElement() {
			return PathwayElement.this;
		}

	}

	// ================================================================================
	// AnnotationRef Class
	// ================================================================================
	/**
	 * This class stores information for an AnnotationRef with source
	 * {@link Annotation}, target {@link Annotatable}, and a list of
	 * {@link CitationRef} and/or {@link EvidenceRef}. The Annotatable target can be
	 * a {@link PathwayElement}, or {@link CitationRef}. In gpml:AnnotationRef, the
	 * attribute elementRef refers to the elementId of the source gpml:Annotation.
	 *
	 * @author finterly
	 */
	public class AnnotationRef extends InfoRef implements Citable, Evidenceable {

		private Annotation annotation; // source annotation, elementRef in GPML
		private Annotatable annotatable; // target pathway, pathway element, or citationRef
		private List<CitationRef> citationRefs; // 0 to unbounded
		private List<EvidenceRef> evidenceRefs; // 0 to unbounded

		// ================================================================================
		// Constructors
		// ================================================================================
		/**
		 * Instantiates an AnnotationRef given source {@link Annotation} and initializes
		 * citationRefs and evidenceRefs lists.
		 *
		 * @param annotation the source annotation this AnnotationRef refers to.
		 */
		public AnnotationRef(Annotation annotation) {
			setAnnotationTo(annotation);
			this.citationRefs = new ArrayList<CitationRef>();
			this.evidenceRefs = new ArrayList<EvidenceRef>();
		}

		// ================================================================================
		// Accessors
		// ================================================================================
		/**
		 * Returns the annotation referenced.
		 *
		 * @return annotation the annotation referenced.
		 */
		public Annotation getAnnotation() {
			return annotation;
		}

		/**
		 * Checks whether this annotationRef has a source annotation.
		 *
		 * @return true if and only if the annotation of this annotationRef is
		 *         effective.
		 */
		public boolean hasAnnotation() {
			return getAnnotation() != null;
		}

		/**
		 * Sets the source annotation for this annotationRef. Adds this annotationRef to
		 * the source annotation.
		 *
		 * @param annotation the given source annotation to set.
		 */
		public void setAnnotationTo(Annotation annotation) {
			if (annotation == null)
				throw new IllegalArgumentException("Invalid annotation.");
			if (hasAnnotation())
				throw new IllegalStateException("AnnotationRef already has a source annotation.");
			setAnnotation(annotation);
			if (!annotation.hasAnnotationRef(this))
				annotation.addAnnotationRef(this);
		}

		/**
		 * Sets the source annotation for this annotationRef.
		 *
		 * @param v the given source annotation to set.
		 */
		private void setAnnotation(Annotation v) {
			annotation = v;
		}

		/**
		 * Unsets the annotation, if any, from this annotationRef. Removes this
		 * annotationRef from the source annotation.
		 */
		protected void unsetAnnotation() {
			if (hasAnnotation()) {
				Annotation annotation = getAnnotation();
				setAnnotation(null);
				if (annotation.hasAnnotationRef(this))
					annotation.removeAnnotationRef(this);
			}
		}

		/**
		 * Returns the target pathway, pathway element, or citationRef
		 * {@link Annotatable} for this annotationRef.
		 *
		 * @return annotatable the target of the annotationRef.
		 */
		public Annotatable getAnnotatable() {
			return annotatable;
		}

		/**
		 * Checks whether this annotationRef has a target annotatable.
		 *
		 * @return true if and only if the annotatable of this annotationRef is
		 *         effective.
		 */
		public boolean hasAnnotatable() {
			return getAnnotatable() != null;
		}

		/**
		 * Sets the target pathway element or citationRef {@link Annotatable} for this
		 * annotationRef. NB: Annotatable is only set when an Annotatable adds an
		 * AnnotationRef. This method is not used directly.
		 *
		 * @param annotatable the given target annotatable to set.
		 */
		protected void setAnnotatableTo(Annotatable annotatable) {
			if (annotatable == null)
				throw new IllegalArgumentException("Invalid annotatable.");
			if (hasAnnotatable())
				throw new IllegalStateException("AnnotationRef already has a target annotatable.");
			setAnnotatable(annotatable);
		}

		/**
		 * Sets the target pathway, pathway element, or citationRef {@link Annotatable}
		 * for this annotationRef.
		 *
		 * @param v the given target annotatable to set.
		 */
		private void setAnnotatable(Annotatable v) {
			annotatable = v;
		}

		/**
		 * Unsets the annotatable, if any, from this annotationRef. NB: This method is
		 * not used directly.
		 */
		protected void unsetAnnotatable() {
			if (hasAnnotatable()) {
				Annotatable annotatable = getAnnotatable();
				setAnnotatable(null);
				if (annotatable.hasAnnotationRef(this))
					annotatable.removeAnnotationRef(this);
			}
		}

		// ================================================================================
		// CitationRef Methods
		// ================================================================================
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
		 * Creates and adds an citationRef to citationRefs list. Sets citable for the
		 * given citationRef.
		 *
		 * @param citation the citation for citationRef.
		 * @return the citationRef of added citation.
		 */
		@Override
		public CitationRef addCitation(Citation citation) {
			// add citation to pathway model if applicable
			if (pathwayModel != null && !pathwayModel.getCitations().contains(citation)) {
				citation = pathwayModel.addCitation(citation);
			}
			CitationRef citationRef = new CitationRef(citation);
			// adds citationRef
			if (citationRef != null && !hasCitationRef(citationRef)) {
				citationRef.setCitableTo(this);
				assert (citationRef.getCitable() == this);
				citationRefs.add(citationRef);
				fireObjectModifiedEvent(
						PathwayObjectEvent.createSinglePropertyEvent(PathwayElement.this, StaticProperty.CITATIONREF));
			}
			return citationRef;
		}

		/**
		 * Creates a citation with given xref and urlLink, and adds citation to pathway
		 * model. Creates a citationRef for citation, and adds to citationRefs list for
		 * this citable.
		 *
		 * @param xref    the citation xref.
		 * @param urlLink the url link and description (optional) for a web address.
		 * @return the citationRef of added citation.
		 */
		@Override
		public CitationRef addCitation(Xref xref, String urlLink) {
			Citation citation = new Citation(xref, urlLink);
			// creates and adds citationRef
			return addCitation(citation);
		}

		/**
		 * Creates a citation with given xref and urlLink, and adds citation to pathway
		 * model. Creates a citationRef for citation, and adds to citationRefs list for
		 * this citable. Sets elementId for citation. This method is used when reading
		 * gpml. Calls {@link #addCitation(Citation citation)}.
		 *
		 * @param elementId the elementId to set.
		 * @param xref      the citation xref.
		 * @param urlLink   the url link and description (optional) for a web address.
		 * @return the citationRef of added citation.
		 */
		@Override
		public CitationRef addCitation(String elementId, Xref xref, String urlLink) {
			Citation citation = new Citation(xref, urlLink);
			// set elementId
			citation.setElementId(elementId);
			// creates and adds citationRef
			return addCitation(citation);
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
				fireObjectModifiedEvent(
						PathwayObjectEvent.createSinglePropertyEvent(PathwayElement.this, StaticProperty.CITATIONREF));
			}
		}

		/**
		 * Removes all citationRef from citationRefs list.
		 */
		@Override
		public void removeCitationRefs() {
			for (int i = citationRefs.size() - 1; i >= 0; i--) {
				removeCitationRef(citationRefs.get(i));
			}
		}

		// ================================================================================
		// EvidenceRef Methods
		// ================================================================================
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
		 * Checks whether citationRefs has the given citationRef.
		 *
		 * @param evidenceRef the evidenceRef to look for.
		 * @return true if has evidenceRef, false otherwise.
		 */
		@Override
		public boolean hasEvidenceRef(EvidenceRef evidenceRef) {
			return evidenceRefs.contains(evidenceRef);
		}

		/**
		 * Creates and adds an evidenceRef to evidenceRefs list. Sets evidenceable for
		 * the given evidenceRef.
		 *
		 * @param evidence the evidence for evidenceRef.
		 * @return the evidencRef of added evidence.
		 */
		@Override
		public EvidenceRef addEvidence(Evidence evidence) {
			// add evidence to pathway model if applicable
			if (pathwayModel != null && !pathwayModel.getEvidences().contains(evidence)) {
				evidence = pathwayModel.addEvidence(evidence);
			}
			EvidenceRef evidenceRef = new EvidenceRef(evidence);
			// adds evidenceRef
			if (evidenceRef != null && !hasEvidenceRef(evidenceRef)) {
				evidenceRef.setEvidenceableTo(this);
				assert (evidenceRef.getEvidenceable() == this);
				evidenceRefs.add(evidenceRef);
				fireObjectModifiedEvent(
						PathwayObjectEvent.createSinglePropertyEvent(PathwayElement.this, StaticProperty.EVIDENCEREF));
			}
			return evidenceRef;
		}

		/**
		 * Creates an evidence with given properties, and adds evidence to pathway
		 * model. Creates a evidenceRef for evidence, and adds to evidenceRefs list for
		 * this evidenceable. Calls {@link #addEvidence(Evidence evidence)}.
		 *
		 * @param value   the name, term, or text of the evidence.
		 * @param xref    the evidence xref.
		 * @param urlLink the url link and description (optional) for a web address.
		 * @return the evidencRef of added evidence.
		 */
		@Override
		public EvidenceRef addEvidence(String value, Xref xref, String urlLink) {
			Evidence evidence = new Evidence(value, xref, urlLink);
			// creates and adds evidenceRef
			return addEvidence(evidence);
		}

		/**
		 * Creates an evidence with given properties, and adds evidence to pathway
		 * model. Creates a evidenceRef for evidence, and adds to evidenceRefs list for
		 * this evidenceable. Sets elementId for evidence. This method is used when
		 * reading gpml. Calls {@link #addEvidence(Evidence evidence)}.
		 *
		 * @param elementId the elementId to set.
		 * @param value     the name, term, or text of the evidence.
		 * @param xref      the evidence xref.
		 * @param urlLink   the url link and description (optional) for a web address.
		 * @return the evidencRef of added evidence.
		 */
		@Override
		public EvidenceRef addEvidence(String elementId, String value, Xref xref, String urlLink) {
			Evidence evidence = new Evidence(value, xref, urlLink);
			// set elementId
			evidence.setElementId(elementId);
			// creates and adds evidenceRef
			return addEvidence(evidence);
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
				fireObjectModifiedEvent(
						PathwayObjectEvent.createSinglePropertyEvent(PathwayElement.this, StaticProperty.EVIDENCEREF));
			}
		}

		/**
		 * Removes all evidenceRefs from evidenceRefs list.
		 */
		@Override
		public void removeEvidenceRefs() {
			for (int i = evidenceRefs.size() - 1; i >= 0; i--) {
				removeEvidenceRef(evidenceRefs.get(i));
			}
		}

		// ================================================================================
		// Other Methods
		// ================================================================================
		/**
		 * Terminates this annotationRef. The annotation and annotatable, if any, are
		 * unset from this annotationRef.
		 */
		protected void terminate() {
			removeCitationRefs();
			removeEvidenceRefs();
			unsetAnnotation();
			unsetAnnotatable();
		}

		/**
		 * Writes annotationRef out as a string.
		 */
		@Override
		public String toString() {
			boolean semicolon = false;
			StringBuilder builder = new StringBuilder();
			// Value and Type
			String value = annotation.getValue();
			AnnotationType type = annotation.getType();
			if (value != null && type != null && !Utils.stringEquals(value, "")) {
				builder.append(value).append(" (" + type.getName() + ")").append("</A>");
				semicolon = true;
			}
			// Xref
			Xref xref = annotation.getXref();
			if (xref != null) {
				String id = XrefUtils.getIdentifier(xref);
				String ds = XrefUtils.getDataSource(xref).getFullName();
				if (semicolon) {
					builder.append("; ");
				}
				if (!Utils.isEmpty(id)) {
					builder.append("<A href='" + xref.getKnownUrl()).append("'>").append(ds).append(" ").append(id)
							.append("</A>");
					semicolon = true;
				}
			}
			// Url
			String urlLink = annotation.getUrlLink();
			if (!Utils.isEmpty(urlLink)) {
				if (semicolon) {
					builder.append("; ");
				}
				builder.append("<A href='" + urlLink).append("'>").append(urlLink).append("</A>");
			}
			return builder.toString();
		}

	}

	// ================================================================================
	// CitationRef Class
	// ================================================================================
	/**
	 * This class stores information for a CitationRef with source {@link Citation},
	 * target {@link Citable}, and a list of {@link AnnotationRef}. The Citable
	 * target can be a {@link PathwayElement}, or {@link AnnotationRef}. In
	 * gpml:CitationRef, the attribute elementRef refers to the elementId of the
	 * source gpml:Citation.
	 *
	 * @author finterly
	 */
	public class CitationRef extends InfoRef implements Annotatable {

		private Citation citation; // source citation, elementRef in GPML
		private Citable citable; // target pathway, pathway element, or annotationRef
		private List<AnnotationRef> annotationRefs; // 0 to unbounded

		// ================================================================================
		// Constructors
		// ================================================================================
		/**
		 * Instantiates an CitationRef given source {@link Citation} and initializes
		 * annotationRefs lists.
		 *
		 * @param citation the source citation this CitationRef refers to.
		 */
		protected CitationRef(Citation citation) {
			setCitationTo(citation);
			annotationRefs = new ArrayList<AnnotationRef>();
		}

		// ================================================================================
		// Accessors
		// ================================================================================
		/**
		 * Returns the citation referenced.
		 *
		 * @return citation the citation referenced.
		 */
		public Citation getCitation() {
			return citation;
		}

		/**
		 * Checks whether this citationRef has a source citation.
		 *
		 * @return true if and only if the citation of this citationRef is effective.
		 */
		public boolean hasCitation() {
			return getCitation() != null;
		}

		/**
		 * Sets the source citation for this citationRef. Adds this citationRef to the
		 * source citation.
		 *
		 * @param citation the given source citation to set.
		 */
		public void setCitationTo(Citation citation) {
			if (citation == null)
				throw new IllegalArgumentException("Invalid citation.");
			if (hasCitation())
				throw new IllegalStateException("CitationRef already has a source citation.");
			setCitation(citation);
			if (!citation.hasCitationRef(this))
				citation.addCitationRef(this);
		}

		/**
		 * Sets the source citation for this citationRef.
		 *
		 * @param v the given source citation to set.
		 */
		private void setCitation(Citation v) {
			citation = v;
		}

		/**
		 * Unsets the citation, if any, from this citationRef. Removes this citationRef
		 * from the source citation.
		 */
		public void unsetCitation() {
			if (hasCitation()) {
				Citation citation = getCitation();
				setCitation(null);
				if (citation.hasCitationRef(this))
					citation.removeCitationRef(this);
			}
		}

		/**
		 * Returns the target pathway, pathway element, or annotationRef {@link Citable}
		 * for this citationRef.
		 *
		 * @return citable the target of the citationRef.
		 */
		public Citable getCitable() {
			return citable;
		}

		/**
		 * Checks whether this citationRef has a target citable.
		 *
		 * @return true if and only if the citable of this citationRef is effective.
		 */
		public boolean hasCitable() {
			return getCitable() != null;
		}

		/**
		 * Sets the target pathway, pathway element, or annotationRef {@link Citable}
		 * for this annotationRef. NB: Citable is only set when an Citable adds a
		 * CitationRef. This method is not used directly.
		 *
		 * @param citable the given target citable to set.
		 */
		protected void setCitableTo(Citable citable) {
			if (citable == null)
				throw new IllegalArgumentException("Invalid citable.");
			if (hasCitable())
				throw new IllegalStateException("CitationRef already has a target citable.");
			setCitable(citable);
		}

		/**
		 * Sets the target pathway, pathway element, or annotationRef {@link Citable} to
		 * which the annotationRef belongs.
		 *
		 * @param v the given target citable to set.
		 */
		private void setCitable(Citable v) {
			citable = v;
		}

		/**
		 * Unsets the citable, if any, from this citationRef. NB: This method is not
		 * used directly.
		 */
		protected void unsetCitable() {
			if (hasCitable()) {
				Citable citable = getCitable();
				setCitable(null);
				if (citable.hasCitationRef(this))
					citable.removeCitationRef(this);
			}
		}

		// ================================================================================
		// AnnotationRef Methods
		// ================================================================================
		/**
		 * Returns the list of annotation references.
		 *
		 * @return annotationRefs the list of annotations referenced, an empty list if
		 *         no properties are defined.
		 */
		@Override
		public List<AnnotationRef> getAnnotationRefs() {
			return annotationRefs;
		}

		/**
		 * Checks whether annotationRefs has the given annotationRef.
		 *
		 * @param annotationRef the annotationRef to look for.
		 * @return true if has annotationRef, false otherwise.
		 */
		@Override
		public boolean hasAnnotationRef(AnnotationRef annotationRef) {
			return annotationRefs.contains(annotationRef);
		}

		/**
		 * Creates and adds an annotationRef to annotationRefs list.Sets annotable for
		 * the given annotationRef.
		 *
		 * @param annotation the annotation for annotationRef.
		 * @return the annotationRef of added annotation.
		 */
		@Override
		public AnnotationRef addAnnotation(Annotation annotation) {
			// add annotation to pathway model if applicable
			if (pathwayModel != null && !pathwayModel.getAnnotations().contains(annotation)) {
				annotation = pathwayModel.addAnnotation(annotation);
			}
			AnnotationRef annotationRef = new AnnotationRef(annotation);
			// adds annotationRef
			if (annotationRef != null && !hasAnnotationRef(annotationRef)) {
				annotationRef.setAnnotatableTo(this);
				assert (annotationRef.getAnnotatable() == this);
				annotationRefs.add(annotationRef);
				fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(PathwayElement.this,
						StaticProperty.ANNOTATIONREF));
			}
			return annotationRef;
		}

		/**
		 * Creates a annotation with given properties, and adds annotation to pathway
		 * model. Creates a annotationRef for annotation, and adds to annotationRefs
		 * list for this annotatable. Calls
		 * {@link #addAnnotation(Annotation annotation)}.
		 *
		 * @param value   the name, term, or text of the annotation.
		 * @param type    the type of the annotation, e.g. ontology.
		 * @param xref    the annotation xref.
		 * @param urlLink the url link of the annotation.
		 * @return the annotationRef of added annotation.
		 */
		@Override
		public AnnotationRef addAnnotation(String value, AnnotationType type, Xref xref, String urlLink) {
			Annotation annotation = new Annotation(value, type, xref, urlLink);
			// creates and adds annotationRef
			return addAnnotation(annotation);
		}

		/**
		 * Creates a annotation with given properties, and adds annotation to pathway
		 * model. Creates a annotationRef for annotation, and adds to annotationRefs
		 * list for this annotatable. Sets elementId for annotation. This method is used
		 * when reading gpml. Calls {@link #addAnnotation(Annotation annotation)}.
		 *
		 * @param elementId the elementId to set.
		 * @param value     the name, term, or text of the annotation.
		 * @param type      the type of the annotation, e.g. ontology.
		 * @param xref      the annotation xref.
		 * @param urlLink   the url link of the annotation.
		 * @return the annotationRef of added annotation.
		 */
		@Override
		public AnnotationRef addAnnotation(String elementId, String value, AnnotationType type, Xref xref,
				String urlLink) {
			Annotation annotation = new Annotation(value, type, xref, urlLink);
			annotation.setElementId(elementId);
			// creates and adds annotationRef
			return addAnnotation(annotation);
		}

		/**
		 * Removes given annotationRef from annotationRefs list. The annotationRef
		 * ceases to exist and is terminated.
		 *
		 * @param annotationRef the annotationRef to be removed.
		 */
		@Override
		public void removeAnnotationRef(AnnotationRef annotationRef) {
			if (annotationRef != null) {
				annotationRefs.remove(annotationRef);
				annotationRef.terminate();
				fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(PathwayElement.this,
						StaticProperty.ANNOTATIONREF));
			}
		}

		/**
		 * Removes all annotationRefs from annotationRefs list.
		 */
		@Override
		public void removeAnnotationRefs() {
			for (int i = annotationRefs.size() - 1; i >= 0; i--) {
				removeAnnotationRef(annotationRefs.get(i));
			}
		}

		// ================================================================================
		// Other Methods
		// ================================================================================
		/**
		 * Terminates this citationRef. The citation and citable, if any, are unset from
		 * this citationRef. Links to all annotationRefs are removed from this
		 * citationRef.
		 */
		protected void terminate() {
			removeAnnotationRefs();
			unsetCitation();
			unsetCitable();
		}

		/**
		 * Writes citationRef out as a string.
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			boolean semicolon = false;
			// Xref
			Xref xref = citation.getXref();
			if (xref != null) {
				String id = XrefUtils.getIdentifier(xref);
				String ds = XrefUtils.getDataSource(xref).getFullName();
				if (!Utils.isEmpty(id)) {
					builder.append("<A href='" + xref.getKnownUrl()).append("'>").append(ds).append(" ").append(id)
							.append("</A>");
					semicolon = true;
				}
			}
			// Url
			String urlLink = citation.getUrlLink();
			if (!Utils.isEmpty(urlLink)) {
				if (semicolon) {
					builder.append("; ");
				}
				builder.append("<A href='" + urlLink).append("'>").append(urlLink).append("</A>");
			}
			return builder.toString();
		}

	}

	// ================================================================================
	// EvidenceRef Class
	// ================================================================================
	/**
	 * This class stores information for a EvidenceRef which references an
	 * {@link Evidence}.
	 *
	 * @author finterly
	 */
	public class EvidenceRef extends InfoRef {

		private Evidence evidence; // source evidence, elementRef in GPML
		private Evidenceable evidenceable; // target pathway, pathway element, or evidenceRef

		// ================================================================================
		// Constructors
		// ================================================================================
		/**
		 * Instantiates an EvidenceRef given source {@link Evidence} and initializes
		 * evidenceRefs lists.
		 *
		 * @param evidence the source evidence this EvidenceRef refers to.
		 */
		protected EvidenceRef(Evidence evidence) {
			setEvidenceTo(evidence);
		}

		// ================================================================================
		// Accessors
		// ================================================================================
		/**
		 * Returns the evidence referenced.
		 *
		 * @return evidence the evidence referenced.
		 */
		public Evidence getEvidence() {
			return evidence;
		}

		/**
		 * Checks whether this evidenceRef has a source evidence.
		 *
		 * @return true if and only if the evidence of this evidenceRef is effective.
		 */
		public boolean hasEvidence() {
			return getEvidence() != null;
		}

		/**
		 * Sets the source evidence for this evidenceRef. Adds this evidencRef to the
		 * source evidence.
		 *
		 * @param evidence the given source evidence to set.
		 */
		public void setEvidenceTo(Evidence evidence) {
			if (evidence == null)
				throw new IllegalArgumentException("Invalid evidence.");
			if (hasEvidence())
				throw new IllegalStateException("EvidenceRef already has a source citation.");
			setEvidence(evidence);
			if (!evidence.hasEvidenceRef(this))
				evidence.addEvidenceRef(this);
		}

		/**
		 * Sets the source evidence for this evidenceRef.
		 *
		 * @param v the given source evidence to set.
		 */
		private void setEvidence(Evidence v) {
			evidence = v;
		}

		/**
		 * Unsets the evidence, if any, from this evidenceRef. Removes this evidenceRef
		 * from the source evidence.
		 */
		public void unsetEvidence() {
			if (hasEvidence()) {
				Evidence evidence = getEvidence();
				setEvidence(null);
				if (evidence.hasEvidenceRef(this))
					evidence.removeEvidenceRef(this);
			}
		}

		/**
		 * Returns the target pathway, pathway element, or evidenceRef
		 * {@link Evidenceable} for this evidenceRef.
		 *
		 * @return evidenceable the target of the evidenceRef.
		 */
		public Evidenceable getEvidenceable() {
			return evidenceable;
		}

		/**
		 * Checks whether this evidenceRef has a target evidenceable.
		 *
		 * @return true if and only if the evidenceable of this evidenceRef is
		 *         effective.
		 */
		public boolean hasEvidenceable() {
			return getEvidenceable() != null;
		}

		/**
		 * Sets the target pathway, pathway element, or evidenceRef {@link Evidenceable}
		 * for this evidenceRef. NB: Evidenceable is only set when an Evidenceable adds
		 * a EvidenceRef. This method is not used directly.
		 *
		 * @param evidenceable the given target evidenceable to set.
		 */
		protected void setEvidenceableTo(Evidenceable evidenceable) {
			if (evidenceable == null)
				throw new IllegalArgumentException("Invalid evidenceable.");
			if (hasEvidenceable())
				throw new IllegalStateException("EvidenceRef already has a target evidenceable.");
			setEvidenceable(evidenceable);
		}

		/**
		 * Sets the target pathway, pathway element, or evidenceRef {@link Evidenceable}
		 * to which the evidenceRef belongs.
		 *
		 * @param v the given target evidenceable to set.
		 */
		private void setEvidenceable(Evidenceable v) {
			evidenceable = v;
		}

		/**
		 * Unsets the evidenceable, if any, from this evidenceRef. NB: This method is
		 * not used directly.
		 */
		protected void unsetEvidenceable() {
			if (hasEvidenceable()) {
				Evidenceable evidenceable = getEvidenceable();
				setEvidenceable(null);
				if (evidenceable.hasEvidenceRef(this))
					evidenceable.removeEvidenceRef(this);
			}
		}

		// ================================================================================
		// Other Methods
		// ================================================================================
		/**
		 * Terminates this evidenceRef. The evidence and evidenceable, if any, are unset
		 * from this evidenceRef. Links to all evidenceRefs are removed from this
		 * evidenceRef.
		 */
		protected void terminate() {
			unsetEvidence();
			unsetEvidenceable();
		}

		/**
		 * Writes evidenceRef out as string.
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			boolean semicolon = false;
			// Xref
			Xref xref = evidence.getXref();
			if (xref != null) {
				String id = XrefUtils.getIdentifier(xref);
				String ds = XrefUtils.getDataSource(xref).getFullName();
				if (!Utils.isEmpty(id)) {
					if (semicolon) {
						builder.append("; ");
					}
					builder.append(ds).append(":").append(id);
					semicolon = true;
				}
			}
			// Value and Type
			String value = evidence.getValue();
			if (value != null && !Utils.stringEquals(value, "")) {
				if (semicolon) {
					builder.append("; ");
				}
				builder.append(value);
				semicolon = true;
			}
			// Url
			String urlLink = evidence.getUrlLink();
			if (!Utils.isEmpty(urlLink)) {
				if (semicolon) {
					builder.append("; ");
				}
				builder.append("<A href='" + urlLink).append("'>").append(urlLink).append("</A>");
			}
			return builder.toString();
		}
	}

}
