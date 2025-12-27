/* Copyright 2025 Egon Willighagen <egonw@users.sf.net>
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
 */
package org.pathvisio.libgpml.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for reading and writing of a single GPML2021 file, for
 * troubleshooting and resolving specific issues.
 */
class ConverterExceptionTest {

	@Test
	void constructor() {
		Exception exception = new ConverterException("Something bad happened");
		Assertions.assertNotNull(exception);
		Assertions.assertSame("Something bad happened", exception.getMessage());
	}

	@Test
	void constructorException() {
		Exception exception = new ConverterException(new Exception("Something bad happened"));
		Assertions.assertNotNull(exception);
		Assertions.assertNotNull(exception.getMessage());
		Assertions.assertTrue(exception.getMessage().contains("Something bad happened"));
	}

}
