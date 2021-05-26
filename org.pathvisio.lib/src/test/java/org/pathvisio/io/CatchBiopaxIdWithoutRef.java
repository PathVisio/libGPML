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
 * Test which searches for GPML2013a pathways with Biopax PublicationXref
 * rdf:ids which are not referenced by any BiopaxRef.
 * 
 * @author finterly
 */
public class CatchBiopaxIdWithoutRef extends TestCase {

	/**
	 * Searches for GPML2013a files which have Biopax PublicationXref with no BiopaxRef referring it.
	 */
	public static void testBiopaxIDButNoRef() throws IOException, ConverterException {
		Map<String, Set<String>> foundFiles = new TreeMap<String, Set<String>>();
		
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
					
					Set<String> bprfMissingSet = new HashSet<String>();
					Set<String> bprfSet = new HashSet<String>();
					//Read all Pathway BiopaxRefs and add to Set
					List<Element> bprfs = root.getChildren("BiopaxRef", root.getNamespace());
					for (Element bprf : bprfs) {
						String bprfid = bprf.getText();
						bprfSet.add(bprfid);
					}
					//Read all Pathway Element BiopaxRefs and add to Set
					List<Element> children = root.getChildren();
					for (Element child: children) {
						List<Element> bprfsChildren = child.getChildren("BiopaxRef", root.getNamespace());
						for (Element bprfChild : bprfsChildren) {
							String bprfid = bprfChild.getText();
							bprfSet.add(bprfid);
						}
					}
					//Read PublicationXref rdf:id				
					Element bp = root.getChild("Biopax", root.getNamespace());
					for (Element pubxf : bp.getChildren("PublicationXref", BIOPAX_NAMESPACE)) {
						String rdfId = pubxf.getAttributeValue("id", RDF_NAMESPACE);
						if (!bprfSet.contains(rdfId)) {
							bprfMissingSet.add(rdfId);
						}
					}
					if (!bprfMissingSet.isEmpty()) {
						foundFiles.put(file.getName(), bprfMissingSet);
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("There are " + foundFiles.size() + " with rdf:id with no BiopaxRef");
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
			System.out.println(parts[length - 2]);
		}
	}
}
