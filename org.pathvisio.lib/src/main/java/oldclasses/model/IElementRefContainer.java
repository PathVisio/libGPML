package oldclasses.model;

import org.pathvisio.model.Pathway;

import oldclasses.model.ElementLink.ElementIdContainer;

/**
 * This interface allows iteration through all objects containing an elementRef.
 * Classes that refer to an ElementIdContainer implement this interface, e.g.
 * Point, State, DataNode, AnnotationRef, CitationRef, EvidenceRef.
 * 
 * @author unknown, finterly
 */
public interface IElementRefContainer {
	/**
	 * Returns the parent Pathway object, needed for maintaining a consistent list
	 * of elementIds.
	 */
	Pathway getPathway();

	String getElementRef();

	void linkTo(ElementIdContainer elementIdContainer, double relX, double relY);

	void unlink();

	double getRelX();

	double getRelY();

	/**
	 * Called whenever the object being referred to changes coordinates.
	 */
	void refeeChanged();
}
