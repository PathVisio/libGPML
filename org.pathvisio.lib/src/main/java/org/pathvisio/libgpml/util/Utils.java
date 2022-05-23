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
package org.pathvisio.libgpml.util;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pathvisio.libgpml.model.LineElement;
import org.pathvisio.libgpml.model.PathwayObject;
import org.pathvisio.libgpml.model.ShapedElement;
import org.pathvisio.libgpml.model.type.ObjectType;
import org.pathvisio.libgpml.prop.StaticProperty;

/**
 * Various utility functions.
 * 
 * @author unknown
 */
public class Utils {

	// ================================================================================
	// String Manipulation Methods
	// ================================================================================
	/**
	 * Case insensitive contains method for List.
	 * 
	 * @param str     the string.
	 * @param strList the string list.
	 * @return true if string list contains given string regardless of case.
	 */
	public static boolean containsCaseInsensitive(String str, List<String> strList) {
		return strList.stream().anyMatch(x -> x.equalsIgnoreCase(str));
	}

	/**
	 * Compares two Strings, returning <code>true</code> if they are equal.
	 *
	 * @see java.lang.String#equals(Object)
	 * @param str1 the first String, may be null
	 * @param str2 the second String, may be null
	 * @return <code>true</code> if the Strings are equal, case sensitive, or both
	 *         <code>null</code>
	 */
	public static boolean stringEquals(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equals(str2);
	}

	/**
	 * Compares two Strings, returning <code>true</code> if they are equal. In this
	 * case, null is considered equal to Empty.
	 *
	 * @see java.lang.String#equals(Object)
	 * @param str1 the first String, may be null or empty
	 * @param str2 the second String, may be null or empty
	 * @return <code>true</code> if the Strings are equal, case sensitive, or both
	 *         <code>null</code> or empty
	 */
	public static boolean stringNullEqualsEmpty(String str1, String str2) {
		if (str1 == null && stringEquals(str2, "")) {
			return true; // str1 is null and str2 is empty
		} else if (str2 == null && stringEquals(str1, "")) {
			return true;// str2 is null and str1 is empty
		} else {
			return stringEquals(str1, str2); // check
		}
	}

	/**
	 * Returns true if string is empty or null.
	 * 
	 * @param str the string.
	 * @return true if string is empty or null.
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	// ================================================================================
	// Direction Methods (LineElement)
	// ================================================================================
	/**
	 * Get the direction of the line on the x axis
	 * 
	 * @param start The start point of the line
	 * @param end   The end point of the line
	 * @return 1 if the direction is positive (from left to right), -1 if the
	 *         direction is negative (from right to left)
	 */
	public static int getDirectionX(Point2D start, Point2D end) {
		return (int) Math.signum(end.getX() - start.getX());
	}

	/**
	 * Get the direction of the line on the y axis
	 * 
	 * @param start The start point of the line
	 * @param end   The end point of the line
	 * @return 1 if the direction is positive (from top to bottom), -1 if the
	 *         direction is negative (from bottom to top)
	 */
	public static int getDirectionY(Point2D start, Point2D end) {
		return (int) Math.signum(end.getY() - start.getY());
	}

	// ================================================================================
	// Core Methods
	// ================================================================================
	public static final int OS_UNSUPPORTED = -1;
	public static final int OS_WINDOWS = 0;
	public static final int OS_LINUX = 1;
	public static final int OS_MAC = 2;

	/**
	 * Returns the OS.
	 * 
	 * @return the OS.
	 */
	public static int getOS() {
		String os = System.getProperty("os.name");
		if (os.startsWith("Win")) {
			return OS_WINDOWS;
		} else if (os.startsWith("Lin")) {
			return OS_LINUX;
		} else if (os.startsWith("Mac")) {
			return OS_MAC;
		} else {
			return OS_UNSUPPORTED;
		}
	}

	/**
	 * Useful if you want to use one item from a set, and you don't care which one.
	 * 
	 * @param set a set that you want one element out of
	 * @return null if the set is empty or null, or an element from the set
	 *         otherwise.
	 */
	static public <T> T oneOf(Set<T> set) {
		if (set == null || set.size() == 0) {
			return null;
		} else {
			return set.iterator().next();
		}
	}

	/**
	 * Creates a new Set of the given value(s)
	 */
	static public <T> Set<T> setOf(T val) {
		Set<T> result = new HashSet<T>();
		result.add(val);
		return result;
	}

	/**
	 * For safely adding a value to a multimap (i.e. a map of sets).
	 */
	static public <U, V> void multimapPut(Map<U, Set<V>> map, U key, V val) {
		Set<V> x;
		if (map.containsKey(key))
			x = map.get(key);
		else {
			x = new HashSet<V>();
			map.put(key, x);
		}
		x.add(val);
	}

	/**
	 * Returns a set of all unique values of a multimap (i.e. a map of sets).
	 */
	public static <U, V> Set<V> multimapValues(Map<U, Set<V>> map) {
		Set<V> result = new HashSet<V>();
		for (Set<V> set : map.values())
			result.addAll(set);
		return result;
	}

	/**
	 * Formats a message that will be displayed in a dialog, so it's not longer than
	 * 80 symbols in each line
	 * 
	 * @param msg the message to format.
	 */
	public static String formatExceptionMsg(String msg) {
		msg = msg.replace("\n", "");
		if (msg.length() > 80) {
			String result = "";
			for (int i = 0; i < msg.length(); i = i + 80) {
				int end = i + 80;
				if (msg.length() < i + 80)
					end = msg.length();
				String s = msg.substring(i, end);
				int index = s.lastIndexOf(" ");
				if (index == -1) {
					result = result + s + "\n";
				} else {
					result = result + msg.substring(i, i + index) + "\n";
					i = i - (s.length() - index);
				}
			}
			return result;
		} else
			return msg;
	}

	// ================================================================================
	// Currently Unused Methods TODO 
	// ================================================================================

	/** append a property to the summary */
	private static void summaryHelper(PathwayObject elt, StringBuilder result, StaticProperty p, String shortHand) {
		if (!elt.getStaticPropertyKeys().contains(p))
			return;
		result.append(',');
		result.append(shortHand);
		result.append('=');
		result.append(elt.getStaticProperty(p));
	}

	/**
	 * Returns string summary information for a pathway object. TODO
	 * 
	 * @param elt
	 * @return
	 */
	public static String summary(PathwayObject elt) {
		if (elt == null) {
			return "null";
		}
		StringBuilder result = new StringBuilder("[" + elt.getObjectType().getTag());
		summaryHelper(elt, result, StaticProperty.ELEMENTID, "id");
		if (elt instanceof ShapedElement) {
			summaryHelper(elt, result, StaticProperty.TEXTLABEL, "lbl");
			summaryHelper(elt, result, StaticProperty.WIDTH, "w");
			summaryHelper(elt, result, StaticProperty.HEIGHT, "h");
			summaryHelper(elt, result, StaticProperty.CENTERX, "cx");
			summaryHelper(elt, result, StaticProperty.CENTERY, "cy");
		}
		if (elt instanceof LineElement) {
			summaryHelper(elt, result, StaticProperty.STARTX, "x1");
			summaryHelper(elt, result, StaticProperty.STARTY, "y1");
			summaryHelper(elt, result, StaticProperty.ENDX, "x2");
			summaryHelper(elt, result, StaticProperty.ENDY, "y2");
			summaryHelper(elt, result, StaticProperty.STARTELEMENTREF, "startref");
			summaryHelper(elt, result, StaticProperty.ENDELEMENTREF, "endref");
		}
		if (elt.getObjectType() == ObjectType.PATHWAY) {
			summaryHelper(elt, result, StaticProperty.TITLE, "title");
			summaryHelper(elt, result, StaticProperty.AUTHOR, "author");
		}
		result.append("]");
		return result.toString();
	}

	/**
	 * Joins collection into a single string, with a separator between.
	 * 
	 * @param sep
	 * @param values
	 * @return
	 */
	public static String join(String sep, Collection<?> values) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Object o : values) {
			if (first)
				first = false;
			else
				builder.append(sep);
			builder.append("" + o);
		}
		return builder.toString();
	}

	/**
	 * Converts a list to a string.
	 * 
	 * @param list  the list to convert to a string
	 * @param quote a quote character to use
	 * @param sep   the separator to use
	 * @return a String representing the list with given seperator and quote (no
	 *         parentheses)
	 */
	public static String collection2String(Collection<?> list, String quote, String sep) {
		StringBuilder strb = new StringBuilder();
		for (Object o : list) {
			strb.append(quote + o.toString() + quote + sep);
		}
		int last = strb.lastIndexOf(String.valueOf(sep));
		if (last >= 0)
			strb.delete(last, strb.length());

		return strb.toString();
	}

	/**
	 * Converts a string to input stream.
	 * 
	 * @param str the string.
	 * @return
	 */
	public static InputStream stringToInputStream(String str) {
		if (str == null)
			return null;
		InputStream in = null;
		try {
			in = new java.io.ByteArrayInputStream(str.getBytes("UTF-8"));
		} catch (Exception ex) {
		}
		return in;
	}

	/**
	 * Encodes a font to a string that can be converted back into a font object
	 * using Font.decode(String)
	 * 
	 * @param f the font.
	 */
	public static String encodeFont(Font f) {
		String style = "PLAIN";
		if (f.isBold() && f.isItalic())
			style = "BOLDITALIC";
		else if (f.isBold())
			style = "BOLD";
		else if (f.isItalic())
			style = "ITALIC";
		String fs = f.getName() + "-" + style + "-" + f.getSize();
		return fs;
	}

	/**
	 * Helper function to print the contents of maps or collections, or maps of
	 * maps, collections of maps, collections of collections etc. Useful for
	 * debugging. Similar in idea to the perl Data::Dumper module
	 * 
	 * @param indent is for recursive use, e.g. to prefix each line with "\t"
	 */
	public static void printx(PrintStream out, String indent, Object o) {
		if (o instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) o;
			for (Object key : map.keySet()) {
				printx(out, indent, key);
				out.println(indent + "=>");
				Object value = map.get(key);
				printx(out, indent + "\t", value);
			}
		} else if (o instanceof Collection) {
			Collection<?> col = (Collection<?>) o;
			out.println(indent + "(");
			for (Object item : col) {
				printx(out, indent + "\t", item);
			}
			out.println(indent + ")");
		} else {
			out.println(indent + o.toString());
		}
	}

}
