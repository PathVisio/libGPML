/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2021 BiGCaT Bioinformatics, WikiPathways
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package testsForCuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import org.pathvisio.io.ConverterException;
import org.pathvisio.io.GPML2021Writer;
import org.pathvisio.model.*;
import org.pathvisio.util.XrefUtils;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * Test for conversion of GPML2013a to GPML2021. Reads GPML2013a, reads Xref and
 * author information, and writes to GPML2021 format.
 * 
 * @author finterly
 */
public class TestConvertToGPML2021WithXref extends TestCase {

	/**
	 * For testing conversion GPML2013a to newer GPML2021. Reading a directory of
	 * GPML2013a files, reading file names/author information, and writing to
	 * GPML2021 format.
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 */
	public static void testConvertToGPML2021() throws IOException, ConverterException {
		File authorsDir = new File("C:/Users/p70073399/Documents/wikipathways-20210410-rdf-authors/authors");
		File[] authorFiles = authorsDir.listFiles();

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
			// GPML files
			File[] listOfFiles = dirOrganism.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".gpml");
				}
			});
			// Info files
			File[] fileInfos = dirOrganism.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".info");
				}
			});

			// For all gpml of an organism:
			for (int j = 800; j < listOfFiles.length; j++) {
				File file = listOfFiles[j];
				if (file.isFile()) {
					System.out.println("File " + j + " : " + file.getName());
					assertTrue(file.exists());
					/* read xml to pathway model */
					PathwayModel pathwayModel = new PathwayModel();
					pathwayModel.readFromXml(file, true);

					/*
					 * Find info file for this WPID to set Version information
					 */
					String version = null;
					String wpidGpmlStr = file.getName().substring(0, file.getName().lastIndexOf('.'));
					for (File info : fileInfos) {
						String wpidInfoStr = info.getName().substring(0, info.getName().indexOf('.'));
						if (wpidInfoStr.equals(wpidGpmlStr)) {
							try {
				                FileReader reader = new FileReader(info);
				                BufferedReader bufferedReader = new BufferedReader(reader);
				                String line =bufferedReader.readLine();
				                while (line != null) {
				                   if (line.startsWith("Revision=")) {
				                	   version = line.substring(1, line.lastIndexOf('='));
				                   }
				                }
				                reader.close();
				            } catch (IOException e) {
				                e.printStackTrace();
				            }
						}
					}
					String wpid = wpidGpmlStr + "_r" + version; // e.g. WP554_107642
					pathwayModel.getPathway().setXref(XrefUtils.createXref(wpid, "wikipathways"));

					/*
					 * read author information TODO how to parse....
					 */
//				for (File authorFile : authorFiles) {
//					String authorFileName = authorFile.getName();
//					String authorFileWPID = authorFileName.substring(0, authorFileName.lastIndexOf('.'));
//					if (authorFileWPID.equals(wpid)) {
//						System.out.println("FOUND");
//						try (BufferedReader br = new BufferedReader(new FileReader(authorFile))) {
//							String line;
//							while ((line = br.readLine()) != null) {
//								System.out.println("LINE "+ line);
//							}
//						}
//					}
//				}

//					/* write pathway model to xml */
//					File outputFile = new File(outputDir, file.getName());
//					GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, outputFile, true);
//					System.out.println(outputFile);

					/* write pathway model to xml */
					File tmp = File.createTempFile(file.getName() + "_to2021", ".gpml");
					GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp, false);
					System.out.println(tmp);

				} else if (listOfFiles[i].isDirectory()) {
					System.out.println("Directory " + listOfFiles[i].getName());
				}
			}
		}
	}
}