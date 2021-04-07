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
package org.pathvisio.io.gpml2021;

import java.io.InputStream;

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
import org.pathvisio.debug.Logger;
import org.pathvisio.io.ConverterException;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.xml.sax.SAXException;


/**
 * This abstract class is for the reading and writing of GPML files. Base
 * implementation for different GpmlFormat versions. Code that is shared between
 * multiple versions is located here.
 */
public abstract class GPML2021FormatAbstract {
	
	
	protected GPML2021FormatAbstract(String xsdFile, Namespace nsGPML) {
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
	 * OLD validation method TODO validates a JDOM document against the xml-schema
	 * definition specified by 'xsdFile'
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
				/* if no errors occur, this gpml file is valid and conforms to gpml schema */
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
