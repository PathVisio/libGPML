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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.Xref;
import org.pathvisio.libgpml.model.DataNode.State;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.GroupType;
import org.pathvisio.libgpml.model.type.ObjectType;
import org.pathvisio.libgpml.prop.StaticProperty;
import org.pathvisio.libgpml.util.Utils;

/**
 * This class stores all information relevant to a Group pathway element.
 * 
 * @author finterly
 */
public class Group extends ShapedElement implements Xrefable {

	private GroupType type = GroupType.GROUP;
	private String textLabel = ""; // optional TODO
	private Xref xref; // optional
	/* list of pathway elements which belong to the group. */
	private List<Groupable> pathwayElements; // should have at least one pathway element

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates a Group given all required parameters and xref.
	 * 
	 * @param type the type of the group.
	 * @param xref the group Xref.
	 */
	public Group(GroupType type, Xref xref) {
		super();
		this.type = type;
		this.xref = xref;
		this.pathwayElements = new ArrayList<Groupable>();
	}

	/**
	 * Instantiates a Group given all required parameters.
	 */
	public Group(GroupType type) {
		this(type, null);
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * Returns the object type of this pathway element.
	 * 
	 * @return the object type.
	 */
	@Override
	public ObjectType getObjectType() {
		return ObjectType.GROUP;
	}

	/**
	 * Returns the list of pathway element members of the group.
	 * 
	 * @return pathwayElements the list of pathway elements belonging to the group.
	 */
	public List<Groupable> getPathwayElements() {
		return pathwayElements;
	}

	/**
	 * Checks whether pathwayElements has the given pathwayElement.
	 * 
	 * @param pathwayElement the pathway element to look for.
	 * @return true if has pathwayElement, false otherwise.
	 */
	public boolean hasPathwayElement(Groupable pathwayElement) {
		return pathwayElements.contains(pathwayElement);
	}

	/**
	 * Adds the given pathway element to pathwayElements list of this group. Checks
	 * if pathway element is valid. Sets groupRef of pathway element to this group
	 * if necessary.
	 * 
	 * NB: States are not added to group pathway elements lists. States appear
	 * outside of groups in the view.
	 * 
	 * @param pathwayElement the given pathwayElement to add.
	 */
	public void addPathwayElement(Groupable pathwayElement) {
		if (pathwayElement == null) {
			throw new IllegalArgumentException("Cannot add invalid pathway element to group " + getElementId());
		}
		if (pathwayModel != pathwayElement.getPathwayModel()) {
			throw new IllegalArgumentException("Group can only add pathway elements of the same pathway model");
		}
		// do not add if pathway element is a state
		if (pathwayElement.getClass() != State.class) {
			// set groupRef for pathway element if necessary
			if (pathwayElement.getGroupRef() == null || pathwayElement.getGroupRef() != this)
				pathwayElement.setGroupRefTo(this);
			// add pathway element to this group
			if (pathwayElement.getGroupRef() == this && !hasPathwayElement(pathwayElement))
				pathwayElements.add(pathwayElement);
		}
	}

	/**
	 * Removes the given pathway element from pathwayElements list of the group.
	 * Checks if pathway element is valid. Unsets groupRef of pathway element from
	 * this group if necessary.
	 * 
	 * @param pathwayElement the given pathwayElement to remove.
	 */
	public void removePathwayElement(Groupable pathwayElement) {
		if (pathwayElement != null) {
			pathwayElement.unsetGroupRef();
			pathwayElements.remove(pathwayElement);
		}
		// remove group if its empty, and refers to and belongs to the pathway model
		if (pathwayElements.isEmpty() && pathwayModel != null && pathwayModel.hasPathwayObject(this)) {
			pathwayModel.removeGroup(this);
		}
	}

	/**
	 * Adds the given list of pathway elements to pathwayElements list of this
	 * group.
	 * 
	 * @param pathwayElements the given list of pathwayElement to add.
	 */
	public void addPathwayElements(List<? extends Groupable> pathwayElements) {
		for (Groupable pathwayElement : pathwayElements) {
			addPathwayElement(pathwayElement);
		}
	}

	/**
	 * Removes all pathway elements from the pathwayElements list.
	 */
	public void removePathwayElements() {
		for (int i = pathwayElements.size() - 1; i >= 0; i--) {
			removePathwayElement(pathwayElements.get(i));
		}
	}

	/**
	 * Returns GroupType. GroupType is GROUP by default.
	 * 
	 * @return type the type of group, e.g. complex.
	 */
	public GroupType getType() {
		return type;
	}

	/**
	 * Sets GroupType to the given groupType.
	 * 
	 * @param v the type to set for this group, e.g. complex.
	 */
	public void setType(GroupType v) {
		if (type != v && v != null) {
			type = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.GROUPTYPE));
		}
	}

	/**
	 * Returns the text of of this group.
	 * 
	 * @return textLabel the text of of this group.
	 * 
	 */
	@Override
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of this shaped pathway element.
	 * 
	 * @param v the text to set.
	 */
	@Override
	public void setTextLabel(String v) {
		if (v != null && !Utils.stringEquals(textLabel, v)) {
			textLabel = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.TEXTLABEL));
		}
	}

	/**
	 * Returns the Xref for the group.
	 * 
	 * @return xref the xref of this group
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this group.
	 * 
	 * @param v the xref to set for this group.
	 */
	public void setXref(Xref v) {
		if (v != null) {
			xref = v;
			// TODO
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
		}
	}

	// ================================================================================
	// Special Methods
	// ================================================================================

	/**
	 * Creates and returns an Alias data node for this group.
	 */
	public DataNode addAlias(String textLabel) {
		if (pathwayModel != null) {
			DataNode alias = new DataNode(textLabel, DataNodeType.ALIAS, null, this);
			pathwayModel.addDataNode(alias); // TODO
			return alias;
		}
		System.out.println("Cannot create an alias for group without valid pathway model.");
		return null;
	}

	/**
	 * Terminates this group and removes all references and links.
	 * 
	 * NB: Must {@link LineElement.LinePoint#unlink} before removing pathway element
	 * members, so that line points stays in the same position when the group
	 * disappears. If you remove pathway elements first, the group bounds changes
	 * before you can unlink(), effecting the position of line points.
	 */
	@Override
	protected void terminate() {
		pathwayModel.removeAliasRef(this); // removes this group aliasRef from pathway model
		unsetAllLinkableFroms(); // unlink before removing pathway element
		unsetGroupRef();
		removePathwayElements();
		super.terminate();
	}

	// ================================================================================
	// Bounds Methods
	// ================================================================================
	/**
	 * Default margins for group bounding-box in GPML2013a. Makes the bounds
	 * slightly larger than the summed bounds of the containing elements.
	 */
	public static final double DEFAULT_M_MARGIN = 8;
	public static final double COMPLEX_M_MARGIN = 12;

	/**
	 * Returns margin for group bounding-box around contained elements depending on
	 * group type, as specified in GPML2013a.
	 * 
	 * @return the margin for group.
	 */
	public double getMargin() {
		if (type == GroupType.COMPLEX) {
			return COMPLEX_M_MARGIN;
		} else {
			return DEFAULT_M_MARGIN;
		}
	}

	/**
	 * Center x of the group bounds
	 */
	@Override
	public double getCenterX() {
		return getRotatedBounds().getCenterX();
	}

	/**
	 * Center y of the group bounds
	 */
	@Override
	public double getCenterY() {
		return getRotatedBounds().getCenterY();
	}

	/**
	 * Width of the group bounds
	 */
	@Override
	public double getWidth() {
		return getRotatedBounds().getWidth();
	}

	/**
	 * Height of the group bounds
	 */
	@Override
	public double getHeight() {
		return getRotatedBounds().getHeight();
	}

	/**
	 * Left of the group bounds
	 */
	@Override
	public double getLeft() {
		return getRotatedBounds().getX();
	}

	/**
	 * Top of the group bounds
	 */
	@Override
	public double getTop() {
		return getRotatedBounds().getY();
	}

	@Override
	public void setCenterX(double v) {
		double d = v - getRotatedBounds().getCenterX();
		for (Groupable e : pathwayElements) {
			e.setCenterX(e.getCenterX() + d); // TODO PROBLEM!
		}
	}

	@Override
	public void setCenterY(double v) {
		double d = v - getRotatedBounds().getCenterY();
		for (Groupable e : pathwayElements) {
			e.setCenterY(e.getCenterY() + d); // TODO PROBLEM!
		}
	}

	@Override
	public void setWidth(double v) {
		double d = v - getRotatedBounds().getWidth();
		for (Groupable e : pathwayElements) {
			if (e instanceof ShapedElement) {
				((ShapedElement) e).setWidth(e.getWidth() + d); //// TODO PROBLEM!
			}
		}
	}

	@Override
	public void setHeight(double v) {
		double d = v - getRotatedBounds().getHeight();
		for (Groupable e : pathwayElements) {
			if (e instanceof ShapedElement) {
				((ShapedElement) e).setHeight(e.getHeight() + d);
			} //// TODO PROBLEM!
		}
	}

	@Override
	public void setLeft(double v) {
		double d = v - getRotatedBounds().getX();
		for (Groupable e : pathwayElements) {
			e.setLeft(e.getLeft() + d); //// TODO PROBLEM!
		}
	}

	@Override
	public void setTop(double v) {
		double d = v - getRotatedBounds().getY();
		for (Groupable e : pathwayElements) {
			e.setTop(e.getTop() + d); //// TODO PROBLEM!
		}
	}

	/**
	 * Iterates over all group elements to find the total rectangular bounds, taking
	 * into account rotation of the nested elements
	 * 
	 * @return the rectangular bounds for this group with rotation taken into
	 *         account.
	 */
	@Override
	public Rectangle2D getRotatedBounds() {
		Rectangle2D bounds = null;
		for (Groupable e : pathwayElements) {
			if (e == this) {
				continue; // To prevent recursion error
			}
			if (bounds == null) {
				bounds = e.getRotatedBounds();
			} else {
				bounds.add(e.getRotatedBounds());
			}
		}
		if (bounds != null) {
			double margin = getMargin();
			return new Rectangle2D.Double(bounds.getX() - margin, bounds.getY() - margin,
					bounds.getWidth() + 2 * margin, bounds.getHeight() + 2 * margin);
		} else {
			return new Rectangle2D.Double();
		}
	}

	/**
	 * Iterates over all group elements to find the total rectangular bounds. Note:
	 * doesn't include rotation of the nested elements. If you want to include
	 * rotation, use {@link #getRotatedBounds()} instead.
	 * 
	 * @return the rectangular bounds for this group.
	 */
	@Override
	public Rectangle2D getBounds() {
		Rectangle2D bounds = null;
		for (Groupable e : pathwayElements) {
			if (e == this) {
				continue; // To prevent recursion error
			}
			if (bounds == null) {
				bounds = e.getBounds();
			} else {
				bounds.add(e.getBounds());
			}
		}
		if (bounds != null) {
			double margin = getMargin();
			return new Rectangle2D.Double(bounds.getX() - margin, bounds.getY() - margin,
					bounds.getWidth() + 2 * margin, bounds.getHeight() + 2 * margin);
		} else {
			return new Rectangle2D.Double();
		}
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Note: doesn't change parent, only fields
	 *
	 * Used by UndoAction.
	 * 
	 * NB: Pathway element members are not copied but added later.
	 *
	 * @param src
	 */
	public void copyValuesFrom(Group src) { // TODO
		super.copyValuesFrom(src);
		textLabel = src.textLabel;
		type = src.type;
		xref = src.xref;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public CopyElement copy() {
		Group result = new Group(type); // TODO
		result.copyValuesFrom(this);
		return new CopyElement(result, this);
	}

	// ================================================================================
	// Property Methods
	// ================================================================================
	/**
	 * Returns all static properties for this pathway object.
	 * 
	 * @return result the set of static property for this pathway object.
	 */
	@Override
	public Set<StaticProperty> getStaticPropertyKeys() {
		Set<StaticProperty> result = super.getStaticPropertyKeys();
		Set<StaticProperty> propsGroup = EnumSet.of(StaticProperty.GROUPTYPE, StaticProperty.XREF,
				StaticProperty.TEXTLABEL);
		result.addAll(propsGroup);
		return result;
	}

	/**
	 *
	 */
	@Override
	public Object getStaticProperty(StaticProperty key) { // TODO
		Object result = super.getStaticProperty(key);
		if (result == null) {
			switch (key) {
			case GROUPTYPE:
				result = getType().getName();
				break;
			case TEXTLABEL:
				result = getTextLabel();
				break;
			case XREF:
				result = getXref();
				break;
			default:
				// do nothing
			}
		}
		return result;
	}

	/**
	 * This works so that o.setNotes(x) is the equivalent of o.setProperty("Notes",
	 * x);
	 *
	 * Value may be null in some cases, e.g. graphRef
	 *
	 * @param key
	 * @param value
	 */
	@Override
	public void setStaticProperty(StaticProperty key, Object value) {
		super.setStaticProperty(key, value);
		System.out.println(key);
		switch (key) {
		case GROUPTYPE:
			if (value instanceof GroupType) {
				setType((GroupType) value);
			} else {
				setType(GroupType.fromName((String) value));
			}
			break;
		case TEXTLABEL:
			setTextLabel((String) value);
			break;
		case XREF:
			setXref((Xref) value);
			break;
		default:
			// do nothing
		}
	}

}
