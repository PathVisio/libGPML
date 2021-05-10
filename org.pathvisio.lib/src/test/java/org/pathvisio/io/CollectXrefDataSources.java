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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.pathvisio.model.*;

import junit.framework.TestCase;

/**
 * Test for collecting Xref values.
 * 
 * @author finterly
 */
public class CollectXrefDataSources extends TestCase {

	/**
	 * For collecting Xref data sources for BridgeDb from directory of GPML2013a
	 * files. Prints out name of data sources and their frequency of use.
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 */
	public static void testCollectXrefs() throws IOException, ConverterException {
		List<String> dataSources = new ArrayList<String>();
		Set<String> dataSourceSet = new HashSet<String>();
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-complete-gpml-Homo_sapiens");
		File[] listOfFiles = folderGPML2013a.listFiles();
		for (int i = 1; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
//				System.out.println("File " + i + " : " + file.getName());
				assertTrue(file.exists());
				try {
					SAXBuilder builder = new SAXBuilder();
					Document readDoc = builder.build(file);
//					System.out.println("Root: " + readDoc.getRootElement());
					Element root = readDoc.getRootElement();
					List<Element> elements = root.getChildren();
					for (Element e : elements) {
						Element xref = e.getChild("Xref", e.getNamespace());
						if (xref != null) {
							String dataSource = xref.getAttributeValue("Database");
							dataSources.add(dataSource);
							dataSourceSet.add(dataSource);
						}
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		for (String dataSource : dataSourceSet) {
			int occurrences = Collections.frequency(dataSources, dataSource);
			System.out.println(dataSource + ": " + occurrences);
		}
	}
}