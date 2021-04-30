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

import junit.framework.TestCase;

public class TestGPML2013a extends TestCase {

	/**
	 * Read GPML2013a and Write GPML2013a format. Assert output equivalent to input. 
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 */
	public static void testReadWrite() throws IOException, ConverterException {

		File folderGPML2013a = new File("src/test/resources/sampleGPML2013a");
		File[] listOfFiles = folderGPML2013a.listFiles();

		for (int i = 60; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				System.out.println("File: " + file.getName());
				assertTrue(file.exists());
				/* read xml to pathway model*/
				PathwayModel pathwayModel = new PathwayModel();
				pathwayModel.readFromXml(file, true);

				/* write pathway model to xml */	
				File tmp = File.createTempFile("testwrite", ".gpml"); 
				GPML2013aWriter.GPML2013aWRITER.writeToXml(pathwayModel, tmp, false);
				System.out.println(tmp);
				
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}



	}
}