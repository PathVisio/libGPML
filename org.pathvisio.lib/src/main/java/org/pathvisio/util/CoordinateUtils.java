package org.pathvisio.util;

public class CoordinateUtils {

	
	public Point2D toAbsoluteCoordinate(Point2D p) {
		Point2D l = ((MLine)getParent()).getConnectorShape().fromLineCoordinate(getPosition());
		return new Point2D.Double(p.getX() + l.getX(), p.getY() + l.getY());
	}

	public Point2D toRelativeCoordinate(Point2D p) {
		Point2D l = ((MLine)getParent()).getConnectorShape().fromLineCoordinate(getPosition());
		return new Point2D.Double(p.getX() - l.getX(), p.getY() - l.getY());
	}


	
	/**
	 * @param mp a point in absolute model coordinates
	 * @returns the same point relative to the bounding box of this pathway element: -1,-1 meaning the top-left corner, 1,1 meaning the bottom right corner, and 0,0 meaning the center.
	 */
	public Point2D toRelativeCoordinate(Point2D mp) {
		double relX = mp.getX();
		double relY = mp.getY();
		Rectangle2D bounds = getRBounds();
		//Translate
		relX -= bounds.getCenterX();
		relY -= bounds.getCenterY();
		//Scalebounds.getCenterX();
		if(relX != 0 && bounds.getWidth() != 0) relX /= bounds.getWidth() / 2;
		if(relY != 0 && bounds.getHeight() != 0) relY /= bounds.getHeight() / 2;
		return new Point2D.Double(relX, relY);
	}
	
	public Point2D toAbsoluteCoordinate(Point2D p) {
		double x = p.getX();
		double y = p.getY();
		Rectangle2D bounds = getRBounds();
		//Scale
		if(bounds.getWidth() != 0) x *= bounds.getWidth() / 2;
		if(bounds.getHeight() != 0) y *= bounds.getHeight() / 2;
		//Translate
		x += bounds.getCenterX();
		y += bounds.getCenterY();
		return new Point2D.Double(x, y);
	}
	
	/**
	 * Get the rectangular bounds of the object
	 * after rotation is applied
	 */
	public Rectangle2D getRBounds() {
		Rectangle2D bounds = getMBounds();
		AffineTransform t = new AffineTransform();
		t.rotate(getRotation(), getMCenterX(), getMCenterY());
		bounds = t.createTransformedShape(bounds).getBounds2D();
		return bounds;
	}

	/**
	 * Get the rectangular bounds of the object
	 * without rotation taken into accound
	 */
	public Rectangle2D getMBounds() {
		return new Rectangle2D.Double(getMLeft(), getMTop(), getMWidth(), getMHeight());
	}
	

	/**
	 * Get the rectangular bounds of the object
	 * without rotation taken into accound
	 */
	public Rectangle2D getMBounds() {
		return new Rectangle2D.Double(getMLeft(), getMTop(), getMWidth(), getMHeight());
	}
	
	protected double mCenterx = 0;

	public double getMCenterX()
	{
		return mCenterx;
	}

	public void setMCenterX(double v)
	{
		if (mCenterx != v)
		{
			mCenterx = v;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	protected double mCentery = 0;

	public double getMCenterY()
	{
		return mCentery;
	}

	public void setMCenterY(double v)
	{
		if (mCentery != v)
		{
			mCentery = v;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	protected double mWidth = 0;

	public double getMWidth()
	{
		return mWidth;
	}

	public void setMWidth(double v)
	{
		if(mWidth < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		}
		if (mWidth != v)
		{
			mWidth = v;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	protected double mHeight = 0;

	public double getMHeight()
	{
		return mHeight;
	}

	public void setMHeight(double v)
	{
		if(mWidth < 0) {
			throw new IllegalArgumentException("Tried to set dimension < 0: " + v);
		}
		if (mHeight != v)
		{
			mHeight = v;
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}
	}

	// starty for shapes
	public double getMTop()
	{
		return mCentery - mHeight / 2;
	}

	public void setMTop(double v)
	{
		mCentery = v + mHeight / 2;
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
	}

	// startx for shapes
	public double getMLeft()
	{
		return mCenterx - mWidth / 2;
	}

	public void setMLeft(double v)
	{
		mCenterx = v + mWidth / 2;
		fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
	}

	protected IShape shapeType = ShapeType.RECTANGLE;

	public IShape getShapeType()
	{
		return shapeType;
	}

	public void setShapeType(IShape v)
	{
		if (shapeType != v)
		{
			shapeType = v;
			fireObjectModifiedEvent(PathwayElementEvent.createSinglePropertyEvent(this, StaticProperty.SHAPETYPE));
		}
	}

	public void setOrientation(int orientation)
	{
		switch (orientation)
		{
		case OrientationType.TOP:
			setRotation(0);
			break;
		case OrientationType.LEFT:
			setRotation(Math.PI * (3.0 / 2));
			break;
		case OrientationType.BOTTOM:
			setRotation(Math.PI);
			break;
		case OrientationType.RIGHT:
			setRotation(Math.PI / 2);
			break;
		}
	}

	public int getOrientation()
	{
		double r = rotation / Math.PI;
		if (r < 1.0 / 4 || r >= 7.0 / 4)
			return OrientationType.TOP;
		if (r > 5.0 / 4 && r <= 7.0 / 4)
			return OrientationType.LEFT;
		if (r > 3.0 / 4 && r <= 5.0 / 4)
			return OrientationType.BOTTOM;
		if (r > 1.0 / 4 && r <= 3.0 / 4)
			return OrientationType.RIGHT;
		return 0;
	}

	protected double rotation = 0; // in radians

	public double getRotation()
	{
		return rotation;
	}

	public void setRotation(double v)
	{
		if (rotation != v)
		{			
			rotation = v;
			
			// Rotation is not stored for State, so we use a dynamic property.
			// TODO: remove after next GPML update.
			if (objectType == ObjectType.STATE && v != 0)
			{
				setDynamicProperty(State.ROTATION_KEY, "" + v);
			}
			
			fireObjectModifiedEvent(PathwayElementEvent.createCoordinatePropertyEvent(this));
		}

	}

	/**
	 * Get the rectangular bounds of the object
	 * after rotation is applied
	 */
	public Rectangle2D getRBounds() {
		Rectangle2D bounds = getMBounds();
		AffineTransform t = new AffineTransform();
		t.rotate(getRotation(), getMCenterX(), getMCenterY());
		bounds = t.createTransformedShape(bounds).getBounds2D();
		return bounds;
	}

	/**
	 * Get the rectangular bounds of the object
	 * without rotation taken into accound
	 */
	public Rectangle2D getMBounds() {
		return new Rectangle2D.Double(getMLeft(), getMTop(), getMWidth(), getMHeight());
	}
}
