package org.pathvisio.model.ref;

import java.util.List;

/**
 * Interface for classes which can hold a {@link List} of {@link EvidenceRef}.
 * These classes include {@link Pathway}, {@link ElementInfo}, and
 * {@link AnnotationRef}.
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
	 * Adds given evidenceRef to evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be added.
	 */
	public void addEvidenceRef(EvidenceRef evidenceRef);

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
