package org.pathvisio.libgpml.model;

import java.util.List;

import org.pathvisio.libgpml.model.PathwayElement.AnnotationRef;
import org.pathvisio.libgpml.model.PathwayElement.CitationRef;
import org.pathvisio.libgpml.model.PathwayElement.EvidenceRef;

/**
 * This class stores information for copying {@link PathwayElement}.
 * 
 * {@link PathwayElement#copy} returns a {@link CopyElement} which stores the
 * newly created newElement and a reference to the original sourceElement.
 * 
 * @author finterly
 */
public class CopyElement {

	PathwayElement newElement;

	PathwayElement sourceElement;

	/**
	 * @param newElement
	 * @param sourceElement
	 */
	public CopyElement(PathwayElement newElement, PathwayElement sourceElement) {
		super();
		this.newElement = newElement;
		this.sourceElement = sourceElement;
	}

	/**
	 * @return
	 */
	public PathwayElement getNewElement() {
		return newElement;
	}

	/**
	 * @param newElement
	 */
	public void setNewElement(PathwayElement newElement) {
		this.newElement = newElement;
	}

	/**
	 * @return
	 */
	public PathwayElement getSourceElement() {
		return sourceElement;
	}

	/**
	 * @param sourceElement
	 */
	public void setSourceElement(PathwayElement sourceElement) {
		this.sourceElement = sourceElement;
	}

	/**
	 * Loads references. NB: To be called after new pathway element is added to a
	 * pathway model.
	 */
	public void loadReferences() {
		if (newElement != null && sourceElement != null) {
			loadAnnotationRefs(sourceElement.getAnnotationRefs());
			loadCitationRefs(sourceElement.getCitationRefs());
			loadEvidenceRefs(sourceElement.getEvidenceRefs());
		}
	}

	/**
	 * @param citationRefs
	 */
	private void loadCitationRefs(List<CitationRef> citationRefs) {
		for (CitationRef citationRef : citationRefs) {
			newElement.addCitation(citationRef.getCitation().copyRef());
			loadAnnotationRefs(citationRef.getAnnotationRefs());
		}
	}

	/**
	 * 
	 */
	/**
	 * @param annotationRefs
	 */
	private void loadAnnotationRefs(List<AnnotationRef> annotationRefs) {
		for (AnnotationRef annotationRef : annotationRefs) {
			newElement.addAnnotation(annotationRef.getAnnotation().copyRef());
			loadCitationRefs(annotationRef.getCitationRefs());
			loadEvidenceRefs(annotationRef.getEvidenceRefs());

		}
	}

	/**
	 * @param evidenceRefs
	 */
	private void loadEvidenceRefs(List<EvidenceRef> evidenceRefs) {
		for (EvidenceRef evidenceRef : evidenceRefs) {
			newElement.addEvidence(evidenceRef.getEvidence().copyRef());
		}
	}

	/**
	 * 
	 */
	public void getGroup() {
		// TODO
	}

}
