package org.pathvisio.libgpml.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.junit.jupiter.api.Test;

/**
 * Tests for XrefUtils class.
 * 
 * @author finterly
 */
class TestXrefUtils {

	@Test
	void bridgeDb() {
		if (!DataSource.fullNameExists("Affy"))
			DataSourceTxt.init();

		DataSource ds = DataSource.getExistingBySystemCode("Eco");

		System.out.println(ds.getMiriamURN("34"));
		System.out.println(ds.getFullName());
		DataSource ds1 = DataSource.getExistingBySystemCode("En");

		System.out.println(ds1.getMiriamURN("34"));
		System.out.println(ds1.getFullName());

		Xref xref1 = XrefUtils.createXref("123", "doid");

		assertEquals("doid", XrefUtils.getXrefDataSourceStr(xref1.getDataSource()));

	}

	/**
	 * Tests the method for checking if Xrefs are equal
	 */
	@Test
	void equivalentXrefs() {
		if (!DataSource.fullNameExists("Ensembl"))
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
