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

/**
 * Used to notify listeners of changes to the model, i.e a Pathway or
 * PathwayElement. This can mean the addition or removal of whole elements, or
 * just a modification to one of the properties of an existing element.
 *
 * This event is currently used both by PathwayListener's and
 * PathwayElementListener's. That may change in the future.
 * 
 * @author unknown
 */
public class PathwayModelEvent {

	public static final int DELETED = 2; // sent to pathway model listeners when an object deleted
	public static final int ADDED = 3; // sent to pathway model listeners when an object added
	public static final int RESIZED = 4; // sent to pathway model listeners when an object resized

	private PathwayObject affectedData;
	private int type;

	/**
	 * Instantiates a pathway model event.
	 * 
	 * @param pathwayObject the pathway object.
	 * @param t             the integer for type.
	 */
	public PathwayModelEvent(PathwayObject pathwayObject, int t) {
		affectedData = pathwayObject;
		type = t;
	}

	/**
	 * Returns the affected data.
	 * 
	 * @return the pathway object affected.
	 */
	public PathwayObject getAffectedData() {
		return affectedData;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the integer type.
	 */
	public int getType() {
		return type;
	}

}
