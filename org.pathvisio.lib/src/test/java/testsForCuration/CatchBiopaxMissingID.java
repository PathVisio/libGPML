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
			System.out.println("ORGANISM: " + dirOrganisms[i]);
			File dirOrganism = new File(
					"C:/Users/p70073399/Documents/wikipathways-20210527-all-species/cache/" + dirOrganisms[i]);
			File[] listOfFiles = dirOrganism.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".gpml");
				}
			});
			for (int j = 0; j < listOfFiles.length; j++) {
				File file = listOfFiles[j];
				if (file.isFile()) {
					assertTrue(file.exists());
					try {
						SAXBuilder builder = new SAXBuilder();
						Document readDoc = builder.build(file);
						Element root = readDoc.getRootElement();

						String elementIdList = "";

						Element bp = root.getChild("Biopax", root.getNamespace());
						if (bp != null) {
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
			if (foundFiles.size() > 0) {
				System.out.println(dirOrganisms[i] + ": " + foundFiles.size());
			}
			Set<String> keys = foundFiles.keySet();
			for (String key : keys) {
				String keyPrint= key.substring(0, key.lastIndexOf('.'));
				System.out.format("%s	%s	%s " + "\n", keyPrint, foundFiles.get(key), dirOrganisms[i]);
			}
		}
	}
}
