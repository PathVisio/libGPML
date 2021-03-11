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

/**
 * This class stores all information relevant to an Author.
 * 
 * @author finterly
 */
public class Author {

	protected String name;
	protected String fullName;
	protected String email;

	public Author(String name, String fullName, String email) {
		this.name = name;
		this.fullName = fullName;
		this.email = email;
	}

	/**
	 * Gets the name of this author.
	 * 
	 * @return name the name of this author.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this author.
	 * 
	 * @param name the name of this author.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the full name of this author.
	 * 
	 * @return fullName the full name of this author.
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets the name of this author.
	 * 
	 * @param fullName the full name of this author.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Gets the email address of this author.
	 * 
	 * @return email the email address of this author.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email address of this author.
	 *
	 * @param email the email address of this author.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
