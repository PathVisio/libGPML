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
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.core.biopax.BiopaxElement;
import org.pathvisio.core.model.PathwayElement.MAnchor;
import org.pathvisio.core.model.PathwayElement.MPoint;
import org.pathvisio.core.view.ShapeRegistry;

class GpmlFormat2021 extends GpmlFormatAbstract implements GpmlFormatReader, GpmlFormatWriter 
{
	public static final GpmlFormat2021 GPML_2013A = new GpmlFormat2021 (
			"GPML2013a.xsd", Namespace.getNamespace("http://pathvisio.org/GPML/2013a")
		);

	public GpmlFormat2021(String xsdFile, Namespace ns) {
		super (xsdFile, ns);
	}

	private static final Map<String, AttributeInfo> ATTRIBUTE_INFO = initAttributeInfo();

	private static Map<String, AttributeInfo> initAttributeInfo()
	{
		Map<String, AttributeInfo> result = new HashMap<String, AttributeInfo>();
		/* ARRAY */ 
		result.put("Comment@source", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Property@key", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Property@value", new AttributeInfo ("xsd:string", null, "required"));
		result.put("AnnotationRef@elementRef", new AttributeInfo ("xsd:string", null, "required"));
		result.put("CitationRef@elementRef", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Pathway.Graphics@boardWidth", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Pathway.Graphics@boardHeight", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Pathway@title", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Pathway@organism", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway@dataSource", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway@version", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway.Author@name", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway@license", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Pathway.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("DataNodes.DataNode@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("DataNodes.DataNode@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("DataNodes.DataNode@width", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("DataNodes.DataNode@height", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("DataNodes.DataNode@textColor", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("DataNodes.DataNode@fontName", new AttributeInfo ("xsd:string", "Arial", "optional"));
		result.put("DataNodes.DataNode@fontStyle", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("DataNodes.DataNode@fontDecoration", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("DataNodes.DataNode@fontStrikethru", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("DataNodes.DataNode@fontWeight", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("DataNodes.DataNode@fontSize", new AttributeInfo ("xsd:nonNegativeInteger", "12", "optional"));
		result.put("DataNodes.DataNode@hAlign", new AttributeInfo ("xsd:string", "Center", "optional"));
		result.put("DataNodes.DataNode@vAlign", new AttributeInfo ("xsd:string", "Middle", "optional"));
		result.put("DataNodes.DataNode@borderColor", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("DataNodes.DataNode@borderStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("DataNodes.DataNode@borderWidth", new AttributeInfo ("xsd:float", "1.0", "optional"));
		result.put("DataNodes.DataNode@fillColor", new AttributeInfo ("gpml:ColorType", "White", "optional"));
		result.put("DataNodes.DataNode@shapeType", new AttributeInfo ("xsd:string", "Rectangle", "optional"));
		result.put("DataNodes.DataNode@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("DataNodes.DataNode.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("DataNodes.DataNode.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("DataNodes.DataNode@elementId", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("DataNodes.DataNode@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("DataNodes.DataNode@textLabel", new AttributeInfo ("xsd:string", null, "required"));
		result.put("DataNodes.DataNode@type", new AttributeInfo ("xsd:string", "Unknown", "optional"));
		result.put("States.State@relX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("States.State@relY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("States.State@width", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("States.State@height", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("States.State@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("States.State@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("States.State@lineThickness", new AttributeInfo ("xsd:float", "1.0", "optional"));
		result.put("States.State@fillColor", new AttributeInfo ("gpml:ColorType", "White", "optional"));
		result.put("States.State@shapeType", new AttributeInfo ("xsd:string", "Rectangle", "optional"));
		result.put("States.State@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("States.State@fontName", new AttributeInfo ("xsd:string", "Arial", "optional"));
		result.put("States.State@fontStyle", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("States.State@fontWeight", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("States.State@fontSize", new AttributeInfo ("xsd:nonNegativeInteger", "12", "optional"));
		result.put("States.State@align", new AttributeInfo ("xsd:string", "Center", "optional"));
		result.put("States.State@vAlign", new AttributeInfo ("xsd:string", "Top", "optional"));
		result.put("States.State.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("States.State.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("States.State@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("States.State@elementRef", new AttributeInfo ("xsd:IDREF", null, "optional"));
		result.put("States.State@textLabel", new AttributeInfo ("xsd:string", null, "required"));
		result.put("States.State@stateType", new AttributeInfo ("xsd:string", "Unknown", "optional"));
		result.put("GraphicalLines.GraphicalLine.Point@x", new AttributeInfo ("xsd:float", null, "required"));
		result.put("GraphicalLines.GraphicalLine.Point@y", new AttributeInfo ("xsd:float", null, "required"));
		result.put("GraphicalLines.GraphicalLine.Point@relX", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("GraphicalLines.GraphicalLine.Point@relY", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("GraphicalLines.GraphicalLine.Point@elementRef", new AttributeInfo ("xsd:IDREF", null, "optional"));
		result.put("GraphicalLines.GraphicalLine.Point@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("GraphicalLines.GraphicalLine.Point@arrowHead", new AttributeInfo ("xsd:string", "Line", "optional"));
		result.put("GraphicalLines.GraphicalLine.Anchor@position", new AttributeInfo ("xsd:float", null, "required"));
		result.put("GraphicalLines.GraphicalLine.Anchor@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("GraphicalLines.GraphicalLine.Anchor@shapeType", new AttributeInfo ("xsd:string", "ReceptorRound", "optional"));
		result.put("GraphicalLines.GraphicalLine@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("GraphicalLines.GraphicalLine@lineThickness", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("GraphicalLines.GraphicalLine@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("GraphicalLines.GraphicalLine@connectorType", new AttributeInfo ("xsd:string", "Straight", "optional"));
		result.put("GraphicalLines.GraphicalLine@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("GraphicalLines.GraphicalLine@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("GraphicalLines.GraphicalLine@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("GraphicalLines.GraphicalLine@type", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Interactions.Interaction.Point@x", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Interactions.Interaction.Point@y", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Interactions.Interaction.Point@relX", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("Interactions.Interaction.Point@relY", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("Interactions.Interaction.Point@elementRef", new AttributeInfo ("xsd:IDREF", null, "optional"));
		result.put("Interactions.Interaction.Point@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Interactions.Interaction.Point@arrowHead", new AttributeInfo ("xsd:string", "Line", "optional"));
		result.put("Interactions.Interaction.Anchor@position", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Interactions.Interaction.Anchor@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Interactions.Interaction.Anchor@shapeType", new AttributeInfo ("xsd:string", "ReceptorRound", "optional"));
		result.put("Interactions.Interaction@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("Interactions.Interaction@lineThickness", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("Interactions.Interaction@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("Interactions.Interaction@connectorType", new AttributeInfo ("xsd:string", "Straight", "optional"));
		result.put("Interactions.Interaction@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("Interactions.Interaction.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Interactions.Interaction.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Interactions.Interaction@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Interactions.Interaction@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Interactions.Interaction@type", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Labels.Label@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Labels.Label@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Labels.Label@width", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Labels.Label@height", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Labels.Label@fontName", new AttributeInfo ("xsd:string", "Arial", "optional"));
		result.put("Labels.Label@fontStyle", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Labels.Label@fontWeight", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Labels.Label@fontSize", new AttributeInfo ("xsd:nonNegativeInteger", "12", "optional"));
		result.put("Labels.Label@align", new AttributeInfo ("xsd:string", "Center", "optional"));
		result.put("Labels.Label@vAlign", new AttributeInfo ("xsd:string", "Top", "optional"));
		result.put("Labels.Label@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("Labels.Label@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("Labels.Label@lineThickness", new AttributeInfo ("xsd:float", "1.0", "optional"));
		result.put("Labels.Label@fillColor", new AttributeInfo ("gpml:ColorType", "Transparent", "optional"));
		result.put("Labels.Label@shapeType", new AttributeInfo ("xsd:string", "None", "optional"));
		result.put("Labels.Label@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("Labels.Label@href", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Labels.Label@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Labels.Label@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Labels.Label@textLabel", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Shapes.Shape@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Shapes.Shape@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Shapes.Shape@width", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Shapes.Shape@height", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Shapes.Shape@fontName", new AttributeInfo ("xsd:string", "Arial", "optional"));
		result.put("Shapes.Shape@fontStyle", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Shapes.Shape@fontStrikethru", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Shapes.Shape@fontWeight", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Shapes.Shape@fontSize", new AttributeInfo ("xsd:nonNegativeInteger", "12", "optional"));
		result.put("Shapes.Shape@align", new AttributeInfo ("xsd:string", "Center", "optional"));
		result.put("Shapes.Shape@vAlign", new AttributeInfo ("xsd:string", "Top", "optional"));
		result.put("Shapes.Shape@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("Shapes.Shape@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("Shapes.Shape@lineThickness", new AttributeInfo ("xsd:float", "1.0", "optional"));
		result.put("Shapes.Shape@fillColor", new AttributeInfo ("gpml:ColorType", "Transparent", "optional"));
		result.put("Shapes.Shape@shapeType", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Shapes.Shape@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("Shapes.Shape@rotation", new AttributeInfo ("gpml:RotationType", "Top", "optional"));
		result.put("Shapes.Shape@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Shapes.Shape@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Shapes.Shape@textLabel", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Groups.Group@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Groups.Group@groupType", new AttributeInfo ("xsd:string", "None", "optional"));
		result.put("Groups.Group@textLabel", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Groups.Group@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("InfoBox@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("InfoBox@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Legend@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Legend@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Citations.Citation.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation@citationID", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation@title", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation@url", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation@source", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Citations.Citation@year", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Annotations.Annotation@ID", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Annotations.Annotation@term", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Annotations.Annotation@ontology", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Annotations.Annotation@ontologyTermID", new AttributeInfo ("xsd:string", null, "required"));
		/* END OF ARRAY */

		return result;
	}

	@Override
	protected Map<String, AttributeInfo> getAttributeInfo() 
	{
		return ATTRIBUTE_INFO;
	}

	@Override
	protected void mapMappInfoDataVariable(PathwayElement o, Element e)
			throws ConverterException {
		o.setCopyright (getAttribute("Pathway", "License", e));
	}

	@Override
	protected void updateMappInfoVariable(Element root, PathwayElement o)
			throws ConverterException {
		setAttribute("Pathway", "License", root, o.getCopyright());
	}

	private void updateCommon(PathwayElement o, Element e) throws ConverterException
	{
		updateComments(o, e);
		updateBiopaxRef(o, e);
		updateAttributes(o, e);
	}

	private void mapCommon(PathwayElement o, Element e) throws ConverterException
	{
		mapComments(o, e);
		mapBiopaxRef(o, e);
		mapAttributes(o, e);
	}

	// common to Label, Shape, State, DataNode
	private void updateShapeCommon(PathwayElement o, Element e) throws ConverterException
	{
		updateShapeColor(o, e); // FillColor and Transparent
		updateFontData(o, e); // TextLabel. FontName, -Weight, -Style, -Decoration, -StrikeThru, -Size.
		updateGraphId(o, e); // GraphId
		updateShapeType(o, e); // ShapeType
		updateLineStyle(o, e); // LineStyle, LineThickness, Color
	}

	// common to Label, Shape, State, DataNode
	private void mapShapeCommon(PathwayElement o, Element e) throws ConverterException
	{
		mapShapeColor(o, e); // FillColor and Transparent
		mapFontData(o, e); // TextLabel. FontName, -Weight, -Style, -Decoration, -StrikeThru, -Size.
		mapGraphId(o, e);
		mapShapeType(o, e); // ShapeType
	}

	public Element createJdomElement(PathwayElement o) throws ConverterException
	{
		Element e = null;
		switch (o.getObjectType())
		{
			case DATANODE:
				e = new Element("DataNode", getGpmlNamespace());
				updateCommon (o, e);
				e.addContent(new Element("Graphics", getGpmlNamespace()));
				e.addContent(new Element("Xref", getGpmlNamespace()));
				updateShapePosition(o, e);
				updateShapeCommon(o, e);
				updateDataNode(o, e); // Type & Xref
				updateGroupRef(o, e);
				break;
			case STATE:
				e = new Element("State", getGpmlNamespace());
				updateCommon (o, e);
				e.addContent(new Element("Graphics", getGpmlNamespace()));
				e.addContent(new Element("Xref", getGpmlNamespace()));
				updateStateData(o, e);
				updateShapeCommon(o, e);
				break;
			case SHAPE:
				e = new Element ("Shape", getGpmlNamespace());
				updateCommon (o, e);
				e.addContent(new Element("Graphics", getGpmlNamespace()));
				updateShapePosition(o, e);
				updateShapeCommon(o, e);
				updateRotation(o, e);
				updateGroupRef(o, e);
				break;
			case LINE:
				e = new Element("Interaction", getGpmlNamespace());
				updateCommon (o, e);
				e.addContent(new Element("Graphics", getGpmlNamespace()));
				e.addContent(new Element("Xref", getGpmlNamespace()));
				updateLine(o, e); // Xref
				updateLineData(o, e);
				updateLineStyle(o, e);
				updateGraphId(o, e);
				updateGroupRef(o, e);
				break;
			case GRAPHLINE:
				e = new Element("GraphicalLine", getGpmlNamespace());
				updateCommon (o, e);
				e.addContent(new Element("Graphics", getGpmlNamespace()));
				updateLineData(o, e);
				updateLineStyle(o, e);
				updateGraphId(o, e);
				updateGroupRef(o, e);
				break;	
			case LABEL:
				e = new Element("Label", getGpmlNamespace());
				updateCommon (o, e);
				e.addContent(new Element("Graphics", getGpmlNamespace()));
				updateShapePosition(o, e);
				updateShapeCommon(o, e);
				updateHref(o, e);
				updateGroupRef(o, e);
				break;
			case LEGEND:
				e = new Element ("Legend", getGpmlNamespace());
				updateSimpleCenter (o, e);
				break;
			case INFOBOX:
				e = new Element ("InfoBox", getGpmlNamespace());
				updateSimpleCenter (o, e);
				break;
			case GROUP:
				e = new Element ("Group", getGpmlNamespace());
				updateCommon (o, e);
				updateGroup (o, e);
				updateGroupRef(o, e);
				break;
			case BIOPAX:
				e = new Element ("Biopax", getGpmlNamespace());
				updateBiopax(o, e);
				break;
		}
		if (e == null)
		{
			throw new ConverterException ("Error creating jdom element with objectType " + o.getObjectType());
		}
		return e;
	}

	/**
	   Create a single PathwayElement based on a piece of Jdom tree. Used also by Patch utility
	   Pathway p may be null
	 */
	public PathwayElement mapElement(Element e, Pathway p) throws ConverterException
	{
		String tag = e.getName();
		if(tag.equalsIgnoreCase("Interaction")){
			tag = "Line";
		}
		ObjectType ot = ObjectType.getTagMapping(tag);
		if (ot == null)
		{
			// do nothing. This could be caused by
			// tags <comment> or <graphics> that appear
			// as subtags of <pathway>
			return null;
		}

		PathwayElement o = PathwayElement.createPathwayElement(ot);
		if (p != null)
		{
			p.add (o);
		}

		switch (o.getObjectType())
		{
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
				mapLine(o,e);
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
				mapSimpleCenter (o, e);
				break;
			case GROUP:
				mapCommon(o, e);
				mapGroupRef(o, e);
				mapGroup (o, e);
				break;
			case BIOPAX:
				mapBiopax(o, e, p);
				break;
			default:
				throw new ConverterException("Invalid ObjectType'" + tag + "'");
		}
		return o;
	}

	protected void mapRotation(PathwayElement o, Element e) throws ConverterException
	{
    	Element graphics = e.getChild("Graphics", e.getNamespace());
    	String rotation = getAttribute("Shape.Graphics", "Rotation", graphics);
    	double result;
    	if (rotation.equals("Top"))
    	{
    		result = 0.0;
    	}
    	else if (rotation.equals("Right"))
		{
    		result = 0.5 * Math.PI;
		}
    	else if (rotation.equals("Bottom"))
    	{
    		result = Math.PI;
    	}
    	else if (rotation.equals("Left"))
    	{
    		result = 1.5 * Math.PI;
    	}
    	else
    	{
    		result = Double.parseDouble(rotation);
    	}
    	o.setRotation (result);
	}
	
	/**
	 * Converts deprecated shapes to contemporary analogs. This allows us to
	 * maintain backward compatibility while at the same time cleaning up old
	 * shape usages.
	 * 
	 */ 
	protected void mapShapeType(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
    	Element graphics = e.getChild("Graphics", e.getNamespace());
    	IShape s= ShapeRegistry.fromName(getAttribute(base + ".Graphics", "ShapeType", graphics));
    	if (ShapeType.DEPRECATED_MAP.containsKey(s)){
    		s = ShapeType.DEPRECATED_MAP.get(s);
    		o.setShapeType(s);
       		if (s.equals(ShapeType.ROUNDED_RECTANGLE) 
       				|| s.equals(ShapeType.OVAL)){
    			o.setLineStyle(LineStyleType.DOUBLE);
    			o.setLineThickness(3.0);
    			o.setColor(Color.LIGHT_GRAY);
    		}
    	} 
    	else 
    	{
    	o.setShapeType (s);
		mapLineStyle(o, e); // LineStyle
    	}
	}

	protected void updateRotation(PathwayElement o, Element e) throws ConverterException
	{
		Element jdomGraphics = e.getChild("Graphics", e.getNamespace());
		setAttribute("Shape.Graphics", "Rotation", jdomGraphics, Double.toString(o.getRotation()));
	}
	
	protected void updateShapeType(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		Element jdomGraphics = e.getChild("Graphics", e.getNamespace());
		String shapeName = o.getShapeType().getName();
		setAttribute(base + ".Graphics", "ShapeType", jdomGraphics, shapeName);
	}
	
	protected void updateHref(PathwayElement o, Element e) throws ConverterException
	{
		setAttribute ("Label", "Href", e, o.getHref());
	}
	
	protected void mapHref(PathwayElement o, Element e) throws ConverterException
	{
		o.setHref(getAttribute("Label", "Href", e));
	}

	protected void mapFontData(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		o.setTextLabel (getAttribute(base, "TextLabel", e));

		// TODO dirty hack: the fact that state doesn't allow font data is a bug 
		if (e.getName().equals ("State")) return;
		
    	Element graphics = e.getChild("Graphics", e.getNamespace());

    	String fontSizeString = getAttribute(base + ".Graphics", "FontSize", graphics);
    	o.setMFontSize (Integer.parseInt(fontSizeString));

    	String fontWeight = getAttribute(base + ".Graphics", "FontWeight", graphics);
    	String fontStyle = getAttribute(base + ".Graphics", "FontStyle", graphics);
    	String fontDecoration = getAttribute(base + ".Graphics", "FontDecoration", graphics);
    	String fontStrikethru = getAttribute(base + ".Graphics", "FontStrikethru", graphics);

    	o.setBold (fontWeight != null && fontWeight.equals("Bold"));
    	o.setItalic (fontStyle != null && fontStyle.equals("Italic"));
    	o.setUnderline (fontDecoration != null && fontDecoration.equals("Underline"));
    	o.setStrikethru (fontStrikethru != null && fontStrikethru.equals("Strikethru"));
    	
    	o.setFontName (getAttribute(base + ".Graphics", "FontName", graphics));
	    
		o.setValign(ValignType.fromGpmlName(getAttribute(base + ".Graphics", "Valign", graphics)));
		o.setAlign(AlignType.fromGpmlName(getAttribute(base + ".Graphics", "Align", graphics)));	    
	}
	
	protected void updateFontData(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		setAttribute(base, "TextLabel", e, o.getTextLabel());

		// TODO dirty hack: the fact that state doesn't allow font data is a bug 
		if (e.getName().equals ("State")) return;
		
		Element graphics = e.getChild("Graphics", e.getNamespace());
		setAttribute(base + ".Graphics", "FontName", graphics, o.getFontName() == null ? "" : o.getFontName());
		setAttribute(base + ".Graphics", "FontWeight", graphics, o.isBold() ? "Bold" : "Normal");
		setAttribute(base + ".Graphics", "FontStyle", graphics, o.isItalic() ? "Italic" : "Normal");
		setAttribute(base + ".Graphics", "FontDecoration", graphics, o.isUnderline() ? "Underline" : "Normal");
		setAttribute(base + ".Graphics", "FontStrikethru", graphics, o.isStrikethru() ? "Strikethru" : "Normal");
		setAttribute(base + ".Graphics", "FontSize", graphics, Integer.toString((int)o.getMFontSize()));
		setAttribute(base + ".Graphics", "Valign", graphics, o.getValign().getGpmlName());
		setAttribute(base + ".Graphics", "Align", graphics, o.getAlign().getGpmlName());
	}

	protected void mapShapePosition(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
    	o.setMCenterX (Double.parseDouble(getAttribute(base + ".Graphics", "CenterX", graphics)));
    	o.setMCenterY (Double.parseDouble(getAttribute(base + ".Graphics", "CenterY", graphics)));
		o.setMWidth (Double.parseDouble(getAttribute(base + ".Graphics", "Width", graphics)));
		o.setMHeight (Double.parseDouble(getAttribute(base + ".Graphics", "Height", graphics)));
		String zorder = graphics.getAttributeValue("ZOrder");
		if (zorder != null)
			o.setZOrder(Integer.parseInt(zorder));
	}

	protected void updateShapePosition(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		
		setAttribute(base + ".Graphics", "CenterX", graphics, "" + o.getMCenterX());
		setAttribute(base + ".Graphics", "CenterY", graphics, "" + o.getMCenterY());
		setAttribute(base + ".Graphics", "Width", graphics, "" + o.getMWidth());
		setAttribute(base + ".Graphics", "Height", graphics, "" + o.getMHeight());
		setAttribute(base + ".Graphics", "ZOrder", graphics, "" + o.getZOrder());
	}

	protected void mapDataNode(PathwayElement o, Element e) throws ConverterException
	{
		o.setDataNodeType (getAttribute("DataNode", "Type", e));
		Element xref = e.getChild ("Xref", e.getNamespace());
		o.setElementID (getAttribute("DataNode.Xref", "ID", xref));
		o.setDataSource (DataSource.getByFullName (getAttribute("DataNode.Xref", "Database", xref)));
	}

	protected void updateDataNode(PathwayElement o, Element e) throws ConverterException
	{
		setAttribute ("DataNode", "Type", e, o.getDataNodeType());
		Element xref = e.getChild("Xref", e.getNamespace());
		String database = o.getDataSource() == null ? "" : o.getDataSource().getFullName();
		setAttribute ("DataNode.Xref", "Database", xref, database == null ? "" : database);
		setAttribute ("DataNode.Xref", "ID", xref, o.getElementID());
	}
	
	protected void mapLine(PathwayElement o, Element e) throws ConverterException
	{
		Element xref = e.getChild ("Xref", e.getNamespace());
		o.setElementID (getAttribute("Interaction.Xref", "ID", xref));
		o.setDataSource (DataSource.getByFullName (getAttribute("Interaction.Xref", "Database", xref)));
	}

	
	protected void updateLine(PathwayElement o, Element e) throws ConverterException
	{
		Element xref = e.getChild("Xref", e.getNamespace());
		String database = o.getDataSource() == null ? "" : o.getDataSource().getFullName();
		setAttribute ("Interaction.Xref", "Database", xref, database == null ? "" : database);
		setAttribute ("Interaction.Xref", "ID", xref, o.getElementID());
	}

	protected void mapStateData(PathwayElement o, Element e) throws ConverterException
	{
    	String ref = getAttribute("State", "GraphRef", e);
    	if (ref != null) {
    		o.setGraphRef(ref);
    	}

    	Element graphics = e.getChild("Graphics", e.getNamespace());

    	o.setRelX(Double.parseDouble(getAttribute("State.Graphics", "RelX", graphics)));
    	o.setRelY(Double.parseDouble(getAttribute("State.Graphics", "RelY", graphics)));
		o.setMWidth (Double.parseDouble(getAttribute("State.Graphics", "Width", graphics)));
		o.setMHeight (Double.parseDouble(getAttribute("State.Graphics", "Height", graphics)));

		o.setDataNodeType (getAttribute("State", "StateType", e));
		o.setGraphRef(getAttribute("State", "GraphRef", e));
		Element xref = e.getChild ("Xref", e.getNamespace());
		o.setElementID (getAttribute("State.Xref", "ID", xref));
		o.setDataSource (DataSource.getByFullName (getAttribute("State.Xref", "Database", xref)));
	}

	protected void updateStateData(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());

		setAttribute(base + ".Graphics", "RelX", graphics, "" + o.getRelX());
		setAttribute(base + ".Graphics", "RelY", graphics, "" + o.getRelY());
		setAttribute(base + ".Graphics", "Width", graphics, "" + o.getMWidth());
		setAttribute(base + ".Graphics", "Height", graphics, "" + o.getMHeight());
		
		setAttribute ("State", "StateType", e, o.getDataNodeType());
		setAttribute ("State", "GraphRef", e, o.getElementRef());
		Element xref = e.getChild("Xref", e.getNamespace());
		String database = o.getDataSource() == null ? "" : o.getDataSource().getFullName();
		setAttribute ("State.Xref", "Database", xref, database == null ? "" : database);
		setAttribute ("State.Xref", "ID", xref, o.getElementID());
	}

	protected void mapLineStyle(PathwayElement o, Element e) throws ConverterException
	{
    	Element graphics = e.getChild("Graphics", e.getNamespace());

    	String base = e.getName();
		String style = getAttribute (base + ".Graphics", "LineStyle", graphics);
		
		//Check for LineStyle.DOUBLE via arbitrary attribute
		if ("Double".equals (o.getDynamicProperty(LineStyleType.DOUBLE_LINE_KEY)))
		{
			o.setLineStyle(LineStyleType.DOUBLE);
		}
		else
		{
			o.setLineStyle ((style.equals("Solid")) ? LineStyleType.SOLID : LineStyleType.DASHED);
		}
    	
    	String lt = getAttribute(base + ".Graphics", "LineThickness", graphics);
    	o.setLineThickness(lt == null ? 1.0 : Double.parseDouble(lt));
		mapColor(o, e); // Color
	}

	protected void mapLineData(PathwayElement o, Element e) throws ConverterException
	{    	
    	Element graphics = e.getChild("Graphics", e.getNamespace());

    	List<MPoint> mPoints = new ArrayList<MPoint>();

    	String startType = null;
    	String endType = null;

    	List<Element> pointElements = graphics.getChildren("Point", e.getNamespace());
    	for(int i = 0; i < pointElements.size(); i++) {
    		Element pe = pointElements.get(i);
    		MPoint mp = o.new MPoint(
    		    	Double.parseDouble(getAttribute("Interaction.Graphics.Point", "X", pe)),
    		    	Double.parseDouble(getAttribute("Interaction.Graphics.Point", "Y", pe))
    		);
    		mPoints.add(mp);
        	String ref = getAttribute("Interaction.Graphics.Point", "GraphRef", pe);
        	if (ref != null) {
        		mp.setGraphRef(ref);
        		String srx = pe.getAttributeValue("RelX");
        		String sry = pe.getAttributeValue("RelY");
        		if(srx != null && sry != null) {
        			mp.setRelativePosition(Double.parseDouble(srx), Double.parseDouble(sry));
        		}
        	}

        	if(i == 0) {
        		startType = getAttribute("Interaction.Graphics.Point", "ArrowHead", pe);
        	} else if(i == pointElements.size() - 1) {
    			endType = getAttribute("Interaction.Graphics.Point", "ArrowHead", pe);
        	}
    	}

    	o.setMPoints(mPoints);
		o.setStartLineType (LineType.fromName(startType));
    	o.setEndLineType (LineType.fromName(endType));

    	String connType = getAttribute("Interaction.Graphics", "ConnectorType", graphics);
    	o.setConnectorType(ConnectorType.fromName(connType));

    	String zorder = graphics.getAttributeValue("ZOrder");
		if (zorder != null)
			o.setZOrder(Integer.parseInt(zorder));

    	//Map anchors
    	List<Element> anchors = graphics.getChildren("Anchor", e.getNamespace());
    	for(Element ae : anchors) {
    		double position = Double.parseDouble(getAttribute("Interaction.Graphics.Anchor", "Position", ae));
    		MAnchor anchor = o.addMAnchor(position);
    		mapGraphId(anchor, ae);
    		String shape = getAttribute("Interaction.Graphics.Anchor", "Shape", ae);
    		if(shape != null) {
    			anchor.setShape(AnchorType.fromName(shape));
    		}
    	}
	}

	protected void updateLineStyle(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		setAttribute(base + ".Graphics", "LineStyle", graphics, o.getLineStyle() != LineStyleType.DASHED ? "Solid" : "Broken");
		setAttribute (base + ".Graphics", "LineThickness", graphics, "" + o.getLineThickness());
		updateColor(o, e);
	}
	
	protected void updateLineData(PathwayElement o, Element e) throws ConverterException
	{
		Element jdomGraphics = e.getChild("Graphics", e.getNamespace());
		List<MPoint> mPoints = o.getMPoints();

		for(int i = 0; i < mPoints.size(); i++) {
			MPoint mp = mPoints.get(i);
			Element pe = new Element("Point", e.getNamespace());
			jdomGraphics.addContent(pe);
			setAttribute("Interaction.Graphics.Point", "X", pe, Double.toString(mp.getX()));
			setAttribute("Interaction.Graphics.Point", "Y", pe, Double.toString(mp.getY()));
			if (mp.getElementRef() != null && !mp.getElementRef().equals(""))
			{
				setAttribute("Interaction.Graphics.Point", "GraphRef", pe, mp.getElementRef());
				setAttribute("Interaction.Graphics.Point", "RelX", pe, Double.toString(mp.getRelX()));
				setAttribute("Interaction.Graphics.Point", "RelY", pe, Double.toString(mp.getRelY()));
			}
			if(i == 0) {
				setAttribute("Interaction.Graphics.Point", "ArrowHead", pe, o.getStartLineType().getName());
			} else if(i == mPoints.size() - 1) {
				setAttribute("Interaction.Graphics.Point", "ArrowHead", pe, o.getEndLineType().getName());
			}
		}

		for(MAnchor anchor : o.getMAnchors()) {
			Element ae = new Element("Anchor", e.getNamespace());
			setAttribute("Interaction.Graphics.Anchor", "Position", ae, Double.toString(anchor.getPosition()));
			setAttribute("Interaction.Graphics.Anchor", "Shape", ae, anchor.getShape().getName());
			updateGraphId(anchor, ae);
			jdomGraphics.addContent(ae);
		}

		ConnectorType ctype = o.getConnectorType();
		setAttribute("Interaction.Graphics", "ConnectorType", jdomGraphics, ctype.getName());
		setAttribute("Interaction.Graphics", "ZOrder", jdomGraphics, "" + o.getZOrder());
	}

	public Document createJdom(Pathway data) throws ConverterException
	{
		Document doc = new Document();

		Element root = new Element("Pathway", getGpmlNamespace());
		doc.setRootElement(root);

		List<Element> elementList = new ArrayList<Element>();
		
		List<PathwayElement> pathwayElements = data.getDataObjects();
		Collections.sort(pathwayElements);
		for (PathwayElement o : pathwayElements)
		{
			if (o.getObjectType() == ObjectType.MAPPINFO)
			{
				updateMappInfo(root, o);
			}
			else
			{
				Element e = createJdomElement(o);
				if (e != null)
					elementList.add(e);
			}
		}

    	// now sort the generated elements in the order defined by the xsd
		Collections.sort(elementList, new ByElementName());
		for (Element e : elementList) {
			// make sure biopax references are sorted alphabetically by rdf-id 
			if(e.getName().equals("Biopax")) {
				for(Element e3 : e.getChildren()) {
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
	    	if(e1.getAttributes().size() > 0) {
	    		 id1 = e1.getAttributes().get(0).getValue();
	    	}
	    	String id2 = "";
	    	if(e2.getAttributes().size() > 0) {
	    		 id2 = e2.getAttributes().get(0).getValue();
	    	}
	        return id1.compareTo(id2);
	    }
	}

	/**
	 * Writes the JDOM document to the outputstream specified
	 * @param out	the outputstream to which the JDOM document should be writed
	 * @param validate if true, validate the dom structure before writing. If there is a validation error,
	 * 		or the xsd is not in the classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	public void writeToXml(Pathway pwy, OutputStream out, boolean validate) throws ConverterException {
		Document doc = createJdom(pwy);
		
		//Validate the JDOM document
		if (validate) validateDocument(doc);
		//			Get the XML code
		XMLOutputter xmlcode = new XMLOutputter(Format.getPrettyFormat());
		Format f = xmlcode.getFormat();
		f.setEncoding("UTF-8");
		f.setTextMode(Format.TextMode.NORMALIZE);
		xmlcode.setFormat(f);

		try
		{
			//Send XML code to the outputstream
			xmlcode.output(doc, out);
		}
		catch (IOException ie)
		{
			throw new ConverterException(ie);
		}
	}

	/**
	 * Writes the JDOM document to the file specified
	 * @param file	the file to which the JDOM document should be saved
	 * @param validate if true, validate the dom structure before writing to file. If there is a validation error,
	 * 		or the xsd is not in the classpath, an exception will be thrown.
	 */
	public void writeToXml(Pathway pwy, File file, boolean validate) throws ConverterException
	{
		OutputStream out;
		try
		{
			out = new FileOutputStream(file);
		}
		catch (IOException ex)
		{
			throw new ConverterException (ex);
		}
		writeToXml (pwy, out, validate);
	}

	protected void mapSimpleCenter(PathwayElement o, Element e)
	{
		o.setMCenterX (Double.parseDouble(e.getAttributeValue("CenterX")));
		o.setMCenterY (Double.parseDouble(e.getAttributeValue("CenterY")));
	}

	protected void updateSimpleCenter(PathwayElement o, Element e)
	{
		if(e != null)
		{
			e.setAttribute("CenterX", Double.toString(o.getMCenterX()));
			e.setAttribute("CenterY", Double.toString(o.getMCenterY()));
		}
	}

	protected void mapBiopax(PathwayElement o, Element e, Pathway p) throws ConverterException
	{
		//this method clones all content,
		//getContent will leave them attached to the parent, which we don't want
		//We can safely remove them, since the JDOM element isn't used anymore after this method
		Element root = new Element("RDF", GpmlFormat.RDF);
		root.addNamespaceDeclaration(GpmlFormat.RDFS);
		root.addNamespaceDeclaration(GpmlFormat.RDF);
		root.addNamespaceDeclaration(GpmlFormat.OWL);
		root.addNamespaceDeclaration(GpmlFormat.BIOPAX);
		root.setAttribute(new Attribute("base", getGpmlNamespace().getURI() + "#", Namespace.XML_NAMESPACE));
		//Element owl = new Element("Ontology", OWL);
		//owl.setAttribute(new Attribute("about", "", RDF));
		//Element imp = new Element("imports", OWL);
		//imp.setAttribute(new Attribute("resource", BIOPAX.getURI(), RDF));
		//owl.addContent(imp);
		//root.addContent(owl);

		root.addContent(e.cloneContent());
		Document bp = new Document(root);

		((BiopaxElement)o).setBiopax(bp);
		
		for (Object f : e.getChildren("openControlledVocabulary", GpmlFormat.BIOPAX)){
			p.addOntologyTag(((Element) f).getChild("ID", GpmlFormat.BIOPAX).getText(),
					((Element) f).getChild("TERM", GpmlFormat.BIOPAX).getText(),
					((Element) f).getChild("Ontology", GpmlFormat.BIOPAX).getText()
					);
		}
	}

}
