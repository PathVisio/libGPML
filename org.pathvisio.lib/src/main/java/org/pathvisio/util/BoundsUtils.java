package org.pathvisio.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.pathvisio.model.*;
import org.pathvisio.model.graphics.Coordinate;
import org.pathvisio.model.ref.PathwayElement;
import org.pathvisio.model.type.*;

/**
 * This utils class contains methods for calculating rectangular bounds
 * {@link Rectangle2D}.
 * 
 * @author finterly
 */
public class BoundsUtils {

	/**
	 * Default margins for group bounding-box in GPML2013a. Makes the bounds
	 * slightly larger than the summed bounds of the containing elements.
	 */
	public static final double DEFAULT_M_MARGIN = 8;
	public static final double COMPLEX_M_MARGIN = 12;

	/**
	 * Returns margin for group bounding-box around contained elements depending on
	 * group type, as specified in GPML2013a.
	 * 
	 * @param type the type of the group.
	 * @return
	 */
	public static double getMargin(GroupType type) {
		if (type == GroupType.COMPLEX) {
			return COMPLEX_M_MARGIN;
		} else {
			return DEFAULT_M_MARGIN;
		}
	}

	/**
	 * Iterates over all group pathway element members to find the total rectangular
	 * bounds.
	 * 
	 * @param group the group.
	 * @return the rectangular bound for group specified in double coordinates.
	 */
	public static Rectangle2D calculateGroupBounds(Group group) {
		double margin = getMargin(group.getType());
		Rectangle2D bounds = null;
		for (Groupable pathwayElement : group.getPathwayElements()) {
			if (bounds == null) {
				if (pathwayElement instanceof ShapedElement)
					bounds = getShapedElementBounds((ShapedElement) pathwayElement);
				if (pathwayElement instanceof LineElement)
					bounds = getLineElementBound((LineElement) pathwayElement);
			} else {
				if (pathwayElement instanceof ShapedElement)
					bounds.add(getShapedElementBounds((ShapedElement) pathwayElement));
				if (pathwayElement instanceof LineElement)
					bounds.add(getLineElementBound((LineElement) pathwayElement));
			}
		}
		if (bounds != null) {
			return new Rectangle2D.Double(bounds.getX() - margin, bounds.getY() - margin,
					bounds.getWidth() + 2 * margin, bounds.getHeight() + 2 * margin);
		} else {
			return null;
		}
	}

	/**
	 * Calculates and returns the rectangular bounds for given shaped pathway
	 * element.
	 * 
	 * @param shapedElement the shaped pathway element.
	 * @return the rectangular bound for shaped pathway element specified in double
	 *         coordinates.
	 */
	public static Rectangle2D getShapedElementBounds(ShapedElement shapedElement) {
		double centerX = 0; 
		double centerY = 0; 
		// TODO if nested group, calculate bounds of the inner group
		if (shapedElement.getClass() == Group.class) {
			Rectangle2D bounds = calculateGroupBounds((Group) shapedElement);
			centerX = bounds.getCenterX();
			centerY = bounds.getCenterY();
		} else {
			centerX = shapedElement.getCenterXY().getX();
			centerY = shapedElement.getCenterXY().getY();
		}
		double width = shapedElement.getWidth();
		double height = shapedElement.getHeight();
		double leftX = centerX - (width / 2);
		double topY = centerY - (height / 2);
		Rectangle2D bounds = new Rectangle2D.Double(leftX, topY, width, height);
		if (shapedElement.getClass() == Shape.class) {
			AffineTransform transform = new AffineTransform();
			transform.rotate(((Shape) shapedElement).getRotation(), centerX, centerY);
			bounds = transform.createTransformedShape(bounds).getBounds2D();
		}
		return bounds;
	}

	/**
	 * Calculates and returns the rectangular bounds for given line pathway element.
	 * Only ends points (first and last) are taken into account.
	 * 
	 * @param lineElement the line pathway element.
	 * @return the rectangular bound for line pathway element specified in double
	 *         coordinates.
	 */
	public static Rectangle2D getLineElementBound(LineElement lineElement) {
		List<LinePoint> points = lineElement.getLinePoints();
		LinePoint point1 = points.get(0); // first point
		LinePoint point2 = points.get(points.size() - 1); // last point
		double x1 = point1.getXY().getX();
		double x2 = point2.getXY().getX();
		double y1 = point1.getXY().getY();
		double y2 = point2.getXY().getY();
		double topY = Math.min(y1, y2);
		double leftX = Math.min(x1, x2);
		double width = Math.max(x1, x2) - leftX;
		double height = Math.max(y1, y2) - topY;
		return new Rectangle2D.Double(leftX, topY, width, height);
	}

}
