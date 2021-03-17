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
import org.pathvisio.model.State.EmployeeBuilder;
import org.pathvisio.util.Utils;

/**
 * This class stores information relevant to a pathway element which can belong
 * in a gpml:Group.
 * 
 * @author finterly
 */
public class CommentGroupElement extends PathwayElement {

	private List<Comment> comments = new ArrayList<Comment>(); // length 0 to unbounded
	private List<DynamicProperty> dynamicProperties = new ArrayList<DynamicProperty>(); // length 0 to unbounded
	private List<AnnotationRef> annotationRefs = new ArrayList<AnnotationRef>(); // length 0 to unbounded
	private List<CitationRef> citationRefs = new ArrayList<CitationRef>(); // length 0 to unbounded
	private List<EvidenceRef> evidenceRefs = new ArrayList<EvidenceRef>(); // length 0 to unbounded


	//TODO Builder or Constructor with empty Lists? 
	
	
	public CommentGroupElement(String elementId, List<Comment> comments,
			List<DynamicProperty> dynamicProperties, List<AnnotationRef> annotationRefs, List<CitationRef> citationRefs,
			List<EvidenceRef> evidenceRefs) {
		super(elementId);
		this.comments = comments;
		this.dynamicProperties = dynamicProperties;
		this.annotationRefs = annotationRefs;
		this.citationRefs = citationRefs;
		this.evidenceRefs = evidenceRefs;
	}
	
	
	
	public CommentGroupElement(String elementId) {
		super(elementId);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Returns the list of comments.
	 * 
	 * @return comments the list of comments.
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/**
	 * Sets comments to given list of Comment.
	 * 
	 * @param comments the list of comments.
	 */
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	/**
	 * Adds given comment to comments list.
	 * 
	 * @param comment the comment to be added.
	 */
	public void addComment(Comment comment) {
		comments.add(comment);
	}

	/**
	 * Removes given comment from comments list.
	 * 
	 * @param comment the comment to be removed.
	 */
	public void removeComment(Comment comment) {
		comments.remove(comment);
	}

	/**
	 * TODO: Need to be moved or something...
	 * 
	 * Finds the first comment with a specific source.
	 * 
	 * @returns the comment with a given source.
	 */
	public String findComment(String source) {
		for (Comment c : comments) {
			if (source.equals(c.source)) {
				return c.comment;
			}
		}
		return null;
	}

	/**
	 * Returns the list of key value pair information properties.
	 * 
	 * @return properties the list of properties.
	 */
	public List<DynamicProperty> getProperties() {
		return dynamicProperties;
	}

	/**
	 * Returns the list of annotation references.
	 * 
	 * @return annotationRefs the list of annotation references.
	 */
	public List<AnnotationRef> getAnnotationRefs() {
		return annotationRefs;
	}

	/**
	 * Returns the list of citation references.
	 * 
	 * @return citationRefs the list of citation references.
	 */
	public List<CitationRef> getCitationRefs() {
		return citationRefs;
	}



	public List<EvidenceRef> getEvidenceRefs() {
		return evidenceRefs;
	}



	public void setEvidenceRefs(List<EvidenceRef> evidenceRefs) {
		this.evidenceRefs = evidenceRefs;
	}

//	public List<AnnotationRef> getAnnotationRefList() {
//		if (annotationRefs == null) {
//			annotationRefs = new ArrayList<AnnotationRef>();
//		}
//		return this.annotationRefs;
//	}

}
