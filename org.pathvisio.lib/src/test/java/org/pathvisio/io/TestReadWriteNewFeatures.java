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
import java.net.URL;

import org.pathvisio.model.Citation;
import org.pathvisio.model.DataNode;
import org.pathvisio.model.Group;
import org.pathvisio.model.PathwayElement.AnnotationRef;
import org.pathvisio.model.PathwayElement.EvidenceRef;
import org.pathvisio.model.PathwayModel;
import org.pathvisio.model.Shape;
import org.pathvisio.model.type.AnnotationType;
import org.pathvisio.model.type.DataNodeType;
import org.pathvisio.model.type.ShapeType;
import org.pathvisio.util.ColorUtils;
import org.pathvisio.util.XrefUtils;

import junit.framework.TestCase;

/**
 * Test for reading and writing of GPML2021, especially new features that are
 * not in GPML2013a.
 * 
 * @author finterly
 */
public class TestReadWriteNewFeatures extends TestCase {

	/**
	 * For testing GPML2021 new features. A base GPML2013a is read in and new
	 * features are added before writing to GPML2021.
	 * 
	 * @throws IOException
	 * @throws ConverterException
	 */
	public static void testNewFeatures() throws IOException, ConverterException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("Base_Gpml_Test_WP4904.gpml");
		File file = new File(url.getPath());
		assertTrue(file.exists());

		PathwayModel pathwayModel = new PathwayModel();
		pathwayModel.readFromXml(file, true);

		EvidenceRef er1 = pathwayModel.getPathway().addEvidence(null, XrefUtils.createXref("0000001", "pubmed"), null);

		/*
		 * Add citation and evidence to Annotation
		 */
		AnnotationRef annotationRef1 = pathwayModel.getPathway().addAnnotation("value", AnnotationType.UNDEFINED, null,
				null);
		EvidenceRef er2 = annotationRef1.addEvidence(null, XrefUtils.createXref("0000001", "pubmed"), null);

		// adds citation b9d pathwayModel to annotationRef as citationRef
		Citation citation1 = pathwayModel.getCitations().get(2);
		annotationRef1.addCitation(citation1);

		/**
		 * Customize graphics features, change shapeType of virus to customized shape
		 * "virus".
		 */
		Shape virus = (Shape) pathwayModel.getPathwayObject("f2086");
		ShapeType virusShape = ShapeType.register("virus");
		virus.setShapeType(virusShape);

		/**
		 * Customize graphics features of group. Change shapeTypes to triangle,
		 */
		Group group1 = (Group) pathwayModel.getPathwayObject("grp001");
		Group group2 = (Group) pathwayModel.getPathwayObject("grp002");
		group1.setBorderColor(ColorUtils.stringToColor("Purple"));
		// orange --> black (default) since it is not in color map
		group2.setBorderColor(ColorUtils.stringToColor("orange"));

		/**
		 * Create data node with type alias with elementRef to groups grp001 using base
		 * template (alias001)
		 */
		DataNode alias1 = (DataNode) pathwayModel.getPathwayObject("alias001");
		alias1.setType(DataNodeType.ALIAS);

		/**
		 * Create nested groups. We nest group grp002 inside grp001 by adding alias002
		 * data node to grp001.
		 */
		DataNode alias2 = (DataNode) pathwayModel.getPathwayObject("alias002");
		alias2.setType(DataNodeType.ALIAS);
//		alias2.setGroupRef(group1);

		System.out.println(citation1.getElementId());

		/*
		 * Write to GPML2021
		 */
		File tmp = File.createTempFile("test_new_features_", ".gpml");
		GPML2021Writer.GPML2021WRITER.writeToXml(pathwayModel, tmp, true);
		System.out.println(tmp);

	}

}
