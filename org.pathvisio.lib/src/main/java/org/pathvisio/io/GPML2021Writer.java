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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderSchemaFactory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.type.*;

public class GPML2021Writer {

	/**
	 * Writes the JDOM document to the outputstream specified**
	 * 
	 * @param out      the outputstream to which the JDOM document should be writed*
	 * @param validate if true, validate the dom structure before writing. If there*
	 *                 is a validation error, or the xsd is not in the classpath,an*
	 *                 exception will be thrown.*@throws ConverterException
	 */

	public void writeToXml(PathwayModel pathwayModel, OutputStream out, boolean validate) throws ConverterException {

		File xsdfile = new File("GPML2021.xsd");
		Namespace nsGPML = Namespace.getNamespace("http://pathvisio.org/GPML/2021");
		SchemaFactory schemafac = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemafac.newSchema(xsdfile);
		XMLReaderJDOMFactory factory = new XMLReaderSchemaFactory(schema);
		SAXBuilder sb = new SAXBuilder(factory);
		Document doc = writePathwayModel(pathwayModel);

		// Validate the JDOM document TODO ????
		if (validate)
			validateDocument(doc);
		// Get the XML code
		XMLOutputter xmlcode = new XMLOutputter(Format.getPrettyFormat());
		Format f = xmlcode.getFormat();
		f.setEncoding("UTF-8");
		f.setTextMode(Format.TextMode.NORMALIZE);
		xmlcode.setFormat(f);

		try {
			// Send XML code to the outputstream
			xmlcode.output(doc, out);
		} catch (IOException ie) {
			throw new ConverterException(ie);
		}
	}

	/**
	 * Writes the JDOM document to the file specified
	 * 
	 * @param file     the file to which the JDOM document should be saved
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 */
	public void writeToXml(PathwayModel pathwayModel, File file, boolean validate) throws ConverterException {
		OutputStream out;
		try {
			out = new FileOutputStream(file);
		} catch (IOException ex) {
			throw new ConverterException(ex);
		}
		writeToXml(pathwayModel, out, validate);
	}

	// TODO look up how to handle namespace properly

	public Document writePathwayModel(PathwayModel pathwayModel) throws ConverterException {
		Document doc = new Document();
		Element root = new Element("Pathway");

		doc.setRootElement(root);

		if (root != null) {
			writePathway(pathwayModel.getPathway(), root);
			writeAuthors(pathwayModel.getAuthors(), root);
			
			writeDataNodes(pathwayModel.getDataNodes(), root);
			writeInteractions(pathwayModel.getInteractions(), root);
			writeGraphicalLines(pathwayModel.getGraphicalLines(), root);
			writeLabels(pathwayModel.getLabels(), root);
			writeShapes(pathwayModel.getShapes(), root);
			writeGroups(pathwayModel.getGroups(), root);

			writeAnnotations(pathwayModel.getAnnotations(), root);
			writeCitations(pathwayModel.getCitations(), root);
			writeEvidences(pathwayModel.getEvidences(), root);
		}
		return doc;
	}

	/**
	 * Updates pathway information.
	 * 
	 * @param root
	 * @param o
	 * @throws ConverterException
	 */
	protected void writePathway(Pathway pathway, Element root) throws ConverterException {
		root.setAttribute("title", pathway.getTitle());

		Element gfx = new Element("Graphics", root.getNamespace());
		root.addContent(gfx);
		gfx.setAttribute("boardWidth", String.valueOf(pathway.getBoardWidth()));
		gfx.setAttribute("boardHeight", String.valueOf(pathway.getBoardHeight()));

		writeXref(pathway.getXref(), root);

		root.setAttribute("organism", pathway.getOrganism());
		root.setAttribute("source", pathway.getSource());
		root.setAttribute("version", pathway.getVersion());
		root.setAttribute("license", pathway.getLicense());

		writeInfoBox(pathway.getInfoBox(), root);
		writeComments(pathway.getComments(), root);
		writeDynamicProperties(pathway.getDynamicProperties(), root);
		writeAnnotationRefs(pathway.getAnnotationRefs(), root);
		writeCitationRefs(pathway.getCitationRefs(), root);
		writeEvidenceRefs(pathway.getEvidenceRefs(), root);
	}

	protected void writeXref(Xref xref, Element e) { // TODO boolean required
		Element xrf = new Element("Xref", e.getNamespace());
		String identifier = xref.getId();
		String dataSource = xref.getDataSource().getFullName(); // TODO dataSource
		xrf.setAttribute("dataSource", dataSource == null ? "" : dataSource); // TODO null handling
		xrf.setAttribute("identifier", identifier == null ? "" : identifier);
		e.addContent(xrf);
	}

	protected void writeInfoBox(Coordinate infoBox, Element root) {
		Element ifb = new Element("InfoBox", root.getNamespace());
		ifb.setAttribute("centerX", Double.toString(infoBox.getX()));
		ifb.setAttribute("centerY", Double.toString(infoBox.getY()));
	}

	protected void writeComments(List<Comment> comments, Element e) throws ConverterException {
		for (Comment comment : comments) {
			Element cmt = new Element("Comment", e.getNamespace());
			cmt.setText(comment.getContent());
			cmt.setAttribute("source", comment.getSource());
			e.addContent(cmt);
		}
	}

	protected void writeDynamicProperties(Map<String, String> dynamicProperties, Element e) throws ConverterException {
		for (String key : dynamicProperties.keySet()) {
			Element property = new Element("Property", e.getNamespace());
			property.setAttribute("key", key);
			property.setAttribute("value", dynamicProperties.get(key));
			e.addContent(property);
		}
	}

	protected void writeAnnotationRefs(List<AnnotationRef> annotationRefs, Element e) throws ConverterException {
		for (AnnotationRef a : annotationRefs) {
			Element annotationRef = new Element("AnnotationRef", e.getNamespace());
			annotationRef.setAttribute("elementRef", a.getAnnotation().getElementId());
			for (Citation c : a.getCitations()) {
				Element citationRef = new Element("CitationRef", e.getNamespace());
				citationRef.setAttribute("elementRef", c.getElementId());
			}
			for (Evidence ev : a.getEvidences()) {
				Element evidenceRef = new Element("EvidenceRef", e.getNamespace());
				evidenceRef.setAttribute("elementRef", ev.getElementId());
			}
			e.addContent(annotationRef);
		}
	}

	protected void writeCitationRefs(List<Citation> citationRefs, Element e) throws ConverterException {
		if (e != null) {
			for (Citation c : citationRefs) {
				Element citationRef = new Element("CitationRef", e.getNamespace());
				citationRef.setAttribute("elementRef", c.getElementId());
				e.addContent(citationRef);
			}
		}
	}

	protected void writeEvidenceRefs(List<Evidence> evidenceRefs, Element e) throws ConverterException {
		if (e != null) {
			for (Evidence c : evidenceRefs) {
				Element evidenceRef = new Element("EvidenceRef", e.getNamespace());
				evidenceRef.setAttribute("elementRef", c.getElementId());
				e.addContent(evidenceRef);
			}
		}
	}

	private void writeAuthors(List<Author> authors, Element root) throws ConverterException {
		Element aus = new Element("Authors", root.getNamespace());
		List<Element> auList = new ArrayList<Element>();
		for (Author author : authors) {
			if (author == null)
				continue;
			Element au = new Element("Author", root.getNamespace());
			au.setAttribute("name", author.getName());
			au.setAttribute("fullName", author.getFullName());
			au.setAttribute("email", author.getEmail());
			if (au != null) {
				auList.add(au);
			}
		}
		if (auList != null && auList.isEmpty() == false) {
			aus.addContent(auList);
			root.addContent(aus);
		}
	}

	/**
	 * DataNode properties in the order to be written:
	 * 
	 * - elementId, elementRef, textLabel, type, groupRef - Xref, States - Graphics
	 * (RectAttributes, FontAttributes, ShapeStyleAttributes) - Comment, Property,
	 * AnnotationRef, CitationRef, EvidenceRef
	 */
	protected void writeDataNodes(List<DataNode> dataNodes, Element root) throws ConverterException {
		Element dns = new Element("DataNodes", root.getNamespace());
		List<Element> dnList = new ArrayList<Element>();
		for (DataNode dataNode : dataNodes) {
			if (dataNode == null)
				continue;
			Element dn = new Element("DataNode", root.getNamespace());
			writeXref(dataNode.getXref(), dn);
			writeStates(dataNode.getStates(), dn);
			writeShapedElement(dataNode, dn);
			writeElementRef(dataNode.getElementRef().getElementId(), dn);
			dn.setAttribute("textLabel", dataNode.getTextLabel());
			dn.setAttribute("type", dataNode.getType().getName());

			if (dn != null) {
				dnList.add(dn);
			}
		}
		if (dnList != null && dnList.isEmpty() == false) {
			dns.addContent(dnList);
			root.addContent(dns);
		}
	}

	/**
	 * NB: No elementRef in 2021
	 */
	protected void writeStates(List<State> states, Element dn) throws ConverterException {
		Element sts = new Element("States", dn.getNamespace());
		List<Element> stList = new ArrayList<Element>();
		for (State state : states) {
			if (state == null)
				continue;
			Element st = new Element("State", dn.getNamespace());
			writeXref(state.getXref(), st);
			writeElementInfo(state, st);
			st.setAttribute("textLabel", state.getTextLabel());
			st.setAttribute("type", state.getType().getName());

			Element gfx = new Element("Graphics", st.getNamespace());
			st.addContent(gfx);
			gfx.setAttribute("relX", Double.toString(state.getRelX()));
			gfx.setAttribute("relY", Double.toString(state.getRelY()));
			writeRectProperty(state.getRectProperty(), gfx); // TODO x and y may not be required...
			writeFontProperty(state.getFontProperty(), gfx);
			writeShapeStyleProperty(state.getShapeStyleProperty(), gfx);

			if (st != null) {
				stList.add(st);
			}
		}
		if (stList != null && stList.isEmpty() == false) {
			sts.addContent(stList);
			dn.addContent(sts);
		}
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeInteractions(List<Interaction> interactions, Element root) throws ConverterException {
		Element ias = new Element("Interactions", root.getNamespace());
		List<Element> iaList = new ArrayList<Element>();
		for (Interaction interaction : interactions) {
			if (interaction == null)
				continue;
			Element ia = new Element("Interaction", root.getNamespace());
			writeXref(interaction.getXref(), ia);
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

	protected void writeGraphicalLines(List<GraphicalLine> graphicalLines, Element root) throws ConverterException {
		Element gfs = new Element("GraphicalLines", root.getNamespace());
		List<Element> gfList = new ArrayList<Element>();
		for (GraphicalLine graphicalLine : graphicalLines) {
			if (graphicalLine == null)
				continue;
			Element gf = new Element("GraphicalLine", root.getNamespace());
			writeLineElement(graphicalLine, gf);
			if (gf != null) {
				gfList.add(gf);
			}
		}
		if (gfList != null && gfList.isEmpty() == false) {
			gfs.addContent(gfList);
			root.addContent(gfs);
		}
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeLineElement(LineElement lineElement, Element e) throws ConverterException {
		writeElementInfo(lineElement, e);
		Element wyps = new Element("Waypoints", e.getNamespace());
		e.addContent(wyps);
		writePoints(lineElement.getPoints(), wyps);
		writeAnchors(lineElement.getAnchors(), wyps);
		writeGroupRef(lineElement.getGroupRef().getElementId(), e);
		Element gfx = new Element("Graphics", e.getNamespace());
		e.addContent(gfx);
		writeLineStyleProperty(lineElement.getLineStyleProperty(), e);
	}

	/**
	 * elementId, elementRef, arrowHead, x, y, relX, relY //TODO Order
	 * 
	 * @param points
	 * @param ln
	 * @throws ConverterException
	 */
	protected void writePoints(List<Point> points, Element ln) throws ConverterException {
		Element wyps = ln.getChild("Waypoints", ln.getNamespace());
		List<Element> ptList = new ArrayList<Element>();
		for (Point point : points) {
			if (point == null)
				continue;
			Element pt = new Element("Point", ln.getNamespace());
			writeElementId(point.getElementId(), pt);
			pt.setAttribute("x", Double.toString(point.getXY().getX()));
			pt.setAttribute("y", Double.toString(point.getXY().getY()));
			if (point.getElementRef() != null && !point.getElementRef().equals("")) {
				writeElementRef(point.getElementRef().getElementId(), pt);
				pt.setAttribute("relX", Double.toString(point.getRelX()));
				pt.setAttribute("relY", Double.toString(point.getRelY()));
			}
			pt.setAttribute("arrowHead", point.getArrowHead().getName());
			if (pt != null) {
				ptList.add(pt);
			}
		}
		if (ptList != null && ptList.isEmpty() == false) {
			wyps.addContent(ptList);
		}
	}

	/**
	 * elementId, x, y, position, shapeType //TODO Order
	 * 
	 * @param points
	 * @param ln
	 * @throws ConverterException
	 */
	protected void writeAnchors(List<Anchor> anchors, Element ln) throws ConverterException {
		Element wyps = ln.getChild("Waypoints", ln.getNamespace());
		List<Element> anList = new ArrayList<Element>();
		for (Anchor anchor : anchors) {
			if (anchor == null)
				continue;
			Element an = new Element("Point", ln.getNamespace());
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

	protected void writeLabels(List<Label> labels, Element root) throws ConverterException {
		Element lbs = new Element("Labels", root.getNamespace());
		List<Element> lbList = new ArrayList<Element>();
		for (Label label : labels) {
			if (label == null)
				continue;
			Element lb = new Element("Label", root.getNamespace());
			writeShapedElement(label, lb);
			lb.setAttribute("textLabel", label.getTextLabel());
			lb.setAttribute("href", label.getHref());
			if (lb != null) {
				lbList.add(lb);
			}
		}
		if (lbList != null && lbList.isEmpty() == false) {
			lbs.addContent(lbList);
			root.addContent(lbs);
		}
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeShapes(List<Shape> shapes, Element root) throws ConverterException {
		Element shs = new Element("Shapes", root.getNamespace());
		List<Element> shList = new ArrayList<Element>();
		for (Shape shape : shapes) {
			if (shape == null)
				continue;
			Element sh = new Element("Shape", root.getNamespace());
			sh.setAttribute("textLabel", shape.getTextLabel());
			writeShapedElement(shape, sh);
			Element gfx = sh.getChild("Graphics", sh.getNamespace());
			gfx.setAttribute("rotation", Double.toString(shape.getRotation()));
			if (sh != null) {
				shList.add(sh);
			}
		}
		if (shList != null && shList.isEmpty() == false) {
			shs.addContent(shList);
			root.addContent(shs);
		}
	}

	protected void writeGroups(List<Group> groups, Element root) throws ConverterException {
		Element gps = new Element("Shapes", root.getNamespace());
		List<Element> gpList = new ArrayList<Element>();
		for (Group group : groups) {
			if (group == null)
				continue;
			Element gp = new Element("Shape", root.getNamespace());
			writeXref(group.getXref(), gp);
			writeShapedElement(group, gp);
			gp.setAttribute("textLabel", group.getTextLabel());
			gp.setAttribute("type", group.getType().getName());
			if (gp != null) {
				gpList.add(gp);
			}
		}
		if (gpList != null && gpList.isEmpty() == false) {
			gps.addContent(gpList);
			root.addContent(gps);
		}
	}

	protected void writeCitations(List<Citation> citations, Element root) throws ConverterException {
		Element cits = new Element("Citations", root.getNamespace());
		List<Element> citList = new ArrayList<Element>();
		for (Citation citation : citations) {
			if (citation == null)
				continue;
			Element cit = new Element("Citation", root.getNamespace());
			writeElementId(citation.getElementId(), cit);
			writeXref(citation.getXref(), cit);
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

	protected void writeAnnotations(List<Annotation> annotations, Element root) throws ConverterException {
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
				writeXref(annotation.getXref(), annt);
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

	protected void writeEvidences(List<Evidence> evidences, Element root) throws ConverterException {
		Element annts = new Element("Annotations", root.getNamespace());
		List<Element> anntList = new ArrayList<Element>();
		for (Evidence evidence : evidences) {
			if (evidence == null)
				continue;
			Element annt = new Element("Annotation", root.getNamespace());
			writeElementId(evidence.getElementId(), annt);
			writeXref(evidence.getXref(), annt);
			if (evidence.getValue() != null) {
				annt.setAttribute("value", evidence.getValue());
			}
			if (evidence.getUrl() != null) {
				annt.setAttribute("url", evidence.getUrl());
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

	protected void writeElementId(String elementId, Element e) {
		if (elementId != null && !elementId.equals(""))
			e.setAttribute("elementId", elementId);
	}

	protected void writeElementRef(String elementRef, Element e) {
		if (elementRef != null && !elementRef.equals(""))
			e.setAttribute("elementRef", elementRef);
	}

	protected void writeGroupRef(String groupRef, Element e) {
		if (groupRef != null && !groupRef.equals(""))
			e.setAttribute("groupRef", groupRef);
	}

	/**
	 * DataNode, Label, Shape, Group
	 */
	private void writeShapedElement(ShapedElement shapedElement, Element e) throws ConverterException {
		Element gfx = new Element("Graphics", e.getNamespace());
		e.addContent(gfx);
		writeRectProperty(shapedElement.getRectProperty(), e);
		writeFontProperty(shapedElement.getFontProperty(), e);
		writeShapeStyleProperty(shapedElement.getShapeStyleProperty(), e);
		writeElementInfo(shapedElement, e);
		writeGroupRef(shapedElement.getGroupRef().getElementId(), e); // TODO want it after...
	}

	/**
	 * @throws ConverterException
	 */
	private void writeElementInfo(ElementInfo elementInfo, Element e) throws ConverterException {
		writeElementId(elementInfo.getElementId(), e);
		writeComments(elementInfo.getComments(), e);
		writeDynamicProperties(elementInfo.getDynamicProperties(), e);
		writeAnnotationRefs(elementInfo.getAnnotationRefs(), e);
		writeCitationRefs(elementInfo.getCitationRefs(), e);
		writeEvidenceRefs(elementInfo.getEvidenceRefs(), e);
	}

	protected void writeRectProperty(RectProperty rectProp, Element e) throws ConverterException {
		Element gfx = e.getChild("Graphics", e.getNamespace());
		gfx.setAttribute("centerX", Double.toString(rectProp.getCenterXY().getX()));
		gfx.setAttribute("centerY", Double.toString(rectProp.getCenterXY().getY()));
		gfx.setAttribute("width", Double.toString(rectProp.getWidth()));
		gfx.setAttribute("height", Double.toString(rectProp.getHeight()));
	}

	/**
	 * Gets FontProperty values from model and writes to gpml FontAttributes for
	 * Graphics element of the given pathway element.
	 */
	protected void writeFontProperty(FontProperty fontProp, Element e) throws ConverterException {
		Element gfx = e.getChild("Graphics", e.getNamespace());
		gfx.setAttribute("textColor", ColorUtils.colorToHex(fontProp.getTextColor()));
		gfx.setAttribute("fontName", fontProp.getFontName() == null ? "" : fontProp.getFontName());
		gfx.setAttribute("fontWeight", fontProp.getFontWeight() ? "Bold" : "Normal");
		gfx.setAttribute("fontStyle", fontProp.getFontStyle() ? "Italic" : "Normal");
		gfx.setAttribute("fontDecoration", fontProp.getFontDecoration() ? "Underline" : "Normal");
		gfx.setAttribute("fontStrikethru", fontProp.getFontStrikethru() ? "Strikethru" : "Normal");
		gfx.setAttribute("fontSize", Integer.toString((int) fontProp.getFontSize()));
		gfx.setAttribute("hAlign", fontProp.getVAlign().getName());
		gfx.setAttribute("vAlign", fontProp.getHAlign().getName());
	}

	protected void writeShapeStyleProperty(ShapeStyleProperty shapeProp, Element e) throws ConverterException {
		Element gfx = e.getChild("Graphics", e.getNamespace());
		gfx.setAttribute("borderColor", ColorUtils.colorToHex(shapeProp.getBorderColor()));
		gfx.setAttribute("borderStyle", shapeProp.getBorderStyle() != LineStyleType.DASHED ? "Solid" : "Broken");
		gfx.setAttribute("borderWidth", String.valueOf(shapeProp.getBorderWidth()));
		gfx.setAttribute("fillColor", ColorUtils.colorToHex(shapeProp.getFillColor()));
		gfx.setAttribute("shapeType", shapeProp.getShapeType().getName());
		gfx.setAttribute("zOrder", String.valueOf(shapeProp.getZOrder()));
	}

	protected void writeLineStyleProperty(LineStyleProperty lineProp, Element e) throws ConverterException {
		Element gfx = e.getChild("Graphics", e.getNamespace());
		gfx.setAttribute("lineColor", ColorUtils.colorToHex(lineProp.getLineColor()));
		gfx.setAttribute("lineStyle", lineProp.getLineStyle() != LineStyleType.DASHED ? "Solid" : "Broken");
		gfx.setAttribute("lineWidth", String.valueOf(lineProp.getLineWidth()));
		gfx.setAttribute("connectorType", lineProp.getConnectorType().getName());
		gfx.setAttribute("zOrder", String.valueOf(lineProp.getZOrder()));

	}

}
