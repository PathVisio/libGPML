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
package org.pathvisio.libgpml.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.pathvisio.libgpml.model.GPML2013aWriter;
import org.pathvisio.libgpml.model.GPML2021Writer;
import org.pathvisio.libgpml.model.PathwayModel;

import junit.framework.TestCase;

/**
 * Test for reading and writing of a single GPML2013a or GPML2021, for
 * troubleshooting and resolving specific issues.
 * 
 * @author finterly
 */
public class TestSingleGPMLReadWrite extends TestCase {

	private PathwayModel pathwayModel;
	private String inputFile = "example-v2013a.xml";
	private URL url = Thread.currentThread().getContextClassLoader().getResource(inputFile);

	/**
	 * Reads a GPML2013a/GPML2021 file.
	 * 
	 * @throws ConverterException 
	 * @throws IOException      
	 */
	@Before
	public void setUp() throws IOException, ConverterException {
		File file = new File(url.getPath());
		assertTrue(file.exists());
		pathwayModel = new PathwayModel();
		pathwayModel.readFromXml(file, true);
	}

	/**
	 * Writes pathway mode to a GPML2013a and GPML2021 file.
	 * 
	 * @throws ConverterException
	 * @throws IOException
	 */
	@Test
	public void testWrite() throws IOException, ConverterException {
		File tmp = File.createTempFile(inputFile + "_testwriteGPML2013a_", ".gpml");
		GPML2013aWriter.GPML2013aWRITER.writeToXml(pathwayModel, tmp, true);
		System.out.println(tmp);

		File tmp2 = File.createTempFile(inputFile + "_testwriteGPML2021_", ".gpml");
		GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp2, true);
		System.out.println(tmp2);
	}

}