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

import org.bridgedb.Xref;
import org.pathvisio.model.element.*;

/**
 * This class stores information for a Citation.
 * 
 * @author finterly
 */
public class Citation extends PathwayElement {

	/* list of parent pathway elements with citationRef for this citation. */
	private List<PathwayElement> pathwayElements;
	private Xref xref;
	private String url; // optional

	/** Optional attributes for GPML2013a Biopax */
	private String title;
	private String source;
	private String year;
	private List<String> authors;

	/**
	 * Instantiates a Citation pathway element given all possible parameters:
	 * elementId, parent pathway model, xref, and url.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param xref         the citation xref.
	 * @param url          the url of the citation.
	 */
	public Citation(String elementId, PathwayModel pathwayModel, Xref xref, String url) {
		super(elementId, pathwayModel);
		this.pathwayElements = new ArrayList<PathwayElement>();
		this.xref = xref;
		this.url = url;
	}

	/**
	 * Instantiates a Citation pathway element given all possible parameters except
	 * xref.
	 * 
	 * @param elementId    the unique pathway element identifier.
	 * @param pathwayModel the parent pathway model.
	 * @param xref         the citation xref.
	 */
	public Citation(String elementId, PathwayModel pathwayModel, Xref xref) {
		this(elementId, pathwayModel, xref, null);
	}

	/**
	 * Returns the list of pathway elements with citationRef for the citation.
	 * 
	 * @return pathwayElements the list of pathway elements which reference the
	 *         citation.
	 */
	public List<PathwayElement> getPathwayElements() {
		return pathwayElements;
	}

	/**
	 * Adds the given pathway element to pathwayElements list of the citation.
	 * 
	 * @param pathwayElement the given pathwayElement to add.
	 */
	public void addPathwayElement(PathwayElement pathwayElement) {
		pathwayElements.add(pathwayElement);
	}

	/**
	 * Removes the given pathway element from pathwayElements list of the citation.
	 * 
	 * @param pathwayElement the given pathwayElement to remove.
	 */
	public void removePathwayElement(PathwayElement pathwayElement) {
		pathwayElements.remove(pathwayElement);
	}

	/**
	 * Returns the Citation Xref.
	 * 
	 * @return xref the citation xref.
	 */
	public Xref getXref() {
		return xref;
	}

	/**
	 * Sets the Xref for the citation.
	 * 
	 * @param xref the xref of the citation.
	 */
	public void setXref(Xref xref) {
		this.xref = xref;
	}

	/**
	 * Returns the url of the citation.
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

	/**
	 * Returns source for the citation (for GPML2013a Biopax).
	 * 
	 * @return title the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets source for the citation (for GPML2013a Biopax).
	 * 
	 * @param title the title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns source for the citation (for GPML2013a Biopax).
	 * 
	 * @return source the source.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets source for the citation (for GPML2013a Biopax).
	 * 
	 * @param source the source.
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Returns year for the citation (for GPML2013a Biopax).
	 * 
	 * @return year the year.
	 */
	public String getYear() {
		return year;
	}

	/**
	 * Sets year for the citation (for GPML2013a Biopax).
	 * 
	 * @param year the year.
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * Returns list of authors for the citation (for GPML2013a Biopax).
	 * 
	 * @return authors the list of authors.
	 */
	public List<String> getAuthors() {
		return authors;
	}

	/**
	 * Sets list of authors for the citation (for GPML2013a Biopax).
	 * 
	 * @param authors the list of authors.
	 */
	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

//	/**
//	 * Returns list of nested parent objects holding/referencing the citation. Depth
//	 * first search of the pathway model for citation. Returns pathway element if it
//	 * is direct or indirect holder of the citation.
//	 * 
//	 * @param parentsOfParents the list of authors.
//	 */
//	public ArrayList<ArrayList<Object>> getParents() {
//		ArrayList<ArrayList<Object>> parentsOfParents = new ArrayList<ArrayList<Object>>();
//		searchPathway(this.getPathwayModel().getPathway(), parentsOfParents);
//		for (DataNode dataNode : this.getPathwayModel().getDataNodes()) {
//			searchElementInfo(dataNode, parentsOfParents);
//			for (State state : dataNode.getStates())
//				searchElementInfo(state, parentsOfParents);
//		}
//		for (GraphicalLine graphicalLines : this.getPathwayModel().getGraphicalLines())
//			searchElementInfo(graphicalLines, parentsOfParents);
//		for (Interaction interaction : this.getPathwayModel().getInteractions())
//			searchElementInfo(interaction, parentsOfParents);
//		for (Label label : this.getPathwayModel().getLabels())
//			searchElementInfo(label, parentsOfParents);
//		for (Shape shape : this.getPathwayModel().getShapes())
//			searchElementInfo(shape, parentsOfParents);
//		for (Group group : this.getPathwayModel().getGroups())
//			searchElementInfo(group, parentsOfParents);
//		return parentsOfParents;
//	}
//
//	// TODO
//	public void searchPathway(Pathway pathway, ArrayList<ArrayList<Object>> parentsOfParents) {
//		boolean isParent = false;
//		List<Object> parents = new ArrayList<Object>();
//		isParent = searchCitationRefs(pathway.getCitationRefs(), parents, isParent);
//		isParent = false;
//		isParent = searchAnnotationRefs(pathway.getAnnotationRefs(), parents, isParent);
//		if (isParent)
//			parents.add(pathway);
//		if (!parents.isEmpty())
//			parentsOfParents.add((ArrayList<Object>) parents);
//	}

	// TODO
	
	public ArrayList<ArrayList<Object>> getParents() {
		ArrayList<ArrayList<Object>> parentsOfParents = new ArrayList<ArrayList<Object>>();
		PathwayModel pathwayModel = this.getPathwayModel();
		searchPathway(this.getPathwayModel().getPathway(), parentsOfParents);
		for (DataNode dataNode : pathwayModel.getDataNodes()) {
			searchElementInfo(dataNode, parentsOfParents);
			for (State state : dataNode.getStates())
				searchElementInfo(state, parentsOfParents);
		}
		for (GraphicalLine graphicalLines : pathwayModel.getGraphicalLines())
			searchElementInfo(graphicalLines, parentsOfParents);
		for (Interaction interaction : pathwayModel.getInteractions())
			searchElementInfo(interaction, parentsOfParents);
		for (Label label : pathwayModel.getLabels())
			searchElementInfo(label, parentsOfParents);
		for (Shape shape : pathwayModel.getShapes())
			searchElementInfo(shape, parentsOfParents);
		for (Group group : pathwayModel.getGroups())
			searchElementInfo(group, parentsOfParents);
		return parentsOfParents;
	}
	
	public void searchPathway(Pathway pathway, ArrayList<ArrayList<Object>> parentsOfParents) {
		loopCitationRefs(null, pathway.getCitationRefs(), parentsOfParents, 0);
		loopAnnotationRefs(null, pathway.getAnnotationRefs(), parentsOfParents, 0);
//		loopAnnotationRefs(this.getPathwayModel().getPathway().getAnnotationRefs(), parentsOfParents1, parents, 0);
//		loopCitationRefs(this.getPathwayModel().getPathway().getCitationRefs(), parentsOfParents2, parents, 0);
	}
	
	public void searchElementInfo(ElementInfo elementInfo, ArrayList<ArrayList<Object>> parentsOfParents) {
		loopCitationRefs(elementInfo, elementInfo.getCitationRefs(), parentsOfParents, 0);
		loopAnnotationRefs(elementInfo, elementInfo.getAnnotationRefs(), parentsOfParents, 0);
	}
	
	public void loopAnnotationRefs(ElementInfo elementInfo, List<AnnotationRef> annotationRefs, ArrayList<ArrayList<Object>> parentsOfParents,
			int level) {
		for (AnnotationRef annotationRef : annotationRefs) {
			ArrayList<Object> parents = new ArrayList<Object>();
			if (level == 0 && elementInfo != null)
				parents.add(elementInfo);
			parents.add(annotationRef);
			for (CitationRef citationRef : annotationRef.getCitationRefs()) {
				parents.add(citationRef);
				int next = level + 1;
				loopAnnotationRefs(null, citationRef.getAnnotationRefs(), parentsOfParents, next);
			}
			if (level == 0)
				parentsOfParents.add(parents);
		}
	}

	public void loopCitationRefs(ElementInfo elementInfo, List<CitationRef> citationRefs, ArrayList<ArrayList<Object>> parentsOfParents,
			int level) {
		for (CitationRef citationRef : citationRefs) {
			ArrayList<Object> parents = new ArrayList<Object>();
			if (level == 0 && elementInfo != null)
				parents.add(elementInfo);
			parents.add(citationRef);
			for (AnnotationRef annotationRef : citationRef.getAnnotationRefs()) {
				parents.add(annotationRef);
				int next = level + 1;
				loopCitationRefs(null, annotationRef.getCitationRefs(), parentsOfParents, next);
			}
			if (level == 0)
				parentsOfParents.add(parents);
		}
	}

	
//	// TODO Less desirable because necessitates passing in a null or empty list...
//	public void loopAnnotationRefs(List<AnnotationRef> annotationRefs, ArrayList<ArrayList<Object>> parentsOfParents,
//			ArrayList<Object> parents, int level) {
//		for (AnnotationRef annotationRef : annotationRefs) {
//			if (level == 0)
//				parents = new ArrayList<Object>();
//			parents.add(annotationRef);
//			int next = level + 1;
//			loopCitationRefs(annotationRef.getCitationRefs(), parentsOfParents, parents, next);
//			if (level == 0)
//				parentsOfParents.add(parents);
//		}
//	}
//
//	public void loopCitationRefs(List<CitationRef> citationRefs, ArrayList<ArrayList<Object>> parentsOfParents,
//			ArrayList<Object> parents, int level) {
//		for (CitationRef citationRef : citationRefs) {
//			if (level == 0)
//				parents = new ArrayList<Object>();
//			parents.add(citationRef);
//			int next = level + 1;
//			loopAnnotationRefs(citationRef.getAnnotationRefs(), parentsOfParents, parents, next);
//			if (level == 0)
//				parentsOfParents.add(parents);
//		}
//	}

//	// TODO
//	public void searchElementInfo(ElementInfo elementInfo, ArrayList<ArrayList<Object>> parentsOfParents) {
//		boolean isParent = false;
//		List<Object> parents = new ArrayList<Object>();
//		isParent = searchCitationRefs(elementInfo.getCitationRefs(), parents, isParent);
//		isParent = searchAnnotationRefs(elementInfo.getAnnotationRefs(), parents, isParent);
//		if (isParent)
//			parents.add(elementInfo);
//		if (!parents.isEmpty())
//			parentsOfParents.add((ArrayList<Object>) parents);
//	}
//
//	// TODO
//	public boolean searchCitationRefs(List<CitationRef> citationRefs, List<Object> parents, boolean isParent) {
//		for (CitationRef citationRef : citationRefs) {
//			if (this == citationRef.getCitation()) {
//				parents.add(citationRef);
//				isParent = true;
//			}
//			isParent = searchAnnotationRefs(citationRef.getAnnotationRefs(), parents, isParent);
//		}
//		return isParent;
//	}
//
//	// TODO
//	public boolean searchAnnotationRefs(List<AnnotationRef> annotationRefs, List<Object> parents, boolean isParent) {
//		for (AnnotationRef annotationRef : annotationRefs) {
//			System.out.println(annotationRef);
//			isParent = searchCitationRefs(annotationRef.getCitationRefs(), parents, isParent);
//			if (isParent) {
//				parents.add(annotationRef);
//			}
//		}
//		return isParent;
//	}

}
