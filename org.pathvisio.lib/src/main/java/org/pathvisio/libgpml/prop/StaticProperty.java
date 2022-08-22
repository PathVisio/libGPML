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

package org.pathvisio.libgpml.prop;

import java.util.HashMap;
import java.util.Map;

import org.pathvisio.libgpml.model.PathwayObject;

/**
 * Static properties for ObjectTypes, linked in {@link PathwayObject}.
 * 
 * // TODO Tag name must be unique...Does it matter what numbers?
 * 
 * @author unknown, finterly
 */
public enum StaticProperty implements Property {

	// ========================================
	// Pathway Info
	// ========================================
	TITLE("title", "Title", StaticPropertyType.STRING, 101),
	ORGANISM("organism", "Organism", StaticPropertyType.ORGANISM, 102),
	DESCRIPTION("description", "Description", StaticPropertyType.DESCRIPTION, 103),
	SOURCE("source", "Source", StaticPropertyType.STRING, 104),
	VERSION("version", "Version", StaticPropertyType.STRING, 105),
	LICENSE("license", "License", StaticPropertyType.STRING, 106),
	AUTHOR("author", "Author", StaticPropertyType.AUTHOR, 107),

	// ========================================
	// Pathway Object
	// ========================================
	ELEMENTID("elementId", "ElementId", StaticPropertyType.STRING, 120, false, true, false),

	// ========================================
	// Pathway Element
	// ========================================
	COMMENT("comment", "Comment", StaticPropertyType.COMMENT, 130),
	ANNOTATIONREF("annotationRef", "AnnotationRef", StaticPropertyType.ANNOTATIONREF, 131, false, true, false),
	CITATIONREF("citationRef", "CitationRef", StaticPropertyType.CITATIONREF, 132, false, true, false),
	EVIDENCEREF("evidenceRef", "EvidenceRef", StaticPropertyType.EVIDENCEREF, 133, false, true, false),

	// types
	ANNOTATIONTYPE("annotationType", "Annotation Type", StaticPropertyType.ANNOTATIONTYPE, 140),

	// ========================================
	// Xrefable
	// ========================================
	XREF("xref", "Database:Id", StaticPropertyType.XREF, 150),
	
	// ========================================
	// Pathway Canvas Graphics
	// ========================================
	BOARDWIDTH("boardWidth", "Board Width", StaticPropertyType.DOUBLE, 160, true, true, false),
	BOARDHEIGHT("boardHeight", "Board Height", StaticPropertyType.DOUBLE, 161, true, true, false),
	BACKGROUNDCOLOR("backgroundColor", "Background Color", StaticPropertyType.COLOR, 162),

	// ========================================
	// Groupable
	// ========================================
	GROUPREF("groupRef", "GroupRef", StaticPropertyType.STRING, 170, false, true, false),
	ZORDER("zOrder", "Z-Order", StaticPropertyType.INTEGER, 171, false, true, false),

	// ========================================
	// ShapedElement
	// ========================================
	// common properties
	TEXTLABEL("textLabel", "Text Label", StaticPropertyType.STRING, 180),

	// types
	DATANODETYPE("dataNodeType", "DataNode Type", StaticPropertyType.DATANODETYPE, 190),
	STATETYPE("stateType", "State Type", StaticPropertyType.STATETYPE, 191),
	GROUPTYPE("groupType", "Group Type", StaticPropertyType.GROUPTYPE, 192),

	// datanode
	ALIASREF("aliasRef", "Linked to Group", StaticPropertyType.ALIASREF, 200, false, false, false), // TODO

	// state
	RELX("relX", "Relative X", StaticPropertyType.DOUBLE, 210, true, false, false),
	RELY("relY", "Relative Y", StaticPropertyType.DOUBLE, 211, true, false, false),

	// label
	HREF("href", "Hyperlink", StaticPropertyType.STRING, 220),

	// rect properties
	CENTERX("centerX", "Center X", StaticPropertyType.DOUBLE, 240, true, false, false),
	CENTERY("centerY", "Center Y", StaticPropertyType.DOUBLE, 241, true, false, false),
	WIDTH("width", "Width", StaticPropertyType.DOUBLE, 242, true, false, false),
	HEIGHT("height", "Height", StaticPropertyType.DOUBLE, 243, true, false, false),

	// font properties
	TEXTCOLOR("textColor", "Text Color", StaticPropertyType.COLOR, 250), // TODO number
	FONTNAME("fontName", "Font Name", StaticPropertyType.FONTNAME, 251),
	FONTWEIGHT("fontWeight", "Bold", StaticPropertyType.BOOLEAN, 252),
	FONTSTYLE("fontStyle", "Italic", StaticPropertyType.BOOLEAN, 253),
	FONTDECORATION("fontDecoration", "Underline", StaticPropertyType.BOOLEAN, 254), // TODO number
	FONTSTRIKETHRU("fontStrikeThru", "Strikethrough", StaticPropertyType.BOOLEAN, 255), // TODO number
	FONTSIZE("fontSize", "Font Size", StaticPropertyType.DOUBLE, 256), // TODO used to be double
	VALIGN("vAlign", "Vertical Alignment", StaticPropertyType.VALIGNTYPE, 257),
	HALIGN("hAlign", "Horizontal Alignment", StaticPropertyType.HALIGNTYPE, 258),

	// shape style properties
	BORDERCOLOR("borderColor", "Border Color", StaticPropertyType.COLOR, 270),
	BORDERSTYLE("borderStyle", "Border Style", StaticPropertyType.LINESTYLETYPE, 271),
	BORDERWIDTH("borderWidth", "Border Width", StaticPropertyType.DOUBLE, 272),
	FILLCOLOR("fillColor", "Fill Color", StaticPropertyType.COLOR, 273),
	SHAPETYPE("shapeType", "Shape Type", StaticPropertyType.SHAPETYPE, 274),
	ROTATION("rotation", "Rotation", StaticPropertyType.ROTATION, 275, true, false, false),

	// ========================================
	// LineElement
	// ========================================
	// common properties
	STARTARROWHEADTYPE("startArrowHeadType", "Start Arrow ", StaticPropertyType.ARROWHEADTYPE, 280),
	ENDARROWHEADTYPE("endArrowHeadType", "End Arrow", StaticPropertyType.ARROWHEADTYPE, 281),
	STARTX("startX", "Start X", StaticPropertyType.DOUBLE, 282, true, false, false),
	STARTY("startY", "Start Y", StaticPropertyType.DOUBLE, 283, true, false, false),
	ENDX("endX", "End X", StaticPropertyType.DOUBLE, 284, true, false, false),
	ENDY("endY", "End Y", StaticPropertyType.DOUBLE, 285, true, false, false),
	STARTELEMENTREF("startElementRef", "StartElementRef", StaticPropertyType.LINKABLETO, 286, false, true, false),
	ENDELEMENTREF("endElementRef", "EndElementRef", StaticPropertyType.LINKABLETO, 287, false, true, false),

	// types
	ANCHORSHAPETYPE("anchorShapeType", "Anchor Shape Type", StaticPropertyType.ANCHORSHAPETYPE, 290),
	// line style properties
	LINECOLOR("lineColor", "Line Color", StaticPropertyType.COLOR, 300),
	LINESTYLE("lineStyle", "Line Style", StaticPropertyType.LINESTYLETYPE, 301),
	LINEWIDTH("lineWidth", "Line Width", StaticPropertyType.DOUBLE, 302),
	CONNECTORTYPE("connectorType", "Connector Type", StaticPropertyType.CONNECTORTYPE, 303);

	// ================================================================================
	// Properties
	// ================================================================================
	private String tag, name;
	private StaticPropertyType type;
	private boolean isCoordinate;
	private boolean isAdvanced;
	private boolean hidden;
	private int order;

	// ================================================================================
	// Constructors
	// ================================================================================
	private StaticProperty(String aTag, String aName, StaticPropertyType aType, int anOrder, boolean aIsCoordinate,
			boolean aIsAdvanced, boolean isHidden) {
		tag = aTag;
		type = aType;
		name = aName;
		isCoordinate = aIsCoordinate;
		isAdvanced = aIsAdvanced;
		hidden = isHidden;
		order = anOrder;
		PropertyManager.registerProperty(this);
	}

	private StaticProperty(String aTag, String aDesc, StaticPropertyType aType, int anOrder) {
		this(aTag, aDesc, aType, anOrder, false, false, false);
	}

	// ================================================================================
	// Accessors
	// ================================================================================
	/**
	 * @return Name of GPML attribute related to this property.
	 */
	public String tag() {
		return tag;
	}

	/**
	 * @return true if this property causes coordinate changes.
	 */
	public boolean isCoordinateChange() {
		return isCoordinate;
	}

	/**
	 * @return true if this attribute should be hidden unless the "Show advanced
	 *         properties" preference is set to true
	 */
	public boolean isAdvanced() {
		return isAdvanced;
	}

	/**
	 * @return true if this is attribute should not be shown in property table
	 */
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hide) {
		hidden = hide;
	}

	/**
	 * @return Logical sort order for display in Property table. Related properties
	 *         sort together
	 */
	public int getOrder() {
		return order;
	}

	public static StaticProperty getByTag(String value) {
		return tagMapping.get(value);
	}

	static private Map<String, StaticProperty> tagMapping = initTagMapping();

	static private Map<String, StaticProperty> initTagMapping() {
		Map<String, StaticProperty> result = new HashMap<String, StaticProperty>();
		for (StaticProperty o : StaticProperty.values()) {
			result.put(o.tag(), o);
		}
		return result;
	}

	// ================================================================================
	// Property Methods
	// ================================================================================

	public String getId() {
		return "core." + tag;
	}

	/** inheritDoc */
	public String getName() {
		return name;
	}

	/**
	 * inheritDoc
	 * 
	 * Used to be desc()
	 * 
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * Used to be type() which returned StaticPropertyType
	 *
	 * @return type the data type of this property
	 *
	 */
	public PropertyType getType() {
		return type;
	}

	public boolean isCollection() {
		return false;
	}
}
