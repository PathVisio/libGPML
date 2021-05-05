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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.bridgedb.Xref;
import org.pathvisio.io.*;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.graphics.*;
import org.pathvisio.model.type.*;

import junit.framework.TestCase;

public class TestReadWriteGPML2021NewFeatures extends TestCase {

	
	/**
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 */
	public static void testWriteGPML2021NewFeatures() throws IOException, ConverterException {
//		File in = new File (PATHVISIO_BASEDIR, "testData/WP248_2008a.gpml");
//		assertTrue (in.exists());
//		
		Pathway pathway = new Pathway.PathwayBuilder("Title", 100, 100, Color.decode("ffffff"), new Coordinate(2, 2))
				.setOrganism("Homo Sapiens").setSource("WikiPathways").setVersion("r1").setLicense("CC0").build();
		
		
		PathwayModel pathwayModel = new PathwayModel(pathway);

		Author author = new Author.AuthorBuilder("henry").setFullName("fullName").setEmail("email@").build();
		pathwayModel.addAuthor(author);

		Annotation annotation = new Annotation("a1", pathwayModel, "homo sapien", AnnotationType.ONTOLOGY,
				"www.website");

		DataNode dataNode = new DataNode("d1", pathwayModel, new RectProperty(new Coordinate(1, 1), 1, 1),
				new FontProperty(null, "Arial", false, false, false, false, 0, null, null), new ShapeStyleProperty(),
				"TextLabel", DataNodeType.ALIAS, null);
		
//		State state = new State("s1", pathwayModel, dataNode, null, StateType.EPIGENETIC_MODIFICATION, 0, 0, 1, 1,
//				new FontProperty(null, "Arial", false, false, false, false, 0, null, null), new ShapeStyleProperty());
		
		Interaction interaction = new Interaction("i1", pathwayModel, new LineStyleProperty(null, null, 0, null), null, null);
		Point point = new Point("p1", pathwayModel, ArrowHeadType.ARROW, new Coordinate(1, 1));
		Point point2 = new Point("p2", pathwayModel, ArrowHeadType.ARROW, new Coordinate(1, 1));
		interaction.addPoint(point);
		interaction.addPoint(point2);
//		dataNode.addState(state);
		pathwayModel.addAnnotation(annotation);
		pathwayModel.addDataNode(dataNode);
		pathwayModel.addInteraction(interaction);

//		pathwayModel.readFromXml(in, true);
		File tmp = File.createTempFile("testwrite", "gpml");
		GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp, true);
//		System.out.println(tmp);

	}


}
