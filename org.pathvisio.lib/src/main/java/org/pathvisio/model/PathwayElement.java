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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.ElementLink.ElementIdContainer;
import org.pathvisio.model.ElementLink.ElementRefContainer;
import org.pathvisio.util.Utils;

/**
 * Abstract class of pathway elements which are part of a pathway and have an
 * elementId.
 * 
 * Children: DataNode, State, Interaction, GraphicalLine, Label, Shape, Group.
 * 
 * @author unknown, AP20070508, finterly
 */
public abstract class PathwayElement implements ElementIdContainer, Comparable<PathwayElement> {

	private String elementId;
	private Pathway parentPathway = null; // parent pathway: may be null (e.g. when object is in clipboard)

	// TODO parent Pathway handle?


	private List<Comment> comments = null; // length 0 to unbounded
	private List<DynamicProperty> dynamicProperties;
	private List<AnnotationRef> annotationRefs;
	private List<Citation> citations;
	private List<Evidence> evidences;
	
	
	// TODO Builder or Constructor with empty Lists?

	public PathwayElement(String elementId) {
		
		// TODO use setter since its complicated...
		setElementId();
		this.elementId = elementId;
		
		comments = new ArrayList<Comment>(); // length 0 to unbounded
		dynamicProperties = new ArrayList<DynamicProperty>();
		annotationRefs = new ArrayList<AnnotationRef>();
		citations = new ArrayList<Citation>();
		evidences = new ArrayList<Evidence>();
	}
	
	

  
	
	/**
	 * Returns the parent pathway.
	 * 
	 * @return parent the parent pathway.
	 */
	public Pathway getParentPathway() {
		return parentPathway;
	}

	/**
	 * Get the parent pathway. Same as {@link #getParent()}, but necessary to comply
	 * to the {@link ElementIdContainer} interface.
	 * 
	 * @return parent the parent pathway.
	 */
	public Pathway getPathway() {
		return parentPathway;
	}

	/**
	 * Set parent. Do not use this method directly! parent is set automatically when
	 * using Pathway.add/remove
	 * 
	 * @param v the parentGENEID
	 */
	void setParentPathway(Pathway pathway) {
		parentPathway = pathway;
	}

	/* ------------------------------- GROUPREF ------------------------------- */

	protected String groupRef;

	/**
	 * 
	 * @return
	 */
	public String getGroupRef() {
		return groupRef;
	}

	/**
	 * 
	 * @param s
	 */
	public void setGroupRef(String id) {
		if (groupRef == null || !groupRef.equals(id)) {
			if (parentPathway != null) {
				if (groupRef != null) {
					parentPathway.removeGroupRef(groupRef, this);
				}
				// Check: move add before remove??
				if (id != null) {
					parentPathway.addGroupRef(id, this);
				}
			}
			groupRef = id;
		}
	}

	/* ------------------------------- GROUPID ------------------------------- */

	protected String groupId;

	public String getGroupId() {
		return groupId;
	}

	public String createGroupId() {
		if (groupId == null) {
			setGroupId(parentPathway.getUniqueGroupId());
		}
		return groupId;
	}

	/**
	 * Set groupId. This id must be any string unique within the Pathway object
	 *
	 * @see Pathway#getUniqueId(java.util.Set)
	 */
	public void setGroupId(String id) {
		if (groupId == null || !groupId.equals(id)) {
			if (parentPathway != null) {
				if (groupId != null) {
					parentPathway.removeGroupId(groupId);
				}
				// Check: move add before remove??
				if (id != null) {
					parentPathway.addGroupId(id, this);
				}
			}
			groupId = id;
		}
	}

	/* ------------------------------- ELEMENTID ------------------------------- */
	/**
	 * @return
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * 
	 * @return
	 */
	public String doGetElementId() {
		return elementId;
	}

	/**
	 * Set elementId. ElementId must be a unique within the Pathway object
	 *
	 * @see Pathway#getUniqueId(java.util.Set)
	 */
	public void setElementId(String id) {
		ElementLink.setElementId(id, this, parentPathway);
		elementId = id;
	}

	public String setGeneratedElementId() {
		setElementId(parentPathway.getUniqueElementId());
		return elementId;
	}
	/* ------------------------------- ELEMENTREF ------------------------------- */

	protected String elementRef = null;

	/**
	 * Return a list of ElementRefContainers (i.e. points) referring to this pathway
	 * element.
	 */
	public Set<ElementRefContainer> getReferences() {
		return ElementLink.getReferences(this, parentPathway);
	}

	/** elementRef property, used by Modification */
	public String getElementRef() {
		return elementRef;
	}

	/**
	 * Set graphRef property, used by State The new graphRef should exist and point
	 * to an existing DataNode
	 */
	public void setElementRef(String value) {
		// TODO: check that new elementRef exists and that it points to a DataNode
		if (!(elementRef == null ? value == null : elementRef.equals(value))) {
			elementRef = value;
		}
	}

	/* ------------------------------- OTHER.... ------------------------------- */

	public String getStartElementRef() {
		return mPoints.get(0).getElementRef();
	}

	public void setStartElementRef(String ref) {
		MPoint start = mPoints.get(0);
		start.setGraphRef(ref);
	}

	public String getEndElementRef() {
		return mPoints.get(mPoints.size() - 1).getElementRef();
	}

	public void setEndElementRef(String ref) {
		MPoint end = mPoints.get(mPoints.size() - 1);
		end.setGraphRef(ref);
	}

	/* ------------------------------- OTHER.... ------------------------------- */

	/**
	 * Returns keys of available static properties and dynamic properties as an
	 * object list.
	 * 
	 * @return keys the set of keys of available static properties and dynamic
	 *         properties.
	 */
	public Set<Object> getPropertyKeys() {
		Set<Object> keys = new HashSet<Object>();
		keys.addAll(getStaticPropertyKeys());
		keys.addAll(getDynamicPropertyKeys());
		return keys;
	}

	/**
	 * Sets dynamic or static properties at the same time.
	 * 
	 * TODO: Will be replaced with setProperty in the future.
	 */
	public void setPropertyEx(Object key, Object value) {
		if (key instanceof StaticProperty) {
			setStaticProperty((StaticProperty) key, value);
		} else if (key instanceof String) {
			setDynamicProperty((String) key, value.toString());
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Gets dynamic or static properties at the same time.
	 * 
	 * @param key the key for dynamic or static property values.
	 * @return dynamic or static properties.
	 */
	public Object getPropertyEx(Object key) {
		if (key instanceof StaticProperty) {
			return getStaticProperty((StaticProperty) key);
		} else if (key instanceof String) {
			return getDynamicProperty((String) key);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/* ------------------------------- DATANODE ------------------------------- */

	public void printRefsDebugInfo() {
		System.err.println(objectType + " " + getElementId());
		if (this instanceof MLine) {
			for (MPoint p : getMPoints()) {
				System.err.println("  p: " + p.getElementId());
			}
			for (MAnchor a : getMAnchors()) {
				System.err.println("  a: " + a.getElementId());
			}
		}
		if (this instanceof MState) {
			System.err.println("  " + getElementRef());
		}
	}

	/*
	 * ------------------------------- CommentGroup -------------------------------
	 */

	/**
	 * Returns the list of comments.
	 * 
	 * @return comments the list of comments.
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/**
	 * Sets comments to given list of Comment.
	 * 
	 * @param comments the list of comments.
	 */
	public void setComments(List<Comment> comments) {
		this.comments = comments;
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
	 * TODO: Need to be moved or something...
	 * 
	 * Finds the first comment with a specific source.
	 * 
	 * @returns the comment with a given source.
	 */
	public String findComment(String source) {
		for (Comment c : comments) {
			if (source.equals(c.source)) {
				return c.comment;
			}
		}
		return null;
	}

	/**
	 * Returns the list of key value pair information properties.
	 * 
	 * @return properties the list of properties.
	 */
	public List<DynamicProperty> getProperties() {
		return dynamicProperties;
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
	 * Returns the list of citation references.
	 * 
	 * @return citationRefs the list of citation references.
	 */
	public List<Citation> getCitations() {
		return citations;
	}

	public List<Evidence> getEvidences() {
		return evidences;
	}

	public void setEvidenceRefs(List<Evidence> evidences) {
		this.evidences = evidences;
	}

//	public List<AnnotationRef> getAnnotationRefList() {
//		if (annotationRefs == null) {
//			annotationRefs = new ArrayList<AnnotationRef>();
//		}
//		return this.annotationRefs;
//	}

}
