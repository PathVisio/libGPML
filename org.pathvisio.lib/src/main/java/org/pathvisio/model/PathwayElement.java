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
 * A class of pathway elements which are part of a pathway and have an
 * elementId.
 * 
 * @author unknown, AP20070508, finterly
 */
public abstract class PathwayElement implements ElementIdContainer, Comparable<PathwayElement> {

	/**
	 * Parent pathway of this object: may be null (for example, when object is in
	 * clipboard)
	 */
	protected Pathway parent = null;

	/**
	 * Returns the parent pathway.
	 * 
	 * @return parent the parent pathway.
	 */
	public Pathway getParent() {
		return parent;
	}

	/**
	 * Get the parent pathway. Same as {@link #getParent()}, but necessary to comply
	 * to the {@link ElementIdContainer} interface.
	 * 
	 * @return parent the parent pathway.
	 */
	public Pathway getPathway() {
		return parent;
	}

	/**
	 * Set parent. Do not use this method directly! parent is set automatically when
	 * using Pathway.add/remove
	 * 
	 * @param v the parentGENEID
	 */
	void setParent(Pathway v) {
		parent = v;
	}

	/* ------------------------------- ID & GROUP ------------------------------- */

	/**
	 * 
	 */
	protected String elementId;

	/**
	 * 
	 */
	protected String groupRef;

	/**
	 * 
	 * @return
	 */
	public String doGetElementId() {
		return elementId;
	}

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
	public void setGroupRef(String s) {
		if (groupRef == null || !groupRef.equals(s)) {
			if (parent != null) {
				if (groupRef != null) {
					parent.removeGroupRef(groupRef, this);
				}
				// Check: move add before remove??
				if (s != null) {
					parent.addGroupRef(s, this);
				}
			}
			groupRef = s;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
		}
	}

	/* AP20070508 */
	/**
	 * TODO: Is replaced with elementId in 2021.
	 */
	protected String groupId;

	/**
	 * 
	 * TODO: groupId is replaced with elementId in 2021.
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 
	 * TODO: groupId is replaced with elementId in 2021.
	 */
	public String createGroupId() {
		if (groupId == null) {
			setGroupId(parent.getUniqueGroupId());
		}
		return groupId;
	}

	/**
	 * Set groupId. This id must be any string unique within the Pathway object
	 *
	 * @see Pathway#getUniqueId(java.util.Set)
	 */
	public void setGroupId(String w) {
		if (groupId == null || !groupId.equals(w)) {
			if (parent != null) {
				if (groupId != null) {
					parent.removeGroupId(groupId);
				}
				// Check: move add before remove??
				if (w != null) {
					parent.addGroupId(w, this);
				}
			}
			groupId = w;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPID));
		}

	}

	/** graphRef property, used by Modification */
	public String getElementRef() {
		return elementRef;
	}

	/**
	 * Set graphRef property, used by State The new graphRef should exist and point
	 * to an existing DataNode
	 */
	public void setGraphRef(String value) {
		// TODO: check that new graphRef exists and that it points to a DataNode
		if (!(elementRef == null ? value == null : elementRef.equals(value))) {
			elementRef = value;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GRAPHREF));
		}
	}

	public String getElementId() {
		return elementId;
	}

	/**
	 * Set graphId. This id must be any string unique within the Pathway object
	 *
	 * @see Pathway#getUniqueId(java.util.Set)
	 */
	public void setElementId(String v) {
		ElementLink.setElementId(v, this, parent);
		elementId = v;
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GRAPHID));
	}

	public String setGeneratedElementId() {
		setElementId(parent.getUniqueGraphId());
		return elementId;
	}

	public String getStartGraphRef() {
		return mPoints.get(0).getElementRef();
	}

	public void setStartGraphRef(String ref) {
		MPoint start = mPoints.get(0);
		start.setGraphRef(ref);
	}

	public String getEndGraphRef() {
		return mPoints.get(mPoints.size() - 1).getElementRef();
	}

	public void setEndGraphRef(String ref) {
		MPoint end = mPoints.get(mPoints.size() - 1);
		end.setGraphRef(ref);
	}

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

	/**
	 * 
	 */
	protected String setGeneID = "";

	/**
	 * @deprecated Use {@link #getElementID()} instead
	 */
	public String getGeneID() {
		return getElementID();
	}

	public String getElementID() {
		return setGeneID;
	}

	/**
	 * @deprecated Use {@link #setElementID(String)} instead
	 */
	public void setGeneID(String v) {
		setElementID(v);
	}

	public void setElementID(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		v = v.trim();
		if (!Utils.stringEquals(setGeneID, v)) {
			setGeneID = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GENEID));
		}
	}

	/**
	 * The pathway data source.
	 */
	protected DataSource source = null;

	/**
	 * Gets pathway data source.
	 * 
	 * @return source the pathway data source.
	 */
	public DataSource getDataSource() {
		return source;
	}

	/**
	 * Sets pathway data source.
	 * 
	 * @param source the pathway data source.
	 */
	public void setDataSource(DataSource source) {
		if (this.source != source) {
			this.source = source;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.DATASOURCE));
		}
	}

	/**
	 * returns GeneID and dataSource combined in an Xref. Pathway elements DataNode,
	 * State, Interaction, and Group can contain a Xref.
	 *
	 * Same as new Xref ( pathwayElement.getGeneID(), pathwayElement.getDataSource()
	 * );
	 */
	public Xref getXref() {
		// TODO: Store Xref by default, derive setGeneID and dataSource from it.
		return new Xref(setGeneID, source);
	}

	public Set<ElementRefContainer> getReferences() {
		return ElementLink.getReferences(this, parent);
	}

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
}
