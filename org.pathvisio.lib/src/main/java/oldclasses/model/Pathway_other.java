package oldclasses.model;

import org.pathvisio.model.BatikImageExporter;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.File;
import org.pathvisio.model.InputStream;
import org.pathvisio.model.MappFormat;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.Reader;

public class Pathway_other {


	/**
	 * Constructor for this class, creates a new gpml document.
	 */
	public Pathway() {
		mappInfo = PathwayElement.createPathwayElement(ObjectType.MAPPINFO);
		this.add(mappInfo);
		infoBox = PathwayElement.createPathwayElement(ObjectType.INFOBOX);
		this.add(infoBox);
	}


	/*
	 * Call when making a new mapp.
	 */
	public void initMappInfo()
	{
		String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
		mappInfo.setVersion(dateString);
		mappInfo.setMapInfoName("New Pathway");
	}

	/**
	 * Writes the JDOM document to the file specified
	 * @param file	the file to which the JDOM document should be saved
	 * @param validate if true, validate the dom structure before writing to file. If there is a validation error,
	 * 		or the xsd is not in the classpath, an exception will be thrown.
	 */
	public void writeToXml(File file, boolean validate) throws ConverterException
	{
		GpmlFormat.writeToXml (this, file, validate);
		setSourceFile (file);
		clearChangedFlag();

	}

	public void readFromXml(Reader in, boolean validate) throws ConverterException
	{
		GpmlFormat.readFromXml (this, in, validate);
		setSourceFile (null);
		clearChangedFlag();
	}

	public void readFromXml(InputStream in, boolean validate) throws ConverterException
	{
		GpmlFormat.readFromXml (this, in, validate);
		setSourceFile (null);
		clearChangedFlag();
	}

	public void readFromXml(File file, boolean validate) throws ConverterException
	{
		Logger.log.info("Start reading the XML file: " + file);
		GpmlFormat.readFromXml (this, file, validate);
		setSourceFile (file);
		clearChangedFlag();
	}

	public void writeToMapp (File file) throws ConverterException
	{
		new MappFormat().doExport(file, this);
	}

	public void writeToSvg (File file) throws ConverterException
	{
		//Use Batik instead of SvgFormat
		//SvgFormat.writeToSvg (this, file);
		new BatikImageExporter(ImageExporter.TYPE_SVG).doExport(file, this);
	}

	/**
	 * Implement this interface if you want to be notified when the "changed" status changes.
	 * This happens e.g. when the user makes a change to an unchanged pathway,
	 * or when a changed pathway is saved.
	 */
	public interface StatusFlagListener extends EventListener
	{
		public void statusFlagChanged (StatusFlagEvent e);
	}

	/**
	 * Event for a change in the "changed" status of this Pathway
	 */
	public static class StatusFlagEvent
	{
		private boolean newStatus;
		public StatusFlagEvent (boolean newStatus) { this.newStatus = newStatus; }
		public boolean getNewStatus() {
			return newStatus;
		}
	}

	private List<StatusFlagListener> statusFlagListeners = new ArrayList<StatusFlagListener>();

	/**
	 * Register a status flag listener
	 */
	public void addStatusFlagListener (StatusFlagListener v)
	{
		if (!statusFlagListeners.contains(v)) statusFlagListeners.add(v);
	}

	/**
	 * Remove a status flag listener
	 */
	public void removeStatusFlagListener (StatusFlagListener v)
	{
		statusFlagListeners.remove(v);
	}

	//TODO: make private
	public void fireStatusFlagEvent(StatusFlagEvent e)
	{
		for (StatusFlagListener g : statusFlagListeners)
		{
			g.statusFlagChanged (e);
		}
	}

	private List<PathwayListener> listeners = new ArrayList<PathwayListener>();

	public void addListener(PathwayListener v)
	{
		if(!listeners.contains(v)) listeners.add(v);
	}

	public void removeListener(PathwayListener v) { listeners.remove(v); }

    /**
	   Firing the ObjectModifiedEvent has the side effect of
	   marking the Pathway as changed.
	 */
	public void fireObjectModifiedEvent(PathwayEvent e)
	{
		markChanged();
		for (PathwayListener g : listeners)
		{
			g.pathwayModified(e);
		}
	}

	public Pathway clone()
	{
		Pathway result = new Pathway();
		for (PathwayElement pe : dataObjects)
		{
			result.add (pe.copy());
		}
		result.changed = changed;
		if(sourceFile != null) {
			result.sourceFile = new File(sourceFile.getAbsolutePath());
		}
		// do not copy status flag listeners
//		for(StatusFlagListener l : statusFlagListeners) {
//			result.addStatusFlagListener(l);
//		}
		return result;
	}

	public String summary()
	{
		String result = "    " + toString() + "\n    with Objects:";
		for (PathwayElement pe : dataObjects)
		{
			String code = pe.toString();
			code = code.substring (code.lastIndexOf ('@'), code.length() - 1);
			result += "\n      " + code + " " +
				pe.getObjectType().getTag() + " " + pe.getParent();
		}
		return result;
	}

	/**
	 * Check for any dangling references, and fix them if found
	 * This is called just before writing out a pathway.
	 *
	 * This is a fallback solution for problems elsewhere in the
	 * reference handling code. Theoretically, if the rest of
	 * the code is bug free, this should always return 0.
	 *
	 * @return number of references fixed. Should be 0 under normal
	 * circumstances.
	 */
	public int fixReferences()
	{
		int result = 0;
		Set <String> graphIds = new HashSet <String>();
		for (PathwayElement pe : dataObjects)
		{
			String id = pe.getGraphId();
			if (id != null)
			{
				graphIds.add (id);
			}
			for (PathwayElement.MAnchor pp : pe.getMAnchors())
			{
				String pid = pp.getGraphId();
				if (pid != null)
				{
					graphIds.add (pid);
				}
			}
		}
		for (PathwayElement pe : dataObjects)
		{
			if (pe.getObjectType() == ObjectType.LINE)
			{
				String ref = pe.getStartGraphRef();
				if (ref != null && !graphIds.contains(ref))
				{
					pe.setStartGraphRef(null);
					result++;
				}

				ref = pe.getEndGraphRef();
				if (ref != null && !graphIds.contains(ref))
				{
					pe.setEndGraphRef(null);
					result++;
				}
			}
		}
		if (result > 0)
		{
			Logger.log.warn("Pathway.fixReferences fixed " + result + " reference(s)");
		}
		for (String ref : biopaxReferenceToDelete ){
			getBiopax().removeElement(getBiopax().getElement(ref));
		}
		return result;
	}

	/**
	 * Transfer statusflag listeners from one pathway to another.
	 * This is used needed when copies of the pathway are created / returned
	 * by UndoManager. The status flag listeners are only interested in status flag
	 * events of the active copy.
	 */
	public void transferStatusFlagListeners(Pathway dest)
	{
		for (Iterator<StatusFlagListener> i = statusFlagListeners.iterator(); i.hasNext(); )
		{
			StatusFlagListener l = i.next();
			dest.addStatusFlagListener(l);
			i.remove();
		}
	}

	public void printRefsDebugInfo()
	{
		for (PathwayElement elt : dataObjects)
		{
			elt.printRefsDebugInfo();
		}
	}

	List<OntologyTag> ontologyTags = new ArrayList<OntologyTag>();
	public void addOntologyTag(String id, String term, String ontology){
		ontologyTags.add(new OntologyTag(id,term,ontology));
	}

	public List<OntologyTag> getOntologyTags(){
		return ontologyTags;
	}
	/**
	 * List of Biopax references to be deleted.
	 * The deletion is done before to save the pathway.
	 */
	private List<String> biopaxReferenceToDelete = new ArrayList<String>();
}



