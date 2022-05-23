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
package org.pathvisio.libgpml.model;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Set;

import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.model.type.ObjectType;
import org.pathvisio.libgpml.util.Utils;

/**
 * This class only contains static methods and should not be instantiated.
 */
public abstract class GraphLink {

	// ================================================================================
	// LinkableTo Class
	// ================================================================================
	/**
	 * PathwayElements which can be referred to must implement this interface.
	 * {@link DataNode}, {@link DataNode.State}, {@link LineElement.Anchor},
	 * {@link Label}, {@link Shape}, and {@link Group} can all be referred to by a
	 * end {@link LineElement.LinePoint}.
	 */
	public interface LinkableTo {

		/**
		 * Returns the elementId for this pathway element.
		 *
		 * @return the elementId.
		 */
		String getElementId();

		/**
		 * Returns the object type of the pathway object.
		 *
		 * @return objectType the object type.
		 */
		ObjectType getObjectType();

		/**
		 * Returns {@link LinkableFrom}s {@link LineElement.LinePoint} for this pathway
		 * element.
		 *
		 * @return the LinkableFrom line points.
		 */
		Set<LinkableFrom> getLinkableFroms();

		/**
		 * Converts a point to pathway coordinates (relative to the pathway)
		 *
		 * @param p the point2d coordinate.
		 * @return point2d absolute coordinate.
		 */
		Point2D toAbsoluteCoordinate(Point2D p);

		/**
		 * Converts a point to shape coordinates (relative to the bounds of the
		 * LinkableTo).
		 *
		 * @param p a point in absolute model coordinates
		 * @return the same point relative to the bounding box of this pathway element:
		 *         -1,-1 meaning the top-left corner, 1,1 meaning the bottom right
		 *         corner, and 0,0 meaning the center.
		 */
		Point2D toRelativeCoordinate(Point2D p);

	}

	// ================================================================================
	// LinkableFrom Class
	// ================================================================================
	/**
	 * Classes which want to refer *to* a {@link LinkableTo} PathwayElement must
	 * implement this interface. At this time that only goes for
	 * {@link LineElement.LinePoint}.
	 */
	public interface LinkableFrom {

		/**
		 * Returns the {@link LinkableTo} pathway element this {@link LinkableFrom}
		 * elementRef refers to.
		 *
		 * @return the LinkableTo elementRef refers to.
		 */
		LinkableTo getElementRef();

		/**
		 * Returns the relative x coordinate. When the given point is linked to a
		 * pathway element, relX and relY are the relative coordinates on the element,
		 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
		 * of the object.
		 *
		 * @return the relative x.
		 */
		double getRelX();

		/**
		 * Returns the relative y coordinate. When the given point is linked to a
		 * pathway element, relX and relY are the relative coordinates on the element,
		 * where 0,0 is at the center of the object and 1,1 at the bottom right corner
		 * of the object.
		 *
		 * @return the relative y.
		 */
		double getRelY();

		/**
		 * Links this linkableFrom (e.g. linePoint) to the given linkableTo.
		 *
		 * @param elementRef the linkableTo pathway element.
		 * @param relX       the relative x coordinates.
		 * @param relY       the relative y coordinates.
		 */
		void linkTo(LinkableTo elementRef, double relX, double relY);

		/**
		 * Unlinks this linkableFrom (e.g. linePoint) from linkableTo pathway element.
		 */
		void unlink();

		/**
		 * Called whenever the object being referred to changes coordinates.
		 */
		void refeeChanged();
	}

	/**
	 * Return a list of {@link LinkableFrom} {@link LinePoint}(s) referring to a
	 * certain {@link LinkableTo} pathway element or anchor.
	 *
	 * @param elementRef   the LinkableTo.
	 * @param pathwayModel the pathway model.
	 * @return
	 */
	public static Set<LinkableFrom> getReferences(LinkableTo elementRef, PathwayModel pathwayModel) {
		if (pathwayModel == null || Utils.isEmpty(elementRef.getElementId())) {
			return Collections.emptySet();
		} else {
			return pathwayModel.getReferringLinkableFroms(elementRef);
		}
	}
}
