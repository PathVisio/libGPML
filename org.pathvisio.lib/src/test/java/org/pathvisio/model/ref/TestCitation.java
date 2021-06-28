package org.pathvisio.model.ref;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.pathvisio.io.ConverterException;
import org.pathvisio.io.GPML2013aWriter;
import org.pathvisio.model.PathwayModel;

import junit.framework.TestCase;

/**
 * For testing Citation methods 
 * 
 * @author finterly
 */
public class TestCitation extends TestCase {

	/**
	 * Tests equals method for citations. 
	 */
	public static void testEqualsCitation()  {
		
		UrlRef url1 = new UrlRef("Hi", null);
		UrlRef url2 = new UrlRef("Hi",null);
		System.out.println(Objects.equals(url1,url2));
		System.out.println(url1.getDescription().equals(url2.getDescription()));

		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		list1.add("Hello");
		list2.add("Hello");
		list1.add("Hellow");
		list2.add("Hellow");
		System.out.println(Objects.equals(list1,list2));

		Set<UrlRef> list3 = new HashSet<UrlRef>();
		Set<UrlRef> list4 = new HashSet<UrlRef>();
		list3.add(url1);
		list4.add(url2);
		list3.add(url2);
		list4.add(url1);
		System.out.println(Objects.equals(list3,list4));

	}
	
	
}
