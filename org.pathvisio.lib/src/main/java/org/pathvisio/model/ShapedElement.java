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

import java.awt.Color;
import java.util.Set;

import org.pathvisio.model.GraphLink.LinkableFrom;
import org.pathvisio.model.GraphLink.LinkableTo;
import org.pathvisio.model.graphics.Coordinate;
import org.pathvisio.model.ref.ElementInfo;
import org.pathvisio.model.type.HAlignType;
import org.pathvisio.model.type.LineStyleType;
import org.pathvisio.model.type.ShapeType;
import org.pathvisio.model.type.VAlignType;

/**
 * This class stores information for shaped pathway element {@link DataNode},
 * {@link Label}, {@link Shape}, and {@link Group}.
 * 
 * @author finterly
 */
public abstract class ShapedElement extends ElementInfo implements LinkableTo, Groupable {

	private Group groupRef; // optional, the parent group to which a pathway element belongs.

	// rect properties
	private Coordinate centerXY;
	private double width;
	private double height;
	// font properties
	private Color textColor = Color.decode("#000000"); // black
	private String fontName = "Arial"; // Arial
	private boolean fontWeight = false; // bold or normal
	private boolean fontStyle = false; // italic or normal
	private boolean fontDecoration = false; // underline or normal
	private boolean fontStrikethru = false;// strikethru or normal
	private int fontSize = 12; // 12, only integer full size values
	private HAlignType hAlign = HAlignType.CENTER; // horizontal alignment of text
	private VAlignType vAlign = VAlignType.MIDDLE; // vertical alignment of text
	// shape style properties
	private Color borderColor = Color.decode("#000000"); // black
	private LineStyleType borderStyle = LineStyleType.SOLID; // solid TODO: Fix
	private double borderWidth = 1.0; // TODO: type?
	private Color fillColor = Color.decode("#ffffff"); // white TODO: Transparent if Label
	private ShapeType shapeType = ShapeType.RECTANGLE; // rectangle TODO: NONE if Label.
	private int zOrder; // optional
	private double rotation; // optional, in radians

	/**
	 * Instantiates a shaped pathway element. Property groupRef is to be set by
	 * {@link #setGroupRefTo(Group)}. In GPML, groupRef refers to the elementId
	 * (formerly groupId) of the parent gpml:Group. Note, a group can also belong in
	 * another group.
	 */
	public ShapedElement() {
		super();
	}

	/**
	 * Returns the center x and y coordinate of an object.
	 * 
	 * @return centerXY the middle of an object in the x and y direction.
	 */
	public Coordinate getCenterXY() {
		return centerXY;
	}

	/**
	 * Sets the center x and y coordinate of an object.
	 * 
	 * @param centerXY the middle of an object in the x and y direction.
	 */
	public void setCenterXY(Coordinate centerXY) {
		this.centerXY = centerXY;
	}

	/**
	 * Returns the width of an object.
	 * 
	 * @return width the width of an object.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the width of an object.
	 * 
	 * @param width the width of an object.
	 * @throws IllegalArgumentException if width is a negative value.
	 */
	public void setWidth(double width) {
		if (width < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + width);
		} else {
			this.width = width;
		}
	}

	/**
	 * Returns the height of an object.
	 * 
	 * @return height the height of an object.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the height of an object.
	 * 
	 * @param height the height of an object.
	 * @throws IllegalArgumentException if height is a negative value.
	 */
	public void setHeight(double height) {
		if (height < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + height);
		} else {
			this.height = height;
		}
	}

	/**
	 * Returns the color of text.
	 * 
	 * @return textColor the color of text.
	 */
	public Color getTextColor() {
		if (textColor == null) {
			return Color.decode("#000000"); // black
		} else {
			return textColor;
		}
	}

	/**
	 * Sets the color of text.
	 * 
	 * @param textColor the color of text
	 * @throws IllegalArgumentException if color null.
	 */
	public void setTextColor(Color textColor) {
		if (textColor == null) {
			throw new IllegalArgumentException();
		} else {
			this.textColor = textColor;
		}
	}

	/**
	 * Returns the name of the set of printable text characters to be used for
	 * visualization, e.g., Arial.
	 * 
	 * @return fontName the name of the font.
	 */
	public String getFontName() {
		if (fontName == null) {
			return "Arial";
		} else {
			return fontName;
		}
	}

	/**
	 * Sets the name of the set of printable text characters to be used for
	 * visualization, e.g., Arial.
	 * 
	 * @param fontName the name of the font.
	 * @throws IllegalArgumentException if given fontName is null.
	 */
	public void setFontName(String fontName) {
		if (fontName == null) {
			throw new IllegalArgumentException();
		} else {
			this.fontName = fontName;
		}
	}

	/**
	 * Returns the thickness of the font used, a bold font would have more weight.
	 * 
	 * @return fontWeight the boolean, if true font weight is bold. If false, font
	 *         weight is normal.
	 * 
	 */
	public boolean getFontWeight() {
		return fontWeight;
	}

	/**
	 * Sets the thickness of the font used, a bold font would have more weight.
	 * 
	 * @param fontWeight the boolean, if true font weight is bold. If false, font
	 *                   weight is normal.
	 */
	public void setFontWeight(boolean fontWeight) {
		this.fontWeight = fontWeight;
	}

	/**
	 * Returns the typographic style applied to displayed text, e.g. normal or
	 * italic.
	 * 
	 * @return fontStyle the boolean, if true typographic style is italic. If false,
	 *         typographic style is normal.
	 * 
	 */
	public boolean getFontStyle() {
		return fontStyle;
	}

	/**
	 * Sets the typographic style applied to displayed text, e.g. normal or italic.
	 * 
	 * @param fontStyle the boolean, if true typographic style is italic. If false,
	 *                  typographic style is normal.
	 */
	public void setFontStyle(boolean fontStyle) {
		this.fontStyle = fontStyle;
	}

	/**
	 * Returns the typographic style for underline or normal.
	 * 
	 * @return fontDecoration the boolean, if true typographic style is underline.
	 *         If false, typographic style is normal.
	 * 
	 */
	public boolean getFontDecoration() {
		return fontDecoration;
	}

	/**
	 * Sets the typographic style for underline or normal.
	 * 
	 * @param fontDecoration the boolean, if true typographic style is underline. If
	 *                       false, typographic style is normal.
	 */
	public void setFontDecoration(boolean fontDecoration) {
		this.fontDecoration = fontDecoration;
	}

	/**
	 * Returns the typographic style for strikethru or normal.
	 * 
	 * @return fontStrikethru the boolean, if true typographic style is strikethru.
	 *         If false, typographic style is normal.
	 */
	public boolean getFontStrikethru() {
		return fontStrikethru;
	}

	/**
	 * Sets the typographic style for strikethru or normal.
	 * 
	 * @param fontStrikethru the boolean, if true typographic style is strikethru.
	 *                       If false, typographic style is normal.
	 */
	public void setFontStrikethru(boolean fontStrikethru) {
		this.fontStrikethru = fontStrikethru;
	}

	/**
	 * Returns the point value for the size of the font.
	 * 
	 * @return fontSize the value for the size of the font.
	 * 
	 */
	public double getFontSize() {
		if (fontSize < 0) {
			return 12;
		} else {
			return fontSize;
		}
	}

	/**
	 * Sets point value for the size of the font.
	 * 
	 * @param fontSize the value for the size of the font.
	 * @throws IllegalArgumentException if fontSize is a negative value.
	 */
	public void setFontSize(int fontSize) {
		if (fontSize < 0) {
			throw new IllegalArgumentException("Tried to set font size < 0: " + fontSize);
		} else {
			this.fontSize = fontSize;
		}
	}

	/**
	 * Returns the horizontal alignment of displayed text, e.g., Left, Center,
	 * Right.
	 * 
	 * @return hAlign the horizontal alignment value of displayed text.
	 */
	public HAlignType getHAlign() {
		if (hAlign == null) {
			return HAlignType.CENTER;
		} else {
			return hAlign;
		}
	}

	/**
	 * Sets the horizontal alignment of displayed text, e.g., Left, Center, Right.
	 * 
	 * @param hAlign the horizontal alignment value of displayed text.
	 */
	public void setHAlign(HAlignType hAlign) {
		this.hAlign = hAlign;
	}

	/**
	 * Returns the vertical alignment of displayed text, e.g., Top, Middle, Bottom.
	 * 
	 * @return vAlign the vertical alignment value of displayed text.
	 */
	public VAlignType getVAlign() {
		if (vAlign == null) {
			return VAlignType.MIDDLE;
		} else {
			return vAlign;
		}
	}

	/**
	 * Sets the vertical alignment of displayed text, e.g., Top, Middle, Bottom.
	 * 
	 * @param vAlign the vertical alignment value of displayed text.
	 * 
	 */
	public void setVAlign(VAlignType vAlign) {
		this.vAlign = vAlign;
	}

	/**
	 * Returns the border color of an object.
	 * 
	 * @return borderColor the border color of an object.
	 */
	public Color getBorderColor() {
		if (borderColor == null) {
			return Color.decode("#000000"); // black
		} else {
			return borderColor;
		}
	}

	/**
	 * Sets the border color of an object.
	 * 
	 * @param borderColor the border color of an object.
	 * @throws IllegalArgumentException if color null.
	 */
	public void setBorderColor(Color borderColor) {
		if (borderColor == null) {
			throw new IllegalArgumentException();
		} else {
			this.borderColor = borderColor;
		}
	}

	/**
	 * Returns the visual appearance of a border, e.g. Solid or Broken.
	 * 
	 * @return borderStyle the style of a border.
	 */
	public LineStyleType getBorderStyle() {
		if (borderStyle == null) {
			return LineStyleType.SOLID;
		} else {
			return borderStyle;
		}
	}

	/**
	 * Sets the visual appearance of a border, e.g. Solid or Broken.
	 * 
	 * @param borderStyle the style of a border.
	 */
	public void setBorderStyle(LineStyleType borderStyle) {
		this.borderStyle = borderStyle;
	}

	/**
	 * Returns the pixel value for the border of an object.
	 * 
	 * @return borderWidth the width of a border.
	 */
	public double getBorderWidth() {
		if (borderWidth < 0) {
			return 1.0; // TODO: Can borderWidth be zero?
		} else {
			return borderWidth;
		}
	}

	/**
	 * Sets the pixel value for the border of an object.
	 * 
	 * @param borderWidth the width of a border.
	 * @throws IllegalArgumentException if borderWidth is a negative value.
	 */
	public void setBorderWidth(double borderWidth) {
		if (borderWidth < 0) {
			throw new IllegalArgumentException();
		} else {
			this.borderWidth = borderWidth;
		}
	}

	/**
	 * Returns the color used to paint the area of an object, not including its
	 * border.
	 * 
	 * @return fillColor the fill color of an object.
	 */
	public Color getFillColor() {
		if (fillColor == null) {
			return Color.decode("#ffffff"); // white
		} else {
			return fillColor;
		}
	}

	/**
	 * Sets the color used to paint the area of an object, not including its border.
	 * 
	 * @param fillColor the fill color of an object.
	 * @throws IllegalArgumentException if fillColor null.
	 */
	public void setFillColor(Color fillColor) {
		if (fillColor == null) {
			throw new IllegalArgumentException();
		} else {
			this.fillColor = fillColor;
		}
	}

	/**
	 * Returns the visual appearance of a two dimensional object, e.g. Rectangle,
	 * Arc, Mitochondria, Oval.
	 * 
	 * NB: Shape.type is for object type while shapeType is the visual appearance.
	 * For example, an object may have Shape.type "Nucleus" and shapeType "Oval".
	 * 
	 * @return shapeType the visual appearance of an object.
	 */
	public ShapeType getShapeType() {
		if (shapeType == null) {
			return ShapeType.RECTANGLE;
		} else {
			return shapeType;
		}
	}

	/**
	 * Returns the visual appearance of a two dimensional object, e.g. Rectangle,
	 * Arc, Mitochondria, Oval.
	 * 
	 * NB: Shape.type is for object type while shapeType is the visual appearance.
	 * For example, an object may have Shape.type "Nucleus" and shapeType "Oval".
	 * 
	 * @param shapeType the visual appearance of an object.
	 * @throws IllegalArgumentException if shapeType null.
	 */
	public void setShapeType(ShapeType shapeType) {
		if (shapeType == null) {
			throw new IllegalArgumentException();
		} else {
			this.shapeType = shapeType;
		}
	}

	/**
	 * Returns the z-order of an object.
	 * 
	 * @return zOrder the order of an object.
	 */
	public int getZOrder() {
		return zOrder;
	}

	/**
	 * Sets the z-order of an object.
	 * 
	 * @param zOrder the order of an object.
	 */
	public void setZOrder(int zOrder) {
		this.zOrder = zOrder;
	}

	/**
	 * Returns the rotation of this object.
	 * 
	 * @return rotation the rotation of the object.
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Sets the rotation of this object.
	 * 
	 * @param rotation the rotation of the object.
	 */
	public void setRotation(Double rotation) {
		this.rotation = rotation;
	}

	/**
	 * Returns the parent group of the pathway element. In GPML, groupRef refers to
	 * the elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of the pathway element.
	 */
	public Group getGroupRef() {
		return groupRef;
	}

	/**
	 * Checks whether this pathway element belongs to a group.
	 *
	 * @return true if and only if the group of this pathway element is effective.
	 */
	public boolean hasGroupRef() {
		return getGroupRef() != null;
	}

	/**
	 * Verifies if given parent group is new and valid. Sets the parent group of the
	 * pathway element. Adds this pathway element to the the pathwayElements list of
	 * the new parent group. If there is an old parent group, this pathway element
	 * is removed from its pathwayElements list.
	 * 
	 * @param groupRef the new parent group to set.
	 */
	public void setGroupRefTo(Group groupRef) {
		if (groupRef == null)
			throw new IllegalArgumentException("Invalid group.");
		unsetGroupRef(); // first unsets if necessary
		setGroupRef(groupRef);
		if (!groupRef.hasPathwayElement(this))
			groupRef.addPathwayElement(this);
	}

	/**
	 * Sets the parent group for this pathway element.
	 * 
	 * @param groupRef the given group to set.
	 */
	private void setGroupRef(Group groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	public void unsetGroupRef() {
		if (hasGroupRef()) {
			Group groupRef = getGroupRef();
			setGroupRef(null);
			if (groupRef.hasPathwayElement(this))
				groupRef.removePathwayElement(this);
		}
	}

	/**
	 * Returns {@link LinkableFrom} pathway elements, at this time that only goes
	 * for {@link LinePoint}, for this {@link LinkableTo} pathway element.
	 */
	@Override
	public Set<LinkableFrom> getLinkableFroms() {
		return GraphLink.getReferences(this, getPathwayModel());
	}

	/**
	 * Terminates this pathway element. The pathway model, if any, is unset from
	 * this pathway element. Links to all annotationRefs, citationRefs, and
	 * evidenceRefs are removed from this data node.
	 */
	@Override
	public void terminate() {
		removeAnnotationRefs();
		removeCitationRefs();
		removeEvidenceRefs();// TODO
		unsetGroupRef();
		unsetPathwayModel();
	}

}
