package org.pathvisio.util;


import org.bridgedb.DataSource;
import org.bridgedb.Xref;
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

		Xref xref1 = XrefUtils.createXref("123", "doid");
		
		assertEquals(XrefUtils.getXrefDataSourceStr(xref1.getDataSource()), "doid");

		

	}
	
}
