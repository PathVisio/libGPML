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
 * Test which searches for GPML2013a pathways with Comments with DataSource but
 * no Text.
 * 
 * @author finterly
 */
public class FindCommentsWithoutText extends TestCase {

	/**
	 * Searches for comments with data source but without text.
	 */
	public static void testCommentsWithoutText() throws IOException, ConverterException {
		Map<String, List<String>> foundFiles = new TreeMap<String, List<String>>();

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
						List<String> commentTexts = new ArrayList<String>();
						SAXBuilder builder = new SAXBuilder();
						Document readDoc = builder.build(file);
						Element root = readDoc.getRootElement();
						List<Element> pwyCmmts = root.getChildren("Comment");
						for (Element pwyCmmt : pwyCmmts) {
							String commentText = pwyCmmt.getText();
							String dataSource = pwyCmmt.getAttributeValue("Source");
							if (commentText == null || commentText.equals("") && dataSource != null)
								commentTexts.add(dataSource);
						}
						List<Element> elements = root.getChildren();
						for (Element e : elements) {
							List<Element> cmmts = e.getChildren("Comment", e.getNamespace());
							for (Element cmmt : cmmts) {
								String commentText = cmmt.getText();
								String dataSource = cmmt.getAttributeValue("Source");
								if (commentText == null || commentText.equals("") && dataSource != null)
									commentTexts.add(dataSource);
							}
						}
						if (!commentTexts.isEmpty()) {
							foundFiles.put(file.getName(), commentTexts);
						}
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		Set<String> keys = foundFiles.keySet();
		for (String key : keys) {
			String keyPrint = key.substring(0, key.lastIndexOf('.'));
			System.out.format("%s	%s " + "\n", keyPrint, foundFiles.get(key).toString());
		}
	}
}