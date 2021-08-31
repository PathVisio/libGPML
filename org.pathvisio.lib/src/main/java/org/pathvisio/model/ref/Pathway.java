/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2021 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.model.ref;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bridgedb.Xref;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.events.PathwayElementListener;
import org.pathvisio.props.StaticProperty;
import org.pathvisio.util.Utils;

/**
 * This class stores metadata for a Pathway.
 * 
 * Because of multiple optional parameters, a builder pattern is implemented for
 * Pathway. Example of how a Pathway object can be created:
 * 
 * Pathway pathway = new Pathway.PathwayBuilder("Title", 100, 100,
 * Color.decode("#ffffff")).setOrganism("Homo Sapiens")
 * .setSource("WikiPathways").setVersion("r1").setLicense("CC0").setXref(xref).build();
 * 
 * @author finterly
 */
public class Pathway implements Annotatable, Citable, Evidenceable {

	private String title;
	private double boardWidth;
	private double boardHeight;
	private Color backgroundColor;
	private List<Author> authors;
	private List<Comment> comments;
	private Map<String, String> dynamicProperties;
	private List<AnnotationRef> annotationRefs;
	private List<CitationRef> citationRefs;
	private List<EvidenceRef> evidenceRefs;
	private String description;
	private String organism;
	private String source;
	private String version;
	private String license;
	private Xref xref;

	/**
	 * This builder class builds an Pathway object step-by-step.
	 * 
	 * @author finterly
	 */
	public static class PathwayBuilder {

		private String title = "Click to add title"; // TODO
		private double boardWidth = 0;
		private double boardHeight = 0;
		private Color backgroundColor = Color.decode("#ffffff");
		private List<Author> authors;
		private List<Comment> comments;
		private Map<String, String> dynamicProperties;
		private List<AnnotationRef> annotationRefs;
		private List<CitationRef> citationRefs;
		private List<EvidenceRef> evidenceRefs;
		private String description; // optional
		private String organism; // optional
		private String source; // optional
		private String version; // optional
		private String license; // optional
		private Xref xref; // optional

		/**
		 * Public constructor with required attribute name as parameter. //TODO actually
		 * required?
		 * 
		 * @param title           the title of this pathway.
		 * @param boardWidth      together with...
		 * @param boardHeight     define the drawing size.
		 * @param backgroundColor the background color of the drawing, default #ffffff
		 *                        (white)
		 */
		public PathwayBuilder(String title, double boardWidth, double boardHeight, Color backgroundColor) {
			this.title = title;
			this.boardWidth = boardWidth;
			this.boardHeight = boardHeight;
			this.backgroundColor = backgroundColor;
			this.authors = new ArrayList<Author>();
			this.comments = new ArrayList<Comment>(); // 0 to unbounded
			this.dynamicProperties = new TreeMap<String, String>(); // 0 to unbounded
			this.annotationRefs = new ArrayList<AnnotationRef>(); // 0 to unbounded
			this.citationRefs = new ArrayList<CitationRef>(); // 0 to unbounded
			this.evidenceRefs = new ArrayList<EvidenceRef>(); // 0 to unbounded
		}

		/**
		 * Sets description and returns this builder object. Description is the textual
		 * description for this pathway.
		 * 
		 * @param v the description of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setDescription(String v) {
			description = v;
			return this;
		}

		/**
		 * Sets organism and returns this builder object. Organism is the scientific
		 * name (e.g., Homo sapiens) of the species being described by this pathway.
		 * 
		 * @param v the organism of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setOrganism(String v) {
			organism = v;
			return this;
		}

		/**
		 * Sets source and returns this builder object. The source of this pathway, e.g.
		 * WikiPathways, KEGG, Cytoscape.
		 * 
		 * @param v the source of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setSource(String v) {
			source = v;
			return this;
		}

		/**
		 * Sets version and returns this builder object.
		 * 
		 * @param v the version of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setVersion(String v) {
			version = v;
			return this;
		}

		/**
		 * Sets license and returns this builder object.
		 * 
		 * @param v the license of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setLicense(String v) {
			license = v;
			return this;
		}

		/**
		 * Sets xref and returns this builder object.
		 * 
		 * @param v the xref of this pathway.
		 * @return this pathwayBuilder object.
		 */
		public PathwayBuilder setXref(Xref v) {
			xref = v;
			return this;
		}

		/**
		 * Calls the private constructor in this pathway class and passes builder object
		 * itself as the parameter to this private constructor.
		 * 
		 * @return the created Pathway object.
		 */
		public Pathway build() {
			return new Pathway(this);
		}
	}

	/**
	 * Private constructor for Pathway which takes PathwayBuilder object as its
	 * argument.
	 * 
	 * @param builder this pathwayBuilder object.
	 */
	private Pathway(PathwayBuilder builder) {
		this.title = builder.title;
		this.boardWidth = builder.boardWidth;
		this.boardHeight = builder.boardHeight;
		this.backgroundColor = builder.backgroundColor;
		this.authors = builder.authors;
		this.comments = builder.comments;
		this.dynamicProperties = builder.dynamicProperties;
		this.annotationRefs = builder.annotationRefs;
		this.citationRefs = builder.citationRefs;
		this.evidenceRefs = builder.evidenceRefs;
		this.description = builder.description;
		this.organism = builder.organism;
		this.source = builder.source;
		this.version = builder.version;
		this.license = builder.license;
		this.xref = builder.xref;
	}

	/**
	 * Returns the title or name of this pathway.
	 * 
	 * @return title the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title or name of this pathway.
	 * 
	 * @param v the title to set. 
	 */
	public void setTitle(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (title != v) {
			title = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TITLE));
		}
	}

	/**
	 * Returns the board width. Board width together with board height define
	 * drawing size.
	 * 
	 * @return boardWidth the board width
	 */
	public double getBoardWidth() {
		return boardWidth;
	}

	/**
	 * Sets the board width.
	 * 
	 * @param v the board width to set. 
	 */
	public void setBoardWidth(double v) {
		if (v < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		} else {
			boardWidth = v;
		}
	}

	/**
	 * Returns the board height. Board width together with board height define
	 * drawing size.
	 * 
	 * @return boardHeight the board height
	 */
	public double getBoardHeight() {
		return boardHeight;
	}

	/**
	 * Sets the board height.
	 * 
	 * @param v the board height to set. 
	 */
	public void setBoardHeight(double v) {
		if (v < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		} else {
			boardHeight = v;
		}
	}

	/**
	 * Returns the background color of this pathway.
	 * 
	 * @return backgroundColor the background color.
	 */
	public Color getBackgroundColor() {
		if (backgroundColor == null) {
			this.backgroundColor = Color.decode("#ffffff");
		}
		return backgroundColor;
	}

	/**
	 * Sets the background color of this pathway.
	 * 
	 * @param v the background color to set.
	 */
	public void setBackgroundColor(Color v) {
		backgroundColor = v;
	}

	/**
	 * Returns the list of authors for this pathway model.
	 * 
	 * @return authors the list of authors.
	 */
	public List<Author> getAuthors() {
		return authors;
	}

	/**
	 * Adds the given author to authors list.
	 * 
	 * @param author the author to add.
	 */
	public void addAuthor(Author author) {
		authors.add(author);
	}

	/**
	 * Removes the given author from authors list.
	 * 
	 * @param author the author to remove.
	 */
	public void removeAuthor(Author author) {
		authors.remove(author);
	}

	/**
	 * Returns the list of comments.
	 * 
	 * @return comments the list of comments.
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/**
	 * Adds given comment to comments list.
	 * 
	 * @param comment the comment to be added.
	 */
	public void addComment(Comment comment) {
		comments.add(comment);
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.COMMENT));
	}

	/**
	 * Removes given comment from comments list.
	 * 
	 * @param comment the comment to be removed.
	 */
	public void removeComment(Comment comment) {
		comments.remove(comment);
	}

	/**
	 * TODO Finds the first comment with a specific source.
	 * 
	 * @param source the source of the comment to be found.
	 * @return the comment content with a given source.
	 */
	public String findComment(String source) {
		for (Comment comment : comments) {
			if (source.equals(comment.getSource())) {
				return comment.getCommentText();
			}
		}
		return null;
	}

	/**
	 * Returns the map of dynamic properties.
	 * 
	 * @return dynamicProperties the dynamic properties map
	 */
	public Map<String, String> getDynamicProperties() {
		return dynamicProperties;
	}

	/**
	 * Returns a set of all dynamic property keys.
	 * 
	 * @return a set of all dynamic property keys.
	 */
	public Set<String> getDynamicPropertyKeys() {
		return dynamicProperties.keySet();
	}

	/**
	 * Returns a dynamic property string value.
	 * 
	 * @param key the key of a key value pair.
	 * @return the value or dynamic property.
	 */
	public String getDynamicProperty(String key) {
		return dynamicProperties.get(key);
	}

	/**
	 * Sets a dynamic property. Setting to null means removing this dynamic property
	 * altogether.
	 * 
	 * @param key   the key of a key value pair.
	 * @param value the value of a key value pair.
	 */
	public void setDynamicProperty(String key, String value) {
		if (value == null)
			dynamicProperties.remove(key);
		else
			dynamicProperties.put(key, value);
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, key));
	}

	/**
	 * Returns the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotation references, an empty list if no
	 *         properties are defined.
	 */
	@Override
	public List<AnnotationRef> getAnnotationRefs() {
		return annotationRefs;
	}

	/**
	 * Checks whether annotationRefs has the given annotationRef. *
	 * 
	 * @param annotationRef the annotationRef to look for.
	 * @return true if has annotationRef, false otherwise.
	 */
	@Override
	public boolean hasAnnotationRef(AnnotationRef annotationRef) {
		return annotationRefs.contains(annotationRef);
	}

	/**
	 * Adds given annotationRef to annotationRefs list. Sets annotable for the given
	 * annotationRef.
	 * 
	 * @param annotationRef the annotationRef to be added.
	 */
	@Override
	public void addAnnotationRef(AnnotationRef annotationRef) {
		if (annotationRef != null && !hasAnnotationRef(annotationRef)) {
			annotationRef.setAnnotatableTo(this);
			assert (annotationRef.getAnnotatable() == this);
			annotationRefs.add(annotationRef);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATIONREF));
		}
	}

	/**
	 * Removes given annotationRef from annotationRefs list. The annotationRef
	 * ceases to exist and is terminated.
	 * 
	 * @param annotationRef the annotationRef to be removed.
	 */
	@Override
	public void removeAnnotationRef(AnnotationRef annotationRef) {
		if (annotationRef != null && hasAnnotationRef(annotationRef)) {
			annotationRefs.remove(annotationRef);
			annotationRef.terminate();
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ANNOTATIONREF));
		}
	}

	/**
	 * Removes all annotationRefs from annotationRefs list.
	 */
	@Override
	public void removeAnnotationRefs() {
		for (int i = 0; i < annotationRefs.size(); i++) {
			removeAnnotationRef(annotationRefs.get(i));
		}
	}

	/**
	 * Returns the list of citation references.
	 * 
	 * @return citationRefs the list of citations referenced, an empty list if no
	 *         properties are defined.
	 */
	@Override
	public List<CitationRef> getCitationRefs() {
		return citationRefs;
	}

	/**
	 * Checks whether citationRefs has the given citationRef.
	 * 
	 * @param citationRef the citationRef to look for.
	 * @return true if has citationRef, false otherwise.
	 */
	@Override
	public boolean hasCitationRef(CitationRef citationRef) {
		return citationRefs.contains(citationRef);
	}

	/**
	 * Adds given citationRef to citationRefs list. Sets citable for the given
	 * citationRef.
	 * 
	 * @param citationRef the citationRef to be added.
	 */
	@Override
	public void addCitationRef(CitationRef citationRef) {
		if (citationRef != null && !hasCitationRef(citationRef)) {
			citationRef.setCitableTo(this);
			assert (citationRef.getCitable() == this);
			citationRefs.add(citationRef);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.CITATIONREF));

		}
	}

	/**
	 * Removes given citationRef from citationRefs list. The citationRef ceases to
	 * exist and is terminated.
	 * 
	 * @param citationRef the citationRef to be removed.
	 */
	@Override
	public void removeCitationRef(CitationRef citationRef) {
		if (citationRef != null && hasCitationRef(citationRef)) {
			citationRefs.remove(citationRef);
			citationRef.terminate();
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.CITATIONREF));
		}
	}

	/**
	 * Removes all citationRef from citationRefs list.
	 */
	@Override
	public void removeCitationRefs() {
		for (int i = 0; i < citationRefs.size(); i++) {
			removeCitationRef(citationRefs.get(i));
		}
	}

	/**
	 * Returns the list of evidence references.
	 * 
	 * @return evidenceRefs the list of evidences referenced, an empty list if no
	 *         properties are defined.
	 */
	@Override
	public List<EvidenceRef> getEvidenceRefs() {
		return evidenceRefs;
	}

	/**
	 * Checks whether evidenceRefs has the given evidenceRef.
	 * 
	 * @param evidenceRef the evidenceRef to look for.
	 * @return true if has evidenceRef, false otherwise.
	 */
	@Override
	public boolean hasEvidenceRef(EvidenceRef evidenceRef) {
		return evidenceRefs.contains(evidenceRef);
	}

	/**
	 * Adds given evidenceRef to evidenceRefs list. Sets evidenceable for the given
	 * evidenceRef.
	 * 
	 * @param evidenceRef the evidenceRef to be added.
	 */
	@Override
	public void addEvidenceRef(EvidenceRef evidenceRef) {
		if (evidenceRef != null && !hasEvidenceRef(evidenceRef)) {
			evidenceRef.setEvidenceableTo(this);
			assert (evidenceRef.getEvidenceable() == this);
			evidenceRefs.add(evidenceRef);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.EVIDENCEREF));
		}
	}

	/**
	 * Removes given evidenceRef from evidenceRefs list. The evidenceRef ceases to
	 * exist and is terminated.
	 * 
	 * @param evidenceRef the evidenceRef to be removed.
	 */
	@Override
	public void removeEvidenceRef(EvidenceRef evidenceRef) {
		if (evidenceRef != null && hasEvidenceRef(evidenceRef)) {
			evidenceRefs.remove(evidenceRef);
			evidenceRef.terminate();
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.EVIDENCEREF));
		}
	}

	/**
	 * Removes all evidenceRefs from evidenceRefs list.
	 */
	@Override
	public void removeEvidenceRefs() {
		for (EvidenceRef evidenceRef : evidenceRefs) {
			removeEvidenceRef(evidenceRef);
		}
	}

	/**
	 * Returns the description of this pathway.
	 * 
	 * @return description the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this pathway.
	 * 
	 * @param v the description to set.
	 */
	public void setDescription(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		} else
			description = v;
	}

	/**
	 * Returns the organism of this pathway. Organism is the scientific name (e.g.,
	 * Homo sapiens) of the species being described by this pathway.
	 * 
	 * @return organism the organism.
	 */
	public String getOrganism() {
		return organism;
	}

	/**
	 * Sets the organism of this pathway. Organism is the scientific name (e.g., Homo
	 * sapiens) of the species being described by this pathway.
	 * 
	 * @param v the organism to set. 
	 */
	public void setOrganism(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (!Utils.stringEquals(organism, v)) {
			organism = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ORGANISM));
		}
	}

	/**
	 * Returns the source of this pathway, e.g. WikiPathways, KEGG, Cytoscape.
	 * 
	 * @return source the source.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source of this pathway, e.g. WikiPathways, KEGG, Cytoscape.
	 * 
	 * @param v the source to set.
	 */
	public void setSource(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		} else
			source = v;
	}

	/**
	 * Returns the version of this pathway.
	 * 
	 * @return version the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version of this pathway.
	 * 
	 * @param v the version to set.
	 */
	public void setVersion(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (version != v) {
			version = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.VERSION));
		}
	}

	/**
	 * Returns the license of this pathway.
	 * 
	 * @return license the license.
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * Sets the license of this pathway.
	 * 
	 * @param v the license to set.
	 */
	public void setLicense(String v) {
		license = v;
	}

	/**
	 * Returns the Xref for this pathway.
	 * 
	 * @return xref the xref of this pathway.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this pathway.
	 * 
	 * @param v the xref to set for this pathway.
	 */
	public void setXref(Xref v) {
		xref = v;
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.IDENTIFIER));
	}

	// TODO....

	/**
	 * Fire and Listener methods below TODO
	 */
	int noFire = 0;

	public void dontFireEvents(int times) {
		noFire = times;
	}

	private Set<PathwayElementListener> listeners = new HashSet<PathwayElementListener>();

	public void addListener(PathwayElementListener v) {
		if (!listeners.contains(v))
			listeners.add(v);
	}

	public void removeListener(PathwayElementListener v) {
		listeners.remove(v);
	}

	public void fireObjectModifiedEvent(PathwayElementEvent e) {
		if (noFire > 0) {
			noFire -= 1;
			return;
		}
		if (pathwayModel != null)
			pathwayModel.childModified(e);
		for (PathwayElementListener g : listeners) {
			g.gmmlObjectModified(e);
		}
	}

}
