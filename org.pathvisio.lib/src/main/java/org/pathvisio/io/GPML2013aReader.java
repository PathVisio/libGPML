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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.ByteArrayInputStream;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.pathvisio.debug.Logger;
import org.pathvisio.io.*;
import org.pathvisio.model.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.type.*;

/**
 * This class reads a PathwayModel from an input source (GPML 2013a).
 * 
 * @author finterly
 */
public class GPML2013aReader extends GpmlFormatAbstract implements GpmlFormatReader {

	public static final GPML2013aReader GPML2013aREADER = new GPML2013aReader("GPML2013a.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2013a"));

	protected GPML2013aReader(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

	public final static String PATHWAY_AUTHOR = "pathway_author_gpml2013a";
	public final static String PATHWAY_MAINTAINER = "pathway_maintainer_gpml2013a";
	public final static String PATHWAY_EMAIL = "pathway_email_gpml2013a";
	public final static String PATHWAY_LASTMODIFIED = "pathway_lastModified_gpml2013a";
	public final static String LEGEND_CENTER_X = "pathway_legend_centerX_gpml2013a";
	public final static String LEGEND_CENTER_Y = "pathway_legend_centerY_gpml2013a";

	public final static String GROUP_GRAPHID = "group_graphId_gpml2013a";
	public final static String ATTRIBUTE_BIOPAXREF = "attribute_biopaxRef_gpml2013a";

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
	 * @returns pathwayModel the pathway model after reading root element.
	 * @throws ConverterException
	 */
	public PathwayModel readFromRoot(PathwayModel pathwayModel, Element root) throws ConverterException {

		Pathway pathway = readPathway(root);
		pathwayModel.setPathway(pathway); // TODO, should allow instantiate pathwayModel without Pathway???

		readAnnotations(pathwayModel, root);// TODO Should be biopax....
		readCitations(pathwayModel, root); //

		readPathwayInfo(pathwayModel, root);
		/* read groups first */
		readGroups(pathwayModel, root);
		readLabels(pathwayModel, root);
		readShapes(pathwayModel, root);
		readDataNodes(pathwayModel, root);
		readStates(pathwayModel, root); // state elementRef refers to parent DataNode
		readInteractions(pathwayModel, root);
		readGraphicalLines(pathwayModel, root);
		/* read elementRefs last */
		readDataNodeElementRef(pathwayModel, root);
		readPointElementRef(pathwayModel, root);

		Logger.log.trace("End reading gpml");

		// TODO check groups have at least one pathwayElement inside?
		// TODO check at least 2 points per line element?
		// TODO handle relative and absolute coordinates
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
		String title = root.getAttributeValue("Name");
		Element gfx = root.getChild("Graphics", root.getNamespace());
		double boardWidth = Double.parseDouble(gfx.getAttributeValue("BoardWidth"));
		double boardHeight = Double.parseDouble(gfx.getAttributeValue("BoardHeight"));
		Coordinate infoBox = readInfoBox(root);
		/* backgroundColor default is ffffff (white) */
		Pathway pathway = new Pathway.PathwayBuilder(title, boardWidth, boardHeight, Color.decode("#ffffff"), infoBox)
				.build();
		/* optional properties */
		String organism = root.getAttributeValue("Organism");
		String source = root.getAttributeValue("Data-Source");
		String version = root.getAttributeValue("Version");
		String license = root.getAttributeValue("License");
		if (organism != null)
			pathway.setOrganism(organism);
		if (source != null)
			pathway.setSource(source);
		if (version != null)
			pathway.setVersion(version);
		if (license != null)
			pathway.setLicense(license);
		/* optional dynamic properties */
		String author = root.getAttributeValue("Author");
		if (author != null) {
			pathway.setDynamicProperty(PATHWAY_AUTHOR, author);
		}
		String maintainer = root.getAttributeValue("Maintainer");
		if (maintainer != null) {
			pathway.setDynamicProperty(PATHWAY_MAINTAINER, maintainer);
		}
		String email = root.getAttributeValue("Email");
		if (email != null) {
			pathway.setDynamicProperty(PATHWAY_EMAIL, email);
		}
		String lastModified = root.getAttributeValue("Last-Modified");
		if (lastModified != null) {
			pathway.setDynamicProperty(PATHWAY_LASTMODIFIED, lastModified);
		}
		return pathway;
	}

	/**
	 * Reads the infobox x and y coordinate {@link Pathway#setInfoBox()}
	 * information.
	 * 
	 * @param root the root element.
	 * @return the infoBox as coordinates.
	 */
	protected Coordinate readInfoBox(Element root) {
		Element ifbx = root.getChild("InfoBox", root.getNamespace());
		double centerX = Double.parseDouble(ifbx.getAttributeValue("CenterX"));
		double centerY = Double.parseDouble(ifbx.getAttributeValue("CenterY"));
		return new Coordinate(centerX, centerY);
	}

	/**
	 * Reads the Legend CenterX and CenterY to pathway dynamic properties
	 * {@link Pathway#setDynamicProperty()} .
	 * 
	 * @param pathway the pathway.
	 * @param root    the root element.
	 * @return the infoBox as coordinates.
	 */
	protected void readLegend(Pathway pathway, Element root) {
		Element lgd = root.getChild("Legend", root.getNamespace());
		if (lgd != null) {
			// TODO will need to be able to handle legend as coordinates later
			String centerX = lgd.getAttributeValue("CenterX");
			pathway.setDynamicProperty(LEGEND_CENTER_X, centerX);
			String centerY = lgd.getAttributeValue("CenterY");
			pathway.setDynamicProperty(LEGEND_CENTER_Y, centerY);
		}
	}

	// TODO BIOPAX!!!

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
				AnnotationType type = AnnotationType.register(annt.getAttributeValue("type"));
				Annotation annotation = new Annotation(elementId, pathwayModel, value, type);
				/* optional properties */
				Xref xref = readXref(annt);
				String url = annt.getAttributeValue("url");
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
	 * element.
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
				Citation citation = new Citation(elementId, pathwayModel, xref);
				/* optional properties */
				String url = cit.getAttributeValue("url");
				if (url != null)
					citation.setUrl(url);
				if (citation != null)
					pathwayModel.addCitation(citation);
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
		readPathwayComments(pathwayModel, root);
		readPathwayPublicationXrefs(pathwayModel, root);
		readPathwayBiopaxRefs(pathwayModel, root);
		readPathwayAttributes(pathwayModel, root); // dynamic properties
	}

	/**
	 * Reads comment {@link Comment} information for pathway from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayComments(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element cmt : root.getChildren("Comment", root.getNamespace())) {
			String source = cmt.getAttributeValue("source");
			String content = cmt.getText();
			if (content != null && !content.equals("")) {
				Comment comment = new Comment(content); // TODO needs parent pathwayModel?
				if (source != null && !source.equals(""))
					comment.setSource(source);
				pathwayModel.getPathway().addComment(new Comment(source, content));
			}
		}
	}

	/**
	 * Reads gpml:PublicationXre or citation reference
	 * {@link Pathway#addCitationRef()} information for pathway model from root
	 * element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayPublicationXrefs(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element citRef : root.getChildren("CitationRef", root.getNamespace())) {
			Citation citationRef = (Citation) pathwayModel.getPathwayElement(citRef.getAttributeValue("elementRef"));
			if (citationRef != null)
				pathwayModel.getPathway().addCitationRef(citationRef);
		}
	}

	/**
	 * Reads gpml:BiopaxRef or annotation reference
	 * {@link Pathway#addAnnotationRef()} information for pathway from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayBiopaxRefs(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element anntRef : root.getChildren("AnnotationRef", root.getNamespace())) {
			Annotation annotation = (Annotation) pathwayModel
					.getPathwayElement(anntRef.getAttributeValue("elementRef"));
			AnnotationRef annotationRef = new AnnotationRef(annotation);
			for (Element citRef : anntRef.getChildren("CitationRef", anntRef.getNamespace())) {
				Citation citationRef = (Citation) pathwayModel
						.getPathwayElement(citRef.getAttributeValue("elementRef"));
				if (citationRef != null)
					annotationRef.addCitationRef(citationRef);
			}
			for (Element evidRef : anntRef.getChildren("EvidenceRef", anntRef.getNamespace())) {
				Evidence evidenceRef = (Evidence) pathwayModel
						.getPathwayElement(evidRef.getAttributeValue("elementRef"));
				if (evidenceRef != null)
					annotationRef.addEvidenceRef(evidenceRef);
			}
			pathwayModel.getPathway().addAnnotationRef(annotationRef);
		}
	}

	/**
	 * Reads gpml:Attribute or dynamic property {@link Pathway#setDynamicProperty()}
	 * information for pathway from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayAttributes(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element dp : root.getChildren("Attribute", root.getNamespace())) {
			String key = dp.getAttributeValue("Key");
			String value = dp.getAttributeValue("Value");
			pathwayModel.getPathway().setDynamicProperty(key, value);
		}
	}

	/**
	 * Reads group {@link Group} information for pathway model from root element.
	 * 
	 * TODO notes about groupId and graphId...
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readGroups(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element grp : root.getChildren("Group", root.getNamespace())) {
			/* for group, groupId is essentially elementId */
			String elementId = grp.getAttributeValue("GroupId");
			if (elementId == null) // TODO graphId is optional in GPML2013a
				elementId = pathwayModel.getUniqueElementId();
			GroupType type = GroupType.register(grp.getAttributeValue("Style"));
			Element gfx = grp.getChild("Graphics", grp.getNamespace());
			RectProperty rectProperty = new RectProperty(); // TODO Groups did not have these....
			FontProperty fontProperty = new FontProperty(); // TODO Set based on Type...?
			ShapeStyleProperty shapeStyleProperty = new ShapeStyleProperty(); // TODO
			Group group = new Group(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, type);
			/* read comment group, evidenceRefs */
			readElementInfo(group, grp);
			/* set optional properties */
			String textLabel = grp.getAttributeValue("TextLabel");
			/* for group, graphId is optional and referred to only by points */
			String graphId = grp.getAttributeValue("GraphId");
			String biopaxRef = grp.getAttributeValue("BiopaxRef");
			if (textLabel != null)
				group.setTextLabel(textLabel);
			if (graphId != null) {
				group.setDynamicProperty(GROUP_GRAPHID, graphId); // TODO handle graphId, make sure unique?
				pathwayModel.addElementId(graphId, group); // graphId key added for this group
			}
			if (biopaxRef != null)
				group.setDynamicProperty(ATTRIBUTE_BIOPAXREF, biopaxRef);
			if (group != null)
				pathwayModel.addGroup(group);
		}
		/**
		 * Because a group may refer to another group not yet initialized. We read all
		 * group elements before setting groupRef.
		 */
		for (Element grp : root.getChildren("Group", root.getNamespace())) {
			String groupRef = grp.getAttributeValue("GroupRef");
			if (groupRef != null && !groupRef.equals("")) {
				String elementId = grp.getAttributeValue("GroupId");
				Group group = (Group) pathwayModel.getPathwayElement(elementId);
				group.setGroupRef((Group) group.getPathwayModel().getPathwayElement(groupRef));
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
		for (Element lb : root.getChildren("Label", root.getNamespace())) {
			String elementId = lb.getAttributeValue("GraphId");
			if (elementId == null) // TODO graphId is optional in GPML2013a
				elementId = pathwayModel.getUniqueElementId();
			String textLabel = lb.getAttributeValue("TextLabel");
			Element gfx = lb.getChild("Graphics", lb.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			Label label = new Label(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, textLabel);
			/* read comment group, evidenceRefs */
			readShapedElement(label, lb);
			/* set optional properties */
			String href = lb.getAttributeValue("Href");
			String groupRef = lb.getAttributeValue("GroupRef");
			String biopaxRef = lb.getAttributeValue("BiopaxRef");
			if (href != null)
				label.setHref(href);
			if (groupRef != null && !groupRef.equals(""))
				label.setGroupRef((Group) label.getPathwayModel().getPathwayElement(groupRef));
			if (biopaxRef != null)
				label.setDynamicProperty(ATTRIBUTE_BIOPAXREF, biopaxRef);
			if (label != null)
				pathwayModel.addLabel(label);
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
		for (Element shp : root.getChildren("Shape", root.getNamespace())) {
			String elementId = shp.getAttributeValue("GraphId");
			Element gfx = shp.getChild("Graphics", shp.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			double rotation = Double.parseDouble(gfx.getAttributeValue("Rotation"));
			Shape shape = new Shape(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, rotation);
			/* read comment group, evidenceRefs */
			readShapedElement(shape, shp); // TODO handle dynamic properties....
			/* set optional properties */
			String textLabel = shp.getAttributeValue("TextLabel");
			String groupRef = shp.getAttributeValue("GroupRef");
			String biopaxRef = shp.getAttributeValue("BiopaxRef");
			if (textLabel != null)
				shape.setTextLabel(textLabel);
			if (groupRef != null && !groupRef.equals(""))
				shape.setGroupRef((Group) shape.getPathwayModel().getPathwayElement(groupRef));
			if (biopaxRef != null)
				shape.setDynamicProperty(ATTRIBUTE_BIOPAXREF, biopaxRef);
			if (shape != null)
				pathwayModel.addShape(shape);
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
		for (Element dn : root.getChildren("DataNode", root.getNamespace())) {
			String elementId = dn.getAttributeValue("GraphId");
			Element gfx = dn.getChild("Graphics", dn.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			String textLabel = dn.getAttributeValue("TextLabel");
			DataNodeType type = DataNodeType.register(dn.getAttributeValue("Type"));
			Xref xref = readXref(dn);
			DataNode dataNode = new DataNode(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty,
					textLabel, type, xref);
			/* read comment group, evidenceRefs */
			readShapedElement(dataNode, dn);
			/* set optional properties */
			String groupRef = dn.getAttributeValue("GroupRef");
			String biopaxRef = dn.getAttributeValue("BiopaxRef");
			if (groupRef != null && !groupRef.equals(""))
				dataNode.setGroupRef((Group) pathwayModel.getPathwayElement(groupRef));
			if (biopaxRef != null)
				dataNode.setDynamicProperty(ATTRIBUTE_BIOPAXREF, biopaxRef);
			if (dataNode != null)
				pathwayModel.addDataNode(dataNode);
		}
	}

	/**
	 * TODO should absolute x and y be calculated??? Reads state {@link State}
	 * information for pathway model from root element.
	 * 
	 * @param dataNode the data node object {@link DataNode}.
	 * @param dn       the data node element.
	 * @throws ConverterException
	 */
	protected void readStates(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element st : root.getChildren("State", root.getNamespace())) {
			String elementId = st.getAttributeValue("GraphId");
			String textLabel = st.getAttributeValue("TextLabel");
			StateType type = StateType.register(st.getAttributeValue("StateType"));
			Element gfx = st.getChild("Graphics", st.getNamespace());
			double relX = Double.parseDouble(gfx.getAttributeValue("RelX"));
			double relY = Double.parseDouble(gfx.getAttributeValue("RelY"));
			double width = Double.parseDouble(gfx.getAttributeValue("Width"));
			double height = Double.parseDouble(gfx.getAttributeValue("Height"));
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			/* find parent datanode from state elementRef */
			String elementRef = st.getAttributeValue("ElementRef");
			DataNode dataNode = (DataNode) pathwayModel.getPathwayElement(elementRef);
			/* finally instantiate state */
			State state = new State(elementId, pathwayModel, dataNode, textLabel, type, relX, relY, width, height,
					fontProperty, shapeStyleProperty);
			/* read comment group, evidenceRefs */
			readElementInfo(state, st);
			// TODO looks okay for now? 
			if ("Double".equals(state.getDynamicProperty("org.pathvisio.DoubleLineProperty"))) {
				state.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
			}
			/* set optional properties */
			Xref xref = readXref(st);
			if (xref != null)
				state.setXref(xref);
			if (state != null)
				dataNode.addState(state);
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
	protected void readShapedElement(ShapedElement shapedElement, Element se) throws ConverterException {
		readElementInfo(shapedElement, se); // comment group and evidenceRef
		// TODO looks okay for now? 
		if ("Double".equals(shapedElement.getDynamicProperty("org.pathvisio.DoubleLineProperty"))) {
			shapedElement.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
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
		for (Element ia : root.getChildren("Interaction", root.getNamespace())) {
			String elementId = ia.getAttributeValue("GraphId");
			Element gfx = ia.getChild("Graphics", ia.getNamespace());
			LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
			Xref xref = readXref(ia);
			Interaction interaction = new Interaction(elementId, pathwayModel, lineStyleProperty, xref);
			/* read comment group, evidenceRefs */
			readLineElement(interaction, ia);
			if (interaction != null) {
				if (interaction.getPoints().size() < 2) {
					System.out.println("Interaction elementId:" + elementId + "has" + interaction.getPoints().size()
							+ " points,  must have at least 2 points");// TODO error!
				}
				pathwayModel.addInteraction(interaction);
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
		for (Element gln : root.getChildren("GraphicaLine", root.getNamespace())) {
			String elementId = gln.getAttributeValue("GraphId");
			Element gfx = gln.getChild("Graphics", gln.getNamespace());
			LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
			GraphicalLine graphicalLine = new GraphicalLine(elementId, pathwayModel, lineStyleProperty);
			readLineElement(graphicalLine, gln);
			if (graphicalLine != null)
				if (graphicalLine.getPoints().size() < 2) {
					System.out.println("GraphicalLine elementId:" + elementId + "has" + graphicalLine.getPoints().size()
							+ " points,  must have at least 2 points");// TODO error!
				}
			pathwayModel.addGraphicalLine(graphicalLine);
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
	protected void readLineElement(LineElement lineElement, Element ln) throws ConverterException {
		readElementInfo(lineElement, ln); // comment group and evidenceRef
		// TODO looks okay for now? 
		if ("Double".equals(lineElement.getDynamicProperty("org.pathvisio.DoubleLineProperty"))) {
			lineElement.getLineStyleProperty().setLineStyle(LineStyleType.DOUBLE);
		}
		Element gfx = ln.getChild("Graphics", ln.getNamespace());
		readPoints(lineElement, gfx);
		readAnchors(lineElement, gfx);
		/* set optional properties */
		String groupRef = ln.getAttributeValue("GroupRef");
		if (groupRef != null && !groupRef.equals(""))
			lineElement.setGroupRef((Group) lineElement.getPathwayModel().getPathwayElement(groupRef));
	}

	/**
	 * Reads point {@link Point} information for line element from element.
	 * 
	 * @param lineElement the line element object.
	 * @param gfx         the graphics element.
	 * @throws ConverterException
	 */
	protected void readPoints(LineElement lineElement, Element gfx) throws ConverterException {
		for (Element pt : gfx.getChildren("Point", gfx.getNamespace())) {
			String elementId = pt.getAttributeValue("GraphId");
			ArrowHeadType arrowHead = ArrowHeadType.register(pt.getAttributeValue("ArrowHead"));
			Coordinate xy = new Coordinate(Double.parseDouble(pt.getAttributeValue("X")),
					Double.parseDouble(pt.getAttributeValue("Y")));
			Point point = new Point(elementId, lineElement.getPathwayModel(), arrowHead, xy);
			if (point != null) // set elementRef and optional properties later
				lineElement.addPoint(point);
		}
	}

	/**
	 * Reads anchor {@link Anchor} information for line element from element.
	 * 
	 * @param lineElement the line element object.
	 * @param gfx         the graphics element.
	 * @throws ConverterException
	 */
	protected void readAnchors(LineElement lineElement, Element gfx) throws ConverterException {
		for (Element an : gfx.getChildren("Anchor", gfx.getNamespace())) {
			String elementId = an.getAttributeValue("GraphId");
			double position = Double.parseDouble(an.getAttributeValue("Position"));
			Coordinate xy = new Coordinate(); // TODO calculate!!
			AnchorType shapeType = AnchorType.register(an.getAttributeValue("Shape"));
			Anchor anchor = new Anchor(elementId, lineElement.getPathwayModel(), position, xy, shapeType);
			if (anchor != null)
				lineElement.addAnchor(anchor);
		}
	}

	/**
	 * Reads elementRef {@link DataNode#setElementRef()} for pathway model
	 * datanodes.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readDataNodeElementRef(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element dns = root.getChild("DataNodes", root.getNamespace());
		for (Element dn : dns.getChildren("DataNode", dns.getNamespace())) {
			String elementRef = dn.getAttributeValue("GraphRef");
			if (elementRef != null && !elementRef.equals("")) {
				PathwayElement elemRf = pathwayModel.getPathwayElement(elementRef);
				if (elemRf != null) {
					String elementId = dn.getAttributeValue("GraphId");
					DataNode dataNode = (DataNode) pathwayModel.getPathwayElement(elementId);
					dataNode.setElementRef(elemRf);
				}
			}
		}
	}

	/**
	 * Reads elementRef {@link Point#setElementRef()} for pathway model points.
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
					Element gfx = ia.getChild("Graphics", ia.getNamespace());
					for (Element pt : gfx.getChildren("Point", gfx.getNamespace())) {
						String elementRef = pt.getAttributeValue("GraphRef");
						if (elementRef != null && !elementRef.equals("")) {
							PathwayElement elemRf = pathwayModel.getPathwayElement(elementRef);
							if (elemRf != null) {
								String elementId = pt.getAttributeValue("GraphId");
								Point point = (Point) pathwayModel.getPathwayElement(elementId);
								point.setElementRef(elemRf);
								point.setRelX(Double.parseDouble(pt.getAttributeValue("RelX")));
								point.setRelY(Double.parseDouble(pt.getAttributeValue("RelY")));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Reads xref {@link Xref} information from element. Xref is required for
	 * DataNodes, Interactions. Xref is optional for States.
	 * 
	 * @param e the element.
	 * @return xref the new xref or null if no or invalid xref information.
	 * @throws ConverterException
	 */
	protected Xref readXref(Element e) throws ConverterException {
		Element xref = e.getChild("Xref", e.getNamespace());
		if (xref != null) {
			String identifier = xref.getAttributeValue("ID");
			String dataSource = xref.getAttributeValue("Database");
			if (DataSource.fullNameExists(dataSource)) {
				return new Xref(identifier, DataSource.getExistingByFullName(dataSource));
			} else if (DataSource.systemCodeExists(dataSource)) {
				return new Xref(identifier, DataSource.getByAlias(dataSource));
			} else {
				System.out.println("Invalid xref dataSource: " + dataSource);
				return null; // TODO how to handle better
//			throw new IllegalArgumentException("Invalid xref dataSource: " + dataSource);
			}
		}
		return null;
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
	private void readElementInfo(ElementInfo elementInfo, Element e) throws ConverterException {
		readComments(elementInfo, e);
		readDynamicProperties(elementInfo, e);
		readAnnotationRefs(elementInfo, e);
		readCitationRefs(elementInfo, e);
		readEvidenceRefs(elementInfo, e);
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
			String content = cmt.getText();
			if (content != null && !content.equals("")) {
				Comment comment = new Comment(content); // TODO needs parent pathwayModel?
				if (source != null && !source.equals(""))
					comment.setSource(source);
				elementInfo.addComment(new Comment(source, content));
			}
		}
	}

	/**
	 * Reads dynamic property {@link ElementInfo#setDynamicProperty()} information
	 * for pathway element from element.
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
	 * Reads annotationRef {@link ElementInfo#addAnnotationRef()} information for
	 * pathway element from element.
	 * 
	 * @param elementInfo the element info pathway element object.
	 * @param e           the pathway element element.
	 * @throws ConverterException
	 */
	protected void readAnnotationRefs(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element anntRef : e.getChildren("AnnotationRef", e.getNamespace())) {
			Annotation annotation = (Annotation) elementInfo.getPathwayModel()
					.getPathwayElement(anntRef.getAttributeValue("elementRef"));
			AnnotationRef annotationRef = new AnnotationRef(annotation);
			for (Element citRef : anntRef.getChildren("CitationRef", anntRef.getNamespace())) {
				Citation citationRef = (Citation) elementInfo.getPathwayModel()
						.getPathwayElement(citRef.getAttributeValue("elementRef"));
				if (citationRef != null)
					annotationRef.addCitationRef(citationRef);
			}
			for (Element evidRef : anntRef.getChildren("EvidenceRef", anntRef.getNamespace())) {
				Evidence evidenceRef = (Evidence) elementInfo.getPathwayModel()
						.getPathwayElement(evidRef.getAttributeValue("elementRef"));
				if (evidenceRef != null)
					annotationRef.addEvidenceRef(evidenceRef);
			}
			elementInfo.addAnnotationRef(annotationRef);
		}
	}

	/**
	 * Reads citationRef {@link ElementInfo#addCitationRef()} information for
	 * pathway element from element.
	 * 
	 * @param elementInfo the element info pathway element object.
	 * @param e           the pathway element element.
	 * @throws ConverterException
	 */
	protected void readCitationRefs(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element citRef : e.getChildren("CitationRef", e.getNamespace())) {
			Citation citationRef = (Citation) elementInfo.getPathwayModel()
					.getPathwayElement(citRef.getAttributeValue("elementRef"));
			if (citationRef != null) {
				elementInfo.addCitationRef(citationRef);
			}
		}
	}

	/**
	 * Reads evidenceRef {@link ElementInfo#addEvidenceRef()} information for
	 * pathway element from element.
	 * 
	 * @param elementInfo the element info pathway element object.
	 * @param e           the pathway element element.
	 * @throws ConverterException
	 */
	protected void readEvidenceRefs(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element evidRef : e.getChildren("EvidenceRef", e.getNamespace())) {
			Evidence evidenceRef = (Evidence) elementInfo.getPathwayModel()
					.getPathwayElement(evidRef.getAttributeValue("elementRef"));
			if (evidenceRef != null)
				elementInfo.addEvidenceRef(evidenceRef);
		}
	}

	/**
	 * Reads rect property {@link RectProperty} information. Jdom handles schema
	 * default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @throws ConverterException
	 */
	protected RectProperty readRectProperty(Element gfx) throws ConverterException {
		double centerX = Double.parseDouble(gfx.getAttributeValue("CenterX"));
		double centerY = Double.parseDouble(gfx.getAttributeValue("CenterY"));
		double width = Double.parseDouble(gfx.getAttributeValue("Width"));
		double height = Double.parseDouble(gfx.getAttributeValue("Height"));
		return new RectProperty(new Coordinate(centerX, centerY), width, height);
	}

	/**
	 * Reads font property {@link FontProperty} information. Jdom handles schema
	 * default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @throws ConverterException
	 */
	protected FontProperty readFontProperty(Element gfx) throws ConverterException {
		Color textColor = ColorUtils.stringToColor(gfx.getAttributeValue("Color"));
		String fontName = gfx.getAttributeValue("FontName");
		boolean fontWeight = gfx.getAttributeValue("FontWeight").equals("Bold");
		boolean fontStyle = gfx.getAttributeValue("FontStyle").equals("Italic");
		boolean fontDecoration = gfx.getAttributeValue("FontDecoration").equals("Underline");
		boolean fontStrikethru = gfx.getAttributeValue("FontStrikethru").equals("Strikethru");
		int fontSize = Integer.parseInt(gfx.getAttributeValue("FontSize"));
		HAlignType hAlignType = HAlignType.fromName(gfx.getAttributeValue("Align"));
		VAlignType vAlignType = VAlignType.fromName(gfx.getAttributeValue("Valign"));
		return new FontProperty(textColor, fontName, fontWeight, fontStyle, fontDecoration, fontStrikethru, fontSize,
				hAlignType, vAlignType);
	}

	/**
	 * Reads shape style property {@link ShapeStyleProperty} information. Jdom
	 * handles schema default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @throws ConverterException
	 */
	protected ShapeStyleProperty readShapeStyleProperty(Element gfx) throws ConverterException {
		Color borderColor = ColorUtils.stringToColor(gfx.getAttributeValue("Color"));
		// TODO handle dynamic properties....
		LineStyleType borderStyle = LineStyleType.register(gfx.getAttributeValue("LineStyle"));
		double borderWidth = Double.parseDouble(gfx.getAttributeValue("LineThickness"));
		Color fillColor = ColorUtils.stringToColor(gfx.getAttributeValue("FillColor"));
		ShapeType shapeType = ShapeType.register(gfx.getAttributeValue("ShapeType"));
		String zOrder = gfx.getAttributeValue("ZOrder");
		// TODO handle dynamic properties....
		ShapeStyleProperty shapeStyleProperty = new ShapeStyleProperty(borderColor, borderStyle, borderWidth, fillColor,
				shapeType);
		if (zOrder != null) {
			shapeStyleProperty.setZOrder(Integer.parseInt(zOrder));
		}
		return shapeStyleProperty;
	}

	/**
	 * Reads line style property {@link LineStyleProperty} information. Jdom handles
	 * schema default values.
	 * 
	 * @paramt lb the parent line element.
	 * @param gfx the parent graphics element.
	 * @throws ConverterException
	 */
	protected LineStyleProperty readLineStyleProperty(Element gfx) throws ConverterException {
		Color lineColor = ColorUtils.stringToColor(gfx.getAttributeValue("Color"));
		// TODO handle dynamic properties....
		LineStyleType lineStyle = LineStyleType.register(gfx.getAttributeValue("LineStyle"));
		double lineWidth = Double.parseDouble(gfx.getAttributeValue("LineThickness"));
		ConnectorType connectorType = ConnectorType.register(gfx.getAttributeValue("ConnectorType"));
		String zOrder = gfx.getAttributeValue("ZOrder");
		LineStyleProperty lineStyleProperty = new LineStyleProperty(lineColor, lineStyle, lineWidth, connectorType);
		if (zOrder != null) {
			lineStyleProperty.setZOrder(Integer.parseInt(zOrder));
		}
		return lineStyleProperty;
	}

}
