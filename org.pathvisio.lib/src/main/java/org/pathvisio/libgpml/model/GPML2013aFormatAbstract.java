/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.libgpml.model;

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

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.SAXOutputter;
import org.jdom2.output.XMLOutputter;
import org.pathvisio.libgpml.debug.Logger;
import org.pathvisio.libgpml.io.ConverterException;
import org.pathvisio.libgpml.model.shape.ShapeType;
import org.pathvisio.libgpml.model.type.ArrowHeadType;
import org.pathvisio.libgpml.util.ColorUtils;
import org.pathvisio.libgpml.util.MiscUtils;
import org.xml.sax.SAXException;

/**
 * Abstract class for GPML2013a format. Contains static properties
 * {@link String}, {@link Map}, {@link BidiMap}, {@link List}, and methods used
 * in reading or writing GPML2013a.
 * 
 * @author finterly
 */
public abstract class GPML2013aFormatAbstract {

	/**
	 * The namespace
	 */
	private final Namespace nsGPML;

	/**
	 * The schema file
	 */
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

	// ================================================================================
	// Static Variables
	// ================================================================================
	/**
	 * In GPML2013a, specific {@link Namespace} are defined for Biopax elements.
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
	 * Some GPML2013a properties are removed from GPML2021 and therefore cannot be
	 * mapped to the Java model. These deprecated properties are stored in dynamic
	 * properties with the following static strings as keys.
	 */
	public final static String PATHWAY_AUTHOR = "pathway_author_gpml2013a";
	public final static String PATHWAY_MAINTAINER = "pathway_maintainer_gpml2013a";
	public final static String PATHWAY_EMAIL = "pathway_email_gpml2013a";
	public final static String PATHWAY_LASTMODIFIED = "pathway_lastModified_gpml2013a";
	public final static String INFOBOX_CENTER_X = "pathway_infobox_centerX_gpml2013a";
	public final static String INFOBOX_CENTER_Y = "pathway_infobox_centerY_gpml2013a";
	public final static String LEGEND_CENTER_X = "pathway_legend_centerX_gpml2013a";
	public final static String LEGEND_CENTER_Y = "pathway_legend_centerY_gpml2013a";

	/**
	 * This {@link Set} stores the deprecated GPML2013a properties. Dynamic
	 * properties with these keys are ignored when writing GPML2013a
	 * {@link GPML2013aWriter#writePathwayDynamicProperties} and GPML2021
	 * {@link GPML2021Writer#writeDynamicProperties}.
	 */
	public static final Set<String> GPML2013A_KEY_SET = new HashSet<>(Arrays.asList(PATHWAY_AUTHOR, PATHWAY_MAINTAINER,
			PATHWAY_EMAIL, PATHWAY_LASTMODIFIED, INFOBOX_CENTER_X, INFOBOX_CENTER_Y, LEGEND_CENTER_X, LEGEND_CENTER_Y));

	/**
	 * In GPML2013a, {@link Pathway} description is written as a
	 * {@link PathwayElement.Comment} with source="WikiPathways-description".
	 */
	public final static String WP_DESCRIPTION = "WikiPathways-description";

	/**
	 * In GPML2013a, Double LineStyleType, Cellular Component Shape Types, and State
	 * rotation were stored as a dynamic properties using the following String keys.
	 */
	public final static String DOUBLE_LINE_KEY = "org.pathvisio.DoubleLineProperty";
	public final static String CELL_CMPNT_KEY = "org.pathvisio.CellularComponentProperty";
	public final static String STATE_ROTATION = "org.pathvisio.core.StateRotation";

	/**
	 * This {@link BidiMap}is used for mapping {@link ShapeType} Strings to their
	 * new camelCase spelling for reading and writing GPML2013a.
	 */
	public static final BidiMap<String, String> SHAPETYPE_TO_CAMELCASE = new DualHashBidiMap<>();
	static {
		SHAPETYPE_TO_CAMELCASE.put("Sarcoplasmic Reticulum", "SarcoplasmicReticulum");
		SHAPETYPE_TO_CAMELCASE.put("Endoplasmic Reticulum", "EndoplasmicReticulum");
		SHAPETYPE_TO_CAMELCASE.put("Golgi Apparatus", "GolgiApparatus");
		SHAPETYPE_TO_CAMELCASE.put("Cytosol region", "CytosolRegion");
		SHAPETYPE_TO_CAMELCASE.put("Extracellular region", "ExtracellularRegion");
	}

	/**
	 * Converts shapeType {@link String} to UpperCamelCase convention. In GPML2013a,
	 * naming convention was inconsistent. Moving forward, enum types strings are
	 * all in UpperCamelCase.
	 * 
	 * @param str the string.
	 * @return the string in camelCase format, or string as it was.
	 * @throws ConverterException
	 */
	protected String toCamelCase(String str) throws ConverterException {
		if (SHAPETYPE_TO_CAMELCASE.containsKey(str)) {
			return SHAPETYPE_TO_CAMELCASE.get(str);
		} else
			return str;
	}

	/**
	 * Converts shapeType {@link String} from UpperCamelCase convention back to its
	 * original appearance in GPML2013a.
	 * 
	 * @param str the string.
	 * @return the string in its original format.
	 * @throws ConverterException
	 */
	protected String fromCamelCase(String str) throws ConverterException {
		if (SHAPETYPE_TO_CAMELCASE.containsValue(str)) {
			return SHAPETYPE_TO_CAMELCASE.getKey(str);
		} else
			return str;
	}

	/**
	 * This {@link Map} maps deprecated {@link ShapeType} to the new shape types
	 * when reading GPML2013a {@link GPML2013aReader#readShapeStyleProperty}.
	 * However, if the pathway element has dynamic property with
	 * {@link #CELL_CMPNT_KEY}, shapeType may be overridden after reading dynamic
	 * properties.
	 */
	public static final Map<ShapeType, ShapeType> DEPRECATED_MAP = new HashMap<ShapeType, ShapeType>();
	static {
		DEPRECATED_MAP.put(ShapeType.CELL, ShapeType.ROUNDED_RECTANGLE);
		DEPRECATED_MAP.put(ShapeType.ORGANELLE, ShapeType.ROUNDED_RECTANGLE);
		DEPRECATED_MAP.put(ShapeType.MEMBRANE, ShapeType.ROUNDED_RECTANGLE);
		DEPRECATED_MAP.put(ShapeType.CELLA, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.NUCLEUS, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.ORGANA, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.ORGANB, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.ORGANC, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.VESICLE, ShapeType.OVAL);
		DEPRECATED_MAP.put(ShapeType.PROTEINB, ShapeType.HEXAGON);
		DEPRECATED_MAP.put(ShapeType.RIBOSOME, ShapeType.HEXAGON);
	}

	/**
	 * This cellular component {@link Map} maps {@link ShapeType}s. In GPML2013a,
	 * cellular component shapeTypes are written as dynamic properties
	 * {@link GPML2013aWriter#writeShapedOrStateDynamicProperties} with
	 * {@link #CELL_CMPNT_KEY} key and a value (e.g. "Nucleus); and property
	 * shapeType in Graphics is written with a corresponding shapeType value (e.g.
	 * "Oval") {@link GPML2013aWriter#writeShapeStyleProperty}.
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
	 * This {@link BidiMap} maps GPML2013a openControlledVocabulary Ontology types
	 * to their {@link DataSource} Prefix for reading
	 * {@link GPML2013aReader#readOpenControlledVocabulary} and writing
	 * {@link GPML2013aWriter#writeOpenControlledVocabulary}.
	 */
	public static final BidiMap<String, String> OCV_ONTOLOGY_MAP = new DualHashBidiMap<>();
	static {
		OCV_ONTOLOGY_MAP.put("Disease", "DOID");
		OCV_ONTOLOGY_MAP.put("Pathway Ontology", "PW");
		OCV_ONTOLOGY_MAP.put("Cell Type", "CL");
	}

	/**
	 * String values for {@link DataNode.State} phosphosite
	 * {@link PathwayElement.Comment} information in GPML2013a.
	 */
	public final static String PARENT = "parent";
	public final static String POSITION = "position";
	public final static String PARENTID = "parentid";
	public final static String PARENTSYMBOL = "parentsymbol";
	public final static String PTM = "ptm";
	public final static String DIRECTION = "direction";
	public final static String SITE = "site";
	public final static String SITEGRPID = "sitegrpid";

	public final static String PARENTID_DB = "uniprot";
	public final static String PARENTSYMBOL_DB = "hgnc";
	public final static String SITEGRPID_DB = "phosphositeplus";

	/**
	 * This {@link Set} contains known phosphosite related annotation types for
	 * {@link DataNode.State} phosphosite {@link PathwayElement.Comment} in
	 * GPML2013a. This set is used in determining whether a state comment should be
	 * written as {@link Annotation}s and {@link Xref} in
	 * {@link GPML2013aReader#convertStateCommentToRefs}.
	 */
	Set<String> STATE_REF_LIST = new HashSet<>(
			Arrays.asList(PARENT, POSITION, PTM, DIRECTION, PARENTID, PARENTSYMBOL, SITE, SITEGRPID));

	/**
	 * This {@link Map} for {@link DataNode.State} phosphosite
	 * {@link PathwayElement.Comment} maps PTM character to {@link Annotation} and
	 * {@link Xref} information. E.g. for ptm=p, Annotation value=Phosphorylation,
	 * Xref identifier=0000216, and dataSource = SBO. Used in writing state comments
	 * to annotations and xref {@link GPML2013aReader#convertStateCommentToRefs}.
	 */
	public static final Map<String, List<String>> STATE_PTM_MAP = new HashMap<String, List<String>>();
	static {
		STATE_PTM_MAP.put("p", new ArrayList<>(Arrays.asList("Phosphorylation", "0000216", "SBO")));
		STATE_PTM_MAP.put("m", new ArrayList<>(Arrays.asList("Methylation", "0000214", "SBO")));
		STATE_PTM_MAP.put("me", new ArrayList<>(Arrays.asList("Methylation", "0000214", "SBO")));
		STATE_PTM_MAP.put("u", new ArrayList<>(Arrays.asList("Ubiquitination", "000022", "SBO")));
		STATE_PTM_MAP.put("ub", new ArrayList<>(Arrays.asList("Ubiquitination", "000022", "SBO")));
	}

	/**
	 * Map for {@link DataNode.State} phosphosite {@link PathwayElement.Comment}
	 * direction character to {@link Annotation} and {@link Xref} information. "u"
	 * for up-regulated and "d" for down-regulated. Used in writing state comments
	 * to annotations and xref {@link GPML2013aReader#convertStateCommentToRefs}.
	 */
	public static final Map<String, List<String>> STATE_DIRECTION_MAP = new HashMap<String, List<String>>();
	static {
		STATE_DIRECTION_MAP.put("u",
				new ArrayList<>(Arrays.asList("positive regulation of biological process", "0048518", "GO")));
		STATE_DIRECTION_MAP.put("d",
				new ArrayList<>(Arrays.asList("negative regulation of biological process", "0048519", "GO")));
	}

	/**
	 * In GPML2013a, we introduce a new Interaction Panel of {@link ArrowHeadType}.
	 * For each new arrowHead type we define a {@link List} of the old arrowHead
	 * types from GPML2013a which correspond to it. The first GPML2013a arrow head
	 * type string in the list is prioritized when writing from GPML2021 to
	 * GPML2013a.
	 */
	public static final List<String> UNDIRECTED_LIST = new ArrayList<>(Arrays.asList("Line"));
	public static final List<String> DIRECTED_LIST = new ArrayList<>(Arrays.asList("Arrow"));
	public static final List<String> CONVERSION_LIST = new ArrayList<>(Arrays.asList("mim-conversion",
			"mim-modification", "mim-cleavage", "mim-gap", "mim-branching-left", "mim-branching-right"));
	public static final List<String> INHIBITION_LIST = new ArrayList<>(Arrays.asList("mim-inhibition", "TBar"));
	public static final List<String> CATALYSIS_LIST = new ArrayList<>(Arrays.asList("mim-catalysis"));
	public static final List<String> STIMULATION_LIST = new ArrayList<>(
			Arrays.asList("mim-stimulation", "mim-necessary-stimulation"));
	public static final List<String> BINDING_LIST = new ArrayList<>(Arrays.asList("mim-binding", "mim-covalent-bond"));
	public static final List<String> TRANSLOCATION_LIST = new ArrayList<>(Arrays.asList("mim-translocation"));
	public static final List<String> TRANSCRIPTION_TRANSLATION_LIST = new ArrayList<>(
			Arrays.asList("mim-transcription-translation"));

	/**
	 * This {@link Map} maps new Interaction Panel arrow head types to the defined
	 * {@link List} for corresponding GPML2013a arrowHead types.
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

	/**
	 * Attribute info map is initiated with {@link #initAttributeInfo()}.
	 * 
	 */
	private static final Map<String, AttributeInfo> ATTRIBUTE_INFO = initAttributeInfo();

	/**
	 * The {@link Map} initAttributeInfo maps {@link String} tag to
	 * {@link AttributeInfo}. For GPML2013a reading/writing, we often use
	 * {@link #getAttr} and {@link #setAttr} in place of standard jdom methods
	 * {@link Element#getAttributeValue} and {@link Element#setAttribute}
	 * respectively. If an attribute is null when reading, its default value is
	 * fetched from this map. When writing, if trying to set a default value or an
	 * optional value to null, the attribute is omitted which results in a leaner
	 * xml output.
	 * 
	 * This map defines custom default values not in the GPML2013a schema such as
	 * default "Label.Graphics@FillColor" as "Transparent". We do not do this for
	 * GPML2021 as it can be confusing to have custom reading/writing resulting in
	 * xml which do not adhere to the schema.
	 * 
	 * @return
	 */
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
	 * Returns {@link Map} ATTRIBUTE_INFO collection that contains {@link String} as
	 * key and {@link AttributeInfo} as value.
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
	 * @return true if color is equal, false otherwise.
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
	 * results in a leaner xml output. This customized method is often used in place
	 * of {@link Element#setAttribute} for writing GPML2013a.
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
	 * certain conditions. This customized method is often used in place of
	 * {@link Element#getAttributeValue} for reading GPML2013a.
	 *
	 * @param tag  used for lookup in the defaults table.
	 * @param name used for lookup in the defaults table.
	 * @param el   jdom element to get the attribute from.
	 * @throws ConverterException if {@link #getAttributeInfo} does not contain a
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
}