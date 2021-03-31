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

import java.io.File; //Class that will represent a system file name
import java.io.FileOutputStream; //Used to write data to a file
import java.io.IOException; //Triggered when an I/O error occurs
import org.jdom2.Document; //Represents your XML document and contains useful methods
import org.jdom2.Element; //Represents XML elements and contains useful methods
import org.jdom2.JDOMException; //Top level JDOM exception
import org.jdom2.Namespace;
import org.jdom2.Text; //Represents text used with JDOM
import org.jdom2.input.SAXBuilder; //Creates a JDOM document parsed using SAX Simple API for XML
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.jdom2.output.Format; //Formats how the XML document will look
import org.jdom2.output.XMLOutputter; //Outputs the JDOM document to a file

public class Playground {

	public static void main(String[] args) throws JDOMException {
		File xsdfile = new File("schema.xsd");

//		writeXML();
		readXML();
	}

	private static void readXML() throws JDOMException {

		File xsdfile = new File("schema.xsd");

		try {
			// SOME VALIDATION STEPS
			XMLReaderJDOMFactory schemafactory = new XMLReaderXSDFactory(xsdfile);
			SAXBuilder builder = new SAXBuilder(schemafactory);

//			SAXBuilder builder = new SAXBuilder();

			// Parses the file supplied into a JDOM document
			Document readDoc = builder.build(new File("jdomMade.xml"));

			// Returns the root element for the document
			System.out.println("Root: " + readDoc.getRootElement());

			Element root = readDoc.getRootElement();
			Namespace ns = root.getNamespace();
			System.out.println(ns);

			Namespace xsd = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
			Namespace gpml = Namespace.getNamespace("gpml", "http://pathvisio.org/GPML/2021");
			System.out.println(xsd);
			System.out.println(gpml);

			// Gets the text found between the name tags. GETS THE FIRST ONE?
			System.out.println("---------");
			System.out.println(root.getChild("Labels", gpml).getChild("Label", gpml).getChild("Graphics", gpml)
					.getAttributeValue("shapeType") + "\n");
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writeXML() {
		try {
			// Creates a JDOM document
			Document doc = new Document();

			// Creates a element named tvshows and makes it the root
			Element theRoot = new Element("tvshows");
			doc.setRootElement(theRoot);

			// Creates elements show and name
			Element show = new Element("show");
			Element name = new Element("name");

			// Assigns an attribute to name and gives it a value
			name.setAttribute("show_id", "show_001");
			// Adds text between the name tags
			name.addContent(new Text("Life On Mars"));

			Element network = new Element("network");
			network.setAttribute("country", "US");
			network.addContent(new Text("ABC"));

			// Adds name and network to the show tag
			show.addContent(name);
			show.addContent(network);

			// Adds the show tag and all of its children to the root
			theRoot.addContent(show);

			// ----------------
			// Add a new show element like above
			Element show2 = new Element("show");
			Element name2 = new Element("name");
			name2.setAttribute("show_id", "show_002");

			name2.addContent(new Text("Freaks and Geeks"));

			Element network2 = new Element("network");
			network2.setAttribute("country", "UK");

			network2.addContent(new Text("BBC"));

			show2.addContent(name2);
			show2.addContent(network2);

			theRoot.addContent(show2);

			// Uses indenting with pretty format
			XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());

			// Create a new file and write XML to it
			xmlOutput.output(doc, new FileOutputStream(new File("jdomMade.xml")));
			System.out.println("Wrote to file");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}