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
import java.util.Objects;

import org.bridgedb.Xref;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.type.AnnotationType;
import org.pathvisio.props.StaticProperty;

/**
 * This class stores information for an Annotation.
 * 
 * @author finterly
 */
public class Annotation extends PathwayElement {

	private String value;
	private AnnotationType type;
	private Xref xref; // optional
	private UrlRef url; // optional
	private List<AnnotationRef> annotationRefs; // annotationRefs with this annotation as source

	/**
	 * Instantiates an Annotation pathway element given all possible parameters:
	 * elementId, parent pathway model, value, type, url, and xref.
	 * 
	 * @param value the name, term, or text of the annotation.
	 * @param type  the type of the annotation, e.g. ontology.
	 * @param xref  the annotation xref.
	 * @param url   the url of the annotation.
	 */
	public Annotation(String value, AnnotationType type, Xref xref, UrlRef url) {
		super();
		this.value = value;
		this.type = type;
		this.xref = xref;
		this.url = url;
		this.annotationRefs = new ArrayList<AnnotationRef>();
	}

	/**
	 * Instantiates an Annotation given all possible parameters except xref.
	 */
	public Annotation(String value, AnnotationType type, UrlRef url) {
		this(value, type, null, url);
	}

	/**
	 * Instantiates an Annotation given all possible parameters except url.
	 */
	public Annotation(String value, AnnotationType type, Xref xref) {
		this(value, type, xref, null);
	}

	/**
	 * Instantiates an Annotation given all possible parameters except url and xref.
	 */
	public Annotation(String value, AnnotationType type) {
		this(value, type, null, null);
	}

	/**
	 * Returns the name, term, or text of this annotation.
	 * 
	 * @return value the name, term, or text of this annotation.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the name, term, or text of this annotation.
	 * 
	 * @param v the name, term, or text of this annotation.
	 */
	public void setValue(String v) {
		value = v;
	}

	/**
	 * Returns the type of this annotation.
	 * 
	 * @return type the type of this annotation, e.g. ontology term.
	 */
	public AnnotationType getType() {
		return type;
	}

	/**
	 * Sets the type of this annotation.
	 * 
	 * @param v the type to set for this annotation, e.g. ontology term.
	 */
	public void setType(AnnotationType v) {
		if (type != v && v != null) {
			type = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATIONTYPE));
		}
	}

	/**
	 * Returns the url of this annotation.
	 * 
	 * @return url the url of this annotation.
	 */
	public UrlRef getUrl() {
		return url;
	}

	/**
	 * Sets the url of this annotation.
	 * 
	 * @param url the url to set for this annotation.
	 */
	public void setUrl(UrlRef v) {
		url = v;
	}

	/**
	 * Returns the Annotation Xref.
	 * 
	 * @return xref the annotation xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this annotation.
	 * 
	 * @param v the xref to set for this annotation.
	 */
	public void setXref(Xref v) {
		xref = v;
	}

	/**
	 * Returns the list of annotationRefs which reference the annotation.
	 * 
	 * @return annotationRefs the list of pathway elements which reference the
	 *         annotation.
	 */
	public List<AnnotationRef> getAnnotationRefs() {
		return annotationRefs;
	}

	/**
	 * Check whether annotationRefs has the given annotationRef.
	 * 
	 * @param annotationRef the annotationRef to look for.
	 * @return true if has annotationRef, false otherwise.
	 */
	public boolean hasAnnotationRef(AnnotationRef annotationRef) {
		return annotationRefs.contains(annotationRef);
	}

	/**
	 * Adds the given annotationRef to annotationRefs list of the annotation. NB:
	 * This method is not used directly.
	 * 
	 * @param annotationRef the given annotationRef to add.
	 */
	protected void addAnnotationRef(AnnotationRef annotationRef) {
		assert (annotationRef != null);
		// add citationRef to citationRefs
		if (annotationRef.getAnnotation() == this && !hasAnnotationRef(annotationRef))
			annotationRefs.add(annotationRef);
	}

	/**
	 * Removes the given annotationRef from annotationRefs list of this annotation.
	 * If annotationRefs becomes empty, this annotation is removed from the pathway
	 * model because it is no longer referenced/used. NB: This method is not used
	 * directly.
	 * 
	 * @param annotationRef the given annotationRef to remove.
	 */
	protected void removeAnnotationRef(AnnotationRef annotationRef) {
		assert (annotationRef != null);
		Annotation annotation = annotationRef.getAnnotation();
		// remove annotationRef from this annotation
		if (annotation == this || annotation == null && hasAnnotationRef(annotationRef)) {
			annotationRefs.remove(annotationRef);
			annotationRef.terminate();
		}
		// remove this annotation from pathway model if empty!
		if (annotationRefs.isEmpty()) {
			getPathwayModel().removeAnnotation(this);
		}
	}

	/**
	 * Removes all annotationRefs from annotationRefs list of the citation.
	 */
	public void removeAnnotationRefs() {
		for (int i = 0; i < annotationRefs.size(); i++) {
			removeAnnotationRef(annotationRefs.get(i));
		}
	}

	/**
	 * Terminates this annotation. The pathway model, if any, is unset from this
	 * annotation. Links to all annotationRefs are removed from this annotation.
	 */
	@Override
	public void terminate() {
		removeAnnotationRefs();
		unsetPathwayModel();
	}

	/**
	 * Checks all properties of given annotations to determine whether they are
	 * equal.
	 * 
	 * TODO
	 * 
	 * @param annotation the annotation to compare to.
	 * @return true if annotations have equal properties, false otherwise.
	 */
	public boolean equalsAnnotation(Annotation annotation) {
		// checks if value and type property equivalent
		if (!Objects.equals(value, annotation.getValue()))
			return false;
		if (!Objects.equals(type, annotation.getType()))
			return false;
		// checks if xref is equivalent
		if (xref != null && annotation.getXref() == null)
			return false;
		if (xref == null && annotation.getXref() != null)
			return false;
		if (xref != null && annotation.getXref() != null) {
			if (xref.getId().equals(annotation.getXref().getId()))
				return false;
			if (xref.getDataSource().equals(annotation.getXref().getDataSource()))
				return false;
		}
		// checks if url link and description are equivalent
		if (url != null && annotation.getUrl() == null)
			return false;
		if (url == null && annotation.getUrl() != null)
			return false;
		if (url != null && annotation.getUrl() != null) {
			if (!Objects.equals(url.getLink(), annotation.getUrl().getLink()))
				return false;
			if (!Objects.equals(url.getDescription(), annotation.getUrl().getDescription()))
				return false;
		}
		// checks if annotation has the same annotationRefs
		if (!Objects.equals(annotationRefs, annotation.getAnnotationRefs()))
			return false;
		return true;
	}

}
