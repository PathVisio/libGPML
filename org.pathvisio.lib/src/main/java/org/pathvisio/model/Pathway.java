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
package org.pathvisio.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.graphics.Coordinate;

/**
 * This class stores metadata for a Pathway.
 * 
 * Because of multiple optional parameters, a builder pattern is implemented for
 * Pathway. Example of how a Pathway object can be created:
 * 
 * Pathway pathway = new Pathway.PathwayBuilder("Title", 100, 100,
 * Color.decode("#ffffff"), infobox).setOrganism("Homo Sapiens")
 * .setSource("WikiPathways").setVersion("r1").setLicense("CC0").setXref(xref).build();
 * 
 * @author finterly
 */
public class Pathway {

	private String title;
	private double boardWidth;
	private double boardHeight;
	private Color backgroundColor; 
	private Coordinate infoBox; // the centerXY of gpml:InfoBox
	private String organism;
	private String source;
	private String version;
	private String license;
	private Xref xref;
	private List<Comment> comments;
	private Map<String, String> dynamicProperties;
	private List<AnnotationRef> annotationRefs;
	private List<Citation> citationRefs;
	private List<Evidence> evidenceRefs;

	/**
	 * This builder class builds an Pathway object step-by-step.
	 * 
	 * @author finterly
	 */
	public static class PathwayBuilder {

		private String title; //TODO = "New Pathway";? 
		private double boardWidth;
		private double boardHeight;
		private Color backgroundColor = Color.decode("#ffffff"); // default #ffffff (white) TODO or optional
		private Coordinate infoBox;
		private List<Comment> comments;
		private Map<String, String> dynamicProperties;
		private List<AnnotationRef> annotationRefs;
		private List<Citation> citationRefs;
		private List<Evidence> evidenceRefs;
		private String organism; // optional
		private String source; // optional
		private String version; // optional
		private String license; // optional
		private Xref xref; // optional

		/**
		 * Public constructor with required attribute name as parameter.
		 * 
		 * @param title           the title of the pathway.
		 * @param boardWidth      together with...
		 * @param boardHeight     define the drawing size.
		 * @param backgroundColor the background color of the drawing, default #ffffff
		 *                        (white)
		 * @param infoBox         the info box xy coordinates for where information,
		 *                        e.g. name and organism, are displayed in the pathway.
		 */
		public PathwayBuilder(String title, double boardWidth, double boardHeight, Color backgroundColor,
				Coordinate infoBox) {
			this.title = title;
			this.boardWidth = boardWidth;
			this.boardHeight = boardHeight;
			this.backgroundColor = backgroundColor;
			this.infoBox = infoBox;
			this.comments = new ArrayList<Comment>(); // 0 to unbounded
			this.dynamicProperties = new TreeMap<String, String>(); // 0 to unbounded
			this.annotationRefs = new ArrayList<AnnotationRef>(); // 0 to unbounded
			this.citationRefs = new ArrayList<Citation>(); // 0 to unbounded
			this.evidenceRefs = new ArrayList<Evidence>(); // 0 to unbounded
		}

		/**
		 * Sets organism and returns this builder object. Organism is the scientific
		 * name (e.g., Homo sapiens) of the species being described by the pathway.
		 * 
		 * @param organism the organism of the pathway.
		 * @return the PathwayBuilder object.
		 */
		public PathwayBuilder setOrganism(String organism) {
			this.organism = organism;
			return this;
		}

		/**
		 * Sets source and returns this builder object. The source of the pathway, e.g.
		 * WikiPathways, KEGG, Cytoscape.
		 * 
		 * @param source the source of the pathway.
		 * @return the PathwayBuilder object.
		 */
		public PathwayBuilder setSource(String source) {
			this.source = source;
			return this;
		}

		/**
		 * Sets version and returns this builder object.
		 * 
		 * @param source the source of the pathway.
		 * @return the PathwayBuilder object.
		 */
		public PathwayBuilder setVersion(String version) {
			this.version = version;
			return this;
		}

		/**
		 * Sets license and returns this builder object.
		 * 
		 * @param source the source of the pathway.
		 * @return the PathwayBuilder object.
		 */
		public PathwayBuilder setLicense(String license) {
			this.license = license;
			return this;
		}

		/**
		 * Sets xref and returns this builder object.
		 * 
		 * @param xref the xref of the pathway.
		 * @return the PathwayBuilder object.
		 */
		public PathwayBuilder setXref(Xref xref) {
			this.xref = xref;
			return this;
		}

		/**
		 * Calls the private constructor in the Pathway class and passes builder object
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
	 * @param builder the PathwayBuilder object.
	 */
	private Pathway(PathwayBuilder builder) {
		this.title = builder.title;
		this.comments = builder.comments;
		this.dynamicProperties = builder.dynamicProperties;
		this.annotationRefs = builder.annotationRefs;
		this.citationRefs = builder.citationRefs;
		this.evidenceRefs = builder.evidenceRefs;
		this.boardWidth = builder.boardWidth;
		this.boardHeight = builder.boardHeight;
		this.backgroundColor = builder.backgroundColor;
		this.infoBox = builder.infoBox;
		this.organism = builder.organism;
		this.source = builder.source;
		this.version = builder.version;
		this.license = builder.license;
		this.xref = builder.xref;
	}

	/**
	 * Returns the title or name of the pathway.
	 * 
	 * @return title the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title or name of the pathway.
	 * 
	 * @param title the title.
	 */
	public void setTitle(String title) {
		if (title == null) {
			throw new IllegalArgumentException();
		} else
			this.title = title;
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
	 * @param boardWidth the board width
	 */
	public void setBoardWidth(double boardWidth) {
		if (boardWidth < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + boardWidth);
		} else {
			this.boardWidth = boardWidth;
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
	 * @param boardWidth the board width
	 */
	public void setBoardHeight(double boardHeight) {
		if (boardHeight < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + boardHeight);
		} else {
			this.boardHeight = boardHeight;
		}
	}

	/**
	 * Returns the background color of the pathway.
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
	 * Sets the background color of the pathway.
	 * 
	 * @param backgroundColor the background color.
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Returns infoBox. InfoBox holds the xy coordinates for where information, e.g.
	 * name and organism, are displayed in the pathway. Is Pathway.InfoBox or
	 * gpml:InfoBox in GPML.
	 *
	 * @return infoBox the Coordinate center of the info box.
	 */
	public Coordinate getInfoBox() {
		return infoBox;
	}

	/**
	 * Sets infoBox. InfoBox holds the xy coordinates for where information, e.g.
	 * name and organism, are displayed in the pathway. Is Pathway.InfoBox or
	 * gpml:InfoBox in GPML.
	 *
	 * @param infoBox the Coordinate center of the info box.
	 */
	public void setInfoBox(Coordinate infoBox) {
		this.infoBox = infoBox;
	}

	/**
	 * Returns the organism of the pathway. Organism is the scientific name (e.g.,
	 * Homo sapiens) of the species being described by the pathway.
	 * 
	 * @return organism the organism.
	 */
	public String getOrganism() {
		return organism;
	}

	/**
	 * Sets the organism of the pathway. Organism is the scientific name (e.g., Homo
	 * sapiens) of the species being described by the pathway.
	 * 
	 * @param organism the organism.
	 */
	public void setOrganism(String organism) {
		if (organism == null) {
			throw new IllegalArgumentException();
		} else
			this.organism = organism;
	}

	/**
	 * Returns the source of the pathway, e.g. WikiPathways, KEGG, Cytoscape.
	 * 
	 * @return source the source.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source of the pathway, e.g. WikiPathways, KEGG, Cytoscape.
	 * 
	 * @param source the source.
	 */
	public void setSource(String source) {
		if (source == null) {
			throw new IllegalArgumentException();
		} else
			this.source = source;
	}

	/**
	 * Returns the version of the pathway.
	 * 
	 * @return version the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version of the pathway.
	 * 
	 * @param version the version.
	 */
	public void setVersion(String version) {
		if (version == null) {
			throw new IllegalArgumentException();
		} else
			this.version = version;
	}

	/**
	 * Returns the license of the pathway.
	 * 
	 * @return license the license.
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * Sets the license of the pathway.
	 * 
	 * @param license the license.
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	/**
	 * Returns the Xref for the pathway.
	 * 
	 * @return xref the xref of the pathway.
	 */
	public Xref getXref() {
		return xref;
	}
	
	/**
	 * Sets the Xref for the pathway.
	 * 
	 * @param xref the xref of the pathway.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

	/**
	 * Creates Xref given identifier and dataSource. Checks whether
	 * dataSource string is fullName, systemCode, or invalid.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 * @throws IllegalArgumentException is given dataSource does not exist.
	 */
	public void createXref(String identifier, String dataSource) {
		if (DataSource.fullNameExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		} else if (DataSource.systemCodeExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getByAlias(dataSource));
		} else {
			DataSource.register(dataSource, dataSource);
			System.out.println("DataSource: " + dataSource + " is registered."); // TODO warning
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource)); // TODO fullname/code both ok
		}
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
	 * @returns the comment content with a given source.
	 */
	public String findComment(String source) {
		for (Comment comment : comments) {
			if (source.equals(comment.getSource())) {
				return comment.getContent();
			}
		}
		return null;
	}

	/**
	 * Returns the map of dynamic properties.
	 * 
	 * @param key the key of a key value pair.
	 * @return the value or dynamic property.
	 */
	public Map<String, String> getDynamicProperties() {
		return dynamicProperties;
	}

	/**
	 * Gets a set of all dynamic property keys.
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
	}

	/**
	 * Returns the list of annotations referenced.
	 * 
	 * @return annotationRefs the list of annotation references.
	 */
	public List<AnnotationRef> getAnnotationRefs() {
		return annotationRefs;
	}

	/**
	 * Adds given annotationRef to annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be added.
	 */
	public void addAnnotationRef(AnnotationRef annotationRef) {
		annotationRefs.add(annotationRef);
	}

	/**
	 * Removes given annotationRef from annotationRefs list.
	 * 
	 * @param annotationRef the annotationRef to be removed.
	 */
	public void removeAnnotationRef(AnnotationRef annotationRef) {
		annotationRefs.remove(annotationRef);
	}

	/**
	 * Returns the list of citations referenced.
	 * 
	 * @return citationRefs the list of citations referenced.
	 */
	public List<Citation> getCitationRefs() {
		return citationRefs;
	}

	/**
	 * Adds given citationRef to citationRefs list.
	 * 
	 * @param citationRef the citationRef to be added.
	 */
	public void addCitationRef(Citation citationRef) {
		citationRefs.add(citationRef);
	}

	/**
	 * Removes given citationRef from citationRefs list.
	 * 
	 * @param citationRef the citationRef to be removed.
	 */
	public void removeCitationRef(Citation citationRef) {
		citationRefs.remove(citationRef);
	}

	/**
	 * Returns the list of evidences referenced.
	 * 
	 * @return evidenceRefs the list of evidences referenced.
	 */
	public List<Evidence> getEvidenceRefs() {
		return evidenceRefs;
	}

	/**
	 * Adds given evidenceRef to evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be added.
	 */
	public void addEvidenceRef(Evidence evidenceRef) {
		evidenceRefs.add(evidenceRef);
	}

	/**
	 * Removes given evidenceRef from evidenceRefs list.
	 * 
	 * @param evidenceRef the evidenceRef to be removed.
	 */
	public void removeEvidenceRef(Evidence evidenceRef) {
		evidenceRefs.remove(evidenceRef);
	}

}
