package org.pathvisio.model.ref;

import java.util.List;

import org.pathvisio.model.ref.PathwayElement.AnnotationRef;
import org.pathvisio.model.ref.PathwayElement.CitationRef;

/**
 * Interface for classes which can hold a {@link List} of {@link CitationRef}.
 * These classes include {@link Pathway}, {@link PathwayElement}, and
 * {@link AnnotationRef}.
 * 
 * @author finterly
 */
public interface Citable {

	/**
	 * Returns the list of citation references.
	 * 
	 * @return citationRefs the list of citations referenced, an empty list if no
	 *         properties are defined.
	 */
	public List<CitationRef> getCitationRefs();

	/**
	 * Check whether this citable has the given citationRef.
	 * 
	 * @param citationRef the citationRef to look for.
	 * @return true if has citationRef, false otherwise.
	 */
	public boolean hasCitationRef(CitationRef citationRef);

	/**
	 * Creates and adds an citationRefs to citationRefs list.
	 * 
	 * @param citation the citation for citationRef.
	 */
	public CitationRef addCitationRef(Citation citation);

	/**
	 * Removes given citationRef from citationRefs list.
	 * 
	 * @param citationRef the citationRef to be removed.
	 */
	public void removeCitationRef(CitationRef citationRef);

	/**
	 * Removes all citationRefs from citationRefs list.
	 */
	public void removeCitationRefs();

	// TODO addCitation();
	// add citation. Take the citation, and creates citationRef, and links
	// everything

}
