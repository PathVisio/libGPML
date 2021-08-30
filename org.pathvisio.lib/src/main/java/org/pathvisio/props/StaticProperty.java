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

package org.pathvisio.props;

import java.util.HashMap;
import java.util.Map;

/**
 * Static properties for ObjectTypes.
 * 
 * // TODO Can Tag and name just be the same??? 
 * // TODO FIX NUMBERS
 * 
 * @author unknown, finterly
 */
public enum StaticProperty implements Property {

	// pathway
	TITLE("title", "title", StaticPropertyType.STRING, 134),
	ORGANISM("organism", "organism", StaticPropertyType.ORGANISM, 135),
	SOURCE("source", "source", StaticPropertyType.STRING, 136),
	VERSION("version", "version", StaticPropertyType.STRING, 137),
	LICENSE("license", "license", StaticPropertyType.STRING, 142),

	// xref: pathway, author, datanode, state, interaction, group
	IDENTIFIER("identifier", "identifier", StaticPropertyType.IDENTIFIER, 122),
	DATASOURCE("dataSource", "dataSource", StaticPropertyType.DATASOURCE, 123),

	// author
	NAME("name", "author name", StaticPropertyType.STRING, 138),
	USERNAME("username", "username", StaticPropertyType.STRING, 138),
	ORDER("order", "order", StaticPropertyType.INTEGER, 138),

	// pathway graphics
	BOARDWIDTH("boardWidth", "Board Width", StaticPropertyType.DOUBLE, 143, true, true, false),
	BOARDHEIGHT("boardHeight", "Board Height", StaticPropertyType.DOUBLE, 144, true, true, false),
	BACKGROUNDCOLOR("backgroundColor", "backgroundColor", StaticPropertyType.COLOR, 144, true, true, false),

	// all pathway elements
	ELEMENTID("elementId", "elementId", StaticPropertyType.STRING, 147, false, true, false),

	// elementinfo: datanode, state, interaction, graphicalline, label, shape, group
	COMMENT("comment", "comment", StaticPropertyType.COMMENT, 101),

	// rect props
	CENTERX("centerX", "centerX", StaticPropertyType.DOUBLE, 103, true, false, false),
	CENTERY("centerY", "centerY", StaticPropertyType.DOUBLE, 104, true, false, false),
	WIDTH("width", "width", StaticPropertyType.DOUBLE, 105, true, false, false),
	HEIGHT("height", "height", StaticPropertyType.DOUBLE, 106, true, false, false),

	// font props
	TEXTCOLOR("textColor", "textColor", StaticPropertyType.COLOR, 202), // TODO number
	FONTNAME("fontName", "fontName", StaticPropertyType.FONTNAME, 129),
	FONTWEIGHT("fontWeight", "bold", StaticPropertyType.BOOLEAN, 130),
	FONTSTYLE("fontStyle", "italic", StaticPropertyType.BOOLEAN, 131),
	FONTDECORATION("fontDecoration", "underline", StaticPropertyType.BOOLEAN, 131), // TODO number
	FONTSTRIKETHRU("fontStrikeThru", "strikethru", StaticPropertyType.BOOLEAN, 131), // TODO number
	FONTSIZE("fontSize", "fontSize", StaticPropertyType.INTEGER, 132), // TODO used to be double
	VALIGN("vAlign", "vAlign", StaticPropertyType.VALIGNTYPE, 133),
	HALIGN("hAlign", "hAlign", StaticPropertyType.HALIGNTYPE, 134),

	// shape style props
	BORDERCOLOR("borderColor", "borderColor", StaticPropertyType.COLOR, 202),
	BORDERSTYLE("borderStyle", "borderStyle", StaticPropertyType.LINESTYLETYPE, 120),
	BORDERWIDTH("borderWidth", "borderWidth", StaticPropertyType.DOUBLE, 120),
	FILLCOLOR("fillColor", "fillColor", StaticPropertyType.COLOR, 211),
	SHAPETYPE("shapeType", "shapeType", StaticPropertyType.SHAPETYPE, 112),
	ROTATION("rotation", "rotation", StaticPropertyType.ROTATION, 113, true, false, false),
	// TRANSPARENT ("Transparent", "Transparent", StaticPropertyType.BOOLEAN, 210),

	// line style props
	LINECOLOR("lineColor", "lineColor", StaticPropertyType.COLOR, 202),
	LINESTYLE("lineStyle", "lineStyle", StaticPropertyType.LINESTYLETYPE, 120),
	LINEWIDTH("lineWidth", "lineWidth", StaticPropertyType.DOUBLE, 120),
	CONNECTORTYPE("connectorType", "connectorType", StaticPropertyType.DOUBLE, 120),

	// line style and shape style props
	ZORDER("zOrder", "zOrder", StaticPropertyType.INTEGER, 154, false, true, false),

	// shaped pathway elements: datanode, state, label, shape, group
	TEXTLABEL("textLabel", "textLabel", StaticPropertyType.STRING, 128),

	// types: anchor, annotation, linepoint, datanode, state, group
	ANCHORSHAPETYPE("shapeType", "shapeType", StaticPropertyType.ANCHORSHAPETYPE, 126),
	ANNOTATIONTYPE("type", "type", StaticPropertyType.ANNOTATIONTYPE, 126),
	ARROWHEADTYPE("arrowHead", "arrowHead", StaticPropertyType.ARROWHEADTYPE, 118),
	DATANODETYPE("type", "type", StaticPropertyType.DATANODETYPE, 126),
	STATETYPE("type", "type", StaticPropertyType.STATETYPE, 126),
	GROUPTYPE("type", "type", StaticPropertyType.GROUPTYPE, 126),

	// datanode
	ALIASREF("aliasRef", "aliasRef", StaticPropertyType.STRING, 109, false, true, false), // TODO number???

	// state
	RELX("relX", "Relative X", StaticPropertyType.DOUBLE, 107, true, false, false),
	RELY("relY", "Relative Y", StaticPropertyType.DOUBLE, 108, true, false, false),
	ELEMENTREF("elementRef", "elementRef", StaticPropertyType.STRING, 109, false, true, false),

	// label
	HREF("Href", "Link", StaticPropertyType.STRING, 135),


	// brace
	ORIENTATION("Orientation", "Orientation", StaticPropertyType.ORIENTATION, 121); // TODO????

	
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
	// GROUPID("GroupId","GroupId",StaticPropertyType.STRING,150,false,true,false),GROUPREF("GroupRef","GroupRef",StaticPropertyType.STRING,151,false,true,false),GROUPSTYLE("GroupStyle","Group
	// style",StaticPropertyType.GROUPSTYLETYPE,152),BIOPAXREF("BiopaxRef","BiopaxRef",StaticPropertyType.BIOPAXREF,153,false,true,false),

	private String tag, name;
	private StaticPropertyType type;
	private boolean isCoordinate;
	private boolean isAdvanced;
	private boolean hidden;
	private int order;

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

	/**
	 * @return Name of GPML attribute related to this property.
	 */
	public String tag() {
		return tag;
	}

	/**
	 * @deprecated use getDescription() instead.
	 */
	public String desc() {
		return name;
	}

	/**
	 * @return Data type of this property
	 * @deprecated use getType() instead.
	 */
	public StaticPropertyType type() {
		return type;
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

	// -- Property methods --//

	public String getId() {
		return "core." + tag;
	}

	/** @{inheritDoc} */
	public String getName() {
		return name;
	}

	/** @{inheritDoc} */
	public String getDescription() {
		return null;
	}

	public PropertyType getType() {
		return type;
	}

	public boolean isCollection() {
		return false;
	}
}
