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

/**
 * Exception that occurs during import, export, save or load of a pathway model.
 * 
 * @author unknown
 */
public class ConverterException extends Exception {

	/**
	 * @param msg the string message. 
	 */
	public ConverterException(String msg) {
		super(msg);
	}

	/**
	 * @param e the exception
	 */
	public ConverterException(Exception e) {
		super(e.getClass() + ": " + e.getMessage(), e);
		setStackTrace(e.getStackTrace());
	}

}
