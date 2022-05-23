/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.libgpml.model;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.io.ConverterException;
import org.pathvisio.libgpml.model.DataNode.State;
import org.pathvisio.libgpml.model.GraphLink.LinkableTo;
import org.pathvisio.libgpml.model.LineElement.Anchor;
import org.pathvisio.libgpml.model.LineElement.LinePoint;
import org.pathvisio.libgpml.model.PathwayElement.AnnotationRef;
import org.pathvisio.libgpml.model.PathwayElement.CitationRef;
import org.pathvisio.libgpml.model.PathwayElement.Comment;
import org.pathvisio.libgpml.model.shape.IShape;
import org.pathvisio.libgpml.model.type.ArrowHeadType;
import org.pathvisio.libgpml.model.type.LineStyleType;
import org.pathvisio.libgpml.util.ColorUtils;
import org.pathvisio.libgpml.util.XrefUtils;

/**
 * This class writes a PathwayModel to an output (GPML 2013a).
 * <p>
 * NB:
 * <ol>
 * <li>If writing (converting) GPML2021 to GPML2013a, warning messages are
 * thrown.
 * <li>In the GUI, export allows writing to the GPML2013a format.
 * </ol>
 *
 * @author finterly
 */
public class Gpml2013aWriter extends Gpml2013aFormatAbstract implements GpmlFormatWriter {

	public static final Gpml2013aWriter GPML2013aWRITER = new Gpml2013aWriter("GPML2013a.xsd",
			Namespace.getNamespace("http://pathvisio.org/GPML/2013a"));

	/**
	 * Constructor for GPML2013aWriter.
	 *
	 * @param xsdFile the GPML schema file.
	 * @param nsGPML  the GPML namespace.
	 */
	protected Gpml2013aWriter(String xsdFile, Namespace nsGPML) {
		super(xsdFile, nsGPML);
	}

	/**
	 * Writes the JDOM {@link Document} document to the outputstream specified.
	 *
	 * @param pathwayModel the pathway model.
	 * @param output       the outputstream to which the JDOM document should be
	 *                     written
	 * @param validate     if true, validate the dom structure before writing. If
	 *                     there is a validation error, or the xsd is not in the
	 *                     classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	@Override
	public void writeToXml(PathwayModel pathwayModel, OutputStream output, boolean validate) throws ConverterException {

		Document doc = createJdom(pathwayModel);

		if (validate) {
			validateDocument(doc);
		}
		// Get the XML code
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		Format xmlformat = xmlOutput.getFormat();
		xmlformat.setEncoding("UTF-8");
//		xmlformat.setTextMode(Format.TextMode.NORMALIZE); // for now, use default to preserve spaces TODO 
		xmlOutput.setFormat(xmlformat);

		try {
			// Send XML code to the outputstream
			xmlOutput.output(doc, output); // new FileOutputStream(new File("fileName.gpml")
			// Create a new file and write XML to it
			Logger.log.trace("Wrote pathway model successfully to gpml file");
		} catch (IOException e) {
			throw new ConverterException(e);
		}

	}

	/**
	 * Writes the JDOM document to the file specified.
	 *
	 * @param pathwayModel the pathway model.
	 * @param file         the file to which the JDOM document should be saved.
	 * @param validate     if true, validate the dom structure before writing to
	 *                     file.
	 * @throws ConverterException
	 */
	@Override
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
	@Override
	public Document createJdom(PathwayModel pathwayModel) throws ConverterException {
		// removes empty groups and updates group dimensions
		updateGroups(pathwayModel);

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
			writeInfoBox(pathwayModel.getPathway(), root);
			writeLegend(pathwayModel.getPathway(), root);
			writeBiopax(pathwayModel, root);
		}
		return doc;
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
		// sets optional properties, in the order written in GPML2013a
		String description = pathway.getDescription();
		if (description != null) {
			Element cmt = new Element("Comment", root.getNamespace());
			cmt.setAttribute("Source", WP_DESCRIPTION);
			cmt.setText(description);
			root.addContent(cmt);
		}
		setAttr("Pathway", "Data-Source", root, pathway.getSource());
		setAttr("Pathway", "Version", root, pathway.getVersion());
		setAttr("Pathway", "Author", root, pathway.getDynamicProperty(PATHWAY_AUTHOR));
		setAttr("Pathway", "Maintainer", root, pathway.getDynamicProperty(PATHWAY_MAINTAINER));
		setAttr("Pathway", "Email", root, pathway.getDynamicProperty(PATHWAY_EMAIL));
		setAttr("Pathway", "Last-Modified", root, pathway.getDynamicProperty(PATHWAY_LASTMODIFIED));
		setAttr("Pathway", "Organism", root, pathway.getOrganism());
		setAttr("Pathway", "License", root, pathway.getLicense());
		// writes comments, biopaxRefs/citationRefs, dynamic properties
		writeComments(pathway.getComments(), root);
		writeBiopaxRefs(pathway.getCitationRefs(), root);
		writePathwayDynamicProperties(pathway, root);
		// sets graphics properties
		Element gfx = new Element("Graphics", root.getNamespace());
		root.addContent(gfx);
		setAttr("Pathway.Graphics", "BoardWidth", gfx, String.valueOf(pathway.getBoardWidth()));
		setAttr("Pathway.Graphics", "BoardHeight", gfx, String.valueOf(pathway.getBoardHeight()));
		// warnings for conversion GPML2021 to GPML2013a
		if (!pathway.getAuthors().isEmpty())
			Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a: Pathway authors info lost.");
		if (!pathway.getAnnotationRefs().isEmpty())
			Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a: Pathway annotationRef info lost.");
		if (!pathwayModel.getEvidences().isEmpty())
			Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a: Pathway evidences info lost.");
		if (pathway.getXref() != null)
			Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a: Pathway xref info lost.");
		if (!pathway.getBackgroundColor().equals(Color.decode("#ffffff")))
			Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a: Pathway backgroundColor info lost.");
	}

	/**
	 * Writes xref {@link Xref} information to new element. In GPML2013a, Xref is
	 * required for DataNodes, Interactions, and optional for States.
	 *
	 * @param xref     the xref of the pathway or pathway element.
	 * @param e        the parent element.
	 * @param required if true, xref is a required property.
	 */
	protected void writeXref(Xref xref, Element e, boolean required) throws ConverterException {
		// if xref null and required, writes out ""
		if (xref == null && required) {
			Element xrf = new Element("Xref", e.getNamespace());
			xrf.setAttribute("Database", "");
			xrf.setAttribute("ID", "");
			e.addContent(xrf);
		}
		if (xref != null) {
			String identifier = xref.getId();
			DataSource dataSource = xref.getDataSource();
			String dataSourceStr = XrefUtils.getXrefDataSourceStrGPML2013a(dataSource);
			if (dataSourceStr != null && !dataSourceStr.equals("")) {
				Element xrf = new Element("Xref", e.getNamespace());
				String base = e.getName();
				setAttr(base + ".Xref", "Database", xrf, dataSourceStr);
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
			if (comment != null) {
				// write comment only if comment has text
				String commentText = comment.getCommentText();
				if (commentText != null && !commentText.equals("")) {
					Element cmt = new Element("Comment", e.getNamespace());
					cmt.setText(commentText);
					String source = comment.getSource();
					if (source != null && !source.equals(""))
						cmt.setAttribute("Source", source);
					if (cmt != null)
						e.addContent(cmt);
				}
			}
		}
	}

	/**
	 * Writes BiopaxRef information from {@link PathwayElement#getCitationRefs} for
	 * pathway or pathway element. BiopaxRefs are equivalent to citationRefs in
	 * GPML2013a.
	 *
	 * @param citationRefs the list of citation references.
	 * @param e            the parent element.
	 * @throws ConverterException
	 */
	protected void writeBiopaxRefs(List<CitationRef> citationRefs, Element e) throws ConverterException {
		if (e != null) {
			for (CitationRef citationRef : citationRefs) {
				Element bpRef = new Element("BiopaxRef", e.getNamespace());
				bpRef.setText(citationRef.getCitation().getElementId());
				if (bpRef != null)
					e.addContent(bpRef);
			}
		}
	}

	/**
	 * Writes dynamic property information for pathway or pathway element.
	 * {@link PathwayElement#getDynamicProperty}.
	 *
	 * @param pathway the pathway.
	 * @param root    the jdom root element.
	 * @throws ConverterException
	 */
	protected void writePathwayDynamicProperties(Pathway pathway, Element root) throws ConverterException {
		Map<String, String> dynamicProperties = pathway.getDynamicProperties();
		for (String key : dynamicProperties.keySet()) {
			// if key is in static key set, do not write to GPML2013a
			if (GPML2013A_KEY_SET.contains(key))
				continue;
			Element dp = new Element("Attribute", root.getNamespace());
			setAttr("Attribute", "Key", dp, key);
			setAttr("Attribute", "Value", dp, dynamicProperties.get(key));
			if (dp != null) {
				root.addContent(dp);
			}
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
		for (DataNode dataNode : dataNodes) {
			Element dn = new Element("DataNode", root.getNamespace());
			setAttr("DataNode", "TextLabel", dn, dataNode.getTextLabel());
			writeShapedElement(dataNode, dn);
			String typeStr = dataNode.getType().getName();
			// in GPML2013a, "Undefined" type is written as "Unknown"
			if (typeStr.equalsIgnoreCase("Undefined")) {
				typeStr = "Unknown";
			}
			setAttr("DataNode", "Type", dn, typeStr);
			writeGroupRef(dataNode.getGroupRef(), dn);
			// writes xref (required)
			writeXref(dataNode.getXref(), dn, true);
			if (dn != null) {
				root.addContent(dn);
			}
			// warnings for conversion GPML2021 to GPML2013a
			if (dataNode.getAliasRef() != null) {
				Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a: DataNode " + dataNode.getElementId()
						+ " aliasRef info lost.");
			}
		}
	}

	/**
	 * Writes state {@link State} information.
	 *
	 * @param dataNodes the parent dataNode.
	 * @param root      the root element.
	 * @throws ConverterException
	 */
	protected void writeStates(List<DataNode> dataNodes, Element root) throws ConverterException {
		for (DataNode dataNode : dataNodes) {
			List<State> states = dataNode.getStates();
			for (State state : states) {
				Element st = new Element("State", root.getNamespace());
				// NB: StateType was not fully implemented in GPML2013a
				String typeStr = state.getType().getName();
				// in GPML2013a, "Undefined" type is written as "Unknown"
				if (typeStr.equalsIgnoreCase("Undefined")) {
					typeStr = "Unknown";
				}
				setAttr("State", "StateType", st, typeStr);
				setAttr("State", "GraphRef", st, dataNode.getElementId());
				setAttr("State", "TextLabel", st, state.getTextLabel() == null ? "" : state.getTextLabel());
				// if there are annotationRefs, writes this information to comment
				convertStateRefToComments(state, st);
				writeElementInfo(state, st);
				writeShapedOrStateDynamicProperties(state.getDynamicProperties(), state, st);
				// sets graphics properties
				Element gfx = new Element("Graphics", st.getNamespace());
				st.addContent(gfx);
				setAttr("State.Graphics", "RelX", gfx, Double.toString(state.getRelX()));
				setAttr("State.Graphics", "RelY", gfx, Double.toString(state.getRelY()));
				setAttr("State.Graphics", "Width", gfx, Double.toString(state.getWidth()));
				setAttr("State.Graphics", "Height", gfx, Double.toString(state.getHeight()));
				// state does not have custom font properties in GPML2013a
				// z-order for state is not written to GPML2013a
				setAttr("State.Graphics", "FillColor", gfx, ColorUtils.colorToHex(state.getFillColor(), false));
				writeShapeStyleProperty(state, gfx);
				writeColor(state, gfx);
				// writes xref (write even if empty)
				writeXref(state.getXref(), st, true);
				if (st != null) {
					root.addContent(st);
				}
			}
		}
	}

	/**
	 * This method handles converting {@link State} phosphosite
	 * {@link AnnotationRef} and {@link Xref} information back to {@link Comment}
	 * for writing to GPML2013a.
	 *
	 * NB: "ptm" and "direction" are specially handled.
	 *
	 * @param state
	 * @param st
	 * @throws ConverterException
	 */
	protected void convertStateRefToComments(State state, Element st) throws ConverterException {
		// linked hash map to maintain order of input
		Map<String, String> annotationsMap = new LinkedHashMap<String, String>();
		for (AnnotationRef annotationRef : state.getAnnotationRefs()) {
			String value = annotationRef.getAnnotation().getValue();
			String type = annotationRef.getAnnotation().getType().getName();
			// specially handle ptm
			for (String key : STATE_PTM_MAP.keySet()) {
				if (value.equals(STATE_PTM_MAP.get(key).get(0))) {
					value = key;
					type = PTM;
				}
			}
			// specially handle direction
			for (String key : STATE_DIRECTION_MAP.keySet()) {
				if (value.equals(STATE_DIRECTION_MAP.get(key).get(0))) {
					value = key;
					type = DIRECTION;
				}
			}
			annotationsMap.put(type, value);
		}
		// if sitegrpid from xref, also add to comment
		if (state.getXref() != null) {
			if (XrefUtils.getXrefDataSourceStr(state.getXref().getDataSource()).equalsIgnoreCase(SITEGRPID_DB)) {
				String type = SITEGRPID;
				String value = state.getXref().getId();
				annotationsMap.put(type, value);
			}
		}
		// create commentText string from annotations map.
		if (!annotationsMap.isEmpty()) {
			String commentText = annotationsMap.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
					.collect(Collectors.joining("; "));
			Element cmt = new Element("Comment", st.getNamespace());
			cmt.setText(commentText);
			if (cmt != null) {
				st.addContent(cmt);
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
		// writes comments, biopaxRefs/citationRefs, dynamic properties
		writeElementInfo(lineElement, ln);
		writeLineDynamicProperties(lineElement.getDynamicProperties(), lineElement, ln);
		// sets graphics properties
		Element gfx = new Element("Graphics", ln.getNamespace());
		ln.addContent(gfx);
		writeLineStyleProperty(lineElement, gfx);
		// writes points
		writePoints(lineElement, gfx);
		// writes anchors
		writeAnchors(lineElement.getAnchors(), gfx);
		writeGroupRef(lineElement.getGroupRef(), ln);
	}

	/**
	 * Writes point {@link LinePoint} information.
	 *
	 * @param lineElement the line element.
	 * @param gfx    the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writePoints(LineElement lineElement, Element gfx) throws ConverterException {
		List<Element> ptList = new ArrayList<Element>();
		List<LinePoint> points = lineElement.getLinePoints();
		for (int i = 0; i < points.size(); i++) {
			LinePoint point = points.get(i);
			Element pt = new Element("Point", gfx.getNamespace());
			writeElementId(point.getElementId(), pt);
			String base = ((Element) gfx.getParent()).getName();
			setAttr(base + ".Graphics.Point", "X", pt, Double.toString(point.getX()));
			setAttr(base + ".Graphics.Point", "Y", pt, Double.toString(point.getY()));
			// sets optional properties elementRef, relX, and relY
			if (writePointElementRef(point.getElementRef(), pt)) {
				setAttr(base + ".Graphics.Point", "RelX", pt, Double.toString(point.getRelX()));
				setAttr(base + ".Graphics.Point", "RelY", pt, Double.toString(point.getRelY()));
			}
			// if start or end point, write arrowhead type.
			if (i == 0) {
				writeArrowHeadType(lineElement.getStartArrowHeadType(), base, pt);
			} else if (i == points.size() - 1) {
				writeArrowHeadType(lineElement.getEndArrowHeadType(), base, pt);
			} else { // otherwise arrowHeadType = Line
				setAttr(base + ".Graphics.Point", "ArrowHead", pt, "Line");
			}
			// add point jdom element to list
			if (pt != null) {
				ptList.add(pt);
			}
		}
		if (ptList != null && ptList.isEmpty() == false) {
			gfx.addContent(ptList);
		}
	}

	/**
	 * Writes the arrowHead for point jdom element. // TODO arrowhead sub types
	 * Handling?
	 *
	 * @param arrowHead the arrow head.
	 * @param base      the string for either "Interaction" or "GraphicalLine"
	 * @param pt        the point jdom element to write to.
	 * @throws ConverterException
	 */
	protected void writeArrowHeadType(ArrowHeadType arrowHead, String base, Element pt) throws ConverterException {
		String arrowHeadStr = getArrowHeadTypeStr(arrowHead);
		if (arrowHeadStr == null) {
			arrowHeadStr = arrowHead.getName();
		}
		setAttr(base + ".Graphics.Point", "ArrowHead", pt, arrowHeadStr);
	}

	/**
	 * Writes anchor {@link Anchor} information.
	 *
	 * @param anchors the list of anchors.
	 * @param gfx     the jdom graphics element.
	 * @throws ConverterException
	 */
	protected void writeAnchors(List<Anchor> anchors, Element gfx) throws ConverterException {
		if (!anchors.isEmpty()) {
			List<Element> anList = new ArrayList<Element>();
			for (Anchor anchor : anchors) {
				Element an = new Element("Anchor", gfx.getNamespace());
				String base = ((Element) gfx.getParent()).getName();
				setAttr(base + ".Graphics.Anchor", "Position", an, Double.toString(anchor.getPosition()));
				setAttr(base + ".Graphics.Anchor", "Shape", an, anchor.getShapeType().getName());
				writeElementId(anchor.getElementId(), an);
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
			Element lb = new Element("Label", root.getNamespace());
			setAttr("Label", "TextLabel", lb, label.getTextLabel());
			writeShapedElement(label, lb);
			if (label.getHref() != null) {
				setAttr("Label", "Href", lb, label.getHref());
			}
			writeGroupRef(label.getGroupRef(), lb);
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
			Element shp = new Element("Shape", root.getNamespace());
			if (shape.getTextLabel() != null) {
				setAttr("Shape", "TextLabel", shp, shape.getTextLabel());
			}
			writeShapedElement(shape, shp);
			writeGroupRef(shape.getGroupRef(), shp);
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
	 * <p>
	 * NB:
	 * <ol>
	 * <li>In GPML2013a, group has default font properties and shape style
	 * properties which are not written to the gpml.
	 * <li>Group type "Group" (GPML2021) is written as "None" (GPML2013a).
	 * <li>Group type "Transparent" (GPML2021) is written as "Group" (GPML2013a).
	 * </ol>
	 *
	 * @param groups the list of groups.
	 * @param root   the root element.
	 * @throws ConverterException
	 */
	protected void writeGroups(List<Group> groups, Element root) throws ConverterException {
		for (Group group : groups) {
			Element grp = new Element("Group", root.getNamespace());
			setAttr("Group", "GroupId", grp, group.getElementId());
			setAttr("Group", "GraphId", grp, group.getElementId());
			String typeStr = group.getType().getName();
			if (typeStr.equals("Group")) {
				typeStr = "None"; // "Group" (GPML2021) is written as "None" (GPML2013a)
			} else if (typeStr.equals("Transparent")) {
				typeStr = "Group"; // "Transparent" (GPML2021) is written as "Group" (GPML2013a)
			}
			setAttr("Group", "Style", grp, typeStr);
			writeXref(group.getXref(), grp, false);
			writeElementInfo(group, grp);
			writeShapedOrStateDynamicProperties(group.getDynamicProperties(), group, grp);
			// sets optional properties
			if (group.getTextLabel() != null) {
				setAttr("Group", "TextLabel", grp, group.getTextLabel());
			}
			writeGroupRef(group.getGroupRef(), grp);
			if (grp != null) {
				root.addContent(grp);
			}
		}
	}

	/**
	 * Writes the infobox x and y coordinate information.
	 *
	 * @param pathway the pathway.
	 * @param root    the root element.
	 */
	protected void writeInfoBox(Pathway pathway, Element root) {
		String centerX = pathway.getDynamicProperty(INFOBOX_CENTER_X);
		String centerY = pathway.getDynamicProperty(INFOBOX_CENTER_Y);
		Element ifb = new Element("InfoBox", root.getNamespace());
		ifb.setAttribute("CenterX", centerX == null ? "0.0" : centerX);
		ifb.setAttribute("CenterY", centerY == null ? "0.0" : centerY);
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
	 * Writes gpml:Biopax information openControlledVocabulary and PublicationXref.
	 * {@link #writeOpenControlledVocabulary} from {@link Annotation}, and
	 * {@link #writePublicationXref} from {@link Citation}.
	 *
	 * @param pathwayModel the pathway model.
	 * @param root         the root element.
	 * @throws ConverterException
	 */
	protected void writeBiopax(PathwayModel pathwayModel, Element root) throws ConverterException {
		Element bp = new Element("Biopax", root.getNamespace());
		// writes openControlledVocabulary, equivalent to annotation
		writeOpenControlledVocabulary(pathwayModel, bp);
		// writes publicationXref, equivalent to citation
		writePublicationXref(pathwayModel.getCitations(), bp);
		if (!bp.getChildren().isEmpty()) {
			root.addContent(bp);
		}
	}

	/**
	 * Writes gpml:Biopax bp:OpenControlledVocabulary {@link Annotation}
	 * information. Because it is not possible to write {@link AnnotationRef} for
	 * {@link PathwayObject} in GPML2013a, the State annotationRef information is
	 * written as {@link Comment} in {@link #convertStateRefToComments}. We avoid
	 * writing annotation information if it is for a state annotationRef/comment
	 * since it cannot be properly linked to a state pathway element and is
	 * duplicate information.
	 *
	 * @param pathwayModel the pathway model.
	 * @param bp           the jdom biopax element.
	 * @throws ConverterException
	 */
	protected void writeOpenControlledVocabulary(PathwayModel pathwayModel, Element bp) throws ConverterException {
		for (Annotation annotation : pathwayModel.getAnnotations()) {
			String type = annotation.getType().getName();
			// for GPML2013a, we only write annotations with annotationsRefs on the pathway
			boolean hasPathwayAnnotation = false;
			for (AnnotationRef annotationRef : annotation.getAnnotationRefs()) {
				if (annotationRef.getAnnotatable().getClass() == Pathway.class) {
					hasPathwayAnnotation = true;
				}
			}
			if (!hasPathwayAnnotation)
				continue;
			Element ocv = new Element("openControlledVocabulary", BIOPAX_NAMESPACE);
			Element term = new Element("TERM", BIOPAX_NAMESPACE);
			Element id = new Element("ID", BIOPAX_NAMESPACE);
			Element onto = new Element("Ontology", BIOPAX_NAMESPACE);
			term.setAttribute("datatype", RDF_STRING, RDF_NAMESPACE);
			id.setAttribute("datatype", RDF_STRING, RDF_NAMESPACE);
			onto.setAttribute("datatype", RDF_STRING, RDF_NAMESPACE);
			term.setText(annotation.getValue());
			String prefix = XrefUtils.getXrefDataSourceStr(annotation.getXref().getDataSource());
			if (OCV_ONTOLOGY_MAP.containsValue(prefix)) {
				type = OCV_ONTOLOGY_MAP.getKey(prefix);
			}
			onto.setText(type);
			id.setText(prefix + ":" + annotation.getXref().getId());
			ocv.addContent(term);
			ocv.addContent(id);
			ocv.addContent(onto);
			if (ocv != null) {
				bp.addContent(ocv);
			}
			// warnings for conversion GPML2021 to GPML2013a
			if (annotation.getUrlLink() != null) {
				Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a: Annotation " + annotation.getElementId()
						+ " url and elementId info lost.");
			}
		}
	}

	/**
	 * Writes gpml:Biopax bp:PublicationXref {@link Citation} information.
	 *
	 * @param citations the list of citations.
	 * @param bp        the jdom biopax element.
	 * @throws ConverterException
	 */
	protected void writePublicationXref(List<Citation> citations, Element bp) throws ConverterException {
		for (Citation citation : citations) {
			Element pubxf = new Element("PublicationXref", BIOPAX_NAMESPACE);
			pubxf.setAttribute("id", citation.getElementId(), RDF_NAMESPACE);
			List<String> authors = citation.getAuthors();
			writePublicationXrefInfo(citation.getXref().getId(), "ID", pubxf);
			writePublicationXrefInfo(citation.getXref().getDataSource().getFullName(), "DB", pubxf);
			writePublicationXrefInfo(citation.getTitle(), "TITLE", pubxf);
			writePublicationXrefInfo(citation.getSource(), "SOURCE", pubxf);
			writePublicationXrefInfo(citation.getYear(), "YEAR", pubxf);
			if (authors != null && !authors.isEmpty()) {
				for (String author : authors)
					if (author != null && !author.equals(""))
						writePublicationXrefInfo(author, "AUTHORS", pubxf);
			}
			if (pubxf != null) {
				bp.addContent(pubxf);
			}
			// warnings for conversion GPML2021 to GPML2013a
			if (citation.getUrlLink() != null) {
				Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a: Citation " + citation.getElementId()
						+ " url info lost.");
			}
		}
	}

	/**
	 * Writes Biopax PublicationXref information to PublicationXref element. NB: The
	 * main purpose of this method is to make {@link #writePublicationXref} more
	 * concise. If property value is null, writes "".
	 *
	 * @param propertyValue the value of the property.
	 * @param elementName   the name for new child element of pubxf element.
	 * @param pubxf         the PublicationXref element.
	 * @throws ConverterException
	 */
	protected void writePublicationXrefInfo(String propertyValue, String elementName, Element pubxf)
			throws ConverterException {
		propertyValue = propertyValue == null ? "" : propertyValue;
		Element e = new Element(elementName, BIOPAX_NAMESPACE);
		e.setAttribute("datatype", RDF_STRING, RDF_NAMESPACE);
		e.setText(propertyValue);
		if (e != null) {
			pubxf.addContent(e);
		}
	}

	/**
	 * Writes elementId {@link PathwayObject} property information.
	 *
	 * @param elementId the elementId.
	 * @param e         the parent element.
	 */
	protected void writeElementId(String elementId, Element e) {
		if (elementId != null && !elementId.equals("")) {
			e.setAttribute("GraphId", elementId);
		}
	}

	/**
	 * Writes groupRef property information. {@link Group} stores GroupId as its
	 * elementId.
	 *
	 * @param groupRef the groupRef.
	 * @param e        the parent element.
	 */
	protected void writeGroupRef(Group groupRef, Element e) {
		if (groupRef != null) {
			String groupRefStr = groupRef.getElementId();
			if (groupRefStr != null && !groupRefStr.equals("")) {
				e.setAttribute("GroupRef", groupRefStr);
			}
		}
	}

	/**
	 * Writes point elementRef property information as GraphRef. This method is used
	 * only by {@link #writePoints}
	 *
	 * @param elementRef the pathway element point refers to. .
	 * @param pt         the jdom point element.
	 * @return true if elementRef exists and is successfully written.
	 */
	protected boolean writePointElementRef(LinkableTo elementRef, Element pt) {
		if (elementRef != null) {
			String elementRefStr = elementRef.getElementId();
			if (elementRefStr != null && !elementRefStr.equals("")) {
				pt.setAttribute("GraphRef", elementRefStr);
			}
			return true;
		}
		return false;
	}

	/**
	 * Writes shapedElement {@link ShapedElement} information for datanodes, labels,
	 * shapes, or groups.
	 *
	 * @param shapedElement the datanode, label, shape, or group.
	 * @param se            the shape element.
	 * @throws ConverterException
	 */
	protected void writeShapedElement(ShapedElement shapedElement, Element se) throws ConverterException {
		String base = se.getName();
		writeElementInfo(shapedElement, se);
		writeShapedOrStateDynamicProperties(shapedElement.getDynamicProperties(), shapedElement, se);
		Element gfx = new Element("Graphics", se.getNamespace());
		se.addContent(gfx);
		// writes rect properties
		writeRectProperty(shapedElement, gfx);
		// writes z-order and fill color (separately to preserve GPML2013 order)
		setAttr(base + ".Graphics", "ZOrder", gfx, String.valueOf(shapedElement.getZOrder()));
		setAttr(base + ".Graphics", "FillColor", gfx, ColorUtils.colorToHex(shapedElement.getFillColor(), false));
		// writes font properties
		writeFontProperty(shapedElement, gfx);
		// writes rest of shape style properties
		writeShapeStyleProperty(shapedElement, gfx);
		// writes color
		writeColor(shapedElement, gfx);
	}

	/**
	 * Writes elementId, comment group {comment, dynamic property, annotationRef,
	 * citationRef) and evidenceRef {@link PathwayElement} information for
	 * datanodes, interactions, graphicalLines, labels, shapes, and group.
	 *
	 * NB: writing of dynamic properties (gpml:Attribute) requires special handling
	 * of DoubleLineProperty and CellularComponentProperty for shaped, state, or
	 * line pathway elements. {@link #writeLineDynamicProperties} ,
	 * {@link #writeShapedOrStateDynamicProperties}
	 *
	 * @param elementInfo the pathway element.
	 * @param e           the parent element.
	 * @throws ConverterException
	 */
	protected void writeElementInfo(PathwayElement elementInfo, Element e) throws ConverterException {
		// different method for writing group id
		if (elementInfo.getClass() != Group.class) {
			writeElementId(elementInfo.getElementId(), e);
		}
		writeComments(elementInfo.getComments(), e);
		writeBiopaxRefs(elementInfo.getCitationRefs(), e);
		// warnings for conversion GPML2021 to GPML2013a
		if (!elementInfo.getAnnotationRefs().isEmpty()) {
			Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a format: AnnotationRef info lost.");
		}
		if (!elementInfo.getEvidenceRefs().isEmpty()) {
			Logger.log.trace("Warning: Conversion GPML2021 to GPML2013a format: EvidenceRef info lost.");
		}
	}

	/**
	 * Writes dynamic property information for {@link ShapedElement} or
	 * {@link State} pathway element. In GPML2013a, cellular component shapeTypes
	 * and double lineStyle information are written to dynamic properties because
	 * they were not yet defined in the GPML2013a schema/enum classes.
	 *
	 * @param dynamicProperties the list of dynamic properties.
	 * @param shapedElement     the shaped pathway element.
	 * @param se                the jdom shaped pathway element element.
	 * @throws ConverterException
	 */
	protected void writeShapedOrStateDynamicProperties(Map<String, String> dynamicProperties,
			ShapedElement shapedElement, Element se) throws ConverterException {
		for (String key : dynamicProperties.keySet()) {
			Element dp = new Element("Attribute", se.getNamespace());
			setAttr("Attribute", "Key", dp, key);
			setAttr("Attribute", "Value", dp, dynamicProperties.get(key));
			if (dp != null) {
				se.addContent(dp);
			}
		}
		// if shape in cellular component map, write info to dynamic property
		IShape shapeType = shapedElement.getShapeType();
		if (CELL_CMPNT_MAP.containsKey(shapeType)) {
			Element dp = new Element("Attribute", se.getNamespace());
			setAttr("Attribute", "Key", dp, CELL_CMPNT_KEY);
			String shapeTypeStr = shapeType.getName();
			shapeTypeStr = fromCamelCase(shapeTypeStr);
			setAttr("Attribute", "Value", dp, shapeTypeStr);
			if (dp != null) {
				se.addContent(dp);
			}
		}
		// if double lineStyle, write info to dynamic property
		LineStyleType borderStyle = shapedElement.getBorderStyle();
		if (borderStyle.getName().equalsIgnoreCase("Double")) {
			Element dp = new Element("Attribute", se.getNamespace());
			setAttr("Attribute", "Key", dp, DOUBLE_LINE_KEY);
			setAttr("Attribute", "Value", dp, "Double");
			if (dp != null) {
				se.addContent(dp);
			}
		}
		// if state has rotation, write info to dynamic property
		if (shapedElement.getClass() == State.class) {
			Double rotation = shapedElement.getRotation();
			if (rotation != 0) {
				String rotationStr = String.valueOf(rotation);
				Element dp = new Element("Attribute", se.getNamespace());
				setAttr("Attribute", "Key", dp, STATE_ROTATION);
				setAttr("Attribute", "Value", dp, rotationStr);
				if (dp != null) {
					se.addContent(dp);
				}
			}
		}
	}

	/**
	 * Writes dynamic property information for {@link LineElement}. In GPML2013a,
	 * double lineStyle information is written to dynamic properties because it was
	 * not yet defined in the GPML2013a schema/enum classes.
	 *
	 * @param dynamicProperties the list of dynamic properties.
	 * @param lineElement       the line pathway element.
	 * @param ln                the jdom line pathway element element.
	 * @throws ConverterException
	 */
	protected void writeLineDynamicProperties(Map<String, String> dynamicProperties, LineElement lineElement,
			Element ln) throws ConverterException {
		for (String key : dynamicProperties.keySet()) {
			Element dp = new Element("Attribute", ln.getNamespace());
			setAttr("Attribute", "Key", dp, key);
			setAttr("Attribute", "Value", dp, dynamicProperties.get(key));
			if (dp != null) {
				ln.addContent(dp);
			}
		}
		// if double lineStyle, write info to dynamic property
		LineStyleType lineStyle = lineElement.getLineStyle();
		if (lineStyle.getName().equalsIgnoreCase("Double")) {
			Element dp = new Element("Attribute", ln.getNamespace());
			setAttr("Attribute", "Key", dp, DOUBLE_LINE_KEY);
			setAttr("Attribute", "Value", dp, "Double");
			if (dp != null) {
				ln.addContent(dp);
			}
		}
	}

	/**
	 * Writes rect property information.
	 *
	 * @param shapedElement the shaped pathway element.
	 * @param gfx           the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeRectProperty(ShapedElement shapedElement, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		if (shapedElement.getClass() != State.class) {
			setAttr(base + ".Graphics", "CenterX", gfx, Double.toString(shapedElement.getCenterX()));
			setAttr(base + ".Graphics", "CenterY", gfx, Double.toString(shapedElement.getCenterY()));
		}
		setAttr(base + ".Graphics", "Width", gfx, Double.toString(shapedElement.getWidth()));
		setAttr(base + ".Graphics", "Height", gfx, Double.toString(shapedElement.getHeight()));
	}

	/**
	 * Writes color property information for shaped pathway elements. This method is
	 * not used by line pathway elements.
	 *
	 * NB: In GPML2013a, there is only color (textColor and borderColor are the same
	 * color unless shapeType is "None" in which case there is no border). Color is
	 * written separately to preserve the original order of properties in GPML2013a.
	 *
	 * @param shapedElement the shaped pathway element.
	 * @param gfx           the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeColor(ShapedElement shapedElement, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		setAttr(base + ".Graphics", "Color", gfx, ColorUtils.colorToHex(shapedElement.getTextColor(), false));

	}

	/**
	 * Writes font property information, except color.
	 *
	 * @param shapedElement the shaped pathway element.
	 * @param gfx           the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeFontProperty(ShapedElement shapedElement, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		setAttr(base + ".Graphics", "FontName", gfx,
				shapedElement.getFontName() == null ? "" : shapedElement.getFontName());
		setAttr(base + ".Graphics", "FontWeight", gfx, shapedElement.getFontWeight() ? "Bold" : "Normal");
		setAttr(base + ".Graphics", "FontStyle", gfx, shapedElement.getFontStyle() ? "Italic" : "Normal");
		setAttr(base + ".Graphics", "FontDecoration", gfx, shapedElement.getFontDecoration() ? "Underline" : "Normal");
		setAttr(base + ".Graphics", "FontStrikethru", gfx, shapedElement.getFontStrikethru() ? "Strikethru" : "Normal");
		setAttr(base + ".Graphics", "FontSize", gfx, Integer.toString((int) shapedElement.getFontSize()));
		setAttr(base + ".Graphics", "Valign", gfx, shapedElement.getVAlign().getName());
		setAttr(base + ".Graphics", "Align", gfx, shapedElement.getHAlign().getName());
	}

	/**
	 * Writes shape style property information, except borderColor, zOrder, and
	 * fillColor. These properties are written separately to preserve the original
	 * order of properties in GPML2013a.
	 *
	 * NB: borderColor is not set, Color is set solely by textColor. zOrder and
	 * fillColor are written separately to preserve the order of properties in
	 * GPML2013a.
	 *
	 * @param shapedElement the shaped pathway element.
	 * @param gfx           the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeShapeStyleProperty(ShapedElement shapedElement, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		// do not set borderColor, Color is set by textColor
		IShape shapeType = shapedElement.getShapeType();
		// if shape in cellular component map, specially handle
		if (CELL_CMPNT_MAP.containsKey(shapeType)) {
			String shapeTypeNewStr = CELL_CMPNT_MAP.get(shapeType).getName();
			shapeTypeNewStr = fromCamelCase(shapeTypeNewStr);
			setAttr(base + ".Graphics", "ShapeType", gfx, shapeTypeNewStr);
		} else {
			String shapeTypeStr = shapeType.getName();
			shapeTypeStr = fromCamelCase(shapeTypeStr);
			// if deprecated shape type, write 2013a shape type string
			if (DEPRECATED_MAP.containsValue(shapeType)) {
				shapeTypeStr = DEPRECATED_MAP.getKey(shapeType);
			}
			setAttr(base + ".Graphics", "ShapeType", gfx, shapeTypeStr);
		}
		String borderStyleStr = shapedElement.getBorderStyle().getName();
		// in GPML2013a, "Dashed" line style is "Broken" and must be written so
		if (borderStyleStr.equalsIgnoreCase("Dashed")) {
			borderStyleStr = "Broken";
		}
		// if "Double", line style information is written to dynamic property instead
		if (!borderStyleStr.equalsIgnoreCase("Double")) {
			setAttr(base + ".Graphics", "LineStyle", gfx, borderStyleStr);
		}
		setAttr(base + ".Graphics", "LineThickness", gfx, String.valueOf(shapedElement.getBorderWidth()));
	}

	/**
	 * Writes line style property information.
	 *
	 * @param lineElement the line pathway element.
	 * @param gfx         the parent graphics element.
	 * @throws ConverterException
	 */
	protected void writeLineStyleProperty(LineElement lineElement, Element gfx) throws ConverterException {
		String base = ((Element) gfx.getParent()).getName();
		setAttr(base + ".Graphics", "ConnectorType", gfx, lineElement.getConnectorType().getName());
		setAttr(base + ".Graphics", "ZOrder", gfx, String.valueOf(lineElement.getZOrder()));
		String lineStyleStr = lineElement.getLineStyle().getName();
		// in GPML2013a, "Dashed" line style is "Broken" and must be written so
		if (lineStyleStr.equalsIgnoreCase("Dashed")) {
			lineStyleStr = "Broken";
		}
		// if "Double", line style information is written to dynamic property instead
		if (!lineStyleStr.equalsIgnoreCase("Double")) {
			setAttr(base + ".Graphics", "LineStyle", gfx, lineStyleStr);
		}
		setAttr(base + ".Graphics", "LineThickness", gfx, String.valueOf(lineElement.getLineWidth()));
		setAttr(base + ".Graphics", "Color", gfx, ColorUtils.colorToHex(lineElement.getLineColor(), false));
	}

}
