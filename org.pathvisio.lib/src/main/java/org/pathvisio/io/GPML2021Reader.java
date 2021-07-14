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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.pathvisio.model.*;
import org.pathvisio.model.element.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.ref.Annotatable;
import org.pathvisio.model.ref.Annotation;
import org.pathvisio.model.ref.AnnotationRef;
import org.pathvisio.model.ref.Citable;
import org.pathvisio.model.ref.Citation;
import org.pathvisio.model.ref.CitationRef;
import org.pathvisio.model.ref.ElementInfo;
import org.pathvisio.model.ref.Evidence;
import org.pathvisio.model.ref.EvidenceRef;
import org.pathvisio.model.ref.Evidenceable;
import org.pathvisio.model.ref.UrlRef;
import org.pathvisio.model.type.*;
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
		Pathway pathway = readPathway(root);
		pathwayModel.setPathway(pathway);
		// reads before annotationRef, citationRef, evidenceRef
		readAnnotations(pathwayModel, root);
		readCitations(pathwayModel, root);
		readEvidences(pathwayModel, root);
		// reads pathway info
		readPathwayInfo(pathwayModel, root);
		// reads groups first
		readGroups(pathwayModel, root);
		readLabels(pathwayModel, root);
		readShapes(pathwayModel, root);
		readDataNodes(pathwayModel, root);
		readInteractions(pathwayModel, root);
		readGraphicalLines(pathwayModel, root);
		// reads elementRefs last
		readDataNodeElementRef(pathwayModel, root);
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
	protected Pathway readPathway(Element root) throws ConverterException {
		String title = root.getAttributeValue("title");
		Element gfx = root.getChild("Graphics", root.getNamespace());
		double boardWidth = Double.parseDouble(gfx.getAttributeValue("boardWidth").trim());
		double boardHeight = Double.parseDouble(gfx.getAttributeValue("boardHeight").trim());
		Color backgroundColor = ColorUtils
				.stringToColor(gfx.getAttributeValue("backgroundColor", BACKGROUNDCOLOR_DEFAULT));
		Coordinate infoBox = readInfoBox(root);
		Pathway pathway = new Pathway.PathwayBuilder(title, boardWidth, boardHeight, backgroundColor, infoBox).build();
		readAuthors(pathway, root);
		// sets optional properties
		Xref xref = readXref(root);
		Element desc = root.getChild("Description", root.getNamespace());
		if (desc != null) {
			String description = desc.getText();
			if (description != null)
				pathway.setDescription(description);
		}
		String organism = root.getAttributeValue("organism");
		String source = root.getAttributeValue("source");
		String version = root.getAttributeValue("version");
		String license = root.getAttributeValue("license");
		if (xref != null)
			pathway.setXref(xref);
		if (organism != null)
			pathway.setOrganism(organism);
		if (source != null)
			pathway.setSource(source);
		if (version != null)
			pathway.setVersion(version);
		if (license != null)
			pathway.setLicense(license);
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
	 * Reads Url {@link UrlRef} information from element. Url is optional for
	 * Annotations, Citations, and Evidences. Citations must have either Xref or
	 * Url, or both.
	 * 
	 * @param e the element.
	 * @return xref the new xref or null if no or invalid xref information.
	 * @throws ConverterException
	 */
	protected UrlRef readUrl(Element e) throws ConverterException {
		Element u = e.getChild("Url", e.getNamespace());
		if (u != null) {
			String link = u.getAttributeValue("link");
			String description = u.getAttributeValue("description");
			if (link != null && !link.equals("")) {
				UrlRef url = new UrlRef(link);
				// sets optional description
				if (description != null && !description.equals(""))
					url.setDescription(description);
				return url;
			}
		}
		return null;
	}

	/**
	 * Reads the infobox x and y coordinate {@link Pathway#setInfoBox} information.
	 * 
	 * @param root the root element.
	 * @return the infoBox as coordinates.
	 */
	protected Coordinate readInfoBox(Element root) {
		Element ifbx = root.getChild("InfoBox", root.getNamespace());
		double centerX = Double.parseDouble(ifbx.getAttributeValue("centerX").trim());
		double centerY = Double.parseDouble(ifbx.getAttributeValue("centerY").trim());
		return new Coordinate(centerX, centerY);
	}

	/**
	 * Reads author {@link Author} information for pathway from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readAuthors(Pathway pathway, Element root) throws ConverterException {
		Element aus = root.getChild("Authors", root.getNamespace());
		if (aus != null) {
			for (Element au : aus.getChildren("Author", aus.getNamespace())) {
				String name = au.getAttributeValue("name");
				Author author = new Author.AuthorBuilder(name).build();
				// sets optional properties
				String username = au.getAttributeValue("username");
				String order = au.getAttributeValue("order");
				Xref xref = readXref(au);
				if (username != null)
					author.setUsername(username);
				if (order != null)
					author.setOrder(Integer.parseInt(order.trim()));
				if (xref != null)
					author.setXref(xref);
				if (author != null)
					pathway.addAuthor(author);
			}
		}
	}

	/**
	 * Reads annotation {@link Annotation} information for pathway model from root
	 * element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readAnnotations(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element annts = root.getChild("Annotations", root.getNamespace());
		if (annts != null) {
			for (Element annt : annts.getChildren("Annotation", annts.getNamespace())) {
				String elementId = annt.getAttributeValue("elementId");
				String value = annt.getAttributeValue("value");
				AnnotationType type = AnnotationType.register(annt.getAttributeValue("type", ANNOTATIONTYPE_DEFAULT));
				Annotation annotation = new Annotation(value, type);
				annotation.setElementId(elementId);
				// sets optional properties
				Xref xref = readXref(annt);
				UrlRef url = readUrl(annt);
				if (xref != null)
					annotation.setXref(xref);
				if (url != null)
					annotation.setUrl(url);
				if (annotation != null)
					pathwayModel.addAnnotation(annotation);
			}
		}
	}

	/**
	 * Reads citation {@link Citation} information for pathway model from root
	 * element. A citation much have either xref or url, or both.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readCitations(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element cits = root.getChild("Citations", root.getNamespace());
		if (cits != null) {
			for (Element cit : cits.getChildren("Citation", cits.getNamespace())) {
				String elementId = cit.getAttributeValue("elementId");
				Xref xref = readXref(cit);
				UrlRef url = readUrl(cit);
				// citation has xref, and maybe also url
				if (xref != null) {
					Citation citation = new Citation(xref);
					citation.setElementId(elementId);
					if (url != null)
						citation.setUrl(url);
					if (citation != null)
						pathwayModel.addCitation(citation);
				} else {
					// citation has url
					if (url != null) {
						Citation citation = new Citation(url);
						pathwayModel.addCitation(citation);
					}
				}
			}
		}
	}

	/**
	 * Reads evidence {@link Evidence} information for pathway model from root
	 * element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readEvidences(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element evids = root.getChild("Evidences", root.getNamespace());
		if (evids != null) {
			for (Element evid : evids.getChildren("Evidence", evids.getNamespace())) {
				String elementId = evid.getAttributeValue("elementId");
				Xref xref = readXref(evid);
				Evidence evidence = new Evidence(xref);
				evidence.setElementId(elementId);
				// sets optional properties
				String value = evid.getAttributeValue("value");
				UrlRef url = readUrl(evid);
				if (value != null)
					evidence.setValue(value);
				if (url != null)
					evidence.setUrl(url);
				pathwayModel.addEvidence(evidence);
			}
		}
	}

	/**
	 * Reads comment group (comment, dynamic property, annotationRef, citationRef)
	 * and evidencRef information {@link PathwayModel} for pathway model from root
	 * element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayInfo(PathwayModel pathwayModel, Element root) throws ConverterException {
		Pathway pathway = pathwayModel.getPathway();
		readPathwayComments(pathway, root);
		readPathwayDynamicProperties(pathway, root);
		readAnnotationRefs(pathwayModel, pathway, root);
		readCitationRefs(pathwayModel, pathway, root);
		readEvidenceRefs(pathwayModel, pathway, root);
	}

	/**
	 * Reads comment {@link Comment} information for pathway from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayComments(Pathway pathway, Element root) throws ConverterException {
		for (Element cmt : root.getChildren("Comment", root.getNamespace())) {
			String source = cmt.getAttributeValue("source");
			String commentText = cmt.getText();
			// comment must have text
			if (commentText != null && !commentText.equals("")) {
				// instantiates comment
				Comment comment = new Comment(commentText);
				// sets source
				if (source != null && !source.equals(""))
					comment.setSource(source);
				// adds comment to pathway model
				pathway.addComment(new Comment(source, commentText));
			}
		}
	}

	/**
	 * Reads dynamic property {@link Pathway#setDynamicProperty} information for
	 * pathway from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayDynamicProperties(Pathway pathway, Element root) throws ConverterException {
		for (Element dp : root.getChildren("Property", root.getNamespace())) {
			String key = dp.getAttributeValue("key");
			String value = dp.getAttributeValue("value");
			pathway.setDynamicProperty(key, value);
		}
	}

	/**
	 * Reads annotationRefs {@link ElementInfo#addAnnotationRef} information for an
	 * annotatable from jdom element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param annotatable  the target pathway, pathway element, or citationRef for
	 *                     annotationRefs.
	 * @param e            the jdom element.
	 * @throws ConverterException
	 */
	protected void readAnnotationRefs(PathwayModel pathwayModel, Annotatable annotatable, Element e)
			throws ConverterException {
		for (Element anntRef : e.getChildren("AnnotationRef", e.getNamespace())) {
			Annotation annotation = (Annotation) pathwayModel
					.getPathwayElement(anntRef.getAttributeValue("elementRef"));
			AnnotationRef annotationRef = new AnnotationRef(annotation);
			readCitationRefs(pathwayModel, annotationRef, anntRef);
			readEvidenceRefs(pathwayModel, annotationRef, anntRef);
			annotatable.addAnnotationRef(annotationRef);
		}
	}

	/**
	 * Reads citationRefs {@link ElementInfo#addCitationRef} information for a
	 * citable from jdom element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param citable      the target pathway, pathway element, or annotationRef for
	 *                     citationRefs.
	 * @param e            the jdom element.
	 * @throws ConverterException
	 */
	protected void readCitationRefs(PathwayModel pathwayModel, Citable citable, Element e) throws ConverterException {
		for (Element citRef : e.getChildren("CitationRef", e.getNamespace())) {
			Citation citation = (Citation) pathwayModel.getPathwayElement(citRef.getAttributeValue("elementRef"));
			if (citation != null) {
				// create new citationRef for citation reference
				CitationRef citationRef = new CitationRef(citation);
				readAnnotationRefs(pathwayModel, citationRef, citRef);
				citable.addCitationRef(citationRef);
			}
		}
	}

	/**
	 * Reads evidenceRef {@link ElementInfo#addEvidenceRef} information for an
	 * evidenceable from jdom element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param evidenceable the target pathway, pathway element, or annotationRef for
	 *                     evidenceRefs.
	 * @param e            the jdom element.
	 * @throws ConverterException
	 */
	protected void readEvidenceRefs(PathwayModel pathwayModel, Evidenceable evidenceable, Element e)
			throws ConverterException {
		for (Element evidRef : e.getChildren("EvidenceRef", e.getNamespace())) {
			Evidence evidence = (Evidence) pathwayModel.getPathwayElement(evidRef.getAttributeValue("elementRef"));
			if (evidence != null) {
				// create new evidenceRef for evidence referenced
				EvidenceRef evidenceRef = new EvidenceRef(evidence, evidenceable);
				evidenceable.addEvidenceRef(evidenceRef);
			}
		}
	}

	/**
	 * Reads group {@link Group} information for pathway model from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readGroups(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element grps = root.getChild("Groups", root.getNamespace());
		if (grps != null) {
			for (Element grp : grps.getChildren("Group", grps.getNamespace())) {
				String elementId = grp.getAttributeValue("elementId");
				GroupType type = GroupType.register(grp.getAttributeValue("type", GROUPTYPE_DEFAULT));
				Element gfx = grp.getChild("Graphics", grp.getNamespace());
				RectProperty rectProperty = readRectProperty(gfx);
				FontProperty fontProperty = readFontProperty(gfx);
				ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
				Group group = new Group(rectProperty, fontProperty, shapeStyleProperty, type);
				group.setElementId(elementId);
				// reads comment group, evidenceRefs
				readElementInfo(pathwayModel, group, grp);
				// sets optional properties
				String textLabel = grp.getAttributeValue("textLabel");
				Xref xref = readXref(grp);
				if (xref != null)
					group.setXref(xref);
				if (textLabel != null)
					group.setTextLabel(textLabel);
				pathwayModel.addGroup(group);
			}
			/**
			 * Because a group may refer to another group not yet initialized. We read all
			 * group elements before setting groupRef.
			 */
			for (Element grp : grps.getChildren("Group", grps.getNamespace())) {
				String groupRef = grp.getAttributeValue("groupRef");
				if (groupRef != null && !groupRef.equals("")) {
					String elementId = grp.getAttributeValue("elementId");
					Group group = (Group) pathwayModel.getPathwayElement(elementId);
					group.setGroupRefTo((Group) group.getPathwayModel().getPathwayElement(groupRef));
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
	protected void readLabels(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element lbs = root.getChild("Labels", root.getNamespace());
		if (lbs != null) {
			for (Element lb : lbs.getChildren("Label", lbs.getNamespace())) {
				String elementId = lb.getAttributeValue("elementId");
				String textLabel = lb.getAttributeValue("textLabel");
				Element gfx = lb.getChild("Graphics", lb.getNamespace());
				RectProperty rectProperty = readRectProperty(gfx);
				FontProperty fontProperty = readFontProperty(gfx);
				ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
				Label label = new Label(rectProperty, fontProperty, shapeStyleProperty, textLabel);
				label.setElementId(elementId);
				// reads comment group, evidenceRefs
				readElementInfo(pathwayModel, label, lb);
				// sets optional properties
				String href = lb.getAttributeValue("href");
				String groupRef = lb.getAttributeValue("groupRef");
				if (href != null)
					label.setHref(href);
				if (groupRef != null && !groupRef.equals(""))
					label.setGroupRefTo((Group) pathwayModel.getPathwayElement(groupRef));
				pathwayModel.addLabel(label);
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
	protected void readShapes(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element shps = root.getChild("Shapes", root.getNamespace());
		if (shps != null) {
			for (Element shp : shps.getChildren("Shape", shps.getNamespace())) {
				String elementId = shp.getAttributeValue("elementId");
				Element gfx = shp.getChild("Graphics", shp.getNamespace());
				RectProperty rectProperty = readRectProperty(gfx);
				FontProperty fontProperty = readFontProperty(gfx);
				ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
				double rotation = Double.parseDouble(gfx.getAttributeValue("rotation").trim());
				Shape shape = new Shape(rectProperty, fontProperty, shapeStyleProperty, rotation);
				shape.setElementId(elementId);
				// reads comment group, evidenceRefs
				readElementInfo(pathwayModel, shape, shp);
				// sets optional properties
				String textLabel = shp.getAttributeValue("textLabel");
				String groupRef = shp.getAttributeValue("groupRef");
				if (textLabel != null)
					shape.setTextLabel(textLabel);
				if (groupRef != null && !groupRef.equals(""))
					shape.setGroupRefTo((Group) pathwayModel.getPathwayElement(groupRef));
				pathwayModel.addShape(shape);
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
	protected void readDataNodes(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element dns = root.getChild("DataNodes", root.getNamespace());
		if (dns != null) {
			for (Element dn : dns.getChildren("DataNode", dns.getNamespace())) {
				String elementId = dn.getAttributeValue("elementId");
				Element gfx = dn.getChild("Graphics", dn.getNamespace());
				RectProperty rectProperty = readRectProperty(gfx);
				FontProperty fontProperty = readFontProperty(gfx);
				ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
				String textLabel = dn.getAttributeValue("textLabel");
				DataNodeType type = DataNodeType.register(dn.getAttributeValue("type", DATANODETYPE_DEFAULT));
				DataNode dataNode = new DataNode(rectProperty, fontProperty, shapeStyleProperty, textLabel, type);
				dataNode.setElementId(elementId);
				// reads comment group, evidenceRefs
				readElementInfo(pathwayModel, dataNode, dn);
				// reads states
				readStates(pathwayModel, dataNode, dn);
				// sets optional properties
				String groupRef = dn.getAttributeValue("groupRef");
				Xref xref = readXref(dn);
				if (groupRef != null && !groupRef.equals(""))
					dataNode.setGroupRefTo((Group) pathwayModel.getPathwayElement(groupRef));
				if (xref != null)
					dataNode.setXref(xref);
				// adds dataNode to pathwayModel
				pathwayModel.addDataNode(dataNode);
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
	protected void readStates(PathwayModel pathwayModel, DataNode dataNode, Element dn) throws ConverterException {
		Element sts = dn.getChild("States", dn.getNamespace());
		if (sts != null) {
			for (Element st : sts.getChildren("State", sts.getNamespace())) {
				String elementId = st.getAttributeValue("elementId");
				String textLabel = st.getAttributeValue("textLabel");
				StateType type = StateType.register(st.getAttributeValue("type", STATETYPE_DEFAULT));
				Element gfx = st.getChild("Graphics", st.getNamespace());
				double relX = Double.parseDouble(gfx.getAttributeValue("relX").trim());
				double relY = Double.parseDouble(gfx.getAttributeValue("relY").trim());
				double width = Double.parseDouble(gfx.getAttributeValue("width").trim());
				double height = Double.parseDouble(gfx.getAttributeValue("height").trim());
				FontProperty fontProperty = readFontProperty(gfx);
				ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
				// sets zOrder based on parent data node TODO
				shapeStyleProperty.setZOrder(dataNode.getShapeStyleProperty().getZOrder() + 1);
				State state = new State(textLabel, type, relX, relY, width, height, fontProperty,
						shapeStyleProperty);
				state.setElementId(elementId);
				// reads comment group, evidenceRefs
				readElementInfo(pathwayModel, state, st);
				// sets optional properties
				Xref xref = readXref(st);
				if (xref != null)
					state.setXref(xref);
				dataNode.addState(state);
			}
		}
	}

	/**
	 * Reads interaction {@link Interaction} information for pathway model from root
	 * element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readInteractions(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element ias = root.getChild("Interactions", root.getNamespace());
		if (ias != null) {
			for (Element ia : ias.getChildren("Interaction", ias.getNamespace())) {
				String elementId = ia.getAttributeValue("elementId");
				Element gfx = ia.getChild("Graphics", ia.getNamespace());
				LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
				Interaction interaction = new Interaction(lineStyleProperty);
				interaction.setElementId(elementId);
				// add interaction to pathwayModel
				if (interaction != null)
					pathwayModel.addInteraction(interaction);
				// reads comment group
				readLineElement(pathwayModel, interaction, ia);
				// sets optional properties
				Xref xref = readXref(ia);
				if (xref != null)
					interaction.setXref(xref);

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
	protected void readGraphicalLines(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element glns = root.getChild("GraphicalLines", root.getNamespace());
		if (glns != null) {
			for (Element gln : glns.getChildren("GraphicalLine", glns.getNamespace())) {
				String elementId = gln.getAttributeValue("elementId");
				Element gfx = gln.getChild("Graphics", gln.getNamespace());
				LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
				GraphicalLine graphicalLine = new GraphicalLine(lineStyleProperty);
				graphicalLine.setElementId(elementId);
				// add graphicalLine to pathwayModel
				pathwayModel.addGraphicalLine(graphicalLine);
				readLineElement(pathwayModel, graphicalLine, gln);

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
	protected void readLineElement(PathwayModel pathwayModel, LineElement lineElement, Element ln)
			throws ConverterException {
		readElementInfo(pathwayModel, lineElement, ln); // comment group and evidenceRef
		Element wyps = ln.getChild("Waypoints", ln.getNamespace());
		readPoints(pathwayModel, lineElement, wyps);
		// checks if line has at least 2 point
		if (lineElement.getPoints().size() < 2) {
			throw new ConverterException("Line " + lineElement.getElementId() + " has " + lineElement.getPoints().size()
					+ " point(s),  must have at least 2.");
		}
		readAnchors(pathwayModel, lineElement, wyps);
		// sets optional properties
		String groupRef = ln.getAttributeValue("groupRef");
		if (groupRef != null && !groupRef.equals(""))
			lineElement.setGroupRefTo((Group) lineElement.getPathwayModel().getPathwayElement(groupRef));
	}

	/**
	 * Reads point {@link LinePoint} information for line element from element.
	 * 
	 * @param lineElement the line element object.
	 * @param wyps        the waypoints element.
	 * @throws ConverterException
	 */
	protected void readPoints(PathwayModel pathwayModel, LineElement lineElement, Element wyps)
			throws ConverterException {
		for (Element pt : wyps.getChildren("Point", wyps.getNamespace())) {
			String elementId = pt.getAttributeValue("elementId");
			ArrowHeadType arrowHead = ArrowHeadType.register(pt.getAttributeValue("arrowHead", ARROWHEAD_DEFAULT));
			Coordinate xy = new Coordinate(Double.parseDouble(pt.getAttributeValue("x").trim()),
					Double.parseDouble(pt.getAttributeValue("y").trim()));
			LinePoint point = new LinePoint(arrowHead, xy);
			point.setElementId(elementId);
			// adds point to lineElement (elementRef, relX, and relY read later)
			lineElement.addPoint(point);
		}
	}

	/**
	 * Reads anchor {@link Anchor} information for line element from element.
	 * 
	 * @param lineElement the line element object.
	 * @param wyps        the waypoints element.
	 * @throws ConverterException
	 */
	protected void readAnchors(PathwayModel pathwayModel, LineElement lineElement, Element wyps)
			throws ConverterException {
		for (Element an : wyps.getChildren("Anchor", wyps.getNamespace())) {
			String elementId = an.getAttributeValue("elementId");
			double position = Double.parseDouble(an.getAttributeValue("position"));
			AnchorShapeType shapeType = AnchorShapeType
					.register(an.getAttributeValue("shapeType", ANCHORSHAPETYPE_DEFAULT));
			Anchor anchor = new Anchor(position, shapeType);
			anchor.setElementId(elementId);
			lineElement.addAnchor(anchor);
		}
	}

	/**
	 * Reads elementRef {@link DataNode#setElementRef} for pathway model datanodes.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readDataNodeElementRef(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element dns = root.getChild("DataNodes", root.getNamespace());
		for (Element dn : dns.getChildren("DataNode", dns.getNamespace())) {
			String elementRefStr = dn.getAttributeValue("elementRef");
			if (elementRefStr != null && !elementRefStr.equals("")) {
				PathwayElement elementRef = pathwayModel.getPathwayElement(elementRefStr);
				if (elementRef != null) {
					String elementId = dn.getAttributeValue("elementId");
					DataNode dataNode = (DataNode) pathwayModel.getPathwayElement(elementId);
					dataNode.setElementRef(elementRef);
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
							PathwayElement elementRef = pathwayModel.getPathwayElement(elementRefStr);
							if (elementRef != null) {
								String elementId = pt.getAttributeValue("elementId");
								LinePoint point = (LinePoint) pathwayModel.getPathwayElement(elementId);
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
	 * Reads comment group (comment, dynamic property, annotationRef, citationRef)
	 * and elementRef {@link ElementInfo} information, , for pathway element from
	 * element.
	 * 
	 * @param elementInfo the element info pathway element object.
	 * @param e           the pathway element element.
	 * @throws ConverterException
	 */
	protected void readElementInfo(PathwayModel pathwayModel, ElementInfo elementInfo, Element e)
			throws ConverterException {
		readComments(elementInfo, e);
		readDynamicProperties(elementInfo, e);
		readAnnotationRefs(pathwayModel, elementInfo, e);
		readCitationRefs(pathwayModel, elementInfo, e);
		readEvidenceRefs(pathwayModel, elementInfo, e);
	}

	/**
	 * Reads comment {@link Comment} information for pathway element from element.
	 * 
	 * @param elementInfo the element info pathway element object.
	 * @param e           the pathway element element.
	 * @throws ConverterException
	 */
	protected void readComments(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element cmt : e.getChildren("Comment", e.getNamespace())) {
			String source = cmt.getAttributeValue("source");
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
	 * Reads dynamic property {@link ElementInfo#setDynamicProperty} information for
	 * pathway element from element.
	 * 
	 * @param elementInfo the element info pathway element object .
	 * @param e           the pathway element element.
	 * @throws ConverterException
	 */
	protected void readDynamicProperties(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element dp : e.getChildren("Property", e.getNamespace())) {
			String key = dp.getAttributeValue("key");
			String value = dp.getAttributeValue("value");
			elementInfo.setDynamicProperty(key, value);
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
		double centerX = Double.parseDouble(gfx.getAttributeValue("centerX").trim());
		double centerY = Double.parseDouble(gfx.getAttributeValue("centerY").trim());
		double width = Double.parseDouble(gfx.getAttributeValue("width").trim());
		double height = Double.parseDouble(gfx.getAttributeValue("height").trim());
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
		return new FontProperty(textColor, fontName, fontWeight, fontStyle, fontDecoration, fontStrikethru, fontSize,
				hAlignType, vAlignType);
	}

	/**
	 * Reads shape style property {@link ShapeStyleProperty} information. Jdom
	 * handles schema default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @returns the shapeStyleProperty object.
	 * @throws ConverterException
	 */
	protected ShapeStyleProperty readShapeStyleProperty(Element gfx) throws ConverterException {
		Color borderColor = ColorUtils.stringToColor(gfx.getAttributeValue("borderColor", BORDERCOLOR_DEFAULT));
		LineStyleType borderStyle = LineStyleType.register(gfx.getAttributeValue("borderStyle", BORDERSTYLE_DEFAULT));
		double borderWidth = Double.parseDouble(gfx.getAttributeValue("borderWidth", BORDERWIDTH_DEFAULT).trim());
		Color fillColor = ColorUtils.stringToColor(gfx.getAttributeValue("fillColor", FILLCOLOR_DEFAULT));
		ShapeType shapeType = ShapeType.register(gfx.getAttributeValue("shapeType", SHAPETYPE_DEFAULT));
		String zOrder = gfx.getAttributeValue("zOrder");
		ShapeStyleProperty shapeStyleProperty = new ShapeStyleProperty(borderColor, borderStyle, borderWidth, fillColor,
				shapeType);
		if (zOrder != null) {
			shapeStyleProperty.setZOrder(Integer.parseInt(zOrder.trim()));
		}
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
		Color lineColor = ColorUtils.stringToColor(gfx.getAttributeValue("lineColor", LINECOLOR_DEFAULT));
		LineStyleType lineStyle = LineStyleType.register(gfx.getAttributeValue("lineStyle", LINESTYLE_DEFAULT));
		double lineWidth = Double.parseDouble(gfx.getAttributeValue("lineWidth", LINEWIDTH_DEFAULT).trim());
		ConnectorType connectorType = ConnectorType
				.register(gfx.getAttributeValue("connectorType", CONNECTORTYPE_DEFAULT));
		String zOrder = gfx.getAttributeValue("zOrder");
		LineStyleProperty lineStyleProperty = new LineStyleProperty(lineColor, lineStyle, lineWidth, connectorType);
		if (zOrder != null) {
			lineStyleProperty.setZOrder(Integer.parseInt(zOrder.trim()));
		}
		return lineStyleProperty;
	}

}
