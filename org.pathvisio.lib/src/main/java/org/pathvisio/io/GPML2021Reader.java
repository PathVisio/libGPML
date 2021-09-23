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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.pathvisio.model.Annotation;
import org.pathvisio.model.Citation;
import org.pathvisio.model.DataNode;
import org.pathvisio.model.DataNode.State;
import org.pathvisio.model.Evidence;
import org.pathvisio.model.GraphLink.LinkableTo;
import org.pathvisio.model.GraphicalLine;
import org.pathvisio.model.Group;
import org.pathvisio.model.Info.Annotatable;
import org.pathvisio.model.Info.Citable;
import org.pathvisio.model.Info.Evidenceable;
import org.pathvisio.model.Interaction;
import org.pathvisio.model.Label;
import org.pathvisio.model.LineElement;
import org.pathvisio.model.LineElement.Anchor;
import org.pathvisio.model.LineElement.LinePoint;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.Pathway.Author;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.PathwayElement.AnnotationRef;
import org.pathvisio.model.PathwayElement.CitationRef;
import org.pathvisio.model.PathwayElement.Comment;
import org.pathvisio.model.PathwayElement.EvidenceRef;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.Shape;
import org.pathvisio.model.ShapedElement;
import org.pathvisio.model.type.AnchorShapeType;
import org.pathvisio.model.type.AnnotationType;
import org.pathvisio.model.type.ArrowHeadType;
import org.pathvisio.model.type.ConnectorType;
import org.pathvisio.model.type.DataNodeType;
import org.pathvisio.model.type.GroupType;
import org.pathvisio.model.type.HAlignType;
import org.pathvisio.model.type.LineStyleType;
import org.pathvisio.model.type.ShapeType;
import org.pathvisio.model.type.StateType;
import org.pathvisio.model.type.VAlignType;
import org.pathvisio.util.ColorUtils;
import org.pathvisio.util.XrefUtils;

/**
 * This class reads a PathwayModel from an input source (GPML 2021).
 * 
 * @author finterly
 */
public class GPML2021Reader extends GPML2021FormatAbstract implements GpmlFormatReader {

	public static final GPML2021Reader GPML2021READER = new GPML2021Reader("GPML2021.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2021"));

	/**
	 * Constructor for GPML reader.
	 * 
	 * @param xsdFile the schema file.
	 * @param nsGPML  the GPML namespace.
	 */
	protected GPML2021Reader(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

	/**
	 * Reads information from root element of Jdom document {@link Document} to the
	 * pathway model {@link PathwayModel}.
	 * 
	 * NB: Order of reading is done in such as way that referenced elements are read
	 * first. Groups are read first as other pathway elements reference groupRef.
	 * Point and DataNode elementRef are read last to ensure the Pathway Elements
	 * referenced are already instantiated.
	 * 
	 * @param pathwayModel the given pathway model.
	 * @param root         the root element of given Jdom document.
	 * @return pathwayModel the pathway model after reading root element.
	 * @throws ConverterException
	 */
	public PathwayModel readFromRoot(PathwayModel pathwayModel, Element root) throws ConverterException {
		readPathway(pathwayModel.getPathway(), root);
		// reads annotation/citation/evidence ref info into a map
		Map<String, Element> refIdToJdomElement = new HashMap<String, Element>();
		readInfoMap(root, refIdToJdomElement);
		// reads pathway info
		readCommentGroup(pathwayModel, pathwayModel.getPathway(), root, refIdToJdomElement);
		// reads groups first
		readGroups(pathwayModel, root, refIdToJdomElement);
		readLabels(pathwayModel, root, refIdToJdomElement);
		readShapes(pathwayModel, root, refIdToJdomElement);
		readDataNodes(pathwayModel, root, refIdToJdomElement);
		readInteractions(pathwayModel, root, refIdToJdomElement);
		readGraphicalLines(pathwayModel, root, refIdToJdomElement);
		// reads aliasRefs and elementRefs last
		readDataNodeAliasRef(pathwayModel, root);
		readPointElementRef(pathwayModel, root);
		// removes empty groups
		removeEmptyGroups(pathwayModel);
		return pathwayModel;
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
		pathway.setTitle(root.getAttributeValue("title"));
		Element gfx = root.getChild("Graphics", root.getNamespace());
		pathway.setBoardWidth(Double.parseDouble(gfx.getAttributeValue("boardWidth").trim()));
		pathway.setBoardHeight(Double.parseDouble(gfx.getAttributeValue("boardHeight").trim()));
		pathway.setBackgroundColor(
				ColorUtils.stringToColor(gfx.getAttributeValue("backgroundColor", BACKGROUNDCOLOR_DEFAULT)));
		readAuthors(pathway, root);
		// sets optional properties
		Element desc = root.getChild("Description", root.getNamespace());
		if (desc != null) {
			String description = desc.getText();
			pathway.setDescription(description);
		}
		pathway.setXref(readXref(root));
		pathway.setOrganism(root.getAttributeValue("organism"));
		pathway.setSource(root.getAttributeValue("source"));
		pathway.setVersion(root.getAttributeValue("version"));
		pathway.setLicense(root.getAttributeValue("license"));
		return pathway;
	}

	/**
	 * Reads xref {@link Xref} information from element. Xref is required for
	 * DataNodes, Interactions, and Evidences. Xref is optional for the Pathway,
	 * States, Groups, and Annotations. Citations must have either Xref or Url, or
	 * both.
	 * 
	 * @param e the element.
	 * @return xref the new xref or null if no or invalid xref information.
	 * @throws ConverterException
	 */
	protected Xref readXref(Element e) throws ConverterException {
		Element xref = e.getChild("Xref", e.getNamespace());
		if (xref != null) {
			String identifier = xref.getAttributeValue("identifier");
			String dataSource = xref.getAttributeValue("dataSource");
			XrefUtils.createXref(identifier, dataSource);
			return XrefUtils.createXref(identifier, dataSource);
		}
		return null;
	}

	/**
	 * Reads Url link information from element. Url is optional for Annotations,
	 * Citations, and Evidences. Citations must have either Xref or Url, or both.
	 * 
	 * @param e the element.
	 * @return urlLink the link for the url.
	 * @throws ConverterException
	 */
	protected String readUrl(Element e) throws ConverterException {
		Element u = e.getChild("Url", e.getNamespace());
		if (u != null) {
			String urlLink = u.getAttributeValue("link");
			return urlLink;
		}
		return null;
	}

	/**
	 * Reads author {@link Author} information for pathway from root element.
	 * 
	 * @param pathway the pathway.
	 * @param root    the root element.
	 * @throws ConverterException
	 */
	protected void readAuthors(Pathway pathway, Element root) throws ConverterException {
		Element aus = root.getChild("Authors", root.getNamespace());
		if (aus != null) {
			for (Element au : aus.getChildren("Author", aus.getNamespace())) {
				String name = au.getAttributeValue("name");
				Author author = pathway.addAuthor(name);
				// sets optional properties
				String order = au.getAttributeValue("order");
				if (order != null)
					author.setOrder(Integer.parseInt(order.trim()));
				author.setUsername(au.getAttributeValue("username"));
				author.setXref(readXref(au));
			}
		}
	}

	// TODO
	/**
	 * @param root
	 * @param refIdToJdomElement
	 * @throws ConverterException
	 */
	protected void readInfoMap(Element root, Map<String, Element> refIdToJdomElement)
			throws ConverterException {
		List<String> refType = Collections.unmodifiableList(Arrays.asList("Annotation", "Citation", "Evidence"));
		for (int i = 0; i < refType.size(); i++) {
			Element refs = root.getChild(refType.get(i) + "s", root.getNamespace());
			if (refs != null) {
				for (Element ref : refs.getChildren(refType.get(i), root.getNamespace())) {
					String elementId = ref.getAttributeValue("elementId");
					refIdToJdomElement.put(elementId, ref);
				}
			}
		}
	}

	/**
	 * Reads {@link Annotation} and {@link AnnotationRef} information for an
	 * {@link Annotatable}. TODO
	 * 
	 * @param pathwayModel       the pathway model.
	 * @param annotatable        the pathway object which can have annotation.
	 * @param e                  the jdom element.
	 * @param refIdToJdomElement the map of ref elementId to jdom element
	 *                           (annotation, citation, or evidence).
	 * @throws ConverterException
	 */
	protected void readAnnotationRefs(PathwayModel pathwayModel, Annotatable annotatable, Element e,
			Map<String, Element> refIdToJdomElement) throws ConverterException {
		for (Element anntRef : e.getChildren("AnnotationRef", e.getNamespace())) {
			String elementRef = anntRef.getAttributeValue("elementRef");
			// if annotation already added, create and add annotationRef
			Annotation annotation = (Annotation) pathwayModel.getPathwayObject(elementRef);
			if (annotation != null) {
				AnnotationRef annotationRef = annotatable.addAnnotation(annotation);
				readCitationRefs(pathwayModel, annotationRef, anntRef, refIdToJdomElement);
				readEvidenceRefs(pathwayModel, annotationRef, anntRef, refIdToJdomElement);
			}
			// else if map contains refId, create and add annotation and annotationRef
			else if (refIdToJdomElement.containsKey(elementRef)) {
				Element annt = refIdToJdomElement.get(elementRef);
				String elementId = annt.getAttributeValue("elementId");
				String value = annt.getAttributeValue("value");
				AnnotationType type = AnnotationType.register(annt.getAttributeValue("type", ANNOTATIONTYPE_DEFAULT));
				Xref xref = readXref(annt);
				String urlLink = readUrl(annt);
				// annotation must have value and type, xref and urlLink optional
				if (xref != null || urlLink != null) {
					AnnotationRef annotationRef = annotatable.addAnnotation(elementId, value, type, xref, urlLink);
					readCitationRefs(pathwayModel, annotationRef, anntRef, refIdToJdomElement);
					readEvidenceRefs(pathwayModel, annotationRef, anntRef, refIdToJdomElement);
				}
			}
			// else invalid annotation and annotationRef
			else {
				throw new ConverterException("AnnotationRef refers to non-existent Annotation " + elementRef);
			}
		}
	}

	/**
	 * Reads {@link Citation} and {@link CitationRef} information for a
	 * {@link Citable}. TODO
	 * 
	 * @param pathwayModel       the pathway model.
	 * @param citable            the pathway object which can have citation.
	 * @param e                  the jdom element.
	 * @param refIdToJdomElement the map of ref elementId to jdom element
	 *                           (annotation, citation, or evidence).
	 * @throws ConverterException
	 */
	protected void readCitationRefs(PathwayModel pathwayModel, Citable citable, Element e,
			Map<String, Element> refIdToJdomElement) throws ConverterException {
		for (Element citRef : e.getChildren("CitationRef", e.getNamespace())) {
			String elementRef = citRef.getAttributeValue("elementRef");
			// if citation already added, create and add citationRef
			Citation citation = (Citation) pathwayModel.getPathwayObject(elementRef);
			if (citation != null) {
				CitationRef citationRef = citable.addCitation(citation);
				readAnnotationRefs(pathwayModel, citationRef, citRef, refIdToJdomElement);
			}
			// else if map contains refId, create and add citation and citationRef
			else if (refIdToJdomElement.containsKey(elementRef)) {
				Element cit = refIdToJdomElement.get(elementRef);
				String elementId = cit.getAttributeValue("elementId");
				Xref xref = readXref(cit);
				String urlLink = readUrl(cit);
				// citation must have xref or urlLink
				if (xref != null || urlLink != null) {
					CitationRef citationRef = citable.addCitation(elementId, xref, urlLink);
					readAnnotationRefs(pathwayModel, citationRef, citRef, refIdToJdomElement);
				}
			}
			// else invalid citation and citationRef
			else {
				throw new ConverterException("CitationRef refers to non-existent Citation " + elementRef);
			}
		}
	}

	/**
	 * Reads {@link Evidence} and {@link EvidenceRef} information for an
	 * {@link Evidenceable}. TODO
	 * 
	 * @param pathwayModel       the pathway model.
	 * @param evidenceable       the pathway object which can have evidence.
	 * @param e                  the jdom element.
	 * @param refIdToJdomElement the map of ref elementId to jdom element
	 *                           (annotation, citation, or evidence).
	 * @throws ConverterException
	 */
	protected void readEvidenceRefs(PathwayModel pathwayModel, Evidenceable evidenceable, Element e,
			Map<String, Element> refIdToJdomElement) throws ConverterException {
		for (Element evidRef : e.getChildren("EvidenceRef", e.getNamespace())) {
			String elementRef = evidRef.getAttributeValue("elementRef");
			// if evidence already added, create and add evidenceRef
			Evidence evidence = (Evidence) pathwayModel.getPathwayObject(elementRef);
			if (evidence != null) {
				evidenceable.addEvidence(evidence);
			}
			// else if map contains refId, create and add evidence and evidenceRef
			else if (refIdToJdomElement.containsKey(elementRef)) {
				Element evid = refIdToJdomElement.get(elementRef);
				String elementId = evid.getAttributeValue("elementId");
				Xref xref = readXref(evid);
				String urlLink = readUrl(evid);
				String value = evid.getAttributeValue("value");
				// evidence must have xref, value and urlLink optional
				if (xref != null || urlLink != null) {
					evidenceable.addEvidence(elementId, value, xref, urlLink);
				}
			}
			// else invalid evidence and evidenceRef
			else {
				throw new ConverterException("EvidenceRef refers to non-existent Evidence " + elementRef);
			}
		}
	}

	/**
	 * Reads comment group (comment, dynamic property, annotationRef, citationRef,
	 * evidenceRef) information for {@link PathwayElement} from jdom element.
	 * 
	 * @param pathwayElement the element info pathway element object.
	 * @param e              the jdom element to read.
	 * @throws ConverterException
	 */
	protected void readCommentGroup(PathwayModel pathwayModel, PathwayElement pathwayElement, Element e,
			Map<String, Element> refIdToJdomElement) throws ConverterException {
		readComments(pathwayElement, e);
		readDynamicProperties(pathwayElement, e);
		readAnnotationRefs(pathwayModel, pathwayElement, e, refIdToJdomElement);
		readCitationRefs(pathwayModel, pathwayElement, e, refIdToJdomElement);
		readEvidenceRefs(pathwayModel, pathwayElement, e, refIdToJdomElement);
	}

	/**
	 * Reads comment {@link Comment} information for pathway element from jdom
	 * element.
	 * 
	 * @param pathwayElement the pathway element.
	 * @param e              the jdom element to read from.
	 * @throws ConverterException
	 */
	protected void readComments(PathwayElement pathwayElement, Element e) throws ConverterException {
		for (Element cmt : e.getChildren("Comment", e.getNamespace())) {
			String source = cmt.getAttributeValue("source");
			String commentText = cmt.getText();
			// comment must have text
			if (commentText != null && !commentText.equals("")) {
				pathwayElement.addComment(commentText, source);
			}
		}
	}

	/**
	 * Reads dynamic property {@link PathwayElement#setDynamicProperty} information
	 * for pathway element from jdom element.
	 * 
	 * @param pathwayElement the the pathway element.
	 * @param e              the jdom element to read from.
	 * @throws ConverterException
	 */
	protected void readDynamicProperties(PathwayElement pathwayElement, Element e) throws ConverterException {
		for (Element dp : e.getChildren("Property", e.getNamespace())) {
			String key = dp.getAttributeValue("key");
			String value = dp.getAttributeValue("value");
			pathwayElement.setDynamicProperty(key, value);
		}
	}

	/**
	 * Reads group {@link Group} information for pathway model from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readGroups(PathwayModel pathwayModel, Element root, Map<String, Element> refIdToJdomElement)
			throws ConverterException {
		Element grps = root.getChild("Groups", root.getNamespace());
		if (grps != null) {
			for (Element grp : grps.getChildren("Group", grps.getNamespace())) {
				String elementId = grp.getAttributeValue("elementId");
				GroupType type = GroupType.register(grp.getAttributeValue("type", GROUPTYPE_DEFAULT));
				Group group = new Group(type);
				group.setElementId(elementId);
				pathwayModel.addGroup(group);
				// reads graphics and comment group props
				readShapedElement(pathwayModel, group, grp, refIdToJdomElement);
				// sets optional properties
				group.setXref(readXref(grp));
				group.setTextLabel(grp.getAttributeValue("textLabel"));
			}
			/**
			 * Because a group may refer to another group not yet initialized. We read all
			 * group elements before setting groupRef.
			 */
			for (Element grp : grps.getChildren("Group", grps.getNamespace())) {
				String groupRef = grp.getAttributeValue("groupRef");
				if (groupRef != null && !groupRef.equals("")) {
					String elementId = grp.getAttributeValue("elementId");
					Group group = (Group) pathwayModel.getPathwayObject(elementId);
					group.setGroupRefTo((Group) group.getPathwayModel().getPathwayObject(groupRef));
				}
			}
		}
	}

	/**
	 * Reads label {@link Label} information for pathway model from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readLabels(PathwayModel pathwayModel, Element root, Map<String, Element> refIdToJdomElement)
			throws ConverterException {
		Element lbs = root.getChild("Labels", root.getNamespace());
		if (lbs != null) {
			for (Element lb : lbs.getChildren("Label", lbs.getNamespace())) {
				String elementId = lb.getAttributeValue("elementId");
				String textLabel = lb.getAttributeValue("textLabel");
				Label label = new Label(textLabel);
				label.setElementId(elementId);
				pathwayModel.addLabel(label);
				// reads graphics and comment group props
				readShapedElement(pathwayModel, label, lb, refIdToJdomElement);
				// sets optional properties
				label.setHref(lb.getAttributeValue("href"));
				String groupRef = lb.getAttributeValue("groupRef");
				if (groupRef != null && !groupRef.equals(""))
					label.setGroupRefTo((Group) pathwayModel.getPathwayObject(groupRef));
			}
		}
	}

	/**
	 * Reads shape {@link Shape} information for pathway model from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readShapes(PathwayModel pathwayModel, Element root, Map<String, Element> refIdToJdomElement)
			throws ConverterException {
		Element shps = root.getChild("Shapes", root.getNamespace());
		if (shps != null) {
			for (Element shp : shps.getChildren("Shape", shps.getNamespace())) {
				String elementId = shp.getAttributeValue("elementId");
				Shape shape = new Shape();
				shape.setElementId(elementId);
				pathwayModel.addShape(shape);
				// reads graphics and comment group props
				readShapedElement(pathwayModel, shape, shp, refIdToJdomElement);
				// sets optional properties
				shape.setTextLabel(shp.getAttributeValue("textLabel"));
				String groupRef = shp.getAttributeValue("groupRef");
				if (groupRef != null && !groupRef.equals(""))
					shape.setGroupRefTo((Group) pathwayModel.getPathwayObject(groupRef));
			}
		}
	}

	/**
	 * Reads data node {@link DataNode} information for pathway model from root
	 * element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readDataNodes(PathwayModel pathwayModel, Element root, Map<String, Element> refIdToJdomElement)
			throws ConverterException {
		Element dns = root.getChild("DataNodes", root.getNamespace());
		if (dns != null) {
			for (Element dn : dns.getChildren("DataNode", dns.getNamespace())) {
				String elementId = dn.getAttributeValue("elementId");
				String textLabel = dn.getAttributeValue("textLabel");
				DataNodeType type = DataNodeType.register(dn.getAttributeValue("type", DATANODETYPE_DEFAULT));
				DataNode dataNode = new DataNode(textLabel, type);
				dataNode.setElementId(elementId);
				pathwayModel.addDataNode(dataNode);
				// reads graphics and comment group props
				readShapedElement(pathwayModel, dataNode, dn, refIdToJdomElement);
				// reads states
				readStates(pathwayModel, dataNode, dn, refIdToJdomElement);
				// sets optional properties
				dataNode.setXref(readXref(dn));
				String groupRef = dn.getAttributeValue("groupRef");
				if (groupRef != null && !groupRef.equals(""))
					dataNode.setGroupRefTo((Group) pathwayModel.getPathwayObject(groupRef));
			}
		}
	}

	/**
	 * Reads state {@link State} information for data node from element.
	 * 
	 * @param dataNode the data node object {@link DataNode}.
	 * @param dn       the data node element.
	 * @throws ConverterException
	 */
	protected void readStates(PathwayModel pathwayModel, DataNode dataNode, Element dn,
			Map<String, Element> refIdToJdomElement) throws ConverterException {
		Element sts = dn.getChild("States", dn.getNamespace());
		if (sts != null) {
			for (Element st : sts.getChildren("State", sts.getNamespace())) {
				String elementId = st.getAttributeValue("elementId");
				String textLabel = st.getAttributeValue("textLabel");
				StateType type = StateType.register(st.getAttributeValue("type", STATETYPE_DEFAULT));
				Element gfx = st.getChild("Graphics", st.getNamespace());
				double relX = Double.parseDouble(gfx.getAttributeValue("relX").trim());
				double relY = Double.parseDouble(gfx.getAttributeValue("relY").trim());
				// sets zOrder based on parent data node TODO
				State state = dataNode.addState(elementId, textLabel, type, relX, relY);
				// reads graphics and comment group props
				readShapedElement(pathwayModel, state, st, refIdToJdomElement);
				// sets optional properties
				state.setXref(readXref(st));
				state.setZOrder(dataNode.getZOrder() + 1);
			}
		}
	}

	/**
	 * Reads common properties for shaped pathway elements {@link ShapedElement}.
	 * 
	 * @param pathwayModel  the pathway model.
	 * @param shapedElement the shaped pathway element.
	 * @param se            the jdom (shaped) pathway element element.
	 * @throws ConverterException
	 */
	protected void readShapedElement(PathwayModel pathwayModel, ShapedElement shapedElement, Element se,
			Map<String, Element> refIdToJdomElement) throws ConverterException {
		Element gfx = se.getChild("Graphics", se.getNamespace());
		// reads graphics properties
		readRectProperty(shapedElement, gfx);
		readFontProperty(shapedElement, gfx);
		readShapeStyleProperty(shapedElement, gfx);
		// reads comment group, evidenceRefs
		readCommentGroup(pathwayModel, shapedElement, se, refIdToJdomElement);
	}

	/**
	 * Reads interaction {@link Interaction} information for pathway model from root
	 * element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readInteractions(PathwayModel pathwayModel, Element root, Map<String, Element> refIdToJdomElement)
			throws ConverterException {
		Element ias = root.getChild("Interactions", root.getNamespace());
		if (ias != null) {
			for (Element ia : ias.getChildren("Interaction", ias.getNamespace())) {
				String elementId = ia.getAttributeValue("elementId");
				Interaction interaction = new Interaction();
				interaction.setElementId(elementId);
				pathwayModel.addInteraction(interaction);
				// reads graphics, comment group, points, and anchors
				readLineElement(pathwayModel, interaction, ia, refIdToJdomElement);
				// sets optional properties
				interaction.setXref(readXref(ia));
			}
		}
	}

	/**
	 * Reads graphical line {@link GraphicalLine} information for pathway model from
	 * root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readGraphicalLines(PathwayModel pathwayModel, Element root, Map<String, Element> refIdToJdomElement)
			throws ConverterException {
		Element glns = root.getChild("GraphicalLines", root.getNamespace());
		if (glns != null) {
			for (Element gln : glns.getChildren("GraphicalLine", glns.getNamespace())) {
				String elementId = gln.getAttributeValue("elementId");
				GraphicalLine graphicalLine = new GraphicalLine();
				graphicalLine.setElementId(elementId);
				pathwayModel.addGraphicalLine(graphicalLine);
				// reads graphics, comment group, points, and anchors
				readLineElement(pathwayModel, graphicalLine, gln, refIdToJdomElement);
			}
		}
	}

	/**
	 * Reads line element {@link LineElement} information for interaction or
	 * graphical line from element.
	 * 
	 * @param lineElement the line element object.
	 * @param ln          the line element.
	 * @throws ConverterException
	 */
	protected void readLineElement(PathwayModel pathwayModel, LineElement lineElement, Element ln,
			Map<String, Element> refIdToJdomElement) throws ConverterException {
		// reads graphics
		Element gfx = ln.getChild("Graphics", ln.getNamespace());
		readLineStyleProperty(lineElement, gfx);
		// reads comment group
		readCommentGroup(pathwayModel, lineElement, ln, refIdToJdomElement);
		// reads points and anchors
		Element wyps = ln.getChild("Waypoints", ln.getNamespace());
		readPoints(lineElement, wyps);
		// checks if line has at least 2 point
		if (lineElement.getLinePoints().size() < 2) {
			throw new ConverterException("Line " + lineElement.getElementId() + " has "
					+ lineElement.getLinePoints().size() + " point(s),  must have at least 2.");
		}
		readAnchors(lineElement, wyps);
		// sets optional properties
		lineElement.setGroupRefTo((Group) pathwayModel.getPathwayObject(ln.getAttributeValue("groupRef")));
	}

	/**
	 * Reads point {@link LinePoint} information for line element from element.
	 * 
	 * @param lineElement the line element object.
	 * @param wyps        the waypoints element.
	 * @throws ConverterException
	 */
	protected void readPoints(LineElement lineElement, Element wyps)
			throws ConverterException {
		List<LinePoint> ptList = new ArrayList<LinePoint>();
		for (Element pt : wyps.getChildren("Point", wyps.getNamespace())) {
			String elementId = pt.getAttributeValue("elementId");
			ArrowHeadType arrowHead = ArrowHeadType.register(pt.getAttributeValue("arrowHead", ARROWHEAD_DEFAULT));
			double x = Double.parseDouble(pt.getAttributeValue("x").trim());
			double y = Double.parseDouble(pt.getAttributeValue("y").trim());
			LinePoint point = lineElement.new LinePoint(arrowHead, x, y);
			point.setElementId(elementId);
			ptList.add(point);
		}
		// adds points to line
		lineElement.setLinePoints(ptList);
	}

	/**
	 * Reads anchor {@link Anchor} information for line element from element.
	 * 
	 * @param lineElement the line element object.
	 * @param wyps        the waypoints element.
	 * @throws ConverterException
	 */
	protected void readAnchors(LineElement lineElement, Element wyps)
			throws ConverterException {
		for (Element an : wyps.getChildren("Anchor", wyps.getNamespace())) {
			String elementId = an.getAttributeValue("elementId");
			double position = Double.parseDouble(an.getAttributeValue("position"));
			AnchorShapeType shapeType = AnchorShapeType
					.register(an.getAttributeValue("shapeType", ANCHORSHAPETYPE_DEFAULT));
			lineElement.addAnchor(elementId, position, shapeType);
		}
	}

	/**
	 * Reads aliasRef {@link DataNode#setAliasRef} for pathway model datanodes.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readDataNodeAliasRef(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element dns = root.getChild("DataNodes", root.getNamespace());
		for (Element dn : dns.getChildren("DataNode", dns.getNamespace())) {
			String aliasRefStr = dn.getAttributeValue("aliasRef");
			if (aliasRefStr != null && !aliasRefStr.equals("")) {
				Group aliasRef = (Group) pathwayModel.getPathwayObject(aliasRefStr);
				if (aliasRef != null) {
					String elementId = dn.getAttributeValue("elementId");
					DataNode dataNode = (DataNode) pathwayModel.getPathwayObject(elementId);
					dataNode.setAliasRefTo(aliasRef);
				}
			}
		}
	}

	/**
	 * Reads elementRef {@link LinePoint#setElementRef} for pathway model points.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPointElementRef(PathwayModel pathwayModel, Element root) throws ConverterException {
		List<String> lnElementNames = Collections.unmodifiableList(Arrays.asList("Interactions", "GraphicalLines"));
		List<String> lnElementName = Collections.unmodifiableList(Arrays.asList("Interaction", "GraphicalLine"));
		for (int i = 0; i < lnElementNames.size(); i++) {
			Element ias = root.getChild(lnElementNames.get(i), root.getNamespace());
			if (ias != null) {
				for (Element ia : ias.getChildren(lnElementName.get(i), ias.getNamespace())) {
					Element wyps = ia.getChild("Waypoints", ia.getNamespace());
					for (Element pt : wyps.getChildren("Point", wyps.getNamespace())) {
						String elementRefStr = pt.getAttributeValue("elementRef");
						if (elementRefStr != null && !elementRefStr.equals("")) {
							LinkableTo elementRef = (LinkableTo) pathwayModel.getPathwayObject(elementRefStr);
							if (elementRef != null) {
								String elementId = pt.getAttributeValue("elementId");
								LinePoint point = (LinePoint) pathwayModel.getPathwayObject(elementId);
								point.setElementRef(elementRef);
								point.setRelX(Double.parseDouble(pt.getAttributeValue("relX").trim()));
								point.setRelY(Double.parseDouble(pt.getAttributeValue("relY").trim()));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Reads rect property information. Jdom handles schema default values.
	 * 
	 * @param shapedElement the shaped pathway element.
	 * @throws ConverterException
	 */
	protected void readRectProperty(ShapedElement shapedElement, Element gfx) throws ConverterException {
		if (shapedElement.getClass() != State.class) {
			double centerX = Double.parseDouble(gfx.getAttributeValue("centerX").trim());
			double centerY = Double.parseDouble(gfx.getAttributeValue("centerY").trim());
			shapedElement.setCenterX(centerX);
			shapedElement.setCenterY(centerY);

		}
		double width = Double.parseDouble(gfx.getAttributeValue("width").trim());
		double height = Double.parseDouble(gfx.getAttributeValue("height").trim());
		shapedElement.setWidth(width);
		shapedElement.setHeight(height);
	}

	/**
	 * Reads font property information. Jdom handles schema default values.
	 * 
	 * @param shapedElement the shaped pathway element.
	 * @throws ConverterException
	 */
	protected void readFontProperty(ShapedElement shapedElement, Element gfx) throws ConverterException {
		Color textColor = ColorUtils.stringToColor(gfx.getAttributeValue("textColor", TEXTCOLOR_DEFAULT));
		String fontName = gfx.getAttributeValue("fontName", FONTNAME_DEFAULT);
		boolean fontWeight = gfx.getAttributeValue("fontWeight", FONTWEIGHT_DEFAULT).equalsIgnoreCase("Bold");
		boolean fontStyle = gfx.getAttributeValue("fontStyle", FONTSTYLE_DEFAULT).equals("Italic");
		boolean fontDecoration = gfx.getAttributeValue("fontDecoration", FONTDECORATION_DEFAULT)
				.equalsIgnoreCase("Underline");
		boolean fontStrikethru = gfx.getAttributeValue("fontStrikethru", FONTSTRIKETHRU_DEFAULT)
				.equalsIgnoreCase("Strikethru");
		int fontSize = Integer.parseInt(gfx.getAttributeValue("fontSize", FONTSIZE_DEFAULT).trim());
		HAlignType hAlignType = HAlignType.fromName(gfx.getAttributeValue("hAlign", HALIGN_DEFAULT));
		VAlignType vAlignType = VAlignType.fromName(gfx.getAttributeValue("vAlign", VALIGN_DEFAULT));
		// set font props
		shapedElement.setTextColor(textColor);
		shapedElement.setFontName(fontName);
		shapedElement.setFontWeight(fontWeight);
		shapedElement.setFontStyle(fontStyle);
		shapedElement.setFontDecoration(fontDecoration);
		shapedElement.setFontStrikethru(fontStrikethru);
		shapedElement.setFontSize(fontSize);
		shapedElement.setHAlign(hAlignType);
		shapedElement.setVAlign(vAlignType);
	}

	/**
	 * Reads shape style property information. Jdom handles schema default values.
	 * 
	 * @param shapedElement the shaped pathway element.
	 * @throws ConverterException
	 */
	protected void readShapeStyleProperty(ShapedElement shapedElement, Element gfx) throws ConverterException {
		Color borderColor = ColorUtils.stringToColor(gfx.getAttributeValue("borderColor", BORDERCOLOR_DEFAULT));
		LineStyleType borderStyle = LineStyleType.register(gfx.getAttributeValue("borderStyle", BORDERSTYLE_DEFAULT));
		double borderWidth = Double.parseDouble(gfx.getAttributeValue("borderWidth", BORDERWIDTH_DEFAULT).trim());
		Color fillColor = ColorUtils.stringToColor(gfx.getAttributeValue("fillColor", FILLCOLOR_DEFAULT));
		ShapeType shapeType = ShapeType.register(gfx.getAttributeValue("shapeType", SHAPETYPE_DEFAULT));
		String zOrder = gfx.getAttributeValue("zOrder");
		String rotation = gfx.getAttributeValue("rotation");
		// set shape style props
		shapedElement.setBorderColor(borderColor);
		shapedElement.setBorderStyle(borderStyle);
		shapedElement.setBorderWidth(borderWidth);
		shapedElement.setFillColor(fillColor);
		shapedElement.setShapeType(shapeType);
		if (zOrder != null) {
			shapedElement.setZOrder(Integer.parseInt(zOrder.trim()));
		}
		if (rotation != null) {
			shapedElement.setRotation(Double.parseDouble(rotation.trim()));
		}
	}

	/**
	 * Reads line style property information. Jdom handles schema default values.
	 * 
	 * @param lineElement the line pathway element.
	 * @throws ConverterException
	 */
	protected void readLineStyleProperty(LineElement lineElement, Element gfx) throws ConverterException {
		Color lineColor = ColorUtils.stringToColor(gfx.getAttributeValue("lineColor", LINECOLOR_DEFAULT));
		LineStyleType lineStyle = LineStyleType.register(gfx.getAttributeValue("lineStyle", LINESTYLE_DEFAULT));
		double lineWidth = Double.parseDouble(gfx.getAttributeValue("lineWidth", LINEWIDTH_DEFAULT).trim());
		ConnectorType connectorType = ConnectorType
				.register(gfx.getAttributeValue("connectorType", CONNECTORTYPE_DEFAULT));
		String zOrder = gfx.getAttributeValue("zOrder");
		// set line style props
		lineElement.setLineColor(lineColor);
		lineElement.setLineStyle(lineStyle);
		lineElement.setLineWidth(lineWidth);
		lineElement.setConnectorType(connectorType);
		if (zOrder != null) {
			lineElement.setZOrder(Integer.parseInt(zOrder.trim()));
		}
	}

}
