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
public class Comment {

	private String source; // optional
	private String commentText; //optional 

	/**
	 * Instantiates a Comment with source and commentText.
	 * 
	 * @param source the source of the comment.
	 * @param text   the text of the comment, between Comment tags in GPML.
	 */
	public Comment(String source, String commentText) {
		this.source = source;
		this.commentText = commentText;
	}

	/**
	 * Instantiates a Comment with just commentText.
	 * 
	 * @param text the text of the comment, between Comment tags in GPML.
	 */
	public Comment(String commentText) {
		this.commentText = commentText;
	}
	

	/**
	 * Returns the source of the Comment.
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
		if (source != null) {
			this.source = source;
			// changed();
		}
	}

	/**
	 * Returns the text of the Comment.
	 * 
	 * @return comment the text of the comment.
	 */
	public String getCommentText() {
		return commentText;
	}

	/**
	 * Sets the text of the Comment.
	 * 
	 * @param comment the text of the comment.
	 */
	public void setCommentText(String commentText) {
		if (commentText != null) {
			this.commentText = commentText;
		}
	}

}
