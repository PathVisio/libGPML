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
import java.util.Random;
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
	 * Generates random IDs, based on strings of hex digits (0..9 or a..f). IDs are
	 * unique across elementIds per pathway and referenced by elementRefs and
	 * groupRefs.
	 * 
	 * NB: elementId previously named graphId. Group pathway elements previously had
	 * both elementId and groupId(deprecated).
	 * 
	 * @param ids the collection of already existing IDs.
	 * @return result the new unique ID unique for this pathway.
	 */
	public static String getUniqueId(Set<String> ids) {
		String result;
		Random random = new Random();
		int mod = 0x60000; // 3 hex letters
		int min = 0xa0000; // must start with a letter
		// add hex letters if set size large
		if ((ids.size()) > 0x10000) {
			mod = 0x60000000;
			min = 0xa0000000;
		}
		do {
			result = Integer.toHexString(Math.abs(random.nextInt()) % mod + min);
		} while (ids.contains(result));
		return result;
	}

	/**
	 * Gives an object that implements the ElementIdContainer interface a elementId,
	 * thereby possibly linking it to new objects.
	 *
	 * This is a helper for classes that need to implement the ElementIdContainer
	 * interface, to avoid duplication.
	 *
	 * @param elementId     the elementId.
	 * @param object        the object which is going to get the new elementId
	 * @param parentPathway the pathway model, which is maintaining a complete list
	 *                      of all elementIds in this pathway.
	 */
	protected static void setElementId(String elementId, ElementIdContainer object, Pathway parentPathway) {
		String graphId = object.getElementId();
		if (graphId == null || !graphId.equals(elementId)) {
			if (parentPathway != null) {
				if (graphId != null) {
					parentPathway.removeElementId(graphId);
				}
				if (elementId != null) {
					parentPathway.addElementId(elementId, object);
				}
			}
		}
	}

	/**
	 * Returns a list of ElementRefContainers (i.e. points) referring to a certain
	 * ElementId.
	 *
	 * @param elementId     the elementId.
	 * @param parentPathway the parent pathway model, which is maintaining a
	 *                      complete list of all elementIds in this pathway.
	 * @return a list of ElementRefContainers.
	 */
	public static Set<ElementRefContainer> getReferences(ElementIdContainer elementId, Pathway parentPathway) {
		if (parentPathway == null || Utils.isEmpty(elementId.getElementId()))
			return Collections.emptySet();
		else
			return parentPathway.getReferringObjects(elementId.getElementId());
	}

	/**
	 * This interface allows iteration through all objects containing an elementId.
	 * All pathway element classes have an elementId and implement this interface.
	 * 
	 * @author unknown, finterly
	 */
	public interface ElementIdContainer {

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

	/**
	 * This interface allows iteration through all objects containing an elementRef.
	 * Classes that refer to an ElementIdContainer implement this interface, e.g.
	 * Point, State, DataNode, AnnotationRef, CitationRef, EvidenceRef.
	 * 
	 * @author unknown, finterly
	 */
	public interface ElementRefContainer {
		/**
		 * Returns the parent Pathway object, needed for maintaining a consistent list
		 * of elementIds.
		 */
		Pathway getPathway();

		String getElementRef();

		void linkTo(ElementIdContainer elementIdContainer, double relX, double relY);

		void unlink();

		double getRelX();

		double getRelY();

		/**
		 * Called whenever the object being referred to changes coordinates.
		 */
		void refeeChanged();
	}

}
