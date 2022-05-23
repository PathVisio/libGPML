package org.pathvisio.libgpml.model;

/**
 * This class stores information for copied {@link PathwayElement}. Storing both
 * the new pathway element and its original source pathway element helps
 * maintain pathway element data, such as reference information, when copying
 * (to clipboard) and pasting or transferring pathway data.
 *
 * {@link PathwayElement#copy} returns a {@link CopyElement} which stores the
 * newly created newElement and a reference to the original sourceElement.
 *
 * @author finterly
 */
public class CopyElement {

	PathwayElement newElement;
	PathwayElement sourceElement;

	/**
	 * Instantiates a CopyElement which holds references to a new pathway element
	 * and the original source element it was copied from.
	 *
	 * @param newElement    the new pathway element copied from a source element.
	 * @param sourceElement the original pathway element copied.
	 */
	public CopyElement(PathwayElement newElement, PathwayElement sourceElement) {
		super();
		this.newElement = newElement;
		this.sourceElement = sourceElement;
	}

	/**
	 * Returns the new pathway element.
	 *
	 * @return newElement the new pathway element copied from source element.
	 */
	public PathwayElement getNewElement() {
		return newElement;
	}

	/**
	 * Sets the new pathway element.
	 *
	 * @param newElement the new pathway element copied from source element.
	 */
	public void setNewElement(PathwayElement newElement) {
		this.newElement = newElement;
	}

	/**
	 * Returns source pathway element.
	 *
	 * @return sourceElement the original pathway element copied.
	 */
	public PathwayElement getSourceElement() {
		return sourceElement;
	}

	/**
	 * Sets source pathway element.
	 *
	 * @param sourceElement the the original pathway element copied.
	 */
	public void setSourceElement(PathwayElement sourceElement) {
		this.sourceElement = sourceElement;
	}

}
