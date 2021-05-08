/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2021 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.model.type;


import java.util.List;

import junit.framework.TestCase;

public class TestDataNodeType extends TestCase {

	/**
	 * For testing data node type features.
	 */
	public static void testDataNodeType() {
		
		DataNodeType.register("Rna");
		// should not add "RNA" as it is same as "Rna" aside from case 
		DataNodeType.register("RNA");
		// should not add "comPLEX" as it is same as "Complex" aside from case 
		DataNodeType.register("comPLEX");
		// should add "new data node type" 
		DataNodeType.register("new data node type");
		// should not add "NEW data NODE type" 
		DataNodeType.register("NEW data NODE type");
		
		List<String> names = DataNodeType.getNames();
		for (String i: names) {
			System.out.println(i);
		}
		
		List<DataNodeType> values = DataNodeType.getValues();

		for (DataNodeType i: values) {
			System.out.println(i);
		}
		System.out.println(names.contains("Food"));

		System.out.println(DataNodeType.getNames());
		
	}
}