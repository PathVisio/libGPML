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
package org.pathvisio.io;

import java.io.File;
import java.io.IOException;

import org.pathvisio.model.PathwayModel;
import org.pathvisio.util.XrefUtils;

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
		File folderGPML2013a = new File("C:/Users/p70073399/Documents/wikipathways-complete-gpml-Homo_sapiens");
		String outputDir = "C:/Users/p70073399/Documents/wikipathways-convert-to-GPML2021-with-info";
		File[] listOfFiles = folderGPML2013a.listFiles();

		File authorsDir = new File("C:/Users/p70073399/Documents/wikipathways-20210410-rdf-authors/authors");
		File[] authorFiles = authorsDir.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				System.out.println("File " + i + " : " + file.getName());
				assertTrue(file.exists());
				/* read xml to pathway model */
				PathwayModel pathwayModel = new PathwayModel();
				pathwayModel.readFromXml(file, true);

				/*
				 * read xref information from file name, e.g.
				 * Hs_ACE_Inhibitor_Pathway_WP554_107642.gpml
				 */
				String[] parts = file.getName().split("_");
				int length = parts.length;
				String wpid1 = parts[length - 2]; // e.g. WP554
				String wpid2 = parts[length - 1]; // e.g. 107642.gpml
				String wpid = wpid1 + "_r" + wpid2.substring(0, wpid2.lastIndexOf('.')); // e.g. WP554_107642
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

				/* write pathway model to xml */
				File outputFile = new File(outputDir, file.getName());
				GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, outputFile, true);
				System.out.println(outputFile);

			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

	}
}