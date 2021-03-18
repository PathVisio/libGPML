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

import org.pathvisio.model.ElementLink.ElementIdContainer;
import org.pathvisio.model.ElementLink.ElementRefContainer;
import org.pathvisio.util.Utils;

/**
 * This class stores all information relevant to a Point pathway element.
 * 
 * @author finterly
 */
public class Point extends GenericPoint {

	private PathwayElement elementRef; // optional?
	private LineType arrowHead; // line by default
	private Coordinate xy;
	private double relX; // optional
	private double relY; // optional
	
	//TODO: PathwayElement parent? 

	
	
	/**
	 * Instantiates a Point pathway element, with reference to another pathway
	 * element.
	 * 
	 * @param elementId  the unique id of the point.
	 * @param elementRef the id of the pathway element to which the point refers.
	 * @param arrowHead  the arrowhead property of the point.
	 * @param x          the x coordinate position of the point.
	 * @param y          the y coordinate position of the point.
	 * @param relX       the relative x coordinate.
	 * @param relY       the relative x coordinate.
	 */
	public Point(String elementId, String elementRef, LineType arrowHead, Coordinate xy, double relX, double relY) {
		super(elementId);
		this.elementRef = elementRef;
		this.arrowHead = arrowHead;
		this.xy = xy;
		this.relX = relX;
		this.relY = relY;
	}

	/**
	 * Instantiates a Point pathway element, with no reference to another pathway
	 * element.
	 * 
	 * @param elementId the unique id of the point.
	 * @param arrowHead the arrowhead property of the point.
	 * @param x         the x coordinate position of the point.
	 * @param y         the y coordinate position of the point.
	 */
	public Point(String elementId, LineType arrowHead, Coordinate xy) {
		super(elementId);
		this.arrowHead = arrowHead;
		this.xy = xy;
	}


	/*-----------------------------------------------------------------------*/

	/**
	 * Gets the elementRef of the point, indicates a child/parent relationship
	 * between pathway elements. The elementRef of the child refers to the elementId
	 * of the parent.
	 * 
	 * @return elementRef the elementRef of the point.
	 * 
	 */
	public String getElementRef() {
		return elementRef;
	}

	private ElementIdContainer getElementIdContainer() {
		return getPathway().getElementIdContainer(elementRef);
	}

	/**
	 * Sets the elementRef of the point, indicates a child/parent relationship
	 * between pathway elements. The elementRef of the child refers to the elementId
	 * of the parent.
	 * 
	 * @param elementRef the elementRef of the point.
	 * 
	 */
	public void setElementRef(String elementRef) {
		this.elementRef = elementRef;
	}

	/**
	 * Set a (elementRef) reference to another object with an elementId. If a parent
	 * is set, this will automatically deregister the previously held reference and
	 * register the new reference as necessary.
	 *
	 * @param v reference to set.
	 */
	public void setElementRef(String ref) {
		if (!Utils.stringEquals(elementRef, ref)) {
			if (parentPathway != null) {
				if (elementRef != null) {
					getPathway().removeElementRef(elementRef, (ElementRefContainer) this);
				}
				if (ref != null) {
					getPathway().addElementRef(ref, (ElementRefContainer) this);
				}
			}
			elementRef = ref;
		}
	}

	/*-----------------------------------------------------------------------*/

//	/**
//	 * Link to an object. Current absolute coordinates will be converted to relative
//	 * coordinates based on the object to link to.
//	 */
//	public void linkTo(ElementIdContainer idc) {
//		Point2D rel = idc.toRelativeCoordinate(toPoint2D());
//		linkTo(idc, rel.getX(), rel.getY());
//	}
//
//	/**
//	 * Link to an object using the given relative coordinates
//	 */
//	public void linkTo(ElementIdContainer idc, double relX, double relY) {
//		String id = idc.getElementId();
//		if (id == null)
//			id = idc.setGeneratedElementId();
//		setElementRef(idc.getElementId());
//		setRelativePosition(relX, relY);
//	}
//
//	/**
//	 * note that this may be called any number of times when this point is already
//	 * unlinked
//	 */
//	public void unlink() {
//		if (graphRef != null) {
//			if (getPathway() != null) {
//				Point2D abs = getAbsolute();
//				moveTo(abs.getX(), abs.getY());
//			}
//			relativeSet = false;
//			setElementRef(null);
//		}
//	}
//
//	/**
//	 * Find out if this point is linked to an object. Returns true if a graphRef
//	 * exists and is not an empty string
//	 */
//	public boolean isLinked() {
//		String ref = getElementRef();
//		return ref != null && !"".equals(ref);
//	}

	/*-----------------------------------------------------------------------*/



	/**
	 * Gets the arrowHead property of the point. Arrowhead specifies the glyph at
	 * the ends of lines and interactions. Only the arrowHead attribute on first and
	 * last points are used, the rest is ignored.
	 * 
	 * @return arrowhead the arrowhead property of the point.
	 * 
	 */
	public LineType getArrowHead() {
		if (arrowHead == null) {
			return LineType.LINE;
		} else {
			return arrowHead;
		}
	}

	/**
	 * Sets the arrowHead property of the point. Arrowhead specifies the glyph at
	 * the ends of lines and interactions. Only the arrowHead attribute on first and
	 * last points are used, the rest is ignored.
	 * 
	 * @param arrowhead the arrowhead property of the point.
	 * 
	 */
	public void setArrowHead(LineType arrowHead) {
		this.arrowHead = arrowHead;
	}

	/**
	 * Gets the xy coordinate position of the point.
	 * 
	 * @param xy the xy coordinate position of the point.
	 */
	public Coordinate getXY() {
		return xy;
	}

	/**
	 * Sets the xy coordinate position of the point.
	 * 
	 * @return xy the xy coordinate position of the point.
	 */
	public void setXY(Coordinate xy) {
		this.xy = xy;
	}

	/**
	 * Gets the relative x coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relX the relative x coordinate.
	 */
	public double getRelX() {
		return relX;
	}

	/**
	 * Sets the relative x coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relX the relative x coordinate.
	 */
	public void setRelX(double relX) {
		this.relX = relX;
	}

	/**
	 * Gets the relative y coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relY the relative y coordinate.
	 */
	public double getRelY() {
		return relY;
	}

	/**
	 * Sets the relative y coordinate. When the given point is linked to a pathway
	 * element, relX and relY are the relative coordinates on the element, where 0,0
	 * is at the center of the object and 1,1 at the bottom right corner of the
	 * object.
	 * 
	 * @param relY the relative y coordinate.
	 */
	public void setRelY(double relY) {
		this.relY = relY;
	}

}
