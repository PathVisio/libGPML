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

import java.util.ArrayList;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * This class stores all information relevant to an Interaction pathway element.
 * 
 * @author finterly
 */
public class Interaction extends PathwayElement {

	protected String groupRef; //optional
	protected LineStyleProperty lineStyleProperty;
	
	protected Xref xref;
	
	// TODO Add getters and setters
	protected List<Point> points;
	protected List<Anchor> anchors = new ArrayList<Anchor>(); 
	protected List<Comment> comments; //optional
	protected List<DynamicProperty> dynamicProperties; // optional
	protected List<AnnotationRef> annotationRefs; //optional
	protected List<CitationRef> citationRefs; //optional
	protected List<EvidenceRef> evidenceRefs; // optional

	public Interaction() {
		super();
	}

	// Add Constructors

	/**
	 * Gets the DataNode Xref.
	 * 
	 * @return xref the datanode xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of DataNode Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}

}
