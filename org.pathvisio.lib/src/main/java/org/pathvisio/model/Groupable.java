package org.pathvisio.model;

/**
 * Interface for classes which can belong in a {@link Group}. These classes
 * include {@link DataNode}, {@link GraphicalLine}, {@link Label},
 * {@link Shape}, and {@link Group}.
 * 
 * @author finterly
 */
public interface Groupable {
	/**
	 * Returns the parent group of the pathway element. In GPML, groupRef refers to
	 * the elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of the pathway element.
	 */
	public Group getGroupRef();

	/**
	 * Checks whether this pathway element belongs to a group.
	 *
	 * @return true if and only if the group of this pathway element is effective.
	 */
	public boolean hasGroupRef();

	/**
	 * Verifies if given parent group is new and valid. Sets the parent group of the
	 * pathway element. Adds this pathway element to the the pathwayElements list of
	 * the new parent group. If there is an old parent group, this pathway element
	 * is removed from its pathwayElements list.
	 * 
	 * @param groupRefNew the new parent group to set.
	 */
	public void setGroupRefTo(Group groupRef);

	/**
	 * Sets the parent group for this pathway element.
	 * 
	 * @param groupRef the given group to set.
	 */
//	protected void setGroupRef(Group groupRef);

	/**
	 * Unsets the parent group, if any, from this pathway element.
	 */
	public void unsetGroupRef();
	
	/**
	 * Terminates the pathway element. 
	 */
	public void terminate();
}
