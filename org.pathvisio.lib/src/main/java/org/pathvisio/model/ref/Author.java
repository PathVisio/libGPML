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
package org.pathvisio.model.ref;

import org.bridgedb.Xref;
import org.pathvisio.events.PathwayElementEvent;
import org.pathvisio.props.StaticProperty;

/**
 * This class stores information for an Author. An Author must have name and
 * optionally username, order, and Xref.
 * 
 * A builder pattern is used because it was already written this way, and
 * somewhat preferable to constructor overloading. Example of how an Author
 * object can be created:
 * 
 * Author author = new Author.AuthorBuilder("Jan Doe") .setUsername("janD")
 * .setOrder("1").setXref(new Xref...).build();
 * 
 * @author finterly
 */
public class Author {

	private String name;
	private String username;
	private int order;
	private Xref xref;

	/**
	 * This builder class builds an Author object step-by-step.
	 * 
	 * @author finterly
	 */
	public static class AuthorBuilder {
		private String name; // required
		private String username; // optional
		private int order; // optional
		private Xref xref; // optional

		/**
		 * Public constructor with required attribute name as parameter.
		 * 
		 * @param v the name of this author.
		 */
		public AuthorBuilder(String v) {
			name = v;
		}

		/**
		 * Sets username and returns this builder object.
		 * 
		 * @param v the username of this author.
		 * @return the AuthorBuilder object.
		 */
		public AuthorBuilder setUsername(String v) {
			username = v;
			return this;
		}

		/**
		 * Sets authorship order and returns this builder object.
		 * 
		 * @param v the authorship order of this author.
		 * @return the AuthorBuilder object.
		 */
		public AuthorBuilder setOrder(int v) {
			order = v;
			return this;
		}

		/**
		 * Sets xref and returns this builder object.
		 * 
		 * @param v the orcid number of this author.
		 * @return the AuthorBuilder object.
		 */
		public AuthorBuilder setXref(Xref v) {
			xref = v;
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
		this.username = builder.username;
		this.order = builder.order;
		this.xref = builder.xref;
	}

	/**
	 * Returns the name of this author.
	 * 
	 * @return name the name of this author.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this author.
	 * 
	 * @param v the name of this author.
	 */
	public void setName(String v) {
		name = v;
	}

	/**
	 * Returns the username of this author.
	 * 
	 * @return username the username of this author.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of this author.
	 * 
	 * @param v the username of this author.
	 */
	public void setUsername(String v) {
		username = v;
	}

	/**
	 * Returns the authorship order of this author.
	 * 
	 * @return order the authorship order.
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the authorship order of this author.
	 *
	 * @param v the authorship order.
	 */
	public void setOrder(int v) {
		order = v;
	}

	/**
	 * Returns the Xref for the author.
	 * 
	 * @return xref the xref of the author.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the author.
	 * 
	 * @param v the xref of the author.
	 */
	public void setXref(Xref v) {
		xref = v;
		// TODO
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.XREF));
	}

}
