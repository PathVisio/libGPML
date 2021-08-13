package org.pathvisio.model;

import org.pathvisio.model.graphics.FontProperty;
import org.pathvisio.model.graphics.ShapeStyleProperty;

/**
 * Interface for classes which can be rotated. These classes include
 * {@link DataNode}, {@link State}, {@link Label}, and {@link Shape}.
 * 
 * @author finterly
 */
public interface Rotatable {

	/**
	 * Returns the rotation of this pathway element.
	 * 
	 * @return rotation the rotation of the pathway element.
	 */
	public double getRotation();

	/**
	 * Sets the rotation of this pathway element.
	 * 
	 * @param rotation the rotation of the pathway element.
	 */
	public void setRotation(Double rotation);

	/**
	 * Returns the text of of the pathway element.
	 * 
	 * @return textLabel the text of of the pathway element.
	 * 
	 */
	public String getTextLabel();

	/**
	 * Sets the text of of the pathway element.
	 * 
	 * @param textLabel the text of of the pathway element.
	 * 
	 */
	public void setTextLabel(String textLabel);

	/**
	 * Returns the font properties of the pathway element, e.g. textColor,
	 * fontName...
	 * 
	 * @return fontProperty the font properties.
	 */
	public FontProperty getFontProp();

	/**
	 * Sets the font properties of the pathway element, e.g. textColor, fontName...
	 * 
	 * @param fontProperty the font properties.
	 */
	public void setFontProp(FontProperty fontProperty);

	/**
	 * Returns the shape style properties of the pathway element, e.g.
	 * borderColor...
	 * 
	 * @return shapeStyleProperty the shape style properties.
	 */
	public ShapeStyleProperty getShapeStyleProp();

	/**
	 * Sets the shape style properties of the pathway element, e.g. borderColor...
	 * 
	 * @param shapeStyleProperty the shape style properties.
	 */
	public void setShapeStyleProp(ShapeStyleProperty shapeStyleProperty);

}
