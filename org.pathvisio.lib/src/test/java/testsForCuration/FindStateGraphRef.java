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
 * Test which searches for datanode types of GPML2013a pathways.
 * 
 * @author finterly
 */
public class FindStateGraphRef extends TestCase {

	/**
	 * Searches for GPML2013a files to find common data node types.
	 * 
	 * @throws Exception
	 */
	public static void testBiopaxIDButNoRef() throws Exception {
//		Map<String, String> specificList = new HashMap<String, String>();
		// Gets all organism directories
		int count = 1; 
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
			System.out.println(dirOrganism.getName());
			// For all gpml of an organism:
			for (int j = 1; j < listOfFiles.length; j++) {
				System.out.println(String.valueOf(count));
				count = count +1;
				File file = listOfFiles[j];
				System.out.println(file.getName());
				Map<String, String> idMap = new HashMap<String, String>();
				if (file.isFile()) {
					assertTrue(file.exists());
					try {
						SAXBuilder builder = new SAXBuilder();
						Document readDoc = builder.build(file);
						Element root = readDoc.getRootElement();
						List<Element> elements = root.getChildren();
						for (Element e : elements) {
							String graphId = e.getAttributeValue("GraphId");
							if (graphId != null)
								idMap.put(graphId, e.getName());
							if (e.getName() == "Interaction" || e.getName() == "GraphicalLine") {
								Element gfx = e.getChild("Graphics", e.getNamespace());
								// reads GraphId of Points and Anchors two levels down
								for (Element e2 : gfx.getChildren()) {
									String graphId2 = e2.getAttributeValue("GraphId");
									if (graphId2 != null)
										idMap.put(graphId2, e2.getName());
								}
							}
						}
						List<Element> sts = root.getChildren("State", root.getNamespace());
						for (Element st : sts) {
							String graphRef = st.getAttributeValue("GraphRef");
							if (graphRef != null) {
								if (idMap.get(graphRef) != "DataNode")
									throw new Exception("Not DataNode!");
							}
						}
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
