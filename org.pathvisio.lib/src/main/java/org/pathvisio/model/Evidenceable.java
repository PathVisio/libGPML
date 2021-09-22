package org.pathvisio.model;

import java.util.List;

import org.bridgedb.Xref;
import org.pathvisio.model.PathwayElement.AnnotationRef;
import org.pathvisio.model.PathwayElement.EvidenceRef;

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
	 * @param evidence the evidenceRef for evidenceRef.
	 */
	public EvidenceRef addEvidenceRef(Evidence evidence);

	/**
	 * Creates an evidence with given properties, and adds evidence to pathway
	 * model. Creates a evidenceRef for evidence, and adds to evidenceRefs list for
	 * this evidenceable. Calls {@link #addEvidence(Evidence evidence)}.
	 * 
	 * @param value   the name, term, or text of the evidence.
	 * @param xref    the evidence xref.
	 * @param urlLink the url link and description (optional) for a web address.
	 */
	public EvidenceRef addEvidence(String value, Xref xref, String urlLink);

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
	 */
	public EvidenceRef addEvidence(String elementId, String value, Xref xref, String urlLink);

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
