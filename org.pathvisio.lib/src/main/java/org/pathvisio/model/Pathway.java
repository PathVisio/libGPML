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

import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * Add your documentation here
 * @author ...
 *
 */
public class Pathway {
    private Xref xref;
    private List<Pathway.Author> author;
    private List<Pathway.Comment> comment;
    private List<Pathway.Property> property;
    private List<Pathway.AnnotationRef> annotationRef;
    private List<Pathway.CitationRef> citationRef;
    private Pathway.Graphics graphics;
    
    
    private List<DataNode> dataNodes;
    private List<State> states;
    private List<Interaction> interactions;
    private List<GraphicalLine> graphicalLines;
    private List<Label> labels;
    private List<Shape> shapes;
    private List<Group> groups;
    private InfoBox infoBox;
    private Legend legend;
    private List<Annotation> annotations;
    private List<Citation> citations;
    private String title;
    private String organism;
    private String source;
    private String version;
    private String license;

	/**
	 * Gets the Pathway Xref.
	 * 
	 * @return xref the pathway xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of Pathway Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}
    
    
    
    
    
    
}
