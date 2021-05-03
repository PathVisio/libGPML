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

public class TestGPMLsReadWrite extends TestCase {

//	private static final File PATHVISIO_BASEDIR = new File("../..");

	public static void testReadWrite() throws IOException, ConverterException {

		File folderGPML2013a = new File("src/test/resources/sampleGPML2013a");

//		File folder = new File(System.getProperty("sampleGPML2013a")+"/src/test/resources/");
		File[] listOfFiles = folderGPML2013a.listFiles();

		for (int i = 60; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				System.out.println("File: " + file.getName());
//				URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
//				System.out.println(url.getPath());
				assertTrue(file.exists());
				PathwayModel pathwayModel = new PathwayModel();
				pathwayModel.readFromXml(file, true);

			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

////		
//		File tmp = File.createTempFile("testwrite", ".gpml"); //extension
////		GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp, true);
//		GPML2013aWriter.GPML2013aWRITER.writeToXml(pathwayModel, tmp, false);
//		System.out.println(tmp);

	}
}