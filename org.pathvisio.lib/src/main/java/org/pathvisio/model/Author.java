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
 * This class stores information for an Author. An Author must have name and
 * optionally fullName and/or email. Is Pathway.Author in GPML. 
 * 
 * Because constructors cannot have the same signature, a builder pattern is
 * implemented for Author. Example of how an Author object can be created:
 * 
 * Author author = new Author.AuthorBuilder("Jan") 
 * 			.setfullName("Jan Doe")
 * 			.setEmail("jdoe@email.com").build();
 * 
 * @author finterly
 */
public class Author {

	private String name;
	private String fullName;
	private String email;

	/**
	 * This builder class builds an Author object step-by-step.
	 * 
	 * @author finterly
	 */
	public static class AuthorBuilder {
		private String name; // required
		private String fullName; // optional
		private String email; // optional

		/**
		 * Public constructor with required attribute name as parameter.
		 * 
		 * @param name the name of this author.
		 */
		public AuthorBuilder(String name) {
			this.name = name;
		}

		/**
		 * Sets fullName and returns this builder object.
		 * 
		 * @param fullName the full name of this author.
		 * @return the AuthorBuilder object.
		 */
		public AuthorBuilder setFullName(String fullName) {
			this.fullName = fullName;
			return this;
		}

		/**
		 * Sets email and returns this builder object.
		 * 
		 * @param email the email of this author.
		 * @return the AuthorBuilder object.
		 */
		public AuthorBuilder setEmail(String email) {
			this.email = email;
			return this;
		}

		/**
		 * Calls the private constructor in the Author class and passes builder object
		 * itself as the parameter to this private constructor.
		 * 
		 * @return the created Author object.
		 */
		public Author build() {
			return new Author(this);
		}
	}

	/**
	 * Private constructor for Author which takes AuthorBuilder object as its
	 * argument.
	 * 
	 * @param builder the AuthorBuilder object.
	 */
	private Author(AuthorBuilder builder) {
		this.name = builder.name;
		this.fullName = builder.fullName;
		this.email = builder.email;
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
	 * Sets the full name of this author.
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
