package org.pathvisio.libgpml.util;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.pathvisio.libgpml.util.XrefUtils;

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

	/**
	 * Tests the method for checking if Xrefs are equal
	 */
	public void testEquivalentXrefs() {
		DataSourceTxt.init();
		Xref xref00 = null;
		Xref xref0 = null;
		Xref xref11 = XrefUtils.createXref("11", "ensembl");
		Xref xref1 = XrefUtils.createXref("11", "ensembl");
		Xref xref2 = XrefUtils.createXref("11", "hbgn");
		Xref xref3 = XrefUtils.createXref("12", "ensembl");
		Xref xref4 = XrefUtils.createXref("12", "Ensembl");

		assertTrue(XrefUtils.equivalentXrefs(xref00, xref0));
		assertTrue(XrefUtils.equivalentXrefs(xref11, xref1));
		assertFalse(XrefUtils.equivalentXrefs(xref0, xref1));
		assertFalse(XrefUtils.equivalentXrefs(xref2, xref0));
		assertFalse(XrefUtils.equivalentXrefs(xref1, xref2));
		assertFalse(XrefUtils.equivalentXrefs(xref1, xref3));
		assertTrue(XrefUtils.equivalentXrefs(xref3, xref4));

	}

}
