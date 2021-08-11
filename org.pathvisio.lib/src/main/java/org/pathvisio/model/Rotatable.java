package org.pathvisio.model;

/**
 * Interface for classes which can be rotated. These classes include
 * {@link DataNode}, {@link State}, {@link Label}, and {@link Shape}.
 * 
 * @author finterly
 */
public interface Rotatable {

	/**
	 * Returns the rotation of this data node.
	 * 
	 * @return rotation the rotation of the data node.
	 */
	public double getRotation();

	/**
	 * Sets the rotation of this data node.
	 * 
	 * @param rotation the rotation of the data node.
	 */
	public void setRotation(Double rotation);

}
