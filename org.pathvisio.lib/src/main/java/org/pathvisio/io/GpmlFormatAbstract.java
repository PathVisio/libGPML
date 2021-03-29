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
import org.pathvisio.biopax.BiopaxElement;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.*;
import org.xml.sax.SAXException;

/**
 * This abstract class is for the reading and writing of GPML files. Base
 * implementation for different GpmlFormat versions. Code that is shared between
 * multiple versions is located here.
 */
public abstract class GpmlFormatAbstract {
	protected GpmlFormatAbstract(String xsdFile, Namespace nsGPML) {
		this.xsdFile = xsdFile;
		this.nsGPML = nsGPML;
	}

	private final Namespace nsGPML;
	private final String xsdFile;

	/**
	 * getAttributeInfo is a {@link Map} collection that contains {@link String} as
	 * key and {@link AttributeInfo} as value.
	 */
	protected abstract Map<String, AttributeInfo> getAttributeInfo();

	/**
	 * @return nsGPML the gpml namespace.
	 */
	public Namespace getGpmlNamespace() {
		return nsGPML;
	}

	/**
	 * Name of resource containing the gpml schema definition.
	 */
	protected static class AttributeInfo {
		/**
		 * xsd validated type. Note that in the current implementation we don't do
		 * anything with restrictions, only with the base type.
		 */
		public String schemaType;

		/**
		 * default value for the attribute
		 */
		public String def; // default

		/**
		 * use of the attribute: can be "required" or "optional"
		 */
		public String use;

		/**
		 * Creates an object containing the gpml schema definition of a given attribute.
		 * 
		 * @param aSchemaType the xsd validated type of the attribute.
		 * @param aDef        the default value for the attribute.
		 * @param aUse        the use of the attribute.
		 */
		AttributeInfo(String aSchemaType, String aDef, String aUse) {
			schemaType = aSchemaType;
			def = aDef;
			use = aUse;
		}
	}

//	/**
//	 * Returns true if given string value and default value are equal.
//	 * 
//	 * @param def   the default string.
//	 * @param value the given string.
//	 * @return true if the specified arguments are equal, or both null.
//	 */
//	private boolean isEqualsString(String def, String value) {
//		return ((def == null && value == null) || (def != null && def.equals(value))
//				|| (def == null && value != null && value.equals("")));
//	}
//
//	/**
//	 * Returns true if given string value and default value are numerically equal.
//	 * 
//	 * @param def   the string for default number value.
//	 * @param value the string for given number value.
//	 * @return true if absolute value of difference between def and value is less
//	 *         than 1e-6, and false otherwise.
//	 */
//	private boolean isEqualsNumber(String def, String value) {
//		if (def != null && value != null) {
//			Double x = Double.parseDouble(def);
//			Double y = Double.parseDouble(value);
//			if (Math.abs(x - y) < 1e-6)
//				return true;
//		}
//		return false;
//	}
//
//	/**
//	 * Returns true if given value and default value are the same color object.
//	 * 
//	 * @param def   the string for default color object.
//	 * @param value the string for given color object.
//	 * @return
//	 */
//	private boolean isEqualsColor(String def, String value) {
//		if (def != null && value != null) {
//			boolean aTrans = "Transparent".equals(def);
//			boolean bTrans = "Transparent".equals(value);
//			Color a = ColorUtils.stringToColor(def);
//			Color b = ColorUtils.stringToColor(value);
//			return (a.equals(b) && aTrans == bTrans);
//		}
//		return def == null && value == null;
//	}

	/**
	 * The GPML xsd implies a certain ordering for children of the pathway element.
	 * (e.g. DataNode always comes before LineShape, etc.)
	 *
	 * This Comparator can sort jdom Elements so that they are in the correct order
	 * for the xsd.
	 */
	protected static class ByElementName implements Comparator<Element> {
		// hashread for quick lookups during sorting
		private Map<String, Integer> elementOrdering;

		// correctly ordered list of tag names, which are loaded into the hashread in
		// the constructor.
		private final String[] elements = new String[] { "Comment", "BiopaxRef", "Graphics", "DataNode", "State",
				"Interaction", "Line", "GraphicalLine", "Label", "Shape", "Group", "InfoBox", "Legend", "Biopax"

		};

		/*
		 * Constructor creates Comparator ByElementName which sorts elements by the
		 * given order.
		 * 
		 */
		public ByElementName() {
			elementOrdering = new HashMap<String, Integer>();
			for (int i = 0; i < elements.length; ++i) {
				elementOrdering.put(elements[i], new Integer(i)); // Autoboxing/Unboxing?
			}
		}

		/*
		 * As a comparison measure, returns difference of index of element names of a
		 * and b in elements array. E.g: Comment -> index 1 in elements array Graphics
		 * -> index 2 in elements array. If a.getName() is Comment and b.getName() is
		 * Graphics, returns 1-2 -> -1
		 * 
		 */
		public int compare(Element a, Element b) {
			return ((Integer) elementOrdering.get(a.getName())).intValue()
					- ((Integer) elementOrdering.get(b.getName())).intValue();
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


	
	protected void readGroupRef(PathwayElement o, Element e) {
		String id = e.getAttributeValue("GroupRef");
		if (id != null && !id.equals("")) {
			o.setGroupRef(id);
		}

	}



	protected abstract void readMappInfoDataVariable(Pathway p, Element e) throws ConverterException;

	protected void readMappInfoData(Pathway p, Element e) throws ConverterException {
		p.setTitle(getAttribute("Pathway", "Name", e));
		p.setOrganism(getAttribute("Pathway", "Organism", e));
		p.setSource(getAttribute("Pathway", "Data-Source", e));
		p.setVersion(getAttribute("Pathway", "Version", e));

		// TODO: HANDLE
		p.setAuthor(getAttribute("Pathway", "Author", e));
		p.setMaintainer(getAttribute("Pathway", "Maintainer", e));
		p.setEmail(getAttribute("Pathway", "Email", e));
		p.setLastModified(getAttribute("Pathway", "Last-Modified", e));

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
