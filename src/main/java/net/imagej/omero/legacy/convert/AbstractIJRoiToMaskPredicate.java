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

package net.imagej.omero.legacy.convert;

import java.lang.reflect.Type;
import java.util.Set;

import ij.gui.Roi;

import net.imagej.legacy.convert.roi.AbstractRoiToMaskPredicateConverter;
import net.imagej.legacy.convert.roi.MaskPredicateWrapper;
import net.imagej.omero.OMEROService;
import net.imagej.omero.legacy.LegacyOMEROROIService;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.MaskPredicate;

import omero.gateway.model.ROIData;

import org.scijava.convert.ConversionRequest;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;

/**
 * Abstract base class for {@link Converter}s which convert {@link Roi} to
 * {@link MaskPredicate} and updates the {@link OMEROService} roi mappings.
 *
 * @author Alison Walter
 */
public abstract class AbstractIJRoiToMaskPredicate<R extends Roi, M extends MaskPredicate<? extends RealLocalizable>, C extends AbstractRoiToMaskPredicateConverter<R, M>>
	extends AbstractRoiToMaskPredicateConverter<R, M>
{

	@Parameter
	private OMEROService omero;

	@Parameter
	private LegacyOMEROROIService legacyRoi;

	private final static String ID_KEY = "net.imagej.omero.legacy:ID";

	@Override
	public boolean canConvert(final ConversionRequest request) {
		return super.canConvert(request) && !MaskPredicateWrapper.class
			.isAssignableFrom(request.sourceClass());
	}

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		return super.canConvert(src, dest) &&
			!(src instanceof MaskPredicateWrapper);
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		return super.canConvert(src, dest) &&
			!(src instanceof MaskPredicateWrapper);
	}

	@Override
	public Class<R> getInputType() {
		return getConverter().getInputType();
	}

	@Override
	public Class<M> getOutputType() {
		return getConverter().getOutputType();
	}

	@Override
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!getInputType().isInstance(src)) throw new IllegalArgumentException(
			"Expected " + getInputType() + " but received " + src.getClass());
		if (!dest.isAssignableFrom(getOutputType()))
			throw new IllegalArgumentException("Expected " + getOutputType() +
				" but received " + dest);

		omeroMapping((Roi) src);

		return getConverter().convert(src, dest);
	}

	@Override
	public M convert(final R src) {
		return getConverter().convert(src);
	}

	@Override
	public boolean supportedType(final R src) {
		return getConverter().supportedType(src);
	}

	public abstract C getConverter();

	// -- Helper methods --

	/**
	 * Certain ImageJ 1.x calls clone the ROIs, making the saved ROI mappings
	 * invalid. This checks all ROI mappings for a Roi with the same
	 * {@code ID_KEY} property and if one is found, it is updated to use the new
	 * Roi as a key.
	 * <p>
	 * If the Roi is new, it is assigned a unique ID.
	 * </p>
	 *
	 * @param ijRoi the {@link Roi} key entry to update
	 */
	private void omeroMapping(final ij.gui.Roi ijRoi) {
		final Set<Object> keys = omero.roiCache().getROIMappingKeys();

		if (ijRoi.getProperty(ID_KEY) == null || ijRoi.getProperty(ID_KEY)
			.isEmpty()) ijRoi.setProperty(ID_KEY, Long.toString(legacyRoi
				.getLegacyRoiId()));

		for (final Object key : keys) {
			if (key instanceof Roi && ((Roi) key).getProperty(ID_KEY).equals(ijRoi
				.getProperty(ID_KEY)))
			{
				final ROIData rd = omero.getROIMapping(key);
				omero.removeROIMapping(key);
				omero.addROIMapping(ijRoi, rd);
				return;
			}
		}
	}
}
