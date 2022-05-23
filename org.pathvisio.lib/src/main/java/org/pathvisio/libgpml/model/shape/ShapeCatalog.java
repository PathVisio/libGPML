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
package org.pathvisio.libgpml.model.shape;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * This class defines some shapes. Shapes are defined and registered in the
 * {@link GeneralPath}.
 * 
 * <p>
 * NB:
 * <ol>
 * <li>BRACE
 * <li>MITOCHONDRIA
 * <li>SARCOPLASMICRETICULUM
 * <li>ENDOPLASMICRETICULUM
 * <li>GOLGIAPPARATUS
 * <li>CORONAVIRUS_ICON
 * <li>DNA_ICON
 * <li>RNA_ICON
 * <li>CELL_ICON
 * <li>MEMBRANE_ICON
 * <li>DEGRADATION
 * </ol>
 * 
 * NB: shapes were previously specific to GenMAPP, such as the GenMAPP
 * ProteinComplex, Vesicle and Ribosome.
 * 
 * @author unknown, finterly
 */
public class ShapeCatalog {

	static void registerShapes() {
	}

	/**
	 * these constants are internal, only for the switch statement below. There is
	 * no relation with the constants defined in ShapeType.
	 */
	public enum Internal {

		// Basic shapes
		OCTAGON,

		// Basic line shapes
		BRACE,

		// Cellular components
		MITOCHONDRIA, SARCOPLASMIC_RETICULUM, ENDOPLASMIC_RETICULUM, GOLGI_APPARATUS,

		// Miscellaneous shapes
		CORONAVIRUS_ICON, DNA_ICON, RNA_ICON, CELL_ICON, MEMBRANE_ICON, DEGRADATION

	}

	/**
	 * Internal, Only for general shape types that can be described as a path. The
	 * shapes are constructed as a general path with arbitrary size and then resized
	 * to fit w and h parameters.
	 */
	static public java.awt.Shape getPluggableShape(Internal st) {
		GeneralPath path = new GeneralPath();
		switch (st) {
		// ========================================
		// Basic shapes
		// ========================================
		case OCTAGON:
			path.moveTo(52.32, 100);
			path.lineTo(21.68, 100);
			path.lineTo(0, 70.71);
			path.lineTo(0, 29.29);
			path.lineTo(21.67, 0);
			path.lineTo(52.32, 0);
			path.lineTo(74, 29.29);
			path.lineTo(74, 70.71);
			path.lineTo(52.32, 100);
			path.closePath();
			break;
		// ========================================
		// Basic line shapes
		// ========================================
		case BRACE:
			path.moveTo(0, 4);
			path.quadTo(0, 2, 3, 2);
			path.quadTo(6, 2, 6, 0);
			path.quadTo(6, 2, 9, 2);
			path.quadTo(12, 2, 12, 4);
			break;
		// ========================================
		// Cellular components (irregular shape)
		// ========================================
		case MITOCHONDRIA:
			path.moveTo(72.81f, 85.70f);
			path.curveTo(97.59f, 83.01f, 94.55f, 147.38f, 119.28f, 144.29f);
			path.curveTo(166.27f, 144.40f, 136.22f, 42.38f, 175.51f, 41.70f);
			path.curveTo(215.08f, 41.02f, 188.27f, 150.12f, 227.79f, 148.28f);
			path.curveTo(271.14f, 146.25f, 230.67f, 29.04f, 274.00f, 26.55f);
			path.curveTo(317.72f, 24.05f, 290.58f, 142.55f, 334.36f, 143.22f);
			path.curveTo(371.55f, 143.80f, 351.55f, 43.14f, 388.66f, 45.75f);
			path.curveTo(429.51f, 48.62f, 392.43f, 153.80f, 432.85f, 160.40f);
			path.curveTo(459.82f, 164.80f, 457.96f, 94.30f, 485.13f, 97.26f);
			path.curveTo(548.33f, 124.69f, 534.13f, 233.75f, 472.75f, 258.89f);
			path.curveTo(454.92f, 261.42f, 450.22f, 220.87f, 432.35f, 223.03f);
			path.curveTo(400.60f, 226.86f, 409.73f, 303.71f, 377.80f, 301.95f);
			path.curveTo(348.05f, 300.30f, 365.16f, 223.61f, 335.37f, 223.28f);
			path.curveTo(295.83f, 222.85f, 316.30f, 327.99f, 276.78f, 326.44f);
			path.curveTo(241.90f, 325.08f, 266.95f, 236.11f, 232.34f, 231.61f);
			path.curveTo(200.07f, 227.42f, 201.79f, 311.88f, 169.71f, 306.49f);
			path.curveTo(134.22f, 300.53f, 167.04f, 209.92f, 131.32f, 205.60f);
			path.curveTo(110.14f, 203.04f, 116.28f, 257.74f, 94.95f, 258.26f);
			path.curveTo(15.35f, 236.77f, 5.51f, 114.51f, 72.81f, 85.70f);
			path.closePath();
			path.moveTo(272.82f, 0.84f);
			path.curveTo(378.97f, 1.13f, 542.51f, 62.39f, 543.54f, 168.53f);
			path.curveTo(544.58f, 275.18f, 381.50f, 342.19f, 274.84f, 342.28f);
			path.curveTo(166.69f, 342.36f, 0.84f, 274.66f, 2.10f, 166.51f);
			path.curveTo(3.33f, 60.72f, 167.03f, 0.56f, 272.82f, 0.84f);
			path.closePath();
			break;
		case SARCOPLASMIC_RETICULUM:
			path.moveTo(118.53f, 16.63f);
			path.curveTo(34.13f, 22.00f, 23.84f, 107.76f, 49.44f, 169.22f);
			path.curveTo(73.73f, 242.63f, 0.51f, 289.88f, 56.13f, 366.83f);
			path.curveTo(99.99f, 419.32f, 176.93f, 391.26f, 192.04f, 332.54f);
			path.curveTo(207.42f, 271.52f, 163.49f, 228.38f, 183.45f, 168.61f);
			path.curveTo(211.75f, 89.03f, 181.43f, 16.01f, 118.53f, 16.63f);
			path.lineTo(118.53f, 16.63f);
			path.closePath();
			break;
		case ENDOPLASMIC_RETICULUM:
			path.moveTo(115.62f, 170.76f);
			path.curveTo(106.85f, 115.66f, 152.29f, 74.72f, 152.11f, 37.31f);
			path.curveTo(151.57f, 22.91f, 135.75f, 10.96f, 123.59f, 21.51f);
			path.curveTo(97.02f, 44.83f, 99.19f, 108.29f, 90.52f, 146.58f);
			path.curveTo(89.97f, 157.27f, 79.04f, 153.89f, 78.44f, 145.14f);
			path.curveTo(69.32f, 111.41f, 105.16f, 72.62f, 87.74f, 58.00f);
			path.curveTo(57.12f, 33.80f, 42.90f, 120.64f, 53.32f, 143.34f);
			path.curveTo(65.01f, 185.32f, 49.93f, 215.62f, 42.80f, 189.23f);
			path.curveTo(39.00f, 173.52f, 52.26f, 156.40f, 41.55f, 141.32f);
			path.curveTo(34.82f, 133.03f, 23.22f, 139.41f, 16.36f, 150.49f);
			path.curveTo(0.00f, 182.29f, 23.74f, 271.85f, 49.05f, 257.53f);
			path.curveTo(56.38f, 251.73f, 44.01f, 231.76f, 55.14f, 229.10f);
			path.curveTo(66.52f, 226.70f, 63.22f, 247.43f, 67.13f, 256.43f);
			path.curveTo(70.73f, 268.42f, 74.67f, 281.17f, 83.91f, 290.85f);
			path.curveTo(91.38f, 298.36f, 107.76f, 297.10f, 110.06f, 285.05f);
			path.curveTo(113.23f, 257.62f, 69.35f, 201.07f, 93.40f, 192.41f);
			path.curveTo(122.33f, 184.37f, 100.80f, 263.03f, 131.30f, 280.35f);
			path.curveTo(146.12f, 286.36f, 155.69f, 278.51f, 154.40f, 268.41f);
			path.curveTo(150.12f, 235.05f, 115.21f, 201.24f, 115.47f, 170.24f);
			path.lineTo(115.62f, 170.76f);
			path.closePath();
			break;
		case GOLGI_APPARATUS:
			path.moveTo(148.89f, 77.62f);
			path.curveTo(100.07f, 3.50f, 234.06f, 7.65f, 207.78f, 62.66f);
			path.curveTo(187.00f, 106.50f, 171.09f, 190.54f, 209.13f, 287.47f);
			path.curveTo(240.55f, 351.33f, 111.35f, 353.69f, 144.36f, 284.72f);
			path.curveTo(171.13f, 215.31f, 165.77f, 107.32f, 148.89f, 77.62f);
			path.lineTo(148.89f, 77.62f);
			path.closePath();
			path.moveTo(88.16f, 91.24f);
			path.curveTo(62.70f, 40.69f, 158.70f, 44.41f, 131.59f, 92.83f);
			path.curveTo(116.28f, 128.91f, 117.95f, 238.10f, 134.33f, 269.85f);
			path.curveTo(154.45f, 313.72f, 56.82f, 315.51f, 85.96f, 264.54f);
			path.curveTo(102.37f, 223.58f, 110.67f, 141.16f, 88.16f, 91.24f);
			path.lineTo(88.16f, 91.24f);
			path.closePath();
			path.moveTo(83.40f, 133.15f);
			path.curveTo(86.43f, 160.23f, 86.72f, 203.15f, 82.05f, 220.09f);
			path.curveTo(73.24f, 250.74f, 69.98f, 262.93f, 50.80f, 265.89f);
			path.curveTo(32.17f, 265.52f, 22.80f, 242.80f, 39.49f, 227.87f);
			path.curveTo(50.94f, 214.61f, 53.98f, 202.20f, 55.20f, 173.72f);
			path.curveTo(54.63f, 152.16f, 56.07f, 133.57f, 43.25f, 126.63f);
			path.curveTo(25.26f, 121.45f, 30.31f, 86.90f, 56.06f, 93.20f);
			path.curveTo(69.86f, 95.63f, 79.23f, 109.03f, 83.40f, 133.15f);
			path.lineTo(83.40f, 133.15f);
			path.closePath();
			break;
		// ========================================
		// Special shapes
		// ========================================
		case CORONAVIRUS_ICON:
			path.append(new Ellipse2D.Double(90, 90, 150, 150), false);
			double origin = 165;
			for (double angle = 0; angle < 360; angle += 30) {
				double rads = Math.toRadians(angle);
				// draw coronavirus "spikes"
				double x = origin + (Math.cos(rads) * 150) - 15;
				double y = origin + (Math.sin(rads) * 150) - 15;
				path.append(new Ellipse2D.Double(x, y, 30, 30), false);
				// draw connecting lines
				double startX = origin + (Math.cos(rads) * 75);
				double startY = origin + (Math.sin(rads) * 75);
				double endX = origin + (Math.cos(rads) * 135);
				double endY = origin + (Math.sin(rads) * 135);
				path.moveTo(startX, startY);
				path.lineTo(endX, endY);
			}
			break;
		case DNA_ICON:
			path.moveTo(17.63, 25.01);
			path.curveTo(23.64, 19.04, 29.93, 12.07, 30, 0);
			path.lineTo(27.15, 0);
			path.curveTo(27.15, 0, 27.42, 1.39, 27, 3.23);
			path.lineTo(3, 3.23);
			path.curveTo(2.74, 1.52, 2.85, 0, 2.85, 0);
			path.lineTo(0, 0);
			path.curveTo(0, 12.17, 6.37, 19.37, 12.37, 24.99);
			path.curveTo(6.25, 30.11, 0, 37.83, 0, 50.01);
			path.curveTo(0, 62.19, 8.42, 70.74, 12.3, 75.05);
			path.curveTo(7.73, 80.05, 0, 87.82, 0, 100);
			path.lineTo(2.85, 100);
			path.curveTo(2.85, 100, 2.81, 98.43, 3, 96.79);
			path.lineTo(27, 96.79);
			path.curveTo(27.47, 98.41, 27.2, 99.03, 27.15, 100);
			path.lineTo(30, 100);
			path.curveTo(30, 87.82, 21.77, 79.33, 17.63, 75.05);
			path.curveTo(21.67, 71, 23.81, 68.9, 25.99, 65.23);
			path.curveTo(28.36, 61.24, 30, 56.35, 30, 50.01);
			path.curveTo(30, 37.83, 23.68, 29.19, 17.63, 25.01);
			path.closePath();
			path.moveTo(3.37, 6.63);
			path.lineTo(26.62, 6.63);
			path.curveTo(26.12, 9.26, 24.87, 11.11, 23.62, 13.25);
			path.lineTo(6.38, 13.25);
			path.curveTo(5.02, 11.42, 4.22, 8.73, 3.37, 6.63);
			path.closePath();
			path.moveTo(9, 16.65);
			path.lineTo(21, 16.65);
			path.curveTo(18.55, 19.17, 17.91, 20.04, 15, 22.46);
			path.curveTo(11.7, 20.22, 11.59, 19.32, 9, 16.65);
			path.closePath();
			path.moveTo(26.63, 93.39);
			path.lineTo(3.37, 93.39);
			path.curveTo(4.67, 90.13, 4.86, 89.61, 6.38, 86.78);
			path.lineTo(23.62, 86.78);
			path.curveTo(25.05, 89.38, 25.67, 90.5, 26.62, 93.39);
			path.closePath();
			path.moveTo(21, 83.37);
			path.lineTo(9, 83.37);
			path.curveTo(10.22, 82.08, 13.08, 79.03, 15, 77.56);
			path.curveTo(16.75, 79.22, 19.76, 82.06, 21, 83.37);
			path.closePath();
			path.moveTo(15, 72.55);
			path.curveTo(12.46, 70.33, 10.58, 68.63, 9, 66.74);
			path.lineTo(21, 66.74);
			path.curveTo(19.47, 68.7, 17.34, 70.47, 15, 72.55);
			path.closePath();
			path.moveTo(23.62, 63.33);
			path.lineTo(6.38, 63.33);
			path.curveTo(5.67, 61.72, 3.92, 59.37, 3.37, 56.72);
			path.lineTo(26.62, 56.72);
			path.curveTo(26.22, 58.73, 25.31, 61.08, 23.62, 63.33);
			path.closePath();
			path.moveTo(27, 53.32);
			path.lineTo(3, 53.32);
			path.curveTo(2.71, 50.62, 2.73, 48.87, 3, 46.71);
			path.lineTo(27, 46.71);
			path.curveTo(27.33, 49.16, 27.28, 50.85, 27, 53.32);
			path.closePath();
			path.moveTo(26.62, 43.3);
			path.lineTo(3.37, 43.3);
			path.curveTo(4.07, 41.19, 4.68, 38.95, 6.38, 36.69);
			path.lineTo(23.62, 36.69);
			path.curveTo(25.05, 39.31, 25.96, 41.25, 26.62, 43.3);
			path.closePath();
			path.moveTo(21, 33.28);
			path.lineTo(9, 33.28);
			path.curveTo(10.7, 31.42, 12.79, 29.17, 15, 27.47);
			path.curveTo(17.02, 29.02, 19.9, 31.76, 21, 33.28);
			path.closePath();
			break;
		case RNA_ICON:
			path.moveTo(21.26, 83.38);
			path.lineTo(16.54, 83.38);
			path.lineTo(16.54, 86.79);
			path.lineTo(23.69, 86.79);
			path.curveTo(25.51, 89.42, 25.94, 90.43, 26.7, 93.39);
			path.lineTo(16.54, 93.39);
			path.lineTo(16.54, 96.8);
			path.lineTo(27.04, 96.8);
			path.curveTo(27.09, 97.68, 27.16, 98.65, 27.18, 99.95);
			path.curveTo(27.18, 100, 28.59, 99.95, 30, 99.95);
			path.curveTo(29.98, 87.77, 26.46, 83.53, 20.57, 78.07);
			path.curveTo(14.68, 72.62, 12.94, 70.92, 9.02, 66.76);
			path.lineTo(14.29, 66.76);
			path.lineTo(14.29, 63.36);
			path.lineTo(6.39, 63.36);
			path.curveTo(5.12, 61.15, 4.32, 59.47, 3.38, 56.75);
			path.lineTo(14.29, 56.75);
			path.lineTo(14.29, 53.35);
			path.lineTo(3.01, 53.55);
			path.curveTo(2.72, 50.86, 2.73, 49.28, 3.01, 46.74);
			path.lineTo(14.29, 46.74);
			path.lineTo(14.29, 43.34);
			path.lineTo(3.38, 43.34);
			path.curveTo(4.19, 40.93, 4.98, 39.52, 6.39, 36.73);
			path.lineTo(14.29, 36.73);
			path.lineTo(14.29, 33.32);
			path.lineTo(9.02, 33.32);
			path.curveTo(12.37, 28.49, 14.66, 26.12, 20.57, 21.01);
			path.curveTo(26.49, 15.91, 29.93, 12.29, 30, 0.23);
			path.lineTo(30, 0.06);
			path.curveTo(30, 0.06, 28.24, 0, 27.18, 0.06);
			path.curveTo(27.18, 0.06, 27.21, 0.84, 27.04, 3.29);
			path.lineTo(16.54, 3.29);
			path.lineTo(16.54, 6.69);
			path.lineTo(26.7, 6.69);
			path.curveTo(25.89, 9.51, 25.17, 11.17, 23.69, 13.3);
			path.lineTo(16.54, 13.3);
			path.lineTo(16.54, 16.71);
			path.lineTo(21.26, 16.71);
			path.curveTo(6.31, 28.1, 0, 37.89, 0, 50.04);
			path.curveTo(0, 62.2, 5.96, 69.49, 21.26, 83.38);
			path.closePath();
			break;
		case CELL_ICON:
			// cell membrane
			path.moveTo(0.87, 42.66);
			path.curveTo(0, 58.6, 1.29, 79.48, 14.32, 88.6);
			path.curveTo(22.71, 94.47, 34.73, 87.46, 44.9, 86.47);
			path.curveTo(67.63, 84.26, 97.85, 95.25, 112.88, 77.96);
			path.curveTo(125.2, 63.79, 124.12, 36.12, 112.34, 21.5);
			path.curveTo(102.41, 9.17, 81.8, 13.21, 66.16, 11.2);
			path.curveTo(49.15, 9, 29.21, 0, 14.77, 9.29);
			path.curveTo(4.67, 15.78, 1.54, 30.62, 0.87, 42.66);
			path.closePath();
			path.moveTo(112.37, 21.48); // for "3D"
			path.curveTo(124.15, 36.1, 125.22, 63.78, 112.91, 77.94);
			path.curveTo(97.87, 95.24, 67.65, 84.24, 44.92, 86.45);
			path.curveTo(34.75, 87.44, 22.73, 94.45, 14.34, 88.58);
			path.curveTo(23.57, 96.12, 38, 91.78, 49.88, 91.44);
			path.curveTo(72.58, 90.81, 103.16, 100, 117.37, 82.18);
			path.curveTo(130, 66.35, 125.64, 36.78, 112.37, 21.48);
			path.closePath();
			// nucleolus
			path.append(new Ellipse2D.Double(65, 55, 10, 10), false);
			// nucleus
			path.append(new Ellipse2D.Double(34.5, 27.5, 55, 50), false);
			path.append(new Ellipse2D.Double(34.5, 27.5, 55, 50), false);// for fill color
			// mitochondria (simplified version)
			GeneralPath mito = new GeneralPath();
			mito.moveTo(2.6, 2.5);
			mito.curveTo(3.56, 2.41, 3.45, 4.3, 4.36, 4.21);
			mito.curveTo(6, 4.21, 6.04, 1.16, 7.5, 1.14);
			mito.curveTo(9, 1.12, 9, 4, 10.5, 4);
			mito.curveTo(11.91, 4.12, 11.7, 1.11, 13.07, 1.19);
			mito.curveTo(14.57, 1.27, 14.4, 4.48, 15.89, 4.68);
			mito.curveTo(16.88, 4.81, 16.81, 2.74, 17.81, 2.83);
			mito.curveTo(19.49, 3.41, 19.44, 5.24, 18.43, 6.48);
			mito.curveTo(18.05, 6.96, 17.52, 7.35, 16.89, 7.56);
			mito.curveTo(16.24, 7.63, 14.93, 6.58, 14.28, 6.64);
			mito.curveTo(13.11, 6.75, 13.05, 9.07, 11.87, 9.02);
			mito.curveTo(10.78, 8.97, 10.79, 7.09, 9.51, 6.96);
			mito.curveTo(8.33, 6.84, 8.39, 9.11, 7.21, 8.95);
			mito.curveTo(5.9, 8.78, 6.11, 6.13, 4.8, 6);
			mito.curveTo(4.02, 5.92, 4.25, 7.52, 3.46, 7.54);
			mito.curveTo(0.53, 6.91, 0.17, 3.33, 2.65, 2.49);
			mito.closePath();
			mito.append(new Ellipse2D.Double(0, 0, 20, 10), false);
			mito.append(new Ellipse2D.Double(0, 0, 20, 10), false);// for fill color
			AffineTransform at = new AffineTransform();
			at.translate(100, 30);
			at.scale(1.5, 1.5);
			at.rotate(40, 50);
			Shape mitochondria = at.createTransformedShape(mito);
			path.append(mitochondria, false);
			// endoplasmic reticulum
			AffineTransform at2 = new AffineTransform();
			at2.translate(35, -5);
			at2.scale(0.2, 0.2);
			at2.rotate(50, 40);
			Shape endoplasmicReticulum = at2.createTransformedShape(getPluggableShape(Internal.ENDOPLASMIC_RETICULUM));
			path.moveTo(34.5, 27.5);
			path.append(endoplasmicReticulum, false);
			// small miscellaneous organelles
			path.append(new Ellipse2D.Double(20, 65, 5, 5), false);
			path.append(new Ellipse2D.Double(20, 65, 5, 5), false);// for fill color
			path.append(new Ellipse2D.Double(25, 75, 5, 5), false);
			path.append(new Ellipse2D.Double(25, 75, 5, 5), false);// for fill color
			break;
		case MEMBRANE_ICON:
			// phospholipid bilayer
			GeneralPath plipid = new GeneralPath();
			plipid.moveTo(7.09, 16.54); // hydrophobic tail
			plipid.curveTo(6.89, 16.14, 6.07, 14.67, 6.02, 14.08);
			plipid.curveTo(5.9, 12.87, 7.11, 12.28, 7.11, 11.06);
			plipid.curveTo(7.08, 9.8, 5.19, 8.09, 4.9, 7.94);
			plipid.curveTo(5.18, 8.12, 7.01, 9.8, 7.08, 11.06);
			plipid.curveTo(7.04, 12.28, 5.88, 12.88, 6, 14.09);
			plipid.curveTo(6.05, 14.69, 6.84, 16.16, 7.04, 16.56);
			plipid.curveTo(7.62, 17.7, 7.7, 18.13, 7.64, 19);
			plipid.curveTo(7.68, 18.15, 7.67, 17.68, 7.09, 16.54);
			plipid.closePath();
			plipid.moveTo(8, 4.02); // hydrophilic head
			plipid.curveTo(8, 6.24, 6.21, 8.05, 4, 8.05);
			plipid.curveTo(1.79, 8.05, 0, 6.24, 0, 4.02);
			plipid.curveTo(0, 1.8, 1.79, 0, 4, 0);
			plipid.curveTo(6.21, 0, 8, 1.8, 8, 4.02);
			plipid.closePath();
			plipid.moveTo(0.87, 16.54); // hydrophobic tail
			plipid.curveTo(1.07, 16.14, 1.89, 14.67, 1.93, 14.08);
			plipid.curveTo(2.06, 12.87, 0.85, 12.28, 0.85, 11.06);
			plipid.curveTo(0.88, 9.8, 2.77, 8.09, 3.06, 7.94);
			plipid.curveTo(2.77, 8.12, 0.95, 9.8, 0.88, 11.06);
			plipid.curveTo(0.92, 12.28, 2.08, 12.88, 1.96, 14.09);
			plipid.curveTo(1.91, 14.69, 1.12, 16.16, 0.92, 16.56);
			plipid.curveTo(0.34, 17.7, 0.26, 18.13, 0.31, 19);
			plipid.curveTo(0.28, 18.15, 0.29, 17.68, 0.87, 16.54);
			plipid.closePath();
			for (int i = 0; i < 6; i++) {
				AffineTransform at4 = new AffineTransform();
				at4.translate(i * 10.5, 0);
				path.append(at4.createTransformedShape(plipid), false);
				AffineTransform at5 = AffineTransform.getScaleInstance(1, -1);
				at5.translate(i * 10.5, -40);
				path.append(at5.createTransformedShape(plipid), false);
			}
			break;
		case DEGRADATION:
			path.moveTo(31.59f, 18.46f);
			path.curveTo(31.59f, 25.44f, 25.72f, 31.10f, 18.50f, 31.10f);
			path.curveTo(11.27f, 31.10f, 5.41f, 25.44f, 5.41f, 18.46f);
			path.curveTo(5.41f, 11.48f, 11.27f, 5.82f, 18.50f, 5.82f);
			path.curveTo(25.72f, 5.82f, 31.59f, 11.48f, 31.59f, 18.46f);
			path.closePath();
			path.moveTo(0.39f, 0.80f);
			path.curveTo(34.84f, 36.07f, 35.25f, 35.67f, 35.25f, 35.67f);
			break;
		default:
			break;
		}
		return path;
	}

	/**
	 * Returns regular polygon shape given number of sides, width, and height.
	 * 
	 * @param sides the number of sides of polygon.
	 * @param w     the width.
	 * @param h     the height.
	 * @return
	 */
	public static java.awt.Shape getRegularPolygon(int sides, double w, double h) {
		GeneralPath path = new GeneralPath();
		for (int i = 0; i < sides; ++i) {
			double angle = Math.PI * 2 * i / sides;
			double x = (w / 2) * (1 + Math.cos(angle));
			double y = (h / 2) * (1 + Math.sin(angle));
			if (i == 0) {
				path.moveTo((float) x, (float) y);
			} else {
				path.lineTo((float) x, (float) y);
			}
		}
		path.closePath();
		return path;
	}

//	static public java.awt.Shape getCircle(double xCenter, double yCenter, double r, int nPoints) {
//		GeneralPath gp = new GeneralPath();
//		for (int i = 0; i < nPoints; i++) {
//			double angle = i / (double) nPoints * Math.PI * 2;
//			double x = r * Math.cos(angle) + xCenter;
//			double y = r * Math.sin(angle) + yCenter;
//			if (i == 0)
//				gp.moveTo(x, y);
//			else
//				gp.lineTo(x, y);
//		}
//		gp.closePath();
//		return gp;
//	}

	// TODO
//	@Deprecated
//	MIM_PHOSPHORYLATED_SHAPE;
//	MIM_DEGRADATION_SHAPE;
//	MIM_INTERACTION_SHAPE;

//	/**
//	 * Internal, Only for general shape types that can be described as a path. The
//	 * shapes are constructed as a general path with arbitrary size and then resized
//	 * to fit w and h parameters.
//	 */
//	static private java.awt.Shape getPluggableShape(int st) {
//		GeneralPath path = new GeneralPath();
//		switch (st) {
//		case MIM_DEGRADATION:
//			path.moveTo(31.59f, 18.46f);
//			path.curveTo(31.59f, 25.44f, 25.72f, 31.10f, 18.50f, 31.10f);
//			path.curveTo(11.27f, 31.10f, 5.41f, 25.44f, 5.41f, 18.46f);
//			path.curveTo(5.41f, 11.48f, 11.27f, 5.82f, 18.50f, 5.82f);
//			path.curveTo(25.72f, 5.82f, 31.59f, 11.48f, 31.59f, 18.46f);
//			path.closePath();
//			path.moveTo(0.39f, 0.80f);
//			path.curveTo(34.84f, 36.07f, 35.25f, 35.67f, 35.25f, 35.67f);
//			break;
//		case MIM_PHOSPHORYLATED:
//			path.moveTo(5.79f, 4.72f);
//			path.lineTo(5.79f, 18.18f);
//			path.lineTo(13.05f, 18.18f);
//			path.curveTo(15.74f, 18.18f, 17.81f, 17.60f, 19.28f, 16.43f);
//			path.curveTo(20.75f, 15.26f, 21.48f, 13.60f, 21.48f, 11.44f);
//			path.curveTo(21.48f, 9.29f, 20.75f, 7.64f, 19.28f, 6.47f);
//			path.curveTo(17.81f, 5.30f, 15.74f, 4.72f, 13.05f, 4.72f);
//			path.lineTo(5.79f, 4.72f);
//			path.moveTo(0.02f, 0.73f);
//			path.lineTo(13.05f, 0.73f);
//			path.curveTo(17.83f, 0.73f, 21.44f, 1.65f, 23.88f, 3.47f);
//			path.curveTo(26.34f, 5.28f, 27.57f, 7.93f, 27.57f, 11.44f);
//			path.curveTo(27.57f, 14.98f, 26.34f, 17.65f, 23.88f, 19.46f);
//			path.curveTo(21.44f, 21.26f, 17.83f, 22.17f, 13.05f, 22.17f);
//			path.lineTo(5.79f, 22.17f);
//			path.lineTo(5.79f, 36.57f);
//			path.lineTo(0.02f, 36.57f);
//			path.lineTo(0.02f, 0.73f);
//			break;
//		case MIM_INTERACTION:
//			path.moveTo(30.90f, 15.20f);
//			path.curveTo(30.90f, 23.18f, 24.02f, 29.65f, 15.55f, 29.65f);
//			path.curveTo(7.08f, 29.65f, 0.20f, 23.18f, 0.20f, 15.20f);
//			path.curveTo(0.20f, 7.23f, 7.08f, 0.76f, 15.55f, 0.76f);
//			path.curveTo(24.02f, 0.76f, 30.90f, 7.23f, 30.90f, 15.20f);
//			path.closePath();
//			break;
//		default:
//			assert (false);
//		}
//		return path;
//	}

}
