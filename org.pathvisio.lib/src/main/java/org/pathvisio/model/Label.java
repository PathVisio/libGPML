/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2021 BiGCaT Bioinformatics, WikiPathways
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
package org.pathvisio.model;

import java.util.ArrayList;
import java.util.List;

import org.pathvisio.util.Utils;

/**
 * This class stores all information relevant to a Label pathway element.
 * 
 * @author finterly
 */
public class Label extends ShapedElement {

	private String textLabel;
	/*
	 * The parent group to which the label belongs. In GPML, groupRef refers to the
	 * elementId (formerly groupId) of the parent gpml:Group.
	 */
	private Group groupRef; // optional
	private String href; // optional

	
	/**
	 * Gets the text of of the label.
	 * 
	 * @return textLabel the text of of the label.
	 * 
	 */
	public String getTextLabel() {
		return textLabel;
	}

	/**
	 * Sets the text of of the label.
	 * 
	 * @param textLabel the text of of the label.
	 * 
	 */
	public void setTextLabel(String textLabel) {
		this.textLabel = textLabel;
	}

	/**
	 * Returns the parent group of the label. In GPML, groupRef refers to the
	 * elementId (formerly groupId) of the parent gpml:Group.
	 * 
	 * @return groupRef the parent group of the label.
	 */
	public Group getGroup() {
		return groupRef;
	}

	/**
	 * Sets the parent group of the label. The group is added to the pathwayElements
	 * list of the parent group.
	 * 
	 * @param groupRef the parent group of the label.
	 */
	public void setGroup(Group groupRef) {
		groupRef.addPathwayElement(this);
		this.groupRef = groupRef;
	}

	/**
	 * Gets the hyperlink for a Label.
	 * 
	 * @return href the hyperlink reference to a url.
	 */
	public String getHref() {
		return href;
	}

	/**
	 * Sets the hyperlink for a Label.
	 * 
	 * @param input the given string link. If input is null, href is set to
	 *              ""(empty).
	 */
	public void setHref(String input) {
		String link = (input == null) ? "" : input;
		if (!Utils.stringEquals(href, link)) {
			href = link;
			if (PreferenceManager.getCurrent() == null)
				PreferenceManager.init();
			setColor(PreferenceManager.getCurrent().getColor(GlobalPreference.COLOR_LINK));
		}
	}
}