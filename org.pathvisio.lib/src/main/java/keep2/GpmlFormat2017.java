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
package keep2;

import org.bridgedb.DataSource;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.pathvisio.core.view.ShapeRegistry;
import org.pathvisio.io.ConverterException;
import org.pathvisio.io.GpmlFormatReader;
import org.pathvisio.io.GpmlFormatWriter;

import keep2.GpmlFormatAbstract2017.AttributeInfo;
import keep2.GpmlFormatAbstract2017.ByElementName;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

class GpmlFormat2017 extends GpmlFormatAbstract2017 implements GpmlFormatReader, GpmlFormatWriter
{
	public static final GpmlFormat2017 GPML_2017 = new GpmlFormat2017(
			"GPML2017.xsd", Namespace.getNamespace("http://pathvisio.org/GPML/2017")
		);

	public GpmlFormat2017(String xsdFile, Namespace ns) {
		super (xsdFile, ns);
	}

	private static final Map<String, AttributeInfo> ATTRIBUTE_INFO = initAttributeInfo();

	private static Map<String, AttributeInfo> initAttributeInfo()
	{
		Map<String, AttributeInfo> result = new HashMap<String, AttributeInfo>();
		// IMPORTANT: this array has been generated from the xsd with
		// an automated perl script. Don't edit this directly, use the perl script instead.
		/* START OF AUTO-GENERATED CONTENT */
		result.put("Comment@source", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("PublicationXref@ID", new AttributeInfo ("xsd:string", null, "required"));
		result.put("PublicationXref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Attribute@key", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Attribute@value", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Pathway@width", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Pathway@height", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Pathway@name", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Pathway@organism", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway@dataSource", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway@revision", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway.Author@name", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway@license", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Pathway.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Pathway.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("DataNode@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("DataNode@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("DataNode@width", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("DataNode@height", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("DataNode@fontName", new AttributeInfo ("xsd:string", "Arial", "optional"));
		result.put("DataNode@fontStyle", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("DataNode@fontStrikethru", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("DataNode@fontWeight", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("DataNode@fontSize", new AttributeInfo ("xsd:nonNegativeInteger", "12", "optional"));
		result.put("DataNode@align", new AttributeInfo ("xsd:string", "Center", "optional"));
		result.put("DataNode@vAlign", new AttributeInfo ("xsd:string", "Top", "optional"));
		result.put("DataNode@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("DataNode@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("DataNode@lineThickness", new AttributeInfo ("xsd:float", "1.0", "optional"));
		result.put("DataNode@fillColor", new AttributeInfo ("gpml:ColorType", "White", "optional"));
		result.put("DataNode@shapeType", new AttributeInfo ("xsd:string", "Rectangle", "optional"));
		result.put("DataNode@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("DataNode.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("DataNode.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("DataNode@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("DataNode@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("DataNode@textLabel", new AttributeInfo ("xsd:string", null, "required"));
		result.put("DataNode@type", new AttributeInfo ("xsd:string", "Unknown", "optional"));
		result.put("State@relX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("State@relY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("State@width", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("State@height", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("State@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("State@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("State@lineThickness", new AttributeInfo ("xsd:float", "1.0", "optional"));
		result.put("State@fillColor", new AttributeInfo ("gpml:ColorType", "White", "optional"));
		result.put("State@shapeType", new AttributeInfo ("xsd:string", "Rectangle", "optional"));
		result.put("State@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("State@fontName", new AttributeInfo ("xsd:string", "Arial", "optional"));
		result.put("State@fontStyle", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("State@fontWeight", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("State@fontSize", new AttributeInfo ("xsd:nonNegativeInteger", "12", "optional"));
		result.put("State@align", new AttributeInfo ("xsd:string", "Center", "optional"));
		result.put("State@vAlign", new AttributeInfo ("xsd:string", "Top", "optional"));
		result.put("State.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("State.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("State@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("State@elementRef", new AttributeInfo ("xsd:IDREF", null, "optional"));
		result.put("State@textLabel", new AttributeInfo ("xsd:string", null, "required"));
		result.put("State@stateType", new AttributeInfo ("xsd:string", "Unknown", "optional"));
		result.put("GraphicalLine.Point@x", new AttributeInfo ("xsd:float", null, "required"));
		result.put("GraphicalLine.Point@y", new AttributeInfo ("xsd:float", null, "required"));
		result.put("GraphicalLine.Point@relX", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("GraphicalLine.Point@relY", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("GraphicalLine.Point@elementRef", new AttributeInfo ("xsd:IDREF", null, "optional"));
		result.put("GraphicalLine.Point@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("GraphicalLine.Point@arrowHead", new AttributeInfo ("xsd:string", "Line", "optional"));
		result.put("GraphicalLine.Anchor@position", new AttributeInfo ("xsd:float", null, "required"));
		result.put("GraphicalLine.Anchor@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("GraphicalLine.Anchor@shapeType", new AttributeInfo ("xsd:string", "ReceptorRound", "optional"));
		result.put("GraphicalLine@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("GraphicalLine@lineThickness", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("GraphicalLine@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("GraphicalLine@connectorType", new AttributeInfo ("xsd:string", "Straight", "optional"));
		result.put("GraphicalLine@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("GraphicalLine@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("GraphicalLine@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("GraphicalLine@type", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Interaction.Point@x", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Interaction.Point@y", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Interaction.Point@relX", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("Interaction.Point@relY", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("Interaction.Point@elementRef", new AttributeInfo ("xsd:IDREF", null, "optional"));
		result.put("Interaction.Point@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Interaction.Point@arrowHead", new AttributeInfo ("xsd:string", "Line", "optional"));
		result.put("Interaction.Anchor@position", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Interaction.Anchor@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Interaction.Anchor@shapeType", new AttributeInfo ("xsd:string", "ReceptorRound", "optional"));
		result.put("Interaction@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("Interaction@lineThickness", new AttributeInfo ("xsd:float", null, "optional"));
		result.put("Interaction@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("Interaction@connectorType", new AttributeInfo ("xsd:string", "Straight", "optional"));
		result.put("Interaction@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("Interaction.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Interaction.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Interaction@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Interaction@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Interaction@type", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Label@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Label@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Label@width", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Label@height", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Label@fontName", new AttributeInfo ("xsd:string", "Arial", "optional"));
		result.put("Label@fontStyle", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Label@fontWeight", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Label@fontSize", new AttributeInfo ("xsd:nonNegativeInteger", "12", "optional"));
		result.put("Label@align", new AttributeInfo ("xsd:string", "Center", "optional"));
		result.put("Label@vAlign", new AttributeInfo ("xsd:string", "Top", "optional"));
		result.put("Label@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("Label@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("Label@lineThickness", new AttributeInfo ("xsd:float", "1.0", "optional"));
		result.put("Label@fillColor", new AttributeInfo ("gpml:ColorType", "Transparent", "optional"));
		result.put("Label@shapeType", new AttributeInfo ("xsd:string", "None", "optional"));
		result.put("Label@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("Label@href", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Label@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Label@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Label@textLabel", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Shape@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Shape@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Shape@width", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Shape@height", new AttributeInfo ("gpml:Dimension", null, "required"));
		result.put("Shape@fontName", new AttributeInfo ("xsd:string", "Arial", "optional"));
		result.put("Shape@fontStyle", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Shape@fontStrikethru", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Shape@fontWeight", new AttributeInfo ("xsd:string", "Normal", "optional"));
		result.put("Shape@fontSize", new AttributeInfo ("xsd:nonNegativeInteger", "12", "optional"));
		result.put("Shape@align", new AttributeInfo ("xsd:string", "Center", "optional"));
		result.put("Shape@vAlign", new AttributeInfo ("xsd:string", "Top", "optional"));
		result.put("Shape@color", new AttributeInfo ("gpml:ColorType", "Black", "optional"));
		result.put("Shape@lineStyle", new AttributeInfo ("gpml:StyleType", "Solid", "optional"));
		result.put("Shape@lineThickness", new AttributeInfo ("xsd:float", "1.0", "optional"));
		result.put("Shape@fillColor", new AttributeInfo ("gpml:ColorType", "Transparent", "optional"));
		result.put("Shape@shapeType", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Shape@zOrder", new AttributeInfo ("xsd:integer", null, "optional"));
		result.put("Shape@rotation", new AttributeInfo ("gpml:RotationType", "Top", "optional"));
		result.put("Shape@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("Shape@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Shape@textLabel", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Group@groupRef", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Group@groupType", new AttributeInfo ("xsd:string", "None", "optional"));
		result.put("Group@textLabel", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Group@elementID", new AttributeInfo ("xsd:ID", null, "required"));
		result.put("InfoBox@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("InfoBox@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Legend@centerX", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Legend@centerY", new AttributeInfo ("xsd:float", null, "required"));
		result.put("Citations.Citation.Xref@dataSource", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation.Xref@identifier", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation.Author@name", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation@citationID", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation@title", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation@URL", new AttributeInfo ("xsd:string", null, "required"));
		result.put("Citations.Citation@source", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("Citations.Citation@year", new AttributeInfo ("xsd:string", null, "optional"));
		result.put("OntologyTerms.OntologyTerm@ID", new AttributeInfo ("xsd:string", null, "required"));
		result.put("OntologyTerms.OntologyTerm@term", new AttributeInfo ("xsd:string", null, "required"));
		result.put("OntologyTerms.OntologyTerm@ontology", new AttributeInfo ("xsd:string", null, "required"));
		result.put("OntologyTerms.OntologyTerm@ontologyTermID", new AttributeInfo ("xsd:string", null, "required"));
		/* END OF AUTO-GENERATED CONTENT */

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
		o.setCopyright (getAttribute("Pathway", "license", e));
	}

	@Override
	protected void updateMappInfoVariable(Element root, PathwayElement o)
			throws ConverterException {
		setAttribute("Pathway", "license", root, o.getCopyright());
	}

	private void updateCommon(PathwayElement o, Element e) throws ConverterException
	{
		updateComments(o, e);
		updateAttributes(o, e);
		updateCitationRefs(o, e);
	}

	private void mapCommon(PathwayElement o, Element e) throws ConverterException
	{
		mapComments(o, e);
		mapAttributes(o, e);
		mapCitationRefs(o, e);
	}

	// common to Label, Shape, State, DataNode
	private void updateShapeCommon(PathwayElement o, Element e) throws ConverterException
	{
		updateGraphId(o, e); // GraphId
		updateTextLabel(o, e); // TextLabel
		updateShapeType(o, e); // ShapeType
		updateLineStyle(o, e); // LineStyle, LineThickness, Color
		updateShapeColor(o, e); // FillColor and Transparent
		updateFontData(o, e); // TextLabel. FontName, -Weight, -Style, -Decoration, -StrikeThru, -Size.
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
				updateGraphId(o, e);
				updateTextLabel(o,e);
				updateDataNodeType(o,e);
				updateGroupRef(o, e);
				updateShapeCommon(o, e);
				updateCommon (o, e);
				e.addContent(new Element("Xref", getGpmlNamespace()));
				updateDataNode(o, e); // Type & Xref
				updateShapePosition(o, e);
				break;
			case STATE:
				e = new Element("State", getGpmlNamespace());
				updateShapeCommon(o, e);
				updateCommon (o, e);
				e.addContent(new Element("Xref", getGpmlNamespace()));
				updateStateData(o, e);
				break;
			case SHAPE:
				e = new Element ("Shape", getGpmlNamespace());
				updateGraphId(o, e);
				updateGroupRef(o, e);
				updateShapeCommon(o, e);
				updateCommon (o, e);
				updateShapePosition(o, e);
				updateRotation(o, e);
				break;
			case LINE:
				e = new Element("Interaction", getGpmlNamespace());
				e.addContent(new Element("Xref", getGpmlNamespace()));
				updateGraphId(o, e);
				updateGroupRef(o, e);
				updateLine(o, e); // Xref
				updateCommon (o, e);
				updateLineData(o, e);
				updateLineStyle(o, e);
				break;
			case GRAPHLINE:
				e = new Element("GraphicalLine", getGpmlNamespace());
				updateGraphId(o, e);
				updateGroupRef(o, e);
				updateCommon (o, e);
				updateLineData(o, e);
				updateLineStyle(o, e);
				break;
			case LABEL:
				e = new Element("Label", getGpmlNamespace());
				updateShapeCommon(o, e);
				updateGroupRef(o, e);
				updateCommon (o, e);
				updateShapePosition(o, e);
				updateHref(o, e);
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
				updateGroup (o, e);
				updateGroupRef(o, e);
				updateCommon (o, e);
				break;
			case BIOPAX:
				return null;
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
		if (p != null && o!= null)
		{
			p.add (o);
		}

		switch (ot)
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
				mapOntologyTermRefs(e, p);
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
			case CITATION:
				mapCitations(e, p);
				break;
			case ONTOLOGY:
				mapOntology(e, p);
				break;
			default:
				throw new ConverterException("Invalid ObjectType'" + tag + "'");
		}
		return o;
	}

	protected void mapRotation(PathwayElement o, Element e) throws ConverterException
	{
    	String rotation = getAttribute("Shape", "rotation", e);
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
    	IShape s= ShapeRegistry.fromName(getAttribute(base, "shapeType", e));
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
		setAttribute("Shape", "rotation", e, Double.toString(o.getRotation()));
	}
	
	protected void updateShapeType(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		String shapeName = o.getShapeType().getName();
		setAttribute(base, "shapeType", e, shapeName);
	}
	
	protected void updateHref(PathwayElement o, Element e) throws ConverterException
	{
		setAttribute ("Label", "href", e, o.getHref());
	}
	
	protected void mapHref(PathwayElement o, Element e) throws ConverterException
	{
		o.setHref(getAttribute("Label", "href", e));
	}

	protected void mapFontData(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		o.setTextLabel (getAttribute(base, "textLabel", e));

    	String fontSizeString = getAttribute(base, "fontSize", e);
    	o.setMFontSize (Integer.parseInt(fontSizeString));

    	String fontWeight = getAttribute(base, "fontWeight", e);
    	String fontStyle = getAttribute(base, "fontStyle", e);

    	o.setBold (fontWeight != null && fontWeight.equals("Bold"));
    	o.setItalic (fontStyle != null && fontStyle.equals("Italic"));

    	o.setFontName (getAttribute(base, "fontName", e));
	    
		o.setValign(ValignType.fromGpmlName(getAttribute(base, "vAlign", e)));
		o.setAlign(AlignType.fromGpmlName(getAttribute(base, "align", e)));
	}

	protected void updateTextLabel(PathwayElement o, Element e) throws ConverterException{
		String base = e.getName();
		setAttribute(base, "textLabel", e, o.getTextLabel());
	}
	
	protected void updateDataNodeType(PathwayElement o, Element e) throws ConverterException{
		String base = e.getName();
		setAttribute(base, "type", e, o.getDataNodeType());
	}

	protected void updateFontData(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		setAttribute(base, "textLabel", e, o.getTextLabel());

		setAttribute(base, "fontName", e, o.getFontName() == null ? "" : o.getFontName());
		setAttribute(base, "fontWeight", e, o.isBold() ? "Bold" : "Normal");
		setAttribute(base, "fontStyle", e, o.isItalic() ? "Italic" : "Normal");
		setAttribute(base, "fontSize", e, Integer.toString((int)o.getMFontSize()));
		setAttribute(base, "vAlign", e, o.getValign().getGpmlName());
		setAttribute(base, "align", e, o.getAlign().getGpmlName());
	}

	protected void mapShapePosition(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
    	o.setMCenterX (Double.parseDouble(getAttribute(base , "centerX", e)));
    	o.setMCenterY (Double.parseDouble(getAttribute(base , "centerY", e)));
		o.setMWidth (Double.parseDouble(getAttribute(base , "width", e)));
		o.setMHeight (Double.parseDouble(getAttribute(base , "height", e)));
		String zorder = e.getAttributeValue("zOrder");
		if (zorder != null)
			o.setZOrder(Integer.parseInt(zorder));
	}

	protected void updateShapePosition(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();

		setAttribute(base, "centerX", e, "" + o.getMCenterX());
		setAttribute(base, "centerY", e, "" + o.getMCenterY());
		setAttribute(base, "width", e, "" + o.getMWidth());
		setAttribute(base, "height", e, "" + o.getMHeight());
		setAttribute(base, "zOrder", e, "" + o.getZOrder());
	}

	protected void mapDataNode(PathwayElement o, Element e) throws ConverterException
	{
		o.setDataNodeType (getAttribute("DataNode", "type", e));
		Element xref = e.getChild ("Xref", e.getNamespace());
		o.setElementID (getAttribute("DataNode.Xref", "identifier", xref));
		o.setDataSource (DataSource.getByFullName (getAttribute("DataNode.Xref", "dataSource", xref)));
	}

	protected void updateDataNode(PathwayElement o, Element e) throws ConverterException
	{
		setAttribute ("DataNode", "type", e, o.getDataNodeType());
		Element xref = e.getChild("Xref", e.getNamespace());
		String database = o.getDataSource() == null ? "" : o.getDataSource().getFullName();
		setAttribute ("DataNode.Xref", "dataSource", xref, database == null ? "" : database);
		setAttribute ("DataNode.Xref", "identifier", xref, o.getElementID());
	}
	
	protected void mapLine(PathwayElement o, Element e) throws ConverterException
	{
		Element xref = e.getChild ("Xref", e.getNamespace());
		o.setElementID (getAttribute("Interaction.Xref", "identifier", xref));
		o.setDataSource (DataSource.getByFullName (getAttribute("Interaction.Xref", "dataSource", xref)));
	}

	
	protected void updateLine(PathwayElement o, Element e) throws ConverterException
	{
		Element xref = e.getChild("Xref", e.getNamespace());
		String database = o.getDataSource() == null ? "" : o.getDataSource().getFullName();
		setAttribute ("Interaction.Xref", "dataSource", xref, database == null ? "" : database);
		setAttribute ("Interaction.Xref", "identifier", xref, o.getElementID());
	}

	protected void mapStateData(PathwayElement o, Element e) throws ConverterException
	{
    	String ref = getAttribute("State", "elementRef", e);
    	if (ref != null) {
    		o.setGraphRef(ref);
    	}

    	o.setRelX(Double.parseDouble(getAttribute("State", "relX", e)));
    	o.setRelY(Double.parseDouble(getAttribute("State", "relY", e)));
		o.setMWidth (Double.parseDouble(getAttribute("State", "width", e)));
		o.setMHeight (Double.parseDouble(getAttribute("State", "height", e)));

		o.setDataNodeType (getAttribute("State", "stateType", e));
		o.setGraphRef(getAttribute("State", "elementRef", e));
		Element xref = e.getChild ("Xref", e.getNamespace());
		o.setElementID (getAttribute("State.Xref", "identifier", xref));
		o.setDataSource (DataSource.getByFullName (getAttribute("State.Xref", "dataSource", xref)));
	}

	protected void updateStateData(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();

		setAttribute ("State", "stateType", e, o.getDataNodeType());
		setAttribute ("State", "elementRef", e, o.getElementRef());

		setAttribute(base, "relX", e, "" + o.getRelX());
		setAttribute(base, "relY", e, "" + o.getRelY());
		setAttribute(base, "width", e, "" + o.getMWidth());
		setAttribute(base, "height", e, "" + o.getMHeight());

		Element xref = e.getChild("Xref", e.getNamespace());
		String database = o.getDataSource() == null ? "" : o.getDataSource().getFullName();
		setAttribute ("State.Xref", "dataSource", xref, database == null ? "" : database);
		setAttribute ("State.Xref", "identifier", xref, o.getElementID());
	}

	protected void mapLineStyle(PathwayElement o, Element e) throws ConverterException
	{
    	String base = e.getName();
		String style = getAttribute (base, "lineStyle", e);
		
		//Check for LineStyle.DOUBLE via arbitrary attribute
		if ("Double".equals (o.getDynamicProperty(LineStyleType.DOUBLE_LINE_KEY)))
		{
			o.setLineStyle(LineStyleType.DOUBLE);
		}
		else
		{
			o.setLineStyle ((style.equals("Solid")) ? LineStyleType.SOLID : LineStyleType.DASHED);
		}
    	
    	String lt = getAttribute(base, "lineThickness", e);
    	o.setLineThickness(lt == null ? 1.0 : Double.parseDouble(lt));
		mapColor(o, e); // Color
	}

	protected void mapLineData(PathwayElement o, Element e) throws ConverterException
	{    	
    	List<MPoint> mPoints = new ArrayList<MPoint>();

    	String startType = null;
    	String endType = null;

    	List<Element> pointElements = e.getChildren("Point", e.getNamespace());
    	for(int i = 0; i < pointElements.size(); i++) {
    		Element pe = pointElements.get(i);
    		MPoint mp = new MPoint(
    		    	Double.parseDouble(getAttribute("Interaction.Point", "x", pe)),
    		    	Double.parseDouble(getAttribute("Interaction.Point", "y", pe)), o
    		);
    		mPoints.add(mp);
			String ref = getAttribute("Interaction.Point", "elementRef", pe);
			String graphId = getAttribute("Interaction.Point", "elementID", pe);

        	if (ref != null) {
        		mp.setGraphRef(ref);
        		String srx = pe.getAttributeValue("relX");
        		String sry = pe.getAttributeValue("relY");
        		if(srx != null && sry != null) {
        			mp.setRelativePosition(Double.parseDouble(srx), Double.parseDouble(sry));
        		}
        	}

        	if(i == 0) {
        		startType = getAttribute("Interaction.Point", "arrowHead", pe);
        	} else if(i == pointElements.size() - 1) {
    			endType = getAttribute("Interaction.Point", "arrowHead", pe);
        	}
    	}

    	o.setMPoints(mPoints);
		o.setStartLineType (ArrowHeadType.fromName(startType));
    	o.setEndLineType (ArrowHeadType.fromName(endType));

    	String connType = getAttribute("Interaction", "connectorType", e);
    	o.setConnectorType(ConnectorType.fromName(connType));

    	String zorder = e.getAttributeValue("zOrder");
		if (zorder != null)
			o.setZOrder(Integer.parseInt(zorder));

    	//Map anchors
    	List<Element> anchors = e.getChildren("Anchor", e.getNamespace());
    	for(Element ae : anchors) {
    		double position = Double.parseDouble(getAttribute("Interaction.Anchor", "position", ae));
    		MAnchor anchor = o.addMAnchor(position);
    		mapGraphId(anchor, ae);
    		String shape = getAttribute("Interaction.Anchor", "shapeType", ae);
    		if(shape != null) {
    			anchor.setShape(AnchorType.fromName(shape));
    		}
    	}
	}

	protected void updateLineStyle(PathwayElement o, Element e) throws ConverterException
	{
		String base = e.getName();
		setAttribute(base, "lineStyle", e, o.getLineStyle() != LineStyleType.DASHED ? "Solid" : "Broken");
		setAttribute (base, "lineThickness", e, "" + o.getLineThickness());
		updateColor(o, e);
	}
	
	protected void updateLineData(PathwayElement o, Element e) throws ConverterException
	{
		List<MPoint> mPoints = o.getMPoints();

		for(int i = 0; i < mPoints.size(); i++) {
			MPoint mp = mPoints.get(i);

			Element pe = new Element("Point", e.getNamespace());
			e.addContent(pe);

			if(mp.getElementId()==null)
				mp.setGeneratedElementId();

			setAttribute("Interaction.Point", "elementID", pe, mp.getElementId());

			if (mp.getElementRef() != null && !mp.getElementRef().equals(""))
				setAttribute("Interaction.Point", "elementRef", pe, mp.getElementRef());

			if(i == 0) {
				setAttribute("Interaction.Point", "arrowHead", pe, o.getStartLineType().getName());
			} else if(i == mPoints.size() - 1) {
				setAttribute("Interaction.Point", "arrowHead", pe, o.getEndLineType().getName());
			}
			setAttribute("Interaction.Point", "x", pe, Double.toString(mp.getX()));
			setAttribute("Interaction.Point", "y", pe, Double.toString(mp.getY()));

			if (mp.getElementRef() != null && !mp.getElementRef().equals(""))
			{
				setAttribute("Interaction.Point", "relX", pe, Double.toString(mp.getRelX()));
				setAttribute("Interaction.Point", "relY", pe, Double.toString(mp.getRelY()));
			}

		}

		for(MAnchor anchor : o.getMAnchors()) {
			Element ae = new Element("Anchor", e.getNamespace());
			if(anchor.getElementId()==null)
				anchor.setGeneratedElementId();
			updateGraphId(anchor, ae);
			setAttribute("Interaction.Anchor", "shapeType", ae, anchor.getShape().getName());
			setAttribute("Interaction.Anchor", "position", ae, Double.toString(anchor.getPosition()));
			e.addContent(ae);
		}

		ConnectorType ctype = o.getConnectorType();
		setAttribute("Interaction", "connectorType", e, ctype.getName());
		setAttribute("Interaction", "zOrder", e, "" + o.getZOrder());
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
				updateCitationRefs(o, root);
				updateOntologyTermRefs(o.getParent(), root);
			}
			else
			{
				Element e = createJdomElement(o);
				if (e != null)
					elementList.add(e);
			}
		}
		// Add the non-PathwayElement type Elements
		// Which include the new Elements introduced with the 2017a schema
		// Citations, OntologyTerms

		if(data.getCitations().size()>0)
			elementList.add(updateCitations(data, new Element("Citations", getGpmlNamespace())));
		if(data.getOntologyTerms().size()>0)
			elementList.add(updateOntologyTerms(data, new Element("OntologyTerms", getGpmlNamespace())));

    	// now sort the generated elements in the order defined by the xsd
		Collections.sort(elementList, new ByElementName());
		for (Element e : elementList)
		{
			root.addContent(e);
		}

		return doc;
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
		f.setTextMode(Format.TextMode.PRESERVE);
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
		o.setMCenterX (Double.parseDouble(e.getAttributeValue("centerX")));
		o.setMCenterY (Double.parseDouble(e.getAttributeValue("centerY")));
	}

	protected void updateSimpleCenter(PathwayElement o, Element e)
	{
		if(e != null)
		{
			e.setAttribute("centerX", Double.toString(o.getMCenterX()));
			e.setAttribute("centerY", Double.toString(o.getMCenterY()));
		}
	}

	private Element updateOntologyTerms(Pathway p, Element e)
	{
		if(e != null)
		{
			for(OntologyTerm ontologyTerm : p.getOntologyTerms()){
				Element element = new Element("OntologyTerm", getGpmlNamespace());
				e.addContent(element);
				element.setAttribute("ontologyTermID", ontologyTerm.getId());
				element.setAttribute("term", ontologyTerm.getTerm());
				element.setAttribute("ontology", ontologyTerm.getOntology());
			}
		}
		return e;
	}

	private void mapOntology(Element e, Pathway p) throws ConverterException
	{
		List<Element> OntologyTags = e.getChildren("OntologyTerm", e.getNamespace());
		String id,term,ontology,ontologyTermId;
		for(Element ot: OntologyTags){
			term=ot.getAttributeValue("term");
			ontologyTermId=ot.getAttributeValue("ontologyTermID");
			ontology=ot.getAttributeValue("ontology");
			p.addOntologyTerm(ontologyTermId,term,ontology);
		}
	}

	private Element updateCitations(Pathway p, Element e)  throws ConverterException
	{
		if(e != null)
		{
			for(Citation citation:p.getCitations()) {

				Element citationElement = new Element("Citation",getGpmlNamespace());
				e.addContent(citationElement);

				citationElement.setAttribute("citationID", citation.getCitationId());
				citationElement.setAttribute("URL", citation.getURL());
				citationElement.setAttribute("title", citation.getTitle());

				if(citation.getYear()!=null)
					citationElement.setAttribute("year", citation.getYear());

				if(citation.getSource()!=null)
					citationElement.setAttribute("source", citation.getSource());

				if(citation.getXref()!=null){
					Element xref = new Element("Xref",getGpmlNamespace());
					citationElement.addContent(xref);
					String dataSource = citation.getXref().getDataSource() == null ? "" : citation.getXref().getDataSource().getFullName();
					xref.setAttribute("dataSource", dataSource);
					if(citation.getXref().getId()!=null)
					xref.setAttribute("identifier", citation.getXref().getId());
					else
					xref.setAttribute("identifier", "");
				}

				List<String> authors = citation.getAuthors();
				for (String author : authors) {
					Element element = new Element("Author", getGpmlNamespace());
					citationElement.addContent(element);
					element.setAttribute("name", author);
				}
			}
		}
		return e;
	}

	private void mapCitations(Element e, Pathway p) throws ConverterException {
		List<Element> citationElements = e.getChildren("Citation", getGpmlNamespace());
		for (Element citationElement : citationElements) {

			Citation citation = new Citation(citationElement.getAttributeValue("citationID"),
					citationElement.getAttributeValue("URL"),
					citationElement.getAttributeValue("title"));

			p.addCitation(citation);

			citation.setYear(citationElement.getAttributeValue("year"));
			citation.setSource(citationElement.getAttributeValue("source"));

			Element xref = citationElement.getChild("Xref", e.getNamespace());

			if (xref != null) {
				citation.setXref(xref.getAttributeValue("identifier"),
						xref.getAttributeValue("dataSource"));
			}

			List<Element> authors = citationElement.getChildren("Author", e.getNamespace());

			for (Element author : authors) {
				citation.addAuthor(author.getAttributeValue("name"));
			}

		}
	}


	private void mapCitationRefs(PathwayElement o, Element e) throws ConverterException
	{
		List<Element> citationRefs = e.getChildren("CitationRef", e.getNamespace());
		String id;
		for(Element ot: citationRefs){
			id=ot.getAttributeValue("citationID");
			if(id!=null)
				o.addCitationRef(id);
		}
	}


	private void mapOntologyTermRefs(Element e, Pathway p) throws ConverterException
	{
		List<Element> ontologyTermRefs = e.getChildren("OntologyTermRef", e.getNamespace());
		String id;
		for(Element ot: ontologyTermRefs){
			id=ot.getAttributeValue("ontologyTermID");
			p.addOntologyTermRef(id);
		}
	}


	private Element updateOntologyTermRefs(Pathway p, Element e)
	{
		if(e != null)
		{
			for(String ontologyTermRef : p.getOntologyTermRefs()){
				Element element = new Element("OntologyTermRef", getGpmlNamespace());
				e.addContent(element);
				element.setAttribute("ontologyTermID", ontologyTermRef);
			}
		}
		return e;
	}

	private void updateCitationRefs(PathwayElement o, Element e)
	{
		if(e != null)
		{
			for(String citationRef : o.getCitationRefs()){
				Element element = new Element("CitationRef", getGpmlNamespace());
				e.addContent(element);
				element.setAttribute("citationID", citationRef);
			}
		}
	}
}
