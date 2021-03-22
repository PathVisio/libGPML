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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.core.biopax.BiopaxElement;
import org.pathvisio.core.model.PathwayElement.MPoint;
import org.pathvisio.core.view.ShapeRegistry;
import org.pathvisio.model.*;

class GpmlFormat2013a extends GpmlFormatAbstract implements GpmlFormatReader, GpmlFormatWriter {
	public static final GpmlFormat2013a GPML_2013A = new GpmlFormat2013a("GPML2013a.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2013a"));

	/**
	 * @param xsdFile
	 * @param ns
	 */
	public GpmlFormat2013a(String xsdFile, Namespace ns) {
		super(xsdFile, ns);
	}

	private static final Map<String, AttributeInfo> ATTRIBUTE_INFO = initAttributeInfo();

	private static Map<String, AttributeInfo> initAttributeInfo() {
		Map<String, AttributeInfo> result = new HashMap<String, AttributeInfo>();
		// IMPORTANT: this array has been generated from the xsd with
		// an automated perl script. Don't edit this directly, use the perl script
		// instead.
		/* START OF AUTO-GENERATED CONTENT */
		result.put("Comment@Source", new AttributeInfo("xsd:string", null, "optional"));
		result.put("PublicationXref@ID", new AttributeInfo("xsd:string", null, "required"));
		result.put("PublicationXref@Database", new AttributeInfo("xsd:string", null, "required"));
		result.put("Attribute@Key", new AttributeInfo("xsd:string", null, "required"));
		result.put("Attribute@Value", new AttributeInfo("xsd:string", null, "required"));
		result.put("Pathway.Graphics@BoardWidth", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Pathway.Graphics@BoardHeight", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Pathway@Name", new AttributeInfo("xsd:string", null, "required"));
		result.put("Pathway@Organism", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Data-Source", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Version", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Author", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Maintainer", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Email", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@License", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Last-Modified", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("DataNode.Graphics@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("DataNode.Graphics@CenterY", new AttributeInfo("xsd:float", null, "required"));
		result.put("DataNode.Graphics@Width", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("DataNode.Graphics@Height", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("DataNode.Graphics@FontName", new AttributeInfo("xsd:string", "Arial", "optional"));
		result.put("DataNode.Graphics@FontStyle", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("DataNode.Graphics@FontDecoration", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("DataNode.Graphics@FontStrikethru", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("DataNode.Graphics@FontWeight", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("DataNode.Graphics@FontSize", new AttributeInfo("xsd:nonNegativeInteger", "12", "optional"));
		result.put("DataNode.Graphics@Align", new AttributeInfo("xsd:string", "Center", "optional"));
		result.put("DataNode.Graphics@Valign", new AttributeInfo("xsd:string", "Top", "optional"));
		result.put("DataNode.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("DataNode.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("DataNode.Graphics@LineThickness", new AttributeInfo("xsd:float", "1.0", "optional"));
		result.put("DataNode.Graphics@FillColor", new AttributeInfo("gpml:ColorType", "White", "optional"));
		result.put("DataNode.Graphics@ShapeType", new AttributeInfo("xsd:string", "Rectangle", "optional"));
		result.put("DataNode.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("DataNode.Xref@Database", new AttributeInfo("xsd:string", null, "required"));
		result.put("DataNode.Xref@ID", new AttributeInfo("xsd:string", null, "required"));
		result.put("DataNode@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("DataNode@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("DataNode@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("DataNode@TextLabel", new AttributeInfo("xsd:string", null, "required"));
		result.put("DataNode@Type", new AttributeInfo("xsd:string", "Unknown", "optional"));
		result.put("State.Graphics@RelX", new AttributeInfo("xsd:float", null, "required"));
		result.put("State.Graphics@RelY", new AttributeInfo("xsd:float", null, "required"));
		result.put("State.Graphics@Width", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("State.Graphics@Height", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("State.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("State.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("State.Graphics@LineThickness", new AttributeInfo("xsd:float", "1.0", "optional"));
		result.put("State.Graphics@FillColor", new AttributeInfo("gpml:ColorType", "White", "optional"));
		result.put("State.Graphics@ShapeType", new AttributeInfo("xsd:string", "Rectangle", "optional"));
		result.put("State.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("State.Xref@Database", new AttributeInfo("xsd:string", null, "required"));
		result.put("State.Xref@ID", new AttributeInfo("xsd:string", null, "required"));
		result.put("State@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("State@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("State@GraphRef", new AttributeInfo("xsd:IDREF", null, "optional"));
		result.put("State@TextLabel", new AttributeInfo("xsd:string", null, "required"));
		result.put("State@StateType", new AttributeInfo("xsd:string", "Unknown", "optional"));
		result.put("GraphicalLine.Graphics.Point@X", new AttributeInfo("xsd:float", null, "required"));
		result.put("GraphicalLine.Graphics.Point@Y", new AttributeInfo("xsd:float", null, "required"));
		result.put("GraphicalLine.Graphics.Point@RelX", new AttributeInfo("xsd:float", null, "optional"));
		result.put("GraphicalLine.Graphics.Point@RelY", new AttributeInfo("xsd:float", null, "optional"));
		result.put("GraphicalLine.Graphics.Point@GraphRef", new AttributeInfo("xsd:IDREF", null, "optional"));
		result.put("GraphicalLine.Graphics.Point@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("GraphicalLine.Graphics.Point@ArrowHead", new AttributeInfo("xsd:string", "Line", "optional"));
		result.put("GraphicalLine.Graphics.Anchor@Position", new AttributeInfo("xsd:float", null, "required"));
		result.put("GraphicalLine.Graphics.Anchor@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("GraphicalLine.Graphics.Anchor@Shape", new AttributeInfo("xsd:string", "ReceptorRound", "optional"));
		result.put("GraphicalLine.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("GraphicalLine.Graphics@LineThickness", new AttributeInfo("xsd:float", null, "optional"));
		result.put("GraphicalLine.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("GraphicalLine.Graphics@ConnectorType", new AttributeInfo("xsd:string", "Straight", "optional"));
		result.put("GraphicalLine.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("GraphicalLine@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("GraphicalLine@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("GraphicalLine@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("GraphicalLine@Type", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Interaction.Graphics.Point@X", new AttributeInfo("xsd:float", null, "required"));
		result.put("Interaction.Graphics.Point@Y", new AttributeInfo("xsd:float", null, "required"));
		result.put("Interaction.Graphics.Point@RelX", new AttributeInfo("xsd:float", null, "optional"));
		result.put("Interaction.Graphics.Point@RelY", new AttributeInfo("xsd:float", null, "optional"));
		result.put("Interaction.Graphics.Point@GraphRef", new AttributeInfo("xsd:IDREF", null, "optional"));
		result.put("Interaction.Graphics.Point@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Interaction.Graphics.Point@ArrowHead", new AttributeInfo("xsd:string", "Line", "optional"));
		result.put("Interaction.Graphics.Anchor@Position", new AttributeInfo("xsd:float", null, "required"));
		result.put("Interaction.Graphics.Anchor@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Interaction.Graphics.Anchor@Shape", new AttributeInfo("xsd:string", "ReceptorRound", "optional"));
		result.put("Interaction.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("Interaction.Graphics@LineThickness", new AttributeInfo("xsd:float", null, "optional"));
		result.put("Interaction.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("Interaction.Graphics@ConnectorType", new AttributeInfo("xsd:string", "Straight", "optional"));
		result.put("Interaction.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("Interaction.Xref@Database", new AttributeInfo("xsd:string", null, "required"));
		result.put("Interaction.Xref@ID", new AttributeInfo("xsd:string", null, "required"));
		result.put("Interaction@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Interaction@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Interaction@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Interaction@Type", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Label.Graphics@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("Label.Graphics@CenterY", new AttributeInfo("xsd:float", null, "required"));
		result.put("Label.Graphics@Width", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Label.Graphics@Height", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Label.Graphics@FontName", new AttributeInfo("xsd:string", "Arial", "optional"));
		result.put("Label.Graphics@FontStyle", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Label.Graphics@FontDecoration", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Label.Graphics@FontStrikethru", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Label.Graphics@FontWeight", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Label.Graphics@FontSize", new AttributeInfo("xsd:nonNegativeInteger", "12", "optional"));
		result.put("Label.Graphics@Align", new AttributeInfo("xsd:string", "Center", "optional"));
		result.put("Label.Graphics@Valign", new AttributeInfo("xsd:string", "Top", "optional"));
		result.put("Label.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("Label.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("Label.Graphics@LineThickness", new AttributeInfo("xsd:float", "1.0", "optional"));
		result.put("Label.Graphics@FillColor", new AttributeInfo("gpml:ColorType", "Transparent", "optional"));
		result.put("Label.Graphics@ShapeType", new AttributeInfo("xsd:string", "None", "optional"));
		result.put("Label.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("Label@Href", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Label@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Label@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Label@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Label@TextLabel", new AttributeInfo("xsd:string", null, "required"));
		result.put("Shape.Graphics@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("Shape.Graphics@CenterY", new AttributeInfo("xsd:float", null, "required"));
		result.put("Shape.Graphics@Width", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Shape.Graphics@Height", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Shape.Graphics@FontName", new AttributeInfo("xsd:string", "Arial", "optional"));
		result.put("Shape.Graphics@FontStyle", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Shape.Graphics@FontDecoration", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Shape.Graphics@FontStrikethru", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Shape.Graphics@FontWeight", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Shape.Graphics@FontSize", new AttributeInfo("xsd:nonNegativeInteger", "12", "optional"));
		result.put("Shape.Graphics@Align", new AttributeInfo("xsd:string", "Center", "optional"));
		result.put("Shape.Graphics@Valign", new AttributeInfo("xsd:string", "Top", "optional"));
		result.put("Shape.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("Shape.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("Shape.Graphics@LineThickness", new AttributeInfo("xsd:float", "1.0", "optional"));
		result.put("Shape.Graphics@FillColor", new AttributeInfo("gpml:ColorType", "Transparent", "optional"));
		result.put("Shape.Graphics@ShapeType", new AttributeInfo("xsd:string", null, "required"));
		result.put("Shape.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("Shape.Graphics@Rotation", new AttributeInfo("gpml:RotationType", "Top", "optional"));
		result.put("Shape@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Shape@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Shape@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Shape@TextLabel", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Group@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Group@GroupId", new AttributeInfo("xsd:string", null, "required"));
		result.put("Group@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Group@Style", new AttributeInfo("xsd:string", "None", "optional"));
		result.put("Group@TextLabel", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Group@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("InfoBox@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("InfoBox@CenterY", new AttributeInfo("xsd:float", null, "required"));
		result.put("Legend@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("Legend@CenterY", new AttributeInfo("xsd:float", null, "required"));
		/* END OF AUTO-GENERATED CONTENT */

		return result;
	}

	@Override
	protected Map<String, AttributeInfo> getAttributeInfo() {
		return ATTRIBUTE_INFO;
	}

	@Override
	protected void readMappInfoDataVariable(Pathway p, Element e) throws ConverterException {
		p.setLicense(getAttribute("Pathway", "License", e));
	}

	@Override
	protected void writeMappInfoVariable(Element root, Pathway p) throws ConverterException {
		setAttribute("Pathway", "License", root, p.getLicense());
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	private void readPathwayElement(PathwayElement o, Element e) throws ConverterException {
		writeGraphId(o, e); // GraphId
		readComments(o, e);
		readBiopaxRef(o, e);
		writeAttributes(o, e);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	private void writePathwayElement(PathwayElement o, Element e) throws ConverterException {
		readGraphId(o, e); // GraphId
		writeComments(o, e);
		writeBiopaxRef(o, e);
		writeAttributes(o, e);
	}

	/**
	 * DataNode, Label, Shape, Group
	 * 
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	private void writeShapedElement(ShapedElement o, Element e) throws ConverterException {
		writeRectProperty(o, e);
		writeFontProperty(o, e);
		writeShapeStyleProperty(o, e);
	}

	/**
	 * DataNode, Label, Shape, Group
	 * 
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	private void readShapedElement(ShapedElement o, Element e) throws ConverterException {
		readRectProperty(o, e);
		readFontProperty(o, e);
		readShapeStyleProperty(o, e);
	}

	/**
	 *
	 */
	public Element createJdomElement(PathwayElement o) throws ConverterException {
		Element e = null;
		switch (o.getObjectType()) {
		case DATANODE:
			e = new Element("DataNode", getGpmlNamespace());
			updateCommon(o, e);
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			e.addContent(new Element("Xref", getGpmlNamespace()));
			updateShapePosition(o, e);
			updateShapeCommon(o, e);
			updateDataNode(o, e); // Type & Xref
			updateGroupRef(o, e);
			break;
		case STATE:
			e = new Element("State", getGpmlNamespace());
			updateCommon(o, e);
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			e.addContent(new Element("Xref", getGpmlNamespace()));
			updateStateData(o, e);
			updateShapeCommon(o, e);
			break;
		case SHAPE:
			e = new Element("Shape", getGpmlNamespace());
			updateCommon(o, e);
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			updateShapePosition(o, e);
			updateShapeCommon(o, e);
			updateRotation(o, e);
			updateGroupRef(o, e);
			break;
		case LINE:
			e = new Element("Interaction", getGpmlNamespace());
			updateCommon(o, e);
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			e.addContent(new Element("Xref", getGpmlNamespace()));
			updateLine(o, e); // Xref
			updateLineData(o, e);
			updateLineStyle(o, e);
			writeGraphId(o, e);
			updateGroupRef(o, e);
			break;
		case GRAPHLINE:
			e = new Element("GraphicalLine", getGpmlNamespace());
			updateCommon(o, e);
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			updateLineData(o, e);
			updateLineStyle(o, e);
			writeGraphId(o, e);
			updateGroupRef(o, e);
			break;
		case LABEL:
			e = new Element("Label", getGpmlNamespace());
			updateCommon(o, e);
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			updateShapePosition(o, e);
			updateShapeCommon(o, e);
			updateHref(o, e);
			updateGroupRef(o, e);
			break;
		case LEGEND:
			e = new Element("Legend", getGpmlNamespace());
			updateSimpleCenter(o, e);
			break;
		case INFOBOX:
			e = new Element("InfoBox", getGpmlNamespace());
			updateSimpleCenter(o, e);
			break;
		case GROUP:
			e = new Element("Group", getGpmlNamespace());
			updateCommon(o, e);
			updateGroup(o, e);
			updateGroupRef(o, e);
			break;
		case BIOPAX:
			e = new Element("Biopax", getGpmlNamespace());
			updateBiopax(o, e);
			break;
		}
		if (e == null) {
			throw new ConverterException("Error creating jdom element with objectType " + o.getObjectType());
		}
		return e;
	}

	/**
	 * Create a single PathwayElement based on a piece of Jdom tree. Used also by
	 * Patch utility Pathway p may be null
	 */
	public PathwayElement mapElement(Element e, Pathway p) throws ConverterException {
		String tag = e.getName();
		if (tag.equalsIgnoreCase("Interaction")) {
			tag = "Line";
		}
		ObjectType ot = ObjectType.getTagMapping(tag);
		if (ot == null) {
			// do nothing. This could be caused by
			// tags <comment> or <graphics> that appear
			// as subtags of <pathway>
			return null;
		}

		PathwayElement o = PathwayElement.createPathwayElement(ot);
		if (p != null) {
			p.add(o);
		}

		switch (e) {
		case DATANODE:
			mapCommon(o, e);
			mapShapePosition(o, e);
			mapShapeCommon(o, e);
			mapDataNode(o, e);
			mapGroupRef(o, e);
			break;
		case STATE:
			mapCommon(o, e);
			mapStateData(o, e);
			mapShapeCommon(o, e);
			break;
		case LABEL:
			mapCommon(o, e);
			mapShapePosition(o, e);
			mapShapeCommon(o, e);
			mapGroupRef(o, e);
			mapHref(o, e);
			break;
		case LINE:
			mapCommon(o, e);
			mapLine(o, e);
			mapLineData(o, e); // Points, ConnectorType, ZOrder
			mapLineStyle(o, e); // LineStyle, LineThickness, Color
			mapGraphId(o, e);
			mapGroupRef(o, e);
			break;
		case GRAPHLINE:
			mapCommon(o, e);
			mapLineData(o, e); // Points, ConnectorType, ZOrder
			mapLineStyle(o, e); // LineStyle, LineThickness, Color
			mapGraphId(o, e);
			mapGroupRef(o, e);
			break;
		case MAPPINFO:
			mapCommon(o, e);
			mapMappInfoData(o, e);
			break;
		case SHAPE:
			mapCommon(o, e);
			mapShapePosition(o, e);
			mapShapeCommon(o, e);
			mapRotation(o, e);
			mapGroupRef(o, e);
			break;
		case LEGEND:
			mapSimpleCenter(o, e);
			break;
		case INFOBOX:
			mapSimpleCenter(o, e);
			break;
		case GROUP:
			mapCommon(o, e);
			mapGroupRef(o, e);
			mapGroup(o, e);
			break;
		case BIOPAX:
			mapBiopax(o, e, p);
			break;
		default:
			throw new ConverterException("Invalid ObjectType'" + tag + "'");
		}
		return o;
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readRectProperty(ShapedElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		double centerX = Double.parseDouble(getAttribute(base + ".Graphics", "CenterX", graphics));
		double centerY = Double.parseDouble(getAttribute(base + ".Graphics", "CenterY", graphics));
		double width = Double.parseDouble(getAttribute(base + ".Graphics", "Width", graphics));
		double height = Double.parseDouble(getAttribute(base + ".Graphics", "Height", graphics));
		o.getRectProperty().getCenterXY().setX(centerX);
		o.getRectProperty().getCenterXY().setY(centerY);
		o.getRectProperty().setWidth(width);
		o.getRectProperty().setWidth(height);
	}

	// TODO WRITERECTPROPERTY?
// TODO 		setAttribute(base + ".Graphics", "ZOrder", graphics, "" + o.getZOrder());

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeRectProperty(ShapedElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		setAttribute(base + ".Graphics", "CenterX", graphics, "" + o.getRectProperty().getCenterXY().getX());
		setAttribute(base + ".Graphics", "CenterY", graphics, "" + o.getRectProperty().getCenterXY().getY());
		setAttribute(base + ".Graphics", "Width", graphics, "" + o.getRectProperty().getWidth());
		setAttribute(base + ".Graphics", "Height", graphics, "" + o.getRectProperty().getHeight());
	}

	/**
	 * Reads gpml FontAttributes from Graphics element and sets FontProperty values
	 * in model.
	 * 
	 * NB: GPML 2013a Schema does not have FontAttribute textColor.
	 * 
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readFontProperty(ShapedElement o, Element e) throws ConverterException {
		// TODO dirty hack: the fact that state doesn't allow font data is a bug
		if (e.getName().equals("State"))
			return;

		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String fontName = getAttribute(base + ".Graphics", "FontName", graphics);
		String fontWeight = getAttribute(base + ".Graphics", "FontWeight", graphics);
		String fontStyle = getAttribute(base + ".Graphics", "FontStyle", graphics);
		String fontDecoration = getAttribute(base + ".Graphics", "FontDecoration", graphics);
		String fontStrikethru = getAttribute(base + ".Graphics", "FontStrikethru", graphics);
		String fontSize = getAttribute(base + ".Graphics", "FontSize", graphics);
		String hAlignType = getAttribute(base + ".Graphics", "Align", graphics);
		String vAlignType = getAttribute(base + ".Graphics", "Valign", graphics);

		o.getFontProperty().setFontName(fontName);
		o.getFontProperty().setFontWeight(fontWeight != null && fontWeight.equals("Bold"));
		o.getFontProperty().setFontStyle(fontStyle != null && fontStyle.equals("Italic"));
		o.getFontProperty().setFontDecoration(fontDecoration != null && fontDecoration.equals("Underline"));
		o.getFontProperty().setFontStrikethru(fontStrikethru != null && fontStrikethru.equals("Strikethru"));
		o.getFontProperty().setFontSize(Integer.parseInt(fontSize));
		o.getFontProperty().setHAlign(HAlignType.fromName(hAlignType));
		o.getFontProperty().setVAlign(VAlignType.fromName(vAlignType));
	}

// TODO STATE is NOT SHAPED PROPERTY :( 
//	 TODO o.setTextLabel(getAttribute(base, "TextLabel", e));
//	TODO setAttribute(base, "TextLabel", e, o.getTextLabel());

	/**
	 * Gets FontProperty values from model and writes to gpml FontAttributes for
	 * Graphics element of the given pathway element.
	 * 
	 * @param o the given pathway element.
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeFontProperty(ShapedElement o, Element e) throws ConverterException {
		// TODO dirty hack: the fact that state doesn't allow font data is a bug
		if (e.getName().equals("State"))
			return;

		String fontName = o.getFontProperty().getFontName() == null ? "" : o.getFontProperty().getFontName();
		String fontWeight = o.getFontProperty().getFontWeight() ? "Bold" : "Normal";
		String fontStyle = o.getFontProperty().getFontStyle() ? "Italic" : "Normal";
		String fontDecoration = o.getFontProperty().getFontDecoration() ? "Underline" : "Normal";
		String fontStrikethru = o.getFontProperty().getFontStrikethru() ? "Strikethru" : "Normal";
		// TODO can font size be half size?
		String fontSize = Integer.toString((int) o.getFontProperty().getFontSize());
		String hAlignType = o.getFontProperty().getVAlign().getName();
		String vAlignType = o.getFontProperty().getHAlign().getName();

		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		setAttribute(base + ".Graphics", "FontName", graphics, fontName);
		setAttribute(base + ".Graphics", "FontWeight", graphics, fontWeight);
		setAttribute(base + ".Graphics", "FontStyle", graphics, fontStyle);
		setAttribute(base + ".Graphics", "FontDecoration", graphics, fontDecoration);
		setAttribute(base + ".Graphics", "FontStrikethru", graphics, fontStrikethru);
		setAttribute(base + ".Graphics", "FontSize", graphics, fontSize);
		setAttribute(base + ".Graphics", "Align", graphics, hAlignType);
		setAttribute(base + ".Graphics", "Valign", graphics, vAlignType);
	}

	// TODO
//	String zorder = graphics.getAttributeValue("ZOrder");
//	if (zorder != null)
//		o.setZOrder(Integer.parseInt(zorder));

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readShapeStyleProperty(ShapedElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());

		// TODO readLineColor(o, e); // Color --> borderColor

		String borderColor = getAttribute(base + ".Graphics", "Color", graphics);
		String borderStyle = getAttribute(base + ".Graphics", "LineStyle", graphics);
		String borderWidth = getAttribute(base + ".Graphics", "LineThickness", graphics);

		// fillColor
		// shapeType

		String zOrder = getAttribute(base + ".Graphics", "ZOrder", graphics);

		/**
		 * TODO: If lineStyle is double... Check for LineStyle.DOUBLE via arbitrary
		 * attribute. Deprecated in 2021 GPML.
		 */
		if ("Double".equals(o.getDynamicProperty(LineStyleType.DOUBLE_LINE_KEY))) {
			o.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
		} else {
			o.getShapeStyleProperty()
					.setBorderStyle((style.equals("Solid")) ? LineStyleType.SOLID : LineStyleType.DASHED);
		}
		o.getShapeStyleProperty().setBorderWidth(lineWidth == null ? 1.0 : Double.parseDouble(lineWidth));
		if (zorder != null)
			o.getShapeStyleProperty().setZOrder(Integer.parseInt(zOrder));
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeShapeStyleProperty(ShapedElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());

		// TODO border COLOR
		// writeColor(o, e);
		String borderStyle = o.getShapeStyleProperty().getBorderStyle() != LineStyleType.DASHED ? "Solid" : "Broken";
		String borderWidth = String.valueOf(o.getShapeStyleProperty().getBorderWidth());
		// fillColor
		// shapeType
		String zOrder = String.valueOf(o.getShapeStyleProperty().getZOrder());

		setAttribute(base + ".Graphics", "LineStyle", graphics, borderStyle);
		setAttribute(base + ".Graphics", "LineThickness", graphics, borderWidth);
		// TODO ConnectorType enum
		// fillColor
		// shapeType
		setAttribute("Interaction.Graphics", "ZOrder", graphics, zOrder);

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readLineStyleProperty(LineElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());

		// TODO lineColor
		String lineColor = getAttribute(base + ".Graphics", "Color", graphics);
		String lineStyle = getAttribute(base + ".Graphics", "LineStyle", graphics);
		String lineWidth = getAttribute(base + ".Graphics", "LineThickness", graphics);
		String connectorType = getAttribute("Interaction.Graphics", "ConnectorType", graphics);
		String zOrder = getAttribute(base + ".Graphics", "ZOrder", graphics);

		readLineColor(o, e); // Color --> lineColor

		/**
		 * TODO: If lineStyle is double... Check for LineStyle.DOUBLE via arbitrary
		 * attribute. Deprecated in 2021 GPML.
		 */
		if ("Double".equals(o.getDynamicProperty(LineStyleType.DOUBLE_LINE_KEY))) {
			o.getLineStyleProperty().setLineStyle(LineStyleType.DOUBLE);
		} else {
			o.getLineStyleProperty().setLineStyle((style.equals("Solid")) ? LineStyleType.SOLID : LineStyleType.DASHED);
		}
		o.getLineStyleProperty().setLineWidth(lineWidth == null ? 1.0 : Double.parseDouble(lineWidth));
		o.getLineStyleProperty().setConnectorType(ConnectorType.fromName(connectorType));
		if (zorder != null)
			o.getLineStyleProperty().setZOrder(Integer.parseInt(zOrder));
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeLineStyleProperty(LineElement o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String lineStyle = o.getLineStyleProperty().getLineStyle() != LineStyleType.DASHED ? "Solid" : "Broken";
		String lineWidth = String.valueOf(o.getLineStyleProperty().getLineWidth());
		String connectorType = o.getLineStyleProperty().getConnectorType().getName();
		String zOrder = String.valueOf(o.getLineStyleProperty().getZOrder());

		// TODO COLOR
		writeColor(o, e);

		setAttribute(base + ".Graphics", "LineStyle", graphics, lineStyle);
		setAttribute(base + ".Graphics", "LineThickness", graphics, lineWidth);
		// TODO ConnectorType enum
		setAttribute("Interaction.Graphics", "ConnectorType", graphics, connectorType);
		setAttribute("Interaction.Graphics", "ZOrder", graphics, zOrder);

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readDataNode(DataNode o, Element e) throws ConverterException {

		// TODO elementRef
		String textLabel = getAttribute("DataNode", "TextLabel", e);
		String type = getAttribute("DataNode", "Type", e);
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = getAttribute("DataNode.Xref", "ID", xref);
		String dataSource = getAttribute("DataNode.Xref", "Database", xref);
		o.setTextLabel(textLabel);
		o.setType(DataNodeType.fromName(type));
		o.setXref(identifier, dataSource);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeDataNode(DataNode o, Element e) throws ConverterException {

		// TODO elementRef
		String textLabel = o.getTextLabel();
		String type = o.getType().getName();
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = o.getXref().getId();
		String dataSource = o.getXref().getDataSource().getFullName();
		setAttribute("DataNode", "TextLabel", e, textLabel);
		setAttribute("DataNode", "Type", e, type);
		setAttribute("DataNode.Xref", "Database", xref, dataSource == null ? "" : dataSource);
		setAttribute("DataNode.Xref", "ID", xref, identifier);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readLabel(Label o, Element e) throws ConverterException {
		String textLabel = getAttribute("Label", "TextLabel", e);
		String href = getAttribute("Label", "Href", e);
		o.setTextLabel(textLabel);
		o.setHref(href);
	}

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
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readShape(Shape o, Element e) throws ConverterException {
		String textLabel = getAttribute("Shape", "TextLabel", e);
		String type = getAttribute("Shape", "Type", e);
		String rotation = getAttribute("Shape", "Rotation", e);
		o.setTextLabel(textLabel);
		// TODO ShapeType
		o.setType(ShapeType.fromName(type));
		// TODO Rotation
		o.setRotation(rotation);

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeShape(Shape o, Element e) throws ConverterException {
		String textLabel = o.getTextLabel();
		// TODO Shape type
		// TODO rotation
		setAttribute("Shape", "TextLabel", e, textLabel);
		// TODO ShapeType
		setAttribute("Shape", "Type", e, type);
		setAttribute("Shape", "Rotation", e, rotation);

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readRotation(Shape o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String rotation = getAttribute("Shape.Graphics", "Rotation", graphics);
		double result;
		if (rotation.equals("Top")) {
			result = 0.0;
		} else if (rotation.equals("Right")) {
			result = 0.5 * Math.PI;
		} else if (rotation.equals("Bottom")) {
			result = Math.PI;
		} else if (rotation.equals("Left")) {
			result = 1.5 * Math.PI;
		} else {
			result = Double.parseDouble(rotation);
		}
		o.setRotation(result);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeRotation(PathwayElement o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());
		setAttribute("Shape.Graphics", "Rotation", graphics, Double.toString(o.getRotation()));
	}

	/**
	 * Converts deprecated shapes to contemporary analogs. This allows us to
	 * maintain backward compatibility while at the same time cleaning up old shape
	 * usages.
	 * 
	 */
	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readShapeType(Shape o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		IShape s = ShapeRegistry.fromName(getAttribute(base + ".Graphics", "ShapeType", graphics));
		if (ShapeType.DEPRECATED_MAP.containsKey(s)) {
			s = ShapeType.DEPRECATED_MAP.get(s);
			o.setShapeType(s);
			if (s.equals(ShapeType.ROUNDED_RECTANGLE) || s.equals(ShapeType.OVAL)) {
				o.setLineStyle(LineStyleType.DOUBLE);
				o.setLineThickness(3.0);
				o.setColor(Color.LIGHT_GRAY);
			}
		} else {
			o.setShapeType(s);
			mapLineStyle(o, e); // LineStyle
		}
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
	protected void readInteraction(Interaction o, Element e) throws ConverterException {
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = getAttribute("Interaction.Xref", "ID", xref);
		String dataSource = getAttribute("Interaction.Xref", "Database", xref);
		o.setXref(identifier, dataSource);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeInteraction(Interaction o, Element e) throws ConverterException {
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
	protected void readState(State o, Element e) throws ConverterException {
//		Xref
//		relX
//		relY
//		width
//		height
//		FontAttributes
//		ShapeAttributes
//		elementRef
//		textLabel
//		type

		String ref = getAttribute("State", "GraphRef", e);
		if (ref != null) {
			o.setGraphRef(ref);
		}

		Element graphics = e.getChild("Graphics", e.getNamespace());

		o.setRelX(Double.parseDouble(getAttribute("State.Graphics", "RelX", graphics)));
		o.setRelY(Double.parseDouble(getAttribute("State.Graphics", "RelY", graphics)));
		o.setWidth(Double.parseDouble(getAttribute("State.Graphics", "Width", graphics)));
		o.setHeight(Double.parseDouble(getAttribute("State.Graphics", "Height", graphics)));

		o.setType(getAttribute("State", "StateType", e));
		o.setElementRef(getAttribute("State", "GraphRef", e));
		Element xref = e.getChild("Xref", e.getNamespace());
		o.setElementID(getAttribute("State.Xref", "ID", xref));
		o.setDataSource(DataSource.getExistingByFullName(getAttribute("State.Xref", "Database", xref)));
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeState(State o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());

		setAttribute(base + ".Graphics", "RelX", graphics, "" + o.getRelX());
		setAttribute(base + ".Graphics", "RelY", graphics, "" + o.getRelY());
		setAttribute(base + ".Graphics", "Width", graphics, "" + o.getWidth());
		setAttribute(base + ".Graphics", "Height", graphics, "" + o.getHeight());

		setAttribute("State", "StateType", e, o.getDataNodeType());
		setAttribute("State", "GraphRef", e, o.getElementRef());
		Element xref = e.getChild("Xref", e.getNamespace());
		String database = o.getDataSource() == null ? "" : o.getDataSource().getFullName();
		setAttribute("State.Xref", "Database", xref, database == null ? "" : database);
		setAttribute("State.Xref", "ID", xref, o.getElementID());
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void mapLineData(PathwayElement o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());

		List<MPoint> mPoints = new ArrayList<MPoint>();

		String startType = null;
		String endType = null;

		List<Element> pointElements = graphics.getChildren("Point", e.getNamespace());
		for (int i = 0; i < pointElements.size(); i++) {
			Element pe = pointElements.get(i);
			MPoint mp = o.new MPoint(Double.parseDouble(getAttribute("Interaction.Graphics.Point", "X", pe)),
					Double.parseDouble(getAttribute("Interaction.Graphics.Point", "Y", pe)));
			mPoints.add(mp);
			String ref = getAttribute("Interaction.Graphics.Point", "GraphRef", pe);
			if (ref != null) {
				mp.setGraphRef(ref);
				String srx = pe.getAttributeValue("RelX");
				String sry = pe.getAttributeValue("RelY");
				if (srx != null && sry != null) {
					mp.setRelativePosition(Double.parseDouble(srx), Double.parseDouble(sry));
				}
			}

			if (i == 0) {
				startType = getAttribute("Interaction.Graphics.Point", "ArrowHead", pe);
			} else if (i == pointElements.size() - 1) {
				endType = getAttribute("Interaction.Graphics.Point", "ArrowHead", pe);
			}
		}

		o.setMPoints(mPoints);
		o.setStartLineType(LineType.fromName(startType));
		o.setEndLineType(LineType.fromName(endType));

		// Map anchors
		List<Element> anchors = graphics.getChildren("Anchor", e.getNamespace());
		for (Element ae : anchors) {
			double position = Double.parseDouble(getAttribute("Interaction.Graphics.Anchor", "Position", ae));
			MAnchor anchor = o.addMAnchor(position);
			mapGraphId(anchor, ae);
			String shape = getAttribute("Interaction.Graphics.Anchor", "Shape", ae);
			if (shape != null) {
				anchor.setShape(AnchorType.fromName(shape));
			}
		}
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void updateLineData(PathwayElement o, Element e) throws ConverterException {
		Element jdomGraphics = e.getChild("Graphics", e.getNamespace());
		List<MPoint> mPoints = o.getMPoints();

		for (int i = 0; i < mPoints.size(); i++) {
			MPoint mp = mPoints.get(i);
			Element pe = new Element("Point", e.getNamespace());
			jdomGraphics.addContent(pe);
			setAttribute("Interaction.Graphics.Point", "X", pe, Double.toString(mp.getX()));
			setAttribute("Interaction.Graphics.Point", "Y", pe, Double.toString(mp.getY()));
			if (mp.getElementRef() != null && !mp.getElementRef().equals("")) {
				setAttribute("Interaction.Graphics.Point", "GraphRef", pe, mp.getElementRef());
				setAttribute("Interaction.Graphics.Point", "RelX", pe, Double.toString(mp.getRelX()));
				setAttribute("Interaction.Graphics.Point", "RelY", pe, Double.toString(mp.getRelY()));
			}
			if (i == 0) {
				setAttribute("Interaction.Graphics.Point", "ArrowHead", pe, o.getStartLineType().getName());
			} else if (i == mPoints.size() - 1) {
				setAttribute("Interaction.Graphics.Point", "ArrowHead", pe, o.getEndLineType().getName());
			}
		}

		for (MAnchor anchor : o.getMAnchors()) {
			Element ae = new Element("Anchor", e.getNamespace());
			setAttribute("Interaction.Graphics.Anchor", "Position", ae, Double.toString(anchor.getPosition()));
			setAttribute("Interaction.Graphics.Anchor", "Shape", ae, anchor.getShape().getName());
			updateGraphId(anchor, ae);
			jdomGraphics.addContent(ae);
		}

	}

	/**
	 *
	 */
	public Document createJdom(Pathway data) throws ConverterException {
		Document doc = new Document();

		Element root = new Element("Pathway", getGpmlNamespace());
		doc.setRootElement(root);

		List<Element> elementList = new ArrayList<Element>();

		List<PathwayElement> pathwayElements = data.getDataObjects();
		Collections.sort(pathwayElements);
		for (PathwayElement o : pathwayElements) {
			if (o.getObjectType() == ObjectType.MAPPINFO) {
				updateMappInfo(root, o);
			} else {
				Element e = createJdomElement(o);
				if (e != null)
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
	}

	public class BiopaxAttributeComparator implements Comparator<Element> {
		public int compare(Element e1, Element e2) {
			String id1 = "";
			if (e1.getAttributes().size() > 0) {
				id1 = e1.getAttributes().get(0).getValue();
			}
			String id2 = "";
			if (e2.getAttributes().size() > 0) {
				id2 = e2.getAttributes().get(0).getValue();
			}
			return id1.compareTo(id2);
		}
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

	/**
	 * @param o
	 * @param e
	 */
	protected void mapSimpleCenter(PathwayElement o, Element e) {
		o.setCenterX(Double.parseDouble(e.getAttributeValue("CenterX")));
		o.setCenterY(Double.parseDouble(e.getAttributeValue("CenterY")));
	}

	/**
	 * @param o
	 * @param e
	 */
	protected void updateSimpleCenter(PathwayElement o, Element e) {
		if (e != null) {
			e.setAttribute("CenterX", Double.toString(o.getCenterX()));
			e.setAttribute("CenterY", Double.toString(o.getCenterY()));
		}
	}

	/**
	 * @param o
	 * @param e
	 * @param p
	 * @throws ConverterException
	 */
	protected void mapBiopax(PathwayElement o, Element e, Pathway p) throws ConverterException {
		// this method clones all content,
		// getContent will leave them attached to the parent, which we don't want
		// We can safely remove them, since the JDOM element isn't used anymore after
		// this method
		Element root = new Element("RDF", GpmlFormat.RDF);
		root.addNamespaceDeclaration(GpmlFormat.RDFS);
		root.addNamespaceDeclaration(GpmlFormat.RDF);
		root.addNamespaceDeclaration(GpmlFormat.OWL);
		root.addNamespaceDeclaration(GpmlFormat.BIOPAX);
		root.setAttribute(new Attribute("base", getGpmlNamespace().getURI() + "#", Namespace.XML_NAMESPACE));
		// Element owl = new Element("Ontology", OWL);
		// owl.setAttribute(new Attribute("about", "", RDF));
		// Element imp = new Element("imports", OWL);
		// imp.setAttribute(new Attribute("resource", BIOPAX.getURI(), RDF));
		// owl.addContent(imp);
		// root.addContent(owl);

		root.addContent(e.cloneContent());
		Document bp = new Document(root);

		((BiopaxElement) o).setBiopax(bp);

		for (Object f : e.getChildren("openControlledVocabulary", GpmlFormat.BIOPAX)) {
			p.addOntologyTag(((Element) f).getChild("ID", GpmlFormat.BIOPAX).getText(),
					((Element) f).getChild("TERM", GpmlFormat.BIOPAX).getText(),
					((Element) f).getChild("Ontology", GpmlFormat.BIOPAX).getText());
		}
	}

}
