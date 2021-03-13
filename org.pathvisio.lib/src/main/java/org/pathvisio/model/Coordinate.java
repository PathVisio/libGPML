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
 * This class holds coordinates x and y.
 * 
 * @see RectProperty
 * @author finterly
 */
public class Coordinate {

	private double x;
	private double y;

	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public boolean equals(final Object o) {
		return this.equals((Coordinate) o);
	}

	public boolean equals(final Coordinate c) {
		return this.x == c.x && this.y == c.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String toString() {
		return String.format("%f/%f", this.x, this.y);
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

}
