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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.type.*;
import org.pathvisio.util.ColorUtils;

/**
 * This class writes a PathwayModel to an output (GPML 2013a).
 * 
 * @author finterly
 */
public class GPML2013aWriter extends GPML2013aFormatAbstract implements GpmlFormatWriter {

	public static final GPML2013aWriter GPML2013aWRITER = new GPML2013aWriter("GPML2013a.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2013a"));

	protected GPML2013aWriter(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

	/**
	 * Writes the JDOM {@link Document} document to the outputstream specified.
	 * 
	 * @param out      the outputstream to which the JDOM document should be written
	 * @param validate if true, validate the dom structure before writing. If there
	 *                 is a validation error, or the xsd is not in the classpath, an
	 *                 exception will be thrown.
	 * @throws ConverterException
	 */
	public void writeToXml(PathwayModel pathwayModel, OutputStream output, boolean validate) throws ConverterException {

		Document doc = createJdom(pathwayModel);

		if (validate)
			validateDocument(doc); // TODO Boolean validate not relevant to 2021...

		// Get the XML code
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		Format xmlformat = xmlOutput.getFormat();
		xmlformat.setEncoding("UTF-8");
		xmlformat.setTextMode(Format.TextMode.NORMALIZE);
		xmlOutput.setFormat(xmlformat);

		try {
			// Send XML code to the outputstream
			xmlOutput.output(doc, output); // new FileOutputStream(new File("fileName.gpml")
			// Create a new file and write XML to it
			System.out.println("Wrote pathway model to gpml file");
		} catch (IOException e) {
			throw new ConverterException(e);
		}

	}

	/**
	 * Writes the JDOM document to the file specified.
	 * 
	 * @param file     the file to which the JDOM document should be saved.
	 * @param validate if true, validate the dom structure before writing to file.
	 * @throws ConverterException
	 */
	public void writeToXml(PathwayModel pathwayModel, File file, boolean validate) throws ConverterException {
		OutputStream out;
		try {
			out = new FileOutputStream(file);
		} catch (IOException ex) {
			throw new ConverterException(ex);
		}
		writeToXml(pathwayModel, out, true);
	}

	/**
	 * Creates and returns the JDOM document {@link Document} written from given
	 * pathwayModel {@link PathwayModel} data.
	 * 
	 * @param pathwayModel the pathway model to be written.
	 * @throws ConverterException
	 */
	public Document createJdom(PathwayModel pathwayModel) throws ConverterException {

		/* Checks if pathway model interactions/graphicaLines and groups are valid */
		checkLineAndGroupSize(pathwayModel); // TODO

		Document doc = new Document();
		Element root = new Element("Pathway", getGpmlNamespace());
		doc.setRootElement(root);

		if (root != null) {
			writePathwayInfo(pathwayModel, root);

			writeDataNodes(pathwayModel.getDataNodes(), root);
			writeStates(pathwayModel.getDataNodes(), root);
			writeInteractions(pathwayModel.getInteractions(), root);
			writeGraphicalLines(pathwayModel.getGraphicalLines(), root);
			writeLabels(pathwayModel.getLabels(), root);
			writeShapes(pathwayModel.getShapes(), root);
			writeGroups(pathwayModel.getGroups(), root);

			writeInfoBox(pathwayModel.getPathway().getInfoBox(), root);
			writeLegend(pathwayModel.getPathway(), root);

			writeBiopax(pathwayModel, root);
		}
		return doc;
	}

	/**
	 * Checks whether interactions and graphicaLines have at least two points, and
	 * groups have at least two pathway element members.
	 * 
	 * @param pathwayModel the pathway model.
	 * @throws ConverterException
	 */
	protected void checkLineAndGroupSize(PathwayModel pathwayModel) throws ConverterException {
		for (Interaction interaction : pathwayModel.getInteractions()) {
			if (interaction.getPoints().size() < 2) {
				throw new ConverterException("Interaction " + interaction.getElementId() + " has "
						+ interaction.getPoints().size() + " point(s),  must have at least 2.");
			}
		}
		for (GraphicalLine graphicalLine : pathwayModel.getGraphicalLines()) {
			if (graphicalLine.getPoints().size() < 2) {
				throw new ConverterException("GraphicalLine " + graphicalLine.getElementId() + " has "
						+ graphicalLine.getPoints().size() + " point(s),  must have at least 2.");
			}
		}
		for (Group group : pathwayModel.getGroups()) {
			if (group.getPathwayElements().size() < 2) {
				throw new ConverterException("Group " + group.getElementId() + " has "
						+ group.getPathwayElements().size() + " pathway element(s) members,  must have at least 2");
			}
		}
	}

	/**
	 * Writes pathway object {@link Pathway} information to root element.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void writePathwayInfo(PathwayModel pathwayModel, Element root) throws ConverterException {
		Pathway pathway = pathwayModel.getPathway();
		setAttr("Pathway", "Name", root, pathway.getTitle());
		/* set optional properties, in the order written in gpml 2013a */
		String source = pathway.getSource();
		String version = pathway.getVersion();
		String author = writeAuthorEmail(pathwayModel)[0];
		String maintainer = pathway.getDynamicProperty(PATHWAY_MAINTAINER);
		String email = writeAuthorEmail(pathwayModel)[1];
		String lastModified = pathway.getDynamicProperty(PATHWAY_LASTMODIFIED);
		String organism = pathway.getOrganism();
		String license = pathway.getLicense();
		if (source != null)
			setAttr("Pathway", "Data-Source", root, source);
		if (version != null)
			setAttr("Pathway", "Version", root, version);
		if (author != null && !author.equals(""))
			setAttr("Pathway", "Author", root, author);
		if (maintainer != null)
			setAttr("Pathway", "Maintainer", root, maintainer);
		if (email != null && !email.equals(""))
			setAttr("Pathway", "Email", root, email);
		if (lastModified != null)
			setAttr("Pathway", "Last-Modified", root, lastModified);
		if (organism != null)
			setAttr("Pathway", "Organism", root, organism);
		if (license != null)
			setAttr("Pathway", "License", root, license);
		/* set comment group */
		writeComments(pathway.getComments(), root);
		// TODO PublicationXref?
		writeBiopaxRefs(pathway.getCitationRefs(), root);
		writePathwayDynamicProperties(pathway.getDynamicProperties(), root);

		/* set graphics */
		Element gfx = new Element("Graphics", root.getNamespace());
		root.addContent(gfx);
		setAttr("Pathway.Graphics", "BoardWidth", gfx, String.valueOf(pathway.getBoardWidth()));
		setAttr("Pathway.Graphics", "BoardHeight", gfx, String.valueOf(pathway.getBoardHeight()));
//		result.put("Pathway@BiopaxRef", new AttributeInfo ("xsd:string", null, "optional"));

	}

	/**
	 * Combines information...
	 * 
	 * @param pathwayModel
	 * @return
	 * @throws ConverterException
	 */
	protected String[] writeAuthorEmail(PathwayModel pathwayModel) throws ConverterException {
		List<String> authorList = new ArrayList<String>();
		List<String> emailList = new ArrayList<String>();
		String authorStr = pathwayModel.getPathway().getDynamicProperty(PATHWAY_AUTHOR);
		String emailStr = pathwayModel.getPathway().getDynamicProperty(PATHWAY_EMAIL);
		if (authorStr != null)
			authorList.add(authorStr);
		if (emailStr != null)
			emailList.add(emailStr);
		List<Author> authors = pathwayModel.getAuthors();
		if (!authors.isEmpty()) {
			for (Author author : authors) {
				if (author == null)
					continue;
				String name = author.getName();
				String fullName = author.getFullName();
				if (fullName != null)
					name = name + "(" + fullName + ")";
				if (name != null)
					authorList.add(name);
				String email = author.getEmail();
				if (email != null)
					emailList.add(email);
			}
		}
		String authorListStr = StringUtils.join(authorList, ", ");
		String emailListStr = StringUtils.join(emailList, ", ");
		return new String[] { authorListStr, emailListStr };
	}

	/**
	 * Writes xref {@link Xref} information to new element. Xref is required for
	 * DataNodes, Interactions, Citations and Evidences. Xref is optional for the
	 * Pathway, States, Groups, and Annotations.
	 * 
	 * @param xref     the xref of the pathway or pathway element.
	 * @param e        the parent element.
	 * @param required if true, xref is a required property.
	 */
	protected void writeXref(Xref xref, Element e, boolean required) throws ConverterException { // TODO boolean
																									// required
		if (xref == null && required) {
			Element xrf = new Element("Xref", e.getNamespace());
			xrf.setAttribute("ID", "");
			xrf.setAttribute("Database", ""); // TODO null handling?
			e.addContent(xrf);
		}
		if (xref != null) {
			String identifier = xref.getId();
			DataSource dataSrc = xref.getDataSource();
			if (dataSrc != null && identifier != null || required) {
				Element xrf = new Element("Xref", e.getNamespace());
				String dataSource = xref.getDataSource().getFullName(); // TODO dataSource
				String base = e.getName();
				setAttr(base + ".Xref", "Database", xrf, dataSource == null ? "" : dataSource);
				setAttr(base + ".Xref", "ID", xrf, identifier == null ? "" : identifier);
				e.addContent(xrf);
			}
		}
	}

	/**
	 * Writes comments {@link Comment} information for pathway or pathway element.
	 * 
	 * @param comments the list of comments of pathway or pathway element.
	 * @param e        the parent element.
	 * @throws ConverterException
	 */
	protected void writeComments(List<Comment> comments, Element e) throws ConverterException {
		for (Comment comment : comments) {
			Element cmt = new Element("Comment", e.getNamespace());
			if (comment.getCommentText() != null) // TODO may be excessive
				cmt.setText(comment.getCommentText());
			if (comment.getSource() != null)
				cmt.setAttribute("Source", comment.getSource());
			if (cmt != null)
				e.addContent(cmt);
		}
	}

	/**
	 * Writes dynamic property information for pathway or pathway element.
	 * {@link Pathway#getDynamicProperty() , ElementInfo#getDynamicProperty()}
	 * 
	 * @param dynamicProperties the list of dynamic properties.
	 * @param e                 the parent element.
	 * @throws ConverterException
	 */
	protected void writePathwayDynamicProperties(Map<String, String> dynamicProperties, Element e)
			throws ConverterException {
		for (String key : dynamicProperties.keySet()) {
			Element dp = new Element("Attribute", e.getNamespace());
			setAttr("Attribute", "Key", dp, key);
			setAttr("Attribute", "Value", dp, dynamicProperties.get(key));
			// TODO may need to handle BiopaxRef attribute
			if (dp != null)
				e.addContent(dp);
		}
	}

	/**
	 * Writes BiopaxRef information from {@link ElementInfo#getCitationRef()} for
	 * pathway or pathway element.
	 * 
	 * @param citationRefs
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeBiopaxRefs(List<Citation> citationRefs, Element e) throws ConverterException {
		if (e != null) {
			for (Citation citationRef : citationRefs) {
				Element bpRef = new Element("BiopaxRef", e.getNamespace());
				bpRef.setText(citationRef.getElementId());
				if (bpRef != null)
					e.addContent(bpRef);
			}
		}
	}

	/**
	 * Writes the infobox x and y coordinate {@link Pathway#getInfoBox()}
	 * information.
	 * 
	 * @param infoBox the infobox xy coordinates.
	 * @param root    the root element.
	 */
	protected void writeInfoBox(Coordinate infoBox, Element root) {
		Element ifb = new Element("InfoBox", root.getNamespace());
		ifb.setAttribute("CenterX", Double.toString(infoBox.getX()));
		ifb.setAttribute("CenterY", Double.toString(infoBox.getY()));
		root.addContent(ifb);
	}

	/**
	 * Writes the legend x and y coordinate information.
	 * 
	 * @param pathway the pathway.
	 * @param root    the root element.
	 */
	protected void writeLegend(Pathway pathway, Element root) {
		String centerX = pathway.getDynamicProperty(LEGEND_CENTER_X);
		String centerY = pathway.getDynamicProperty(LEGEND_CENTER_Y);
		if (centerX != null && centerY != null) {
			Element lgd = new Element("Legend", root.getNamespace());
			lgd.setAttribute("CenterX", centerX);
			lgd.setAttribute("CenterY", centerY);
			root.addContent(lgd);
		}
	}

	/**
	 * Writes datanode {@link DataNode} information.
	 * 
	 * @param dataNodes the list of datanodes.
	 * @param root      the root element.
	 * @throws ConverterException
	 */
	protected void writeDataNodes(List<DataNode> dataNodes, Element root) throws ConverterException {
		if (!dataNodes.isEmpty()) {
			for (DataNode dataNode : dataNodes) {
				if (dataNode == null)
					continue;
				Element dn = new Element("DataNode", root.getNamespace());
				setAttr("DataNode", "TextLabel", dn, dataNode.getTextLabel());
				writeShapedElement(dataNode, dn);
				setAttr("DataNode", "Type", dn, dataNode.getType().getName());
				writeGroupRef(dataNode.getGroupRef(), dn); // TODO location
				writeXref(dataNode.getXref(), dn, true);
				if (dn != null)
					root.addContent(dn);
			}
		}
	}

	/**
	 * Writes state {@link State} information.
	 * 
	 * @param dataNode the parent dataNode.
	 * @param root     the root element.
	 * @throws ConverterException
	 */
	protected void writeStates(List<DataNode> dataNodes, Element root) throws ConverterException {
		for (DataNode dataNode : dataNodes) {
			List<State> states = dataNode.getStates();
			for (State state : states) {
				if (state == null)
					continue;
				Element st = new Element("State", root.getNamespace());
				setAttr("State", "StateType", st, state.getType().getName()); // TODO wasn't actually used
				setAttr("State", "GraphRef", st, dataNode.getElementId());
				setAttr("State", "TextLabel", st, state.getTextLabel() == null ? "" : state.getTextLabel());
				writeElementInfo(state, st);
				writeShapedOrStateDynamicProperties(state.getDynamicProperties(), state.getShapeStyleProperty(), st);
				/* sets graphics properties */
				Element gfx = new Element("Graphics", st.getNamespace());
				st.addContent(gfx);
				setAttr("State.Graphics", "RelX", gfx, Double.toString(state.getRelX()));
				setAttr("State.Graphics", "RelY", gfx, Double.toString(state.getRelY()));
				setAttr("State.Graphics", "Width", gfx, Double.toString(state.getWidth()));
				setAttr("State.Graphics", "Height", gfx, Double.toString(state.getHeight()));
				/* state does not have font properties in GPML2013a */
				writeShapeStyleProperty(state.getShapeStyleProperty(), gfx);
				/* optionally writes xref */
				writeXref(state.getXref(), st, false);
				if (st != null) {
					root.addContent(st);
				}
			}
		}
	}

	/**
	 * Writes interaction {@link Interaction} information.
	 * 
	 * @param interactions the list of interactions.
	 * @param root         the root element;
	 * @throws ConverterException
	 */
	protected void writeInteractions(List<Interaction> interactions, Element root) throws ConverterException {
		for (Interaction interaction : interactions) {
			if (interaction == null)
				continue;
			Element ia = new Element("Interaction", root.getNamespace());
			writeLineElement(interaction, ia);
			writeXref(interaction.getXref(), ia, true);
			if (ia != null) {
				root.addContent(ia);
			}
		}
	}

	/**
	 * Writes graphical line {@link GraphicalLine} information.
	 * 
	 * @param graphicalLines the list of graphical lines.
	 * @param root           the root element.
	 * @throws ConverterException
	 */
	protected void writeGraphicalLines(List<GraphicalLine> graphicalLines, Element root) throws ConverterException {
		for (GraphicalLine graphicalLine : graphicalLines) {
			if (graphicalLine == null)
				continue;
			Element gln = new Element("GraphicalLine", root.getNamespace());
			writeLineElement(graphicalLine, gln);
			if (gln != null) {
				root.addContent(gln);
			}
		}
	}

	/**
	 * Writes line element {@link LineElement} information for interactions or
	 * graphicalLines.
	 * 
	 * @param lineElement the interaction or graphicalLine.
	 * @param ln          the line element.
	 * @throws ConverterException
	 */
	protected void writeLineElement(LineElement lineElement, Element ln) throws ConverterException {
		writeElementInfo(lineElement, ln);
		writeLineDynamicProperties(lineElement.getDynamicProperties(), lineElement.getLineStyleProperty(), ln);
		Element gfx = new Element("Graphics", ln.getNamespace());
		ln.addContent(gfx);
		writeLineStyleProperty(lineElement.getLineStyleProperty(), gfx);
		writePoints(lineElement.getPoints(), gfx);
		writeAnchors(lineElement.getAnchors(), gfx);
		writeGroupRef(lineElement.getGroupRef(), ln); // TODO location
	}

	/**
	 * Writes point {@link Point} information.
	 * 
	 * 
	 * TODO elementId, elementRef, arrowHead, x, y, relX, relY (Order)
	 * 
	 * @param points the list of points.
	 * @param wyps   the parent element.
	 * @throws ConverterException
	 */
	protected void writePoints(List<Point> points, Element gfx) throws ConverterException {
		List<Element> ptList = new ArrayList<Element>();
		for (Point point : points) {
			if (point == null)
				continue;
			Element pt = new Element("Point", gfx.getNamespace());
			writeElementId(point.getElementId(), pt); // TODO optional....
			String base = ((Element) gfx.getParent()).getName();
			setAttr(base + ".Graphics.Point", "X", pt, Double.toString(point.getXY().getX()));
			setAttr(base + ".Graphics.Point", "Y", pt, Double.toString(point.getXY().getY()));
			if (writePointElementRef(point.getElementRef(), pt)) {
				setAttr(base + ".Graphics.Point", "RelX", pt, Double.toString(point.getRelX()));
				setAttr(base + ".Graphics.Point", "RelY", pt, Double.toString(point.getRelY()));
			}
			setAttr(base + ".Graphics.Point", "ArrowHead", pt, point.getArrowHead().getName());
			if (pt != null)
				ptList.add(pt);
		}
		if (ptList != null && ptList.isEmpty() == false)
			gfx.addContent(ptList);
	}

	/**
	 * Writes anchor {@link Anchor} information. TODO elementId, x, y, position,
	 * shapeType (Order)
	 * 
	 * @param anchors the list of anchors.
	 * @param wyps    the parent element.
	 * @throws ConverterException
	 */
	protected void writeAnchors(List<Anchor> anchors, Element gfx) throws ConverterException {
		if (!anchors.isEmpty()) {
			List<Element> anList = new ArrayList<Element>();
			for (Anchor anchor : anchors) {
				if (anchor == null)
					continue;
				Element an = new Element("Anchor", gfx.getNamespace());
				writeElementId(anchor.getElementId(), an);
				String base = ((Element) gfx.getParent()).getName();
				setAttr(base + ".Graphics.Anchor", "Position", an, Double.toString(anchor.getPosition()));
				setAttr(base + ".Graphics.Anchor", "Shape", an, anchor.getShapeType().getName());
				if (an != null) {
					anList.add(an);
				}
			}
			if (anList != null && anList.isEmpty() == false) {
				gfx.addContent(anList);
			}
		}
	}

	/**
	 * Writes label {@link Label} information.
	 * 
	 * @param labels the list of labels.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeLabels(List<Label> labels, Element root) throws ConverterException {
		for (Label label : labels) {
			if (label == null)
				continue;
			Element lb = new Element("Label", root.getNamespace());
			setAttr("Label", "TextLabel", lb, label.getTextLabel());
			writeShapedElement(label, lb);
			if (label.getHref() != null)
				setAttr("Label", "Href", lb, label.getHref());
			writeGroupRef(label.getGroupRef(), lb); // TODO location
			if (lb != null) {
				root.addContent(lb);
			}
		}
	}

	/**
	 * Writes shape {@link Shape} information.
	 * 
	 * @param shapes the list of shapes.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeShapes(List<Shape> shapes, Element root) throws ConverterException {
		for (Shape shape : shapes) {
			if (shape == null)
				continue;
			Element shp = new Element("Shape", root.getNamespace());
			if (shape.getTextLabel() != null)
				setAttr("Shape", "TextLabel", shp, shape.getTextLabel());
			writeShapedElement(shape, shp);
			writeGroupRef(shape.getGroupRef(), shp); // TODO location
			Element gfx = shp.getChild("Graphics", shp.getNamespace());
			setAttr("Shape.Graphics", "Rotation", gfx, Double.toString(shape.getRotation()));
			if (shp != null) {
				root.addContent(shp);
			}
		}
	}

	/**
	 * Writes group {@link Group} information.
	 * 
	 * @param groups the list of groups.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeGroups(List<Group> groups, Element root) throws ConverterException {
		for (Group group : groups) {
			if (group == null)
				continue;
			Element grp = new Element("Group", root.getNamespace());
			writeXref(group.getXref(), grp, false);
			writeElementInfo(group, grp);
			writeShapedOrStateDynamicProperties(group.getDynamicProperties(), group.getShapeStyleProperty(), grp);
			setAttr("Group", "GroupId", grp, group.getElementId());
			setAttr("Group", "GraphId", grp, group.getDynamicProperty(GROUP_GRAPHID)); // TODO check if ok
			setAttr("Group", "Style", grp, group.getType().getName());
			if (group.getTextLabel() != null)
				setAttr("Group", "TextLabel", grp, group.getTextLabel());
			writeGroupRef(group.getGroupRef(), grp); // TODO location
			if (grp != null) {
				root.addContent(grp);
			}
		}
	}

	protected void writeBiopax(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element bp = new Element("Biopax", root.getNamespace());
		writeBiopaxOpenControlledVocabulary(pathwayModel.getAnnotations(), bp);
		writeBiopaxPublicationXref(pathwayModel.getCitations(), bp);
		if (!bp.getChildren().isEmpty()) {
			root.addContent(bp);
		}
	}

	/**
	 * Writes gpml:Biopax bp:OpenControlledVocabulary {@link Annotation}
	 * information.
	 * 
	 * @param annotations the list of annotations.
	 * @param root        the root element.
	 * @throws ConverterException
	 */
	protected void writeBiopaxOpenControlledVocabulary(List<Annotation> annotations, Element bp)
			throws ConverterException {
		for (Annotation annotation : annotations) {
			if (annotation == null)
				continue;
			Element ocv = new Element("openControlledVocabulary", GpmlFormat.BIOPAX);
			Element term = new Element("TERM", GpmlFormat.BIOPAX);
			Element id = new Element("ID", GpmlFormat.BIOPAX);
			Element onto = new Element("Ontology", GpmlFormat.BIOPAX);
			term.setText(annotation.getValue());
			id.setText(annotation.getXref().getId());
			onto.setText(annotation.getType().getName());
			ocv.addContent(term); // TODO more concise method?
			ocv.addContent(id);
			ocv.addContent(onto);
			if (ocv != null) {
				bp.addContent(ocv);
			}
		}
	}

	/**
	 * Writes gpml:Biopax bp:PublicationXref {@link Citation} information.
	 * 
	 * @param citations the list of citations.
	 * @param root      the root element.
	 * @throws ConverterException
	 */
	protected void writeBiopaxPublicationXref(List<Citation> citations, Element bp) throws ConverterException {
		for (Citation citation : citations) {
			if (citation == null)
				continue;
			Element pubxf = new Element("PublicationXref", GpmlFormat.BIOPAX);
			pubxf.setAttribute("id", citation.getElementId(), GpmlFormat.RDF);
			// TODO add empty or drop?
			String biopaxId = citation.getXref().getId();
			String biopaxDatabase = citation.getXref().getDataSource().getFullName();
			String title = citation.getTitle();
			String source = citation.getSource();
			String year = citation.getYear();
			List<String> authors = citation.getAuthors();
			writePubxfInfo(biopaxId, "ID", pubxf);
			writePubxfInfo(biopaxDatabase, "DB", pubxf);
			writePubxfInfo(title, "TITLE", pubxf);
			writePubxfInfo(source, "SOURCE", pubxf);
			writePubxfInfo(year, "YEAR", pubxf);
			if (authors != null && !authors.isEmpty()) {
				for (String author : authors)
					writePubxfInfo(author, "AUTHOR", pubxf);
			}
			if (pubxf != null)
				bp.addContent(pubxf);
		}
	}

	/**
	 * Writes Biopax PublicationXref information to PublicationXref element. NB: The
	 * main purpose of this method is to make {@link #writeBiopaxPublicationXref}
	 * more concise.
	 * 
	 * @param propertyValue the value of the property.
	 * @param elementName   the name for new child element of pubxf element.
	 * @param pubxf         the PublicationXref element.
	 * @throws ConverterException
	 */
	protected void writePubxfInfo(String propertyValue, String elementName, Element pubxf) throws ConverterException {
		if (propertyValue != null && !propertyValue.equals("")) {
			Element e = new Element(elementName, GpmlFormat.BIOPAX);
			e.setText(propertyValue);
			pubxf.addContent(e);
		}
	}

	/**
	 * Writes elementId {@link PathwayElement} property information.
	 * 
	 * @param elementId the elementId.
	 * @param e         the parent element.
	 */
	protected void writeElementId(String elementId, Element e) {
		if (elementId != null && !elementId.equals(""))
			e.setAttribute("GraphId", elementId);
	}

	/**
	 * Writes groupRef property information. {@link Group} stores GroupId as its
	 * elementId.
	 * 
	 * @param groupRef the groupRef.
	 * @param e        the parent element.
	 */
	protected void writeGroupRef(Group grpRf, Element e) {
		if (grpRf != null) {
			String groupRef = grpRf.getElementId();
			if (groupRef != null && !groupRef.equals(""))
				e.setAttribute("GroupRef", groupRef);
		}
	}

	/**
	 * Writes point elementRef property information as GraphRef. When {@link Point}
	 * refers to {@link Group}, GraphRef refers to the group GraphId rather than
	 * GroupId. This method is used only by {@link #writePoints}
	 * 
	 * @param elementRef the pathway element point refers to. .
	 * @param pt         the jdom point element.
	 * @return true if elementRef exists and is successfully written.
	 */
	protected boolean writePointElementRef(PathwayElement elementRef, Element pt) {
		System.out.println("WRITE " + elementRef);
		if (elementRef != null && elementRef.getClass() == Group.class) {
			String elementRefStr = ((Group) elementRef).getDynamicProperty(GROUP_GRAPHID);
			System.out.println(elementRefStr);
			if (elementRefStr != null && !elementRefStr.equals(""))
				pt.setAttribute("GraphRef", elementRefStr);
			return true;
		}
		if (elementRef != null) {
			String elementRefStr = elementRef.getElementId();
			System.out.println(elementRefStr);
			if (elementRefStr != null && !elementRefStr.equals(""))
				pt.setAttribute("GraphRef", elementRefStr);
			return true;
		}
		return false;
	}

	/**
	 * Writes shapedElement {@link ShapedElement} information for datanodes, labels,
	 * shapes, or groups.
	 * 
	 * @param shapedElement the datanode, label, shape, or group.
	 * @param e             the shape element.
	 * @throws ConverterException
	 */
	private void writeShapedElement(ShapedElement shapedElement, Element se) throws ConverterException {
		writeElementInfo(shapedElement, se);
		writeShapedOrStateDynamicProperties(shapedElement.getDynamicProperties(), shapedElement.getShapeStyleProperty(),
				se);
		Element gfx = new Element("Graphics", se.getNamespace());
		se.addContent(gfx);
		writeRectProperty(shapedElement.getRectProperty(), gfx);
		writeFontProperty(shapedElement.getFontProperty(), gfx);
		writeShapeStyleProperty(shapedElement.getShapeStyleProperty(), gfx);
	}

	/**
	 * Writes elementId, comment group {comment, dynamic property, annotationRef,
	 * citationRef) and evidenceRef {@link ElementInfo} information for datanodes,
	 * interactions, graphicalLines, labels, shapes, and group.
	 * 
	 * NB: writing of dynamic properties (gpml:Attribute) requires special handling
	 * of DoubleLineProperty and CellularComponentProperty for shaped, state, or
	 * line pathway elements.
	 * 
	 * @param elementInfo the pathway element.
	 * @param e           the parent element.
	 * @throws ConverterException
	 */
	private void writeElementInfo(ElementInfo elementInfo, Element e) throws ConverterException {
		if (elementInfo.getClass() != Group.class)
			writeElementId(elementInfo.getElementId(), e);
		writeComments(elementInfo.getComments(), e);
		// TODO PublicationXref
		writeBiopaxRefs(elementInfo.getCitationRefs(), e);
	}

	/**
	 * @param dynamicProperties
	 * @param shapeProp
	 * @param se
	 * @throws ConverterException
	 */
	protected void writeShapedOrStateDynamicProperties(Map<String, String> dynamicProperties,
			ShapeStyleProperty shapeProp, Element se) throws ConverterException {
		for (String key : dynamicProperties.keySet()) {
			Element dp = new Element("Attribute", se.getNamespace());
			setAttr("Attribute", "Key", dp, key);
			setAttr("Attribute", "Value", dp, dynamicProperties.get(key));
			// TODO may need to handle BiopaxRef attribute
			if (dp != null)
				se.addContent(dp);
		}
		LineStyleType borderStyle = shapeProp.getBorderStyle();
		System.out.println("BORDERSTYLETYLE" + borderStyle);
		if (borderStyle.getName().equalsIgnoreCase("Double")) {
			/* double lineStyle is stored in dynamic property in GPML2013a */
			Element dp = new Element("Attribute", se.getNamespace());
			setAttr("Attribute", "Key", dp, DOUBLE_LINE_KEY);
			setAttr("Attribute", "Value", dp, "Double");
			if (dp != null)
				se.addContent(dp);
		}
		ShapeType shapeType = shapeProp.getShapeType();
		if (ShapeType.CELL_CMPNT_MAP.containsKey(shapeType)) {
			/* if shape in cellular component map, store info in dynamic property */
			Element dp = new Element("Attribute", se.getNamespace());
			setAttr("Attribute", "Key", dp, CELL_CMPNT_KEY);
			setAttr("Attribute", "Value", dp, shapeType.getName());
			if (dp != null)
				se.addContent(dp);

		}

	}

	/**
	 * @param dynamicProperties
	 * @param lineProp
	 * @param ln
	 * @throws ConverterException
	 */
	protected void writeLineDynamicProperties(Map<String, String> dynamicProperties, LineStyleProperty lineProp,
			Element ln) throws ConverterException {
		for (String key : dynamicProperties.keySet()) {
			Element dp = new Element("Attribute", ln.getNamespace());
			setAttr("Attribute", "Key", dp, key);
			setAttr("Attribute", "Value", dp, dynamicProperties.get(key));
			// TODO may need to handle BiopaxRef attribute
			if (dp != null)
				ln.addContent(dp);
		}
		LineStyleType lineStyle = lineProp.getLineStyle();
		System.out.println("LINESTYLE" + lineStyle);
		if (lineStyle.getName().equalsIgnoreCase("Double")) {
			/* double lineStyle is stored in dynamic property in GPML2013a */
			Element dp = new Element("Attribute", ln.getNamespace());
			setAttr("Attribute", "Key", dp, DOUBLE_LINE_KEY);
			setAttr("Attribute", "Value", dp, "Double");
			if (dp != null)
				ln.addContent(dp);
		}
	}

	/**
	 * Writes rect property {@link RectProperty} information.
	 * 
	 * @param rectProp the rectproperties.
	 * @param gfx      the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeRectProperty(RectProperty rectProp, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		setAttr(base + ".Graphics", "CenterX", gfx, Double.toString(rectProp.getCenterXY().getX()));
		setAttr(base + ".Graphics", "CenterY", gfx, Double.toString(rectProp.getCenterXY().getY()));
		setAttr(base + ".Graphics", "Width", gfx, Double.toString(rectProp.getWidth()));
		setAttr(base + ".Graphics", "Height", gfx, Double.toString(rectProp.getHeight()));
	}

	/**
	 * Writes font property {@link FontProperty} information.
	 * 
	 * NB: TODO Color is set by textColor
	 * 
	 * @param fontProp the font properties.
	 * @param gfx      the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeFontProperty(FontProperty fontProp, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		setAttr(base + ".Graphics", "Color", gfx, ColorUtils.colorToHex(fontProp.getTextColor(), false));
		setAttr(base + ".Graphics", "FontName", gfx, fontProp.getFontName() == null ? "" : fontProp.getFontName());
		setAttr(base + ".Graphics", "FontWeight", gfx, fontProp.getFontWeight() ? "Bold" : "Normal");
		setAttr(base + ".Graphics", "FontStyle", gfx, fontProp.getFontStyle() ? "Italic" : "Normal");
		setAttr(base + ".Graphics", "FontDecoration", gfx, fontProp.getFontDecoration() ? "Underline" : "Normal");
		setAttr(base + ".Graphics", "FontStrikethru", gfx, fontProp.getFontStrikethru() ? "Strikethru" : "Normal");
		setAttr(base + ".Graphics", "FontSize", gfx, Integer.toString((int) fontProp.getFontSize()));
		setAttr(base + ".Graphics", "Align", gfx, fontProp.getHAlign().getName());
		setAttr(base + ".Graphics", "Valign", gfx, fontProp.getVAlign().getName());
	}

	/**
	 * Writes shape style property {@link ShapeStyleProperty} information.
	 * 
	 * TODO NB: borderColor is not set, Color is set solely by textColor.
	 * 
	 * @param shapeProp the shape style properties.
	 * @param gfx       the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeShapeStyleProperty(ShapeStyleProperty shapeProp, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		/* do not set borderColor, Color is set by textColor */
		LineStyleType borderStyle = shapeProp.getBorderStyle();
		if (borderStyle != null && !borderStyle.getName().equalsIgnoreCase("Double"))
			setAttr(base + ".Graphics", "LineStyle", gfx, borderStyle.getName());
		setAttr(base + ".Graphics", "LineThickness", gfx, String.valueOf(shapeProp.getBorderWidth()));
		setAttr(base + ".Graphics", "FillColor", gfx, ColorUtils.colorToHex(shapeProp.getFillColor(), false));
		ShapeType shapeType = shapeProp.getShapeType();
		if (ShapeType.CELL_CMPNT_MAP.containsKey(shapeType)) {
			ShapeType shapeTypeNew = ShapeType.CELL_CMPNT_MAP.get(shapeType);
			setAttr(base + ".Graphics", "ShapeType", gfx, shapeTypeNew.getName());
		} else {
			setAttr(base + ".Graphics", "ShapeType", gfx, shapeType.getName());

		}
		setAttr(base + ".Graphics", "ZOrder", gfx, String.valueOf(shapeProp.getZOrder()));
	}

	/**
	 * Writes line style property {@link LineStyleProperty} information.
	 * 
	 * @param lineProp the line style properties.
	 * @param gfx      the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeLineStyleProperty(LineStyleProperty lineProp, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		setAttr(base + ".Graphics", "ConnectorType", gfx, lineProp.getConnectorType().getName());
		setAttr(base + ".Graphics", "ZOrder", gfx, String.valueOf(lineProp.getZOrder()));
		LineStyleType lineStyle = lineProp.getLineStyle();
		if (lineStyle != null && !lineStyle.getName().equalsIgnoreCase("Double"))
			setAttr(base + ".Graphics", "LineStyle", gfx, lineStyle.getName());
		setAttr(base + ".Graphics", "LineThickness", gfx, String.valueOf(lineProp.getLineWidth()));
		setAttr(base + ".Graphics", "Color", gfx, ColorUtils.colorToHex(lineProp.getLineColor(), false));
	}

}
