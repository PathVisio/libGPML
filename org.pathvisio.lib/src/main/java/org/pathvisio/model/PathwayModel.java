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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.bridgedb.Xref;
import org.pathvisio.debug.Logger;
import org.pathvisio.io.ConverterException;
import org.pathvisio.io.GpmlFormat;
import java.io.Reader;
import org.pathvisio.model.elements.*;

/**
 * This class stores information for a Pathway model. Pathway model contains
 * pathway elements and properties.
 * 
 * TODO: MOVE: It is responsible for storing all information necessary for
 * maintaining, loading and saving pathway data. Reading in, writing from.
 * 
 * @author unknown, finterly
 */
public class PathwayModel {

	private Pathway pathway; // pathway information
	private List<Author> authors; // move to Pathway?
	private Map<String, PathwayElement> elementIdToPathwayElement;
	private List<Annotation> annotations;
	private List<Citation> citations;
	private List<Evidence> evidences;
	private List<DataNode> dataNodes; // contains states
	private List<Interaction> interactions; // contains waypoints
	private List<GraphicalLine> graphicalLines; // contains waypoints
	private List<Label> labels;
	private List<Shape> shapes;
	private List<Group> groups;
	
	private File sourceFile;

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Constructor for this class, creates a new gpml document
	 */
	public PathwayModel() {
		this.pathway = null;
		this.authors = new ArrayList<Author>();
		this.elementIdToPathwayElement = new HashMap<String, PathwayElement>();
		this.annotations = new ArrayList<Annotation>();
		this.citations = new ArrayList<Citation>();
		this.evidences = new ArrayList<Evidence>();
		this.dataNodes = new ArrayList<DataNode>();
		this.interactions = new ArrayList<Interaction>();
		this.graphicalLines = new ArrayList<GraphicalLine>();
		this.labels = new ArrayList<Label>();
		this.shapes = new ArrayList<Shape>();
		this.groups = new ArrayList<Group>();
	}

	/**
	 * Initializes a pathway model object.
	 * 
	 * @param pathway the pathway object containing metadata information, e.g.
	 *                title, organism...
	 */
	public PathwayModel(Pathway pathway) {
		this.pathway = pathway;
		this.authors = new ArrayList<Author>();
		this.elementIdToPathwayElement = new HashMap<String, PathwayElement>();
		this.annotations = new ArrayList<Annotation>();
		this.citations = new ArrayList<Citation>();
		this.evidences = new ArrayList<Evidence>();
		this.dataNodes = new ArrayList<DataNode>();
		this.interactions = new ArrayList<Interaction>();
		this.graphicalLines = new ArrayList<GraphicalLine>();
		this.labels = new ArrayList<Label>();
		this.shapes = new ArrayList<Shape>();
		this.groups = new ArrayList<Group>();
	}

	/**
	 * Returns the pathway object containing metadata, e.g. title, organism...
	 * 
	 * @return pathway the pathway meta information.
	 */
	public Pathway getPathway() {
		return pathway;
	}

	/**
	 * Sets the pathway object containing metadata, e.g. title, organism...
	 * 
	 * @param pathway the pathway meta information.
	 */
	public void setPathway(Pathway pathway) {
		this.pathway = pathway;
	}

	/**
	 * Returns the list of authors for the pathway model.
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
	 * Returns a unique elementId.
	 * 
	 * @return a unique elementId.
	 */
	public String getUniqueElementId() {
		return getUniqueId(elementIdToPathwayElement.keySet());
	}

	/**
	 * Returns a set view of String elementId keys from the
	 * elementIdToPathwayElements hash map.
	 * 
	 * @return a set of elementId keys.
	 */
	public Set<String> getElementIds() {
		return elementIdToPathwayElement.keySet();
	}

	/**
	 * Returns PathwayElement for the given String elementId key.
	 * 
	 * @param elementId the given elementId key.
	 * @return the PathwayElement for the given elementId key.
	 */
	public PathwayElement getPathwayElement(String elementId) {
		return elementIdToPathwayElement.get(elementId);
	}

	/**
	 * Adds mapping of elementId key to PathwayElement value in the
	 * elementIdToPathwayElement hash map.
	 * 
	 * @param elementId      the elementId
	 * @param pathwayElement the PathwayElement
	 * @throws IllegalArgumentException if elementId or elementIdContainer are null.
	 * @throws IllegalArgumentException if elementId is not unique.
	 */
	public void addElementId(String elementId, PathwayElement pathwayElement) {
		if (pathwayElement == null || elementId == null) {
			throw new IllegalArgumentException("unique elementId can't be null");
		}
		if (elementIdToPathwayElement.containsKey(elementId)) {
			throw new IllegalArgumentException("elementId '" + elementId + "' is not unique");
		}
		elementIdToPathwayElement.put(elementId, pathwayElement);
	}

	/**
	 * Removes the mapping of given elementId key from the elementIdToPathwayElement
	 * hash map.
	 * 
	 * @param elementId the elementId key.
	 */
	void removeElementId(String elementId) {
		elementIdToPathwayElement.remove(elementId);
	}

	/**
	 * Generates random IDs, based on strings of hex digits (0..9 or a..f). IDs are
	 * unique across elementIds per pathway and referenced by elementRefs and
	 * groupRefs.
	 * 
	 * NB: elementId previously named graphId. Group pathway elements previously had
	 * both elementId and groupId(deprecated).
	 * 
	 * @param ids the collection of already existing IDs.
	 * @return result the new unique ID unique for this pathway.
	 */
	public static String getUniqueId(Set<String> ids) {
		String result;
		Random random = new Random();
		int mod = 0x60000; // 3 hex letters
		int min = 0xa0000; // must start with a letter
		// add hex letters if set size large
		if ((ids.size()) > 0x10000) {
			mod = 0x60000000;
			min = 0xa0000000;
		}
		do {
			result = Integer.toHexString(Math.abs(random.nextInt()) % mod + min);
		} while (ids.contains(result));
		return result;
	}

	/**
	 * Returns the list of annotations.
	 * 
	 * @return annotations the list of annotations.
	 */
	public List<Annotation> getAnnotations() {
		return annotations;
	}

	/**
	 * Adds given annotation to annotations list.
	 * 
	 * @param annotation the annotation to be added.
	 */
	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);
	}

	/**
	 * Removes given annotation from annotations list.
	 * 
	 * @param annotation the annotation to be removed.
	 */
	public void removeAnnotation(Annotation annotation) {
		annotations.remove(annotation);
	}

	/**
	 * Returns the list of citations.
	 * 
	 * @return citation the list of citations.
	 */
	public List<Citation> getCitations() {
		return citations;
	}

	/**
	 * Adds given citation to citations list.
	 * 
	 * @param citation the citation to be added.
	 */
	public void addCitation(Citation citation) {
		citations.add(citation);
	}

	/**
	 * Removes given citation from citation=s list.
	 * 
	 * @param citation the citation to be removed.
	 */
	public void removeCitation(Citation citation) {
		citations.remove(citation);
	}

	/**
	 * Returns the list of evidences.
	 * 
	 * @return evidences the list of evidences.
	 */
	public List<Evidence> getEvidences() {
		return evidences;
	}

	/**
	 * Adds given evidence to evidences.
	 * 
	 * @param evidence the evidence to be added.
	 */
	public void addEvidence(Evidence evidence) {
		evidences.add(evidence);
	}

	/**
	 * Removes given evidence from evidences list.
	 * 
	 * @param evidence the evidence to be removed.
	 */
	public void removeEvidence(Evidence evidence) {
		evidences.remove(evidence);
	}

	/**
	 * Returns the list of data node pathway elements.
	 * 
	 * @return dataNodes the list of data nodes.
	 */
	public List<DataNode> getDataNodes() {
		return dataNodes;
	}

	/**
	 * Adds the given dataNode to dataNodes list.
	 * 
	 * @param dataNode the data node to be added.
	 */
	public void addDataNode(DataNode dataNode) {
		dataNodes.add(dataNode);
	}

	/**
	 * Removes the given dataNodes from dataNodes list.
	 * 
	 * @param dataNode the data node to be removed.
	 */
	public void removeDataNode(DataNode dataNode) {
		dataNodes.remove(dataNode);
	}

	/**
	 * Returns the list of interaction pathway elements.
	 * 
	 * @return interactions the list of interactions.
	 */
	public List<Interaction> getInteractions() {
		return interactions;
	}

	/**
	 * Adds the given interaction to interactions list.
	 * 
	 * @param interaction the interaction to be added.
	 */
	public void addInteraction(Interaction interaction) {
		interactions.add(interaction);
	}

	/**
	 * Removes the given interaction from interactions list.
	 * 
	 * @param interaction the interaction to be removed.
	 */
	public void removeInteraction(Interaction interaction) {
		interactions.remove(interaction);
	}

	/**
	 * Returns the list of graphical line pathway elements.
	 * 
	 * @return graphicalLines the list of graphicalLines.
	 */
	public List<GraphicalLine> getGraphicalLines() {
		return graphicalLines;
	}

	/**
	 * Adds the given graphicalLine to graphicalLines list.
	 * 
	 * @param graphicalLine the graphicalLine to be added.
	 */
	public void addGraphicalLine(GraphicalLine graphicalLine) {
		graphicalLines.add(graphicalLine);
	}

	/**
	 * Removes the given graphicalLine from graphicalLines list.
	 * 
	 * @param graphicalLine the graphicalLine to be removed.
	 */
	public void removeGraphicalLine(GraphicalLine graphicalLine) {
		graphicalLines.remove(graphicalLine);
	}

	/**
	 * Returns the list of label pathway elements.
	 * 
	 * @return labels the list of labels.
	 */
	public List<Label> getLabels() {
		return labels;
	}

	/**
	 * Adds the given label to labels list.
	 * 
	 * @param label the label to be added.
	 */
	public void addLabel(Label label) {
		labels.add(label);
	}

	/**
	 * Removes the given label from labels list.
	 * 
	 * @param label the label to be removed.
	 */
	public void removeLabel(Label label) {
		labels.remove(label);
	}

	/**
	 * Returns the list of shape pathway elements.
	 * 
	 * @return shapes the list of shapes.
	 */
	public List<Shape> getShapes() {
		return shapes;
	}

	/**
	 * Adds the given shape to shapes list.
	 * 
	 * @param shape the shape to be added.
	 */
	public void addShape(Shape shape) {
		shapes.add(shape);
	}

	/**
	 * Removes the given shape from shapes list.
	 * 
	 * @param shape the shape to be removed.
	 */
	public void removeShape(Shape shape) {
		shapes.remove(shape);
	}

	/**
	 * Returns the list of group pathway elements.
	 * 
	 * @return groups the list of groups.
	 */
	public List<Group> getGroups() {
		return groups;
	}

	/**
	 * Adds the given group to groups list.
	 * 
	 * @param group the group to be added.
	 */
	public void addGroup(Group group) {
		groups.add(group);
	}

	/**
	 * Removes the given group from groups list.
	 * 
	 * @param group the group to be removed.
	 */
	public void removeGroup(Group group) {
		groups.remove(group);
	}

	/**
	 * Returns the Xref of all DataNodes in this pathway as a List.
	 * 
	 * @return result the list of xref of all datanodes or an empty arraylist if
	 *         there are no datanodes in this pathway.
	 */
	public List<Xref> getDataNodeXrefs() {
		List<Xref> result = new ArrayList<Xref>();
		for (DataNode dataNode : dataNodes) {
			result.add(dataNode.getXref());
		}
		return result;
	}

	/**
	 * Returns the Xref of all States of all DataNodes in this pathway as a List.
	 *
	 * @return result the list of xref of all states or an empty arraylist if there
	 *         are no interactions in this pathway.
	 */
	public List<Xref> getStateXrefs() {
		List<Xref> result = new ArrayList<Xref>();
		for (DataNode dataNode : dataNodes) {
			List<State> states = dataNode.getStates();
			for (State state : states) {
				Xref stateXref = state.getXref();
				if (stateXref != null) {
					result.add(stateXref);

				}
			}
		}
		return result;
	}

	/**
	 * Returns the Xref of all Interactions in this pathway as a List.
	 *
	 * @return result the list of xref of all interactions or an empty arraylist if
	 *         there are no interactions in this pathway.
	 */
	public List<Xref> getInteractionXrefs() {
		List<Xref> result = new ArrayList<Xref>();
		for (Interaction interaction : interactions) {
			result.add(interaction.getXref());
		}
		return result;
	}

	/**
	 * Returns the Xref of all Groups in this pathway as a List.
	 *
	 * @return result the list of xref of all groups or an empty arraylist if there
	 *         are no interactions in this pathway.
	 */
	public List<Xref> getGroupXrefs() {
		List<Xref> result = new ArrayList<Xref>();
		for (Group group : groups) {
			Xref groupXref = group.getXref();
			if (groupXref != null) {
				result.add(groupXref);
			}
		}
		return result;
	}

	public PathwayModel readFromXml(Reader in) throws ConverterException {
		GpmlFormat.readFromXml(in);
		setSourceFile(null);
	}

	public void readFromXml(InputStream in) throws ConverterException {
		GpmlFormat.readFromXml(this, in);
		setSourceFile(null);
	}

	public void readFromXml(File file) throws ConverterException {
		Logger.log.info("Start reading the XML file: " + file);
		GpmlFormat.readFromXml(this, file);
		setSourceFile(file);
	}

}