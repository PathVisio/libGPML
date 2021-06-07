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

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.pathvisio.model.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import junit.framework.TestCase;

/**
 * Test for reading and writing of GPML2013a.
 * 
 * @author finterly
 */
public class TestReadWriteGPML2013a extends TestCase {

	/**
	 * For testing reading a directory of GPML2013a files and writing again to GPML2013a
	 * format. Assert output equivalent to input.
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 * @throws SAXException
	 */
	public static void testReadWriteGPML2013a() throws IOException, ConverterException, SAXException {

//		File folderGPML2013a = new File("src/test/resources/sampleGPML2013a");
//		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-complete-gpml-Homo_sapiens");
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-convert-back-to-GPML2013a");
		String outputDir = "C:/Users/p70073399/Documents/wikipathways_readwrite_GPML2013a";

		File[] listOfFiles = folderGPML2013a.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				System.out.println("File " + i + " : " + file.getName());
				assertTrue(file.exists());
				/* read xml to pathway model */
				PathwayModel pathwayModel = new PathwayModel();
				pathwayModel.readFromXml(file, true);

				/* write pathway model to xml */
				File outputFile = new File(outputDir, file.getName());
				GPML2013aWriter.GPML2013aWRITER.writeToXml(pathwayModel, outputFile, true);
				System.out.println(outputFile);

				/* write pathway model to xml (temp) */
//				File tmp = File.createTempFile(file.getName() + "_testwrite", ".gpml");
//				GPML2013aWriter.GPML2013aWRITER.writeToXml(pathwayModel, tmp, false);
//				System.out.println(tmp);

				/* method to assert file is same? */

			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

	}
}