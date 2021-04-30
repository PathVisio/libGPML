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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.bridgedb.Xref;
import org.pathvisio.io.*;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.type.*;

import junit.framework.TestCase;

public class TestGPMLRead extends TestCase {

//	private static final File PATHVISIO_BASEDIR = new File("../..");


	public static void testRead2021() throws ConverterException, IOException
	{
		URL url = Thread.currentThread().getContextClassLoader().getResource("readtest.xml");
		File file = new File(url.getPath());
//		File in = new File ("readtest.xml");
		assertTrue (file.exists());
	
		PathwayModel pathwayModel = new PathwayModel();
		pathwayModel.readFromXml(file, true);
	}
	

//	public static void testConvert08a13a() throws ConverterException, IOException
//	{
//		File in = new File (PATHVISIO_BASEDIR, "testData/WP248_2008a.gpml");
//		assertTrue (in.exists());
//		
//		Pathway pwy = new Pathway();
//		pwy.readFromXml(in, true);
//		
//		File tmp = File.createTempFile("test", "gpml");
//		GpmlFormat2013a.GPML_2013A.writeToXml(pwy, tmp, true);		
//	}
//	
//	/**
//	 * Test reading 2008a & 2010a files, then writing them as 2013a
//	 */
//	public static void testConvert10a13a() throws ConverterException, IOException
//	{
//		File in = new File (PATHVISIO_BASEDIR, "testData/WP248_2010a.gpml");
//		assertTrue (in.exists());
//		
//		Pathway pwy = new Pathway();
//		pwy.readFromXml(in, true);
//		
//		File tmp = File.createTempFile("test", "gpml");
//		GpmlFormat2013a.GPML_2013A.writeToXml(pwy, tmp, true);		
//	}
//	
//	
//	
//	private static final File FILE1 = 
//		new File (PATHVISIO_BASEDIR, "testData/2008a-deprecation-test.gpml");
//	
//	public void testDeprecatedFields() throws ConverterException
//	{
//		assertTrue (FILE1.exists());
//		
//		Pathway pwy = new Pathway();
//		GpmlFormat.readFromXml(pwy, FILE1, true);
//		
//		PathwayElement dn = pwy.getElementById("e4fa1");
//		assertEquals ("This is a backpage head", dn.getDynamicProperty("org.pathvisio.model.BackpageHead"));
//	}
//	
}
