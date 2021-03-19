package org.pathvisio.model;

import java.awt.geom.Point2D;
import java.util.Set;

import org.pathvisio.model.ElementLink.ElementRefContainer;

/**
 * This interface allows iteration through all objects containing an elementId.
 * All pathway element classes have an elementId and implement this interface.
 * 
 * @author unknown, finterly
 */
public interface IElementIdContainer {
	
	
	boolean isValidElementId(String elementId);

	/**
	 * Returns the parent Pathway object, needed for maintaining a consistent list
	 * of elementIds.
	 */
	Pathway getPathway();

	/**
	 * Gets elementId.
	 */
	String getElementId();

	/**
	 * Sets elementId as given String id.
	 * 
	 * @param elementId the string elementId is set to.
	 */
	void setElementId(String elementId);

	/**
	 * Generates a unique elementId and uses that.
	 */
	String setGeneratedElementId();

	/**
	 * Returns a set of ElementRefContainer.
	 */
	Set<ElementRefContainer> getReferences();

	/**
	 * Converts a point to shape coordinates (relative to the bounds of the
	 * ElementIdContainer).
	 */
	Point2D toRelativeCoordinate(Point2D p);

	/**
	 * Converts a point to pathway coordinates (relative to the pathway).
	 */
	Point2D toAbsoluteCoordinate(Point2D p);
}
