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
	// Pathway shapes
	// ========================================
	TITLE("title", "Title", StaticPropertyType.STRING, 101),
	ORGANISM("organism", "Organism", StaticPropertyType.ORGANISM, 102),
	SOURCE("source", "Source", StaticPropertyType.STRING, 103),
	VERSION("version", "Version", StaticPropertyType.STRING, 104),
	LICENSE("license", "License", StaticPropertyType.STRING, 105),

	// author
	AUTHOR("author", "Author", StaticPropertyType.STRING, 110),
	NAME("name", "Author name", StaticPropertyType.STRING, 111), // TODO
	USERNAME("username", "Author Username", StaticPropertyType.STRING, 112), // TODO
	ORDER("order", "Author Order", StaticPropertyType.INTEGER, 113), // TODO

	// pathway graphics
	BOARDWIDTH("boardWidth", "Board Width", StaticPropertyType.DOUBLE, 120, true, true, false),
	BOARDHEIGHT("boardHeight", "Board Height", StaticPropertyType.DOUBLE, 121, true, true, false),
	BACKGROUNDCOLOR("backgroundColor", "Background Color", StaticPropertyType.COLOR, 122, true, true, false),

	// ========================================
	// Pathway Object
	// ========================================
	ELEMENTID("elementId", "ElementId", StaticPropertyType.STRING, 130, false, true, false),

	// ========================================
	// Pathway Element
	// ========================================
	COMMENT("comment", "Comment", StaticPropertyType.COMMENT, 140),
	ANNOTATION("annotation", "Annotation", StaticPropertyType.ANNOTATION, 141, false, true, false),
	CITATION("citation", "Citation", StaticPropertyType.CITATION, 142, false, true, false),
	EVIDENCE("evidence", "Evidence", StaticPropertyType.EVIDENCE, 143, false, true, false),

	// types
	ANNOTATIONTYPE("annotationType", "Annotation Type", StaticPropertyType.ANNOTATIONTYPE, 150),

	// ========================================
	// Xrefable 
	// ========================================
	XREF("xref", "Xref", StaticPropertyType.XREF, 160),

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
	ALIASREF("aliasRef", "Alias Reference", StaticPropertyType.GROUP, 200, false, false, false), //TODO 

	// state
	RELX("relX", "Relative X", StaticPropertyType.DOUBLE, 210, true, false, false),
	RELY("relY", "Relative Y", StaticPropertyType.DOUBLE, 211, true, false, false),

	// label
	HREF("href", "Href", StaticPropertyType.STRING, 220),

	// brace
	ORIENTATION("Orientation", "Orientation", StaticPropertyType.ORIENTATION, 230), // TODO????

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
	FONTSTRIKETHRU("fontStrikeThru", "Strikethru", StaticPropertyType.BOOLEAN, 255), // TODO number
	FONTSIZE("fontSize", "Font Size", StaticPropertyType.DOUBLE, 256), // TODO used to be double
	VALIGN("vAlign", "Vertical Alignment", StaticPropertyType.VALIGNTYPE, 257),
	HALIGN("hAlign", "Horizontal Alignment", StaticPropertyType.HALIGNTYPE, 258),

	// shape style properties
	BORDERCOLOR("borderColor", "Border Color", StaticPropertyType.COLOR, 260),
	BORDERSTYLE("borderStyle", "Border Style", StaticPropertyType.LINESTYLETYPE, 261),
	BORDERWIDTH("borderWidth", "Border Width", StaticPropertyType.DOUBLE, 262),
	FILLCOLOR("fillColor", "Fill Color", StaticPropertyType.COLOR, 263),
	SHAPETYPE("shapeType", "Shape Type", StaticPropertyType.SHAPETYPE, 264),
	ROTATION("rotation", "Rotation", StaticPropertyType.ROTATION, 265, true, false, false),
	// TRANSPARENT ("Transparent", "Transparent", StaticPropertyType.BOOLEAN, 210),

	// ========================================
	// LineElement
	// ========================================
	// common properties
	STARTARROWHEADTYPE("startArrowHeadType", "Start Arrow ", StaticPropertyType.ARROWHEADTYPE, 270),
	ENDARROWHEADTYPE("endArrowHeadType", "End Arrow", StaticPropertyType.ARROWHEADTYPE, 271),
	STARTX("startX", "Start X", StaticPropertyType.DOUBLE, 272, true, false, false),
	STARTY("startY", "Start Y", StaticPropertyType.DOUBLE, 273, true, false, false),
	ENDX("endX", "End X", StaticPropertyType.DOUBLE, 274, true, false, false),
	ENDY("endY", "End Y", StaticPropertyType.DOUBLE, 275, true, false, false),
	STARTELEMENTREF("startElementRef", "StartElementRef", StaticPropertyType.LINKABLETO, 276, false, true, false),
	ENDELEMENTREF("endElementRef", "EndElementRef", StaticPropertyType.LINKABLETO, 277, false, true, false),
//	ELEMENTREF("elementRef", "Element Reference", StaticPropertyType.LINKABLETO, 172, false, true, false),

	// types
	ANCHORSHAPETYPE("anchorShapeType", "Anchor Shape Type", StaticPropertyType.ANCHORSHAPETYPE, 280),
	// line style properties
	LINECOLOR("lineColor", "Line Color", StaticPropertyType.COLOR, 290),
	LINESTYLE("lineStyle", "Line Style", StaticPropertyType.LINESTYLETYPE, 291),
	LINEWIDTH("lineWidth", "Line Width", StaticPropertyType.DOUBLE, 292),
	CONNECTORTYPE("connectorType", "Connector Type", StaticPropertyType.CONNECTORTYPE, 293);

	// GROUPID("GroupId","GroupId",StaticPropertyType.STRING,150,false,true,false),
	// style",StaticPropertyType.GROUPSTYLETYPE,152),BIOPAXREF("BiopaxRef","BiopaxRef",StaticPropertyType.BIOPAXREF,153,false,true,false),

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
