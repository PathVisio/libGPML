package org.pathvisio.model;

import java.util.List;

import org.bridgedb.Xref;
import org.pathvisio.model.PathwayElement.AnnotationRef;
import org.pathvisio.model.PathwayElement.CitationRef;

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
	 * Creates and adds a citationRefs to citationRefs list.
	 * 
	 * @param citation the citation for citationRef.
	 */
	public CitationRef addCitationRef(Citation citation);

	/**
	 * Creates a citation with given xref and urlLink, and adds citation to pathway
	 * model. Creates a citationRef for citation, and adds to citationRefs list for
	 * this citable. Calls {@link #addCitation(Citation citation)}.
	 * 
	 * @param xref    the citation xref.
	 * @param urlLink the url link and description (optional) for a web address.
	 */
	public CitationRef addCitation(Xref xref, String urlLink);

	// TODO
	/**
	 * Creates a citation with given xref and urlLink, and adds citation to pathway
	 * model. Creates a citationRef for citation, and adds to citationRefs list for
	 * this citable. Sets elementId for citation. This method is used when reading
	 * gpml. Calls {@link #addCitation(Citation citation)}.
	 * 
	 * @param elementId the elementId to set.
	 * @param xref      the citation xref.
	 * @param urlLink   the url link and description (optional) for a web address.
	 */
	public CitationRef addCitation(String elementId, Xref xref, String urlLink);

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
