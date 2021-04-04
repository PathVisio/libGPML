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

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.elements.DataNode;
import org.pathvisio.model.elements.GraphicalLine;
import org.pathvisio.model.elements.Group;
import org.pathvisio.model.elements.Interaction;
import org.pathvisio.model.elements.Label;
import org.pathvisio.model.elements.Point;
import org.pathvisio.model.elements.Shape;
import org.pathvisio.model.elements.State;
import org.pathvisio.util.Utils;

import temp.ElementLink.ElementIdContainer;
import temp.ElementLink.ElementRefContainer;

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
	private List<Author> authors = new ArrayList<Author>(); // move to Pathway?
	private List<Annotation> annotations;
	private List<Citation> citations;
	private List<Evidence> evidences;
	private List<DataNode> dataNodes; // contains states
	private List<Interaction> interactions; // contains waypoints
	private List<GraphicalLine> graphicalLines; // contains waypoints
	private List<Label> labels;
	private List<Shape> shapes;
	private List<Group> groups;

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

	/*
	 * -------------------------- ELEMENTID & ELEMENTREF ---------------------------
	 */
	/** Mapping of String elementId key to PathwayElement value. */
	private Map<String, PathwayElement> elementIdToPathwayElement = new HashMap<String, PathwayElement>();

	/** Mapping of String elementRef key to Set of PathwayElements. */
	private Map<String, Set<PathwayElement>> elementRefToPathwayElements = new HashMap<String, Set<PathwayElement>>();
	/** Mapping of String groupRef key to Set of PathwayElements of Group. */
	private Map<String, Set<PathwayElement>> groupRefToPathwayElements = new HashMap<String, Set<PathwayElement>>();

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
	 * Returns a set of points that refer to a pathway element with a particular
	 * elementId.
	 * 
	 * TODO DataNode refer to Group as an alias.
	 * 
	 * @param elementRef the reference to elementId.
	 */
	public Set<Point> getReferringPoints(String elementId) {
		Set<Point> result = new HashSet<Point>();
		for (Interaction interaction : interactions) {
			List<Point> points = interaction.getPoints();
			for (Point point : points) {
				if (point.getElementRef().getElementId() == elementId) {
					result.add(point);
				}
			}
		}
		for (GraphicalLine graphicalLine : graphicalLines) {
			List<Point> points = graphicalLine.getPoints();
			for (Point point : points) {
				if (point.getElementRef().getElementId() == elementId) {
					result.add(point);
				}
			}
		}
	}

	/**
	 * Inserts mapping of elementId key to ElementRefContainer value in the
	 * elementIdToSetRefContainer hash map.
	 * 
	 * @param elementRef the reference to elementId.
	 * @param target     the target ElementRefContainer.
	 */
	public void addElementRef(String elementRef, PathwayElement pathwayElement) {
		Utils.multimapPut(elementRefToPathwayElements, elementRef, pathwayElement);
	}

	/**
	 * Removes the mapping of the given elementId key from the
	 * elementIdToSetRefContainer hash map.
	 * 
	 * @param elementRef the reference to elementId.
	 * @param target     the target ElementRefContainer.
	 * @throws IllegalArgumentException if hash map does not contain the given
	 *                                  elementId key.
	 */
	public void removeElementRef(String elementRef, PathwayElement pathwayElement) {
		if (!elementRefToPathwayElements.containsKey(elementRef)) {
			throw new IllegalArgumentException();
		} else {
			elementRefToPathwayElements.get(elementRef).remove(pathwayElement);
			// remove elementId key if zero ElementRefContainers values.
			if (elementRefToPathwayElements.get(elementRef).size() == 0)
				elementRefToPathwayElements.remove(elementRef);
		}
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

	/*
	 * ------------------------------- CommentGroup -------------------------------
	 */

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

	// TODO CitationRef/Annotation Manager

	/*------------------------------- Pathway Elements ------------------------------*/

	public List<DataNode> getDataNodes() {
		return dataNodes;
	}

	public void addDataNode(DataNode dataNode) {
		dataNodes.add(dataNode);
	}

	public void removeDataNode(DataNode dataNode) {
		dataNodes.remove(dataNode);
	}

	public List<Interaction> getInteractions() {
		return interactions;
	}

	public void addInteraction(Interaction interaction) {
		interactions.add(interaction);
	}

	public void removeInteraction(Interaction interaction) {
		interactions.remove(interaction);
	}

	public List<GraphicalLine> getGraphicalLines() {
		return graphicalLines;
	}

	public void addGraphicalLine(GraphicalLine graphicalLine) {
		graphicalLines.add(graphicalLine);
	}

	public void removeGraphicalLine(GraphicalLine graphicalLine) {
		graphicalLines.remove(graphicalLine);
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void addLabel(Label label) {
		labels.add(label);
	}

	public void removeLabel(Label label) {
		labels.remove(label);
	}

	public List<Shape> getShapes() {
		return shapes;
	}

	public void addShape(Shape shape) {
		shapes.add(shape);
	}

	public void removeShape(Shape shape) {
		shapes.remove(shape);
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void addGroup(Group group) {
		groups.add(group);
	}

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

	// -------------------- OLD THINGS -------------------------------------

	/*
	 * Call when making a new mapp. TODO WHAT IS THIS?
	 */
	public void initMappInfo() {
		String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
		pathway.setVersion(dateString);
		pathway.setTitle("New Pathway");
	}

	public String summary() {
		String result = "    " + toString() + "\n    with Objects:";
		for (PathwayElement pe : dataObjects) {
			String code = pe.toString();
			code = code.substring(code.lastIndexOf('@'), code.length() - 1);
			result += "\n      " + code + " " + pe.getObjectType().getTag() + " " + pe.getParent();
		}
		return result;
	}

	/**
	 * Check for any dangling references, and fix them if found This is called just
	 * before writing out a pathway.
	 *
	 * This is a fallback solution for problems elsewhere in the reference handling
	 * code. Theoretically, if the rest of the code is bug free, this should always
	 * return 0.
	 *
	 * @return number of references fixed. Should be 0 under normal circumstances.
	 */
	public int fixReferences() {
		int result = 0;
		Set<String> graphIds = new HashSet<String>();
		for (PathwayElement pe : dataObjects) {
			String id = pe.getElementId();
			if (id != null) {
				graphIds.add(id);
			}
			for (PathwayElement.MAnchor pp : pe.getMAnchors()) {
				String pid = pp.getElementId();
				if (pid != null) {
					graphIds.add(pid);
				}
			}
		}
		for (PathwayElement pe : dataObjects) {
			if (pe.getObjectType() == ObjectType.LINE) {
				String ref = pe.getStartGraphRef();
				if (ref != null && !graphIds.contains(ref)) {
					pe.setStartGraphRef(null);
					result++;
				}

				ref = pe.getEndGraphRef();
				if (ref != null && !graphIds.contains(ref)) {
					pe.setEndGraphRef(null);
					result++;
				}
			}
		}
		if (result > 0) {
			Logger.log.warn("Pathway.fixReferences fixed " + result + " reference(s)");
		}
		for (String ref : biopaxReferenceToDelete) {
			getBiopax().removeElement(getBiopax().getElement(ref));
		}
		return result;
	}

	public void printRefsDebugInfo() {
		for (PathwayElement elt : dataObjects) {
			elt.printRefsDebugInfo();
		}
	}

}
