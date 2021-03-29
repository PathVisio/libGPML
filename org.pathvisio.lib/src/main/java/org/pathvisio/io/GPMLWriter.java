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
package org.pathvisio.io;
import java.io.File;

import org.pathvisio.model.PathwayModel;

/**
 * Interface for an exporter that writes a pathway model to a file
 */
public interface GPMLWriter extends PathwayIO {
	

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
	 * Export the given pathway to the file
	 * @param file The file to export to
	 * @param pathway The pathway to export
	 * @throws ConverterException when there is a fatal conversion problem. Implementations should only throw in case there is a non-recoverable error. Ohterwise, it should emit a warning.
	 */
	public void writeGPML(File file, PathwayModel pathwayModel) throws ConverterException;


	
	/**
	 * Export the given pathway to the file
	 * @param file The file to export to
	 * @param pathway The pathway to export
	 * @param zoom 
	 * @throws ConverterException when there is a fatal conversion problem. Implementations should only throw in case there is a non-recoverable error. Ohterwise, it should emit a warning.
	 */
	public void writeGPML(File file, PathwayModel pathwayModel, int zoom) throws ConverterException;
//	public void doExport(File file, Pathway pathway, int width, int height) throws ConverterException;
	
	public void writeMapp(File file) throws ConverterException {
		new MappFormat().doExport(file, this);
	}

	public void writeSvg(File file) throws ConverterException {
		// Use Batik instead of SvgFormat
		// SvgFormat.writeToSvg (this, file);
		new BatikImageExporter(ImageExporter.TYPE_SVG).doExport(file, this);
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
	
	
}
