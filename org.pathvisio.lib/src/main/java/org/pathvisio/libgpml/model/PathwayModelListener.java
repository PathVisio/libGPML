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

import java.util.EventListener;

/**
 * Implement this if you want to be notified of changes to a PathwayModel.
 *
 * This means addition of new elements to a PathwayModel and removal of elements from
 * a PathwayModel, but not changes to properties of a single PathwayElement
 *
 * For example this is used by VPathwayModel to refresh itself when a new element is
 * added.
 */
public interface PathwayModelListener extends EventListener {
	
	public void pathwayModified(PathwayModelEvent e);
	
}
