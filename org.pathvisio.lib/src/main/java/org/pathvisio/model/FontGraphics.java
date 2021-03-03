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

import org.pathvisio.util.Utils;

/**
 * This class stores information for common font properties.
 * 
 * @author finterly
 */
public class FontGraphics implements Graphics {

	protected Color textColor;
	protected String fontName = "Arial";
	protected boolean fontWeight = false; // bold or normal
	protected boolean fontStyle = false; // italic or normal
	protected boolean fontDecoration = false; // underline or normal
	protected boolean fontStrikethru = false;// strikethru or normal
	protected double fontSize = 12; // M_INITIAL_FONTSIZE
	protected HAlignType hAlign = HAlignType.CENTER; // horizontal alignment of text
	protected VAlignType vAlign = VAlignType.MIDDLE; // vertical alignment of text
	protected PathwayElement parent; // TODO: Getter/Setter

	/**
	 * Gets the color of text.
	 * 
	 * @return textColor the color of text.
	 */
	public Color getTextColor() {
		if (textColor == null) {
			return new Color(0, 0, 0); // black
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
	public void setLineColor(Color textColor) {
		if (textColor == null)
			throw new IllegalArgumentException();
		if (this.textColor != textColor) {
			this.textColor = textColor;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.TEXTCOLOR));
		}
	}

	/**
	 * Gets the name of the set of printable text characters to be used for
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
		if (fontName == null)
			throw new IllegalArgumentException();
		if (!Utils.stringEquals(this.fontName, fontName)) {
			this.fontName = fontName;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.FONTNAME));
		}
	}

	/**
	 * Gets the thickness of the font used, a bold font would have more weight.
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
		if (this.fontWeight != fontWeight) {
			this.fontWeight = fontWeight;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.FONTWEIGHT));
		}
	}

	/**
	 * Gets the typographic style applied to displayed text, e.g. normal or italic.
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
		if (this.fontStyle != fontStyle) {
			this.fontStyle = fontStyle;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.FONTSTYLE));
		}
	}

	/**
	 * Gets the typographic style for underline or normal.
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
		if (this.fontDecoration != fontDecoration) {
			this.fontDecoration = fontDecoration;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.FONTDECORATION));
		}
	}

	/**
	 * Gets the typographic style for strikethru or normal.
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
		if (this.fontStrikethru != fontStrikethru) {
			this.fontStrikethru = fontStrikethru;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.FONTSTRIKETHRU));
		}
	}

	/**
	 * Gets the point value for the size of the font.
	 * 
	 * @return fontSize the value for the size of the font.
	 * 
	 */
	public double getFontSize() {
		if (fontSize == 0) {
			return 12;
		} else {
			return fontSize;
		}
	}

	/**
	 * Sets point value for the size of the font.
	 * 
	 * @param fontSize the value for the size of the font.
	 */
	public void setFontSize(double fontSize) {
		if (this.fontSize != fontSize) {
			this.fontSize = fontSize;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.FONTSIZE));
		}
	}

	/**
	 * Gets the horizontal alignment of displayed text, e.g., Left, Center, Right.
	 * 
	 * @return hAlign the horizontal alignment value of displayed text.
	 */
	public HAlignType getHAlign() {
//		if (hAlign == null) {
//			return "Center";
//		} else {
		return hAlign;
//		}
	}

	/**
	 * Sets the horizontal alignment of displayed text, e.g., Left, Center, Right.
	 * 
	 * @param hAlign the horizontal alignment value of displayed text.
	 */
	public void setHAlign(HAlignType hAlign) {
		if (this.hAlign != hAlign) {
			this.hAlign = hAlign;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.HALIGN));
		}
	}

	/**
	 * Gets the vertical alignment of displayed text, e.g., Top, Middle, Bottom.
	 * 
	 * @return vAlign the vertical alignment value of displayed text.
	 */
	public VAlignType getVAlign() {
//		if (vAlign == null) { TODO
//			return VAlignType;
//		} else {
		return vAlign;
//		}
	}

	/**
	 * Sets the vertical alignment of displayed text, e.g., Top, Middle, Bottom.
	 * 
	 * @param vAlign the vertical alignment value of displayed text.
	 * 
	 */
	public void setVAlign(VAlignType vAlign) {
		if (this.vAlign != vAlign) {
			this.vAlign = vAlign;
			parent.fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(parent, StaticProperty.VALIGN));
		}
	}

}
