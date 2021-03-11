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

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.ElementLink.ElementRefContainer;

/**
 * This class stores all information relevant to a State pathway element.
 * 
 * @author finterly
 */
public class State extends PathwayElement implements ElementRefContainer {

	protected String elementId;
	protected String elementRef;
	protected String textLabel;
	protected StateType type = StateType.PHOSPHORYLATED; // TODO: Getter/Setter weird
	private double relX;
	private double relY;
	private double width;
	private double height;	
	protected FontProperty fontProperty;
	protected ShapeStyleProperty shapeStyleProperty;
	protected Xref xref;
	protected List<Comment> comments; // optional
	protected List<DynamicProperty> dynamicProperties; // optional
	protected List<AnnotationRef> annotationRefs; // optional
	protected List<CitationRef> citationRefs; // optional
	protected List<EvidenceRef> evidenceRefs; // optional

	
	
	/**
	 * Gets the parent data node of this state.
	 * 
	 * @return the parent pathway element data node of this state, null if no parent
	 *         exists.
	 */
	public PathwayElement getParentDataNode() {
		Pathway parent = getParent();
		if (parent == null) {
			return null;
		}
		return parent.getElementById(getElementRef());
	}
	
	@Override
	public void setParent(Pathway v) {
		if (parent != v) {
			super.setParent(v);
			if (parent != null && graphRef != null) {
				updateCoordinates();
			}
		}
	}

	protected State(ObjectType state) {
		super(ObjectType.STATE);
		// TODO Auto-generated constructor stub
	}

	/**
	 * relX property, used by State. Should normally be between -1.0 and 1.0, where
	 * 1.0 corresponds to the edge of the parent object
	 */
	public double getRelX() {
		return relX;
	}

	/**
	 * See getRelX
	 */
	public void setRelX(double value) {
		if (relX != value) {
			relX = value;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}


	/**
	 * relX property, used by State. Should normally be between -1.0 and 1.0, where
	 * 1.0 corresponds to the edge of the parent object
	 */
	public double getRelY() {
		return relY;
	}

	/**
	 * See getRelX
	 */
	public void setRelY(double value) {
		if (relY != value) {
			relY = value;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}
	
	
	/**
	 * Gets the elementId of the state.
	 * 
	 * @return elementId the unique id of the state.
	 * 
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the state.
	 * 
	 * @param elementId the unique id of the state.
	 * 
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the groupRef of the state. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the state.
	 * 
	 */
	public String getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the state. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the state.
	 * 
	 */
	public void getGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * Gets the state Xref.
	 * 
	 * @return xref the state xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of state Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}

	/**
	 * Gets the text of of the state.
	 * 
	 * @return textLabel the text of of the state.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the state.
	 * 
	 * @param textLabel the text of of the state.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Gets the type of the state.
	 * 
	 * @return type the type of state, e.g. complex.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the state.
	 * 
	 * @param type the type of state, e.g. complex.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the list of comments.
	 * 
	 * @return comments the list of comments.
	 */
	public List<Comment> getCommentList() {
		return comments;
	}

	/**
	 * Gets the list of key value pair information properties.
	 * 
	 * @return properties the list of properties.
	 */
	public List<Property> getPropertyList() {
		return properties;
	}

	/**
	 * Gets the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotation references.
	 */
	public List<AnnotationRef> getAnnotationRefList() {
		return annotationRefs;
	}

	/**
	 * Gets the list of citation references.
	 * 
	 * @return citationRefs the list of citation references.
	 */
	public List<CitationRef> getCitationRefList() {
		return citationRefs;
	}

//	public List<AnnotationRef> getAnnotationRefList() {
//		if (annotationRefs == null) {
//			annotationRefs = new ArrayList<AnnotationRef>();
//		}
//		return this.annotationRefs;
//	}

}
