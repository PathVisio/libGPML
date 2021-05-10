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
package org.pathvisio.model.graphics;

import org.pathvisio.debug.Logger;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.elements.Point;

/**
 * This class holds coordinates x and y.
 * 
 * See use in {@link RectProperty}, {@link Point}
 * 
 * @author finterly
 */
public class Coordinate {

	private double x;
	private double y;

	/**
	 * Instantiates a Coordinate with x and y coordinate values. Coordinate values
	 * cannot be negative.
	 * 
	 * @param x the coordinate value for x.
	 * @param y the coordinate value for y.
	 */
	public Coordinate(double x, double y) {
		if (x < 0)
			System.out.println("Warning: negative x coordinate " + String.valueOf(x));
		if (y < 0)
			System.out.println("Warning: negative y coordinate " + String.valueOf(y));
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns x coordinate value.
	 * 
	 * @return x the coordinate value for x.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets x coordinate to given value.
	 * 
	 * @param x the coordinate value for x.
	 */
	public void setX(double x) {
		if (x < 0)
			Logger.log.trace("Warning: negative x coordinate " + String.valueOf(x));
		this.x = x;
	}

	/**
	 * Returns y coordinate value.
	 * 
	 * @return y the coordinate value for y.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets y coordinate to given value.
	 * 
	 * @param y the coordinate value for y.
	 */
	public void setY(double y) {
		if (y < 0)
			Logger.log.trace("Warning: negative y coordinate " + String.valueOf(y));
		this.y = y;
	}

	/**
	 * Returns true is given object is equal to this Coordinate object.
	 * 
	 * @return true if objects equal.
	 */
	public boolean equals(final Object o) {
		return this.equals((Coordinate) o);
	}

	/**
	 * Returns true is given Coordinate object is equal to this Coordinate object.
	 * 
	 * @param c the given coordinate object
	 * @return true if coordinates equal.
	 */
	public boolean equals(final Coordinate c) {
		return this.x == c.x && this.y == c.y;
	}

	/**
	 * Returns Coordinate as string of x/y value,
	 */
	public String toString() {
		return String.format("%f/%f", this.x, this.y);
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

}
