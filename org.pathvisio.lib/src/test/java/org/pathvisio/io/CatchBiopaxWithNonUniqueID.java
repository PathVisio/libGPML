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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
 * Class for catching GPML files which have conflicting non unique Biopax
 * rdf:ids
 * 
 * @author finterly
 */
public class CatchBiopaxWithNonUniqueID extends TestCase {

	/**
	 * For catching GPMLs which have Biopax with non unique IDs. Although the ids
	 * are automatically handled by the reader and writer, we also much manually
	 * look into these particular GMPL files.
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 */
	public static void testCollectXrefs() throws IOException, ConverterException {
		final Namespace RDF_NAMESPACE = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		final Namespace BIOPAX_NAMESPACE = Namespace.getNamespace("bp",
				"http://www.biopax.org/release/biopax-level3.owl#");

		Map<String, String> fileNames = new HashMap<String, String>();
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-complete-gpml-Homo_sapiens");
		File[] listOfFiles = folderGPML2013a.listFiles();

		for (int i = 1; i < listOfFiles.length; i++) {
			Set<String> biopaxIds = new HashSet<String>();
			File file = listOfFiles[i];
			if (file.isFile()) {
				assertTrue(file.exists());
				try {
					SAXBuilder builder = new SAXBuilder();
					Document readDoc = builder.build(file);
					Element root = readDoc.getRootElement();
					Element bp = root.getChild("Biopax", root.getNamespace());
					for (Element pubxf : bp.getChildren("PublicationXref", BIOPAX_NAMESPACE)) {
						String biopaxId = pubxf.getAttributeValue("id", RDF_NAMESPACE);
						if (biopaxIds.contains(biopaxId))
							fileNames.put(file.getName(),biopaxId);
						biopaxIds.add(biopaxId);
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("There are " + fileNames.size() + " GPML2013a files with non unique biopax rdf:id");
		Set<String> keys = fileNames.keySet();
		for (String key: keys) {
			System.out.println(key);
		}
		for (String key: keys) {
			System.out.println(fileNames.get(key));
		}
	}
}