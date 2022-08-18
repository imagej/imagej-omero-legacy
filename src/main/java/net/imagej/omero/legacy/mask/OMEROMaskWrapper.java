/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2018 - 2022 Open Microscopy Environment:
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

package net.imagej.omero.legacy.mask;

import ij.gui.ImageRoi;

import net.imagej.legacy.convert.roi.MaskPredicateWrapper;
import net.imagej.omero.roi.mask.OMEROMask;

import omero.gateway.model.MaskData;

/**
 * Wraps an {@link OMEROMask} as a {@link ImageRoi}.
 *
 * @author Alison Walter
 */
public class OMEROMaskWrapper extends ImageRoi implements
	MaskPredicateWrapper<OMEROMask>
{

	private final OMEROMask source;

	public OMEROMaskWrapper(final OMEROMask source) {
		super((int) Math.floor(source.getShape().getX()), (int) Math.floor(source
			.getShape().getY()), source.getShape().getMaskAsBufferedImage());
		this.source = source;
	}

	@Override
	public OMEROMask getSource() {
		return source;
	}

	@Override
	public void synchronize() {
		final MaskData md = source.getShape();

		// Only set X and Y if they changed, since ImageRoi only has integer
		// coordinates setting without checking would be destructive
		if ((long) Math.floor(md.getX()) != getXBase()) md.setX(getXBase());
		if ((long) Math.floor(md.getY()) != getYBase()) md.setY(getYBase());

		md.setMask(getProcessor().getBufferedImage());
	}

}
