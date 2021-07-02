package testsForCuration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.pathvisio.io.ConverterException;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.ref.Citation;
import org.pathvisio.model.ref.UrlRef;
import org.pathvisio.util.XrefUtils;

import junit.framework.TestCase;

/**
 * Test which searches for GPML2013a pathways with Biopax which have the same
 * values, e.g. same xref and url.
 * 
 * @author finterly
 */
public class FindBiopaxDuplicateInformation extends TestCase {

	/**
	 * Searches for GPML2013a files which have Biopax.
	 */
	public static void testBiopaxDuplicateInfo() throws IOException, ConverterException {
		final Namespace BIOPAX_NAMESPACE = Namespace.getNamespace("bp",
				"http://www.biopax.org/release/biopax-level3.owl#");
		final Namespace RDF_NAMESPACE = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		
		DataSourceTxt.init();

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
					try {
						SAXBuilder builder = new SAXBuilder();
						Document readDoc = builder.build(file);
						Element root = readDoc.getRootElement();

						PathwayModel pathwayModel = new PathwayModel();
						Set<String> elementIds = new HashSet<String>();
						Set<Citation> biopaxes = new HashSet<Citation>();
						Set<String> duplicates = new HashSet<String>();
						// Read all Pathway BiopaxRefs and add to Set
						Element bps = root.getChild("Biopax", root.getNamespace());
						if (bps != null) {
							for (Element bp : bps.getChildren("PublicationXref", BIOPAX_NAMESPACE)) {
								String elementId = bp.getAttributeValue("id", RDF_NAMESPACE);
								if (elementIds.contains(elementId)) {
									continue;
								}
								elementIds.add(elementId);
								String biopaxId = null;
								for (Element pubxfElement : bp.getChildren("ID", BIOPAX_NAMESPACE)) {
									if (biopaxId == null || biopaxId.equals("")) {
										if (pubxfElement != null)
											biopaxId = pubxfElement.getText();
									} else {
										continue;
									}
								}
								String biopaxDb = null;
								for (Element pubxfElement : bp.getChildren("DB", BIOPAX_NAMESPACE)) {
									if (biopaxDb == null || biopaxDb.equals("")) {
										if (pubxfElement != null)
											biopaxDb = pubxfElement.getText();
									} else {
										continue;
									}
								}
								Xref xref = XrefUtils.createXref(biopaxId, biopaxDb);
								// instantiates citation
								Citation citation = new Citation(pathwayModel, elementId, xref);
								// sets optional properties
								String title = null;
								for (Element pubxfElement : bp.getChildren("TITLE", BIOPAX_NAMESPACE)) {
									if (title == null || title.equals("")) {
										if (pubxfElement != null)
											title = pubxfElement.getText();
									} else {
										continue;
									}
								}
								String source = null;
								for (Element pubxfElement : bp.getChildren("SOURCE", BIOPAX_NAMESPACE)) {
									if (source == null || source.equals("")) {
										if (pubxfElement != null)
											source = pubxfElement.getText();
									} else {
										continue;
									}
								}
								String year = null;
								for (Element pubxfElement : bp.getChildren("YEAR", BIOPAX_NAMESPACE)) {
									if (year == null || year.equals("")) {
										if (pubxfElement != null)
											year = pubxfElement.getText();
									} else {
										continue;
									}
								}
								List<String> authors = new ArrayList<String>();
								for (Element au : bp.getChildren("AUTHORS", BIOPAX_NAMESPACE)) {
									String author = au.getText();
									if (author != null)
										authors.add(author);
								}
								if (!authors.isEmpty())
									citation.setAuthors(authors);
								if (citation != null) {
									for (Citation biopax : biopaxes) {
										if (citation.equalsCitation(biopax)) {
											duplicates.add(citation.getElementId());
										}
									}
									biopaxes.add(citation);
								}
							}
							if (!duplicates.isEmpty()) {
								foundFiles.put(file.getName(), duplicates);
							}
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
			for (String key : keys) {
				String keyPrint = key.substring(0, key.lastIndexOf('.'));
				System.out.format("%s	%s	%s " + "\n", keyPrint, foundFiles.get(key), dirOrganisms[i]);
			}
		}
	}
}
