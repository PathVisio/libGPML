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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.type.*;

/**
 * This class writes a PathwayModel to an output (GPML 2013a).
 * 
 * @author finterly
 */
public class GPML2013aWriter extends GpmlFormatAbstract implements GpmlFormatWriter {

	public static final GPML2013aWriter GPML2013aWRITER = new GPML2013aWriter("GPML2013a.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2013a"));

	// TODO copied from READER....
	public final static String PATHWAY_AUTHOR = "pathway_author_gpml2013a";
	public final static String PATHWAY_MAINTAINER = "pathway_maintainer_gpml2013a";
	public final static String PATHWAY_EMAIL = "pathway_email_gpml2013a";
	public final static String PATHWAY_LASTMODIFIED = "pathway_lastModified_gpml2013a";
	public final static String LEGEND_CENTER_X = "pathway_legend_centerX_gpml2013a";
	public final static String LEGEND_CENTER_Y = "pathway_legend_centerY_gpml2013a";

	public final static String GROUP_GRAPHID = "group_graphId_gpml2013a";
	public final static String ATTRIBUTE_BIOPAXREF = "attribute_biopaxRef_gpml2013a";

	protected GPML2013aWriter(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

	/**
	 * Writes the JDOM {@link Document} document to the outputstream specified.
	 * 
	 * @param out      the outputstream to which the JDOM document should be written
	 * @param validate if true, validate the dom structure before writing. If there
	 *                 is a validation error, or the xsd is not in the classpath, an
	 *                 exception will be thrown.
	 * @throws ConverterException
	 */
	public void writeToXml(PathwayModel pathwayModel, OutputStream output, boolean validate) throws ConverterException {

		Document doc = createJdom(pathwayModel);
		System.out.println(doc);

		if (validate)
			validateDocument(doc); // TODO Boolean validate not relevant to 2021...

		// Get the XML code
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		Format xmlformat = xmlOutput.getFormat();
		xmlformat.setEncoding("UTF-8");
		xmlformat.setTextMode(Format.TextMode.NORMALIZE);
		xmlOutput.setFormat(xmlformat);

		try {
			// Send XML code to the outputstream
			xmlOutput.output(doc, output); // new FileOutputStream(new File("fileName.gpml")
			// Create a new file and write XML to it
			System.out.println("Wrote pathway model to gpml file");
		} catch (IOException e) {
			throw new ConverterException(e);
		}

	}

	/**
	 * Writes the JDOM document to the file specified.
	 * 
	 * @param file     the file to which the JDOM document should be saved.
	 * @param validate if true, validate the dom structure before writing to file.
	 * @throws ConverterException
	 */
	public void writeToXml(PathwayModel pathwayModel, File file, boolean validate) throws ConverterException {
		OutputStream out;
		try {
			out = new FileOutputStream(file);
		} catch (IOException ex) {
			throw new ConverterException(ex);
		}
		writeToXml(pathwayModel, out, true);
	}

	/**
	 * Creates and returns the JDOM document {@link Document} written from given
	 * pathwayModel {@link PathwayModel} data.
	 * 
	 * @param pathwayModel the pathway model to be written.
	 * @throws ConverterException
	 */
	public Document createJdom(PathwayModel pathwayModel) throws ConverterException {
		Document doc = new Document();
		Element root = new Element("Pathway", getGpmlNamespace());
		doc.setRootElement(root);

		if (root != null) {
			writePathwayInfo(pathwayModel, root);

			writeDataNodes(pathwayModel.getDataNodes(), root);
			writeStates(pathwayModel, root);
			writeInteractions(pathwayModel.getInteractions(), root);
			writeGraphicalLines(pathwayModel.getGraphicalLines(), root);
			writeLabels(pathwayModel.getLabels(), root);
			writeShapes(pathwayModel.getShapes(), root);
			writeGroups(pathwayModel.getGroups(), root);

			writeInfoBox(pathwayModel.getPathway().getInfoBox(), root);
			writeLegend(pathwayModel.getPathway(), root);

//			writeAnnotations(pathwayModel.getAnnotations(), root);
//			writeCitations(pathwayModel.getCitations(), root);
//			writeEvidences(pathwayModel.getEvidences(), root);
//			<xsd:element ref="gpml:Biopax" minOccurs="0" maxOccurs="1"/>
		}
		return doc;
	}

	/**
	 * Writes pathway object {@link Pathway} information to root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void writePathwayInfo(PathwayModel pathwayModel, Element root) throws ConverterException {
		Pathway pathway = pathwayModel.getPathway();
		root.setAttribute("Name", pathway.getTitle());
		/* set optional properties, in the order written in gpml 2013a */
		String source = pathway.getSource();
		String version = pathway.getVersion();
		String author = pathway.getDynamicProperty(PATHWAY_AUTHOR);
		String maintainer = pathway.getDynamicProperty(PATHWAY_MAINTAINER);
		String email = pathway.getDynamicProperty(PATHWAY_EMAIL);
		String lastModified = pathway.getDynamicProperty(PATHWAY_LASTMODIFIED);
		String organism = pathway.getOrganism();
		String license = pathway.getLicense();
		if (source != null)
			root.setAttribute("Data-Source", source);
		if (version != null)
			root.setAttribute("Version", version);
		if (author != null)
			root.setAttribute("Author", author);
		if (maintainer != null)
			root.setAttribute("Maintainer", maintainer);
		if (email != null)
			root.setAttribute("Email", email);
		if (lastModified != null)
			root.setAttribute("Last-Modified", lastModified);
		if (organism != null)
			root.setAttribute("Organism", organism);
		if (license != null)
			root.setAttribute("License", license);
		
		/* set comment group */
		writeComments(pathway.getComments(), root);
		// TODO BiopaxRef
		// TODO PublicationRef
		writeAttributes(pathway.getDynamicProperties(), root);
		
		/* set graphics */
		Element gfx = new Element("Graphics", root.getNamespace());
		root.addContent(gfx);
		gfx.setAttribute("BoardWidth", String.valueOf(pathway.getBoardWidth()));
		gfx.setAttribute("BoardHeight", String.valueOf(pathway.getBoardHeight()));


//		result.put("Pathway@BiopaxRef", new AttributeInfo ("xsd:string", null, "optional"));

	}

	/**
	 * Writes xref {@link Xref} information to new element. Xref is required for
	 * DataNodes, Interactions, Citations and Evidences. Xref is optional for the
	 * Pathway, States, Groups, and Annotations.
	 * 
	 * @param xref     the xref of the pathway or pathway element.
	 * @param e        the parent element.
	 * @param required if true, xref is a required property.
	 */
	protected void writeXref(Xref xref, Element e, boolean required) { // TODO boolean required
		if (xref == null && required) {
			Element xrf = new Element("Xref", e.getNamespace());
			xrf.setAttribute("identifier", "");
			xrf.setAttribute("dataSource", ""); // TODO null handling
			e.addContent(xrf);
		}
		if (xref != null) {
			String identifier = xref.getId();
			DataSource dataSrc = xref.getDataSource();
			if (dataSrc != null && identifier != null || required) {
				Element xrf = new Element("Xref", e.getNamespace());
				String dataSource = xref.getDataSource().getFullName(); // TODO dataSource
				xrf.setAttribute("identifier", identifier == null ? "" : identifier);
				xrf.setAttribute("dataSource", dataSource == null ? "" : dataSource); // TODO null handling
				e.addContent(xrf);
			}
		}
	}

	/**
	 * Writes comments {@link Comment} information for pathway or pathway element.
	 * 
	 * @param comments the list of comments of pathway or pathway element.
	 * @param e        the parent element.
	 * @throws ConverterException
	 */
	protected void writeComments(List<Comment> comments, Element e) throws ConverterException {
		for (Comment comment : comments) {
			Element cmt = new Element("Comment", e.getNamespace());
			if (comment.getContent() != null) // TODO may be excessive
				cmt.setText(comment.getContent());
			if (comment.getSource() != null)
				cmt.setAttribute("source", comment.getSource());
			if (cmt != null)
				e.addContent(cmt);
		}
	}

	/**
	 * Writes dynamic property information for pathway or pathway element.
	 * {@link Pathway#getDynamicProperty() , ElementInfo#getDynamicProperty()}
	 * 
	 * @param dynamicProperties the list of dynamic properties.
	 * @param e                 the parent element.
	 * @throws ConverterException
	 */
	protected void writeAttributes(Map<String, String> dynamicProperties, Element e) throws ConverterException {
		for (String key : dynamicProperties.keySet()) {
			Element dp = new Element("Property", e.getNamespace());
			dp.setAttribute("key", key);
			dp.setAttribute("value", dynamicProperties.get(key));
			if (dp != null)
				e.addContent(dp);
		}
	}

	// TODO
	protected void writeBiopaxRefs(List<AnnotationRef> annotationRefs, Element e) throws ConverterException {
		for (AnnotationRef annotationRef : annotationRefs) {
			Element anntRef = new Element("AnnotationRef", e.getNamespace());
			anntRef.setAttribute("elementRef", annotationRef.getAnnotation().getElementId());
			for (Citation citationRef : annotationRef.getCitationRefs()) {
				Element citRef = new Element("CitationRef", e.getNamespace());
				citRef.setAttribute("elementRef", citationRef.getElementId());
			}
			for (Evidence evidence : annotationRef.getEvidenceRefs()) {
				Element evidRef = new Element("EvidenceRef", e.getNamespace());
				evidRef.setAttribute("elementRef", evidence.getElementId());
			}
			if (anntRef != null)
				e.addContent(anntRef);
		}
	}

	/**
	 * TODO
	 */
	protected void writePublicationXrefs(List<Citation> citationRefs, Element e) throws ConverterException {
		if (e != null) {
			for (Citation citationRef : citationRefs) {
				Element citRef = new Element("CitationRef", e.getNamespace());
				citRef.setAttribute("elementRef", citationRef.getElementId());
				if (citRef != null)
					e.addContent(citRef);
			}
		}
	}

	/**
	 * Writes the infobox x and y coordinate {@link Pathway#getInfoBox()}
	 * information.
	 * 
	 * @param infoBox the infobox xy coordinates.
	 * @param root    the root element.
	 */
	protected void writeInfoBox(Coordinate infoBox, Element root) {
		Element ifb = new Element("InfoBox", root.getNamespace());
		ifb.setAttribute("centerX", Double.toString(infoBox.getX()));
		ifb.setAttribute("centerY", Double.toString(infoBox.getY()));
		root.addContent(ifb);
	}

	/**
	 * Writes the legend x and y coordinate information.
	 * 
	 * @param pathway the pathway.
	 * @param root    the root element.
	 */
	protected void writeLegend(Pathway pathway, Element root) {
		Element lgd = new Element("Legend", root.getNamespace());
		lgd.setAttribute("centerX", pathway.getDynamicProperty(LEGEND_CENTER_X));
		lgd.setAttribute("centerY", pathway.getDynamicProperty(LEGEND_CENTER_Y));
		root.addContent(lgd);
	}

	/**
	 * Writes datanode {@link DataNode} information.
	 * 
	 * @param dataNodes the list of datanodes.
	 * @param root      the root element.
	 * @throws ConverterException
	 */
	protected void writeDataNodes(List<DataNode> dataNodes, Element root) throws ConverterException {
		if (!dataNodes.isEmpty()) {
			Element dns = new Element("DataNodes", root.getNamespace());
			List<Element> dnList = new ArrayList<Element>();
			for (DataNode dataNode : dataNodes) {
				if (dataNode == null)
					continue;
				Element dn = new Element("DataNode", root.getNamespace());
				writeXref(dataNode.getXref(), dn, true);
				writeStates(dataNode.getStates(), dn);
				writeShapedElement(dataNode, dn);
				writeElementRef(dataNode.getElementRef(), dn);
				dn.setAttribute("textLabel", dataNode.getTextLabel());
				dn.setAttribute("type", dataNode.getType().getName());
				writeGroupRef(dataNode.getGroupRef(), dn); // TODO location

				if (dn != null) {
					dnList.add(dn);
				}
			}
			if (dnList != null && dnList.isEmpty() == false) {
				dns.addContent(dnList);
				root.addContent(dns);
			}
		}
	}

	/**
	 * Writes state {@link State} information.
	 * 
	 * @param states the list of states.
	 * @param dn     the parent data node element.
	 * @throws ConverterException
	 */
	protected void writeStates(List<State> states, Element dn) throws ConverterException {
		if (!states.isEmpty()) {
			Element sts = new Element("States", dn.getNamespace());
			List<Element> stList = new ArrayList<Element>();
			for (State state : states) {
				if (state == null)
					continue;
				Element st = new Element("State", dn.getNamespace());
				writeXref(state.getXref(), st, false);

				Element gfx = new Element("Graphics", st.getNamespace());
				st.addContent(gfx);
				gfx.setAttribute("RelX", Double.toString(state.getRelX()));
				gfx.setAttribute("RelY", Double.toString(state.getRelY()));
				gfx.setAttribute("Width", Double.toString(state.getWidth()));
				gfx.setAttribute("Height", Double.toString(state.getHeight()));
				writeFontProperty(state.getFontProperty(), gfx);
				writeShapeStyleProperty(state.getShapeStyleProperty(), gfx);

				writeElementInfo(state, st);
				st.setAttribute("textLabel", state.getTextLabel() == null ? "" : state.getTextLabel());
				st.setAttribute("type", state.getType().getName());

				if (st != null) {
					stList.add(st);
				}
			}
			if (stList != null && stList.isEmpty() == false) {
				sts.addContent(stList);
				dn.addContent(sts);
			}
		}
	}

	/**
	 * Writes interaction {@link Interaction} information.
	 * 
	 * @param interactions the list of interactions.
	 * @param root         the root element;
	 * @throws ConverterException
	 */
	protected void writeInteractions(List<Interaction> interactions, Element root) throws ConverterException {
		if (!interactions.isEmpty()) {
			Element ias = new Element("Interactions", root.getNamespace());
			List<Element> iaList = new ArrayList<Element>();
			for (Interaction interaction : interactions) {
				if (interaction == null)
					continue;
				Element ia = new Element("Interaction", root.getNamespace());
				writeXref(interaction.getXref(), ia, true);
				writeLineElement(interaction, ia);
				if (ia != null) {
					iaList.add(ia);
				}
			}
			if (iaList != null && iaList.isEmpty() == false) {
				ias.addContent(iaList);
				root.addContent(ias);
			}
		}
	}

	/**
	 * Writes graphical line {@link GraphicalLine} information.
	 * 
	 * @param graphicalLines the list of graphical lines.
	 * @param root           the root element.
	 * @throws ConverterException
	 */
	protected void writeGraphicalLines(List<GraphicalLine> graphicalLines, Element root) throws ConverterException {
		if (!graphicalLines.isEmpty()) {
			Element glns = new Element("GraphicalLines", root.getNamespace());
			List<Element> glnList = new ArrayList<Element>();
			for (GraphicalLine graphicalLine : graphicalLines) {
				if (graphicalLine == null)
					continue;
				Element gln = new Element("GraphicalLine", root.getNamespace());
				writeLineElement(graphicalLine, gln);
				if (gln != null) {
					glnList.add(gln);
				}
			}
			if (glnList != null && glnList.isEmpty() == false) {
				glns.addContent(glnList);
				root.addContent(glns);
			}
		}
	}

	/**
	 * Writes line element {@link LineElement} information for interactions or
	 * graphicalLines.
	 * 
	 * @param lineElement the interaction or graphicalLine.
	 * @param ln          the line element.
	 * @throws ConverterException
	 */
	protected void writeLineElement(LineElement lineElement, Element ln) throws ConverterException {
		Element wyps = new Element("Waypoints", ln.getNamespace());
		ln.addContent(wyps);
		writePoints(lineElement.getPoints(), wyps);
		writeAnchors(lineElement.getAnchors(), wyps);
		writeGroupRef(lineElement.getGroupRef(), ln);
		Element gfx = new Element("Graphics", ln.getNamespace());
		ln.addContent(gfx);
		writeLineStyleProperty(lineElement.getLineStyleProperty(), gfx);
		writeElementInfo(lineElement, ln);
		writeGroupRef(lineElement.getGroupRef(), ln); // TODO location

	}

	/**
	 * Writes point {@link Point} information.
	 * 
	 * 
	 * TODO elementId, elementRef, arrowHead, x, y, relX, relY (Order)
	 * 
	 * @param points the list of points.
	 * @param wyps   the parent element.
	 * @throws ConverterException
	 */
	protected void writePoints(List<Point> points, Element wyps) throws ConverterException {
		List<Element> ptList = new ArrayList<Element>();
		for (Point point : points) {
			if (point == null)
				continue;
			Element pt = new Element("Point", wyps.getNamespace());
			writeElementId(point.getElementId(), pt);
			pt.setAttribute("x", Double.toString(point.getXY().getX()));
			pt.setAttribute("y", Double.toString(point.getXY().getY()));
			if (writeElementRef(point.getElementRef(), pt)) {
				pt.setAttribute("relX", Double.toString(point.getRelX()));
				pt.setAttribute("relY", Double.toString(point.getRelY()));
			}
			pt.setAttribute("arrowHead", point.getArrowHead().getName());
			if (pt != null)
				ptList.add(pt);
		}
		if (ptList != null && ptList.isEmpty() == false)
			wyps.addContent(ptList);
	}

	/**
	 * Writes anchor {@link Anchor} information. TODO elementId, x, y, position,
	 * shapeType (Order)
	 * 
	 * @param anchors the list of anchors.
	 * @param wyps    the parent element.
	 * @throws ConverterException
	 */
	protected void writeAnchors(List<Anchor> anchors, Element wyps) throws ConverterException {
		if (!anchors.isEmpty()) {
			List<Element> anList = new ArrayList<Element>();
			for (Anchor anchor : anchors) {
				if (anchor == null)
					continue;
				Element an = new Element("Anchor", wyps.getNamespace());
				writeElementId(anchor.getElementId(), an);
				an.setAttribute("x", Double.toString(anchor.getXY().getX()));
				an.setAttribute("y", Double.toString(anchor.getXY().getY()));
				an.setAttribute("position", Double.toString(anchor.getPosition()));
				an.setAttribute("shapeType", anchor.getShapeType().getName());
				if (an != null) {
					anList.add(an);
				}
			}
			if (anList != null && anList.isEmpty() == false) {
				wyps.addContent(anList);
			}
		}
	}

	/**
	 * Writes label {@link Label} information.
	 * 
	 * @param labels the list of labels.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeLabels(List<Label> labels, Element root) throws ConverterException {
		if (!labels.isEmpty()) {
			Element lbs = new Element("Labels", root.getNamespace());
			List<Element> lbList = new ArrayList<Element>();
			for (Label label : labels) {
				if (label == null)
					continue;
				Element lb = new Element("Label", root.getNamespace());
				writeShapedElement(label, lb);
				lb.setAttribute("textLabel", label.getTextLabel());
				if (label.getHref() != null)
					lb.setAttribute("href", label.getHref());
				writeGroupRef(label.getGroupRef(), lb); // TODO location
				if (lb != null) {
					lbList.add(lb);
				}
			}
			if (lbList != null && lbList.isEmpty() == false) {
				lbs.addContent(lbList);
				root.addContent(lbs);
			}
		}
	}

	/**
	 * Writes shape {@link Shape} information.
	 * 
	 * @param shapes the list of shapes.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeShapes(List<Shape> shapes, Element root) throws ConverterException {
		if (!shapes.isEmpty()) {
			Element shps = new Element("Shapes", root.getNamespace());
			List<Element> shpList = new ArrayList<Element>();
			for (Shape shape : shapes) {
				if (shape == null)
					continue;
				Element shp = new Element("Shape", root.getNamespace());
				if (shape.getTextLabel() != null)
					shp.setAttribute("textLabel", shape.getTextLabel());
				writeShapedElement(shape, shp);
				writeGroupRef(shape.getGroupRef(), shp); // TODO location
				Element gfx = shp.getChild("Graphics", shp.getNamespace());
				gfx.setAttribute("rotation", Double.toString(shape.getRotation()));
				if (shp != null) {
					shpList.add(shp);
				}
			}
			if (shpList != null && shpList.isEmpty() == false) {
				shps.addContent(shpList);
				root.addContent(shps);
			}
		}
	}

	/**
	 * Writes group {@link Group} information.
	 * 
	 * @param groups the list of groups.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeGroups(List<Group> groups, Element root) throws ConverterException {
		if (!groups.isEmpty()) {
			Element grps = new Element("Groups", root.getNamespace());
			List<Element> grpList = new ArrayList<Element>();
			for (Group group : groups) {
				if (group == null)
					continue;
				Element grp = new Element("Group", root.getNamespace());
				writeXref(group.getXref(), grp, false);
				writeShapedElement(group, grp);
				if (group.getTextLabel() != null)
					grp.setAttribute("textLabel", group.getTextLabel());
				grp.setAttribute("type", group.getType().getName());
				writeGroupRef(group.getGroupRef(), grp); // TODO location
				if (grp != null) {
					grpList.add(grp);
				}
			}
			if (grpList != null && grpList.isEmpty() == false) {
				grps.addContent(grpList);
				root.addContent(grps);
			}
		}
	}

	/**
	 * TODO
	 */
	protected void WriteBiopax(List<Annotation> annotations, Element root) throws ConverterException {
		if (!annotations.isEmpty()) {
			Element annts = new Element("Annotations", root.getNamespace());
			List<Element> anntList = new ArrayList<Element>();
			for (Annotation annotation : annotations) {
				if (annotation == null)
					continue;
				Element annt = new Element("Annotation", root.getNamespace());
				writeElementId(annotation.getElementId(), annt);
				annt.setAttribute("value", annotation.getValue());
				annt.setAttribute("type", annotation.getType().getName());
				if (annotation.getXref() != null) { // TODO optional Xref handling
					writeXref(annotation.getXref(), annt, false);
				}
				if (annotation.getUrl() != null) {
					annt.setAttribute("url", annotation.getUrl());
				}
				if (annt != null) {
					anntList.add(annt);
				}
			}
			if (anntList != null && anntList.isEmpty() == false) {
				annts.addContent(anntList);
				root.addContent(annts);
			}
		}
	}

	/**
	 * TODO
	 */
	protected void writePublicationXRefs(List<Citation> citations, Element root) throws ConverterException {
		if (!citations.isEmpty()) {
			Element cits = new Element("Citations", root.getNamespace());
			List<Element> citList = new ArrayList<Element>();
			for (Citation citation : citations) {
				if (citation == null)
					continue;
				Element cit = new Element("Citation", root.getNamespace());
				writeElementId(citation.getElementId(), cit);
				writeXref(citation.getXref(), cit, true);
				if (citation.getUrl() != null) {
					cit.setAttribute("url", citation.getUrl());
				}
				if (cit != null) {
					citList.add(cit);
				}
			}
			if (citList != null && citList.isEmpty() == false) {
				cits.addContent(citList);
				root.addContent(cits);
			}
		}
	}

	/**
	 * Writes elementId {@link PathwayElement} property information.
	 * 
	 * @param elementId the elementId.
	 * @param e         the parent element.
	 */
	protected void writeElementId(String elementId, Element e) {
		if (elementId != null && !elementId.equals(""))
			e.setAttribute("elementId", elementId);
	}

	/**
	 * Writes elementRef property information.
	 * 
	 * @param elementRef the elementRef.
	 * @param e          the parent element.
	 * @return true if elementRef exists and is successfully written.
	 */
	protected boolean writeElementRef(PathwayElement elemRf, Element e) {
		if (elemRf != null) {
			String elementRef = elemRf.getElementId();
			if (elementRef != null && !elementRef.equals(""))
				e.setAttribute("elementRef", elementRef);
			return true;
		}
		return false;
	}

	/**
	 * Writes groupRef property information.
	 * 
	 * @param groupRef the groupRef.
	 * @param e        the parent element.
	 */
	protected void writeGroupRef(Group grpRf, Element e) {
		if (grpRf != null) {
			String groupRef = grpRf.getElementId();
			if (groupRef != null && !groupRef.equals(""))
				e.setAttribute("groupRef", groupRef);
		}
	}

	/**
	 * Writes shapedElement {@link ShapedElement} information for datanodes, labels,
	 * shapes, or groups.
	 * 
	 * @param shapedElement the datanode, label, shape, or group.
	 * @param e             the shape element.
	 * @throws ConverterException
	 */
	private void writeShapedElement(ShapedElement shapedElement, Element se) throws ConverterException {
		Element gfx = new Element("Graphics", se.getNamespace());
		se.addContent(gfx);
		writeRectProperty(shapedElement.getRectProperty(), gfx);
		writeFontProperty(shapedElement.getFontProperty(), gfx);
		writeShapeStyleProperty(shapedElement.getShapeStyleProperty(), gfx);
		writeElementInfo(shapedElement, se);
//		writeGroupRef(shapedElement.getGroupRef(), se); // TODO REMOVE
	}

	/**
	 * Writes elementId, comment group {comment, dynamic property, annotationRef,
	 * citationRef) and evidenceRef {@link ElementInfo} information for datanodes,
	 * interactions, graphicalLines, labels, shapes, and group.
	 * 
	 * @param elementInfo the pathway element.
	 * @param e           the parent element.
	 * @throws ConverterException
	 */
	private void writeElementInfo(ElementInfo elementInfo, Element e) throws ConverterException {
		writeElementId(elementInfo.getElementId(), e);
		writeComments(elementInfo.getComments(), e);
		writeBiopaxRefs(elementInfo.getAnnotationRefs(), e);
		writePublicationXrefs(elementInfo.getCitationRefs(), e);
		writeAttributes(elementInfo.getDynamicProperties(), e);
	}

	/**
	 * Writes rect property {@link RectProperty} information.
	 * 
	 * @param rectProp the rectproperties.
	 * @param gfx      the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeRectProperty(RectProperty rectProp, Element gfx) throws ConverterException {
		gfx.setAttribute("CenterX", Double.toString(rectProp.getCenterXY().getX()));
		gfx.setAttribute("CenterY", Double.toString(rectProp.getCenterXY().getY()));
		gfx.setAttribute("Width", Double.toString(rectProp.getWidth()));
		gfx.setAttribute("Height", Double.toString(rectProp.getHeight()));
	}

	/**
	 * Writes font property {@link FontProperty} information.
	 * 
	 * NB: TODO Color is set by textColor
	 * 
	 * @param fontProp the font properties.
	 * @param gfx      the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeFontProperty(FontProperty fontProp, Element gfx) throws ConverterException {
		gfx.setAttribute("Color", ColorUtils.colorToHex(fontProp.getTextColor()));
		gfx.setAttribute("FontName", fontProp.getFontName() == null ? "" : fontProp.getFontName());
		gfx.setAttribute("FontWeight", fontProp.getFontWeight() ? "Bold" : "Normal");
		gfx.setAttribute("FontStyle", fontProp.getFontStyle() ? "Italic" : "Normal");
		gfx.setAttribute("FontDecoration", fontProp.getFontDecoration() ? "Underline" : "Normal");
		gfx.setAttribute("FontStrikethru", fontProp.getFontStrikethru() ? "Strikethru" : "Normal");
		gfx.setAttribute("FontSize", Integer.toString((int) fontProp.getFontSize()));
		gfx.setAttribute("Align", fontProp.getHAlign().getName());
		gfx.setAttribute("Valign", fontProp.getVAlign().getName());
	}

	/**
	 * Writes shape style property {@link ShapeStyleProperty} information.
	 * 
	 * TODO NB: borderColor is not set, Color is set solely by textColor.
	 * 
	 * @param shapeProp the shape style properties.
	 * @param gfx       the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeShapeStyleProperty(ShapeStyleProperty shapeProp, Element gfx) throws ConverterException {
		gfx.setAttribute("LineStyle", shapeProp.getBorderStyle() != LineStyleType.DASHED ? "Solid" : "Broken");
		gfx.setAttribute("LineThickness", String.valueOf(shapeProp.getBorderWidth()));
		gfx.setAttribute("FillColor", ColorUtils.colorToHex(shapeProp.getFillColor()));
		gfx.setAttribute("ShapeType", shapeProp.getShapeType().getName());
		gfx.setAttribute("ZOrder", String.valueOf(shapeProp.getZOrder()));
	}

	/**
	 * Writes line style property {@link LineStyleProperty} information.
	 * 
	 * @param lineProp the line style properties.
	 * @param gfx      the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeLineStyleProperty(LineStyleProperty lineProp, Element gfx) throws ConverterException {
		gfx.setAttribute("LineColor", ColorUtils.colorToHex(lineProp.getLineColor()));
		gfx.setAttribute("LineStyle", lineProp.getLineStyle() != LineStyleType.DASHED ? "Solid" : "Broken");
		gfx.setAttribute("LineThickness", String.valueOf(lineProp.getLineWidth()));
		gfx.setAttribute("ConnectorType", lineProp.getConnectorType().getName());
		gfx.setAttribute("ZOrder", String.valueOf(lineProp.getZOrder()));
	}
}
