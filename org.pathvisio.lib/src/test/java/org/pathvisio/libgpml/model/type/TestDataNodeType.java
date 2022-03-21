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
package org.pathvisio.libgpml.model.type;

import java.util.Arrays;
import java.util.List;

import org.pathvisio.libgpml.model.type.DataNodeType;
import org.pathvisio.libgpml.model.type.GroupType;

import junit.framework.TestCase;

/**
 * Test for extensible enum class. Tests DataNodeType as an example.
 * 
 * @author finterly
 */
public class TestDataNodeType extends TestCase {

	/**
	 * For testing data node type features.
	 */
	public static void testDataNodeType() {

		// returns "RNA"
		DataNodeType rna1 = DataNodeType.register("RNA");
		// should return "RNA" in place of "Rna"
		DataNodeType rna2 = DataNodeType.register("Rna");
		// should both equal "RNA"
		assertEquals(rna1, rna2);

		// should return "Complex" in place of "comPLEX"
		DataNodeType complex1 = DataNodeType.register("comPLEX");
		// should both equal "Complex"
		assertEquals(complex1, DataNodeType.COMPLEX);

		// should add "new data node type"
		DataNodeType new1 = DataNodeType.register("new data node type");
		// should not add "NEW data NODE type"
		DataNodeType new2 = DataNodeType.register("NEW data NODE type");
		// should both equal "new data node type"
		assertEquals(new1, new2);

		List<String> names = Arrays.asList(DataNodeType.getNames());
		for (String i : names) { 
			System.out.println(i);
		}
	
		List<DataNodeType> values = Arrays.asList(DataNodeType.getValues());
		for (DataNodeType i : values) {
			System.out.println(i);
		}
		
		assertTrue(names.contains("RNA"));
		assertFalse(names.contains("Rna"));
		assertTrue(names.contains("new data node type"));
		assertFalse(names.contains("NEW data NODE type"));

		System.out.println(DataNodeType.getNames());
		System.out.println(rna1 == DataNodeType.RNA);
	}
	
	/**
	 * For testing group type features.
	 */
	public static void testGroupType() {
		GroupType type1 = GroupType.register("group");
		assertEquals(GroupType.GROUP, type1);
		assertTrue(GroupType.GROUP == type1);
	}
	
}