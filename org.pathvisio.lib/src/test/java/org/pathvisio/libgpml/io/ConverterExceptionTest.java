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

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test for reading and writing of a single GPML2021 file, for
 * troubleshooting and resolving specific issues.
 */
public class ConverterExceptionTest extends TestCase {

	@Test
	public void testConstructor() {
		Exception exception = new ConverterException("Something bad happened");
		Assert.assertNotNull(exception);
		Assert.assertSame("Something bad happened", exception.getMessage());
	}

	@Test
	public void testConstructor_Exception() {
		Exception exception = new ConverterException(new Exception("Something bad happened"));
		Assert.assertNotNull(exception);
		Assert.assertNotNull(exception.getMessage());
		Assert.assertTrue(exception.getMessage().contains("Something bad happened"));
	}

}
