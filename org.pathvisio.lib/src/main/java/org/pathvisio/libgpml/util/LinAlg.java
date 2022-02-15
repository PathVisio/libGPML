/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.libgpml.util;

import java.awt.geom.Point2D;

/**
 * Helper class for rotation calculations.
 */
public class LinAlg {

	/**
	 * Determines the angle between two vectors defined by p1 and p2. Both vectors
	 * start at 0.0
	 *
	 * @param p1 start point of vector
	 * @param p2 end point of vector
	 * @return angle in radians
	 */
	public static double angle(Point p1, Point p2) {
		// Angle:
		// p1.p2
		// cos(angle) = --------------
		// ||p1||*||p2||

		double cos = dot(p1, p2) / (p1.len() * p2.len());
		if (cos > 1) {
			cos = 1;
		}
		return direction(p1, p2) * Math.acos(cos);
	}

	/**
	 * Returns the direction of the points.
	 * 
	 * negative: ccw positive: cw
	 * 
	 * @param p1 the first point.
	 * @param p2 the second point.
	 * @return
	 */
	public static double direction(Point p1, Point p2) {
		return Math.signum(p1.x * p2.y - p1.y * p2.x);
	}

	/**
	 * 
	 * @param v1 the first point.
	 * @param v2 the second point.
	 * @return
	 */
	private static double dot(Point v1, Point v2) {
		double[] d1 = v1.asArray();
		double[] d2 = v2.asArray();
		double sum = 0;
		for (int i = 0; i < Math.min(d1.length, d2.length); i++)
			sum += d1[i] * d2[i];
		return sum;
	}

	/**
	 * Projection of point q on a line through p with direction vector v. If p is
	 * 0,0, it's the same as the two-argument function with the same name.
	 * 
	 * @param p the point.
	 * @param q the point.
	 * @param v the direction vector.
	 * @return
	 */
	public static Point project(Point p, Point q, Point v) {
		Point q2 = new Point(q.x - p.x, q.y - p.y);
		double vlen = dot(v, v);
		if (vlen == 0) {
			return p;
		} else {
			double c = dot(q2, v) / dot(v, v);
			return new Point(p.x + v.x * c, p.y + v.y * c);
		}
	}

	/**
	 * Convert a 2-D point to 1-D line coordinates (relative position on the line,
	 * range {0,1})
	 * 
	 * @param start the start point.
	 * @param end   the end point.
	 * @param p     the point.
	 * @return
	 */
	public static double toLineCoordinates(Point start, Point end, Point p) {
		// Project v position on line and calculate relative position
		Point direction = start.subtract(end);
		Point projection = project(start, p, direction);
		double lineLength = distance(start, end);
		double anchorLength = distance(start, projection);
		double position = anchorLength / lineLength;

		double ldir = direction(start, end);
		double adir = direction(start, projection);
		if (adir != ldir) {
			position = 0;
		}
		if (position > 1)
			position = 1;
		if (position < 0)
			position = 0;
		if (Double.isNaN(position))
			position = 0;
		return position;
	}

	/**
	 * Projection of p1 on p2:
	 * 
	 * p1.p2 ----- . p2 p2.p2
	 * 
	 * @param p1.
	 * @param p2.
	 * @return
	 */
	public static Point project(Point p1, Point p2) {
		double c = dot(p1, p2) / dot(p2, p2);
		return new Point(p2.x * c, p2.y * c);
	}

	/**
	 * Distance between p1 and p2.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double distance(Point p1, Point p2) {
		Point dp = p2.subtract(p1);
		return dp.len();
	}

	/**
	 * Rotation of p by angle.
	 * 
	 * @param p
	 * @param angle
	 * @return
	 */
	public static Point rotate(Point p, double angle) {
		Point pr = new Point(0, 0);
		pr.x = p.x * Math.cos(angle) + p.y * Math.sin(angle);
		pr.y = -p.x * Math.sin(angle) + p.y * Math.cos(angle);
		return pr;
	}

	/**
	 * Point, pair of doubles. Supports adding, subtracting, calculating the length
	 * of the vector, etc.
	 * 
	 * @author unknown
	 */
	public static class Point {

		public double x, y;

		/**
		 * Construct for point.
		 * 
		 * @param x the x coordinate.
		 * @param y the y coordinate.
		 */
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Constructor for point given a point2d.
		 * 
		 * @param p2d
		 */
		public Point(Point2D p2d) {
			this(p2d.getX(), p2d.getY());
		}

		/**
		 * Returns this point as a point2d.
		 * 
		 * @return the point2d.
		 */
		public Point2D toPoint2D() {
			return new Point2D.Double(x, y);
		}

		/**
		 * Returns this point x y coordinates as an integer array.
		 * 
		 * @return the integer array.
		 */
		public int[] asIntArray() {
			return new int[] { (int) x, (int) y };
		}

		/**
		 * Returns this point x y coordinates a double array.
		 * 
		 * @return the double array.
		 */
		public double[] asArray() {
			return new double[] { x, y };
		}

		/**
		 * Calculates the norm.
		 * 
		 * @return the norm.
		 */
		public Point norm() {
			double l = len();
			return new Point(x / l, y / l);
		}

		/**
		 * Calculates the length of the vector.
		 * 
		 * @return the length.
		 */
		public double len() {
			return Math.sqrt(dot(this, this));
		}

		/**
		 * Adds point to this point.
		 * 
		 * @param p the point to add.
		 * @return the new added point.
		 */
		public Point add(Point p) {
			return new Point(x + p.x, y + p.y);
		}

		/**
		 * Subtracts point form this point.
		 * 
		 * @param p the point to subtract.
		 * @return the new subtracted point.
		 */
		public Point subtract(Point p) {
			return new Point(x - p.x, y - p.y);
		}

		/**
		 * Multiplies this point by given double.
		 * 
		 * @param d the double.
		 * @return the new multiplied point.
		 */
		public Point multiply(double d) {
			return new Point(x *= d, y *= d);
		}

		/**
		 * Clones this point.
		 *
		 * @return the new clone point.
		 */
		public Point clone() {
			return new Point(x, y);
		}

		/**
		 * Writes this point to a String.
		 * 
		 * @return the string for this point.
		 */
		public String toString() {
			return "Point: " + x + ", " + y;
		}
	}
}
