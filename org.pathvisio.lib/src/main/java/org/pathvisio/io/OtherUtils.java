package org.pathvisio.io;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.pathvisio.model.PathwayElement;

public class OtherUtils {

	/* -------------------- OLD THINGS PATHWAY MODEL---------------------------*/
	
	/*
	 * Call when making a new mapp. TODO WHAT IS THIS?
	 */
	public void initMappInfo() {
		String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
		pathway.setVersion(dateString);
	}

	public String summary() {
		String result = "    " + toString() + "\n    with Objects:";
		for (PathwayElement pe : dataObjects) {
			String code = pe.toString();
			code = code.substring(code.lastIndexOf('@'), code.length() - 1);
			result += "\n      " + code + " " + pe.getObjectType().getTag() + " " + pe.getParent();
		}
		return result;
	}

	/**
	 * Check for any dangling references, and fix them if found This is called just
	 * before writing out a pathway.
	 *
	 * This is a fallback solution for problems elsewhere in the reference handling
	 * code. Theoretically, if the rest of the code is bug free, this should always
	 * return 0.
	 *
	 * @return number of references fixed. Should be 0 under normal circumstances.
	 */
	public int fixReferences() {
		int result = 0;
		Set<String> graphIds = new HashSet<String>();
		for (PathwayElement pe : dataObjects) {
			String id = pe.getElementId();
			if (id != null) {
				graphIds.add(id);
			}
			for (PathwayElement.MAnchor pp : pe.getMAnchors()) {
				String pid = pp.getElementId();
				if (pid != null) {
					graphIds.add(pid);
				}
			}
		}
		for (PathwayElement pe : dataObjects) {
			if (pe.getObjectType() == ObjectType.LINE) {
				String ref = pe.getStartGraphRef();
				if (ref != null && !graphIds.contains(ref)) {
					pe.setStartGraphRef(null);
					result++;
				}

				ref = pe.getEndGraphRef();
				if (ref != null && !graphIds.contains(ref)) {
					pe.setEndGraphRef(null);
					result++;
				}
			}
		}
		if (result > 0) {
			Logger.log.warn("Pathway.fixReferences fixed " + result + " reference(s)");
		}
		for (String ref : biopaxReferenceToDelete) {
			getBiopax().removeElement(getBiopax().getElement(ref));
		}
		return result;
	}

	public void printRefsDebugInfo() {
		for (PathwayElement elt : dataObjects) {
			elt.printRefsDebugInfo();
		}
	}

	/*-------------------------------------------- OLD METHODS READ ----------------------------------------*/

	
	// OLD METHOD Add graphIds for objects that don't have one
//	addElementIds(pwy);

	// OLD METHOD Convert absolute point coordinates of linked points to
	// relative coordinates
//	convertPointCoordinates(pwy);
	
	
	protected void readGroupRef(LineElement o, Element e) {
		String groupRef = e.getAttributeValue("groupRef");
		if (groupRef != null && !groupRef.equals("")) {
			o.setGroupRef((Group) o.getPathwayModel().getPathwayElement(id));
		}
	}

	protected void readGroupRefs(PathwayModel pathwayModel, Element root) {
		List<String> shpElements = Collections
				.unmodifiableList(Arrays.asList("DataNodes", "Labels", "Shapes", "Groups"));
		List<String> shpElement = Collections.unmodifiableList(Arrays.asList("DataNode", "Label", "Shape", "Group"));
		for (int i = 0; i < shpElements.size(); i++) {
			Element grps = root.getChild(shpElements.get(i), root.getNamespace());
			for (Element grp : grps.getChildren(shpElement.get(i), grps.getNamespace())) {
				String groupRef = grp.getAttributeValue("groupRef");
				if (groupRef != null && !groupRef.equals("")) {
					String elementId = grp.getAttributeValue("elementId");
					ShapedElement shapedElement = (ShapedElement) pathwayModel.getPathwayElement(elementId);
					shapedElement.setGroupRef((Group) pathwayModel.getPathwayElement(groupRef));
				}
			}
		}
		List<String> lnElements = Collections.unmodifiableList(Arrays.asList("Interactions", "GraphicalLines"));
		List<String> lnElement = Collections.unmodifiableList(Arrays.asList("Interaction", "GraphicalLine"));
		for (int i = 0; i < shpElements.size(); i++) {
			Element grps = root.getChild(lnElements.get(i), root.getNamespace());
			for (Element grp : grps.getChildren(lnElement.get(i), grps.getNamespace())) {
				String groupRef = grp.getAttributeValue("groupRef");
				if (groupRef != null && !groupRef.equals("")) {
					String elementId = grp.getAttributeValue("elementId");
					LineElement lineElement = (Group) pathwayModel.getPathwayElement(elementId);
					lineElement.setGroupRef((Group) pathwayModel.getPathwayElement(groupRef));

				}
			}
		}
	}

	private static void addElementIds(Pathway pathway) throws ConverterException {
		for (PathwayElement pe : pathway.getDataObjects()) {
			String id = pe.getElementId();
			if (id == null || "".equals(id)) {
				if (pe.getObjectType() == ObjectType.LINE || pe.getObjectType() == ObjectType.GRAPHLINE) {
					// because we forgot to write out graphId's on Lines on older pathways
					// generate a graphId based on hash of coordinates
					// so that pathways with branching history still have the same id.
					// This part may be removed for future versions of GPML (2010+)
					StringBuilder builder = new StringBuilder();
					builder.append(pe.getMStartX());
					builder.append(pe.getMStartY());
					builder.append(pe.getMEndX());
					builder.append(pe.getMEndY());
					builder.append(pe.getStartLineType());
					builder.append(pe.getEndLineType());

					String newId;
					int i = 1;
					do {
						newId = "id" + Integer.toHexString((builder.toString() + ("_" + i)).hashCode());
						i++;
					} while (pathway.getGraphIds().contains(newId));
					pe.setElementId(newId);
				}
			}
		}
	}

	/**
	 * Converts deprecated shapes to contemporary analogs. This allows us to
	 * maintain backward compatibility while at the same time cleaning up old shape
	 * usages.
	 * 
	 */
	/**
	 * @param o
	 * @param e
	 * @throws ConverterException
	 */
	protected void readShapeType(Shape o, Element e) throws ConverterException {
		String base = e.getName();
		Element graphics = e.getChild("Graphics", e.getNamespace());
		IShape s = ShapeRegistry.fromName(graphics.getAttributeValue("shapeType"));
		if (ShapeType.DEPRECATED_MAP.containsKey(s)) {
			s = ShapeType.DEPRECATED_MAP.get(s);
			o.setShapeType(s);
			if (s.equals(ShapeType.ROUNDED_RECTANGLE) || s.equals(ShapeType.OVAL)) {
				o.setLineStyle(LineStyleType.DOUBLE);
				o.setLineThickness(3.0);
				o.setColor(Color.LIGHT_GRAY);
			}
		} else {
			o.setShapeType(s);
			mapLineStyle(o, e); // LineStyle
		}
	}

}

