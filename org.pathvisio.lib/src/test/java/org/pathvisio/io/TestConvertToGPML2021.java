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
import java.io.InputStream;
import java.net.URL;
import org.pathvisio.model.*;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class TestConvertToGPML2021 extends TestCase {

	/**
	 * For testing conversion GPML2013a to newer GPML2021. Reading a directory of
	 * GPML2013a files and writing to GPML2021 format. Assert output equivalent to
	 * input.
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 * @throws SAXException
	 */
	public static void testConvertToGPML2021() throws IOException, ConverterException {
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-complete-gpml-Homo_sapiens");
		String outputDir = "C:/Users/p70073399/Documents/wikipathways-convert-to-GPML2021";

		File[] listOfFiles = folderGPML2013a.listFiles();

		for (int i = 1; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				System.out.println("File " + i + " : " + file.getName());
				assertTrue(file.exists());
				/* read xml to pathway model */
				PathwayModel pathwayModel = new PathwayModel();
				pathwayModel.readFromXml(file, true);

				/* write pathway model to xml */
				File outputFile = new File(outputDir, file.getName());
				GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, outputFile, true);
				System.out.println(outputFile);

				/* write pathway model to xml */
//				File tmp = File.createTempFile(file.getName() + "_to2021", ".gpml");
//				GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp, false);
//				System.out.println(tmp);

				/* method to assert file is same? */

			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

	}
}