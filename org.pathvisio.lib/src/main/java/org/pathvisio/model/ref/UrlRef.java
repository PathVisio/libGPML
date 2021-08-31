package org.pathvisio.model.ref;

/**
 * This class stores information for an Url. Url is a property of
 * {@link Annotation}, {@link Citation}, and {@link Evidence}.
 * 
 * @author finterly
 */
public class UrlRef {

	private String link;
	private String description; // optional url description

	/**
	 * Instantiates an UrlRef given a web address link and description.
	 * 
	 * @param link        the url link for a web address.
	 * @param description the description for the link.
	 */
	public UrlRef(String link, String description) {
		this.link = link;
		this.description = description;
	}

	/**
	 * Instantiates an UrlRef given only a web address link.
	 * 
	 * @param link        the url link for a web address.
	 * @param description the description for the link.
	 */
	public UrlRef(String link) {
		this(link, null);
	}

	/**
	 * Returns the url link for a web address.
	 * 
	 * @return link the url link.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Sets the url link for a web address.
	 * 
	 * @param v the url link.
	 */
	public void setLink(String v) {
		link = v;
	}

	/**
	 * Returns the description for a url link.
	 * 
	 * @return description the description for a url link.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description for this url link.
	 * 
	 * @param v the description for a url link.
	 */
	public void setDescription(String v) {
		description = v;
	}

}
