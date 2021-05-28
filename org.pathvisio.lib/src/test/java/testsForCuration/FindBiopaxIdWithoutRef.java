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
 * Test which searches for GPML2013a pathways with Biopax PublicationXref
 * rdf:ids which are not referenced by any BiopaxRef.
 * 
 * @author finterly
 */
public class FindBiopaxIdWithoutRef extends TestCase {

	/**
	 * Searches for GPML2013a files which have Biopax PublicationXref with no
	 * BiopaxRef referring it.
	 */
	public static void testBiopaxIDButNoRef() throws IOException, ConverterException {
		final Namespace BIOPAX_NAMESPACE = Namespace.getNamespace("bp",
				"http://www.biopax.org/release/biopax-level3.owl#");
		final Namespace RDF_NAMESPACE = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

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
			Map<String, Set<String>> foundFiles = new TreeMap<String, Set<String>>();
			// For all gpml of an organism:
			for (int j = 0; j < listOfFiles.length; j++) {
				File file = listOfFiles[j];
				if (file.isFile()) {
					assertTrue(file.exists());
//					System.out.println(file.getName());
					try {
						SAXBuilder builder = new SAXBuilder();
						Document readDoc = builder.build(file);
						Element root = readDoc.getRootElement();

						Set<String> bprfMissingSet = new HashSet<String>();
						Set<String> bprfSet = new HashSet<String>();
						// Read all Pathway BiopaxRefs and add to Set
						List<Element> bprfs = root.getChildren("BiopaxRef", root.getNamespace());
						for (Element bprf : bprfs) {
							String bprfid = bprf.getText();
							bprfSet.add(bprfid);
						}
						// Read all Pathway Element BiopaxRefs and add to Set
						List<Element> children = root.getChildren();
						for (Element child : children) {
							List<Element> bprfsChildren = child.getChildren("BiopaxRef", root.getNamespace());
							for (Element bprfChild : bprfsChildren) {
								String bprfid = bprfChild.getText();
								bprfSet.add(bprfid);
							}
						}
						// Read PublicationXref rdf:id
						Element bp = root.getChild("Biopax", root.getNamespace());
						if (bp != null) {
							for (Element pubxf : bp.getChildren("PublicationXref", BIOPAX_NAMESPACE)) {
								String rdfId = pubxf.getAttributeValue("id", RDF_NAMESPACE);
								if (!bprfSet.contains(rdfId)) {
									bprfMissingSet.add(rdfId);
								}
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
			if (foundFiles.size() > 0) {
				System.out.println(dirOrganisms[i] + ": " + foundFiles.size());
			}
			Set<String> keys = foundFiles.keySet();
//			for (String key : keys) {
//				String keyPrint = key.substring(0, key.lastIndexOf('.'));
//				System.out.format("%s	%s	%s " + "\n", keyPrint, foundFiles.get(key), dirOrganisms[i]);
//			}

		}
	}
}
