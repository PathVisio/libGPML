package org.pathvisio.model.ref;

import java.util.List;

/**
 * Interface for classes which can hold a {@link List} of {@link AnnotationRef}.
 * These classes include {@link Pathway}, {@link PathwayElement}, and
 * {@link CitationRef}.
 * 
 * @author finterly
 */
public interface Annotatable {

	/**
	 * Returns the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotation references, an empty list if no
	 *         properties are defined.
	 */
	public List<AnnotationRef> getAnnotationRefs();

	/**
	 * Check whether this annotatable has the given annotationRef.
	 * 
	 * @param annotationRef the annotationRef to look for.
	 * @return true if has annotationRef, false otherwise.
	 */
	public boolean hasAnnotationRef(AnnotationRef annotationRef);

	/**
	 * Adds given annotationRef to annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be added.
	 */
	public void addAnnotationRef(AnnotationRef annotationRef);

	/**
	 * Creates and adds an annotationRef to annotationRefs list. Calls
	 * {@link #addAnnotationRef(AnnotationRef annotationRef)}.
	 * 
	 * @param annotation the annotation for annotationRef.
	 */
	public AnnotationRef addAnnotationRef(Annotation annotation);

	/**
	 * Removes given annotationRef from annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be removed.
	 */
	public void removeAnnotationRef(AnnotationRef annotationRef);

	/**
	 * Removes all annotationRefs from annotationRefs list.
	 */
	public void removeAnnotationRefs();
}
