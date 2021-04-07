/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2019 BiGCaT Bioinformatics
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

import org.pathvisio.io.gpml2021.GPML2021Writer;
import org.pathvisio.model.*;
import org.pathvisio.model.graphics.Coordinate;

//import junit.framework.TestCase;

public class TestGpml2021Writer {

	public static void main(String[] args) {

		Pathway pathway = new Pathway.PathwayBuilder("Title", 100, 100, Color.decode("#ffffff"), new Coordinate(2, 2))
				.setOrganism("Homo Sapiens").setSource("WikiPathways").setVersion("r1").setLicense("CC0").setXref(null)
				.build();
		
		PathwayModel pathwayModel = new PathwayModel(pathway);
		
		
		File file = new File("writeTest.xml");
		
		GPML2021Writer.writeGPML(pathwayModel, file);
	}
}
