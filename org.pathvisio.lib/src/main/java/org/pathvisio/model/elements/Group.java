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
package org.pathvisio.model.elements;

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.graphics.FontProperty;
import org.pathvisio.model.graphics.RectProperty;
import org.pathvisio.model.graphics.ShapeStyleProperty;
import org.pathvisio.model.type.GroupType;

/**
 * This class stores all information relevant to a Group pathway element.
 * 
 * @author finterly
 */
public class Group extends ShapedElement {

	/* list of pathway elements which belong to the group. */
	private List<PathwayElement> pathwayElements; // 0 to unbounded TODO should have at least one?
	private GroupType type = GroupType.GROUP;
	private String textLabel; // optional
	private Xref xref; // optional

	/**
	 * Instantiates a Group given all possible parameters.
	 * 
	 * @param elementId          the unique pathway element identifier.
	 * @param pathwayModel       the parent pathway model.
	 * @param rectProperty       the centering (position) and dimension properties.
	 * @param fontProperty       the font properties, e.g. textColor, fontName...
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor.
	 * @param groupRef           the parent group in which the pathway element
	 *                           belongs.
	 * @param type               the type of the group.
	 * @param pathwayElements    the list of pathway element members of the group.
	 * @param textLabel          the text of the group.
	 * @param xref               the group Xref.
	 */
	public Group(String elementId, PathwayModel pathwayModel, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Group groupRef, GroupType type, String textLabel, Xref xref) {
		super(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, groupRef);
		this.pathwayElements = new ArrayList<PathwayElement>();
		this.type = type;
		this.textLabel = textLabel;
		this.xref = xref;
	}

	/**
	 * Instantiates a Group given all possible parameters except textLabel.
	 */
	public Group(String elementId, PathwayModel pathwayModel, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Group groupRef, GroupType type, Xref xref) {
		this(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, groupRef, type, null, xref);
	}

	/**
	 * Instantiates a Group given all possible parameters except xref.
	 */
	public Group(String elementId, PathwayModel pathwayModel, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Group groupRef, GroupType type, String textLabel) {
		this(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, groupRef, type, textLabel, null);
	}

	/**
	 * Instantiates a Group given all possible parameters except textLabel and xref.
	 */
	public Group(String elementId, PathwayModel pathwayModel, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Group groupRef, GroupType type) {
		this(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, groupRef, type, null, null);
	}

	/**
	 * Instantiates a Group given all possible parameters except groupRef, because
	 * the group does not belong in another group.
	 */
	public Group(String elementId, PathwayModel pathwayModel, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, GroupType type, String textLabel, Xref xref) {
		this(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, null, type, textLabel, xref);
	}

	/**
	 * Instantiates a Group given all possible parameters except groupRef and xref,
	 * because the group neither belongs in another group nor has xref.
	 */
	public Group(String elementId, PathwayModel pathwayModel, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, GroupType type, String textLabel) {
		this(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, null, type, textLabel, null);
	}

	/**
	 * Instantiates a Group given all possible parameters except groupRef and
	 * textLabel, because the group neither belongs in another group nor has
	 * textLabel.
	 */
	public Group(String elementId, PathwayModel pathwayModel, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, GroupType type, Xref xref) {
		this(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, null, type, null, xref);
	}

	/**
	 * Instantiates a Group given all possible parameters except groupRef,
	 * textLabel, and xref, because the group neither belongs in another group, nor
	 * has a textLabel, nor has xref.
	 */
	public Group(String elementId, PathwayModel pathwayModel, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, GroupType type) {
		this(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, null, type, null, null);
	}

	/**
	 * Returns the list of pathway element members of the group.
	 * 
	 * @return pathwayElements the list of pathway elements belonging to the group.
	 */
	public List<PathwayElement> getPathwayElements() {
		return pathwayElements;
	}

	/**
	 * Adds the given pathway element to pathwayElements list of the group.
	 * 
	 * @param pathwayElement the given pathwayElement to add.
	 */
	public void addPathwayElement(PathwayElement pathwayElement) {
		pathwayElements.add(pathwayElement);
	}

	/**
	 * Removes the given pathway element from pathwayElements list of the group.
	 * 
	 * @param pathwayElement the given pathwayElement to remove.
	 */
	public void removePathwayElement(PathwayElement pathwayElement) {
		pathwayElements.remove(pathwayElement);
	}

	/**
	 * Gets GroupType. GroupType is GROUP by default.
	 * 
	 * TODO: Default GroupType used to be NONE. NONE needs to be changed to GROUP.
	 * 
	 * @return type the type of group, e.g. complex.
	 */
	public GroupType getType() {
		if (type == null) {
			type = GroupType.GROUP;
		}
		return type;
	}

	/**
	 * Sets GroupType to the given groupType.
	 * 
	 * @param type the type of group, e.g. complex.
	 */
	public void setType(GroupType type) {
		this.type = type;
	}

	/**
	 * Gets the text of of the group.
	 * 
	 * @return textLabel the text of of the group.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the group.
	 * 
	 * @param textLabel the text of of the group.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Returns the Xref for the group.
	 * 
	 * @return xref the xref of the group
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the group.
	 * 
	 * @param xref the xref of the group.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

	/**
	 * Instantiates data node Xref given identifier and dataSource. Checks whether
	 * dataSource string is fullName, systemCode, or invalid.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 * @throws IllegalArgumentException is given dataSource does not exist.
	 */
	public void createXref(String identifier, String dataSource) {
		if (DataSource.fullNameExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		} else if (DataSource.systemCodeExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getByAlias(dataSource));
		} else {
			DataSource.register(dataSource, dataSource);
			System.out.println("DataSource: " + dataSource + " is registered."); // TODO warning
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource)); // TODO fullname/code both ok
		}
	}

}
