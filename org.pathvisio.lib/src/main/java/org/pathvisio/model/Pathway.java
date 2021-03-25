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
import java.util.List;
import org.bridgedb.Xref;

/**
 * This class stores metadata for a Pathway.
 * 
 * @author unknown, finterly
 */
public class Pathway {

	private String title = "untitled";
	private String organism = null; // optional
	private String source = null; // optional
	private String version = null; // optional
	private String license = null; // optional
	private Xref xref; // optional
	private List<Comment> comments; // 0 to unbounded
	private List<DynamicProperty> dynamicProperties; // 0 to unbounded
	private List<AnnotationRef> annotationRefs; // 0 to unbounded
	private List<Citation> citationRefs; // 0 to unbounded
	private List<Evidence> evidenceRefs; // 0 to unbounded
	private double boardWidth;
	private double boardHeight;
	private Color backgroundColor;
	private Coordinate infoBox; // the centerXY of gpml:InfoBox

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
	 * @param title the title.
	 */
	public void setTitle(String title) {
		if (title == null) {
			throw new IllegalArgumentException();
		} else
			this.title = title;
	}

	/**
	 * Returns the organism of this pathway. Organism is the scientific name (e.g.,
	 * Homo sapiens) of the species being described by the pathway.
	 * 
	 * @return organism the organism.
	 */
	public String getOrganism() {
		return organism;
	}

	/**
	 * Sets the organism of this pathway. Organism is the scientific name (e.g.,
	 * Homo sapiens) of the species being described by the pathway.
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

	public Xref getXref() {
		return xref;
	}

	public void setXref(Xref xref) {
		this.xref = xref;
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
	 * Returns the list of key value pair information properties.
	 * 
	 * @return properties the list of properties.
	 */
	public List<DynamicProperty> getDynamicProperties() {
		return dynamicProperties;
	}

	/**
	 * Adds given comment to comments list.
	 * 
	 * @param comment the comment to be added.
	 */
	public void addDynamicProperty(DynamicProperty dynamicProperty) {
		dynamicProperties.add(dynamicProperty);
	}

	/**
	 * Removes given comment from comments list.
	 * 
	 * @param comment the comment to be removed.
	 */
	public void removeDynamicProperty(DynamicProperty dynamicProperty) {
		dynamicProperties.remove(dynamicProperty);
	}

	/**
	 * Returns the list of annotation references.
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
	 * Returns the list of citation references.
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
	 * Returns the list of evidence references.
	 * 
	 * @return evidenceRefs the list of evidences referenced.
	 */
	public List<Evidence> getEvidenceRef() {
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

}
