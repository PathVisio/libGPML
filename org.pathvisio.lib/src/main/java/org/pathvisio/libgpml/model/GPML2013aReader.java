/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.libgpml.model;

import java.awt.Color;
import java.util.ArrayList;
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
import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.io.ConverterException;
import org.pathvisio.libgpml.model.DataNode.State;
import org.pathvisio.libgpml.model.GraphLink.LinkableTo;
import org.pathvisio.libgpml.model.LineElement.Anchor;
import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.model.PathwayElement.AnnotationRef;
import org.pathvisio.libgpml.model.PathwayElement.CitationRef;
import org.pathvisio.libgpml.model.PathwayElement.Comment;
import org.pathvisio.libgpml.model.shape.ShapeType;
import org.pathvisio.libgpml.model.type.AnchorShapeType;
import org.pathvisio.libgpml.model.type.AnnotationType;
import org.pathvisio.libgpml.model.type.ArrowHeadType;
import org.pathvisio.libgpml.model.type.ConnectorType;
import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.GroupType;
import org.pathvisio.libgpml.model.type.HAlignType;
import org.pathvisio.libgpml.model.type.LineStyleType;
import org.pathvisio.libgpml.model.type.StateType;
import org.pathvisio.libgpml.model.type.VAlignType;
import org.pathvisio.libgpml.util.ColorUtils;
import org.pathvisio.libgpml.util.XrefUtils;

/**
 * This class reads a PathwayModel from an input source (GPML 2013a).
 * <p>
 * NB:
 * <ol>
 * <li>In GPML2013a, there are two kinds of gpml:Biopax {@link Element}
 * bp:openControlledVocabulary and bp:PublicationXref which correspond to
 * {@link Annotation} and {@link Citation} respectively.
 * <li>gpml:CommentGroup:Biopax holds references to bp:PublicationXref rdf:id.
 * <li>All openControlledVocabulary belong to the pathway in GPML2013a.
 * </ol>
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
	 * are read first. For example, Groups are read before other pathway element.
	 * DataNodes are read before States. And Points are read last.
	 * 
	 * 
	 * <p>
	 * NB: In GPML2013a, GraphIds (elementIds) are missing for some pathway
	 * elements, or may conflict with Biopax rdf:id or GroupId
	 * 
	 * <ol>
	 * <li>elementIdSet: stores all ids for a reading session to ensure unique
	 * elementIds, {@link #readAllElementIds}
	 * <li>idToPublicationXref: stores Biopax:PublicationXref id and element
	 * information, {@link #readPublicationXrefMap}
	 * <li>groupIdToNew: stores GroupId and newly assigned elementIds,
	 * {@link #readGroups}
	 * <li>lineList: stores line elementIds in the order read,
	 * {@link #readGraphicalLines}, {@link #readInteractions}, {@link #readPoints}.
	 * </ol>
	 * 
	 * @param pathwayModel the given pathway model.
	 * @param root         the root element of given Jdom document.
	 * @return pathwayModel the pathway model after reading root element.
	 * @throws ConverterException
	 */
	public PathwayModel readFromRoot(PathwayModel pathwayModel, Element root) throws ConverterException {

		Set<String> elementIdSet = new HashSet<String>(); // all elementIds
		Map<String, PublicationXref> idToPublicationXref = new HashMap<String, PublicationXref>(); // ids and biopax
		Map<String, Group> groupIdToGroup = new HashMap<String, Group>(); // graphId to group
		Map<String, Group> graphIdToGroup = new HashMap<String, Group>(); // groupId to group
		Map<Element, LinePoint> elementToPoint = new HashMap<Element, LinePoint>(); // jdom element to point

		readAllElementIds(root, elementIdSet);// reads all elementIds and stores in set
		readPublicationXrefMap(root, elementIdSet, idToPublicationXref);
		// reads pathway information and comment group
		readPathway(pathwayModel.getPathway(), root);
		readCommentGroup(pathwayModel.getPathway(), root, idToPublicationXref);
		// reads biopax OpenControlledVocabulary/Annotation(s)
		readOpenControlledVocabulary(pathwayModel, root, elementIdSet);
		// reads groups first and then groupRefs
		readGroups(pathwayModel, root, elementIdSet, idToPublicationXref, groupIdToGroup, graphIdToGroup);
		readGroupGroupRef(pathwayModel, root, groupIdToGroup);
		readLabels(pathwayModel, root, elementIdSet, idToPublicationXref, groupIdToGroup);
		readShapes(pathwayModel, root, elementIdSet, idToPublicationXref, groupIdToGroup);
		readDataNodes(pathwayModel, root, elementIdSet, idToPublicationXref, groupIdToGroup);
		// reads states after data nodes
		readStates(pathwayModel, root, elementIdSet, idToPublicationXref);
		readInteractions(pathwayModel, root, elementIdSet, idToPublicationXref, groupIdToGroup, elementToPoint);
		readGraphicalLines(pathwayModel, root, elementIdSet, idToPublicationXref, groupIdToGroup, elementToPoint);
		// reads line point elementRefs last
		readPointElementRefs(pathwayModel, graphIdToGroup, elementToPoint);
		// removes empty groups
		removeEmptyGroups(pathwayModel);
		return pathwayModel;
	}

	/**
	 * Reads elementIds (GraphIds in GPML2013a) of root element and adds to
	 * elementIdSet. In GPML2013a, elementId (previously named GraphId) is sometimes
	 * missing. In order to assign unique new elementIds, we must first read all
	 * elementIds from the Jdom document to store in elementIdSet.
	 * 
	 * @param root         the root element.
	 * @param elementIdSet the set of all elementIds.
	 */
	protected void readAllElementIds(Element root, Set<String> elementIdSet) {
		List<Element> elements = root.getChildren();
		for (Element e : elements) {
			String graphId = e.getAttributeValue("GraphId");
			if (graphId != null)
				elementIdSet.add(graphId);
			// reads graphId for Points and Anchors
			if (e.getName() == "Interaction" || e.getName() == "GraphicalLine") {
				Element gfx = e.getChild("Graphics", e.getNamespace());
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
	protected Pathway readPathway(Pathway pathway, Element root) throws ConverterException {
		pathway.setTitle(getAttr("Pathway", "Name", root));
		Element gfx = root.getChild("Graphics", root.getNamespace());
		pathway.setBoardWidth(Double.parseDouble(getAttr("Pathway.Graphics", "BoardWidth", gfx).trim()));
		pathway.setBoardHeight(Double.parseDouble(getAttr("Pathway.Graphics", "BoardHeight", gfx).trim()));
		// sets optional properties
		pathway.setOrganism(getAttr("Pathway", "Organism", root));
		pathway.setSource(getAttr("Pathway", "Data-Source", root));
		pathway.setVersion(getAttr("Pathway", "Version", root));
		pathway.setLicense(getAttr("Pathway", "License", root));
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
		readInfoBox(pathway, root);
		readLegend(pathway, root);
		return pathway;
	}

	/**
	 * Reads the infobox x and y coordinate information. NB: Infobox is removed in
	 * GPML2021, as the info box is always located in the top left corner (0,0).
	 * 
	 * @param pathway the pathway.
	 * @param root    the root element.
	 */
	protected void readInfoBox(Pathway pathway, Element root) {
		Element ifbx = root.getChild("InfoBox", root.getNamespace());
		if (ifbx != null) {
			String centerX = ifbx.getAttributeValue("CenterX").trim();
			pathway.setDynamicProperty(INFOBOX_CENTER_X, centerX);
			String centerY = ifbx.getAttributeValue("CenterY").trim();
			pathway.setDynamicProperty(INFOBOX_CENTER_Y, centerY);
		}
	}

	/**
	 * Reads the Legend CenterX and CenterY to pathway dynamic properties
	 * {@link Pathway#setDynamicProperty} .
	 * 
	 * @param pathway the pathway.
	 * @param root    the root element.
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
	 * Reads gpml:Biopax bp:OpenControlledVocabulary information to
	 * {@link Annotation} for pathway model from root element.
	 * 
	 * NB:
	 * <ul>
	 * <li>In GPML2013a, all annotations are by default added to {@link Pathway}
	 * using {@link AnnotationRef}.</li>
	 * <li>New unique elementIds are assigned (missing in GPML2013a).</li>
	 * </ul>
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the jdom root element.
	 * @param elementIdSet the set of all elementIds.
	 * @throws ConverterException
	 */
	protected void readOpenControlledVocabulary(PathwayModel pathwayModel, Element root, Set<String> elementIdSet)
			throws ConverterException {
		Element bp = root.getChild("Biopax", root.getNamespace());
		if (bp != null) {
			for (Element ocv : bp.getChildren("openControlledVocabulary", BIOPAX_NAMESPACE)) {
				// generates new unique elementId and adds to elementIdSet
				String elementId = PathwayModel.getUniqueId(elementIdSet);
				Logger.log.trace("Annotation missing elementId, new id is: " + elementId);
				elementIdSet.add(elementId);
				// reads OpenControlledVocabulary
				String value = ocv.getChild("TERM", BIOPAX_NAMESPACE).getText();
				String biopaxOntology = ocv.getChild("Ontology", BIOPAX_NAMESPACE).getText();
				// "Disease", "Pathway Ontology", "Cell Type" are categorized as ontology
				AnnotationType type = null;
				if (OCV_ONTOLOGY_MAP.containsKey(biopaxOntology)) {
					type = AnnotationType.ONTOLOGY;
				} else {
					type = AnnotationType.register(biopaxOntology);
				}
				// reads xref information from "ID"
				String biopaxIdDbStr = ocv.getChild("ID", BIOPAX_NAMESPACE).getText(); // e.g PW:0000650
				String[] biopaxIdDb = biopaxIdDbStr.split(":"); // splits "ID" into Id and Database
				String biopaxDb = biopaxIdDb[0]; // e.g. PW
				String biopaxId = biopaxIdDb[1]; // e.g 0000650
				Xref xref = XrefUtils.createXref(biopaxId, biopaxDb);
				// creates and adds annotation and annotationRef
				pathwayModel.getPathway().addAnnotation(elementId, value, type, xref, null);
			}
		}
	}

	/**
	 * Reads gpml:Biopax:bp:PublicationXref and maps "id" to {@link PublicationXref}
	 * in idToPublicationXref. PublicationXref stores jdom {@link Element}
	 * information.
	 * 
	 * NB: If biopax "id" is not unique, a new unique elementId (value) is assigned,
	 * added to elementIdSet.
	 * 
	 * @param root                the jdom root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @throws ConverterException
	 */
	protected void readPublicationXrefMap(Element root, Set<String> elementIdSet,
			Map<String, PublicationXref> idToPublicationXref) throws ConverterException {
		Element bp = root.getChild("Biopax", root.getNamespace());
		if (bp != null) {
			// adds PublicationXref(s)
			for (Element pubxf : bp.getChildren("PublicationXref", BIOPAX_NAMESPACE)) {
				// the rdf:id
				String id = pubxf.getAttributeValue("id", RDF_NAMESPACE);
				// if id not unique, generates new unique elementId
				String elementId = null;
				if (elementIdSet.contains(id)) {
					String newId = PathwayModel.getUniqueId(elementIdSet);
					Logger.log.trace("Biopax id " + id + " is not unique, new id is: " + newId);
					elementId = newId;
				} else {
					elementId = id; // if rdf:id unique, use as elementId
				}
				elementIdSet.add(elementId);
				// store info for reading
				PublicationXref biopax = new PublicationXref(pubxf, elementId);
				idToPublicationXref.put(id, biopax);
			}
		}
	}

	/**
	 * Reads gpml:Biopax:bp:PublicationXref and BiopaxRef information for pathway
	 * element from jdom element. Creates and adds {@link CitationRef} and
	 * {@link Citation}.
	 * <p>
	 * NB:
	 * <ol>
	 * <li>gpml:CommentGroup:BiopaxRef {@link CitationRef} holds reference to
	 * gpml:Biopax:PublicationXref {@link Citation}.
	 * <li>BiopaxRef was an attribute (not the element) of many of the pathway
	 * elements in the GPML2013a schema, but was never used/implemented.
	 * <li>Biopax PublicationXref which do not have a BiopaxRef will not be read.
	 * </ol>
	 * 
	 * @param pathwayElement      the element info pathway element object.
	 * @param e                   the jdom element.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @throws ConverterException
	 */
	protected void readPublicationXrefs(PathwayElement pathwayElement, Element e,
			Map<String, PublicationXref> idToPublicationXref) throws ConverterException {
		for (Element bpRef : e.getChildren("BiopaxRef", e.getNamespace())) {
			String id = bpRef.getText();
			if (idToPublicationXref.containsKey(id)) {
				// retrieve jdom element and elementId
				PublicationXref publicationXref = idToPublicationXref.get(id);
				publicationXref.setHasRef(true); // indicate this publicationXref is used
				Element pubxf = publicationXref.getElement();
				String elementId = publicationXref.getElementId();
				// reads PublicationXref/citation
				String biopaxId = readPublicationXrefInfo(pubxf.getChildren("ID", BIOPAX_NAMESPACE));
				String biopaxDb = readPublicationXrefInfo(pubxf.getChildren("DB", BIOPAX_NAMESPACE));
				Xref xref = XrefUtils.createXref(biopaxId, biopaxDb);
				String source = readPublicationXrefInfo(pubxf.getChildren("SOURCE", BIOPAX_NAMESPACE));
				// if source is an url, also set as citation urlLink
				String urlLink = null;
				if (source != null) {
					if (source.startsWith("http") || source.startsWith("www")) {
						urlLink = source;
					}
				}
				// instantiates citation and adds
				CitationRef citationRef = pathwayElement.addCitation(elementId, xref, urlLink);
				Citation citation = citationRef.getCitation();
				// sets optional properties
				citation.setTitle(readPublicationXrefInfo(pubxf.getChildren("TITLE", BIOPAX_NAMESPACE)));
				citation.setSource(source);
				citation.setYear(readPublicationXrefInfo(pubxf.getChildren("YEAR", BIOPAX_NAMESPACE)));
				List<String> authors = new ArrayList<String>();
				for (Element au : pubxf.getChildren("AUTHORS", BIOPAX_NAMESPACE)) {
					String author = au.getText();
					if (author != null)
						authors.add(author);
				}
				if (!authors.isEmpty())
					citation.setAuthors(authors);
			} else {
				Logger.log.trace("Warning: biopaxRef " + id
						+ " refers to invalid Biopax PublicationXref, biopaxRef is not created.");
			}
		}
	}

	/**
	 * Reads Biopax PublicationXref information from PublicationXref children
	 * elements. In GPML2013a there are some duplicated and/or empty gpml:Biopax
	 * PublicationXref information. This method reads the list of child elements
	 * until a valid value is obtained.
	 * 
	 * @param pubxfElements the pubxf children elements with the same local name.
	 * @return elementText the string text value for the element.
	 * @throws ConverterException
	 */
	protected String readPublicationXrefInfo(List<Element> pubxfElements) throws ConverterException {
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
	 * Reads comment group (comment, biopaxref/citationRef, dynamic property) for
	 * pathway element from jdom element.
	 * <p>
	 * NB about CommentGroup:
	 * <ol>
	 * <li>In GPML2013a, gpml:CommentGroup:PublicationXref was not implemented.
	 * <li>The element gpml:CommentGroup:BiopaxRef acts as {@link CitationRef} and
	 * holds the reference to to gpml:Biopax:PublicationXref equivalent to
	 * {@link Citation}.
	 * <li>In GPML2021, Evidence and EvidenceRef was added.
	 * <li>BiopaxRef was an attribute (not the element) of many of the pathway
	 * elements in the GPML2013a schema, but was never used/implemented.
	 * </ol>
	 * 
	 * @param pathwayElement the pathway element.
	 * @param e              the jdom element.
	 * @throws ConverterException
	 */
	protected void readCommentGroup(PathwayElement pathwayElement, Element e,
			Map<String, PublicationXref> idToPublicationXref) throws ConverterException {
		readComments(pathwayElement, e);
		readPublicationXrefs(pathwayElement, e, idToPublicationXref);
		readDynamicProperties(pathwayElement, e);
	}

	/**
	 * Reads shaped pathway element {@link ShapedElement} information: graphics and
	 * comment group (comments, biopaxRefs (equivalent to citationRefs), and dynamic
	 * properties).
	 * 
	 * NB: {@link #readGroups} does not call this method and instead calls
	 * {@link #readCommentGroup}. In GPML2013a, Group graphics properties were hard
	 * coded and were not written to the gpml.
	 * 
	 * @param shapedElement       the shaped pathway element.
	 * @param se                  the jdom element.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @throws ConverterException
	 */
	protected void readShapedElement(ShapedElement shapedElement, Element se,
			Map<String, PublicationXref> idToPublicationXref) throws ConverterException {
		Element gfx = se.getChild("Graphics", se.getNamespace());
		readRectProperty(shapedElement, gfx);
		readShapeStyleProperty(shapedElement, gfx);
		if (shapedElement.getClass() != State.class) {
			readFontProperty(shapedElement, gfx);
		}
		readCommentGroup(shapedElement, se, idToPublicationXref); // reads comments,
	}

	/**
	 * Reads comment {@link Comment} information for pathway element from jdom
	 * element.
	 * 
	 * NB: If pathway element is {@link Pathway} and source is
	 * WikiPathways-description, set comment as pathway description instead!
	 * 
	 * @param pathwayElement the pathway element.
	 * @param e              the jdom element.
	 * @throws ConverterException
	 */
	protected void readComments(PathwayElement pathwayElement, Element e) throws ConverterException {
		for (Element cmt : e.getChildren("Comment", e.getNamespace())) {
			String source = getAttr("Comment", "Source", cmt);
			String commentText = cmt.getText();
			// if pathway and source is WikiPathways-description, set as pathway description
			if (pathwayElement.getClass() == Pathway.class) {
				if (Objects.equals(WP_DESCRIPTION, source)) {
					((Pathway) pathwayElement).setDescription(commentText);
					continue;
				}
			}
			// comment must have text
			if (commentText != null && !commentText.equals("")) {
				// instantiates and adds comment to pathway element
				pathwayElement.addComment(commentText, source);
			}
		}
	}

	/**
	 * Reads gpml:Attribute or dynamic property information for
	 * {@link PathwayElement} from jdom element.
	 * 
	 * <p>
	 * NB:
	 * <ol>
	 * <li>For {@link ShapedElement}, if dynamic property codes for
	 * DoubleLineProperty or CellularComponentProperty, updates/overrides
	 * borderStyle or shapeType. Otherwise, sets dynamic property.
	 * <li>For {@link LineElement}, if dynamic property codes for
	 * DoubleLineProperty, updates lineStyle. Otherwise, sets dynamic property.
	 * </ol>
	 * NB: Property (dynamic property) was named Attribute in GPML2013a.
	 * 
	 * @param pathwayElement the pathway element.
	 * @param root           the jdom root element.
	 * @throws ConverterException
	 */
	protected void readDynamicProperties(PathwayElement pathwayElement, Element root) throws ConverterException {
		for (Element dp : root.getChildren("Attribute", root.getNamespace())) {
			String key = getAttr("Attribute", "Key", dp);
			String value = getAttr("Attribute", "Value", dp);
			// if instance of shaped pathway element
			if (pathwayElement instanceof ShapedElement) {
				if (key.equals(DOUBLE_LINE_KEY) && value.equalsIgnoreCase("Double")) {
					((ShapedElement) pathwayElement).setBorderStyle(LineStyleType.DOUBLE);
					continue;
				}
				if (key.equals(CELL_CMPNT_KEY)) {
					((ShapedElement) pathwayElement).setShapeType(ShapeType.register(toCamelCase(value), null));
					continue;
				}
			}
			// if instance of line pathway element
			if (pathwayElement instanceof LineElement) {
				if (key.equals(DOUBLE_LINE_KEY) && value.equalsIgnoreCase("Double")) {
					((LineElement) pathwayElement).setLineStyle(LineStyleType.DOUBLE);
					continue;
				}
			}
			// if state, read rotation
			if (pathwayElement.getClass() == State.class) {
				if (key.equals(STATE_ROTATION)) {
					((State) pathwayElement).setRotation(Double.parseDouble(value));
					continue;
				}
			}
			pathwayElement.setDynamicProperty(key, value);
		}
	}

	/**
	 * Reads group {@link Group} information for pathway model from root element.
	 *
	 * <p>
	 * NB:
	 * <ol>
	 * <li>A group has identifier GroupId (essentially ElementId), while GraphId is
	 * optional. A group has GraphId if there is at least one {@link LinePoint}
	 * referring to this group by GraphRef. Because GroupIds may conflict with an
	 * elementId, new unique elementIds (value) can be assigned with reference back
	 * to the original GroupIds (key) in groupIdToNew map.
	 * <li>Group type "None" (GPML2013a) is replaced with "Group" (GPML2021).
	 * <li>Group type "Group" (GPML2013a) is replaced with "Transparent" (GPML2021).
	 * <li>Rect properties (centerX, centerY, width, height) are not set but
	 * calculated when needed.
	 * </ol>
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @param groupIdToGroup      the map of groupId to group.
	 * @throws ConverterException
	 */
	protected void readGroups(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, PublicationXref> idToPublicationXref, Map<String, Group> groupIdToGroup,
			Map<String, Group> graphIdToGroup) throws ConverterException {
		for (Element grp : root.getChildren("Group", root.getNamespace())) {
			// reads groupId, graphId. If groupId not unique, generate new elementId
			String elementId = null;
			String groupId = getAttr("Group", "GroupId", grp);
			String graphId = getAttr("Group", "GraphId", grp);
			if (elementIdSet.contains(groupId)) {
				String newId = PathwayModel.getUniqueId(elementIdSet);
				Logger.log.trace("GroupId " + elementId + " is not unique, new id is: " + newId);
				elementId = newId;
			} else {
				elementId = groupId; // if groupId unique, use as elementId
			}
			elementIdSet.add(elementId);
			// read type
			String typeStr = getAttr("Group", "Style", grp);
			if (typeStr.equals("Group")) {
				typeStr = "Transparent"; // "Group" (GPML2013a) group type is replaced with "Transparent" (GPML2021)
			} else if (typeStr.equals("None")) {
				typeStr = "Group";// "None" (GPML2013a) group type is replaced with "Group" (GPML2021)
			}
			GroupType type = GroupType.register(typeStr);
			// instantiates group and adds to pathway model
			Group group = new Group(type);
			group.setElementId(elementId);
			pathwayModel.addGroup(group);
			// store info for reading
			groupIdToGroup.put(groupId, group);
			graphIdToGroup.put(graphId, group);
			// Sets graphics.
			setGroupGraphicsProperty(group, type);
			// reads comment group
			readCommentGroup(group, grp, idToPublicationXref);
			// sets optional properties
			group.setTextLabel(getAttr("Group", "TextLabel", grp));
		}

	}

	/**
	 * Reads groupRef property {@link Group#setGroupRef} information of group
	 * pathway element. Because a group may refer to another group not yet
	 * initialized. We read and set groupRef after reading all group elements. The
	 * groupRef is the "parent" group to which this group refers.
	 * 
	 * @param pathwayModel   the pathway model.
	 * @param root           the root element.
	 * @param groupIdToGroup the map of groupId to group.
	 * @throws ConverterException
	 */
	protected void readGroupGroupRef(PathwayModel pathwayModel, Element root, Map<String, Group> groupIdToGroup)
			throws ConverterException {
		for (Element grp : root.getChildren("Group", root.getNamespace())) {
			String groupId = getAttr("Group", "GroupId", grp);
			String groupRefStr = getAttr("Group", "GroupRef", grp);
			if (groupRefStr != null && !groupRefStr.equals("")) {
				Group group = groupIdToGroup.get(groupId);
				Group groupRef = groupIdToGroup.get(groupRefStr);
				// sets groupRef for this group
				if (group != null && groupRef != null)
					group.setGroupRefTo(groupRef);
			}
		}
	}

	/**
	 * Reads and sets graphics properties for {@link Group}.
	 * <p>
	 * Graphics depends on group type:
	 * <ol>
	 * <li>Because textLabel was not implemented for groups in 2013a, we set the
	 * textColor to transparent TODO
	 * <li>TRANSPARENT: transparent rectangle, hovers to blue #0000ff0c
	 * <li>COMPLEX: gray #b4b46419 octagon with gray #808080 solid border, hovers to
	 * red #ff00000c
	 * <li>PATHWAY: green #00ff000c rectangle with gray #808080 dashed border,
	 * hovers to green #00ff0019.
	 * <li>GROUP (DEFAULT): gray #b4b46419 rectangle with gray #808080 dashed
	 * border, hovers to red #ff00000c
	 * </ol>
	 * 
	 * @param group the group pathway element.
	 * @param type  the group type.
	 * @throws ConverterException
	 */
	protected void setGroupGraphicsProperty(Group group, GroupType type) throws ConverterException {
		group.setTextColor(Color.decode("#808080"));
		group.setBorderWidth(1.0);
		if (type == GroupType.TRANSPARENT) {
			group.setBorderColor(Color.decode("#00000000"));
			group.setBorderStyle(LineStyleType.SOLID);
			group.setFillColor(ColorUtils.hexToColor("#00000000"));
			group.setShapeType(ShapeType.RECTANGLE);
		} else if (type == GroupType.COMPLEX) {
			group.setBorderColor(Color.decode("#808080"));
			group.setBorderStyle(LineStyleType.SOLID);
			group.setFillColor(ColorUtils.hexToColor("#b4b46419"));
			group.setShapeType(ShapeType.OCTAGON);
		} else if (type == GroupType.PATHWAY) {
			group.setBorderColor(Color.decode("#808080"));
			group.setBorderStyle(LineStyleType.DASHED);
			group.setFillColor(ColorUtils.hexToColor("#00ff000c"));
			group.setShapeType(ShapeType.RECTANGLE);
		} else { // DEFAULT
			group.setBorderColor(Color.decode("#808080"));
			group.setBorderStyle(LineStyleType.DASHED);
			group.setFillColor(ColorUtils.hexToColor("#b4b46419"));
			group.setShapeType(ShapeType.RECTANGLE);
		}
	}

	/**
	 * Reads label {@link Label} information for pathway model from root element.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @param groupIdToGroup      the map of groupId to group.
	 * @throws ConverterException
	 */
	protected void readLabels(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, PublicationXref> idToPublicationXref, Map<String, Group> groupIdToGroup)
			throws ConverterException {
		for (Element lb : root.getChildren("Label", root.getNamespace())) {
			String elementId = readElementId("Label", lb, elementIdSet);
			String textLabel = getAttr("Label", "TextLabel", lb);
			// instantiates label and adds to pathway model
			Label label = new Label(textLabel);
			label.setElementId(elementId);
			pathwayModel.addLabel(label);
			// read graphics and comment group
			readShapedElement(label, lb, idToPublicationXref);
			// sets optional properties
			readGroupRef(label, lb, groupIdToGroup);
			label.setHref(getAttr("Label", "Href", lb));
		}
	}

	/**
	 * Reads shape {@link Shape} information for pathway model from root element.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @param groupIdToGroup      the map of groupId to group.
	 * @throws ConverterException
	 */
	protected void readShapes(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, PublicationXref> idToPublicationXref, Map<String, Group> groupIdToGroup)
			throws ConverterException {
		for (Element shp : root.getChildren("Shape", root.getNamespace())) {
			String elementId = readElementId("Shape", shp, elementIdSet);
			// instantiates shape and adds to pathway model
			Shape shape = new Shape();
			shape.setElementId(elementId);
			pathwayModel.addShape(shape);
			// read graphics and comment group
			readShapedElement(shape, shp, idToPublicationXref);
			// sets optional properties
			shape.setTextLabel(getAttr("Shape", "TextLabel", shp));
			readGroupRef(shape, shp, groupIdToGroup);

		}
	}

	/**
	 * Reads data node {@link DataNode} information for pathway model from root
	 * element.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @param groupIdToGroup      the map of groupId to group.
	 * @throws ConverterException
	 */
	protected void readDataNodes(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, PublicationXref> idToPublicationXref, Map<String, Group> groupIdToGroup)
			throws ConverterException {
		for (Element dn : root.getChildren("DataNode", root.getNamespace())) {
			String elementId = readElementId("DataNode", dn, elementIdSet);
			String textLabel = getAttr("DataNode", "TextLabel", dn);
			String typeStr = getAttr("DataNode", "Type", dn);
			// in GPML2021, "Unknown" data node type is named "Undefined"
			if (typeStr.equals("Unknown"))
				typeStr = "Undefined";
			DataNodeType type = DataNodeType.register(typeStr);
			// instantiates data node and adds to pathway model
			DataNode dataNode = new DataNode(textLabel, type);
			dataNode.setElementId(elementId);
			pathwayModel.addDataNode(dataNode);
			// read graphics and comment group
			readShapedElement(dataNode, dn, idToPublicationXref);
			// sets optional properties
			readGroupRef(dataNode, dn, groupIdToGroup);
			dataNode.setXref(readXref(dn));
		}
	}

	/**
	 * Reads state {@link State} information for pathway model from root element.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @throws ConverterException
	 */
	protected void readStates(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, PublicationXref> idToPublicationXref) throws ConverterException {
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
			// finds parent datanode from state elementRef
			String elementRef = getAttr("State", "GraphRef", st);
			DataNode dataNode = (DataNode) pathwayModel.getPathwayObject(elementRef);
			// instantiates state and adds state
			State state = dataNode.addState(elementId, textLabel, type, relX, relY);
			// reads graphics and comment group
			readShapedElement(state, st, idToPublicationXref);
			state.setZOrder(dataNode.getZOrder() + 1);
			state.setTextColor(state.getTextColor());
			// convert comments to Xref and AnnotationRef if applicable
			convertStateCommentToRefs(state, elementIdSet);
			// sets optional properties
			state.setXref(readXref(st));

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
	protected void convertStateCommentToRefs(State state, Set<String> elementIdSet) throws ConverterException {
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
					try {
						annotationsMap.put(type, parts[1]); // type and value
					} catch (ArrayIndexOutOfBoundsException exception) {
						continue; // skip if part[1] invalid
					}
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
						state.setXref(XrefUtils.createXref(identifier, SITEGRPID_DB));
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
					state.addAnnotation(elementId, value, type, xref, null);
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
	 * NB: points are read by {@link #readPoints}.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @param groupIdToGroup      the map of groupId to group.
	 * @param elementToPoint      the map of jdom element to line points.
	 * @throws ConverterException
	 */
	protected void readInteractions(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, PublicationXref> idToPublicationXref, Map<String, Group> groupIdToGroup,
			Map<Element, LinePoint> elementToPoint) throws ConverterException {
		for (Element ia : root.getChildren("Interaction", root.getNamespace())) {
			String elementId = readElementId("Interaction", ia, elementIdSet);
			// instantiates interaction and adds to pathway model
			Interaction interaction = new Interaction();
			interaction.setElementId(elementId);
			readPoints(interaction, ia, elementIdSet, elementToPoint);
			pathwayModel.addInteraction(interaction);
			// reads line properties
			readLineElement(interaction, ia, elementIdSet, idToPublicationXref, groupIdToGroup);
			// sets optional properties
			interaction.setXref(readXref(ia));
		}
	}

	/**
	 * Reads graphical line {@link GraphicalLine} information for pathway model from
	 * root element. New unique elementIds are assigned to line pathway elements are
	 * stored in the ordered lineList so that line pathway elements can be retrieved
	 * based on previous read order.
	 * 
	 * NB: points are read by {@link #readPoints}.
	 * 
	 * @param pathwayModel        the pathway model.
	 * @param root                the root element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @param groupIdToGroup      the map of groupId to group.
	 * @param elementToPoint      the map of jdom element to line points.
	 * @throws ConverterException
	 */
	protected void readGraphicalLines(PathwayModel pathwayModel, Element root, Set<String> elementIdSet,
			Map<String, PublicationXref> idToPublicationXref, Map<String, Group> groupIdToGroup,
			Map<Element, LinePoint> elementToPoint) throws ConverterException {
		for (Element gln : root.getChildren("GraphicalLine", root.getNamespace())) {
			String elementId = readElementId("GraphicalLine", gln, elementIdSet);
			// instantiates graphical line and adds to pathway model
			GraphicalLine graphicalLine = new GraphicalLine();
			graphicalLine.setElementId(elementId);
			readPoints(graphicalLine, gln, elementIdSet, elementToPoint);
			pathwayModel.addGraphicalLine(graphicalLine);
			// reads line properties
			readLineElement(graphicalLine, gln, elementIdSet, idToPublicationXref, groupIdToGroup);
		}
	}

	/**
	 * Reads {@link LinePoint} elementRefs. ElementRefs must be read after the
	 * pathway elements they refer to. {@link Map} elementToPoint stores jdom
	 * elements for Points and their corresponding LinePoint pathway element so that
	 * elementRef information can be easily added.
	 * 
	 * NB: points refer to a group by its GraphId not GroupId(essentially
	 * elementId). If the pathway element referenced by a point is a group, we must
	 * search for the group by its GraphId in {@link Map} graphIdToGroup.
	 * 
	 * @param pathwayModel   the pathway model.
	 * @param graphIdToGroup the map of graphId to group.
	 * @param elementToPoint the map of jdom element to line points.
	 * @throws ConverterException
	 */
	protected void readPointElementRefs(PathwayModel pathwayModel, Map<String, Group> graphIdToGroup,
			Map<Element, LinePoint> elementToPoint) throws ConverterException {
		for (Element pt : elementToPoint.keySet()) {
			String base = pt.getParentElement().getParentElement().getName();
			String elementRefStr = getAttr(base + ".Graphics.Point", "GraphRef", pt);
			if (elementRefStr != null && !elementRefStr.equals("")) {
				// retrieves referenced pathway element (aside from group) by elementId
				LinkableTo elementRef = (LinkableTo) pathwayModel.getPathwayObject(elementRefStr);
				// if elementRef refers to a group, it cannot be found by elementId
				// find group by its graphId
				if (elementRef == null) {
					elementRef = graphIdToGroup.get(elementRefStr);
				}
				// sets elementRef, relX, and relY for point
				if (elementRef != null) {
					LinePoint point = elementToPoint.get(pt);
					double relX = Double.parseDouble(pt.getAttributeValue("RelX").trim());
					double relY = Double.parseDouble(pt.getAttributeValue("RelY").trim());
					point.linkTo(elementRef, relX, relY);
				}
			}
		}
	}

	/**
	 * Reads line element {@link LineElement} information for interaction or
	 * graphical line from jdom element.
	 * 
	 * NB: points are read by {@link #readPoints}.
	 * 
	 * @param lineElement         the line pathway element.
	 * @param ln                  the jdom line element.
	 * @param elementIdSet        the set of all elementIds.
	 * @param idToPublicationXref the map of id to biopax jdom element.
	 * @param groupIdToGroup      the map of groupId to group.
	 * @throws ConverterException
	 */
	protected void readLineElement(LineElement lineElement, Element ln, Set<String> elementIdSet,
			Map<String, PublicationXref> idToPublicationXref, Map<String, Group> groupIdToGroup)
			throws ConverterException {
		// reads comment group
		readCommentGroup(lineElement, ln, idToPublicationXref);
		// reads line style graphics
		Element gfx = ln.getChild("Graphics", ln.getNamespace());
		readLineStyleProperty(lineElement, gfx);
		// reads anchors
		readAnchors(lineElement, gfx, elementIdSet);
		// reads groupRef
		String groupRefStr = getAttr(ln.getName(), "GroupRef", ln);
		if (groupRefStr != null && !groupRefStr.equals("")) {
			// get parent group from groupRef
			Group groupRef = groupIdToGroup.get(groupRefStr);
			// sets groupRef for this line pathway element
			if (groupRef != null) {
				lineElement.setGroupRefTo(groupRef);
			}
		}
	}

	/**
	 * Reads points {@link LinePoint} for pathway model line pathway elements. Point
	 * elementRefs must be read after the pathway elements they refer to. Therefore
	 * points are read last in {@link #readPointElementRefs}.
	 * 
	 * @param lineElement    the line pathway element.
	 * @param ln             the jdom line element.
	 * @param elementIdSet   the set of all elementIds.
	 * @param elementToPoint the map of jdom element to line points.
	 * @throws ConverterException
	 */
	protected void readPoints(LineElement lineElement, Element ln, Set<String> elementIdSet,
			Map<Element, LinePoint> elementToPoint) throws ConverterException {
		List<LinePoint> ptList = new ArrayList<LinePoint>();
		String base = ln.getName();
		Element gfx = ln.getChild("Graphics", ln.getNamespace());
		List<Element> pts = gfx.getChildren("Point", gfx.getNamespace());
		for (int i = 0; i < pts.size(); i++) {
			Element pt = pts.get(i);
			String elementId = readElementId(base + ".Graphics.Point", pt, elementIdSet);
			// if start or end point, set arrowhead type for parent line element
			if (i == 0) {
				lineElement.setStartArrowHeadType(readArrowHeadType(pt, base));	
			} else if (i == pts.size() - 1) {
				lineElement.setEndArrowHeadType(readArrowHeadType(pt, base));	
			}
			double x = Double.parseDouble(getAttr(base + ".Graphics.Point", "X", pt).trim());
			double y = Double.parseDouble(getAttr(base + ".Graphics.Point", "Y", pt).trim());
			// instantiates and adds point
			LinePoint point = lineElement.new LinePoint(x, y);
			point.setElementId(elementId);
			ptList.add(point);
			// store info for reading
			elementToPoint.put(pt, point);
		}
		// set points for line
		lineElement.setLinePoints(ptList);
	}


	/**
	 * Returns the arrowHead for give jdom point element. 
	 * 
	 * @param pt the jdom point element. 
	 * @param base the string for either "Interaction" or "GraphicalLine"
	 * @return the arrowhead for given jdom point element.  
	 * @throws ConverterException
	 */
	protected ArrowHeadType readArrowHeadType(Element pt, String base) throws ConverterException {
		String arrowHeadStr = getAttr(base + ".Graphics.Point", "ArrowHead", pt);
		ArrowHeadType arrowHead = getInteractionPanelType(arrowHeadStr);
		if (arrowHead == null) {
			arrowHead = ArrowHeadType.register(arrowHeadStr);
		}
		return arrowHead;
	}
	
	
	/**
	 * Reads anchor {@link Anchor} information for line element from element.
	 * 
	 * @param lineElement  the line element object.
	 * @param gfx          the jdom graphics element.
	 * @param elementIdSet the set of all elementIds.
	 * @throws ConverterException
	 */
	protected void readAnchors(LineElement lineElement, Element gfx, Set<String> elementIdSet)
			throws ConverterException {
		String base = gfx.getParentElement().getName();
		for (Element an : gfx.getChildren("Anchor", gfx.getNamespace())) {
			String elementId = readElementId(base + ".Graphics.Anchor", an, elementIdSet);
			double position = Double.parseDouble(getAttr(base + ".Graphics.Anchor", "Position", an).trim());
			AnchorShapeType shapeType = AnchorShapeType.register(getAttr(base + ".Graphics.Anchor", "Shape", an));
			// adds anchor to line pathway element and pathway model
			lineElement.addAnchor(elementId, position, shapeType);
		}
	}

	/**
	 * Reads xref {@link Xref} information from element. In GPML2013a, Xref is
	 * required for DataNodes, Interactions, and optional for States.
	 * 
	 * @param e the element.
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
	 * @param shapedElement  the (shaped) pathway element.
	 * @param se             the jdom (shaped) pathway element.
	 * @param groupIdToGroup the map of groupId to group.
	 * @throws ConverterException
	 */
	protected void readGroupRef(ShapedElement shapedElement, Element se, Map<String, Group> groupIdToGroup)
			throws ConverterException {
		String groupRefStr = getAttr(se.getName(), "GroupRef", se);
		if (groupRefStr != null && !groupRefStr.equals("")) {
			// get parent group from groupRef
			Group groupRef = groupIdToGroup.get(groupRefStr);
			// sets groupRef for this shaped pathway element
			if (groupRef != null) {
				shapedElement.setGroupRefTo(groupRef);
			}
		}
	}

	/**
	 * Reads rect property information. Jdom handles schema default values.
	 * 
	 * @param shapedElement the shaped pathway element.
	 * @param gfx           the jdom graphics element.
	 * @throws ConverterException
	 */
	protected void readRectProperty(ShapedElement shapedElement, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		// if not state
		if (shapedElement.getClass() != State.class) {
			shapedElement.setCenterX(Double.parseDouble(getAttr(base + ".Graphics", "CenterX", gfx).trim()));
			shapedElement.setCenterY(Double.parseDouble(getAttr(base + ".Graphics", "CenterY", gfx).trim()));

		}
		shapedElement.setWidth(Double.parseDouble(getAttr(base + ".Graphics", "Width", gfx).trim()));
		shapedElement.setHeight(Double.parseDouble(getAttr(base + ".Graphics", "Height", gfx).trim()));
	}

	/**
	 * Reads font property information. Jdom handles schema default values.
	 * 
	 * NB: State has no font properties in GPML2013a, set basically default values.
	 * 
	 * @param shapedElement the shaped pathwayElement.
	 * @param gfx           the jdom graphics element.
	 * @throws ConverterException
	 */
	protected void readFontProperty(ShapedElement shapedElement, Element gfx) throws ConverterException {

		String base = ((Element) gfx.getParent()).getName();
		Color textColor = ColorUtils.stringToColor(getAttr(base + ".Graphics", "Color", gfx));
		String fontName = getAttr(base + ".Graphics", "FontName", gfx);
		String fontWeightStr = getAttr(base + ".Graphics", "FontWeight", gfx);
		String fontStyleStr = getAttr(base + ".Graphics", "FontStyle", gfx);
		String fontDecorationStr = getAttr(base + ".Graphics", "FontDecoration", gfx);
		String fontStrikethruStr = getAttr(base + ".Graphics", "FontStrikethru", gfx);
		int fontSize = Integer.parseInt(getAttr(base + ".Graphics", "FontSize", gfx).trim());
		HAlignType hAlignType = HAlignType.fromName(getAttr(base + ".Graphics", "Align", gfx));
		VAlignType vAlignType = VAlignType.fromName(getAttr(base + ".Graphics", "Valign", gfx));
		// set font properties
		shapedElement.setTextColor(textColor);
		shapedElement.setFontName(fontName);
		shapedElement.setFontWeight(fontWeightStr != null && fontWeightStr.equalsIgnoreCase("Bold"));
		shapedElement.setFontStyle(fontStyleStr != null && fontStyleStr.equalsIgnoreCase("Italic"));
		shapedElement.setFontDecoration(fontDecorationStr != null && fontDecorationStr.equalsIgnoreCase("Underline"));
		shapedElement.setFontStrikethru(fontStrikethruStr != null && fontStrikethruStr.equalsIgnoreCase("Strikethru"));
		shapedElement.setFontSize(fontSize);
		shapedElement.setHAlign(hAlignType);
		shapedElement.setVAlign(vAlignType);
	}

	/**
	 * Reads shape style property information. Jdom handles schema default values.
	 * If shape type is a key in {@link GPML2013aFormatAbstract#DEPRECATED_MAP},
	 * replaces deprecated shape type with newer value.
	 * <p>
	 * NB:
	 * <ol>
	 * <li>If pathway element has dynamic property key CellularComponentProperty,
	 * shape type is again replaced, this time with the dynamic property value in
	 * {@link #readDynamicProperties}.
	 * <li>If state pathway element has dynamic property key
	 * </ol>
	 * 
	 * @param shapedElement the shaped pathway element.
	 * @param gfx           the jdom graphics element.
	 * @throws ConverterException
	 */
	protected void readShapeStyleProperty(ShapedElement shapedElement, Element gfx) throws ConverterException {
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
		ShapeType shapeType = ShapeType.register(shapeTypeStr, null);
		// checks deprecated shape type map for newer shape
		if (DEPRECATED_MAP.containsKey(shapeType)) {
			shapeType = DEPRECATED_MAP.get(shapeType);
		}
		// set shape style properties
		shapedElement.setBorderColor(borderColor);
		shapedElement.setBorderStyle(borderStyle);
		shapedElement.setBorderWidth(borderWidth);
		shapedElement.setFillColor(fillColor);
		shapedElement.setShapeType(shapeType);
		String zOrder = getAttr(base + ".Graphics", "ZOrder", gfx);
		if (zOrder != null) {
			shapedElement.setZOrder(Integer.parseInt(zOrder.trim()));
		}
		// set rotation if shape
		if (shapedElement.getClass() == Shape.class) {
			double rotation = Double.parseDouble(getAttr("Shape.Graphics", "Rotation", gfx).trim());
			shapedElement.setRotation(rotation);
		}
	}

	/**
	 * Reads line style property information. Jdom handles schema default values.
	 * 
	 * @param lineElement the line pathway element.
	 * @param gfx         the jdom graphics element.
	 * @throws ConverterException
	 */
	protected void readLineStyleProperty(LineElement lineElement, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		Color lineColor = ColorUtils.stringToColor(getAttr(base + ".Graphics", "Color", gfx));
		String lineStyleStr = getAttr(base + ".Graphics", "LineStyle", gfx);
		// in GPML2021, "Broken" line style was renamed to "Dashed"
		if (lineStyleStr.equalsIgnoreCase("Broken"))
			lineStyleStr = "Dashed";
		LineStyleType lineStyle = LineStyleType.register(lineStyleStr);
		double lineWidth = Double.parseDouble(getAttr(base + ".Graphics", "LineThickness", gfx).trim());
		ConnectorType connectorType = ConnectorType.register(getAttr(base + ".Graphics", "ConnectorType", gfx));
		// set line style property
		lineElement.setLineColor(lineColor);
		lineElement.setLineStyle(lineStyle);
		lineElement.setLineWidth(lineWidth);
		lineElement.setConnectorType(connectorType);
		// sets optional property z-order
		String zOrder = getAttr(base + ".Graphics", "ZOrder", gfx);
		if (zOrder != null)
			lineElement.setZOrder(Integer.parseInt(zOrder.trim()));
	}

	/**
	 * Local class to help with reading gpml:Biopax:bp:PublicationXref.
	 * 
	 * @author finterly
	 */
	public class PublicationXref {

		Element element;
		String elementId;
		boolean hasRef = false; // true if this biopax has biopaxRef

		/**
		 * Instantiates a PublicationXref object. Stores the jdom
		 * gpml:Biopax:PublicationXref and the assigned elementId in
		 * {@link #readPublicationXrefMap}.
		 * 
		 * @param element   the jdom element.
		 * @param elementId the unique elementId, may or may not be the same string as
		 *                  the original biopax rdf:id.
		 */
		public PublicationXref(Element element, String elementId) {
			super();
			this.element = element;
			this.elementId = elementId;
		}

		/**
		 * Returns the jdom gpml:Biopax:PublicationXref element.
		 * 
		 * @return element the jdom PublicationXref element.
		 */
		public Element getElement() {
			return element;
		}

		/**
		 * Returns the assigned unique elementId for this PublicationXref.
		 * 
		 * @return elementId;
		 */
		public String getElementId() {
			return elementId;
		}

		/**
		 * Returns true if there is at least one gpml:CommentGroup:BiopaxRef which
		 * refers to this gpml:Biopax:PublicationXref, false otherwise.
		 * 
		 * @return true if referenced, false otherwise.
		 */
		public boolean getHasRef() {
			return hasRef;
		}

		/**
		 * Sets hasRef to true if at least one gpml:CommentGroup:BiopaxRef which refers
		 * to this gpml:Biopax:PublicationXref, false otherwise. Called in
		 * {@link readPublicationXrefs}.
		 * 
		 * @param hasRef the boolean for whether referenced.
		 */
		public void setHasRef(boolean hasRef) {
			this.hasRef = hasRef;
		}
	}

}
