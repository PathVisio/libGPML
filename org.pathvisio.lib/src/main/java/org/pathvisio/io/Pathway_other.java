package org.pathvisio.io;

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




}
