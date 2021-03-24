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
//		
//		
//		private String title = "untitled";
//		private String organism = null;
//		private String source = null;
//		private String version = null;
//		private String license = null;
//	}

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
		writeElementId(o, e); // GraphId
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
		readElementId(o, e); // GraphId
		writeComment(o, e); // Comment
		writeBiopaxRef(o, e); // AnnotationRef, CitationRef, Evidence
		writeDynamicProperty(o, e);
	}


	
	// NOT PATHWAYELEMENTS
//	case LEGEND:
//	mapSimpleCenter(o, e);
//	break;
//	case INFOBOX:
//
//	readInfoBox (o, e);
//	break;
//case MAPPINFO:
//	mapCommon(o, e);
//	mapMappInfoData(o, e);
//	break;

	/**
	 * 
	 * Create a single PathwayElement based on a piece of Jdom tree. Used also by
	 * Patch utility. Pathway p may be null
	 */
	public PathwayElement mapElement(Element e, Pathway p) throws ConverterException {		
	//		Creates a pathway element
	//		Adds pathway element to given Pathway. 
	//		reads(pathway element o, element e)... In the process it sets the pathway element?
	//		Finally this pathway element is returned. 
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
		o.getFontProperty().setFontSize(Integer.parseInt(fontSize)); // TODO Integer?
		o.getFontProperty().setHAlign(HAlignType.fromName(hAlignType));
		o.getFontProperty().setVAlign(VAlignType.fromName(vAlignType));
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
	 * DataNode, Label, Shape, Group
	 * 
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	private void readShapedElement(ShapedElement o, Element e) throws ConverterException {
		readPathwayElement(o, e); // TODO: // ElementId, CommentGroup
		String base = e.getName();
		String groupRef = getAttribute(base, "GroupRef", e);
		o.setGroupRef(groupRef);
		readRectProperty(o, e);
		readFontProperty(o, e);
		readShapeStyleProperty(o, e);
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
		String groupRef = o.getParentGroup().getElementId();
		setAttribute(base, "GroupRef", e, groupRef);
		writeRectProperty(o, e);
		writeFontProperty(o, e);
		writeShapeStyleProperty(o, e);
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
	 */
	protected void readInfoBox(InfoBox o, Element e) {
		String centerX = e.getAttributeValue("CenterX");
		String centerY = e.getAttributeValue("CenterY");
		o.getCenterXY().setX(Double.parseDouble(centerX));
		o.getCenterXY().setY(Double.parseDouble(centerY));
	}

	/**
	 * @param o
	 * @param e
	 */
	protected void writeInfoBox(InfoBox o, Element e) {
		String centerX = Double.toString(o.getCenterXY().getX());
		String centerY = Double.toString(o.getCenterXY().getY());
		if (e != null) {
			e.setAttribute("CenterX", centerX);
			e.setAttribute("CenterY", centerY);
		}
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readDataNode(DataNode o, Element e) throws ConverterException {
		// TODO STATE
		// TODO elementRef
		String textLabel = getAttribute("DataNode", "TextLabel", e);
		String type = getAttribute("DataNode", "Type", e);
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = getAttribute("DataNode.Xref", "ID", xref);
		String dataSource = getAttribute("DataNode.Xref", "Database", xref);
		o.setTextLabel(textLabel);
		o.setType(DataNodeType.fromName(type));
		o.setXref(identifier, dataSource);
		readShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty
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
		setAttribute("DataNode", "TextLabel", e, textLabel);
		setAttribute("DataNode", "Type", e, type);
		setAttribute("DataNode.Xref", "Database", xref, dataSource == null ? "" : dataSource);
		setAttribute("DataNode.Xref", "ID", xref, identifier);
//		writeCommon(o, e); //Comment, Attribute, Biopax 
		writeShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readState(State o, Element e) throws ConverterException {
		readPathwayElement(o, e); // TODO: // ElemenId, CommentGroup
		String elementRef = getAttribute("State", "GraphRef", e); // TODO elementRef

		if (elementRef != null) {
			o.setElementRef(elementRef);
		}
		o.setTextLabel(getAttribute("State", "TextLabel", e));
		o.setType(StateType.fromName(getAttribute("State", "StateType", e))); // TODO Enum
		o.setElementRef(getAttribute("State", "GraphRef", e));
		/** Graphics */
		Element graphics = e.getChild("Graphics", e.getNamespace());
		o.setRelX(Double.parseDouble(getAttribute("State.Graphics", "RelX", graphics)));
		o.setRelY(Double.parseDouble(getAttribute("State.Graphics", "RelY", graphics)));
		o.setWidth(Double.parseDouble(getAttribute("State.Graphics", "Width", graphics)));
		o.setHeight(Double.parseDouble(getAttribute("State.Graphics", "Height", graphics)));
		/** FontProperty */
		String fontWeight = getAttribute("State.Graphics", "FontWeight", graphics);
		String fontStyle = getAttribute("State.Graphics", "FontStyle", graphics);
		String fontDecoration = getAttribute("State.Graphics", "FontDecoration", graphics);
		String fontStrikethru = getAttribute("State.Graphics", "FontStrikethru", graphics);
		o.getFontProperty().setFontName(getAttribute("State.Graphics", "FontName", graphics));
		o.getFontProperty().setFontWeight(fontWeight != null && fontWeight.equals("Bold"));
		o.getFontProperty().setFontStyle(fontStyle != null && fontStyle.equals("Italic"));
		o.getFontProperty().setFontDecoration(fontDecoration != null && fontDecoration.equals("Underline"));
		o.getFontProperty().setFontStrikethru(fontStrikethru != null && fontStrikethru.equals("Strikethru"));
		o.getFontProperty().setFontSize(Integer.parseInt(getAttribute("State.Graphics", "FontSize", graphics))); // TODO
																													// Integer
		o.getFontProperty().setHAlign(HAlignType.fromName(getAttribute("State.Graphics", "Align", graphics)));
		o.getFontProperty().setVAlign(VAlignType.fromName(getAttribute("State.Graphics", "Valign", graphics)));
		/** TODO ShapeStyleProperty */

		/** Xref */
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = getAttribute("DataNode.Xref", "ID", xref);
		String dataSource = getAttribute("DataNode.Xref", "Database", xref);
		o.setXref(identifier, dataSource);

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeState(State o, Element e) throws ConverterException {
		writePathwayElement(o, e); // TODO: // ElemenId, CommentGroup
		// TODO elementRef
		setAttribute("State", "GraphRef", e, o.getElementRef()); // TODO Check if null
		setAttribute("State", "TextLabel", e, o.getTextLabel());
		setAttribute("State", "StateType", e, o.getType().getName());
		Element graphics = e.getChild("Graphics", e.getNamespace());
		setAttribute("State.Graphics", "RelX", graphics, "" + o.getRelX());
		setAttribute("State.Graphics", "RelY", graphics, "" + o.getRelY());
		setAttribute("State.Graphics", "Width", graphics, "" + o.getWidth());
		setAttribute("State.Graphics", "Height", graphics, "" + o.getHeight());
		/** FontProperty */
		setAttribute("State.Graphics", "FontName", graphics,
				o.getFontProperty().getFontName() == null ? "" : o.getFontProperty().getFontName());
		setAttribute("State.Graphics", "FontWeight", graphics, o.getFontProperty().getFontWeight() ? "Bold" : "Normal");
		setAttribute("State.Graphics", "FontStyle", graphics, o.getFontProperty().getFontStyle() ? "Italic" : "Normal");
		setAttribute("State.Graphics", "FontDecoration", graphics,
				o.getFontProperty().getFontDecoration() ? "Underline" : "Normal");
		setAttribute("State.Graphics", "FontStrikethru", graphics,
				o.getFontProperty().getFontStrikethru() ? "Strikethru" : "Normal");
		setAttribute("State.Graphics", "FontSize", graphics, Integer.toString((int) o.getFontProperty().getFontSize()));
		setAttribute("State.Graphics", "Align", graphics, o.getFontProperty().getVAlign().getName());
		setAttribute("State.Graphics", "Valign", graphics, o.getFontProperty().getHAlign().getName());
		/** TODO ShapeStyleProperty */
		/** Xref */
		Element xref = e.getChild("Xref", e.getNamespace());
		String dataSource = o.getXref().getDataSource().getFullName();
		setAttribute("State.Xref", "Database", xref, dataSource == null ? "" : dataSource);
		setAttribute("State.Xref", "ID", xref, o.getXref().getId());
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
		readLineElement(o, e);
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
	protected void readLineElement(LineElement o, Element e) throws ConverterException {
		readPathwayElement(o, e); /** TODO elementId, CommentGroup */

		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());

		String groupRef = getAttribute(base, "GroupRef", e); // TODO GroupRef
		o.setGroupRef(groupRef);
		readLineStyleProperty(o, e);

		String startType = null;
		String endType = null;

		// Reads the entire list
		List<Point> pts = new ArrayList<Point>(); // TODO Should be added
		List<Element> points = graphics.getChildren("Point", e.getNamespace());
		for (int i = 0; i < points.size(); i++) {
			Element point = points.get(i);
			String elementId = getAttribute(base + ".Graphics.Point", "GraphId", point); // TODO ElementId
			double x = Double.parseDouble(getAttribute(base + ".Graphics.Point", "X", point));
			double y = Double.parseDouble(getAttribute(base + ".Graphics.Point", "Y", point));
			LineType arrowHead = getAttribute("Interaction.Graphics.Point", "ArrowHead", point) == null ? LineType.LINE
					: LineType.fromName(getAttribute("Interaction.Graphics.Point", "ArrowHead", point));
			String elementRef = getAttribute(base + ".Graphics.Point", "GraphRef", point);
			if (elementRef == null) {
				Point pt = new Point(elementId, arrowHead, new Coordinate(x, y));
				pts.add(pt);
			} else {
				double relX = Double.parseDouble(point.getAttributeValue("RelX"));
				double relY = Double.parseDouble(point.getAttributeValue("RelY"));
				Point pt = new Point(elementId, arrowHead, new Coordinate(x, y), elementRef, relX, relY);
				pts.add(pt);
			}
		}

		// Reads the entire list
		List<Element> anchors = graphics.getChildren("Anchor", e.getNamespace());
		for (Element anchor : anchors) {
			String elementId = getAttribute(base + ".Graphics.Anchor", "GraphId", anchor); // TODO ElementId
			double position = Double.parseDouble(getAttribute("Interaction.Graphics.Anchor", "Position", anchor));
			Coordinate xy = new Coordinate(0, 0); // method to calculate
			AnchorType shapeType = AnchorType.fromName(getAttribute("Interaction.Graphics.Anchor", "Shape", anchor));
//			if (shape != null) {
//				a.setShape(AnchorType.fromName(shape)); //TODO default shapeType handle? 
//			}
			Anchor a = new Anchor(elementId, position, xy, shapeType);
			o.addAnchor(a);
		}
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
		String groupRef = o.getParentGroup().getElementId(); // TODO GroupRef
		setAttribute(base, "GroupRef", e, groupRef);
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
		readShapeStyleProperty(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
										// ShapeStyleProperty

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
		writeShapeStyleProperty(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
										// ShapeStyleProperty
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
		o.setType(ShapeType.fromName(type)); // TODO ShapeType
		readShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty
		// TODO Rotation
		o.setRotation(rotation);

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

	protected void readGroup(Group o, Element e) throws ConverterException {

		readShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty
		/** TODO GroupID */
		String groupId = e.getAttributeValue("GroupId");
		if ((groupId == null || groupId.equals("")) && o.getParentPathway() != null) {
			groupId = o.getParentPathway().getUniqueGroupId();
		}
		o.setGroupId(groupId);

		String textLabel = getAttribute("Group", "TextLabel", e);
		String type = getAttribute("Group", "Style", e); // NB. GroupType was named Style
		if (textLabel != null) {
			o.setTextLabel(textLabel);
		}
		o.setType(GroupType.fromName(type));

		/** Xref added in GPML2021 */
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = getAttribute("DataNode.Xref", "ID", xref);
		String dataSource = getAttribute("DataNode.Xref", "Database", xref);
		o.setXref(identifier, dataSource);

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

		List<Label> labels = p.getLabels();
		Collections.sort(labels); // TODO necessary?
		for (Label o : labels) {
			Element e = new Element("Label", getGpmlNamespace());
			e.addContent(new Element("Graphics", getGpmlNamespace()));
			writeLabel(o, e);
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
	protected void readLegend(Pathway p, Element e) {
		// TODO Dynamic Property....
	}p.getDynamicProperty(Legend_CenterX)

	{
		o.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
		String centerX = e.getAttributeValue("CenterX");
		String centerY = e.getAttributeValue("CenterY");
		o.getCenterXY().setX(Double.parseDouble(centerX));
		o.getCenterXY().setY(Double.parseDouble(centerY));
	}

	/**
	 * @param o
	 * @param e
	 */
	protected void writeLegend(Legend o, Element e) {
		String centerX = o.getCenterXY().
		String centerY = o.getCenterXY().
		if (e != null) {
			e.setAttribute("CenterX", centerX);
			e.setAttribute("CenterY", centerY);
		}
	}

	// TODO LEGEND!!!

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
