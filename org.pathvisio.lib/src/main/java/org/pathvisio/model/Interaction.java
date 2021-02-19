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

/**
 * This class stores all information relevant to an Interaction pathway element.
 * 
 * @author finterly
 */
public class Interaction {

	private Xref xref;
	private List<Point> points;
	private List<Anchor> anchors;
	private Graphics graphics;
	private List<Comment> comments;
	private List<Property> properties;
	private List<AnnotationRef> annotationRefs;
	private List<CitationRef> citationRefs;
	private String elementId;
	private String groupRef;
	private String type;

	// Add Constructors

	/**
	 * Gets the elementId of the interaction.
	 * 
	 * @return elementId the unique id of the interaction.
	 * 
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the interaction.
	 * 
	 * @param elementId the unique id of the interaction.
	 * 
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the groupRef of the interaction. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the interaction.
	 * 
	 */
	public Object getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the interaction. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the interaction.
	 * 
	 */
	public void getGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * Gets the type of the interaction.
	 * 
	 * @return type the type of interaction, e.g. complex.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the interaction.
	 * 
	 * @param type the type of interaction, e.g. complex.
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
}

//	@XmlAccessorType(XmlAccessType.FIELD)
//	@XmlType(name = "", propOrder = { "anchor" })
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
