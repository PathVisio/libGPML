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

import java.util.List;

import org.bridgedb.Xref;
import org.pathvisio.libgpml.model.PathwayElement.AnnotationRef;
import org.pathvisio.libgpml.model.PathwayElement.CitationRef;
import org.pathvisio.libgpml.model.PathwayElement.EvidenceRef;
import org.pathvisio.libgpml.model.type.AnnotationType;

/**
 * Interface for {@link Annotatable}, {@link Citable}, and {@link Evidenceable}.
 *
 * @author finterly
 */
public interface Referenceable {

	/**
	 * Interface for classes which can hold a {@link List} of {@link AnnotationRef}.
	 * These classes include {@link PathwayElement}, and {@link CitationRef}.
	 *
	 * @author finterly
	 */
	public interface Annotatable extends Referenceable {

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
		 * @param annotation the annotation added.
		 * @return the annotationRef for the annotation added.
		 */
		public AnnotationRef addAnnotation(Annotation annotation);

		/**
		 * Creates a annotation with given properties, and adds annotation to pathway
		 * model. Creates a annotationRef for annotation, and adds to annotationRefs
		 * list for this annotatable. Calls
		 * {@link #addAnnotation(Annotation annotation)}.
		 *
		 * @param value   the name, term, or text of the annotation.
		 * @param type    the type of the annotation, e.g. ontology.
		 * @param xref    the annotation xref.
		 * @param urlLink the url link of the annotation.
		 * @return the annotationRef for the annotation added.
		 */
		public AnnotationRef addAnnotation(String value, AnnotationType type, Xref xref, String urlLink);

		/**
		 * Creates a annotation with given properties, and adds annotation to pathway
		 * model. Creates a annotationRef for annotation, and adds to annotationRefs
		 * list for this annotatable. Sets elementId for annotation. This method is used
		 * when reading gpml. Calls {@link #addAnnotation(Annotation annotation)}.
		 *
		 * @param elementId the elementId to set.
		 * @param value     the name, term, or text of the annotation.
		 * @param type      the type of the annotation, e.g. ontology.
		 * @param xref      the annotation xref.
		 * @param urlLink   the url link of the annotation.
		 * @return the annotationRef for the annotation added.
		 */
		public AnnotationRef addAnnotation(String elementId, String value, AnnotationType type, Xref xref,
				String urlLink);

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

	/**
	 * Interface for classes which can hold a {@link List} of {@link CitationRef}.
	 * These classes include {@link Pathway}, {@link PathwayElement}, and
	 * {@link AnnotationRef}.
	 *
	 * @author finterly
	 */
	public interface Citable extends Referenceable {

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
		 * @return the citationRef for the citation added.
		 */
		public CitationRef addCitation(Citation citation);

		/**
		 * Creates a citation with given xref and urlLink, and adds citation to pathway
		 * model. Creates a citationRef for citation, and adds to citationRefs list for
		 * this citable. Calls {@link #addCitation(Citation citation)}.
		 *
		 * @param xref    the citation xref.
		 * @param urlLink the url link and description (optional) for a web address.
		 * @return the citationRef for the citation added.
		 */
		public CitationRef addCitation(Xref xref, String urlLink);

		/**
		 * Creates a citation with given xref and urlLink, and adds citation to pathway
		 * model. Creates a citationRef for citation, and adds to citationRefs list for
		 * this citable. Sets elementId for citation. This method is used when reading
		 * gpml. Calls {@link #addCitation(Citation citation)}.
		 *
		 * @param elementId the elementId to set.
		 * @param xref      the citation xref.
		 * @param urlLink   the url link and description (optional) for a web address.
		 * @return the citationRef for the citation added.
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

	}

	/**
	 * Interface for classes which can hold a {@link List} of {@link EvidenceRef}.
	 * These classes include {@link PathwayElement} and {@link AnnotationRef}.
	 *
	 * @author finterly
	 */
	public interface Evidenceable extends Referenceable {

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
		 * @return the evidencRef for the evidence added.
		 */
		public EvidenceRef addEvidence(Evidence evidence);

		/**
		 * Creates an evidence with given properties, and adds evidence to pathway
		 * model. Creates a evidenceRef for evidence, and adds to evidenceRefs list for
		 * this evidenceable. Calls {@link #addEvidence(Evidence evidence)}.
		 *
		 * @param value   the name, term, or text of the evidence.
		 * @param xref    the evidence xref.
		 * @param urlLink the url link and description (optional) for a web address.
		 * @return the evidencRef for the evidence added.
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
		 * @return the evidencRef for the evidence added.
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

}
