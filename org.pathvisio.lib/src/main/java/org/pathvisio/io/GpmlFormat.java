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

import java.io.File; //Class that will represent a system file name
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException; //Triggered when an I/O error occurs
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.bridgedb.bio.DataSourceTxt;
import org.jdom2.Document; //Represents the XML document and contains useful methods
import org.jdom2.Element; //Represents XML elements and contains useful methods
import org.jdom2.JDOMException; //Top level JDOM exception
import org.jdom2.Namespace;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder; //Creates a JDOM document parsed using SAX Simple API for XML
// import org.jdom2.input.sax.XMLReaders;
import org.pathvisio.debug.Logger;
import org.pathvisio.util.RootElementFinder;
import org.pathvisio.model.*;
import org.xml.sax.InputSource;

/**
 * Class responsible for interaction with Gpml format. Contains all
 * gpml-specific constants, and should be the only class (apart from svgFormat)
 * that needs to import jdom.
 */
public class GpmlFormat extends AbstractPathwayFormat {

	static private final GpmlFormat2021 CURRENT = GpmlFormat2021.GPML_2021;
	static private final GpmlFormat2013a PREVIOUS = GpmlFormat2013a.GPML_2013A; // TODO
	public static final Namespace RDF = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	public static final Namespace RDFS = Namespace.getNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	public static final Namespace BIOPAX = Namespace.getNamespace("bp",
			"http://www.biopax.org/release/biopax-level3.owl#");
	public static final Namespace OWL = Namespace.getNamespace("owl", "http://www.w3.org/2002/07/owl#");

	/**
	 * Initializes data sources for BridgeDb 2.0. 
	 */
	static {
		DataSourceTxt.init();
	}

	/**
	 * Imports pathway from file.
	 * 
	 * @param file the file.
	 * @return pathwayModel the pathway model.
	 * @throws Converter Exception
	 */
	public PathwayModel doImport(File file) throws ConverterException {
		PathwayModel pathwayModel = new PathwayModel();
		readFromXml(pathwayModel, file, true);
//		pathway.clearChangedFlag();
		return pathwayModel;
	}

	/**
	 * Exports pathway to gpml file.
	 * 
	 * @param file the gpml file.
	 * @return pathway the Pathway.
	 * @throws ConverterException.
	 */
	public void doExport(File file, PathwayModel pathway) throws ConverterException {
		writeToXml(pathway, file, true);
	}

	/**
	 * @return new string array.
	 */
	public String[] getExtensions() {
		return new String[] { "gpml", "xml" };
	}

	/**
	 * @return string "GPML file".
	 */
	public String getName() {
		return "GPML file";
	}

	/**
	 * @return data the pathway.
	 * @throws ConverterException.
	 */
	public static Document createJdom(PathwayModel pathwayModel) throws ConverterException {
		return CURRENT.createJdom(pathwayModel);
	}

	static public Element createJdomElement(PathwayElement o) throws ConverterException {
		return CURRENT.createJdomElement(o);
	}

	public static PathwayElement mapElement(Element e) throws ConverterException {
		return CURRENT.mapElement(e);
	}

	/**
	 * Writes the JDOM document to the file specified
	 * 
	 * @param pathway
	 * @param file     the file to which the JDOM document should be saved.
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	static public void writeToXml(PathwayModel pathway, File file, boolean validate) throws ConverterException {
		CURRENT.writeToXml(pathway, file, validate);
	}

	/**
	 * Writes the JDOM document to the file specified
	 * 
	 * @param pwy
	 * @param out
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	static public void writeToXml(Pathway pwy, OutputStream out, boolean validate) throws ConverterException {
		CURRENT.writeToXml(pwy, out, validate);
	}

	/**
	 * Read the JDOM document from the file specified
	 * 
	 * @param pwy
	 * @param file     the file from which the JDOM document should be read.
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	static public void readFromXml(PathwayModel pwy, File file, boolean validate) throws ConverterException {
		InputStream inf;
		try {
			inf = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new ConverterException(e);
		}
		readFromXmlImpl(pwy, new InputSource(inf), validate);
	}

	/**
	 * Read the JDOM document from the file specified
	 * 
	 * @param pwy
	 * @param in
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	static public void readFromXml(PathwayModel pwy, InputStream in, boolean validate) throws ConverterException {
		readFromXmlImpl(pwy, new InputSource(in), validate);
	}

	/**
	 * Read the JDOM document from the file specified
	 * 
	 * @param pwy
	 * @param in
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	static public void readFromXml(PathwayModel pwy, Reader in, boolean validate) throws ConverterException {
		readFromXmlImpl(pwy, new InputSource(in), validate);
	}

	/**
	 * Gets GpmlFormatReader.
	 * 
	 * @param ns the Namespace.
	 * @return format the gpml reader format.
	 */
	public static GpmlFormatReader getReaderForNamespace(Namespace ns) {
		GpmlFormatReader[] formats = new GpmlFormatReader[] { GpmlFormat200X.GPML_2007, GpmlFormat200X.GPML_2008A,
				GpmlFormat2010a.GPML_2010A, GpmlFormat2013a.GPML_2013A };
		for (GpmlFormatReader format : formats) {
			if (ns.equals(format.getGpmlNamespace())) {
				return format;
			}
		}
		return null;
	}

	/**
	 * Read the JDOM document from the file specified
	 * 
	 * @param pwy
	 * @param in
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	private static void readFromXmlImpl(PathwayModel pwy, InputSource in, boolean validate) throws ConverterException {
		// Start XML processing

		SAXBuilder builder = new SAXBuilder(false); // SAXBuilder(XMLReaders.NONVALIDATING)?
		// no validation when reading the xml file
		// try to read the file; if an error occurs, catch the exception and print
		// feedback
		try {
			Logger.log.trace("Build JDOM tree");
			// build JDOM tree
			Document doc = builder.build(in);

			// Copy the pathway information to a VPathway
			Element root = doc.getRootElement();
			if (!root.getName().equals("Pathway")) {
				throw new ConverterException("Not a Pathway file");
			}

			Namespace ns = root.getNamespace();
			GpmlFormatReader format = getReaderForNamespace(ns);
			if (format == null) {
				throw new ConverterException("This file looks like a pathway, " + "but the namespace " + ns
						+ " was not recognized. This application might be out of date.");
			}
			Logger.log.info("Recognized format " + ns);

			Logger.log.trace("Start Validation");
			if (validate)
				format.validateDocument(doc);
			Logger.log.trace("Copy map elements");

			format.readFromRoot(root, pwy);
		} catch (JDOMParseException pe) {
			throw new ConverterException(pe);
		} catch (JDOMException e) {
			throw new ConverterException(e);
		} catch (IOException e) {
			throw new ConverterException(e);
		} catch (NullPointerException e) {
			throw new ConverterException(e);
		} catch (IllegalArgumentException e) {
			throw new ConverterException(e);
		} catch (Exception e) { // Make all types of exceptions a ConverterException
			throw new ConverterException(e);
		}
	}

	@Override
	public boolean isCorrectType(File f) {
		String uri;
		try {
			uri = "" + RootElementFinder.getRootUri(f);
			return uri.startsWith("http://genmapp.org/");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void doExport(File file, PathwayModel pathway, int zoom) throws ConverterException {
		// TODO Auto-generated method stub

	}

}
