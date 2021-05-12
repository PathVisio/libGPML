package org.pathvisio.util;


import org.bridgedb.DataSource;
import org.bridgedb.bio.DataSourceTxt;

import junit.framework.TestCase;

/**
 * Tests for XrefUtils class. 
 * 
 * @author finterly
 */
public class TestXrefUtils extends TestCase {
	
	public void testBridgeDb() {

		DataSourceTxt.init();
		
		DataSource testChEBI = DataSource.getExistingByFullName("ChEBI");
		DataSource.register("SysCode", "FullName");
		DataSource testNew = DataSource.getExistingByFullName("FullName");
		DataSource testNull = DataSource.getByCompactIdentifierPrefix("Not Exist");

		System.out.println(testChEBI.getCompactIdentifierPrefix());
		System.out.println(testNew.getCompactIdentifierPrefix());
		System.out.println(testNull); //null


	}
	
}
