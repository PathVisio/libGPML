package org.pathvisio.util;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.debug.Logger;

/**
 * This utils class contains methods for checking DataSource and creating Xref.
 * 
 * @author finterly
 */
public class XrefUtils {

	/**
	 * Instantiates an {@link Xref} given string identifier and dataSource.
	 * {@link getXrefDataSource} returns a {@link DataSource} for string compact
	 * identifier prefix, fullName, or systemCode.
	 * 
	 * @param identifier the identifier of the database entry.
	 * @param dataSource the source of database entry.
	 * @return the xref for given identifier and data source.
	 */
	public static Xref createXref(String identifier, String dataSource) {
		// TODO allow identifier to be null or empty
		if (identifier == null)
			identifier = "";
		// data source cannot be null nor empty
		if (dataSource != null && !dataSource.equals("")) {
			return new Xref(identifier, getXrefDataSource(dataSource));
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
		// retrieve data source using string as compact identifier prefix
		DataSource dataSource = DataSource.getByCompactIdentifierPrefix(dataSourceStr);
		// if compact identifier prefix does not exist, data source is null
		if (dataSource == null) {
			// if string is existing full name, retrieve data source
			if (DataSource.fullNameExists(dataSourceStr)) {
				return DataSource.getExistingByFullName(dataSourceStr);
				// if string is existing system code, retrieve data source
			} else if (DataSource.systemCodeExists(dataSourceStr)) {
				return DataSource.getByAlias(dataSourceStr);
				// if all else fails, register string as new data source
			} else {
				DataSource.register(dataSourceStr, dataSourceStr).compactIdentifierPrefix(dataSourceStr).asDataSource();
				Logger.log.trace("Registered xref datasource " + dataSourceStr);
				return DataSource.getByCompactIdentifierPrefix(dataSourceStr);
			}
		}
		return dataSource;
	}

	/**
	 * Returns string for data source given {@link DataSource}. Priority is compact
	 * identifier prefix string. If no compact identifier prefix exists, returns
	 * data source full name. If no valid data source string, returns null.
	 * 
	 * @param dataSource the data source.
	 * @return dataSourceStr the string for given data source, or null if no valid
	 *         data source string.
	 */
	public static String getXrefDataSourceStr(DataSource dataSource) {
		// data source cannot be null
		if (dataSource != null) {
			String dataSourceStr = dataSource.getCompactIdentifierPrefix();
			if (dataSourceStr == null) {
				// retrieve data source full name if compact identifier prefix is null
				dataSourceStr = dataSource.getFullName();
				// TODO try system code if still null?
			}
			return dataSourceStr;
		}
		// returns null if no valid data source
		return dataSource.getSystemCode();
	}

}
