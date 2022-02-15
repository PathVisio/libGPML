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

	// pathway
	TITLE("title", "Title", StaticPropertyType.STRING, 101),
	ORGANISM("organism", "Organism", StaticPropertyType.ORGANISM, 102),
	SOURCE("source", "Source", StaticPropertyType.STRING, 103),
	VERSION("version", "Version", StaticPropertyType.STRING, 104),
	LICENSE("license", "License", StaticPropertyType.STRING, 105),

	// author
	AUTHOR("author", "Author", StaticPropertyType.STRING, 106),
	NAME("name", "Author name", StaticPropertyType.STRING, 107), // TODO
	USERNAME("username", "Author Username", StaticPropertyType.STRING, 108), // TODO
	ORDER("order", "Author Order", StaticPropertyType.INTEGER, 109), // TODO

	// pathway graphics
	BOARDWIDTH("boardWidth", "Board Width", StaticPropertyType.DOUBLE, 110, true, true, false),
	BOARDHEIGHT("boardHeight", "Board Height", StaticPropertyType.DOUBLE, 111, true, true, false),
	BACKGROUNDCOLOR("backgroundColor", "Background Color", StaticPropertyType.COLOR, 112, true, true, false),

	// all pathway objects
	ELEMENTID("elementId", "ElementId", StaticPropertyType.STRING, 120, false, true, false),

	// xref: pathway, author, datanode, state, interaction, group
	XREF("xref", "Xref", StaticPropertyType.XREF, 121),

	// pathway, datanode, state, interaction, graphicalline, label, shape, group
	COMMENT("comment", "Comment", StaticPropertyType.COMMENT, 122),

	// rect props
	CENTERX("centerX", "Center X", StaticPropertyType.DOUBLE, 130, true, false, false),
	CENTERY("centerY", "Center Y", StaticPropertyType.DOUBLE, 131, true, false, false),
	WIDTH("width", "Width", StaticPropertyType.DOUBLE, 132, true, false, false),
	HEIGHT("height", "Height", StaticPropertyType.DOUBLE, 133, true, false, false),

	// font props
	TEXTCOLOR("textColor", "Text Color", StaticPropertyType.COLOR, 134), // TODO number
	FONTNAME("fontName", "Font Name", StaticPropertyType.FONTNAME, 135),
	FONTWEIGHT("fontWeight", "Bold", StaticPropertyType.BOOLEAN, 136),
	FONTSTYLE("fontStyle", "Italic", StaticPropertyType.BOOLEAN, 137),
	FONTDECORATION("fontDecoration", "Underline", StaticPropertyType.BOOLEAN, 138), // TODO number
	FONTSTRIKETHRU("fontStrikeThru", "Strikethru", StaticPropertyType.BOOLEAN, 139), // TODO number
	FONTSIZE("fontSize", "Font Size", StaticPropertyType.INTEGER, 140), // TODO used to be double
	VALIGN("vAlign", "Vertical Alignment", StaticPropertyType.VALIGNTYPE, 141),
	HALIGN("hAlign", "Horizontal Alignment", StaticPropertyType.HALIGNTYPE, 142),

	// shape style props
	BORDERCOLOR("borderColor", "Border Color", StaticPropertyType.COLOR, 143),
	BORDERSTYLE("borderStyle", "Border Style", StaticPropertyType.LINESTYLETYPE, 144),
	BORDERWIDTH("borderWidth", "Border Width", StaticPropertyType.DOUBLE, 145),
	FILLCOLOR("fillColor", "Fill Color", StaticPropertyType.COLOR, 146),
	SHAPETYPE("shapeType", "Shape Type", StaticPropertyType.SHAPETYPE, 147),
	ROTATION("rotation", "Rotation", StaticPropertyType.ROTATION, 148, true, false, false),
	// TRANSPARENT ("Transparent", "Transparent", StaticPropertyType.BOOLEAN, 210),

	// line style props
	LINECOLOR("lineColor", "Line Color", StaticPropertyType.COLOR, 150),
	LINESTYLE("lineStyle", "Line Style", StaticPropertyType.LINESTYLETYPE, 151),
	LINEWIDTH("lineWidth", "Line Width", StaticPropertyType.DOUBLE, 152),
	CONNECTORTYPE("connectorType", "Connector Type", StaticPropertyType.DOUBLE, 153),

	// line style and shape style props
	ZORDER("zOrder", "Z-Order", StaticPropertyType.INTEGER, 154, false, true, false),

	// shaped pathway elements: datanode, state, label, shape, group
	TEXTLABEL("textLabel", "Text Label", StaticPropertyType.STRING, 160),

	// types: anchor, annotation, linepoint, datanode, state, group //TODO TAG
	// NAMES???
	ANCHORSHAPETYPE("anchorShapeType", "Anchor Shape Type", StaticPropertyType.ANCHORSHAPETYPE, 161),
	ANNOTATIONTYPE("annotationType", "Annotation Type", StaticPropertyType.ANNOTATIONTYPE, 162),
	ARROWHEADTYPE("arrowHead", "ArrowHead Type", StaticPropertyType.ARROWHEADTYPE, 163),
	DATANODETYPE("dataNodeType", "DataNode Type", StaticPropertyType.DATANODETYPE, 164),
	STATETYPE("stateType", "State Type", StaticPropertyType.STATETYPE, 165),
	GROUPTYPE("groupType", "Group Type", StaticPropertyType.GROUPTYPE, 166),

	// datanode
	ALIASREF("aliasRef", "Alias Reference", StaticPropertyType.STRING, 167, false, true, false),

	// state
	RELX("relX", "Relative X", StaticPropertyType.DOUBLE, 168, true, false, false),
	RELY("relY", "Relative Y", StaticPropertyType.DOUBLE, 169, true, false, false),
	ELEMENTREF("elementRef", "Element Reference", StaticPropertyType.STRING, 170, false, true, false),

	// label
	HREF("href", "Href", StaticPropertyType.STRING, 171),

	// brace
	ORIENTATION("Orientation", "Orientation", StaticPropertyType.ORIENTATION, 172), // TODO????

	// datanode, state, interaction, graphicalline, label, shape, group
	GROUPREF("GroupRef", "GroupRef", StaticPropertyType.STRING, 173, false, true, false),

	// annotation, citation, evidence //TODO
	ANNOTATION("Annotation", "Annotation", StaticPropertyType.ANNOTATION, 174, false, true, false),
	CITATION("Citation", "Citation", StaticPropertyType.CITATION, 175, false, true, false),
	EVIDENCE("Evidence", "Evidence", StaticPropertyType.EVIDENCE, 176, false, true, false);

	// line
//STARTX ("StartX", "Start X", StaticPropertyType.DOUBLE, 114, true, false, false),
//STARTY ("StartY", "Start Y", StaticPropertyType.DOUBLE, 115, true, false, false),
//ENDX ("EndX", "End X", StaticPropertyType.DOUBLE, 116, true, false, false),
//ENDY ("EndY", "End Y", StaticPropertyType.DOUBLE, 117, true, false, false),
//STARTLINETYPE ("StartLineType", "Start Line Type", StaticPropertyType.LINETYPE, 118),
//ENDLINETYPE ("EndLineType", "End Line Type", StaticPropertyType.LINETYPE, 119),
	// other
//	STARTGRAPHREF ("StartGraphRef", "StartGraphRef", StaticPropertyType.STRING, 148, false, true, false),
	// ENDGRAPHREF ("EndGraphRef", "EndGraphRef", StaticPropertyType.STRING, 149,
	// false, true, false),
	// GROUPID("GroupId","GroupId",StaticPropertyType.STRING,150,false,true,false),
	// style",StaticPropertyType.GROUPSTYLETYPE,152),BIOPAXREF("BiopaxRef","BiopaxRef",StaticPropertyType.BIOPAXREF,153,false,true,false),

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
