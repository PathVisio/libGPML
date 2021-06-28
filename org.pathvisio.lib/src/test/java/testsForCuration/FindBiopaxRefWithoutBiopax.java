package testsForCuration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
import org.pathvisio.model.ref.Citation;
import org.pathvisio.model.ref.CitationRef;

import junit.framework.TestCase;

/**
 * Test which searches for GPML2013a pathways with BiopaxRefs which refer to no
 * Biopax.
 * 
 * @author finterly
 */
public class FindBiopaxRefWithoutBiopax extends TestCase {

	/**
	 * Searches for GPML2013a files which have BiopaxRef pointing to nonexistent
	 * Biopax.
	 */
	public static void testBiopaxMultipleID() throws IOException, ConverterException {
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
				Set<String> existingBiopaxIds = new HashSet<String>();
				Set<String> problemBiopaxRefs = new HashSet<String>();
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
								existingBiopaxIds.add(biopaxId);
							}
						}
						// Pathway BiopaxRef
						for (Element bpRef : root.getChildren("BiopaxRef", root.getNamespace())) {
							// reads biopaxRef
							String biopaxRef = bpRef.getText();
							if (!existingBiopaxIds.contains(biopaxRef)) {
								problemBiopaxRefs.add(biopaxRef);
							}
						}
						// Get Pathway Element BiopaxRefs
						for (Element child : root.getChildren()) {
							for (Element bpRef : child.getChildren("BiopaxRef", child.getNamespace())) {
								String biopaxRef = bpRef.getText();
								if (!existingBiopaxIds.contains(biopaxRef)) {
									problemBiopaxRefs.add(biopaxRef);
								}
							}
						}
						if (!problemBiopaxRefs.isEmpty()) {
							foundFiles.put(file.getName(), problemBiopaxRefs);
						}

					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
//			System.out.println(dirOrganisms[i] + ": " + foundFiles.size());
			Set<String> keys = foundFiles.keySet();
			for (String key : keys) {
				String keyPrint = key.substring(0, key.lastIndexOf('.'));
				System.out.format("%s	%s	%s " + "\n", keyPrint, foundFiles.get(key.toString()), dirOrganisms[i]);
			}
		}
	}
}