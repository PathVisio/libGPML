package testsForCuration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
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
public class FindArrowHeadType extends TestCase {

	/**
	 * Searches for GPML2013a files to find common arrowHead types.
	 */
	public static void testArrowHeadType() throws IOException, ConverterException {
		// To see all used arrowHead Types
		Set<String> arrowHeadTypes = new HashSet<String>();
		// Gets all organism directories
		File dirAllOrganisms = new File("C:/Users/p70073399/Documents/wikipathways-20210527-all-species/cache");
		String[] dirOrganisms = dirAllOrganisms.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		// For each Organism
		for (int i = 0; i < dirOrganisms.length; i++) {
			Map<String, Set<String>> specificList = new HashMap<String, Set<String>>();
//			System.out.println(dirOrganisms[i].toString());
			// For each type
			List<String> TYPES = new ArrayList<>(Arrays.asList("Receptor", "ReceptorRound", "ReceptorSquare",
					"LigandRound", "LigandSquare", "mim-branching-left", "mim-branching-right", "mim-cleavage",
					"mim-gap", "SBGN-Catalysis", "SBGN-Production", "SBGN-Inhibition"));
			for (String TYPE : TYPES) {
				File dirOrganism = new File(
						"C:/Users/p70073399/Documents/wikipathways-20210527-all-species/cache/" + dirOrganisms[i]);
				File[] listOfFiles = dirOrganism.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".gpml");
					}
				});
				// For all gpml of an organism:
				for (int j = 0; j < listOfFiles.length; j++) {
					File file = listOfFiles[j];
					if (file.isFile()) {
						assertTrue(file.exists());
						try {
							SAXBuilder builder = new SAXBuilder();
							Document readDoc = builder.build(file);
							Element root = readDoc.getRootElement();
							List<Element> ias = root.getChildren("Interaction", root.getNamespace());
							for (Element ia : ias) {
								Element gfx = ia.getChild("Graphics", ia.getNamespace());
								List<Element> pts = gfx.getChildren("Point", gfx.getNamespace());
								for (Element pt : pts) {
									String arrowHeadType = pt.getAttributeValue("ArrowHead");
									// finds files containing mim-gap
									if (arrowHeadType != null) {
										if (arrowHeadType.equalsIgnoreCase(TYPE)) {

											if (specificList.containsKey(file.getName())) {
												specificList.get(file.getName()).add(TYPE);
											} else {
												Set<String> newSet = new HashSet<String>();
												newSet.add(TYPE);
												specificList.put(file.getName(), newSet);
											}
										}
									}
									arrowHeadTypes.add(arrowHeadType);
								}
							}
							List<Element> glns = root.getChildren("GraphicalLine", root.getNamespace());
							for (Element gln : glns) {
								Element gfx = gln.getChild("Graphics", gln.getNamespace());
								List<Element> pts = gfx.getChildren("Point", gfx.getNamespace());
								for (Element pt : pts) {
									String arrowHeadType = pt.getAttributeValue("ArrowHead");
									// finds files containing mim-gap
									if (arrowHeadType != null) {
										if (arrowHeadType.equalsIgnoreCase(TYPE)) {
											if (specificList.containsKey(file.getName())) {
												specificList.get(file.getName()).add(TYPE);
											} else {
												Set<String> newSet = new HashSet<String>();
												newSet.add(TYPE);
												specificList.put(file.getName(), newSet);
											}
										}
									}
									arrowHeadTypes.add(arrowHeadType);
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
//		for (String arrowHead : arrowHeadTypes) {
//			System.out.println(arrowHead);
//		}
			Set<String> keys = specificList.keySet();
			for (String key : keys) {
				String keyPrint = key.substring(0, key.lastIndexOf('.'));
				System.out.format("%s	%s	%s" + "\n", keyPrint, dirOrganisms[i].toString(), specificList.get(key));
			}
		}
	}
}
