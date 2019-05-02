/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2018 - 2019 Open Microscopy Environment:
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

package net.imagej.omero.legacy;

import net.imagej.legacy.convert.roi.IJRoiWrapper;
import net.imagej.legacy.convert.roi.MaskPredicateWrapper;
import net.imagej.omero.DefaultOMEROService;
import net.imagej.omero.OMEROService;
import net.imglib2.roi.MaskPredicate;

import omero.gateway.model.ROIData;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.service.Service;

/**
 * {@link OMEROService} which can handle ImageJ 1.x data structures.
 *
 * @author Alison Walter
 */
@Plugin(type = Service.class, priority = Priority.HIGH)
public class LegacyOMEROService extends DefaultOMEROService {

	@Override
	public void addROIMapping(final Object roi, final ROIData shape) {
		final Object key = unwrap(roi);
		super.addROIMapping(key, shape);
	}

	@Override
	public ROIData getROIMapping(final Object key) {
		final Object k = unwrap(key);
		return super.getROIMapping(k);
	}

	@Override
	public void removeROIMapping(final Object key) {
		final Object k = unwrap(key);
		super.removeROIMapping(k);
	}

	// -- Helper methods --

	/**
	 * If the given {@code Object} is a wrapped {@link ij.gui.Roi} or
	 * {@link MaskPredicate} it is unwrapped and returned. If it is a ROI but not
	 * wrapped it is simply returned. And if the given {@code Object} isn't a ROI
	 * type an exception is thrown.
	 *
	 * @param roi the ROI to try and unwrap, this should be a
	 *          {@link MaskPredicate} or {@link ij.gui.Roi}
	 * @return the unwrapped ROI, or the original ROI if unwrapping isn't
	 *         necessary
	 */
	private Object unwrap(final Object roi) {
		if (roi instanceof IJRoiWrapper) return unwrap(((IJRoiWrapper<?, ?>) roi)
			.getRoi());
		if (roi instanceof MaskPredicateWrapper) return unwrap(
			((MaskPredicateWrapper<?>) roi).getSource());
		if (roi instanceof ij.gui.Roi || roi instanceof MaskPredicate) return roi;
		throw new IllegalArgumentException(roi.getClass() + " is not a ROI type");
	}

}
