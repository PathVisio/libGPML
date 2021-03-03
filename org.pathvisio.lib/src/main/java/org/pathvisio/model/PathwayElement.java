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

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.ElementLink.ElementIdContainer;
import org.pathvisio.model.ElementLink.ElementRefContainer;
import org.pathvisio.util.Utils;

/**
 * PathwayElement is responsible for maintaining the data for all the individual
 * objects that can appear on a pathway (Lines, GeneProducts, Shapes, etc.)
 * <p>
 * All PathwayElements have an ObjectType. This ObjectType is specified at
 * creation time and can't be modified. To create a PathwayElement, use the
 * createPathwayElement() function. This is a factory method that returns a
 * different implementation class depending on the specified ObjectType.
 * <p>
 * PathwayElements have a number of properties which consist of a key, value
 * pair.
 * <p>
 * There are two types of properties: Static and Dynamic.
 * <p>
 * Dynamic properties can have any String as key. Their value is always of type
 * String. Dynamic properties are not essential for the functioning of PathVisio
 * and can be used to store arbitrary data. In GPML, dynamic properties are
 * stored in CommentGroup.Property in an <Attribute key="" value=""/> tag.
 * Internally, dynamic properties are stored in a Map<String, String>.
 * <p>
 * Static properties must have a key from the StaticProperty enum. Their value
 * can be various types {@link StaticPropertyType} obtained from
 * {@link StaticProperty#type()}. Static properties can be queried with
 * getStaticProperty(key) and setStaticProperty(key, value), but also specific
 * accessors such as e.g. getTextLabel() and setTextLabel()
 * <p>
 * Internally, dynamic properties are stored in various fields of the
 * PathwayElement Object. The static properties are a union of all possible
 * fields (e.g it has both start and end points for lines, and label text for
 * labels)
 * <p>
 * the setPropertyEx() and getPropertyEx() functions can be used to access both
 * dynamic and static properties from the same function. If key instanceof
 * String then it's assumed the caller wants a dynamic property, if key
 * instanceof StaticProperty then the static property is used.
 * <p>
 * most static properties cannot be set to null. Notable exceptions are graphId,
 * startGraphRef and endGraphRef.
 * 
 * @author unknown, finterly
 */
public class PathwayElement implements ElementIdContainer, Comparable<PathwayElement> {
	// TreeMap has better performance than HashMap
	// in the (common) case where no attributes are present
	// This map should never contain non-null values, if a value
	// is set to null the key should be removed.

	/**
	 * Map for storing dynamic properties. Dynamic properties can have any String as
	 * key and value of type String. Dynamic properties represent
	 * CommentGroup.Property in gpml.
	 */
	private Map<String, String> dynamicProperties = new TreeMap<String, String>();

	/**
	 * Gets a set of all dynamic property keys.
	 * 
	 * @return a set of all dynamic property keys.
	 */
	public Set<String> getDynamicPropertyKeys() {
		return dynamicProperties.keySet();
	}

	/**
	 * Sets a dynamic property. Setting to null means removing this dynamic property
	 * altogether.
	 * 
	 * @param key   the key of a key value pair.
	 * @param value the value of a key value pair.
	 */
	public void setDynamicProperty(String key, String value) {
		if (value == null)
			dynamicProperties.remove(key);
		else
			dynamicProperties.put(key, value);
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, key));
	}

	/**
	 * Gets a dynamic property string value.
	 * 
	 * @param key the key of a key value pair.
	 * @return the value or dynamic property.
	 */
	public String getDynamicProperty(String key) {
		return dynamicProperties.get(key);
	}

	/**
	 * Initial values for pathway element properties.
	 */
	private static final int M_INITIAL_FONTSIZE = 12;
	private static final int M_INITIAL_LABEL_WIDTH = 90;
	private static final int M_INITIAL_LABEL_HEIGHT = 25;
	private static final int M_INITIAL_LINE_LENGTH = 30;
	private static final int M_INITIAL_STATE_SIZE = 15;
	private static final int M_INITIAL_SHAPE_SIZE = 30;
	private static final int M_INITIAL_CELLCOMP_HEIGHT = 100;
	private static final int M_INITIAL_CELLCOMP_WIDTH = 200;
	private static final int M_INITIAL_BRACE_HEIGHT = 15;
	private static final int M_INITIAL_BRACE_WIDTH = 60;
	private static final int M_INITIAL_GENEPRODUCT_WIDTH = 90;
	private static final int M_INITIAL_GENEPRODUCT_HEIGHT = 25;

	// groups should be behind other graphics
	// to allow background colors
	private static final int Z_ORDER_GROUP = 0x1000;
	// default order of geneproduct, label, shape and line determined
	// by GenMAPP legacy
	private static final int Z_ORDER_GENEPRODUCT = 0x8000;
	private static final int Z_ORDER_LABEL = 0x7000;
	private static final int Z_ORDER_SHAPE = 0x4000;
	private static final int Z_ORDER_LINE = 0x3000;
	// default order of uninteresting elements.
	private static final int Z_ORDER_DEFAULT = 0x0000;

	/**
	 * Gets default z order for newly created objects
	 */
	private static int getDefaultZOrder(ObjectType value) {
		switch (value) {
		case SHAPE:
			return Z_ORDER_SHAPE;
		case STATE:
			return Z_ORDER_GENEPRODUCT + 10;
		case DATANODE:
			return Z_ORDER_GENEPRODUCT;
		case LABEL:
			return Z_ORDER_LABEL;
		case LINE:
			return Z_ORDER_LINE;
		case GRAPHLINE:
			return Z_ORDER_LINE;
		case LEGEND:
		case INFOBOX:
		case MAPPINFO:
		case BIOPAX:
			return Z_ORDER_DEFAULT;
		case GROUP:
			return Z_ORDER_GROUP;
		default:
			throw new IllegalArgumentException("Invalid object type " + value);
		}
	}

	/**
	 * Instantiate a pathway element. The required parameter objectType ensures only
	 * objects with a valid type can be created.
	 *
	 * @param objectType the type of pathway element {@link @ObjectType}.
	 */
	public static PathwayElement createPathwayElement(ObjectType objectType) {
		PathwayElement pathwayElement;
		switch (objectType) {
		case BIOPAX:
			pathwayElement = new BiopaxElement();
			break;
		case GROUP:
			pathwayElement = new MGroup();
			break;
		case LINE:
			pathwayElement = new MLine(ObjectType.LINE);
			break;
		case GRAPHLINE:
			pathwayElement = new MLine(ObjectType.GRAPHLINE);
			break;
		case STATE:
			pathwayElement = new MState();
			break;
		default:
			pathwayElement = new PathwayElement(objectType);
			break;
		}
		return pathwayElement;
	}

	/**
	 * Instantiate a pathway element of objectType and sets default values.
	 * 
	 * @param objectType the type of pathway element {@link @ObjectType}
	 */
	protected PathwayElement(ObjectType objectType) {
		/**
		 * Set fillColor to WHITE if objectType is Line, Label, DataNode, State, or
		 * GraphLine. Otherwise set to null.
		 */
		if (objectType == ObjectType.LINE || objectType == ObjectType.LABEL || objectType == ObjectType.DATANODE
				|| objectType == ObjectType.STATE || objectType == ObjectType.GRAPHLINE) {
			this.fillColor = Color.WHITE;
		} else {
			this.fillColor = null;
		}
		/**
		 * Set shapeType to NONE if objectType is Label. Otherwise set to RECTANGLE.
		 */
		if (objectType == ObjectType.LABEL) {
			this.shapeType = ShapeType.NONE;
		} else {
			this.shapeType = ShapeType.RECTANGLE;
		}
		/**
		 * Set objectType to given objectType.
		 */
		this.objectType = objectType;
		/**
		 * Set zOrder to default zOrder.
		 */
		this.zOrder = getDefaultZOrder(objectType);
	}

	/**
	 * Z-order of this pathway element. Z-order is an ordering of overlapping
	 * two-dimensional objects.
	 */
	int zOrder;

	public int getZOrder() {
		return zOrder;
	}

	public void setZOrder(int z) {
		if (z != zOrder) {
			zOrder = z;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ZORDER));
		}
	}

	/**
	 * Parent of this object: may be null (for example, when object is in clipboard)
	 */
	protected Pathway parent = null;

	/**
	 * Returns the parent pathway.
	 * 
	 * @return parent the parent pathway.
	 */
	public Pathway getParent() {
		return parent;
	}

	/**
	 * Get the parent pathway. Same as {@link #getParent()}, but necessary to comply
	 * to the {@link ElementIdContainer} interface.
	 * 
	 * @return parent the parent pathway.
	 */
	public Pathway getPathway() {
		return parent;
	}

	/**
	 * Set parent. Do not use this method directly! parent is set automatically when
	 * using Pathway.add/remove
	 * 
	 * @param v the parentGENEID
	 */
	void setParent(Pathway v) {
		parent = v;
	}

	/**
	 * Returns keys of available static properties and dynamic properties as an
	 * object list.
	 * 
	 * @return keys the set of keys of available static properties and dynamic
	 *         properties.
	 */
	public Set<Object> getPropertyKeys() {
		Set<Object> keys = new HashSet<Object>();
		keys.addAll(getStaticPropertyKeys());
		keys.addAll(getDynamicPropertyKeys());
		return keys;
	}

	/**
	 * Map for storing allowed properties with ObjectType as key and a set of
	 * StaticProperty(s) as value.
	 */
	private static final Map<ObjectType, Set<StaticProperty>> ALLOWED_PROPS;

	static {
		Set<StaticProperty> propsCommon = EnumSet.of(StaticProperty.COMMENTS, StaticProperty.GRAPHID,
				StaticProperty.GROUPREF, StaticProperty.BIOPAXREF, StaticProperty.ZORDER);
		Set<StaticProperty> propsCommonShape = EnumSet.of(StaticProperty.CENTERX, StaticProperty.CENTERY,
				StaticProperty.WIDTH, StaticProperty.HEIGHT, StaticProperty.COLOR);
		Set<StaticProperty> propsCommonStyle = EnumSet.of(StaticProperty.TEXTLABEL, StaticProperty.FONTNAME,
				StaticProperty.FONTWEIGHT, StaticProperty.FONTSTYLE, StaticProperty.FONTSIZE, StaticProperty.ALIGN,
				StaticProperty.VALIGN, StaticProperty.COLOR, StaticProperty.FILLCOLOR, StaticProperty.TRANSPARENT,
				StaticProperty.SHAPETYPE, StaticProperty.LINETHICKNESS, StaticProperty.LINESTYLE);
		Set<StaticProperty> propsCommonLine = EnumSet.of(StaticProperty.COLOR, StaticProperty.STARTX,
				StaticProperty.STARTY, StaticProperty.ENDX, StaticProperty.ENDY, StaticProperty.STARTLINETYPE,
				StaticProperty.ENDLINETYPE, StaticProperty.LINESTYLE, StaticProperty.LINETHICKNESS,
				StaticProperty.STARTGRAPHREF, StaticProperty.ENDGRAPHREF);
		ALLOWED_PROPS = new EnumMap<ObjectType, Set<StaticProperty>>(ObjectType.class);
		{
			Set<StaticProperty> propsMappinfo = EnumSet.of(StaticProperty.COMMENTS, StaticProperty.MAPINFONAME,
					StaticProperty.ORGANISM, StaticProperty.MAPINFO_DATASOURCE, StaticProperty.VERSION,
					StaticProperty.AUTHOR, StaticProperty.MAINTAINED_BY, StaticProperty.EMAIL,
					StaticProperty.LAST_MODIFIED, StaticProperty.LICENSE, StaticProperty.BOARDWIDTH,
					StaticProperty.BOARDHEIGHT);
			ALLOWED_PROPS.put(ObjectType.MAPPINFO, propsMappinfo);
		}
		{
			Set<StaticProperty> propsState = EnumSet.of(StaticProperty.RELX, StaticProperty.RELY, StaticProperty.WIDTH,
					StaticProperty.HEIGHT, StaticProperty.MODIFICATIONTYPE, StaticProperty.GRAPHREF,
					StaticProperty.ROTATION);
			propsState.addAll(propsCommon);
			propsState.addAll(propsCommonStyle);
			ALLOWED_PROPS.put(ObjectType.STATE, propsState);
		}
		{
			Set<StaticProperty> propsShape = EnumSet.of(StaticProperty.FILLCOLOR, StaticProperty.SHAPETYPE,
					StaticProperty.ROTATION, StaticProperty.TRANSPARENT, StaticProperty.LINESTYLE);
			propsShape.addAll(propsCommon);
			propsShape.addAll(propsCommonStyle);
			propsShape.addAll(propsCommonShape);
			ALLOWED_PROPS.put(ObjectType.SHAPE, propsShape);
		}
		{
			Set<StaticProperty> propsDatanode = EnumSet.of(StaticProperty.GENEID, StaticProperty.DATASOURCE,
					StaticProperty.TEXTLABEL, StaticProperty.TYPE);
			propsDatanode.addAll(propsCommon);
			propsDatanode.addAll(propsCommonStyle);
			propsDatanode.addAll(propsCommonShape);
			ALLOWED_PROPS.put(ObjectType.DATANODE, propsDatanode);
		}
		{
			Set<StaticProperty> propsGraphLine = new HashSet<StaticProperty>();
			propsGraphLine.addAll(propsCommon);
			propsGraphLine.addAll(propsCommonLine);
			ALLOWED_PROPS.put(ObjectType.GRAPHLINE, propsGraphLine);
		}
		{
			Set<StaticProperty> propsLine = EnumSet.of(StaticProperty.GENEID, StaticProperty.DATASOURCE);
			propsLine.addAll(propsCommon);
			propsLine.addAll(propsCommonLine);
			ALLOWED_PROPS.put(ObjectType.LINE, propsLine);
		}
		{
			Set<StaticProperty> propsLabel = EnumSet.of(StaticProperty.HREF);
			propsLabel.addAll(propsCommon);
			propsLabel.addAll(propsCommonStyle);
			propsLabel.addAll(propsCommonShape);
			ALLOWED_PROPS.put(ObjectType.LABEL, propsLabel);
		}
		{
			Set<StaticProperty> propsGroup = EnumSet.of(StaticProperty.GROUPID, StaticProperty.GROUPREF,
					StaticProperty.BIOPAXREF, StaticProperty.GROUPSTYLE, StaticProperty.TEXTLABEL,
					StaticProperty.COMMENTS, StaticProperty.ZORDER);
			ALLOWED_PROPS.put(ObjectType.GROUP, propsGroup);
		}
		{
			Set<StaticProperty> propsInfobox = EnumSet.of(StaticProperty.CENTERX, StaticProperty.CENTERY,
					StaticProperty.ZORDER);
			ALLOWED_PROPS.put(ObjectType.INFOBOX, propsInfobox);
		}
		{
			Set<StaticProperty> propsLegend = EnumSet.of(StaticProperty.CENTERX, StaticProperty.CENTERY,
					StaticProperty.ZORDER);
			ALLOWED_PROPS.put(ObjectType.LEGEND, propsLegend);
		}
		{
			Set<StaticProperty> propsBiopax = EnumSet.noneOf(StaticProperty.class);
			ALLOWED_PROPS.put(ObjectType.BIOPAX, propsBiopax);
		}
	};

	/**
	 * Gets all attributes that are stored as static members.
	 * 
	 * @return all attributes stored as static members.
	 */
	public Set<StaticProperty> getStaticPropertyKeys() {
		return ALLOWED_PROPS.get(getObjectType());
	}

	/**
	 * Sets dynamic or static properties at the same time.
	 * 
	 * TODO: Will be replaced with setProperty in the future.
	 */
	public void setPropertyEx(Object key, Object value) {
		if (key instanceof StaticProperty) {
			setStaticProperty((StaticProperty) key, value);
		} else if (key instanceof String) {
			setDynamicProperty((String) key, value.toString());
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Gets dynamic or static properties at the same time.
	 * 
	 * @param key the key for dynamic or static property values.
	 * @return dynamic or static properties.
	 */
	public Object getPropertyEx(Object key) {
		if (key instanceof StaticProperty) {
			return getStaticProperty((StaticProperty) key);
		} else if (key instanceof String) {
			return getDynamicProperty((String) key);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * This works so that o.setNotes(x) is the equivalent of o.setProperty("Notes",
	 * x);
	 *
	 * Value may be null in some cases, e.g. graphRef
	 *
	 * @param key
	 * @param value
	 */
	public void setStaticProperty(StaticProperty key, Object value) {
		if (!getStaticPropertyKeys().contains(key))
			throw new IllegalArgumentException(
					"Property " + key.name() + " is not allowed for objects of type " + getObjectType());
		switch (key) {
		case COMMENTS:
			setComments((List<Comment>) value);
			break;
		case COLOR:
			setColor((Color) value);
			break;

		case CENTERX:
			setMCenterX((Double) value);
			break;
		case CENTERY:
			setMCenterY((Double) value);
			break;
		case WIDTH:
			setMWidth((Double) value);
			break;
		case HEIGHT:
			setMHeight((Double) value);
			break;

		case FILLCOLOR:
			setFillColor((Color) value);
			break;
		case SHAPETYPE:
			setShapeType((IShape) value);
			break;
		case ROTATION:
			setRotation((Double) value);
			break;
		case RELX:
			setRelX((Double) value);
			break;
		case RELY:
			setRelY((Double) value);
			break;
		case STARTX:
			setMStartX((Double) value);
			break;
		case STARTY:
			setMStartY((Double) value);
			break;
		case ENDX:
			setMEndX((Double) value);
			break;
		case ENDY:
			setMEndY((Double) value);
			break;
		case ENDLINETYPE:
			setEndLineType((LineType) value);
			break;
		case STARTLINETYPE:
			setStartLineType((LineType) value);
			break;
		case LINESTYLE:
			setLineStyle((Integer) value);
			break;

		case ORIENTATION:
			setOrientation((Integer) value);
			break;

		case GENEID:
			setElementID((String) value);
			break;
		case DATASOURCE:
			if (value instanceof DataSource) {
				setDataSource((DataSource) value);
			} else {
				setDataSource(DataSource.getByFullName((String) value));
			}
			break;
		case TYPE:
			setDataNodeType((String) value);
			break;

		case TEXTLABEL:
			setTextLabel((String) value);
			break;
		case HREF:
			setHref((String) value);
			break;
		case FONTNAME:
			setFontName((String) value);
			break;
		case FONTWEIGHT:
			setBold((Boolean) value);
			break;
		case FONTSTYLE:
			setItalic((Boolean) value);
			break;
		case FONTSIZE:
			setMFontSize((Double) value);
			break;
		case MAPINFONAME:
			setMapInfoName((String) value);
			break;
		case ORGANISM:
			setOrganism((String) value);
			break;
		case MAPINFO_DATASOURCE:
			setMapInfoDataSource((String) value);
			break;
		case VERSION:
			setVersion((String) value);
			break;
		case AUTHOR:
			setAuthor((String) value);
			break;
		case MAINTAINED_BY:
			setMaintainer((String) value);
			break;
		case EMAIL:
			setEmail((String) value);
			break;
		case LAST_MODIFIED:
			setLastModified((String) value);
			break;
		case LICENSE:
			setCopyright((String) value);
			break;
		case BOARDWIDTH:
			// ignore, board width is calculated automatically
			break;
		case BOARDHEIGHT:
			// ignore, board width is calculated automatically
			break;
		case GRAPHID:
			setElementId((String) value);
			break;
		case STARTGRAPHREF:
			setStartGraphRef((String) value);
			break;
		case ENDGRAPHREF:
			setEndGraphRef((String) value);
			break;
		case GROUPID:
			setGroupId((String) value);
			break;
		case GROUPREF:
			setGroupRef((String) value);
			break;
		case TRANSPARENT:
			setTransparent((Boolean) value);
			break;

		case BIOPAXREF:
			setBiopaxRefs((List<String>) value);
			break;
		case ZORDER:
			setZOrder((Integer) value);
			break;
		case GROUPSTYLE:
			if (value instanceof GroupType) {
				setGroupStyle((GroupType) value);
			} else {
				setGroupStyle(GroupType.fromName((String) value));
			}
			break;
		case ALIGN:
			setAlign((HAlignType) value);
			break;
		case VALIGN:
			setValign((VAlignType) value);
			break;
		case LINETHICKNESS:
			setLineThickness((Double) value);
			break;
		}
	}

	/**
	 * Gets static properties
	 * 
	 * @param key
	 * @return
	 */
	public Object getStaticProperty(StaticProperty key) {
		if (!getStaticPropertyKeys().contains(key))
			throw new IllegalArgumentException(
					"Property " + key.name() + " is not allowed for objects of type " + getObjectType());
		Object result = null;
		switch (key) {
		case COMMENTS:
			result = getComments();
			break;
		case COLOR:
			result = getColor();
			break;

		case CENTERX:
			result = getMCenterX();
			break;
		case CENTERY:
			result = getMCenterY();
			break;
		case WIDTH:
			result = getMWidth();
			break;
		case HEIGHT:
			result = getMHeight();
			break;

		case FILLCOLOR:
			result = getFillColor();
			break;
		case SHAPETYPE:
			result = getShapeType();
			break;
		case ROTATION:
			result = getRotation();
			break;
		case RELX:
			result = getRelX();
			break;
		case RELY:
			result = getRelY();
			break;
		case STARTX:
			result = getMStartX();
			break;
		case STARTY:
			result = getMStartY();
			break;
		case ENDX:
			result = getMEndX();
			break;
		case ENDY:
			result = getMEndY();
			break;
		case ENDLINETYPE:
			result = getEndLineType();
			break;
		case STARTLINETYPE:
			result = getStartLineType();
			break;
		case LINESTYLE:
			result = getLineStyle();
			break;

		case ORIENTATION:
			result = getOrientation();
			break;

		case GENEID:
			result = getElementID();
			break;
		case DATASOURCE:
			result = getDataSource();
			break;
		case TYPE:
			result = getDataNodeType();
			break;

		case TEXTLABEL:
			result = getTextLabel();
			break;
		case HREF:
			result = getHref();
			break;
		case FONTNAME:
			result = getFontName();
			break;
		case FONTWEIGHT:
			result = isBold();
			break;
		case FONTSTYLE:
			result = isItalic();
			break;
		case FONTSIZE:
			result = getMFontSize();
			break;

		case MAPINFONAME:
			result = getMapInfoName();
			break;
		case ORGANISM:
			result = getOrganism();
			break;
		case MAPINFO_DATASOURCE:
			result = getMapInfoDataSource();
			break;
		case VERSION:
			result = getVersion();
			break;
		case AUTHOR:
			result = getAuthor();
			break;
		case MAINTAINED_BY:
			result = getMaintainer();
			break;
		case EMAIL:
			result = getEmail();
			break;
		case LAST_MODIFIED:
			result = getLastModified();
			break;
		case LICENSE:
			result = getCopyright();
			break;
		case BOARDWIDTH:
			result = getMBoardSize()[0];
			break;
		case BOARDHEIGHT:
			result = getMBoardSize()[1];
			break;
		case GRAPHID:
			result = getElementId();
			break;
		case STARTGRAPHREF:
			result = getStartGraphRef();
			break;
		case ENDGRAPHREF:
			result = getEndGraphRef();
			break;
		case GROUPID:
			result = createGroupId();
			break;
		case GROUPREF:
			result = getGroupRef();
			break;
		case TRANSPARENT:
			result = isTransparent();
			break;
		case BIOPAXREF:
			result = getBiopaxRefs();
			break;
		case ZORDER:
			result = getZOrder();
			break;
		case GROUPSTYLE:
			result = getGroupStyle().toString();
			break;
		case ALIGN:
			result = getAlign();
			break;
		case VALIGN:
			result = getValign();
			break;
		case LINETHICKNESS:
			result = getLineThickness();
			break;
		}

		return result;
	}

	/**
	 * Copies values from a source PathwayElement by setting attributes to that of
	 * the source.
	 * 
	 * Note: doesn't change parent, only fields. Used by UndoAction.
	 *
	 * @param src the source PathwayElement from which to copy values.
	 */
	public void copyValuesFrom(PathwayElement src) {
		dynamicProperties = new TreeMap<String, String>(src.dynamicProperties); // create copy
		author = src.author;
		copyright = src.copyright;
		mCenterx = src.mCenterx;
		mCentery = src.mCentery;
		relX = src.relX;
		relY = src.relY;
		zOrder = src.zOrder;
		color = src.color;
		fillColor = src.fillColor;
		dataSource = src.dataSource;
		email = src.email;
		fontName = src.fontName;
		mFontSize = src.mFontSize;
		fBold = src.fBold;
		fItalic = src.fItalic;
		fStrikethru = src.fStrikethru;
		fUnderline = src.fUnderline;
		setGeneID = src.setGeneID;
		dataNodeType = src.dataNodeType;
		mHeight = src.mHeight;
		textLabel = src.textLabel;
		href = src.href;
		lastModified = src.lastModified;
		lineStyle = src.lineStyle;
		startLineType = src.startLineType;
		endLineType = src.endLineType;
		maintainer = src.maintainer;
		mapInfoDataSource = src.mapInfoDataSource;
		mapInfoName = src.mapInfoName;
		organism = src.organism;
		rotation = src.rotation;
		shapeType = src.shapeType;
		lineThickness = src.lineThickness;
		align = src.align;
		valign = src.valign;
		mPoints = new ArrayList<MPoint>();
		for (MPoint p : src.mPoints) {
			mPoints.add(new MPoint(p));
		}
		for (MAnchor a : src.anchors) {
			anchors.add(new MAnchor(a));
		}
		comments = new ArrayList<Comment>();
		for (Comment c : src.comments) {
			try {
				comments.add((Comment) c.clone());
			} catch (CloneNotSupportedException e) {
				assert (false);
				/* not going to happen */
			}
		}
		version = src.version;
		mWidth = src.mWidth;
		graphId = src.graphId;
		graphRef = src.graphRef;
		groupId = src.groupId;
		groupRef = src.groupRef;
		groupStyle = src.groupStyle;
		connectorType = src.connectorType;
		biopaxRefs = (List<String>) ((ArrayList<String>) src.biopaxRefs).clone();
		fireObjectModifiedEvent(PathwayElementEvent.createAllPropertiesEvent(this));
	}

	/**
	 * Copies PathwayElement object. The object will not be part of the same Pathway
	 * object, it's parent will be set to null.
	 *
	 * No events will be sent to the parent of the original PathwayElement.
	 * 
	 * @return result the copy of PathwayElement.
	 */
	public PathwayElement copy() {
		PathwayElement result = PathwayElement.createPathwayElement(objectType);
		result.copyValuesFrom(this);
		result.parent = null;
		return result;
	}

	protected ObjectType objectType = ObjectType.DATANODE;

	/**
	 * Returns objectType of this pathway element.
	 * 
	 * @return objectType the objectType of this pathway element.
	 */
	public ObjectType getObjectType() {
		return objectType;
	}

	/* ------------------------------- LINES ------------------------------- */

	/**
	 * List of Points.
	 */
	private List<MPoint> mPoints = Arrays.asList(new MPoint(0, 0), new MPoint(0, 0));

	public void setMPoints(List<MPoint> points) {
		if (points != null) {
			if (points.size() < 2) {
				throw new IllegalArgumentException("Points array should at least have two elements");
			}
			mPoints = points;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	public MPoint getMStart() {
		return mPoints.get(0);
	}

	public void setMStart(MPoint p) {
		getMStart().moveTo(p);
	}

	public MPoint getMEnd() {
		return mPoints.get(mPoints.size() - 1);
	}

	public void setMEnd(MPoint p) {
		getMEnd().moveTo(p);
	}

	public List<MPoint> getMPoints() {
		return mPoints;
	}

	public double getMStartX() {
		return getMStart().getX();
	}

	public void setMStartX(double v) {
		getMStart().setX(v);
	}

	public double getMStartY() {
		return getMStart().getY();
	}

	public void setMStartY(double v) {
		getMStart().setY(v);
	}

	public double getMEndX() {
		return mPoints.get(mPoints.size() - 1).getX();
	}

	public void setMEndX(double v) {
		getMEnd().setX(v);
	}

	public double getMEndY() {
		return getMEnd().getY();
	}

	public void setMEndY(double v) {
		getMEnd().setY(v);
	}

	protected int lineStyle = LineStyleType.SOLID;

	public int getLineStyle() {
		return lineStyle;
	}

	public void setLineStyle(int value) {
		if (lineStyle != value) {
			lineStyle = value;
			// handle LineStyle.DOUBLE until GPML is updated
			// TODO: remove after next GPML update
			if (lineStyle == LineStyleType.DOUBLE)
				setDynamicProperty(LineStyleType.DOUBLE_LINE_KEY, "Double");
			else
				setDynamicProperty(LineStyleType.DOUBLE_LINE_KEY, null);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
		}
	}

	protected LineType endLineType = LineType.LINE;
	protected LineType startLineType = LineType.LINE;

	public LineType getStartLineType() {
		return startLineType == null ? LineType.LINE : startLineType;
	}

	public LineType getEndLineType() {
		return endLineType == null ? LineType.LINE : endLineType;
	}

	public void setStartLineType(LineType value) {
		if (startLineType != value) {
			startLineType = value;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.STARTLINETYPE));
		}
	}

	public void setEndLineType(LineType value) {
		if (endLineType != value) {
			endLineType = value;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ENDLINETYPE));
		}
	}

	private ConnectorType connectorType = ConnectorType.STRAIGHT;

	public void setConnectorType(ConnectorType type) {
		if (connectorType == null) {
			throw new IllegalArgumentException();
		}
		if (!connectorType.equals(type)) {
			connectorType = type;
			// TODO: create a static property for connector type, linestyle is not the
			// correct mapping
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
		}
	}

	public ConnectorType getConnectorType() {
		return connectorType;
	}

	// TODO: end of new elements
	protected List<MAnchor> anchors = new ArrayList<MAnchor>();

	/**
	 * Get the anchors for this line.
	 * 
	 * @return A list with the anchors, or an empty list, if no anchors are defined
	 */
	public List<MAnchor> getMAnchors() {
		return anchors;
	}

	/**
	 * Add a new anchor to this line at the given position.
	 * 
	 * @param position The relative position on the line, between 0 (start) to 1
	 *                 (end).
	 */
	public MAnchor addMAnchor(double position) {
		if (position < 0 || position > 1) {
			throw new IllegalArgumentException("Invalid position value '" + position + "' must be between 0 and 1");
		}
		MAnchor anchor = new MAnchor(position);
		anchors.add(anchor);
		// No property for anchor, use LINESTYLE as dummy property to force redraw on
		// line
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
		return anchor;
	}

	/**
	 * Remove the given anchor
	 */
	public void removeMAnchor(MAnchor anchor) {
		if (anchors.remove(anchor)) {
			// No property for anchor, use LINESTYLE as dummy property to force redraw on
			// line
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINESTYLE));
		}
	}

	/* ------------------------------- COLOR ------------------------------- */

	/**
	 * Color is white by default.
	 */
	protected Color color = new Color(0, 0, 0);

	/**
	 * Gets color of this pathway element.
	 * 
	 * @return color the color of this pathway element.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets color of this pathway element.
	 * 
	 * @param color the color of this pathway element.
	 */
	public void setColor(Color color) {
		if (color == null)
			throw new IllegalArgumentException();
		if (this.color != color) {
			this.color = color;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.COLOR));
		}
	}

	/**
	 * FillColor of null is equivalent to transparent.
	 */
	protected Color fillColor = null;

	/**
	 * Gets fillColor of this pathway element.
	 * 
	 * @return fillColor the fill color of this pathway element.
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Sets fillColor of this pathway element.
	 * 
	 * @param color the fill color of this pathway element.
	 */
	public void setFillColor(Color color) {
		if (this.fillColor != color) {
			this.fillColor = color;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FILLCOLOR));
		}
	}

	/**
	 * Checks if fill color is equal to null or the alpha value is equal to 0.
	 * 
	 * @return true if fill color equal to null or alpha value equal to 0, false
	 *         otherwise.
	 */
	public boolean isTransparent() {
		return fillColor == null || fillColor.getAlpha() == 0;
	}

	/**
	 * TODO: Logic seems weird...
	 * 
	 * Sets the alpha component of fillColor to 0 if true, sets the alpha component
	 * of fillColor to 255 if false.
	 * 
	 * @param value the boolean value.
	 */
	public void setTransparent(boolean value) {
		if (isTransparent() != value) {
			if (fillColor == null) {
				fillColor = Color.WHITE;
			}
			int alpha = value ? 0 : 255;
			fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), alpha);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TRANSPARENT));
		}
	}

	/* ------------------------------- GENERAL ------------------------------- */

	/**
	 * List of comments.
	 */
	List<Comment> comments = new ArrayList<Comment>();

	/**
	 * Returns comments the list of Comment.
	 * 
	 * @return comments the list of comments.
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/**
	 * Sets comments to given list of Comment.
	 * 
	 * @param comments the list of comments.
	 */
	public void setComments(List<Comment> comments) {
		if (this.comments != comments) {
			this.comments = comments;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.COMMENTS));
		}
	}

	/**
	 * TODO: Move this
	 * 
	 * Adds String comment and String source...
	 * 
	 * @param comment the comment.
	 * @param source  the source.
	 */
	public void addComment(String comment, String source) {
		addComment(new Comment(comment, source));
	}

	/**
	 * Adds given comment to comments list.
	 * 
	 * @param comment the comment to be added.
	 */
	public void addComment(Comment comment) {
		comments.add(comment);
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.COMMENTS));
	}

	/**
	 * Removes given comment from comments list.
	 * 
	 * @param comment the comment to be removed.
	 */
	public void removeComment(Comment comment) {
		comments.remove(comment);
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.COMMENTS));
	}

	/**
	 * TODO: Need to be moved or something...
	 * 
	 * Finds the first comment with a specific source.
	 * 
	 * @returns the comment with a given source.
	 */
	public String findComment(String source) {
		for (Comment c : comments) {
			if (source.equals(c.source)) {
				return c.comment;
			}
		}
		return null;
	}

	/* ------------------------------- DATANODE ------------------------------- */

	/**
	 * 
	 */
	protected String setGeneID = "";

	/**
	 * @deprecated Use {@link #getElementID()} instead
	 */
	public String getGeneID() {
		return getElementID();
	}

	public String getElementID() {
		return setGeneID;
	}

	/**
	 * @deprecated Use {@link #setElementID(String)} instead
	 */
	public void setGeneID(String v) {
		setElementID(v);
	}

	public void setElementID(String v) {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		v = v.trim();
		if (!Utils.stringEquals(setGeneID, v)) {
			setGeneID = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GENEID));
		}
	}

	/**
	 * DataNodeType is "Unknown by default".
	 */
	protected String dataNodeType = "Unknown";

	/**
	 * Gets DataNodeType.
	 * 
	 * @return dataNodeType the data node type.
	 */
	public String getDataNodeType() {
		return dataNodeType;
	}

	/**
	 * Sets data node type to given DataNodeType.
	 * 
	 * @param dataNodeType the data node type.
	 */
	public void setDataNodeType(DataNodeType dataNodeType) {
		setDataNodeType(dataNodeType.getName());
	}

	/**
	 * Sets data node type to given String.
	 * 
	 * @return dataNodeType the data node type.
	 */
	public void setDataNodeType(String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		if (!Utils.stringEquals(dataNodeType, value)) {
			dataNodeType = value;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TYPE));
		}
	}

	/**
	 * The pathway data source.
	 */
	protected DataSource source = null;

	/**
	 * Gets pathway data source.
	 * 
	 * @return source the pathway data source.
	 */
	public DataSource getDataSource() {
		return source;
	}

	/**
	 * Sets pathway data source.
	 * 
	 * @param source the pathway data source.
	 */
	public void setDataSource(DataSource source) {
		if (this.source != source) {
			this.source = source;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.DATASOURCE));
		}
	}

	/**
	 * returns GeneID and dataSource combined in an Xref. Pathway elements DataNode,
	 * State, Interaction, and Group can contain a Xref.
	 *
	 * Same as new Xref ( pathwayElement.getGeneID(), pathwayElement.getDataSource()
	 * );
	 */
	public Xref getXref() {
		// TODO: Store Xref by default, derive setGeneID and dataSource from it.
		return new Xref(setGeneID, source);
	}

	/**
	 * CenterX of this pathway element object. The x coordinate of the middle of an
	 * object.
	 */
	protected double mCenterX = 0;

	/**
	 * Gets the x coordinate of the middle of an object.
	 * 
	 * @return mCenterX the middle of an object in the x direction.
	 */
	public double getMCenterX() {
		return mCenterX;
	}

	/**
	 * Sets the x coordinate of the middle of an object.
	 * 
	 * @param mCenterX the middle of an object in the x direction.
	 */
	public void setMCenterX(double mCenterX) {
		if (this.mCenterX != mCenterX) {
			this.mCenterX = mCenterX;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * CenterY of this pathway element object. The y coordinate of the middle of an
	 * object.
	 */
	protected double mCenterY = 0;

	/**
	 * Gets the y coordinate of the middle of an object.
	 * 
	 * @return mCenterY the middle of an object in the y direction.
	 */
	public double getMCenterY() {
		return mCenterY;
	}

	/**
	 * Sets the y coordinate of the middle of an object.
	 * 
	 * @param mCenterY the middle of an object in the y direction.
	 */
	public void setMCenterY(double mCenterY) {
		if (this.mCenterY != mCenterY) {
			this.mCenterY = mCenterY;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * mWidth of this object. The pixel value for the x dimensional length of an
	 * object.
	 */
	protected double mWidth = 0;

	/**
	 * Gets the pixel value for the x dimensional length of an object.
	 * 
	 * @return mWidth the width of an object.
	 */
	public double getMWidth() {
		return mWidth;
	}

	/**
	 * Sets the pixel value for the x dimensional length of an object.
	 * 
	 * @param mWidth the width of an object.
	 * @throws IllegalArgumentException if mWidth is a negative value.
	 */
	public void setMWidth(double mWidth) {
		if (mWidth < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + mWidth);
		}
		if (this.mWidth != mWidth) {
			this.mWidth = mWidth;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/**
	 * mHeight of this object. The pixel value for the y dimensional length of an
	 * object.
	 */
	protected double mHeight = 0;

	/**
	 * Gets the pixel value for the y dimensional length of an object.
	 * 
	 * @return mHeight the height of an object.
	 */
	public double getMHeight() {
		return mHeight;
	}

	/**
	 * Sets the pixel value for the y dimensional length of an object.
	 * 
	 * @param mHeight the height of an object.
	 * @throws IllegalArgumentException if mHeight is a negative value.
	 */
	public void setMHeight(double mHeight) {
		if (mHeight < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + mHeight);
		}
		if (this.mHeight != mHeight) {
			this.mHeight = mHeight;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	/* ------------------------------- SHAPE ------------------------------- */

	/**
	 * Gets the starty for shapes.
	 * 
	 * @return the start y for shape.
	 */
	public double getMTop() {
		return mCenterY - mHeight / 2;
	}

	/**
	 * Sets the start y for shape.
	 * 
	 * @return the start y for shape.
	 */
	public void setMTop(double v) {
		mCenterY = v + mHeight / 2;
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
	}

	/**
	 * Gets the startx for shapes.
	 * 
	 * @return the start x for shape.
	 */
	public double getMLeft() {
		return mCenterX - mWidth / 2;
	}

	/**
	 * Sets the startx for shapes.
	 * 
	 * @return the start x for shape.
	 */
	public void setMLeft(double v) {
		mCenterX = v + mWidth / 2;
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
	}

	/**
	 * Default IShape shapeType is Rectangle.
	 */
	protected IShape shapeType = ShapeType.RECTANGLE;

	/**
	 * Gets shapeType the IShape.
	 * 
	 * @return shapeType the IShape.
	 */
	public IShape getShapeType() {
		return shapeType;
	}

	/**
	 * Sets shape type to given IShape.
	 * 
	 * @param IShape the shape type.
	 */
	public void setShapeType(IShape shapeType) {
		if (this.shapeType != shapeType) {
			this.shapeType = shapeType;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.SHAPETYPE));
		}
	}

	/**
	 * Gets the orientation for shapes.
	 * 
	 * @return the orientation for
	 */
	public int getOrientation() {
		double r = rotation / Math.PI;
		if (r < 1.0 / 4 || r >= 7.0 / 4)
			return OrientationType.TOP;
		if (r > 5.0 / 4 && r <= 7.0 / 4)
			return OrientationType.LEFT;
		if (r > 3.0 / 4 && r <= 5.0 / 4)
			return OrientationType.BOTTOM;
		if (r > 1.0 / 4 && r <= 3.0 / 4)
			return OrientationType.RIGHT;
		return 0;
	}

	/**
	 * Sets the orientation for shapes.
	 * 
	 * @param orientation the orientation integer.
	 */
	public void setOrientation(int orientation) {
		switch (orientation) {
		case OrientationType.TOP:
			setRotation(0);
			break;
		case OrientationType.LEFT:
			setRotation(Math.PI * (3.0 / 2));
			break;
		case OrientationType.BOTTOM:
			setRotation(Math.PI);
			break;
		case OrientationType.RIGHT:
			setRotation(Math.PI / 2);
			break;
		}
	}

	/**
	 * Rotation of this shape object.
	 */
	protected double rotation = 0; // in radians

	/**
	 * Gets the rotation of this shape.
	 * 
	 * @return rotation the rotation of this shape.
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Sets the rotation of this shape.
	 * 
	 * @return rotation the rotation of this shape.
	 */
	public void setRotation(double rotation) {
		if (this.rotation != rotation) {
			this.rotation = rotation;

			// Rotation is not stored for State, so we use a dynamic property.
			// TODO: remove after next GPML update.
			if (objectType == ObjectType.STATE && rotation != 0) {
				setDynamicProperty(State.ROTATION_KEY, "" + rotation);
			}
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}

	}

	/**
	 * Gets the rectangular bounds of the object after rotation is applied.
	 * 
	 * @return bounds the rectangular bounds of the object.
	 */
	public Rectangle2D getRBounds() {
		Rectangle2D bounds = getMBounds();
		AffineTransform t = new AffineTransform();
		t.rotate(getRotation(), getMCenterX(), getMCenterY());
		bounds = t.createTransformedShape(bounds).getBounds2D();
		return bounds;
	}

	/**
	 * Gets the rectangular bounds of the object without taking rotation into
	 * account.
	 * 
	 * @return the rectangular bounds of the object.
	 */
	public Rectangle2D getMBounds() {
		return new Rectangle2D.Double(getMLeft(), getMTop(), getMWidth(), getMHeight());
	}

	/* ------------------------------- LABEL ------------------------------- */

	/**
	 * The boolean for font weight normal (default) or bold, a bold font would have
	 * more weight. FontAttributes.fontWeight in GPML.
	 */
	protected boolean fBold = false;

	/**
	 * Checks if font weight is normal or bold.
	 * 
	 * @return fBold the boolean, if true font weight is bold. If false, font weight
	 *         is normal.
	 */
	public boolean isBold() {
		return fBold;
	}

	/**
	 * Sets fBold the boolean for font weight
	 * 
	 * @param fBold the boolean, if true font weight is bold. If false, font weight
	 *              is normal.
	 */
	public void setBold(boolean fBold) {
		if (this.fBold != fBold) {
			this.fBold = fBold;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTWEIGHT));
		}
	}

	/**
	 * The boolean for typographic style normal (default) or strikethru.
	 * FontAttributes.fontStrikethru in GPML.
	 */
	protected boolean fStrikethru = false;

	/**
	 * Checks if typographic style is normal or strikethru.
	 * 
	 * @return fStrikethru the boolean, if true typographic style is strikethru. If
	 *         false, typographic style is normal.
	 */
	public boolean isStrikethru() {
		return fStrikethru;
	}

	/**
	 * Sets fStrikethru the boolean for typographic style normal or strikethru.
	 * 
	 * @param fStrikethru the boolean, if true typographic style is strikethru. If
	 *                    false, typographic style is normal.
	 */
	public void setStrikethru(boolean fStrikethru) {
		if (this.fStrikethru != fStrikethru) {
			this.fStrikethru = fStrikethru;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTSTYLE));
		}
	}

	/**
	 * The boolean for typographic style normal (default) or underline.
	 * FontAttributes.fontDecoration in GPML.
	 */
	protected boolean fUnderline = false;

	/**
	 * Checks if typographic style is normal or underline.
	 * 
	 * @return fUnderline the boolean, if true typographic style is underline. If
	 *         false, typographic style is normal.
	 */
	public boolean isUnderline() {
		return fUnderline;
	}

	/**
	 * Sets fUnderline the boolean for typographic style normal or underline.
	 * 
	 * @param fUnderline the boolean, if true typographic style is underline. If
	 *                   false, typographic style is normal.
	 */
	public void setUnderline(boolean v) {
		if (fUnderline != v) {
			fUnderline = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTSTYLE));
		}
	}

	/**
	 * The boolean for typographic style normal (default) or italic.
	 * FontAttributes.fontStyle in GPML.
	 */
	protected boolean fItalic = false;

	/**
	 * Checks if typographic style is normal or italic.
	 * 
	 * @return fItalic the boolean, if true typographic style is italic. If false,
	 *         typographic style is normal.
	 */
	public boolean isItalic() {
		return fItalic;
	}

	/**
	 * Sets fItalic the boolean for typographic style normal or italic.
	 * 
	 * @param fItalic the boolean, if true typographic style is italic. If false,
	 *                typographic style is normal.
	 */
	public void setItalic(boolean fItalic) {
		if (this.fItalic != fItalic) {
			this.fItalic = fItalic;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTSTYLE));
		}
	}

	/**
	 * The name of the set of printable text characters to be used for
	 * visualization, e.g., Arial. FontAttributes.fontName in GPML.
	 */
	protected String fontName = "Arial";

	/**
	 * Gets the font name of the printable text characters.
	 * 
	 * @return fontName the name of font.
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * Sets the font name of the printable text characters.
	 * 
	 * @param fontName the name of font.
	 * @throws IllegalArgumentException if given fontName is null.
	 */
	public void setFontName(String fontName) {
		if (fontName == null)
			throw new IllegalArgumentException();
		if (!Utils.stringEquals(this.fontName, fontName)) {
			this.fontName = fontName;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTNAME));
		}
	}

	/**
	 * The text label of this object.
	 */
	protected String textLabel = "";

	/**
	 * Gets the text label of this object.
	 * 
	 * @return textLabel the text label of this object.
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text label of this object to the given string or ""(empty) if input
	 * is null.
	 * 
	 * @param input the given string text. If input is null, textLabel is set to
	 *              ""(empty).
	 */
	public void setTextLabel(String input) {
		String text = (input == null) ? "" : input;
		if (!Utils.stringEquals(textLabel, text)) {
			textLabel = text;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.TEXTLABEL));
		}
	}

	/**
	 * The hyperlink optionally specified in a Label for a reference to a url.
	 */
	protected String href = "";

	/**
	 * Gets the hyperlink for a Label.
	 * 
	 * @return href the hyperlink reference to a url.
	 */
	public String getHref() {
		return href;
	}

	/**
	 * Sets the hyperlink for a Label.
	 * 
	 * @param input the given string link. If input is null, href is set to
	 *              ""(empty).
	 */
	public void setHref(String input) {
		String link = (input == null) ? "" : input;
		if (!Utils.stringEquals(href, link)) {
			href = link;
			if (PreferenceManager.getCurrent() == null)
				PreferenceManager.init();
			setColor(PreferenceManager.getCurrent().getColor(GlobalPreference.COLOR_LINK));
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.HREF));
		}
	}

	/**
	 * Line width the pixel value for the width of a line.
	 */
	private double lineWidth = 1.0;

	/**
	 * Gets the pixel value for the width of a line.
	 * 
	 * @return lineWidth the width of a line.
	 */
	public double getLineWidth() {
		return lineWidth;
	}

	/**
	 * Sets the pixel value for the width of a line.
	 * 
	 * @param lineWidth the width of a line.
	 */
	public void setLineWidth(double lineWidth) {
		if (this.lineWidth != lineWidth) {
			this.lineWidth = lineWidth;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LINETHICKNESS));
		}
	}

	/**
	 * Font size the point value for the size of the font.
	 */
	protected double mFontSize = M_INITIAL_FONTSIZE;

	/**
	 * Sets point value for the size of the font.
	 * 
	 * @param fontSize the value for the size of the font.
	 */
	public double getMFontSize() {
		return mFontSize;
	}

	/**
	 * Sets point value for the size of the font.
	 * 
	 * @param fontSize the value for the size of the font.
	 */
	public void setMFontSize(double mFontSize) {
		if (this.mFontSize != mFontSize) {
			this.mFontSize = mFontSize;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.FONTSIZE));
		}
	}

	/* ------------------------------- PATHWAY ------------------------------- */

	/**
	 * The title of the pathway?
	 */
	protected String mapInfoName = "untitled";

	/**
	 * 
	 * @return
	 */
	public String getMapInfoName() {
		return mapInfoName;
	}

	/**
	 * 
	 * @param v
	 */
	public void setMapInfoName(String v) {
		if (v == null)
			throw new IllegalArgumentException();

		if (!Utils.stringEquals(mapInfoName, v)) {
			mapInfoName = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.MAPINFONAME));
		}
	}

	/**
	 * 
	 */
	protected String organism = null;

	/**
	 * 
	 * @return
	 */
	public String getOrganism() {
		return organism;
	}

	/**
	 * 
	 * @param v
	 */
	public void setOrganism(String v) {
		if (!Utils.stringEquals(organism, v)) {
			organism = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.ORGANISM));
		}
	}

	/**
	 * 
	 */
	protected String mapInfoDataSource = null;

	/**
	 * 
	 * @return
	 */
	public String getMapInfoDataSource() {
		return mapInfoDataSource;
	}

	/**
	 * 
	 * @param v
	 */
	public void setMapInfoDataSource(String v) {
		if (!Utils.stringEquals(mapInfoDataSource, v)) {
			mapInfoDataSource = v;
			fireObjectModifiedEvent(
					PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.MAPINFO_DATASOURCE));
		}
	}

	/**
	 * Vertical alignment.
	 */
	protected VAlignType vAlign = VAlignType.MIDDLE;

	/**
	 * Gets vertical alignment.
	 * 
	 * @return vAlign
	 */
	public VAlignType getValign() {
		return vAlign;
	}

	/**
	 * Sets vertical alignment.
	 * 
	 * @param vAlign
	 */
	public void setValign(VAlignType vAlign) {
		if (this.vAlign != vAlign) {
			this.vAlign = vAlign;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.VALIGN));
		}
	}

	/**
	 * Horizontal alignment.
	 */
	protected HAlignType hAlign = HAlignType.CENTER;

	/**
	 * Gets horizontal alignment.
	 * 
	 * @return hAlign
	 */
	public HAlignType getAlign() {
		return hAlign;
	}

	/**
	 * Sets horizontal alignment.
	 * 
	 * @param hAlign
	 */
	public void setAlign(HAlignType hAlign) {
		if (this.hAlign != hAlign) {
			this.hAlign = hAlign;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.HALIGN));
		}
	}

	/**
	 * 
	 */
	protected String version = null;

	/**
	 * 
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 
	 * @param v
	 */
	public void setVersion(String v) {
		if (!Utils.stringEquals(version, v)) {
			version = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.VERSION));
		}
	}

	/**
	 * 
	 */
	protected String author = null;

	/**
	 * 
	 * @return
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * 
	 * @param v
	 */
	public void setAuthor(String v) {
		if (!Utils.stringEquals(author, v)) {
			author = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.AUTHOR));
		}
	}

	/**
	 * 
	 */
	protected String maintainer = null;

	/**
	 * 
	 * @return
	 */
	public String getMaintainer() {
		return maintainer;
	}

	/**
	 * 
	 * @param v
	 */
	public void setMaintainer(String v) {
		if (!Utils.stringEquals(maintainer, v)) {
			maintainer = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.MAINTAINED_BY));
		}
	}

	/**
	 * 
	 */
	protected String email = null;

	/**
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @param v
	 */
	public void setEmail(String v) {
		if (!Utils.stringEquals(email, v)) {
			email = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.EMAIL));
		}
	}

	/**
	 * 
	 */
	protected String copyright = null;

	/**
	 * 
	 */
	public String getCopyright() {
		return copyright;
	}

	/**
	 * 
	 */
	public void setCopyright(String v) {
		if (!Utils.stringEquals(copyright, v)) {
			copyright = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LICENSE));
		}
	}

	/**
	 * 
	 */
	protected String lastModified = null;

	/**
	 * 
	 */
	public String getLastModified() {
		return lastModified;
	}

	/**
	 * 
	 */
	public void setLastModified(String v) {
		if (!Utils.stringEquals(lastModified, v)) {
			lastModified = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.LAST_MODIFIED));
		}
	}

	/**
	 * Calculates the drawing size on basis of the location and size of the
	 * containing pathway elements.
	 * 
	 * @return the drawing size
	 */
	public double[] getMBoardSize() {
		return parent.getMBoardSize();
	}

	/**
	 * Gets the board width from the drawing size.
	 * 
	 * @return the board width
	 */
	public double getMBoardWidth() {
		return getMBoardSize()[0];
	}

	/**
	 * Gets the board height from the drawing size.
	 * 
	 * @return the board height
	 */
	public double getMBoardHeight() {
		return getMBoardSize()[1];
	}

	/* ------------------------------- ID & GROUP ------------------------------- */

	/* AP20070508 */
	/**
	 * TODO: Is replaced with elementId in 2021.
	 */
	protected String groupId;

	/**
	 * 
	 */
	protected String elementId;

	/**
	 * 
	 */
	protected String groupRef;

	/**
	 * GroupType the type of group, e.g. Complex, Pathway, etc. GroupType is GROUP
	 * by default.
	 * 
	 * NB: groupStyle was renamed to groupType.
	 */
	protected GroupType groupType = GroupType.GROUP;

	/**
	 * 
	 * @return
	 */
	public String doGetElementId() {
		return elementId;
	}

	/**
	 * 
	 * @return
	 */
	public String getGroupRef() {
		return groupRef;
	}

	/**
	 * 
	 * @param s
	 */
	public void setGroupRef(String s) {
		if (groupRef == null || !groupRef.equals(s)) {
			if (parent != null) {
				if (groupRef != null) {
					parent.removeGroupRef(groupRef, this);
				}
				// Check: move add before remove??
				if (s != null) {
					parent.addGroupRef(s, this);
				}
			}
			groupRef = s;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPREF));
		}
	}

	/**
	 * 
	 * TODO: groupId is replaced with elementId in 2021.
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 
	 * TODO: groupId is replaced with elementId in 2021.
	 */
	public String createGroupId() {
		if (groupId == null) {
			setGroupId(parent.getUniqueGroupId());
		}
		return groupId;
	}

	/**
	 * Gets GroupType. GroupType is GROUP by default.
	 * 
	 * TODO: Default GroupType used to be NONE. NONE needs to be changed to GROUP.
	 * 
	 * @return groupType the type of group.
	 */
	public GroupType getGroupType() {
		if (groupType == null) {
			groupType = GroupType.GROUP;
		}
		return groupType;
	}

	/**
	 * Sets GroupType to the given groupType. 
	 * 
	 * @param groupType the type of group.
	 */
	public void setGroupType(GroupType groupType) {
		if (this.groupType != groupType) {
			this.groupType = groupType;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPTYPE));
		}
	}

	/**
	 * Set groupId. This id must be any string unique within the Pathway object
	 *
	 * @see Pathway#getUniqueId(java.util.Set)
	 */
	public void setGroupId(String w) {
		if (groupId == null || !groupId.equals(w)) {
			if (parent != null) {
				if (groupId != null) {
					parent.removeGroupId(groupId);
				}
				// Check: move add before remove??
				if (w != null) {
					parent.addGroupId(w, this);
				}
			}
			groupId = w;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GROUPID));
		}

	}

	protected String graphRef = null;

	/** graphRef property, used by Modification */
	public String getElementRef() {
		return graphRef;
	}

	/**
	 * Set graphRef property, used by State The new graphRef should exist and point
	 * to an existing DataNode
	 */
	public void setGraphRef(String value) {
		// TODO: check that new graphRef exists and that it points to a DataNode
		if (!(graphRef == null ? value == null : graphRef.equals(value))) {
			graphRef = value;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GRAPHREF));
		}
	}

	private double relX;

	/**
	 * relX property, used by State. Should normally be between -1.0 and 1.0, where
	 * 1.0 corresponds to the edge of the parent object
	 */
	public double getRelX() {
		return relX;
	}

	/**
	 * See getRelX
	 */
	public void setRelX(double value) {
		if (relX != value) {
			relX = value;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	private double relY;

	/**
	 * relX property, used by State. Should normally be between -1.0 and 1.0, where
	 * 1.0 corresponds to the edge of the parent object
	 */
	public double getRelY() {
		return relY;
	}

	/**
	 * See getRelX
	 */
	public void setRelY(double value) {
		if (relY != value) {
			relY = value;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	public String getElementId() {
		return elementId;
	}

	/**
	 * Set graphId. This id must be any string unique within the Pathway object
	 *
	 * @see Pathway#getUniqueId(java.util.Set)
	 */
	public void setElementId(String v) {
		ElementLink.setElementId(v, this, parent);
		elementId = v;
		fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.GRAPHID));
	}

	public String setGeneratedElementId() {
		setElementId(parent.getUniqueGraphId());
		return elementId;
	}

	public String getStartGraphRef() {
		return mPoints.get(0).getElementRef();
	}

	public void setStartGraphRef(String ref) {
		MPoint start = mPoints.get(0);
		start.setGraphRef(ref);
	}

	public String getEndGraphRef() {
		return mPoints.get(mPoints.size() - 1).getElementRef();
	}

	public void setEndGraphRef(String ref) {
		MPoint end = mPoints.get(mPoints.size() - 1);
		end.setGraphRef(ref);
	}

	private BiopaxReferenceManager bpRefMgr;

	public BiopaxReferenceManager getBiopaxReferenceManager() {
		if (bpRefMgr == null) {
			bpRefMgr = new BiopaxReferenceManager(this);
		}
		return bpRefMgr;
	}

	protected List<String> biopaxRefs = new ArrayList<String>();

	public List<String> getBiopaxRefs() {
		return biopaxRefs;
	}

	public void setBiopaxRefs(List<String> refs) {
		if (refs != null && !biopaxRefs.equals(refs)) {
			biopaxRefs = refs;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BIOPAXREF));
		}
	}

	public void addBiopaxRef(String ref) {
		if (ref != null && !biopaxRefs.contains(ref)) {
			biopaxRefs.add(ref);
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BIOPAXREF));
		}
	}

	public void removeBiopaxRef(String ref) {
		if (ref != null) {
			boolean changed = biopaxRefs.remove(ref);
			if (changed) {
				fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.BIOPAXREF));
			}
		}
	}

	public PathwayElement[] splitLine() {
		double centerX = (getMStartX() + getMEndX()) / 2;
		double centerY = (getMStartY() + getMEndY()) / 2;
		PathwayElement l1 = new PathwayElement(ObjectType.LINE);
		l1.copyValuesFrom(this);
		l1.setMStartX(getMStartX());
		l1.setMStartY(getMStartY());
		l1.setMEndX(centerX);
		l1.setMEndY(centerY);
		PathwayElement l2 = new PathwayElement(ObjectType.LINE);
		l2.copyValuesFrom(this);
		l2.setMStartX(centerX);
		l2.setMStartY(centerY);
		l2.setMEndX(getMEndX());
		l2.setMEndY(getMEndY());
		return new PathwayElement[] { l1, l2 };
	}

	int noFire = 0;

	public void dontFireEvents(int times) {
		noFire = times;
	}

	private Set<PathwayElementListener> listeners = new HashSet<PathwayElementListener>();

	public void addListener(PathwayElementListener v) {
		if (!listeners.contains(v))
			listeners.add(v);
	}

	public void removeListener(PathwayElementListener v) {
		listeners.remove(v);
	}

	public void fireObjectModifiedEvent(PathwayElementEvent e) {
		if (noFire > 0) {
			noFire -= 1;
			return;
		}
		if (parent != null)
			parent.childModified(e);
		for (PathwayElementListener g : listeners) {
			g.gmmlObjectModified(e);
		}
	}

	/**
	 * This sets the object to a suitable default size.
	 *
	 * This method is intended to be called right after the object is placed on the
	 * drawing with a click.
	 */
	public void setInitialSize() {
		switch (objectType) {
		case SHAPE:
			if (shapeType == ShapeType.BRACE) {
				setMWidth(M_INITIAL_BRACE_WIDTH);
				setMHeight(M_INITIAL_BRACE_HEIGHT);
			} else if (shapeType == ShapeType.MITOCHONDRIA || lineStyle == LineStyleType.DOUBLE) {
				setMWidth(M_INITIAL_CELLCOMP_WIDTH);
				setMHeight(M_INITIAL_CELLCOMP_HEIGHT);
			} else if (shapeType == ShapeType.SARCOPLASMICRETICULUM || shapeType == ShapeType.ENDOPLASMICRETICULUM
					|| shapeType == ShapeType.GOLGIAPPARATUS) {
				setMWidth(M_INITIAL_CELLCOMP_HEIGHT);
				setMHeight(M_INITIAL_CELLCOMP_WIDTH);
			} else {
				setMWidth(M_INITIAL_SHAPE_SIZE);
				setMHeight(M_INITIAL_SHAPE_SIZE);
			}
			break;
		case DATANODE:
			setMWidth(M_INITIAL_GENEPRODUCT_WIDTH);
			setMHeight(M_INITIAL_GENEPRODUCT_HEIGHT);
			break;
		case LINE:
			setMEndX(getMStartX() + M_INITIAL_LINE_LENGTH);
			setMEndY(getMStartY() + M_INITIAL_LINE_LENGTH);
			break;
		case GRAPHLINE:
			setMEndX(getMStartX() + M_INITIAL_LINE_LENGTH);
			setMEndY(getMStartY() + M_INITIAL_LINE_LENGTH);
			break;
		case STATE:
			setMWidth(M_INITIAL_STATE_SIZE);
			setMHeight(M_INITIAL_STATE_SIZE);
			break;
		case LABEL:
			setMWidth(M_INITIAL_LABEL_WIDTH);
			setMHeight(M_INITIAL_LABEL_HEIGHT);
		}
	}

	public Set<ElementRefContainer> getReferences() {
		return ElementLink.getReferences(this, parent);
	}

	/**
	 * 
	 */
	public int compareTo(PathwayElement o) {
		int rez = getZOrder() - o.getZOrder();
		if (rez != 0) {
			return rez;
		}
		String a = getElementId();
		String b = o.getElementId();
		if (a == null) {
			if (b == null) {
				return 0;
			}
			return -1;
		}
		if (b == null) {
			return 1;
		}
		return a.compareTo(b);
	}

	public Point2D toAbsoluteCoordinate(Point2D p) {
		double x = p.getX();
		double y = p.getY();
		Rectangle2D bounds = getRBounds();
		// Scale
		if (bounds.getWidth() != 0)
			x *= bounds.getWidth() / 2;
		if (bounds.getHeight() != 0)
			y *= bounds.getHeight() / 2;
		// Translate
		x += bounds.getCenterX();
		y += bounds.getCenterY();
		return new Point2D.Double(x, y);
	}

	/**
	 * @param mp a point in absolute model coordinates
	 * @returns the same point relative to the bounding box of this pathway element:
	 *          -1,-1 meaning the top-left corner, 1,1 meaning the bottom right
	 *          corner, and 0,0 meaning the center.
	 */
	public Point2D toRelativeCoordinate(Point2D mp) {
		double relX = mp.getX();
		double relY = mp.getY();
		Rectangle2D bounds = getRBounds();
		// Translate
		relX -= bounds.getCenterX();
		relY -= bounds.getCenterY();
		// Scalebounds.getCenterX();
		if (relX != 0 && bounds.getWidth() != 0)
			relX /= bounds.getWidth() / 2;
		if (relY != 0 && bounds.getHeight() != 0)
			relY /= bounds.getHeight() / 2;
		return new Point2D.Double(relX, relY);
	}

	public void printRefsDebugInfo() {
		System.err.println(objectType + " " + getElementId());
		if (this instanceof MLine) {
			for (MPoint p : getMPoints()) {
				System.err.println("  p: " + p.getElementId());
			}
			for (MAnchor a : getMAnchors()) {
				System.err.println("  a: " + a.getElementId());
			}
		}
		if (this instanceof MState) {
			System.err.println("  " + getElementRef());
		}
	}
}
