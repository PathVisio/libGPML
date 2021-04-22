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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.bridgedb.bio.DataSourceTxt;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.pathvisio.debug.*;
import org.pathvisio.model.*;
import org.pathvisio.util.RootElementFinder;
import org.pathvisio.model.PathwayModel;
import org.xml.sax.InputSource;

/**
 * class responsible for interaction with Gpml format. Contains all
 * gpml-specific constants, and should be the only class (apart from svgFormat)
 * that needs to import jdom
 */
public class GpmlFormat extends AbstractPathwayFormat {
	static private final GPML2021Writer CURRENT = GPML2021Writer.GPML2021WRITER;
	static private final GPML2013aWriter PREVIOUS = GPML2013aWriter.GPML2013aWRITER;

//	static private final GpmlFormat2013a PREVIOUS = GpmlFormat2013a.GPML_2013A;
	public static final Namespace RDF = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	public static final Namespace RDFS = Namespace.getNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	public static final Namespace BIOPAX = Namespace.getNamespace("bp",
			"http://www.biopax.org/release/biopax-level3.owl#");
	public static final Namespace OWL = Namespace.getNamespace("owl", "http://www.w3.org/2002/07/owl#");

	static {
		DataSourceTxt.init();
	}

	public PathwayModel doImport(File file) throws ConverterException {
		PathwayModel pathwayModel = new PathwayModel();
		readFromXml(pathwayModel, file, true); //TODO validate always true here? 
//		pathwayModel.clearChangedFlag();
		return pathwayModel;
	}

	public void doExport(File file, PathwayModel pathwayModel) throws ConverterException {
		writeToXml(pathwayModel, file, true); //TODO validate always true here? 
	}

	public String[] getExtensions() {
		return new String[] { "gpml", "xml" };
	}

	public String getName() {
		return "GPML file";
	}

	public static Document createJdom(PathwayModel data) throws ConverterException {
		return PREVIOUS.createJdom(data);
	}

//	static public Element createJdomElement(PathwayElement o) throws ConverterException
//	{
//		return CURRENT.createJdomElement(o);
//	}
//
//	public static PathwayElement mapElement(Element e) throws ConverterException
//	{
//		return CURRENT.mapElement(e);
//	}

	/**
	 * Writes the JDOM document to the file specified
	 * 
	 * @param file     the file to which the JDOM document should be saved
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 */
	static public void writeToXml(PathwayModel pwy, File file, boolean validate) throws ConverterException {
		PREVIOUS.writeToXml(pwy, file, validate);
	}

	static public void writeToXml(PathwayModel pwy, OutputStream out, boolean validate) throws ConverterException {
		PREVIOUS.writeToXml(pwy, out, validate);
	}

	/**
	 * Read the JDOM document from the file specified
	 * 
	 * @param file the file from which the JDOM document should be read.
	 * @throws ConverterException
	 */
	static public PathwayModel readFromXml(PathwayModel pathwayModel, File file, boolean validate)
			throws ConverterException {
		InputStream in;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new ConverterException(e);
		}
		return readFromXmlImpl(pathwayModel, new InputSource(in), validate);
	}

	/**
	 * Read the JDOM document from the file specified
	 * 
	 * @param s      the string input.
	 * @param string the file from which the JDOM document should be read.
	 * @throws ConverterException
	 */
	static public PathwayModel readFromXml(PathwayModel pathwayModel, String str, boolean validate)
			throws ConverterException {
		if (str == null)
			return null;
		InputStream in;
		try {
			in = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));// TODO does this work?
		} catch (Exception e) {
			throw new ConverterException(e);
		}
		return readFromXmlImpl(pathwayModel, new InputSource(in), validate);
	}

//	static public void readFromXml(PathwayModel pathwayModel, File file, boolean validate) throws ConverterException {
//		InputStream inf;
//		try {
//			inf = new FileInputStream(file);
//		} catch (FileNotFoundException e) {
//			throw new ConverterException(e);
//		}
//		readFromXmlImpl(pathwayModel, new InputSource(inf), validate);
//	}
//
//	
	static public void readFromXml(PathwayModel pathwayModel, InputStream in, boolean validate)
			throws ConverterException {
		readFromXmlImpl(pathwayModel, new InputSource(in), validate);
	}

	static public void readFromXml(PathwayModel pathwayModel, Reader in, boolean validate) throws ConverterException {
		readFromXmlImpl(pathwayModel, new InputSource(in), validate);
	}

	public static GpmlFormatReader getReaderForNamespace(Namespace ns) {
		GpmlFormatReader[] formats = new GpmlFormatReader[] { GPML2021Reader.GPML2021READER, GPML2013aReader.GPML2013aREADER };
		for (GpmlFormatReader format : formats) {
			if (ns.equals(format.getGpmlNamespace())) {
				return format;
			}
		}
		return null;
	}
	

	private static PathwayModel readFromXmlImpl(PathwayModel pathwayModel, InputSource is, boolean validate)
			throws ConverterException {
		
		//TODO fix schema file etc...

		URL url = Thread.currentThread().getContextClassLoader().getResource(CURRENT.getSchemaFile());
		File xsdFile = new File(url.getPath());

		try {
			XMLReaderJDOMFactory schemafactory = new XMLReaderXSDFactory(xsdFile); // schema

			SAXBuilder builder = new SAXBuilder();
			
			/* if validate by schema*/
			if (validate)
				builder = new SAXBuilder(schemafactory);
			Document doc = builder.build(is);

			System.out.println("file validated");

			Element root = doc.getRootElement();

			Namespace ns = root.getNamespace();
			GpmlFormatReader format = getReaderForNamespace(ns);
			if (format == null) {
				throw new ConverterException("This file looks like a pathwayModel, " + "but the namespace " + ns
						+ " was not recognized. This application might be out of date.");
			}
			Logger.log.info("Recognized format " + ns);
			Logger.log.trace("Start Validation");
			if (validate)
				format.validateDocument(doc);
			Logger.log.trace("Copy map elements");
			format.readFromRoot(pathwayModel, root);
		} catch (JDOMException e) {
			throw new ConverterException(e);
		} catch (IOException e) {
			throw new ConverterException(e);
		} catch (Exception e) {
			throw new ConverterException(e); // TODO e.printStackTrace()?
		}
		return pathwayModel;// TODO do we want to return pathway or not?
	}

//	private static void readFromXmlImpl(PathwayModel pathwayModel, InputSource is, boolean validate)
//			throws ConverterException {
//		// Start XML processing
//
//		SAXBuilder builder = new SAXBuilder(false); // no validation when reading the xml file
//		// try to read the file; if an error occurs, catch the exception and print
//		// feedback
//		try {
//			Logger.log.trace("Build JDOM tree");
//			// build JDOM tree
//			Document doc = builder.build(is);
//
//			// Copy the pathwayModel information to a VPathway
//			Element root = doc.getRootElement();
//			if (!root.getName().equals("Pathway")) {
//				throw new ConverterException("Not a PathwayModel file");
//			}
//
//			Namespace ns = root.getNamespace();
//			GpmlFormatReader format = getReaderForNamespace(ns);
//			if (format == null) {
//				throw new ConverterException("This file looks like a pathwayModel, " + "but the namespace " + ns
//						+ " was not recognized. This application might be out of date.");
//			}
//			Logger.log.info("Recognized format " + ns);
//
//			Logger.log.trace("Start Validation");
//			if (validate)
//				format.validateDocument(doc);
//			Logger.log.trace("Copy map elements");
//
//			format.readFromRoot(pathwayModel, root);
//		} catch (JDOMParseException pe) {
//			throw new ConverterException(pe);
//		} catch (JDOMException e) {
//			throw new ConverterException(e);
//		} catch (IOException e) {
//			throw new ConverterException(e);
//		} catch (NullPointerException e) {
//			throw new ConverterException(e);
//		} catch (IllegalArgumentException e) {
//			throw new ConverterException(e);
//		} catch (Exception e) { // Make all types of exceptions a ConverterException
//			throw new ConverterException(e);
//		}
//	}

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

//	@Override  TODO 
	public void doExport(File file, PathwayModel pathwayModel, int zoom) throws ConverterException {
		// TODO Auto-generated method stub

	}

}
