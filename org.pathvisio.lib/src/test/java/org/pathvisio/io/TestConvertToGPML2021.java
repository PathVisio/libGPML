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

public class TestConvertToGPML2021 extends TestCase {

	public static void testReadWrite() throws IOException, ConverterException {
		
//		File folderGPML2013a = new File("src/test/resources/sampleGPML2013a");
//		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-convert-problem-gpmls");
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-20210410-gpml-Homo_sapiens");
		File[] listOfFiles = folderGPML2013a.listFiles();

		for (int i = 1; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				System.out.println("File " + i + " : "+ file.getName());
				assertTrue(file.exists());
				/* read xml to pathway model */
				PathwayModel pathwayModel = new PathwayModel();
				pathwayModel.readFromXml(file, true);

				/* write pathway model to xml */
				File tmp = File.createTempFile(file.getName() + "_to2021", ".gpml");
				GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp, false);
				System.out.println(tmp);

				/* method to assert file is same? */

			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

	}
}