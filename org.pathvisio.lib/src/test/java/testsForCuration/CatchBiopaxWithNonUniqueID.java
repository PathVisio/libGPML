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
package testsForCuration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.pathvisio.io.ConverterException;
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
	 * are automatically handled by the reader and writer, we also must manually
	 * look into these particular GMPL files.
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 */
	public static void testNonUniqueIDs() throws IOException, ConverterException {
		final Namespace RDF_NAMESPACE = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		final Namespace BIOPAX_NAMESPACE = Namespace.getNamespace("bp",
				"http://www.biopax.org/release/biopax-level3.owl#");

		// Gets all organism directories
		File dirAllOrganisms = new File("C:/Users/p70073399/Documents/wikipathways-20210527-all-species/cache");
		String[] dirOrganisms = dirAllOrganisms.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		System.out.println(Arrays.toString(dirOrganisms));
		// Gets all gpml for each organism directory
		for (int i = 0; i < dirOrganisms.length; i++) {
			File dirOrganism = new File(
					"C:/Users/p70073399/Documents/wikipathways-20210527-all-species/cache/" + dirOrganisms[i]);
			File[] listOfFiles = dirOrganism.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".gpml");
				}
			});
			Map<String, Set<String>> foundFiles = new HashMap<String, Set<String>>();
			// For all gpml of an organism:
			for (int j = 0; j < listOfFiles.length; j++) {
				Set<String> biopaxIdsExist = new HashSet<String>();
				Set<String> biopaxIdsDuplicate = new HashSet<String>();
				File file = listOfFiles[j];
				if (file.isFile()) {
					assertTrue(file.exists());
					try {
						SAXBuilder builder = new SAXBuilder();
						Document readDoc = builder.build(file);
						Element root = readDoc.getRootElement();
						Element bp = root.getChild("Biopax", root.getNamespace());
						if (bp != null) {
							for (Element pubxf : bp.getChildren("PublicationXref", BIOPAX_NAMESPACE)) {
								String biopaxId = pubxf.getAttributeValue("id", RDF_NAMESPACE);
								if (biopaxIdsExist.contains(biopaxId)) {
									biopaxIdsDuplicate.add(biopaxId);
								}
								biopaxIdsExist.add(biopaxId);
							}
							if (!biopaxIdsDuplicate.isEmpty()) {
								foundFiles.put(file.getName(), biopaxIdsDuplicate);
							}
						}
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println(dirOrganisms[i] + ": " + foundFiles.size());

			Set<String> keys = foundFiles.keySet();
			for (String key : keys) {
				String keyPrint = key.substring(0, key.lastIndexOf('.'));
				System.out.format("%s	%s	%s " + "\n", keyPrint, foundFiles.get(key.toString()), dirOrganisms[i]);
			}
		}
	}
}