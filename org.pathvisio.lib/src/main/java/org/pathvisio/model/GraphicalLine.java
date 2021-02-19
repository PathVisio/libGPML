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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * 
 */
public class GraphicalLine {

	private List<Point> points;
	private List<Anchor> anchors;
	private Graphics graphics;
	private List<Comment> comments; //optional
	private List<Property> properties; //optional
	private List<AnnotationRef> annotationRefs; //optional
	private List<CitationRef> citationRefs; //optional
	private String elementId;
	private String elementRef; //optional
//	protected String type; //debate
	
	
	// Add Constructors

	
	/**
	 * Gets the elementId of the graphical line.
	 * 
	 * @return elementId the unique id of the graphical line.
	 * 
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the graphical line.
	 * 
	 * @param elementId the unique id of the graphical line.
	 * 
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the groupRef of the graphical line. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the graphical line.
	 * 
	 */
	public Object getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the graphical line. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the graphical line.
	 * 
	 */
	public void getGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * Gets the graphical line Xref.
	 * 
	 * @return xref the graphical line xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of graphical line Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}

	/**
	 * Gets the text of of the graphical line.
	 * 
	 * @return textLabel the text of of the graphical line.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the graphical line.
	 * 
	 * @param textLabel the text of of the graphical line.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

//	/**
//	 * Gets the type of the graphical line.
//	 * 
//	 * @return type the type of graphical line, e.g. complex.
//	 */
//	public String getType() {
//		return type;
//	}
//
//	/**
//	 * Sets the type of the graphical line.
//	 * 
//	 * @param type the type of graphical line, e.g. complex.
//	 */
//	public void setType(String type) {
//		this.type = type;
//	}

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


//	public static class Graphics {
//
//		@XmlElement(name = "Anchor")
//		protected List<Anchor> anchor;
//		@XmlAttribute(name = "lineColor")
//		protected String lineColor;
//		@XmlAttribute(name = "lineStyle")
//		protected StyleType lineStyle;
//		@XmlAttribute(name = "lineWidth")
//		protected Float lineWidth;
//		@XmlAttribute(name = "connectorType")
//		protected String connectorType;
//		@XmlAttribute(name = "zOrder")
//		protected BigInteger zOrder;
//
//		/**
//		 * Gets the value of the anchor property.
//		 * 
//		 * <p>
//		 * This accessor method returns a reference to the live list, not a snapshot.
//		 * Therefore any modification you make to the returned list will be present
//		 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
//		 * for the anchor property.
//		 * 
//		 * <p>
//		 * For example, to add a new item, do as follows:
//		 * 
//		 * <pre>
//		 * getAnchor().add(newItem);
//		 * </pre>
//		 * 
//		 * 
//		 * <p>
//		 * Objects of the following type(s) are allowed in the list {@link Anchor }
//		 * 
//		 * 
//		 */
//		public List<Anchor> getAnchor() {
//			if (anchor == null) {
//				anchor = new ArrayList<Anchor>();
//			}
//			return this.anchor;
//		}
//
//		/**
//		 * Gets the value of the lineColor property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getLineColor() {
//			if (lineColor == null) {
//				return "Black";
//			} else {
//				return lineColor;
//			}
//		}
//
//		/**
//		 * Sets the value of the lineColor property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setLineColor(String value) {
//			this.lineColor = value;
//		}
//
//		/**
//		 * Gets the value of the lineStyle property.
//		 * 
//		 * @return possible object is {@link StyleType }
//		 * 
//		 */
//		public StyleType getLineStyle() {
//			if (lineStyle == null) {
//				return StyleType.SOLID;
//			} else {
//				return lineStyle;
//			}
//		}
//
//		/**
//		 * Sets the value of the lineStyle property.
//		 * 
//		 * @param value allowed object is {@link StyleType }
//		 * 
//		 */
//		public void setLineStyle(StyleType value) {
//			this.lineStyle = value;
//		}
//
//		/**
//		 * Gets the value of the lineWidth property.
//		 * 
//		 * @return possible object is {@link Float }
//		 * 
//		 */
//		public float getLineWidth() {
//			if (lineWidth == null) {
//				return 1.0F;
//			} else {
//				return lineWidth;
//			}
//		}
//
//		/**
//		 * Sets the value of the lineWidth property.
//		 * 
//		 * @param value allowed object is {@link Float }
//		 * 
//		 */
//		public void setLineWidth(Float value) {
//			this.lineWidth = value;
//		}
//
//		/**
//		 * Gets the value of the connectorType property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getConnectorType() {
//			if (connectorType == null) {
//				return "Straight";
//			} else {
//				return connectorType;
//			}
//		}
//
//		/**
//		 * Sets the value of the connectorType property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setConnectorType(String value) {
//			this.connectorType = value;
//		}
//
//		/**
//		 * Gets the value of the zOrder property.
//		 * 
//		 * @return possible object is {@link BigInteger }
//		 * 
//		 */
//		public BigInteger getZOrder() {
//			return zOrder;
//		}
//
//		/**
//		 * Sets the value of the zOrder property.
//		 * 
//		 * @param value allowed object is {@link BigInteger }
//		 * 
//		 */
//		public void setZOrder(BigInteger value) {
//			this.zOrder = value;
//		}
//
//	}
//
//}
