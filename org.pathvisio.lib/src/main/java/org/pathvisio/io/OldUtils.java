package org.pathvisio.io;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;

public class OldUtils {
	
	
	
	// Pathway.java 
	
	private File sourceFile = null;

	/**
	 * Gets the xml file containing the Gpml pathway currently displayed.
	 * 
	 * @return sourceFile the current xml file.
	 */
	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File file) {
		sourceFile = file;
	}
	
	/**
	 * Writes the JDOM document to the file specified.
	 * 
	 * @param file     the file to which the JDOM document should be saved.
	 */
	public void writeToXml(File file) throws ConverterException {
		GpmlFormat.writeToXml(this, file);
		setSourceFile(file);
		clearChangedFlag();

	}
	
	/**
	 * GpmlFormat.java
	 * TODO ??? 
	 * Export the given pathway to the file
	 * @param file The file to export to
	 * @param pathway The pathway to export
	 * @param zoom 
	 * @throws ConverterException when there is a fatal conversion problem. Implementations should only throw in case there is a non-recoverable error. Ohterwise, it should emit a warning.
	 */
//	public void writeGPML(File file, PathwayModel pathwayModel, int zoom) throws ConverterException;
	
	
	
	public void writeMapp(File file) throws ConverterException {
		new MappFormat().doExport(file, this);
	}

	public void writeSvg(File file) throws ConverterException {
		// Use Batik instead of SvgFormat
		// SvgFormat.writeToSvg (this, file);
		new BatikImageExporter(ImageExporter.TYPE_SVG).doExport(file, this);
	}
	
	
	
	//From Pathway.java 
	public void readXML(Reader in, boolean validate) throws ConverterException {
		GpmlFormat.readFromXml(this, in, validate);
		setSourceFile(null);
		clearChangedFlag();
	}

	public void readXML(InputStream in, boolean validate) throws ConverterException {
		GpmlFormat.readFromXml(this, in, validate);
		setSourceFile(null);
		clearChangedFlag();
	}

	public void readXMLl(File file, boolean validate) throws ConverterException {
		Logger.log.info("Start reading the XML file: " + file);
		GpmlFormat.readFromXml(this, file, validate);
		setSourceFile(file);
		clearChangedFlag();
	}
	
	/* -------------------- OLD THINGS GPML2021FormatAbsract ---------------------------*/

	
	// TODO HANDLE
		protected void writeBiopax(PathwayElement o, Element e) throws ConverterException {
			Document bp = ((BiopaxElement) o).getBiopax();
			if (e != null && bp != null) {
				List<Content> content = bp.getRootElement().cloneContent();
				for (Content c : content) {
					if (c instanceof Element) {
						Element elm = (Element) c;
						if (elm.getNamespace().equals(GpmlFormat.BIOPAX)) {
							e.addContent(c);
						} else if (elm.getName().equals("RDF") && elm.getNamespace().equals(GpmlFormat.RDF)) {
							for (Object ce : elm.getChildren()) {
								if (((Element) ce).getNamespace().equals(GpmlFormat.BIOPAX)) {
									e.addContent((Element) ce);
								}
							}
						} else {
							Logger.log.info("Skipped non-biopax element" + c);
						}
					}
				}
			}
		}

		protected void readBiopaxRef(PathwayElement o, Element e) throws ConverterException {
			for (Object f : e.getChildren("BiopaxRef", e.getNamespace())) {
				o.addBiopaxRef(((Element) f).getText());
			}
		}

		protected void writeBiopaxRef(PathwayElement o, Element e) throws ConverterException {
			if (e != null) {
				for (String ref : o.getBiopaxRefs()) {
					Element f = new Element("BiopaxRef", e.getNamespace());
					f.setText(ref);
					e.addContent(f);
				}
			}
		}

		public void readFromRoot(Element root, Pathway pwy) throws ConverterException {
			mapElement(root, pwy); // MappInfo

			// Iterate over direct children of the root element
			for (Object e : root.getChildren()) {
				mapElement((Element) e, pwy);
			}
			Logger.log.trace("End copying read elements");

			// Add graphIds for objects that don't have one
			addGraphIds(pwy);

			// Convert absolute point coordinates of linked points to
			// relative coordinates
			convertPointCoordinates(pwy);
		}

		private static void addGraphIds(Pathway pathway) throws ConverterException {
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

		private static void convertPointCoordinates(Pathway pathway) throws ConverterException {
			for (PathwayElement pe : pathway.getDataObjects()) {
				if (pe.getObjectType() == ObjectType.LINE || pe.getObjectType() == ObjectType.GRAPHLINE) {
					String sr = pe.getStartGraphRef();
					String er = pe.getEndGraphRef();
					if (sr != null && !"".equals(sr) && !pe.getMStart().relativeSet()) {
						ElementIdContainer idc = pathway.getGraphIdContainer(sr);
						Point2D relative = idc.toRelativeCoordinate(
								new Point2D.Double(pe.getMStart().getRawX(), pe.getMStart().getRawY()));
						pe.getMStart().setRelativePosition(relative.getX(), relative.getY());
					}
					if (er != null && !"".equals(er) && !pe.getMEnd().relativeSet()) {
						ElementIdContainer idc = pathway.getGraphIdContainer(er);
						Point2D relative = idc
								.toRelativeCoordinate(new Point2D.Double(pe.getMEnd().getRawX(), pe.getMEnd().getRawY()));
						pe.getMEnd().setRelativePosition(relative.getX(), relative.getY());
					}
					((MLine) pe).getConnectorShape().recalculateShape(((MLine) pe));
				}
			}
		}

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

