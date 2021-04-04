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
package temp;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import org.pathvisio.model.Pathway;
import org.pathvisio.util.Utils;

/**
 * This abstract class only contains static methods and should not be
 * instantiated.
 * 
 * @author unknown, finterly
 */
public abstract class ElementLink {


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


}
