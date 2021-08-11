package org.pathvisio.model.graphics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class Graphics {

	protected PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propChangeSupport.removePropertyChangeListener(listener);
	}

}
