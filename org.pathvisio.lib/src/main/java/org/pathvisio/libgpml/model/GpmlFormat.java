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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.bridgedb.bio.DataSourceTxt;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.pathvisio.libgpml.debug.*;
import org.pathvisio.libgpml.io.AbstractPathwayModelFormat;
import org.pathvisio.libgpml.io.ConverterException;
import org.pathvisio.libgpml.util.RootElementFinder;
import org.xml.sax.InputSource;


/**
 * class responsible for interaction with Gpml format. Contains all
 * gpml-specific constants, and should be the only class (apart from svgFormat)
 * that needs to import jdom
 */
public class GpmlFormat extends AbstractPathwayModelFormat {
	static private final GPML2021Writer CURRENT = GPML2021Writer.GPML2021WRITER;
	static private final GPML2013aWriter PREVIOUS = GPML2013aWriter.GPML2013aWRITER;

	/**
	 * Initialize Xref data source text. 
	 */
	static {
		DataSourceTxt.init();
	}

	public PathwayModel doImport(File file) throws ConverterException {
		PathwayModel pathwayModel = new PathwayModel();
		readFromXml(pathwayModel, file, true); // TODO validate always true here?
//		pathwayModel.clearChangedFlag();
		return pathwayModel;
	}

	public void doExport(File file, PathwayModel pathwayModel) throws ConverterException {
		writeToXml(pathwayModel, file, true); // TODO validate always true here?
	}

	/**
	 *
	 */
	public String[] getExtensions() {
		return new String[] { "gpml", "xml" };
	}

	/**
	 *
	 */
	public String getName() {
		return "GPML file";
	}

	/**
	 * @param data
	 * @return
	 * @throws ConverterException
	 */
	public static Document createJdom(PathwayModel data) throws ConverterException {
		return CURRENT.createJdom(data);
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
	 * @param pathwayModel the pathway model.
	 * @param file         the file to which the JDOM document should be saved
	 * @param validate     if true, validate the dom structure before writing to
	 *                     file. If there is a validation error, or the xsd is not
	 *                     in the classpath, an exception will be thrown.
	 */
	static public void writeToXml(PathwayModel pathwayModel, File file, boolean validate) throws ConverterException {
		CURRENT.writeToXml(pathwayModel, file, validate);
	}

	/**
	 * @param pathwayModel the pathway model.
	 * @param out          the file to which the JDOM document should be saved
	 * @param validate     if true, validate the dom structure before writing to
	 *                     file. If there is a validation error, or the xsd is not
	 *                     in the classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	static public void writeToXml(PathwayModel pathwayModel, OutputStream out, boolean validate)
			throws ConverterException {
		CURRENT.writeToXml(pathwayModel, out, validate);
	}

	/**
	 * Read the JDOM document from the file specified
	 * 
	 * @param pathwayModel the pathway model.
	 * @param file         the file from which the JDOM document should be read.
	 * @param validate     if true, validate the dom structure during/after reading.
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
	 * Read the JDOM document from the string specified
	 * 
	 * @param pathwayModel the pathway model.
	 * @param str          the file from which the JDOM document should be read.
	 * @param validate     if true, validate the dom structure during/after reading.
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

	/**
	 * Read the JDOM document from the string specified
	 * 
	 * @param pathwayModel the pathway model.
	 * @param in           the file from which the JDOM document should be read.
	 * @param validate     if true, validate the dom structure during/after reading.
	 * @throws ConverterException
	 */
	static public void readFromXml(PathwayModel pathwayModel, InputStream in, boolean validate)
			throws ConverterException {
		readFromXmlImpl(pathwayModel, new InputSource(in), validate);
	}

	/**
	 * Read the JDOM document from the string specified
	 * 
	 * @param pathwayModel the pathway model.
	 * @param in           the file from which the JDOM document should be read.
	 * @param validate     if true, validate the dom structure during/after reading.
	 * @throws ConverterException
	 */
	static public void readFromXml(PathwayModel pathwayModel, Reader in, boolean validate) throws ConverterException {
		readFromXmlImpl(pathwayModel, new InputSource(in), validate);
	}

	/**
	 * Returns GPML reader given namespace.
	 * 
	 * @param ns the given namespace.
	 * @return the correct GPML reader for the given namespace.
	 */
	public static GpmlFormatReader getReaderForNamespace(Namespace ns) {
		GpmlFormatReader[] formats = new GpmlFormatReader[] { GPML2021Reader.GPML2021READER,
				GPML2013aReader.GPML2013aREADER };
		for (GpmlFormatReader format : formats) {
			if (ns.equals(format.getGpmlNamespace())) {
				return format;
			}
		}
		return null;
	}

	/**
	 * Reads a pathway model from given input source.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param is           the file from which the JDOM document should be read.
	 * @param validate     if true, validate the dom structure during/after reading.
	 * @return the read pathway model.
	 * @throws ConverterException
	 */
	private static PathwayModel readFromXmlImpl(PathwayModel pathwayModel, InputSource is, boolean validate)
			throws ConverterException {
//
//		String schemaFile = CURRENT.getSchemaFile();
//		URL url = Thread.currentThread().getContextClassLoader().getResource(schemaFile);
//		File xsdFile = new File(url.getPath());

		try {
//			XMLReaderJDOMFactory schemafactory = new XMLReaderXSDFactory(xsdFile); // schema

			SAXBuilder builder = new SAXBuilder();

//			/* if validate by schema */
//			if (validate) {
//				builder = new SAXBuilder(schemafactory);
//				System.out.println("Validated with schema: " + schemaFile);
//			}
			Document doc = builder.build(is);
			Element root = doc.getRootElement();

			Namespace ns = root.getNamespace();
			GpmlFormatReader format = getReaderForNamespace(ns);
			if (format == null) {
				throw new ConverterException("This file looks like a pathwayModel, " + "but the namespace " + ns
						+ " was not recognized. This application might be out of date.");
			}
			Logger.log.info("Recognized format " + ns);
			if (validate) {
				format.validateDocument(doc);
				Logger.log.trace("Validated with schema: " + format.getSchemaFile());
			}
			Logger.log.trace("Copy map elements");
			format.readFromRoot(pathwayModel, root);
			System.out.println("Read pathway model successfully from gpml file");
		} catch (JDOMException e) {
			throw new ConverterException(e);
		} catch (IOException e) {
			throw new ConverterException(e);
		} catch (Exception e) {
			throw new ConverterException(e); // TODO e.printStackTrace()?
		}
		return pathwayModel;// TODO do we want to return pathway or not?
	}

	private static PathwayModel readFromXmlImplNew(PathwayModel pathwayModel, InputSource is, boolean validate)
			throws ConverterException {

		try {
			InputStream in = new BufferedInputStream(is.getByteStream());
			in.mark(0);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			reader.readLine();
			String schema = reader.readLine();
			in.reset();

			SAXBuilder builder = new SAXBuilder();

//			/* if validate by schema */
//			if (validate) {
//				builder = new SAXBuilder(schemafactory);
//				System.out.println("Validated with schema: " + schemaFile);
//			}
			Document doc = builder.build(in);//new ByteArrayInputStream(byteArray)
			Element root = doc.getRootElement();

			Namespace ns = root.getNamespace();
			GpmlFormatReader format = getReaderForNamespace(ns);
			if (format == null) {
				throw new ConverterException("This file looks like a pathwayModel, " + "but the namespace " + ns
						+ " was not recognized. This application might be out of date.");
			}
			Logger.log.info("Recognized format " + ns);
			if (validate) {
				format.validateDocument(doc);
				Logger.log.trace("Validated with schema: " + format.getSchemaFile());
			}
			Logger.log.trace("Copy map elements");
			format.readFromRoot(pathwayModel, root);
			System.out.println("Read pathway model successfully from gpml file");
		} catch (JDOMException e) {
			throw new ConverterException(e);
		} catch (IOException e) {
			throw new ConverterException(e);
		} catch (Exception e) {
			throw new ConverterException(e); // TODO e.printStackTrace()?
		}
		return pathwayModel;// TODO do we want to return pathway or not?
	}

	/**
	 * @param f the file.
	 */
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
