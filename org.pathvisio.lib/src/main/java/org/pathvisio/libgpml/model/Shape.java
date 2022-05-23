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

import java.util.Set;

import org.pathvisio.libgpml.model.type.ObjectType;
import org.pathvisio.libgpml.prop.StaticProperty;
import org.pathvisio.libgpml.util.Utils;

/**
 * This class stores all information relevant to a Shape pathway element.
 *
 * @author finterly
 */
public class Shape extends ShapedElement {

	private String textLabel; // optional

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Instantiates a Shape pathway element.
	 */
	public Shape() {
		super();
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
		return ObjectType.SHAPE;
	}

	/**
	 * Returns the text of of the shape.
	 *
	 * @return textLabel the text of of the shape.
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

	// ================================================================================
	// Inherited Methods
	// ================================================================================
	/**
	 * Terminates this shape and removes all links and references.
	 */
	@Override
	protected void terminate() {
		unsetAllLinkableFroms();
		unsetGroupRef();
		super.terminate();
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Copies values from the given source pathway element.
	 *
	 * @param src the source pathway element.
	 */
	public void copyValuesFrom(Shape src) {
		super.copyValuesFrom(src);
		textLabel = src.textLabel;
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
		Shape result = new Shape();
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
		result.add(StaticProperty.TEXTLABEL);
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
			case TEXTLABEL:
				result = getTextLabel();
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
	 * @return the static property value.
	 */
	@Override
	public void setStaticProperty(StaticProperty key, Object value) {
		super.setStaticProperty(key, value);
		switch (key) {
		case TEXTLABEL:
			setTextLabel((String) value);
			break;
		default:
			// do nothing
		}
	}

}
