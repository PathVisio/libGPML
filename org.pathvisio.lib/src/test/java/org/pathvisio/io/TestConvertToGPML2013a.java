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
import java.io.FilenameFilter;
import java.io.IOException;

import org.pathvisio.model.PathwayModel;

import junit.framework.TestCase;

/**
 * Test for conversion of GPML2021 to GPML2013a.
 * 
 * @author finterly
 */
public class TestConvertToGPML2013a extends TestCase {

	/**
	 * Tests converting GPML2021 to GPML2013a.
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 */
	public static void testConvertToGPML2013a() throws IOException, ConverterException {
		// input
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-convert-to-GPML2021");

		// output
		String outputDir = "C:/Users/p70073399/Documents/wikipathways-convert-to-GPML2013a";

		// filter for gpml files
		File[] listOfFiles = folderGPML2013a.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".gpml");
			}
		});

		// read in and write out
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				System.out.println("File " + i + " : " + file.getName());
				assertTrue(file.exists());
				// read files
				PathwayModel pathwayModel = new PathwayModel();
				pathwayModel.readFromXml(file, true);

				// write to specified directory
				File outputFile = new File(outputDir, file.getName());
				GPML2013aWriter.GPML2013aWRITER.writeToXml(pathwayModel, outputFile, true);
				System.out.println(outputFile);

				// write to temp
//				File tmp = File.createTempFile(file.getName() + "_to2021", ".gpml");
//				GPML2013aWriter.GPML2013aWRITER.writeToXml(pathwayModel, outputFile, true);
//				System.out.println(tmp);

			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

	}
}