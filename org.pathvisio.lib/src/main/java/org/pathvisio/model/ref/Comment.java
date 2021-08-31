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

/**
 * This class stores all information relevant to a Comment. Comments can be
 * descriptions or arbitrary notes. Each comment has a source and a text.
 * Pathway elements (e.g. DataNode, State, Interaction, GraphicalLine, Label,
 * Shape, Group) can have zero or more comments with it.
 * 
 * @author unknown, finterly
 */
public class Comment {

	private String source; // optional
	private String commentText; // required

	/**
	 * Instantiates a Comment with source and commentText.
	 * 
	 * @param source      the source of this comment.
	 * @param commentText the text of the comment, between Comment tags in GPML.
	 */
	public Comment(String source, String commentText) {
		this.source = source;
		this.commentText = commentText;
	}

	/**
	 * Instantiates a Comment with just commentText.
	 * 
	 * @param commentText the text of this comment, between Comment tags in GPML.
	 */
	public Comment(String commentText) {
		this.commentText = commentText;
	}

	/**
	 * Returns the source of this Comment.
	 * 
	 * @return source the source of this comment.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source of this Comment.
	 * 
	 * @param v the source of this comment.
	 */
	public void setSource(String v) {
		if (v != null) {
			source = v;
			// changed();
		}
	}

	/**
	 * Returns the text of this Comment.
	 * 
	 * @return commentText the text of this comment.
	 */
	public String getCommentText() {
		return commentText;
	}

	/**
	 * Sets the text of this Comment.
	 * 
	 * @param v the text of this comment.
	 */
	public void setCommentText(String v) {
		if (v != null) {
			commentText = v;
		}
	}

}
