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
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderSchemaFactory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.type.LineStyleType;

public class GPML2021Writer {

//	protected GpmlFormatAbstract (String xsdFile, Namespace nsGPML)
//	{
//		this.xsdFile = xsdFile;
//		this.nsGPML = nsGPML;
//	}
//
//	private final Namespace nsGPML;
//	private final String xsdFile;
//	
//	protected abstract Map<String, AttributeInfo> getAttributeInfo();
//
//	public Namespace getGpmlNamespace () { return nsGPML; }
//	
//	

	PathwayModel pathwayModel = new PathwayModel();
	SchemaFactory schemafac = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	Schema schema = schemafac.newSchema(new File("GPML2021.xsd"));
	XMLReaderJDOMFactory factory = new XMLReaderSchemaFactory(schema);
	SAXBuilder sb = new SAXBuilder(factory);
	Document doc = writePathwayModel(pathwayModel);

	// TODO look up how to handle namespace properly

	public Document writePathwayModel(PathwayModel m) throws ConverterException {
		Document doc = new Document();
		Element root = new Element("Pathway", root.getNamespace());
		doc.setRootElement(root);

		List<Element> elementList = new ArrayList<Element>();

		writePathway(m.getPathway(), root);

		root.addContent(elementList);

		writeAuthors(m, root);
		
		List<Author> authors = m.getAuthors();

		List<DataNode> dataNodes = m.getDataNodes(); // TODO And states
		List<Interaction> interactions = m.getInteractions();
		List<GraphicalLine> graphicaLines = m.getGraphicalLines();
		List<Label> labels = m.getLabels();
		List<Shape> shapes = m.getShapes();
		List<Group> groups = m.getGroups();

		List<Annotation> annotations = m.getAnnotations();
		List<Citation> citations = m.getCitations();
		List<Evidence> evidences = m.getEvidences();
//			for (Evidence o: evidences) {
//				Element e = createJdomElement(o);
//				if (e ! = null)
//					elementList.add(e);
//			}

		root.addContent(e);
		return doc;

	}

	protected void writeElementId(PathwayElement o, Element e) {
		String elementId = o.getElementId();
		if (elementId != null && !elementId.equals("")) {
			e.setAttribute("elementId", elementId);
		}
	}

	/**
	 * Updates pathway information.
	 * 
	 * @param root
	 * @param o
	 * @throws ConverterException
	 */
	protected void writePathway(Pathway p, Element root) throws ConverterException {
		root.setAttribute("title", p.getTitle());

		Element graphics = new Element("Graphics", root.getNamespace());
		root.addContent(graphics);
		graphics.setAttribute("boardWidth", String.valueOf(p.getBoardWidth()));
		graphics.setAttribute("boardHeight", String.valueOf(p.getBoardHeight()));

		// Add element Xref
		Element xref = new Element("Xref", root.getNamespace());
		String identifier = p.getXref().getId();
		String dataSource = p.getXref().getDataSource().getFullName(); // TODO dataSource
		xref.setAttribute("dataSource", dataSource == null ? "" : dataSource); // TODO null handling
		xref.setAttribute("identifier", identifier == null ? "" : identifier);
		root.addContent(xref);

		root.setAttribute("organism", p.getOrganism());
		root.setAttribute("source", p.getSource());
		root.setAttribute("version", p.getVersion());
		root.setAttribute("license", p.getLicense());

		writeInfoBox(p, root);

		writeComments(p, root);
		writeDynamicProperties(p, root);
		writeAnnotationRefs(p, root);
		writeCitationRefs(p, root);
		writeEvidenceRefs(p, root);
	}

	protected void writeInfoBox(Pathway p, Element root) {
		String centerX = Double.toString(p.getInfoBox().getX());
		String centerY = Double.toString(p.getInfoBox().getY());
		if (root != null) {
			Element infoBox = new Element("InfoBox", root.getNamespace());
			infoBox.setAttribute("CenterX", centerX);
			infoBox.setAttribute("CenterY", centerY);
		}
	}

	private void writeAuthors(PathwayModel m, Element root) throws ConverterException {
		for (Author a : m.getAuthors()) {
			if (a == null)
				continue;
			Element author = new Element("Author", root.getNamespace());
			author.setAttribute("name", a.getName());
			author.setAttribute("fullName", a.getFullName());
			author.setAttribute("email", a.getEmail());
			root.addContent(author);
		}
	}


	protected void writeComments(List<Comment> comments, Element e) throws ConverterException {
			for (Comment c : comments) {
				Element comment = new Element("Comment", e.getNamespace());
				comment.setText(c.getContent());
				comment.setAttribute("source", c.getSource());
				e.addContent(comment);
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


	protected void writeGroupRef(ShapedElement o, Element e) {
		String groupRef = o.getGroupRef().getElementId();
		if (groupRef != null && !groupRef.equals("")) {
			e.setAttribute("GroupRef", groupRef);
		}
	}

	protected void writeGroupRef(LineElement o, Element e) {
		String groupRef = o.getGroupRef().getElementId();
		if (groupRef != null && !groupRef.equals("")) {
			e.setAttribute("GroupRef", groupRef);
		}
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	private void writeElementInfo(ElementInfo o, Element e) throws ConverterException {
		writeElementId(o, e);
		writeComments(o, e);
		writeAnnotationRefs(o, e);
		writeCitationRefs(o, e);
		writeEvidenceRefs(o, e);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeRectProperty(ShapedElement o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());
		graphics.setAttribute("centerX", Double.toString(o.getRectProperty().getCenterXY().getX()));
		graphics.setAttribute("centerY", Double.toString(o.getRectProperty().getCenterXY().getY()));
		graphics.setAttribute("width", Double.toString(o.getRectProperty().getWidth()));
		graphics.setAttribute("height", Double.toString(o.getRectProperty().getHeight()));
	}

	/**
	 * Gets FontProperty values from model and writes to gpml FontAttributes for
	 * Graphics element of the given pathway element.
	 * 
	 * @param o the given pathway element.
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeFontProperty(ShapedElement o, Element e) throws ConverterException {
		String textColor = ColorUtils.colorToHex(o.getFontProperty().getTextColor());
		String fontName = o.getFontProperty().getFontName() == null ? "" : o.getFontProperty().getFontName();
		String fontWeight = o.getFontProperty().getFontWeight() ? "Bold" : "Normal";
		String fontStyle = o.getFontProperty().getFontStyle() ? "Italic" : "Normal";
		String fontDecoration = o.getFontProperty().getFontDecoration() ? "Underline" : "Normal";
		String fontStrikethru = o.getFontProperty().getFontStrikethru() ? "Strikethru" : "Normal";
		String fontSize = Integer.toString((int) o.getFontProperty().getFontSize());
		String hAlignType = o.getFontProperty().getVAlign().getName();
		String vAlignType = o.getFontProperty().getHAlign().getName();
//		if (e != null) {
//			if (graphics != null) {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		graphics.setAttribute("textColor", textColor);
		graphics.setAttribute("fontName", fontName);
		graphics.setAttribute("fontWeight", fontWeight);
		graphics.setAttribute("fontStyle", fontStyle);
		graphics.setAttribute("fontDecoration", fontDecoration);
		graphics.setAttribute("fontStrikethru", fontStrikethru);
		graphics.setAttribute("fontSize", fontSize);
		graphics.setAttribute("hAlign", hAlignType);
		graphics.setAttribute("vAlign", vAlignType);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeShapeStyleProperty(ShapedElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());

		String borderColor = ColorUtils.colorToHex(o.getShapeStyleProperty().getBorderColor());
		String borderStyle = o.getShapeStyleProperty().getBorderStyle() != LineStyleType.DASHED ? "Solid" : "Broken";
		String borderWidth = String.valueOf(o.getShapeStyleProperty().getBorderWidth());
		// TODO transparent
		String fillColor = o.getShapeStyleProperty().getFillColor() == Color.decode("#00000000") ? "Transparent"
				: ColorUtils.colorToHex(o.getShapeStyleProperty().getFillColor());
		// TODO shapeType
		String zOrder = String.valueOf(o.getShapeStyleProperty().getZOrder());
		graphics.setAttribute("borderColor", borderColor);
		graphics.setAttribute("borderStyle", borderStyle);
		graphics.setAttribute("borderWidth", borderWidth);
		// TODO ConnectorType enum
		// TODO shapeType
		graphics.setAttribute("fillColor", fillColor);
		graphics.setAttribute("zOrder", zOrder);

	}

	/**
	 * DataNode, Label, Shape, Group
	 * 
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	private void writeShapedElement(ShapedElement o, Element e) throws ConverterException {
		writePathwayElement(o, e); // TODO: ElementId, CommentGroup
		String base = e.getName();

		String groupRef = o.getGroupRef().getElementId();
		if (groupRef != null && groupRef.equals("")) {
			e.setAttribute("GroupRef", groupRef);
		}
		writeRectProperty(o, e);
		writeFontProperty(o, e);
		writeShapeStyleProperty(o, e);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeLineStyleProperty(LineElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String lineColor = ColorUtils.colorToHex(o.getLineStyleProperty().getLineColor());
		String lineStyle = o.getLineStyleProperty().getLineStyle() != LineStyleType.DASHED ? "Solid" : "Broken";
		String lineWidth = String.valueOf(o.getLineStyleProperty().getLineWidth());
		String connectorType = o.getLineStyleProperty().getConnectorType().getName();
		String zOrder = String.valueOf(o.getLineStyleProperty().getZOrder());

		// TODO COLOR
		graphics.setAttribute("lineColor", lineColor);

		graphics.setAttribute("lineStyle", lineStyle);
		graphics.setAttribute("lineWidth", lineWidth);
		// TODO ConnectorType enum
		graphics.setAttribute("connectorType", connectorType);
		graphics.setAttribute("zOrder", zOrder);

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeDataNode(DataNode o, Element e) throws ConverterException {

		// TODO STATE
		// TODO elementRef
		String textLabel = o.getTextLabel();
		String type = o.getType().getName();
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = o.getXref().getId();
		String dataSource = o.getXref().getDataSource().getFullName();
		e.setAttribute("textLabel", textLabel);
		e.setAttribute("type", type);
		xref.setAttribute("dataSource", dataSource == null ? "" : dataSource);
		xref.setAttribute("identifier", identifier);
//		writeCommon(o, e); //Comment, Attribute, Biopax 
		writeShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeState(State o, Element e) throws ConverterException {
		writePathwayElement(o, e); // TODO: // ElemenId, CommentGroup
		// TODO elementRef
		e.setAttribute("State", "GraphRef", e, o.getElementRef()); // TODO Check if null
		e.setAttribute("State", "TextLabel", e, o.getTextLabel());
		e.setAttribute("State", "StateType", e, o.getType().getName());

		Element graphics = e.getChild("Graphics", e.getNamespace());
//		graphics.setAttribute("State.Graphics", "RelX", graphics, "" + o.getRelX());
//		graphics.setAttribute("State.Graphics", "RelY", graphics, "" + o.getRelY());
//		
//		graphics.setAttribute("State.Graphics", "Width", graphics, "" + o.getWidth());
//		graphics.setAttribute("State.Graphics", "Height", graphics, "" + o.getHeight());
//		/** FontProperty */
//		graphics.setAttribute("State.Graphics", "FontName", graphics,
//				o.getFontProperty().getFontName() == null ? "" : o.getFontProperty().getFontName());
//		graphics.setAttribute("State.Graphics", "FontWeight", graphics, o.getFontProperty().getFontWeight() ? "Bold" : "Normal");
//		graphics.setAttribute("State.Graphics", "FontStyle", graphics, o.getFontProperty().getFontStyle() ? "Italic" : "Normal");
//		graphics.setAttribute("State.Graphics", "FontDecoration", graphics,
//				o.getFontProperty().getFontDecoration() ? "Underline" : "Normal");
//		graphics.setAttribute("State.Graphics", "FontStrikethru", graphics,
//				o.getFontProperty().getFontStrikethru() ? "Strikethru" : "Normal");
//		graphics.setAttribute("State.Graphics", "FontSize", graphics, Integer.toString((int) o.getFontProperty().getFontSize()));
//		graphics.setAttribute("State.Graphics", "Align", graphics, o.getFontProperty().getVAlign().getName());
//		graphics.setAttribute("State.Graphics", "Valign", graphics, o.getFontProperty().getHAlign().getName());
//		/** TODO ShapeStyleProperty */
		/** Xref */
		Element xref = e.getChild("Xref", e.getNamespace());
		String dataSource = o.getXref().getDataSource().getFullName();
		xref.setAttribute("dataSource", dataSource == null ? "" : dataSource);
		xref.setAttribute("identifier", o.getXref().getId());
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeRotation(Shape o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());
		setAttribute("Shape.Graphics", "Rotation", graphics, Double.toString(o.getRotation()));
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeShapeType(PathwayElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String shapeName = o.getShapeType().getName();
		setAttribute(base + ".Graphics", "ShapeType", graphics, shapeName);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeInteraction(Interaction o, Element e) throws ConverterException {
		writeLineElement(o, e);
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = o.getXref().getId();
		String dataSource = o.getXref().getDataSource().getFullName();
		setAttribute("Interaction.Xref", "Database", xref, dataSource == null ? "" : dataSource);
		setAttribute("Interaction.Xref", "ID", xref, identifier);

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeLineElement(LineElement o, Element e) throws ConverterException {
		writePathwayElement(o, e); /** TODO elementId, CommentGroup */

		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String groupRef = o.getGroupRef().getElementId();
		if (groupRef != null && groupRef.equals("")) {
			e.setAttribute("GroupRef", groupRef);
		}
		writeLineStyleProperty(o, e);

		// Writes the entire list
		/** TODO GPML2021 Points not in Graphics */
		List<Point> pts = o.getPoints();
		for (int i = 0; i < pts.size(); i++) {
			Point pt = pts.get(i);
			Element point = new Element("Point", e.getNamespace());
			graphics.addContent(point);
			writeElementId(pt, point); // TODO
			setAttribute(base + ".Graphics.Point", "X", point, Double.toString(pt.getXY().getX()));
			setAttribute(base + ".Graphics.Point", "Y", point, Double.toString(pt.getXY().getY()));
			if (pt.getElementRef() != null && !pt.getElementRef().equals("")) {
				setAttribute(base + ".Graphics.Point", "GraphRef", point, pt.getElementRef());
				setAttribute(base + ".Graphics.Point", "RelX", point, Double.toString(pt.getRelX()));
				setAttribute(base + ".Graphics.Point", "RelY", point, Double.toString(pt.getRelY()));
			}
			if (pt.getArrowHead() != null) {
				setAttribute(base + ".Graphics.Point", "ArrowHead", point, pt.getArrowHead().getName());
			}
		}

		// Writes the entire list
		/** TODO GPML2021 Anchors not in Graphics */
		for (Anchor a : o.getAnchors()) {
			Element anchor = new Element("Anchor", e.getNamespace());
			writeElementId(a, anchor); // TODO
			setAttribute(base + ".Graphics.Anchor", "Position", anchor, Double.toString(a.getPosition()));
			setAttribute(base + ".Graphics.Anchor", "Shape", anchor, a.getShapeType().getName());
			graphics.addContent(anchor);
		}
	}

//
//	List<Label> labels = p.getLabels();
//	Collections.sort(labels); // TODO necessary?
//	for (Label o : labels) {
//		Element e = new Element("Label", getGpmlNamespace());
//		e.addContent(new Element("Graphics", getGpmlNamespace()));
//		writeLabel(o, e);
//		if (e != null) {
//			elementList.add(e);
//		}
//	}
//	
//	protected Label readLabels(List<Element> e) throws ConverterException {
//
//	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeLabel(Label o, Element e) throws ConverterException {
		String textLabel = o.getTextLabel();
		String href = o.getHref();
		setAttribute("Label", "TextLabel", e, textLabel);
		setAttribute("Label", "Href", e, href);
		writeShapeStyleProperty(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
										// ShapeStyleProperty
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeShape(Shape o, Element e) throws ConverterException {

//		writeCommon(o, e); //CommentGroup, elementId
		String textLabel = o.getTextLabel();
		String type = o.getType().getName(); // TODO Shape type enum
		setAttribute("Shape", "TextLabel", e, textLabel);
		setAttribute("Shape", "Type", e, type);
		writeShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty
		writeRotation(o, e); // TODO rotation
		break;
	}

	protected void writeGroup(Group o, Element e) throws ConverterException {

		// TODO 2013a group does not have graphics
		writeShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty
		/** TODO GroupID */
		String id = o.createGroupId();
		if (id != null && !id.equals("")) {
			e.setAttribute("GroupId", o.createGroupId());
		}

		String textLabel = o.getTextLabel();
		String type = o.getType().getName(); // TODO Group type
		setAttribute("Group", "TextLabel", e, textLabel);
		setAttribute("Group", "Style", e, type);

		/** Xref added in GPML2021 */
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = o.getXref().getId();
		String dataSource = o.getXref().getDataSource().getFullName();
		setAttribute("State.Xref", "Database", xref, dataSource == null ? "" : dataSource);
		setAttribute("State.Xref", "ID", xref, identifier);

	}

	/**
	 *
	 */
	public Document createJdom(Pathway p) throws ConverterException {
		Document doc = new Document();

		Element root = new Element("Pathway", getGpmlNamespace());
		doc.setRootElement(root);

		List<Element> elementList = new ArrayList<Element>();

		// MappInfo
		Xref xref = p.getXref();
		List<Author> authors = p.getAuthors(); // length 0 to unbounded

//		List<Annotation> annotations  // --> Manager Pathway.getCitationManager.getCitations()
//		private List<Citation> citations; // --> Manager
//		private List<Evidence> evidences; // --> Manager

		List<Comment> comments = p.getComments(); // length 0 to unbounded
		List<DynamicProperty> dynamicProperties = p.getDynamicProperties(); // length 0 to unbounded
		List<AnnotationRef> annotationRefs = p.getAnnotationRefs(); // length 0 to unbounded
//		List<Citation> citationRefs; // length 0 to unbounded
//		List<Evidence> evidenceRef; // length 0 to unbounded
		double boardWidth = p.getBoardWidth();
		double boardHeight = p.getBoardHeight();

		InfoBox i = p.getInfoBox();
		Element ie = new Element("InfoBox", getGpmlNamespace());
		writeInfoBox(i, ie);

		e = new Element("Legend", getGpmlNamespace()); // TODO handle Legend

		List<DataNode> dataNodes = p.getDataNodes(); // TODO States
		Collections.sort(dataNodes); // TODO necessary?
		for (DataNode o : dataNodes) {
			Element e = new Element("DataNode", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			e.addContent(new Element("Xref", getGpmlNamespace()));
			writeDataNode(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		e = new Element("State", getGpmlNamespace());
		e.addContent(new Element("Graphics", getGpmlNamespace()));
		e.addContent(new Element("Xref", getGpmlNamespace()));

		List<Interaction> interactions = p.getInteractions();
		Collections.sort(interactions); // TODO necessary?
		for (Interaction o : interactions) {
			Element e = new Element("Interaction", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			e.addContent(new Element("Xref", getGpmlNamespace()));
			writeInteraction(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		List<GraphicalLine> graphicalLines = p.getGraphicalLines();
		Collections.sort(graphicalLines); // TODO necessary?
		for (GraphicalLine o : graphicalLines) {
			Element e = new Element("GraphicalLine", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			writeLineElement(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		List<Shape> shapes = p.getShapes();
		Collections.sort(shapes); // TODO necessary?
		for (Shape o : shapes) {
			Element e = new Element("Shape", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			writeShape(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		List<Group> groups = p.getGroups();
		Collections.sort(groups); // TODO necessary?
		for (Group o : groups) {
			Element e = new Element("Group", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			writeGroup(o, e);
			if (e != null) {
				elementList.add(e);
			}
		}

		// now sort the generated elements in the order defined by the xsd
		Collections.sort(elementList, new ByElementName());
		for (Element e : elementList) {
			// make sure biopax references are sorted alphabetically by rdf-id
			if (e.getName().equals("Biopax")) {
				for (Element e3 : e.getChildren()) {
					e3.removeChildren("AUTHORS", GpmlFormat.BIOPAX);
				}
				e.sortChildren(new BiopaxAttributeComparator());
			}
			root.addContent(e);
		}

		return doc;

//		Collections.sort(pathwayElements);
//		for (PathwayElement o : pathwayElements)
//		{
//			if (o.getObjectType() == ObjectType.MAPPINFO)
//			{
//				updateMappInfo(root, o);
//			}
//			else
//			{
//				Element e = createJdomElement(o);
//				if (e != null)
//					elementList.add(e);
//			}
//		}

//					case BIOPAX:
//						e = new Element("Biopax", getGpmlNamespace());
//						writeBiopax(o, e);
//						break;
//					}if(e==null)
//
//	{
//		throw new ConverterException("Error creating jdom element with objectType " + o.getObjectType());
//	}return e;}

	}

	/**
	 * Writes the JDOM document to the outputstream specified
	 * 
	 * @param out      the outputstream to which the JDOM document should be writed
	 * @param validate if true, validate the dom structure before writing. If there
	 *                 is a validation error, or the xsd is not in the classpath, an
	 *                 exception will be thrown.
	 * @throws ConverterException
	 */
	public void writeToXml(Pathway pwy, OutputStream out, boolean validate) throws ConverterException {
		Document doc = createJdom(pwy);

		// Validate the JDOM document
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
	public void writeToXml(Pathway pwy, File file, boolean validate) throws ConverterException {
		OutputStream out;
		try {
			out = new FileOutputStream(file);
		} catch (IOException ex) {
			throw new ConverterException(ex);
		}
		writeToXml(pwy, out, validate);
	}
}
