package temp2;

import org.jdom2.Element;

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
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.debug.Logger;
import org.pathvisio.io.GpmlFormatAbstract.AttributeInfo;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.Anchor;
import org.pathvisio.model.elements.ElementInfo;
import org.pathvisio.model.elements.LineElement;
import org.pathvisio.model.elements.Point;
import org.pathvisio.model.elements.ShapedElement;
import org.pathvisio.model.graphics.Coordinate;
import org.pathvisio.model.pathwayelements.DataNode;
import org.pathvisio.model.pathwayelements.Group;
import org.pathvisio.model.pathwayelements.Interaction;
import org.pathvisio.model.pathwayelements.Label;
import org.pathvisio.model.pathwayelements.Shape;
import org.pathvisio.model.pathwayelements.State;
import org.pathvisio.model.type.AnchorType;
import org.pathvisio.model.type.ConnectorType;
import org.pathvisio.model.type.DataNodeType;
import org.pathvisio.model.type.GroupType;
import org.pathvisio.model.type.HAlignType;
import org.pathvisio.model.type.LineStyleType;
import org.pathvisio.model.type.ShapeType;
import org.pathvisio.model.type.StateType;
import org.pathvisio.model.type.VAlignType;
public abstract class AbstractGPMLReader {


	/**
	 * Gets a certain attribute value, and replaces it with a suitable default under
	 * certain conditions.
	 *
	 * @param tag  used for lookup in the defaults table.
	 * @param name used for lookup in the defaults table.
	 * @param el   jdom element to get the attribute from.
	 * @throws ConverterException if {@link getAttributeInfo} does not contain a
	 *                            mapping for the specified key.
	 */
	protected String getAttribute(String tag, String name, Element el) throws ConverterException {
		//key: "DataNode.Graphics@CenterX"
		//tag is location, name is attribute name, element is the jdom element? 
		String key = tag + "@" + name; 
		if (!getAttributeInfo().containsKey(key))
			throw new ConverterException("Trying to get invalid attribute " + key);
		//aInfo: new AttributeInfo("xsd:float", null, "required")
		AttributeInfo aInfo = getAttributeInfo().get(key);
		//if element is  null, get default value, else get the actual value? 
		String result = ((el == null) ? aInfo.def : el.getAttributeValue(name, aInfo.def));
		return result;
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
	
	
	public abstract PathwayElement readElement(Element e, Pathway p) throws ConverterException;

	public PathwayElement readElement(Element e) throws ConverterException {
		return readElement(e, null);
	}
	
	public void readFromRoot(Element root, Pathway pwy) throws ConverterException {
		readElement(root, pwy); // MappInfo

		// Iterate over direct children of the root element
		for (Object e : root.getChildren()) {
			readElement((Element) e, pwy);
		}
		Logger.log.trace("End copying read elements");

		// Add graphIds for objects that don't have one
		addElementIds(pwy);

		// Convert absolute point coordinates of linked points to
		// relative coordinates
		convertPointCoordinates(pwy);
	}

	
	
	protected void readTextColor(ShapedElement o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String textColor = getAttribute(e.getName() + ".Graphics", "Color", graphics);
		o.getFontProperty().setTextColor(ColorUtils.stringToColor(textColor));
	}
	protected void readBorderColor(ShapedElement o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String borderColor = getAttribute(e.getName() + ".Graphics", "Color", graphics);
		o.getShapeStyleProperty().setBorderColor(ColorUtils.stringToColor(borderColor));
	}

	protected void readLineColor(LineElement o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String lineColor = getAttribute(e.getName() + ".Graphics", "Color", graphics);
		o.getLineStyleProperty().setLineColor(ColorUtils.stringToColor(lineColor));
	}
	
	protected void readFillColor(ShapedElement o, Element e) throws ConverterException {
		Element graphics = e.getChild("Graphics", e.getNamespace());
		String scol = getAttribute(e.getName() + ".Graphics", "FillColor", graphics);
		if (scol.equals("Transparent")) {
			o.getShapeStyleProperty().setFillColor(Color.decode("#00000000")); //TODO transparent? 
		} else {
			o.getShapeStyleProperty().setFillColor(ColorUtils.stringToColor(scol));
		}
	}
	
	

	protected void readComments(ElementInfo o, Element e) throws ConverterException {
		for (Object f : e.getChildren("Comment", e.getNamespace())) {
			o.addComment(((Element) f).getText(), getAttribute("Comment", "Source", (Element) f));
		}
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
		if ("Double".equals(o.getDynamicProperty("org.pathvisio.DoubleLineProperty"))) {
			o.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
		} else {
			o.getShapeStyleProperty()
					.setBorderStyle((borderStyle.equals("Solid")) ? LineStyleType.SOLID : LineStyleType.DASHED);
		}
		o.getShapeStyleProperty().setBorderWidth(borderWidth == null ? 1.0 : Double.parseDouble(borderWidth));
		if (zOrder != null)
			o.getShapeStyleProperty().setZOrder(Integer.parseInt(zOrder));
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
		o.setGroupRef((Group) o.getPathwayModel().getPathwayElement(groupRef));
		readRectProperty(o, e);
		readFontProperty(o, e);
		readShapeStyleProperty(o, e);
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
		if ("Double".equals(o.getDynamicProperty("org.pathvisio.DoubleLineProperty"))) {
			o.getLineStyleProperty().setLineStyle(LineStyleType.DOUBLE);
		} else {
			o.getLineStyleProperty().setLineStyle((lineStyle.equals("Solid")) ? LineStyleType.SOLID : LineStyleType.DASHED);
		}
		o.getLineStyleProperty().setLineWidth(lineWidth == null ? 1.0 : Double.parseDouble(lineWidth));
		o.getLineStyleProperty().setConnectorType(ConnectorType.fromName(connectorType));
		if (zOrder != null)
			o.getLineStyleProperty().setZOrder(Integer.parseInt(zOrder));
	}
	
	
	
	/**
	 * @param o
	 * @param e
	 */
	protected void readInfoBox(Pathway p, Element e) {
		String centerX = e.getAttributeValue("CenterX");
		String centerY = e.getAttributeValue("CenterY");
		p.getInfoBox().setX(Double.parseDouble(centerX));
		p.getInfoBox().setY(Double.parseDouble(centerY));
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
	protected void readState(State o, Element e) throws ConverterException {
		readPathwayElement(o, e); // TODO: // ElemenId, CommentGroup
		String elementRef = getAttribute("State", "GraphRef", e); // TODO elementRef

		if (elementRef != null) {
			o.setDataNode((DataNode) o.getPathwayModel().getPathwayElement(elementRef)); // TODO have element
		}
		o.setTextLabel(getAttribute("State", "TextLabel", e));
		o.setType(StateType.fromName(getAttribute("State", "StateType", e))); // TODO Enum
		/** Graphics */
		Element graphics = e.getChild("Graphics", e.getNamespace());
		o.setRelX(Double.parseDouble(getAttribute("State.Graphics", "RelX", graphics)));
		o.setRelY(Double.parseDouble(getAttribute("State.Graphics", "RelY", graphics)));
		
		readShapedElement(o,e);
		
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
	protected void readLineElement(LineElement o, Element e) throws ConverterException {
		readPathwayElement(o, e); /** TODO elementId, CommentGroup */

		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());

		String groupRef = getAttribute(base, "GroupRef", e); // TODO GroupRef
		o.setGroupRef((Group) o.getPathwayModel().getPathwayElement(groupRef));
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
			Point pt = new Point(elementId, arrowHead, new Coordinate(x, y));
			pts.add(pt);
			if (elementRef != null) {
				double relX = Double.parseDouble(point.getAttributeValue("RelX"));
				double relY = Double.parseDouble(point.getAttributeValue("RelY"));
				pt.setElementRef(elementRef);
				pt.setRelX(relX);
				pt.setRelY(relY);
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
	protected void readLabels(Label o, Element e) throws ConverterException {
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
	
	protected void readGroup(Group o, Element e) throws ConverterException {

		readShapedElement(o, e); // elementId, CommentGroup, groupRef, RectProperty, FontProperty,
									// ShapeStyleProperty
		/** TODO GroupID */
		String groupId = e.getAttributeValue("GroupId");
		if ((groupId == null || groupId.equals("")) && o.getPathwayModel() != null) {
			groupId = o.getPathwayModel().getUniqueGroupId();
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
	
	
	private static void addElementIds(Pathway pathway) throws ConverterException {
		for (PathwayElement pe : pathway.getDataObjects()) {
			String id = pe.getElementId();
			if (id == null || "".equals(id)) {
				if (pe.getObjectType() == ObjectType.LINE || pe.getObjectType() == ObjectType.GRAPHLINE) {
					// because we forgot to write out graphId's on Lines on older pathways
					// generate a graphId based on hash of coordinates
					// so that pathways with branching history still have the same id.
					// This part may be removed for future versions of GPML (2010+)

					StringBuilder builder = new StringBuilder();
					builder.append(pe.getMStartX());
					builder.append(pe.getMStartY());
					builder.append(pe.getMEndX());
					builder.append(pe.getMEndY());
					builder.append(pe.getStartLineType());
					builder.append(pe.getEndLineType());

					String newId;
					int i = 1;
					do {
						newId = "id" + Integer.toHexString((builder.toString() + ("_" + i)).hashCode());
						i++;
					} while (pathway.getGraphIds().contains(newId));
					pe.setElementId(newId);
				}
			}
		}
	}
	
}
