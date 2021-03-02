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

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Set;

import org.pathvisio.util.Utils;

/**
 * This abstract class only contains static methods and should not be
 * instantiated.
 * 
 * @author unknown, finterly
 */
public abstract class ElementLink {

	/**
	 * This interface allows iteration through all objects containing an elementId.
	 * Classes that have an elementId implement this interface, e.g. Anchor, Point,
	 * Citation, Annotation, DataNode, State, Interaction, GraphicalLine, Labels,
	 * Shape, Group.
	 * 
	 * @author unknown, finterly
	 */
	/**
	 * @author p70073399
	 *
	 */
	public interface ElementIdContainer {

		/**
		 * Gets elementId.
		 */
		String getElementId();

		/**
		 * Sets elementId as given String id.
		 * 
		 * @param id the string elementId is set to.
		 */
		void setElementId(String id);

		/**
		 * Generates a unique elementId and uses that.
		 */
		String setGeneratedElementId();

		/**
		 * Returns a set of ElementRefContainer.
		 */
		Set<ElementRefContainer> getReferences();

		/**
		 * Returns the parent gpml data object, needed for maintaining a consistent list
		 * of elementIds.
		 */
		Pathway getPathway();

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

	/**
	 * This interface allows iteration through all objects containing an elementRef.
	 * Classes that refer *to* a ElementIdContainer implement this interface, e.g.
	 * Point, State, DataNode? AnnotationRef?, CitationRef?
	 * 
	 * @author unknown, finterly
	 */
	public interface ElementRefContainer {

		String getElementRef();

		void linkTo(ElementIdContainer idc, double relX, double relY);

		void unlink();

		double getRelX();

		double getRelY();

		/**
		 * Returns the parent Pathway object, needed for maintaining a consistent list
		 * of elementIds.
		 */
		Pathway getPathway();

		/**
		 * Called whenever the object being referred to changes coordinates.
		 */
		void refeeChanged();
	}

	/**
	 * Gives an object that implements the ElementIdContainer interface a elementId,
	 * thereby possibly linking it to new objects.
	 *
	 * This is a helper for classes that need to implement the ElementIdContainer
	 * interface, to avoid duplication.
	 *
	 * @param elementId          the elementId.
	 * @param elementIdContainer the ElementIdContainer which is going to get the
	 *                           new elementId
	 * @param data               the pathway model, which is maintaining a complete
	 *                           list of all elementIds in this pathway.
	 */
	protected static void setElementId(String elementId, ElementIdContainer elementIdContainer, Pathway data) {
		String graphId = elementIdContainer.getElementId();
		if (graphId == null || !graphId.equals(elementId)) {
			if (data != null) {
				if (graphId != null) {
					data.removeGraphId(graphId);
				}
				if (elementId != null) {
					data.addGraphId(elementId, elementIdContainer);
				}
			}
		}
	}

	/**
	 * Return a list of ElementRefContainers (i.e. points) referring to a certain
	 * ElementId.
	 *
	 * @param elementIdthe elementId.
	 * @param data         the pathway model, which is maintaining a complete list
	 *                     of all elementIds in this pathway.
	 * @return a list of ElementRefContainers.
	 */
	public static Set<ElementRefContainer> getReferences(ElementIdContainer elementId, Pathway data) {
		if (data == null || Utils.isEmpty(elementId.getElementId()))
			return Collections.emptySet();
		else
			return data.getReferringObjects(elementId.getElementId());
	}
}
