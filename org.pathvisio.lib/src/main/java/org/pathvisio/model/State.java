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

import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * This class stores all information relevant to a State pathway element.
 * 
 * @author finterly
 */
public class State extends ShapedElement {


	private DataNode dataNode; // parent dataNode (NB: elementRef was formerly elementId of parent data node)
	private String textLabel;
	private StateType type = StateType.PHOSPHORYLATED; // TODO: Getter/Setter weird
	private double relX; //
	private double relY;
	private Xref xref; // optional

	/**
	 * Instantiates a State pathway element given all possible parameters.
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
	 * @param shapeStyleProperty the shape style properties, e.g. borderColor...
	 * @param dataNode           the parent data node (NB: elementRef was formerly
	 *                           elementId of parent data node).
	 * @param textLabel          the text label of the state.
	 * @param type               the type of the state, e.g. protein modification.
	 * @param relX               the relative x coordinates on the parent object,
	 *                           where 0,0 is at the center of the object and 1,1 at
	 *                           the bottom-right corner of the object.
	 * @param relY               the relative y coordinates on the parent object,
	 *                           where 0,0 is at the center of the object and 1,1 at
	 *                           the bottom-right corner of the object.
	 * @param xref               the state xref.
	 */
	public State(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, DataNode dataNode, String textLabel, StateType type, double relX,
			double relY, Xref xref) {
		super(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty);
		this.dataNode = dataNode;
		this.textLabel = textLabel;
		this.type = type;
		this.relX = relX;
		this.relY = relY;
		this.xref = xref;
	}

	/**
	 * Instantiates a State pathway element given all possible parameters except
	 * xref.
	 */
	public State(String elementId, PathwayModel pathwayModel, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<Citation> citationRefs,
			List<Evidence> evidenceRefs, RectProperty rectProperty, FontProperty fontProperty,
			ShapeStyleProperty shapeStyleProperty, DataNode dataNode, String textLabel, StateType type, double relX,
			double relY) {
		this(elementId, pathwayModel, comments, dynamicProperties, annotationRefs, citationRefs, evidenceRefs,
				rectProperty, fontProperty, shapeStyleProperty, dataNode, textLabel, type, relX, relY, null);
	}

	/**
	 * Returns the parent data node to which the state belongs.
	 * 
	 * NB: prior to GPML2021, elementRef was used to refer to the elementId of
	 * parent data node.
	 * 
	 * @return dataNode the parent data node of the state.
	 */
	public DataNode getDataNode() {
		return dataNode;
	}

	/**
	 * Sets the parent data node to which the state belongs.
	 * 
	 * NB: prior to GPML2021, elementRef was used to refer to the elementId of
	 * parent data node.
	 * 
	 * @param dataNode the parent data node of the state.
	 */
	public void setDataNode(DataNode dataNode) {
		this.dataNode = dataNode;
	}

	/**
	 * Returns the text of of the state.
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
	 * Returns the type of the state.
	 * 
	 * @return type the type of state, e.g. complex.
	 */
	public StateType getType() {
		return type;
	}

	/**
	 * Sets the type of the state.
	 * 
	 * @param type the type of state, e.g. complex.
	 */
	public void setType(StateType type) {
		this.type = type;
	}

	/**
	 * Returns the relative x coordinate. When the given point is linked to a
	 * pathway element, relX and relY are the relative coordinates on the element,
	 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
	 * of the object.
	 * 
	 * @param relX the relative x coordinate.
	 */
	public double getRelX() {
		return relX;
	}

	/**
	 * Sets the relative x coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relX the relative x coordinate.
	 * @throws IllegalArgumentException if relX is not between -1.0 and 1.0. t
	 */
	public void setRelX(double relX) {
		if (Math.abs(relX) <= 1.0) {
			this.relX = relX;
		} else {
			throw new IllegalArgumentException("relX " + relX + " should be between -1.0 and 1.0");
		}
	}

	/**
	 * Returns the relative y coordinate. When the given point is linked to a
	 * pathway element, relX and relY are the relative coordinates on the element,
	 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
	 * of the object.
	 * 
	 * @param relY the relative y coordinate.
	 */
	public double getRelY() {
		return relY;
	}

	/**
	 * Sets the relative y coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relY the relative y coordinate.
	 */
	public void setRelY(double relY) {
		if (Math.abs(relY) <= 1.0) {
			this.relY = relY;
		} else {
			throw new IllegalArgumentException("relY " + relY + " should be between -1.0 and 1.0");
		}
	}

	/**
	 * Returns the Xref for the state.
	 * 
	 * @return xref the xref of the state.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates state Xref given identifier and dataSource. Checks whether
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

}
