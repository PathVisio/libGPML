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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bridgedb.Xref;
import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.io.ConverterException;
import org.pathvisio.libgpml.model.DataNode.State;
import org.pathvisio.libgpml.model.GraphLink.LinkableFrom;
import org.pathvisio.libgpml.model.GraphLink.LinkableTo;
import org.pathvisio.libgpml.model.LineElement.Anchor;
import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.util.Utils;

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
	private Map<String, PathwayObject> elementIdToPathwayObject;
	// for PathwayElement and all LinePoints which point to it
	private Map<LinkableTo, Set<LinkableFrom>> elementRefToLinePoints;
	// for Group aliasRef and all DataNode aliases for it
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

	// ================================================================================
	// Constructors
	// ================================================================================

	/**
	 * Initializes a pathway model object with default {@link Pathway}.
	 */
	public PathwayModel() {
		this.pathway = new Pathway();
		pathway.setPathwayModelTo(this);
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

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the pathway object containing metadata, e.g. title, organism.
	 *
	 * @return pathway the pathway meta information.
	 */
	public Pathway getPathway() {
		return pathway;
	}

	/**
	 * Replaces the Pathway, calls {@link removeOldPathway} and then
	 * {@link setNewPathway}.
	 *
	 * NB: There can only be one pathway per pathway model.
	 *
	 * @param newP the new pathway info.
	 */
	protected void replacePathway(Pathway newP) {
		Pathway oldP = pathway;
		removeOldPathway(oldP);
		setNewPathway(newP);
	}

	/**
	 * Removes old pathway prior to {@link setNewPathway}, both called by
	 * {@link replacePathway}.
	 *
	 * @param oldP the old pathway.
	 */
	private void removeOldPathway(Pathway oldP) {
		oldP.terminate();
		fireObjectModifiedEvent(new PathwayModelEvent(oldP, PathwayModelEvent.DELETED));
	}

	/**
	 * Sets new pathway after to {@link removeOldPathway}, both called by
	 * {@link replacePathway}.
	 *
	 * @param newP the new pathway to be set.
	 */
	private void setNewPathway(Pathway newP) {
		this.pathway = newP;
		newP.setPathwayModelTo(this);
		fireObjectModifiedEvent(new PathwayModelEvent(newP, PathwayModelEvent.ADDED));
		checkMBoardSize(newP);
	}

	/**
	 * Returns all pathway elements for the pathway model (pathway, dataNodes,
	 * interactions, graphicalLines, labels, shapes, and groups). Includes Pathway.
	 *
	 * @return the pathway elements for this pathway model.
	 */
	public List<PathwayElement> getPathwayElements() {
		List<PathwayElement> result = Stream.of(dataNodes, interactions, graphicalLines, labels, shapes, groups)
				.flatMap(Collection::stream).collect(Collectors.toList());
		result.add(pathway);
		return result;
	}

	/**
	 * Returns all shaped pathway elements for the pathway model (dataNodes, states,
	 * labels, shapes, and groups). NB: Includes states.
	 *
	 * @return the pathway elements for this pathway model.
	 */
	public List<ShapedElement> getShapedElements() {
		List<State> states = new ArrayList<State>();
		for (DataNode dataNode : dataNodes) {
			states.addAll(dataNode.getStates());
		}
		return Stream.of(dataNodes, states, labels, shapes, groups).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all shaped pathway elements for the pathway model (dataNodes, labels,
	 * shapes, and groups). NB: Excludes states.
	 *
	 * @return the pathway elements for this pathway model.
	 */
	public List<ShapedElement> getShapedElementsExclStates() {
		return Stream.of(dataNodes, labels, shapes, groups).flatMap(Collection::stream).collect(Collectors.toList());
	}

	/**
	 * Returns all line pathway elements for the pathway model (interactions and
	 * graphicalLines).
	 *
	 * @return the pathway elements for this pathway model.
	 */
	public List<LineElement> getLineElements() {
		return Stream.of(interactions, graphicalLines).flatMap(Collection::stream).collect(Collectors.toList());
	}

	// ================================================================================
	// ElementIdToPathwayObject Map Methods
	// ================================================================================
	/**
	 * Returns a unique elementId.
	 *
	 * @return a unique elementId.
	 */
	public String getUniqueElementId() {
		return getUniqueId(elementIdToPathwayObject.keySet());
	}

	/**
	 * Returns Pathway Object for the given String elementId key.
	 *
	 * @param elementId the given elementId key.
	 * @return the PathwayObject for the given elementId key.
	 */
	public PathwayObject getPathwayObject(String elementId) {
		return elementIdToPathwayObject.get(elementId);
	}

	/**
	 * Returns all pathway objects for the pathway model.
	 *
	 * @return pathwayObjects the pathway objects for this pathway model.
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
			throw new IllegalArgumentException("elementId '" + elementId + "' is not unique");
		}
		elementIdToPathwayObject.put(elementId, pathwayObject);
	}

	/**
	 * Removes the mapping of given elementId key from the elementIdToPathwayObject
	 * hash map.
	 *
	 * @param elementId the elementId key.
	 */
	protected void removeElementId(String elementId) {
		elementIdToPathwayObject.remove(elementId);
	}

	/**
	 * Randomly generates a new unique ID, based on strings of hex digits (0..9 or
	 * a..f) given a set of existing IDs.
	 *
	 * @param ids the collection of already existing IDs.
	 * @return result the new unique ID.
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

	// ================================================================================
	// ElementRefToLinePoints Map Methods
	// ================================================================================
	/**
	 * Returns all {@link LinkableFrom} {@link LineElement.LinePoint} that refer to
	 * a {@link LinkableTo} pathway element or anchor.
	 */
	public Set<LinkableFrom> getReferringLinkableFroms(LinkableTo elementRef) {
		Set<LinkableFrom> refs = elementRefToLinePoints.get(elementRef);
		if (refs != null) {
			// create defensive copy to prevent problems with ConcurrentModification.
			return new HashSet<LinkableFrom>(refs);
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Register a link from a elementRef to a linePoint(s).
	 *
	 * @param elementRef the pathway element which can be linked to.
	 * @param linePoint  the linePoint with given elementRef.
	 */
	protected void addElementRef(LinkableTo elementRef, LinkableFrom linePoint) {
		Utils.multimapPut(elementRefToLinePoints, elementRef, linePoint);
	}

	/**
	 * Removes a linePoint linked to a elementRef.
	 *
	 * @param elementRef the pathway element which is linked to linePoint.
	 * @param linePoint  the linePoint with given elementRef.
	 */
	protected void removeElementRef(LinkableTo elementRef, LinkableFrom linePoint) {
		if (!elementRefToLinePoints.containsKey(elementRef)) {
			throw new IllegalArgumentException();
		}
		elementRefToLinePoints.get(elementRef).remove(linePoint);
		if (elementRefToLinePoints.get(elementRef).size() == 0) {
			elementRefToLinePoints.remove(elementRef);
		}
	}

	// ================================================================================
	// AliasRefToAliases Map Methods
	// ================================================================================

	/**
	 * Returns the set of Group aliasRef keys for this pathway model. A Group
	 * aliasRef can have or more DataNode aliases.
	 *
	 * @return the group aliasRef keys for this pathway model
	 */
	public Set<Group> getAliasRefs() {
		return aliasRefToAliases.keySet();
	}

	/**
	 * Returns the set of DataNode aliases for a Group aliasRef. When a DataNode has
	 * type="alias" it may be an alias for a Group pathway element. To get aliasRef
	 * for a dataNode use {@link DataNode#getAliasRef()}.
	 *
	 * @param aliasRef the group which has datanode aliases.
	 * @return the datanode aliases for the group aliasRef.
	 */
	public Set<DataNode> getLinkedAliases(Group aliasRef) {
		return aliasRefToAliases.get(aliasRef);
	}

	/**
	 * Returns true if pathway model has Group aliasRef.
	 *
	 * @param aliasRef the group.
	 * @return true if pathway model has aliasRef.
	 */
	public boolean hasAliasRef(Group aliasRef) {
		return aliasRefToAliases.containsKey(aliasRef);
	}

	/**
	 * Returns true if pathway model has DataNode alias and Group aliasRef.
	 *
	 * @param aliasRef the group.
	 * @param alias    the alias datanode.
	 * @return true if pathway model has alias and aliasRef.
	 */
	protected boolean hasLinkedAlias(Group aliasRef, DataNode alias) {
		return aliasRefToAliases.get(aliasRef).contains(alias);
	}

	/**
	 * Adds mapping of aliasRef to data node alias in the aliasRefToAliases hash
	 * map.
	 *
	 * NB: This method is not used directly.
	 * <ol>
	 * <li>It is called from {@link DataNode#setAliasRef}.
	 * </ol>
	 *
	 * @param aliasRef the group for which a dataNode alias refers.
	 * @param alias    the datanode which has an aliasRef.
	 * @throws IllegalArgumentException if elementRef or dataNode are null.
	 */
	protected void linkAlias(Group aliasRef, DataNode alias) {
		if (aliasRef == null || alias == null)
			throw new IllegalArgumentException("AliasRef and alias must be valid.");
		Set<DataNode> aliases = aliasRefToAliases.get(aliasRef);
		if (aliases == null) {
			aliases = new HashSet<DataNode>();
			aliasRefToAliases.put(aliasRef, aliases);
		}
		aliases.add(alias);
	}

	/**
	 * Removes the link between given aliasRef and alias, and removes mapping from
	 * aliasRefToAliases of this pathway model.
	 *
	 * <p>
	 * NB: This method is not used directly.
	 * <ol>
	 * <li>It is called from {@link DataNode#unsetAliasRef}.
	 * </ol>
	 *
	 * @param aliasRef the group for which a dataNode alias refers.
	 * @param alias    the datanode which has an aliasRef.
	 */
	protected void unlinkAlias(Group aliasRef, DataNode alias) {
		if (alias == null || aliasRef == null) {
			throw new IllegalArgumentException("AliasRef and alias must be valid.");
		}
		assert (alias.getAliasRef() == aliasRef);
		assert (hasLinkedAlias(aliasRef, alias));
		Set<DataNode> aliases = aliasRefToAliases.get(aliasRef);
		aliases.remove(alias);
		// removes aliasRef if it has no aliases
		if (aliases.isEmpty())
			removeAliasRef(aliasRef);
	}

	/**
	 * Removes the mapping of given elementRef key from the elementRefToDataNode
	 * hash map.
	 *
	 * @param aliasRef the aliasRef key.
	 */
	protected void removeAliasRef(Group aliasRef) {
		if (hasAliasRef(aliasRef)) {
			Set<DataNode> aliases = aliasRefToAliases.get(aliasRef);
			for (DataNode alias : aliases) {
				alias.unsetAliasRef();
			}
			aliasRefToAliases.remove(aliasRef);
		}
	}

	// ================================================================================
	// PathwayElement List Methods
	// ================================================================================
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
		labels.add(label);
		addPathwayObject(label);
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
	 * Also removes group from aliasRefToAliases if applicable.
	 *
	 * @param group the group to be removed.
	 */
	public void removeGroup(Group group) {
		groups.remove(group);
		removePathwayObject(group);
	}

	// ================================================================================
	// Annotation, Citation, Evidence List Methods
	// ================================================================================
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
	protected Annotation addAnnotation(Annotation annotation) {
		Annotation annotationExisting = hasEqualAnnotation(annotation);
		if (annotationExisting != null) {
			Logger.log.trace("Annotation not added, information equivalent to " + annotationExisting.getElementId());
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
	private Annotation hasEqualAnnotation(Annotation annotation) {
		for (Annotation annotationExisting : annotations) {
			if (annotation.equalsAnnotation(annotationExisting)) {
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
	protected Citation addCitation(Citation citation) {
		if (citation != null) {
			Citation citationExisting = hasEqualCitation(citation);
			if (citationExisting != null) {
				Logger.log.trace("Citation not added, information equivalent to " + citationExisting.getElementId());
				return citationExisting;
			} else {
				addPathwayObject(citation);
				citations.add(citation);
				return citation;
			}
		} else {
			return null;
		}
	}

	/**
	 * Checks if given citation already exists for the pathway model.
	 *
	 * @param citation the given citation to be checked.
	 * @return citationExisting the existing equivalent citation, or null if no
	 *         equivalent citation exists for given citation.
	 */
	private Citation hasEqualCitation(Citation citation) {
		for (Citation citationExisting : citations) {
			if (citation.equalsCitation(citationExisting)) {
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
		citations.remove(citation);
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
	protected Evidence addEvidence(Evidence evidence) {
		Evidence evidenceExisting = hasEqualEvidence(evidence);
		if (evidenceExisting != null) {
			Logger.log.trace("Evidence not added, information equivalent to " + evidenceExisting.getElementId());
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
	private Evidence hasEqualEvidence(Evidence evidence) {
		for (Evidence evidenceExisting : evidences) {
			if (evidence.equalsEvidence(evidenceExisting)) {
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

	// ================================================================================
	// General PathwayObject Add/Remove Methods
	// ================================================================================

	/**
	 * Adds the given pathway object to pathway model. Sets pathwayModel for the
	 * given pathway object. Sets an unique elementId for given pathway object if
	 * not already set. Corresponding elementId and given pathway object are added
	 * to elementIdToPathwayObject map.
	 *
	 * Fires PathwayEvent.ADDED event <i>after</i> addition of the object
	 *
	 * @param o the pathway object to add.
	 */
	protected void addPathwayObject(PathwayObject o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot add invalid pathway object to pathway model");
		}
		String elementId = o.getElementId();
		// if pathway object already has elementId (from reading), it must be unique
		if (elementId != null && elementIdToPathwayObject.containsKey(elementId)) {
			throw new IllegalArgumentException("id '" + elementId + "' is not unique");
		}
		// set pathway model
		o.setPathwayModelTo(this);
		if (o.pathwayModel != this) {
			throw new IllegalArgumentException("Pathway object does not refer to this pathway model");
		}
		// if pathway object does not yet have id, set a unique elementId
		if (elementId == null) {
			elementId = o.setGeneratedElementId();
		}
		addElementId(elementId, o);
		fireObjectModifiedEvent(new PathwayModelEvent(o, PathwayModelEvent.ADDED));
		checkMBoardSize(o);
	}

	/**
	 * Removes the given pathway object from pathway model and
	 * elementIdToPathwayObject map. The pathway object is terminated in the
	 * process.
	 *
	 * Sets parent of object to null and removed elementId <i>before</i> removal of
	 * the object. Fires PathwayEvent.DELETED event <i>after</i> removal of the
	 * object
	 *
	 * @param o the pathway object to remove.
	 */
	protected void removePathwayObject(PathwayObject o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot remove invalid pathway object");
		}
		if (!hasPathwayObject(o)) {
			throw new IllegalArgumentException("Pathway model does not have this pathway object");
		}
		removeElementId(o.getElementId());
		o.terminate();
		fireObjectModifiedEvent(new PathwayModelEvent(o, PathwayModelEvent.DELETED));
	}

	/**
	 * Adds a PathwayObject to this Pathway. Calls the appropriate add method based
	 * on PathwayObject class.
	 *
	 * @param o the pathway object to add
	 */
	public void add(PathwayObject o) {
		assert (o != null);
		switch (o.getObjectType()) {
		case PATHWAY:
			replacePathway((Pathway) o);
			break;
		case DATANODE:
			addDataNode((DataNode) o);
			break;
		case INTERACTION:
			addInteraction((Interaction) o);
			break;
		case GRAPHLINE:
			addGraphicalLine((GraphicalLine) o);
			break;
		case LABEL:
			addLabel((Label) o);
			break;
		case SHAPE:
			addShape((Shape) o);
			break;
		case GROUP:
			addGroup((Group) o);
			break;
		case ANNOTATION:
			addAnnotation((Annotation) o);
			break;
		case CITATION:
			addCitation((Citation) o);
			break;
		case EVIDENCE:
			addEvidence((Evidence) o);
			break;
		default:
			// nothing
		}
	}

	/**
	 * Removes a PathwayObject from this Pathway. Calls the appropriate remove
	 * method based on PathwayObject class.
	 *
	 * @param o the pathway object to remove
	 */
	public void remove(PathwayObject o) {
		assert (o.pathwayModel == this);
		switch (o.getObjectType()) {
		case DATANODE:
			removeDataNode((DataNode) o);
			break;
		case STATE: // states are removed from datanodes, not pathway model
			((State) o).getDataNode().removeState((State) o);
			break;
		case INTERACTION:
			removeInteraction((Interaction) o);
			break;
		case GRAPHLINE:
			removeGraphicalLine((GraphicalLine) o);
			break;
		case LABEL:
			removeLabel((Label) o);
			break;
		case SHAPE:
			removeShape((Shape) o);
			break;
		case GROUP:
			removeGroup((Group) o);
			break;
		case ANNOTATION:
			removeAnnotation((Annotation) o);
			break;
		case CITATION:
			removeCitation((Citation) o);
			break;
		case EVIDENCE:
			removeEvidence((Evidence) o);
			break;
		default:
			// nothing
		}
	}

	// ================================================================================
	// Xref Methods
	// ================================================================================
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

	// ================================================================================
	// Clone Methods
	// ================================================================================
	/**
	 * Clones this pathway model.
	 * 
	 * @return the clone of this pathway model.
	 */
	@Override
	public PathwayModel clone() {
		PathwayModel result = new PathwayModel();
		BidiMap<PathwayObject, PathwayObject> newToSource = new DualHashBidiMap<>();
		for (PathwayElement e : getPathwayElements()) {
			CopyElement copyElement = e.copy();
			PathwayElement newElement = copyElement.getNewElement();
			PathwayElement srcElement = copyElement.getSourceElement();
			result.add(newElement);
			// load references
			newElement.copyReferencesFrom(srcElement);
			// store information
			newToSource.put(newElement, srcElement);
			// specially store anchor information
			if (newElement instanceof LineElement) {
				Iterator<Anchor> it1 = ((LineElement) newElement).getAnchors().iterator();
				Iterator<Anchor> it2 = ((LineElement) srcElement).getAnchors().iterator();
				while (it1.hasNext() && it2.hasNext()) {
					Anchor na = it1.next();
					Anchor sa = it2.next();
					if (na != null && sa != null) {
						newToSource.put(na, sa);
					}
				}
			}
		}
		// add group members in new Group
		for (Group g : result.getGroups()) {
			Group src = (Group) newToSource.get(g);
			for (Groupable srcMember : src.getPathwayElements()) {
				Groupable newMember = (Groupable) newToSource.getKey(srcMember);
				if (newMember != null) {
					g.addPathwayElement(newMember);
				}
			}
			g.updateDimensions();
		}
		// set aliasRef if any
		for (Group g : getAliasRefs()) {
			for (DataNode d : getLinkedAliases(g)) {
				DataNode newAlias = (DataNode) newToSource.getKey(d);
				Group newAliasRef = (Group) newToSource.getKey(g);
				if (newAlias != null && newAliasRef != null) {
					newAlias.setAliasRef(newAliasRef);
				}
			}
		}
		// link LineElement LinePoint elementRefs
		for (LineElement l : result.getLineElements()) {
			LineElement src = (LineElement) newToSource.get(l);
			// set start elementRef
			LinkableTo srcStartElementRef = src.getStartElementRef();
			if (srcStartElementRef != null) {
				LinkableTo newStartElementRef = (LinkableTo) newToSource.getKey(srcStartElementRef);
				if (newStartElementRef != null) {
					LinePoint startPoint = l.getStartLinePoint();
					LinePoint srcPoint = src.getStartLinePoint();
					startPoint.linkTo(newStartElementRef, srcPoint.getRelX(), srcPoint.getRelY());
				}
			}
			// set end elementRef
			LinkableTo srcEndElementRef = src.getEndElementRef();
			if (srcEndElementRef != null) {
				LinkableTo newEndElementRef = (LinkableTo) newToSource.getKey(srcEndElementRef);
				if (newEndElementRef != null) {
					LinePoint endPoint = l.getEndLinePoint();
					LinePoint srcPoint = src.getEndLinePoint();
					endPoint.linkTo(newEndElementRef, srcPoint.getRelX(), srcPoint.getRelY());
				}
			}
		}
		// refresh connector shapes
		for (LineElement o : result.getLineElements()) {
			o.getConnectorShape().recalculateShape(o);
		}
		result.changed = changed;
		if (sourceFile != null) {
			result.sourceFile = new File(sourceFile.getAbsolutePath());
		}
		// do not copy status flag listeners
//		for(StatusFlagListener l : statusFlagListeners) {
//			result.addStatusFlagListener(l);
//		}
		return result;
	}

	// ================================================================================
	// Read Write Methods
	// ================================================================================
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
		GPMLFormat gpmlFormat = new GPMLFormat(GPMLFormat.CURRENT);
		gpmlFormat.writeToXml(this, file, validate);
		setSourceFile(file);
		clearChangedFlag();

	}

	public void readFromXml(Reader in, boolean validate) throws ConverterException {
		GPMLFormat.readFromXml(this, in, validate);
		setSourceFile(null);
		clearChangedFlag();
	}

	public void readFromXml(InputStream in, boolean validate) throws ConverterException {
		GPMLFormat.readFromXml(this, in, validate);
		setSourceFile(null);
		clearChangedFlag();
	}

	public void readFromXml(File file, boolean validate) throws ConverterException {
		Logger.log.info("Start reading the XML file: " + file);
		GPMLFormat.readFromXml(this, file, validate);
		setSourceFile(file);
		clearChangedFlag();
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

	// ================================================================================
	// FireEvent and Listener Methods
	// ================================================================================
	private boolean changed = true;

	/**
	 * The "changed" flag tracks if the Pathway has been changed since the file was
	 * opened or last saved. New pathways start changed.
	 */
	public boolean hasChanged() {
		return changed;
	}

	/**
	 * ClearChangedFlag should be called after when the current pathway is known to
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
	 * 
	 * @param e the pathway object event.
	 */
	void childModified(PathwayObjectEvent e) {
		markChanged();
		if (e.isCoordinateChange()) {
			PathwayObject elt = e.getModifiedPathwayObject();
			if (elt instanceof LinkableTo) {
				for (LinkableFrom refc : getReferringLinkableFroms((LinkableTo) elt)) {
					refc.refeeChanged();
				}
			}
			if (elt instanceof DataNode) {
				for (State st : ((DataNode) elt).getStates()) {
					st.coordinatesChanged();
				}
			} else if (elt instanceof State) {
				((State) elt).coordinatesChanged();
			}
			if (elt instanceof Groupable) {
				Group group = ((Groupable) elt).getGroupRef();
				if (group != null) {
					// identify group object and notify model change to trigger view update
					group.fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(group));
				}
			}
			checkMBoardSize(e.getModifiedPathwayObject());
		}
	}

	/**
	 * Checks whether the board size is still large enough for the given
	 * {@link PathwayElement} and increases the size if not
	 *
	 * @param e The element to check the board size for
	 */
	protected void checkMBoardSize(PathwayObject e) {
		final int BORDER_SIZE = 30;
		double mw = getPathway().getBoardWidth();
		double mh = getPathway().getBoardHeight();
		if (e instanceof LineElement) {
			mw = Math.max(mw, BORDER_SIZE
					+ Math.max(((LineElement) e).getStartLinePointX(), ((LineElement) e).getEndLinePointX()));
			mh = Math.max(mh, BORDER_SIZE
					+ Math.max(((LineElement) e).getStartLinePointY(), ((LineElement) e).getEndLinePointY()));
		} else if (e instanceof ShapedElement) {
			mw = Math.max(mw, BORDER_SIZE + ((ShapedElement) e).getLeft() + ((ShapedElement) e).getWidth());
			mh = Math.max(mh, BORDER_SIZE + ((ShapedElement) e).getTop() + ((ShapedElement) e).getHeight());
		}
		if (Math.abs(getPathway().getBoardWidth() - mw) + Math.abs(getPathway().getBoardHeight() - mh) > 0.01) {
			getPathway().setBoardWidth(mw);
			getPathway().setBoardHeight(mh);
			fireObjectModifiedEvent(new PathwayModelEvent(getPathway(), PathwayModelEvent.RESIZED));
		}
	}

	/**
	 * Implement this interface if you want to be notified when the "changed" status
	 * changes. This happens e.g. when the user makes a change to an unchanged
	 * pathway, or when a changed pathway is saved.
	 * 
	 * @author unknown
	 */
	public interface StatusFlagListener extends EventListener {
		public void statusFlagChanged(StatusFlagEvent e);
	}

	/**
	 * Event for a change in the "changed" status of this Pathway
	 * 
	 * @author unknown
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
	 * Registers a status flag listener
	 * 
	 * @param v the given status flag listener to add.
	 */
	public void addStatusFlagListener(StatusFlagListener v) {
		if (!statusFlagListeners.contains(v))
			statusFlagListeners.add(v);
	}

	/**
	 * Removes a status flag listener
	 * 
	 * @param v the given status flag listener to remove.
	 */
	public void removeStatusFlagListener(StatusFlagListener v) {
		statusFlagListeners.remove(v);
	}

	/**
	 * Fires status flag event. TODO make private if possible
	 * 
	 * @param e the status flag event.
	 */
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

	private List<PathwayModelListener> listeners = new ArrayList<PathwayModelListener>();

	/**
	 * Adds listener to this pathway model.
	 * 
	 * @param v the pathway model listener to add.
	 */
	public void addListener(PathwayModelListener v) {
		if (!listeners.contains(v))
			listeners.add(v);
	}

	/**
	 * Removes listener from this pathway model.
	 * 
	 * @param v the pathway model listener to removed.
	 */
	public void removeListener(PathwayModelListener v) {
		listeners.remove(v);
	}

	/**
	 * Firing the ObjectModifiedEvent has the side effect of marking the Pathway as
	 * changed.
	 * 
	 * @param e the pathway model event.
	 */
	public void fireObjectModifiedEvent(PathwayModelEvent e) {
		markChanged();
		for (PathwayModelListener g : listeners) {
			g.pathwayModified(e);
		}
	}

	// ================================================================================
	// Helper Methods
	// ================================================================================
	/**
	 * Prints a summary of this pathway model.
	 * 
	 * @return the string summary of this pathway model.
	 */
	public String summary() {
		String result = "    " + toString() + "\n    with Objects:";
		for (PathwayObject pe : getPathwayObjects()) {
			String code = pe.toString();
			code = code.substring(code.lastIndexOf('@'), code.length() - 1);
			result += "\n      " + code + " " + pe.getClass().getSimpleName() + " " + pe.pathwayModel;
		}
		return result;
	}

}