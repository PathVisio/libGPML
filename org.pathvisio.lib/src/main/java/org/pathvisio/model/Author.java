package org.pathvisio.model;

import org.pathvisio.util.Utils;

public class Author {
	
	protected String name;
	protected String fullName = null; // if part of group
	protected String email = null;

	

	/**
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @param v
	 */
	public void setEmail(String v) {
		if (!Utils.stringEquals(email, v)) {
			email = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.EMAIL));
		}
	}
}
