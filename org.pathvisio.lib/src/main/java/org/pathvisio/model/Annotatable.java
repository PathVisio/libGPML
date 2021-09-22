package org.pathvisio.model;

import java.util.List;

import org.bridgedb.Xref;
import org.pathvisio.model.PathwayElement.AnnotationRef;
import org.pathvisio.model.PathwayElement.CitationRef;
import org.pathvisio.model.type.AnnotationType;

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
	 * Creates and adds an annotationRef to annotationRefs list.
	 * 
	 * @param annotation the annotation for annotationRef.
	 */
	public AnnotationRef addAnnotationRef(Annotation annotation);

	/**
	 * Creates a annotation with given properties, and adds annotation to pathway
	 * model. Creates a annotationRef for annotation, and adds to annotationRefs
	 * list for this annotatable. Calls
	 * {@link #addAnnotationRef(Annotation annotation)}.
	 * 
	 * @param value   the name, term, or text of the annotation.
	 * @param type    the type of the annotation, e.g. ontology.
	 * @param xref    the annotation xref.
	 * @param urlLink the url link of the annotation.
	 */
	public AnnotationRef addAnnotation(String value, AnnotationType type, Xref xref, String urlLink);

	/**
	 * Creates a annotation with given properties, and adds annotation to pathway
	 * model. Creates a annotationRef for annotation, and adds to annotationRefs
	 * list for this annotatable. Sets elementId for annotation. This method is used
	 * when reading gpml. Calls {@link #addAnnotationRef(Annotation annotation)}.
	 * 
	 * @param elementId the elementId to set.
	 * @param value   the name, term, or text of the annotation.
	 * @param type    the type of the annotation, e.g. ontology.
	 * @param xref    the annotation xref.
	 * @param urlLink the url link of the annotation.
	 */
	public AnnotationRef addAnnotation(String elementId, String value, AnnotationType type, Xref xref, String urlLink);

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
