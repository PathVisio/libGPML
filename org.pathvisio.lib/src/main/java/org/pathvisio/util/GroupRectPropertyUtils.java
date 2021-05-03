package org.pathvisio.util;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.type.*;
import org.pathvisio.model.graphics.*;

/**
 * 
 * @author finterly
 */
public class GroupRectPropertyUtils {

	/**
	 * Default margins for group bounding-box in GPML2013a. Makes the bounds
	 * slightly larger than the summed bounds of the containing elements.
	 */
	public static final double DEFAULT_M_MARGIN = 8;
	public static final double COMPLEX_M_MARGIN = 12;

	/**
	 * Returns margin of group bounding-box around contained elements depending in
	 * group type.
	 * 
	 * @param type the type of the group.
	 * @return
	 */
	public double getMargin(GroupType type) {
		if (type == GroupType.COMPLEX) {
			return COMPLEX_M_MARGIN;
		} else {
			return DEFAULT_M_MARGIN;
		}
	}

	public double[] calculateGroupRectProperty(Group group) {
		List<Double> xlist = new ArrayList<Double>();
		List<Double> ylist = new ArrayList<Double>();

		double top = Collections.min(ylist);
		double right = Collections.max(xlist);
		double bottom = Collections.max(ylist);
		double left = Collections.min(xlist);
		double centerX = (left + right) / 2;
		double centerY = (top + bottom) / 2;
		double width = right - left;
		double height = bottom - top;

		return new double[] { centerX, centerY, width, height };
	}

	public void getLineElementBounds(LineElement lineElement, List<Double> xlist, List<Double> ylist) {
		for (Point point : lineElement.getPoints()) {
			double x = point.getXY().getX();
			double y = point.getXY().getY();
			xlist.add(x);
			ylist.add(y);
		}
	}

	public void getShapedElementBounds(ShapedElement shapedElement, List<Double> xlist, List<Double> ylist) {
		double centerX = shapedElement.getRectProperty().getCenterXY().getX();
		double centerY = shapedElement.getRectProperty().getCenterXY().getY();
		double width = shapedElement.getRectProperty().getWidth();
		double height = shapedElement.getRectProperty().getHeight();
		ylist.add(centerX - (width / 2)); // left
		ylist.add(centerX + (width / 2)); // right
		ylist.add(centerY - (height / 2)); // top
		ylist.add(centerY + (height / 2)); // bottom
	}

}
