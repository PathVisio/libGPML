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
package org.pathvisio.io;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.*;
import org.pathvisio.model.element.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.ref.Annotation;
import org.pathvisio.model.ref.AnnotationRef;
import org.pathvisio.model.ref.Citation;
import org.pathvisio.model.ref.CitationRef;
import org.pathvisio.model.ref.ElementInfo;
import org.pathvisio.model.ref.UrlRef;
import org.pathvisio.model.type.*;
import org.pathvisio.util.ColorUtils;
import org.pathvisio.util.GroupRectPropertyUtils;
import org.pathvisio.util.XrefUtils;

/**
 * This class reads a PathwayModel from an input source (GPML 2013a).
 * 
 * In GPML2013a, GraphIds (elementIds) are missing for some pathway elements, or
 * may conflict with Biopax rdf:id (Biopax elementIds) or GroupId (equivalent to
 * elementIds for Groups).
 * 
 * To ensure unique elementIds for a reading session, all ids are stored in
 * {@link Set} elementIdSet. Biopax id or GroupId and corresponding newly
 * assigned elementIds are stored in {@link Map} biopaxIdToNew or groupIdToNew.
 * Some Biopax contain duplicate information and are not added to the pathway
 * model. The {@link Map} duplicateToBiopaxId stores the duplicate Biopax id and
 * the id of the existing Biopax/Citation so that BiopaxRefs for the duplicate
 * can be properly mapped to the existing Biopax. The {@link List} lineList
 * stores line elementIds .
 * 
 * @author finterly
 */
public class GPML2013aReader extends GPML2013aFormatAbstract implements GpmlFormatReader {

	public static final GPML2013aReader GPML2013aREADER = new GPML2013aReader("GPML2013a.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2013a"));

	/**
	 * {@link #readFromRoot}
	 */
	protected GPML2013aReader(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

	/**
	 * Reads information from root element of Jdom document {@link Document} to the
	 * pathway model {@link PathwayModel}. Referenced objects or pathway elements
	 * are read first. For example, Biopax are read before BiopaxRefs. Groups are
	 * read before other pathway element. DataNodes are read before States. And
	 * Points are read last.
	 * 
	 * See the following links for more information on elementIdSet:
	 * {@link #readAllElementIds}; biopaxIdToNew:
	 * {@link #readBiopaxPublicationXref}; duplicateToBiopaxId:
	 * {@link #readBiopaxPublicationXref}; groupIdToNew: {@link #readGroups};
	 * lineList {@link #readGraphicalLines}, {@link #readInteractions},
	 * {@link #readPoints}.
	 * 
	 * @param pathwayModel the given pathway model.
	 * @param root         the root element of given Jdom document.
	 * @return pathwayModel the pathway model after reading root element.
	 * @throws ConverterException
	 */
	public PathwayModel readFromRoot(PathwayModel pathwayModel, Element root) throws ConverterException {

		Set<String> elementIdSet = new HashSet<String>(); // all elementIds
		Map<String, String> biopaxIdToNew = new HashMap<String, String>(); // biopax ids and new unique elementIds
		Map<String, String> duplicateToBiopaxId = new HashMap<String, String>(); // duplicate biopaxIds
		Map<String, String> groupIdToNew = new HashMap<String, String>(); // groupIds and new unique elementIds
		List<String> lineList = new ArrayList<String>(); // ordered list of line pathway element elementIds
		readAllElementIds(pathwayModel, root, elementIdSet);// reads all elementIds and stores in set
		// reads pathway meta data
		Pathway pathway = readPathway(root);
		pathwayModel.setPathway(pathway);
		// reads biopax, equivalent to annotations and citations
		readBiopax(pathwayModel, root, elementIdSet, biopaxIdToNew, duplicateToBiopaxId);
		// reads pathway comments, biopaxRefs, dynamic properties
		readPathwayInfo(pathwayModel, root, biopaxIdToNew, duplicateToBiopaxId);
		// reads groups first and then groupRefs
		readGroups(pathwayModel, root, elementIdSet, biopaxIdToNew, duplicateToBiopaxId, groupIdToNew);
		readGroupGroupRef(pathwayModel, root, groupIdToNew);
		readLabels(pathwayModel, root, elementIdSet, biopaxIdToNew, duplicateToBiopaxId, groupIdToNew);
		readShapes(pathwayModel, root, elementIdSet, biopaxIdToNew, duplicateToBiopaxId, groupIdToNew);
		readDataNodes(pathwayModel, root, elementIdSet, biopaxIdToNew, duplicateToBiopaxId, groupIdToNew);
		// reads states after data nodes
		readStates(pathwayModel, root, elementIdSet, biopaxIdToNew, duplicateToBiopaxId);
		readInteractions(pathwayModel, root, elementIdSet, biopaxIdToNew, duplicateToBiopaxId, groupIdToNew, lineList);
		readGraphicalLines(pathwayModel, root, elementIdSet, biopaxIdToNew, duplicateToBiopaxId, groupIdToNew,
				lineList);
		// reads points last
		readPoints(pathwayModel, root, elementIdSet, groupIdToNew, lineList);
		// removes empty groups
		removeEmptyGroups(pathwayModel);
		calculateGroupRectProperty(pathwayModel.getGroups());
		return pathwayModel;
	}

	/**
	 * Calculates and sets rect properties (centerX, centerY, width, height) for
	 * groups after pathwayElements {@link List} is filled.
	 * {@link #GroupRectProperty#calculateGroupBounds} Iterates over all group
	 * pathway element members to find the total rectangular bounds, taking into
	 * account rotation of the nested elements.
	 * 
	 * @param groups the list of groups.
	 */
	protected void calculateGroupRectProperty(List<Group> groups) {
		for (Group group : groups) {
			Rectangle2D bounds = GroupRectPropertyUtils.calculateGroupBounds(group);
			group.getRectProperty().getCenterXY().setX(bounds.getCenterX());
			group.getRectProperty().getCenterXY().setY(bounds.getCenterY());
			group.getRectProperty().setWidth(bounds.getWidth());
			group.getRectProperty().setHeight(bounds.getHeight());
		}
	}

	/**
	 * Reads elementIds (GraphIds in GPML2013a) of root element and adds to
	 * elementIdSet. In GPML2013a, elementId (previously named GraphId) is sometimes
	 * missing. In order to assign unique new elementIds, we must first read all
	 * elementIds from the Jdom document to store in elementIdSet.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @param elementIdSet the set of all elementIds.
	 */
	protected void readAllElementIds(PathwayModel pathwayModel, Element root, Set<String> elementIdSet) {
		List<Element> elements = root.getChildren();
		for (Element e : elements) {
			String graphId = e.getAttributeValue("GraphId");
			if (graphId != null)
				elementIdSet.add(graphId);
			if (e.getName() == "Interaction" || e.getName() == "GraphicalLine") {
				Element gfx = e.getChild("Graphics", e.getNamespace());
				// reads GraphId of Points and Anchors two levels down
				for (Element e2 : gfx.getChildren()) {
					String graphId2 = e2.getAttributeValue("GraphId");
					if (graphId2 != null)
						elementIdSet.add(graphId2);
				}
			}
		}
	}

	/**
	 * Reads pathway information from root element. Instantiates and returns the
	 * pathway object {@link Pathway}.
	 * 
	 * @param root the root element.
	 * @return pathway the pathway object.
	 * @throws ConverterException
	 */
	protected Pathway readPathway(Element root) throws ConverterException {
		String title = getAttr("Pathway", "Name", root);
		Element gfx = root.getChild("Graphics", root.getNamespace());
		double boardWidth = Double.parseDouble(getAttr("Pathway.Graphics", "BoardWidth", gfx).trim());
		double boardHeight = Double.parseDouble(getAttr("Pathway.Graphics", "BoardHeight", gfx).trim());
		Coordinate infoBox = readInfoBox(root);
		// instantiates pathway, default backgroundColor is ffffff (white)
		Pathway pathway = new Pathway.PathwayBuilder(title, boardWidth, boardHeight, Color.decode("#ffffff"), infoBox)
				.build();
		// sets optional properties
		String organism = getAttr("Pathway", "Organism", root);
		String source = getAttr("Pathway", "Data-Source", root);
		String version = getAttr("Pathway", "Version", root);
		String license = getAttr("Pathway", "License", root);
		if (organism != null)
			pathway.setOrganism(organism);
		if (source != null)
			pathway.setSource(source);
		if (version != null)
			pathway.setVersion(version);
		if (license != null)
			pathway.setLicense(license);
		// sets optional dynamic properties
		String author = getAttr("Pathway", "Author", root);
		String maintainer = getAttr("Pathway", "Maintainer", root);
		String email = getAttr("Pathway", "Email", root);
		String lastModified = getAttr("Pathway", "Last-Modified", root);
		if (author != null)
			pathway.setDynamicProperty(PATHWAY_AUTHOR, author);
		if (maintainer != null)
			pathway.setDynamicProperty(PATHWAY_MAINTAINER, maintainer);
		if (email != null)
			pathway.setDynamicProperty(PATHWAY_EMAIL, email);
		if (lastModified != null)
			pathway.setDynamicProperty(PATHWAY_LASTMODIFIED, lastModified);
		// reads legend
		readLegend(pathway, root);
		return pathway;
	}

	/**
	 * Reads the infobox x and y coordinate {@link Pathway#setInfoBox} information.
	 * 
	 * @param root the root element.
	 * @return the infoBox as coordinates.
	 */
	protected Coordinate readInfoBox(Element root) {
		Element ifbx = root.getChild("InfoBox", root.getNamespace());
		double centerX = Double.parseDouble(ifbx.getAttributeValue("CenterX").trim());
		double centerY = Double.parseDouble(ifbx.getAttributeValue("CenterY").trim());
		return new Coordinate(centerX, centerY);
	}

	/**
	 * Reads the Legend CenterX and CenterY to pathway dynamic properties
	 * {@link Pathway#setDynamicProperty} .
	 * 
	 * @param pathway the pathway.
	 * @param root    the root element.
	 * @return the infoBox as coordinates.
	 */
	protected void readLegend(Pathway pathway, Element root) {
		Element lgd = root.getChild("Legend", root.getNamespace());
		if (lgd != null) {
			String centerX = lgd.getAttributeValue("CenterX");
			pathway.setDynamicProperty(LEGEND_CENTER_X, centerX);
			String centerY = lgd.getAttributeValue("CenterY");
			pathway.setDynamicProperty(LEGEND_CENTER_Y, centerY);
		}
	}

	/**
	 * Reads gpml:Biopax information openControlledVocabulary and PublicationXref.
	 * {@link #readBiopaxOpenControlledVocabulary} to {@link Annotation}, and
	 * {@link #readBiopaxPublicationXref} to {@link Citation}.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds for this pathway model.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @throws ConverterException
	 */
	protected void readBiopax(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId) throws ConverterException {
		Element bp = root.getChild("Biopax", root.getNamespace());
		if (bp != null) {
			// reads openControlledVocabulary, equivalent to annotation
			readBiopaxOpenControlledVocabulary(pathwayModel, bp, elementIdSet);
			// reads publicationXref, equivalent to citation
			readBiopaxPublicationXref(pathwayModel, bp, elementIdSet, biopaxIdToNew, duplicateToBiopaxId);
		}
	}

	/**
	 * Reads gpml:Biopax bp:OpenControlledVocabulary information to
	 * {@link Annotation} for pathway model from root element. In GPML2013a, all
	 * annotations are by default linked to {@link Pathway} using
	 * {@link AnnotationRef}.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param bp           the biopax element.
	 * @param elementIdSet the set of all elementIds.
	 * @throws ConverterException
	 */
	protected void readBiopaxOpenControlledVocabulary(PathwayModel pathwayModel, Element bp, Set<String> elementIdSet)
			throws ConverterException {
		for (Element ocv : bp.getChildren("openControlledVocabulary", BIOPAX_NAMESPACE)) {
			// generates new unique elementId and adds to elementIdSet
			String elementId = PathwayModel.getUniqueId(elementIdSet);
			elementIdSet.add(elementId);
			Logger.log.trace("Annotation missing elementId, new id is: " + elementId);
			// reads OpenControlledVocabulary
			String value = ocv.getChild("TERM", BIOPAX_NAMESPACE).getText();
			String biopaxOntology = ocv.getChild("Ontology", BIOPAX_NAMESPACE).getText();
			AnnotationType type = null;
			if (OCV_ONTOLOGY_MAP.containsKey(biopaxOntology)) {
				type = AnnotationType.ONTOLOGY;
			} else {
				type = AnnotationType.register(biopaxOntology);
			}
			// instantiates annotation
			Annotation annotation = new Annotation(value, type);
			annotation.setElementId(elementId);
			// sets optional property xref
			String biopaxIdDbStr = ocv.getChild("ID", BIOPAX_NAMESPACE).getText(); // e.g PW:0000650
			String[] biopaxIdDb = biopaxIdDbStr.split(":"); // splits "ID" into Id and Database
			String biopaxDb = biopaxIdDb[0]; // e.g. PW
			String biopaxId = biopaxIdDb[1]; // e.g 0000650
			Xref xref = XrefUtils.createXref(biopaxId, biopaxDb);
			if (xref != null)
				annotation.setXref(xref);
			// adds annotation to pathway model and annotationRef to pathway
			Annotation annotationExisting = pathwayModel.addAnnotation(annotation);
			pathwayModel.getPathway().addAnnotationRef(new AnnotationRef(annotationExisting));
		}
	}

	/**
	 * Reads gpml:Biopax bp:PublicationXref information to {@link Citation} for
	 * pathway model from root element.
	 * 
	 * NB: Because biopax id may conflict with an elementId. A new unique elementId
	 * (value) can be assigned with reference back to the original id (key) in
	 * biopaxIdToNew {@link Map}.
	 * 
	 * Some PublicationXrefs contain duplicate information and are not added to the
	 * pathway model.The {@link Map} duplicateToBiopaxId links the id of the
	 * duplicate citation to the id of the existing citation so that BiopaxRef for
	 * the duplicate citation can find the correct existing citation.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param bp                  the biopax element.
	 * @param elementIdSet        the set of all elementIds for this pathway model.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @throws ConverterException
	 */
	protected void readBiopaxPublicationXref(PathwayModel pathwayModel, Element bp, Set<String> elementIdSet,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId) throws ConverterException {
		for (Element pubxf : bp.getChildren("PublicationXref", BIOPAX_NAMESPACE)) {
			// reads rdf:id of biopax
			String elementId = pubxf.getAttributeValue("id", RDF_NAMESPACE);
			// if not unique, generates new unique elementId
			if (elementIdSet.contains(elementId)) {
				String newId = PathwayModel.getUniqueId(elementIdSet);
				Logger.log.trace("Biopax id " + elementId + " is not unique, new id is: " + newId);
				biopaxIdToNew.put(elementId, newId);
				elementId = newId;
			}
			// elementId is added to elementIdSet
			elementIdSet.add(elementId);
			// reads rest of PublicationXref
			String biopaxId = readPubxfInfo(pubxf.getChildren("ID", BIOPAX_NAMESPACE));
			String biopaxDb = readPubxfInfo(pubxf.getChildren("DB", BIOPAX_NAMESPACE));
			Xref xref = XrefUtils.createXref(biopaxId, biopaxDb);
			// instantiates citation
			Citation citation = new Citation(xref);
			citation.setElementId(elementId);
			// sets optional properties
			String title = readPubxfInfo(pubxf.getChildren("TITLE", BIOPAX_NAMESPACE));
			String source = readPubxfInfo(pubxf.getChildren("SOURCE", BIOPAX_NAMESPACE));
			String year = readPubxfInfo(pubxf.getChildren("YEAR", BIOPAX_NAMESPACE));
			if (title != null && !title.equals(""))
				citation.setTitle(title);
			if (source != null && !source.equals("")) {
				citation.setSource(source);
				// if source is an url, also set as citation url
				if (source.startsWith("http") || source.startsWith("www"))
					citation.setUrl(new UrlRef(source));
			}
			if (year != null && !year.equals(""))
				citation.setYear(year);
			List<String> authors = new ArrayList<String>();
			for (Element au : pubxf.getChildren("AUTHORS", BIOPAX_NAMESPACE)) {
				String author = au.getText();
				if (author != null)
					authors.add(author);
			}
			if (!authors.isEmpty())
				citation.setAuthors(authors);
			// adds citation to pathway model
			if (citation != null) {
				Citation citationExisting = pathwayModel.addCitation(citation);
				// if an existing citation exists, the duplicate citation is not added
				if (citation != citationExisting)
					// adds reference from duplicate citation id to existing citation id
					duplicateToBiopaxId.put(citation.getElementId(), citationExisting.getElementId());
			}
		}
	}

	/**
	 * Reads Biopax PublicationXref information from PublicationXref children
	 * elements. In GPML2013a there are some duplicated and/or empty gpml:Biopax
	 * PublicationXref information. This method reads the list of child elements
	 * with the same name (e.g. "ID") as long as the elementText value is still null
	 * or empty "". Used in {@link #readBiopaxPublicationXref}
	 * 
	 * @param pubxfElements the pubxf children elements with the same local name.
	 * @return elementText the string text value for the element.
	 * @throws ConverterException
	 */
	protected String readPubxfInfo(List<Element> pubxfElements) throws ConverterException {
		String elementText = null;
		for (Element pubxfElement : pubxfElements) {
			if (elementText == null || elementText.equals("")) {
				if (pubxfElement != null)
					elementText = pubxfElement.getText();
			} else {
				continue;
			}
		}
		return elementText;
	}

	/**
	 * Reads comment group (comment, biopaxref/citationRef, dynamic property) and
	 * evidencRef information {@link Pathway} for pathway model from root element.
	 * 
	 * NB: In GPML2013a, no equivalent of annotationRef was implemented, all
	 * openControlledVocabulary belong to the parent pathway. Also,
	 * CommentGroup:PublicationXref and BiopaxRef the attribute were not
	 * implemented.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @throws ConverterException
	 */
	protected void readPathwayInfo(PathwayModel pathwayModel, Element root, Map<String, String> biopaxIdToNew,
			Map<String, String> duplicateToBiopaxId) throws ConverterException {
		readPathwayComments(pathwayModel, root);
		readPathwayBiopaxRefs(pathwayModel, root, biopaxIdToNew, duplicateToBiopaxId); // equivalent to citationRefs
		readPathwayDynamicProperties(pathwayModel, root);
	}

	/**
	 * Reads comment {@link Comment} information for pathway from root element. If
	 * source is WikiPathways-description, set comment as pathway description
	 * instead!
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayComments(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element cmt : root.getChildren("Comment", root.getNamespace())) {
			String source = getAttr("Comment", "Source", cmt);
			String commentText = cmt.getText();
			// if source is WikiPathways-description, set as pathway description
			if (Objects.equals(WP_DESCRIPTION, source)) {
				pathwayModel.getPathway().setDescription(commentText);
				continue;
			}
			// comment must have text
			if (commentText != null && !commentText.equals("")) {
				Comment comment = new Comment(commentText);
				if (source != null && !source.equals(""))
					comment.setSource(source);
				pathwayModel.getPathway().addComment(new Comment(source, commentText));
			}
		}
	}

	/**
	 * Reads biopax reference information {@link Pathway#addCitationRef} for pathway
	 * model from root element. BiopaxRef is equivalent to citationRef.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @throws ConverterException
	 */
	protected void readPathwayBiopaxRefs(PathwayModel pathwayModel, Element root, Map<String, String> biopaxIdToNew,
			Map<String, String> duplicateToBiopaxId) throws ConverterException {
		for (Element bpRef : root.getChildren("BiopaxRef", root.getNamespace())) {
			String biopaxRef = bpRef.getText();
			// if biopaxIdToNew contains biopaxRef, sets biopaxRef to its new elementId
			if (biopaxIdToNew.containsKey(biopaxRef))
				biopaxRef = biopaxIdToNew.get(biopaxRef);
			// if duplicateToBiopaxId contains biopaxRef, sets biopaxRef to elementId of
			// existing biopax
			if (duplicateToBiopaxId.containsKey(biopaxRef))
				biopaxRef = duplicateToBiopaxId.get(biopaxRef);
			// returns citation referenced by biopaxRef
			Citation citation = (Citation) pathwayModel.getPathwayElement(biopaxRef);
			// if citation is valid, create citationRef and add to pathway model
			if (citation != null) {
				CitationRef citationRef = new CitationRef(citation);
				if (citationRef != null)
					pathwayModel.getPathway().addCitationRef(citationRef);
			} else {
				Logger.log.trace("Warning: biopaxRef " + biopaxRef
						+ " refers to invalid Biopax PublicationXref, biopaxRef is not created.");
			}
		}
	}

	/**
	 * Reads gpml:Attribute or dynamic property {@link Pathway#setDynamicProperty}
	 * information for pathway from root element.
	 * 
	 * NB: Property (dynamic property) was named Attribute in GPML2013a.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayDynamicProperties(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element dp : root.getChildren("Attribute", root.getNamespace())) {
			String key = getAttr("Attribute", "Key", dp);
			String value = getAttr("Attribute", "Value", dp);
			pathwayModel.getPathway().setDynamicProperty(key, value);
		}
	}

	/**
	 * Reads group {@link Group} information for pathway model from root element.
	 * Rect properties (centerX, centerY, width, height) are calculated by
	 * {@link calculateGroupRectProperty} after group {@link List} is filled with
	 * pathway elements.
	 * 
	 * NB: A group has identifier GroupId (essentially ElementId), while GraphId is
	 * optional. A group has GraphId if there is at least one {@link LinePoint}
	 * referring to this group by GraphRef. Because GroupIds may conflict with an
	 * elementId, new unique elementIds (value) can be assigned with reference back
	 * to the original GroupIds (key) in groupIdToNew map.
	 * 
	 * NB: Group type "None" is deprecated in GPML2021. Group type "None" is
	 * replaced with "Group".
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @param groupIdToNew        the map of groupId to new unique elementIds.
	 * @throws ConverterException
	 */
	protected void readGroups(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId,
			Map<String, String> groupIdToNew) throws ConverterException {
		for (Element grp : root.getChildren("Group", root.getNamespace())) {
			// reads group GroupId, equivalent to elementId
			String elementId = getAttr("Group", "GroupId", grp);
			// if not unique, generates new unique elementId
			if (elementIdSet.contains(elementId)) {
				String newId = PathwayModel.getUniqueId(elementIdSet);
				Logger.log.trace("GroupId " + elementId + " is not unique, new id is: " + newId);
				groupIdToNew.put(elementId, newId);
				elementId = newId;
			}
			// adds group elementId to elementIdSet
			elementIdSet.add(elementId);
			String typeStr = getAttr("Group", "Style", grp);
			// in GPML2021, "None" group type is replaced with "Group"
			if (typeStr.equals("None"))
				typeStr = "Group";
			GroupType type = GroupType.register(typeStr);
			// temp rect properties (calculate rect properties after pathway elements added)
			RectProperty rectProperty = new RectProperty(new Coordinate(0, 0), 1, 1);
			FontProperty fontProperty = new FontProperty(Color.decode("#808080"), "Arial", false, false, false, false,
					12, HAlignType.CENTER, VAlignType.MIDDLE);
			ShapeStyleProperty shapeStyleProperty = readGroupShapeStyleProperty(type);
			// instantiates group
			Group group = new Group(rectProperty, fontProperty, shapeStyleProperty, type);
			group.setElementId(elementId);
			// type "Pathway" has font size (custom font name "Times" never implemented)
			if (type == GroupType.PATHWAY)
				group.getFontProperty().setFontSize(32);
			// reads comments, biopaxRefs/citationRefs, dynamic properties
			readElementInfo(pathwayModel, group, grp, biopaxIdToNew, duplicateToBiopaxId);
			// sets optional properties
			String textLabel = getAttr("Group", "TextLabel", grp);
			String graphId = getAttr("Group", "GraphId", grp);
			if (textLabel != null)
				group.setTextLabel(textLabel);
			if (graphId != null)
				group.setDynamicProperty(GROUP_GRAPHID, graphId);
			// add group to pathway model
			pathwayModel.addGroup(group);
		}

	}

	/**
	 * Reads groupRef property {@link Group#setGroupRef} information of group
	 * pathway element. Because a group may refer to another group not yet
	 * initialized. We read and set groupRef after reading all group elements. The
	 * groupRef is the "parent" group to which this group refers.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @param groupIdToNew the map of groupId to new unique elementIds.
	 * @throws ConverterException
	 */
	protected void readGroupGroupRef(PathwayModel pathwayModel, Element root, Map<String, String> groupIdToNew)
			throws ConverterException {
		for (Element grp : root.getChildren("Group", root.getNamespace())) {
			String groupRefStr = getAttr("Group", "GroupRef", grp);
			if (groupRefStr != null && !groupRefStr.equals("")) {
				// if groupIdToNew contains groupRefStr, sets groupRefStr to its new elementId
				if (groupIdToNew.containsKey(groupRefStr))
					groupRefStr = groupIdToNew.get(groupRefStr);
				// given the correct groupRefStr/elementId, retrieves groupRef
				Group groupRef = (Group) pathwayModel.getPathwayElement(groupRefStr);
				// read this group elementId
				String elementId = getAttr("Group", "GroupId", grp);
				// if groupIdToNew contains elementId, sets elementId to its new elementId
				if (groupIdToNew.containsKey(elementId))
					elementId = groupIdToNew.get(elementId);
				// given the correct elementId, retrieves this group
				Group group = (Group) pathwayModel.getPathwayElement(elementId);
				// sets groupRef for this group
				if (group != null && groupRef != null)
					group.setGroupRefTo(groupRef);
			}
		}
	}

	/**
	 * Reads default shape style property for type of group. In 2013a, group
	 * graphics was hard coded for each group type in GroupPainterRegistry.java.
	 * {@link Group#setShapeStyleProperty}. Hover fillColor implemented in view.
	 * 
	 * @param type the group type.
	 * @returns the shapeStyleProperty object.
	 * @throws ConverterException
	 */
	protected ShapeStyleProperty readGroupShapeStyleProperty(GroupType type) throws ConverterException {
		if (type.getName() == "Group") {
			// fillColor translucent blue, hovers to transparent
			return new ShapeStyleProperty(Color.decode("#808080"), LineStyleType.DASHED, 1.0,
					ColorUtils.hexToColor("#0000ff0c"), ShapeType.RECTANGLE);
		} else if (type.getName() == "Complex") {
			// fillColor translucent yellowish-gray, hovers to translucent red #ff00000c
			return new ShapeStyleProperty(Color.decode("#808080"), LineStyleType.SOLID, 1.0,
					ColorUtils.hexToColor("#b4b46419"), ShapeType.OCTAGON);
		} else if (type.getName() == "Pathway") {
			// fontSize 32, fontName "Times" (was not implemented)
			// fillColor translucent green, hovers to more opaque green #00ff0019
			return new ShapeStyleProperty(Color.decode("#808080"), LineStyleType.SOLID, 1.0,
					ColorUtils.hexToColor("#00ff000c"), ShapeType.RECTANGLE);
		} else {
			// GroupType "None", or default
			// fillColor translucent yellowish-gray, hovers to translucent red #ff00000c
			return new ShapeStyleProperty(Color.decode("#808080"), LineStyleType.DASHED, 1.0,
					ColorUtils.hexToColor("#b4b46419"), ShapeType.RECTANGLE);
		}
	}

	/**
	 * Reads label {@link Label} information for pathway model from root element.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @param groupIdToNew        the map of groupId to new unique elementIds.
	 * @throws ConverterException
	 */
	protected void readLabels(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId,
			Map<String, String> groupIdToNew) throws ConverterException {
		for (Element lb : root.getChildren("Label", root.getNamespace())) {
			String elementId = readElementId("Label", lb, elementIdSet);
			String textLabel = getAttr("Label", "TextLabel", lb);
			Element gfx = lb.getChild("Graphics", lb.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			// instantiates label
			Label label = new Label(rectProperty, fontProperty, shapeStyleProperty, textLabel);
			label.setElementId(elementId);
			// reads comments, biopaxRefs/citationRefs, dynamic properties
			readShapedElement(pathwayModel, label, lb, biopaxIdToNew, duplicateToBiopaxId);
			// sets optional properties
			String href = getAttr("Label", "Href", lb);
			readGroupRef(pathwayModel, label, lb, groupIdToNew);
			if (href != null)
				label.setHref(href);
			// adds label to pathway model
			pathwayModel.addLabel(label);
		}
	}

	/**
	 * Reads shape {@link Shape} information for pathway model from root element.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @param groupIdToNew        the map of groupId to new unique elementIds.
	 * @throws ConverterException
	 */
	protected void readShapes(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId,
			Map<String, String> groupIdToNew) throws ConverterException {
		for (Element shp : root.getChildren("Shape", root.getNamespace())) {
			String elementId = readElementId("Shape", shp, elementIdSet);
			Element gfx = shp.getChild("Graphics", shp.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			double rotation = Double.parseDouble(getAttr("Shape.Graphics", "Rotation", gfx).trim());
			// instantiates shape
			Shape shape = new Shape(rectProperty, fontProperty, shapeStyleProperty, rotation);
			shape.setElementId(elementId);
			// reads comments, biopaxRefs/citationRefs, dynamic properties
			readShapedElement(pathwayModel, shape, shp, biopaxIdToNew, duplicateToBiopaxId);
			// sets optional properties
			String textLabel = getAttr("Shape", "TextLabel", shp);
			readGroupRef(pathwayModel, shape, shp, groupIdToNew);
			if (textLabel != null)
				shape.setTextLabel(textLabel);
			// adds shape to pathway model
			pathwayModel.addShape(shape);
		}
	}

	/**
	 * Reads data node {@link DataNode} information for pathway model from root
	 * element.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @param groupIdToNew        the map of groupId to new unique elementIds.
	 * @throws ConverterException
	 */
	protected void readDataNodes(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId,
			Map<String, String> groupIdToNew) throws ConverterException {
		for (Element dn : root.getChildren("DataNode", root.getNamespace())) {
			String elementId = readElementId("DataNode", dn, elementIdSet);
			Element gfx = dn.getChild("Graphics", dn.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			String textLabel = getAttr("DataNode", "TextLabel", dn);
			String typeStr = getAttr("DataNode", "Type", dn);
			// in GPML2021, "Unknown" data node type is named "Undefined"
			if (typeStr.equals("Unknown"))
				typeStr = "Undefined";
			DataNodeType type = DataNodeType.register(typeStr);
			// instantiates data node
			DataNode dataNode = new DataNode(rectProperty, fontProperty, shapeStyleProperty, textLabel, type);
			dataNode.setElementId(elementId);
			// reads comments, biopaxRefs/citationRefs, dynamic properties
			readShapedElement(pathwayModel, dataNode, dn, biopaxIdToNew, duplicateToBiopaxId);
			// sets optional properties
			readGroupRef(pathwayModel, dataNode, dn, groupIdToNew);
			Xref xref = readXref(dn);
			if (xref != null)
				dataNode.setXref(xref);
			// adds data node to pathway model
			pathwayModel.addDataNode(dataNode);
		}
	}

	/**
	 * Reads state {@link State} information for pathway model from root element.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @throws ConverterException
	 */
	protected void readStates(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId) throws ConverterException {
		for (Element st : root.getChildren("State", root.getNamespace())) {
			String elementId = readElementId("State", st, elementIdSet);
			String textLabel = getAttr("State", "TextLabel", st);
			String typeStr = getAttr("State", "StateType", st);
			// in GPML2021, "Unknown" state type is named "Undefined"
			if (typeStr.equalsIgnoreCase("Unknown"))
				typeStr = "Undefined";
			StateType type = StateType.register(typeStr);
			Element gfx = st.getChild("Graphics", st.getNamespace());
			double relX = Double.parseDouble(getAttr("State.Graphics", "RelX", gfx).trim());
			double relY = Double.parseDouble(getAttr("State.Graphics", "RelY", gfx).trim());
			double width = Double.parseDouble(getAttr("State.Graphics", "Width", gfx).trim());
			double height = Double.parseDouble(getAttr("State.Graphics", "Height", gfx).trim());
			// state does not have font properties in GPML2013a, set default values
			FontProperty fontProperty = new FontProperty(Color.decode("#000000"), "Arial", false, false, false, false,
					12, HAlignType.CENTER, VAlignType.MIDDLE);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			// finds parent datanode from state elementRef
			String elementRef = getAttr("State", "GraphRef", st);
			DataNode dataNode = (DataNode) pathwayModel.getPathwayElement(elementRef);
			// sets zOrder based on parent data node TODO
			shapeStyleProperty.setZOrder(dataNode.getShapeStyleProperty().getZOrder() + 1);
			// instantiates state
			State state = new State(textLabel, type, relX, relY, width, height, fontProperty, shapeStyleProperty);
			state.setElementId(elementId);
			// sets textColor to same color as borderColor
			state.getFontProperty().setTextColor(state.getShapeStyleProperty().getBorderColor());
			// reads comments, biopaxRefs/citationRefs, dynamic properties
			readElementInfo(pathwayModel, state, st, biopaxIdToNew, duplicateToBiopaxId);
			// convert comments to Xref and AnnotationRef if applicable
			convertStateCommentToRefs(pathwayModel, state, elementIdSet);
			readStateDynamicProperties(state, st);
			// if has DoubleLineProperty key, sets border style as Double
			if ("Double".equalsIgnoreCase(state.getDynamicProperty("org.pathvisio.DoubleLineProperty"))) {
				state.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
			}
			// sets optional properties
			Xref xref = readXref(st);
			if (xref != null)
				state.setXref(xref);
			// adds state to parent data node of pathway model
			dataNode.addState(state);
		}
	}

	/**
	 * This method is used to handle {@link State} {@link Comment} which were
	 * manually added to encode phosphosite information. This method first checks
	 * whether a comment for a state is indeed phosphosite information. The
	 * phosphosite information is parsed and stored in a {@link LinkedHashMap}
	 * annotationsMap.
	 * 
	 * For each key value pair in annotationsMap, a new {@link Xref} or
	 * {@link Annotation} is created. The annotation is added to the pathway model
	 * after checking for annotations with equivalent properties. A new
	 * {@link AnnotationRef} is created for the annotation and added to the state.
	 * Lastly the comment itself is removed from the state so that information is
	 * not duplicated.
	 * 
	 * NB: "sitegrpid", "ptm" and "direction" are specially handled.
	 * 
	 * @param state
	 * @param elementIdSet
	 * @throws ConverterException
	 */
	protected void convertStateCommentToRefs(PathwayModel pathwayModel, State state, Set<String> elementIdSet)
			throws ConverterException {
		// for each comment
		List<Comment> commentsToRemove = new ArrayList<Comment>();
		for (Comment comment : state.getComments()) {
			// isAnnt true if at least one annotation type present after parsing
			boolean isAnnotation = false;
			String commentText = comment.getCommentText();
			Map<String, String> annotationsMap = new LinkedHashMap<String, String>();
			// parse information to map if state commentText contains "=" or ";"
			if (commentText.contains("=") || commentText.contains(";")) {
				String[] annotations = commentText.trim().split(";");
				for (String annotation : annotations) {
					String[] parts = annotation.trim().split("=");
					String type = parts[0];
					// replace parent with parentid for consistency
					if (type.equals("parent"))
						type = "parentid";
					if (STATE_REF_LIST.contains(type))// type
						isAnnotation = true;
					annotationsMap.put(type, parts[1]); // type and value
				}
			}
			if (isAnnotation) {
				for (String key : annotationsMap.keySet()) {
					Xref xref = null;
					String value = annotationsMap.get(key);
					// if parentid, set xref
					if (key.equalsIgnoreCase(PARENTID))
						xref = XrefUtils.createXref(value, PARENTID_DB);
					// if parentsymbol, set xref
					if (key.equalsIgnoreCase(PARENTSYMBOL))
						xref = XrefUtils.createXref(value, PARENTSYMBOL_DB);
					// if sitegrpid, set xref and add to state
					if (key.equalsIgnoreCase(SITEGRPID)) {
						String identifier = annotationsMap.get(SITEGRPID);
						xref = XrefUtils.createXref(identifier, SITEGRPID_DB);
						if (xref != null)
							state.setXref(xref);
						continue;
					}
					// if ptm, add as SBO annotation
					if (key.equalsIgnoreCase(PTM)) {
						key = "Ontology";
						if (STATE_PTM_MAP.containsKey(value)) { // e.g. "p"
							List<String> ptmInfo = STATE_PTM_MAP.get(value);
							value = ptmInfo.get(0); // e.g. "Phosphorylation"
							xref = XrefUtils.createXref(ptmInfo.get(1), ptmInfo.get(2));
						}
					}
					// if direction, add as GO term annotation
					if (key.equalsIgnoreCase(DIRECTION)) {
						key = "Ontology";
						if (STATE_DIRECTION_MAP.containsKey(value)) { // e.g. "u"
							List<String> dirInfo = STATE_DIRECTION_MAP.get(value);
							value = dirInfo.get(0); // e.g. "positive regulation..."
							xref = XrefUtils.createXref(dirInfo.get(1), dirInfo.get(2));
						}
					}
					// generate elementId for new annotation
					String elementId = PathwayModel.getUniqueId(elementIdSet);
					elementIdSet.add(elementId);
					AnnotationType type = AnnotationType.register(key);
					Annotation annotation = new Annotation(value, type);
					annotation.setElementId(elementId);
					if (xref != null)
						annotation.setXref(xref);
					// returns existing annotation in pathway model if applicable
					annotation = pathwayModel.addAnnotation(annotation);
					AnnotationRef annotationRef = new AnnotationRef(annotation);
					state.addAnnotationRef(annotationRef);
				}
				// add comment to list to be removed after creating annotation and annotationRef
				commentsToRemove.add(comment);
				Logger.log.trace("State " + state.getElementId() + " comment converted to Annotations/AnnotationRefs");
			}
		}
		// remove state comments which were converted into xref or annotationRefs
		for (Comment comment : commentsToRemove) {
			state.removeComment(comment);
		}
	}

	/**
	 * Reads interaction {@link Interaction} information for pathway model from root
	 * element. New unique elementIds are assigned to line pathway elements are
	 * stored in the ordered lineList so that line pathway elements can be retrieved
	 * based on previous read order.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @param groupIdToNew        the map of groupId to new unique elementIds.
	 * @param lineList            the ordered list of line elementIds.
	 * @throws ConverterException
	 */
	protected void readInteractions(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId,
			Map<String, String> groupIdToNew, List<String> lineList) throws ConverterException {
		for (Element ia : root.getChildren("Interaction", root.getNamespace())) {
			String elementId = readElementId("Interaction", ia, elementIdSet);
			// adds elementId to lineList
			lineList.add(elementId);
			Element gfx = ia.getChild("Graphics", ia.getNamespace());
			LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
			// instantiates interaction
			Interaction interaction = new Interaction(lineStyleProperty);
			interaction.setElementId(elementId);
			// reads comments, biopaxRefs/citationRefs, dynamic properties
			// graphics, anchors, groupRef
			readLineElement(pathwayModel, interaction, ia, elementIdSet, biopaxIdToNew, duplicateToBiopaxId,
					groupIdToNew);
			// sets optional properties
			Xref xref = readXref(ia);
			if (xref != null)
				interaction.setXref(xref);
			// adds interaction to pathway model
			pathwayModel.addInteraction(interaction);
		}
	}

	/**
	 * Reads graphical line {@link GraphicalLine} information for pathway model from
	 * root element. New unique elementIds are assigned to line pathway elements are
	 * stored in the ordered lineList so that line pathway elements can be retrieved
	 * based on previous read order.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @param groupIdToNew        the map of groupId to new unique elementIds.
	 * @param lineList            the ordered list of line elementIds.
	 * @throws ConverterException
	 */
	protected void readGraphicalLines(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId,
			Map<String, String> groupIdToNew, List<String> lineList) throws ConverterException {
		for (Element gln : root.getChildren("GraphicalLine", root.getNamespace())) {
			String elementId = readElementId("GraphicalLine", gln, elementIdSet);
			// adds elementId to lineList
			lineList.add(elementId);
			Element gfx = gln.getChild("Graphics", gln.getNamespace());
			LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
			// instantiates graphical line
			GraphicalLine graphicalLine = new GraphicalLine(lineStyleProperty);
			graphicalLine.setElementId(elementId);
			// reads comments, biopaxRefs/citationRefs, dynamic properties
			// graphics, anchors, groupRef
			readLineElement(pathwayModel, graphicalLine, gln, elementIdSet, biopaxIdToNew, duplicateToBiopaxId,
					groupIdToNew);
			// adds graphical line to pathway model
			pathwayModel.addGraphicalLine(graphicalLine);
		}
	}

	/**
	 * Reads line element {@link LineElement} information for interaction or
	 * graphical line from jdom element.
	 * 
	 * NB: points are read in {@link #readPoints}
	 * 
	 * @param lineElement         the line pathway element.
	 * @param ln                  the jdom line pathway element element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @param groupIdToNew        the map of groupId to new unique elementIds.
	 * @throws ConverterException
	 */
	protected void readLineElement(PathwayModel pathwayModel, LineElement lineElement, Element ln,
			Set<String> elementIdSet, Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId,
			Map<String, String> groupIdToNew) throws ConverterException {
		String base = ln.getName();
		// reads comments, biopaxRefs/citationRefs, dynamic properties
		readElementInfo(pathwayModel, lineElement, ln, biopaxIdToNew, duplicateToBiopaxId);
		readLineDynamicProperties(lineElement, ln);
		Element gfx = ln.getChild("Graphics", ln.getNamespace());
		// reads anchors
		readAnchors(pathwayModel, lineElement, gfx, elementIdSet);
		// sets optional property groupRef
		String groupRefStr = getAttr(base, "GroupRef", ln);
		if (groupRefStr != null && !groupRefStr.equals("")) {
			// if groupIdToNew contains groupRef, sets groupRef to its new elementId
			if (groupIdToNew.containsKey(groupRefStr))
				groupRefStr = groupIdToNew.get(groupRefStr);
			Group groupRef = (Group) pathwayModel.getPathwayElement(groupRefStr);
			// sets groupRef for this line pathway element
			lineElement.setGroupRefTo(groupRef);
		}
	}

	/**
	 * Reads anchor {@link Anchor} information for line element from element.
	 * 
	 * @param lineElement  the line element object.
	 * @param gfx          the jdom graphics element.
	 * @param elementIdSet the set of all elementIds.
	 * @throws ConverterException
	 */
	protected void readAnchors(PathwayModel pathwayModel, LineElement lineElement, Element gfx,
			Set<String> elementIdSet) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		for (Element an : gfx.getChildren("Anchor", gfx.getNamespace())) {
			String elementId = readElementId(base + ".Graphics.Anchor", an, elementIdSet);
			double position = Double.parseDouble(getAttr(base + ".Graphics.Anchor", "Position", an).trim());
			AnchorShapeType shapeType = AnchorShapeType.register(getAttr(base + ".Graphics.Anchor", "Shape", an));
			Anchor anchor = new Anchor(position, shapeType);
			anchor.setElementId(elementId);
			// adds anchor to line pathway element and pathway model
			lineElement.addAnchor(anchor);
		}
	}

	/**
	 * Reads points {@link LinePoint} for pathway model line pathway elements.
	 * Points must be read after the pathway elements they refer to. Therefore
	 * points are read last in {@link #readFromRoot}.
	 * 
	 * NB: points refer to a group by its GraphId not GroupId(essentially
	 * elementId). If the pathway element referenced by a point is a group, we must
	 * search for the group by its GraphId {@link Group#getDynamicProperty}, instead
	 * of using {@link PathwayModel#getPathwayElement()} which retrieves groups by
	 * their elementId.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @param elementIdSet the set of all elementIds.
	 * @param groupIdToNew the map of groupId to new unique elementIds.
	 * @param lineList     the ordered list of line elementIds.
	 * @throws ConverterException
	 */
	protected void readPoints(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, String> groupIdToNew, List<String> lineList) throws ConverterException {
		// reads points for interactions, and then graphical lines
		List<String> lnElementName = Collections.unmodifiableList(Arrays.asList("Interaction", "GraphicalLine"));
		// initiates lineList index
		int lineListIndex = 0;
		for (int i = 0; i < lnElementName.size(); i++) {
			for (Element ln : root.getChildren(lnElementName.get(i), root.getNamespace())) {
				String base = ln.getName();
				// retrieve line pathway element by its graphId
				String lineElementId = getAttr(lnElementName.get(i), "GraphId", ln);
				LineElement lineElement = (LineElement) pathwayModel.getPathwayElement(lineElementId);
				// if line graphId null, retrieve line object by its order stored to lineList
				if (lineElementId == null && lineElement == null)
					lineElement = (LineElement) pathwayModel.getPathwayElement(lineList.get(lineListIndex));
				Element gfx = ln.getChild("Graphics", ln.getNamespace());
				for (Element pt : gfx.getChildren("Point", gfx.getNamespace())) {
					String elementId = readElementId(base + ".Graphics.Point", pt, elementIdSet);
					String arrowHeadStr = getAttr(base + ".Graphics.Point", "ArrowHead", pt);
					ArrowHeadType arrowHead = getInteractionPanelType(arrowHeadStr);
					if (arrowHead == null)
						arrowHead = ArrowHeadType.register(arrowHeadStr);
					// TODO Sub types added to Annotations while converting...
					Coordinate xy = new Coordinate(
							Double.parseDouble(getAttr(base + ".Graphics.Point", "X", pt).trim()),
							Double.parseDouble(getAttr(base + ".Graphics.Point", "Y", pt).trim()));
					// instantiates point
					LinePoint point = new LinePoint(arrowHead, xy);
					point.setElementId(elementId);
					// adds point to line pathway element
					lineElement.addPoint(point); // TODO
					// sets optional parameters including elementRef (GraphRef in GPML2013a)
					String elementRefStr = getAttr(base + ".Graphics.Point", "GraphRef", pt);
					if (elementRefStr != null && !elementRefStr.equals("")) {
						// retrieves referenced pathway element (aside from group) by elementId
						PathwayElement elementRef = pathwayModel.getPathwayElement(elementRefStr);
						// if elementRef refers to a group, it cannot be found by elementId
						if (elementRef == null) {
							// finds group by its graphId stored in dynamic properties
							for (Group group : pathwayModel.getGroups()) {
								if (elementRefStr.equals(group.getDynamicProperty(GROUP_GRAPHID)))
									elementRef = group;
							}
						}
						// sets elementRef, relX, and relY for this point
						if (elementRef != null) {
							point.setElementRef(elementRef);
							point.setRelX(Double.parseDouble(pt.getAttributeValue("RelX").trim()));
							point.setRelY(Double.parseDouble(pt.getAttributeValue("RelY").trim()));
						}
					}
				}
				// checks line pathway element has at least 2 points
				if (lineElement.getPoints().size() < 2)
					throw new ConverterException(lnElementName.get(i) + lineElement.getElementId() + " has "
							+ lineElement.getPoints().size() + " point(s),  must have at least 2.");
				lineListIndex += 1;
			}
		}
	}

	/**
	 * Reads xref {@link Xref} information from element. In GPML2013a, Xref is
	 * required for DataNodes, Interactions, and optional for States.
	 * 
	 * @param e the element.
	 * @return xref the new xref or null if no or invalid xref information.
	 * @throws ConverterException
	 */
	protected Xref readXref(Element e) throws ConverterException {
		Element xref = e.getChild("Xref", e.getNamespace());
		if (xref != null) {
			String base = e.getName();
			String identifier = getAttr(base + ".Xref", "ID", xref);
			String dataSource = getAttr(base + ".Xref", "Database", xref);
			return XrefUtils.createXref(identifier, dataSource);
		}
		return null;
	}

	/**
	 * Returns elementId read for given Element. If elementId is missing (null),
	 * generates and returns a new unique elementId. New unique elementIds are added
	 * to elementIdSet.
	 * 
	 * @param tag          the string tag for
	 *                     {@link GPML2013aFormatAbstract#getAttr}
	 * @param e            the jdom element.
	 * @param elementIdSet the set of all elementIds.
	 * @return elementId the elementId for given element.
	 * @throws ConverterException
	 */
	protected String readElementId(String tag, Element e, Set<String> elementIdSet) throws ConverterException {
		String elementId = getAttr(tag, "GraphId", e);
		// if elementId null, generates new unique elementId and adds to elementIdSet
		if (elementId == null) {
			elementId = PathwayModel.getUniqueId(elementIdSet);
			elementIdSet.add(elementId);
			Logger.log.trace(e.getName() + " missing elementId, new id is: " + elementId);
		}
		return elementId;
	}

	/**
	 * Reads and sets groupRef for given (shaped) pathway element and jdom Element.
	 * 
	 * @param pathwayModel  the pathwayModel.
	 * @param shapedElement the (shaped) pathway element.
	 * @param se            the jdom (shaped) pathway element element.
	 * @param groupIdToNew  the map of groupId to new unique elementIds.
	 * @throws ConverterException
	 */
	protected void readGroupRef(PathwayModel pathwayModel, ShapedElement shapedElement, Element se,
			Map<String, String> groupIdToNew) throws ConverterException {
		String groupRefStr = getAttr(se.getName(), "GroupRef", se);
		if (groupRefStr != null && !groupRefStr.equals("")) {
			// if groupIdToNew contains groupRef, sets groupRef to its new elementId
			if (groupIdToNew.containsKey(groupRefStr))
				groupRefStr = groupIdToNew.get(groupRefStr);
			Group groupRef = (Group) pathwayModel.getPathwayElement(groupRefStr);
			// sets groupRef for this shaped pathway element
			if (groupRef != null) {
				shapedElement.setGroupRefTo(groupRef);
			}
		}
	}

	/**
	 * Reads shaped pathway element {@link ShapedElement} information: comments,
	 * biopaxRefs (equivalent to citationRefs), and dynamic properties.
	 * 
	 * @param shapedElement       the shaped pathway element.
	 * @param se                  the jdom (shaped) pathway element element.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @throws ConverterException
	 */
	protected void readShapedElement(PathwayModel pathwayModel, ShapedElement shapedElement, Element se,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId) throws ConverterException {
		readElementInfo(pathwayModel, shapedElement, se, biopaxIdToNew, duplicateToBiopaxId); // reads comments,
		// biopaxRefs/citationRefs
		readShapedDynamicProperties(shapedElement, se); // reads dynamic properties
	}

	/**
	 * Reads comment and biopaxref {@link ElementInfo} information, for pathway
	 * element from element. Dynamic properties read by
	 * {@link #readLineDynamicProperties} , {@link #readShapedDynamicProperties} ,
	 * {@link #readStateDynamicProperties}.
	 * 
	 * NB: In GPML2013a, no equivalent of annotationRef was implemented, all
	 * openControlledVocabulary belong to the parent pathway. Also,
	 * CommentGroup:PublicationXref and BiopaxRef the attribute were not
	 * implemented. In GPML2021, Evidence and EvidenceRef was added.
	 * 
	 * @param elementInfo         the element info pathway element object.
	 * @param e                   the jdom pathway element element.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @throws ConverterException
	 */
	protected void readElementInfo(PathwayModel pathwayModel, ElementInfo elementInfo, Element e,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId) throws ConverterException {
		readComments(elementInfo, e);
		readBiopaxRefs(pathwayModel, elementInfo, e, biopaxIdToNew, duplicateToBiopaxId);
	}

	/**
	 * Reads comment {@link Comment} information for pathway element from element.
	 * 
	 * @param elementInfo the element info pathway element object.
	 * @param e           the jdom pathway element element.
	 * @throws ConverterException
	 */
	protected void readComments(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element cmt : e.getChildren("Comment", e.getNamespace())) {
			String source = getAttr("Comment", "Source", cmt);
			String commentText = cmt.getText();
			// comment must have text
			if (commentText != null && !commentText.equals("")) {
				// instantiates comment
				Comment comment = new Comment(commentText);
				// sets source
				if (source != null && !source.equals(""))
					comment.setSource(source);
				// adds comment to pathway element
				elementInfo.addComment(new Comment(source, commentText));
			}
		}
	}

	/**
	 * Reads BiopaxRef information to {@link ElementInfo#addCitationRef} information
	 * for pathway element from element. NB: BiopaxRef is also an attribute in the
	 * GPML2013a schema but was never implemented.
	 * 
	 * @param elementInfo         the element info pathway element object.
	 * @param e                   the jdom pathway element element.
	 * @param biopaxIdToNew       the map of biopaxId to new unique elementIds.
	 * @param duplicateToBiopaxId the map of duplicate biopaxId to existing
	 *                            biopax/citation id.
	 * @throws ConverterException
	 */
	protected void readBiopaxRefs(PathwayModel pathwayModel, ElementInfo elementInfo, Element e,
			Map<String, String> biopaxIdToNew, Map<String, String> duplicateToBiopaxId) throws ConverterException {
		for (Element bpRef : e.getChildren("BiopaxRef", e.getNamespace())) {
			String biopaxRef = bpRef.getText();
			// if biopaxIdToNew contains biopaxRef, sets biopaxRef to its new elementId
			if (biopaxIdToNew.containsKey(biopaxRef))
				biopaxRef = biopaxIdToNew.get(biopaxRef);
			// if duplicateToBiopaxId contains biopaxRef, set biopaxRef to elementId of
			// existing biopax
			if (duplicateToBiopaxId.containsKey(biopaxRef))
				biopaxRef = duplicateToBiopaxId.get(biopaxRef);
			// given the correct biopaxRef/elementId, retrieves citation referenced
			Citation citation = (Citation) pathwayModel.getPathwayElement(biopaxRef);
			// if citation valid, create citationRef and add to pathway model.
			if (citation != null) {
				CitationRef citationRef = new CitationRef(citation);
				elementInfo.addCitationRef(citationRef);
			} else {
				Logger.log.trace("Warning: biopaxRef " + biopaxRef
						+ " refers to invalid Biopax PublicationXref, biopaxRef is not created.");
			}
		}
	}

	/**
	 * Reads dynamic property {@link ElementInfo#setDynamicProperty} information for
	 * shaped pathway elements. If dynamic property codes for DoubleLineProperty or
	 * CellularComponentProperty, updates/overrides borderStyle or shapeType.
	 * Otherwise, sets dynamic property.
	 * 
	 * NB: Property (dynamic property) was named Attribute in GPML2013a.
	 * 
	 * @param shapedElement the shaped pathway element.
	 * @param se            the jdom shaped pathway element element.
	 * @throws ConverterException
	 */
	protected void readShapedDynamicProperties(ShapedElement shapedElement, Element se) throws ConverterException {
		for (Element dp : se.getChildren("Attribute", se.getNamespace())) {
			String key = getAttr("Attribute", "Key", dp);
			String value = getAttr("Attribute", "Value", dp);
			if (key.equals(DOUBLE_LINE_KEY) && value.equalsIgnoreCase("Double")) {
				shapedElement.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
			} else if (key.equals(CELL_CMPNT_KEY)) {
				value = toCamelCase(value);
				ShapeType type = ShapeType.register(value);
				shapedElement.getShapeStyleProperty().setShapeType(type);
			} else {
				shapedElement.setDynamicProperty(key, value);
			}
		}
	}

	/**
	 * Reads dynamic property {@link ElementInfo#setDynamicProperty} information for
	 * state pathway element. If dynamic property codes for DoubleLineProperty or
	 * CellularComponentProperty, updates/overrides borderStyle or shapeType.
	 * Otherwise, sets dynamic property.
	 * 
	 * NB: Property (dynamic property) was named Attribute in GPML2013a.
	 * 
	 * @param state the state pathway element.
	 * @param st    the jdom state pathway element element.
	 * @throws ConverterException
	 */
	protected void readStateDynamicProperties(State state, Element st) throws ConverterException {
		for (Element dp : st.getChildren("Attribute", st.getNamespace())) {
			String key = getAttr("Attribute", "Key", dp);
			String value = getAttr("Attribute", "Value", dp);
			if (key.equals(DOUBLE_LINE_KEY) && value.equalsIgnoreCase("Double")) {
				state.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
			} else if (key.equals(CELL_CMPNT_KEY)) {
				ShapeType type = ShapeType.register(value);
				state.getShapeStyleProperty().setShapeType(type);
			} else {
				state.setDynamicProperty(key, value);
			}
		}
	}

	/**
	 * Reads dynamic property {@link ElementInfo#setDynamicProperty} information for
	 * interaction or graphicalLine pathway element. If dynamic property codes for
	 * DoubleLineProperty, updates lineStyle. Otherwise, sets dynamic property.
	 * 
	 * NB: Property (dynamic property) was named Attribute in GPML2013a.
	 * 
	 * @param lineElement the line pathway element.
	 * @param ln          the jdom line pathway element element.
	 * @throws ConverterException
	 */
	protected void readLineDynamicProperties(LineElement lineElement, Element ln) throws ConverterException {
		for (Element dp : ln.getChildren("Attribute", ln.getNamespace())) {
			String key = getAttr("Attribute", "Key", dp);
			String value = getAttr("Attribute", "Value", dp);
			/* dynamic property DoubleLineProperty sets lineStyle */
			if (key.equals(DOUBLE_LINE_KEY) && value.equalsIgnoreCase("Double")) {
				lineElement.getLineStyleProperty().setLineStyle(LineStyleType.DOUBLE);
			} else {
				lineElement.setDynamicProperty(key, value);
			}
		}
	}

	/**
	 * Reads rect property {@link RectProperty} information. Jdom handles schema
	 * default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @returns the rectProperty object.
	 * @throws ConverterException
	 */
	protected RectProperty readRectProperty(Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		double centerX = Double.parseDouble(getAttr(base + ".Graphics", "CenterX", gfx).trim());
		double centerY = Double.parseDouble(getAttr(base + ".Graphics", "CenterY", gfx).trim());
		double width = Double.parseDouble(getAttr(base + ".Graphics", "Width", gfx).trim());
		double height = Double.parseDouble(getAttr(base + ".Graphics", "Height", gfx).trim());
		// instantiates rect properties
		return new RectProperty(new Coordinate(centerX, centerY), width, height);
	}

	/**
	 * Reads font property {@link FontProperty} information. Jdom handles schema
	 * default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @returns the fontProperty object.
	 * @throws ConverterException
	 */
	protected FontProperty readFontProperty(Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		Color textColor = ColorUtils.stringToColor(getAttr(base + ".Graphics", "Color", gfx));
		String fontName = getAttr(base + ".Graphics", "FontName", gfx);
		String fontWeightStr = getAttr(base + ".Graphics", "FontWeight", gfx);
		String fontStyleStr = getAttr(base + ".Graphics", "FontStyle", gfx);
		String fontDecorationStr = getAttr(base + ".Graphics", "FontDecoration", gfx);
		String fontStrikethruStr = getAttr(base + ".Graphics", "FontStrikethru", gfx);
		boolean fontWeight = fontWeightStr != null && fontWeightStr.equalsIgnoreCase("Bold");
		boolean fontStyle = fontStyleStr != null && fontStyleStr.equalsIgnoreCase("Italic");
		boolean fontDecoration = fontDecorationStr != null && fontDecorationStr.equalsIgnoreCase("Underline");
		boolean fontStrikethru = fontStrikethruStr != null && fontStrikethruStr.equalsIgnoreCase("Strikethru");
		int fontSize = Integer.parseInt(getAttr(base + ".Graphics", "FontSize", gfx).trim());
		HAlignType hAlignType = HAlignType.fromName(getAttr(base + ".Graphics", "Align", gfx));
		VAlignType vAlignType = VAlignType.fromName(getAttr(base + ".Graphics", "Valign", gfx));
		// instantiates font properties and returns
		return new FontProperty(textColor, fontName, fontWeight, fontStyle, fontDecoration, fontStrikethru, fontSize,
				hAlignType, vAlignType);
	}

	/**
	 * Reads shape style property {@link ShapeStyleProperty} information. Jdom
	 * handles schema default values. If shape type is a key in
	 * {@link GPML2013aFormatAbstract#DEPRECATED_MAP}, replaces deprecated shape
	 * type with newer value.
	 * 
	 * NB: If pathway element has dynamic property key CellularComponentProperty,
	 * shape type is again replaced, this time with the dynamic property value in
	 * {@link #readShapedDynamicProperties} or {@link #readStateDynamicProperties}.
	 * 
	 * @param gfx the parent graphics element.
	 * @returns the shapeStyleProperty object.
	 * @throws ConverterException
	 */
	protected ShapeStyleProperty readShapeStyleProperty(Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		Color borderColor = ColorUtils.stringToColor(getAttr(base + ".Graphics", "Color", gfx));
		String borderStyleStr = getAttr(base + ".Graphics", "LineStyle", gfx);
		// in GPML2021, "Broken" line style was renamed to "Dashed"
		if (borderStyleStr.equalsIgnoreCase("Broken"))
			borderStyleStr = "Dashed";
		LineStyleType borderStyle = LineStyleType.register(borderStyleStr);
		double borderWidth = Double.parseDouble(getAttr(base + ".Graphics", "LineThickness", gfx).trim());
		Color fillColor = ColorUtils.stringToColor(getAttr(base + ".Graphics", "FillColor", gfx));

		String shapeTypeStr = getAttr(base + ".Graphics", "ShapeType", gfx);
		shapeTypeStr = toCamelCase(shapeTypeStr);
		ShapeType shapeType = ShapeType.register(shapeTypeStr);
		// checks deprecated shape type map for newer shape
		if (DEPRECATED_MAP.containsKey(shapeType))
			shapeType = DEPRECATED_MAP.get(shapeType);
		// instantiates shape style properties
		ShapeStyleProperty shapeStyleProperty = new ShapeStyleProperty(borderColor, borderStyle, borderWidth, fillColor,
				shapeType);
		// sets optional property z-order
		String zOrder = getAttr(base + ".Graphics", "ZOrder", gfx);
		if (zOrder != null)
			shapeStyleProperty.setZOrder(Integer.parseInt(zOrder.trim()));
		// returns shape style property
		return shapeStyleProperty;
	}

	/**
	 * Reads line style property {@link LineStyleProperty} information. Jdom handles
	 * schema default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @returns the lineStyleProperty object.
	 * @throws ConverterException
	 */
	protected LineStyleProperty readLineStyleProperty(Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		Color lineColor = ColorUtils.stringToColor(getAttr(base + ".Graphics", "Color", gfx));
		String lineStyleStr = getAttr(base + ".Graphics", "LineStyle", gfx);
		// in GPML2021, "Broken" line style was renamed to "Dashed"
		if (lineStyleStr.equalsIgnoreCase("Broken"))
			lineStyleStr = "Dashed";
		LineStyleType lineStyle = LineStyleType.register(lineStyleStr);
		double lineWidth = Double.parseDouble(getAttr(base + ".Graphics", "LineThickness", gfx).trim());
		ConnectorType connectorType = ConnectorType.register(getAttr(base + ".Graphics", "ConnectorType", gfx));
		// instantiates line style property
		LineStyleProperty lineStyleProperty = new LineStyleProperty(lineColor, lineStyle, lineWidth, connectorType);
		// sets optional property z-order
		String zOrder = getAttr(base + ".Graphics", "ZOrder", gfx);
		if (zOrder != null)
			lineStyleProperty.setZOrder(Integer.parseInt(zOrder.trim()));
		// returns line style property
		return lineStyleProperty;
	}

}
