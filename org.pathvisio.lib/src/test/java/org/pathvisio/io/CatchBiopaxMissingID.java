package org.pathvisio.io;

import java.io.File;
import java.io.IOException;
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

import junit.framework.TestCase;

/**
 * Test which searches for GPML2013a pathways with missing IDs
 * 
 * @author finterly
 */
public class CatchBiopaxMissingID extends TestCase {

	/**
	 * Searches for GPML2013a files which have Biopax with no ID.
	 */
	public static void testBiopaxMultipleID() throws IOException, ConverterException {
		Map<String, String> foundFiles = new TreeMap<String, String>();
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-complete-gpml-Homo_sapiens");
		File[] listOfFiles = folderGPML2013a.listFiles();
		final Namespace BIOPAX_NAMESPACE = Namespace.getNamespace("bp",
				"http://www.biopax.org/release/biopax-level3.owl#");
		final Namespace RDF_NAMESPACE = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		for (int i = 1; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				assertTrue(file.exists());
				try {
					SAXBuilder builder = new SAXBuilder();
					Document readDoc = builder.build(file);
					Element root = readDoc.getRootElement();

					String elementIdList = "";

					Element bp = root.getChild("Biopax", root.getNamespace());
					for (Element pubxf : bp.getChildren("PublicationXref", BIOPAX_NAMESPACE)) {
						String elementId = pubxf.getAttributeValue("id", RDF_NAMESPACE);
						List<Element> ids = pubxf.getChildren("ID", BIOPAX_NAMESPACE);
						String myText = null;
						for (Element id : ids) {
							if (myText == null || myText.equals("")) {
								if (id != null)
									myText = id.getText();
							} else {
								continue;
							}
						}
						if (myText == null || myText.equals("")) {
							elementIdList += elementId + "; ";
						}
					}
					if (!elementIdList.equals(""))
						foundFiles.put(file.getName(), elementIdList);
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("There are " + foundFiles.size() + " GPML2013a files with no ID");
		Set<String> keys = foundFiles.keySet();
		for (String key : keys) {
			System.out.println(key);
		}
		for (String key : keys) {
			System.out.println(foundFiles.get(key));
		}
		for (String key : keys) {
			String[] parts = key.split("_");
			int length = parts.length;
			System.out.println(parts[length-2]);
		}
	}
}
