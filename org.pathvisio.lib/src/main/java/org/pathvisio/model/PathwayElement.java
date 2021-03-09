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
 * @author unknown, AP20070508, finterly
 */
public class PathwayElement implements ElementIdContainer, Comparable<PathwayElement> {

	/**
	 * Parent pathway of this object: may be null (for example, when object is in clipboard)
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
				setDataSource(DataSource.getExistingByFullName((String) value)); // getByFullName
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






	/* ------------------------------- ID & GROUP ------------------------------- */


	/**
	 * 
	 */
	protected String elementId;

	/**
	 * 
	 */
	protected String groupRef;



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


	/* AP20070508 */
	/**
	 * TODO: Is replaced with elementId in 2021.
	 */
	protected String groupId;
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
