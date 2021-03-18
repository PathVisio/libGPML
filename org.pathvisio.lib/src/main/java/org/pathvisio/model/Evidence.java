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
 * This class stores information for an Evidence.
 * 
 * @author finterly
 */
public class Evidence {

	private String elementId;
	private Xref xref;
	private String name; // optional
	private String url; // optional

	// Uses Builder

	public static class EvidenceBuilder {
		private String elementId;
		private Xref xref;
		private String name; // optional
		private String url; // optional

		public EvidenceBuilder(String elementId, Xref xref) {
			this.elementId = elementId;
			this.xref = xref;
		}

		public EvidenceBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public EvidenceBuilder setUrl(String url) {
			this.url = url;
			return this;
		}

		public Evidence build() {
			// call the private constructor in the outer class
			return new Evidence(this);
		}
	}

	private Evidence(EvidenceBuilder builder) {
		this.elementId = builder.elementId;
		this.xref = builder.xref;
		this.setName(builder.name);
		this.url = builder.url;
	}

	/**
	 * Gets the elementId of the citation.
	 * 
	 * @return elementId the unique id of the citation.
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * Sets ID of the citations
	 * 
	 * @param elementId the unique id of the citation.
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * Gets the Citation Xref.
	 * 
	 * @return xref the citation xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Instantiates and sets the value of Citation Xref.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 */
	public void setXref(String identifier, String dataSource) {
		xref = new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		xref = new Xref(identifier, DataSource.getByAlias(dataSource));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the url of the citation.
	 * 
	 * @return url the url of the citation.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url of the citation.
	 * 
	 * @param url the url of the citation.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
