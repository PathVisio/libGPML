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

import org.pathvisio.model.*;
import org.pathvisio.model.ref.AnnotationRef;
import org.pathvisio.model.ref.Citation;
import org.pathvisio.model.ref.CitationRef;
import org.pathvisio.model.ref.Evidence;
import org.pathvisio.model.ref.EvidenceRef;
import org.pathvisio.model.type.*;
import org.pathvisio.util.ColorUtils;
import org.pathvisio.util.XrefUtils;

import junit.framework.TestCase;

/**
 * Test for reading and writing of GPML2021, especially new features that are
 * not in GPML2013a.
 * 
 * @author finterly
 */
public class TestReadWriteGPML2021NewFeatures extends TestCase {

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

		/*
		 * Create and add evidences. Creates evidenceRef1 and adds to pathwayModel.
		 * Creates evidenceRef2 and adds to first annotationRef of pathway.
		 */
		Evidence evidence1 = new Evidence("evidence 1", XrefUtils.createXref("0000001", "pubmed"));
		Evidence evidence2 = new Evidence("evidence 1", XrefUtils.createXref("0000002", "pubmed"));
		// adds evidence to pathway model
		pathwayModel.addEvidence(evidence1);
		pathwayModel.addEvidence(evidence2);
		// adds evidenceRef to pathway
		pathwayModel.getPathway().addEvidenceRef(new EvidenceRef(evidence1));

		/*
		 * Add citation and evidence to Annotation
		 */
		AnnotationRef annotationRef1 = pathwayModel.getPathway().getAnnotationRefs().get(0);
		// adds evidenceRef to annotationRef
		annotationRef1.addEvidenceRef(new EvidenceRef(evidence2));
		// adds citation b9d pathwayModel to annotationRef as citationRef
		Citation citation1 = pathwayModel.getCitations().get(2);
		CitationRef citationRef1 = new CitationRef(citation1);
		annotationRef1.addCitationRef(citationRef1);

		/**
		 * Customize graphics features, change shapeType of virus to customized shape
		 * "virus".
		 */
		Shape virus = (Shape) pathwayModel.getPathwayElement("f2086");
		ShapeType virusShape = ShapeType.register("virus");
		virus.getShapeStyleProp().setShapeType(virusShape);

		/**
		 * Customize graphics features of group. Change shapeTypes to triangle,
		 */
		Group group1 = (Group) pathwayModel.getPathwayElement("grp001");
		Group group2 = (Group) pathwayModel.getPathwayElement("grp002");
		group1.getShapeStyleProp().setBorderColor(ColorUtils.stringToColor("Purple"));
		// orange --> black (default) since it is not in color map
		group2.getShapeStyleProp().setBorderColor(ColorUtils.stringToColor("orange"));

		/**
		 * Create data node with type alias with elementRef to groups grp001 using base
		 * template (alias001)
		 */
		DataNode alias1 = (DataNode) pathwayModel.getPathwayElement("alias001");
		alias1.setType(DataNodeType.ALIAS);
		alias1.setElementRef(group1);

		/**
		 * Create nested groups. We nest group grp002 inside grp001 by adding alias002
		 * data node to grp001.
		 */
		DataNode alias2 = (DataNode) pathwayModel.getPathwayElement("alias002");
		alias2.setType(DataNodeType.ALIAS);
		alias2.setElementRef(group2);
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
