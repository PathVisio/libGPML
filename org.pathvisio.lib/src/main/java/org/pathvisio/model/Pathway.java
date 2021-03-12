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
import org.pathvisio.model.ElementLink.ElementIdContainer;
import org.pathvisio.model.ElementLink.ElementRefContainer;
import org.pathvisio.util.Utils;

/**
 * This class stores all information relevant to a Pathway. Pathway contains
 * pathway elements and properties.
 * 
 * TODO: MOVE: It is responsible for storing all information necessary for
 * maintaining, loading and saving pathway data.
 * 
 * @author unknown, finterly
 */
public class Pathway {

	protected String title = "untitled";
	protected String organism = null;
	protected String source = null;
	protected String version = null;
	protected Xref xref;
	protected List<Author> authors = new ArrayList<Author>(); // length 0 to unbounded

	protected List<Annotation> annotations;
	protected List<Citation> citations;
	protected List<Evidence> evidences;

	protected List<Comment> comments = new ArrayList<Comment>(); // length 0 to unbounded
	protected List<DynamicProperty> dynamicProperties = new ArrayList<DynamicProperty>(); // length 0 to unbounded
	protected List<AnnotationRef> annotationRefs = new ArrayList<AnnotationRef>(); // length 0 to unbounded
	protected List<CitationRef> citationRefs = new ArrayList<CitationRef>(); // length 0 to unbounded
	protected List<EvidenceRef> evidenceRefs = new ArrayList<EvidenceRef>(); // length 0 to unbounded
	protected double boardWidth;
	protected double boardHeight;

	protected List<DataNode> dataNodes;
	protected List<State> states;
	protected List<Interaction> interactions;
	protected List<GraphicalLine> graphicalLines;
	protected List<Label> labels;
	protected List<Shape> shapes;
	protected List<Group> groups;
	protected InfoBox infoBox;

	/* -------------------------- ELEMENTID & ELEMENTREF --------------------------- */

	/** Mapping of String elementId key to ElementIdContainer value. */
	private Map<String, ElementIdContainer> elementIdToContainer = new HashMap<String, ElementIdContainer>();
	/** Mapping of String elementId key to Set of ElementRefContainer value. */
	private Map<String, Set<ElementRefContainer>> elementIdToSetRefContainer = new HashMap<String, Set<ElementRefContainer>>();

	/**
	 * Returns a set view of String elementId keys from the elementIdToContainer
	 * hash map.
	 * 
	 * @return a set of elementId keys.
	 */
	public Set<String> getElementIds() {
		return elementIdToContainer.keySet();
	}

	/**
	 * Returns ElementIdcontainer for the given String elementId key.
	 * 
	 * @param elementId the given elementId key.
	 * @return the ElementIdcontainer for the given elementId key.
	 */
	public ElementIdContainer getElementIdContainer(String elementId) {
		return elementIdToContainer.get(elementId);
	}

	/**
	 * Registers a link from an elementId to an ElementIdContainer. Inserts a
	 * mapping of elementId key and ElementIdContainer value to the
	 * elementIdToContainer hash map.
	 * 
	 * @param elementId          the elementId
	 * @param elementIdContainer the ElementIdContainer
	 * @throws IllegalArgumentException if elementId or elementIdContainer are null.
	 * @throws IllegalArgumentException if elementId is not unique.
	 */
	public void addElementId(String elementId, ElementIdContainer elementIdContainer) {
		if (elementIdContainer == null || elementId == null) {
			throw new IllegalArgumentException("unique elementId can't be null");
		}
		if (elementIdToContainer.containsKey(elementId)) {
			throw new IllegalArgumentException("elementId '" + elementId + "' is not unique");
		}
		elementIdToContainer.put(elementId, elementIdContainer);
	}

	/**
	 * Removes the mapping of given elementId key from the elementIdToContainer hash
	 * map.
	 * 
	 * @param elementId the elementId key.
	 */
	void removeElementId(String elementId) {
		elementIdToContainer.remove(elementId);
	}

	/**
	 * Returns a set of ElementRefContainers that refer to an object with a
	 * particular elementId.
	 * 
	 * @param elementId the elementId key.
	 */
	public Set<ElementRefContainer> getReferringObjects(String elementId) {
		Set<ElementRefContainer> elementRefContainers = elementIdToSetRefContainer.get(elementId);
		if (elementRefContainers != null) {
			// create defensive copy to prevent problems with ConcurrentModification.
			return new HashSet<ElementRefContainer>(elementRefContainers);
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Registers a link from an elementId to an ElementRefContainer. Inserts a
	 * mapping of elementId key and ElementRefContainer value to the
	 * elementIdToSetRefContainer hash map.
	 * 
	 * @param elementId           the elementId.
	 * @param elementRefContainer the target ElementRefContainer.
	 */
	public void addElementRef(String elementId, ElementRefContainer elementRefContainer) {
		Utils.multimapPut(elementIdToSetRefContainer, elementId, elementRefContainer);
	}

	/**
	 * Removes a reference to an elementId by removing the mapping of the given
	 * elementId key from the elementIdToSetRefContainer hash map.
	 * 
	 * @param elementId           the elementId.
	 * @param elementRefContainer the target ElementRefContainer.
	 * @throws IllegalArgumentException if hash map does not contain the given
	 *                                  elementId key.
	 */
	void removeElementRef(String elementId, ElementRefContainer elementRefContainer) {
		if (!elementIdToSetRefContainer.containsKey(elementId)) {
			throw new IllegalArgumentException();
		} else {
			elementIdToSetRefContainer.get(elementId).remove(elementRefContainer);
			// remove elementId key if zero ElementRefContainers values.
			if (elementIdToSetRefContainer.get(elementId).size() == 0)
				elementIdToSetRefContainer.remove(elementId);
		}
	}

	/* ------------------------------- GROUPID ------------------------------- */

	private Map<String, PathwayElement> groupIdToPathwayElement = new HashMap<String, PathwayElement>();
	private Map<String, Set<PathwayElement>> groupIdToSetPathwayElement = new HashMap<String, Set<PathwayElement>>();

	/**
	 * Returns a set view of String groupId keys from the groupIdToPathwayElement
	 * hash map.
	 * 
	 * @return set of groupId key.
	 */
	public Set<String> getGroupIds() {
		return groupIdToPathwayElement.keySet();
	}
	

	/**
	 * Returns a PathwayElement for the given String groupId key.
	 * 
	 * @param groupId the given groupId key.
	 * @return the PathwayElement for the given groupId.
	 */
	public PathwayElement getGroupById(String groupId) {
		return groupIdToPathwayElement.get(groupId);
	}

	/**
	 * Registers a link from a group id to a PathwayElement. Inserts a mapping of
	 * groupId key and PathwayElement to the groupIdToPathwayElement hash map.
	 * 
	 * @param groupId        the groupId.
	 * @param pathwayElement the pathway element.
	 */
	void addGroupId(String groupId, PathwayElement pathwayElement) {
		if (groupId == null) {
			throw new IllegalArgumentException("unique groupId can't be null");
		}
		if (groupIdToPathwayElement.containsKey(groupId)) {
			throw new IllegalArgumentException("groupId '" + groupId + "' is not unique");
		}
		groupIdToPathwayElement.put(groupId, pathwayElement);
	}

	/**
	 * Removes a reference to a groupId by removing the mapping of the given groupId
	 * key from the groupIdToPathwayElement hash map.
	 * 
	 * @param groupId the group id.
	 */
	void removeGroupId(String groupId) {
		groupIdToPathwayElement.remove(groupId);
		Set<PathwayElement> pathwayElements = groupIdToSetPathwayElement.get(groupId);
		if (pathwayElements != null)
			for (PathwayElement pathwayElement : pathwayElements) {
				pathwayElement.groupRef = null;
			}
		groupIdToSetPathwayElement.remove(groupId);
	}


	/* ------------------------------- GROUPREF ------------------------------- */

	/**
	 * Adds a groupref to child pathway element.
	 * 
	 * @param ref   the groupref.
	 * @param child the child pathway element.
	 */
	void addGroupRef(String ref, PathwayElement child) {
		Utils.multimapPut(groupIdToSetPathwayElement, ref, child);
	}

	/**
	 * Removes a groupref from child pathway element.
	 * 
	 * @param ref   the groupref.
	 * @param child the child pathway element.
	 */
	void removeGroupRef(String id, PathwayElement child) {
		if (!groupIdToSetPathwayElement.containsKey(id))
			throw new IllegalArgumentException();

		groupIdToSetPathwayElement.get(id).remove(child);

		// Find out if this element is the last one in a group
		// If so, remove the group as well
		if (groupIdToSetPathwayElement.get(id).size() == 0) {
			groupIdToSetPathwayElement.remove(id);
			PathwayElement group = getGroupById(id);
			if (group != null)
				forceRemove(group);
		} else {
			// redraw group outline
			if (getGroupById(id) != null) {
				Group group = (Group) getGroupById(id);
			}
		}
	}

	/**
	 * Gets the pathway elements that are part of the given group.
	 * 
	 * @param id the id of the group.
	 * @return the set of pathway elements part of the group.
	 */
	public Set<PathwayElement> getGroupElements(String id) {
		Set<PathwayElement> result = groupIdToSetPathwayElement.get(id);
		// Return an empty set if the group is empty
		return result == null ? new HashSet<PathwayElement>() : result;
	}

	/**
	 * Gets the unique graphId.
	 * 
	 * @return unique graph id.
	 */
	public String getUniqueElementId() {
		return ElementLink.getUniqueId(elementIdToContainer.keySet());
	}

	/**
	 * Gets the unique groupId.
	 * 
	 * @return unique group id.
	 */
	public String getUniqueGroupId() {
		return ElementLink.getUniqueId(groupIdToSetPathwayElement.keySet());
	}

	// ------------------------------- ID -------------------------------------

	/**
	 * 
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		if (title == null) {
			throw new IllegalArgumentException();
		} else
			this.title = title;
	}

	/**
	 * 
	 * @return
	 */
	public String getOrganism() {
		return organism;
	}

	/**
	 * 
	 * @param v
	 */
	public void setOrganism(String organism) {
		if (organism == null) {
			throw new IllegalArgumentException();
		} else
			this.organism = organism;
	}

	/**
	 * 
	 * @return
	 */
	public String getSource() {
		return source;
	}

	/**
	 * 
	 * @param v
	 */
	public void setSource(String source) {
		if (source == null) {
			throw new IllegalArgumentException();
		} else
			this.source = source;
	}

	/**
	 * 
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 
	 * @param v
	 */
	public void setVersion(String version) {
		if (version == null) {
			throw new IllegalArgumentException();
		} else
			this.version = version;
	}

	/**
	 * 
	 * @return
	 */
	public List<Author> getAuthor() {
		return authors;
	}

	/**
	 * 
	 * @param v
	 */
	public void setAuthor(List<Author> author) {
		this.authors = authors;
	}

	/**
	 * Gets the board width. Board width together with board height define drawing
	 * size.
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
	public double setBoardWidth() {
		if (boardWidth < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + boardWidth);
		} else {
			this.boardWidth = boardWidth;
		}
	}

	/**
	 * Gets the board height. Board width together with board height define drawing
	 * size.
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
	public double setBoardHeight() {
		if (boardHeight < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + boardHeight);
		} else {
			this.boardHeight = boardHeight;
		}
	}

	/**
	 * List of contained dataObjects.
	 */
	private List<PathwayElement> dataObjects = new ArrayList<PathwayElement>();

	/**
	 * Gets dataObjects contained. There is no setter, you have to add dataObjects
	 * individually
	 * 
	 * @return List of dataObjects contained in this pathway
	 */
	public List<PathwayElement> getDataObjects() {
		return dataObjects;
	}

	/**
	 * Gets a pathway element by it's GraphId.
	 * 
	 * @param graphId the graphId of the element.
	 * @return e the pathway element with the given id, or null when no element was
	 *         found.
	 */
	public PathwayElement getElementById(String graphId) {
		// TODO: dataobject should be stored in a hashmap, with the graphId as key!
		if (graphId != null) {
			for (PathwayElement e : dataObjects) {
				if (graphId.equals(e.getElementId())) {
					return e;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the Xref of all DataNodes in this pathway as a List.
	 * 
	 * @return result the list of xref of all datanodes or an empty arraylist if
	 *         there are no datanodes in this pathway.
	 */
	public List<Xref> getDataNodeXrefs() {
		List<Xref> result = new ArrayList<Xref>();
		for (PathwayElement e : dataObjects) {
			if (e.getObjectType() == ObjectType.DATANODE) {
				result.add(e.getXref());
			}
		}
		return result;
	}

	/**
	 * Returns the Xref of all Lines in this pathway as a List.
	 *
	 * @return result the list of xref of all lines or an empty arraylist if there
	 *         are no lines in this pathway.
	 */
	public List<Xref> getLineXrefs() {
		List<Xref> result = new ArrayList<Xref>();
		for (PathwayElement e : dataObjects) {
			if (e.getObjectType() == ObjectType.LINE) {
				result.add(e.getXref());
			}
		}
		return result;
	}

	/**
	 * Gets the one and only InfoBox object.
	 *
	 * @return infoBox the PathwayElement with ObjectType set to mappinfo.
	 */
	public PathwayElement getInfoBox() {
		return infoBox;
	}

	/**
	 * Gets the BioPAX element of this pathway, containing literature references and
	 * other optional biopax elements.
	 * 
	 * @returns biopax the BioPAX element of this pathway. Guaranteed to not return
	 *          null. If a BioPAX element does not yet exist, it is automatically
	 *          created.
	 */
	public BiopaxElement getBiopax() {
		if (biopax == null) {
			PathwayElement tmp = PathwayElement.createPathwayElement(ObjectType.BIOPAX);
			this.add(tmp); // biopax will now be set.
		}
		return biopax;
	}

	/** @deprecated use getBiopax() instead */
	public BiopaxElement getBiopaxElementManager() {
		return getBiopax();
	}

	/**
	 * Adds a PathwayElement to this Pathway. takes care of setting parent and
	 * removing from possible previous parent.
	 *
	 * fires PathwayEvent.ADDED event <i>after</i> addition of the object
	 *
	 * @param o the pathway element to add.
	 */
	public void add(PathwayElement o) {
		assert (o != null);
		/**
		 * There can be only one mappInfo object. New object replaces old one.
		 */
		if (o.getObjectType() == ObjectType.MAPPINFO && o != mappInfo) {
			if (mappInfo != null) {
				replaceUnique(mappInfo, o);
				mappInfo = o;
				return;
			}
			mappInfo = o;
		}
		/**
		 * There can be only one InfoBox object. New object replaces old one.
		 */
		if (o.getObjectType() == ObjectType.INFOBOX && o != infoBox) {
			if (infoBox != null) {
				replaceUnique(infoBox, o);
				infoBox = o;
				return;
			}
			infoBox = o;
		}
		/**
		 * There can be only one Legend object. New object replaces old one.
		 */
		if (o.getObjectType() == ObjectType.LEGEND && o != legend) {
			if (legend != null) {
				replaceUnique(legend, o);
				legend = o;
				return;
			}
			legend = o;
		}
		if (o.getParent() == this)
			return; // trying to re-add the same object
		forceAddObject(o);
	}

	/**
	 * Adds a PathwayElement forcibly to this Pathway. Fires PathwayEvent.ADDED
	 * event <i>after</i> addition of the object
	 *
	 * @param o the pathway element to add.
	 */
	private void forceAddObject(PathwayElement o) {
		if (o.getParent() != null) {
			o.getParent().remove(o);
		}
		dataObjects.add(o);
		o.setParent(this);
		for (MPoint p : o.getMPoints()) {
			if (p.getElementRef() != null) {
				addGraphRef(p.getElementRef(), p);
			}
		}
		if (o.getGroupRef() != null) {
			addGroupRef(o.getGroupRef(), o);
		}
		for (MAnchor a : o.getMAnchors()) {
			if (a.getElementId() != null) {
				addGraphId(a.getElementId(), a);
			}
		}
		if (o.getElementId() != null) {
			addGraphId(o.getElementId(), o);
		}
		if (o.getGroupId() != null) {
			addGroupId(o.getGroupId(), o);
		}
		if (o.getElementRef() != null) {
			addGraphRef(o.getElementRef(), (ElementRefContainer) o);
		}
		fireObjectModifiedEvent(new PathwayEvent(o, PathwayEvent.ADDED));
		checkMBoardSize(o);
	}

	/**
	 * Removes object sets parent of object to null fires PathwayEvent.DELETED event
	 * <i>before</i> removal of the object.
	 *
	 * @param o the object to remove.
	 */
	public void remove(PathwayElement o) {
		assert (o.getParent() == this); // can only remove direct child objects
		if (o.getObjectType() == ObjectType.MAPPINFO)
			throw new IllegalArgumentException("Can't remove mappinfo object!");
		if (o.getObjectType() == ObjectType.INFOBOX)
			throw new IllegalArgumentException("Can't remove infobox object!");
		forceRemove(o);
	}

	/**
	 * Removes object, regardless whether the object may be removed or not sets
	 * parent of object to null fires PathwayEvent.DELETED event <i>before</i>
	 * removal of the object.
	 *
	 * @param o the object to remove.
	 */
	private void forceRemove(PathwayElement o) {
		dataObjects.remove(o);
		for (ElementRefContainer refc : getReferringObjects(o.getElementId())) {
			refc.unlink();
		}
		String groupRef = o.getGroupRef();
		if (groupRef != null) {
			removeGroupRef(groupRef, o);
		}
		// Add one or multiples literature(s) reference(s) to the list to deletion
		if (o.getBiopaxRefs() != null) {
			for (String ref : o.getBiopaxRefs()) {
				BiopaxNode node = getBiopax().getElement(ref);
				// if no an another pathway element use this literature reference
				// add to the list to deletion
				if (!getBiopax().hasReferences(node))
					biopaxReferenceToDelete.add(ref);
			}
		}
		for (MAnchor a : o.getMAnchors()) {
			if (a.getElementId() != null) {
				removeGraphId(a.getElementId());
			}
		}
		if (o.getElementId() != null) {
			removeGraphId(o.getElementId());
		}
		if (o.getGroupId() != null) {
			removeGroupId(o.getGroupId());
		}
		if (o.getElementRef() != null) {
			removeGraphRef(o.getElementRef(), (ElementRefContainer) o);
		}
		fireObjectModifiedEvent(new PathwayEvent(o, PathwayEvent.DELETED));
		o.setParent(null);
	}

	/*
	 * Call when making a new mapp.
	 */
	public void initMappInfo() {
		String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
		mappInfo.setVersion(dateString);
		mappInfo.setMapInfoName("New Pathway");
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

	List<OntologyTag> ontologyTags = new ArrayList<OntologyTag>();

	public void addOntologyTag(String id, String term, String ontology) {
		ontologyTags.add(new OntologyTag(id, term, ontology));
	}

	public List<OntologyTag> getOntologyTags() {
		return ontologyTags;
	}

	/**
	 * List of Biopax references to be deleted. The deletion is done before to save
	 * the pathway.
	 */
	private List<String> biopaxReferenceToDelete = new ArrayList<String>();

}
