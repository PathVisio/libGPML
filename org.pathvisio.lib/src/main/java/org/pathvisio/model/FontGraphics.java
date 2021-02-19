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

/**
 * This class stores information for common font properties.
 * 
 * @author finterly
 */
public class FontGraphics implements Graphics {

	private String textColor;
	private String fontName;
	private String fontWeight;
	private String fontStyle;
	private String fontDecoration;
	private String fontStrikethru;
	private double fontSize; // want half size? String? 
	private String hAlign;
	private String vAlign;

	/**
	 * Gets the color of text.
	 * 
	 * @return textColor the color of text.
	 */
	public String getTextColor() {
		if (textColor == null) {
			return "White";
		} else {
			return textColor;
		}
	}

	/**
	 * Sets the color of text.
	 * 
	 * @param textColor the color of text
	 */
	public void setTextColor(String textColor) {
		this.textColor = textColor;
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
	 * 
	 */
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	/**
	 * Gets the thickness of the font used, a bold font would have more weight.
	 * 
	 * @return fontWeight the thickness of the font, e.g. normal or bold.
	 * 
	 */
	public String getFontWeight() {
		if (fontWeight == null) {
			return "Normal";
		} else {
			return fontWeight;
		}
	}

	/**
	 * Gets the thickness of the font used, a bold font would have more weight.
	 * 
	 * @param fontWeight the thickness of the font, e.g. normal or bold.
	 * 
	 */
	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	/**
	 * Gets the typographic style applied to displayed text, e.g. normal or italic.
	 * 
	 * @return fontStyle the typographic style, e.g. normal or italic.
	 * 
	 */
	public String getFontStyle() {
		if (fontStyle == null) {
			return "Normal";
		} else {
			return fontStyle;
		}
	}

	/**
	 * Sets the typographic style applied to displayed text, e.g. normal or italic.
	 * 
	 * @param fontStyle the typographic style, e.g. normal or italic.
	 * 
	 */
	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	/**
	 * Gets the typographic style for underline or normal.
	 * 
	 * @return fontDecoration the typographic style, e.g. underline or normal.
	 * 
	 */
	public String getFontDecoration() {
		if (fontDecoration == null) {
			return "Normal";
		} else {
			return fontDecoration;
		}
	}

	/**
	 * Sets the typographic style for underline or normal.
	 * 
	 * @param fontDecoration the typographic style, e.g. underline or normal.
	 * 
	 */
	public void setFontDecoration(String fontDecoration) {
		this.fontDecoration = fontDecoration;
	}

	/**
	 * Gets the typographic style for strikethru or normal.
	 * 
	 * @return fontStrikethru the typographic style for strikethru or normal.
	 * 
	 */
	public String getFontStrikethru() {
		if (fontStrikethru == null) {
			return "Normal";
		} else {
			return fontStrikethru;
		}
	}

	/**
	 * Sets the typographic style for strikethru or normal.
	 * 
	 * @param fontStrikethru the typographic style for strikethru or normal.
	 * 
	 */
	public void setFontStrikethru(String fontStrikethru) {
		this.fontStrikethru = fontStrikethru;
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
		this.fontSize = fontSize;
	}

	/**
	 * Gets the horizontal alignment of displayed text, e.g., Left, Center, Right.
	 * 
	 * @return hAlign the horizontal alignment value of displayed text. 
	 */
	public String getHAlign() {
		if (hAlign == null) {
			return "Center";
		} else {
			return hAlign;
		}
	}

	/**
	 * Sets the horizontal alignment of displayed text, e.g., Left, Center, Right.
	 * 
	 * @param hAlign the horizontal alignment value of displayed text. 
	 * 
	 */
	public void setHAlign(String hAlign) {
		this.hAlign = hAlign;
	}

	/**
	 * Gets the vertical alignment of displayed text, e.g., Top, Middle, Bottom.
	 * 
	 * @return vAlign the vertical alignment value of displayed text. 
	 * 
	 */
	public String getVAlign() {
		if (vAlign == null) {
			return "Middle";
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
	public void setVAlign(String vAlign) {
		this.vAlign = vAlign;
	}

}
