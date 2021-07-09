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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.pathvisio.io.ConverterException;

import junit.framework.TestCase;

/**
 * Tests searching GPML2013a files for properties and values. 
 * 
 * NB: For all GPML2013a files, there are no StateTypes. 
 * 
 * @author finterly
 */
public class SearchGPMLs extends TestCase {

	/**
	 * Searches for GPML2013a files which have Biopax with no SOURCE, YEAR, or etc.
	 * 
	 * Result: Hs_Riboflavin_and_CoQ_disorders_WP5037_115140.gpml
	 */
	public static void testBiopaxMissingSource() throws IOException, ConverterException {
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
						List<Element> sources = pubxf.getChildren("SOURCE", BIOPAX_NAMESPACE);
						if (sources.isEmpty())
							System.out.println(file.getName());
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Searches for GPML2013a files to find common anchor shape types. Result:
	 * [Circle , None ]
	 */
	public static void testAnchorShapeTypes() throws IOException, ConverterException {
		Set<String> shapeTypes = new HashSet<String>();
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-complete-gpml-Homo_sapiens");
		File[] listOfFiles = folderGPML2013a.listFiles();
		for (int i = 1; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				assertTrue(file.exists());
				try {
					SAXBuilder builder = new SAXBuilder();
					Document readDoc = builder.build(file);
					Element root = readDoc.getRootElement();
					List<Element> es = root.getChildren("Interaction", root.getNamespace());
					for (Element e : es) {
						Element gfx = e.getChild("Graphics", e.getNamespace());
						List<Element> ans = gfx.getChildren("Anchor", gfx.getNamespace());
						for (Element an : ans) {
							String shapeType = an.getAttributeValue("Shape");
							shapeTypes.add(shapeType);
						}
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(shapeTypes);
	}

	/**
	 * Searches for GPML2013a files to find common group types.
	 * 
	 * Result: [null, Group, Pathway, Complex]
	 */
	public static void testGroupTypes() throws IOException, ConverterException {
		Set<String> groupTypes = new HashSet<String>();
		File dirAllOrganisms = new File("C:/Users/p70073399/Documents/wikipathways-20210527-all-species/cache");
		String[] dirOrganisms = dirAllOrganisms.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		System.out.println(Arrays.toString(dirOrganisms));
		for (int i = 0; i < dirOrganisms.length; i++) {
			File dirOrganism = new File(
					"C:/Users/p70073399/Documents/wikipathways-20210527-all-species/cache/" + dirOrganisms[i]);
			File[] listOfFiles = dirOrganism.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".gpml");
				}
			});
			// For all gpml of an organism:
			for (int j = 1; j < listOfFiles.length; j++) {
				File file = listOfFiles[j];
				if (file.isFile()) {
					assertTrue(file.exists());
					try {
						SAXBuilder builder = new SAXBuilder();
						Document readDoc = builder.build(file);
						Element root = readDoc.getRootElement();
						List<Element> grps = root.getChildren("Group", root.getNamespace());
						for (Element grp : grps) {
							String groupType = grp.getAttributeValue("Style");
							groupTypes.add(groupType);
						}
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println(groupTypes);
		}
	}
}