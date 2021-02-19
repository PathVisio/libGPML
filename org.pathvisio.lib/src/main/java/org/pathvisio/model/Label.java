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
 * This class stores all information relevant to a Label pathway element.
 * 
 * @author finterly
 */
public class Label {

	private Graphics graphics;
	private List<Comment> comments;
	private List<Property> properties;
	private List<AnnotationRef> annotationRefs;
	private List<CitationRef> citationRefs;
	private String href;
	private String elementId;
	private String groupRef;
	private String textLabel;

	// Add Constructors

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	
	/**
	 * Gets the elementId of the label.
	 * 
	 * @return elementId the unique id of the label.
	 * 
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the label.
	 * 
	 * @param elementId the unique id of the label.
	 * 
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the groupRef of the label. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @return groupRef the groupRef of the label.
	 * 
	 */
	public Object getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the groupRef of the label. A groupRef indicates an object is part of a
	 * gpml:Group with a elementId.
	 * 
	 * @param groupRef the groupRef of the label.
	 * 
	 */
	public void getGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}


	/**
	 * Gets the text of of the label.
	 * 
	 * @return textLabel the text of of the label.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the label.
	 * 
	 * @param textLabel the text of of the label.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
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




//	@XmlAccessorType(XmlAccessType.FIELD)
//	@XmlType(name = "")
//	public static class Graphics {
//
//		@XmlAttribute(name = "centerX", required = true)
//		protected float centerX;
//		@XmlAttribute(name = "centerY", required = true)
//		protected float centerY;
//		@XmlAttribute(name = "width", required = true)
//		protected float width;
//		@XmlAttribute(name = "height", required = true)
//		protected float height;
//		@XmlAttribute(name = "textColor")
//		protected String textColor;
//		@XmlAttribute(name = "fontName")
//		protected String fontName;
//		@XmlAttribute(name = "fontStyle")
//		protected String fontStyle;
//		@XmlAttribute(name = "fontDecoration")
//		protected String fontDecoration;
//		@XmlAttribute(name = "fontStrikethru")
//		protected String fontStrikethru;
//		@XmlAttribute(name = "fontWeight")
//		protected String fontWeight;
//		@XmlAttribute(name = "fontSize")
//		@XmlSchemaType(name = "nonNegativeInteger")
//		protected BigInteger fontSize;
//		@XmlAttribute(name = "hAlign")
//		protected String hAlign;
//		@XmlAttribute(name = "vAlign")
//		protected String vAlign;
//		@XmlAttribute(name = "borderColor")
//		protected String borderColor;
//		@XmlAttribute(name = "borderStyle")
//		protected StyleType borderStyle;
//		@XmlAttribute(name = "borderWidth")
//		protected Float borderWidth;
//		@XmlAttribute(name = "fillColor")
//		protected String fillColor;
//		@XmlAttribute(name = "shapeType")
//		protected String shapeType;
//		@XmlAttribute(name = "zOrder")
//		protected BigInteger zOrder;
//
//		/**
//		 * Gets the value of the centerX property.
//		 * 
//		 */
//		public float getCenterX() {
//			return centerX;
//		}
//
//		/**
//		 * Sets the value of the centerX property.
//		 * 
//		 */
//		public void setCenterX(float value) {
//			this.centerX = value;
//		}
//
//		/**
//		 * Gets the value of the centerY property.
//		 * 
//		 */
//		public float getCenterY() {
//			return centerY;
//		}
//
//		/**
//		 * Sets the value of the centerY property.
//		 * 
//		 */
//		public void setCenterY(float value) {
//			this.centerY = value;
//		}
//
//		/**
//		 * Gets the value of the width property.
//		 * 
//		 */
//		public float getWidth() {
//			return width;
//		}
//
//		/**
//		 * Sets the value of the width property.
//		 * 
//		 */
//		public void setWidth(float value) {
//			this.width = value;
//		}
//
//		/**
//		 * Gets the value of the height property.
//		 * 
//		 */
//		public float getHeight() {
//			return height;
//		}
//
//		/**
//		 * Sets the value of the height property.
//		 * 
//		 */
//		public void setHeight(float value) {
//			this.height = value;
//		}
//
//		/**
//		 * Gets the value of the textColor property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getTextColor() {
//			if (textColor == null) {
//				return "White";
//			} else {
//				return textColor;
//			}
//		}
//
//		/**
//		 * Sets the value of the textColor property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setTextColor(String value) {
//			this.textColor = value;
//		}
//
//		/**
//		 * Gets the value of the fontName property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getFontName() {
//			if (fontName == null) {
//				return "Arial";
//			} else {
//				return fontName;
//			}
//		}
//
//		/**
//		 * Sets the value of the fontName property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setFontName(String value) {
//			this.fontName = value;
//		}
//
//		/**
//		 * Gets the value of the fontStyle property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getFontStyle() {
//			if (fontStyle == null) {
//				return "Normal";
//			} else {
//				return fontStyle;
//			}
//		}
//
//		/**
//		 * Sets the value of the fontStyle property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setFontStyle(String value) {
//			this.fontStyle = value;
//		}
//
//		/**
//		 * Gets the value of the fontDecoration property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getFontDecoration() {
//			if (fontDecoration == null) {
//				return "Normal";
//			} else {
//				return fontDecoration;
//			}
//		}
//
//		/**
//		 * Sets the value of the fontDecoration property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setFontDecoration(String value) {
//			this.fontDecoration = value;
//		}
//
//		/**
//		 * Gets the value of the fontStrikethru property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getFontStrikethru() {
//			if (fontStrikethru == null) {
//				return "Normal";
//			} else {
//				return fontStrikethru;
//			}
//		}
//
//		/**
//		 * Sets the value of the fontStrikethru property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setFontStrikethru(String value) {
//			this.fontStrikethru = value;
//		}
//
//		/**
//		 * Gets the value of the fontWeight property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getFontWeight() {
//			if (fontWeight == null) {
//				return "Normal";
//			} else {
//				return fontWeight;
//			}
//		}
//
//		/**
//		 * Sets the value of the fontWeight property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setFontWeight(String value) {
//			this.fontWeight = value;
//		}
//
//		/**
//		 * Gets the value of the fontSize property.
//		 * 
//		 * @return possible object is {@link BigInteger }
//		 * 
//		 */
//		public BigInteger getFontSize() {
//			if (fontSize == null) {
//				return new BigInteger("12");
//			} else {
//				return fontSize;
//			}
//		}
//
//		/**
//		 * Sets the value of the fontSize property.
//		 * 
//		 * @param value allowed object is {@link BigInteger }
//		 * 
//		 */
//		public void setFontSize(BigInteger value) {
//			this.fontSize = value;
//		}
//
//		/**
//		 * Gets the value of the hAlign property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getHAlign() {
//			if (hAlign == null) {
//				return "Center";
//			} else {
//				return hAlign;
//			}
//		}
//
//		/**
//		 * Sets the value of the hAlign property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setHAlign(String value) {
//			this.hAlign = value;
//		}
//
//		/**
//		 * Gets the value of the vAlign property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getVAlign() {
//			if (vAlign == null) {
//				return "Middle";
//			} else {
//				return vAlign;
//			}
//		}
//
//		/**
//		 * Sets the value of the vAlign property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setVAlign(String value) {
//			this.vAlign = value;
//		}
//
//		/**
//		 * Gets the value of the borderColor property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getBorderColor() {
//			if (borderColor == null) {
//				return "Black";
//			} else {
//				return borderColor;
//			}
//		}
//
//		/**
//		 * Sets the value of the borderColor property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setBorderColor(String value) {
//			this.borderColor = value;
//		}
//
//		/**
//		 * Gets the value of the borderStyle property.
//		 * 
//		 * @return possible object is {@link StyleType }
//		 * 
//		 */
//		public StyleType getBorderStyle() {
//			if (borderStyle == null) {
//				return StyleType.SOLID;
//			} else {
//				return borderStyle;
//			}
//		}
//
//		/**
//		 * Sets the value of the borderStyle property.
//		 * 
//		 * @param value allowed object is {@link StyleType }
//		 * 
//		 */
//		public void setBorderStyle(StyleType value) {
//			this.borderStyle = value;
//		}
//
//		/**
//		 * Gets the value of the borderWidth property.
//		 * 
//		 * @return possible object is {@link Float }
//		 * 
//		 */
//		public float getBorderWidth() {
//			if (borderWidth == null) {
//				return 1.0F;
//			} else {
//				return borderWidth;
//			}
//		}
//
//		/**
//		 * Sets the value of the borderWidth property.
//		 * 
//		 * @param value allowed object is {@link Float }
//		 * 
//		 */
//		public void setBorderWidth(Float value) {
//			this.borderWidth = value;
//		}
//
//		/**
//		 * Gets the value of the fillColor property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getFillColor() {
//			if (fillColor == null) {
//				return "White";
//			} else {
//				return fillColor;
//			}
//		}
//
//		/**
//		 * Sets the value of the fillColor property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setFillColor(String value) {
//			this.fillColor = value;
//		}
//
//		/**
//		 * Gets the value of the shapeType property.
//		 * 
//		 * @return possible object is {@link String }
//		 * 
//		 */
//		public String getShapeType() {
//			if (shapeType == null) {
//				return "Rectangle";
//			} else {
//				return shapeType;
//			}
//		}
//
//		/**
//		 * Sets the value of the shapeType property.
//		 * 
//		 * @param value allowed object is {@link String }
//		 * 
//		 */
//		public void setShapeType(String value) {
//			this.shapeType = value;
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
