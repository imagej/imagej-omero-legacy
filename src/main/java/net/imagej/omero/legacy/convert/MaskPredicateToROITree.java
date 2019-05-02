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

package net.imagej.omero.legacy.convert;

import java.util.Collections;

import net.imagej.roi.DefaultROITree;
import net.imagej.roi.ROITree;
import net.imglib2.roi.MaskPredicate;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@link MaskPredicate} to a {@link ROITree}.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class)
public class MaskPredicateToROITree extends
	AbstractConverter<MaskPredicate<?>, ROITree>
{

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!getInputType().isInstance(src)) throw new IllegalArgumentException(
			"Expected " + getInputType() + " but received " + src.getClass());
		if (!dest.isAssignableFrom(getOutputType()))
			throw new IllegalArgumentException("Expected " + getOutputType() +
				" but received " + dest);

		final ROITree tree = new DefaultROITree();
		tree.addROIs(Collections.singletonList((MaskPredicate<?>) src));
		return (T) tree;
	}

	@Override
	public Class<ROITree> getOutputType() {
		return ROITree.class;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<MaskPredicate<?>> getInputType() {
		return (Class) MaskPredicate.class;
	}

}
