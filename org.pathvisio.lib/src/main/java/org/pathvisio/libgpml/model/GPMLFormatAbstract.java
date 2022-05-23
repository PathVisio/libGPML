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
 * Abstract class for GPML format. Contains common properties and methods used
 * in reading and writing GPML.
 *
 * @author finterly
 */
public abstract class GpmlFormatAbstract {

	/**
	 * The namespace
	 */
	private final Namespace nsGPML;

	/**
	 * The schema file
	 */
	private final String xsdFile;

	/**
	 * Constructor for GPML2013aFormat Abstract.
	 *
	 * @param xsdFile the schema file.
	 * @param nsGPML  the GPML namespace.
	 */
	protected GpmlFormatAbstract(String xsdFile, Namespace nsGPML) {
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

	// ================================================================================
	// Static Variables
	// ================================================================================
	/**
	 * In GPML2013a, specific {@link Namespace} are defined for Biopax elements.
	 */
	public static final Namespace RDF_NAMESPACE = Namespace.getNamespace("rdf",
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	public static final Namespace RDFS_NAMESPACE = Namespace.getNamespace("rdfs",
			"http://www.w3.org/2000/01/rdf-schema#");
	public static final Namespace BIOPAX_NAMESPACE = Namespace.getNamespace("bp",
			"http://www.biopax.org/release/biopax-level3.owl#");
	public static final Namespace OWL_NAMESPACE = Namespace.getNamespace("owl", "http://www.w3.org/2002/07/owl#");
	public final static String RDF_STRING = "http://www.w3.org/2001/XMLSchema#string";

	// ================================================================================
	// Common Methods
	// ================================================================================
	/**
	 * Removes group from pathwayModel if empty. If group valid and not empty,
	 * update group dimensions of Groups.
	 * 
	 * NB: Executed after reading and before writing.
	 *
	 * @param pathwayModel the pathway model.
	 * @throws ConverterException
	 */
	protected void updateGroups(PathwayModel pathwayModel) throws ConverterException {
		List<Group> groups = pathwayModel.getGroups();
		List<Group> groupsToRemove = new ArrayList<Group>();
		for (Group group : groups) {
			if (group.getPathwayElements().isEmpty()) {
				groupsToRemove.add(group);
			} else {
				group.updateDimensions();
			}
		}
		for (Group groupToRemove : groupsToRemove) {
			Logger.log.trace("Warning: Removed empty group " + groupToRemove.getElementId());
			pathwayModel.removeGroup(groupToRemove);
		}
	}

	/**
	 * Refreshes line elements.
	 * 
	 * @param pathwayModel the pathway model.
	 * @throws ConverterException
	 */
	protected static void refreshLineElements(PathwayModel pathwayModel) throws ConverterException {
		for (LineElement pe : pathwayModel.getLineElements()) {
			pe.getConnectorShape().recalculateShape(pe);
		}
	}

	// ================================================================================
	// Validate Method
	// ================================================================================
	/**
	 * Validates a JDOM document against the xml-schema definition specified by
	 * 'xsdFile.'
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
				// if no errors, the file is valid according to the gpml xml schema
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