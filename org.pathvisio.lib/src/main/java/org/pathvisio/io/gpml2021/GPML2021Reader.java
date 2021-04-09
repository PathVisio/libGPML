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
package org.pathvisio.io.gpml2021;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.ByteArrayInputStream;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.pathvisio.debug.Logger;
import org.pathvisio.io.ColorUtils;
import org.pathvisio.io.ConverterException;
import org.pathvisio.io.Group;
import org.pathvisio.io.LineElement;
import org.pathvisio.io.PathwayModel;
import org.pathvisio.io.ShapedElement;
import org.pathvisio.model.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.type.*;

public class GPML2021Reader extends GPML2021FormatAbstract implements GPMLReader {

	public static final GPML2021Reader GPML2021READER = new GPML2021Reader("GPML2021.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2021"));

	protected GPML2021Reader(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

	static final File xsdFile = new File("GPML2021.xsd");
	// TODO how to best handle namespace?
	static final Namespace nsGPML = Namespace.getNamespace("http://pathvisio.org/GPML/2021");

	public void readGPML(InputStream is) throws ConverterException {
		PathwayModel pathwayModel = null;
		try {
			XMLReaderJDOMFactory schemafactory = new XMLReaderXSDFactory(xsdFile); // schema
			SAXBuilder builder = new SAXBuilder(schemafactory);
			Document doc = builder.build(is);
			Element root = doc.getRootElement();
			System.out.println("Root: " + doc.getRootElement());
			pathwayModel = readRoot(root);
		} catch (JDOMException e) {
			throw new ConverterException(e);
		} catch (IOException e) {
			throw new ConverterException(e);
		} catch (Exception e) {
			throw new ConverterException(e); // TODO e.printStackTrace()?
		}
		return pathwayModel; // TODO do we want to return pathway or not?
	}

	/**
	 * Read the JDOM document from the file specified
	 * 
	 * @param file the file from which the JDOM document should be read.
	 * @throws ConverterException
	 */
	public PathwayModel readGPML(File file) throws ConverterException {
		InputStream is;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new ConverterException(e);
		}
		return readGPML(is);
	}

	/**
	 * Read the JDOM document from the file specified
	 * 
	 * @param s      the string input.
	 * @param string the file from which the JDOM document should be read.
	 * @throws ConverterException
	 */
	public PathwayModel readGPML(String str) throws ConverterException {
		if (str == null)
			return null;
		InputStream is;
		try {
			is = stringToInputStream(str);// TODO does this work?
		} catch (Exception e) {
			throw new ConverterException(e);
		}
		return readGPML(is);
	}

	// METHOD FROM UTILS
	public static InputStream stringToInputStream(String str) {
		if (str == null)
			return null;
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
		} catch (Exception ex) {
		}
		return is;
	}

	public void readFromRoot(PathwayModel pathwayModel, Element root) throws ConverterException {
		Pathway pathway = readPathway(root);
		pathwayModel.setPathway(pathway); //= new PathwayModel(pathway); // TODO think about order

		readAuthors(pathwayModel, root);

		readAnnotations(pathwayModel, root);
		readCitations(pathwayModel, root);
		readEvidences(pathwayModel, root);

		readPathwayInfo(pathwayModel, root); // comment group and evidenceRefs

		readGroups(pathwayModel, root); // TODO read group first
		readLabels(pathwayModel, root);
		readShapes(pathwayModel, root);

		readDataNodes(pathwayModel, root); // TODO will elementRef (refers to only Group?)

		readInteractions(pathwayModel, root);
		readGraphicalLines(pathwayModel, root);
		readLinePoints(pathwayModel, root); // TODO reads points last due to possible reference to anchor

		Logger.log.trace("End reading gpml");
		// TODO check groups have at least one pathwayElement inside?
		// TODO check at least 2 points per line element?
		// TODO handle relative and absolute coordinates
//		return pathwayModel;
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
		Element xref = e.getChild("Xref", e.getNamespace());
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

	protected void readAnnotations(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element annts = root.getChild("Annotations", root.getNamespace());
		for (Element annt : annts.getChildren("Annotation", annts.getNamespace())) {
			String elementId = annt.getAttributeValue("elementId");
			String value = annt.getAttributeValue("value");
			AnnotationType type = AnnotationType.register(annt.getAttributeValue("type"));
			Annotation annotation = new Annotation(elementId, pathwayModel, value, type);
			/* optional properties */
			Xref xref = readXref(annt);
			String url = annt.getAttributeValue("url");
			if (xref != null)
				annotation.setXref(xref);
			if (url != null)
				annotation.setUrl(url);
			if (annotation != null)
				pathwayModel.addAnnotation(annotation);
		}
	}

	protected void readCitations(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element cits = root.getChild("Citations", root.getNamespace());
		for (Element cit : cits.getChildren("Citation", cits.getNamespace())) {
			String elementId = cit.getAttributeValue("elementId");
			Xref xref = readXref(cit);
			Citation citation = new Citation(elementId, pathwayModel, xref);
			/* optional properties */
			String url = cit.getAttributeValue("url");
			if (url != null)
				citation.setUrl(url);
			if (citation != null)
				pathwayModel.addCitation(citation);
		}
	}

	protected void readEvidences(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element evids = root.getChild("Evidences", root.getNamespace());
		for (Element evid : evids.getChildren("Evidence", evids.getNamespace())) {
			String elementId = evid.getAttributeValue("elementId");
			Xref xref = readXref(evid);
			Evidence evidence = new Evidence(elementId, pathwayModel, xref);
			/* optional properties */
			String value = evid.getAttributeValue("value");
			String url = evid.getAttributeValue("url");
			if (value != null)
				evidence.setValue(value);
			if (url != null)
				evidence.setUrl(url);
			if (evidence != null)
				pathwayModel.addEvidence(evidence);
		}
	}

	protected void readPathwayInfo(PathwayModel pathwayModel, Element root) throws ConverterException {
		readPathwayComments(pathwayModel, root);
		readPathwayDynamicProperties(pathwayModel, root);
		readPathwayAnnotationRefs(pathwayModel, root);
		readPathwayCitationRefs(pathwayModel, root);
		readPathwayEvidenceRefs(pathwayModel, root);
	}

	protected void readPathwayComments(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element cmt : root.getChildren("Comment", root.getNamespace())) {
			String source = cmt.getAttributeValue("Source");
			String content = cmt.getText();
			if (content != null && !content.equals("")) {
				Comment comment = new Comment(content); // TODO needs parent pathwayModel?
				if (source != null && !source.equals(""))
					comment.setSource(source);
				pathwayModel.getPathway().addComment(new Comment(source, content));
			}
		}
	}

	protected void readPathwayDynamicProperties(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element dp : root.getChildren("Property", root.getNamespace())) {
			String key = dp.getAttributeValue("key");
			String value = dp.getAttributeValue("value");
			pathwayModel.getPathway().setDynamicProperty(key, value);
		}
	}

	protected void readPathwayAnnotationRefs(PathwayModel pathwayModel, Element root) throws ConverterException {
		for (Element anntRef : root.getChildren("AnnotationRef", root.getNamespace())) {
			Annotation annotation = (Annotation) pathwayModel
					.getPathwayElement(anntRef.getAttributeValue("elementRef"));
			AnnotationRef annotationRef = new AnnotationRef(annotation);
			for (Element citRef : anntRef.getChildren("CitationRef", anntRef.getNamespace())) {
				Citation citationRef = (Citation) pathwayModel
						.getPathwayElement(citRef.getAttributeValue("elementRef"));
				if (citationRef != null)
					annotationRef.addCitationRef(citationRef);
			}
			for (Element evidRef : anntRef.getChildren("EvidenceRef", anntRef.getNamespace())) {
				Evidence evidenceRef = (Evidence) pathwayModel
						.getPathwayElement(evidRef.getAttributeValue("elementRef"));
				if (evidenceRef != null)
					annotationRef.addEvidenceRef(evidenceRef);
			}
			pathwayModel.getPathway().addAnnotationRef(annotationRef);
		}
	}

	protected void readPathwayCitationRefs(PathwayModel pathwayModel, Element e) throws ConverterException {
		for (Element citRef : e.getChildren("CitationRef", e.getNamespace())) {
			Citation citationRef = (Citation) pathwayModel.getPathwayElement(citRef.getAttributeValue("elementRef"));
			if (citationRef != null)
				pathwayModel.getPathway().addCitationRef(citationRef);
		}
	}

	protected void readPathwayEvidenceRefs(PathwayModel pathwayModel, Element e) throws ConverterException {
		for (Element evidRef : e.getChildren("EvidenceRef", e.getNamespace())) {
			Evidence evidenceRef = (Evidence) pathwayModel.getPathwayElement(evidRef.getAttributeValue("elementRef"));
			if (evidenceRef != null)
				pathwayModel.getPathway().addEvidenceRef(evidenceRef);
		}
	}

	protected void readGroups(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element grps = root.getChild("Groups", root.getNamespace());
		for (Element grp : grps.getChildren("Group", grps.getNamespace())) {
			String elementId = grp.getAttributeValue("elementId");
			GroupType type = GroupType.register(grp.getAttributeValue("type"));
			Element gfx = grp.getChild("Graphics", grp.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			Group group = new Group(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, type);
			/* read comment group, evidenceRefs */
			readElementInfo(group, grp);
			/* set optional properties */
			String textLabel = grp.getAttributeValue("textLabel");
			Xref xref = readXref(grp);
			if (xref != null)
				group.setXref(xref);
			if (textLabel != null)
				group.setTextLabel(textLabel);
			if (group != null)
				pathwayModel.addGroup(group);
		}
		/**
		 * Because a group may refer to another group not yet initialized. We read all
		 * group elements before setting groupRef.
		 */
		for (Element grp : grps.getChildren("Group", grps.getNamespace())) {
			String groupRef = grp.getAttributeValue("groupRef");
			if (groupRef != null && !groupRef.equals("")) {
				String elementId = grp.getAttributeValue("elementId");
				Group group = (Group) pathwayModel.getPathwayElement(elementId);
				group.setGroupRef((Group) group.getPathwayModel().getPathwayElement(groupRef));
			}
		}
	}

	protected void readLabels(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element lbs = root.getChild("Labels", root.getNamespace());
		for (Element lb : lbs.getChildren("Label", lbs.getNamespace())) {
			String elementId = lb.getAttributeValue("elementId");
			String textLabel = lb.getAttributeValue("textLabel");
			Element gfx = lb.getChild("Graphics", lb.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			Label label = new Label(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, textLabel);
			/* read comment group, evidenceRefs */
			readElementInfo(label, lb);
			/* set optional properties */
			String href = lb.getAttributeValue("href");
			String groupRef = lb.getAttributeValue("grouRef");
			if (href != null)
				label.setHref(href);
			if (groupRef != null && !groupRef.equals(""))
				label.setGroupRef((Group) label.getPathwayModel().getPathwayElement(groupRef));
			if (label != null)
				pathwayModel.addLabel(label);
		}
	}

	protected void readShapes(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element shps = root.getChild("Shapes", root.getNamespace());
		for (Element shp : shps.getChildren("Shape", shps.getNamespace())) {
			String elementId = shp.getAttributeValue("elementId");
			Element gfx = shp.getChild("Graphics", shp.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			double rotation = Double.parseDouble(gfx.getAttributeValue("rotation"));
			Shape shape = new Shape(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty, rotation);
			/* read comment group, evidenceRefs */
			readElementInfo(shape, shp);
			/* set optional properties */
			String textLabel = shp.getAttributeValue("textLabel");
			String groupRef = shp.getAttributeValue("grouRef");
			if (textLabel != null)
				shape.setTextLabel(textLabel);
			if (groupRef != null && !groupRef.equals(""))
				shape.setGroupRef((Group) shape.getPathwayModel().getPathwayElement(groupRef));
			if (shape != null)
				pathwayModel.addShape(shape);
		}
	}

	protected void readDataNodes(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element dns = root.getChild("DataNodes", root.getNamespace());
		for (Element dn : dns.getChildren("DataNode", dns.getNamespace())) {
			String elementId = dn.getAttributeValue("elementId");
			Element gfx = dn.getChild("Graphics", dn.getNamespace());
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			String textLabel = dn.getAttributeValue("textLabel");
			DataNodeType type = DataNodeType.register(dn.getAttributeValue("type"));
			Xref xref = readXref(dn);
			DataNode dataNode = new DataNode(elementId, pathwayModel, rectProperty, fontProperty, shapeStyleProperty,
					textLabel, type, xref);
			/* read comment group, evidenceRefs */
			readElementInfo(dataNode, dn);
			/* read states */
			readStates(dataNode, dn);
			/* set optional properties */
			String groupRef = dn.getAttributeValue("groupRef");
			String elementRef = dn.getAttributeValue("elementRef");
			if (groupRef != null && !groupRef.equals(""))
				dataNode.setGroupRef((Group) dataNode.getPathwayModel().getPathwayElement(groupRef));
			if (elementRef != null && !elementRef.equals(""))
				dataNode.setElementRef(dataNode.getPathwayModel().getPathwayElement(elementRef));
			if (dataNode != null)
				pathwayModel.addDataNode(dataNode);
		}
	}

	/**
	 * TODO should absolute x and y be optional?
	 */
	protected void readStates(DataNode dataNode, Element dn) throws ConverterException {
		Element sts = dn.getChild("States", dn.getNamespace());
		for (Element st : sts.getChildren("State", sts.getNamespace())) {
			String elementId = st.getAttributeValue("elementId");
			String textLabel = st.getAttributeValue("textLabel");
			StateType type = StateType.register(st.getAttributeValue("type"));
			Element gfx = st.getChild("Graphics", st.getNamespace());
			double relX = Double.parseDouble(gfx.getAttributeValue("relX"));
			double relY = Double.parseDouble(gfx.getAttributeValue("relY"));
			RectProperty rectProperty = readRectProperty(gfx);
			FontProperty fontProperty = readFontProperty(gfx);
			ShapeStyleProperty shapeStyleProperty = readShapeStyleProperty(gfx);
			State state = new State(elementId, dataNode.getPathwayModel(), dataNode, textLabel, type, relX, relY,
					rectProperty, fontProperty, shapeStyleProperty);
			/* read comment group, evidenceRefs */
			readElementInfo(dataNode, st);
			/* set optional properties */
			Xref xref = readXref(st);
			if (xref != null)
				state.setXref(xref);
			if (state != null)
				dataNode.addState(state);
		}
	}

	protected void readGraphicalLines(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element glns = root.getChild("GraphicaLines", root.getNamespace());
		for (Element gln : glns.getChildren("GraphicaLine", glns.getNamespace())) {
			String elementId = gln.getAttributeValue("elementId");
			Element gfx = gln.getChild("Graphics", gln.getNamespace());
			LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
			GraphicalLine graphicalLine = new GraphicalLine(elementId, pathwayModel, lineStyleProperty);
			/* read comment group, evidenceRefs */
			readElementInfo(graphicalLine, gln);
			/* read anchors (NB: points are read later) */
			readAnchors(graphicalLine, gln);
			/* set optional properties */
			String groupRef = gln.getAttributeValue("groupRef");
			if (groupRef != null && !groupRef.equals(""))
				graphicalLine.setGroupRef((Group) graphicalLine.getPathwayModel().getPathwayElement(groupRef));
			if (graphicalLine != null)
				pathwayModel.addGraphicalLine(graphicalLine);
		}
	}

	protected void readInteractions(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element ias = root.getChild("Interactions", root.getNamespace());
		for (Element ia : ias.getChildren("Interaction", ias.getNamespace())) {
			String elementId = ia.getAttributeValue("elementId");
			Element gfx = ia.getChild("Graphics", ia.getNamespace());
			LineStyleProperty lineStyleProperty = readLineStyleProperty(gfx);
			Xref xref = readXref(ia);
			Interaction interaction = new Interaction(elementId, pathwayModel, lineStyleProperty, xref);
			/* read comment group, evidenceRefs */
			readElementInfo(interaction, ia);
			/* read anchors (NB: points are read later) */
			readAnchors(interaction, ia);
			/* set optional properties */
			String groupRef = ia.getAttributeValue("groupRef");
			if (groupRef != null && !groupRef.equals(""))
				interaction.setGroupRef((Group) interaction.getPathwayModel().getPathwayElement(groupRef));
			if (interaction != null)
				pathwayModel.addInteraction(interaction);
		}
	}

	protected void readAnchors(LineElement lineElement, Element e) throws ConverterException {
		Element wyps = e.getChild("Waypoints", e.getNamespace());
		for (Element an : wyps.getChildren("Anchor", e.getNamespace())) {
			String elementId = an.getAttributeValue("elementId");
			double position = Double.parseDouble(an.getAttributeValue("position"));
			Coordinate xy = new Coordinate(Double.parseDouble(an.getAttributeValue("x")),
					Double.parseDouble(an.getAttributeValue("y")));
			AnchorType shapeType = AnchorType.register(an.getAttributeValue("shapeType"));
			Anchor anchor = new Anchor(elementId, lineElement.getPathwayModel(), position, xy, shapeType);
			if (anchor != null)
				lineElement.addAnchor(anchor);
		}
	}

	// TODO can cast to LinedElement? to reduce duplicate code
	protected void readLinePoints(PathwayModel pathwayModel, Element e) throws ConverterException {
		Element ias = e.getChild("Interactions", e.getNamespace());
		for (Element ia : ias.getChildren("Interaction", ias.getNamespace())) {
			String elementId = ia.getAttributeValue("elementId");
			Interaction interaction = (Interaction) pathwayModel.getPathwayElement(elementId);
			readPoints(interaction, e);
			// TODO check here
			if (interaction.getPoints().size() <= 2) {
				// TODO error!
			}
		}
		Element glns = e.getChild("GraphicaLines", e.getNamespace());
		for (Element gln : glns.getChildren("GraphicaLine", glns.getNamespace())) {
			String elementId = gln.getAttributeValue("elementId");
			GraphicalLine graphicalLine = (GraphicalLine) pathwayModel.getPathwayElement(elementId);
			readPoints(graphicalLine, e);
			// TODO check here
			if (graphicalLine.getPoints().size() <= 2) {
				// TODO error!
			}
		}
	}

	protected void readPoints(LineElement lineElement, Element e) throws ConverterException {
		Element wyps = e.getChild("Waypoints", e.getNamespace());
		for (Element pt : wyps.getChildren("Anchor", e.getNamespace())) {
			String elementId = pt.getAttributeValue("elementId");
			ArrowHeadType arrowHead = ArrowHeadType.register(pt.getAttributeValue("elementId"));
			Coordinate xy = new Coordinate(Double.parseDouble(pt.getAttributeValue("x")),
					Double.parseDouble(pt.getAttributeValue("y")));
			Point point = new Point(elementId, lineElement.getPathwayModel(), arrowHead, xy);
			/* set optional properties */
			String elementRef = pt.getAttributeValue("elementRef");
			double relX = Double.parseDouble(pt.getAttributeValue("relX"));
			double relY = Double.parseDouble(pt.getAttributeValue("relY"));
			if (elementRef != null && !elementRef.equals(""))
				point.setElementRef(point.getPathwayModel().getPathwayElement(elementRef));
			point.setRelX(relX);
			point.setRelY(relY);
			if (point != null)
				lineElement.addPoint(point);
		}
	}

	private void readElementInfo(ElementInfo elementInfo, Element e) throws ConverterException {
		readComments(elementInfo, e);
		readDynamicProperties(elementInfo, e);
		readAnnotationRefs(elementInfo, e);
		readCitationRefs(elementInfo, e);
		readEvidenceRefs(elementInfo, e);
	}

	protected void readComments(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element cmt : e.getChildren("Comment", e.getNamespace())) {
			String source = cmt.getAttributeValue("Source");
			String content = cmt.getText();
			if (content != null && !content.equals("")) {
				Comment comment = new Comment(content); // TODO needs parent pathwayModel?
				if (source != null && !source.equals(""))
					comment.setSource(source);
				elementInfo.addComment(new Comment(source, content));
			}
		}
	}

	protected void readDynamicProperties(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element dp : e.getChildren("Property", e.getNamespace())) {
			String key = dp.getAttributeValue("key");
			String value = dp.getAttributeValue("value");
			elementInfo.setDynamicProperty(key, value);
		}
	}

	protected void readAnnotationRefs(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element anntRef : e.getChildren("AnnotationRef", e.getNamespace())) {
			Annotation annotation = (Annotation) elementInfo.getPathwayModel()
					.getPathwayElement(anntRef.getAttributeValue("elementRef"));
			AnnotationRef annotationRef = new AnnotationRef(annotation);
			for (Element citRef : anntRef.getChildren("CitationRef", anntRef.getNamespace())) {
				Citation citationRef = (Citation) elementInfo.getPathwayModel()
						.getPathwayElement(citRef.getAttributeValue("elementRef"));
				if (citationRef != null)
					annotationRef.addCitationRef(citationRef);
			}
			for (Element evidRef : anntRef.getChildren("EvidenceRef", anntRef.getNamespace())) {
				Evidence evidenceRef = (Evidence) elementInfo.getPathwayModel()
						.getPathwayElement(evidRef.getAttributeValue("elementRef"));
				if (evidenceRef != null)
					annotationRef.addEvidenceRef(evidenceRef);
			}
			elementInfo.addAnnotationRef(annotationRef);
		}
	}

	protected void readCitationRefs(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element citRef : e.getChildren("CitationRef", e.getNamespace())) {
			Citation citationRef = (Citation) elementInfo.getPathwayModel()
					.getPathwayElement(citRef.getAttributeValue("elementRef"));
			if (citationRef != null) {
				elementInfo.addCitationRef(citationRef);
			}
		}
	}

	protected void readEvidenceRefs(ElementInfo elementInfo, Element e) throws ConverterException {
		for (Element evidRef : e.getChildren("EvidenceRef", e.getNamespace())) {
			Evidence evidenceRef = (Evidence) elementInfo.getPathwayModel()
					.getPathwayElement(evidRef.getAttributeValue("elementRef"));
			if (evidenceRef != null)
				elementInfo.addEvidenceRef(evidenceRef);
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
		ShapeStyleProperty shapeStyleProperty = new ShapeStyleProperty(borderColor, borderStyle, borderWidth, fillColor,
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
		return lineStyleProperty;
	}

	
	/*---------------------------------------------------------------------------*/
	protected void readGroupRefs(PathwayModel pathwayModel, Element root) {
		List<String> shpElements = Collections
				.unmodifiableList(Arrays.asList("DataNodes", "Labels", "Shapes", "Groups"));
		List<String> shpElement = Collections.unmodifiableList(Arrays.asList("DataNode", "Label", "Shape", "Group"));
		for (int i = 0; i < shpElements.size(); i++) {
			Element grps = root.getChild(shpElements.get(i), root.getNamespace());
			for (Element grp : grps.getChildren(shpElement.get(i), grps.getNamespace())) {
				String groupRef = grp.getAttributeValue("groupRef");
				if (groupRef != null && !groupRef.equals("")) {
					String elementId = grp.getAttributeValue("elementId");
					ShapedElement shapedElement = (ShapedElement) pathwayModel.getPathwayElement(elementId);
					shapedElement.setGroupRef((Group) pathwayModel.getPathwayElement(groupRef));
				}
			}
		}
		List<String> lnElements = Collections.unmodifiableList(Arrays.asList("Interactions", "GraphicalLines"));
		List<String> lnElement = Collections.unmodifiableList(Arrays.asList("Interaction", "GraphicalLine"));
		for (int i = 0; i < shpElements.size(); i++) {
			Element grps = root.getChild(lnElements.get(i), root.getNamespace());
			for (Element grp : grps.getChildren(lnElement.get(i), grps.getNamespace())) {
				String groupRef = grp.getAttributeValue("groupRef");
				if (groupRef != null && !groupRef.equals("")) {
					String elementId = grp.getAttributeValue("elementId");
					LineElement lineElement = (LineElement) pathwayModel.getPathwayElement(elementId);
					lineElement.setGroupRef((Group) pathwayModel.getPathwayElement(groupRef));
				}
			}
		}
	}

}
