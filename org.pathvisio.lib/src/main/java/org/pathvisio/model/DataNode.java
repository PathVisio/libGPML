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

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * This class stores information for a DataNode pathway element.
 * 
 * @author finterly
 */
public class DataNode extends ShapedElement {

	private String elementRef; // optional
	private String textLabel;
	private DataNodeType type = DataNodeType.UNKNOWN; // TODO: Getter/Setter weird
	private Xref xref;
	// TODO elementRef methods

	public String getElementRef() {
		return elementRef;
	}

	public void setElementRef(String elementRef) {
		this.elementRef = elementRef;
	}

	/**
	 * Returns the text of of the datanode.
	 * 
	 * @return textLabel the text of of the datanode.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the datanode.
	 * 
	 * @param textLabel the text of of the datanode.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Returns the type of the datanode.
	 * 
	 * @return type the type of datanode, e.g. complex.
	 */
	public DataNodeType getType() {
		return type;
	}

	/**
	 * Sets the type of the datanode.
	 * 
	 * @param type the type of datanode, e.g. complex.
	 */
	public void setType(DataNodeType type) {
		this.type = type; // TODO: default type
		this.type = type;
	}

	/**
	 * Returns the DataNode Xref.
	 * 
	 * @return xref the datanode xref.
	 */
	public Xref getXref() {
		return xref;
	}

	public String getXrefIdentifier() {
		return xref.getId();
	}

	public DataSource getXrefDataSource(Xref xref) {
		DataSource dataSource = xref.getDataSource();
		String sysCode = dataSource.getSystemCode();
		String fullName = dataSource.getFullName();
		return dataSource;
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
