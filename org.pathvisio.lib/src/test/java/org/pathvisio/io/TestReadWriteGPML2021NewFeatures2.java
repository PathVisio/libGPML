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
import java.net.URL;

import org.pathvisio.io.ConverterException;
import org.pathvisio.io.GPML2021Writer;
import org.pathvisio.model.*;
import org.pathvisio.model.element.*;
import org.pathvisio.model.ref.Citation;
import org.pathvisio.model.type.*;
import org.pathvisio.util.ColorUtils;
import org.pathvisio.util.XrefUtils;

import junit.framework.TestCase;

/**
 * Test for reading and writing of GPML2021, especially new features that are
 * not in GPML2013a.
 * 
 * @author finterly
 */
public class TestReadWriteGPML2021NewFeatures2 extends TestCase {

	/**
	 * For testing GPML2021 new features. A base GPML2013a is read in and new
	 * features are added before writing to GPML2021.
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 */
	public static void testNewFeatures() throws IOException, ConverterException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("example-v2021.xml");
		File file = new File(url.getPath());
		assertTrue(file.exists());

		PathwayModel pathwayModel = new PathwayModel();
		pathwayModel.readFromXml(file, true);

		System.out.println(pathwayModel.getPathwayElements());
		System.out.println(pathwayModel.getAnnotations());
		System.out.println(pathwayModel.getCitations());
		
//		/*
//		 * Write to GPML2021
//		 */
//		File tmp = File.createTempFile("test_new_features_", ".gpml");
//		GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp, true);
//		System.out.println(tmp);

	}

}
