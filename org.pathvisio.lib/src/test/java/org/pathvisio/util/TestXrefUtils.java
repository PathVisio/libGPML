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

		DataSource ds = DataSource.getExistingBySystemCode("Eco");
		
		System.out.println(ds.getMiriamURN("34"));
		System.out.println(ds.getFullName());
		DataSource ds1 = DataSource.getExistingBySystemCode("En");
		
		System.out.println(ds1.getMiriamURN("34"));
		System.out.println(ds1.getFullName());
		
		Xref xref1 = XrefUtils.createXref("123", "doid");
		
		assertEquals(XrefUtils.getXrefDataSourceStr(xref1.getDataSource()), "doid");

		

	}
	
}
