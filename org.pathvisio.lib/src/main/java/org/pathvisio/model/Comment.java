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
 * This class stores all information relevant to a Comment. Comments can be
 * descriptions or arbitrary notes. Each comment has a source and a text.
 * Pathway elements (e.g. DataNode, State, Interaction, GraphicalLine, Label,
 * Shape, Group) can have zero or more comments with it.
 * 
 * @author unknown, finterly
 */
public class Comment implements Cloneable {

	private String source;
	private String content;
	private PathwayElement parent;

	/**
	 * Instantiates a Comment.
	 * 
	 * @param source the source of the comment.
	 */
	public Comment(String source, String commentText, PathwayElement parent) {
		this.source = source;
		this.content = commentText;
		this.parent = parent;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Gets the source of the Comment.
	 * 
	 * @return source the source of the comment.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source of the Comment.
	 * 
	 * @param source the source of the comment.
	 */
	public void setSource(String source) {
		if (source != null && !this.source.equals(source)) {
			this.source = source;
			// changed();
		}
	}

	/**
	 * Gets the text of the Comment.
	 * 
	 * @return comment the text of the comment.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the text of the Comment.
	 * 
	 * @param comment the text of the comment.
	 */
	public void setContent(String content) {
		if (content != null) {
			this.content = content;
		}
	}

	/**
	 * Gets the parent PathwayElement to which the Comment belongs.
	 * 
	 * @return parent the parent pathway element.
	 */
	public PathwayElement getParent() {
		return parent;
	}

	/**
	 * Sets the parent PathwayElement to which the Comment belongs.
	 * 
	 * @param parent the parent pathway element.
	 */
	public void setCommentText(PathwayElement parent) {
		this.parent = parent;

	}

}
