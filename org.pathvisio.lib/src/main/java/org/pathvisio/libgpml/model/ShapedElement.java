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

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Set;

import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.model.GraphLink.LinkableFrom;
import org.pathvisio.libgpml.model.GraphLink.LinkableTo;
import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.model.shape.IShape;
import org.pathvisio.libgpml.model.shape.ShapeType;
import org.pathvisio.libgpml.model.type.HAlignType;
import org.pathvisio.libgpml.model.type.LineStyleType;
import org.pathvisio.libgpml.model.type.VAlignType;
import org.pathvisio.libgpml.prop.StaticProperty;
import org.pathvisio.libgpml.util.Utils;

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
	private double fontSize = 12; // allows 0.5 sizes
	private HAlignType hAlign = HAlignType.CENTER; // horizontal alignment of text
	private VAlignType vAlign = VAlignType.MIDDLE; // vertical alignment of text
	// shape style properties
	private Color borderColor = Color.decode("#000000"); // black
	private LineStyleType borderStyle = LineStyleType.SOLID; // solid
	private double borderWidth = 1.0;
	private Color fillColor = Color.decode("#ffffff"); // white
	private IShape shapeType = ShapeType.RECTANGLE; // rectangle
	private int zOrder; // optional
	private double rotation = 0; // optional, in radians

	// ================================================================================
	// Constructors
	// ================================================================================
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

	// ================================================================================
	// Accessors
	// ================================================================================
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
		if (v.getPathwayModel() != pathwayModel) {
			throw new IllegalArgumentException(
					getClass().getSimpleName() + " cannot be added to a group of a different pathway model.");
		}
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
		groupRef = v;
		fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
	}

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	@Override
	public void unsetGroupRef() {
		if (hasGroupRef()) {
			Group groupRef = getGroupRef();
			setGroupRef(null);
			if (groupRef.hasPathwayElement(this)) {
				groupRef.removePathwayElement(this);
			}
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
		}
	}

	// ================================================================================
	// Rect Graphics Properties
	// ================================================================================
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
		if (centerX != v) {
			if (v < 0) {
				Logger.log.trace("Warning: negative x coordinate " + String.valueOf(v));
			}
			centerX = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
		}
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
		if (centerY != v) {
			if (v < 0) {
				Logger.log.trace("Warning: negative y coordinate " + String.valueOf(v));
			}
			centerY = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
		}
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
			fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
		}
	}

	// ================================================================================
	// Font Graphics Properties
	// ================================================================================
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.FONTNAME));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.FONTWEIGHT));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.FONTSTYLE));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.FONTDECORATION));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.FONTSTRIKETHRU));
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
	 * Sets point value for the size of the font. Rounds any decimals to the nearest
	 * 0.5. 
	 * 
	 * @param v the value for the size of the font.
	 * @throws IllegalArgumentException if fontSize is a negative value.
	 */
	public void setFontSize(double v) {
		if (v < 0) {
			throw new IllegalArgumentException("Tried to set font size < 0: " + v);
		}
		if (fontSize != v) {
			fontSize = Math.round(v * 2) / 2.0; // round to nearest 0.5
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.FONTSIZE));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.HALIGN));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.VALIGN));
		}
	}

	// ================================================================================
	// Shape Style Graphics Properties
	// ================================================================================
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.BORDERCOLOR));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.BORDERSTYLE));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.BORDERWIDTH));
		}
	}

	/**
	 * Returns the color used to paint the area of this shaped pathway element., not
	 * including its border.
	 * 
	 * @return fillColor the fill color of this shaped pathway element.
	 */
	public Color getFillColor() {
		return fillColor;
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.FILLCOLOR));
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
	public IShape getShapeType() {
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
	public void setShapeType(IShape v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (shapeType != v) {
			shapeType = v;
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.SHAPETYPE));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createSinglePropertyEvent(this, StaticProperty.ZORDER));
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
			fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
		}
	}

	// ================================================================================
	// Inherited Methods
	// ================================================================================
	/**
	 * Returns {@link LinkableFrom} pathway elements, at this time that only goes
	 * for {@link LineElement.LinePoint}, for this {@link LinkableTo} pathway
	 * element.
	 */
	@Override
	public Set<LinkableFrom> getLinkableFroms() {
		return GraphLink.getReferences(this, pathwayModel);
	}

	/**
	 * Removes links from all {@link LinkableFrom} line points to this
	 * {@link LinkableTo} pathway element.
	 */
	public void unsetAllLinkableFroms() {
		for (LinkableFrom linePoint : getLinkableFroms()) {
			((LinePoint) linePoint).unlink();
		}
	}

	/**
	 * Terminates this pathway element. The pathway model, if any, is unset from
	 * this pathway element. Links to all annotationRefs, citationRefs, and
	 * evidenceRefs are removed from this data node.
	 */
	@Override
	protected void terminate() {
		super.terminate();
	}

	// ================================================================================
	// Bounds Methods
	// ================================================================================
	/**
	 * Returns the rectangular bounds of this shaped pathway element after rotation
	 * is applied.
	 * 
	 * @return the rectangular bounds for this shaped pathway element with rotation
	 *         taken into account.
	 */
	@Override
	public Rectangle2D getRotatedBounds() {
		Rectangle2D bounds = getBounds();
		AffineTransform t = new AffineTransform();
		t.rotate(getRotation(), getCenterX(), getCenterY());
		bounds = t.createTransformedShape(bounds).getBounds2D();
		return bounds;
	}

	/**
	 * Returns the rectangular bounds of this shaped pathway element without
	 * rotation taken into account.
	 * 
	 * @return the rectangular bounds for this shaped pathway element.
	 */
	@Override
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(getLeft(), getTop(), getWidth(), getHeight());
	}

	/**
	 * Returns the left x coordinate of the bounding box around (start, end) this
	 * shaped pathway element.
	 */
	public double getLeft() {
		return centerX - width / 2;
	}

	/**
	 * Sets the center x coordinate given the left x coordinate of the bounding box
	 * around (start, end) this shaped pathway element.
	 * 
	 * @param v the left coordinate.
	 */
	public void setLeft(double v) {
		centerX = v + width / 2;
		fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
	}

	/**
	 * Returns the top y coordinate of the bounding box around (start, end) this
	 * shaped pathway element.
	 */
	public double getTop() {
		return centerY - height / 2;
	}

	/**
	 * Sets the center y coordinate given the top y coordinate of the bounding box
	 * around (start, end) this shaped pathway element.
	 * 
	 * @param v the y top coordinate.
	 */
	public void setTop(double v) {
		centerY = v + height / 2;
		fireObjectModifiedEvent(PathwayObjectEvent.createCoordinatePropertyEvent(this));
	}

	/**
	 * @param p
	 * @return
	 */
	@Override
	public Point2D toAbsoluteCoordinate(Point2D p) {
		double x = p.getX();
		double y = p.getY();
		Rectangle2D bounds = getRotatedBounds();
		// Scale
		if (bounds.getWidth() != 0)
			x *= bounds.getWidth() / 2;
		if (bounds.getHeight() != 0)
			y *= bounds.getHeight() / 2;
		// Translate
		x += bounds.getCenterX();
		y += bounds.getCenterY();
		return new Point2D.Double(x, y);
	}

	/**
	 * @param mp a point in absolute model coordinates
	 * @return the same point relative to the bounding box of this pathway element:
	 *         -1,-1 meaning the top-left corner, 1,1 meaning the bottom right
	 *         corner, and 0,0 meaning the center.
	 */
	public Point2D toRelativeCoordinate(Point2D mp) {
		double relX = mp.getX();
		double relY = mp.getY();
		Rectangle2D bounds = getRotatedBounds();
		// Translate
		relX -= bounds.getCenterX();
		relY -= bounds.getCenterY();
		// Scalebounds.getCenterX();
		if (relX != 0 && bounds.getWidth() != 0)
			relX /= bounds.getWidth() / 2;
		if (relY != 0 && bounds.getHeight() != 0)
			relY /= bounds.getHeight() / 2;
		return new Point2D.Double(relX, relY);
	}

	// ================================================================================
	// Copy Methods
	// ================================================================================
	/**
	 * Note: doesn't change parent, only fields
	 *
	 * Used by UndoAction.
	 * 
	 * NB: GroupRef is not copied, but can be set later if the parent group and all
	 * other pathway element members are copied.
	 *
	 * @param src
	 */
	public void copyValuesFrom(ShapedElement src) {
		super.copyValuesFrom(src);
		centerX = src.centerX;
		centerY = src.centerY;
		width = src.width;
		height = src.height;
		textColor = src.textColor;
		fontName = src.fontName;
		fontWeight = src.fontWeight;
		fontStyle = src.fontStyle;
		fontDecoration = src.fontDecoration;
		fontStrikethru = src.fontStrikethru;
		fontSize = src.fontSize;
		hAlign = src.hAlign;
		vAlign = src.vAlign;
		borderColor = src.borderColor;
		borderStyle = src.borderStyle;
		borderWidth = src.borderWidth;
		fillColor = src.fillColor;
		shapeType = src.shapeType;
		zOrder = src.zOrder;
		rotation = src.rotation;
		fireObjectModifiedEvent(PathwayObjectEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copy Object. The object will not be part of the same Pathway object, it's
	 * parent will be set to null.
	 *
	 * No events will be sent to the parent of the original.
	 */
	public abstract CopyElement copy();

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
		Set<StaticProperty> propsShapedElement = EnumSet.of(StaticProperty.GROUPREF, StaticProperty.CENTERX,
				StaticProperty.CENTERY, StaticProperty.WIDTH, StaticProperty.HEIGHT, StaticProperty.TEXTCOLOR,
				StaticProperty.FONTNAME, StaticProperty.FONTWEIGHT, StaticProperty.FONTSTYLE,
				StaticProperty.FONTDECORATION, StaticProperty.FONTSTRIKETHRU, StaticProperty.FONTSIZE,
				StaticProperty.HALIGN, StaticProperty.VALIGN, StaticProperty.BORDERCOLOR, StaticProperty.BORDERSTYLE,
				StaticProperty.BORDERWIDTH, StaticProperty.FILLCOLOR, StaticProperty.SHAPETYPE, StaticProperty.ZORDER,
				StaticProperty.ROTATION);
		result.addAll(propsShapedElement);
		return result;
	}

	/**
	 *
	 */
	@Override
	public Object getStaticProperty(StaticProperty key) {
		Object result = super.getStaticProperty(key);
		if (result == null) {
			switch (key) {
			case GROUPREF:
				result = getGroupRef();
				break;
			case CENTERX:
				result = getCenterX();
				break;
			case CENTERY:
				result = getCenterY();
				break;
			case WIDTH:
				result = getWidth();
				break;
			case HEIGHT:
				result = getHeight();
				break;
			case TEXTCOLOR:
				result = getTextColor();
				break;
			case FONTNAME:
				result = getFontName();
				break;
			case FONTWEIGHT:
				result = getFontWeight();
				break;
			case FONTSTYLE:
				result = getFontStyle();
				break;
			case FONTDECORATION:
				result = getFontDecoration();
				break;
			case FONTSTRIKETHRU:
				result = getFontStrikethru();
				break;
			case FONTSIZE:
				result = getFontSize();
				break;
			case HALIGN:
				result = getHAlign();
				break;
			case VALIGN:
				result = getVAlign();
				break;
			case BORDERCOLOR:
				result = getBorderColor();
				break;
			case BORDERSTYLE:
				result = getBorderStyle().getName(); // TODO
				break;
			case BORDERWIDTH:
				result = getBorderWidth();
				break;
			case FILLCOLOR:
				result = getFillColor();
				break;
			case SHAPETYPE:
				result = getShapeType();
				break;
			case ZORDER:
				result = getZOrder();
				break;
			case ROTATION:
				result = getRotation();
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
	 * @param key
	 * @param value
	 */
	@Override
	public void setStaticProperty(StaticProperty key, Object value) {
		super.setStaticProperty(key, value);
		switch (key) {
		case GROUPREF:
			setGroupRef((Group) value);
			break;
		case CENTERX:
			setCenterX((Double) value);
			break;
		case CENTERY:
			setCenterY((Double) value);
			break;
		case WIDTH:
			setWidth((Double) value);
			break;
		case HEIGHT:
			setHeight((Double) value);
			break;
		case TEXTCOLOR:
			setTextColor((Color) value);
			break;
		case FONTNAME:
			setFontName((String) value);
			break;
		case FONTWEIGHT:
			setFontWeight((Boolean) value);
			break;
		case FONTSTYLE:
			setFontStyle((Boolean) value);
			break;
		case FONTDECORATION:
			setFontDecoration((Boolean) value);
			break;
		case FONTSTRIKETHRU:
			setFontStrikethru((Boolean) value);
			break;
		case FONTSIZE:
			setFontSize((Double) value);
			break;
		case HALIGN:
			setHAlign((HAlignType) value);
			break;
		case VALIGN:
			setVAlign((VAlignType) value);
			break;
		case BORDERCOLOR:
			setBorderColor((Color) value);
			break;
		case BORDERSTYLE:
			if (value instanceof LineStyleType) {
				setBorderStyle((LineStyleType) value);
			} else {
				setBorderStyle(LineStyleType.fromName((String) value));
			}
			break;
		case BORDERWIDTH:
			setBorderWidth((Double) value);
			break;
		case FILLCOLOR:
			setFillColor((Color) value);
			break;
		case SHAPETYPE:
			setShapeType((IShape) value);
			break;
		case ZORDER:
			setZOrder((Integer) value);
			break;
		case ROTATION:
			setRotation((Double) value);
			break;
		default:
			// do nothing
		}

	}

}
