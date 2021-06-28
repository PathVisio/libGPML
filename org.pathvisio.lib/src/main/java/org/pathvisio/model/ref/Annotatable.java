package org.pathvisio.model.ref;

import java.util.List;

/**
 * Interface for classes which can hold a {@link List} of {@link AnnotationRef}.
 * These classes include {@link Pathway}, {@link ElementInfo}, and
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
	 * Adds given annotationRef to annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be added.
	 */
	public void addAnnotationRef(AnnotationRef annotationRef);

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
