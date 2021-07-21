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
 * Test which searches for infoBox coordinates of GPML2013a pathways.
 * 
 * @author finterly
 */
public class FindInfoBox extends TestCase {

	/**
	 * Searches for GPML2013a files to find infoBox coordinates.
	 */
	public static void testInfobox() throws IOException, ConverterException {
		Set<String> xs = new HashSet<String>();
		Set<String> ys = new HashSet<String>();

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
						Element ifbx = root.getChild("InfoBox", root.getNamespace());
						String x = ifbx.getAttributeValue("CenterX");
						String y = ifbx.getAttributeValue("CenterY");
						xs.add(x);
						ys.add(y);
						if (Double.valueOf(x) != 0 || Double.valueOf(y) != 0) {
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
		System.out.println(xs);
		System.out.println(ys);
	}
}
