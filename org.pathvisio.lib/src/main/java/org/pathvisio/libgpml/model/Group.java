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

import java.awt.geom.AffineTransform;
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
	private String textLabel = ""; // optional
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
			if (pathwayElement.getGroupRef() == null || pathwayElement.getGroupRef() != this) {
				pathwayElement.setGroupRefTo(this);
			}
			// add pathway element to this group
			if (pathwayElement.getGroupRef() == this && !hasPathwayElement(pathwayElement)) {
				pathwayElements.add(pathwayElement);
			}
		}
	}

	/**
	 * Updates Group centerX, centerY, width, and height.
	 * <p>
	 * NB:
	 * <ol>
	 * <li>Generally called after {@link addPathwayElement} or setGroupRefTo
	 * Methods, when pathway elements are added to group.
	 * <li>Called after reading methods in {@link GPMLFormatAbstract#updateGroups}.
	 * <li>Also called in copy and paste methods.
	 * </ol>
	 */
	public void updateDimensions() {
		// if newly created group (size 1 or smaller)
		// or if width or height not yet updated (zero)
		if (pathwayElements.size() <= 1 || getWidth() == 0 || getHeight() == 0) {
			Rectangle2D r = getMinBounds(true);
			setCenterX(r.getCenterX());
			setCenterY(r.getCenterY());
			setWidth(r.getWidth());
			setHeight(r.getHeight());
		} else {
			Rectangle2D r = getRotatedBounds();
			setCenterX(r.getCenterX());
			setCenterY(r.getCenterY());
			setWidth(r.getWidth());
			setHeight(r.getHeight());
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
		updateDimensions();
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
	@Override
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for this group.
	 *
	 * @param v the xref to set for this group.
	 */
	@Override
	public void setXref(Xref v) {
		if (v != null) {
			xref = v;
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
			pathwayModel.addDataNode(alias);
			return alias;
		}
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
	 * Rotation is not allowed for Groups and will always be set to 0 (default).
	 *
	 * @param v
	 */
	@Override
	public void setRotation(Double v) {
		// rotation not allowed
		super.setRotation(0.0);
	}

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
	 * Iterates over all group elements to find the TOTAL rectangular bounds, taking
	 * into account rotation of the nested elements.
	 * 
	 * NB: For now, groups should never be rotated.
	 *
	 * @return the rectangular bounds for this group with rotation taken into
	 *         account.
	 */
	@Override
	public Rectangle2D getRotatedBounds() {
		Rectangle2D bounds = getBounds();
		AffineTransform t = new AffineTransform();
		t.rotate(getRotation(), getCenterX(), getCenterY());
		bounds = t.createTransformedShape(bounds).getBounds2D();
		Rectangle2D minbounds = getMinBounds(true);
		bounds.add(minbounds); // add bounds
		return new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	/**
	 * Iterates over all group elements to find the TOTAL rectangular bounds.
	 *
	 * @return the rectangular bounds for this group.
	 */
	@Override
	public Rectangle2D getBounds() {
		Rectangle2D bounds = new Rectangle2D.Double(getLeft(), getTop(), getWidth(), getHeight());
		Rectangle2D minbounds = getMinBounds(false);
		bounds.add(minbounds); // add bounds
		return new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	/**
	 * Iterates over all group elements to find the MINIMAL total rectangular
	 * bounds.
	 *
	 * @param rotated if true, take into account rotation.
	 * @return the rectangular bounds for this group.
	 */
	public Rectangle2D getMinBounds(boolean rotated) {
		Rectangle2D bounds = null;
		for (Groupable e : pathwayElements) {
			if (e == this) {
				continue; // To prevent recursion error
			}
			if (bounds == null) {
				bounds = rotated ? e.getRotatedBounds() : e.getBounds();
			} else {
				bounds.add(rotated ? e.getRotatedBounds() : e.getBounds());
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
	 * Copies values from the given source pathway element.
	 *
	 * @param src the source pathway element.
	 */
	public void copyValuesFrom(Group src) {
		super.copyValuesFrom(src);
		textLabel = src.textLabel;
		type = src.type;
		xref = src.xref;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copies this pathway element.
	 *
	 * @return the copyElement for the new pathway element and this source pathway
	 *         element.
	 */
	@Override
	public CopyElement copy() {
		Group result = new Group(type);
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
	 * Returns static property value for given key.
	 *
	 * @param key the key.
	 * @return the static property value.
	 */
	@Override
	public Object getStaticProperty(StaticProperty key) {
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
	 * @param key the key.
	 * @param value the static property value.
	 */
	@Override
	public void setStaticProperty(StaticProperty key, Object value) {
		super.setStaticProperty(key, value);
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

	/**
	 * Writes this group out as a string.
	 * 
	 * @return the string representing this group.
	 */
	@Override
	public String toString() {
		String result = "Group " + getElementId();
		if (textLabel != null && !Utils.stringEquals(textLabel, "")) {
			result = result + ": " + textLabel;
		}
		return result;
	}

}
