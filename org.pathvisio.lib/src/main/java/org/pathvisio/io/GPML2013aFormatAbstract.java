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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.SAXOutputter;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.*;
import org.pathvisio.model.elements.*;
import org.pathvisio.model.type.*;
import org.pathvisio.util.ColorUtils;
import org.pathvisio.util.MiscUtils;
import org.xml.sax.SAXException;

/**
 * Abstract class for GPML2013a format. Contains static properties and maps used
 * in reading or writing GPML2013a.
 * 
 * @author finterly
 */
public abstract class GPML2013aFormatAbstract {

	private final Namespace nsGPML;
	private final String xsdFile;

	/**
	 * Constructor for GPML2013aFormat Abstract.
	 * 
	 * @param xsdFile the schema file.
	 * @param nsGPML  the GPML namespace.
	 */
	protected GPML2013aFormatAbstract(String xsdFile, Namespace nsGPML) {
		this.xsdFile = xsdFile;
		this.nsGPML = nsGPML;
	}

	/**
	 * Returns the GPML schema file.
	 * 
	 * @return xsdFile the schema file.
	 */
	public String getSchemaFile() {
		return xsdFile;
	}

	/**
	 * Returns the GPML namespace.
	 * 
	 * @return nsGPML the GPML namespace.
	 */
	public Namespace getGpmlNamespace() {
		return nsGPML;
	}

	/**
	 * Namespaces and string used for writing GPML2013a
	 */
	public static final Namespace RDF_NAMESPACE = Namespace.getNamespace("rdf",
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	public static final Namespace RDFS_NAMESPACE = Namespace.getNamespace("rdfs",
			"http://www.w3.org/2000/01/rdf-schema#");
	public static final Namespace BIOPAX_NAMESPACE = Namespace.getNamespace("bp",
			"http://www.biopax.org/release/biopax-level3.owl#");
	public static final Namespace OWL_NAMESPACE = Namespace.getNamespace("owl", "http://www.w3.org/2002/07/owl#");
	public final static String RDF_STRING = "http://www.w3.org/2001/XMLSchema#string";

	/**
	 * These dynamic properties are used to store deprecated GPML2013a properties,
	 * but will not be written to GPML2013a/GPML2021
	 */
	public final static String PATHWAY_AUTHOR = "pathway_author_gpml2013a";
	public final static String PATHWAY_MAINTAINER = "pathway_maintainer_gpml2013a";
	public final static String PATHWAY_EMAIL = "pathway_email_gpml2013a";
	public final static String PATHWAY_LASTMODIFIED = "pathway_lastModified_gpml2013a";
	public final static String LEGEND_CENTER_X = "pathway_legend_centerX_gpml2013a";
	public final static String LEGEND_CENTER_Y = "pathway_legend_centerY_gpml2013a";
	public final static String GROUP_GRAPHID = "group_graphId_gpml2013a";

	/**
	 * Dynamic properties key set for deprecated GPML2013a pathway properties. Used
	 * in {@link GPML2013aWriter#writePathwayDynamicProperties}. Dynamic properties
	 * with these keys are ignored when writing GPML2013a and GPML2021.
	 */
	public static final Set<String> GPML2013A_KEY_SET = new HashSet<>(Arrays.asList(PATHWAY_AUTHOR, PATHWAY_MAINTAINER,
			PATHWAY_EMAIL, PATHWAY_LASTMODIFIED, LEGEND_CENTER_X, LEGEND_CENTER_Y, GROUP_GRAPHID));

	/** Strings used when writing GPML2013a */
	public final static String DOUBLE_LINE_KEY = "org.pathvisio.DoubleLineProperty";
	public final static String CELL_CMPNT_KEY = "org.pathvisio.CellularComponentProperty";

	/**
	 * Deprecated map used to track deprecated shape types for conversion and
	 * exclusion.
	 */
	public static final Map<ShapeType, ShapeType> DEPRECATED_MAP = new HashMap<ShapeType, ShapeType>();
	static {
		DEPRECATED_MAP.put(ShapeType.CELL, ShapeType.ROUNDED_RECTANGLE); // TODO
		DEPRECATED_MAP.put(ShapeType.ORGANELLE, ShapeType.ROUNDED_RECTANGLE); // TODO
		DEPRECATED_MAP.put(ShapeType.MEMBRANE, ShapeType.ROUNDED_RECTANGLE);
		DEPRECATED_MAP.put(ShapeType.CELLA, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.NUCLEUS, ShapeType.OVAL); // TODO
		DEPRECATED_MAP.put(ShapeType.ORGANA, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.ORGANB, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.ORGANC, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.VESICLE, ShapeType.OVAL); // TODO
		DEPRECATED_MAP.put(ShapeType.PROTEINB, ShapeType.HEXAGON);
		DEPRECATED_MAP.put(ShapeType.RIBOSOME, ShapeType.HEXAGON);
	}

	/**
	 * Cellular Component map used for writing GPML2013a. Temporary Dynamic Property
	 * for cellular component (for GPML2013a and earlier)
	 */
	public static final Map<ShapeType, ShapeType> CELL_CMPNT_MAP = new HashMap<ShapeType, ShapeType>();
	static {
		CELL_CMPNT_MAP.put(ShapeType.CELL, ShapeType.ROUNDED_RECTANGLE);
		CELL_CMPNT_MAP.put(ShapeType.NUCLEUS, ShapeType.OVAL);
		CELL_CMPNT_MAP.put(ShapeType.ENDOPLASMIC_RETICULUM, ShapeType.ENDOPLASMIC_RETICULUM);
		CELL_CMPNT_MAP.put(ShapeType.GOLGI_APPARATUS, ShapeType.GOLGI_APPARATUS);
		CELL_CMPNT_MAP.put(ShapeType.MITOCHONDRIA, ShapeType.MITOCHONDRIA);
		CELL_CMPNT_MAP.put(ShapeType.SARCOPLASMIC_RETICULUM, ShapeType.SARCOPLASMIC_RETICULUM);
		CELL_CMPNT_MAP.put(ShapeType.ORGANELLE, ShapeType.ROUNDED_RECTANGLE);
		CELL_CMPNT_MAP.put(ShapeType.LYSOSOME, ShapeType.OVAL);
		CELL_CMPNT_MAP.put(ShapeType.NUCLEOLUS, ShapeType.OVAL);
		CELL_CMPNT_MAP.put(ShapeType.VACUOLE, ShapeType.OVAL);
		CELL_CMPNT_MAP.put(ShapeType.VESICLE, ShapeType.OVAL);
		CELL_CMPNT_MAP.put(ShapeType.CYTOSOL, ShapeType.ROUNDED_RECTANGLE);
		CELL_CMPNT_MAP.put(ShapeType.EXTRACELLULAR, ShapeType.ROUNDED_RECTANGLE);
		CELL_CMPNT_MAP.put(ShapeType.MEMBRANE, ShapeType.ROUNDED_RECTANGLE);
	}

	/**
	 * List of GPML2013a arrow head types which correspond to a new Interaction
	 * Panel arrow head type. The first arrow head type string in the list is
	 * priority and is returned by {@link #getArrowHeadTypeStr(ArrowHeadType)}. 
	 */
	public static final List<String> UNDIRECTED_LIST = new ArrayList<>(Arrays.asList("Line"));
	public static final List<String> DIRECTED_LIST = new ArrayList<>(Arrays.asList("Arrow"));
	public static final List<String> CONVERSION_LIST = new ArrayList<>(Arrays.asList("mim-conversion", "mim-conversion",
			"mim-modification"));
	public static final List<String> INHIBITION_LIST = new ArrayList<>(
			Arrays.asList("mim-inhibition", "TBar"));
	public static final List<String> CATALYSIS_LIST = new ArrayList<>(
			Arrays.asList("mim-catalysis"));
	public static final List<String> STIMULATION_LIST = new ArrayList<>(
			Arrays.asList("mim-stimulation", "mim-necessary-stimulation"));
	public static final List<String> BINDING_LIST = new ArrayList<>(Arrays.asList("mim-binding", "mim-covalent-bond"));
	public static final List<String> TRANSLOCATION_LIST = new ArrayList<>(Arrays.asList("mim-translocation"));
	public static final List<String> TRANSCRIPTION_TRANSLATION_LIST = new ArrayList<>(
			Arrays.asList("mim-transcription-translation"));

	/**
	 * Map for GPML2021 Interaction Panel arrow head types to GPML2013a arrowHead
	 * types.
	 */
	public static final Map<ArrowHeadType, List<String>> IA_PANEL_MAP = new HashMap<ArrowHeadType, List<String>>();
	static {
		IA_PANEL_MAP.put(ArrowHeadType.UNDIRECTED, UNDIRECTED_LIST);
		IA_PANEL_MAP.put(ArrowHeadType.DIRECTED, DIRECTED_LIST);
		IA_PANEL_MAP.put(ArrowHeadType.CONVERSION, CONVERSION_LIST);
		IA_PANEL_MAP.put(ArrowHeadType.INHIBITION, INHIBITION_LIST);
		IA_PANEL_MAP.put(ArrowHeadType.CATALYSIS, CATALYSIS_LIST);
		IA_PANEL_MAP.put(ArrowHeadType.STIMULATION, STIMULATION_LIST);
		IA_PANEL_MAP.put(ArrowHeadType.BINDING, BINDING_LIST);
		IA_PANEL_MAP.put(ArrowHeadType.TRANSLOCATION, TRANSLOCATION_LIST);
		IA_PANEL_MAP.put(ArrowHeadType.TRANSCRIPTION_TRANSLATION, TRANSCRIPTION_TRANSLATION_LIST);
	}

	/**
	 * Returns the GPML2021 Interaction Panel arrow head type for given GPML2013a
	 * arrowHead type string.
	 * 
	 * @param arrowHeadStr the string for GPML2013a arrow head type.
	 * @return arrowHead the interaction panel arrow head type which corresponds to
	 *         arrowHeadStr, or null if no corresponding type exists.
	 * @throws ConverterException
	 */
	protected ArrowHeadType getInteractionPanelType(String arrowHeadStr) throws ConverterException {
		Set<ArrowHeadType> arrowHeads = IA_PANEL_MAP.keySet();
		for (ArrowHeadType arrowHead : arrowHeads) {
			List<String> arrowHeadStrs = IA_PANEL_MAP.get(arrowHead);
			// case insensitive method for matching in list
			if (MiscUtils.containsCaseInsensitive(arrowHeadStr, arrowHeadStrs)) {
				return arrowHead;
			}
		}
		return null;
	}

	/**
	 * Returns the prioritized GPML2013a arrowHead type string for given GPML2021
	 * Interaction Panel arrow head type.
	 * 
	 * @param arrowHead the interaction panel arrow head type for GPML2021e.
	 * @return the first GPML2013a arrow head which corresponds to the interaction
	 *         panel arrow head type, or null if no corresponding type exists.
	 * @throws ConverterException
	 */
	protected String getArrowHeadTypeStr(ArrowHeadType arrowHead) throws ConverterException {
		List<String> arrowHeadStrs = IA_PANEL_MAP.get(arrowHead);
		if (arrowHeadStrs != null && !arrowHeadStrs.isEmpty()) {
			// first arrow head string is priority.
			return arrowHeadStrs.get(0);
		} else {
			return null;
		}
	}

	private static final Map<String, AttributeInfo> ATTRIBUTE_INFO = initAttributeInfo();

	private static Map<String, AttributeInfo> initAttributeInfo() {
		Map<String, AttributeInfo> result = new HashMap<String, AttributeInfo>();
		result.put("Comment@Source", new AttributeInfo("xsd:string", null, "optional"));
		result.put("PublicationXref@ID", new AttributeInfo("xsd:string", null, "required"));
		result.put("PublicationXref@Database", new AttributeInfo("xsd:string", null, "required"));
		result.put("Attribute@Key", new AttributeInfo("xsd:string", null, "required"));
		result.put("Attribute@Value", new AttributeInfo("xsd:string", null, "required"));
		result.put("Pathway.Graphics@BoardWidth", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Pathway.Graphics@BoardHeight", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Pathway@Name", new AttributeInfo("xsd:string", null, "required"));
		result.put("Pathway@Organism", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Data-Source", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Version", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Author", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Maintainer", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Email", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@License", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@Last-Modified", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Pathway@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("DataNode.Graphics@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("DataNode.Graphics@CenterY", new AttributeInfo("xsd:float", null, "required"));
		result.put("DataNode.Graphics@Width", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("DataNode.Graphics@Height", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("DataNode.Graphics@FontName", new AttributeInfo("xsd:string", "Arial", "optional"));
		result.put("DataNode.Graphics@FontStyle", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("DataNode.Graphics@FontDecoration", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("DataNode.Graphics@FontStrikethru", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("DataNode.Graphics@FontWeight", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("DataNode.Graphics@FontSize", new AttributeInfo("xsd:nonNegativeInteger", "12", "optional"));
		result.put("DataNode.Graphics@Align", new AttributeInfo("xsd:string", "Center", "optional"));
		result.put("DataNode.Graphics@Valign", new AttributeInfo("xsd:string", "Top", "optional"));
		result.put("DataNode.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("DataNode.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("DataNode.Graphics@LineThickness", new AttributeInfo("xsd:float", "1.0", "optional"));
		result.put("DataNode.Graphics@FillColor", new AttributeInfo("gpml:ColorType", "White", "optional"));
		result.put("DataNode.Graphics@ShapeType", new AttributeInfo("xsd:string", "Rectangle", "optional"));
		result.put("DataNode.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("DataNode.Xref@Database", new AttributeInfo("xsd:string", null, "required"));
		result.put("DataNode.Xref@ID", new AttributeInfo("xsd:string", null, "required"));
		result.put("DataNode@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("DataNode@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("DataNode@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("DataNode@TextLabel", new AttributeInfo("xsd:string", null, "required"));
		result.put("DataNode@Type", new AttributeInfo("xsd:string", "Unknown", "optional"));
		result.put("State.Graphics@RelX", new AttributeInfo("xsd:float", null, "required"));
		result.put("State.Graphics@RelY", new AttributeInfo("xsd:float", null, "required"));
		result.put("State.Graphics@Width", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("State.Graphics@Height", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("State.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("State.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("State.Graphics@LineThickness", new AttributeInfo("xsd:float", "1.0", "optional"));
		result.put("State.Graphics@FillColor", new AttributeInfo("gpml:ColorType", "White", "optional"));
		result.put("State.Graphics@ShapeType", new AttributeInfo("xsd:string", "Rectangle", "optional"));
		result.put("State.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("State.Xref@Database", new AttributeInfo("xsd:string", null, "required"));
		result.put("State.Xref@ID", new AttributeInfo("xsd:string", null, "required"));
		result.put("State@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("State@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("State@GraphRef", new AttributeInfo("xsd:IDREF", null, "optional"));
		result.put("State@TextLabel", new AttributeInfo("xsd:string", null, "required"));
		result.put("State@StateType", new AttributeInfo("xsd:string", "Unknown", "optional"));
		result.put("GraphicalLine.Graphics.Point@X", new AttributeInfo("xsd:float", null, "required"));
		result.put("GraphicalLine.Graphics.Point@Y", new AttributeInfo("xsd:float", null, "required"));
		result.put("GraphicalLine.Graphics.Point@RelX", new AttributeInfo("xsd:float", null, "optional"));
		result.put("GraphicalLine.Graphics.Point@RelY", new AttributeInfo("xsd:float", null, "optional"));
		result.put("GraphicalLine.Graphics.Point@GraphRef", new AttributeInfo("xsd:IDREF", null, "optional"));
		result.put("GraphicalLine.Graphics.Point@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("GraphicalLine.Graphics.Point@ArrowHead", new AttributeInfo("xsd:string", "Line", "optional"));
		result.put("GraphicalLine.Graphics.Anchor@Position", new AttributeInfo("xsd:float", null, "required"));
		result.put("GraphicalLine.Graphics.Anchor@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("GraphicalLine.Graphics.Anchor@Shape", new AttributeInfo("xsd:string", "ReceptorRound", "optional"));
		result.put("GraphicalLine.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("GraphicalLine.Graphics@LineThickness", new AttributeInfo("xsd:float", null, "optional"));
		result.put("GraphicalLine.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("GraphicalLine.Graphics@ConnectorType", new AttributeInfo("xsd:string", "Straight", "optional"));
		result.put("GraphicalLine.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("GraphicalLine@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("GraphicalLine@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("GraphicalLine@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("GraphicalLine@Type", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Interaction.Graphics.Point@X", new AttributeInfo("xsd:float", null, "required"));
		result.put("Interaction.Graphics.Point@Y", new AttributeInfo("xsd:float", null, "required"));
		result.put("Interaction.Graphics.Point@RelX", new AttributeInfo("xsd:float", null, "optional"));
		result.put("Interaction.Graphics.Point@RelY", new AttributeInfo("xsd:float", null, "optional"));
		result.put("Interaction.Graphics.Point@GraphRef", new AttributeInfo("xsd:IDREF", null, "optional"));
		result.put("Interaction.Graphics.Point@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Interaction.Graphics.Point@ArrowHead", new AttributeInfo("xsd:string", "Line", "optional"));
		result.put("Interaction.Graphics.Anchor@Position", new AttributeInfo("xsd:float", null, "required"));
		result.put("Interaction.Graphics.Anchor@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Interaction.Graphics.Anchor@Shape", new AttributeInfo("xsd:string", "ReceptorRound", "optional"));
		result.put("Interaction.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("Interaction.Graphics@LineThickness", new AttributeInfo("xsd:float", null, "optional"));
		result.put("Interaction.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("Interaction.Graphics@ConnectorType", new AttributeInfo("xsd:string", "Straight", "optional"));
		result.put("Interaction.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("Interaction.Xref@Database", new AttributeInfo("xsd:string", null, "required"));
		result.put("Interaction.Xref@ID", new AttributeInfo("xsd:string", null, "required"));
		result.put("Interaction@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Interaction@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Interaction@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Interaction@Type", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Label.Graphics@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("Label.Graphics@CenterY", new AttributeInfo("xsd:float", null, "required"));
		result.put("Label.Graphics@Width", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Label.Graphics@Height", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Label.Graphics@FontName", new AttributeInfo("xsd:string", "Arial", "optional"));
		result.put("Label.Graphics@FontStyle", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Label.Graphics@FontDecoration", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Label.Graphics@FontStrikethru", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Label.Graphics@FontWeight", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Label.Graphics@FontSize", new AttributeInfo("xsd:nonNegativeInteger", "12", "optional"));
		result.put("Label.Graphics@Align", new AttributeInfo("xsd:string", "Center", "optional"));
		result.put("Label.Graphics@Valign", new AttributeInfo("xsd:string", "Top", "optional"));
		result.put("Label.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("Label.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("Label.Graphics@LineThickness", new AttributeInfo("xsd:float", "1.0", "optional"));
		result.put("Label.Graphics@FillColor", new AttributeInfo("gpml:ColorType", "Transparent", "optional"));
		result.put("Label.Graphics@ShapeType", new AttributeInfo("xsd:string", "None", "optional"));
		result.put("Label.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("Label@Href", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Label@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Label@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Label@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Label@TextLabel", new AttributeInfo("xsd:string", null, "required"));
		result.put("Shape.Graphics@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("Shape.Graphics@CenterY", new AttributeInfo("xsd:float", null, "required"));
		result.put("Shape.Graphics@Width", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Shape.Graphics@Height", new AttributeInfo("gpml:Dimension", null, "required"));
		result.put("Shape.Graphics@FontName", new AttributeInfo("xsd:string", "Arial", "optional"));
		result.put("Shape.Graphics@FontStyle", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Shape.Graphics@FontDecoration", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Shape.Graphics@FontStrikethru", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Shape.Graphics@FontWeight", new AttributeInfo("xsd:string", "Normal", "optional"));
		result.put("Shape.Graphics@FontSize", new AttributeInfo("xsd:nonNegativeInteger", "12", "optional"));
		result.put("Shape.Graphics@Align", new AttributeInfo("xsd:string", "Center", "optional"));
		result.put("Shape.Graphics@Valign", new AttributeInfo("xsd:string", "Top", "optional"));
		result.put("Shape.Graphics@Color", new AttributeInfo("gpml:ColorType", "Black", "optional"));
		result.put("Shape.Graphics@LineStyle", new AttributeInfo("gpml:StyleType", "Solid", "optional"));
		result.put("Shape.Graphics@LineThickness", new AttributeInfo("xsd:float", "1.0", "optional"));
		result.put("Shape.Graphics@FillColor", new AttributeInfo("gpml:ColorType", "Transparent", "optional"));
		result.put("Shape.Graphics@ShapeType", new AttributeInfo("xsd:string", null, "required"));
		result.put("Shape.Graphics@ZOrder", new AttributeInfo("xsd:integer", null, "optional"));
		result.put("Shape.Graphics@Rotation", new AttributeInfo("gpml:RotationType", "Top", "optional"));
		result.put("Shape@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Shape@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("Shape@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Shape@TextLabel", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Group@BiopaxRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Group@GroupId", new AttributeInfo("xsd:string", null, "required"));
		result.put("Group@GroupRef", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Group@Style", new AttributeInfo("xsd:string", "None", "optional"));
		result.put("Group@TextLabel", new AttributeInfo("xsd:string", null, "optional"));
		result.put("Group@GraphId", new AttributeInfo("xsd:ID", null, "optional"));
		result.put("InfoBox@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("InfoBox@CenterY", new AttributeInfo("xsd:float", null, "required"));
		result.put("Legend@CenterX", new AttributeInfo("xsd:float", null, "required"));
		result.put("Legend@CenterY", new AttributeInfo("xsd:float", null, "required"));
		return result;
	}

	/**
	 * A {@link Map} collection that contains {@link String} as key and
	 * {@link AttributeInfo} as value.
	 */
	protected Map<String, AttributeInfo> getAttributeInfo() {
		return ATTRIBUTE_INFO;
	}

	/**
	 * Name of resource containing the gpml schema definition.
	 */
	protected static class AttributeInfo {
		/**
		 * xsd validated type. Note that in the current implementation we don't do
		 * anything with restrictions, only with the base type.
		 */
		public String schemaType;

		/**
		 * default value for the attribute
		 */
		public String def; // default

		/**
		 * use of the attribute: can be "required" or "optional"
		 */
		public String use;

		/**
		 * Creates an object containing the gpml schema definition of a given attribute.
		 * 
		 * @param aSchemaType the xsd validated type of the attribute.
		 * @param aDef        the default value for the attribute.
		 * @param aUse        the use of the attribute.
		 */
		AttributeInfo(String aSchemaType, String aDef, String aUse) {
			schemaType = aSchemaType;
			def = aDef;
			use = aUse;
		}
	}

	/**
	 * Returns true if given string value and default value are equal.
	 * 
	 * @param def   the default string.
	 * @param value the given string.
	 * @return true if the specified arguments are equal, or both null.
	 */
	private boolean isEqualsString(String def, String value) {
		return ((def == null && value == null) || (def != null && def.equals(value))
				|| (def == null && value != null && value.equals("")));
	}

	/**
	 * Returns true if given string value and default value are numerically equal.
	 * 
	 * @param def   the string for default number value.
	 * @param value the string for given number value.
	 * @return true if absolute value of difference between def and value is less
	 *         than 1e-6, and false otherwise.
	 */
	private boolean isEqualsNumber(String def, String value) {
		if (def != null && value != null) {
			Double x = Double.parseDouble(def);
			Double y = Double.parseDouble(value);
			if (Math.abs(x - y) < 1e-6)
				return true;
		}
		return false;
	}

	/**
	 * Returns true if given value and default value are the same color object.
	 * 
	 * @param def   the string for default color object.
	 * @param value the string for given color object.
	 * @return
	 */
	private boolean isEqualsColor(String def, String value) {
		if (def != null && value != null) {
			boolean aTrans = "Transparent".equals(def);
			boolean bTrans = "Transparent".equals(value);
			Color a = ColorUtils.stringToColor(def);
			Color b = ColorUtils.stringToColor(value);
			return (a.equals(b) && aTrans == bTrans);
		}
		return def == null && value == null;
	}

	/**
	 * Sets a certain attribute value, performs a basic check for some types, and
	 * throws an exception if trying to set an invalid value. If trying to set a
	 * default value or an optional value to null, the attribute is omitted, which
	 * results in a leaner xml output.
	 *
	 * @param tag   used for lookup in the defaults table.
	 * @param name  used for lookup in the defaults table.
	 * @param el    jdom element where this attribute belongs in.
	 * @param value value you want to check and set.
	 * @throws ConverterException if value invalid.
	 */
	protected void setAttr(String tag, String name, Element el, String value) throws ConverterException {
		String key = tag + "@" + name;
		// throw exception for value invalid
		if (!getAttributeInfo().containsKey(key))
			throw new ConverterException("Trying to set invalid attribute " + key);
		AttributeInfo aInfo = getAttributeInfo().get(key);
		boolean isDefault = false;
		// if attribute equal to the default value, leave out from the jdom
		if (aInfo.use.equals("optional")) {
			if (aInfo.schemaType.equals("xsd:string") || aInfo.schemaType.equals("xsd:ID")
					|| aInfo.schemaType.equals("gpml:StyleType")) {
				isDefault = isEqualsString(aInfo.def, value);
			} else if (aInfo.schemaType.equals("xsd:float") || aInfo.schemaType.equals("Dimension")) {
				isDefault = isEqualsNumber(aInfo.def, value);
			} else if (aInfo.schemaType.equals("gpml:ColorType")) {
				isDefault = isEqualsColor(aInfo.def, value);
			}
		}
		if (!isDefault)
			el.setAttribute(name, value);
	}

	/**
	 * Gets a certain attribute value, and replaces it with a suitable default under
	 * certain conditions.
	 *
	 * @param tag  used for lookup in the defaults table.
	 * @param name used for lookup in the defaults table.
	 * @param el   jdom element to get the attribute from.
	 * @throws ConverterException if {@link getAttributeInfo} does not contain a
	 *                            mapping for the specified key.
	 */
	protected String getAttr(String tag, String name, Element el) throws ConverterException {
		String key = tag + "@" + name;
		if (!getAttributeInfo().containsKey(key))
			throw new ConverterException("Trying to get invalid attribute " + key);
		AttributeInfo aInfo = getAttributeInfo().get(key);
		String result = ((el == null) ? aInfo.def : el.getAttributeValue(name, aInfo.def));
		return result;
	}

	/**
	 * Removes group from pathwayModel if empty. Check executed after reading and
	 * before writing.
	 * 
	 * @param pathwayModel the pathway model.
	 * @throws ConverterException
	 */
	protected void removeEmptyGroups(PathwayModel pathwayModel) throws ConverterException {
		List<Group> groups = pathwayModel.getGroups();
		List<Group> groupsToRemove = new ArrayList<Group>();
		for (Group group : groups) {
			if (group.getPathwayElements().isEmpty()) {
				groupsToRemove.add(group);
			}
		}
		for (Group groupToRemove : groupsToRemove) {
			Logger.log.trace("Warning: Removed empty group " + groupToRemove.getElementId());
			pathwayModel.removeGroup(groupToRemove);
		}
	}

	/**
	 * validates a JDOM document against the xml-schema definition specified by
	 * 'xsdFile'
	 * 
	 * @param doc the document to validate
	 */
	public void validateDocument(Document doc) throws ConverterException {
		ClassLoader cl = PathwayModel.class.getClassLoader();
		InputStream is = cl.getResourceAsStream(xsdFile);
		if (is != null) {
			Schema schema;
			try {
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				StreamSource ss = new StreamSource(is);
				schema = factory.newSchema(ss);
				ValidatorHandler vh = schema.newValidatorHandler();
				SAXOutputter so = new SAXOutputter(vh);
				so.output(doc);
				// If no errors occur, the file is valid according to the gpml xml schema
				// definition
				Logger.log
						.info("Document is valid according to the xml schema definition '" + xsdFile.toString() + "'");
			} catch (SAXException se) {
				Logger.log.error("Could not parse the xml-schema definition", se);
				throw new ConverterException(se);
			} catch (JDOMException je) {
				Logger.log.error("Document is invalid according to the xml-schema definition!: " + je.getMessage(), je);
				XMLOutputter xmlcode = new XMLOutputter(Format.getPrettyFormat());

				Logger.log.error("The invalid XML code:\n" + xmlcode.outputString(doc));
				throw new ConverterException(je);
			}
		} else {
			Logger.log.error("Document is not validated because the xml schema definition '" + xsdFile
					+ "' could not be found in classpath");
			throw new ConverterException("Document is not validated because the xml schema definition '" + xsdFile
					+ "' could not be found in classpath");
		}
	}

//	/**
//	 * Default order of pathway elements gene product, label, shape and line
//	 * determined by GenMAPP legacy
//	 */
//	private static final int Z_ORDER_GROUP = 0x1000; // groups behind other graphics
//	private static final int Z_ORDER_GENEPRODUCT = 0x8000;
//	private static final int Z_ORDER_LABEL = 0x7000;
//	private static final int Z_ORDER_SHAPE = 0x4000;
//	private static final int Z_ORDER_LINE = 0x3000;
//	// default order of uninteresting elements.
//	private static final int Z_ORDER_DEFAULT = 0x0000;
//
//	/**
//	 * Pathway element types as used in GPML2013a
//	 */
//	public final static String SHAPE = "Shape";
//	public final static String GRAPHLINE = "GraphicalLine";
//	public final static String DATANODE = "DataNode";
//	public final static String LABEL = "Label";
//	public final static String LINE = "Line";
//	public final static String LEGEND = "Legend";
//	public final static String INFOBOX = "InfoBox";
//	public final static String MAPPINFO = "Pathway";
//	public final static String GROUP = "Group";
//	public final static String BIOPAX = "Biopax";
//	public final static String STATE = "State";
//
//	/**
//	 * default z order for newly created objects
//	 */
//	private static int getDefaultZOrder(String pathwayElementType) {
//		switch (pathwayElementType) {
//		case SHAPE:
//			return Z_ORDER_SHAPE;
//		case STATE:
//			return Z_ORDER_GENEPRODUCT + 10;
//		case DATANODE:
//			return Z_ORDER_GENEPRODUCT;
//		case LABEL:
//			return Z_ORDER_LABEL;
//		case LINE:
//			return Z_ORDER_LINE;
//		case GRAPHLINE:
//			return Z_ORDER_LINE;
//		case LEGEND:
//		case INFOBOX:
//		case MAPPINFO:
//		case BIOPAX:
//			return Z_ORDER_DEFAULT;
//		case GROUP:
//			return Z_ORDER_GROUP;
//		default:
//			throw new IllegalArgumentException("Invalid object type " + pathwayElementType);
//		}
//	}
}