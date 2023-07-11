/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2018 - 2023 Open Microscopy Environment:
 * 	- Board of Regents of the University of Wisconsin-Madison
 * 	- Glencoe Software, Inc.
 * 	- University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imagej.omero.legacy.text;

import ij.gui.TextRoi;

import net.imagej.legacy.convert.roi.MaskPredicateWrapper;

import ome.model.enums.UnitsLength;
import omero.gateway.model.ShapeSettingsData;
import omero.model.LengthI;

/**
 * Wraps an {@link OMEROText} as an ImageJ 1.x ROI.
 *
 * @author Alison Walter
 */
public class OMEROTextWrapper extends TextRoi implements
	MaskPredicateWrapper<OMEROText>
{

	private final OMEROText text;

	public OMEROTextWrapper(final OMEROText text) {
		super(text.getX(), text.getY(), text.getText(), text.getShape()
			.getShapeSettings().getFont());
		this.text = text;
	}

	@Override
	public OMEROText getSource() {
		return text;
	}

	@Override
	public void synchronize() {
		text.setText(getText());
		text.setX(getXBase());
		text.setY(getYBase());
		text.getShape().getShapeSettings().setFontFamily(getCurrentFont()
			.getFamily());
		text.getShape().getShapeSettings().setFontSize(new LengthI(getCurrentFont()
			.getSize(), UnitsLength.POINT));
		text.getShape().getShapeSettings().setFontStyle(computeStyle());
	}

	// -- Helper methods --

	private String computeStyle() {
		if (getCurrentFont().isBold()) {
			if (getCurrentFont().isItalic())
				return ShapeSettingsData.FONT_BOLD_ITALIC;
			return ShapeSettingsData.FONT_BOLD;
		}
		else if (getCurrentFont().isItalic()) return ShapeSettingsData.FONT_ITALIC;
		return ShapeSettingsData.FONT_REGULAR;
	}
}
