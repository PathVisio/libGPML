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
	 * Constructor for this class, creates a new gpml document.
	 */
	public Pathway() {
		mappInfo = PathwayElement.createPathwayElement(ObjectType.MAPPINFO);
		this.add(mappInfo);
		infoBox = PathwayElement.createPathwayElement(ObjectType.INFOBOX);
		this.add(infoBox);
	}
	/**
	 * Writes the JDOM document to the file specified.
	 * 
	 * @param file     the file to which the JDOM document should be saved.
	 * @param validate if true, validate the dom structure before writing to file.
	 *                 If there is a validation error, or the xsd is not in the
	 *                 classpath, an exception will be thrown.
	 */
	public void writeToXml(File file, boolean validate) throws ConverterException {
		GpmlFormat.writeToXml(this, file, validate);
		setSourceFile(file);
		clearChangedFlag();

	}

	public void readFromXml(Reader in, boolean validate) throws ConverterException {
		GpmlFormat.readFromXml(this, in, validate);
		setSourceFile(null);
		clearChangedFlag();
	}

	public void readFromXml(InputStream in, boolean validate) throws ConverterException {
		GpmlFormat.readFromXml(this, in, validate);
		setSourceFile(null);
		clearChangedFlag();
	}

	public void readFromXml(File file, boolean validate) throws ConverterException {
		Logger.log.info("Start reading the XML file: " + file);
		GpmlFormat.readFromXml(this, file, validate);
		setSourceFile(file);
		clearChangedFlag();
	}

	public void writeToMapp(File file) throws ConverterException {
		new MappFormat().doExport(file, this);
	}

	public void writeToSvg(File file) throws ConverterException {
		// Use Batik instead of SvgFormat
		// SvgFormat.writeToSvg (this, file);
		new BatikImageExporter(ImageExporter.TYPE_SVG).doExport(file, this);
	}

}
