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

import java.io.InputStream;
import java.util.ArrayList;

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.SAXOutputter;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.io.ConverterException;
import org.xml.sax.SAXException;

/**
 * Read / write GPML files. Base implementation for different GpmlFormat
 * versions. Code that is shared between multiple versions is located here.
 */
public abstract class GPML2021FormatAbstract {

	private final String xsdFile;
	private final Namespace nsGPML;

	/**
	 * Constructor for GPML2021Format Abstract.
	 * 
	 * @param xsdFile the schema file.
	 * @param nsGPML  the GPML namespace.
	 */
	protected GPML2021FormatAbstract(String xsdFile, Namespace nsGPML) {
		this.xsdFile = xsdFile;
		this.nsGPML = nsGPML;
	}

	/**
	 * Returns the GPML schema file.
	 * 
	 * @return xsdFile the schema file.
	 */
	public String getSchemaFile() {
		return xsdFile;
	}

	/**
	 * Returns the GPML namespace.
	 * 
	 * @return nsGPML the GPML namespace.
	 */
	public Namespace getGpmlNamespace() {
		return nsGPML;
	}

	/**
	 * Default values necessary for reading (when validation off)
	 */
	public final static String BACKGROUNDCOLOR_DEFAULT = "ffffff";
	public final static String GROUPTYPE_DEFAULT = "Group";
	public final static String DATANODETYPE_DEFAULT = "Undefined";
	public final static String STATETYPE_DEFAULT = "Undefined";
	public final static String ARROWHEAD_DEFAULT = "Undirected";
	public final static String ANCHORSHAPETYPE_DEFAULT = "Square";
	public final static String ANNOTATIONTYPE_DEFAULT = "Undefined";
	// font properties
	public final static String TEXTCOLOR_DEFAULT = "000000";
	public final static String FONTNAME_DEFAULT = "Arial";
	public final static String FONTWEIGHT_DEFAULT = "Normal";
	public final static String FONTSTYLE_DEFAULT = "Normal";
	public final static String FONTDECORATION_DEFAULT = "Normal";
	public final static String FONTSTRIKETHRU_DEFAULT = "Normal";
	public final static String FONTSIZE_DEFAULT = "12";
	public final static String HALIGN_DEFAULT = "Center";
	public final static String VALIGN_DEFAULT = "Middle";
	// shape style properties
	public final static String BORDERCOLOR_DEFAULT = "000000";
	public final static String BORDERSTYLE_DEFAULT = "Solid";
	public final static String BORDERWIDTH_DEFAULT = "1.0";
	public final static String FILLCOLOR_DEFAULT = "ffffff";
	public final static String SHAPETYPE_DEFAULT = "Rectangle";
	// line style properties
	public final static String LINECOLOR_DEFAULT = "000000";
	public final static String LINESTYLE_DEFAULT = "Solid";
	public final static String LINEWIDTH_DEFAULT = "1.0";
	public final static String CONNECTORTYPE_DEFAULT = "Straight";
	
	/**
	 * Removes group from pathwayModel if empty. Check executed after reading and
	 * before writing.
	 * 
	 * @param pathwayModel the pathway model.
	 * @throws ConverterException
	 */
	protected void removeEmptyGroups(PathwayModel pathwayModel) throws ConverterException {
		List<Group> groups = pathwayModel.getGroups();
		List<Group> groupsToRemove = new ArrayList<Group>();
		for (Group group : groups) {
			if (group.getPathwayElements().isEmpty()) {
				groupsToRemove.add(group);
			}
		}
		for (Group groupToRemove : groupsToRemove) {
			Logger.log.trace("Warning: Removed empty group " + groupToRemove.getElementId());
			pathwayModel.removeGroup(groupToRemove);
		}
	}

	/**
	 * validates a JDOM document against the xml-schema definition specified by
	 * 'xsdFile'
	 * 
	 * @param doc the document to validate
	 */
	public void validateDocument(Document doc) throws ConverterException {
		ClassLoader cl = PathwayModel.class.getClassLoader();
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
