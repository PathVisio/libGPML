/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.libgpml.io;

import java.io.File;

import org.pathvisio.libgpml.model.PathwayModel;

/**
 * Interface for an exporter that writes a pathway to a file.
 * 
 * @author unknown
 */
public interface PathwayModelExporter extends PathwayModelIO {
	/**
	 * Exports the given pathway to the file
	 * 
	 * @param file         the file to export to
	 * @param pathwayModel the pathway model to export
	 * @throws ConverterException when there is a fatal conversion problem.
	 *                            Implementations should only throw in case there is
	 *                            a non-recoverable error. Otherwise, it should emit
	 *                            a warning.
	 */
	public void doExport(File file, PathwayModel pathwayModel) throws ConverterException;

	/**
	 * Exports the given pathway to the file
	 * 
	 * @param file         the file to export to
	 * @param pathwayModel the pathway model to export
	 * @param zoom         the zoom factor.
	 * @throws ConverterException when there is a fatal conversion problem.
	 *                            Implementations should only throw in case there is
	 *                            a non-recoverable error. Otherwise, it should emit
	 *                            a warning.
	 */
	public void doExport(File file, PathwayModel pathwayModel, int zoom) throws ConverterException;
//	public void doExport(File file, Pathway pathway, int width, int height) throws ConverterException;
}
