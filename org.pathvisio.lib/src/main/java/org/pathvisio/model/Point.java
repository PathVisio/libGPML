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
 * This class stores all information relevant to a Point pathway element.
 * 
 * @author finterly
 */
public class Point {

	private String elementId;
	private String elementRef; // optional?
	private String arrowHead; //optional
	private double x;
	private double y;
	private double relX; //optional
	private double relY; //optional

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
	public Point(String elementId, String elementRef, String arrowHead, double x, double y, double relX, double relY) {
		this.elementId = elementId;
		this.elementRef = elementRef;
		this.arrowHead = arrowHead;
		this.x = x;
		this.y = y;
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
	public Point(String elementId, String arrowHead, double x, double y) {
		this.elementId = elementId;
		this.arrowHead = arrowHead;
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the elementId of the point.
	 * 
	 * @return elementId the unique id of the point.
	 * 
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets the elementId of the point.
	 * 
	 * @param elementId the unique id of the point.
	 * 
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the elementRef of the point, indicates a child/parent relationship
	 * between pathway elements. The elementRef of the child refers to the elementId
	 * of the parent.
	 * 
	 * @return elementRef the elementRef of the point.
	 * 
	 */
	public Object getElementRef() {
		return elementRef;
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
	 * Gets the arrowHead property of the point. Arrowhead specifies the glyph at
	 * the ends of lines and interactions. Only the arrowHead attribute on first and
	 * last points are used, the rest is ignored.
	 * 
	 * @return arrowhead the arrowhead property of the point.
	 * 
	 */
	public String getArrowHead() {
		if (arrowHead == null) {
			return "Line";
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
	public void setArrowHead(String arrowHead) {
		this.arrowHead = arrowHead;
	}

	/**
	 * Gets the x coordinate position of the point.
	 * 
	 * @param x the x coordinate position of the point.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the x coordinate position of the point.
	 * 
	 * @return x the x coordinate position of the point.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the y coordinate position of the point.
	 * 
	 * @param y the y coordinate position of the point.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the y coordinate position of the point.
	 * 
	 * @return y the y coordinate position of the point.
	 */
	public void setY(double y) {
		this.y = y;
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
