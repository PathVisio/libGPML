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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.type.*;
import org.pathvisio.util.ColorUtils;

/**
 * This class reads a PathwayModel from an input source (GPML 2013a).
 * 
 * @author finterly
 */
public class GPML2013aReader extends GpmlFormatAbstract implements GpmlFormatReader {

	public static final GPML2013aReader GPML2013aREADER = new GPML2013aReader("GPML2013a.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2013a"));

	public final static String PATHWAY_AUTHOR = "pathway_author_gpml2013a";
	public final static String PATHWAY_MAINTAINER = "pathway_maintainer_gpml2013a";
	public final static String PATHWAY_EMAIL = "pathway_email_gpml2013a";
	public final static String PATHWAY_LASTMODIFIED = "pathway_lastModified_gpml2013a";
	public final static String LEGEND_CENTER_X = "pathway_legend_centerX_gpml2013a";
	public final static String LEGEND_CENTER_Y = "pathway_legend_centerY_gpml2013a";

	public final static String GROUP_GRAPHID = "group_graphId_gpml2013a";
	public final static String OPT_BIOPAXREF = "optional_attribute_biopaxRef_gpml2013a";

	/* static variables for dynamic properties (named Attribute in GPML2013a) */
	public final static String DOUBLE_LINE_KEY = "org.pathvisio.DoubleLineProperty";
	public final static String CELL_CMPNT_KEY = "org.pathvisio.CellularComponentProperty";

	protected GPML2013aReader(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

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

		readBiopax(pathwayModel, root);// TODO
		readPathwayInfo(pathwayModel, root);
		/* reads groups first */
		readGroups(pathwayModel, root);
		readGroupGroupRef(pathwayModel, root);
		readLabels(pathwayModel, root);
		readShapes(pathwayModel, root);
		readDataNodes(pathwayModel, root);
		readStates(pathwayModel, root); // state elementRef refers to parent DataNode
		readInteractions(pathwayModel, root);
		readGraphicalLines(pathwayModel, root);
		/* checks groups have at least two pathway elements */
		checkGroupSize(pathwayModel.getGroups());
		/* reads point elementRefs last */
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
		/* sets optional properties */
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
		/* sets optional dynamic properties */
		String author = root.getAttributeValue("Author");
		String maintainer = root.getAttributeValue("Maintainer");
		String email = root.getAttributeValue("Email");
		String lastModified = root.getAttributeValue("Last-Modified");
		if (author != null) {
			pathway.setDynamicProperty(PATHWAY_AUTHOR, author);
		}
		if (maintainer != null) {
			pathway.setDynamicProperty(PATHWAY_MAINTAINER, maintainer);
		}
		if (email != null) {
			pathway.setDynamicProperty(PATHWAY_EMAIL, email);
		}
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
			String centerX = lgd.getAttributeValue("CenterX");
			pathway.setDynamicProperty(LEGEND_CENTER_X, centerX);
			String centerY = lgd.getAttributeValue("CenterY");
			pathway.setDynamicProperty(LEGEND_CENTER_Y, centerY);
		}
	}

	/**
	 * Read gpml:Biopax information openControlledVocabulary and PublicationXref.
	 * {@link #readBiopaxOpenControlledVocabulary(), #readBiopaxPublicationXref()}.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readBiopax(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element bp = root.getChild("Biopax", root.getNamespace());
		if (bp != null) {
			readBiopaxOpenControlledVocabulary(pathwayModel, bp);
			readBiopaxPublicationXref(pathwayModel, bp);
		}
	}

	/**
	 * Reads gpml:Biopax bp:OpenControlledVocabulary information to
	 * {@link Annotation} for pathway model from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param biopax       the biopax element.
	 * @throws ConverterException
	 */
	protected void readBiopaxOpenControlledVocabulary(PathwayModel pathwayModel, Element bp) throws ConverterException {
		for (Element ocv : bp.getChildren("openControlledVocabulary", GpmlFormat.BIOPAX)) {
			String elementId = pathwayModel.getUniqueElementId();
			String value = ocv.getChild("TERM", GpmlFormat.BIOPAX).getText();
			String biopaxOntology = ocv.getChild("Ontology", GpmlFormat.BIOPAX).getText();
			AnnotationType type = AnnotationType.register(biopaxOntology);
			Annotation annotation = new Annotation(elementId, pathwayModel, value, type);
			/*
			 * saves ID as Xref with biopaxOntology as dataSource TODO is Xref required...?
			 */
			String biopaxId = ocv.getChild("ID", GpmlFormat.BIOPAX).getText();
			Xref xref = readBiopaxXref(biopaxId, biopaxOntology);
			if (xref != null)
				annotation.setXref(xref);
			if (annotation != null)
				pathwayModel.addAnnotation(annotation);
		}
	}

	/**
	 * Reads gpml:Biopax bp:PublicationXref information to {@link Citation} for
	 * pathway model from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param biopax       the biopax element.
	 * @throws ConverterException
	 */
	protected void readBiopaxPublicationXref(PathwayModel pathwayModel, Element bp) throws ConverterException {

		for (Element pubxf : bp.getChildren("PublicationXref", GpmlFormat.BIOPAX)) {
			// TODO Is there ever multiple title, source, year?
			String elementId = pubxf.getAttributeValue("id", GpmlFormat.RDF);
			String biopaxId = pubxf.getChild("ID", GpmlFormat.BIOPAX).getText();
			String biopaxDatabase = pubxf.getChild("DB", GpmlFormat.BIOPAX).getText();
			Xref xref = readBiopaxXref(biopaxId, biopaxDatabase);
			Citation citation = new Citation(elementId, pathwayModel, xref);
			/* sets optional properties */
			String title = pubxf.getChild("TITLE", GpmlFormat.BIOPAX).getText();
			String source = pubxf.getChild("SOURCE", GpmlFormat.BIOPAX).getText();
			String year = pubxf.getChild("YEAR", GpmlFormat.BIOPAX).getText();
			if (title != null && !title.equals("")) {
				citation.setTitle(title);
			}
			if (source != null && !source.equals("")) {
				citation.setSource(source);
			}
			if (year != null && !year.equals("")) {
				citation.setYear(year);
			}
			List<String> authors = new ArrayList<String>();
			for (Element au : pubxf.getChildren("AUTHORS", GpmlFormat.BIOPAX)) {
				String author = au.getText();
				if (author != null)
					authors.add(author);
			}
			if (!authors.isEmpty())
				citation.setAuthors(authors);
			if (citation != null)
				pathwayModel.addCitation(citation);
		}
	}

	/**
	 * Reads biopax xref information.
	 * 
	 * @param identifier the xref identifier.
	 * @param dataSource the xref data source.
	 * @return the xref with given identifier and dataSource.
	 * @throws ConverterException
	 */
	protected Xref readBiopaxXref(String identifier, String dataSource) throws ConverterException {
		if (dataSource != null && !dataSource.equals("")) {
			if (DataSource.fullNameExists(dataSource)) {
				return new Xref(identifier, DataSource.getExistingByFullName(dataSource));
			} else if (DataSource.systemCodeExists(dataSource)) {
				return new Xref(identifier, DataSource.getByAlias(dataSource));
			} else {
				DataSource.register(dataSource, dataSource);
				System.out.println("DataSource: " + dataSource + " is registered."); // TODO warning
				return new Xref(identifier, DataSource.getExistingByFullName(dataSource));
			}
		}
		return null;
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
		// TODO PublicationXref?????
		readPathwayBiopaxRefs(pathwayModel, root);
		readPathwayDynamicProperties(pathwayModel, root); // dynamic properties
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
				Comment comment = new Comment(content);
				if (source != null && !source.equals(""))
					comment.setSource(source);
				pathwayModel.getPathway().addComment(new Comment(source, content));
			}
		}
	}

	/**
	 * Reads biopax reference information {@link Pathway#addCitationRef()} for
	 * pathway model from root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayBiopaxRefs(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element bpRef : root.getChildren("BiopaxRef", root.getNamespace())) {
			Citation biopaxRef = (Citation) pathwayModel.getPathwayElement(bpRef.getText());
			if (biopaxRef != null) {
				pathwayModel.getPathway().addCitationRef(biopaxRef);
			}
		}
	}

	/**
	 * Reads gpml:Attribute or dynamic property {@link Pathway#setDynamicProperty()}
	 * information for pathway from root element.
	 * 
	 * NB: Property (dynamic property) was named Attribute in GPML2013a.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readPathwayDynamicProperties(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element dp : root.getChildren("Attribute", root.getNamespace())) {
			String key = dp.getAttributeValue("Key");
			String value = dp.getAttributeValue("Value");
			pathwayModel.getPathway().setDynamicProperty(key, value);
		}
	}

	/**
	 * Reads group {@link Group} information for pathway model from root element.
	 * 
	 * NB: A group has identifier GroupId (essentially ElementId), while GraphId is
	 * optional. A group has GraphId if there is at least one {@link Point}
	 * referring to this group by GraphRef.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readGroups(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element grp : root.getChildren("Group", root.getNamespace())) {
			String elementId = grp.getAttributeValue("GroupId");
			if (elementId == null)
				elementId = pathwayModel.getUniqueElementId();
			GroupType type = GroupType.register(grp.getAttributeValue("Style"));
			Element gfx = grp.getChild("Graphics", grp.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = new FontProperty(Color.decode("#808080"), "Arial", false, false, false, false,
					12, HAlignType.CENTER, VAlignType.MIDDLE);
			ShapeStyleProperty shapeStyleProperty = readGroupShapeStyleProperty(type);
			Group group = new Group(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, type);
			/* group of type "Pathway" has custom default font size and name */
			if (type.getName() == "Pathway") {
				group.getFontProperty().setFontSize(32);
				group.getFontProperty().setFontName("Times"); // TODO
			}
			/* reads comment group, evidenceRefs */
			readElementInfo(group, grp);
			/* sets optional properties */
			String textLabel = grp.getAttributeValue("TextLabel");
			String graphId = grp.getAttributeValue("GraphId");
			if (textLabel != null)
				group.setTextLabel(textLabel);
			if (graphId != null) {
				group.setDynamicProperty(GROUP_GRAPHID, graphId); // TODO handle graphId, make sure unique?
				pathwayModel.addElementId(graphId, group); // graphId key added for this group
			}
			if (group != null)
				pathwayModel.addGroup(group);
		}

	}

	/**
	 * Reads groupRef property {@link Group#setGroupRef(Group)} information of group
	 * pathway element. Because a group may refer to another group not yet
	 * initialized. We read and set groupRef after reading all group elements.
	 * 
	 * NB: this method is separated out because {@link #readGroups()} method became
	 * long.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void readGroupGroupRef(PathwayModel pathwayModel, Element root) throws ConverterException {
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
	 * Reads default shape style property for type of group. In 2013a, group
	 * graphics was hard coded for each group type in GroupPainterRegistry.java.
	 * {@link Group#setShapeStyleProperty()}. Hover fillColor implemented in view.
	 * 
	 * @param type the group type.
	 * @returns the shapeStyleProperty object.
	 * @throws ConverterException
	 */
	protected ShapeStyleProperty readGroupShapeStyleProperty(GroupType type) throws ConverterException {
		if (type.getName() == "Group") {
			/* fillColor translucent blue, hovers to transparent */
			return new ShapeStyleProperty(Color.decode("#808080"), LineStyleType.DASHED, 1.0, Color.decode("#0000ff0c"),
					ShapeType.RECTANGLE);
		} else if (type.getName() == "Complex") {
			/* fillColor translucent yellowish-gray, hovers to translucent red #ff00000c */
			return new ShapeStyleProperty(Color.decode("#808080"), LineStyleType.SOLID, 1.0, Color.decode("#b4b46419"),
					ShapeType.OCTAGON);
		} else if (type.getName() == "Pathway") {
			/* fontSize 32, fontName "Times" (was not implemented) */
			/* fillColor translucent green, hovers to more opaque green #00ff0019 */
			return new ShapeStyleProperty(Color.decode("#808080"), LineStyleType.SOLID, 1.0, Color.decode("#00ff000c"),
					ShapeType.RECTANGLE);
		} else {
			/* GroupType "None", or default */
			/* fillColor translucent yellowish-gray, hovers to translucent red #ff00000c */
			return new ShapeStyleProperty(Color.decode("#808080"), LineStyleType.DASHED, 1.0, Color.decode("#b4b46419"),
					ShapeType.RECTANGLE);
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
			if (elementId == null)
				elementId = pathwayModel.getUniqueElementId();
			String textLabel = lb.getAttributeValue("TextLabel");
			Element gfx = lb.getChild("Graphics", lb.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			Label label = new Label(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, textLabel);
			/* reads comment group, evidenceRefs */
			readShapedElement(label, lb);
			/* sets optional properties */
			String href = lb.getAttributeValue("Href");
			String groupRef = lb.getAttributeValue("GroupRef");
			if (href != null)
				label.setHref(href);
			if (groupRef != null && !groupRef.equals(""))
				label.setGroupRef((Group) label.getPathwayModel().getPathwayElement(groupRef));
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
			if (elementId == null)
				elementId = pathwayModel.getUniqueElementId();
			Element gfx = shp.getChild("Graphics", shp.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			double rotation = Double.parseDouble(gfx.getAttributeValue("Rotation"));
			Shape shape = new Shape(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, rotation);
			/* reads comment group, evidenceRefs */
			readShapedElement(shape, shp); // TODO handle dynamic properties....
			/* sets optional properties */
			String textLabel = shp.getAttributeValue("TextLabel");
			String groupRef = shp.getAttributeValue("GroupRef");
			if (textLabel != null)
				shape.setTextLabel(textLabel);
			if (groupRef != null && !groupRef.equals(""))
				shape.setGroupRef((Group) shape.getPathwayModel().getPathwayElement(groupRef));
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
			if (elementId == null)
				elementId = pathwayModel.getUniqueElementId();
			Element gfx = dn.getChild("Graphics", dn.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			String textLabel = dn.getAttributeValue("TextLabel");
			DataNodeType type = DataNodeType.register(dn.getAttributeValue("Type"));
			Xref xref = readXref(dn);
			DataNode dataNode = new DataNode(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty,
					textLabel, type, xref);
			/* reads comment group, evidenceRefs */
			readShapedElement(dataNode, dn);
			/* sets optional properties */
			String groupRef = dn.getAttributeValue("GroupRef");
			if (groupRef != null && !groupRef.equals(""))
				dataNode.setGroupRef((Group) pathwayModel.getPathwayElement(groupRef));
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
			if (elementId == null)
				elementId = pathwayModel.getUniqueElementId();
			String textLabel = st.getAttributeValue("TextLabel");
			StateType type = StateType.register(st.getAttributeValue("StateType"));
			Element gfx = st.getChild("Graphics", st.getNamespace());
			double relX = Double.parseDouble(gfx.getAttributeValue("RelX"));
			double relY = Double.parseDouble(gfx.getAttributeValue("RelY"));
			double width = Double.parseDouble(gfx.getAttributeValue("Width"));
			double height = Double.parseDouble(gfx.getAttributeValue("Height"));
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			/* finds parent datanode from state elementRef */
			String elementRef = st.getAttributeValue("ElementRef");
			DataNode dataNode = (DataNode) pathwayModel.getPathwayElement(elementRef);
			/* finally instantiate state */
			State state = new State(elementId, pathwayModel, dataNode, textLabel, type, relX, relY, width, height,
					fontProperty, shapeStyleProperty);
			/* reads comment group */
			readElementInfo(state, st);
			readStateDynamicProperties(state, st);
			// TODO looks okay for now?
			if ("Double".equals(state.getDynamicProperty("org.pathvisio.DoubleLineProperty"))) {
				state.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
			}
			/* sets optional properties */
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
		readShapedDynamicProperties(shapedElement, se);
	}

	/**
	 * @param shapedElement
	 * @param e
	 * @throws ConverterException
	 */
	protected void mapShapeType(ShapedElement shapedElement, Element e) throws ConverterException {
		Element gfx = e.getChild("Graphics", e.getNamespace());
		ShapeType shapeType = ShapeType.fromName(gfx.getAttributeValue("ShapeType"));

		/* check deprecated shape type map */
		if (ShapeType.DEPRECATED_MAP.containsKey(shapeType)) {
			ShapeType shapeTypeNew = ShapeType.DEPRECATED_MAP.get(shapeType);
			shapedElement.getShapeStyleProperty().setShapeType(shapeTypeNew);
		} else {
			shapedElement.getShapeStyleProperty().setShapeType(shapeType);
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
			if (elementId == null)
				elementId = pathwayModel.getUniqueElementId();
			Element gfx = ia.getChild("Graphics", ia.getNamespace());
			LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
			Xref xref = readXref(ia);
			Interaction interaction = new Interaction(elementId, pathwayModel, lineStyleProperty, xref);
			/* reads comment group, evidenceRefs */
			readLineElement(interaction, ia);
			if (interaction != null)
				pathwayModel.addInteraction(interaction);
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
			if (elementId == null)
				elementId = pathwayModel.getUniqueElementId();
			Element gfx = gln.getChild("Graphics", gln.getNamespace());
			LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
			GraphicalLine graphicalLine = new GraphicalLine(elementId, pathwayModel, lineStyleProperty);
			readLineElement(graphicalLine, gln);
			if (graphicalLine != null)
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
		readLineDynamicProperties(lineElement, ln);
		Element gfx = ln.getChild("Graphics", ln.getNamespace());
		readPoints(lineElement, gfx);
		/* checks if line has at least 2 point */
		if (lineElement.getPoints().size() < 2) {
			throw new ConverterException("Line " + lineElement.getElementId() + " has " + lineElement.getPoints().size()
					+ " point(s),  must have at least 2.");
		}
		readAnchors(lineElement, gfx);
		/* sets optional properties */
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
			if (elementId == null)
				elementId = lineElement.getPathwayModel().getUniqueElementId();
			ArrowHeadType arrowHead = ArrowHeadType.register(pt.getAttributeValue("ArrowHead"));
			Coordinate xy = new Coordinate(Double.parseDouble(pt.getAttributeValue("X")),
					Double.parseDouble(pt.getAttributeValue("Y")));
			Point point = new Point(elementId, lineElement.getPathwayModel(), lineElement, arrowHead, xy);
			if (point != null) // sets elementRef and optional properties later
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
			if (elementId == null)
				elementId = lineElement.getPathwayModel().getUniqueElementId();
			double position = Double.parseDouble(an.getAttributeValue("Position"));
//			Coordinate xy = new Coordinate(); // TODO calculate!!
			AnchorType shapeType = AnchorType.register(an.getAttributeValue("Shape"));
			Anchor anchor = new Anchor(elementId, lineElement.getPathwayModel(), lineElement, position, shapeType);
			if (anchor != null)
				lineElement.addAnchor(anchor);
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
						String elementRefStr = pt.getAttributeValue("GraphRef");
						if (elementRefStr != null && !elementRefStr.equals("")) {
							PathwayElement elementRef = pathwayModel.getPathwayElement(elementRefStr);
							if (elementRef != null) {
								String elementId = pt.getAttributeValue("GraphId");
								Point point = (Point) pathwayModel.getPathwayElement(elementId);
								point.setElementRef(elementRef);
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
			String identifier = xref.getAttributeValue("identifier");
			String dataSource = xref.getAttributeValue("dataSource");
			if (dataSource != null && !dataSource.equals("")) {
				if (DataSource.fullNameExists(dataSource)) {
					return new Xref(identifier, DataSource.getExistingByFullName(dataSource));
				} else if (DataSource.systemCodeExists(dataSource)) {
					return new Xref(identifier, DataSource.getByAlias(dataSource));
				} else {
					DataSource.register(dataSource, dataSource);
					System.out.println("DataSource: " + dataSource + " is registered."); // TODO warning
					return new Xref(identifier, DataSource.getExistingByFullName(dataSource)); // TODO fullname/code
				}
			}
		}
		return null;
	}

	/**
	 * Reads comment group (comment, dynamic property, annotationRef, citationRef)
	 * and elementRef {@link ElementInfo} information, , for pathway element from
	 * element.
	 * 
	 * NB: dynamic properties read by {@link #readLineDynamicProperties()} ,
	 * {@link #readShapedDynamicProperties()} ,
	 * {@link #readStateDynamicProperties()}
	 * 
	 * @param elementInfo the element info pathway element object.
	 * @param e           the pathway element element.
	 * @throws ConverterException
	 */
	private void readElementInfo(ElementInfo elementInfo, Element e) throws ConverterException {
		/*
		 * Biopax attribute for DataNode, State, Interaction, GraphicalLine, Label,
		 * Shape, Group TODO
		 */
		String biopaxRefStr = e.getAttributeValue("BiopaxRef");
		if (biopaxRefStr != null) {
			elementInfo.setDynamicProperty(OPT_BIOPAXREF, biopaxRefStr);
//			Citation biopaxRef = (Citation) elementInfo.getPathwayModel().getPathwayElement(biopaxRefStr);
//			if (biopaxRef != null)
//				elementInfo.addCitationRef(biopaxRef);
		}
		readComments(elementInfo, e);
		//PublicationXref TODO
		readBiopaxRefs(elementInfo, e);
		//readDynamicProperties (see above)
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
	 * Reads BiopaxRef information to {@link ElementInfo#addCitationRef()}
	 * information for pathway element from element.
	 * 
	 * @param elementInfo the element info pathway element object.
	 * @param e           the pathway element element.
	 * @throws ConverterException
	 */
	protected void readBiopaxRefs(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element bpRef : e.getChildren("BiopaxRef", e.getNamespace())) {
			Citation biopaxRef = (Citation) elementInfo.getPathwayModel().getPathwayElement(bpRef.getText());
			if (biopaxRef != null) {
				elementInfo.addCitationRef(biopaxRef);
			}
		}
	}

	/**
	 * Reads dynamic property {@link ElementInfo#setDynamicProperty()} information
	 * for interaction or graphicalLine pathway element. If dynamic property codes
	 * for DoubleLineProperty, updates lineStyle. Otherwise, sets dynamic property.
	 * 
	 * NB: Property (dynamic property) was named Attribute in GPML2013a.
	 * 
	 * @param lineElement the line pathway element.
	 * @param ln          the line element element.
	 * @throws ConverterException
	 */
	protected void readLineDynamicProperties(LineElement lineElement, Element ln) throws ConverterException {
		for (Element dp : ln.getChildren("Attribute", ln.getNamespace())) {
			String key = dp.getAttributeValue("Key");
			String value = dp.getAttributeValue("Value");
			/* dynamic property DoubleLineProperty sets lineStyle */
			if (key.equals(DOUBLE_LINE_KEY) && value.equals("Double")) {
				lineElement.getLineStyleProperty().setLineStyle(LineStyleType.DOUBLE);
			} else {
				lineElement.setDynamicProperty(key, value);
			}
		}
	}

	/**
	 * Reads dynamic property {@link ElementInfo#setDynamicProperty()} information
	 * for shaped pathway elements. If dynamic property codes for DoubleLineProperty
	 * or CellularComponentProperty, updates borderStyle or shapeType. Otherwise,
	 * sets dynamic property.
	 * 
	 * NB: Property (dynamic property) was named Attribute in GPML2013a.
	 * 
	 * @param shapedElement the shaped pathway element.
	 * @param se            the shaped element element.
	 * @throws ConverterException
	 */
	protected void readShapedDynamicProperties(ShapedElement shapedElement, Element se) throws ConverterException {
		for (Element dp : se.getChildren("Attribute", se.getNamespace())) {
			String key = dp.getAttributeValue("Key");
			String value = dp.getAttributeValue("Value");
			if (key.equals(DOUBLE_LINE_KEY) && value.equals("Double")) {
				shapedElement.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
			} else if (key.equals(CELL_CMPNT_KEY)) {
				ShapeType type = ShapeType.register(value);
				shapedElement.getShapeStyleProperty().setShapeType(type);
			} else {
				shapedElement.setDynamicProperty(key, value);
			}
		}
	}

	/**
	 * Reads dynamic property {@link ElementInfo#setDynamicProperty()} information
	 * for state pathway element. If dynamic property codes for DoubleLineProperty
	 * or CellularComponentProperty, updates borderStyle or shapeType. Otherwise,
	 * sets dynamic property.
	 * 
	 * NB: Property (dynamic property) was named Attribute in GPML2013a.
	 * 
	 * @param state the state pathway element.
	 * @param st    the state element element.
	 * @throws ConverterException
	 */
	protected void readStateDynamicProperties(State state, Element st) throws ConverterException {
		for (Element dp : st.getChildren("Attribute", st.getNamespace())) {
			String key = dp.getAttributeValue("Key");
			String value = dp.getAttributeValue("Value");
			if (key.equals(DOUBLE_LINE_KEY) && value.equals("Double")) {
				state.getShapeStyleProperty().setBorderStyle(LineStyleType.DOUBLE);
			} else if (key.equals(CELL_CMPNT_KEY)) {
				ShapeType type = ShapeType.register(value);
				state.getShapeStyleProperty().setShapeType(type);
			} else {
				state.setDynamicProperty(key, value);
			}
		}
	}

	/**
	 * Reads rect property {@link RectProperty} information. Jdom handles schema
	 * default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @returns the rectProperty object.
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
	 * TODO fix...
	 * 
	 * Reads font property {@link FontProperty} information. Jdom handles schema
	 * default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @returns the fontProperty object.
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
	 * TODO fix...
	 * 
	 * Reads shape style property {@link ShapeStyleProperty} information. Jdom
	 * handles schema default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @returns the shapeStyleProperty object.
	 * @throws ConverterException
	 */
	protected ShapeStyleProperty readShapeStyleProperty(Element gfx) throws ConverterException {
		Color borderColor = ColorUtils.stringToColor(gfx.getAttributeValue("Color"));
		LineStyleType borderStyle = LineStyleType.register(gfx.getAttributeValue("LineStyle"));
		double borderWidth = Double.parseDouble(gfx.getAttributeValue("LineThickness"));
		Color fillColor = ColorUtils.stringToColor(gfx.getAttributeValue("FillColor"));
		ShapeType shapeType = ShapeType.register(gfx.getAttributeValue("ShapeType"));
		String zOrder = gfx.getAttributeValue("ZOrder");
		ShapeStyleProperty shapeStyleProperty = new ShapeStyleProperty(borderColor, borderStyle, borderWidth, fillColor,
				shapeType);
		if (zOrder != null) {
			shapeStyleProperty.setZOrder(Integer.parseInt(zOrder));
		}
		return shapeStyleProperty;
	}

	/**
	 * TODO fix...DOUBLE....
	 * 
	 * Reads line style property {@link LineStyleProperty} information. Jdom handles
	 * schema default values.
	 * 
	 * @param gfx the parent graphics element.
	 * @returns the lineStyleProperty object.
	 * @throws ConverterException
	 */
	protected LineStyleProperty readLineStyleProperty(Element gfx) throws ConverterException {
		Color lineColor = ColorUtils.stringToColor(gfx.getAttributeValue("Color"));
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

	/**
	 * Checks whether groups have at least two pathway element members.
	 * 
	 * @param groups the list of groups.
	 * @throws ConverterException
	 */
	protected void checkGroupSize(List<Group> groups) throws ConverterException {
		for (Group group : groups) {
			if (group.getPathwayElements().size() < 2) {
				throw new ConverterException("Group " + group.getElementId() + " has "
						+ group.getPathwayElements().size() + " pathway element(s) members,  must have at least 2");
			}
		}
	}

}
