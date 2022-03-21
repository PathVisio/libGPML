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
import java.util.List;
import java.util.Objects;

import org.bridgedb.Xref;
import org.pathvisio.libgpml.model.PathwayObjectEvent;
import org.pathvisio.libgpml.model.PathwayElement.AnnotationRef;
import org.pathvisio.libgpml.model.type.AnnotationType;
import org.pathvisio.libgpml.model.type.ObjectType;
import org.pathvisio.libgpml.prop.StaticProperty;
import org.pathvisio.libgpml.util.Utils;
import org.pathvisio.libgpml.util.XrefUtils;

/**
 * This class stores information for an Annotation.
 * 
 * @author finterly
 */
public class Annotation extends PathwayObject {

	private String value;
	private AnnotationType type = AnnotationType.UNDEFINED;
	private Xref xref; // optional
	private String urlLink; // optional
	private List<AnnotationRef> annotationRefs; // annotationRefs with this annotation as source

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates an Annotation pathway element given all possible parameters:
	 * elementId, parent pathway model, value, type, url, and xref.
	 * 
	 * @param value   the name, term, or text of the annotation.
	 * @param type    the type of the annotation, e.g. ontology.
	 * @param xref    the annotation xref.
	 * @param urlLink the url link of the annotation.
	 */
	protected Annotation(String value, AnnotationType type, Xref xref, String urlLink) {
		super();
		setValue(value); // must be valid
		setType(type);// must be valid
		this.xref = xref;
		this.urlLink = urlLink;
		this.annotationRefs = new ArrayList<AnnotationRef>();
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
		return ObjectType.ANNOTATION;
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
		if (v == null) {
			throw new IllegalArgumentException("Value is a required field for Annotation.");
		}
		if (!Utils.stringEquals(value, v)) {
			value = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATION));
		}
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATIONTYPE));
		}
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
		if (v != null) {
			xref = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
		}
	}

	/**
	 * Returns the url link for a web address.
	 * 
	 * @return urlLink the url link.
	 */
	public String getUrlLink() {
		return urlLink;
	}

	/**
	 * Sets the url link for a web address.
	 * 
	 * @param v the url link.
	 */
	public void setUrlLink(String v) {
		if (v != null && !Utils.stringEquals(urlLink, v)) {
			urlLink = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATION));
		}
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
	 * Adds the given annotationRef to annotationRefs list of the annotation.
	 * 
	 * NB: This method is not used directly.
	 * 
	 * @param annotationRef the given annotationRef to add.
	 */
	protected void addAnnotationRef(AnnotationRef annotationRef) {
		if (annotationRef == null) {
			throw new IllegalArgumentException("Cannot add invalid annotationRef to annotation.");
		}
		// add citationRef to citationRefs
		if (annotationRef.getAnnotation() == this && !hasAnnotationRef(annotationRef)) {
			annotationRefs.add(annotationRef);
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATION));
		}
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
		if (annotationRef != null) {
			annotationRefs.remove(annotationRef);
			annotationRef.terminate();
			// if citationResf empty, remove this annotation from pathway model
			if (annotationRefs.isEmpty()) {
				pathwayModel.removeAnnotation(this);
				fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATION));
			}
		}
	}

	/**
	 * Removes all annotationRefs from annotationRefs list.
	 */
	private void removeAnnotationRefs() {
		for (int i = annotationRefs.size() - 1; i >= 0; i--) {
			AnnotationRef ref = annotationRefs.get(i);
			annotationRefs.remove(ref);
			ref.terminate();
		}
	}

	/**
	 * Terminates this annotation. The pathway model, if any, is unset from this
	 * annotation. Links to all annotationRefs are removed from this annotation.
	 */
	@Override
	protected void terminate() {
		removeAnnotationRefs();
		super.terminate();
	}

	// ================================================================================
	// Equals Methods
	// ================================================================================
	/**
	 * Compares this annotation to the given annotation. Checks all properties
	 * except annotationsRefs list to determine whether they are equal.
	 * 
	 * @param annotation the annotation to compare to.
	 * @return true if annotations have equal properties, false otherwise.
	 */
	public boolean equalsAnnotation(Annotation annotation) {
		// checks if value and type property equivalent
		if (!Objects.equals(value, annotation.getValue())) {
			return false;
		}
		if (!Objects.equals(type, annotation.getType())) {
			return false;
		}
		// checks if xref is equivalent
		if (!XrefUtils.equivalentXrefs(xref, annotation.getXref())) {
			return false;
		}
		// checks if url link property equivalent
		if (!Objects.equals(urlLink, annotation.getUrlLink())) {
			return false;
		}
		return true;
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Note: doesn't change parent, only fields
	 *
	 * Used by UndoAction.
	 *
	 * NB: annotationRefs list is not copied, annotationRefs are created and added
	 * when an annotation is added to a pathway element.
	 * 
	 * @param src
	 */
	public void copyValuesFrom(Annotation src) { // TODO
		value = src.value;
		type = src.type;
		xref = src.xref;
		urlLink = src.urlLink;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public Annotation copyRef() {
		Annotation result = new Annotation(value, type, xref, urlLink);
		result.copyValuesFrom(this);
		return result;
	}

}
