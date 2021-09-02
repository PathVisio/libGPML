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
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.bridgedb.Xref;
import org.pathvisio.debug.*;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.events.PathwayEvent;
import org.pathvisio.events.PathwayListener;
import org.pathvisio.io.*;

import java.io.Reader;

import org.pathvisio.model.DataNode.State;
import org.pathvisio.model.LineElement.LinePoint;
import org.pathvisio.model.LineElement.Anchor;
import org.pathvisio.model.GraphLink.LinkableFrom;
import org.pathvisio.model.GraphLink.LinkableTo;
import org.pathvisio.model.ref.Annotation;
import org.pathvisio.model.ref.Citation;
import org.pathvisio.model.ref.Evidence;
import org.pathvisio.model.ref.Pathway;
import org.pathvisio.util.Utils;

/**
 * This class stores information for a Pathway model. Pathway model contains
 * pathway elements and properties. The pathway model stores all information
 * necessary for maintaining, loading and saving pathway data; reading in,
 * writing from.
 * 
 * @author unknown, finterly
 */
public class PathwayModel {

	private Pathway pathway; // pathway information
	// for elementId to PathwayElement
	private Map<String, PathwayObject> elementIdToPathwayObject; // TODO PathwayElement????
	// for PathwayElement and all the LinePoints which point to it
	private Map<LinkableTo, Set<LinkableFrom>> elementRefToLinePoints;
	// for Group aliasRef and all the DataNode aliases for it
	private Map<Group, Set<DataNode>> aliasRefToAliases;
	private List<DataNode> dataNodes; // contains states
	private List<Interaction> interactions; // contains points and anchors
	private List<GraphicalLine> graphicalLines; // contains points and anchors
	private List<Label> labels;
	private List<Shape> shapes;
	private List<Group> groups;
	private List<Annotation> annotations;
	private List<Citation> citations;
	private List<Evidence> evidences;

	/**
	 * Initializes a pathway model object with {@link Pathway} information.
	 * 
	 * @param pathway the pathway object containing metadata information, e.g.
	 *                title, organism...
	 */
	public PathwayModel(Pathway pathway) {
		this.pathway = pathway;
		this.elementIdToPathwayObject = new HashMap<String, PathwayObject>();
		this.elementRefToLinePoints = new HashMap<LinkableTo, Set<LinkableFrom>>();
		this.aliasRefToAliases = new HashMap<Group, Set<DataNode>>();
		this.dataNodes = new ArrayList<DataNode>();
		this.interactions = new ArrayList<Interaction>();
		this.graphicalLines = new ArrayList<GraphicalLine>();
		this.labels = new ArrayList<Label>();
		this.shapes = new ArrayList<Shape>();
		this.groups = new ArrayList<Group>();
		this.annotations = new ArrayList<Annotation>();
		this.citations = new ArrayList<Citation>();
		this.evidences = new ArrayList<Evidence>();
	}

	/**
	 * Initializes a pathway model object with {@link Pathway} default values.
	 */
	public PathwayModel() {
		this(new Pathway.PathwayBuilder("Click to add title", 0, 0, Color.decode("#ffffff")).build());
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
		if (pathway == null) {
			this.pathway = pathway;
		}
	}

	/**
	 * Returns a unique elementId.
	 * 
	 * @return a unique elementId.
	 */
	public String getUniqueElementId() {
		return getUniqueId(elementIdToPathwayObject.keySet());
	}

	/**
	 * Returns PathwayElement for the given String elementId key.
	 * 
	 * @param elementId the given elementId key.
	 * @return the PathwayElement for the given elementId key.
	 */
	public PathwayObject getPathwayObject(String elementId) {
		return elementIdToPathwayObject.get(elementId);
	}

	/**
	 * Returns all pathway objects for the pathway model.
	 * 
	 * @param elementId the given elementId key.
	 * @return the pathway object for the given elementId key.
	 */
	public List<PathwayObject> getPathwayObjects() {
		List<PathwayObject> pathwayObjects = new ArrayList<>(elementIdToPathwayObject.values());
		return pathwayObjects;
	}

	/**
	 * Checks if the pathway model has the given pathway object.
	 * 
	 * @param pathwayObject the pathway object to check for.
	 * @return true if pathway model has given pathway object, false otherwise.
	 */
	public boolean hasPathwayObject(PathwayObject pathwayObject) {
		return this.getPathwayObjects().contains(pathwayObject);
	}

	/**
	 * Returns a set view of String elementId keys from the elementIdToPathwayObject
	 * hash map.
	 * 
	 * @return a list of elementId keys.
	 */
	public Set<String> getElementIds() {
		return elementIdToPathwayObject.keySet();
	}

	/**
	 * Adds mapping of elementId key to PathwayObject value in the
	 * elementIdToPathwayObject hash map.
	 * 
	 * @param elementId     the elementId
	 * @param pathwayObject the pathway object
	 * @throws IllegalArgumentException if elementId or elementIdContainer are null.
	 * @throws IllegalArgumentException if elementId is not unique.
	 */
	public void addElementId(String elementId, PathwayObject pathwayObject) {
		if (pathwayObject == null || elementId == null) {
			throw new IllegalArgumentException("unique elementId can't be null");
		}
		if (elementIdToPathwayObject.containsKey(elementId)) {
			System.out.println(elementIdToPathwayObject);
			throw new IllegalArgumentException("elementId '" + elementId + "' is not unique");
		}
		elementIdToPathwayObject.put(elementId, pathwayObject);
	}

	/**
	 * Removes the mapping of given elementId key from the elementIdToPathwayObject
	 * hash map. TODO public?
	 * 
	 * @param elementId the elementId key.
	 */
	public void removeElementId(String elementId) {
		elementIdToPathwayObject.remove(elementId);
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
	 * Returns all {@link LinkableFrom} {@link LinePoints} that refer to a
	 * {@link LinkableTo} pathway element.
	 */
	public Set<LinkableFrom> getReferringLinkableFroms(LinkableTo pathwayElement) {
		Set<LinkableFrom> refs = elementRefToLinePoints.get(pathwayElement);
		if (refs != null) {
			// create defensive copy to prevent problems with ConcurrentModification.
			return new HashSet<LinkableFrom>(refs);
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Register a link from a elementRef to a linePoint(s) //TODO check if correct
	 * 
	 * @param elementRef the pathway element which can be linked to.
	 * @param linePoint  the linePoint with given elementRef.
	 */
	public void addElementRef(LinkableTo elementRef, LinePoint linePoint) {
		Utils.multimapPut(elementRefToLinePoints, elementRef, linePoint);
	}

	/**
	 * Removes a linePoint linked to a elementRef. //TODO check if correct
	 * 
	 * @param elementRef the pathway element which is linked to linePoint.
	 * @param linePoint  the linePoint with given elementRef.
	 */
	void removeElementRef(LinkableTo elementRef, LinePoint linePoint) {
		if (!elementRefToLinePoints.containsKey(elementRef))
			throw new IllegalArgumentException();
		elementRefToLinePoints.get(elementRef).remove(linePoint);
		if (elementRefToLinePoints.get(elementRef).size() == 0)
			elementRefToLinePoints.remove(elementRef);
	}
//
//	/**
//	 * Returns the Group to which a data node refers to. For example, when a
//	 * DataNode has type="alias" it may be an alias for a Group pathway element. To
//	 * get elementRef for a dataNode use {@link DataNode#getAliasRef()}.
//	 * 
//	 * @param dataNode the dataNode which has elementRef
//	 * @return pathway element to which dataNode elementRef refers.
//	 */
//	public PathwayElement getDataNodesFromGroup(DataNode dataNode) {
//		return elementRefToDataNode.get(dataNode);
//	}

	public boolean hasAliasRef(Group aliasRef) {
		return aliasRefToAliases.containsKey(aliasRef);
	}

	public boolean hasAlias(Group aliasRef, DataNode alias) {
		return aliasRefToAliases.get(aliasRef).contains(alias);
	}

	/**
	 * Adds mapping of elementRef to data node in the elementRefToDataNode hash map.
	 * 
	 * @param elementRef the pathway element to which a dataNode refers.
	 * @param dataNode   the datanode which has a elementRef.
	 * @throws IllegalArgumentException if elementRef or dataNode are null.
	 */
	public void addAlias(Group aliasRef, DataNode alias) {
		if (aliasRef == null || alias == null)
			throw new IllegalArgumentException("AliasRef and alias must be valid.");
		Set<DataNode> aliases = aliasRefToAliases.get(aliasRef);
		if (aliases == null) {
			aliases = new HashSet<DataNode>();
			aliasRefToAliases.put(aliasRef, aliases);
		}
		aliases.add(alias);
	}

	public void removeAlias(Group aliasRef, DataNode alias) {
		if (alias == null || aliasRef == null)
			throw new IllegalArgumentException("AliasRef and alias must be valid.");
		assert (alias.getAliasRef() == aliasRef);
		assert (hasAlias(aliasRef, alias));
		Set<DataNode> aliases = aliasRefToAliases.get(aliasRef);
		aliases.remove(alias);
		if (alias.getAliasRef() != null)
			alias.setAliasRefTo(null);
		// removes aliasRef if it has no aliases
		if (aliases.isEmpty())
			removeAliasRef(aliasRef);
	}

	/**
	 * Removes the mapping of given elementRef key from the elementRefToDataNode
	 * hash map. TODO public?
	 * 
	 * @param elementRef the elementRef key.
	 */
	public void removeAliasRef(Group aliasRef) {
		assert (hasAliasRef(aliasRef));
		Set<DataNode> aliases = aliasRefToAliases.get(aliasRef);
		if (!aliases.isEmpty()) {
			for (DataNode alias : aliases) {
				removeAlias(aliasRef, alias);
			}
		}
		aliasRefToAliases.remove(aliasRef);
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
	 * Adds the given dataNode to dataNodes list. Sets pathwayModel and elementId,
	 * and maps to elementIdToPathwayObject.
	 * 
	 * @param dataNode the data node to be added.
	 */
	public void addDataNode(DataNode dataNode) {
		addPathwayObject(dataNode);
		dataNodes.add(dataNode);
	}

	/**
	 * Removes the given dataNode from dataNodes list and elementIdToPathwayObject
	 * map.
	 * 
	 * @param dataNode the data node to be removed.
	 */
	public void removeDataNode(DataNode dataNode) {
		dataNodes.remove(dataNode);
		removePathwayObject(dataNode);
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
	 * Adds the given interaction to interactions list. Sets pathwayModel and
	 * elementId, and maps to elementIdToPathwayObject.
	 * 
	 * @param interaction the interaction to be added.
	 */
	public void addInteraction(Interaction interaction) {
		addPathwayObject(interaction);
		interactions.add(interaction);
	}

	/**
	 * Removes the given interaction from interactions list and
	 * elementIdToPathwayObject map..
	 * 
	 * @param interaction the interaction to be removed.
	 */
	public void removeInteraction(Interaction interaction) {
		interactions.remove(interaction);
		removePathwayObject(interaction);

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
	 * Adds the given graphicalLine to graphicalLines list. Sets pathwayModel and
	 * elementId, and maps to elementIdToPathwayObject.
	 * 
	 * @param graphicalLine the graphicalLine to be added.
	 */
	public void addGraphicalLine(GraphicalLine graphicalLine) {
		addPathwayObject(graphicalLine);
		graphicalLines.add(graphicalLine);
	}

	/**
	 * Removes the given graphicalLine from graphicalLines list and
	 * elementIdToPathwayObject map..
	 * 
	 * @param graphicalLine the graphicalLine to be removed.
	 */
	public void removeGraphicalLine(GraphicalLine graphicalLine) {
		graphicalLines.remove(graphicalLine);
		removePathwayObject(graphicalLine);

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
	 * Adds the given label to labels list. Sets pathwayModel and elementId, and
	 * maps to elementIdToPathwayObject.
	 * 
	 * @param label the label to be added.
	 */
	public void addLabel(Label label) {
		addPathwayObject(label);
		labels.add(label);
	}

	/**
	 * Removes the given label from labels list and elementIdToPathwayObject map.
	 * 
	 * @param label the label to be removed.
	 */
	public void removeLabel(Label label) {
		labels.remove(label);
		removePathwayObject(label);

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
	 * Adds the given shape to shapes list.Sets pathwayModel and elementId, and maps
	 * to elementIdToPathwayObject.
	 * 
	 * @param shape the shape to be added.
	 */
	public void addShape(Shape shape) {
		addPathwayObject(shape);
		shapes.add(shape);
	}

	/**
	 * Removes the given shape from shapes list and elementIdToPathwayObject map.
	 * 
	 * @param shape the shape to be removed.
	 */
	public void removeShape(Shape shape) {
		shapes.remove(shape);
		removePathwayObject(shape);

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
	 * Adds the given group to groups list. Sets pathwayModel and elementId, and
	 * maps to elementIdToPathwayObject.
	 * 
	 * @param group the group to be added.
	 */
	public void addGroup(Group group) {
		addPathwayObject(group);
		groups.add(group);
	}

	/**
	 * Removes the given group from groups list and elementIdToPathwayObject map.
	 * 
	 * @param group the group to be removed.
	 */
	public void removeGroup(Group group) {
		groups.remove(group);
		removePathwayObject(group);
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
	 * Adds given annotation to annotations list. If there is an annotation with
	 * equivalent properties in the pathway model, the given annotation is not added
	 * and the equivalent annotation is returned. Also sets pathwayModel and
	 * elementId, and maps to elementIdToPathwayObject.
	 * 
	 * @param annotation the new annotation to be added.
	 * @return annotation the new annotation or annotationExisting the existing
	 *         equivalent annotation.
	 */
	public Annotation addAnnotation(Annotation annotation) {
		Annotation annotationExisting = annotationExists(annotation);
		if (annotationExisting != null) {
			Logger.log.trace("Duplicate annotation is not added to pathway model.");
			return annotationExisting;
		} else {
			addPathwayObject(annotation);
			annotations.add(annotation);
			return annotation;
		}
	}

	/**
	 * Checks if given annotation already exists for the pathway model.
	 * 
	 * @param annotation the given annotation to be checked.
	 * @return annotationExisting the existing equivalent annotation, or null if no
	 *         equivalent annotation exists for given citation.
	 */
	public Annotation annotationExists(Annotation annotation) {
		for (Annotation annotationExisting : annotations) {
			if (annotation.equalsAnnotation(annotationExisting)) {
				Logger.log.trace("New annotation is equivalent to existing annotation "
						+ annotationExisting.getElementId() + ".");
				return annotationExisting;
			}
		}
		return null;
	}

	/**
	 * Removes given annotation from annotations list and elementIdToPathwayObject
	 * map.
	 * 
	 * @param annotation the annotation to be removed.
	 */
	public void removeAnnotation(Annotation annotation) {
		annotations.remove(annotation);
		removePathwayObject(annotation);
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
	 * Adds given citation to citations list. If there is an citation with
	 * equivalent properties in the pathway model, the given citation is not added
	 * and the equivalent citation is returned. Also sets pathwayModel and
	 * elementId, and maps to elementIdToPathwayObject.
	 * 
	 * @param citation the new citation to be added.
	 * @return citation the new citation or citationExisting the existing equivalent
	 *         citation.
	 */
	public Citation addCitation(Citation citation) {
		Citation citationExisting = hasEqualCitation(citation);
		if (citationExisting != null) {
			Logger.log.trace("Duplicate citation is not added to pathway model.");
			return citationExisting;
		} else {
			addPathwayObject(citation);
			citations.add(citation);
			return citation;
		}
	}

	/**
	 * Checks if given citation already exists for the pathway model.
	 * 
	 * @param citation the given citation to be checked.
	 * @return citationExisting the existing equivalent citation, or null if no
	 *         equivalent citation exists for given citation.
	 */
	public Citation hasEqualCitation(Citation citation) {
		for (Citation citationExisting : citations) {
			if (citation.equalsCitation(citationExisting)) {
				Logger.log.trace(
						"New citation is equivalent to existing citation " + citationExisting.getElementId() + ".");
				return citationExisting;
			}
		}
		return null;
	}

	/**
	 * Removes given citation from citations list and elementIdToPathwayObject map.
	 * 
	 * @param citation the citation to be removed.
	 */
	public void removeCitation(Citation citation) {
		citations.remove(citation);// TODO
		removePathwayObject(citation);
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
	 * Adds given evidence to evidences. If there is an evidence with equivalent
	 * properties in the pathway model, the given evidence is not added and the
	 * equivalent evidence is returned. Also sets pathwayModel and elementId, and
	 * maps to elementIdToPathwayObject.
	 * 
	 * @param evidence the evidence to be added.
	 */
	public Evidence addEvidence(Evidence evidence) {
		Evidence evidenceExisting = hasEqualEvidence(evidence);
		if (evidenceExisting != null) {
			Logger.log.trace("Duplicate evidence is not added to pathway model.");
			return evidenceExisting;
		} else {
			addPathwayObject(evidence);
			evidences.add(evidence);
			return evidence;
		}
	}

	/**
	 * Checks if given evidence already exists for the pathway model.
	 * 
	 * @param evidence the given evidence to be checked.
	 * @return evidenceExisting the existing equivalent citation, or null if no
	 *         equivalent citation exists for given citation.
	 */
	public Evidence hasEqualEvidence(Evidence evidence) {
		for (Evidence evidenceExisting : evidences) {
			if (evidence.equalsEvidence(evidenceExisting)) {
				Logger.log.trace(
						"New Evidence is equivalent to existing evidence " + evidenceExisting.getElementId() + ".");
				return evidenceExisting;
			}
		}
		return null;
	}

	/**
	 * Removes given evidence from evidences list and elementIdToPathwayObject map.
	 * 
	 * @param evidence the evidence to be removed.
	 */
	public void removeEvidence(Evidence evidence) {
		evidences.remove(evidence);
		removePathwayObject(evidence);
	}

	/**
	 * Adds the given pathway object to pathway model. Sets pathwayModel for the
	 * given pathway object. Sets an unique elementId for given pathway object if
	 * not already set. Corresponding elementId and given pathway object are added
	 * to elementIdToPathwayObject map.
	 * 
	 * @param pathwayObject the pathway element to add.
	 */
	public void addPathwayObject(PathwayObject pathwayObject) {
		// do not add pathway object if Pathway TODO 
		if (pathwayObject.getClass() == Pathway.class) {
			throw new IllegalArgumentException("Pathway cannot be added as a pathwayObject");
		}
		assert (pathwayObject != null);
		pathwayObject.setPathwayModelTo(this);
		assert (pathwayObject.getPathwayModel() == this);
		String elementId = pathwayObject.getElementId();
		if (elementId == null)
			pathwayObject.setGeneratedElementId();
		addElementId(pathwayObject.getElementId(), pathwayObject); // TODO
	}

	/**
	 * Removes the given pathway object from pathway model and
	 * elementIdToPathwayObject map. The pathway object is terminated in the
	 * process.
	 * 
	 * @param pathwayObject the pathway object to remove.
	 */
	public void removePathwayObject(PathwayObject pathwayObject) {
		assert (pathwayObject != null);
		assert (hasPathwayObject(pathwayObject));
		removeElementId(pathwayObject.getElementId());
		pathwayObject.terminate(); // TODO
	}

	/**
	 * TODO
	 * 
	 * removes object sets parent of object to null fires PathwayEvent.DELETED event
	 * <i>before</i> removal of the object
	 * 
	 * TODO was forceRemove??? removes object, regardless whether the object may be
	 * removed or not sets parent of object to null fires PathwayEvent.DELETED event
	 * <i>before</i> removal of the object
	 *
	 * @param o the object to remove
	 */
	public void remove(PathwayObject pathwayElement) {
		assert (pathwayElement.getPathwayModel() == this); // can only remove direct child objects
		if (pathwayElement.getClass() == DataNode.class) {
			removeDataNode((DataNode) pathwayElement);
		}
		if (pathwayElement.getClass() == State.class) {
			DataNode dataNode = ((State) pathwayElement).getDataNode();
			dataNode.removeState((State) pathwayElement);
		}
		if (pathwayElement.getClass() == Interaction.class) {
			removeInteraction((Interaction) pathwayElement);
		}
		if (pathwayElement.getClass() == GraphicalLine.class) {
			removeGraphicalLine((GraphicalLine) pathwayElement);
		}
		if (pathwayElement.getClass() == Label.class) {
			removeLabel((Label) pathwayElement);
		}
		if (pathwayElement.getClass() == Shape.class) {
			removeShape((Shape) pathwayElement);
		}
		if (pathwayElement.getClass() == Group.class) {
			removeGroup((Group) pathwayElement);
		}
		if (pathwayElement.getClass() == Anchor.class) {
			LineElement lineElement = ((Anchor) pathwayElement).getLineElement();
			lineElement.removeAnchor((Anchor) pathwayElement);
		}
		if (pathwayElement.getClass() == LinePoint.class) {
			LineElement lineElement = ((LinePoint) pathwayElement).getLineElement();
			lineElement.removeLinePoint((LinePoint) pathwayElement);
		}
		// Citation...Anchor..Annotation...?????
		fireObjectModifiedEvent(new PathwayEvent(pathwayElement, PathwayEvent.DELETED)); // TODO

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

	private File sourceFile = null;

	/**
	 * Returns the xml file containing the Gpml/mapp pathway currently displayed
	 * 
	 * @return current xml file
	 */
	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File file) {
		sourceFile = file;
	}

	/**
	 * Writes the JDOM document to the file specified
	 * 
	 * @param file     the file to which the JDOM document should be saved
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 */
	public void writeToXml(File file, boolean validate) throws ConverterException {
		GpmlFormat.writeToXml(this, file, validate);
		setSourceFile(file);
//		clearChangedFlag();

	}

	public void readFromXml(Reader in, boolean validate) throws ConverterException {
		GpmlFormat.readFromXml(this, in, validate);
		setSourceFile(null);
//		clearChangedFlag();
	}

	public void readFromXml(InputStream in, boolean validate) throws ConverterException {
		GpmlFormat.readFromXml(this, in, validate);
		setSourceFile(null);
//		clearChangedFlag();
	}

	public void readFromXml(File file, boolean validate) throws ConverterException {
		Logger.log.info("Start reading the XML file: " + file);
		GpmlFormat.readFromXml(this, file, validate);
		setSourceFile(file);
//		clearChangedFlag();
	}

//	public void writeToMapp (File file) throws ConverterException
//	{
//		new MappFormat().doExport(file, this);
//	}
//
//	public void writeToSvg (File file) throws ConverterException
//	{
//		//Use Batik instead of SvgFormat
//		//SvgFormat.writeToSvg (this, file);
//		new BatikImageExporter(ImageExporter.TYPE_SVG).doExport(file, this);
//	}
//

	/**
	 * Listener methods
	 * 
	 */
	private boolean changed = true;

	/**
	 * The "changed" flag tracks if the Pathway has been changed since the file was
	 * opened or last saved. New pathways start changed.
	 */
	public boolean hasChanged() {
		return changed;
	}

	/**
	 * clearChangedFlag should be called after when the current pathway is known to
	 * be the same as the one on disk. This happens when you just opened it, or when
	 * you just saved it.
	 */
	public void clearChangedFlag() {
		if (changed) {
			changed = false;
			fireStatusFlagEvent(new StatusFlagEvent(changed));
			// System.out.println ("Changed flag is cleared");
		}
	}

	/**
	 * To be called after each edit operation
	 */
	private void markChanged() {
		if (!changed) {
			changed = true;
			fireStatusFlagEvent(new StatusFlagEvent(changed));
			// System.out.println ("Changed flag is set");
		}
	}

	/**
	 * Used by children of this Pathway to notify the parent of modifications. A
	 * coordinate change could trigger dependent objects such as states, groups and
	 * connectors to be updated as well.
	 */
	void childModified(PathwayElementEvent e) {
		markChanged();
		if (e.isCoordinateChange()) {

			PathwayObject elt = e.getModifiedPathwayElement();

//			if (elt.getClass() == LinkableTo.class) {
//				for (LinkableFrom linePoints : getReferringLinkableFroms((LinkableTo) elt)) {
			// refc.refeeChanged();

//					elt.fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(elt));
//					// TODO looks ok?
//				}
//			}
//			String ref = elt.getGroupRef();
//			if (ref != null && getGroupById(ref) != null) {
//				// identify group object and notify model change to trigger view update
//				PathwayElement group = getGroupById(ref);
//				group.fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(group));
//			}
//			checkMBoardSize(e.getModifiedPathwayElement());
		}
	}

	/**
	 * Implement this interface if you want to be notified when the "changed" status
	 * changes. This happens e.g. when the user makes a change to an unchanged
	 * pathway, or when a changed pathway is saved.
	 */
	public interface StatusFlagListener extends EventListener {
		public void statusFlagChanged(StatusFlagEvent e);
	}

	/**
	 * Event for a change in the "changed" status of this Pathway
	 */
	public static class StatusFlagEvent {
		private boolean newStatus;

		public StatusFlagEvent(boolean newStatus) {
			this.newStatus = newStatus;
		}

		public boolean getNewStatus() {
			return newStatus;
		}
	}

	private List<StatusFlagListener> statusFlagListeners = new ArrayList<StatusFlagListener>();

	/**
	 * Register a status flag listener
	 */
	public void addStatusFlagListener(StatusFlagListener v) {
		if (!statusFlagListeners.contains(v))
			statusFlagListeners.add(v);
	}

	/**
	 * Remove a status flag listener
	 */
	public void removeStatusFlagListener(StatusFlagListener v) {
		statusFlagListeners.remove(v);
	}

	// TODO: make private
	public void fireStatusFlagEvent(StatusFlagEvent e) {
		for (StatusFlagListener g : statusFlagListeners) {
			g.statusFlagChanged(e);
		}
	}

	/**
	 * Transfer statusflag listeners from one pathway to another. This is used
	 * needed when copies of the pathway are created / returned by UndoManager. The
	 * status flag listeners are only interested in status flag events of the active
	 * copy.
	 */
	public void transferStatusFlagListeners(PathwayModel dest) {
		for (Iterator<StatusFlagListener> i = statusFlagListeners.iterator(); i.hasNext();) {
			StatusFlagListener l = i.next();
			dest.addStatusFlagListener(l);
			i.remove();
		}
	}

	private List<PathwayListener> listeners = new ArrayList<PathwayListener>();

	public void addListener(PathwayListener v) {
		if (!listeners.contains(v))
			listeners.add(v);
	}

	public void removeListener(PathwayListener v) {
		listeners.remove(v);
	}

	/**
	 * Firing the ObjectModifiedEvent has the side effect of marking the Pathway as
	 * changed.
	 */
	public void fireObjectModifiedEvent(PathwayEvent e) {
		markChanged();
		for (PathwayListener g : listeners) {
			g.pathwayModified(e);
		}
	}

}