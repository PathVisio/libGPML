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

import junit.framework.TestCase;

/**
 * Test which searches for GPML2013a pathways with Biopax PublicationXref
 * rdf:ids which are not referenced by any BiopaxRef.
 * 
 * @author finterly
 */
public class FindDataNodeType extends TestCase {

	/**
	 * Searches for GPML2013a files to find common data node types.
	 */
	public static void testBiopaxIDButNoRef() throws IOException, ConverterException {
		// store all arrowHeadTypes for all organisms and all gpml
		Set<String> dataNodeTypes = new HashSet<String>();
//		Map<String, String> specificList = new HashMap<String, String>();
		// Gets all organism directories
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
						List<Element> dns = root.getChildren("DataNode", root.getNamespace());
						for (Element dn : dns) {
							String type = dn.getAttributeValue("Type");
							if (type != null) {
								if (type.equals("Key Event")) {
									System.out.println(file.getName());
								}
							}
							dataNodeTypes.add(type);
						}
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println(dataNodeTypes);
	}
}
