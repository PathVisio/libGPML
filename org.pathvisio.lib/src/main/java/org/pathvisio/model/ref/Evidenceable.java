package org.pathvisio.model.ref;

import java.util.List;

/**
 * Interface for classes which can hold a {@link List} of {@link EvidenceRef}.
 * These classes include {@link PathwayElement} and {@link AnnotationRef}.
 * 
 * @author finterly
 */
public interface Evidenceable {

	/**
	 * Returns the list of evidence references.
	 * 
	 * @return evidenceRefs the list of annotation references, an empty list if no
	 *         properties are defined.
	 */
	public List<EvidenceRef> getEvidenceRefs();

	/**
	 * Check whether this annotatable has the given evidenceRef.
	 * 
	 * @param evidenceRef the evidenceRef to look for.
	 * @return true if has evidenceRef, false otherwise.
	 */
	public boolean hasEvidenceRef(EvidenceRef evidenceRef);

	/**
	 * Creates and adds an evidenceRef to evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef for evidenceRef.
	 */
	public EvidenceRef addEvidenceRef(Evidence evidence);

	/**
	 * Removes given evidenceRef from evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be removed.
	 */
	public void removeEvidenceRef(EvidenceRef evidenceRef);

	/**
	 * Removes all evidenceRefs from evidenceRefs list.
	 */
	public void removeEvidenceRefs();
}
