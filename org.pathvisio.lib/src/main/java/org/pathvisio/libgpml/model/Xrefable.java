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

import org.bridgedb.Xref;
import org.pathvisio.libgpml.model.DataNode.State;


/**
 * Interface for classes which can have an {@link Xref}. This class is
 * implemented by {@link Pathway}, {@link DataNode}, {@link State},
 * {@link Interaction}, and {@link Group}.
 * 
 * @author finterly
 */
public interface Xrefable {

	/**
	 * Returns the Xref for this pathway element.
	 * 
	 * @return xref the xref of this pathway element.
	 */
	public Xref getXref();

	/**
	 * Sets the Xref for this pathway element.
	 * 
	 * @param v the xref to set for this pathway element.
	 */
	public void setXref(Xref v);

}