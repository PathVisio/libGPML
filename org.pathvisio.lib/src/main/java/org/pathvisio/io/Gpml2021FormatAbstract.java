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
import java.awt.geom.Point2D;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.SAXOutputter;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.core.biopax.BiopaxElement;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.xml.sax.SAXException;

import keep.IElementIdContainer;

/**
 * This abstract class is for the reading and writing of GPML files. Base
 * implementation for different GpmlFormat versions. Code that is shared between
 * multiple versions is located here.
 */
public abstract class Gpml2021FormatAbstract {
	protected Gpml2021FormatAbstract(String xsdFile, Namespace nsGPML) {
		this.xsdFile = xsdFile;
		this.nsGPML = nsGPML;
	}

	private final Namespace nsGPML;
	private final String xsdFile;

	/**
	 * @return nsGPML the gpml namespace.
	 */
	public Namespace getGpmlNamespace() {
		return nsGPML;
	}

	/**
	 * Updates pathway information.
	 *
	 * @param root the xml element
	 * @param o    the pathway element
	 * @throws ConverterException
	 */
	protected abstract void writeMappInfoVariable(Element root, Pathway p) throws ConverterException;

	@Override
	protected void writeMappInfoVariable(Element root, Pathway p) throws ConverterException {
		setAttribute("Pathway", "License", root, p.getLicense());
	}

	/**
	 * Updates pathway information.
	 * 
	 * @param root
	 * @param o
	 * @throws ConverterException
	 */
	protected void writeMappInfo(Element root, Pathway p) throws ConverterException {
		root.setAttribute("title", p.getTitle());
		root.setAttribute("organism", p.getOrganism());
		root.setAttribute("source", p.getSource());
		root.setAttribute("version", p.getVersion());
		root.setAttribute("license", p.getLicense());

		// TODO License?

		// TODO Handle
		setAttribute("Pathway", "Email", root, p.getEmail());
		setAttribute("Pathway", "Maintainer", root, p.getMaintainer());
		setAttribute("Pathway", "Last-Modified", root, p.getLastModified());
		writeComments(p, root);
		writeBiopaxRef(p, root);
		writeAttributes(p, root);

		// Add element Xref
		Element xref = new Element("Xref", getGpmlNamespace());
		String identifier = p.getXref().getId();
		String dataSource = p.getXref().getDataSource().getFullName(); // TODO dataSource
		xref.setAttribute("dataSource", dataSource == null ? "" : dataSource); // TODO null handling
		xref.setAttribute("identifier", identifier == null ? "" : identifier);
		root.addContent(xref);

		// Add elements Author
		for (Author a : p.getAuthors()) {
			if (a == null)
				continue;
			Element author = new Element("Author", getGpmlNamespace());
			author.setAttribute("name", a.getName());
			author.setAttribute("fullName", a.getFullName());
			author.setAttribute("email", a.getEmail());
			root.addContent(author);
		}

		Element graphics = new Element("Graphics", nsGPML);
		root.addContent(graphics);
		setAttribute("Pathway.Graphics", "BoardWidth", graphics, String.valueOf(p.getBoardWidth()));
		setAttribute("Pathway.Graphics", "BoardHeight", graphics, String.valueOf(p.getBoardHeight()));
	}

	Element graphics = new Element("Graphics", nsGPML);root.addContent(graphics);

	setAttribute("Pathway.Graphics", "BoardWidth", graphics, String.valueOf(p.getBoardWidth()));
		setAttribute("Pathway.Graphics", "BoardHeight", graphics, String.valueOf(p.getBoardHeight()));

		writeMappInfoVariable(root, p);
	}

	public abstract PathwayElement readElement(Element e, Pathway p) throws ConverterException;

	public PathwayElement readElement(Element e) throws ConverterException {
		return readElement(e, null);
	}

	protected void readComments(PathwayElement o, Element e) throws ConverterException {
		for (Object f : e.getChildren("Comment", e.getNamespace())) {
			o.addComment(((Element) f).getText(), getAttribute("Comment", "Source", (Element) f));
		}
	}

	protected void writeComments(PathwayElement o, Element e) throws ConverterException {
		if (e != null) {
			for (PathwayElement.Comment c : o.getComments()) {
				Element f = new Element("Comment", e.getNamespace());
				f.setText(c.getComment());
				setAttribute("Comment", "Source", f, c.getSource());
				e.addContent(f);
			}
		}
	}

	/**
	 * Attribute in gpml
	 * 
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readDynamicProperty(PathwayElement o, Element e) throws ConverterException {
		for (Object f : e.getChildren("Attribute", e.getNamespace())) {
			String key = getAttribute("Attribute", "Key", (Element) f);
			String value = getAttribute("Attribute", "Value", (Element) f);
			o.addDynamicProperty(special); // TODO look into implementation
		}
	}

	/**
	 * Attribute in gpml
	 * 
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void writeDynamicProperty(PathwayElement o, Element e) throws ConverterException {
		if (e != null) {
			for (String key : o.getDynamicPropertyKeys()) {
				Element a = new Element("Attribute", e.getNamespace());
				setAttribute("Attribute", "Key", a, key);
				setAttribute("Attribute", "Value", a, o.getDynamicProperty(key));
				e.addContent(a);
			}
		}
	}

	// TODO
	protected void readElementId(IElementIdContainer o, Element e) {
		String id = e.getAttributeValue("GraphId");
		// Never add graphid until all elements are readped, to prevent duplcate ids!
//		if((id == null || id.equals("")) && o.getGmmlData() != null) {
//			id = o.getGmmlData().getUniqueGraphId();
//		}
		if (id != null) {
			o.setElementId(id);
		}
	}

	// TODO
	protected void writeElementId(IElementIdContainer o, Element e) {
		String id = o.getElementId();
		// id has to be unique!
		if (id != null && !id.equals("")) {
			e.setAttribute("GraphId", o.getElementId());
		}
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
	
	protected void writeGroupRef(ShapedElement o, Element e) {
		String groupRef = o.getGroupRef().getElementId();
		if (groupRef != null && !groupRef.equals("")) {
			e.setAttribute("GroupRef", groupRef);
		}
	}
	
	protected void writeGroupRef(LineElement o, Element e) {
		String groupRef = o.getGroupRef().getElementId();
		if (groupRef != null && !groupRef.equals("")) {
			e.setAttribute("GroupRef", groupRef);
		}
	}

	protected abstract void readMappInfoDataVariable(Pathway p, Element e) throws ConverterException;

	protected void readPathwayInfo(Pathway p, Element root) throws ConverterException {
		p.setTitle(root.getAttributeValue("title"));
		p.setOrganism(root.getAttributeValue("organism"));
		p.setSource(root.getAttributeValue("source"));
		p.setVersion(root.getAttributeValue("version"));
		p.setLicense(root.getAttributeValue("license"));

		//TODO p.setAuthor(root.getAttribute("Author"));
		p.setEmail(root.getAttribute("Pathway", "Email", e));

		readMappInfoDataVariable(p, e);
	}

	// TODO HANDLE
	protected void writeBiopax(PathwayElement o, Element e) throws ConverterException {
		Document bp = ((BiopaxElement) o).getBiopax();
		if (e != null && bp != null) {
			List<Content> content = bp.getRootElement().cloneContent();
			for (Content c : content) {
				if (c instanceof Element) {
					Element elm = (Element) c;
					if (elm.getNamespace().equals(GpmlFormat.BIOPAX)) {
						e.addContent(c);
					} else if (elm.getName().equals("RDF") && elm.getNamespace().equals(GpmlFormat.RDF)) {
						for (Object ce : elm.getChildren()) {
							if (((Element) ce).getNamespace().equals(GpmlFormat.BIOPAX)) {
								e.addContent((Element) ce);
							}
						}
					} else {
						Logger.log.info("Skipped non-biopax element" + c);
					}
				}
			}
		}
	}

	protected void readBiopaxRef(PathwayElement o, Element e) throws ConverterException {
		for (Object f : e.getChildren("BiopaxRef", e.getNamespace())) {
			o.addBiopaxRef(((Element) f).getText());
		}
	}

	protected void writeBiopaxRef(PathwayElement o, Element e) throws ConverterException {
		if (e != null) {
			for (String ref : o.getBiopaxRefs()) {
				Element f = new Element("BiopaxRef", e.getNamespace());
				f.setText(ref);
				e.addContent(f);
			}
		}
	}

	public void readFromRoot(Element root, Pathway pwy) throws ConverterException {
		mapElement(root, pwy); // MappInfo

		// Iterate over direct children of the root element
		for (Object e : root.getChildren()) {
			mapElement((Element) e, pwy);
		}
		Logger.log.trace("End copying read elements");

		// Add graphIds for objects that don't have one
		addGraphIds(pwy);

		// Convert absolute point coordinates of linked points to
		// relative coordinates
		convertPointCoordinates(pwy);
	}

	private static void addGraphIds(Pathway pathway) throws ConverterException {
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

	private static void convertPointCoordinates(Pathway pathway) throws ConverterException {
		for (PathwayElement pe : pathway.getDataObjects()) {
			if (pe.getObjectType() == ObjectType.LINE || pe.getObjectType() == ObjectType.GRAPHLINE) {
				String sr = pe.getStartGraphRef();
				String er = pe.getEndGraphRef();
				if (sr != null && !"".equals(sr) && !pe.getMStart().relativeSet()) {
					ElementIdContainer idc = pathway.getGraphIdContainer(sr);
					Point2D relative = idc.toRelativeCoordinate(
							new Point2D.Double(pe.getMStart().getRawX(), pe.getMStart().getRawY()));
					pe.getMStart().setRelativePosition(relative.getX(), relative.getY());
				}
				if (er != null && !"".equals(er) && !pe.getMEnd().relativeSet()) {
					ElementIdContainer idc = pathway.getGraphIdContainer(er);
					Point2D relative = idc
							.toRelativeCoordinate(new Point2D.Double(pe.getMEnd().getRawX(), pe.getMEnd().getRawY()));
					pe.getMEnd().setRelativePosition(relative.getX(), relative.getY());
				}
				((MLine) pe).getConnectorShape().recalculateShape(((MLine) pe));
			}
		}
	}

	/**
	 * validates a JDOM document against the xml-schema definition specified by
	 * 'xsdFile'
	 * 
	 * @param doc the document to validate
	 */
	public void validateDocument(Document doc) throws ConverterException {
		ClassLoader cl = Pathway.class.getClassLoader();
		InputStream is = cl.getResourceAsStream(xsdFile);
		if (is != null) {
			Schema schema;
			try {
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				StreamSource ss = new StreamSource(is);
				schema = factory.newSchema(ss);
				ValidatorHandler vh = schema.newValidatorHandler();
				SAXOutputter so = new SAXOutputter(vh);
				so.output(doc);
				// If no errors occur, the file is valid according to the gpml xml schema
				// definition
				Logger.log
						.info("Document is valid according to the xml schema definition '" + xsdFile.toString() + "'");
			} catch (SAXException se) {
				Logger.log.error("Could not parse the xml-schema definition", se);
				throw new ConverterException(se);
			} catch (JDOMException je) {
				Logger.log.error("Document is invalid according to the xml-schema definition!: " + je.getMessage(), je);
				XMLOutputter xmlcode = new XMLOutputter(Format.getPrettyFormat());

				Logger.log.error("The invalid XML code:\n" + xmlcode.outputString(doc));
				throw new ConverterException(je);
			}
		} else {
			Logger.log.error("Document is not validated because the xml schema definition '" + xsdFile
					+ "' could not be found in classpath");
			throw new ConverterException("Document is not validated because the xml schema definition '" + xsdFile
					+ "' could not be found in classpath");
		}
	}

}
