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
package org.pathvisio.libgpml.util;

import java.util.Objects;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.libgpml.debug.Logger;

/**
 * This utils class contains methods for checking DataSource and creating Xref.
 * 
 * @author finterly
 */
public class XrefUtils {

	/**
	 * Instantiates an {@link Xref} given String identifier and dataSource.
	 * {@link getXrefDataSource} returns a {@link DataSource} for string compact
	 * identifier prefix, fullName, or systemCode.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 * @return the xref for given identifier and data source.
	 */
	public static Xref createXref(String identifier, String dataSource) {
//		if (identifier == null) {
//			identifier = ""; // TODO for now allow identifier to be null or empty
//		}
		// both identifier and data must be valid
		if (identifier != null && !identifier.equals("") && dataSource != null && !dataSource.equals("")) {
			return new Xref(identifier, getXrefDataSource(dataSource));
		} else {
			return null;
		}
	}

	/**
	 * Instantiates an {@link Xref} given String identifier and DataSource
	 * dataSource.
	 * 
	 * NB: Temporary Method? BridgeDB as of (15/04/2022) still allows the
	 * instantiation of and Xref with null DataSource. This is not desirable for
	 * some use cases.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 * @return the xref for given identifier and data source.
	 */
	public static Xref createXref(String identifier, DataSource dataSource) {
//		if (identifier == null) {
//			identifier = ""; // TODO for now allow identifier to be null or empty
//		}
		// both identifier and data must be valid
		if (identifier != null && !identifier.equals("") && dataSource != null && !dataSource.equals("")) {
			return new Xref(identifier, dataSource);
		} else {
			return null;
		}
	}

	/**
	 * Returns {@link DataSource} object from given string. String may be either
	 * compact identifier prefix, full name, or system code for a data source. If no
	 * data source exists for the given string, registers the string as a new data
	 * source.
	 * 
	 * @param dataSourceStr the string for data source.
	 * @return dataSource the data source for the given string.
	 */
	public static DataSource getXrefDataSource(String dataSourceStr) {
		if (dataSourceStr.equals("")) {
			return null; // null datasource if if empty string
		}
		// if compact identifier prefix
		DataSource dataSource = DataSource.getByCompactIdentifierPrefix(dataSourceStr);
		if (dataSource == null) {
			// if existing full name
			if (DataSource.fullNameExists(dataSourceStr)) {
				dataSource = DataSource.getExistingByFullName(dataSourceStr);
				// if existing system code
			} else if (DataSource.systemCodeExists(dataSourceStr)) {
				dataSource = DataSource.getByAlias(dataSourceStr);
				// else register new data source
			} else {
				DataSource.register(dataSourceStr, dataSourceStr).compactIdentifierPrefix(dataSourceStr).asDataSource();
				Logger.log.trace("Registered xref datasource " + dataSourceStr);
				dataSource = DataSource.getByCompactIdentifierPrefix(dataSourceStr);
			}
		}
		return dataSource;
	}

	/**
	 * Returns string for data source given {@link DataSource}. Priority is compact
	 * identifier prefix string. If compact identifier prefix null, returns full
	 * name. If full name null, returns system code. If no data source exists,
	 * return null.
	 * 
	 * @param dataSource the data source.
	 * @return dataSourceStr the string for given data source, or null if no valid
	 *         data source string.
	 */
	public static String getXrefDataSourceStr(DataSource dataSource) {
		if (dataSource != null) {
			String dataSourceStr = dataSource.getCompactIdentifierPrefix(); // compact identifier prefix
			if (dataSourceStr == null) {
				dataSourceStr = dataSource.getFullName(); // existing full name
				if (dataSourceStr == null)
					dataSourceStr = dataSource.getSystemCode();// existing system code
			}
			return dataSourceStr;
		}
		return null; // return null if no valid data source
	}

	/**
	 * Returns string for data source given {@link DataSource} for GPML2013a format.
	 * Priority is data source full name. If full name null, returns system code. If
	 * system code null, returns compact prefix identifier. If no data source
	 * exists, return null.
	 * 
	 * @param dataSource the data source.
	 * @return dataSourceStr the string for given data source, or null if no valid
	 *         data source string.
	 */
	public static String getXrefDataSourceStrGPML2013a(DataSource dataSource) {
		// data source cannot be null
		if (dataSource != null) {
			String dataSourceStr = dataSource.getFullName();
//			if (dataSourceStr == null) {
//				// retrieve data source system code if full name is null
//				dataSourceStr = dataSource.getSystemCode();
//				// retrieve compact identifier prefix if system code is null
//				if (dataSourceStr == null)
//					dataSourceStr = dataSource.getCompactIdentifierPrefix();
//			}
			return dataSourceStr;
		}
		// returns null if no valid data source
		return null;
	}

	/**
	 * This method checks whether two {@link Xref}(s) are equal in value. Returns
	 * true if the given Xrefs are equal, false otherwise.
	 * 
	 * @return true if given xrefs equal, false otherwise.
	 */
	public static boolean equivalentXrefs(Xref xref1, Xref xref2) {
		if (xref1 == null && xref2 == null) {
			return true;
		} else if ((xref1 != null && xref2 == null) || (xref1 == null && xref2 != null)) {
			return false;
		} else if (xref1 != null && xref2 != null) {
			if (!Objects.equals(xref1.getId(), xref2.getId())) {
				return false;
			}
			if (!Objects.equals(xref1.getDataSource(), xref2.getDataSource())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the identifier for given {@link Xref} if valid, otherwise returns
	 * empty String.
	 * 
	 * @param xref the xref.
	 * @return identifier the id for given xref.
	 */
	public static String getIdentifier(Xref xref) {
		String identifier = "";
		if (xref != null) {
			identifier = xref.getId();
		}
		return identifier;
	}

	/**
	 * Returns the identifier for given {@link Xref} if valid, otherwise returns
	 * null.
	 * 
	 * @param xref the xref.
	 * @return dataSource the dataSource for given xref.
	 */
	public static DataSource getDataSource(Xref xref) {
		DataSource dataSource = null;
		if (xref != null) {
			dataSource = xref.getDataSource();
		}
		return dataSource;
	}

}
