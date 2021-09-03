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

import org.pathvisio.debug.Logger;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.model.GraphLink.LinkableFrom;
import org.pathvisio.model.GraphLink.LinkableTo;
import org.pathvisio.model.ref.PathwayElement;
import org.pathvisio.model.type.HAlignType;
import org.pathvisio.model.type.LineStyleType;
import org.pathvisio.model.type.ShapeType;
import org.pathvisio.model.type.VAlignType;
import org.pathvisio.props.StaticProperty;
import org.pathvisio.util.Utils;

/**
 * This class stores information for shaped pathway element {@link DataNode},
 * {@link DataNode.State}, {@link Label}, {@link Shape}, and {@link Group}.
 * 
 * @author finterly
 */
public abstract class ShapedElement extends PathwayElement implements LinkableTo, Groupable {

	private Group groupRef; // optional, the parent group to which a pathway element belongs.
	// rect properties
	private double centerX;
	private double centerY;
	private double width = 1.0;
	private double height = 1.0;
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
	private double rotation = 0; // optional, in radians

	/**
	 * Instantiates a shaped pathway element. Property groupRef is to be set by
	 * {@link #setGroupRefTo(Group)}. In GPML, groupRef refers to the elementId
	 * (formerly groupId) of the parent gpml:Group. Note, a group can also belong in
	 * another group. Graphics properties have default values and can be set after a
	 * shaped pathway element is already instantiated.
	 */
	public ShapedElement() {
		super();
	}

	/**
	 * Returns text label for shaped pathway elements. Text labels are required for
	 * {@link DataNode}, {@link DataNode.State} and {@link Label}; and optional for
	 * {@link Shape} and {@link Group}.
	 * 
	 * @return the text of of the shaped pathway element.
	 */
	public abstract String getTextLabel();

	/**
	 * Sets text label for this shaped pathway elements.
	 * 
	 * @param v the the text to set for this shaped pathway element.
	 */
	public abstract void setTextLabel(String v);

	/**
	 * Returns the parent group of this pathway element. In GPML, groupRef refers to
	 * the elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of this pathway element.
	 */
	@Override
	public Group getGroupRef() {
		return groupRef;
	}

	/**
	 * Checks whether this pathway element belongs to a group.
	 *
	 * @return true if and only if the group of this pathway element is effective.
	 */
	@Override
	public boolean hasGroupRef() {
		return getGroupRef() != null;
	}

	/**
	 * Verifies if given parent group is new and valid. Sets the parent group of
	 * this pathway element. Adds this pathway element to the the pathwayElements
	 * list of the new parent group. If there is an old parent group, this pathway
	 * element is removed from its pathwayElements list.
	 * 
	 * @param v the new parent group to set.
	 */
	@Override
	public void setGroupRefTo(Group v) {
		if (v == null)
			throw new IllegalArgumentException("Invalid group.");
		if (groupRef != v) {
			unsetGroupRef(); // first unsets if necessary
			setGroupRef(v);
			if (!v.hasPathwayElement(this))
				v.addPathwayElement(this);
		}
	}

	/**
	 * Sets the parent group for this pathway element.
	 * 
	 * @param v the given group to set.
	 */
	private void setGroupRef(Group v) {
		// TODO
		groupRef = v;
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
	}

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	@Override
	public void unsetGroupRef() {
		if (hasGroupRef()) {
			Group groupRef = getGroupRef();
			setGroupRef(null);
			if (groupRef.hasPathwayElement(this))
				groupRef.removePathwayElement(this);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
		}
	}

	/**
	 * Returns the center x coordinate for this shaped pathway element.
	 * 
	 * @return centerX the middle of an object in the x direction.
	 */
	public double getCenterX() {
		return centerX;
	}

	/**
	 * Sets the center x coordinate for this shaped pathway element.
	 * 
	 * @param v the middle of an object in the x direction to set.
	 */
	public void setCenterX(double v) {
		if (v < 0)
			Logger.log.trace("Warning: negative x coordinate " + String.valueOf(v));
		centerX = v;
	}

	/**
	 * Returns the center y coordinate for this shaped pathway element.
	 * 
	 * @return centerY the middle of an object in the y direction.
	 */
	public double getCenterY() {
		return centerY;
	}

	/**
	 * Sets the center y coordinate for this shaped pathway element.
	 * 
	 * @param v the middle of an object in the y direction to set.
	 */
	public void setCenterY(double v) {
		if (v < 0)
			Logger.log.trace("Warning: negative y coordinate " + String.valueOf(v));
		centerY = v;
	}

	/**
	 * Returns the width of this shaped pathway element.
	 * 
	 * @return width the width of this shaped pathway element.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the width of this shaped pathway element.
	 * 
	 * @param v the width to set for this shaped pathway element.
	 * @throws IllegalArgumentException if width is a negative value.
	 */
	public void setWidth(double v) {
		if (v < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		}
		if (width != v) {
			width = v;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * Returns the height of this shaped pathway element.
	 * 
	 * @return height the height of this shaped pathway element.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the height of this shaped pathway element.
	 * 
	 * @param v the height to set for this shaped pathway element.
	 * @throws IllegalArgumentException if height is a negative value.
	 */
	public void setHeight(double v) {
		if (v < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		}
		if (height != v) {
			height = v;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * Returns the color of text for this shaped pathway element..
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
	 * Sets the color of text for this shaped pathway element.
	 * 
	 * @param v the color of text for this shaped pathway element.
	 * @throws IllegalArgumentException if color null.
	 */
	public void setTextColor(Color v) {
		if (v == null) {
			throw new IllegalArgumentException();
		} else {
			textColor = v;
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
	 * @param v the name of the font.
	 * @throws IllegalArgumentException if given fontName is null.
	 */
	public void setFontName(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (!Utils.stringEquals(fontName, v)) {
			fontName = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTNAME));
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
	 * @param v the boolean, if true font weight is bold. If false, font weight is
	 *          normal.
	 */
	public void setFontWeight(boolean v) {
		if (fontWeight != v) {
			fontWeight = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTWEIGHT));
		}
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
	 * @param v the boolean, if true typographic style is italic. If false,
	 *          typographic style is normal.
	 */
	public void setFontStyle(boolean v) {
		if (fontStyle != v) {
			fontStyle = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTSTYLE));
		}
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
	 * @param v the boolean, if true typographic style is underline. If false,
	 *          typographic style is normal.
	 */
	public void setFontDecoration(boolean v) {
		if (fontDecoration != v) {
			fontDecoration = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTDECORATION));
		}
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
	 * @param v the boolean, if true typographic style is strikethru. If false,
	 *          typographic style is normal.
	 */
	public void setFontStrikethru(boolean v) {
		if (fontStrikethru != v) {
			fontStrikethru = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTSTRIKETHRU));
		}
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
	 * @param v the value for the size of the font.
	 * @throws IllegalArgumentException if fontSize is a negative value.
	 */
	public void setFontSize(int v) {
		if (v < 0) {
			throw new IllegalArgumentException("Tried to set font size < 0: " + v);
		}
		if (fontSize != v) {
			fontSize = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTSIZE));
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
	 * @param v the horizontal alignment value of displayed text.
	 */
	public void setHAlign(HAlignType v) {
		if (v != null && hAlign != v) {
			hAlign = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.HALIGN));
		}
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
	 * @param v the vertical alignment value of displayed text.
	 * 
	 */
	public void setVAlign(VAlignType v) {
		if (v != null && vAlign != v) {
			vAlign = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.VALIGN));
		}
	}

	/**
	 * Returns the border color of this shaped pathway element.
	 * 
	 * @return borderColor the border color of this shaped pathway element.
	 */
	public Color getBorderColor() {
		if (borderColor == null) {
			return Color.decode("#000000"); // black
		} else {
			return borderColor;
		}
	}

	/**
	 * Sets the border color of this shaped pathway element.
	 * 
	 * @param v the border color of this shaped pathway element.
	 * @throws IllegalArgumentException if color null.
	 */
	public void setBorderColor(Color v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (borderColor != v) {
			borderColor = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BORDERCOLOR));
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
	 * @param v the style of a border.
	 */
	public void setBorderStyle(LineStyleType v) {
		if (v != null && borderStyle != v) {
			borderStyle = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BORDERSTYLE));
		}
	}

	/**
	 * Returns the pixel value for the border of this shaped pathway element.
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
	 * Sets the pixel value for the border of this shaped pathway element.
	 * 
	 * @param v the width of a border.
	 * @throws IllegalArgumentException if borderWidth is a negative value.
	 */
	public void setBorderWidth(double v) {
		if (v < 0) {
			throw new IllegalArgumentException();
		}
		if (borderWidth != v) {
			borderWidth = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BORDERWIDTH));
		}
	}

	/**
	 * Returns the color used to paint the area of this shaped pathway element., not
	 * including its border.
	 * 
	 * @return fillColor the fill color of this shaped pathway element.
	 */
	public Color getFillColor() {
		if (fillColor == null) {
			return Color.decode("#ffffff"); // white
		} else {
			return fillColor;
		}
	}

	/**
	 * Sets the color used to paint the area of this shaped pathway element, not
	 * including its border.
	 * 
	 * @param v the fill color of this shaped pathway element.
	 * @throws IllegalArgumentException if fillColor null.
	 */
	public void setFillColor(Color v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (fillColor != v) {
			fillColor = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FILLCOLOR));
		}
	}

	/**
	 * Returns the visual appearance of a two dimensional object, e.g. Rectangle,
	 * Arc, Mitochondria, Oval.
	 * 
	 * NB: Shape.type is for object type while shapeType is the visual appearance.
	 * For example, an object may have Shape.type "Nucleus" and shapeType "Oval".
	 * 
	 * @return shapeType the visual appearance of this shaped pathway element.
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
	 * @param v the visual appearance of this shaped pathway element.
	 * @throws IllegalArgumentException if shapeType null.
	 */
	public void setShapeType(ShapeType v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (shapeType != v) {
			shapeType = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.SHAPETYPE));
		}
	}

	/**
	 * Returns the z-order of this pathway element.
	 * 
	 * @return zOrder the order of this pathway element.
	 */
	public int getZOrder() {
		return zOrder;
	}

	/**
	 * Sets the z-order of this pathway element.
	 * 
	 * @param v the order of this pathway element.
	 */
	public void setZOrder(int v) {
		if (zOrder != v) {
			zOrder = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ZORDER));
		}
	}

	/**
	 * Returns the rotation of this shaped pathway element.
	 * 
	 * @return rotation the rotation of this shaped pathway element.
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Sets the rotation of this shaped pathway element.
	 * 
	 * @param v the rotation of this shaped pathway element.
	 */
	public void setRotation(Double v) {
		if (rotation != v) {
			rotation = v;
			// TODO rotation????
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * Returns {@link LinkableFrom} pathway elements, at this time that only goes
	 * for {@link LineElement.LinePoint}, for this {@link LinkableTo} pathway element.
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
