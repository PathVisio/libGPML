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
import org.pathvisio.debug.Logger;
import org.pathvisio.model.*;

import junit.framework.TestCase;

public class SearchGPMLs extends TestCase {


	
	/**
	 * Searches for GPML2013a files which have Biopax with duplicated values. There are 112 files 
	 */
	public static void testBiopaxMultipleID() throws IOException, ConverterException {
		Set<String> foundFiles = new HashSet<String>();
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-complete-gpml-Homo_sapiens");
		File[] listOfFiles = folderGPML2013a.listFiles();
		final Namespace BIOPAX_NAMESPACE = Namespace.getNamespace("bp",
				"http://www.biopax.org/release/biopax-level3.owl#");
		for (int i = 1; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				assertTrue(file.exists());
				try {
					SAXBuilder builder = new SAXBuilder();
					Document readDoc = builder.build(file);
					Element root = readDoc.getRootElement();
					Element bp = root.getChild("Biopax", root.getNamespace());
					for (Element pubxf : bp.getChildren("PublicationXref", BIOPAX_NAMESPACE)) {
						List<Element> ids = pubxf.getChildren("ID", BIOPAX_NAMESPACE);
						if (ids.size()>1) {
							foundFiles.add(file.getName());
						}
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("There are " + foundFiles.size() + " GPML2013a files with duplicated Biopax information");
		for (String foundFile: foundFiles) {
			System.out.println(foundFile);
		}
	}
	
//	/**
//	 * Searches for GPML2013a files which Comments with only Source and no Text. 
//	 */
//	public static void testSearchCommentsWithoutText(File[] listOfFiles) throws IOException, ConverterException {
//		for (int i = 1; i < listOfFiles.length; i++) {
//			File file = listOfFiles[i];
//			if (file.isFile()) {
//				assertTrue(file.exists());
//				try {
//					SAXBuilder builder = new SAXBuilder();
//					Document readDoc = builder.build(file);
//					Element root = readDoc.getRootElement();
//					List<Element> pwyCmmts = root.getChildren("Comment");
//					for (Element pwyCmmt : pwyCmmts) {
//						if (pwyCmmt.getText() == null
//								|| pwyCmmt.getText().equals("") && pwyCmmt.getAttributeValue("Source") != null)
//							System.out.println("File " + i + " : " + file.getName());
//					}
//					List<Element> elements = root.getChildren();
//					for (Element e : elements) {
//						List<Element> cmmts = e.getChildren("Comment", e.getNamespace());
//						for (Element cmmt : cmmts) {
//							if (cmmt.getText() == null
//									|| cmmt.getText().equals("") && cmmt.getAttributeValue("Source") != null)
//								System.out.println("File " + i + " : " + file.getName());
//						}
//					}
//				} catch (JDOMException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}



}