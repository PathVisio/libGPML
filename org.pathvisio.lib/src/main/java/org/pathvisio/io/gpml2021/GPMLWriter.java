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
package org.pathvisio.io.gpml2021;

import java.io.File;
import java.io.OutputStream;

import org.jdom2.Document;
import org.pathvisio.io.ConverterException;
import org.pathvisio.model.PathwayModel;

/**
 * Interface for an exporter that writes a pathway model to a file
 */
public interface GPMLWriter {

	/**
	 * Export the given pathway to the file
	 * 
	 * @param file         the file to export to
	 * @param pathwayModel the pathwayModel to export
	 * @throws ConverterException when there is a fatal conversion problem.
	 *                            Implementations should only throw in case there is
	 *                            a non-recoverable error. Otherwise, it should emit
	 *                            a warning.
	 */
	void writeGPML(PathwayModel pathwayModel, File file) throws ConverterException;

	void writeGPML(PathwayModel pathwayModel, OutputStream out) throws ConverterException;

	Document writePathwayModel(PathwayModel pathwayModel) throws ConverterException;

}
