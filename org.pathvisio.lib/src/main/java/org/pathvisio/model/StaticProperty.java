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
package org.pathvisio.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum class contains static properties of {@link StaticPropertyType} for
 * {@link ObjectType}, linked in {@link PathwayElement}.
 * 
 * @author unknown, finterly
 */
public enum StaticProperty implements Property {

//	Shape, GraphLine, DataNode, Label, Line, Legend, InfoBox, MappInfo, Group, BioPax, State 

	/**
	 * ObjectTypes: DataNode, State, Interaction, GraphicalLine, Label, Shape, Group
	 * have property comment
	 */
	COMMENTS("Comments", "Comments", StaticPropertyType.COMMENTS, 101),

	/**
	 * TODO: Line, Shape, Datanode, Label
	 */
	COLOR("Color", "Color", StaticPropertyType.COLOR, 202),
	/**
	 * TODO: Line
	 */
	LINECOLOR("Color", "Color", StaticPropertyType.COLOR, 203),
	/**
	 * TODO: Shape, Datanode, Label
	 */
	BORDERCOLOR("Color", "Color", StaticPropertyType.COLOR, 204),

	/**
	 * DataNode, Label, Shape
	 */
	CENTERX("CenterX", "Center X", StaticPropertyType.DOUBLE, 103, true, false, false),
	CENTERY("CenterY", "Center Y", StaticPropertyType.DOUBLE, 104, true, false, false),

	/**
	 * Shape, datanode, label, modification
	 */
	WIDTH("Width", "Width", StaticPropertyType.DOUBLE, 105, true, false, false),
	HEIGHT("Height", "Height", StaticPropertyType.DOUBLE, 106, true, false, false),

	/**
	 * modification
	 */
	RELX("relX", "Relative X", StaticPropertyType.DOUBLE, 107, true, false, false),
	RELY("relY", "Relative Y", StaticPropertyType.DOUBLE, 108, true, false, false),
	GRAPHREF("GraphRef", "GraphRef", StaticPropertyType.STRING, 109, false, true, false),

	/**
	 * Shape, modification, label, datanode
	 */
	TRANSPARENT("Transparent", "Transparent", StaticPropertyType.BOOLEAN, 210),
	FILLCOLOR("FillColor", "Fill Color", StaticPropertyType.COLOR, 211),
	SHAPETYPE("ShapeType", "Shape Type", StaticPropertyType.SHAPETYPE, 112),

	/**
	 * Shape
	 */
	ROTATION("Rotation", "Rotation", StaticPropertyType.ANGLE, 113, true, false, false),

	/**
	 * Line
	 */
	STARTX("StartX", "Start X", StaticPropertyType.DOUBLE, 114, true, false, false),
	STARTY("StartY", "Start Y", StaticPropertyType.DOUBLE, 115, true, false, false),
	ENDX("EndX", "End X", StaticPropertyType.DOUBLE, 116, true, false, false),
	ENDY("EndY", "End Y", StaticPropertyType.DOUBLE, 117, true, false, false),

	STARTLINETYPE("StartLineType", "Start Line Type", StaticPropertyType.LINETYPE, 118),
	ENDLINETYPE("EndLineType", "End Line Type", StaticPropertyType.LINETYPE, 119),

	/**
	 * Line, label, shape, state and datanode
	 */
	LINESTYLE("LineStyle", "Line Style", StaticPropertyType.LINESTYLE, 120),
	LINETHICKNESS("LineThickness", "Line Thickness", StaticPropertyType.DOUBLE, 120),

	/**
	 * brace???
	 */
	ORIENTATION("Orientation", "Orientation", StaticPropertyType.ORIENTATION, 121),

	// datanode
	GENEID("GeneID", "Database Identifier", StaticPropertyType.DB_ID, 122),
	DATASOURCE("SystemCode", "Database Name", StaticPropertyType.DATASOURCE, 123),
	TYPE("Type", "Type", StaticPropertyType.GENETYPE, 126),

	// state
	MODIFICATIONTYPE("ModificationType", "ModificationType", StaticPropertyType.STRING, 127),

	// label, shape, state and datanode
	TEXTLABEL("TextLabel", "Text Label", StaticPropertyType.STRING, 128),
	FONTNAME("FontName", "Font Name", StaticPropertyType.FONT, 129),
	FONTWEIGHT("FontWeight", "Bold", StaticPropertyType.BOOLEAN, 130),
	FONTSTYLE("FontStyle", "Italic", StaticPropertyType.BOOLEAN, 131),
	FONTSIZE("FontSize", "Font Size", StaticPropertyType.DOUBLE, 132),
	/**
	 * TODO:
	 */
	VALIGN("Valign", "Vertical Alignment", StaticPropertyType.VALIGNTYPE, 133),
	HALIGN("Halign", "Horizontal Alignment", StaticPropertyType.HALIGNTYPE, 134),
	ALIGN("Align", "Alignment", StaticPropertyType.ALIGNTYPE, 134),

	// label
	HREF("Href", "Link", StaticPropertyType.STRING, 135),

	// mappinfo
	MAPINFONAME("MapInfoName", "Title", StaticPropertyType.STRING, 134),
	ORGANISM("Organism", "Organism", StaticPropertyType.ORGANISM, 135),
	MAPINFO_DATASOURCE("Data-Source", "Data-Source", StaticPropertyType.STRING, 136),
	VERSION("Version", "Version", StaticPropertyType.STRING, 137),
	AUTHOR("Author", "Author", StaticPropertyType.STRING, 138),
	MAINTAINED_BY("Maintained-By", "Maintainer", StaticPropertyType.STRING, 139),
	EMAIL("Email", "Email", StaticPropertyType.STRING, 140),
	LAST_MODIFIED("Last-Modified", "Last Modified", StaticPropertyType.STRING, 141),
	LICENSE("License", "License", StaticPropertyType.STRING, 142),
	BOARDWIDTH("BoardWidth", "Board Width", StaticPropertyType.DOUBLE, 143, true, true, false),
	BOARDHEIGHT("BoardHeight", "Board Height", StaticPropertyType.DOUBLE, 144, true, true, false),

	// other
	GRAPHID("GraphId", "GraphId", StaticPropertyType.STRING, 147, false, true, false),
	STARTGRAPHREF("StartGraphRef", "StartGraphRef", StaticPropertyType.STRING, 148, false, true, false),
	ENDGRAPHREF("EndGraphRef", "EndGraphRef", StaticPropertyType.STRING, 149, false, true, false),
	GROUPID("GroupId", "GroupId", StaticPropertyType.STRING, 150, false, true, false),
	GROUPREF("GroupRef", "GroupRef", StaticPropertyType.STRING, 151, false, true, false),
	/** TODO: remove*/
	GROUPSTYLE("GroupStyle", "Group style", StaticPropertyType.GROUPSTYLETYPE, 152),
	GROUPTYPE("GroupStyle", "Group style", StaticPropertyType.GROUPTYPE, 152),

	BIOPAXREF("BiopaxRef", "BiopaxRef", StaticPropertyType.BIOPAXREF, 153, false, true, false),
	ZORDER("Z order", "ZOrder", StaticPropertyType.INTEGER, 154, false, true, false);

	private String tag;
	private String name;
	private StaticPropertyType type;
	private boolean isCoordinate;
	private boolean isAdvanced;
	private boolean hidden;
	private int order;

	/**
	 * Constructor to initialize the state of enum types given all parameters.
	 * 
	 * @param tag          the name of gpml attribute related to this static
	 *                     property.
	 * @param name         the description of this static property.
	 * @param type         the static property type of this static property
	 * @param order        the logical sort order for display in Property table.
	 *                     Sorts related properties together.
	 * @param isCoordinate the boolean, if true this property causes coordinate
	 *                     changes.
	 * @param isAdvanced   the boolean, if true this attribute should be hidden
	 *                     unless the "Show advanced properties" preference is set
	 *                     to true.
	 * @param hidden       the boolean, if true this is attribute should not be
	 *                     shown in property table.
	 */
	private StaticProperty(String tag, String name, StaticPropertyType type, int order, boolean isCoordinate,
			boolean isAdvanced, boolean hidden) {
		this.tag = tag;
		this.name = name;
		this.type = type;
		this.order = order;
		this.isCoordinate = isCoordinate;
		this.isAdvanced = isAdvanced;
		this.hidden = hidden;
		PropertyManager.registerProperty(this);
	}

	/**
	 * Constructor to initialize the state of enum types given tag, given name,
	 * given type, given order, no isCoordinate, no isAdvances, and no hidden.
	 * 
	 * @param tag   the name of gpml attribute related to this static property.
	 * @param name  the description of this static property.
	 * @param type  the static property type of this static property
	 * @param order the logical sort order for display in Property table. Sorts
	 *              related properties together.
	 */
	private StaticProperty(String tag, String description, StaticPropertyType type, int order) {
		this(tag, description, type, order, false, false, false);
	}

	/**
	 * Returns the tag GPML attribute name of this static property.
	 * 
	 * @return tag the name of gpml attribute related to this property.
	 */
	public String tag() {
		return tag;
	}

	/**
	 * @deprecated use inherited getDescription() instead.
	 */
	public String desc() {
		return name;
	}

	/**
	 * Property methods?
	 * 
	 * @{inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Property methods?
	 * 
	 * @{inheritDoc}
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * @return Data type of this property.
	 * @deprecated use getType() instead.
	 */
	public StaticPropertyType type() {
		return type;
	}

	/**
	 * Property methods? Returns type?
	 * 
	 */
	public PropertyType getType() {
		return type;
	}

	/**
	 * Returns the logical sort order for display in Property table for this static
	 * property.
	 * 
	 * @return order the logical sort order for display in Property table. Sorts
	 *         related properties together.
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Returns the boolean isCoordinate for this static property.
	 * 
	 * @return isCoordinate the boolean, if true this property causes coordinate
	 *         changes.
	 */
	public boolean isCoordinateChange() {
		return isCoordinate;
	}

	/**
	 * Returns the boolean isAdvanced for this static property.
	 * 
	 * @return isAdvanced the boolean, if true this attribute should be hidden
	 *         unless the "Show advanced properties" preference is set to true.
	 */
	public boolean isAdvanced() {
		return isAdvanced;
	}

	/**
	 * Returns the boolean hidden for this static property.
	 * 
	 * @return hidden the boolean, if true this is attribute should not be shown in
	 *         property table.
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Sets the boolean hidden for this static property.
	 * 
	 * @return hidden the boolean, if true this is attribute should not be shown in
	 *         property table.
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * Returns the StaticProperty that corresponds to a certain gpml tag.
	 * 
	 * @param value the String value of gpml tag.
	 * @return the StaticProperty that corresponds to given tag value.
	 */
	public static StaticProperty getByTag(String value) {
		return tagToStaticProperty.get(value);
	}

	static private Map<String, StaticProperty> tagToStaticProperty = initTagMapping();

	/**
	 * Creates a new hashmap of String tag to StaticProperty mappings. Inserts
	 * mappings into map by associating specified values with specified keys.
	 * 
	 * @return result the map of gpml tags to static properties.
	 */
	static private Map<String, StaticProperty> initTagMapping() {
		Map<String, StaticProperty> result = new HashMap<String, StaticProperty>();
		for (StaticProperty object : StaticProperty.values()) {
			result.put(object.tag(), object);
		}
		return result;
	}

	/* -- Property methods -- */

	public String getId() {
		return "core." + tag;
	}

	public boolean isCollection() {
		return false;
	}

}
