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

import java.awt.geom.Rectangle2D;

/**
 * Interface for classes which can belong in a {@link Group}. These classes
 * include {@link DataNode}, {@link GraphicalLine}, {@link Label},
 * {@link Shape}, and {@link Group}.
 * 
 * @author finterly
 */
public interface Groupable {
	
	/**
	 * Returns the pathway model of this pathway element.
	 * 
	 * @return the pathway model of this pathway element.
	 */
	public PathwayModel getPathwayModel();

	
	/**
	 * Returns the parent group of this pathway element. In GPML, groupRef refers to
	 * the elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of this pathway element.
	 */
	public Group getGroupRef();

	/**
	 * Checks whether this pathway element belongs to a group.
	 *
	 * @return true if and only if the group of this pathway element is effective.
	 */
	public boolean hasGroupRef();

	/**
	 * Verifies if given parent group is new and valid. Sets the parent group of the
	 * pathway element. Adds this pathway element to the the pathwayElements list of
	 * the new parent group. If there is an old parent group, this pathway element
	 * is removed from its pathwayElements list.
	 * 
	 * @param groupRef the new parent group to set.
	 */
	public void setGroupRefTo(Group groupRef);

	/**
	 * Sets the parent group for this pathway element.
	 * 
	 * @param groupRef the given group to set.
	 */
//	protected void setGroupRef(Group groupRef);

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	public void unsetGroupRef();

//	/**
//	 * Terminates this pathway element.
//	 */
//	void terminate();

	/**
	 * Returns the rectangular bounds of the object after rotation is applied.
	 */
	public Rectangle2D getRotatedBounds();

	/**
	 * Get the rectangular bounds of the object without rotation taken into account.
	 */
	public Rectangle2D getBounds();

	public double getCenterX();

	public void setCenterX(double d);

	public double getCenterY();

	public void setCenterY(double d);

	/**
	 * NB: no setHeight() for Groupable because not applicable for line pathway
	 * element. Lines can be positioned partially outside of a Group's border.
	 * 
	 * @return
	 */
	public double getHeight();

	/**
	 * NB: no setWidth() for Groupable because not applicable for line pathway
	 * element.Lines can be positioned partially outside of a Group's border.
	 * 
	 * @return
	 */
	public double getWidth();

	public double getLeft();

	public void setLeft(double d);

	public double getTop();

	public void setTop(double d);
}