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
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.bridgedb.Xref;
import org.pathvisio.debug.*;
import org.pathvisio.io.*;
import java.io.Reader;

import org.pathvisio.model.element.*;
import org.pathvisio.model.graphics.Coordinate;
import org.pathvisio.model.ref.Annotation;
import org.pathvisio.model.ref.Citation;
import org.pathvisio.model.ref.CitationRef;
import org.pathvisio.model.ref.Evidence;

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

	/**
	 * Initializes a pathway model object with {@link Pathway} information.
	 * 
	 * @param pathway the pathway object containing metadata information, e.g.
	 *                title, organism...
	 */
	public PathwayModel(Pathway pathway) {
		this.pathway = pathway;
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
	 * Initializes a pathway model object with {@link Pathway} default values.
	 */
	public PathwayModel() {
		this(new Pathway.PathwayBuilder("Click to add title", 0, 0, Color.decode("#ffffff"), new Coordinate(0, 0))
				.build());
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
	 * Returns a unique elementId.
	 * 
	 * @return a unique elementId.
	 */
	public String getUniqueElementId() {
		return getUniqueId(elementIdToPathwayElement.keySet());
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
	 * Returns all PathwayElement for the pathway model.
	 * 
	 * @param elementId the given elementId key.
	 * @return the PathwayElement for the given elementId key.
	 */
	public List<PathwayElement> getPathwayElements() {
		List<PathwayElement> pathwayElements = new ArrayList<>(elementIdToPathwayElement.values());
		return pathwayElements;
	}

	/**
	 * Checks if the pathway model has the given pathway element.
	 * 
	 * @param pathwayElement the pathway element to check for.
	 * @return true if pathway model has given pathway element, false otherwise.
	 */
	public boolean hasPathwayElement(PathwayElement pathwayElement) {
		return this.getPathwayElements().contains(pathwayElement);
	}

	/**
	 * Returns a list view of String elementId keys from the
	 * elementIdToPathwayElements hash map.
	 * 
	 * @return a list of elementId keys.
	 */
	public List<String> getElementIds() {
		List<String> elementIds = new ArrayList<>(elementIdToPathwayElement.keySet());
		return elementIds;
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
			System.out.println(elementIdToPathwayElement);
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
	 * Adds given annotation to annotations list. If there is an annotation with
	 * equivalent properties in the pathway model, the given annotation is not added
	 * and the equivalent annotation is returned.
	 * 
	 * @param annotation the new annotation to be added.
	 * @return annotation the new annotation or annotationExisting the existing
	 *         equivalent annotation.
	 */
	public Annotation addAnnotation(Annotation annotation) {
		Annotation annotationExisting = annotationExists(annotation);
		if (annotationExisting != null) {
			Logger.log.trace("New annotation not added to pathway model.");
			return annotationExisting;
		} else {
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
				Logger.log.trace("This annotation is equivalent to existing pathway model annotation "
						+ annotationExisting.getElementId() + ".");
				return annotationExisting;
			}
		}
		return null;
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
	 * Adds given citation to citations list. If there is an citation with
	 * equivalent properties in the pathway model, the given citation is not added
	 * and the equivalent citation is returned.
	 * 
	 * @param citation the new citation to be added.
	 * @return citation the new citation or citationExisting the existing equivalent
	 *         citation.
	 */
	public Citation addCitation(Citation citation) {
		Citation citationExisting = hasEqualCitation(citation);
		if (citationExisting != null) {
			Logger.log.trace("New citation not added to pathway model.");
			return citationExisting;
		} else {
			assert (citation.getPathwayModel() == this); // TODO
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
				Logger.log.trace("This citation is equivalent to existing pathway model citation "
						+ citationExisting.getElementId() + ".");
				return citationExisting;
			}
		}
		return null;
	}

	/**
	 * Removes given citation from citation=s list.
	 * 
	 * @param citation the citation to be removed.
	 */
	public void removeCitation(Citation citation) {
		removePathwayElement(citation); // TODO?
		citation.removeCitationRefs();
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
		removeElementInfoRefs(dataNode);
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
		removeElementInfoRefs(interaction);
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
		removeElementInfoRefs(graphicalLine);
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
		removeElementInfoRefs(label);
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
		removeElementInfoRefs(shape);
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
		removeElementInfoRefs(group);
		groups.remove(group);
	}

	/**
	 * Adds pathway element to the pathway model. TODO
	 * 
	 * @param pathwayElement the pathway element to add.
	 */
	public void addPathwayElement(PathwayElement pathwayElement) {
		PathwayModel pathwayModel = pathwayElement.getPathwayModel();
		if (pathwayModel != null && pathwayModel != this) {
			pathwayModel.removePathwayElement(pathwayElement); //TODO
			pathwayElement.setPathwayModel(this);
		}
		if(pathwayElement.getClass() == DataNode.class) {
			dataNodes.add((DataNode) pathwayElement);
		}
		assert (pathwayElement != null) && (pathwayElement.getPathwayModel() == this); //TODO
		assert (hasPathwayElement(pathwayElement)); //TODO
	}

	/**
	 * Removes pathway element from the pathway model. TODO
	 * 
	 * @param pathwayElement the pathway element to remove.
	 */
	public void removePathwayElement(PathwayElement pathwayElement) {
		assert (pathwayElement != null) && (pathwayElement.getPathwayModel() == this);
		assert (hasPathwayElement(pathwayElement));
		pathwayElement.setPathwayModel(null);
		
		if(pathwayElement.getClass() == DataNode.class) {
			dataNodes.remove(pathwayElement);
		}

	}

	/**
	 * Removes references to {@link AnnotationRef}, {@link CitationRef}, and
	 * {@link EvidenceRef} of a given pathway element {@link ElementInfo}.
	 * References to annotationRefs, citatationRefs, and evidenceRefs are also
	 * removed from their sources {@link Annotation}, {@link Citation}, and
	 * {@link Evidence}.
	 * 
	 * @param elementInfo the pathway element for which references are to be
	 *                    removed.
	 */
	public void removeElementInfoRefs(ElementInfo elementInfo) {
		removePathwayElement(elementInfo);
		elementInfo.getAnnotationRefs();
		elementInfo.getCitationRefs();
		
		
//		elementInfo.getEvidenceRefs(); TODO


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

//	/**
//	 * Contructor for this class, creates a new gpml document
//	 */
//	public Pathway()
//	{
//		mappInfo = PathwayElement.createPathwayElement(ObjectType.MAPPINFO);
//		this.add (mappInfo);
//		infoBox = PathwayElement.createPathwayElement(ObjectType.INFOBOX);
//		this.add (infoBox);
//	}
//
//	/*
//	 * Call when making a new mapp.
//	 */
//	public void initMappInfo()
//	{
//		String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
//		mappInfo.setVersion(dateString);
//		mappInfo.setMapInfoName("New Pathway");
//	}

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
//	/**
//	 * Implement this interface if you want to be notified when the "changed" status changes.
//	 * This happens e.g. when the user makes a change to an unchanged pathway,
//	 * or when a changed pathway is saved.
//	 */
//	public interface StatusFlagListener extends EventListener
//	{
//		public void statusFlagChanged (StatusFlagEvent e);
//	}
//
//	/**
//	 * Event for a change in the "changed" status of this Pathway
//	 */
//	public static class StatusFlagEvent
//	{
//		private boolean newStatus;
//		public StatusFlagEvent (boolean newStatus) { this.newStatus = newStatus; }
//		public boolean getNewStatus() {
//			return newStatus;
//		}
//	}
//
//	private List<StatusFlagListener> statusFlagListeners = new ArrayList<StatusFlagListener>();
//
//	/**
//	 * Register a status flag listener
//	 */
//	public void addStatusFlagListener (StatusFlagListener v)
//	{
//		if (!statusFlagListeners.contains(v)) statusFlagListeners.add(v);
//	}
//
//	/**
//	 * Remove a status flag listener
//	 */
//	public void removeStatusFlagListener (StatusFlagListener v)
//	{
//		statusFlagListeners.remove(v);
//	}
//
//	//TODO: make private
//	public void fireStatusFlagEvent(StatusFlagEvent e)
//	{
//		for (StatusFlagListener g : statusFlagListeners)
//		{
//			g.statusFlagChanged (e);
//		}
//	}
//
//	private List<PathwayListener> listeners = new ArrayList<PathwayListener>();
//
//	public void addListener(PathwayListener v)
//	{
//		if(!listeners.contains(v)) listeners.add(v);
//	}
//
//	public void removeListener(PathwayListener v) { listeners.remove(v); }
//
//    /**
//	   Firing the ObjectModifiedEvent has the side effect of
//	   marking the Pathway as changed.
//	 */
//	public void fireObjectModifiedEvent(PathwayEvent e)
//	{
//		markChanged();
//		for (PathwayListener g : listeners)
//		{
//			g.pathwayModified(e);
//		}
//	}

}