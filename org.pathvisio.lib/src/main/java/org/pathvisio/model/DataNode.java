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

/**
 * This class stores information for a DataNode pathway element.
 * 
 * @author finterly
 */
public class DataNode extends ShapedElement {

	private String textLabel;
	private DataNodeType type = DataNodeType.UNKNOWN; // TODO: Getter/Setter weird
	private Xref xref;
	private List<State> states;
	/*
	 * . In GPML, this is elementRef which refers to the elementId of a pathway
	 * element (normally gpml:Group).
	 */
	private PathwayElement elementRef; // optional, the pathway element to which the data node refers to as an alias.

	/**
	 * 
	 * Instantiates a Data Node pathway element given all possible parameters. The
	 * data node is an alias and refers to another pathway element. In GPML, the
	 * datanode has elementRef which refers to the elementId of a pathway element
	 * (normally gpml:Group).
	 * 
	 * @param elementId          the unique pathway element identifier.
	 * @param pathwayModel       the parent pathway model.
	 * @param comments           the list of comments.
	 * @param dynamicProperties  the list of dynamic properties, key value pairs.
	 * @param annotationRefs     the list of annotations referenced.
	 * @param citationRefs       the list of citations referenced.
	 * @param evidenceRefs       the list of evidences referenced.
	 * @param rectProperty       the centering (position) and dimension properties.
	 * @param fontProperty       the font properties, e.g. textColor, fontName...
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor.
	 * @param groupRef              the parent group in which the pathway element
	 *                           belongs.
	 * @param textLabel          the text of the datanode.
	 * @param type               the type of datanode, e.g. complex.
	 * @param xref               the data node Xref.
	 * @param states             the list of states of the data node.
	 * @param elementRef         the pathway element the data node refers to.
	 */
	public DataNode(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Group groupRef, String textLabel, DataNodeType type, Xref xref,
			List<State> states, PathwayElement elementRef) {
		super(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, groupRef);
		this.textLabel = textLabel;
		this.type = type;
		this.xref = xref;
		this.states = new ArrayList<State>();
		this.elementRef = elementRef;
	}

	/**
	 * Instantiates a DataNode given all possible parameters except elementRef,
	 * because the data node does not refer to another pathway element.
	 */
	public DataNode(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, Group groupRef, String textLabel, DataNodeType type, Xref xref,
			List<State> states) {
		this(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, groupRef, textLabel, type, xref, states, null);

	}

	/**
	 * Instantiates a DataNode given all possible parameters except groupRef,
	 * because the data node does not belong in a group.
	 */
	public DataNode(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, String textLabel, DataNodeType type, Xref xref, List<State> states,
			PathwayElement elementRef) {
		this(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, null, textLabel, type, xref, states, elementRef);
	}

	/**
	 * Instantiates a DataNode given all possible parameters except groupRef and
	 * elementRef, because the data node neither belongs in a group nor refers
	 * to another pathway element.
	 */
	public DataNode(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, String textLabel, DataNodeType type, Xref xref, List<State> states) {
		this(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, null, textLabel, type, xref, states, null);
	}

	/**
	 * Returns the text of of the datanode.
	 * 
	 * @return textLabel the text of of the datanode.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the datanode.
	 * 
	 * @param textLabel the text of of the datanode.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Returns the type of the datanode.
	 * 
	 * @return type the type of datanode, e.g. complex.
	 */
	public DataNodeType getType() {
		return type;
	}

	/**
	 * Sets the type of the datanode.
	 * 
	 * @param type the type of datanode, e.g. complex.
	 */
	public void setType(DataNodeType type) {
		this.type = type; // TODO: default type
		this.type = type;
	}

	/**
	 * Returns the Xref for the data node.
	 * 
	 * @return xref the xref of the data node.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates data node Xref given identifier and dataSource. Checks whether
	 * dataSource string is fullName, systemCode, or invalid.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 * @throws IllegalArgumentException is given dataSource does not exist.
	 */
	public void setXref(String identifier, String dataSource) {
		if (DataSource.fullNameExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		} else if (DataSource.systemCodeExists(dataSource)) {
			xref = new Xref(identifier, DataSource.getByAlias(dataSource));
		} else {
			throw new IllegalArgumentException("Invalid xref dataSource: " + dataSource);
		}
	}

	/*
	 * Returns the list of states of the data node.
	 * 
	 * @return states the list of states.
	 */
	public List<State> getStates() {
		return states;
	}

	/**
	 * Adds given state to states list.
	 * 
	 * @param state the state to be added.
	 */
	public void addState(State state) {
		states.add(state);
	}

	/**
	 * Removes given state to states list.
	 * 
	 * @param state the state to be removed.
	 */
	public void removeState(State state) {
		states.remove(state);
	}

	/**
	 * Returns the pathway element to which the data node refers to as an alias. In
	 * GPML, this is elementRef which refers to the elementId of a pathway element
	 * (normally gpml:Group). TODO
	 * 
	 * @return elementRef the pathway element to which the data node refers.
	 */
	public PathwayElement getElementRef() {
		return elementRef;
	}

	/**
	 * Sets the pathway element to which the data node refers to as an alias. In
	 * GPML, this is elementRef which refers to the elementId of a pathway element
	 * (normally gpml:Group). TODO
	 * 
	 * @param elementRef the pathway element to which the data node refers.
	 */
	public void setElementRef(PathwayElement elementRef) {
		this.elementRef = elementRef;
	}

//	/**
//	 * Return a list of ElementRefContainers (i.e. points) referring to this pathway
//	 * element.
//	 */
//	public Set<ElementRefContainer> getReferences() {
//		return ElementLink.getReferences(this, pathwayModel);
//	}
//
//	/** elementRef property, used by Modification */
//	public String getElementRef() {
//		return elementRef;
//	}
//
//	/**
//	 * Set graphRef property, used by State The new graphRef should exist and point
//	 * to an existing DataNode
//	 */
//	public void setElementRef(String value) {
//		// TODO: check that new elementRef exists and that it points to a DataNode
//		if (!(elementRef == null ? value == null : elementRef.equals(value))) {
//			elementRef = value;
//		}
//	}

}
