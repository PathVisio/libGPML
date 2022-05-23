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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.swing.JOptionPane;

import org.bridgedb.bio.DataSourceTxt;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.io.AbstractPathwayModelFormat;
import org.pathvisio.libgpml.io.ConverterException;
import org.pathvisio.libgpml.util.RootElementFinder;
import org.xml.sax.InputSource;

/**
 * Class responsible for interaction with Gpml format. Contains all
 * gpml-specific constants, and should be the only class (apart from svgFormat)
 * that needs to import jdom.
 * <p>
 * NB:
 * <ol>
 * <li>Static read methods, both current and previous gpml formats can be read.
 * <li>Pathways are saved/written in the current gpml format.
 * <li>Export allows writing to the previous gpml format.
 * </ol>
 *
 * @author unknown, finterly
 */
public class GPMLFormat extends AbstractPathwayModelFormat {

	static public final GPML2021Writer CURRENT = GPML2021Writer.GPML2021WRITER;
	static public final GPML2013aWriter PREVIOUS = GPML2013aWriter.GPML2013aWRITER;

	private GPMLFormatWriter writer;

	// ================================================================================
	// Constructors and Initialize
	// ================================================================================
	/**
	 * Instantiates a GpmlFormat.
	 *
	 * @param writer
	 */
	public GPMLFormat(GPMLFormatWriter writer) {
		this.writer = writer;
	}

	/**
	 * Initializes Xref data source text.
	 */
	static {
		DataSourceTxt.init();
	}

	// ================================================================================
	// Import Methods
	// ================================================================================
	/**
	 *
	 */
	@Override
	public PathwayModel doImport(File file) throws ConverterException {
		PathwayModel pathwayModel = new PathwayModel();
		readFromXml(pathwayModel, file, true); // always validate
		pathwayModel.clearChangedFlag();
		return pathwayModel;
	}

	// ================================================================================
	// Export Methods
	// ================================================================================
	/**
	 *
	 */
	@Override
	public void doExport(File file, PathwayModel pathwayModel) throws ConverterException {
		writeToXml(pathwayModel, file, true); // always validate
	}

	/**
	 *
	 */
	@Override
	public String[] getExtensions() {
		return new String[] { "gpml", "xml" };
	}

	/**
	 *
	 */
	@Override
	public String getName() {
		if (writer instanceof GPML2013aWriter) {
			return "GPML2013a file";
		} else {
			return "GPML file";
		}
	}

	// ================================================================================
	// Write Methods
	// ================================================================================
	/**
	 * @param data
	 * @return
	 * @throws ConverterException
	 */
	public Document createJdom(PathwayModel data) throws ConverterException {
		return writer.createJdom(data);
	}

	/**
	 * Writes the JDOM document to the file specified
	 *
	 * @param pathwayModel the pathway model.
	 * @param file         the file to which the JDOM document should be saved
	 * @param validate     if true, validate the dom structure before writing to
	 *                     file. If there is a validation error, or the xsd is not
	 *                     in the classpath, an exception will be thrown.
	 */
	public void writeToXml(PathwayModel pathwayModel, File file, boolean validate) throws ConverterException {
		writer.writeToXml(pathwayModel, file, validate);
	}

	/**
	 * Writes the pathway model to output stream.
	 * 
	 * @param pathwayModel the pathway model.
	 * @param out          the file to which the JDOM document should be saved
	 * @param validate     if true, validate the dom structure before writing to
	 *                     file. If there is a validation error, or the xsd is not
	 *                     in the classpath, an exception will be thrown.
	 * @throws ConverterException
	 */
	public void writeToXml(PathwayModel pathwayModel, OutputStream out, boolean validate) throws ConverterException {
		writer.writeToXml(pathwayModel, out, validate);
	}

	// ================================================================================
	// Read Methods
	// ================================================================================
	/**
	 * Reads the JDOM document from the file specified
	 *
	 * @param pathwayModel the pathway model.
	 * @param file         the file from which the JDOM document should be read.
	 * @param validate     if true, validate the dom structure during/after reading.
	 * @throws ConverterException
	 */
	static public void readFromXml(PathwayModel pathwayModel, File file, boolean validate) throws ConverterException {
		InputStream in;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new ConverterException(e);
		}
		readFromXmlImpl(pathwayModel, new InputSource(in), validate);
	}

	/**
	 * Reads the JDOM document from the string specified
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
	 * Reads the JDOM document from the string specified
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
	public static GPMLFormatReader getReaderForNamespace(Namespace ns) {
		GPMLFormatReader[] formats = new GPMLFormatReader[] { GPML2021Reader.GPML2021READER,
				GPML2013aReader.GPML2013aREADER };
		for (GPMLFormatReader format : formats) {
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
	 * @throws ConverterException
	 */
	private static void readFromXmlImpl(PathwayModel pathwayModel, InputSource is, boolean validate)
			throws ConverterException {
		// Start XML processing
		SAXBuilder builder = new SAXBuilder(); // no validation when reading the xml file, validation later.
		try {
			Logger.log.trace("Build JDOM tree");
			// build jdom tree
			Document doc = builder.build(is);
			// copy the pathway information to a VPathway
			Element root = doc.getRootElement();
			if (!root.getName().equals("Pathway")) {
				throw new ConverterException("Not a Pathway file");
			}
			// reader
			Namespace ns = root.getNamespace();
			GPMLFormatReader format = getReaderForNamespace(ns);
			if (format == null) {
				throw new ConverterException("This file looks like a pathwayModel, " + "but the namespace " + ns
						+ " was not recognized. This application might be out of date.");
			}
			Logger.log.info("Recognized format " + ns);
			// validation
			if (validate) {
				format.validateDocument(doc);
				Logger.log.trace("Validated with schema: " + format.getSchemaFile());
			}
			Logger.log.trace("Copy map elements");
			format.readFromRoot(pathwayModel, root);
			// warning message if opening older GPML
			if (!(format instanceof GPML2021Reader)) {
				JOptionPane.showMessageDialog(null,
						"This pathway was written in an older Gpml version.\nSave will automatically update it to GPML2021.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
		} catch (JDOMException e) {
			throw new ConverterException(e);
		} catch (IOException e) {
			throw new ConverterException(e);
		} catch (Exception e) {
			throw new ConverterException(e);
		}
	}

	/**
	 * Returns true if file type is correct.
	 * 
	 * @param f the file.
	 * @return true if file type correct.
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

	@Override
	public void doExport(File file, PathwayModel pathwayModel, int zoom) throws ConverterException {
		// Auto-generated method stub
	}

}
