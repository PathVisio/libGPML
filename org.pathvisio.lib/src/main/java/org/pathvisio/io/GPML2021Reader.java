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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.type.*;

public class GPML2021Reader {

	XMLReaderJDOMFactory schemafactory = new XMLReaderXSDFactory(xsdfile);
	SAXBuilder builder = new SAXBuilder(schemafactory);
	Document readDoc = builder.build(new File("test.xml"));
	Element root = readDoc.getRootElement();

	public PathwayModel readRoot(Element root) throws ConverterException {

		Pathway pathway = readPathway(root);
		PathwayModel pathwayModel = new PathwayModel(pathway); //TODO think about order
		
		readAuthors(pathwayModel, root);

		readAnnotations(pathwayModel, root);
		readCitations(pathwayModel, root);
		readEvidences(pathwayModel, root);
		
		//TODO read CommentRefs 

		readGroups(pathwayModel, root); // TODO group can have groupRef
		readDataNodes(pathwayModel, root); // TODO will elementRef (refers to only Group?)
		readInteractions(pathwayModel, root); // contains waypoints
		readGraphicalLines(pathwayModel, root); // contains waypoints
		readLabels()pathwayModel, root;
		readShapes(pathwayModel, root);

		Logger.log.trace("End copying read elements");

		// Add graphIds for objects that don't have one
		addElementIds(pwy);

		// Convert absolute point coordinates of linked points to
		// relative coordinates
		convertPointCoordinates(pwy);
	}

	protected Pathway readPathway(Element root) throws ConverterException {
		String title = root.getAttributeValue("title");
		Element gfx = root.getChild("Graphics");
		double boardWidth = Double.parseDouble(gfx.getAttributeValue("boardWidth"));
		double boardHeight = Double.parseDouble(gfx.getAttributeValue("boardHeight"));
		Color backgroundColor = ColorUtils.stringToColor(gfx.getAttributeValue("backgroundColor")); // TODO optional?
		Coordinate infoBox = readInfoBox(root);
		Pathway pathway = new Pathway.PathwayBuilder(title, boardWidth, boardHeight, backgroundColor, infoBox).build();
		/* optional properties */
		Xref xref = readXref(root);
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

	protected Xref readXref(Element e) throws ConverterException {
		Element xref = e.getChild("Xref", root.getNamespace());
		String identifier = xref.getAttributeValue("identifier");
		String dataSource = xref.getAttributeValue("dataSource");
		if (DataSource.fullNameExists(dataSource)) {
			return new Xref(identifier, DataSource.getExistingByFullName(dataSource));
		} else if (DataSource.systemCodeExists(dataSource)) {
			return new Xref(identifier, DataSource.getByAlias(dataSource));
		} else {
			System.out.println("Invalid xref dataSource: " + dataSource);
			return null;
//			throw new IllegalArgumentException("Invalid xref dataSource: " + dataSource);
		}
	}

	protected Coordinate readInfoBox(Element root) {
		double centerX = Double.parseDouble(root.getAttributeValue("centerX"));
		double centerY = Double.parseDouble(root.getAttributeValue("centerY"));
		return new Coordinate(centerX, centerY);
	}

	protected void readAuthors(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element aus = root.getChild("Authors", root.getNamespace());
		for (Element au : aus.getChildren("Author", root.getNamespace())) {
			String name = au.getAttributeValue("name");
			String fullName = au.getAttributeValue("fullName");
			String email = au.getAttributeValue("email");
			Author author = new Author.AuthorBuilder(name).build();
			if (fullName != null) {
				author.setFullName(fullName);
			}
			if (email != null) {
				author.setEmail(email);
			}
			pathwayModel.addAuthor(author);
		}
	}

	protected Pathway readPathwayInfo(Pathway pathway, Element root) throws ConverterException {
		pathway.addComment(null);
		pathway.setDynamicProperty("", "");
		pathway.addAnnotationRef(null);
		pathway.addCitationRef(null);
		pathway.addEvidenceRef(null);
	}

	protected List<Comment> readComments(Element e) throws ConverterException {
		List<Comment> comments = new ArrayList<Comment>();
		for (Element cmt : e.getChildren("Comment", e.getNamespace())) {
			String source = cmt.getAttributeValue("Source");
			String content = cmt.getText();
			// TODO check if null
			comments.add(new Comment(source, content));
		}
		return comments;
	}

	protected Map<String, String> readDynamicProperties(Element e) throws ConverterException {
		Map<String, String> dynamicProperties = new TreeMap<String, String>();
		for (Element dp : e.getChildren("Property", e.getNamespace())) {
			String key = dp.getAttributeValue("key");
			String value = dp.getAttributeValue("value");
			dynamicProperties.put(key, value);
		}
		return dynamicProperties;
	}

	protected List<AnnotationRef> readAnnotationRefs(PathwayModel pathwayModel, ElementInfo o, Element e)
			throws ConverterException {
		List<AnnotationRef> annotationRefs = new ArrayList<AnnotationRef>();
		for (Element anntRef : e.getChildren("AnnotationRef", e.getNamespace())) {
			Annotation annotation = (Annotation) pathwayModel
					.getPathwayElement(anntRef.getAttributeValue("elementRef"));
			List<Citation> citationRefs = readCitationRefs(pathwayModel, o, anntRef);
			List<Evidence> evidenceRefs = readEvidenceRefs(pathwayModel, o, anntRef);
			AnnotationRef annotationRef = new AnnotationRef(annotation, citationRefs, evidenceRefs);
		}
	}

	protected List<Citation> readCitationRefs(PathwayModel pathwayModel, ElementInfo o, Element e)
			throws ConverterException {
		List<Citation> citationRefs = new ArrayList<Citation>();
		for (Element citRef : e.getChildren("CitationRef", e.getNamespace())) {
			Citation citationRef = (Citation) pathwayModel.getPathwayElement(citRef.getAttributeValue("elementRef"));
			citationRefs.add(citationRef);
		}
		return citationRefs;
	}

	protected List<Evidence> readEvidenceRefs(PathwayModel pathwayModel, ElementInfo o, Element e)
			throws ConverterException {
		List<Evidence> evidenceRefs = new ArrayList<Evidence>();
		for (Element evidRef : e.getChildren("EvidenceRef", e.getNamespace())) {
			Evidence evidenceRef = (Evidence) pathwayModel.getPathwayElement(evidRef.getAttributeValue("elementRef"));
			evidenceRefs.add(evidenceRef);
		}
		return evidenceRefs;
	}

	private void readElementInfo(ElementInfo elementInfo, Element e) throws ConverterException {
		readElementId(e);
		readComments(e);
		readDynamicProperties(elementInfo, e);
		readAnnotationRefs(elementInfo, e);
		readCitationRefs(elementInfo, e);
		readEvidenceRefs(elementInfo, e);
	}

	protected void readGroupRef(ShapedElement o, Element e) {
		String groupRef = e.getAttributeValue("groupRef");
		if (groupRef != null && !groupRef.equals("")) {
			o.setGroupRef((Group) o.getPathwayModel().getPathwayElement(id));
		}
	}

	protected void readGroupRef(LineElement o, Element e) {
		String groupRef = e.getAttributeValue("groupRef");
		if (groupRef != null && !groupRef.equals("")) {
			o.setGroupRef((Group) o.getPathwayModel().getPathwayElement(id));
		}
	}

	/**
	 * Reads gpml RectAttributes from Graphics element and returns RectProperty.
	 * Default schema values are automatically handled by jdom
	 */
	protected RectProperty readRectProperty(Element gfx) throws ConverterException {
		double centerX = Double.parseDouble(gfx.getAttributeValue("centerX"));
		double centerY = Double.parseDouble(gfx.getAttributeValue("centerY"));
		double width = Double.parseDouble(gfx.getAttributeValue("width"));
		double height = Double.parseDouble(gfx.getAttributeValue("height"));
		return new RectProperty(new Coordinate(centerX, centerY), width, height);
	}

	/**
	 * Reads gpml FontAttributes from Graphics element and returns FontProperty.
	 * Default schema values are automatically handled by jdom
	 */
	protected FontProperty readFontProperty(Element gfx) throws ConverterException {
		Color textColor = ColorUtils.stringToColor(gfx.getAttributeValue("textColor"));
		String fontName = gfx.getAttributeValue("fontName");
		boolean fontWeight = gfx.getAttributeValue("fontWeight").equals("Bold");
		boolean fontStyle = gfx.getAttributeValue("fontStyle").equals("Italic");
		boolean fontDecoration = gfx.getAttributeValue("fontDecoration").equals("Underline");
		boolean fontStrikethru = gfx.getAttributeValue("fontStrikethru").equals("Strikethru");
		int fontSize = Integer.parseInt(gfx.getAttributeValue("fontSize"));
		HAlignType hAlignType = HAlignType.fromName(gfx.getAttributeValue("hAlign"));
		VAlignType vAlignType = VAlignType.fromName(gfx.getAttributeValue("vAlign"));
		return new FontProperty(textColor, fontName, fontWeight, fontStyle, fontDecoration, fontStrikethru, fontSize,
				hAlignType, vAlignType);
	}

	/**
	 * Reads gpml ShapeStyleAttributes from Graphics element and returns
	 * ShapeStyleProperty. Default schema values are automatically handled by jdom
	 */
	protected ShapeStyleProperty readShapeStyleProperty(Element gfx) throws ConverterException {
		Color borderColor = ColorUtils.stringToColor(gfx.getAttributeValue("borderColor"));
		LineStyleType borderStyle = LineStyleType.register(gfx.getAttributeValue("borderStyle"));
		double borderWidth = Double.parseDouble(gfx.getAttributeValue("borderWidth"));
		Color fillColor = ColorUtils.stringToColor(gfx.getAttributeValue("fillColor"));
		ShapeType shapeType = ShapeType.register(gfx.getAttributeValue("shapeType"));
		String zOrder = gfx.getAttributeValue("zOrder");
		ShapeStyleProperty shapeStyleProperty = new ShapeStyleProperty(fillColor, borderStyle, borderWidth, fillColor,
				shapeType);
		if (zOrder != null) {
			shapeStyleProperty.setZOrder(Integer.parseInt(zOrder));
		}
		return shapeStyleProperty;
	}

	/**
	 * Reads gpml LineStyleAttributes from Graphics element and returns
	 * LineStyleProperty. Default schema values are automatically handled by jdom
	 */
	protected LineStyleProperty readLineStyleProperty(Element gfx) throws ConverterException {
		Color lineColor = ColorUtils.stringToColor(gfx.getAttributeValue("lineColor"));
		LineStyleType lineStyle = LineStyleType.register(gfx.getAttributeValue("lineStyle"));
		double lineWidth = Double.parseDouble(gfx.getAttributeValue("lineWidth"));
		ConnectorType connectorType = ConnectorType.register(gfx.getAttributeValue("connectorType"));
		String zOrder = gfx.getAttributeValue("zOrder");
		LineStyleProperty lineStyleProperty = new LineStyleProperty(lineColor, lineStyle, lineWidth, connectorType);
		if (zOrder != null) {
			lineStyleProperty.setZOrder(Integer.parseInt(zOrder));
		}
	}



	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readDataNode(PathwayModel pathwayModel, Element e) throws ConverterException {
		// TODO STATE
		// TODO elementRef
		String textLabel = e.getAttributeValue("textLabel");
		String type = e.getAttributeValue("type");
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = xref.getAttributeValue("identifier");
		String dataSource = xref.getAttributeValue("dataSource");
		o.setTextLabel(textLabel);
		o.setType(DataNodeType.fromName(type));
		o.setXref(identifier, dataSource);
		readShapedElement(o, e); // elementId, CommentGroup, RectProperty, FontProperty,
									// ShapeStyleProperty
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readState(DataNode dataNode, Element e) throws ConverterException {
		readPathwayElement(o, e); // TODO: // ElemenId, CommentGroup
		// TODO
		String elementRef = ((Element) e.getParent()).getAttributeValue("elementId");

		if (elementRef != null) {
			o.setDataNode((DataNode) o.getPathwayModel().getPathwayElement(elementRef)); // TODO have element
		}
		o.setTextLabel(e.getAttributeValue("textLabel"));
		o.setType(StateType.fromName(e.getAttributeValue("type"))); // TODO Enum
		/** Graphics */
		Element graphics = e.getChild("Graphics", e.getNamespace());
		o.setRelX(Double.parseDouble(graphics.getAttributeValue("relX")));
		o.setRelY(Double.parseDouble(graphics.getAttributeValue("relY")));

		readShapedElement(o, e);

		/** TODO ShapeStyleProperty */

		/** Xref */
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = xref.getAttributeValue("identifier");
		String dataSource = xref.getAttributeValue("dataSource");
		o.setXref(identifier, dataSource);

	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readLineElement(PathwayModel pathwayModel, Element e) throws ConverterException {
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
	protected void readInteraction(PathwayModel pathwayModel, Element e) throws ConverterException {
		readLineElement(o, e);
		Element xref = e.getChild("Xref", e.getNamespace());
		String identifier = xref.getAttributeValue("identifier");
		String dataSource = xref.getAttributeValue("dataSource");
		o.setXref(identifier, dataSource);
	}

	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readLabels(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element lbs = root.getChild("Labels", root.getNamespace());
		for (Element lb: lbs.getChildren("Label", lbs.getNamespace())) {
			String elementId = lb.getAttributeValue("elementId"); 
			String textLabel = lb.getAttributeValue("textLabel");
			Element gfx = lb.getChild("Graphics", lb.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			Label label = new Label(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, textLabel);
			/* optional properties */
			String href = lb.getAttributeValue("href");
			String groupRef = lb.getAttributeValue("grouRef");
			if (href != null)
				label.setHref(href);
			if (groupRef != null)
				label.setGroupRef((Group) label.getPathwayModel().getPathwayElement(groupRef));
			if (label != null)
				pathwayModel.addLabel(label);
		}
	}

	protected void readShape(PathwayModel pathwayModel, Element e) throws ConverterException {
		Element shps = root.getChild("Shapes", root.getNamespace());
		for (Element shp: shps.getChildren("Shape", shps.getNamespace())) {
			String elementId = shp.getAttributeValue("elementId"); 
			Element gfx = shp.getChild("Graphics", shp.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			double rotation =  Double.parseDouble(gfx.getAttributeValue("rotation"));
			Shape shape = new Shape(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, rotation);
			/* optional properties */
			String textLabel = shp.getAttributeValue("textLabel");
			String groupRef = shp.getAttributeValue("grouRef");
			if (textLabel != null)
				shape.setTextLabel(textLabel);
			if (groupRef != null)
				shape.setGroupRef((Group) shape.getPathwayModel().getPathwayElement(groupRef));
			if (shape != null)
				pathwayModel.addShape(shape);
		}
	}
		
		
		

	protected void readGroup(PathwayModel pathwayModel, Element e) throws ConverterException {
		Element grps = root.getChild("Shapes", root.getNamespace());
		for (Element grp: grps.getChildren("Shape", grps.getNamespace())) {
			String elementId = grp.getAttributeValue("elementId"); 
			GroupType type = GroupType.register(grp.getAttributeValue("type")); 
			Element gfx = grp.getChild("Graphics", grp.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			double rotation =  Double.parseDouble(gfx.getAttributeValue("rotation")); 
			Group group = new Group(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, type);
			/* optional properties */
			String textLabel = grp.getAttributeValue("textLabel");
			String groupRef = grp.getAttributeValue("grouRef");
			Xref xref = readXref(grp);
			if (xref != null)
				group.setXref(xref);
			if (textLabel != null)
				group.setTextLabel(textLabel);
			if (groupRef != null)
				group.setGroupRef((Group) group.getPathwayModel().getPathwayElement(groupRef));
			if (group != null)
				pathwayModel.addGroup(group);
		}
		//TODO  List<PathwayElement> pathwayElements,
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
		Element gfx = e.getChild("Graphics", e.getNamespace());
		RectProperty rectProperty = readRectProperty(gfx);
		FontProperty fontProperty = readFontProperty(gfx);
		ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);

//		String groupRef = getAttribute(base, "GroupRef", e);
//		o.setGroupRef((Group) o.getPathwayModel().getPathwayElement(groupRef));
	}

//	/**
//	 * Converts deprecated shapes to contemporary analogs. This allows us to
//	 * maintain backward compatibility while at the same time cleaning up old shape
//	 * usages.
//	 * 
//	 */
//	/**
//	 * @param o
//	 * @param e
//	 * @throws ConverterException
//	 */
//	protected void readShapeType(Shape o, Element e) throws ConverterException {
//		String base = e.getName();
//		Element graphics = e.getChild("Graphics", e.getNamespace());
//		IShape s = ShapeRegistry.fromName(graphics.getAttributeValue("shapeType"));
//		if (ShapeType.DEPRECATED_MAP.containsKey(s)) {
//			s = ShapeType.DEPRECATED_MAP.get(s);
//			o.setShapeType(s);
//			if (s.equals(ShapeType.ROUNDED_RECTANGLE) || s.equals(ShapeType.OVAL)) {
//				o.setLineStyle(LineStyleType.DOUBLE);
//				o.setLineThickness(3.0);
//				o.setColor(Color.LIGHT_GRAY);
//			}
//		} else {
//			o.setShapeType(s);
//			mapLineStyle(o, e); // LineStyle
//		}
//	}

	public void readFromRoot(Element root, Pathway pwy) throws ConverterException {
		mapElement(root, pwy); // MappInfo

		// Iterate over direct children of the root element
		for (Object e : root.getChildren()) {
			mapElement((Element) e, pwy);
		}
		Logger.log.trace("End copying map elements");

		// Add graphIds for objects that don't have one
		addElementIds(pwy);

		// Convert absolute point coordinates of linked points to
		// relative coordinates
		convertPointCoordinates(pwy);
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
